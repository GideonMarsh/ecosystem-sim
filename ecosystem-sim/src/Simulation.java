import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;

public class Simulation {
	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 400;
	
	private static final int SIMULATION_TICK = 100;
	
	public static Environment environment;

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });	
	}
	
	private static void createAndShowGUI() {
		////////Initial environment conditions////////
		environment = new Environment(WINDOW_WIDTH / 10, WINDOW_HEIGHT / 10);
		environment.generateGroundTypes();
		
		for (int i = 0; i < 5; i++) {
			environment.addOrganism(new Organism(2), new Position((int) Math.round(Math.random() * 39),(int) Math.round(Math.random() * 39)));
		}
		for (int i = 0; i < 3; i++) {
			environment.addOrganism(new Organism(1), new Position((int) Math.round(Math.random() * 39),(int) Math.round(Math.random() * 39)));
		}
		/////End of initial environment conditions///// 
		
		JFrame f = new JFrame("Ecosystem");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new EnvironmentPanel(environment, WINDOW_WIDTH, WINDOW_HEIGHT));
        f.pack();
        f.setVisible(true);
        
        RepaintTask rt = new RepaintTask(environment,f);
        Timer t = new Timer();
        t.schedule(rt, 500, SIMULATION_TICK);
	}
}

class EnvironmentPanel extends JPanel {
	
	private Environment environment;
	private int windowWidth;
	private int windowHeight;
	
	public EnvironmentPanel(Environment e, int width, int height) {
		environment = e;
		windowWidth = width;
		windowHeight = height;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(windowWidth,windowHeight);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Color[][] colorArray = environment.getColors();
		
		for (int i = 0; i < colorArray.length; i++) {
			for (int j = 0; j < colorArray[i].length; j++) {
				g.setColor(colorArray[i][j]);
				g.fillRect(j * 10, i * 10, 10, 10);
			}
		}
	}
}

class RepaintTask extends TimerTask {
	
	private Environment env;
	private JFrame jf;

	public RepaintTask(Environment e, JFrame f) {
		env = e;
		jf = f;
	}
	
	public void run() {
		env.progressTime();
		jf.repaint();
	}
	
}
