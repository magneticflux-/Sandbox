package com.sandbox.gravity;

import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class RunV2
{
	public static void main(String[] args)
	{
		final JFrame frame = new JFrame("Gravity");
		frame.add(new JPanel()
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void paintComponent(Graphics g)
			{
			}
		});
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
