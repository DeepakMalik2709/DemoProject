package com.notes.nicefact.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.TaskDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.Task;
import com.notes.nicefact.entity.TaskFile;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.TaskTO;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CacheUtils;

public class TaskService extends CommonService<Task> {
	static Logger logger = Logger.getLogger(TaskService.class.getSimpleName());

	private TaskDAO taskDAO;
	BackendTaskService backendTaskService;
	NotificationService notificationService;

	public TaskService(EntityManager em) {
		taskDAO = new TaskDAO(em);
		backendTaskService = new BackendTaskService(em);
		notificationService = new NotificationService(em);
	}

	@Override
	protected CommonDAO<Task> getDAO() {
		return taskDAO;
	}

	private void updateAttachedFiles(Task post, TaskTO postTo) {
		try {
			String fileBasePath = AppProperties.getInstance().getGroupUploadsFolder() + post.getGroupId();
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
			for (Iterator<TaskFile> postFileIter = post.getFiles().iterator(); postFileIter.hasNext();) {
				TaskFile postFile = postFileIter.next();
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
						TaskFile postFile = new TaskFile(fileTO, serverFilePath);
						postFile.setTask(post);
						post.getFiles().add(postFile);
					}
				}
			}
		} catch (IOException e) {
			logger.error("error for post Id : " + post.getId() + " , " + e.getMessage(), e);
		}
	}

	public Task upsertTask(TaskTO postTo, AppUser appUser) {
		if (StringUtils.isBlank(postTo.getComment())) {
			throw new ServiceException(" Task details cannot be empty");
		}
		if (postTo.getGroupId() == null) {
			throw new ServiceException(" Group id cannot be null");
		}
		Task post = new Task(postTo);
		Group group = CacheUtils.getGroup(postTo.getGroupId());
		if (group.getAdmins().contains(appUser.getEmail())) {
			if (group.getBlocked().contains(appUser.getEmail())) {
				throw new UnauthorizedException("User has been blocked by group admin.");
			}

			if (null == postTo.getId() || postTo.getId() <= 0) {
				updateAttachedFiles(post, postTo);
				taskDAO.upsert(post);
				backendTaskService.saveTaskTask(post);

			} else {
				Task postDB = taskDAO.get(postTo.getId());
				if (postDB.getCreatedBy().equals(appUser.getEmail())) {
					postDB.updateProps(post);
					updateAttachedFiles(postDB, postTo);
					taskDAO.upsert(postDB);
					backendTaskService.saveTaskTask(postDB);
					return postDB;
				} else {
					throw new UnauthorizedException("You cannot edit this post.");
				}
			}

		} else {
			throw new UnauthorizedException("You cannot create task for this group.");
		}
		return post;
	}

	public List<TaskTO> searchTasks(SearchTO searchTO) {
		List<Task> posts = taskDAO.search(searchTO);
		List<TaskTO> toList = new ArrayList<>();
		TaskTO postTO;
		for (Task post : posts) {
			postTO = new TaskTO(post);
			toList.add(postTO);
		}
		return toList;
	}
}
