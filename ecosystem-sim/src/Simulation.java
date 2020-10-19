import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;

public class Simulation extends Canvas {
	
	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 400;
	
	private static int xPos = 0;
	private static int yPos = 0;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Ecosystem");
		Canvas canvas = new Simulation();
		canvas.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void paint(Graphics g) {
		g.setColor(new Color(230,255,230));
		g.fillRect(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
		drawOrganism(g,xPos,yPos,new Color(0,100,0));
	}
	
	private void drawOrganism(Graphics g, int xPos, int yPos, Color c) {
		g.setColor(c);
		g.fillRect(xPos * 10, yPos * 10, 10, 10);
	}

}
