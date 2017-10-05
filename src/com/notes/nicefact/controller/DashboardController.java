package com.notes.nicefact.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONException;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Tutorial;
import com.notes.nicefact.entity.TutorialFile;
import com.notes.nicefact.exception.AppException;
import com.notes.nicefact.service.AppUserService;
import com.notes.nicefact.service.CommonEntityService;
import com.notes.nicefact.service.TutorialService;
import com.notes.nicefact.to.AppUserTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.TutorialTO;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.CommonContext;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.EntityManagerHelper;
import com.notes.nicefact.util.Utils;

@Path("/dashboard")
public class DashboardController extends CommonController {

	private final static Logger logger = Logger.getLogger(DashboardController.class.getName());

	
	@GET
	@Path("/items")
	public Viewable loginPage(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		String redirect ="";
		if(CurrentContext.getAppUser() == null){
			CurrentContext.getCommonContext().setRedirectUrl(redirect);
			return new Viewable("/jsp/login.jsp", null);
		}else{
			String recirectUrl = Constants.HOME_PAGE ;
			if(StringUtils.isNotBlank(redirect)){
				recirectUrl = redirect;
			}
			response.sendRedirect(recirectUrl);
			return null;
		}
	}
	
	@GET
	@Path("/logout")
	public void logout(@Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {
		request.getSession().invalidate();
		response.sendRedirect("/");
	}

	@GET
	@Path("/verifyEmail/{code}")
	public void verifyEmail(@PathParam("code")String verifyEmailCode, @Context HttpServletRequest request,
			@Context HttpServletResponse response) throws IOException {
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try{
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.verifyEmail(verifyEmailCode);
			CurrentContext.getCommonContext().setMessage(null);
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
			CurrentContext.getCommonContext().setMessage(e.getMessage());
		}finally{
			if(em.isOpen()){
				em.close();
			}
		}
		response.sendRedirect(Constants.HOME_PAGE);
	}
	
	@POST
	@Path("/sendPasswordResetInstructions")
	public void sendPasswordResetInstructions(@FormParam("email") String email, @Context HttpServletResponse resp){
		Map<String, Object> response = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try{
			AppUserService appUserService = new AppUserService(em);
		AppUser appUser = appUserService.getAppUserByEmail(email);
		if(null == appUser){
			response.put(Constants.MESSAGE, "Account is locked or does not exist.");
			response.put(Constants.CODE,Constants.NO_RESULT);
		}else{
			appUserService.sendPasswordResetInstructions(appUser);
			response.put(Constants.CODE,Constants.RESPONSE_OK);
		}
		}finally{
			if(em.isOpen()){
				em.close();
			}
		}
		renderResponseJson(response, resp);
	}
	
	@GET
	@Path("/resetPassword/{code}")
	public Viewable resetpassword( @PathParam("code") String code, @Context HttpServletResponse response) throws IOException{
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try{
			AppUserService appUserService = new AppUserService(em);
		AppUser appUser = appUserService.getAppUserByPasswordResetCode(code);
		CommonContext commonContext = CurrentContext.getCommonContext();
		commonContext.setMessage(null);
		String errorMsg = null;
		if(appUser == null ){
			errorMsg = "Account Not found";
			commonContext.setMessage(errorMsg);
		}else if(new Date().getTime() -  appUser.getPasswordResetCodeGenDate().getTime() > Constants.RESET_CODE_EXPIRES_TIME){
			errorMsg = "Link has expired.";
			commonContext.setMessage(errorMsg);
		}else{
			commonContext.setPasswordResetCode(code);
			return new Viewable("/jsp/passwordReset", null);
		}
	}finally{
		if(em.isOpen()){
			em.close();
		}
	}
		response.sendRedirect(Constants.DASHBOARD_PAGE);
		return null;
	}

}
