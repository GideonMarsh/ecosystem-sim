import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import java.awt.Dimension;

/*
 * Display can be moved with the arrow keys, zoomed in and out with + and -, and reset with space
 * 
 */
public class Simulation {
	private static final int WINDOW_WIDTH = 600;
	private static final int WINDOW_HEIGHT = 600;
	private static final int SQUARE_SIZE = 5;
	
	private static final int ENVIRONMENT_SIZE = 300;
	
	private static final int SIMULATION_TICK = 10;

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });	
	}
	
	private static void createAndShowGUI() {
		////////Initial environment conditions////////
		Environment.makeEnvironment(ENVIRONMENT_SIZE, ENVIRONMENT_SIZE);
		
		/*for (int i = 0; i < 1; i++) {
			while (! Environment.getEnvironment().addOrganism(new Organism(2), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
		}
		/*for (int i = 0; i < 1; i++) {
			while (! Environment.getEnvironment().addOrganism(new Organism(3), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
		}*/
		for (int i = 0; i < 20; i++) {
			while (! Environment.getEnvironment().addOrganism(new Organism(1), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
		}
		for (int i = 0; i < 10; i++) {
			while (! Environment.getEnvironment().addOrganism(new Organism(4), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
		}
		/////End of initial environment conditions///// 
		JFrame f = new JFrame("Ecosystem");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new EnvironmentPanel(Environment.getEnvironment(), WINDOW_WIDTH, WINDOW_HEIGHT, SQUARE_SIZE));//, f));
        f.pack();
        f.setResizable(false);
        f.setVisible(true);
        f.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			
			public void windowClosing(WindowEvent e) {
				System.out.println("Simulation ended");
			}
        });
        
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
	
	private int screenx, screeny;
	
	public EnvironmentPanel(Environment e, int width, int height, int size) {//, JFrame jf) {
		environment = e;
		windowWidth = width;
		windowHeight = height;
		squareSize = size;
		
		screenx = screeny = 0;
		
		this.getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
		this.getActionMap().put("up", new upAction());
		
		this.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
		this.getActionMap().put("down", new downAction());
		
		this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
		this.getActionMap().put("left", new leftAction());
		
		this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
		this.getActionMap().put("right", new rightAction());
		
		this.getInputMap().put(KeyStroke.getKeyStroke("EQUALS"), "plus");
		this.getActionMap().put("plus", new plusAction());
		
		this.getInputMap().put(KeyStroke.getKeyStroke("MINUS"), "minus");
		this.getActionMap().put("minus", new minusAction());
		
		this.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "space");
		this.getActionMap().put("space", new spaceAction());
		
		/*
		addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                environment.progressTime();
                jf.repaint();
            }
        });*/
        
	}
	
	class upAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			screeny++;
		}
	}
	
	class downAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			screeny--;
		}
	}
	
	class leftAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			screenx++;
		}
	}
	
	class rightAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			screenx--;
		}
	}
	
	class plusAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			squareSize = Math.min(windowHeight / 10, squareSize + 1);
		}
	}
	
	class minusAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			squareSize = Math.max(1, squareSize - 1);
		}
	}
	
	class spaceAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			screenx = 0;
			screeny = 0;
		}
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
				g.fillRect((j + screenx) * squareSize, (i + screeny) * squareSize, squareSize, squareSize);
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
		//if (Environment.getEnvironment().getWorldAge() % 1 == 0) System.out.println(Environment.getEnvironment().getWorldAge());
		if (Environment.getEnvironment().getWorldAge() == 50) {
			for (int i = 0; i < 3; i++) {
				while (! Environment.getEnvironment().addOrganism(new Organism(2), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
			}
		}
		if (Environment.getEnvironment().getWorldAge() == 90) {
			for (int i = 0; i < 3; i++) {
				while (! Environment.getEnvironment().addOrganism(new Organism(3), new Position((int) Math.round(Math.random() * (Environment.getEnvironment().getWorldWidth() - 1)),(int) Math.round(Math.random() * (Environment.getEnvironment().getWorldHeight() - 1)))));
			}		
		}
		jf.repaint();
	}
	
}
