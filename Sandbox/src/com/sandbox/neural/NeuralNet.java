package com.sandbox.neural;

public abstract class NeuralNet
{
	private boolean	globalValidateForwardProp	= true;
	private boolean	globalValidateBackProp		= true;

	public boolean getGlobalValidateForwardProp()
	{
		return this.globalValidateForwardProp;
	}

	public void invalidateForwardProp()
	{
		this.globalValidateForwardProp = !this.globalValidateForwardProp;
	}

	public boolean getGlobalValidateBackProp()
	{
		return this.globalValidateBackProp;
	}

	public void invalidateBackProp()
	{
		this.globalValidateBackProp = !this.globalValidateBackProp;
	}
}
