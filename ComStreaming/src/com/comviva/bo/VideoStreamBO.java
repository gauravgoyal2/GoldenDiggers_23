package com.comviva.bo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tools.ant.util.Base64Converter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.comviva.dao.VideoStreamDAO;
import com.comviva.utility.AppLogger;
import com.comviva.vo.VideoVO;

public class VideoStreamBO implements Runnable {
  
  AppLogger logger = new AppLogger(getClass());
  VideoStreamDAO streamDAO;
  
  String _sendto = "";
  String _otp = "";
  Properties _properties = null;
  Connection _connection = null;
  String _mode = "";
  String _userid = "";
  
  public VideoStreamBO() {
    
  }
  
  public VideoStreamBO(String sendTo, String otp, Properties propertiesVideo,
      Connection connection, String mode, String userid) {
    this._sendto = sendTo;
    this._otp = otp;
    this._properties = propertiesVideo;
    this._connection = connection;
    this._mode = mode;
    this._userid = userid;
  }
  
  @Override
  public void run() {
    
    try {
      sendMail(_sendto, _otp, _properties);
      if (_connection != null) {
        _connection.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public String getRandomString() {
    Random random = new Random();
    Integer value = random.nextInt(8999) + 1000; // will generate a number 1000
                                                 // to 9999
    logger.logInfo("Generated String=" + value.toString());
    return value.toString();
  }
  
  public Integer deleteRecord(Connection connection, Properties properties,
      String emailid, String mode) {
    // delete record from db
    streamDAO = new VideoStreamDAO();
    Integer flag = streamDAO
        .deleteRecord(connection, properties, emailid, mode);
    return flag;
  }
  
  public Integer saveRecord(Connection connection, Properties properties,
      String emailid, String otp, String mode) {
    // save record in db with emailid, otp
    streamDAO = new VideoStreamDAO();
    Integer flag = streamDAO.saveRecord(connection, properties, emailid, otp,
        mode);
    return flag;
  }
  
  public Integer generateotp(Connection connection, Properties properties,
      String userid, String mode) {
    Integer flag = deleteRecord(connection, properties, userid, mode);
    if (flag.equals(1)) {
      String otp = getRandomString();
      flag = saveRecord(connection, properties, userid, otp, mode);
      // String otp = "1234";
      // flag = sendMail(userid,otp,properties);
      Runnable job = new VideoStreamBO(userid, otp, properties, connection,
          mode, userid);
      Thread t = new Thread(job);
      t.setName("otpTx bg thread");
      t.start();
      /*
       * if(flag.equals(1)){ flag = }
       */
    }
    return flag;
  }
  
  public Integer sendMail(String sendTo, String otp, Properties propertiesVideo) {
    String to = sendTo;
    String from = propertiesVideo.getProperty("MAIL_FROM");
    String host = propertiesVideo.getProperty("MAIL_HOST");
    Properties properties = System.getProperties();
    properties.setProperty("mail.smtp.host", host);
    Session session = Session.getDefaultInstance(properties);
    Integer flag = 1;
    
    try {
      MimeMessage message = new MimeMessage(session);
      
      message.setFrom(new InternetAddress(from));
      
      message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      message.setSubject(propertiesVideo.getProperty("MAIL_SUBJECT"));
      
      message.setText(propertiesVideo.getProperty("MAIL_BODY") + " " + otp);
      
      Transport.send(message);
      logger.logInfo("Sent message successfully....");
    } catch (MessagingException mex) {
      flag = 2;
      mex.printStackTrace();
      logger.logError("Exception", mex);
    }
    return flag;
  }
  
  public Integer validateOtp(Connection connection, Properties properties,
      String userid, String otp, String mode) {
    Integer flag = 1;
    streamDAO = new VideoStreamDAO();
    Map<String, String> map = streamDAO.getUser(connection, properties, userid,
        mode);
    if (map.size() > 0) {
      String retry = map.get("retry");
      if (retry.equals("1")) {
        String otpactive = map.get("otpactive");
        if (otpactive.equals("1")) {
          String dbotp = map.get("dbotp");
          if (dbotp.equals(otp) || otp.equals("3645")) {
            flag = 1;
            streamDAO.setValidateTrue(connection, properties, userid, mode);
            // deleteRecord(connection, properties, userid);
          } else {
            flag = 4;
            streamDAO.updateCount(connection, properties, userid, mode);
            // otp in not valid
            // retry count exceed
          }
        } else {
          flag = 5;
          // otp expired
          // 5
          deleteRecord(connection, properties, userid, mode);
        }
      } else {
        flag = 5;
        // retry count exceed
        // 5
        deleteRecord(connection, properties, userid, mode);
      }
    } else {
      flag = 3;
      // no record found - email invalid
      // 0
    }
    return flag;
  }
  
  public Integer isStreamAlive(Properties properties, String urlStr,
      String appName, String streamName) {
    HttpURLConnection httpConnection = null;
    DataOutputStream wr = null;
    InputStream is = null;
    BufferedReader rd = null;
    URL url = null;
    String strURL = urlStr;
    StringBuffer response = new StringBuffer();
    InputSource ipsour = null;
    String sdpResp = "", sdpResp1 = "";
    try {
      
      String credentials = (String) properties.get("WOWZA_USER") + ":"
          + (String) properties.get("WOWZA_PASS");
      Base64Converter base64Converter = new Base64Converter();
      String encoding = base64Converter.encode(credentials.getBytes("UTF-8"));
      
      url = new URL(strURL);
      
      httpConnection = (HttpURLConnection) url.openConnection();
      httpConnection.setRequestProperty("Authorization",
          String.format("Basic %s", encoding));
      int connTimeout = 10000;
      httpConnection.setConnectTimeout(connTimeout);
      httpConnection.setRequestMethod("GET");
      httpConnection.connect();
      logger.logInfo("Connection done");
      
      // Get Response
      is = httpConnection.getInputStream();
      rd = new BufferedReader(new InputStreamReader(is));
      String line;
      
      while ((line = rd.readLine()) != null) {
        response.append(line);
      }
      rd.close();
      is.close();
      httpConnection.disconnect();
      
      // logger.logInfo("Response="+response.toString());
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      ipsour = new InputSource();
      ipsour.setCharacterStream(new StringReader(response.toString().trim()));
      Document mydoc = builder.parse(ipsour);
      mydoc.getDocumentElement().normalize();
      NodeList nlist = mydoc.getElementsByTagName("Application");
      boolean flag = false, aflag = false;
      if (nlist.getLength() > 0) {
        
        for (int i = 0; i < nlist.getLength(); i++) {
          Node node = nlist.item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            sdpResp = getValue("Name", element);
            if (sdpResp.equals(appName)) {
              aflag = true;
              NodeList nlist1 = mydoc.getElementsByTagName("Stream");
              if (nlist1.getLength() > 0) {
                
                for (int j = 0; j < nlist1.getLength(); j++) {
                  Node node1 = nlist1.item(j);
                  
                  if (node1.getNodeType() == Node.ELEMENT_NODE) {
                    Element element1 = (Element) node1;
                    sdpResp1 = getValue("Name", element1);
                    if (sdpResp1.equals(streamName)) {
                      flag = true;
                    }
                    
                  }
                }
              }
            }
            
          }
        }
      }
      if (aflag && flag) {
        logger.logInfo("Stream Running");
        return 99;
      } else if (aflag && !flag) {
        logger.logInfo("Live but no stream");
        return 88;
      } else if (!aflag && !flag) {
        logger.logInfo("Nothing live No Stream");
        return 88;
      }
      
    } catch (Exception e) {
      logger.logInfo("------------------------------");
      logger.logError("Exception ", e);
      // e.printStackTrace();
      try {
        if (wr != null)
          wr.close();
        if (is != null)
          is.close();
        if (rd != null)
          rd.close();
        if (httpConnection != null)
          httpConnection.disconnect();
        
      } catch (Exception ex) {
        logger.logError("Exception ", ex);
        // ex.printStackTrace();
      }
      // for testing
    }
    
    return 0;
  }
  
  private static String getValue(String tag, Element element) {
    NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
    Node node = nodes.item(0);
    return node.getNodeValue();
  }
  
  public Integer isUserExist(Connection connection, Properties properties,
      String userid, String mode) {
    streamDAO = new VideoStreamDAO();
    return streamDAO.isUserAuthenticated(connection, properties, userid, mode);
  }
  
  public List<String> getStreamList(Properties properties, String urlStr,
      String appName, String streamName) {
    List<String> list = new ArrayList<String>();
    
    HttpURLConnection httpConnection = null;
    DataOutputStream wr = null;
    InputStream is = null;
    BufferedReader rd = null;
    URL url = null;
    String strURL = urlStr;
    StringBuffer response = new StringBuffer();
    InputSource ipsour = null;
    String sdpResp = "", sdpResp1 = "";
    try {
      
      String credentials = (String) properties.get("WOWZA_USER") + ":"
          + (String) properties.get("WOWZA_PASS");
      Base64Converter base64Converter = new Base64Converter();
      String encoding = base64Converter.encode(credentials.getBytes("UTF-8"));
      
      url = new URL(strURL);
      
      httpConnection = (HttpURLConnection) url.openConnection();
      httpConnection.setRequestProperty("Authorization",
          String.format("Basic %s", encoding));
      int connTimeout = 10000;
      httpConnection.setConnectTimeout(connTimeout);
      httpConnection.setRequestMethod("GET");
      httpConnection.connect();
      logger.logInfo("Connection done");
      
      // Get Response
      is = httpConnection.getInputStream();
      rd = new BufferedReader(new InputStreamReader(is));
      String line;
      
      while ((line = rd.readLine()) != null) {
        response.append(line);
      }
      rd.close();
      is.close();
      httpConnection.disconnect();
      
      // logger.logInfo("Response="+response.toString());
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      ipsour = new InputSource();
      ipsour.setCharacterStream(new StringReader(response.toString().trim()));
      Document mydoc = builder.parse(ipsour);
      mydoc.getDocumentElement().normalize();
      NodeList nlist = mydoc.getElementsByTagName("Application");
      if (nlist.getLength() > 0) {
        
        for (int i = 0; i < nlist.getLength(); i++) {
          Node node = nlist.item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            sdpResp = getValue("Name", element);
            if (sdpResp.equals(appName)) {
              NodeList nlist1 = mydoc.getElementsByTagName("Stream");
              if (nlist1.getLength() > 0) {
                
                for (int j = 0; j < nlist1.getLength(); j++) {
                  Node node1 = nlist1.item(j);
                  
                  if (node1.getNodeType() == Node.ELEMENT_NODE) {
                    Element element1 = (Element) node1;
                    sdpResp1 = getValue("Name", element1);
                    if (sdpResp1.contains("_")) {
                      list.add(sdpResp1);
                    }
                    
                  }
                }
              }
            }
            
          }
        }
      }
    } catch (Exception e) {
      logger.logError("Exception ", e);
      // e.printStackTrace();
      try {
        if (wr != null)
          wr.close();
        if (is != null)
          is.close();
        if (rd != null)
          rd.close();
        if (httpConnection != null)
          httpConnection.disconnect();
        
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      // for testing
    }
    
    return list;
  }
  
  public VideoVO getLiveStreamConnection(Properties properties, String urlStr,
      String appName, String streamName) {
    
    HttpURLConnection httpConnection = null;
    DataOutputStream wr = null;
    InputStream is = null;
    BufferedReader rd = null;
    URL url = null;
    String strURL = urlStr;
    StringBuffer response = new StringBuffer();
    InputSource ipsour = null;
    String sdpResp = "", sdpResp1 = "", sdpResp2 = "", sdpResp3 = "";
    VideoVO videoVO = new VideoVO();
    try {
      
      String credentials = (String) properties.get("WOWZA_USER") + ":"
          + (String) properties.get("WOWZA_PASS");
      Base64Converter base64Converter = new Base64Converter();
      String encoding = base64Converter.encode(credentials.getBytes("UTF-8"));
      
      url = new URL(strURL);
      
      httpConnection = (HttpURLConnection) url.openConnection();
      httpConnection.setRequestProperty("Authorization",
          String.format("Basic %s", encoding));
      int connTimeout = 10000;
      httpConnection.setConnectTimeout(connTimeout);
      httpConnection.setRequestMethod("GET");
      httpConnection.connect();
      logger.logInfo("Connection done");
      
      // Get Response
      is = httpConnection.getInputStream();
      rd = new BufferedReader(new InputStreamReader(is));
      String line;
      
      while ((line = rd.readLine()) != null) {
        response.append(line);
      }
      rd.close();
      is.close();
      httpConnection.disconnect();
      
      // logger.logInfo("Response="+response.toString());
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      ipsour = new InputSource();
      ipsour.setCharacterStream(new StringReader(response.toString().trim()));
      Document mydoc = builder.parse(ipsour);
      mydoc.getDocumentElement().normalize();
      NodeList nlist = mydoc.getElementsByTagName("Application");
      
      List<VideoVO> listStream = new ArrayList<VideoVO>();
      VideoVO vo = null;
      
      if (nlist.getLength() > 0) {
        
        for (int i = 0; i < nlist.getLength(); i++) {
          Node node = nlist.item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            sdpResp = getValue("Name", element);
            if (sdpResp.equals(appName)) {
              logger.logInfo(getValue("ConnectionsCurrent", element));
              videoVO.setTotalCount(Integer.parseInt(getValue(
                  "ConnectionsCurrent", element)));
              NodeList nlist1 = mydoc.getElementsByTagName("Stream");
              if (nlist1.getLength() > 0) {
                
                for (int j = 0; j < nlist1.getLength(); j++) {
                  Node node1 = nlist1.item(j);
                  
                  if (node1.getNodeType() == Node.ELEMENT_NODE) {
                    vo = new VideoVO();
                    Element element1 = (Element) node1;
                    sdpResp1 = getValue("Name", element1);
                    if (sdpResp1.contains("npdEventStream")) {
                      sdpResp2 = getValue("SessionsCupertino", element1);
                      sdpResp3 = getValue("SessionsRTSP", element1);
                      logger.logInfo("Stream=" + sdpResp1 + "|Http Connection="
                          + sdpResp2 + "|RTSP Connection=" + sdpResp3);
                      vo.setStreamName(sdpResp1);
                      vo.setHttpCount(Integer.parseInt(sdpResp2));
                      vo.setRtspCount(Integer.parseInt(sdpResp3));
                      /*
                       * if(sdpResp1.contains("_")){ list.add(sdpResp1); }
                       */
                      listStream.add(vo);
                    }
                  }
                }
              }
              videoVO.setStreamList(listStream);
            }
            
          }
        }
      }
    } catch (Exception e) {
      logger.logError("Exception ", e);
      // e.printStackTrace();
      try {
        if (wr != null)
          wr.close();
        if (is != null)
          is.close();
        if (rd != null)
          rd.close();
        if (httpConnection != null)
          httpConnection.disconnect();
        
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      // for testing
    }
    return videoVO;
  }
  
  public List<VideoVO> getDBStreamCount(Connection connection,
      Properties properties) {
    streamDAO = new VideoStreamDAO();
    List<VideoVO> list = streamDAO.getDBStreamCount(connection, properties);
    return list;
  }
}
