package com.comviva.scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.comviva.bo.VideoStreamBO;
import com.comviva.db.DBConnectionPooler;
import com.comviva.utility.AppLogger;
import com.comviva.utility.SingletonPropertyLoader;
import com.comviva.vo.VideoVO;

public class VideoScheduler implements Job {

    AppLogger logger = new AppLogger(getClass());
    Properties properties = SingletonPropertyLoader.loadPropertyFile();

    @Override
    public void execute(final JobExecutionContext ctx) throws JobExecutionException {

	String streamName = "", appName = "";
	Connection connection = null;
	String url = (String) properties.getProperty("WOWZA_URL");
	appName = (String) properties.getProperty("WOWZA_APP_NAME");
	streamName = (String) properties.getProperty("WOWZA_STREAM_NAME");
	VideoVO videoVO = null;
	if (!appName.equals("") && !streamName.equals("")) {
	    VideoStreamBO streamBO = new VideoStreamBO();
	    videoVO = streamBO.getLiveStreamConnection(properties, url, appName, streamName);
	}
	System.out.println(videoVO.getStreamList());
	Iterator<VideoVO> iterator = videoVO.getStreamList().iterator();
	PreparedStatement stmt = null;
	try {
	    connection = DBConnectionPooler.getDatabaseConnection("GLOBAL");
	    Date date = new Date();
	    Timestamp sqlTime = new Timestamp(date.getTime());
	    String sql = (String)properties.getProperty("STORE_STREAM_CONNECTION");
	    stmt = connection.prepareStatement(sql);
	    while (iterator.hasNext()) {
		VideoVO vo = (VideoVO) iterator.next();
		System.out.println(vo.getStreamName() + "|" + vo.getHttpCount() + "|" + vo.getRtspCount());
		stmt.setTimestamp(1, sqlTime);
		stmt.setString(2, vo.getStreamName());
		stmt.setInt(3, vo.getHttpCount());
		stmt.setInt(4, vo.getRtspCount());
		stmt.addBatch();

	    }
	    stmt.executeBatch();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		if(stmt!=null)
		    stmt.close();
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
