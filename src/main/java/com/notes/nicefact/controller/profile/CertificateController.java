package com.notes.nicefact.controller.profile;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.notes.nicefact.controller.CommonController;
import com.notes.nicefact.entity.Certificate;
import com.notes.nicefact.service.profile.CertificateService;
import com.notes.nicefact.to.profile.CertificateTo;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/secure")
public class CertificateController  extends CommonController {

	private final static Logger logger = Logger.getLogger(CertificateController.class);
	
	@POST
	@Path("/certificate")
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveCertificate(CertificateTo certificateRequest, @Context HttpServletResponse response) {
		logger.info("save certificate start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			CertificateService certificateService = new CertificateService(em);
			Certificate updated = certificateService.save(certificateRequest);
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
		logger.info("save certificate exit");
	}
	
	@PUT
	@Path("/certificate")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateCertificate(CertificateTo certificateRequest, @Context HttpServletResponse response) {
		logger.info("upsert certificate start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			CertificateService certificateService = new CertificateService(em);
			Certificate updated = certificateService.update(certificateRequest);
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
		logger.info("upsert certificate exit");
	}
	
	@DELETE
	@Path("/certificate/{id}")
	public void deleteCertificate(@PathParam("id") long id, @Context HttpServletResponse response) {
		logger.info("delete certificate start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			CertificateService certificateService = new CertificateService(em);
			certificateService.remove(id);
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
		logger.info("delete certificate exit");
	}
}