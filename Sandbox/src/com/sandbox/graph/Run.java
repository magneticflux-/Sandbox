package com.sandbox.graph;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Run
{
	public static void main(final String[] args)
	{
		final JFrame frame = new JFrame("3D");
		final Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		final SimpleUniverse su = new SimpleUniverse(canvas);
		final BranchGroup contents = new BranchGroup();
		contents.addChild(new ColorCube(0.3));
		su.getViewingPlatform().setNominalViewingTransform();
		su.addBranchGraph(contents);

		frame.add(canvas);

		frame.add(new JPanel()
		{
			private static final long	serialVersionUID	= 1L;
			{
			}
		});

		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
