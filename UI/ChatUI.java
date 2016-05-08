package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.MenuBar;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Main.ChatDateBase;

public class ChatUI extends JFrame {
	String userName = null;
	static List<String> talkerList = new ArrayList<String>(); // 存放聊天的对象的名字
	static int talkNum = 0;

	// -------------------聊天系统的UI部分-------------------------------------

	int width = 600;
	int height = 400;
	int lctnX = 200;
	int lctnY = 300;

	Panel p = new Panel();

	JButton send = new JButton("发送");
	JButton quit = new JButton("退出");

	JTextArea showPanel = new JTextArea(); // 显示区
	JTextField tf = new JTextField(); // 输入框
	JScrollPane showSP = new JScrollPane(showPanel);

	JLabel userLabel = new JLabel("用户名:");// 提示区
	JLabel userTF = new JLabel(); // 输入框

	JLabel stateLabel = new JLabel("在线状态:");// 提示区
	String[] stateString = { "在线", "离开", "忙绿", "请勿打扰" };
	JComboBox<String> stateList = new JComboBox<String>(stateString); // 输入框

	JLabel talkLabel = new JLabel("聊天对象:");// 提示区
	JComboBox<String> talker = new JComboBox<String>();

	public ChatUI(String userName) {
		this.userName = userName;
		talkNum++;
		// showMenu();
		surface();
		lauchFrame();
		func();
		launchClient();
		new SetTalkPerson().start();
		showInfo(); // 控制面板消息交流
	}

