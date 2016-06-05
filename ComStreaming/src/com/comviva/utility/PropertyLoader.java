package com.comviva.utility;

import java.io.InputStream;
import java.util.Properties;

public abstract class PropertyLoader {

    /** *Property file is loaded** */
    public static Properties loadProperties(String p_Name) {
	return loadProperties(p_Name, Thread.currentThread()
		.getContextClassLoader());
    }

    public static Properties loadProperties(String p_Name, ClassLoader p_Loader) {
	if (p_Name == null) {
	    throw new IllegalArgumentException("null input: name");
	}
	if (p_Name.startsWith("/")) {
	    p_Name = p_Name.substring(1);
	}
	if (p_Name.endsWith(".properties")) {
	    p_Name = p_Name.substring(0, p_Name.length()
		    - ".properties".length());
	}
	Properties t_Result = null;

	InputStream t_InSteamOBj = null;
	try {
	    if (p_Loader == null) {
		p_Loader = ClassLoader.getSystemClassLoader();
	    }

	    p_Name = p_Name.replace('.', '/');
	    if (!p_Name.endsWith(".properties")) {
		p_Name = p_Name.concat(".properties");
	    }
	    t_InSteamOBj = p_Loader.getResourceAsStream(p_Name);
	    if (t_InSteamOBj != null) {
		t_Result = new Properties();
		t_Result.load(t_InSteamOBj);
	    }
	} catch (Exception e) {
	    t_Result = null;

	    if (t_InSteamOBj != null)
		try {
		    t_InSteamOBj.close();
		} catch (Throwable localThrowable) {
		}
	} finally {
	    if (t_InSteamOBj != null) {
		try {
		    t_InSteamOBj.close();
		} catch (Throwable localThrowable1) {
		}
	    }
	}
	if (t_Result == null) {
	    throw new IllegalArgumentException("could not load [" + p_Name
		    + "]" + " as " + "a classloader resource");
	}

	return t_Result;
    }
}
