package Server;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.ConnectionPoolDataSource;


public class chatServer implements ActionListener{
	static final int PORT = 7000; 
	Map<String, DataOutputStream> clients;
	Socket socket = null;
	ServerSocket serverSocket = null;
	private Frame frame;
	private Label label;
	private TextArea textArea;
	private Panel panel;
	private Button btnServerOpen, btnServerClose;

	chatServer(){
		clients = Collections.synchronizedMap(new HashMap<String, DataOutputStream>());
		frame = new Frame("Server");
		label = new Label("서버를 연결해 주세요");
		btnServerOpen = new Button("연결");
		btnServerClose = new Button("종료");
		textArea = new TextArea(15,40);
		panel = new Panel();

		panel.setBackground(Color.LIGHT_GRAY);
		panel.add(label);
		panel.add(btnServerOpen);
		panel.add(btnServerClose);
		frame.add(BorderLayout.NORTH,panel);
		frame.add(BorderLayout.CENTER,textArea);
		frame.setVisible(true);
		frame.pack();
		
		btnServerOpen.addActionListener(this);
		btnServerClose.addActionListener(this);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == btnServerOpen) {
			System.out.println("시작");
			start();

		}
		else if(obj == btnServerClose) {
			System.out.println("종료");
			System.exit(0);
		}
	}
	
	public void start() {
		
		try {
			serverSocket = new ServerSocket(PORT);
			textArea.append("서버가 시작되었습니다.\n");
			Thread startThread = new StartThread();
			startThread.start();	

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}


	void send(String message) {
		Iterator<String> iterator = clients.keySet().iterator();

		while(iterator.hasNext()) {
			try {
				String id = iterator.next();
				DataOutputStream output = clients.get(id);
				output.writeUTF(message);
			} catch(Exception e){

			}
		}
	}

	public static void main(String[] args) {
		new chatServer();
	}


	class StartThread extends Thread{
		public void run() {
			boolean a = true;
			while(a) {
				try {
					socket = serverSocket.accept();
				} catch (Exception e) {
					e.printStackTrace();
				}
				textArea.append("[ "+socket.getInetAddress()+" : "+socket.getPort()+" ] "+"에서 접속하셨습니다.\n");
				Receiver thread = new Receiver(socket);
				thread.start();			
			}

		}
	}


	class Receiver extends Thread{
		Socket socket;
		DataOutputStream output;
		DataInputStream input;


		public Receiver(Socket socket) {
			this.socket = socket;
			try {
				output = new DataOutputStream(socket.getOutputStream());
				input = new DataInputStream(socket.getInputStream());
			} catch (Exception e) {

			}

		}
		public void run() {
			String id = "";
			try {
				id = input.readUTF();

				send(id+" 님이 입장 하셨습니다.");
				textArea.append("현재 서버 접속자의 수는 "+(clients.size()+1) +"명 입니다.\n");
				clients.put(id, output);

				while(input != null) {
					send(input.readUTF());
				}

			} catch (Exception e) {

			}finally {
				send(id +" 님이 퇴장하셨습니다.");

				clients.remove(id);
				textArea.append("[ "+socket.getInetAddress()+" : "+socket.getPort()+" ] "+"에서 접속을 종료하였습니다.\n");
				textArea.append("현재 서버 접속자의 수는 "+clients.size() +"명 입니다.\n");

			}
		}


	}


}
