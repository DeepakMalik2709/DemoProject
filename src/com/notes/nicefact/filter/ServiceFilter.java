package com.notes.nicefact.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.CommonContext;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;

import flexjson.JSONSerializer;

public class ServiceFilter implements Filter {
	private static final Logger logger = Logger.getLogger(ServiceFilter.class.getName());

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/*filter will directly forward requests for urls matching these resources*/
	private String[] skipResources = { "/assets/" , "/img/" , "/css/" , "/fonts/" };

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		boolean bypassLogin = true;
		HttpServletRequest request = ((HttpServletRequest) req);
		HttpServletResponse response = ((HttpServletResponse) resp);
		String url = request.getRequestURI();
		if (StringUtils.indexOfAny(url, skipResources) >= 0) {
			chain.doFilter(request, response);
		} else {
			CommonContext commonContext = (CommonContext) request.getSession().getAttribute(Constants.SESSION_KEY_COMMON_CONTEXT);
			AppUser user = (AppUser) request.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);
			logger.info("url : " + url + " , user : " + user);
			if (user == null && bypassLogin && !AppProperties.getInstance().isProduction()) {
				user = CacheUtils.getAppUser("jitender@nicefact.co.in");
				request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);
			}

			if (null == user && (request.getRequestURI().contains("/secure"))) {
				if (request.getRequestURI().contains("/rest")) {
					Map<String, Object> json = new HashMap<>();
					json.put(Constants.CODE, 401);
					json.put(Constants.MESSAGE, "User not logged in");
					response.setContentType("application/json; charset=UTF-8");
					try {
						response.getWriter().print(new JSONSerializer().exclude("class", "*.class", "authorities").deepSerialize(json));
						response.getWriter().flush();
						response.getWriter().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					response.sendRedirect("/a/public/login");
				}
			} else {
				/* test */
				if (null == commonContext) {
					commonContext = new CommonContext();
					request.getSession().setAttribute(Constants.SESSION_KEY_COMMON_CONTEXT, commonContext);
				}
				try {
					CurrentContext.set(commonContext, request.getSession(),  user);
					chain.doFilter(req, resp);
				} finally {
					CurrentContext.remove();
				}
			}
		}
	}

	public void destroy() {
	}
}
