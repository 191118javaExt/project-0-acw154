package com.revature.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class ConnectionUtil {
	
	private static Logger logger = Logger.getLogger(ConnectionUtil.class);

	public static Connection getConnection() {
		
		// The url includes the driver we're using, which is from postgres,
		// as well as the computer, which localhost, and the port, which is 5432
		// The last '/' is important, as it refers to the specific database
		// we are connecting to
		// However, since we only have 1 database, we can connect to that one
		// without specifying
		String url = "jdbc:postgresql://localhost:5432/postgres?currentschema=project0";
		String username = "postgres";
		String password = "postgres";
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			logger.warn("Unable to obtain connection to database", e);
		}
		
		return conn;
	}
}
