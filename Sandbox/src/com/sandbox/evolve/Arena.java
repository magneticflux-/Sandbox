package com.sandbox.evolve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.math3.fraction.BigFraction;
import org.eclipse.jdt.annotation.Nullable;
import org.uncommons.maths.random.MersenneTwisterRNG;

import com.electronauts.mathutil.MathUtil;
import com.electronauts.mathutil.PolarPoint;
import com.sandbox.neural.FeedforwardNetwork;

public class Arena
{
	public class Fighter extends Projectile
	{
		private static final double			radius		= 20;
		private double						angle;
		private double						angleV;
		private final Arena					arena;
		private final FeedforwardNetwork	brain;
		private boolean						isShooting	= false;
		private int							shootDelay	= 0;
		private final Color					team		= new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
		private BigFraction					score;

		public Fighter(final FeedforwardNetwork brain, final double x, final double y, final Arena arena)
		{
			this(brain, x, y, 0, arena);
		}

		public Fighter(final FeedforwardNetwork brain, final double x, final double y, final double angle, final Arena arena)
		{
			super(x, y, null);

			if (!(Integer.parseInt(brain.getLayout().substring(0, 1)) >= 6 && Integer.parseInt(brain.getLayout().substring(brain.getLayout().length() - 1,
					brain.getLayout().length())) >= 4)) throw new IllegalArgumentException("Insufficient brain in/out space.");

			this.angle = angle;
			this.brain = brain;
			this.arena = arena;
			this.score = new BigFraction(0);
		}

		public void decrementScore(final BigFraction i)
		{
			this.score = this.score.subtract(i);
		}

		@Override
		public Rectangle getBoundingBox()
		{
			return new Rectangle((int) (this.x - Fighter.radius), (int) (this.y - Fighter.radius), (int) (2 * Fighter.radius), (int) (2 * Fighter.radius));
		}

		public double getNormalizedX()
		{
			return (this.x - this.arena.bounds.x) / this.arena.bounds.width;
		}

		public double getNormalizedY()
		{
			return (this.y - (this.arena.bounds.y - this.arena.bounds.height)) / this.arena.bounds.height;
		}

		public BigFraction getScore()
		{
			if (this.score.doubleValue() < 0) return BigFraction.ZERO;
			return this.score;
		}

		public void incrementScore(final BigFraction i)
		{
			this.score = this.score.add(i);
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

		public void react()
		{
			this.reactTo(new double[] {
					MathUtil.angleTo(new Point2D.Double(this.getX(), this.getY()), new Point2D.Double(this.arena.getOtherFighter(this).getX(), this.arena
							.getOtherFighter(this).getY()))
							- this.angle,
							this.arena.getOtherFighter(this).xV,
							this.arena.getOtherFighter(this).yV,
							this.arena.getOtherFighter(this).angle,
							MathUtil.distance(new Point2D.Double(this.getX(), this.getY()), new Point2D.Double(this.arena.getOtherFighter(this).getX(), this.arena
									.getOtherFighter(this).getY()))
									/ this.arena.getMaxDistance(), this.arena.r.nextDouble() * 2 - 1 });
		}

		public void reactTo(final double[] enviroment)
		{
			final double[] reaction = this.brain.evaluate(enviroment);
			final PolarPoint p1 = new PolarPoint(reaction[0] * 4, this.angle);
			final PolarPoint p2 = new PolarPoint(reaction[1] * 4, this.angle + Math.PI / 2);
			this.xV = p1.getX() + p2.getX();
			this.yV = p1.getY() + p2.getY();
			this.angleV = reaction[2] / (Math.PI * 2);
			this.isShooting = reaction[3] > 0;
		}

		@Override
		public void updatePosition()
		{
			super.updatePosition();

			this.angle += this.angleV;
			if (Math.abs(this.angle) >= Math.PI * 2) this.angle = (Math.PI * 2 + this.angle) % (Math.PI * 2);

			if (this.shootDelay > 0)
				this.shootDelay--;
			else if (this.isShooting)
			{
				this.shootDelay = 10;
				final PolarPoint p1 = new PolarPoint(Fighter.radius + 10, this.angle);
				final PolarPoint p2 = new PolarPoint(5, this.angle);
				this.arena.addProjectile(new Projectile(p1.getX() + this.getX(), p1.getY() + this.getY(), p2.getX(), p2.getY(), this));
			}
		}
	}

	public class Projectile
	{
		private static final double	radius	= 5;
		protected double			x, y, xV, yV;	// Pixels per second
		private final Fighter		owner;

		public Projectile(final double x, final double y, final double xV, final double yV, final Fighter owner)
		{
			this.x = x;
			this.y = y;
			this.xV = xV;
			this.yV = yV;
			this.owner = owner;
		}

		public Projectile(final double x, final double y, final Fighter owner)
		{
			this(x, y, 0, 0, owner);
		}

