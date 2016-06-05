package com.comviva.utility;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class AppLogger implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected transient Logger logger = null;
    private static final String lineSeperator = "\r\n";
    private static final String indent = "    ";

    /**
     * Constructor , logger will be associated with aServiceClass instance
     * 
     * @param aServiceClass
     */
    @SuppressWarnings("unchecked")
    public AppLogger(Class aServiceClass) {
	logger = Logger.getLogger(aServiceClass);
    }

    /**
     * logInfo mehod is used to log info messages in log file.
     * 
     * @param str
     *                Message to be logged
     * @param t
     *                Throwable Exception
     */
    public void logInfo(String str, Throwable t) {
	if (logger.isInfoEnabled())
	    logger.info(str, t);
    }

    /**
     * logInfo mehod is used to log info messages in log file.
     * 
     * @param str
     *                Message to be logged
     */
    public void logInfo(String str) {
	if (logger.isInfoEnabled()) {
	    String threadID = Thread.currentThread().getName();
	    str = "Msg:" + str;
	    logger.info("ThreadID="+threadID+"|"+str);
	}
    }

    /**
     * logDebug mehod is used to log debug messages in log file.
     * 
     * @param str
     *                Message to be logged
     * @param t
     *                Throwable Exception
     */
    public void logDebug(String str, Throwable t) {
	if (logger.isDebugEnabled())
	    logger.debug(str, t);
    }

    /**
     * logDebug mehod is used to log debug messages in log file.
     * 
     * @param str
     *                Message to be logged
     */
    public void logDebug(String str) {
	String threadID = Thread.currentThread().getName();
	str = "Msg:" + str;
	if (logger.isDebugEnabled())
	    logger.info("ThreadID="+threadID+"|"+str);

    }

    /**
     * logWarn mehod is used to log warn messages in log file.
     * 
     * @param str
     *                Message to be logged
     * @param t
     *                Throwable Exception
     */
    public void logWarn(String str, Throwable t) {
	logger.warn(str, t);
    }

    /**
     * logWarn mehod is used to log warn messages in log file.
     * 
     * @param str
     *                Message to be logged
     */
    public void logWarn(String str) {
	String threadID = Thread.currentThread().getName();
	str = threadID + ",Class:" + this.getClass() + ",Msg:" + str;
	logger.warn(str);

    }

    /**
     * This method is to write debug message into log file
     * 
     * @param msg
     */
    public void log(String aMessage) {
	if (logger.isDebugEnabled())
	    logger.debug(aMessage);

    }

    /**
     * This method is to write error message into log file and database
     * 
     * @param msg
     */
    public void log(String aMessage, Exception anException) {
	logger.error(aMessage, anException);
    }

    /**
     * This method is to write error message into log file and database
     * 
     * @param msg
     */
    public void logError(Throwable aThrowable, Exception anException) {
	if (aThrowable != null) {
	    logger.error("caused by: \n" + getStackTrace(aThrowable),
		    anException);
	}
    }

    /**
     * This method is to write exception into log file and database
     * 
     * @param anException
     */
    public void logError(String error, Exception anException) {

	logger.error(error + " caused by: " + getStackTrace(anException));
    }

    /**
     * This method is to write fatal error message into log file and database
     * 
     * @param msg
     */
    public void logFatal(Throwable aThrowable, Exception anException) {
	if (aThrowable != null) {
	    logger
		    .fatal("caused by: " + getStackTrace(aThrowable),
			    anException);
	}
    }

    /**
     * This method is to write fatal error into log file and database
     * 
     * @param anException
     */
    public void logFatal(String error, Exception anException) {

	logger.error(error + " caused by: " + getStackTrace(anException));
    }

    /**
     * Get stack trace from exception
     * 
     * @param aThrowable
     * @return
     */
    private String getStackTrace(Throwable t) {
	StringBuffer sb = new StringBuffer();

	sb.append(lineSeperator).append(indent).append("<Exception>").append(
		lineSeperator);
	sb.append(indent).append(indent).append("<Message>").append(
		t.toString()).append("</Message>").append(lineSeperator);
	sb.append(indent).append(indent).append("<StackTrace>").append(
		lineSeperator);
	// format throwable's stack trace
	StackTraceElement[] elements = t.getStackTrace();
	for (int i = 0; i < elements.length; i++) {
	    StackTraceElement e = elements[i];
	    sb.append(indent).append(indent).append(indent).append(indent)
		    .append("at ");
	    sb.append(e.getClassName()).append(".").append(e.getMethodName());
	    sb.append("(").append(e.getFileName()).append(":").append(
		    e.getLineNumber()).append(")");
	    sb.append(lineSeperator);
	}
	sb.append(indent).append(indent).append("</StackTrace>").append(
		lineSeperator);
	sb.append(indent).append("</Exception>").append(lineSeperator);
	return sb.toString();
    }

}
