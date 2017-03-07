package util.jdbc;

import java.sql.*;

/**
 * 
 * @version
 * 
 */
public class JDBCParser {

	private Connection con;
	private Statement stmt;

	/**
	 * @Title: connect
	 * @Description: connect to db
	 * @param @param
	 *            dbType db's jdbc driver
	 * @param @param
	 *            url db's url
	 * @param @param
	 *            user db's user
	 * @param @param
	 *            pwd db's password
	 * @param @return
	 * @return boolean true is connect , false is not
	 */
	public boolean connect(String dbType, String url, String user, String pwd) {
		try {
			Class.forName(dbType).newInstance();
			con = DriverManager.getConnection(url, user, pwd);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @Title: insert
	 * @Description: insert to sql
	 * @param @param
	 *            sqlQuery sql text
	 * @return void
	 */
	public void insert(String sqlQuery) {
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sqlQuery);
			stmt.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean sqlExecute(String sql) {
		try {
			stmt = con.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);
			return resultSet.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public ResultSet sqlQuery(String sql) {
		ResultSet resultSet = null;
		try {
			stmt = con.createStatement();
			resultSet = stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultSet;
	}

	public void insertBm(String sqlQuery) throws SQLException {
		stmt = con.createStatement();
		stmt.executeUpdate(sqlQuery);
	}

	public void close() {
		try {
			stmt.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
