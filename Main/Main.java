package Main;

import java.util.Date;

import com.mysql.fabric.xmlrpc.base.Data;

import UI.*;

public class Main {
	
	public static void main(String [] argsStrings){
		new ChatServer();
		new ControlUI();
	}
}
