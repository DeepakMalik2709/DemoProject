package com.notes.nicefact.util;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Helper class that creates fresh {@link EntityManager} instances.
 *
 */
public class EntityManagerHelper {
    private static EntityManagerFactory emfInstance;
    
    private static EntityManagerFactory emfBigTableInstance;

    private EntityManagerHelper() { }

    public static EntityManager getDefaulteEntityManager() {
    	if (emfInstance == null) {
    		  Map<String, String> properties = new HashMap();
    		  
    		  properties.put("javax.persistence.jdbc.driver",
      		          "com.mysql.jdbc.Driver");
      		      properties.put("javax.persistence.jdbc.url",AppProperties.getInstance().getSqlUrl());
      		      
    		 emfInstance = Persistence.createEntityManagerFactory(Constants.PERSISTENSE_UNIT_NAME,properties);    		
    	}
        return emfInstance.createEntityManager();
    }
    
    public static EntityManager getBigTableeEntityManager() {
    	/*if (emfBigTableInstance == null) {
    		emfBigTableInstance = Persistence.createEntityManagerFactory(Constants.PERSISTENSE_BIG_TABLE_UNIT_NAME);    		
   	}
       return emfBigTableInstance.createEntityManager();*/
    	
    	return getDefaulteEntityManager();
    }
    
}
