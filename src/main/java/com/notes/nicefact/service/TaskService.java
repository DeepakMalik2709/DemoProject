package com.notes.nicefact.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.comparator.TaskSubmissionComparator;
import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.GroupMemberDAO;
import com.notes.nicefact.dao.PostDAO;
import com.notes.nicefact.dao.TaskSubmissionDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.TaskSubmission;
import com.notes.nicefact.entity.TaskSubmissionFile;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.TaskSubmissionTO;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.Utils;
import com.notes.nicefact.util.ZipUtils;

public class TaskService extends CommonService<Post> {
	static Logger logger = Logger.getLogger(TaskService.class.getSimpleName());

	private PostDAO postDAO;
	BackendTaskService backendTaskService;
	NotificationService notificationService;
	CommonEntityService commonEntityService;
	TaskSubmissionDAO taskSubmissionDAO;
	EntityManager em;

	public TaskService(EntityManager em) {
		this.em = em;
		postDAO = new PostDAO(em);
		backendTaskService = new BackendTaskService(em);
		notificationService = new NotificationService(em);
		commonEntityService = new CommonEntityService(em);
		taskSubmissionDAO = new TaskSubmissionDAO(em);
	}

	@Override
	protected CommonDAO<Post> getDAO() {
		return postDAO;
	}

	private List<TaskSubmissionFile> updateAttachedFiles(Post task, TaskSubmission post, TaskSubmissionTO postTo) {
		try {
			String fileBasePath = Utils.getTaskFolderPath(task);
			if (Files.notExists(Paths.get(fileBasePath))) {
				Files.createDirectories(Paths.get(fileBasePath));
			}
			fileBasePath += File.separator;
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
			for (Iterator<TaskSubmissionFile> postFileIter = post.getFiles().iterator(); postFileIter.hasNext();) {
				TaskSubmissionFile postFile = postFileIter.next();
				if (!filesToKeppIds.contains(postFile.getId())) {
					Files.deleteIfExists(Paths.get(postFile.getPath()));
					postFileIter.remove();
				}
			}

			for (FileTO fileTO : postTo.getFiles()) {
				if (fileTO.getId() <= 0) {
					serverFilePath = fileBasePath + fileTO.getServerName();
					tempFilePath = AppProperties.getInstance().getTempUploadsFolder() + fileTO.getServerName();
					if (Files.exists(Paths.get(tempFilePath))) {
						Files.move(Paths.get(tempFilePath), Paths.get(serverFilePath),
								StandardCopyOption.REPLACE_EXISTING);
						TaskSubmissionFile postFile = new TaskSubmissionFile(fileTO, post.getPostId(), serverFilePath);
						postFile.setSubmission(post);
						post.getFiles().add(postFile);
					}
				}
			}
		} catch (IOException e) {
			logger.error("error for post Id : " + post.getId() + " , " + e.getMessage(), e);
		}
		return post.getFiles();
	}

	public TaskSubmission upsertTaskSubmission(TaskSubmissionTO sumbmissionTO, AppUser appUser) {
		logger.info("upsertTaskSubmission start");
		TaskSubmission submission = null;
		if (sumbmissionTO.getPostId() == null || sumbmissionTO.getPostId() <= 0) {
			throw new ServiceException(" Task id cannot be null");
		}
		Post task = get(sumbmissionTO.getPostId());
		if (null == task) {
			throw new ServiceException(" Task not found for id : " + sumbmissionTO.getPostId());
		}
		if (task.getDeadline() != null && new Date().getTime() > task.getDeadline().getTime()) {
			throw new ServiceException(" Deadline has passed for : " + task.getTitle());
		}
		if (task.getSubmitters().add(appUser.getEmail())) {
			submission = new TaskSubmission(sumbmissionTO);
			taskSubmissionDAO.upsert(submission);
			List<TaskSubmissionFile> files = updateAttachedFiles(task, submission, sumbmissionTO);
			if (!files.isEmpty()) {
				for (TaskSubmissionFile taskSubmissionFile : files) {
					commonEntityService.upsert(taskSubmissionFile);
				}
				taskSubmissionDAO.upsert(submission);
			}
			backendTaskService.saveTaskSubmissionTask(task.getId(), submission.getId());
		} else {
			throw new ServiceException(" You have already submitted for this task.");
		}
		logger.info("upsertTaskSubmission exit");
		return submission;
	}

	public String getTaskSubmissionDownloadPath(long taskId) {
		String zipPath = null;
		Post task = get(taskId);
		if (null != task) {
			if (task.getZipFileDate() != null && task.getDeadline() != null
					&& task.getZipFileDate().getTime() > task.getDeadline().getTime()) {
				// use previously generated zip file
			} else {
				List<TaskSubmission> list = taskSubmissionDAO.getTAskSubmissionsForByTaskId(taskId);
				if (list.isEmpty()) {
					generateNotSubmittedZip(task, list);

				} else {
					Collections.sort(list, new TaskSubmissionComparator());
					TaskSubmission lastSubmission = list.get(list.size() - 1);
					if (task.getZipFileDate() != null
							&& task.getZipFileDate().getTime() > lastSubmission.getCreatedTime().getTime()) {
						// use previously generated zip file
					} else {
						task = generateTaskSubmissionZip(task, list);

					}
				}

			}
			zipPath = task.getZipFilePath();
		}
		return zipPath;
	}

