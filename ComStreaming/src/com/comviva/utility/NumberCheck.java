package com.comviva.utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class NumberCheck {
    AppLogger logger = new AppLogger(getClass());
    public boolean checkNumber(String msisdn, Connection connection, Properties properties) {
	//int countLine = 0;
	//String filePath = "whitelistNumbers.txt";
	//BufferedReader br;
	//String inputSearch = "+919717397771";
	//String line = "";
	boolean flag = false;
	
	String sql = (String)properties.getProperty("IS_NUMBER_WHITELIST");
	ResultSet resultSet = null;
	PreparedStatement stmt = null;
	
	try{
	    logger.logInfo("SQL="+sql);
	    stmt = connection.prepareStatement(sql);
	    stmt.setString(1, msisdn);
	    resultSet = stmt.executeQuery();
	    if(resultSet.next()){
		flag = true;
	    }
	}catch(Exception e){
	    logger.logError("checkNumber:Exception", e);
	    e.printStackTrace();
	}finally{
	    try{
		if(resultSet!=null)
		    resultSet.close();
		if(stmt!=null)
		    stmt.close();
	    }catch(Exception e){
		e.printStackTrace();
	    }
	}
	return flag;
	/*try {
	    
	    
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	    if (classLoader == null) {
		classLoader = ClassLoader.getSystemClassLoader();
	    }
	    InputStream iniFileStream = classLoader.getResourceAsStream(filePath);

	    InputStreamReader inputStreamReader = new InputStreamReader(iniFileStream);
	    br = new BufferedReader(inputStreamReader);
	    try {
		while ((line = br.readLine()) != null) {
		    countLine++;
		    System.out.println(line);
		    if(line.trim().equals(msisdn)){
			flag = true;
			break;
		    }
		}
		br.close();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		logger.logError("checkNumber",e);
	    }
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    logger.logError("checkNumber",e);
	}
	if(flag){
	    logger.logInfo("Number "+msisdn+" found at line number = "+countLine);
	}else{
	    logger.logInfo("Number "+msisdn+" not found");
	}*/
	
    }
}
