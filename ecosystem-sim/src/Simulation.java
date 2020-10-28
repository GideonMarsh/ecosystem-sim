import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;

public class Simulation {
	private static final int WINDOW_WIDTH = 500;
	private static final int WINDOW_HEIGHT = 500;
	private static final int SQUARE_SIZE = 5;
	
	private static final int SIMULATION_TICK = 100;
	
	public static long[] execTimes;
	public static long lastTime;
	
	public static void benchmarkTime(int which) {
		execTimes[which] += System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
	}
	
	public static void printTimes() {
		double totalTime, totalTimeNoOther;
		totalTime = totalTimeNoOther = 0;
		for (int i = 0; i < execTimes.length; i++) {
			if (i != 0) totalTimeNoOther += execTimes[i];
			totalTime += execTimes[i];
		}
		System.out.println("Other: " + execTimes[0] + " " + execTimes[0] / totalTime);
		System.out.println("growOlder: " + execTimes[1] + " " + execTimes[1] / totalTimeNoOther + " " + execTimes[1] / totalTime);
		System.out.println("behavior choice: " + execTimes[2] + " " + execTimes[2] / totalTimeNoOther + " " + execTimes[2] / totalTime);
		System.out.println("perceive: " + execTimes[3] + " " + execTimes[3] / totalTimeNoOther + " " + execTimes[3] / totalTime);
		System.out.println("move: " + execTimes[4] + " " + execTimes[4] / totalTimeNoOther + " " + execTimes[4] / totalTime);
		System.out.println("eat: " + execTimes[5] + " " + execTimes[5] / totalTimeNoOther + " " + execTimes[5] / totalTime);
		System.out.println("reproduce: " + execTimes[6] + " " + execTimes[6] / totalTimeNoOther + " " + execTimes[6] / totalTime);
		System.out.println("repaint: " + execTimes[7] + " " + execTimes[7] / totalTimeNoOther + " " + execTimes[7] / totalTime);
	}

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });	
	}
	
	private static void createAndShowGUI() {
		execTimes = new long[8];
		////////Initial environment conditions////////
		Environment.makeEnvironment(WINDOW_WIDTH / SQUARE_SIZE, WINDOW_HEIGHT / SQUARE_SIZE);
		Environment.getEnvironment().generateGroundTypes();
		/*
		for (int i = 0; i < 5; i++) {
			while (! Environment.getEnvironment().addOrganism(new Organism(2), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
		}
		for (int i = 0; i < 1; i++) {
			while (! Environment.getEnvironment().addOrganism(new Organism(3), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
		}*/
		for (int i = 0; i < 20; i++) {
			while (! Environment.getEnvironment().addOrganism(new Organism(1), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
		}
		for (int i = 0; i < 5; i++) {
			while (! Environment.getEnvironment().addOrganism(new Organism(4), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
		}
		/////End of initial environment conditions///// 
		
		JFrame f = new JFrame("Ecosystem");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new EnvironmentPanel(Environment.getEnvironment(), WINDOW_WIDTH, WINDOW_HEIGHT, SQUARE_SIZE));//, f));
        f.pack();
        f.setVisible(true);
        
        benchmarkTime(0);
        RepaintTask rt = new RepaintTask(f);
        Timer t = new Timer();
        t.schedule(rt, 500, SIMULATION_TICK);
	}
}

class EnvironmentPanel extends JPanel {
	
	private Environment environment;
	private int windowWidth;
	private int windowHeight;
	private int squareSize;
	
	public EnvironmentPanel(Environment e, int width, int height, int size) {//, JFrame jf) {
		environment = e;
		windowWidth = width;
		windowHeight = height;
		squareSize = size;
		
		/*
		addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                environment.progressTime();
                jf.repaint();
            }
        });*/
        
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
				g.fillRect(j * squareSize, i * squareSize, squareSize, squareSize);
			}
		}
	}
}

class RepaintTask extends TimerTask {
	
	private JFrame jf;

	public RepaintTask(JFrame f) {
		jf = f;
	}
	
	public void run() {
		Environment.getEnvironment().progressTime();
		if (Environment.getEnvironment().getWorldAge() % 1 == 0) System.out.println(Environment.getEnvironment().getWorldAge());
		if (Environment.getEnvironment().getWorldAge() == 20) {
			for (int i = 0; i < 5; i++) {
				while (! Environment.getEnvironment().addOrganism(new Organism(2), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
			}
		}
		if (Environment.getEnvironment().getWorldAge() == 30) {
			for (int i = 0; i < 1; i++) {
				while (! Environment.getEnvironment().addOrganism(new Organism(3), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
			}		
		}
		Simulation.benchmarkTime(0);
		jf.repaint();
		Simulation.benchmarkTime(7);
		if (Environment.getEnvironment().getWorldAge() == 50) {
			Simulation.printTimes();
		}
	}
	
}
