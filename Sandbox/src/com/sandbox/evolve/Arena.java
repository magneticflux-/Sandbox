package com.sandbox.evolve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.util.MathUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.uncommons.maths.random.MersenneTwisterRNG;

import com.electronauts.mathutil.MathUtil;
import com.electronauts.mathutil.PolarPoint;
import com.sandbox.neural.FeedforwardNetwork;

public class Arena
{
	public class Fighter extends Projectile
	{
		private static final double				RADIUS		= 20;
		public double							fov;

		public final boolean					isRobot;

		public boolean							isShooting	= false;
		public double							range;
		public final Color						team		= new Color((int) Math.round(Math.random() * 255), (int) Math.round(Math.random() * 255),
																	(int) Math.round(Math.random() * 255));
		private final Arena						arena;
		@Nullable
		private final FeedforwardNetwork		brain;
		private final CircularFifoQueue<Double>	memory;
		private BigFraction						score;
		private int								shootDelay	= 0;

		public Fighter(final FeedforwardNetwork brain, final double x, final double y, final boolean isRobot, final Arena arena)
		{
			this(brain, x, y, 0, isRobot, arena);
		}

		public Fighter(final FeedforwardNetwork brain, final double x, final double y, final double angle, final boolean isRobot, final Arena arena)
		{
			super(x, y, null);

			if (this.brain != null
					&& !(Integer.parseInt(brain.getLayout().substring(0, 1)) >= 5 && Integer.parseInt(brain.getLayout().substring(
							brain.getLayout().length() - 1, brain.getLayout().length())) >= 7))
				throw new IllegalArgumentException("Insufficient brain in/out space.");

			this.angle = angle;
			this.angleV = 0;
			this.brain = brain;
			this.arena = arena;
			this.score = new BigFraction(0);
			this.memory = new CircularFifoQueue<Double>(3);
			this.memory.addAll(Arrays.asList(0d, 0d, 0d));
			this.isRobot = isRobot;
			this.fov = Math.PI / 3;
			this.range = 400;
			this.isControlled = true;
		}

		public void decrementScore(final BigFraction i)
		{
			this.score = this.score.subtract(i);
		}

		@Override
		public Rectangle getBoundingBox()
		{
			return new Rectangle((int) Math.round(this.x - Fighter.RADIUS), (int) Math.round(this.y - Fighter.RADIUS), (int) Math.round(2 * Fighter.RADIUS),
					(int) Math.round(2 * Fighter.RADIUS));
		}

		public double getNormalizedX()
		{
			return (this.x - this.arena.bounds.x) / this.arena.bounds.width;
		}

		public double getNormalizedY()
		{
			return (this.y - (this.arena.bounds.y - this.arena.bounds.height)) / this.arena.bounds.height;
		}

		public int getNumVisibleFighters()
		{
			int sum = 0;
			final Point2D ownPoint = this.getPoint();
			for (final Fighter f : this.arena.getFighters())
				if (MathUtil.angleTo(ownPoint, f.getPoint()) - this.angle < this.fov / 2 && MathUtil.distance(ownPoint, f.getPoint()) < this.range) sum++;
			return sum;
		}

		public int getNumVisibleProjectiles()
		{
			int sum = 0;
			final Point2D ownPoint = this.getPoint();
			for (final Projectile p : this.arena.getProjectiles())
				if (MathUtil.angleTo(ownPoint, p.getPoint()) - this.angle < this.fov / 2 && MathUtil.distance(ownPoint, p.getPoint()) < this.range) sum++;
			return sum;
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
			final Graphics2D g2d = (Graphics2D) g;
			if (this.isShooting)
			{
				g2d.setColor(Color.RED);
				g2d.fillOval((int) Math.round(this.x - Fighter.RADIUS - 2), (int) Math.round(this.y - Fighter.RADIUS - 2),
						(int) Math.round(2 * Fighter.RADIUS + 4), (int) Math.round(2 * Fighter.RADIUS + 4));
			}

			g2d.setColor(this.team);
			g2d.fillOval((int) Math.round(this.x - Fighter.RADIUS), (int) Math.round(this.y - Fighter.RADIUS), (int) Math.round(2 * Fighter.RADIUS),
					(int) Math.round(2 * Fighter.RADIUS));

			final PolarPoint p = new PolarPoint(this.range, this.angle);
			g2d.setColor(Color.RED);
			g2d.drawLine((int) this.x, (int) this.y, (int) Math.round(this.x + p.getX()), (int) Math.round(this.y + p.getY()));

			g2d.setColor(Color.RED);
			final PolarPoint p2 = new PolarPoint(this.range, this.angle - this.fov / 2);
			g2d.drawLine((int) this.x, (int) this.y, (int) Math.round(this.x + p2.getX()), (int) Math.round(this.y + p2.getY()));
			final PolarPoint p3 = new PolarPoint(this.range, this.angle + this.fov / 2);
			g2d.drawLine((int) this.x, (int) this.y, (int) Math.round(this.x + p3.getX()), (int) Math.round(this.y + p3.getY()));
			g2d.drawArc((int) Math.round(this.x - this.range), (int) Math.round(this.y - this.range), (int) Math.round(this.range * 2),
					(int) Math.round(this.range * 2), -(int) Math.round(Math.toDegrees(this.angle - this.fov / 2)), -(int) Math.round(Math.toDegrees(this.fov)));
		}

