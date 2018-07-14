package com.notes.nicefact.controller.profile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.notes.nicefact.controller.CommonController;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Certificate;
import com.notes.nicefact.entity.InstituteMember;
import com.notes.nicefact.request.profile.PersonalInfoRequest;
import com.notes.nicefact.request.profile.ProfileInfoRequest;
import com.notes.nicefact.service.AppUserService;
import com.notes.nicefact.service.InstituteService;
import com.notes.nicefact.service.PushService;
import com.notes.nicefact.service.profile.CertificateService;
import com.notes.nicefact.to.AppUserTO;
import com.notes.nicefact.to.InstituteMemberTO;
import com.notes.nicefact.to.InstituteTO;
import com.notes.nicefact.to.profile.CertificateTo;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.EntityManagerHelper;
import com.notes.nicefact.util.Utils;

@Path("/secure")
public class ProfileController extends CommonController {

	private final static Logger logger = Logger.getLogger(ProfileController.class);
	
	@POST
	@Path("/profile-info")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertProfileInfo(ProfileInfoRequest profileInfoRequest, @Context HttpServletResponse response) {
		logger.info("upsertProfile start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser updated = appUserService.updateProfileInfo(CurrentContext.getEmail(), profileInfoRequest);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, updated.toMap());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("upsertProfile exit");
	}
	
	@POST
	@Path("/personal-info")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updatePersonalIndo(PersonalInfoRequest personalInfoRequest, @Context HttpServletResponse response) {
		logger.info("upsertProfile start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser updated = appUserService.updatePersonalInfo(CurrentContext.getEmail(), personalInfoRequest);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, updated.toMap());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("upsertProfile exit");
	}
	
	@POST
	@Path("/profile")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertProfile(AppUserTO appUserTO, @Context HttpServletResponse response) {
		logger.info("upsertProfile start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser updated = appUserService.updateAppUser(CurrentContext.getEmail(), appUserTO);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, updated.toMap());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("upsertProfile exit");
	}
	
	@GET
	@Path("/profile/{id}")
	public void fetchContext(@PathParam("id") long id, @Context HttpServletRequest req, @Context HttpServletResponse response) {
		Map<String, Object> json = new HashMap<>();
		json.put(Constants.CODE, Constants.RESPONSE_OK);
		AppUser user = CurrentContext.getAppUser();
		
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		AppUserService appUserService = new AppUserService(em);
		
		AppUser profileUser = null;
		if(id == -1){
			profileUser = user;
		}else{
			profileUser = appUserService.get(id);
		}
		
		if(null == profileUser) {
			// No user exist
		} else {
			try {
				// User Institutes
				InstituteService instituteService = new InstituteService(em);
				List<InstituteMember> instituteMemberList = instituteService.fetchJoinedInstituteMembers(profileUser.getEmail());
				Utils.detachInstituteMembers(instituteMemberList);
				
				List<InstituteTO> institutes = new ArrayList<>();
				List<InstituteMemberTO> instituteMembers = new ArrayList<>();
				
				// If profile user is the logged in user
				if(user.getEmail().equals(profileUser.getEmail())) {
					if (StringUtils.isBlank(user.getRefreshTokenAccountEmail())) {
						if (null == user.getGoogleDriveMsgDate() || ((new Date().getTime() - user.getGoogleDriveMsgDate().getTime()) > (1 * 24 * 60 * 60 * 1000))) {
							AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
							dbUser.setGoogleDriveMsgDate(new Date());
							appUserService.upsert(dbUser);
							req.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, dbUser);
						}
					}
					
					if (null == instituteMemberList) {
						req.getSession().setAttribute(Constants.SESSION_INSTITUTES, instituteMemberList);
					}
					
					if(instituteMemberList.isEmpty()){
						if (null == user.getAddInstituteMsgDate() || ((new Date().getTime() - user.getAddInstituteMsgDate().getTime()) > (1 * 24 * 60 * 60 * 1000))) {
							AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
							dbUser.setAddInstituteMsgDate(new Date());
							appUserService.upsert(dbUser);
							req.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, dbUser);
						}
					}
				}
				
				if(!instituteMemberList.isEmpty()) {
					InstituteTO instituteTO;
					InstituteMemberTO instituteMemberTO;
					for (InstituteMember member : instituteMemberList) {
						instituteTO = new InstituteTO(member);
						institutes.add(instituteTO);
						
						instituteMemberTO = new InstituteMemberTO(member);
						instituteMembers.add(instituteMemberTO);
					}
				}
				
				// User certificates
				List<CertificateTo> certificates = new ArrayList<>();
				
				CertificateService certificateService = new CertificateService(em);
				List<Certificate> certificates2 = certificateService.getByAppUserId(user.getId());
				
				for(Certificate certificate: certificates2) {
					certificates.add(CertificateTo.convert(certificate));
				}
				
				json.put(Constants.SESSION_INSTITUTES, institutes);
				json.put(Constants.SESSION_INSTITUTE_MEMBERS, instituteMembers);
				json.put(Constants.CERTIFICATES, certificates);
				
				Map<String, Object> pushToken = PushService.getInstance().getToken();
				json.put("pushToken" , pushToken);
				Map<String, Object> userMap = profileUser.toMap();
				
				userMap.put("haveEditAccess", user.getEmail().equals(profileUser.getEmail()));
				
				json.put(Constants.LOGIN_USER, userMap);
				json.put(Constants.APPLICATION_URL, AppProperties.getInstance().getApplicationUrl());
				json.put(Constants.CONTEXT, CurrentContext.getCommonContext().toMap());
				CurrentContext.getCommonContext().setMessage(null);
		
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
				json.put(Constants.MESSAGE, e.getMessage());
			} finally {
				if (em.isOpen()) {
					em.close();
				}
			}
		}
		
		renderResponseJson(json, response);
	}
	
	@POST
	@Path("/profile" + Constants.PHOTO_URL)
	public void uploadUserImage(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, JSONException {
		logger.info("uploadUserImage start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			String contentType = request.getContentType();
			String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);
			MultipartStream multipartStream = new MultipartStream(request.getInputStream(), boundary.getBytes(Constants.UTF_8));
			boolean nextPart = multipartStream.skipPreamble();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			while (nextPart) {
				String header = multipartStream.readHeaders();
				/* process headers , DO NOT REMOVE , UPLOAD STOPS WORKING */
				System.out.println(header);
				multipartStream.readBodyData(output);
				nextPart = multipartStream.readBoundary();
			}

			byte[] fileBytes = output.toByteArray();
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.uploadPhoto(CurrentContext.getEmail(), fileBytes);
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("uploadUserImage exit");
	}
	
	@DELETE
	@Path("/profile" + Constants.PHOTO_URL)
	public void removeUserImage(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, JSONException {
		logger.info("removeUserImage start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.removePhoto(CurrentContext.getEmail());
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);

			json.put(Constants.CODE, Constants.RESPONSE_OK);
			AppUserTO userTO = new AppUserTO(user);
			json.put(Constants.LOGIN_USER, userTO);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("removeUserImage exit");

	}
	
	@POST
	@Path("/profile/deauthorizeGoogleDrive" )
	public void deauthorizeGoogleDrive(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, JSONException {
		logger.info("deauthorizeGoogleDrive start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.deauthorizeGoogleDrive(CurrentContext.getEmail());
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);

			json.put(Constants.CODE, Constants.RESPONSE_OK);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("deauthorizeGoogleDrive exit");

	}
	
	
	@POST
	@Path("/profile/deauthorizeGoogleCalendar" )
	public void deauthorizeGoogleCalendar(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, JSONException {
		logger.info("deauthorizeGoogleDrive start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.deauthorizeGoogleDrive(CurrentContext.getEmail());
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);

			json.put(Constants.CODE, Constants.RESPONSE_OK);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("deauthorizeGoogleDrive exit");

	}
}