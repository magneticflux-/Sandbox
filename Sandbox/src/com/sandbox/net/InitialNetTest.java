package com.sandbox.net;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class InitialNetTest
{
	public static final int	DEFAULT_PORT	= 2245;

	public static void main(String[] args) throws IOException
	{
		final ServerSocket server = new ServerSocket(DEFAULT_PORT);
		final Socket[] client = new Socket[1];
		final PrintWriter[] toSend = new PrintWriter[1];

		JFrame frame = new JFrame("Network Test");
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JButton connectButton = new JButton("Create connection");
		JButton sendButton = new JButton("Send String");
		final JTextField ip = new JTextField();
		final JTextField data = new JTextField();
		final JTextArea log = new JTextArea("Textmessage v1.41421 now online.\n\n");
		JScrollPane logScroll = new JScrollPane(log);

		log.setEditable(false);

		connectButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					Socket s = new Socket(Inet4Address.getByName(ip.getText()), DEFAULT_PORT);
					toSend[0] = new PrintWriter(s.getOutputStream(), true);
				}
				catch (UnknownHostException e1)
				{
					e1.printStackTrace();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		});

		sendButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (toSend[0] != null)
				{
					toSend[0].println(data.getText());
					try
					{
						log.setText(log.getText() + '\n' + Inet4Address.getLocalHost().getHostName() + ": " + data.getText());
					}
					catch (UnknownHostException e1)
					{
						e1.printStackTrace();
					}
					log.setCaretPosition(log.getText().length());
				}
			}
		});

		panel.add(new JLabel("User IP address: " + Inet4Address.getLocalHost().getHostAddress()));
		panel.add(ip);
		panel.add(connectButton);
		panel.add(data);
		panel.add(sendButton);
		panel.add(logScroll);
		frame.add(panel);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setVisible(true);

		final PrintWriter[] out = new PrintWriter[1];
		final BufferedReader[] in = new BufferedReader[1];

		Thread t2 = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					System.out.println("Began accepting socket");
					client[0] = server.accept();
					System.out.println("Socket accepted from " + client[0].getInetAddress().getHostName());
					out[0] = new PrintWriter(client[0].getOutputStream(), true);
					in[0] = new BufferedReader(new InputStreamReader(client[0].getInputStream()));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		t2.start();

		Thread t3 = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						while (in[0] == null || !in[0].ready())
						{
							try
							{
								Thread.sleep(10);
							}
							catch (InterruptedException e1)
							{
								e1.printStackTrace();
							}
						}
						log.setText(log.getText() + '\n' + client[0].getInetAddress().getHostName() + ": " + in[0].readLine());
						log.setCaretPosition(log.getText().length());
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		t3.start();
	}
}
