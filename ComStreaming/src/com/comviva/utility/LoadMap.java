package com.comviva.utility;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.comviva.db.DBConnectionPooler;

public class LoadMap extends HttpServlet {
  /** * */
  private static final long serialVersionUID = 1L;
  AppLogger logger = new AppLogger(getClass());
  Properties properties = SingletonPropertyLoader.loadPropertyFile();
  public static HashMap<String, String> yourMap = new HashMap<String, String>();
  
  @Override
  public void init(ServletConfig config) throws ServletException {
    Connection connection = null;
    PreparedStatement stmt1 = null;
    String sql = "";
    ResultSet resultSet1 = null;
    try {
      connection = DBConnectionPooler.getDatabaseConnection("GLOBAL");
      
      sql = properties.getProperty("WHITELISTED_SQL");
      stmt1 = connection.prepareStatement(sql);
      logger.logInfo("getUser:Get Msg SQL=" + stmt1.toString());
      resultSet1 = stmt1.executeQuery();
      while (resultSet1.next()) {
        yourMap.put(resultSet1.getString(1), "");
      }
    } catch (Exception e) {
      logger.logError("Exception", e);
      e.printStackTrace();
    } finally {
      try {
        if (stmt1 != null) {
          stmt1.close();
        }
        if (resultSet1 != null) {
          resultSet1.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        if (connection != null)
          connection.close();
      } catch (Exception e) {
        logger.logError("", e);
      }
      
    }
  }
  
  @Override
  public void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Connection connection = null;
    PreparedStatement stmt1 = null;
    String sql = "";
    ResultSet resultSet1 = null;
    try {
      connection = DBConnectionPooler.getDatabaseConnection("GLOBAL");
      
      sql = properties.getProperty("WHITELISTED_SQL");
      stmt1 = connection.prepareStatement(sql);
      logger.logInfo("getUser:Get Msg SQL=" + stmt1.toString());
      resultSet1 = stmt1.executeQuery();
      while (resultSet1.next()) {
        yourMap.put(resultSet1.getString(1), "");
      }
    } catch (Exception e) {
      logger.logError("Exception", e);
      e.printStackTrace();
    } finally {
      try {
        if (stmt1 != null) {
          stmt1.close();
        }
        if (resultSet1 != null) {
          resultSet1.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        if (connection != null)
          connection.close();
      } catch (Exception e) {
        logger.logError("", e);
      }
      
    }
  }
}
