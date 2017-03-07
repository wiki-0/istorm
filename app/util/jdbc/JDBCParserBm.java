package util.jdbc;

import java.sql.*;

/**
 * 
 * Project Name：istorm_patrol
 * Class Name：JDBCParser
 * Class Desc：jdbc driver
 * Author：Liwei
 * Create Date：2015-3-1 下午8:26:58
 * Last Modified By：Liwei
 * Last Modified：2015-3-1 下午8:26:58
 * Remarks：
 * @version 
 * 
 */
public class JDBCParserBm {
	
	private Connection con;
	private Statement stmt;

	/**
	 * @Title: connect
	 * @Description: connect to db
	 * @param @param dbType
	 *               db's jdbc driver
	 * @param @param url
	 *               db's url
	 * @param @param user
	 *               db's user
	 * @param @param pwd
	 *               db's password
	 * @param @return
	 * @return boolean
	 *             true is connect , false is not
	 * @throws
	 * @since CloudWei v1.0.0
	*/
	public synchronized boolean connect(String dbType, String url, String user, String pwd) {
		try {
			Class.forName(dbType).newInstance(); 
			con = DriverManager.getConnection(
					url, user, pwd); 
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * @Title: insert
	 * @Description: insert to sql
	 * @param @param sqlQuery
	 *               sql text
	 * @return void
	 * @throws
	 * @since CloudWei v1.0.0
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
	
	public synchronized boolean insertBm(String sqlQuery) throws SQLException {
		stmt = con.createStatement();
		stmt.executeUpdate(sqlQuery);
		return false;
    }
	
	public synchronized void close()
	{
		if(stmt!=null)
		{
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(con!=null)
		{
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
	
	
