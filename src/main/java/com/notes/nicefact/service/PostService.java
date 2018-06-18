package com.notes.nicefact.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.notes.nicefact.content.AllSchoolException;
import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.GroupDAO;
import com.notes.nicefact.dao.GroupMemberDAO;
import com.notes.nicefact.dao.PostCommentDAO;
import com.notes.nicefact.dao.PostDAO;
import com.notes.nicefact.dao.PostFileDAO;
import com.notes.nicefact.dao.PostReactionDAO;
import com.notes.nicefact.dao.TaskSubmissionDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.Post.POST_TYPE;
import com.notes.nicefact.entity.PostComment;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.entity.PostReaction;
<<<<<<< HEAD
import com.notes.nicefact.entity.TaskSubmission;
=======
>>>>>>> 8d33ad6de334e179f9c7f9230aad6ba43202e283
import com.notes.nicefact.entity.PostRecipient;
import com.notes.nicefact.entity.PostTag;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.entity.AbstractRecipient.RecipientType;
<<<<<<< HEAD
=======
import com.notes.nicefact.entity.TaskSubmission;

>>>>>>> 8d33ad6de334e179f9c7f9230aad6ba43202e283
import com.notes.nicefact.enums.NotificationAction;
import com.notes.nicefact.enums.ScheduleAttendeeResponseType;
import com.notes.nicefact.exception.NotFoundException;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.to.CommentTO;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.PostRecipientTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
<<<<<<< HEAD
import com.notes.nicefact.to.TaskSubmissionTO;
import com.notes.nicefact.to.TagTO;
=======
import com.notes.nicefact.to.TagTO;
import com.notes.nicefact.to.TaskSubmissionTO;
>>>>>>> 8d33ad6de334e179f9c7f9230aad6ba43202e283
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.Utils;

public class PostService extends CommonService<Post> {
	static Logger logger = Logger.getLogger(PostService.class.getSimpleName());

	private EntityManager em;
	private GroupDAO groupDao;
	private GroupMemberDAO groupMemberDAO;
	private PostDAO postDAO;
	TaskService taskService;

	private PostCommentDAO postCommentDAO;
	PostReactionDAO postReactionDAO;
	private PostFileDAO postFileDAO;
	BackendTaskService backendTaskService;
	NotificationService notificationService;
	private PostTagService postTagService;
	private AppUserService appUserService;
	
	public PostService(EntityManager em) {
		groupDao = new GroupDAO(em);
		groupMemberDAO = new GroupMemberDAO(em);
		postDAO = new PostDAO(em);
		postCommentDAO = new PostCommentDAO(em);
		postReactionDAO = new PostReactionDAO(em);
		postFileDAO = new PostFileDAO(em);
		backendTaskService = new BackendTaskService(em);
		notificationService = new NotificationService(em);
		taskService = new TaskService(em);
		postTagService = new PostTagService(em);
		appUserService = new AppUserService(em);
		
		this.em = em;
	}

	@Override
	protected CommonDAO<Post> getDAO() {
		return postDAO;
	}

