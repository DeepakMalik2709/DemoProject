package com.notes.nicefact.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.service.AppUserService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.PostService;

import net.spy.memcached.MemcachedClient;

public class CacheUtils {

	private static final int MEMCACHE_NODE_1_PORT = 11211;

	private static final String MEMCACHE_NODE_1 = "127.0.0.1";

	public static final Logger logger = Logger.getLogger(Utils.class.getSimpleName());

	private final static Integer CACHE_TIMEOUT = 10 * 24 * 60 * 60; // seconds
																	// in 10
																	// days.

	public static Group getGroup(Long id) {
		String cacheKey = generateGroupKey(id);
		Group group = (Group) getFromCache(cacheKey);
		if (null == group) {
			EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
			GroupService groupService = new GroupService(em);
			group = groupService.get(id);
			if (null!=group) {
				group.getMembers().size();
				group.getMemberGroupsIds().size();
				em.detach(group);
				putInCache(cacheKey, group);
			}
			em.close();
		}
		return group;
	}
	
	private static String generateGroupKey(Long id) {
		return "Group_" + id;
	}

	public static Post getPost(Long id) {
		String cacheKey = generatePostKey(id);
		Post post = (Post) getFromCache(cacheKey);
		if (null == post) {
			EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
			PostService postService = new PostService(em);
			post = postService.get(id);
			if (null!=post) {
				em.detach(post);
				putInCache(cacheKey, post);
			}
			em.close();
		}
		return post;
	}

	private static String generatePostKey(Long id) {
		return "Post_" + id;
	}

	static Map<String, Object> cacheMap = new HashMap<>();
	
	private static MemcachedClient client = null;
	private static MemcachedClient getClient() throws IOException{
		if (null == client) {
			client = new MemcachedClient(new InetSocketAddress(MEMCACHE_NODE_1, MEMCACHE_NODE_1_PORT));
		}
		return client;
	}

	public static Object getFromCache(String key) {
		Object object = null;
		if (AppProperties.getInstance().isProduction()) {
			MemcachedClient mcc = null;
			try {
				logger.info("getFromCache key : " + key);
				mcc = getClient();
				object = mcc.get(key);
				logger.info("cache object : " + object);
			} catch (Exception e) {
				logger.error( e.getMessage(), e);
				e.printStackTrace();
			}
		} else {
			object = cacheMap.get(key);
		}
		return object;
	}

	public static boolean putInCache(String key, Object object) {
		if (object != null) {
			if (AppProperties.getInstance().isProduction()) {
				MemcachedClient mcc = null;
				try {
					logger.info("put in cache key : " + key);
					mcc = getClient();
					mcc.set(key, CACHE_TIMEOUT, object);
					logger.info("put in sucess");
					return true;
				} catch (IOException e) {
					logger.error( e.getMessage(), e);
					e.printStackTrace();
				}
			} else {
				cacheMap.put(key, object);
			}
		} else {
			logger.warn("cannot add null for " + key);
		}
		return false;
	}

	static boolean removeFromCache(String key) {
		if (AppProperties.getInstance().isProduction()) {
			MemcachedClient mcc = null;
			try {
				mcc = getClient();
				mcc.delete(key);
			} catch (IOException e) {
				logger.error("Error : " + e.getMessage(), e);
				e.printStackTrace();
			}
		} else {
			cacheMap.remove(key);
		}
		return true;
	}

	public static void addUserToCache(AppUser user){
		if (null!= user) {
			user.getGroupIds().size();
			user.getScopes().size();
			String cacheKey = generateUserKey(user.getEmail());
			putInCache(cacheKey, user);
		}
	}
	
	public static void addGroupToCache(Group group ){
		if (null!= group) {
			group.getMembers().size();
			group.getMemberGroupsIds().size();
			String cacheKey = generateGroupKey(group.getId());
			putInCache(cacheKey, group);
		}
	}
	
	public static AppUser getAppUser(String email) {
		String cacheKey = generateUserKey(email);
		AppUser user = (AppUser) getFromCache(cacheKey);
		if (null == user) {
			EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
			AppUserService appUserService = new AppUserService(em);
			user = appUserService.getAppUserByEmail(email);
			if (null!=user) {
				user.getScopes().size();
				user.getGroupIds().size();
				em.detach(user);
				putInCache(cacheKey, user);
			}
			em.close();
		}

		return user;
	}

	static String generateUserKey(String email) {
		String cacheKey = "AppUser_" + email + "_2";
		return cacheKey;
	}

	static String generateUserProfilePhotoKey(String email) {
		String cacheKey = email + "_profile_photo";
		return cacheKey;
	}

	public static byte[] getUserPhoto(String email) throws IOException {
		String cacheKey = generateUserProfilePhotoKey(email);
		byte[] bytes = (byte[]) getFromCache(cacheKey);
		if (bytes == null) {
			try {
				AppUser user = getAppUser(email);
				if (null != user) {
					if (StringUtils.isNotBlank(user.getUploadedPhotoPath())) {
						bytes = Files.readAllBytes(Paths.get(user.getUploadedPhotoPath()));
					} else if (StringUtils.isNotBlank(user.getPhotoUrl())) {
						if (user.getPhotoUrl().startsWith("http")) {
							URL url = new URL(user.getPhotoUrl());
							URLConnection con = url.openConnection();
							bytes = IOUtils.toByteArray(con.getInputStream());
						}
					}
				}
				putInCache(cacheKey, bytes);
			} catch (Exception e) {
				logger.error( e.getMessage(), e);
			}
		}
		return bytes;
	}
	
	public static void removeUserPhoto(String email) {
		String cacheKey = generateUserProfilePhotoKey(email);
		removeFromCache(cacheKey);
	}

	public static String getThumbnailCacheKey(String serverName) {
		String key = "Thumbnail_" + serverName;
		try {
			return Base64.encodeBase64URLSafeString(key.getBytes(Constants.UTF_8));
		} catch (UnsupportedEncodingException e) {
			logger.error( e.getMessage(), e);
		}
		return key;
	}

}
