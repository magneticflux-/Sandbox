package com.sandbox.evolve;

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.math3.util.FastMath;
import org.uncommons.watchmaker.framework.PopulationData;

import com.electronauts.mathutil.MathUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkGame
{
	public static final String	SAVE_FILE	= "SAVE_FILE";

	public static void main(final String[] args)
	{
		final Kryo kryo = new Kryo();
		final Arena arena = new Arena(new Rectangle(0, 0, 600, 600), -1);
		final Arena.Fighter player = arena.new Fighter(null, 200, 200, 0, false, false, arena);
		arena.addFighter(player);

		final JFrame frame = new JFrame("Game");
		final MouseListener mouse = new MouseListener()
		{
			@Override
			public void mouseClicked(final MouseEvent e)
			{
			}

			@Override
			public void mouseEntered(final MouseEvent e)
			{
			}

			@Override
			public void mouseExited(final MouseEvent e)
			{
			}

			@Override
			public void mousePressed(final MouseEvent e)
			{
				player.isShooting = true;
			}

			@Override
			public void mouseReleased(final MouseEvent e)
			{
				player.isShooting = false;
			}
		};
		frame.addMouseListener(mouse);
		final KeyListener key = new KeyListener()
		{
			public static final double	SPEED	= 4;

			@Override
			public void keyPressed(final KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_W && player.yV <= 0) player.yV += SPEED;
				if (e.getKeyCode() == KeyEvent.VK_S && player.yV >= 0) player.yV -= SPEED;
				if (e.getKeyCode() == KeyEvent.VK_A && player.xV >= 0) player.xV -= SPEED;
				if (e.getKeyCode() == KeyEvent.VK_D && player.xV <= 0) player.xV += SPEED;
				if (e.getKeyCode() == KeyEvent.VK_SPACE) player.isShooting = true;
				if (e.getKeyCode() == KeyEvent.VK_Z) player.fov += FastMath.PI / 32;
				if (e.getKeyCode() == KeyEvent.VK_X) player.fov -= FastMath.PI / 32;
				if (e.getKeyCode() == KeyEvent.VK_C) player.range += 10;
				if (e.getKeyCode() == KeyEvent.VK_V) player.range -= 10;

			}

			@Override
			public void keyReleased(final KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_W) player.yV -= SPEED;
				if (e.getKeyCode() == KeyEvent.VK_S) player.yV += SPEED;
				if (e.getKeyCode() == KeyEvent.VK_A) player.xV += SPEED;
				if (e.getKeyCode() == KeyEvent.VK_D) player.xV -= SPEED;
				if (e.getKeyCode() == KeyEvent.VK_SPACE) player.isShooting = false;
			}

			@Override
			public void keyTyped(final KeyEvent e)
			{
			}
		};
		frame.addKeyListener(key);
		final ActionListener action = new ActionListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				if (e.getActionCommand() == FeedforwardNetworkGame.SAVE_FILE)
				{
					if (e.getSource() instanceof PopulationData<?>)
						arena.addFighter(arena.new Fighter(((PopulationData<FeedforwardNetwork>) e.getSource()).getBestCandidate(), 100, 100, 0, true, false,
								arena));
				}
				else
					throw new IllegalArgumentException("Illegal parameter specified!");
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
									public void actionPerformed(final ActionEvent e)
									{
										final JFileChooser open = new JFileChooser(this.directory);

										final int returnVal = open.showOpenDialog(null);
										if (returnVal == JFileChooser.APPROVE_OPTION)
										{
											this.directory = open.getSelectedFile();
											try
											{
												Input i = null;
												i = new Input(new FileInputStream(this.directory));
												action.actionPerformed(new ActionEvent(kryo.readClassAndObject(i), 0, FeedforwardNetworkGame.SAVE_FILE));
												i.close();

											}
											catch (final FileNotFoundException e1)
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
		final JPanel panel = new JPanel()
		{
			private static final long	serialVersionUID	= 1L;
			public static final double	updateSpeed			= 50 / 3d;

			@Override
			public void paintComponent(final Graphics g)
			{
				final long startTime = System.nanoTime();
				super.paintComponent(g);
				arena.paint(g);
				arena.updatePhysics();

				final Point p = MouseInfo.getPointerInfo().getLocation();
				SwingUtilities.convertPointFromScreen(p, this);

				player.angleV = FastMath.atan2(
						FastMath.sin(-MathUtil.angleTo(new Point2D.Double(player.getX(), this.getHeight() - player.getY()), p) - player.angle),
						FastMath.cos(-MathUtil.angleTo(new Point2D.Double(player.getX(), this.getHeight() - player.getY()), p) - player.angle)) / 2;

				try
				{
					if (updateSpeed - (System.nanoTime() - startTime) / 1000000 > 0)
						Thread.sleep((long) (updateSpeed - (System.nanoTime() - startTime) / 1000000d));
					else
					{
					}
				}
				catch (final InterruptedException e)
				{
					e.printStackTrace();
				}
				while (updateSpeed - (System.nanoTime() - startTime) / 1000000 > 0)
				{
				}
				this.repaint();
			}
		};
		frame.add(panel);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 700);
		frame.setVisible(true);
	}
}
