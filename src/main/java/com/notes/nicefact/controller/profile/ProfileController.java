package com.notes.nicefact.controller.profile;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.notes.nicefact.controller.CommonController;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.request.profile.PersonalInfoRequest;
import com.notes.nicefact.request.profile.ProfileInfoRequest;
import com.notes.nicefact.service.AppUserService;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.EntityManagerHelper;

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
}