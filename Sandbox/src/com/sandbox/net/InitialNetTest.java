package com.sandbox.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InitialNetTest
{
	public static final int	DEFAULT_PORT	= 2245;

	public static void main(String[] args) throws IOException
	{
		JFrame frame = new JFrame("Network Test");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JButton connectButton = new JButton("Create connection");
		JButton sendButton = new JButton("Send String");
		final JTextField ip = new JTextField();
		JTextField data = new JTextField();

		final Socket[] s = new Socket[1];
		ServerSocket s1 = new ServerSocket(DEFAULT_PORT);

		connectButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					s[0] = new Socket(ip.getText(), DEFAULT_PORT);
					System.out.println("Socket created");
				}
				catch (UnknownHostException e1)
				{
					System.out.println("Invalid IP provided.");
				}
				catch (IOException e1)
				{
					System.out.println("IOException with IP of " + ip.getText());
					e1.printStackTrace();
				}
			}
		});

		panel.add(new JLabel("User IP address: "
				+ NetworkInterface.getNetworkInterfaces().nextElement().getInterfaceAddresses().get(0).getAddress().getHostAddress()));
		panel.add(ip);
		panel.add(connectButton);
		panel.add(data);
		panel.add(sendButton);
		frame.add(panel);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}
}