		public void react()
		{
			if (this.isRobot && this.brain != null)
				this.reactTo(new double[] { this.memory.get(0), this.memory.get(1), this.memory.get(2), this.getNumVisibleProjectiles(),
						this.getNumVisibleFighters() });
			/*
			 * this.reactTo(new double[] { this.memory.get(0), this.memory.get(1), this.memory.get(2), MathUtil.angleTo(new Point2D.Double(this.getX(),
			 * this.getY()), new Point2D.Double(this.arena.getOtherFighter(this).getX(), this.arena .getOtherFighter(this).getY())) - this.angle,
			 * this.arena.getOtherFighter(this).xV, this.arena.getOtherFighter(this).yV, this.arena.getOtherFighter(this).angle, MathUtil.distance(new
			 * Point2D.Double(this.getX(), this.getY()), new Point2D.Double(this.arena.getOtherFighter(this).getX(), this.arena .getOtherFighter(this).getY()))
			 * / this.arena.getMaxDistance(), this.arena.r.nextDouble() * 2 - 1 });
			 */
		}

		public void reactTo(final double[] enviroment)
		{
			final double[] reaction = this.brain.evaluate(enviroment);
			final PolarPoint p1 = new PolarPoint(reaction[0] * 4, this.angle);
			final PolarPoint p2 = new PolarPoint(reaction[1] * 4, this.angle + Math.PI / 2);
			this.xV = p1.getX() + p2.getX();
			this.yV = p1.getY() + p2.getY();
			this.angleV = reaction[2] / (Math.PI * 4);
			this.isShooting = reaction[3] > 0;
			this.memory.add(reaction[4]);
			if (this.fov + (reaction[5] / 10) >= 0 && this.fov + (reaction[5] / 10) <= Math.PI) this.fov += (reaction[5] / 10);
			if (this.range + (reaction[6] * 10) >= 0) this.range += (reaction[6] * 10);
		}

		@Override
		public void updatePosition()
		{
			super.updatePosition();

			if (Math.abs(this.angle) >= Math.PI * 2) this.angle = (Math.PI * 2 + this.angle) % (Math.PI * 2);

			if (this.shootDelay > 0)
				this.shootDelay--;
			else if (this.isShooting)
			{
				this.shootDelay = 10;
				final PolarPoint p1 = new PolarPoint(Fighter.RADIUS + 10, this.angle);
				final PolarPoint p2 = new PolarPoint(25, this.angle);
				this.arena.addProjectile(new Projectile(p1.getX() + this.getX(), p1.getY() + this.getY(), p2.getX() + this.xV, p2.getY() + this.yV,
						this.angleV, this));
			}
		}
	}

	public class Projectile
	{
		private static final double	RADIUS			= 5;
		public double				angle;
		public double				angleV;
		private final Fighter		owner;
		protected double			x, y, xV, yV;				// Pixels per second
		public boolean				isControlled	= false;

		public Projectile(final double x, final double y, final double xV, final double yV, final double angleV, final Fighter owner)
		{
			this.x = x;
			this.y = y;
			this.xV = xV;
			this.yV = yV;
			this.owner = owner;
			this.angleV = angleV;
		}

		public Projectile(final double x, final double y, final Fighter owner)
		{
			this(x, y, 0, 0, 0, owner);
		}

		public Projectile(final Fighter owner)
		{
			this(0, 0, 0, 0, 0, owner);
		}

		public Rectangle getBoundingBox()
		{
			return new Rectangle((int) Math.round(this.x - Projectile.RADIUS), (int) Math.round(this.y - Projectile.RADIUS),
					(int) Math.round(2 * Projectile.RADIUS), (int) Math.round(2 * Projectile.RADIUS));
		}