	public Post upsert(PostTO postTo, AppUser appUser) {
		if (StringUtils.isBlank(postTo.getComment())) {
			throw new ServiceException(" Post cannot be empty");
		}
		
		Post post;
		
		if(postTo.getId() != null && postTo.getId() > 0) { // existing post
			post = postDAO.get(postTo.getId());
			
			post.setComment(postTo.getComment());
			
			post.getRecipients().clear();
			
			PostRecipient recipient;
			List<PostRecipient> postRecipients = new ArrayList<>();
			for (PostRecipientTO postRecipientTO : postTo.getRecipients()) {
				recipient = new PostRecipient();
				recipient.setEmail(postRecipientTO.getEmail());
				recipient.setScheduleResponse(ScheduleAttendeeResponseType.valueOf(postRecipientTO.getScheduleResponse()) );
				recipient.setName(postRecipientTO.getLabel());
				recipient.setPost(post);
				AppUser hr = CacheUtils.getAppUser(postRecipientTO.getEmail());
				if (hr == null) {
					recipient.setType(RecipientType.EMAIL);
				} else {
					recipient.setType(RecipientType.USER);
					recipient.setName(hr.getDisplayName());
					recipient.setPosition(hr.getPosition());
					recipient.setDepartment(hr.getDepartment());
					recipient.setOrganization(hr.getOrganization());
				}
				postRecipients.add(recipient);
			}
			
			post.getRecipients().addAll(postRecipients);
			
			post.setTitle(postTo.getTitle());
			
			if(postTo.getDeadlineTime() > 0){
				post.setDeadline(new Date(postTo.getDeadlineTime()));
			}
			
			post.setAllDayEvent(postTo.getAllDayEvent());
			
			if(postTo.getFromDate() > 0){
				post.setFromDate(new Date(postTo.getFromDate()));
				
				if(post.getAllDayEvent() != null && post.getAllDayEvent() == true){
					post.setFromDate(Utils.removeTimeFromDate(post.getFromDate()));
				}
			}
			if(postTo.getToDate() > 0){
				post.setToDate(new Date(postTo.getToDate()));
				if(post.getAllDayEvent() != null && post.getAllDayEvent() == true){
					post.setToDate(Utils.removeTimeFromDate(post.getToDate()));
				}
			}
			
			post.setWeekdays(postTo.getWeekdays());
			
			if(post.getLocation() != null){
				post.setLocation(postTo.getLocation());
			}
			
			post.setGoogleEventId(postTo.getGoogleEventId());
		} else { // new post
			post = new Post(postTo);
		}
		
		if(postTo.getGroupId() == null || postTo.getGroupId() == 0) {
			// Saving public post
			updateAttachedFiles(post, postTo);
			post = upsert(post);
			backendTaskService.savePostTask(post);
		} else if (appUser.getGroupIds().contains(postTo.getGroupId())) {
			Group group = CacheUtils.getGroup(postTo.getGroupId());
			if (group.getBlocked().contains(appUser.getEmail())) {
				throw new UnauthorizedException(
						"User has been blocked by group admin.");
			}
			if (postTo.getId() > 0) {
				Post postDB = get(postTo.getId());
				if (postDB.getCreatedBy().equals(appUser.getEmail())) {
					postDB.updateProps(post);
					updateAttachedFiles(postDB, postTo);
					upsert(postDB);
					backendTaskService.savePostTask(postDB);
					return postDB;
				} else {
					throw new UnauthorizedException(
							"You cannot edit this post.");
				}
			} else {

				if (group == null
						|| group.getBlocked().contains(appUser.getEmail())) {
					throw new ServiceException("User cannot post to this group");
				} else {
					updateAttachedFiles(post, postTo);
					post = upsert(post);
					backendTaskService.savePostTask(post);
				}
			}

		} else {
			throw new UnauthorizedException("You cannot post to this group.");
		}
		
		// Adding or removing tag
		List<PostTag> postTagList = postTagService.getByPostId(post.getId());
		List<String> tags = null;
		if(postTagList != null && postTagList.size() > 0) {
			if(null != postTo.getNewTag() && !postTo.getNewTag().equals("")) {
				tags = new ArrayList<>(Arrays.asList(postTo.getNewTag().split("\\s+")));
				tags = getHashTag(tags);
				
				for(PostTag postTag: postTagList) {
					if(!tags.contains(postTag.getTag().getName().trim())) {
						postTagService.remove(postTag);
					} else {
						tags.remove(postTag.getTag().getName().trim());
					}
				}
			} else {
				postTagService.removeAll(postTagList);
			}
			
		} else {
			if(null != postTo.getNewTag() && !postTo.getNewTag().equals("")) {
				tags = Arrays.asList(postTo.getNewTag().split("\\s+"));
				tags = getHashTag(tags);
			}
		}
		
		if(null != postTo.getNewTag() && !postTo.getNewTag().equals("")) {
			TagService tagService = new TagService(em);
			TagTO tagTO = new TagTO();
			Tag tag;
			Set<PostTag> postTags = new HashSet<>();
			
			for(String tagName: tags) {
				tagName = tagName.trim();
				tag = tagService.getByName(tagName);
				
				if(tag == null) {
					tagTO = new TagTO();
					tagTO.setName(tagName);
					tagTO.setDescription(tagName);
					
					tag = tagService.upsert(tagTO);
				}
				
				postTags.add(postTagService.save(post, tag));
			}
			
			post.getPostTags().addAll(postTags);
		}
		
		return post;
	}

