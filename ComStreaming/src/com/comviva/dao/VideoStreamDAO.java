package com.comviva.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.comviva.utility.AppLogger;
import com.comviva.vo.VideoVO;

public class VideoStreamDAO {
  AppLogger logger = new AppLogger(getClass());
  
  public Integer deleteRecord(Connection connection, Properties properties,
      String emailid, String mode) {
    int flag = 1;
    String sql = "";
    PreparedStatement stmtDel = null;
    try {
      sql = properties.getProperty("DELETE_USER");
      stmtDel = connection.prepareStatement(sql);
      stmtDel.setString(1, emailid);
      stmtDel.setString(2, mode);
      stmtDel.executeUpdate();
    } catch (Exception e) {
      logger.logError("Exception", e);
      e.printStackTrace();
      flag = 0;
    } finally {
      try {
        if (stmtDel != null) {
          stmtDel.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return flag;
  }
  
  public Integer saveRecord(Connection connection, Properties properties,
      String emailid, String otp, String mode) {
    PreparedStatement stmt = null;
    String sql = "";
    ResultSet resultSet = null;
    long currentTime = 0;
    Integer flag = 1;
    try {
      currentTime = System.currentTimeMillis();
      sql = properties.getProperty("INSERT_USER");
      stmt = connection.prepareStatement(sql);
      stmt.setString(1, emailid);
      stmt.setString(2, otp);
      stmt.setLong(3, currentTime);
      stmt.setString(4, mode);
      stmt.executeUpdate();
      resultSet = stmt.getGeneratedKeys();
      
    } catch (Exception e) {
      flag = 0;
      logger.logError("saveRecord:Exception", e);
      e.printStackTrace();
    } finally {
      
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (resultSet != null) {
          resultSet.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return flag;
  }
  
  public Map<String, String> getUser(Connection connection,
      Properties properties, String emailid, String mode) {
    PreparedStatement stmt1 = null;
    String sql = "";
    ResultSet resultSet1 = null;
    Map<String, String> map = new HashMap<String, String>();
    try {
      sql = properties.getProperty("GET_USER");
      stmt1 = connection.prepareStatement(sql);
      stmt1.setString(1, emailid);
      stmt1.setString(2, mode);
      logger.logInfo("getUser:Get Msg SQL=" + stmt1.toString());
      resultSet1 = stmt1.executeQuery();
      if (resultSet1.next()) {
        map.put("id", resultSet1.getString(1));// userid
        map.put("dbotp", resultSet1.getString(2));// otp
        if (System.currentTimeMillis() - resultSet1.getLong(3) <= Long
            .parseLong(properties.getProperty("OTP_VALIDITY"))) {
          map.put("otpactive", "1");
        } else {
          map.put("otpactive", "0");
        }
        if (resultSet1.getInt(4) >= 0
            && resultSet1.getInt(4) < Integer.parseInt(properties
                .getProperty("OTP_RETRY_COUNT"))) {
          map.put("retry", "1");
        } else {
          map.put("retry", "0");
        }
      }
    } catch (Exception e) {
      logger.logError("getStatus:Exception", e);
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
    }
    return map;
  }
  
  public void updateCount(Connection connection, Properties properties,
      String emailid, String mode) {
    PreparedStatement stmt = null;
    String sql = "";
    ResultSet resultSet = null;
    try {
      sql = properties.getProperty("UPDATE_RETRY_COUNT");
      stmt = connection.prepareStatement(sql);
      stmt.setString(1, emailid);
      stmt.setString(2, mode);
      stmt.executeUpdate();
      resultSet = stmt.getGeneratedKeys();
      
    } catch (Exception e) {
      logger.logError("saveRecord:Exception", e);
      e.printStackTrace();
    } finally {
      
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (resultSet != null) {
          resultSet.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return;
  }
  
  public void setValidateTrue(Connection connection, Properties properties,
      String emailid, String mode) {
    PreparedStatement stmt = null;
    String sql = "";
    ResultSet resultSet = null;
    try {
      sql = properties.getProperty("SET_VALIDATE_TRUE");
      stmt = connection.prepareStatement(sql);
      stmt.setString(1, emailid);
      stmt.setString(2, mode);
      stmt.executeUpdate();
      resultSet = stmt.getGeneratedKeys();
      
    } catch (Exception e) {
      logger.logError("saveRecord:Exception", e);
      e.printStackTrace();
    } finally {
      
      try {
        if (stmt != null) {
          stmt.close();
        }
        if (resultSet != null) {
          resultSet.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return;
  }
  
  public Integer isUserAuthenticated(Connection connection,
      Properties properties, String emailid, String mode) {
    PreparedStatement stmt1 = null;
    String sql = "";
    ResultSet resultSet1 = null;
    Integer flag = 6;
    try {
      sql = properties.getProperty("IS_USER_AUTHENTIC");
      stmt1 = connection.prepareStatement(sql);
      stmt1.setString(1, emailid);
      stmt1.setString(2, mode);
      logger.logInfo("getUser:Get Msg SQL=" + stmt1.toString());
      resultSet1 = stmt1.executeQuery();
      if (resultSet1.next()) {
        flag = 1;
      }
    } catch (Exception e) {
      logger.logError("getStatus:Exception", e);
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
    }
    return flag;
  }
  
  public List<VideoVO> getDBStreamCount(Connection connection,
      Properties properties) {
    PreparedStatement stmt1 = null;
    String sql = "";
    ResultSet resultSet1 = null;
    List<VideoVO> list = new ArrayList<VideoVO>();
    try {
      sql = properties.getProperty("DB_STREAM_COUNT");
      stmt1 = connection.prepareStatement(sql);
      logger.logInfo("getUser:Get Msg SQL=" + stmt1.toString());
      resultSet1 = stmt1.executeQuery();
      VideoVO vo = null;
      while (resultSet1.next()) {
        vo = new VideoVO();
        vo.setTime(resultSet1.getString(1));
        vo.setStreamName(resultSet1.getString(2));
        vo.setHttpCount(resultSet1.getInt(3));
        vo.setRtspCount(resultSet1.getInt(4));
        list.add(vo);
      }
    } catch (Exception e) {
      logger.logError("getStatus:Exception", e);
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
    }
    return list;
  }
}
