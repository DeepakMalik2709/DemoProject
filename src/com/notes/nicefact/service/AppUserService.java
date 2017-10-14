package com.notes.nicefact.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.notes.nicefact.dao.AppUserDAO;
import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.exception.AppException;
import com.notes.nicefact.exception.EmailAlreadyExistsException;
import com.notes.nicefact.to.AppUserTO;
import com.notes.nicefact.to.CommonTO;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.MailService;
import com.notes.nicefact.util.Utils;


public class AppUserService extends CommonService<AppUser> {
	static Logger logger = Logger.getLogger(AppUserService.class.getSimpleName());
	
	private AppUserDAO appUserDao;
	BackendTaskService backendTaskService ;

	public AppUserService(EntityManager em) {
		appUserDao = new AppUserDAO(em);
		backendTaskService = new BackendTaskService(em);
	}

	public AppUser registerNewUser(AppUserTO appUserTO) {
		AppUser appUser = null;
		if (!StringUtils.isBlank(appUserTO.getEmail())) {
			if (getAppUserByEmail(appUserTO.getEmail()) != null) {
				throw new EmailAlreadyExistsException();
			}
			appUser = new AppUser(appUserTO);
			if (StringUtils.isNotBlank(appUserTO.getPassword())) {
				appUser.setPassword(appUserTO.getPassword().trim());
				appUser.setIsVerified(false); //user has registered through
				Long randomNumber = RandomUtils.nextLong();
				appUser.setVerifyEmailCode(randomNumber.toString());
				backendTaskService.createSendVerifyMailTask(appUser.getEmail());
				CurrentContext.getCommonContext().setMessage("Check your email account to verify your email address.");
			} else {
				appUser.setIsVerified(true); /* logged in from facebook or google */
				backendTaskService.createSendWelcomeMailTask(appUser.getEmail());
			}
			appUser = updatePublicDetails(appUser, appUserTO);
			backendTaskService.createFirstLoginTask(appUser.getEmail());
		}

		return appUser;
	}

	public AppUserTO upsert(AppUserTO appUserTO) {
		if (!StringUtils.isBlank(appUserTO.getEmail())) {
			AppUser appUser = null;
			if (appUserTO.getId() == null) {
				registerNewUser(appUserTO);
			} else {
				appUser = get(appUserTO.getId());
				analyzeAndUpdate(appUser, appUserTO);
			}
			appUser = super.upsert(appUser);
			CacheUtils.addUserToCache(appUser);
			appUserTO = new AppUserTO(appUser);
		}
		return appUserTO;
	}

	public AppUser updatePublicDetails(AppUser appUser, AppUserTO appUserTO) {
		if (StringUtils.isNotBlank(appUserTO.getFirstName())) {
			appUser.setFirstName(appUserTO.getFirstName());
		}

		if (StringUtils.isNotBlank(appUserTO.getLastName())) {
			appUser.setLastName(appUserTO.getLastName());
		}

		if (StringUtils.isBlank(appUser.getLanguage())) {
			appUser.setLanguage(appUserTO.getLanguage());
		}

		if (StringUtils.isBlank(appUser.getTimezone())) {
			appUser.setTimezone(appUserTO.getTimezone());
		}

		if (StringUtils.isBlank(appUser.getPhotoUrl())) {
			appUser.setPhotoUrl(appUserTO.getPhotoUrl());
		} else if (StringUtils.isNotBlank(appUserTO.getPhotoUrl()) && !appUserTO.getPhotoUrl().equals(appUser.getPhotoUrl())) {
			appUser.setPhotoUrl(appUserTO.getPhotoUrl());
		}
		upsert(appUser);
		return appUser;
	}

	private void analyzeAndUpdate(AppUser appUser, AppUserTO appUserTO) {
		appUser.setFirstName(appUserTO.getFirstName());
		appUser.setLastName(appUserTO.getLastName());
		if (StringUtils.isNotBlank(appUserTO.getAddress())) {
			appUser.setAddress(appUserTO.getAddress());
		}
		if (StringUtils.isNotBlank(appUserTO.getPhoneNumber())) {
			appUser.setPhoneNumber(appUserTO.getPhoneNumber());
		}
		if (StringUtils.isNotBlank(appUserTO.getPassword())) {
			appUser.setPassword(appUserTO.getPassword());
			MailService.getInstance().sendPasswordChangeMail(appUser);
		}

	}

	/**
	 * retreive user by email id
	 * 
	 * @param email
	 * @return
	 * 
	 */
	public AppUserTO getUserTOByEmail(String email) {
		AppUserTO userTO = null;
		AppUser appUser = appUserDao.getByEmail(email);
		if (appUser != null) {
			userTO = new AppUserTO(appUser);
		}
		return userTO;
	}
	
	public AppUser getAppUserByEmail(String email) {
		AppUser appUser = appUserDao.getByEmail(email);
		CacheUtils.addUserToCache(appUser);
		return appUser;
	}

	public List<AppUser> getAllUsersByDomain(String domain) {
		return appUserDao.getAllUsersByDomain(domain);
	}

	public void updateAll(List<AppUser> appUsers) {
		super.upsertAll(appUsers);

	}

	public Object search() {
		return super.search();
	}

	public void sendPasswordResetInstructions(AppUser appUser) {
		Long randomNumber = RandomUtils.nextLong();
		appUser.setPasswordResetCode(randomNumber.toString());
		appUser.setPasswordResetCodeGenDate(new Date());
		upsert(appUser);
		MailService.getInstance().sendPasswordResetInstructions(appUser);

	}

	public AppUser getAppUserByPasswordResetCode(String code) {
		AppUser appUser = appUserDao.getAppUserByPasswordResetCode(code);
		return appUser;
	}

