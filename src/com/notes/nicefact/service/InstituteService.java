package com.notes.nicefact.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
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
import com.notes.nicefact.entity.TaskSubmissionFile;
import com.notes.nicefact.enums.UserPosition;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.GroupChildrenTO;
import com.notes.nicefact.to.GroupMemberTO;
import com.notes.nicefact.to.InstituteTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.Utils;

public class InstituteService extends CommonService<Institute> {
	static Logger logger = Logger.getLogger(InstituteService.class.getSimpleName());

	EntityManager em ;
	BackendTaskService backendTaskService;
	InstituteDAO instituteDAO;
	InstituteMemberDAO instituteMemberDAO;
	AppUserService appUserService ;
	
	public InstituteService(EntityManager em) {
		this.em = em;
		instituteDAO = new InstituteDAO(em);
		instituteMemberDAO = new InstituteMemberDAO(em);
		backendTaskService  = new BackendTaskService(em);
		appUserService = new AppUserService(em);
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
		List<InstituteMember> members = instituteMemberDAO.fetchByInstituteId(instituteId, true,searchTO);
		List<GroupMemberTO> memberTos = new ArrayList<>();
		GroupMemberTO memberTO;
		for (InstituteMember instituteMember : members) {
			memberTO = new GroupMemberTO(instituteMember);
			memberTos.add(memberTO);
		}
		return memberTos;
	}

	public List<GroupMemberTO> fetchInstituteJoinRequests(long instituteId, SearchTO searchTO) {
		List<InstituteMember> members = instituteMemberDAO.fetchByInstituteId(instituteId,false, searchTO);
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
			Institute instituteDB = get(instituteTO.getId());
			if (instituteDB.getAdmins().contains(appUser.getEmail())) {
				instituteDB.updateProps(instituteTO);
				institute = upsert(instituteDB);
				institute = instituteDB;
			} else {
				throw new UnauthorizedException();
			}
		} else {
			upsert(institute);
			addMembersToNewInstitute(instituteTO, institute, appUser);
			appUser.getInstituteIds().add(institute.getId());
			backendTaskService.createInstituteAfterSaveTask(institute.getId());
		}

		FileTO fileTO = instituteTO.getBgImageFile();
		if (null != fileTO) {
			String fileBasePath = Utils.getInstituteFolderPath(institute);
			try {
				if (Files.notExists(Paths.get(fileBasePath))) {
					Files.createDirectories(Paths.get(fileBasePath));
				}
				fileBasePath += File.separator;
				String serverFilePath = fileBasePath + fileTO.getServerName();
				String tempFilePath = AppProperties.getInstance().getTempUploadsFolder() + fileTO.getServerName();
				if (Files.exists(Paths.get(tempFilePath))) {
					Files.move(Paths.get(tempFilePath), Paths.get(serverFilePath), StandardCopyOption.REPLACE_EXISTING);
					institute.setBgImageId(fileTO.getServerName());
					institute.setBgImagePath(serverFilePath);
					institute.setBgImageName(fileTO.getName());
					upsert(institute);
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}

		}

		return institute;
	}
	
	private void addMembersToNewInstitute(InstituteTO instituteTO, Institute institute, AppUser user) {
		InstituteMember member = new InstituteMember(user);
		member.setIsAdmin(true);
		member.setIsJoinRequestApproved(true);
		member.setIsBlocked(false);
		member.setJoinRequestApproveDate(new Date());
		member.setJoinRequestApprover(user.getEmail());
		member.setInstitute(institute);
		member.getPositions().add(UserPosition.ADMIN);
		Set<InstituteMember> allNewMembers = new HashSet<>();
		allNewMembers.add(member);
		institute.getAdmins().add(user.getEmail());
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
					member.setIsJoinRequestApproved(true);
					member.setIsBlocked(false);
					member.setJoinRequestApproveDate(new Date());
					member.setJoinRequestApprover(user.getEmail());
					member.setInstitute(institute);
					allNewMembers.add(member);
				}
			}
		}
		instituteMemberDAO.upsertAll(allNewMembers);
		
		AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
		dbUser.getInstituteIds().add(institute.getId());
		appUserService.upsert(dbUser);
	}


	public List<InstituteMember> getMembers(Long instituteId, SearchTO searchTO) {
		return instituteMemberDAO.fetchByInstituteId(instituteId,true, searchTO);
	}

	public void updateMember(InstituteMember member) {
		instituteMemberDAO.upsert(member);
	}

	public List<Institute> searchInstitutes( SearchTO searchTO) {
		logger.info(" searchInstitutes , searchTO : " + searchTO);
		return instituteDAO.search(searchTO);
	}

	public InstituteMember joinInstitute(long instituteId, AppUser user) {
		InstituteMember member =  instituteMemberDAO.fetchMemberByEmail(instituteId, user.getEmail());
		if (member == null) {
			Institute institute = get(instituteId);
			if(institute == null){
				throw new ServiceException("Instiute not found for id : " + instituteId);
			}
			member = new InstituteMember(user);
			member.setIsJoinRequestApproved(false);
			member.setIsBlocked(true);
			
			member.setInstitute(institute);
			instituteMemberDAO.upsert(member);
			AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
			dbUser.getJoinRequestInstitutes().add(instituteId);
			appUserService.upsert(dbUser);
		}
		return member;
	}
	
	public InstituteMember approveJoinInstitute(long instituteId, AppUser user) {
		InstituteMember member =  instituteMemberDAO.fetchMemberByEmail(instituteId, user.getEmail());
		if (member != null && !member.getIsJoinRequestApproved()) {
			member.setIsJoinRequestApproved(true);
			member.setIsBlocked(false);
			member.setJoinRequestApproveDate(new Date());
			member.setJoinRequestApprover(user.getEmail());
			instituteMemberDAO.upsert(member);
			AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
			dbUser.getJoinRequestInstitutes().remove(instituteId);
			dbUser.getInstituteIds().add(instituteId);
			appUserService.upsert(dbUser);
		}
		return member;
	}
	
	public List<InstituteMember> fetchJoinedInstituteMembers(String email) {
		return instituteMemberDAO.fetchJoinedInstituteMembers(email);
	}

	public InstituteMember fetchInstituteMember(Long instituteId, String email) {
		return instituteMemberDAO.fetchMemberByEmail(instituteId, email);
	}
}
