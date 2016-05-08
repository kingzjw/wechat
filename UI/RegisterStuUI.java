package UI;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import Main.ConnectDB;

import com.mysql.jdbc.Connection;

public class RegisterStuUI extends JFrame {
	JTextField usertf = null;
	TextField passwordtf = null;
	TextField passwordtf2 = null;
	
	private static final int width = 400;
	private static final int height = 250;
	int x = 500;
	int y = 200;
	String userDefault = "请输入新用户名";
	String passwordDefault1 = "请输入密码";
	String passwordDefault2 = "再次输入密码";

	public RegisterStuUI() {
		this.setTitle("学生注册");
		this.setSize(width, height);
		this.setLocation(x, y);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}

		});
		this.setLayout(null);

		surface();

		this.setResizable(false);
		this.setVisible(true);
	}

	private void surface() {
		// 用户行
		JLabel userLb = new JLabel("新用户：");
		userLb.setBounds(50, 30, 60, 50);
		this.add(userLb);

		 usertf = new JTextField(userDefault);
		usertf.setBounds(130, 40, 100, 35);
		usertf.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String str = usertf.getText();
				if (str.equals(""))
					usertf.setText(userDefault);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				String str = usertf.getText();
				if (str.equals(userDefault))
					usertf.setText("");
			}
		});
		this.add(usertf);

		// 新密码
		JLabel passwordLb = new JLabel("新密码：");
		passwordLb.setBounds(50, 80, 60, 50);
		this.add(passwordLb);

		 passwordtf = new TextField(passwordDefault1);
		passwordtf.setEchoChar('*');
		passwordtf.setBounds(130, 90, 100, 35);
		passwordtf.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String str1 = passwordtf.getText();
				if (str1.equals(""))
					passwordtf.setText(passwordDefault1);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				String str = passwordtf.getText();
				if (str.equals(passwordDefault1))
					passwordtf.setText("");
			}
		});
		this.add(passwordtf);

		// 确认密码行
		JLabel passwordLb2 = new JLabel("确认密码：");
		
		passwordLb2.setBounds(40, 130, 80, 50);
		this.add(passwordLb2);

		passwordtf2 = new TextField(passwordDefault2);
		passwordtf2.setEchoChar('*');
		passwordtf2.setBounds(130, 140, 100, 35);
		passwordtf2.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String str1 = passwordtf2.getText();
				if (str1.equals(""))
					passwordtf2.setText(passwordDefault2);
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				String str = passwordtf2.getText();
				if (str.equals(passwordDefault2))
					passwordtf2.setText("");
			}
		});
		this.add(passwordtf2);

		// 登陆按钮
		JButton checkInBt = new JButton("注册");
		checkInBt.setBounds(250, 50, 80, 40);
		checkInBt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String user = usertf.getText();
				String password = passwordtf.getText();
				String password2 = passwordtf2.getText();

				// 防止没有输入
				if (user.equals(passwordDefault1)
						|| password.equals(passwordDefault1)
						|| password2.equals(passwordDefault2)) {
					JOptionPane.showMessageDialog(null, "请输入完整的登陆信息，谢谢！");
				} else if (!password.equals(password2)) {
					JOptionPane.showMessageDialog(null, "你的这两次的密码不一致，请重新输入！");
					passwordtf.setText(passwordDefault1);
					passwordtf2.setText(passwordDefault2);
				} else {
					String info = addNewStu(user, password);
					if (info == null) {
						JOptionPane.showMessageDialog(null, "您的账号已经成功注册");
						setVisible(false);
					} else {
						JOptionPane.showMessageDialog(null, info);
						usertf.setText(userDefault);
						passwordtf.setText(passwordDefault1);
						passwordtf2.setText(passwordDefault2);
					}
				}
			}
		});
		this.add(checkInBt);

		// 下线按钮
		JButton checkout = new JButton("退出");
		checkout.setBounds(250, 100, 80, 40);
		checkout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		this.add(checkout);

	}

	/**
	 * 添加新的学生用户。
	 * 
	 * @param user
	 *            用户名
	 * @param password
	 *            密码
	 * @return 如果成功返回null，否则返回false；
	 */
	private String addNewStu(String user, String password) {
		String info = null;
		Connection con = ConnectDB.connectDB();
		PreparedStatement pstmt = null;
		Random rd = new Random();

		// 执行动态的语句的statement
		try {
			int id = rd.nextInt();
			pstmt = con.prepareStatement("insert users values(?,?,?,?);");
			pstmt.setInt(1, id);
			pstmt.setString(2, user);
			pstmt.setInt(3, 0);
			pstmt.setString(4, password);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			info = "用户已存在，请输入新的用户名！";
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		ConnectDB.disConnectDB(con);
		return info;
	}
}
