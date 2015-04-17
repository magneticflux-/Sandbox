package com.sandbox.evolve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.electronauts.mathutil.PolarPoint;
import com.sandbox.neural.FeedforwardNetwork;

public class Arena
{
	public class Fighter extends Projectile
	{
		private static final double			radius		= 20;
		private double						angle;
		private double						angleV;
		private final FeedforwardNetwork	brain;
		private boolean						isShooting	= false;
		private int							shootDelay	= 0;
		private final Color					team		= new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
		private final Arena					arena;

		public Fighter(final FeedforwardNetwork brain, final double x, final double y, Arena arena)
		{
			this(brain, x, y, 0, arena);
		}

		public Fighter(final FeedforwardNetwork brain, final double x, final double y, final double angle, Arena arena)
		{
			super(x, y);

			if (!(Integer.parseInt(brain.getLayout().substring(0, 1)) >= 0 && Integer.parseInt(brain.getLayout().substring(brain.getLayout().length() - 1,
					brain.getLayout().length())) >= 0)) throw new IllegalArgumentException("Insufficient brain in/out space.");

			this.angle = angle;
			this.brain = brain;
			this.arena = arena;
		}

		@Override
		public Rectangle getBoundingBox()
		{
			return new Rectangle((int) (this.x - Fighter.radius), (int) (this.y - Fighter.radius), (int) (2 * Fighter.radius), (int) (2 * Fighter.radius));
		}

		@Override
		public void paint(final Graphics g)
		{
			if (this.isShooting)
			{
				g.setColor(Color.RED);
				g.fillOval((int) (this.x - Fighter.radius - 2), (int) (this.y - Fighter.radius - 2), (int) (2 * Fighter.radius + 4),
						(int) (2 * Fighter.radius + 4));
			}

			g.setColor(this.team);
			g.fillOval((int) (this.x - Fighter.radius), (int) (this.y - Fighter.radius), (int) (2 * Fighter.radius), (int) (2 * Fighter.radius));

			final PolarPoint p = new PolarPoint(50, this.angle);
			g.setColor(Color.RED);
			g.drawLine((int) this.x, (int) this.y, (int) (this.x + p.getX()), (int) (this.y + p.getY()));
		}

		public void reactTo(final double[] enviroment)
		{
			final double[] reaction = this.brain.evaluate(enviroment);
			this.xV = reaction[0] * 5;
			this.yV = reaction[1] * 5;
			this.isShooting = reaction[2] > 0;
		}

		@Override
		public void updatePosition()
		{
			super.updatePosition();
			this.angle += this.angleV;

			if (this.shootDelay > 0)
				this.shootDelay--;
			else if (this.isShooting)
			{
				this.shootDelay = 10;
				PolarPoint p1 = new PolarPoint(Fighter.radius + 10, this.angle);
				PolarPoint p2 = new PolarPoint(30, this.angle);
				arena.addProjectile(new Projectile(p1.getX() + this.getX(), p1.getY() + this.getY(), p2.getX(), p2.getY()));
			}
		}
	}

	public class Projectile
	{
		private static final double	radius	= 5;
		double						x, y, xV, yV;	// Pixels per second

		public Projectile()
		{
			this(0, 0, 0, 0);
		}

		@Override
		public void finalize()
		{
			System.out.println(this + " has been removed from memory!");
		}

		public Projectile(final double x, final double y)
		{
			this(x, y, 0, 0);
		}

		public Projectile(final double x, final double y, final double xV, final double yV)
		{
			this.x = x;
			this.y = y;
			this.xV = xV;
			this.yV = yV;
		}

		public Rectangle getBoundingBox()
		{
			return new Rectangle((int) (this.x - Projectile.radius), (int) (this.y - Projectile.radius), (int) (2 * Projectile.radius),
					(int) (2 * Projectile.radius));
		}

		public double getX()
		{
			return this.x;
		}

		public double getY()
		{
			return this.y;
		}

		public void paint(final Graphics g)
		{
			g.setColor(Color.GRAY);
			g.fillOval((int) (this.x - Projectile.radius), (int) (this.y - Projectile.radius), (int) (2 * Projectile.radius), (int) (2 * Projectile.radius));
		}

		public void setLocation(final double x, final double y)
		{
			this.x = x;
			this.y = y;
		}

		public void updatePosition()
		{
			this.x += this.xV;
			this.y += this.yV;
		}
	}

	public static void main(final String[] args)
	{
		final JFrame frame = new JFrame("Frame");
		final Arena a = new Arena(new Rectangle(10, 10, 700, 700), 500);
		a.addFighter(a.new Fighter(new FeedforwardNetwork("8 12 4"), 50, 50, 0, a));
		a.getFighters().get(0).isShooting = true;
		// a.getFighters().get(0).xV = 0.25;
		// a.getFighters().get(0).yV = 0.3;
		// a.getFighters().get(0).angleV = 0.025;
		final JComponent jc = new JComponent()
		{
			private static final long	serialVersionUID	= 1L;
			private static final int	updateSpeed			= 16;

			@Override
			public void paintComponent(final Graphics g)
			{
				final long startTime = System.nanoTime();
				// System.gc();
				a.paint(g);
				a.updatePhysics();

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
		frame.add(jc);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}

	private final Rectangle				bounds;

	private final ArrayList<Fighter>	fighters;
	private final ArrayList<Projectile>	projectiles;

	private int							age;
	private final int					maxAge;

	public Arena(final Rectangle bounds, int maxAge)
	{
		this.fighters = new ArrayList<Fighter>();
		this.projectiles = new ArrayList<Projectile>();
		this.bounds = bounds;
		this.age = 0;
		this.maxAge = maxAge;
	}

	public void addFighter(final Fighter f)
	{
		this.fighters.add(f);
	}

	public ArrayList<Fighter> getFighters()
	{
		return this.fighters;
	}

	public ArrayList<Projectile> getProjectiles()
	{
		return this.projectiles;
	}

	public void paint(final Graphics g)
	{
		final Graphics2D g2d = (Graphics2D) g;
		final AffineTransform old = g2d.getTransform();
		g2d.translate(0, g2d.getClipBounds().getHeight() - 1);
		g2d.scale(1, -1);

		g.setColor(Color.BLACK);
		g.drawRect(this.bounds.getLocation().x, this.bounds.getLocation().y, (int) this.bounds.getWidth(), (int) this.bounds.getHeight());
		for (final Fighter f : this.fighters)
			f.paint(g);
		for (final Projectile p : this.projectiles)
			p.paint(g);

		g2d.setTransform(old);
	}

	public void addProjectile(Projectile p)
	{
		this.projectiles.add(p);
	}

	public void updatePhysics()
	{
		for (Iterator<Fighter> i = this.fighters.iterator(); i.hasNext();)
		{
			Fighter f = i.next();
			f.updatePosition();
			if (!bounds.contains(f.getX(), f.getY()))
			{
				i.remove();
			}
		}
		for (Iterator<Projectile> i = this.projectiles.iterator(); i.hasNext();)
		{
			Projectile p = i.next();
			p.updatePosition();
			if (!bounds.contains(p.getX(), p.getY()))
			{
				i.remove();
			}
		}

		this.age++;
		if (this.age > this.maxAge && this.maxAge > 0)
		{
			System.out.println("Arena is too old! Time for the simulation to end.");
		}
	}
}