	private void generateNotSubmittedZip(Post task, List<TaskSubmission> list) {
		try {
			String basePathStr = AppProperties.getInstance().getTempUploadsFolder() + task.getId() + "_"
					+ new Date().getTime();
			Path basePath = Paths.get(basePathStr);
			if (Files.notExists(basePath)) {
				Files.createDirectories(basePath);
			}
			makeNotSubmittedCsv(task, list, basePath);
			String zipFilePath = Utils.getTaskFolderPath(task) + File.separator + task.getId() + ".zip";
			ZipUtils.zipFolder(basePathStr, zipFilePath);
			ZipUtils.zipFolder(basePathStr, zipFilePath);
			task.setZipFileDate(new Date());
			task.setZipFilePath(zipFilePath);
			postDAO.upsert(task);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private Post generateTaskSubmissionZip(Post task, List<TaskSubmission> list) {
		logger.info(" start generateTaskSubmissionZip , taskId : " + task.getId() + ", submissions : " + list.size());
		try {
			GoogleDriveService driveService = GoogleDriveService.getInstance();
			String basePathStr = AppProperties.getInstance().getTempUploadsFolder() + task.getId() + "_"
					+ new Date().getTime();
			String zipFilePath = Utils.getTaskFolderPath(task) + File.separator + task.getId() + ".zip";
			Path basePath = Paths.get(basePathStr);
			if (Files.notExists(basePath)) {
				Files.createDirectories(basePath);
			}
			String header = "Name,Email,Date,Files\n";
			StringBuffer sb = new StringBuffer(header);
			AppUser user = CacheUtils.getAppUser(task.getCreatedBy());
			DateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT_1);
			Path path;
			for (TaskSubmission submission : list) {
				sb.append("\"" + submission.getCreatedByName() + "\",\"" + submission.getCreatedBy() + "\",\""
						+ formatter.format(submission.getCreatedTime()) + "\",");
				for (TaskSubmissionFile file : submission.getFiles()) {
					byte[] bytes = driveService.downloadFile(file, user);
					if (bytes == null) {
						logger.error("file download failed for : TaskSubmissionFile id " + file.getId() + " , name : "
								+ file.getName() + " , sender : " + file.getCreatedBy());
						sb.append("\"download failed for : " + file.getName() + "\"");
					} else {
						String fileName = file.getCreatedByName() + "_" + file.getId() + "_" + file.getName();
						path = Paths.get(basePath + File.separator + fileName);
						Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
						sb.append("\"" + fileName + "\",");
					}
				}
				sb.append("\n");
			}
			if (sb.length() > header.length()) {
				path = Paths.get(basePath + File.separator + "submissions.csv");
				Files.write(path, sb.toString().getBytes(Constants.UTF_8), StandardOpenOption.CREATE,
						StandardOpenOption.APPEND);
			}
			makeNotSubmittedCsv(task, list, basePath);
			ZipUtils.zipFolder(basePathStr, zipFilePath);
			task.setZipFileDate(new Date());
			task.setZipFilePath(zipFilePath);
			postDAO.upsert(task);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return task;
	}

	private void makeNotSubmittedCsv(Post task, List<TaskSubmission> list, Path basePath)
			throws UnsupportedEncodingException, IOException {
		Set<String> submitters = new HashSet<>();
		for (TaskSubmission submission : list) {
			submitters.add(submission.getCreatedBy());
		}
		String header = "Name,Email\n";
		StringBuffer sb = new StringBuffer(header);
		GroupMemberDAO groupMemberDAO = new GroupMemberDAO(em);
		SearchTO searchTO = SearchTO.getInstances().setLimit(1000);
		List<GroupMember> members = groupMemberDAO.fetchGroupMembersByGroupId(task.getGroupId(), searchTO);

		for (GroupMember groupMember : members) {
			if (!submitters.contains(groupMember.getEmail())) {
				sb.append("\"" + groupMember.getName() + "\",\"" + groupMember.getEmail() + "\"\n");
			}
		}
		if (sb.length() > header.length()) {
			Path path = Paths.get(basePath + File.separator + "not_submitted.csv");
			Files.write(path, sb.toString().getBytes(Constants.UTF_8), StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
		}

	}

	public List<TaskSubmission> getTaskSubmissionsForUserByTaskIds(List<Long> taskIds, String userEmail) {
		return taskSubmissionDAO.getTaskSubmissionsForUserByTaskIds(taskIds, userEmail);
	}

	public void deleteTaskSubmission(Long postId, Long taskSubmissionId, AppUser appUser) {
		TaskSubmission taskSubmission = taskSubmissionDAO.get(taskSubmissionId);
		GoogleDriveService driveService = GoogleDriveService.getInstance();
		List<TaskSubmissionFile> files = taskSubmission.getFiles();

		for (TaskSubmissionFile file : files) {
			driveService.deleteFile(file.getGoogleDriveId(), appUser);
		}
		
		taskSubmissionDAO.remove(taskSubmission.getId());
		
		Post post = postDAO.get(postId);
		
		if(!post.getSubmitters().add(appUser.getEmail())){
			post.getSubmitters().remove(appUser.getEmail());
			postDAO.upsert(post);
		}

	}
}
