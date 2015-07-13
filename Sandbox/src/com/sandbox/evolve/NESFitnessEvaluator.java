package com.sandbox.evolve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.util.FastMath;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.PopulationData;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.grapeshot.halfnes.NES;
import com.grapeshot.halfnes.ui.ControllerImpl;
import com.grapeshot.halfnes.ui.GUIImpl;
import com.sandbox.neural.FeedforwardNetwork;

public class NESFitnessEvaluator implements FitnessEvaluator<FeedforwardNetwork>
{
	public static void main1(String[] args)
	{
		Kryo kryo = new Kryo();
		Input in = null;
		try
		{
			in = new Input(new FileInputStream("codex/Mario/generation_1776.pop"));
		}
		catch (final FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		FeedforwardNetwork candidate = ((PopulationData<FeedforwardNetwork>) kryo.readClassAndObject(in)).getBestCandidate();
		in.close();
		NES nes = new NES(false);
		nes.loadROM("C:\\Users\\Mitchell\\Desktop\\fceux-2.2.2-win32\\ROMs\\Super Mario Bros..nes");
		nes.reset();
		// NESFitnessEvaluator.loadSavestate(nes);

		final GUIImpl gui = ((GUIImpl) nes.getGUI());
		final KeyListener input = gui.getKeyListeners()[0];

		final KeyEvent U = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_UP, '^');
		final KeyEvent D = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_DOWN, 'v');
		final KeyEvent L = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_LEFT, '<');
		final KeyEvent R = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_RIGHT, '>');

		final KeyEvent A = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_X, 'A');
		final KeyEvent B = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_Z, 'B');
		final KeyEvent SELECT = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_SHIFT, 'E');
		final KeyEvent START = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_ENTER, 'T');

		for (int i = 0; i < 31; i++)
			// Exact frame number until it can begin.
			nes.frameAdvance();

		input.keyPressed(START);
		nes.frameAdvance();
		input.keyReleased(START);

		for (int i = 0; i < 162; i++)
			// Exact frame number until Mario gains control
			nes.frameAdvance();

		int fitness = 0;
		int maxDistance = 0;
		int timeout = 0;

		while (true)
		{
			input.keyReleased(U);
			input.keyReleased(D);
			input.keyReleased(L);
			input.keyReleased(R);
			input.keyReleased(A);
			input.keyReleased(B);
			input.keyReleased(SELECT);
			input.keyReleased(START);

			int score = 0;
			int time = 0;
			byte world = (byte) nes.getCPURAM().read(0x075F);
			byte level = (byte) nes.getCPURAM().read(0x0760);
			byte lives = (byte) (nes.getCPURAM().read(0x075A) + 1);
			int marioX = nes.getCPURAM().read(0x6D) * 0x100 + nes.getCPURAM().read(0x86);
			int marioY = nes.getCPURAM().read(0x03B8) + 16;
			int marioState = nes.getCPURAM().read(0x000E);
			for (int i = 0x07DD; i <= 0x07E2; i++)
				score += nes.getCPURAM()._read(i) * FastMath.pow(10, (0x07E2 - i + 1));
			for (int i = 0x07F8; i <= 0x07FA; i++)
				time += nes.getCPURAM()._read(i) * FastMath.pow(10, (0x07FA - i));

			int points = (score / 5) + (time * 10) + (marioX / 4) + (lives * 500) + (level * 250) + (world * 2000);

			timeout++;
			if (marioX > maxDistance)
			{
				maxDistance = marioX;
				timeout = 0;
			}
			// System.out.println("Lives: " + lives + " Timeout: " + timeout + " Distance: " + marioX);
			if (lives <= 2 || timeout > 240 || marioState == 0x0B)
			{
				fitness = points;
				break;
			}

			final int[][] vision = new int[10][10];

			for (int dx = -vision[0].length / 2; dx < vision[0].length / 2; dx += 1)
				for (int dy = -vision.length / 2; dy < vision.length / 2; dy += 1)
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
						vision[dy + (vision.length / 2)][dx + (vision[0].length / 2)] = 0;
					}
					else
					{
						// System.out.println("Block data at " + dx + ", " + dy + ": " + nes.cpuram.read(addr));
						vision[dy + (vision.length / 2)][dx + (vision[0].length / 2)] = nes.getCPURAM().read(addr);
					}
				}

			for (int i = 0; i <= 4; i++)
			{
				int enemy = nes.getCPURAM().read(0xF + i);
				if (enemy != 0)
				{
					int ex = nes.getCPURAM().read(0x6E + i) * 0x100 + nes.getCPURAM().read(0x87 + i);
					int ey = nes.getCPURAM().read(0xCF + i) + 24;
					int enemyMarioDeltaX = (ex - marioX) / 16;
					int enemyMarioDeltaY = (ey - marioY) / 16;
					try
					{
						vision[enemyMarioDeltaY + (vision.length / 2)][enemyMarioDeltaX + (vision[0].length / 2)] = -enemy;
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
					}
				}
			}

			int[] visionunwound = NESFitnessEvaluator.unwind2DArray(vision);
			double[] reactions = candidate.evaluate(visionunwound);

			if (reactions[0] > 0) input.keyPressed(U);
			if (reactions[1] > 0) input.keyPressed(D);
			if (reactions[2] > 0) input.keyPressed(L);
			if (reactions[3] > 0) input.keyPressed(R);
			if (reactions[4] > 0) input.keyPressed(A);
			if (reactions[5] > 0) input.keyPressed(B);
			// if (reactions[6] > 0) input.keyPressed(SELECT);
			// if (reactions[7] > 0) input.keyPressed(START);

			System.out.println("Points: " + points + "Timeout: " + timeout);
			nes.frameAdvance();
			try
			{
				Thread.sleep(8);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args)
	{
		long startTime = System.nanoTime();
		final NES nes = new NES(false);
		long endTime = System.nanoTime();
		System.out.println(endTime - startTime + "ns to initialize NES");
		nes.loadROM("C:\\Users\\Mitchell\\Desktop\\fceux-2.2.2-win32\\ROMs\\Super Mario Bros..nes");
		boolean logging = true;

		final GUIImpl gui = ((GUIImpl) nes.getGUI());
		System.out.println("Getting listeners");
		final ControllerImpl input = (ControllerImpl) gui.getKeyListeners()[0];
		System.out.println("Got listener " + input);

		final KeyEvent U = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_UP, '^');
		final KeyEvent D = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_DOWN, 'v');
		final KeyEvent L = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_LEFT, '<');
		final KeyEvent R = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_RIGHT, '>');

		final KeyEvent A = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_X, 'A');
		final KeyEvent B = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_Z, 'B');
		final KeyEvent SELECT = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_SHIFT, 'E');
		final KeyEvent START = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_ENTER, 'T');

		for (int i = 0; i < 31; i++)
			nes.frameAdvance();

		input.keyPressed(START);
		nes.frameAdvance();
		input.keyReleased(START);

		for (int i = 0; i < 162; i++)
			// Exact frame number until Mario gains control
			nes.frameAdvance();

		nes.runEmulation = true;
		// nes.run();

		Thread nesUpdateThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						Thread.sleep(5);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					long startTime = System.nanoTime();
					nes.frameAdvance();
					long endTime = System.nanoTime();
					// System.out.println("ns taken to compute entire frame: " + (endTime - startTime));
					/*
					 * if (nes.runEmulation) { ((GUIImpl) nes.gui).getKeyListeners()[0].keyPressed(new KeyEvent(((GUIImpl) nes.gui), 0, 0, 0, KeyEvent.VK_RIGHT,
					 * '>')); nes.frameStartTime = System.nanoTime(); nes.getActionReplay().applyPatches(); nes.runframe(); if (nes.isFrameLimiterOn() &&
					 * !nes.dontSleep) { nes.limiter.sleep(); } nes.frameDoneTime = System.nanoTime() - nes.frameStartTime; ((GUIImpl)
					 * nes.gui).getKeyListeners()[0].keyReleased(new KeyEvent(((GUIImpl) nes.gui), 0, 0, 0, KeyEvent.VK_RIGHT, '>')); } else {
					 * nes.limiter.sleepFixed(); if (nes.ppu != null && nes.framecount > 1) { EventQueue.invokeLater(nes.render); } }
					 */

				}
			}
		});

		Thread interfaceThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final int[][] vision = new int[13][13];
				final int viewSizeMultiplier = 2;
				JFrame frame = new JFrame("Vision");
				frame.add(new JPanel()
				{
					private static final long	serialVersionUID	= 1L;

					@Override
					public void paintComponent(Graphics g)
					{
						super.paintComponent(g);
						g.setFont(g.getFont().deriveFont(16.0f));
						for (int y = 0; y < vision.length; y++)
						{
							for (int x = 0; x < vision[y].length; x++)
							{
								synchronized (vision)
								{
									int value = (vision[y][x] + 256) % 256;
									g.setColor(new Color(255 - value, 255 - value, 255 - value));
									g.fillRect((x * 16) * viewSizeMultiplier, (y * 16) * viewSizeMultiplier, (16) * viewSizeMultiplier,
											(16) * viewSizeMultiplier);
									g.setColor(Color.BLACK);
									g.drawRect((x * 16) * viewSizeMultiplier, (y * 16) * viewSizeMultiplier, (16) * viewSizeMultiplier,
											(16) * viewSizeMultiplier);
									g.setColor(Color.RED);
									g.drawString("" + vision[y][x], (x * 16) * viewSizeMultiplier, (y * 16) * viewSizeMultiplier
											+ g.getFontMetrics().getHeight());
								}
							}
						}
						g.setColor(Color.RED);
						g.fillRect(((vision[0].length / 2) * 16 + 8 - 4) * viewSizeMultiplier, ((vision.length / 2) * 16 + 8 - 4) * viewSizeMultiplier,
								(8) * viewSizeMultiplier, (16 + 8) * viewSizeMultiplier);
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
				frame.setSize(600, 600);
				frame.setVisible(true);
				while (true)
				{
					try
					{
						Thread.sleep(16);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					int score = 0;
					int time = 0;
					int world = nes.getCPURAM().read(0x075F);
					int level = nes.getCPURAM().read(0x0760);
					int lives = nes.getCPURAM().read(0x075A);
					int marioX = nes.getCPURAM().read(0x6D) * 0x100 + nes.getCPURAM().read(0x86);
					int marioY = nes.getCPURAM().read(0x03B8) + 16;
					for (int i = 0x07DD; i <= 0x07E2; i++)
						score += nes.getCPURAM()._read(i) * FastMath.pow(10, (0x07E2 - i + 1));
					for (int i = 0x07F8; i <= 0x07FA; i++)
						time += nes.getCPURAM()._read(i) * FastMath.pow(10, (0x07FA - i));

					int points = ((time - 400) * 10) + (marioX) + (level * 250) + (world * 2000);

					if (logging)
						System.out.println("Timeout: " + (30 + (marioX / 250)) + "Points: " + points + ", Time: " + time + " Score: " + score + ", World: "
								+ world + "d, Level: " + level + ", Lives: " + lives + ", MarioX: " + marioX + ", MarioY: " + marioY
								+ (nes.getCPURAM().read(0x000E) == 0x0B ? ", DYING" : ", STATE: " + nes.getCPURAM().read(0x000E)));

					synchronized (vision)
					{
						for (int dx = -vision[0].length / 2; dx < vision[0].length / 2; dx += 1)
							for (int dy = -vision.length / 2; dy < vision.length / 2; dy += 1)
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
									vision[dy + (vision.length / 2)][dx + (vision[0].length / 2)] = 0;
								}
								else
								{
									// System.out.println("Block data at " + dx + ", " + dy + ": " + nes.cpuram.read(addr));
									vision[dy + (vision.length / 2)][dx + (vision[0].length / 2)] = nes.getCPURAM().read(addr);
								}
							}
					}
					for (int i = 0; i <= 4; i++)
					{
						int enemy = nes.getCPURAM().read(0xF + i);
						if (enemy != 0)
						{
							int ex = nes.getCPURAM().read(0x6E + i) * 0x100 + nes.getCPURAM().read(0x87 + i);
							int ey = nes.getCPURAM().read(0xCF + i) + 24;
							int enemyMarioDeltaX = (ex - marioX) / 16;
							int enemyMarioDeltaY = (ey - marioY) / 16;
							try
							{
								vision[enemyMarioDeltaY + (vision.length / 2)][enemyMarioDeltaX + (vision[0].length / 2)] = -enemy;
							}
							catch (ArrayIndexOutOfBoundsException e)
							{
							}
							if (logging)
								System.out.println("Enemy of type " + enemy + " in slot " + i + " @ (" + enemyMarioDeltaX + ", " + enemyMarioDeltaY + ")");
						}
					}
				}
			}
		});
		nesUpdateThread.start();
		interfaceThread.start();
	}

	private static ThreadLocal<Kryo>	kryo	= new ThreadLocal<Kryo>();

	public static void loadSavestate(NES nes)
	{
		if (kryo.get() == null)
		{
			kryo.set(new Kryo());
		}
		Kryo kryo = NESFitnessEvaluator.kryo.get();

		nes.pause();
		// System.out.println("Started read.");
		Input input = null;
		try
		{
			input = new Input(new FileInputStream("savestate.clm"));
		}
		catch (final FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		ImmutablePair<int[], int[]> savestate = (ImmutablePair<int[], int[]>) kryo.readClassAndObject(input);
		// System.arraycopy(savestate.left, 0, nes.cpuram.wram, 0, nes.cpuram.wram.length);
		// System.arraycopy(savestate.right, 0, nes.ppu.bitmap, 0, nes.ppu.bitmap.length);
		input.close();
		// System.out.println("Finished read.");
		nes.resume();
	}

	public static int[] unwind2DArray(int[][] arr)
	{
		int[] out = new int[arr.length * arr[0].length];
		int i = 0;
		for (int x = 0; x < arr[0].length; x++)
		{
			for (int y = 0; y < arr.length; y++)
			{
				out[i] = arr[y][x];
				i++;
			}
		}
		return out;
	}

	private ThreadLocal<NES>	nes	= new ThreadLocal<NES>();

	@Override
	public double getFitness(FeedforwardNetwork candidate, List<? extends FeedforwardNetwork> population)
	{
		if (nes.get() == null)
		{
			nes.set(new NES(true));
			nes.get().loadROM("C:\\Users\\Mitchell\\Desktop\\fceux-2.2.2-win32\\ROMs\\Super Mario Bros..nes");
		}
		NES nes = this.nes.get();
		nes.reset();
		// NESFitnessEvaluator.loadSavestate(nes);

		final GUIImpl gui = ((GUIImpl) nes.getGUI());
		final KeyListener input = gui.getKeyListeners()[0];

		final KeyEvent U = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_UP, '^');
		final KeyEvent D = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_DOWN, 'v');
		final KeyEvent L = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_LEFT, '<');
		final KeyEvent R = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_RIGHT, '>');

		final KeyEvent A = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_X, 'A');
		final KeyEvent B = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_Z, 'B');
		final KeyEvent SELECT = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_SHIFT, 'E');
		final KeyEvent START = new KeyEvent(gui, 0, 0, 0, KeyEvent.VK_ENTER, 'T');

		for (int i = 0; i < 31; i++)
			// Exact frame number until it can begin.
			nes.frameAdvance();

		input.keyPressed(START);
		nes.frameAdvance();
		input.keyReleased(START);

		int fitness = 0;
		int maxDistance = 0;
		int timeout = 0;

		while (true)
		{
			input.keyReleased(U);
			input.keyReleased(D);
			input.keyReleased(L);
			input.keyReleased(R);
			input.keyReleased(A);
			input.keyReleased(B);
			input.keyReleased(SELECT);
			input.keyReleased(START);

			int score = 0;
			int time = 0;
			byte world = (byte) nes.getCPURAM().read(0x075F);
			byte level = (byte) nes.getCPURAM().read(0x0760);
			byte lives = (byte) (nes.getCPURAM().read(0x075A) + 1);
			int marioX = nes.getCPURAM().read(0x6D) * 0x100 + nes.getCPURAM().read(0x86);
			int marioY = nes.getCPURAM().read(0x03B8) + 16;
			int marioState = nes.getCPURAM().read(0x000E);
			for (int i = 0x07DD; i <= 0x07E2; i++)
				score += nes.getCPURAM()._read(i) * FastMath.pow(10, (0x07E2 - i + 1));
			for (int i = 0x07F8; i <= 0x07FA; i++)
				time += nes.getCPURAM()._read(i) * FastMath.pow(10, (0x07FA - i));

			int points = (score / 5) + (time * 10) + (marioX / 4) + (lives * 500) + (level * 250) + (world * 2000);

			timeout++;
			if (marioX > maxDistance)
			{
				maxDistance = marioX;
				timeout = 0;
			}
			// System.out.println("Lives: " + lives + " Timeout: " + timeout + " Distance: " + marioX);
			if (lives <= 2 || timeout > 240 || marioState == 0x0B)
			{
				fitness = points;
				break;
			}

			final int[][] vision = new int[10][10];

			for (int dx = -vision[0].length / 2; dx < vision[0].length / 2; dx += 1)
				for (int dy = -vision.length / 2; dy < vision.length / 2; dy += 1)
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
						vision[dy + (vision.length / 2)][dx + (vision[0].length / 2)] = 0;
					}
					else
					{
						// System.out.println("Block data at " + dx + ", " + dy + ": " + nes.cpuram.read(addr));
						vision[dy + (vision.length / 2)][dx + (vision[0].length / 2)] = nes.getCPURAM().read(addr);
					}
				}

			for (int i = 0; i <= 4; i++)
			{
				int enemy = nes.getCPURAM().read(0xF + i);
				if (enemy != 0)
				{
					int ex = nes.getCPURAM().read(0x6E + i) * 0x100 + nes.getCPURAM().read(0x87 + i);
					int ey = nes.getCPURAM().read(0xCF + i) + 24;
					int enemyMarioDeltaX = (ex - marioX) / 16;
					int enemyMarioDeltaY = (ey - marioY) / 16;
					try
					{
						vision[enemyMarioDeltaY + (vision.length / 2)][enemyMarioDeltaX + (vision[0].length / 2)] = -enemy;
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
					}
				}
			}

			int[] visionunwound = NESFitnessEvaluator.unwind2DArray(vision);
			double[] reactions = candidate.evaluate(visionunwound);

			if (reactions[0] > 0) input.keyPressed(U);
			if (reactions[1] > 0) input.keyPressed(D);
			if (reactions[2] > 0) input.keyPressed(L);
			if (reactions[3] > 0) input.keyPressed(R);
			if (reactions[4] > 0) input.keyPressed(A);
			if (reactions[5] > 0) input.keyPressed(B);
			if (reactions[6] > 0) input.keyPressed(SELECT);
			if (reactions[7] > 0) input.keyPressed(START);

			nes.frameAdvance();
		}
		fitness -= 5400; // The approximate minimum
		return fitness >= 0 ? fitness : 0;
	}

	@Override
	public boolean isNatural()
	{
		return true;
	}
}
