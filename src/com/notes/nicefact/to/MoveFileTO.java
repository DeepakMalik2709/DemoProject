package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.List;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.service.GoogleDriveService.FOLDER;

/**
 * @author jkb
 * 
 * used to single / multiple files. 
 * Construct new object if file owner changes. eg files A,B,C has owner X and file D has owner Y , then make 2 objects for (A,B,C, X) and (D,Y)
 *
 */
public class MoveFileTO {

	private List<String> fileIds = new ArrayList<>();
	
	private String fileOwner;
	
	private Long groupId;

	AppUser user;
	
	Post post; 
	
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

	public Post getPost() {
		return post;
	}

	public MoveFileTO setPost(Post post) {
		this.post = post;
		return instance;
	}

	private static MoveFileTO instance ; 
	public static MoveFileTO getInstances(){
		instance = new MoveFileTO();
		return instance;
	}

	@Override
	public String toString() {
		return "MoveFileTO [fileIds=" + fileIds + ", fileOwner=" + fileOwner + ", groupId=" + groupId + ", user=" + user + ", post=" + post + ", parents=" + parents + "]";
	}
	
	
}
