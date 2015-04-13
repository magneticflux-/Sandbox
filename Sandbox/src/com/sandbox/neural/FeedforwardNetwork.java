package com.sandbox.neural;

import java.util.ArrayList;
import java.util.Scanner;

public class FeedforwardNetwork
{
	private ArrayList<NodeLayer>	layers;
	private NodeLayer				inputs;

	public NodeLayer getInputs()
	{
		return inputs;
	}

	public FeedforwardNetwork(String layout)
	/*
	 * First is input size, last is output size, middle are hidden layers. Given the layout "2 2 1", the network would have a layer of two InputNodes, a layer
	 * of two NeuronNodes, and a layer of one NeuronNode. To set the inputs to the neural net, set the input node's value and then recompute.
	 */
	{
		Scanner s = new Scanner(layout);
		while (s.hasNext())
		{
			int layerSize = Integer.parseInt(s.next());
			if (layers.size() == 0)
			{
				NodeLayer nl = new NodeLayer();
				inputs = nl;
				for (int i = 0; i < layerSize; i++)
				{
					nl.addNode(new InputNode());
				}
				layers.add(nl);
			}
			else
			{
				NodeLayer parentLayer = layers.get(layers.size() - 1);
				NodeLayer nl = new NodeLayer();
				for (int i = 0; i < layerSize; i++)
				{
					nl.addNode(new NeuronNode(parentLayer.getNodes()));
				}
			}
		}
	}
}
