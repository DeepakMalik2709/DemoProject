package com.notes.nicefact.util;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleUtils {
	
	private static final String BUNDLE_NAME = "messages";
    private ResourceBundle resourceBundle;
    
    public ResourceBundleUtils() {
    	resourceBundle = ResourceBundle.getBundle("messages",
				new Locale("en"));
    }
    
    public ResourceBundleUtils(Locale locale) {
    	resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    }
    
    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    public String getString(String key, Object... params) {
        try {
            return MessageFormat.format(resourceBundle.getString(key), params);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

}

