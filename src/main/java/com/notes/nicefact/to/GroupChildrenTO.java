package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupChildrenTO {
	
	List<GroupMemberTO> members = new ArrayList<>();
	
	List<SelectBoxTO> groups = new ArrayList<>();

	public List<GroupMemberTO> getMembers() {
		if(null == members){
			return new ArrayList<>();
		}
		return members;
	}

	public void setMembers(List<GroupMemberTO> members) {
		this.members = members;
	}

	public List<SelectBoxTO> getGroups() {
		if(null == groups){
			return new ArrayList<>();
		}
		return groups;
	}

	public void setGroups(List<SelectBoxTO> groups) {
		this.groups = groups;
	}
	
	

}
