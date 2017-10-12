package com.notes.nicefact.util;

import java.io.File;
 
/**
 * This class search for the web inf class path and gives many path from that
 */
public class ClassPath{
    private static ClassPath instance = null;
    private String webInfPath, webXmlPath;
   /* private static final Logger logger = Logger.getLogger(ClassPath.class);*/
    /**
     * The constructor will get the webInfPath and store it until the app close
     */
    private ClassPath(){
        File myClass = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
    	
 		String path = myClass.getAbsolutePath();
 		String folder = "WEB-INF";
 		path = myClass.getAbsolutePath().substring(0, path.indexOf(folder)+ folder.length());
 
        this.webInfPath = path.replaceAll("%20", " ") + File.separator;
        this.webXmlPath = this.getWebInfPath() + "web.xml";
    }
 
    /**
     * Singleton structure
     * @return himself
     */
    public static ClassPath getInstance(){
        if(instance == null){
            instance = new ClassPath();
        }
        return instance;
    }
 
 
    /**
     * Get back the WEB-INF path
     * @return The WEB-INF path
     */
    public String getWebInfPath(){
        return this.webInfPath;
    }
 
    /**
     * Get back the WEB-INF/web.xml path
     * @return The WEB-INF/web.xml path
     */
    public String getWebXmlPath(){
        return this.webXmlPath;
    }
 
}