package UI;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.omg.PortableInterceptor.ACTIVE;

import com.mysql.jdbc.Connection;

import Main.ConnectDB;

public class ControlUI extends JFrame {
	JTextField usertf = null;
	TextField passwordtf  = null;
	
	private static final int width = 400;
	private static final int height = 250;
	int x = 500;
	int y = 200;

	public ControlUI() {
		this.setTitle("聊天室");
		this.setSize(width, height);
		this.setLocation(x, y);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				// 添加事件的监听器,内部类
				int quit = JOptionPane.showConfirmDialog(null, "是否退出");
				if (quit == JOptionPane.OK_OPTION) {
					System.exit(0);
				}

			}

		});
		this.setLayout(null);
		surface();
		this.setResizable(false);
		this.setVisible(true);
	}

	private void surface() {
		JLabel userLb = new JLabel("用户：");
		userLb.setBounds(100, 30, 50, 50);
		this.add(userLb);

		JLabel passwordLb = new JLabel("密码：");
		passwordLb.setBounds(100, 80, 50, 50);
		this.add(passwordLb);

		 usertf = new JTextField("请输入用户名");
		usertf.setBounds(150, 40, 100, 35);
		usertf.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String str = usertf.getText();
				if (str.equals(""))
					usertf.setText("请输入用户名");
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				String str = usertf.getText();
				if (str.equals("请输入用户名"))
					usertf.setText("");
			}
		});
		this.add(usertf);

		passwordtf = new TextField("请输入密码");
		passwordtf.setEchoChar('*');
		passwordtf.setBounds(150, 90, 100, 35);
		
		passwordtf.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String str1 = passwordtf.getText();
				if (str1.equals(""))
					passwordtf.setText("请输入密码");
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				String str = passwordtf.getText();
				if (str.equals("请输入密码"))
					passwordtf.setText("");
			}
		});
		this.add(passwordtf);

		//登陆按钮
		JButton checkInBt = new JButton("登陆");
		checkInBt.setBounds(80, 150, 80, 40);
		checkInBt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String user = usertf.getText();
				String password  = passwordtf.getText();
				//防止没有输入
				if(user.equals("请输入用户名") || password.equals("请输入密码")) {
					JOptionPane.showMessageDialog(null, "请输入完整的登陆信息，谢谢！");
				}else{
					usertf.setText("");
					passwordtf.setText("");
					String info = checkIn(user, password);
					if(info != null)
						JOptionPane.showMessageDialog(null, info);
				}
			}
		});
		this.add(checkInBt);

		//注册按钮
		JButton register = new JButton("注册");
		register.setBounds(170, 150, 80, 40);
		register.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new RegisterStuUI();
			}
		});
		this.add(register);

		
		//下线按钮
		JButton checkout = new JButton("下线");
		checkout.setBounds(260, 150, 80, 40);
		checkout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// 添加事件的监听器,内部类
				int quit = JOptionPane.showConfirmDialog(null, "是否退出");
				if (quit == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});
		this.add(checkout);

	}

	/**
	 * 在数据库中判断是否有该用户，如果有，那么登陆属于他的界面
	 * @param user 用户名
	 * @param password 密码
	 * @return 成功返回null，否则返回错误信息。
	 */
	private String checkIn(String user, String password) {
		Connection con = ConnectDB.connectDB();
		String info = null;
		Statement stmt = null;
		ResultSet res = null;
		boolean tag = false;
		
		
		String userInLb = null;  //用户名
		int state = -1;  //记录用户在线状态
		
		try {
			stmt = con.createStatement();
			res = stmt.executeQuery("select * from users;");

			// 遍历书库，如果有类似名字的书本，显示给作
			while (res.next()) {
				int id = res.getInt(1);
				userInLb = res.getString(2);   //用户名
				state = res.getInt(3);            //登录状态
				String passwordInLb = res.getString(4);  //密码
				if (user.equals(userInLb) && password.equals(passwordInLb)) {
					tag = true;
					break;
				}
			}
			
			//如果用户存在，但是已经在线
			if(tag == true && state == 1){
				return "用户已经在线，请注意密码是否泄露！";
			}
			//如果用户存在但是没有在线，那么成功登陆，同时修改在线状态为 1（已经在线）
			if(tag == true && state == 0){
				changeState(userInLb,state);
				startUser(user);
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

			if(tag == false) {
				return "用户名密码错误！";
			}
		}
		return null;
	}

	
	
	/**
	 * 如果是下线状态，那么改成上线，如果是上线状态那么改成下线。
	 * @param name 用户名
	 * @return 修改成功 返回true 否则反之。
	 */
	public static void changeState(String name, int state) {
		Connection con = ConnectDB.connectDB();
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;

		// 执行动态的语句的statement
		try {
			pstmt = con
					.prepareStatement("update users Set state=? Where name = ? ");
			if(state == 0){
				pstmt.setInt(1, 1);				
			}else {
				pstmt.setInt(1, 0);
			}
			pstmt.setString(2, name);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}

				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		ConnectDB.disConnectDB(con);
	}
	
	
	/**
	 * 根据用户信息启动相应的界面
	 * @param user
	 * @param id
	 * @param type
	 * @return 如果成功返回true，否则返回错误的信息。
	 */
	private void startUser(String user) {
		new ChatUI(user);
	}
}