	private List<String> getHashTag(List<String> tags) {
		for(int index = 0; index < tags.size(); index++) {
			if(tags.get(index).charAt(0) != '#') {
				tags.set(index, "#"+tags.get(index));
			}
		}
				
		return tags;
	}
	
	void updateAttachedFiles(Post post, PostTO postTo) {
		try {
			String fileBasePath = AppProperties.getInstance()
					.getGroupUploadsFolder() + (post.getGroupId() != null && post.getGroupId() > 0 ? post.getGroupId() : "Public");
			if (Files.notExists(Paths.get(fileBasePath))) {
				Files.createDirectories(Paths.get(fileBasePath));
			}
			fileBasePath += "/";
			String serverFilePath;
			String tempFilePath;

			/* set of file ids that were not delted on UI */
			Set<Long> filesToKeppIds = new HashSet<>();
			for (FileTO fileTO : postTo.getFiles()) {
				if (fileTO.getId() > 0) {
					filesToKeppIds.add(fileTO.getId());
				}
			}
			/* delete files from filesystem and db that were delted on UI */
			for (Iterator<PostFile> postFileIter = post.getFiles().iterator(); postFileIter
					.hasNext();) {
				PostFile postFile = postFileIter.next();
				if (!filesToKeppIds.contains(postFile.getId())) {
					Files.deleteIfExists(Paths.get(postFile.getPath()));
					postFileIter.remove();
				}
			}

			for (FileTO fileTO : postTo.getFiles()) {
				if (fileTO.getId() <= 0) {
					serverFilePath = fileBasePath + fileTO.getServerName();
					tempFilePath = AppProperties.getInstance()
							.getTempUploadsFolder() + fileTO.getServerName();
					if (Files.exists(Paths.get(tempFilePath))) {
						Files.copy(Paths.get(tempFilePath),
								Paths.get(serverFilePath));
						PostFile postFile = new PostFile(fileTO, serverFilePath);
						postFile.setPost(post);
						post.getFiles().add(postFile);
					}
				}
			}
		} catch (IOException e) {
			logger.error(
					"error for post Id : " + post.getId() + " , "
							+ e.getMessage(), e);
		}
	}

	public List<PostTO> search(SearchTO searchTO) {
		List<Post> posts = postDAO.search(searchTO);
		List<PostTO> toList = new ArrayList<>();
		PostTO postTO;
		Date today = new Date();
		
		Map<Long,TaskSubmissionTO> taskSubmissionMapByPostId = getUserWiseTaskSubmissionforPosts(posts, searchTO);
		
		for (Post post : posts) {
			/*if (post.getPostType().equals(POST_TYPE.SCHEDULE)) {
				if ((null != post.getFromDate() && !post.getFromDate().before(today)) || (null != post.getToDate() && !post.getToDate().after(today))) {
					continue;
				}
			}*/
			
			List<TaskSubmissionTO> tsTOList = new ArrayList<>();
			tsTOList.add(taskSubmissionMapByPostId.get(post.getId()));
			postTO = new PostTO(post, tsTOList);
			toList.add(postTO);
		}
		return toList;
	}

