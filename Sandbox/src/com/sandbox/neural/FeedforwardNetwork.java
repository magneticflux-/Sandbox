package com.sandbox.neural;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class FeedforwardNetwork
{
	public static void main(final String[] args)
	{
		final FeedforwardNetwork n1 = new FeedforwardNetwork("2 3 1");
		final FeedforwardNetwork n2 = n1.getDeepCopy();
		((NeuronNode) n1.getLayers().get(1).getNode(0)).getWeights().set(0, 13d);
		((InputNode) n1.getLayers().get(0).getNode(0)).setOutput(4);
		System.out.println(n1);
		System.out.println(n2);
	}

	private NodeLayer					inputs;
	private final ArrayList<NodeLayer>	layers;
	private final InputNode				BIAS_NODE;

	private final String				layout;

	public FeedforwardNetwork(final String layout)
	/*
	 * First is input size, last is output size, middle are hidden layers. Given the layout "2 2 1", the network would have a layer of two InputNodes, a layer
	 * of two NeuronNodes, and a layer of one NeuronNode. To set the inputs to the neural net, set the input node's value and then recompute.
	 */
	{
		BIAS_NODE = new InputNode(1);

		this.layout = layout;
		this.layers = new ArrayList<NodeLayer>();
		final Scanner s = new Scanner(layout);
		while (s.hasNext())
		{
			final int layerSize = Integer.parseInt(s.next());
			if (this.layers.size() == 0)
			{
				final NodeLayer nl = new NodeLayer();
				this.inputs = nl;
				for (int i = 0; i < layerSize; i++)
					nl.addNode(new InputNode());
				this.layers.add(nl);
			}
			else
			{
				final NodeLayer parentLayer = this.layers.get(this.layers.size() - 1);
				final NodeLayer nl = new NodeLayer();
				for (int i = 0; i < layerSize; i++)
				{
					NeuronNode n = new NeuronNode(parentLayer.getNodes());
					n.addParent(BIAS_NODE);
					nl.addNode(n);
				}
				this.layers.add(nl);
			}
		}
		s.close();
	}

	public double[] evaluate(final double[] inputs)
	{
		if (inputs.length != this.inputs.getNodes().size())
			throw new IllegalArgumentException("Invalid input size! Cannot evaluate neural net. Input size: " + inputs.length + " Neural net input size: "
					+ this.inputs.getNodes().size());

		for (int i = 0; i < inputs.length; i++)
			((InputNode) this.inputs.getNode(i)).setOutput(inputs[i]);

		final double[] outputs = new double[this.layers.get(this.layers.size() - 1).getNodes().size()];
		for (int i = 0; i < outputs.length; i++)
			outputs[i] = this.layers.get(this.layers.size() - 1).getNode(i).getOutput();
		return outputs;
	}

	@SuppressWarnings("unchecked")
	public FeedforwardNetwork getDeepCopy()
	{
		final FeedforwardNetwork n = new FeedforwardNetwork(this.layout);

		for (int layer = 0; layer < n.getLayers().size(); layer++)
			for (int node = 0; node < n.getLayers().get(layer).getNodes().size(); node++)
				if (n.getLayers().get(layer).getNode(node) instanceof NeuronNode)
					((NeuronNode) n.getLayers().get(layer).getNode(node)).setWeights((List<Double>) ((ArrayList<Double>) ((NeuronNode) this.getLayers()
							.get(layer).getNode(node)).getWeights()).clone());

		return n;
	}

	public NodeLayer getInputs()
	{
		return this.inputs;
	}

	public ArrayList<NodeLayer> getLayers()
	{
		return this.layers;
	}

	public String getLayout()
	{
		return this.layout;
	}

	public void randomizeWeights(final Random rng, final double range)
	{
		for (final NodeLayer l : this.layers)
			for (final AbstractNode n : l.getNodes())
				n.randomizeWeights(rng, range);
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		for (final NodeLayer nl : this.layers)
		{
			for (final AbstractNode absn : nl.getNodes())
				sb.append(absn + "; ");
			sb.append('\n');
		}
		return sb.toString();
	}
}
