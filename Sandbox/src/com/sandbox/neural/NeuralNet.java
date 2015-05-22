package com.sandbox.neural;

public abstract class NeuralNet
{
	private boolean	globalValidate	= true;

	public boolean getGlobalValidate()
	{
		return this.globalValidate;
	}

	public void invertGlobalValidate()
	{
		this.globalValidate = !this.globalValidate;
	}
}