	public Map<Long,TaskSubmissionTO> getUserWiseTaskSubmissionforPosts(List<Post> posts, SearchTO searchTO) {
		List<Long> postIdList = new ArrayList<>();
		for (Post post : posts) {
			postIdList.add(post.getId());
		}
		List<TaskSubmission> taskSubmissions = taskService.getTaskSubmissionsForUserByTaskIds(postIdList, searchTO.getEmail());
		Map<Long,TaskSubmissionTO> taskSubmissionMapByPostId = new HashMap<>();
		
		for(TaskSubmission ts : taskSubmissions){
			TaskSubmissionTO tsTO = new TaskSubmissionTO(ts);
			taskSubmissionMapByPostId.put(ts.getPostId(), tsTO);
		}
		return taskSubmissionMapByPostId;
	}
	
	public PostComment upsertComment(CommentTO commentTO, AppUser appUser) {
		logger.info("start : addPostComment ," + commentTO);
		if (commentTO.getPostId() <= 0) {
			throw new ServiceException(" Post id is a required field");
		} else if (StringUtils.isBlank(commentTO.getComment())) {
			throw new ServiceException(" Comment cannot be empty");
		}

		Post post = get(commentTO.getPostId());
		if (post == null) {
			throw new NotFoundException("No post found with id "
					+ commentTO.getPostId());
		}
		PostComment postComment = new PostComment(commentTO);
		if (appUser.getGroupIds().contains(post.getGroupId())) {
			Group group = CacheUtils.getGroup(post.getGroupId());
			if (group.getBlocked().contains(appUser.getEmail())) {
				throw new UnauthorizedException(
						"User has been blocked by group admin.");
			}
			if (commentTO.getCommentId() < 0) {
				postComment.setPost(post);
				post.getComments().add(postComment);
				postCommentDAO.upsert(postComment);
				backendTaskService.postCommentedTask(post, postComment);
			} else {
				PostComment dbComment = postCommentDAO.get(commentTO
						.getCommentId());
				dbComment.updateProps(postComment);
				postComment = postCommentDAO.upsert(dbComment);
			}
			// TODO send notifications
		} else {
			throw new UnauthorizedException("You cannot comment on this post.");
		}
		logger.info("exit : addPostComment");
		return postComment;
	}

	public PostComment getPostCommentById(long id) {
		return postCommentDAO.get(id);
	}

	public PostComment upsertCommentReply(CommentTO commentTO, AppUser appUser) {
		logger.info("start : addPostComment ," + commentTO);
		if (commentTO.getPostId() <= 0) {
			throw new ServiceException(" Post id is a required field");
		} else if (commentTO.getCommentId() <= 0) {
			throw new ServiceException(" Comment id is a required field");
		} else if (StringUtils.isBlank(commentTO.getComment())) {
			throw new ServiceException(" Comment cannot be empty");
		}
		PostComment dbComment = postCommentDAO.get(commentTO.getCommentId());
		if (dbComment == null) {
			throw new NotFoundException("No Comment found with id "
					+ commentTO.getCommentId());
		}
		Post post = dbComment.getPost();
		PostComment subComment = new PostComment(commentTO);
		if (appUser.getGroupIds().contains(post.getGroupId())) {
			Group group = CacheUtils.getGroup(post.getGroupId());
			if (group.getBlocked().contains(appUser.getEmail())) {
				throw new UnauthorizedException(
						"User has been blocked by group admin.");
			}
			if (commentTO.getSubCommentId() <= 0) {
				subComment.setParent(dbComment);
				dbComment.getComments().add(subComment);
				postCommentDAO.upsert(subComment);
				backendTaskService.postCommentReplyTask(post, dbComment,
						subComment);
			} else {
				PostComment dbSubComment = postCommentDAO.get(commentTO
						.getSubCommentId());
				dbSubComment.updateProps(subComment);
				subComment = postCommentDAO.upsert(dbSubComment);
			}
			// TODO send notifications
		} else {
			throw new UnauthorizedException("You cannot comment on this post.");
		}

		logger.info("exit : addPostComment");
		return subComment;
	}

