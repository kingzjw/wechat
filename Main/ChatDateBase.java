package Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.mysql.jdbc.Connection;

public class ChatDateBase {

	/**
	 * 得到聊天室活跃的人
	 * 
	 * @param talkList
	 * @return
	 */
	public static void seachActiveTalker(List<String> talkList) {
		talkList.clear();
		talkList.add("All");
		
		Connection con = ConnectDB.connectDB();
		Statement stmt = null;
		ResultSet res = null;

		String userInLb = null; // 用户名
		int state = -1; // 记录用户在线状态

		try {
			stmt = con.createStatement();
			res = stmt.executeQuery("select * from users;");

			while (res.next()) {
				userInLb = res.getString(2); // 用户名
				state = res.getInt(3); // 登录状态

				if (state == 1) {
					
					talkList.add(userInLb);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}

				if (stmt != null) {
					stmt.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			ConnectDB.disConnectDB(con);
		}
	}

}
