package com.comviva.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.comviva.bo.VideoStreamBO;
import com.comviva.db.DBConnectionPooler;
import com.comviva.utility.AppLogger;
import com.comviva.utility.SingletonPropertyLoader;

public class AppRequest extends HttpServlet {

    /** * */
    private static final long serialVersionUID = 1L;
    AppLogger logger = new AppLogger(getClass());
    Properties properties = SingletonPropertyLoader.loadPropertyFile();

    public void init(ServletConfig config) throws ServletException {
	super.init(config);

    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	Connection connection = null;
	JSONObject successJSON = new JSONObject();
	String method = "", emailid = "", otp = "", streamName = "", appName = "";
	Integer flag = 1, isAliveFlag = 88;
	String mode = "APP";
	try {
	    connection = DBConnectionPooler.getDatabaseConnection("GLOBAL");
	    VideoStreamBO streamBO = new VideoStreamBO();
	    if (connection != null) {
		if (null != request.getParameter("method")) {
		    method = (String) request.getParameter("method");
		}
		if (null != request.getParameter("emailid")) {
		    emailid = (String) request.getParameter("emailid");
		}
		if (null != request.getParameter("otp")) {
		    otp = (String) request.getParameter("otp");
		}
		if (null != request.getParameter("streamName")) {
		    streamName = (String) request.getParameter("streamName");
		}
		if (null != request.getParameter("appName")) {
		    appName = (String) request.getParameter("appName");
		}
		System.out.println("EmailId=" + emailid + "|method=" + method);

		if (method.equalsIgnoreCase("generateotp")) {
		    flag = streamBO.generateotp(connection, properties, emailid, mode);
		} else if (method.equalsIgnoreCase("validateotp")) {
		    flag = streamBO.validateOtp(connection, properties, emailid, otp, mode);
		    if (flag.equals(1)) {
			String url = (String) properties.getProperty("WOWZA_URL");
			appName = (String) properties.getProperty("WOWZA_APP_NAME");
			streamName = (String) properties.getProperty("WOWZA_STREAM_NAME");
			logger.logInfo("URL="+url);
			logger.logInfo("AppName="+appName+"|streamName="+streamName);
			if (!appName.equals("") && !streamName.equals("")) {
			    logger.logInfo("Inside If");
			    isAliveFlag = streamBO.isStreamAlive(properties, url, appName, streamName);
			}
		    }

		} else if (method.equalsIgnoreCase("isStreamAlive")) {

		    String url = (String) properties.getProperty("WOWZA_URL");
		    if (!appName.equals("") && !streamName.equals("")) {
			// db check for otp validaity
			flag = streamBO.isUserExist(connection, properties, emailid, mode);
			if(flag.equals(1)){
			    flag = streamBO.isStreamAlive(properties, url, appName, streamName);
			}
		    } else {
			flag = 0;
		    }
		} else if (method.equalsIgnoreCase("isThereAnyStream")) {
		    String url = (String) properties.getProperty("WOWZA_URL");
		    appName = (String) properties.getProperty("WOWZA_APP_NAME");
		    streamName = (String) properties.getProperty("WOWZA_STREAM_NAME");
		    if (!appName.equals("") && !streamName.equals("")) {
			// db check for otp validaity
			flag = streamBO.isUserExist(connection, properties, emailid, mode);
			if(flag.equals(1)){
			    isAliveFlag = streamBO.isStreamAlive(properties, url, appName, streamName);
			}
		    }
		}

		if (flag.equals(1)) {
		    successJSON.put("success", flag.toString());
		    successJSON.put("description", (String) properties.getProperty("RESP_DESC_1"));
		} else if (flag.equals(2)) {
		    successJSON.put("success", "0");
		    successJSON.put("description", (String) properties.getProperty("RESP_DESC_2"));
		} else if (flag.equals(3)) {
		    successJSON.put("success", "0");
		    successJSON.put("description", (String) properties.getProperty("RESP_DESC_3"));
		} else if (flag.equals(4)) {
		    successJSON.put("success", "0");
		    successJSON.put("description", (String) properties.getProperty("RESP_DESC_4"));
		} else if (flag.equals(5)) {
		    successJSON.put("success", "5");
		    successJSON.put("description", (String) properties.getProperty("RESP_DESC_5"));
		} else if (flag.equals(99)) {
		    successJSON.put("success", "1");
		    successJSON.put("description", (String) properties.getProperty("RESP_DESC_1"));
		    successJSON.put("app_name", (String) properties.getProperty("WOWZA_APP_NAME"));
		    successJSON.put("stream_name", (String) properties.getProperty("WOWZA_STREAM_NAME"));
		}  else if (flag.equals(6)) {
		    successJSON.put("success", "6");
		    successJSON.put("description", (String) properties.getProperty("RESP_DESC_6"));
		} else {
		    successJSON.put("success", "0");
		    successJSON.put("description", (String) properties.getProperty("RESP_DESC_0"));
		}
		if ((method.equalsIgnoreCase("validateotp") || method.equalsIgnoreCase("isThereAnyStream")) && flag.equals(1)) {
		    if (isAliveFlag.equals(99)) {
			successJSON.put("rtsp_link", (String) properties.getProperty("RTSP_URL"));
			successJSON.put("hls_link", (String) properties.getProperty("HLS_URL"));
			successJSON.put("app_name", (String) properties.getProperty("WOWZA_APP_NAME"));
			successJSON.put("stream_name", (String) properties.getProperty("WOWZA_STREAM_NAME"));
		    }
		    // appName,live
		    // streamName,mystream
		    // application name - live
		    // stream name - mystream

		}
	    } else {
		successJSON.put("success", "0");// Failure
		successJSON.put("description", "Error");
	    }
	    System.out.println("Disconnected!");

	    PrintWriter t_OutOBJ = response.getWriter();
	    response.setContentType("application/json");
	    logger.logInfo("Email=" + emailid + "|Response=" + successJSON);
	    t_OutOBJ.print(successJSON);
	    t_OutOBJ.flush();
	    t_OutOBJ.close();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (connection != null)
		    connection.close();
	    } catch (Exception e) {
		logger.logError("", e);
	    }
	    try {
	    } catch (Exception e) {
		logger.logError("", e);
	    }

	}
    }

}