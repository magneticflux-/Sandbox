package com.sandbox.evolve;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.math3.util.FastMath;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import com.grapeshot.halfnes.NES;
import com.sandbox.neural.FeedforwardNetwork;

public class NESFitnessEvaluator implements FitnessEvaluator<FeedforwardNetwork>
{
	public static void main(String[] args)
	{
		NESFitnessEvaluator f = new NESFitnessEvaluator();
		f.getFitness(null, null);
	}

	@Override
	public double getFitness(FeedforwardNetwork candidate, List<? extends FeedforwardNetwork> population)
	{
		final NES nes = new NES();
		nes.loadROM("C:\\Users\\Mitchell\\Desktop\\fceux-2.2.2-win32\\ROMs\\Super Mario Bros..nes");

		try
		{
			Thread t1 = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					// nes.run();
					while (true)
					{
						if (nes.runEmulation)
						{
							// ((GUIImpl) nes.gui).getKeyListeners()[0].keyPressed(new KeyEvent(((GUIImpl) nes.gui), 0, 0, 0, KeyEvent.VK_RIGHT, '>'));
							nes.frameStartTime = System.nanoTime();
							nes.getActionReplay().applyPatches();
							nes.runframe();
							if (nes.isFrameLimiterOn() && !nes.dontSleep)
							{
								// nes.limiter.sleep();
							}
							nes.frameDoneTime = System.nanoTime() - nes.frameStartTime;
							// ((GUIImpl) nes.gui).getKeyListeners()[0].keyReleased(new KeyEvent(((GUIImpl) nes.gui), 0, 0, 0, KeyEvent.VK_RIGHT, '>'));
						}
						else
						{
							// nes.limiter.sleepFixed();
							if (nes.ppu != null && nes.framecount > 1)
							{
								java.awt.EventQueue.invokeLater(nes.render);
							}
						}
					}
				}
			});
			Thread t2 = new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					final int[][] vision = new int[12][12];
					JFrame frame = new JFrame("Vision");
					frame.add(new JPanel()
					{
						@Override
						public void paintComponent(Graphics g)
						{
							for (int y = 0; y < vision.length; y++)
							{
								for (int x = 0; x < vision[y].length; x++)
								{
									synchronized (vision)
									{
										int value = (int) (256 * (vision[y][x] / 256d));
										if (vision[y][x] > 255) System.out.println(vision[y][x]);
										g.setColor(new Color(value, value, value));
										g.fillRect(x * 16, y * 16, 16, 16);
										g.setColor(Color.RED);
										g.drawString("" + vision[y][x], x * 16, y * 16 + g.getFontMetrics().getHeight());
									}
								}
							}
							try
							{
								Thread.sleep(16);
							}
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}
							repaint();
						}
					});
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setSize(300, 300);
					frame.setVisible(true);
					while (true)
					{
						try
						{
							Thread.sleep(100);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						nes.cpuram.write(0x0756, 1);
						int score = 0;
						int time = 0;
						int world = nes.cpuram.read(0x075F);
						int level = nes.cpuram.read(0x0760);
						int lives = nes.cpuram.read(0x075A);
						int marioX = nes.cpuram.read(0x6D) * 0x100 + nes.cpuram.read(0x86);
						int marioY = nes.cpuram.read(0x03B8) + 16;
						for (int i = 0x07DD; i <= 0x07E2; i++)
							score += nes.cpuram._read(i) * FastMath.pow(10, (0x07E2 - i + 1));
						for (int i = 0x07F8; i <= 0x07FA; i++)
							time += nes.cpuram._read(i) * FastMath.pow(10, (0x07FA - i));

						int points = (score / 5) + (time * 10) + (marioX / 4) + (lives * 500) + (level * 250) + (world * 2000);

						System.out.println(String.format("Points: %d, Time: %d, Score: %d, World: %d, Level: %d, Lives: %d, MarioX: %d, MarioY: %d"
								+ (nes.cpuram.read(0x000E) == 0x0B ? ", DYING" : ", STATE: " + nes.cpuram.read(0x000E)), points, time, score, world, level,
								lives, marioX, marioY));

						synchronized (vision)
						{
							for (int dx = -6; dx < 6; dx += 1)
								for (int dy = -6; dy < 6; dy += 1)
								{
									int x = marioX + (dx * 16) + 8;
									int y = marioY + (dy * 16) - 16;
									int page = (int) FastMath.floor(x / 256) % 2;
									int subx = (int) FastMath.floor((x % 256) / 16);
									int suby = (int) FastMath.floor((y - 32) / 16);
									int addr = 0x500 + page * 13 * 16 + suby * 16 + subx;
									if (suby >= 13 || suby < 0)
									{
										// System.out.println("Outside level.");
									}
									else
									{
										// System.out.println("Block data at " + dx + ", " + dy + ": " + nes.cpuram.read(addr));
									}
									vision[dy + 6][dx + 6] = nes.cpuram.read(addr);
								}
						}

					}
				}
			});
			t1.start();
			t2.start();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean isNatural()
	{
		return true;
	}
}