		public Projectile(final Fighter owner)
		{
			this(0, 0, 0, 0, owner);
		}

		public Rectangle getBoundingBox()
		{
			return new Rectangle((int) (this.x - Projectile.radius), (int) (this.y - Projectile.radius), (int) (2 * Projectile.radius),
					(int) (2 * Projectile.radius));
		}

		@Nullable
		public Fighter getOwner()
		{
			return this.owner;
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

	public static void main1(final String[] args)
	{
		final JFrame frame = new JFrame("Frame");
		final Arena a = new Arena(new Rectangle(10, 10, 700, 700), 1000);
		final FeedforwardNetwork n = new FeedforwardNetwork("3 4 4");
		for (int i = 0; i < 10; i++)
		{
			n.randomizeWeights(new MersenneTwisterRNG(), 2);
			a.addFighter(a.new Fighter(n.getDeepCopy(), Math.random() * 400 + 200, Math.random() * 400 + 200, Math.random() * Math.PI * 2, a));
		}
		// a.getFighters().get(0).isShooting = true;
		// a.getFighters().get(0).xV = 1;
		// a.getFighters().get(0).yV = 0.3;
		// a.getFighters().get(0).angleV = 0.05;
		final JComponent jc = new JComponent()
		{
			private static final long	serialVersionUID	= 1L;
			private static final double	updateSpeed			= 0;

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
		frame.setSize(800, 800);
		frame.setVisible(true);
	}

	private int							age;

	private final Rectangle				bounds;
	private final ArrayList<Fighter>	fighters;
	private final int					maxAge;
	private final ArrayList<Projectile>	projectiles;
	private final double				maxDistance;
	public final Random					r;

	public Arena(final Rectangle bounds, final int maxAge)
	{
		this.fighters = new ArrayList<Fighter>();
		this.projectiles = new ArrayList<Projectile>();
		this.bounds = bounds;
		this.age = 0;
		this.maxAge = maxAge;
		this.maxDistance = Math.sqrt(Math.pow(bounds.getWidth(), 2) + Math.pow(bounds.getHeight(), 2));
		this.r = new Random(0);
	}

	public void addFighter(final Fighter f)
	{
		this.fighters.add(f);
	}

	public void addProjectile(final Projectile p)
	{
		this.projectiles.add(p);
	}

	private void drawString(final Graphics g, final String text, final int x, int y)
	{
		for (final String line : text.split("\n"))
			g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}

	public Rectangle getBounds()
	{
		return this.bounds;
	}

	public ArrayList<Fighter> getFighters()
	{
		return this.fighters;
	}

	public double getMaxDistance()
	{
		return this.maxDistance;
	}

	public Fighter getOtherFighter(final Fighter f)
	{
		return this.fighters.get((this.fighters.indexOf(f) + 1) % this.fighters.size());
	}

	public ArrayList<Projectile> getProjectiles()
	{
		return this.projectiles;
	}

	public boolean isYoung()
	{
		return this.age < this.maxAge;
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

		g2d.setColor(Color.BLACK);

		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.fighters.size(); i++)
			sb.append("Fighter " + (i + 1) + ": " + this.fighters.get(i).getScore().bigDecimalValue() + "pts.\n");

		this.drawString(g2d, sb.toString(), 50, 20);
	}

	public void updatePhysics()
	{
		this.age++;
		if (this.age > this.maxAge && this.maxAge > 0)
			System.out.println("Arena is too old! Time for the simulation to end.");
		else
		{
			for (final Fighter f : this.fighters)
			{
				f.react();

				if (!this.bounds.contains(f.getX(), f.getY()))
				{
					if (f.getX() > this.bounds.getMaxX() && f.xV > 0)
						f.xV = 0;
					else if (f.getX() < this.bounds.getMinX() && f.xV < 0) f.xV = 0;

					if (f.getY() > this.bounds.getMaxY() && f.yV > 0)
						f.yV = 0;
					else if (f.getY() < this.bounds.getMinY() && f.yV < 0) f.yV = 0;
				}

				f.updatePosition();
				// if (!this.bounds.contains(f.getX(), f.getY())) i.remove();
			}
			for (final Iterator<Projectile> i = this.projectiles.iterator(); i.hasNext();)
			{
				final Projectile p = i.next();
				p.updatePosition();
				if (!this.bounds.contains(p.getX(), p.getY()))
				{
					i.remove();
					if (p.getOwner() != null) p.getOwner().decrementScore(new BigFraction(1, 10));
				}
				else
					for (final Fighter f : this.fighters)
						if (MathUtil.distance(p.getX(), p.getY(), f.getX(), f.getY()) <= Projectile.radius + Fighter.radius)
						{
							i.remove();
							if (p.getOwner() != null) p.getOwner().incrementScore(BigFraction.ONE);
							f.decrementScore(new BigFraction(1, 10));
							break;
						}
			}
		}
	}
}