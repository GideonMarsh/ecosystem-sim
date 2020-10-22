import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Simulation extends Canvas {
	
	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 400;

	private static Environment environment;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Ecosystem");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Canvas canvas = new Simulation();
		canvas.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
		
		environment = new Environment(WINDOW_WIDTH / 10, WINDOW_HEIGHT / 10);
		environment.generateGroundTypes();
		
		/*
		for (int i = 0; i < 3; i++) {organisms.add(new Organism(1));}
		for (int i = 0; i < 10; i++) {organisms.add(new Organism(2));}
		*/
		
		environment.addOrganism(new Organism(2), new Position(33,22));

		try {
			while (true) {
				environment.progressTime();
				canvas.repaint();
				Thread.sleep(300);
			}
		}
		catch (InterruptedException e) {
			System.out.println("Simulation interrupted");
		}
	}
	
	public void paint(Graphics g) {
		Color[][] colorArray = environment.getColors();
		
		for (int i = 0; i < colorArray.length; i++) {
			for (int j = 0; j < colorArray[i].length; j++) {
				g.setColor(colorArray[i][j]);
				g.fillRect(j * 10, i * 10, 10, 10);
			}
		}
	}
}
