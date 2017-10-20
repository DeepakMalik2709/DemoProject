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
		return this;
	}

	public String getFileOwner() {
		return fileOwner;
	}

	public MoveFileTO setFileOwner(String fileOwner) {
		this.fileOwner = fileOwner;
		return this;
	}

	public Long getGroupId() {
		return groupId;
	}

	public MoveFileTO setGroupId(Long groupId) {
		this.groupId = groupId;
		return this;
	}
	
	public AppUser getUser() {
		return user;
	}

	public MoveFileTO setUser(AppUser appuser) {
		this.user = appuser;
		return this;
	}

	public List<FOLDER> getParents() {
		return parents;
	}

	public MoveFileTO addParents(FOLDER... parents) {
		for (FOLDER folder : parents) {
			this.parents.add(folder);
		}
		return this;
	}

	public Post getPost() {
		return post;
	}

	public MoveFileTO setPost(Post post) {
		this.post = post;
		return this;
	}

	public static MoveFileTO getInstances(){
		MoveFileTO x = new MoveFileTO();
		return x;
	}

	@Override
	public String toString() {
		return "MoveFileTO [fileIds=" + fileIds + ", fileOwner=" + fileOwner + ", groupId=" + groupId + ", user=" + user + ", post=" + post + ", parents=" + parents + "]";
	}
	
	
}
