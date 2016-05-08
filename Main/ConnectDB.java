package Main;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;


public class ConnectDB {
	
	public static Connection connectDB() {
		Connection con = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			// �������ݿ�
			String url = "jdbc:mysql://localhost:3306/chatroom";
			String username = "root";
			String password = "1234";
			con = (Connection) DriverManager.getConnection(url, username, password);
			//System.out.println("���ݿ�library���ӳɹ���");
		} catch (ClassNotFoundException e) {
			System.out.println("����������ʧ�ܣ�");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("library���ݿ�����ʧ��");
			e.printStackTrace();
		}
		
		return con;
	}

	/**
	 * �Ͽ���ͼ������ݿ������
	 * @param con ��Ҫ�Ͽ�������
	 */
	public static void disConnectDB(Connection con){
		if(con != null)
			try {
				con.close();
				//System.out.println("�ر����ݿ����ӣ�");
			} catch (SQLException e) {
				System.out.println("�Ͽ�����ʧ�ܣ�");
				e.printStackTrace();
			}
	}
}
