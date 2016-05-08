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
		this.setTitle("������");
		this.setSize(width, height);
		this.setLocation(x, y);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				// ����¼��ļ�����,�ڲ���
				int quit = JOptionPane.showConfirmDialog(null, "�Ƿ��˳�");
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
		JLabel userLb = new JLabel("�û���");
		userLb.setBounds(100, 30, 50, 50);
		this.add(userLb);

		JLabel passwordLb = new JLabel("���룺");
		passwordLb.setBounds(100, 80, 50, 50);
		this.add(passwordLb);

		 usertf = new JTextField("�������û���");
		usertf.setBounds(150, 40, 100, 35);
		usertf.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String str = usertf.getText();
				if (str.equals(""))
					usertf.setText("�������û���");
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				String str = usertf.getText();
				if (str.equals("�������û���"))
					usertf.setText("");
			}
		});
		this.add(usertf);

		passwordtf = new TextField("����������");
		passwordtf.setEchoChar('*');
		passwordtf.setBounds(150, 90, 100, 35);
		
		passwordtf.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				String str1 = passwordtf.getText();
				if (str1.equals(""))
					passwordtf.setText("����������");
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				String str = passwordtf.getText();
				if (str.equals("����������"))
					passwordtf.setText("");
			}
		});
		this.add(passwordtf);

		//��½��ť
		JButton checkInBt = new JButton("��½");
		checkInBt.setBounds(80, 150, 80, 40);
		checkInBt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String user = usertf.getText();
				String password  = passwordtf.getText();
				//��ֹû������
				if(user.equals("�������û���") || password.equals("����������")) {
					JOptionPane.showMessageDialog(null, "�����������ĵ�½��Ϣ��лл��");
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

		//ע�ᰴť
		JButton register = new JButton("ע��");
		register.setBounds(170, 150, 80, 40);
		register.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new RegisterStuUI();
			}
		});
		this.add(register);

		
		//���߰�ť
		JButton checkout = new JButton("����");
		checkout.setBounds(260, 150, 80, 40);
		checkout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// ����¼��ļ�����,�ڲ���
				int quit = JOptionPane.showConfirmDialog(null, "�Ƿ��˳�");
				if (quit == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});
		this.add(checkout);

	}

	/**
	 * �����ݿ����ж��Ƿ��и��û�������У���ô��½�������Ľ���
	 * @param user �û���
	 * @param password ����
	 * @return �ɹ�����null�����򷵻ش�����Ϣ��
	 */
	private String checkIn(String user, String password) {
		Connection con = ConnectDB.connectDB();
		String info = null;
		Statement stmt = null;
		ResultSet res = null;
		boolean tag = false;
		
		
		String userInLb = null;  //�û���
		int state = -1;  //��¼�û�����״̬
		
		try {
			stmt = con.createStatement();
			res = stmt.executeQuery("select * from users;");

			// ������⣬������������ֵ��鱾����ʾ����
			while (res.next()) {
				int id = res.getInt(1);
				userInLb = res.getString(2);   //�û���
				state = res.getInt(3);            //��¼״̬
				String passwordInLb = res.getString(4);  //����
				if (user.equals(userInLb) && password.equals(passwordInLb)) {
					tag = true;
					break;
				}
			}
			
			//����û����ڣ������Ѿ�����
			if(tag == true && state == 1){
				return "�û��Ѿ����ߣ���ע�������Ƿ�й¶��";
			}
			//����û����ڵ���û�����ߣ���ô�ɹ���½��ͬʱ�޸�����״̬Ϊ 1���Ѿ����ߣ�
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
				return "�û����������";
			}
		}
		return null;
	}

	
	
	/**
	 * ���������״̬����ô�ĳ����ߣ����������״̬��ô�ĳ����ߡ�
	 * @param name �û���
	 * @return �޸ĳɹ� ����true ����֮��
	 */
	public static void changeState(String name, int state) {
		Connection con = ConnectDB.connectDB();
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;

		// ִ�ж�̬������statement
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
	 * �����û���Ϣ������Ӧ�Ľ���
	 * @param user
	 * @param id
	 * @param type
	 * @return ����ɹ�����true�����򷵻ش������Ϣ��
	 */
	private void startUser(String user) {
		new ChatUI(user);
	}
}