	/**
	 * 整个窗体的外框
	 */
	private void lauchFrame() {
		setLocation(lctnX, lctnY);
		setTitle(userName);
		setSize(width, height);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int quit = JOptionPane.showConfirmDialog(null, "是否退出");
				if (quit == JOptionPane.OK_OPTION) {
					quitUserInLb(userName);
					setVisible(false);
				}
			}

		});

		setBackground(Color.green);
		setVisible(true);
		setResizable(false);
	}

	/**
	 * UI的外观
	 */
	public void surface() {

		p.setLayout(null);
		send.setBounds(40, 325, 65, 30);
		quit.setBounds(125, 325, 65, 30);
		p.add(send);
		p.add(quit);

		userLabel.setBounds(50, 25, 75, 25);
		userTF.setBounds(150, 25, 75, 25);
		userTF.setText(userName);
		userTF.setEnabled(false);
		p.add(userLabel);
		p.add(userTF);

		stateLabel.setBounds(260, 25, 100, 25);
		stateList.setBounds(340, 25, 100, 25);
		p.add(stateLabel);
		p.add(stateList);

		talkLabel.setBounds(50, 65, 75, 25);
		p.add(talkLabel);
		talker.setBounds(140, 65, 75, 25);
		p.add(talker);

		showSP.setBounds(260, 75, 340, 300);
		p.add(showSP);

		tf.setBounds(10, 100, 250, 200);
		p.add(tf);

		add(p);
	}

	/**
	 * 定制菜单
	 */
	// private void showMenu() {
	// JMenuBar jMenuBar = new JMenuBar();
	// jMenuBar.setToolTipText("设置");
	// JMenu j1 = new JMenu("换肤");
	// JMenu j2 = new JMenu("自动下线");
	//
	// JMenuItem item1 = new JMenuItem("肤色1");
	// JMenuItem item2 = new JMenuItem("肤色2");
	//
	// j1.add(item1);
	// j1.add(item2);
	//
	// jMenuBar.add(j1);
	// jMenuBar.add(j2);
	// add(jMenuBar);
	//
	// }

	/**
	 * 按钮的触发器
	 */
	private void func() {

		// 发送
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				send.addActionListener(new TFListener());
			}
		});

		// 用户退出系统，并改变图书馆的状态
		quit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int quit = JOptionPane.showConfirmDialog(null, "是否退出");
				if (quit == JOptionPane.OK_OPTION) {
					quitUserInLb(userName);
					setVisible(false);
				}
			}
		});
	}

	void showInfo() {

		showPanel.append("欢迎" + userName + "加入聊天室\n");

		send(userName + "  加入!\n");

		showPanel.append("亲，您是聊天室的第" + talkNum + "个用户哦\n");
	}

	/**
	 * 下线的时候，修改用户的在线状态
	 * 
	 * @param name
	 *            用户名
	 */
	private void quitUserInLb(String name) {
		talkNum--;
		ControlUI.changeState(name, 1);
	}

	/**
	 * 自动更新在线的人
	 * 
	 * @author king
	 *
	 */
	private class SetTalkPerson extends Thread {
		int oldsize = 0;
		int newsize = 0;

		@Override
		public void run() {
			ChatDateBase.seachActiveTalker(talkerList);

			oldsize = talkerList.size();
			for (int i = 0; i < oldsize; i++) {
				String nameString = talkerList.get(i);
				// 不要把自己加进去
				if (!nameString.equals(userName))
					talker.addItem(talkerList.get(i));
			}

			// 更新列表
			while (true) {
				try {
					sleep(1000);
					ChatDateBase.seachActiveTalker(talkerList);
					newsize = talkerList.size();
					if (newsize != oldsize) {
						talker.removeAllItems();
						oldsize = newsize;

						for (int i = 0; i < oldsize; i++) {
							String nameString = talkerList.get(i);
							// 不要把自己加进去
							if (!nameString.equals(userName))
								talker.addItem(talkerList.get(i));
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

	}

	/**
	 * 发送按钮的监听器
	 * 
	 * @author king
	 *
	 */
	private class TFListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			String str = tfTxt.getText().trim();
			tfTxt.setText("");
			String talkGroup = getTalkPerson(); // 记录选择聊天的对象
			try {
				if (!str.equals("")) {
					dos.writeUTF(new Date().toString()+"  "+userName+" say to "+getTalkPerson()+" :");
					dos.writeUTF(talkGroup);
					dos.writeUTF(str);
					dos.writeUTF(talkGroup);
					showPanel.append(new Date().toString()+"  "+userName+" say to "+getTalkPerson()+" :\n");
					showPanel.append(str+"\n");
					dos.flush();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

	private String getTalkPerson() {
		String name = null;
		name = (String) talker.getSelectedItem();
		return name;
	}

	// ------------------------------------聊天系统的聊天部分-----------------------------

	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	private boolean bConnected = false;

	JTextField tfTxt = tf;
	JTextArea taContent = showPanel;

	Thread tRecv = new Thread(new RecvThread());

	/**
	 * 客户端后台服务启动（收发消息）
	 */
	public void launchClient() {
		connect();
		tRecv.start();
	}

	/**
	 * 和服务器端建立连接
	 */
	public void connect() {
		try {
			s = new Socket("127.0.0.1", 8888);
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());

			dos.writeUTF(userName); // 向服务器发送自己的用户名信息。
			System.out.println("connected!");
			bConnected = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 向服务器发送一条消息,服务器转发给所有的人
	 * 
	 * @param str
	 *            消息内容
	 */
	public void send(String str) {
		try {
			dos.writeUTF(str);
			dos.writeUTF("All");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 断开与服务器端的连接
	 */
	public void disconnect() {
		try {
			dos.close();
			dis.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打印服务器发送的信息的线程类
	 * 
	 * @author king
	 *
	 */
	private class RecvThread implements Runnable {

		public void run() {
			try {
				while (bConnected) {
					String str = dis.readUTF();
					if (!str.equals("")) {
						String st1 = taContent.getText();
						taContent.setText(st1 + str + '\n');
					}

				}
			} catch (SocketException e) {
				System.out.println("退出了，bye!");
			} catch (EOFException e) {
				System.out.println("推出了，bye - bye!");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
