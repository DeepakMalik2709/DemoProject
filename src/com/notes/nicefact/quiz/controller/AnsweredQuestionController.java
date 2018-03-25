package com.notes.nicefact.quiz.controller;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.notes.nicefact.controller.CommonController;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.quiz.service.AnsweredQuestionService;
import com.notes.nicefact.quiz.to.StudentQuizTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/anweredQuestion")
public class AnsweredQuestionController extends CommonController {

	private final static Logger logger = Logger.getLogger(AnsweredQuestionController.class.getName());

	@POST
	@Path("/upsert")	
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveAnswere(StudentQuizTO studentQuizTO, @Context HttpServletResponse response,@Context HttpServletRequest request) {
		logger.info("get all question start , postId : ");
		
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AnsweredQuestionService ansQuestionService = new AnsweredQuestionService(em);
			AppUser user = (AppUser) request.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);	
			
			ansQuestionService.upsertStudentAnswere(studentQuizTO,user);
			if(!studentQuizTO.getStatus().equalsIgnoreCase("Not Started")){
				ansQuestionService.upsertStudentQuiz(studentQuizTO,user);	
			}
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			
		}  catch (Exception e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("createEvent exit");
	}
	
	
	
}