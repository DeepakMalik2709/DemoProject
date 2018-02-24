package com.notes.nicefact.service;

import java.util.concurrent.Future;

import javax.persistence.EntityManager;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.BackendTaskDAO;
import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.BackendTask;
import com.notes.nicefact.entity.BackendTask.BackendTaskStatus;
import com.notes.nicefact.entity.Notification;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostComment;
import com.notes.nicefact.entity.Tutorial;
import com.notes.nicefact.util.Constants;

public class BackendTaskService extends CommonService<BackendTask> {
	static Logger logger = Logger.getLogger(BackendTaskService.class.getSimpleName());

	private BackendTaskDAO backendTaskDAO;

	public BackendTaskService(EntityManager em) {
		backendTaskDAO = new BackendTaskDAO(em);
	}

	@Override
	protected CommonDAO<BackendTask> getDAO() {
		return backendTaskDAO;
	}

	public void saveAndTrigger(BackendTask task) {
		task.setStatus(BackendTaskStatus.QUEUED);
		BackendTask savedTask = super.upsert(task);
		if (null != savedTask) {
			triggerBackendTask(savedTask);
		}
	}

	public void triggerBackendTask(BackendTask task) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(Constants.LOCALHOST_ADDRESS + "/a/backend/").path("run").queryParam("taskId", task.getId());
		// target.request().get();
		AsyncInvoker asyncInvoker = target.request().async();
		final Future<Response> responseFuture = asyncInvoker.post(null);
	}

	public void runBackendTask(BackendTask task) {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(Constants.LOCALHOST_ADDRESS + "/a/backend/").path(task.getPath());
		if (!task.getParamsMap().isEmpty()) {
			for (String key : task.getParamsMap().keySet()) {
				target.queryParam(key, task.getParamsMap().get(key));
			}
		}
		AsyncInvoker asyncInvoker = target.request().async();
		final Future<Response> responseFuture = asyncInvoker.post(null);
	}

	public void saveTutorialTask(Tutorial tutorial) {
		if (!tutorial.getFiles().isEmpty()) {
			BackendTask task = new BackendTask();
			task.setPath("tutorial/saveTutorialTask");
			task.addToParamsMap("tutorialId", tutorial.getId());
			saveAndTrigger(task);
		}
	}
	
	public void savePostTask(Post post) {
		BackendTask task = new BackendTask();
		task.setPath("post/generateGroupPostCreatedNotification");
		task.addToParamsMap("postId", post.getId());
		saveAndTrigger(task);
		if (!post.getFiles().isEmpty()) {
			task = new BackendTask();
			task.setPath("post/addThumbnail");
			task.addToParamsMap("postId", post.getId());
			saveAndTrigger(task);

		}
	}
	
	public void saveTaskTask(Post post) {
		BackendTask task = new BackendTask();
		task.setPath("task/afterSave");
		task.addToParamsMap("taskId", post.getId());
		saveAndTrigger(task);
		if (!post.getFiles().isEmpty()) {
			task = new BackendTask();
			task.setPath("task/addThumbnail");
			task.addToParamsMap("taskId", post.getId());
			saveAndTrigger(task);

		}
	}

	
	public void saveScheduleTask(Post post) {
		BackendTask task = new BackendTask();
		task.setPath("schedule/afterSave");
		task.addToParamsMap("scheduleId", post.getId());
		saveAndTrigger(task);
		if (!post.getFiles().isEmpty()) {
			task = new BackendTask();
			task.setPath("schedule/addThumbnail");
			task.addToParamsMap("taskId", post.getId());
			saveAndTrigger(task);

		}
	}

	
	public void createGoogleDriveFolderForUserTask(AppUser user) {
		BackendTask task = new BackendTask();
		task.setPath("user/createGoogleDriveFolder");
		task.addToParamsMap("email", user.getEmail());
		saveAndTrigger(task);

	}

	public void postCommentedTask(Post post, PostComment comment) {
		BackendTask task = new BackendTask();
		task.setPath("post/generateCommentedNotification");
		task.addToParamsMap("postId", post.getId());
		task.addToParamsMap("commentId", comment.getId());
		saveAndTrigger(task);
	}

	public void postCommentReplyTask(Post post, PostComment comment, PostComment subComment) {
		BackendTask task = new BackendTask();
		task.setPath("post/generateCommentRepliedNotification");
		task.addToParamsMap("postId", post.getId());
		task.addToParamsMap("commentId", comment.getId());
		task.addToParamsMap("commentReplyId", subComment.getId());
		saveAndTrigger(task);
	}

	public void createSendNotificationMailsTask(Notification notification) {
		if (null != notification && null != notification.getId()) {
			BackendTask task = new BackendTask();
			task.setPath("post/sendNotificationMails");
			task.addToParamsMap("notificationId", notification.getId());
			saveAndTrigger(task);
		}
	}
	
	public void createSendPushNotificationTask(Notification notification) {
		if (null != notification && null != notification.getId()) {
			BackendTask task = new BackendTask();
			task.setPath("post/sendPushNotifications");
			task.addToParamsMap("notificationId", notification.getId());
			saveAndTrigger(task);
		}
	}

	public void createMarkNotificationReadTask(String email) {
		BackendTask task = new BackendTask();
		task.setPath("markNotificationAsRead");
		task.addToParamsMap("email", email);
		saveAndTrigger(task);
	}
	
	
	public void createSendGroupAddNotificationTask(Long groupId) {
		BackendTask task = new BackendTask();
		task.setPath("group/sendGroupAddNotification");
		task.addToParamsMap("groupId", groupId);
		saveAndTrigger(task);
	}
	
	public void createAfterGroupSaveTask(Long groupId) {
		BackendTask task = new BackendTask();
		task.setPath("group/afterSave");
		task.addToParamsMap("groupId", groupId);
		saveAndTrigger(task);
	}
	
	public void createSendWelcomeMailTask(String email) {
		BackendTask task = new BackendTask();
		task.setPath("sendWelcomeMail");
		task.addToParamsMap("email", email);
		saveAndTrigger(task);
	}
	
	public void createSendVerifyMailTask(String email) {
		BackendTask task = new BackendTask();
		task.setPath("sendVerifyMail");
		task.addToParamsMap("email", email);
		saveAndTrigger(task);
	}
	
	public void createFirstLoginTask(String email) {
		BackendTask task = new BackendTask();
		task.setPath("user/firstLogin");
		task.addToParamsMap("email", email);
		saveAndTrigger(task);
	}

	public void saveTaskSubmissionTask(Long taskId, Long submissionId) {
		BackendTask task = new BackendTask();
		task.setPath("task/submission");
		task.addToParamsMap("taskId", taskId);
		task.addToParamsMap("submissionId", submissionId);
		saveAndTrigger(task);
		
	}

	public void createInstituteAfterSaveTask(Long instituteId) {
		BackendTask task = new BackendTask();
		task.setPath("institute/afterSave");
		task.addToParamsMap("instituteId", instituteId);
		saveAndTrigger(task);
	}
}
