package com.sandbox.evolve;

import com.sandbox.neural.FeedforwardNetwork;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.interactive.Renderer;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.UserAbort;
import org.uncommons.watchmaker.swing.evolutionmonitor.EvolutionMonitor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class FeedforwardNetworkEvolveFunction {
	public static void main(String[] args) {
		final String layout = "2 5 5 1";
		CandidateFactory<FeedforwardNetwork> candidateFactory = new NetworkCandidateFactory(layout, 4);
		List<EvolutionaryOperator<FeedforwardNetwork>> operators = new LinkedList<EvolutionaryOperator<FeedforwardNetwork>>();
		operators.add(new FeedforwardNetworkCrossover(1));
		operators.add(new FeedforwardNetworkMutation(new Probability(0.03)));
		EvolutionaryOperator<FeedforwardNetwork> evolutionScheme = new EvolutionPipeline<FeedforwardNetwork>(operators);
		FitnessEvaluator<FeedforwardNetwork> fitnessEvaluator = new FitnessEvaluator<FeedforwardNetwork>() {
			@Override
			public double getFitness(FeedforwardNetwork candidate, List<? extends FeedforwardNetwork> population) {
				double error = 0;
				for (boolean p = false; ; p = !p) {
					for (boolean q = false; ; q = !q) {
						error += Math.abs((candidate.evaluate(new double[]{p ? 1 : 0, q ? 1 : 0})[0]) - FeedforwardNetworkEvolveFunction.function(p ? 1 : 0, q ? 1 : 0));
						if (q == true)
							break;
					}
					if (p == true)
						break;
				}
				return error;
			}

			@Override
			public boolean isNatural() {
				return false;
			}
		};
		SelectionStrategy<Object> selectionStrategy = new TournamentSelection(new Probability(1));
		Random rng = new MersenneTwisterRNG();
		EvolutionEngine<FeedforwardNetwork> engine = new GenerationalEvolutionEngine<FeedforwardNetwork>(candidateFactory, evolutionScheme, fitnessEvaluator, selectionStrategy, rng);
		engine.addEvolutionObserver(new EvolutionObserver<FeedforwardNetwork>() {
			@Override
			public void populationUpdate(PopulationData<? extends FeedforwardNetwork> data) {
				System.out.printf("Generation %d: %s\n", data.getGenerationNumber(), data.getBestCandidate());
			}
		});
		final UserAbort abort = new UserAbort();
		final EvolutionMonitor<FeedforwardNetwork> monitor = new EvolutionMonitor<FeedforwardNetwork>(new Renderer<FeedforwardNetwork, JComponent>() {
			private BufferedImage image = null;

			@Override
			public JComponent render(final FeedforwardNetwork entity) {
				JPanel panel = new JPanel();
				JComponent comp1 = new JPanel();
				comp1.add(new JPanel() {
					private static final long serialVersionUID = 1L;

					{
						this.setPreferredSize(new Dimension(400, 400));
					}

					@Override
					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
						for (int x = 0; x < this.getWidth(); x++) {
							for (int y = 0; y < this.getHeight(); y++) {
								double value = function((x - this.getWidth() / 2d) / 8, (y - this.getHeight() / 2d) / 8);
								bi.setRGB(x, y, (new Color((int) ((value + 1) * 127), (int) ((value + 1) * 127), (int) ((value + 1) * 127)).getRGB()));
							}
						}
						g.drawImage(bi, 0, 0, this);
						System.out.println("drawn");
					}
				}, BorderLayout.LINE_START);
				comp1.add(new JPanel() {
					private static final long serialVersionUID = 1L;

					{
						this.setPreferredSize(new Dimension(400, 400));
					}

					@Override
					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
						for (int x = 0; x < this.getWidth(); x++) {
							for (int y = 0; y < this.getHeight(); y++) {
								double value = entity.evaluate(new double[]{(x - this.getWidth() / 2d) / 8, (y - this.getHeight() / 2d) / 8})[0];
								bi.setRGB(x, y, (new Color((int) ((value + 1) * 127), (int) ((value + 1) * 127), (int) ((value + 1) * 127))).getRGB());
							}
						}
						g.drawImage(bi, 0, 0, this);
					}

				}, BorderLayout.LINE_END);
				// JComponent comp2 = new JGraph(new JGraphModelAdapter<AbstractNode, DefaultWeightedEdge>(entity.getGraphView()));
				if (image == null) {
					this.image = entity.getGraphImage();
				}

				JComponent comp2 = new JPanel() {
					private static final long serialVersionUID = 1L;

					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						Graphics2D g2d = ((Graphics2D) g);
						g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
						g2d.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), Color.PINK, this);
					}
				};
				comp1.setPreferredSize(new Dimension(800, 400));

				JButton button = new JButton("Recalculate Visualization of Neural Network");
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						image = entity.getGraphImage();
					}
				});
				Dimension d = new Dimension(button.getFontMetrics(button.getFont()).stringWidth(button.getText()) + 5, button.getFontMetrics(button.getFont()).getHeight() + 5);
				button.setPreferredSize(d);
				button.setMaximumSize(d);
				button.setMinimumSize(d);

				panel.setLayout(new BorderLayout());
				panel.add(comp1, BorderLayout.PAGE_START);
				panel.add(comp2, BorderLayout.CENTER);
				panel.add(button, BorderLayout.PAGE_END);
				panel.setBackground(Color.WHITE);
				return panel;
			}
		}, false);
		synchronized (monitor.getGUIComponent().getTreeLock()) {
			((JTabbedPane) monitor.getGUIComponent().getComponents()[0]).add(new JPanel() {
				private static final long serialVersionUID = 1L;

				{
					this.setName("Abort Button");
					this.setLayout(new BorderLayout());
					this.add(new JButton("ABORT") {
						private static final long serialVersionUID = 1L;

						{
							this.setBackground(Color.RED);
							this.setMaximumSize(new Dimension(100, 50));
							this.setPreferredSize(new Dimension(100, 50));

							this.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(final ActionEvent e) {
									abort.abort();
									System.out.println("*** ABORT SEQUENCE ACTIVATED ***");
								}
							});
						}
					}, BorderLayout.PAGE_START);
				}
			});
		}
		monitor.showInFrame("Evolution", true);
		engine.addEvolutionObserver(monitor);
		final FeedforwardNetwork result = engine.evolve(2500, 250, abort);
		System.out.println("Fittest individual: " + result);
	}

	public static double function(double p, double q) {
		boolean p1 = p > 0;
		boolean q1 = q > 0;
		return p1 != q1 ? 1 : 0;
	}
}
