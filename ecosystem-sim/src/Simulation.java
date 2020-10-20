import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Simulation extends Canvas {
	
	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 400;

	private static ArrayList<Organism> organisms = new ArrayList<Organism>();

	public static void main(String[] args) {
		JFrame frame = new JFrame("Ecosystem");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Canvas canvas = new Simulation();
		canvas.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
		
		organisms.add(new Organism());
		try {
			while (true) {
				for (Organism organism : organisms) {
					organism.move(new Position(33,22));
				}
				canvas.repaint();
				Thread.sleep(100);
			}
		}
		catch (InterruptedException e) {
			System.out.println("Simulation interrupted");
		}
	}
	
	public void paint(Graphics g) {
		g.setColor(new Color(230,255,230));
		g.fillRect(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
		
		for (Organism organism : organisms) {
			drawOrganism(g,organism, new Color(0,100,0));
		}
	}
	
	private void drawOrganism(Graphics g, Organism o, Color c) {
		if (o.equals(null)) return;
		g.setColor(c);
		g.fillRect(o.getPosition().getXPosition() * 10, o.getPosition().getYPosition() * 10, 10, 10);
	}
}
