package Main;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;


public class ConnectDB {
	
	public static Connection connectDB() {
		Connection con = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			// 连接数据库
			String url = "jdbc:mysql://localhost:3306/chatroom";
			String username = "root";
			String password = "1234";
			con = (Connection) DriverManager.getConnection(url, username, password);
			//System.out.println("数据库library连接成功！");
		} catch (ClassNotFoundException e) {
			System.out.println("加载驱动器失败！");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("library数据库连接失败");
			e.printStackTrace();
		}
		
		return con;
	}

	/**
	 * 断开和图书馆数据库的连接
	 * @param con 需要断开的连接
	 */
	public static void disConnectDB(Connection con){
		if(con != null)
			try {
				con.close();
				//System.out.println("关闭数据库链接！");
			} catch (SQLException e) {
				System.out.println("断开连接失败！");
				e.printStackTrace();
			}
	}
}
