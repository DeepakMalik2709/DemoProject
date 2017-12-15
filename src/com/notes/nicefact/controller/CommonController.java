package com.notes.nicefact.controller;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import flexjson.JSONSerializer;

public class CommonController {

	private final static Logger logger = Logger.getLogger(CommonController.class);

	public void renderResponseJson(Object object, HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		try {
			System.out.println(new JSONSerializer().exclude("class", "*.class", "authorities").deepSerialize(object));
			response.getWriter().print(new JSONSerializer().exclude("class", "*.class", "authorities").deepSerialize(object));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void renderResponseRaw(Object object, HttpServletResponse response) {
		try {
			response.getWriter().print(object);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void renderImage(byte[] fileBytes, HttpServletResponse response) {
		response.setContentType("image/png");
		try {
			response.getOutputStream().write(fileBytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void downloadFile(byte[] fileBytes, String title, String mimeType, HttpServletResponse response) {
		try {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + StringEscapeUtils.escapeJava(title) + "\"");
			response.setContentLength(fileBytes.length);
			response.setContentType(mimeType);
			ServletOutputStream outStream = response.getOutputStream();
			outStream.write(fileBytes);
			outStream.flush();
			outStream.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
