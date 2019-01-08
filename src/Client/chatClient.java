package Client;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class chatClient implements ActionListener{

	private String id;
	private Frame frame;
	private Panel panel, idPanel;
	private Label idLabel, label;
	private TextField textField, idTextField;
	private TextArea textArea;
	private Button btnSend, btnConnect;
	Sender sender;
	private int PORT = 7000;

	private Socket socket;

	public chatClient(String id) {
		this.id = id;
		frame = new Frame("Client");
		panel = new Panel();
		label = new Label("Client");
		btnSend = new Button("send");
		textField = new TextField();

		idPanel = new Panel();
		idLabel = new Label("name");
		idTextField = new TextField();
		btnConnect = new Button("connect");

		textArea = new TextArea(15,40);

	}
	public String getId() {
		return idTextField.getText();
	}

	public void show() {

		btnSend.setBackground(Color.GRAY);
		btnSend.setForeground(Color.WHITE);
		btnSend.addActionListener(this);


		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		textField.setColumns(30);
		textField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				char keyCode = e.getKeyChar();
				if(keyCode == KeyEvent.VK_ENTER) {
					actionPerformed(null);
				}
			}
		});

		idTextField.setColumns(30);

		idPanel.setBackground(Color.LIGHT_GRAY);
		idPanel.add(idLabel);
		idPanel.add(idTextField);
		idPanel.add(btnConnect);
		frame.add(BorderLayout.NORTH, idPanel);


		panel.setBackground(Color.LIGHT_GRAY);
		panel.add(label);
		panel.add(textField);
		panel.add(btnSend);
		frame.add(BorderLayout.SOUTH,panel);

		frame.add(BorderLayout.CENTER, textArea);

		frame.setVisible(true);
		frame.pack();

		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String ip = "127.0.0.1";
					socket = new Socket(ip, PORT);
					System.out.println("������ ����Ǿ����ϴ�.");

					sender = new Sender(socket);

					Thread receiver = new Thread(new Receiver(socket));
					receiver.start();
				} catch (Exception e2) {
					e2.printStackTrace();
					System.out.println("������ ������ ���� �ʽ��ϴ�.");
					System.exit(0);
				} 

			}
		});



	}

	@Override 
	public void actionPerformed(ActionEvent e) {


		sender.send(textField.getText()); 
		textField.setText(""); 
		textField.requestFocus(); 
	} 

	public void setMessage(String message) {
		textArea.append(message);
		textArea.append("\n");
	}

	class Sender{
		Socket socket;
		DataOutputStream output;

		public Sender(Socket socket) {
			this.socket = socket;
			try {
				this.output = new DataOutputStream(socket.getOutputStream());

				if(output != null) {
					output.writeUTF(getId());
				}
			} catch (Exception e) {}
		}
		public void send(String message) {
			if(output != null) {
				if(message.equals("quit")) {
					System.exit(0);
				}
				else {
					try {
						output.writeUTF("[" + getId() + "] " + message);
					} catch (Exception e) {}
				}
			}

		}
	}

	class Receiver implements Runnable{
		Socket socket;
		DataInputStream input;

		public Receiver(Socket socket) {
			this.socket = socket;
			try {
				this.input = new DataInputStream(socket.getInputStream());
			} catch (Exception e) {}
		}
		public void run() {
			while(input != null) {
				try {
					chatClient.this.setMessage(input.readUTF());
				} catch (Exception e) {}
			}
		}
	}

	public static void main(String[] args) {

		String id = null;		
		new chatClient(new chatClient(id).getId()).show();


	}

}
