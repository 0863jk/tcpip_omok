package team_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
	
	public static Connection conn;
	public static Statement stmt;
	public static void init() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.25.4:1521:XE",
					"omok",
					"1234");
			
			stmt = conn.createStatement();
			
			System.out.println("DB 연결 성공");
			
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버 로드 오류");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("DB 연결 오류 또는 Query 오류");
			e.printStackTrace();
		}
	}
	
	// 조회
	public static ResultSet getResultSet(String sql) {
		try {
			Statement stmt = conn.createStatement();
			return stmt.executeQuery(sql);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}

	}
	
	// 삽입, 수정, 삭제
	public static void executeUpdate(String sql) {
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
	}
}
