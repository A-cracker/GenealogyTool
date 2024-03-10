package com.genealogy.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.fy.wetoband.tool.Tool;
/**
 * 
 * @author zzb
 *
 */
public class ConnectionUtil {
	private static Tool tool = null;
	public static void init(Tool tool1) {
		tool = tool1;
	}
	public static Connection getConnection() {
		return tool.getConnection();
	}
	public static void closeConnection(){
		try {
			Connection conn = getConnection();
			if(conn!=null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
