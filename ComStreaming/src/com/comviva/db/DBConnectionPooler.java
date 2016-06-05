package com.comviva.db;


import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;
import com.comviva.utility.AppLogger;
import com.comviva.utility.IniUtils;

public class DBConnectionPooler
{
	private static final AppLogger LOGGER = new AppLogger(DBConnectionPooler.class);

	private static DBConnectionPooler dbConnectionPooler = null;

	private static final Map<String, ComboPooledDataSource> dataSources = new ConcurrentHashMap<String, ComboPooledDataSource>();

	public static final Object synchronizedLock = new Object();

	public static final String PATH_SEPERATOR = File.separator;
	public static final String COMMA = ",";

	public static final String DB_URL = "DB_URL";
	public static final String DB_DRIVER = "DB_DRIVER";
	public static final String DB_USER_NAME = "DB_USER_NAME";
	public static final String DB_PASSWORD = "DB_PASSWORD";
	public static final String DB_MAX_STATEMENTS = "DB_MAX_STATEMENTS";
	public static final String DB_MAX_STATEMENTS_PER_CONNECTION = "DB_MAX_STATEMENTS_PER_CONNECTION";
	public static final String DB_ACQUIRE_INCREMENT = "DB_ACQUIRE_INCREMENT";
	public static final String DB_INITIAL_POOL_SIZE = "DB_INITIAL_POOL_SIZE";
	public static final String DB_MIN_POOL_SIZE = "DB_MIN_POOL_SIZE";
	public static final String DB_MAX_POOL_SIZE = "DB_MAX_POOL_SIZE";
	public static final String DB_ACQUIRE_RETRY_ATTEMPTS = "DB_ACQUIRE_RETRY_ATTEMPTS";
	public static final String DB_IDLE_CONNECTION_TEST_PERIOD = "DB_IDLE_CONNECTION_TEST_PERIOD";
	public static final String DB_MAX_ADMINISTRATIVE_TASK_TIME = "DB_MAX_ADMINISTRATIVE_TASK_TIME";
	public static final String DB_AUTO_COMMIT_ON_CLOSE = "DB_AUTO_COMMIT_ON_CLOSE";
	public static final String DB_REQ_TABLE_NAME = "DB_REQ_TABLE_NAME";
	public static final String DB_RESP_TABLE_NAME = "DB_RESP_TABLE_NAME";
	public static final String DB_RESP_POLL_INTERVAL = "DB_RESP_POLL_INTERVAL";
	public static final String UNRETURNED_CONNECTION_TIMEOUT = "UNRETURNED_CONNECTION_TIMEOUT";
	public static final String DEBUG_UNRETURNED_CONNECTION_STACKTRACES = "DEBUG_UNRETURNED_CONNECTION_STACKTRACES";
	public static final String CONNECTION_CHECKOUT_TIMEOUT = "CHECKOUT_TIMEOUT";

	public static final String INI_SHORT_CODES = "SHORT_CODES";
	public static final String INI_ACTIVE = "ACTIVE";
	
	

	private DBConnectionPooler()
	{

	}

	private static DBConnectionPooler getInstance(){
		String configPath = "dbConfig.ini";
		synchronized (synchronizedLock)
		{
			if (dbConnectionPooler == null && configPath != null && configPath.length()>0)
			{
				dbConnectionPooler = new DBConnectionPooler();
				createDataSources(configPath);
			}
		}
		return dbConnectionPooler;
	}

	public static Connection getDatabaseConnection(String shortCode){
		getInstance();
		ComboPooledDataSource dataSource = null;
		Connection connection = null;
		//synchronized (dataSources)
		//{
			dataSource = dataSources.get(shortCode);
		//}
		if (dataSource != null)
		{
			try
			{
				connection = dataSource.getConnection();
			} catch (SQLException e)
			{
			    LOGGER.logError("getDatabaseConnection", e);
			}
		}
		System.out.println(shortCode);
		getStatus(dataSource);
		
		return connection;
	}

