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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.notes.nicefact.controller.CommonController;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.quiz.entity.Question;
import com.notes.nicefact.quiz.service.QuestionService;
import com.notes.nicefact.quiz.to.QuestionTO;
import com.notes.nicefact.quiz.to.QuizTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/question")
public class QuestionController extends CommonController {

	private final static Logger logger = Logger.getLogger(QuestionController.class.getName());

	@GET
	@Path("/getAll")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getAllQuestion( @Context HttpServletResponse response,@Context HttpServletRequest request) {
		logger.info("get all question start , postId : ");
		
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			QuestionService questionService = new QuestionService(em);		
			List<QuestionTO> questions = questionService.getAllActive();
			
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEMS, questions);
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
	
	@POST
	@Path("/upsert")	
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertQuestion(QuestionTO QuestionTO, @Context HttpServletResponse response,@Context HttpServletRequest request) {
		logger.info("upsert Question start , postId : ");
		System.out.println("Question :" + QuestionTO);
		Map<String, Object> json=null;
		EntityManager em=null;
		QuestionService questionService=null;
		QuestionTO savedTO=null;
		try {
			json = new HashMap<>();
			em = EntityManagerHelper.getDefaulteEntityManager();
			questionService = new QuestionService(em);
			AppUser user = (AppUser) request.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);
			Question question = questionService.upsertQuestion(QuestionTO,user);	
			savedTO = new QuestionTO(question);
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
	
}