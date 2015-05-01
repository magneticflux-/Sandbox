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

import org.uncommons.watchmaker.framework.PopulationData;

import com.electronauts.mathutil.MathUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.sandbox.neural.FeedforwardNetwork;

public class FeedforwardNetworkGame
{
	public static final String	SAVE_FILE	= "SAVE_FILE";

	public static void main(String[] args)
	{
		final Kryo kryo = new Kryo();
		final Arena arena = new Arena(new Rectangle(10, 10, 560, 560), -1);
		final Arena.Fighter player = arena.new Fighter(null, 200, 200, false, arena);
		arena.addFighter(player);

		final JFrame frame = new JFrame("Game");
		final MouseListener mouse = new MouseListener()
		{

			@Override
			public void mouseClicked(MouseEvent e)
			{
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				player.isShooting = true;
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				player.isShooting = false;
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
			}
		};
		frame.addMouseListener(mouse);
		final KeyListener key = new KeyListener()
		{
			public static final double	SPEED	= 4;

			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_W)
				{
					player.yV += SPEED;
				}
				if (e.getKeyCode() == KeyEvent.VK_S)
				{
					player.yV -= SPEED;
				}
				if (e.getKeyCode() == KeyEvent.VK_A)
				{
					player.xV -= SPEED;
				}
				if (e.getKeyCode() == KeyEvent.VK_D)
				{
					player.xV += SPEED;
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
				{
					player.isShooting = true;
				}
				if (e.getKeyCode() == KeyEvent.VK_F)
				{
					MouseInfo.getPointerInfo().getDevice().setFullScreenWindow(frame);
				}
				if (e.getKeyCode() == KeyEvent.VK_Z)
				{
					player.fov += Math.PI / 32;
				}
				if (e.getKeyCode() == KeyEvent.VK_X)
				{
					player.fov -= Math.PI / 32;
				}
				if (e.getKeyCode() == KeyEvent.VK_C)
				{
					player.range += 10;
				}
				if (e.getKeyCode() == KeyEvent.VK_V)
				{
					player.range -= 10;
				}

			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_W)
				{
					player.yV -= SPEED;
				}
				if (e.getKeyCode() == KeyEvent.VK_S)
				{
					player.yV += SPEED;
				}
				if (e.getKeyCode() == KeyEvent.VK_A)
				{
					player.xV += SPEED;
				}
				if (e.getKeyCode() == KeyEvent.VK_D)
				{
					player.xV -= SPEED;
				}
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
				{
					player.isShooting = false;
				}
			}
		};
		frame.addKeyListener(key);
		final ActionListener action = new ActionListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand() == SAVE_FILE)
				{
					if (e.getSource() instanceof PopulationData<?>)
					{
						arena.addFighter(arena.new Fighter(((PopulationData<FeedforwardNetwork>) e.getSource()).getBestCandidate(), 100, 100, true, arena));
					}
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
												Input i = null;
												i = new Input(new FileInputStream(directory));
												action.actionPerformed(new ActionEvent(kryo.readObject(i, PopulationData.class), 0, SAVE_FILE));
												i.close();

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
		final JPanel panel = new JPanel()
		{
			private static final long	serialVersionUID	= 1L;
			public static final double	updateSpeed			= 50 / 3d;

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				final long startTime = System.nanoTime();
				arena.paint(g);
				arena.updatePhysics();

				Point p = MouseInfo.getPointerInfo().getLocation();
				SwingUtilities.convertPointFromScreen(p, this);

				player.angleV = Math.atan2(Math.sin(-MathUtil.angleTo(new Point2D.Double(player.getX(), this.getHeight() - player.getY()), p) - player.angle),
						Math.cos(-MathUtil.angleTo(new Point2D.Double(player.getX(), this.getHeight() - player.getY()), p) - player.angle)) / 2;

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
