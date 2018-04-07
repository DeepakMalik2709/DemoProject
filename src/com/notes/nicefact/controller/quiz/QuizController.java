package com.notes.nicefact.controller.quiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.notes.nicefact.controller.CommonController;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.quiz.entity.Quiz;
import com.notes.nicefact.quiz.service.QuizService;
import com.notes.nicefact.quiz.to.QuizTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/quiz")
public class QuizController extends CommonController {

	private final static Logger logger = Logger.getLogger(QuizController.class.getName());

	@POST
	@Path("/upsert")	
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertQuiz(QuizTO quizTO, @Context HttpServletResponse response,@Context HttpServletRequest request) {
		logger.info("upsert quiz start , postId : ");
		System.out.println("quiz :" + quizTO);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			QuizService quizService = new QuizService(em);
			AppUser user = (AppUser) request.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);
			Quiz quiz = quizService.upsertQuiz(quizTO, user);	
			QuizTO savedTO = new QuizTO(quiz);
			
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEMS, savedTO);
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
	
	@GET
	@Path("/play/{id}")	
	public void quizById(@PathParam("id") long id, @Context HttpServletResponse response,@Context HttpServletRequest request) {
		logger.info("play quiz start , postId : ");
		System.out.println("id :" + id);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			QuizService quizService = new QuizService(em);
			Quiz quiz = quizService.get(id);
			QuizTO savedTO = new QuizTO(quiz);
			
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEMS, savedTO);
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

	
	@GET
	@Path("/myQuiz")	
	public void myQuiz(@Context HttpServletResponse response,@Context HttpServletRequest request) {
		logger.info("my quiz start , postId : ");
		
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUser user = CurrentContext.getAppUser();	
			QuizService quizService = new QuizService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_20);			
			List<QuizTO> quizTos = quizService.fetchMyQuiz(searchTO, user);
			
			if (quizTos.isEmpty()) {
				json.put(Constants.CODE, Constants.NO_RESULT);
			} else {

				json.put(Constants.DATA_ITEMS, quizTos);
				json.put(Constants.NEXT_LINK, searchTO.getNextLink());
				json.put(Constants.CODE, Constants.RESPONSE_OK);
				json.put(Constants.TOTAL, quizTos.size());
			}
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