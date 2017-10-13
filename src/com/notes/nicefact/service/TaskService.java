package com.notes.nicefact.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.PostDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.TaskSubmission;
import com.notes.nicefact.entity.TaskSubmissionFile;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.TaskSubmissionTO;
import com.notes.nicefact.util.AppProperties;

public class TaskService extends CommonService<Post> {
	static Logger logger = Logger.getLogger(TaskService.class.getSimpleName());

	private PostDAO  postDAO;
	BackendTaskService backendTaskService;
	NotificationService notificationService;
	CommonEntityService commonEntityService;

	public TaskService(EntityManager em) {
		postDAO = new PostDAO(em);
		backendTaskService = new BackendTaskService(em);
		notificationService = new NotificationService(em);
		commonEntityService = new CommonEntityService(em);
	}

	@Override
	protected CommonDAO<Post> getDAO() {
		return postDAO;
	}

	private List<TaskSubmissionFile> updateAttachedFiles(TaskSubmission post, TaskSubmissionTO postTo, Long groupId) {
		try {
			String fileBasePath = AppProperties.getInstance().getGroupUploadsFolder() + groupId +  File.separator + post.getPostId();
			if (Files.notExists(Paths.get(fileBasePath))) {
				Files.createDirectories(Paths.get(fileBasePath));
			}
			fileBasePath +=   File.separator;
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
						Files.move(Paths.get(tempFilePath), Paths.get(serverFilePath), StandardCopyOption.REPLACE_EXISTING);
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
		Post task =  get(sumbmissionTO.getPostId());
		if (null == task) {
			throw new ServiceException(" Task not found for id : " + sumbmissionTO.getPostId());
		}
		if (task.getSubmitters().add(appUser.getEmail())) {
			submission = new TaskSubmission(sumbmissionTO);
			submission.setSubmitterEmail(appUser.getEmail());
			submission.setSubmitterName(appUser.getDisplayName());
			commonEntityService.upsert(submission);
			List<TaskSubmissionFile> files = updateAttachedFiles(submission, sumbmissionTO, task.getGroupId());
			if (!files.isEmpty()) {
				for (TaskSubmissionFile taskSubmissionFile : files) {
					commonEntityService.upsert(taskSubmissionFile);
				}
				commonEntityService.upsert(submission);
			}
			backendTaskService.saveTaskSubmissionTask(task.getId(), submission.getId());
		} else {
			throw new ServiceException(" You have already submitted for this task.");
		}
		logger.info("upsertTaskSubmission exit");
		return submission;
	}
}