	public void deletePost(long postId, AppUser appUser) {
		Post post = CacheUtils.getPost(postId);
		if (post.getCreatedBy().equals(appUser.getEmail())) {
			remove(postId);
		}else if (post.getGroupId()!=null){
			Group group = CacheUtils.getGroup(post.getGroupId());
			if(group.getAdmins().contains(appUser.getEmail())){
				remove(postId);
			}else{
				throw new UnauthorizedException("You cannot delete this post.");
			}
		} else {
			throw new UnauthorizedException("You cannot delete this post.");
		}
	}

	public void deletePostComment(long postId, long commentId, AppUser appUser) {
		PostComment comment = postCommentDAO.get(commentId);
		if (comment != null
				&& comment.getCreatedBy().equals(appUser.getEmail())) {
			postCommentDAO.remove(commentId);
		} else {
			throw new UnauthorizedException("You cannot delete this comment.");
		}

	}

	public Post reactToPost(long postId, AppUser appUser) {
		// Post post = get(postId);
		Post post = CacheUtils.getPost(postId);
		if (null == post) {
			throw new NotFoundException("No post found with id " + postId);
		} else {
			if (!appUser.getGroupIds().contains(post.getGroupId())) {
				throw new UnauthorizedException(
						"You do not have permission to view this post");
			}
			post = get(postId);
			PostReaction postGood = new PostReaction(appUser);
			postGood.setPost(post);
			boolean isNew = post.getReactions().add(postGood);
			if (isNew) {
				notificationService.savePostReactionNotification(post, appUser);
			} else {
				for (Iterator<PostReaction> reactIter = post.getReactions()
						.iterator(); reactIter.hasNext();) {
					PostReaction reaction = reactIter.next();
					if (reaction.getEmail().equals(appUser.getEmail())) {
						notificationService.deletePostReactionNotification(
								post, reaction.getEmail(),
								NotificationAction.POST_LIKE);
						reactIter.remove();
						break;
					}
				}
			}

			post.setUpdatedTime(new Date());
			upsert(post);
		}
		return post;
	}

	public PostFile getByServerName(String serverName) {
		return postFileDAO.getByServerName(serverName);
	}

	public List<PostTO> fetchMyPosts(SearchTO searchTO, AppUser appUser) {
		List<Post> posts = postDAO.fetchMyPosts(searchTO, appUser);
		List<PostTO> toList = new ArrayList<>();
		PostTO postTO;
		Date today = new Date();
		
		Map<String, Long> appUserMap = new HashMap<>();
		
		Map<Long,TaskSubmissionTO> taskSubmissionMapByPostId = getUserWiseTaskSubmissionforPosts(posts, searchTO);
		for (Post post : posts) {
/*			if (post.getPostType().equals(POST_TYPE.SCHEDULE)) {
				if (!post.getFromDate().before(today)
						|| !post.getToDate().after(today)) {
					continue;
				}
			}*/
			List<TaskSubmissionTO> tsTOList = new ArrayList<>();
			tsTOList.add(taskSubmissionMapByPostId.get(post.getId()));
			postTO = new PostTO(post, tsTOList);
			
			if(!appUserMap.containsKey(post.getCreatedBy())) {
				AppUser postCreatedBy = appUserService.getAppUserByEmail(post.getCreatedBy());
				appUserMap.put(post.getCreatedBy(), postCreatedBy.getId());
			}
			
			postTO.setPostCreatorId(appUserMap.get(post.getCreatedBy()));
			
			toList.add(postTO);
		}
		return toList;
	}
	
	public List<Post> getAllPublicPost(SearchTO searchTO) {
		return postDAO.fetchAllPublicPosts(searchTO);
	}
	
	public List<Post> searchPublicPost(SearchTO searchTO) {
		return postDAO.searchPublicPost(searchTO);
	}

	public List<PostFile> getPostFilesWithTempDriveId(int offset) {
		List<PostFile> files = postDAO.getPostFilesWithTempDriveId(offset);
		return files;
	}

	public List<PostFile> getDrivePostFilesWithoutThumbnail(int offset) {
		List<PostFile> files = postDAO
				.getDrivePostFilesWithoutThumbnail(offset);
		return files;
	}

