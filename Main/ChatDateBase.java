package Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.mysql.jdbc.Connection;

public class ChatDateBase {

	/**
	 * �õ������һ�Ծ����
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

		String userInLb = null; // �û���
		int state = -1; // ��¼�û�����״̬

		try {
			stmt = con.createStatement();
			res = stmt.executeQuery("select * from users;");

			while (res.next()) {
				userInLb = res.getString(2); // �û���
				state = res.getInt(3); // ��¼״̬

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