		@Nullable
		public Fighter getOwner()
		{
			return this.owner;
		}

		public Point2D getPoint()
		{
			return new Point2D.Double(this.x, this.y);
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
			g.fillOval((int) Math.round(this.x - Projectile.RADIUS), (int) Math.round(this.y - Projectile.RADIUS), (int) Math.round(2 * Projectile.RADIUS),
					(int) Math.round(2 * Projectile.RADIUS));

			final PolarPoint[] points = new PolarPoint[] { new PolarPoint(Projectile.RADIUS, this.angle),
					new PolarPoint(Projectile.RADIUS, this.angle + Math.PI / 2), new PolarPoint(Projectile.RADIUS, this.angle + Math.PI),
					new PolarPoint(Projectile.RADIUS, this.angle + 3 * Math.PI / 2) };
			g.setColor(Color.BLACK);
			for (final PolarPoint p : points)
				g.drawLine((int) Math.round(this.x), (int) Math.round(this.y), (int) Math.round(this.x + p.getX()), (int) Math.round(this.y + p.getY()));
		}

		public void setLocation(final double x, final double y)
		{
			this.x = x;
			this.y = y;
		}

		public void updatePosition()
		{
			if (!this.isControlled)
			{
				final PolarPoint p1 = new PolarPoint(Arena.AIR_DENSITY * Math.sqrt(Math.pow(this.xV, 2) + Math.pow(this.yV, 2)) * MathUtils.TWO_PI
						* this.angleV * Math.pow(Projectile.RADIUS, 2), Math.atan2(this.yV, this.xV) + Math.PI / 2);
				final PolarPoint p2 = new PolarPoint(-(Arena.SPHERE_CD * 0.5 * Arena.AIR_DENSITY * (Math.pow(this.xV, 2) + Math.pow(this.yV, 2))
						* Math.pow(Projectile.RADIUS, 2) * Math.PI), Math.atan2(this.yV, this.xV));
				this.xV += p1.getX() + p2.getX();
				this.yV += p1.getY() + p2.getY();
			}
			this.x += this.xV;
			this.y += this.yV;
			this.angle += this.angleV;
		}
	}

	public static final double	AIR_DENSITY	= 0.00075;
	public static final double	SPHERE_CD	= 0.1;

	public static void main(final String[] args)
	{
		final JFrame frame = new JFrame("Frame");
		final Arena a = new Arena(new Rectangle(10, 10, 700, 700), -1);
		final FeedforwardNetwork n = new FeedforwardNetwork("5 9 7");
		for (int i = 0; i < 10; i++)
		{
			n.randomizeWeights(new MersenneTwisterRNG(), 2);
			a.addFighter(a.new Fighter(n.getDeepCopy(), Math.random() * 400 + 200, Math.random() * 400 + 200, Math.random() * Math.PI * 2, true, a));
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

	public final Random					r;

	private int							age;
	private final Rectangle				bounds;
	private final ArrayList<Fighter>	fighters;
	private final int					maxAge;
	private final double				maxDistance;
	private final ArrayList<Projectile>	projectiles;

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

	/*
	 * private void drawString(final Graphics g, final String text, final int x, int y) { for (final String line : text.split("\n")) g.drawString(line, x, y +=
	 * g.getFontMetrics().getHeight()); }
	 */

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

		for (int i = 0; i < this.fighters.size(); i++)
		{
			g2d.setColor(Color.BLACK);
			g2d.drawString("Fighter " + (i + 1) + ": " + this.fighters.get(i).getScore().bigDecimalValue() + "pts.", 50, 20 + i
					* g2d.getFontMetrics().getHeight());

			g2d.setColor(this.fighters.get(i).team);
			g2d.fillRect(50 - g2d.getFontMetrics().getHeight(), 20 + (i - 1) * g2d.getFontMetrics().getHeight(), g2d.getFontMetrics().getHeight(), g2d
					.getFontMetrics().getHeight());
		}
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
					if (p.getOwner() != null) p.getOwner().decrementScore(BigFraction.ONE);
				}
				else
					for (final Fighter f : this.fighters)
						if (MathUtil.distance(p.getX(), p.getY(), f.getX(), f.getY()) <= Projectile.RADIUS + Fighter.RADIUS)
						{
							i.remove();
							if (p.getOwner() != null) p.getOwner().incrementScore(BigFraction.ONE);
							f.decrementScore(BigFraction.ONE);
							break;
						}
			}
		}
	}
}