	public List<Post> upsertTask(PostTO postTo, AppUser appUser) {
		if (StringUtils.isBlank(postTo.getComment())
				&& postTo.getFiles().isEmpty()) {
			throw new ServiceException(" Task details cannot be empty");
		}
		if (postTo.getGroupId() == null && postTo.getGroupIds().isEmpty()) {
			throw new ServiceException(" Group id cannot be null");
		}
		List<Post> posts = new ArrayList<>();
		if (postTo.getGroupIds().isEmpty()) {
			postTo.getGroupIds().add(postTo.getGroupId());
		}
		for (Long groupId : postTo.getGroupIds()) {
			Post post = new Post(postTo);
			post.setGroupId(groupId);
			post.setPostType(POST_TYPE.TASK);
			Group group = CacheUtils.getGroup(groupId);
			if (group.getAdmins().contains(appUser.getEmail())) {
				if (group.getBlocked().contains(appUser.getEmail())) {
					throw new UnauthorizedException(
							"User has been blocked by group admin.");
				}

				if (null == postTo.getId() || postTo.getId() <= 0) {
					updateAttachedFiles(post, postTo);
					postDAO.upsert(post);
					backendTaskService.saveTaskTask(post);
					posts.add(post);
				} else {
					Post postDB = postDAO.get(postTo.getId());
					if (postDB.getCreatedBy().equals(appUser.getEmail())) {
						postDB.updateProps(post);
						updateAttachedFiles(postDB, postTo);
						postDAO.upsert(postDB);
						backendTaskService.saveTaskTask(postDB);
						posts.add(postDB);
					} else {
						throw new UnauthorizedException(
								"You cannot edit this post.");
					}
				}

			} else {
				throw new UnauthorizedException(
						"You cannot create task for this group.");
			}
		}
		return posts;
	}

	public List<PostTO> fetchScheduleByDate(SearchTO searchTO,Date date) {
		List<Post> posts = postDAO.fetchScheduleByDate(searchTO,date);
		List<PostTO> toList = new ArrayList<>();
		PostTO postTO;
		Date today = new Date();
		for (Post post : posts) {			
			postTO = new PostTO(post);
			toList.add(postTO);
		}
		return toList;

	}

	public int countScheduleByDateAndDay(Date date) {		
		return  postDAO.countScheduleByDateAndDay(date);
	}
	
	public List<Post> upsertEvent(PostTO postTo, AppUser appUser) throws IOException, AllSchoolException {
		if (postTo.getGroupId() == null && postTo.getGroupIds().isEmpty()) {
			throw new ServiceException(" Group id cannot be null");
		}
		List<Post> posts = new ArrayList<>();
		if (postTo.getGroupIds().isEmpty()) {
			postTo.getGroupIds().add(postTo.getGroupId());
		}
			for (Long groupId : postTo.getGroupIds()) {
				Post post = new Post(postTo);
				post.setGroupId(groupId);
				post.setPostType(POST_TYPE.SCHEDULE);
				Group group = CacheUtils.getGroup(groupId);
				if (group.getBlocked().contains(appUser.getEmail())) {
					throw new UnauthorizedException("User has been blocked by group admin.");
				}

				if (null == postTo.getId() || postTo.getId() <= 0) {
					updateAttachedFiles(post, postTo);
					postDAO.upsert(post);
					backendTaskService.saveScheduleTask(post);
					posts.add(post);
				} else {
					Post postDB = postDAO.get(postTo.getId());
					if (postDB.getCreatedBy().equals(appUser.getEmail())) {
						postDB.updateProps(post);
						updateAttachedFiles(postDB, postTo);
						postDAO.upsert(postDB);
						backendTaskService.saveScheduleTask(postDB);
						posts.add(postDB);
					} else {
						throw new UnauthorizedException("You cannot edit this post.");
					}
				}

			}

		logger.info("upsertEvent : ");
		return posts;
	}

}
