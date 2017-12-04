package com.notes.nicefact.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.GroupDAO;
import com.notes.nicefact.dao.GroupMemberDAO;
import com.notes.nicefact.dao.PostCommentDAO;
import com.notes.nicefact.dao.PostDAO;
import com.notes.nicefact.dao.PostFileDAO;
import com.notes.nicefact.dao.PostReactionDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostComment;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.entity.PostReaction;
import com.notes.nicefact.entity.Post.POST_TYPE;
import com.notes.nicefact.enums.NotificationAction;
import com.notes.nicefact.exception.NotFoundException;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.to.CommentTO;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CacheUtils;

public class PostService extends CommonService<Post> {
	static Logger logger = Logger.getLogger(PostService.class.getSimpleName());

	private GroupDAO groupDao;
	private GroupMemberDAO groupMemberDAO;
	private PostDAO postDAO;
	TaskService taskService;

	private PostCommentDAO postCommentDAO;
	PostReactionDAO postReactionDAO;
	private PostFileDAO postFileDAO;
	BackendTaskService backendTaskService;
	NotificationService notificationService;

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
	}

	@Override
	protected CommonDAO<Post> getDAO() {
		return postDAO;
	}

	public Post upsert(PostTO postTo, AppUser appUser) {
		if (StringUtils.isBlank(postTo.getComment())) {
			throw new ServiceException(" Post cannot be empty");
		}
		Post post = new Post(postTo);
		if (appUser.getGroupIds().contains(postTo.getGroupId())) {
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
		return post;
	}

	void updateAttachedFiles(Post post, PostTO postTo) {
		try {
			String fileBasePath = AppProperties.getInstance()
					.getGroupUploadsFolder() + post.getGroupId();
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
		for (Post post : posts) {
			if (post.getPostType().equals(POST_TYPE.SCHEDULE)) {
				if (!post.getFromDate().before(today)
						|| !post.getToDate().after(today)) {
					continue;
				}
			}
			postTO = new PostTO(post);
			toList.add(postTO);
		}
		return toList;
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

	public void deletePost(long groupId, long postId, AppUser appUser) {
		Post post = CacheUtils.getPost(postId);
		if (post.getCreatedBy().equals(appUser.getEmail())) {
			remove(postId);
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
		for (Post post : posts) {
			if (post.getPostType().equals(POST_TYPE.SCHEDULE)) {
				if (!post.getFromDate().before(today)
						|| !post.getToDate().after(today)) {
					continue;
				}
			}
			postTO = new PostTO(post);
			toList.add(postTO);
		}
		return toList;
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

}
