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
	static List<String> talkerList = new ArrayList<String>(); // �������Ķ��������
	static int talkNum = 0;

	// -------------------����ϵͳ��UI����-------------------------------------

	int width = 600;
	int height = 400;
	int lctnX = 200;
	int lctnY = 300;

	Panel p = new Panel();

	JButton send = new JButton("����");
	JButton quit = new JButton("�˳�");

	JTextArea showPanel = new JTextArea(); // ��ʾ��
	JTextField tf = new JTextField(); // �����
	JScrollPane showSP = new JScrollPane(showPanel);

	JLabel userLabel = new JLabel("�û���:");// ��ʾ��
	JLabel userTF = new JLabel(); // �����

	JLabel stateLabel = new JLabel("����״̬:");// ��ʾ��
	String[] stateString = { "����", "�뿪", "æ��", "�������" };
	JComboBox<String> stateList = new JComboBox<String>(stateString); // �����

	JLabel talkLabel = new JLabel("�������:");// ��ʾ��
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
		showInfo(); // ���������Ϣ����
	}

	/**
	 * ������������
	 */
	private void lauchFrame() {
		setLocation(lctnX, lctnY);
		setTitle(userName);
		setSize(width, height);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int quit = JOptionPane.showConfirmDialog(null, "�Ƿ��˳�");
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
	 * UI�����
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
	 * ���Ʋ˵�
	 */
	// private void showMenu() {
	// JMenuBar jMenuBar = new JMenuBar();
	// jMenuBar.setToolTipText("����");
	// JMenu j1 = new JMenu("����");
	// JMenu j2 = new JMenu("�Զ�����");
	//
	// JMenuItem item1 = new JMenuItem("��ɫ1");
	// JMenuItem item2 = new JMenuItem("��ɫ2");
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
	 * ��ť�Ĵ�����
	 */
	private void func() {

		// ����
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				send.addActionListener(new TFListener());
			}
		});

		// �û��˳�ϵͳ�����ı�ͼ��ݵ�״̬
		quit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int quit = JOptionPane.showConfirmDialog(null, "�Ƿ��˳�");
				if (quit == JOptionPane.OK_OPTION) {
					quitUserInLb(userName);
					setVisible(false);
				}
			}
		});
	}

	void showInfo() {

		showPanel.append("��ӭ" + userName + "����������\n");

		send(userName + "  ����!\n");

		showPanel.append("�ף����������ҵĵ�" + talkNum + "���û�Ŷ\n");
	}

	/**
	 * ���ߵ�ʱ���޸��û�������״̬
	 * 
	 * @param name
	 *            �û���
	 */
	private void quitUserInLb(String name) {
		talkNum--;
		ControlUI.changeState(name, 1);
	}

	/**
	 * �Զ��������ߵ���
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
				// ��Ҫ���Լ��ӽ�ȥ
				if (!nameString.equals(userName))
					talker.addItem(talkerList.get(i));
			}

			// �����б�
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
							// ��Ҫ���Լ��ӽ�ȥ
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
	 * ���Ͱ�ť�ļ�����
	 * 
	 * @author king
	 *
	 */
	private class TFListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			String str = tfTxt.getText().trim();
			tfTxt.setText("");
			String talkGroup = getTalkPerson(); // ��¼ѡ������Ķ���
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

	// ------------------------------------����ϵͳ�����첿��-----------------------------

	Socket s = null;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	private boolean bConnected = false;

	JTextField tfTxt = tf;
	JTextArea taContent = showPanel;

	Thread tRecv = new Thread(new RecvThread());

	/**
	 * �ͻ��˺�̨�����������շ���Ϣ��
	 */
	public void launchClient() {
		connect();
		tRecv.start();
	}

	/**
	 * �ͷ������˽�������
	 */
	public void connect() {
		try {
			s = new Socket("127.0.0.1", 8888);
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());

			dos.writeUTF(userName); // ������������Լ����û�����Ϣ��
			System.out.println("connected!");
			bConnected = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * �����������һ����Ϣ,������ת�������е���
	 * 
	 * @param str
	 *            ��Ϣ����
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
	 * �Ͽ���������˵�����
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
	 * ��ӡ���������͵���Ϣ���߳���
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
				System.out.println("�˳��ˣ�bye!");
			} catch (EOFException e) {
				System.out.println("�Ƴ��ˣ�bye - bye!");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
