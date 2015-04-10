package com.sandbox.neural;

import java.util.Arrays;
import java.util.Scanner;

public class Network
{
	InputValue[]	inputs;
	Neuron[][]		neurons;
	String			layout;

	public Network(String layout, double[] input)
	// Example "2 2 1" would create a neural net with 2 inputs, two layers of two neurons each, and then a final neuron.
	{
		this.layout = layout;
		inputs = new InputValue[input.length];
		for (int i = 0; i < input.length; i++)
		{
			inputs[i] = new InputValue(input[i]);
		}

		Scanner s = new Scanner(layout);
		neurons = new Neuron[(layout.length() + 1) / 2][]; // Makes the array the number of layers there will be.

		int o = 0;
		while (s.hasNext())
		{
			int layerSize = Integer.parseInt(s.next());
			neurons[o] = new Neuron[layerSize];
			if (o != 0)
				for (int i = 0; i < neurons[o].length; i++)
				{
					neurons[o][i] = new Neuron(Arrays.asList((HasOutput[]) neurons[o - 1]));
					neurons[o][i].randomizeWeights(-2, 2);
					neurons[o][i].randomizeThreshold(-10, 10);
				}
			else
			{
				for (int i = 0; i < neurons[o].length; i++)
				{
					neurons[o][i] = new Neuron(Arrays.asList((HasOutput[]) inputs));
					neurons[o][i].randomizeWeights(-2, 2);
					neurons[o][i].randomizeThreshold(-10, 10);
				}
			}
			o++;
		}
		s.close();

		if (Run.DEBUG)
		{
			System.out.println(neurons.length + " layers.");
			for (int i = 0; i < neurons.length; i++)
			{
				System.out.println("  " + neurons[i].length + " neurons on layer " + i);
				for (int j = 0; j < neurons[i].length; j++)
				{
					System.out.println("    Item " + j + " has a value of " + neurons[i][j]);
				}
			}
		}
	}

	public void setInputs(double[] input)
	{
		for (int i = 0; i < input.length; i++)
		{
			this.inputs[i].setValue(input[i]);
		}
	}

	public void evalNet()
	{
		System.out.println("Input cases: " + neurons[0][0].getInputs());
		System.out.println("Total actual: " + Arrays.toString(this.getOutputs()));
	}

	public double[] getOutputs()
	{
		double[] output = new double[neurons[neurons.length - 1].length]; // Size of last layer

		for (int i = 0; i < neurons[neurons.length - 1].length; i++)
		{
			output[i] = neurons[neurons.length - 1][i].getOutput();
		}

		return output;
	}

	public Network breed(Network mate)
	{
		return null;
	}
}