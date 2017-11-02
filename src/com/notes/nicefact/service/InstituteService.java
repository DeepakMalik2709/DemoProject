package com.notes.nicefact.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.InstituteDAO;
import com.notes.nicefact.dao.InstituteMemberDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Institute;
import com.notes.nicefact.entity.InstituteMember;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.to.GroupChildrenTO;
import com.notes.nicefact.to.GroupMemberTO;
import com.notes.nicefact.to.InstituteTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.Utils;

public class InstituteService extends CommonService<Institute> {
	static Logger logger = Logger.getLogger(InstituteService.class.getSimpleName());

	EntityManager em ;
	BackendTaskService backendTaskService;
	InstituteDAO instituteDAO;
	InstituteMemberDAO instituteMemberDAO;
	
	public InstituteService(EntityManager em) {
		this.em = em;
		instituteDAO = new InstituteDAO(em);
		instituteMemberDAO = new InstituteMemberDAO(em);
		backendTaskService  = new BackendTaskService(em);
	}

	@Override
	protected CommonDAO<Institute> getDAO() {
		return instituteDAO;
	}

	
	public InstituteMember toggleInstituteAdmin(long instituteId, long memberId, boolean isAdmin) {
		logger.info("toggleinstituteAdmin start , instituteId : " + instituteId  + " , member Id : " + memberId + " , isAdmin : " + isAdmin );
		InstituteMember member = instituteMemberDAO.get(memberId);
		if (member != null) {
			Institute institute = member.getInstitute();
			if (institute.getAdmins().contains(CurrentContext.getEmail())) {
				if (!isAdmin && institute.getAdmins().contains(member.getEmail()) && institute.getAdmins().size() == 1) {
					throw new ServiceException("Cannot remove only admin");
				}
				member.setIsAdmin(isAdmin);
				member = instituteMemberDAO.upsert(member);
				if (isAdmin) {
					institute.getAdmins().add(member.getEmail());
				} else {
					institute.getAdmins().remove(member.getEmail());
				}
				instituteDAO.upsert(institute);
			} else {
				throw new UnauthorizedException("You cannot update this institute");
			}
		}
		return member;
	}

	public InstituteMember toggleInstituteBlock(long instituteId, long memberId, boolean isBlocked) {
		logger.info("deleteInstituteMember start , instituteId : " + instituteId  + " , member Id : " + memberId + " , isBlocked : " + isBlocked );
		InstituteMember member = instituteMemberDAO.get(memberId);
		if (member != null) {
			Institute institute = member.getInstitute();
			if (institute.getAdmins().contains(CurrentContext.getEmail())) {
				member.setIsBlocked(isBlocked);
				member = instituteMemberDAO.upsert(member);
				if (isBlocked) {
					institute.getBlocked().add(member.getEmail());
				} else {
					institute.getBlocked().remove(member.getEmail());
				}
				instituteDAO.upsert(institute);
			} else {
				throw new UnauthorizedException("You cannot update this institute");
			}
		}
		return member;
	}
	
	public void deleteInstituteMember(long instituteId, long memberId, AppUser appUser) {
		logger.info("deleteInstituteMember start , instituteId : " + instituteId  + " , member Id : " + memberId + " , user : " + appUser.getEmail() ); 
		InstituteMember member = instituteMemberDAO.get(memberId);
		if (member != null) {
			Institute institute = member.getInstitute();
			if (institute.getAdmins().contains(appUser.getEmail())) {
				instituteMemberDAO.remove(memberId);
			} else {
				throw new UnauthorizedException("You cannot update this institute");
			}
		}
	}


