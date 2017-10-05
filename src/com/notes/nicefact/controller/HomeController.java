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

@Path("/public")
public class HomeController extends CommonController {

	private final static Logger logger = Logger.getLogger(HomeController.class.getName());

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public void registerUser(AppUserTO appUserTO, @Context HttpServletResponse response) {
		logger.info("getPostGroupOrder start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUserService appUserService = new AppUserService(em);
			if (StringUtils.isBlank(appUserTO.getPassword())) {
				throw new AppException("Password is required field");
			} else if (StringUtils.isBlank(appUserTO.getEmail())) {
				throw new AppException("Email is required field");
			} else if (!Utils.isValidEmailAddress(appUserTO.getEmail())) {
				throw new AppException(appUserTO.getEmail() + " is not a valid email");
			}
			AppUserTO userTo = new AppUserTO(appUserService.registerNewUser(appUserTO));
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, userTo);

		} catch (Exception e) {
			logger.error( e.getMessage(), e);
			
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		}finally{
			if(em.isOpen()){
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("getPostGroupOrder exit");
	}

	@POST
	@Path("/login")
	public void login(@FormParam("username") String username, @FormParam("password") String password, 
			@Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		logger.info("login start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			if (StringUtils.isBlank(username)) {
				throw new AppException("Username is required field");
			} else if (StringUtils.isBlank(password)) {
				throw new AppException("Password is required field");
			}
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.doLogin(username, password);
			logger.info("set user in session : " + user.getEmail()  + ", " + user );
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			if(null != CurrentContext.getCommonContext() && StringUtils.isNotBlank(CurrentContext.getCommonContext().getRedirectUrl())){
				json.put("recirectUrl", CurrentContext.getCommonContext().getRedirectUrl());
			}
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		}finally{
			if(em.isOpen()){
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("login exit");
	}

	@GET
	@Path("/login")
	public Viewable loginPage(@QueryParam("redirect")final String redirect,  @Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
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

	@POST
	@Path("/updatePassword")
	public void updatePassword( @FormParam("password") String password, @Context HttpServletResponse response,@Context HttpServletRequest request) throws IOException{
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try{
		CommonContext commonContext = CurrentContext.getCommonContext();
		String code = commonContext.getPasswordResetCode();
		commonContext.setMessage(null);
		AppUserService appUserService = new AppUserService(em);
		AppUser appUser = appUserService.getAppUserByPasswordResetCode(code);
		String errorMsg = null;
		if(appUser == null ){
			errorMsg = "Account Not found";
			commonContext.setMessage(errorMsg);
		}else if(new Date().getTime() -  appUser.getPasswordResetCodeGenDate().getTime() > Constants.RESET_CODE_EXPIRES_TIME){
			errorMsg = "Link has expired.";
			commonContext.setMessage(errorMsg);
		}else{
			appUserService.updatePassword(appUser,password);
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, appUser);
		}
	}finally{
		if(em.isOpen()){
			em.close();
		}
	}
		response.sendRedirect(Constants.DASHBOARD_PAGE);
	}
	
	@GET
	@Path("/home")
	public Viewable publicHome(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		return new Viewable("/jsp/publicHome.jsp", null);
	}
	
	@GET
	@Path("/search")
	public Viewable searchHome(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		return new Viewable("/jsp/publicHome.jsp", null);
	}
	
	
	@GET
	@Path("/tutorial/{id}")
	public void  fetchTutorial(@PathParam("id")  long id, @Context HttpServletResponse response){
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		logger.info("fetchTutorial start");
		Map<String, Object> json = new HashMap<>();
		try {
			TutorialService notesService = new TutorialService(em);
			Tutorial tutorial = notesService.get(id);
			if (null == tutorial) {
				json.put(Constants.CODE, Constants.NO_RESULT);
			}else{
				TutorialTO savedTO = new TutorialTO(tutorial);
				json.put(Constants.CODE, Constants.RESPONSE_OK);
				json.put(Constants.DATA_ITEM, savedTO);
			}
			
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		}finally{
			if(em.isOpen()){
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("fetchTutorial exit");
	}
	
	@GET
	@Path("/tutorial/search")
	public void  searchTutorial(@QueryParam("q") String searchTerm, @QueryParam("offset") int offset,@Context HttpServletRequest request,   @Context HttpServletResponse response){
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		logger.info("searchTutorial start");
		Map<String, Object> json = new HashMap<>();
		try {
			TutorialService notesService = new TutorialService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_20);
			List<Tutorial> tutorials =  notesService.search(searchTO);
			List<TutorialTO> tutorialTos = Utils.adaptTutorialTO(tutorials);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.TOTAL, tutorialTos.size());
			json.put(Constants.DATA_ITEMS, tutorialTos);
			if(!tutorialTos.isEmpty()){
				json.put(Constants.NEXT_LINK, searchTO.getNextLink());
			}
			
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
			
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		}finally{
			if(em.isOpen()){
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("searchTutorial exit");
	}

	@GET
	@Path( "/{email}" + Constants.PHOTO_URL)
	public void getUserImage(@Context HttpServletRequest request,
			@Context HttpServletResponse response, @PathParam("email") String email)
			throws IOException, JSONException {
		logger.info("getUserImage start");
		String imgPath = Constants.DEFAULT_USER_IMAGE;
		byte[] imageBytes = null;
		imageBytes = CacheUtils.getUserPhoto(email);
		
		if (imageBytes == null) {
			response.sendRedirect(imgPath);
		}else{
			response.setContentType("image/png");
			response.getOutputStream().write(imageBytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
		logger.info("getUserImage exit");
	}
	
	@GET
	@Path("/trending")
	public void  fetchTrending(@Context HttpServletRequest request,   @Context HttpServletResponse response){
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		logger.info("searchTutorial start");
		Map<String, Object> json = new HashMap<>();
		try {
			TutorialService notesService = new TutorialService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_5);
			List<Tutorial> tutorials =  notesService.fetchTrendingTutorialList(searchTO);
			List<TutorialTO> tutorialTos = Utils.adaptTutorialTO(tutorials);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.TOTAL, tutorialTos.size());
			json.put(Constants.DATA_ITEMS, tutorialTos);
			if(!tutorialTos.isEmpty()){
				json.put(Constants.NEXT_LINK, searchTO.getNextLink());
			}
			
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
			
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		}finally{
			if(em.isOpen()){
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("searchTutorial exit");
	}
	
	@GET
	@Path("/tutorial/download")
	public void downloadTutorailFile(@QueryParam("name") String serverName, @Context HttpServletRequest req, @Context HttpServletResponse response) {
		logger.info("downloadTutorailFile start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {

			TutorialService tutorialService = new TutorialService(em);
			CommonEntityService commonEntityService = new CommonEntityService(em);
			TutorialFile tutorialFile = tutorialService.getByServerName(serverName);
			if (tutorialFile == null) {
				renderResponseRaw("File not found", response);
			} else {
				tutorialFile.incrementDownloadCount();
				commonEntityService.upsert(tutorialFile);
				byte[] fileBytes = Utils.readFileBytes(tutorialFile.getPath());
				if (null !=fileBytes) {
					downloadFile(fileBytes, tutorialFile.getName(), tutorialFile.getMimeType(), response);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		logger.info("downloadTutorailFile exit");
	}
	
	@GET
	@Path("/tutorial/file/thumbnail")
	public void generateFileThumbnail(@QueryParam("name") String serverName, @Context HttpServletRequest req, @Context HttpServletResponse response) {
		logger.info("generateFileThumbnail start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			String cacheKey = CacheUtils.getThumbnailCacheKey(serverName);
			byte[] fileBytes = (byte[]) CacheUtils.getFromCache(cacheKey);
			if (null == fileBytes) {
				TutorialService tutorialService = new TutorialService(em);
				TutorialFile tutorialFile = tutorialService.getByServerName(serverName);
				if (tutorialFile != null) {
					if (StringUtils.isNotBlank(tutorialFile.getThumbnail()) && Files.exists(Paths.get(tutorialFile.getThumbnail()))) {
						fileBytes = Utils.readFileBytes(tutorialFile.getThumbnail());
						CacheUtils.putInCache(cacheKey, fileBytes);
					}
				} 
			}
			if (null == fileBytes) {
				response.sendRedirect(Constants.NO_PREVIEW_IMAGE);
			} else {
				renderImage(fileBytes, response);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		logger.info("generateFileThumbnail exit");
	}
}
