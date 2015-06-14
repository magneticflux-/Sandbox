package com.sandbox.badthings;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Clicker
{
	public static void main(String[] args) throws AWTException
	{
		JFrame frame = new JFrame();
		final boolean[] click = new boolean[1];
		final Robot robot = new Robot();
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					if (click[0])
					{
						try
						{
							Thread.sleep(5);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						// System.out.println("test");
						robot.mousePress(InputEvent.BUTTON1_MASK);
						robot.mouseRelease(InputEvent.BUTTON1_MASK);
					}
					else
					{
						try
						{
							Thread.sleep(25);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
		frame.add(new JButton("Shutdown")
		{
			private static final long	serialVersionUID	= 1L;
			{
				this.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						System.out.println("Shutdown");
						click[0] = false;
					}
				});
				this.addKeyListener(new KeyListener()
				{
					@Override
					public void keyTyped(KeyEvent e)
					{
						if (e.getKeyChar() == 'a')
						{
							click[0] = !click[0];
							System.out.println("clicked to " + click[0]);
						}
					}

					@Override
					public void keyReleased(KeyEvent e)
					{
					}

					@Override
					public void keyPressed(KeyEvent e)
					{
					}
				});
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(1000, 500);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}
}
