package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.List;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.service.GoogleDriveService.FOLDER;

public class MoveFileTO {

	private List<String> fileIds = new ArrayList<>();
	
	private String fileOwner;
	
	private Long groupId;

	AppUser user;
	
	List<FOLDER> parents = new ArrayList<>();
	
	public List<String> getFileIds() {
		return fileIds;
	}

	public MoveFileTO addFileIds(String... fileIds) {
		for (String id : fileIds) {
			this.fileIds.add(id);
		}
		return instance;
	}

	public String getFileOwner() {
		return fileOwner;
	}

	public MoveFileTO setFileOwner(String fileOwner) {
		this.fileOwner = fileOwner;
		return instance;
	}

	public Long getGroupId() {
		return groupId;
	}

	public MoveFileTO setGroupId(Long groupId) {
		this.groupId = groupId;
		return instance;
	}
	
	public AppUser getUser() {
		return user;
	}

	public MoveFileTO setUser(AppUser appuser) {
		this.user = appuser;
		return instance;
	}

	public List<FOLDER> getParents() {
		return parents;
	}

	public MoveFileTO addParents(FOLDER... parents) {
		for (FOLDER folder : parents) {
			this.parents.add(folder);
		}
		return instance;
	}

	private static MoveFileTO instance ; 
	public static MoveFileTO getInstances(){
		instance = new MoveFileTO();
		return instance;
	}
	
	
}
