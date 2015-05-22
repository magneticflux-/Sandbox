package com.sandbox.evolve;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.util.FastMath;
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
		private static final double			RADIUS		= 20;
		public double						fov;

		public final boolean				isRobot;

		public boolean						isShooting	= false;
		public double						range;
		public final Color					team		= new Color((int) FastMath.round(FastMath.random() * 255),
																(int) FastMath.round(FastMath.random() * 255), (int) FastMath.round(FastMath.random() * 255));
		private final Arena					arena;
		@Nullable
		private final FeedforwardNetwork	brain;
		private BigFraction					score;
		private int							shootDelay	= 0;
		private boolean						isCheating;

		public Fighter(final FeedforwardNetwork brain, final double x, final double y, final double angle, final boolean isRobot, final boolean isCheating,
				final Arena arena)
		{
			super(x, y, null);

			if (this.brain != null
					&& !(Integer.parseInt(brain.getLayout().substring(0, 1)) >= 3 && Integer.parseInt(brain.getLayout().substring(
							brain.getLayout().length() - 1, brain.getLayout().length())) >= 6))
				throw new IllegalArgumentException("Insufficient brain in/out space.");

			this.angle = angle;
			this.angleV = 0;
			this.brain = brain;
			this.arena = arena;
			this.score = new BigFraction(0);
			this.isRobot = isRobot;
			this.fov = FastMath.PI / 3;
			this.range = 400;
			this.isControlled = true;
			this.isCheating = isCheating;
		}

		@Override
		public String toString()
		{
			return this.brain.toString() + " " + this.team.toString();
		}

		public void decrementScore(final BigFraction i)
		{
			this.score = this.score.subtract(i);
		}

		@Override
		public Rectangle getBoundingBox()
		{
			return new Rectangle((int) FastMath.round(this.x - Fighter.RADIUS), (int) FastMath.round(this.y - Fighter.RADIUS),
					(int) FastMath.round(2 * Fighter.RADIUS), (int) FastMath.round(2 * Fighter.RADIUS));
		}

		public double getNormalizedX()
		{
			return (this.x - this.arena.bounds.x) / this.arena.bounds.width;
		}

		public double getNormalizedY()
		{
			return (this.y - (this.arena.bounds.y - this.arena.bounds.height)) / this.arena.bounds.height;
		}

		public int getNumVisibleEnemyFighters()
		{
			int sum = 0;
			for (final Fighter f : this.arena.getFighters())
			{
				if (!this.isRobot && f != this)
					System.out.printf("AngleTo: %.4f Angle: %.4f Diff: %.4f\n", MathUtil.angleTo(this.getPoint(), f.getPoint()), this.angle,
							FastMath.abs(MathUtil.angleTo(this.getPoint(), f.getPoint()) - this.angle));

				if (FastMath.abs(MathUtil.angleTo(this.getPoint(), f.getPoint()) - this.angle) < this.fov / 2
						&& MathUtil.distance(this.getPoint(), f.getPoint()) < this.range && f != this) sum++;
			}

			return sum;
		}

		public int getNumVisibleEnemyProjectiles()
		{
			int sum = 0;
			for (final Projectile p : this.arena.getProjectiles())
				if (MathUtil.angleTo(this.getPoint(), p.getPoint()) - this.angle < this.fov / 2
						&& MathUtil.distance(this.getPoint(), p.getPoint()) < this.range && this != p.getOwner()) sum++;
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
				g2d.fillOval((int) FastMath.round(this.x - Fighter.RADIUS - 2), (int) FastMath.round(this.y - Fighter.RADIUS - 2),
						(int) FastMath.round(2 * Fighter.RADIUS + 4), (int) FastMath.round(2 * Fighter.RADIUS + 4));
			}

			g2d.setColor(this.team);
			g2d.fillOval((int) FastMath.round(this.x - Fighter.RADIUS), (int) FastMath.round(this.y - Fighter.RADIUS),
					(int) FastMath.round(2 * Fighter.RADIUS), (int) FastMath.round(2 * Fighter.RADIUS));

			final PolarPoint p = new PolarPoint(this.range, this.angle);
			g2d.setColor(Color.RED);
			g2d.drawLine((int) this.x, (int) this.y, (int) FastMath.round(this.x + p.getX()), (int) FastMath.round(this.y + p.getY()));

			g2d.setColor(Color.RED);
			final PolarPoint p2 = new PolarPoint(this.range, this.angle - this.fov / 2);
			g2d.drawLine((int) this.x, (int) this.y, (int) FastMath.round(this.x + p2.getX()), (int) FastMath.round(this.y + p2.getY()));
			final PolarPoint p3 = new PolarPoint(this.range, this.angle + this.fov / 2);
			g2d.drawLine((int) this.x, (int) this.y, (int) FastMath.round(this.x + p3.getX()), (int) FastMath.round(this.y + p3.getY()));
			g2d.drawArc((int) FastMath.round(this.x - this.range), (int) FastMath.round(this.y - this.range), (int) FastMath.round(this.range * 2),
					(int) FastMath.round(this.range * 2), -(int) FastMath.round(FastMath.toDegrees(this.angle - this.fov / 2)),
					-(int) FastMath.round(FastMath.toDegrees(this.fov)));
		}

		public void react()
		{
			if (this.isRobot && this.brain != null)
			{
				if (this.isCheating)
				{
					this.reactTo(new double[] { this.angle - MathUtil.angleTo(this.getPoint(), arena.getOtherFighter(this).getPoint()),
							this.getNumVisibleEnemyProjectiles(), this.getNumVisibleEnemyFighters() });
				}
				else
				{
					this.reactTo(new double[] { (double) this.shootDelay / Arena.RELOAD_TIME, this.getNumVisibleEnemyProjectiles(),
							this.getNumVisibleEnemyFighters() });
				}
			}
			if (!this.isRobot)
			{
				System.out.println("X:" + this.x + " Y:" + this.y);
			}
		}

		public void reactTo(final double[] enviroment)
		{
			final double[] reaction = this.brain.evaluate(enviroment);
			final PolarPoint p1 = new PolarPoint(reaction[0] * 3, this.angle);
			final PolarPoint p2 = new PolarPoint(reaction[1] * 3, this.angle + FastMath.PI / 2);
			this.xV = p1.getX() + p2.getX();
			this.yV = p1.getY() + p2.getY();
			this.angleV = reaction[2] * FastMath.PI * (1 / 10d);
			this.isShooting = reaction[3] > 0;
			if (this.fov + reaction[4] / 10 >= 0 && this.fov + reaction[4] / 10 <= FastMath.PI) this.fov += reaction[4] / 10;
			if (this.range + reaction[5] * 10 >= 50) this.range += reaction[5] * 10;
		}

		@Override
		public void updatePosition()
		{
			super.updatePosition();

			if (FastMath.abs(this.angle) >= FastMath.PI * 2) this.angle = (FastMath.PI * 2 + this.angle) % (FastMath.PI * 2);

			if (this.shootDelay > 0)
				this.shootDelay--;
			else if (this.isShooting)
			{
				this.shootDelay = Arena.RELOAD_TIME;
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
			return new Rectangle((int) FastMath.round(this.x - Projectile.RADIUS), (int) FastMath.round(this.y - Projectile.RADIUS),
					(int) FastMath.round(2 * Projectile.RADIUS), (int) FastMath.round(2 * Projectile.RADIUS));
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
			g.fillOval((int) FastMath.round(this.x - Projectile.RADIUS), (int) FastMath.round(this.y - Projectile.RADIUS),
					(int) FastMath.round(2 * Projectile.RADIUS), (int) FastMath.round(2 * Projectile.RADIUS));

			final PolarPoint[] points = new PolarPoint[] { new PolarPoint(Projectile.RADIUS, this.angle),
					new PolarPoint(Projectile.RADIUS, this.angle + FastMath.PI / 2), new PolarPoint(Projectile.RADIUS, this.angle + FastMath.PI),
					new PolarPoint(Projectile.RADIUS, this.angle + 3 * FastMath.PI / 2) };
			g.setColor(Color.BLACK);
			for (final PolarPoint p : points)
				g.drawLine((int) FastMath.round(this.x), (int) FastMath.round(this.y), (int) FastMath.round(this.x + p.getX()),
						(int) FastMath.round(this.y + p.getY()));
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
				final PolarPoint p1 = new PolarPoint(Arena.AIR_DENSITY * FastMath.sqrt(FastMath.pow(this.xV, 2) + FastMath.pow(this.yV, 2)) * MathUtils.TWO_PI
						* this.angleV * FastMath.pow(Projectile.RADIUS, 2), FastMath.atan2(this.yV, this.xV) + FastMath.PI / 2);
				final PolarPoint p2 = new PolarPoint(-(Arena.SPHERE_CD * 0.5 * Arena.AIR_DENSITY * (FastMath.pow(this.xV, 2) + FastMath.pow(this.yV, 2))
						* FastMath.pow(Projectile.RADIUS, 2) * FastMath.PI), FastMath.atan2(this.yV, this.xV));
				this.xV += p1.getX() + p2.getX();
				this.yV += p1.getY() + p2.getY();
			}
			this.x += this.xV;
			this.y += this.yV;
			this.angle += this.angleV;
			this.angleV *= 0.99;
		}
	}

	public static final double	AIR_DENSITY	= 0.00075;
	public static final double	SPHERE_CD	= 0.1;
	public static final int		RELOAD_TIME	= 20;

	public static void main1(final String[] args)
	{
		final JFrame frame = new JFrame("Frame");
		final Arena a = new Arena(new Rectangle(10, 10, 700, 700), -1);
		final FeedforwardNetwork n = new FeedforwardNetwork("5 9 7");
		for (int i = 0; i < 10; i++)
		{
			n.randomizeWeights(new MersenneTwisterRNG(), 2);
			a.addFighter(a.new Fighter(n.getDeepCopy(), FastMath.random() * 400 + 200, FastMath.random() * 400 + 200, FastMath.random() * FastMath.PI * 2,
					true, false, a));
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
	private final double				maxDistance;
	private final ArrayList<Projectile>	projectiles;

	public Arena(final Rectangle bounds, final int maxAge)
	{
		this.fighters = new ArrayList<Fighter>();
		this.projectiles = new ArrayList<Projectile>();
		this.bounds = bounds;
		this.age = 0;
		this.maxAge = maxAge;
		this.maxDistance = FastMath.sqrt(FastMath.pow(bounds.getWidth(), 2) + FastMath.pow(bounds.getHeight(), 2));
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

		for (final Fighter f : this.fighters)
			f.paint(g);
		for (final Projectile p : this.projectiles)
			p.paint(g);

		g2d.setColor(Color.BLACK);
		g2d.drawRect(this.bounds.getLocation().x, this.bounds.getLocation().y, (int) this.bounds.getWidth(), (int) this.bounds.getHeight() - 1);

		g2d.setTransform(old);

		g2d.setColor(Color.BLACK);

		for (int i = 0; i < this.fighters.size(); i++)
		{
			g2d.setColor(Color.BLACK);
			g2d.drawString("Fighter " + (i + 1) + ": " + this.fighters.get(i).getScore().bigDecimalValue() + "pts. "
					+ this.fighters.get(i).getNumVisibleEnemyFighters() + " visible fighters. " + this.fighters.get(i).getNumVisibleEnemyProjectiles()
					+ " visible projectiles. " + this.fighters.get(i).shootDelay, 50, 20 + i * g2d.getFontMetrics().getHeight());

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
					if (p.getOwner() != null) p.getOwner().decrementScore(new BigFraction(1, 4));
				}
				else
					for (final Fighter f : this.fighters)
						if (MathUtil.distance(p.getX(), p.getY(), f.getX(), f.getY()) <= Projectile.RADIUS + Fighter.RADIUS)
						{
							i.remove();
							if (p.getOwner() != null) p.getOwner().incrementScore(BigFraction.ONE);
							f.decrementScore(new BigFraction(0, 10));
							break;
						}
			}
		}
	}
}