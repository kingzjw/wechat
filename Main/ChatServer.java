package Main;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ChatServer  {
	boolean started = false;
	ServerSocket ss = null;

	public ChatServer() {
		new myFrame();
	}

	List<Client> clients = new ArrayList<Client>();

	/**
	 * 启动服务器，开始接受连接，并调用线程
	 */
	public void start() {
		try {
			ss = new ServerSocket(8888);
			started = true;
		} catch (BindException e) {
			System.out.println("端口使用中....");
			System.out.println("请关掉相关程序并重新运行服务器！");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			while (started) {
				Socket s = ss.accept();

				// 一个客户端连接后，启动线程
				Client c = new Client(s);
				new Thread(c).start();
				clients.add(c);
				// dis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 处理客户端的线程
	 * 
	 * @author king
	 *
	 */
	private class Client implements Runnable {
		private String userName = null;
		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean bConnected = false;
		private boolean first = true;  //控制接受名字

		public Client(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 向当前客户端发送消息
		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("对方退出了！我从List里面去掉了！");
				// e.printStackTrace();
			}
		}

		/**
		 * 接受该客户端的用户名
		 * @return用户名
		 */
		public String getUserName() {
			String str = null;
			try {
				str = dis.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(str+"连接上了服务器");
			return str;
		}

		// 当收到信息后，向所有客户端发送消息
		public void run() {
			String talkPerson  = null;  //记录选择聊天的对象
			try {
				while (bConnected) {
					if(first){
						userName = getUserName();
						first = false;
					}
					String str = dis.readUTF();
					talkPerson = dis.readUTF();

					//判断是否私聊还是群发
					if(talkPerson.equals("All")){
						for (int i = 0; i < clients.size(); i++) {
							Client c = clients.get(i);
							c.send(str);
						}
					}else{
						for (int i = 0; i < clients.size(); i++) {
							Client c = clients.get(i);
							if(c.userName.equals(talkPerson)){
								c.send(str);								
							}
						}
					}
					

				}
			} catch (EOFException e) {
				System.out.println("Client closed!");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (dis != null)
						dis.close();
					if (dos != null)
						dos.close();
					if (s != null) {
						s.close();
						// s = null;
					}

				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		}

	}

	/**
	 * 控制UI界面
	 * 
	 * @author king
	 *
	 */
	static private class myFrame extends JFrame {

		JButton j1 = new JButton("启动服务器");
		JButton j2 = new JButton("停止服务器");

		public myFrame() {
			launchFrame1();
			func();
		}

		private void func() {
			j1.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					new myThread().start();
				}
			});

			j2.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					System.exit(0);
				}
			});
		}

		void launchFrame1() {
			setLocation(100, 100);
			setVisible(true);
			setLayout(new FlowLayout());
			add(j1);
			add(j2);
			pack();
		}

	}

	/**
	 * 
	 */
	static private class myThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			new ChatServer().start();
		}

	}
}