	private static void createDataSources(String confFilePath){
		LOGGER.logInfo("Creating the datasources");

		IniUtils iniUtils = getDBConfigIni(confFilePath);
		if (iniUtils != null)
		{
			String activeShortCodes = iniUtils.get(INI_SHORT_CODES, INI_ACTIVE);
			if (activeShortCodes != null)
			{
				activeShortCodes = activeShortCodes.trim();
				String[] shortCodes = activeShortCodes.split(COMMA);
				for (String shortCode : shortCodes)
				{
					shortCode = shortCode.trim();
					ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
					comboPooledDataSource.setJdbcUrl(iniUtils.get(shortCode, DB_URL));
					comboPooledDataSource.setUser(iniUtils.get(shortCode, DB_USER_NAME));
					comboPooledDataSource.setPassword(iniUtils.get(shortCode, DB_PASSWORD));
					try
					{
						comboPooledDataSource.setDriverClass(iniUtils.get(shortCode, DB_DRIVER));
					} catch (Exception e1)
					{
					    LOGGER.logError("createDataSources", e1);
					}

					int maxStatements = 0;
					try
					{
						maxStatements = Integer.valueOf(iniUtils.get(shortCode, DB_MAX_STATEMENTS));
					} catch (Exception e)
					{
					    LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setMaxStatements(maxStatements);

					int maxStatementsPerConn = 100;
					try
					{
						maxStatementsPerConn = Integer.valueOf(iniUtils
								.get(shortCode, DB_MAX_STATEMENTS_PER_CONNECTION));
					} catch (Exception e)
					{
					    LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setMaxStatementsPerConnection(maxStatementsPerConn);

					int acquireIncrement = 100;
					try
					{
						acquireIncrement = Integer.valueOf(iniUtils.get(shortCode, DB_ACQUIRE_INCREMENT));
					} catch (Exception e)
					{
					    LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setAcquireIncrement(acquireIncrement);

					int initialPoolSize = 2;
					try
					{
						initialPoolSize = Integer.valueOf(iniUtils.get(shortCode, DB_INITIAL_POOL_SIZE));
					} catch (Exception e)
					{
					    LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setInitialPoolSize(initialPoolSize);

					int minPoolSize = 2;
					try
					{
						minPoolSize = Integer.valueOf(iniUtils.get(shortCode, DB_MIN_POOL_SIZE));
					} catch (Exception e)
					{
					    LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setMinPoolSize(minPoolSize);

					int maxPoolSize = 5;
					try
					{
						maxPoolSize = Integer.valueOf(iniUtils.get(shortCode, DB_MAX_POOL_SIZE));
					} catch (Exception e)
					{
					    LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setMaxPoolSize(maxPoolSize);

					int acquireRetryAttempts = 3;
					try
					{
						acquireRetryAttempts = Integer.valueOf(iniUtils.get(shortCode, DB_ACQUIRE_RETRY_ATTEMPTS));
					} catch (Exception e)
					{
					    LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setAcquireRetryAttempts(acquireRetryAttempts);

					int idleConnectionTestPeriod = 10800;
					try
					{
						idleConnectionTestPeriod = Integer.valueOf(iniUtils.get(shortCode,
								DB_IDLE_CONNECTION_TEST_PERIOD));
					} catch (Exception e)
					{
					    LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);

					int maxAdministrativeTaskTime = 0;
					try
					{
						maxAdministrativeTaskTime = Integer.valueOf(iniUtils.get(shortCode,
								DB_MAX_ADMINISTRATIVE_TASK_TIME));
					} catch (Exception e)
					{
					    LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setMaxAdministrativeTaskTime(maxAdministrativeTaskTime);

					boolean autoCommitOnClose = true;
					try
					{
						int tmp = Integer.valueOf(iniUtils.get(shortCode, DB_AUTO_COMMIT_ON_CLOSE));
						if (tmp == 1)
						{
							autoCommitOnClose = true;
						} else
						{
							autoCommitOnClose = false;
						}
					} catch (Exception e)
					{
					    LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setAutoCommitOnClose(autoCommitOnClose);
					int unreturnedConnectionTimeout = 0; 
					try
					{
						unreturnedConnectionTimeout = Integer.valueOf(iniUtils.get(shortCode, UNRETURNED_CONNECTION_TIMEOUT));
						
					} catch (Exception e)
					{
						unreturnedConnectionTimeout = 0;
						LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setUnreturnedConnectionTimeout(unreturnedConnectionTimeout);
					
					
					int checkoutTimeout  = 0; 
					try
					{
						checkoutTimeout = Integer.valueOf(iniUtils.get(shortCode, CONNECTION_CHECKOUT_TIMEOUT));
						
					} catch (Exception e)
					{
						checkoutTimeout = 0;
						LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setCheckoutTimeout(checkoutTimeout);
					
					
					boolean debugUnreturnedConnectionStackTraces = false; 
					try
					{
						debugUnreturnedConnectionStackTraces = (Integer.valueOf(iniUtils.get(shortCode, DEBUG_UNRETURNED_CONNECTION_STACKTRACES)))==1?true:false;
						
					} catch (Exception e)
					{
						debugUnreturnedConnectionStackTraces = false;
						LOGGER.logError("createDataSources", e);
					}
					comboPooledDataSource.setDebugUnreturnedConnectionStackTraces(debugUnreturnedConnectionStackTraces);
					addDataSource(shortCode, comboPooledDataSource);
				}
			}
		}
	}

	private static void addDataSource(String shortCode, ComboPooledDataSource dataSource)
	{
		synchronized (dataSource)
		{
			dataSource = dataSources.put(shortCode, dataSource);
		}
	}

	private static IniUtils getDBConfigIni(String fileName){
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		if(classLoader==null){
			classLoader = ClassLoader.getSystemClassLoader();
		}
		InputStream iniFileStream = classLoader.getResourceAsStream(fileName);
		//InputStream iniFileStream = getPropertiesStream(fileName);
		IniUtils iniUtils = new IniUtils();
		try
		{
			if(iniFileStream!=null){
				iniUtils.load((InputStream) iniFileStream);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			LOGGER.logError("getDBConfigIni", e);
			//return null;
		} finally
		{
			try
			{
				if (iniFileStream != null)
				{
					iniFileStream.close();
				}
			} catch (Exception ee)
			{
			}
		}
		return iniUtils;
	}

	/*private static InputStream getPropertiesStream(String propertyFileName) throws BLException
	{
		InputStream fileStream = null;
		try
		{
			
			fileStream = new FileInputStream(propertyFileName);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			throw new BLException("Properties file " + propertyFileName + " not found" + e.getMessage());
		} catch (Exception e)
		{
			throw new BLException("Failed to read the configuration file:" + propertyFileName, e);
		}
		return fileStream;
	}*/
	
	public static void getStatus(DataSource ds) {
		try {
			if (ds instanceof PooledDataSource) {
				PooledDataSource pds = (PooledDataSource) ds;
				
				LOGGER.logInfo("num_connections: "
						+ pds.getNumConnectionsDefaultUser());
				LOGGER.logInfo("num_busy_connections: "
						+ pds.getNumBusyConnectionsDefaultUser());
				LOGGER.logInfo("num_idle_connections: "
						+ pds.getNumIdleConnectionsDefaultUser());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

