package com.comviva.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.comviva.bo.VideoStreamBO;
import com.comviva.db.DBConnectionPooler;
import com.comviva.utility.AppLogger;
import com.comviva.utility.SingletonPropertyLoader;
import com.comviva.vo.VideoVO;

public class SubmitRequest extends HttpServlet {
  
  /** * */
  private static final long serialVersionUID = 1L;
  AppLogger logger = new AppLogger(getClass());
  Properties properties = SingletonPropertyLoader.loadPropertyFile();
  
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    
  }
  
  @Override
  public void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Connection connection = null;
    response.setContentType("text/html");
    String method = "", emailid = "", otp = "", streamName = "", appName = "";
    Integer flag = 1, isAliveFlag = 88;
    String mode = "WEB";
    Integer liveCount = -1, maxStreamConnection = 0;
    
    try {
      
      connection = DBConnectionPooler.getDatabaseConnection("GLOBAL");
      VideoStreamBO streamBO = new VideoStreamBO();
      if (connection != null) {
        if (null != request.getParameter("method")) {
          method = request.getParameter("method");
        }
        if (null != request.getParameter("emailid")) {
          emailid = request.getParameter("emailid").toLowerCase();
        }
        if (null != request.getParameter("otp")) {
          otp = request.getParameter("otp");
        }
        logger.logInfo("EmailId=" + emailid + "|method=" + method);
        maxStreamConnection = Integer.parseInt(properties
            .getProperty("MAX_STREAM_CONNECTION"));
        if (method.equalsIgnoreCase("generateotp")) {
          String regex = "^[A-Za-z0-9.-_-\\+]+(\\[._A-Za-z0-9-]+)*@mahindracomviva.com";
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(emailid);
          String regex2 = "^[A-Za-z0-9.-_-\\+]+(\\[._A-Za-z0-9-]+)*@techmahindra.com";
          Pattern pattern2 = Pattern.compile(regex2);
          Matcher matcher2 = pattern2.matcher(emailid);
          // isAliveFlag =
          // Integer.parseInt(request.getParameter("isAliveFlag"));
          // liveCount = Integer.parseInt(request.getParameter("liveCount"));
          String url = properties.getProperty("WOWZA_URL");
          appName = properties.getProperty("WOWZA_APP_NAME");
          streamName = properties.getProperty("WOWZA_STREAM_NAME");
          isAliveFlag = streamBO.isStreamAlive(properties, url, appName,
              streamName);
          if (isAliveFlag.equals(99)) {
            VideoVO videoVO = null;
            if (!appName.equals("") && !streamName.equals("")) {
              videoVO = streamBO.getLiveStreamConnection(properties, url,
                  appName, streamName);
              liveCount = videoVO.getTotalCount();
            }
          } else {
            liveCount = -1;
          }
          if (matcher.matches() || matcher2.matches()) {
            if (isAliveFlag.equals(99)) {
              if (liveCount <= maxStreamConnection) {
                flag = streamBO.generateotp(connection, properties, emailid,
                    mode);
                logger.logInfo("Flag=" + flag);
              } else {
                flag = 4;
              }
            } /*
               * else { flag = 3; if (!appName.equals("") &&
               * !streamName.equals("")) { isAliveFlag =
               * streamBO.isStreamAlive(properties, url, appName, streamName); }
               * }
               */
            
            request.setAttribute("isAliveFlag", isAliveFlag);
            request.setAttribute("liveCount", liveCount);
            if (flag.equals(1)) {
              request.setAttribute("emailid", emailid);
              RequestDispatcher view = request.getRequestDispatcher("/otp.jsp");
              view.forward(request, response);
            } else if (flag.equals(2)) {
              request.setAttribute("emailid", emailid);
              request.setAttribute("error",
                  properties.getProperty("RESP_DESC_2"));
              RequestDispatcher view = request
                  .getRequestDispatcher("/login.jsp");
              view.forward(request, response);
            } else if (flag.equals(3)) {
              request.setAttribute("emailid", emailid);
              request.setAttribute("error",
                  properties.getProperty("RESP_DESC_7"));
              RequestDispatcher view = request
                  .getRequestDispatcher("/login.jsp");
              view.forward(request, response);
            } else if (flag.equals(4)) {
              request.setAttribute("emailid", emailid);
              request.setAttribute("error",
                  properties.getProperty("RESP_DESC_8"));
              RequestDispatcher view = request
                  .getRequestDispatcher("/login.jsp");
              view.forward(request, response);
            }
          } else {
            request.setAttribute("isAliveFlag", isAliveFlag);
            request.setAttribute("liveCount", liveCount);
            request.setAttribute("emailid", emailid);
            request
                .setAttribute("error", properties.getProperty("RESP_DESC_9"));
            RequestDispatcher view = request.getRequestDispatcher("/login.jsp");
            view.forward(request, response);
            try {
              if (connection != null) {
                connection.close();
                logger.logInfo("connection closed");
              }
            } catch (Exception e) {
              logger.logError("", e);
            }
          }
        } else if (method.equalsIgnoreCase("validateotp")) {
          flag = streamBO.validateOtp(connection, properties, emailid, otp,
              mode);
          logger.logInfo("validateOtp=" + flag);
          if (flag.equals(1)) {
            String url = properties.getProperty("WOWZA_URL");
            appName = properties.getProperty("WOWZA_APP_NAME");
            streamName = properties.getProperty("WOWZA_STREAM_NAME");
            isAliveFlag = streamBO.isStreamAlive(properties, url, appName,
                streamName);
            request.setAttribute("isAliveFlag", isAliveFlag);
            request
                .setAttribute("streamurl", properties.getProperty("HLS_URL"));
            request.setAttribute("emailid", emailid);
            RequestDispatcher view = request
                .getRequestDispatcher("/player.jsp");
            view.forward(request, response);
          } else if (flag.equals(3)) {
            request.setAttribute("emailid", emailid);
            request
                .setAttribute("error", properties.getProperty("RESP_DESC_3"));
            RequestDispatcher view = request.getRequestDispatcher("/login.jsp");
            view.forward(request, response);
          } else if (flag.equals(4)) {
            request.setAttribute("emailid", emailid);
            request
                .setAttribute("error", properties.getProperty("RESP_DESC_4"));
            RequestDispatcher view = request.getRequestDispatcher("/otp.jsp");
            view.forward(request, response);
          } else if (flag.equals(5)) {
            request.setAttribute("emailid", emailid);
            request
                .setAttribute("error", properties.getProperty("RESP_DESC_5"));
            RequestDispatcher view = request.getRequestDispatcher("/login.jsp");
            view.forward(request, response);
          }
        } else if (method.equalsIgnoreCase("init")) {
          String url = properties.getProperty("WOWZA_URL");
          appName = properties.getProperty("WOWZA_APP_NAME");
          streamName = properties.getProperty("WOWZA_STREAM_NAME");
          
          if (!appName.equals("") && !streamName.equals("")) {
            // db check for otp validaity
            // flag = streamBO.isUserExist(connection, properties, emailid,
            // mode);
            // if(flag.equals(1)){
            isAliveFlag = streamBO.isStreamAlive(properties, url, appName,
                streamName);
            // }
          }
          request.setAttribute("isAliveFlag", isAliveFlag);
          if (!isAliveFlag.equals(99)) {
            request
                .setAttribute("error", properties.getProperty("RESP_DESC_7"));
          } else {
            VideoVO videoVO = null;
            if (!appName.equals("") && !streamName.equals("")) {
              videoVO = streamBO.getLiveStreamConnection(properties, url,
                  appName, streamName);
              liveCount = videoVO.getTotalCount();
            }
            if (liveCount > maxStreamConnection) {
              request.setAttribute("error",
                  properties.getProperty("RESP_DESC_8"));
            }
          }
          request.setAttribute("liveCount", liveCount);
          RequestDispatcher view = request.getRequestDispatcher("/login.jsp");
          view.forward(request, response);
        } else if (method.equalsIgnoreCase("getStreamCount")) {
          String url = properties.getProperty("WOWZA_URL");
          appName = properties.getProperty("WOWZA_APP_NAME");
          streamName = properties.getProperty("WOWZA_STREAM_NAME");
          VideoVO videoVO = null;
          if (!appName.equals("") && !streamName.equals("")) {
            videoVO = streamBO.getLiveStreamConnection(properties, url,
                appName, streamName);
          }
          request.setAttribute("appName", appName);
          request.setAttribute("totalCount", videoVO.getTotalCount());
          request.setAttribute("streamlist", videoVO.getStreamList());
          RequestDispatcher view = request
              .getRequestDispatcher("/streamList.jsp");
          view.forward(request, response);
        } else if (method.equalsIgnoreCase("getDBStreamCount")) {
          List<VideoVO> list = streamBO
              .getDBStreamCount(connection, properties);
          request.setAttribute("streamlist", list);
          RequestDispatcher view = request
              .getRequestDispatcher("/streamListDB.jsp");
          view.forward(request, response);
        }
      }
      
      /*
       * request.setAttribute("emailid", emailid); RequestDispatcher view =
       * request.getRequestDispatcher("/otppage.jsp"); view.forward(request,
       * response);
       */
      
    } catch (Exception e) {
      logger.logError("Exception", e);
      // e.printStackTrace();
    } finally {
      try {
        if (!method.equalsIgnoreCase("generateotp")) {
          if (connection != null)
            connection.close();
        }
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