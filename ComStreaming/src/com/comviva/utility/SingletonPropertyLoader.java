package com.comviva.utility;

import java.util.Properties;

public class SingletonPropertyLoader {
    private static Properties m_Properties = PropertyLoader
	    .loadProperties("COMMON");
    //private static HashMap<String, Properties> m_PropertiesFileMap = new HashMap<String, Properties>();

    /** *Load Property file** */
    public static Properties loadPropertyFile() {
	if (m_Properties == null) {
	    m_Properties = PropertyLoader.loadProperties("COMMON");
	}
	return m_Properties;
    }

    /** *Load Property file based on the value passed in the method** */
    /*public static Properties loadPropertyFile(String t_AirtelServiceID) {
	Properties t_Properties = null;
	if (m_PropertiesFileMap.containsKey("SQL_" + t_AirtelServiceID)) {
	    t_Properties = (Properties) m_PropertiesFileMap.get("SQL_"
		    + t_AirtelServiceID);
	} else {
	    t_Properties = PropertyLoader.loadProperties("SQL_"
		    + t_AirtelServiceID);
	    m_PropertiesFileMap.put("SQL_" + t_AirtelServiceID, t_Properties);
	}
	return t_Properties;
    }*/

    protected Object clone() throws CloneNotSupportedException {
	throw new CloneNotSupportedException("Clone is not allowed.");
    }
}