	public InstituteTO addInstituteMembers(long instituteId, GroupChildrenTO children, AppUser appuser) {
		List<GroupMemberTO> members = children.getMembers();
		InstituteTO instituteTO = null;
		Institute institute = instituteDAO.get(instituteId);
		if (institute.getAdmins().contains(appuser.getEmail())) {
			Set<InstituteMember> allNewMembers = new HashSet<>();
			InstituteMember member;
			for (GroupMemberTO memberTO : members) {
				if (Utils.isValidEmailAddress(memberTO.getEmail())) {
						InstituteMember dbmember = instituteMemberDAO.fetchMemberByEmail(instituteId, memberTO.getEmail());
						if(null == dbmember){
						AppUser userHr = CacheUtils.getAppUser(memberTO.getEmail());
						if (null == userHr) {
							member = new InstituteMember(memberTO.getEmail(), memberTO.getName());
							member.setIsAppUser(false);
						} else {
							member = new InstituteMember(userHr);
						}
						member.setPositions(memberTO.getPositions());
						member.setInstitute(institute);
						allNewMembers.add(member);
					}
				}
			}

			
			institute = upsert(institute);
			instituteMemberDAO.upsertAll(allNewMembers);
			instituteTO = new InstituteTO(institute);

			for (InstituteMember addedMember : allNewMembers) {
				GroupMemberTO to = new GroupMemberTO(addedMember);
				instituteTO.getMembers().add(to);
			}

			backendTaskService.createInstituteAfterSaveTask(instituteId);
		} else {
			throw new UnauthorizedException(appuser.getEmail() + " does not have permission to edit this institute.");
		}

		return instituteTO;
	}
	
	public List<GroupMemberTO> fetchInstituteMembers(long instituteId, SearchTO searchTO) {
		List<InstituteMember> members = instituteMemberDAO.fetchByInstituteId(instituteId, searchTO);
		List<GroupMemberTO> memberTos = new ArrayList<>();
		GroupMemberTO memberTO;
		for (InstituteMember instituteMember : members) {
			memberTO = new GroupMemberTO(instituteMember);
			memberTos.add(memberTO);
		}
		return memberTos;
	}

	public Institute upsert(Institute institute) {
		Institute db = super.upsert(institute);
		CacheUtils.addInstituteToCache(db);
		return db;
	}
	
	public Institute upsert(InstituteTO instituteTO, AppUser appUser) {
		Institute institute = new Institute(instituteTO);
		if (instituteTO.getId() > 0) {
			Institute tutorialDB = get(instituteTO.getId());
			if (tutorialDB.getAdmins().contains(appUser.getEmail())) {
				tutorialDB.updateProps(instituteTO);
				upsert(tutorialDB);
				return tutorialDB;
			} else {
				throw new UnauthorizedException();
			}
		} else {
			addMembersToNewInstitute(instituteTO, institute, appUser);
			upsert(institute);
			appUser.getInstituteIds().add(institute.getId());
		}
		backendTaskService.createInstituteAfterSaveTask(institute.getId());
		return institute;
	}
	
	private void addMembersToNewInstitute(InstituteTO instituteTO, Institute institute, AppUser appUserTO) {
		InstituteMember member = new InstituteMember(appUserTO);
		member.setIsAdmin(true);
		Set<InstituteMember> allNewMembers = new HashSet<>();
		allNewMembers.add(member);
		institute.getAdmins().add(appUserTO.getEmail());
		if (instituteTO.getMembers() != null) {
			for (GroupMemberTO memberTO : instituteTO.getMembers()) {
				if (Utils.isValidEmailAddress(memberTO.getEmail())) {
					AppUser userHr = CacheUtils.getAppUser(memberTO.getEmail());
					if (null == userHr) {
						member = new InstituteMember(memberTO.getEmail(), memberTO.getName());
						member.setIsAppUser(false);
					} else {
						member = new InstituteMember(userHr);
					}
					allNewMembers.add(member);
				}
			}
		}
		instituteMemberDAO.upsertAll(allNewMembers);
	}


	public List<InstituteMember> getMembers(Long instituteId, SearchTO searchTO) {
		return instituteMemberDAO.fetchByInstituteId(instituteId, searchTO);
	}

	public void updateMember(InstituteMember member) {
		instituteMemberDAO.upsert(member);
	}

	public List<Institute> searchInstitutes( SearchTO searchTO) {
		logger.info(" searchInstitutes , searchTO : " + searchTO);
		return instituteDAO.search(searchTO);
	}
}
