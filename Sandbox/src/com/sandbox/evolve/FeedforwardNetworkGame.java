package com.sandbox.evolve;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

public class FeedforwardNetworkGame
{
	public static final String	SAVE_FILE	= "SAVE_FILE";

	public static void main(String[] args)
	{
		final Kryo kryo = new Kryo();
		final JFrame frame = new JFrame("Game");
		final ActionListener action = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand() == SAVE_FILE)
				{
					System.out.println("SaveFileAction" + e.getSource().getClass());
				}
				else
				{
					throw new IllegalArgumentException("Illegal parameter specified!");
				}
			}
		};
		frame.setJMenuBar(new JMenuBar()
		{
			private static final long	serialVersionUID	= 1L;
			{
				this.add(new JMenu("File")
				{
					private static final long	serialVersionUID	= 1L;
					{
						this.add(new JMenuItem("Open...")
						{
							private static final long	serialVersionUID	= 1L;
							{
								this.addActionListener(new ActionListener()
								{
									File	directory	= new File(System.getProperty("user.home"));

									@Override
									public void actionPerformed(ActionEvent e)
									{
										final JFileChooser open = new JFileChooser(directory);

										final int returnVal = open.showOpenDialog(null);
										if (returnVal == JFileChooser.APPROVE_OPTION)
										{
											directory = open.getSelectedFile();
											try
											{
												System.out.println(kryo.readClass(new Input(new FileInputStream(directory))));
												action.actionPerformed(new ActionEvent(kryo.readObject(new Input(new FileInputStream(directory)), kryo
														.readClass(new Input(new FileInputStream(directory))).getClass()), 0, SAVE_FILE));

											}
											catch (FileNotFoundException e1)
											{
												e1.printStackTrace();
											}
										}
									}
								});
							}
						});
					}
				});
			}
		});

		frame.add(new JPanel()
		{
			private static final long	serialVersionUID	= 1L;
		});

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