	public AppUser getAppUserByVerifyEmailCode(String code) {
		AppUser appUser = appUserDao.getByField("verifyEmailCode", code);
		return appUser;
	}

	public void updatePassword(AppUser appUser, String password) {
		appUser.setPasswordChagneDate(new Date());
		appUser.setPasswordResetCode(null);
		appUser.setPreviousPassword(appUser.getPassword());
		appUser.setPassword(password);
		appUserDao.upsert(appUser);

	}

	public List<AppUserTO> searchUsers() {
		String term = CurrentContext.getPagination().getSearchTerm().toLowerCase().trim();
		List<AppUser> users = appUserDao.search(term);
		List<AppUserTO> tos = new ArrayList<>();
		AppUserTO to;
		for (AppUser appUser : users) {
			to = new AppUserTO(appUser);
			tos.add(to);
		}
		return tos;
	}

	public void requestDelete(CommonTO commonTO) {
		AppUser appUser = appUserDao.get(commonTO.getId());
		appUser.setRequestedDelete(true);
		appUser.setDeleteComment(commonTO.getComment());
		appUserDao.upsert(appUser);
	}

	public void softDelete(List<AppUserTO> appUserTos) {
		List<AppUser> updated = new ArrayList<>();
		for (AppUserTO userTO : appUserTos) {
			AppUser user = get(userTO.getId());
			if (user != null) {
				user.setIsDeleted(true);
				user.setIsActive(false);
				updated.add(user);
			}
		}
		if (!updated.isEmpty()) {
			upsertAll(updated);
		}
	}

	public AppUserTO saveProfile(AppUserTO appUserTO) {
		if (!appUserTO.getId().equals(CurrentContext.getAppUser().getId())) {
			throw new AppException("Illegal Operation attempted !!");
		}

		AppUser appUser = get(appUserTO.getId());
		analyzeAndUpdate(appUser, appUserTO);
		appUser = super.upsert(appUser);
		appUserTO = new AppUserTO(appUser);
		return appUserTO;
	}

	public AppUser verifyEmail(String code) {
		AppUser appUser = appUserDao.getByField("verifyEmailCode", code);
		if (null == appUser) {
			throw new AppException("Invalid verification code.");
		}else{
			appUser.setComments("Email verified on " + new Date());
			appUser.setIsVerified(true);
			appUser.setVerifyEmailCode(null);
			super.upsert(appUser);
			backendTaskService.createSendWelcomeMailTask(appUser.getEmail());
		}
		return appUser;
	}

	@Override
	protected CommonDAO<AppUser> getDAO() {
		return appUserDao;
	}

	public AppUser doLogin(String username, String password) {
		AppUser appUser = getAppUserByEmail(username);
		if (appUser == null) {
			throw new AppException("Please check username and password.");
		} else if (StringUtils.isBlank(appUser.getPassword())) {
			throw new AppException("You have not set a password. Please use Google or Facebook login.");
		}else if(!appUser.getIsVerified()){
			backendTaskService.createSendVerifyMailTask(appUser.getEmail());
			throw new AppException("Please verify your email address by checking verification email.");
		} else if (!BCrypt.checkpw(password, appUser.getPassword())) {
				throw new AppException("Please check username and password.");
		}
		if(StringUtils.isNotBlank(appUser.getRefreshToken())){
			Utils.refreshToken(appUser);
		}
		CurrentContext.getCommonContext().setMessage(null);
		return appUser;
	}

	public void addTutorial(String email) {
		AppUser appUser = getAppUserByEmail(email);
		appUser.setNoOfTutorials(appUser.getNoOfTutorials() +1 );
		upsert(appUser);
		if(CurrentContext.getSession() !=null){
			CurrentContext.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, appUser);
		}
	}

	public AppUser updateAppUser(String email, AppUserTO appUserTO) {
		AppUser appUser = getAppUserByEmail(email);
		Utils.updateAppUserFromTo(appUser, appUserTO);
		appUser = upsert(appUser);
		if(CurrentContext.getSession() !=null){
			CurrentContext.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, appUser);
		}
		CacheUtils.addUserToCache(appUser);
		return  appUser;
	}

	public AppUser removePhoto(String email) {
		AppUser user = getAppUserByEmail(email);
		user.setUploadedPhotoPath(null);
		user = upsert(user);
		CacheUtils.removeUserPhoto(email);
		CacheUtils.addUserToCache(user);
		return user;
	}

	public AppUser uploadPhoto(String email, byte[] fileBytes) {
		String filePath = AppProperties.getInstance().getUserProfilePhotoFolder() + CurrentContext.getEmail()  + "_profile_photo.png";
		Path photoFolderPath = Paths.get(AppProperties.getInstance().getUserProfilePhotoFolder());
		java.nio.file.Path output1 = Paths.get(filePath );
		try {
			if(Files.notExists(photoFolderPath)){
				Files.createDirectories(photoFolderPath);
			}
			Files.deleteIfExists(output1);
			Files.write(output1, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
		}
		CacheUtils.removeUserPhoto(email);
		
		AppUser user = getAppUserByEmail(email);
		user.setUploadedPhotoPath(filePath);
		user = upsert(user);
		CacheUtils.addUserToCache(user);
		return user;
	}

	/** as of now it is not possible to revoke access by scope. so using common method */
	public AppUser deauthorizeGoogleDrive(String email) {
		AppUser user = getAppUserByEmail(email);
		if(StringUtils.isNotBlank(user.getRefreshToken())){
			Utils.revokeToken(user.getRefreshToken());
			user.setAccessToken(null);
			user.setRefreshTokenAccountEmail(null);
			user.setRefreshToken(null);
			user.getScopes().clear();
			user = upsert(user);
			CacheUtils.addUserToCache(user);
		}
		return user;
	}

	
}
