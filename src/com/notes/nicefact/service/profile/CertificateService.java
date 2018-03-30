package com.notes.nicefact.service.profile;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.profile.CertificateDao;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Certificate;
import com.notes.nicefact.service.AppUserService;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonService;
import com.notes.nicefact.to.profile.CertificateTo;
import com.notes.nicefact.util.EntityManagerHelper;

public class CertificateService extends CommonService<Certificate> {

	private static Logger logger = Logger.getLogger(CertificateService.class.getSimpleName());
	
	private CertificateDao certificateDao;
	private BackendTaskService backendTaskService;
	
	public CertificateService(EntityManager em) {
		certificateDao = new CertificateDao(em);
		backendTaskService = new BackendTaskService(em);
	}
	
	@Override
	protected CommonDAO<Certificate> getDAO() {
		return certificateDao;
	}
	
	public Certificate save(CertificateTo certificateRequest) {
		Certificate certificate = new Certificate();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		AppUserService appUserService = new AppUserService(em);
		
		AppUser appUser = appUserService.get(certificateRequest.getAppUserId());
		
		certificate.setGrade(certificateRequest.getGrade());
		certificate.setImage(certificateRequest.getImage());
		certificate.setName(certificateRequest.getName());
		certificate.setOrganisation(certificateRequest.getOrganisation());
		certificate.setAppUser(appUser);
		
		certificate = upsert(certificate);
		
		return certificate;
	}
	
	public Certificate update(CertificateTo certificateRequest) {
		Certificate certificate = getById(certificateRequest.getCertificateId());
		
		if(certificate != null) {
			certificate.setGrade(certificateRequest.getGrade());
			certificate.setImage(certificateRequest.getImage());
			certificate.setName(certificateRequest.getName());
			certificate.setOrganisation(certificateRequest.getOrganisation());
			
			certificate = upsert(certificate);
		}
		
		return certificate;
	}

	public Certificate getById(Long id) {
		Certificate certificate = certificateDao.get(id);
		return certificate;
	}
	
	public List<Certificate> getByAppUserId(Long id) {
		List<Certificate> certificates = certificateDao.getByAppUserId(id);
		return certificates;
	}
	
	public Certificate upsert(Certificate certificate) {
		Certificate db = super.upsert(certificate);
		return db;
	}
}
