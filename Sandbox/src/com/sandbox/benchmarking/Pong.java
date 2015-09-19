package com.sandbox.benchmarking;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Pong {
	public static final int PADDLE_WIDTH = 20;
	public static final int PADDLE_HEIGHT = 100;
	public static final int BALL_SIDE_LENGTH = 15;
	public static final double PADDLE_SPEED = 10;
	public static final double BALL_SPEED = 5;
	public static final double BALL_SPEED_MULTIPLIER = 1.1;
	public static final int GAME_WINDOW_WIDTH = 500;
	public static final int GAME_WINDOW_HEIGHT = 500;

	public static void main(String[] args) {
		final double[] values = new double[10];// Left pos and vel, right pos and vel, ball x, y, xV, yV, left score, right score
		final Random r = new Random(0);

		values[0] = GAME_WINDOW_HEIGHT;// GAME_WINDOW_HEIGHT / 2;
		values[2] = GAME_WINDOW_HEIGHT;// GAME_WINDOW_HEIGHT / 2;

		values[4] = GAME_WINDOW_WIDTH / 2;
		values[5] = GAME_WINDOW_HEIGHT / 2;

		values[6] = r.nextBoolean() ? BALL_SPEED : -BALL_SPEED;
		values[7] = r.nextBoolean() ? BALL_SPEED / 2 : -BALL_SPEED / 2;

		JFrame frame = new JFrame("Pong");
		frame.add(new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				if (values[0] + values[1] - PADDLE_HEIGHT >= 0 && values[0] + values[1] <= this.getHeight()) {
					values[0] += values[1];
				}
				if (values[2] + values[3] - PADDLE_HEIGHT >= 0 && values[2] + values[3] <= this.getHeight()) {
					values[2] += values[3];
				}

				g.fillRect(0, (int) (GAME_WINDOW_HEIGHT - values[0]), PADDLE_WIDTH, PADDLE_HEIGHT);
				g.fillRect(GAME_WINDOW_WIDTH - PADDLE_WIDTH, (int) (GAME_WINDOW_HEIGHT - values[2]), PADDLE_WIDTH, PADDLE_HEIGHT);

				g.fillRect((int) (values[4] - BALL_SIDE_LENGTH / 2), (int) (GAME_WINDOW_HEIGHT - values[5] - BALL_SIDE_LENGTH / 2), BALL_SIDE_LENGTH, BALL_SIDE_LENGTH);

				values[4] += values[6];
				values[5] += values[7];

				if (values[4] < PADDLE_WIDTH && (values[5] > values[0] || values[5] < values[0] - PADDLE_HEIGHT)) {
					values[8]++;
					System.out.println("Point to left. Left: " + values[8] + "pts. Right: " + values[9] + "pts.");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					values[4] = GAME_WINDOW_WIDTH / 2;
					values[5] = GAME_WINDOW_HEIGHT / 2;

					values[6] = r.nextBoolean() ? BALL_SPEED : -BALL_SPEED;
					values[7] = r.nextBoolean() ? BALL_SPEED / 2 : -BALL_SPEED / 2;
				} else if (values[4] < PADDLE_WIDTH) {
					values[6] = -values[6] * BALL_SPEED_MULTIPLIER;
				}

				if (values[4] > this.getWidth() - PADDLE_WIDTH && (values[5] > values[2] || values[5] < values[2] - PADDLE_HEIGHT)) {
					values[9]++;
					System.out.println("Point to right. Left: " + values[8] + "pts. Right: " + values[9] + "pts.");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					values[4] = GAME_WINDOW_WIDTH / 2;
					values[5] = GAME_WINDOW_HEIGHT / 2;

					values[6] = r.nextBoolean() ? BALL_SPEED : -BALL_SPEED;
					values[7] = r.nextBoolean() ? BALL_SPEED / 2 : -BALL_SPEED / 2;
				} else if (values[4] > this.getWidth() - PADDLE_WIDTH) {
					values[6] = -values[6] * BALL_SPEED_MULTIPLIER;
				}

				if (values[5] > GAME_WINDOW_HEIGHT || values[5] < 0) {
					values[7] = -values[7] * BALL_SPEED_MULTIPLIER;
				}

				try {
					Thread.sleep(15);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				repaint();
			}
		});
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_W:
						values[1] += PADDLE_SPEED;
						break;
					case KeyEvent.VK_S:
						values[1] -= PADDLE_SPEED;
						break;
					case KeyEvent.VK_I:
						values[3] += PADDLE_SPEED;
						break;
					case KeyEvent.VK_K:
						values[3] -= PADDLE_SPEED;
						break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_W:
						values[1] -= PADDLE_SPEED;
						break;
					case KeyEvent.VK_S:
						values[1] += PADDLE_SPEED;
						break;
					case KeyEvent.VK_I:
						values[3] -= PADDLE_SPEED;
						break;
					case KeyEvent.VK_K:
						values[3] += PADDLE_SPEED;
						break;
				}
			}
		});
		frame.setSize(GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT + 22);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
