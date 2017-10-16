package com.notes.nicefact.comparator;

import java.util.Comparator;

import com.notes.nicefact.entity.TaskSubmission;

public class TaskSubmissionComparator implements Comparator<TaskSubmission> {

	@Override
	public int compare(TaskSubmission o1, TaskSubmission o2) {
		int result = 0;

		if (o1 == null || o2 == null)
			return result;

		result = o1.getCreatedTime().compareTo(o2.getCreatedTime());

		return result;
	}

}
