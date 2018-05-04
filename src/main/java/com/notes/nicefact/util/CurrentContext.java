package com.notes.nicefact.util;

import javax.servlet.http.HttpSession;

import com.notes.nicefact.entity.AppUser;

/**
 * 
 * 
 */
public class CurrentContext {

	private static ThreadLocal<InfoWrapper> currentContext = new ThreadLocal<InfoWrapper>();

	public static void set(CommonContext commonContext, HttpSession session,  AppUser  user) {
		if (currentContext.get() == null && !(commonContext == null && user == null)) {
			currentContext.set(new InfoWrapper(commonContext, session,  user));
		}
	}

	private static class InfoWrapper {

		CommonContext commonContext;

		AppUser user;
		HttpSession session;
		Pagination pagination;

		// private Pagination pagination;

		public InfoWrapper(CommonContext commonContext, HttpSession session,  AppUser user) {
			super();
			this.commonContext = commonContext;
			this.session = session;
			this.user = user;
		}

	};

	/**
	 * @return the user
	 */
	public static Pagination getPagination() {
		return currentContext.get() == null ? null : currentContext.get().pagination;
	}

	public static CommonContext getCommonContext() {
		return currentContext.get() == null ? null : currentContext.get().commonContext;
	}

	public static String getEmail() {
		return currentContext.get() == null ? null : currentContext.get().user.getEmail();
	}

	public static AppUser getAppUser() {
		return currentContext.get() == null ? null : currentContext.get().user;
	}

	public static HttpSession getSession() {
		return currentContext.get() == null ? null : currentContext.get().session;
	}

	/**
	 * remove the thread local to protect memory leak
	 */
	public static void remove() {
		currentContext.remove();
	}

}
