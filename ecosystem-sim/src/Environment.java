/*
 * Environment class follows Singleton design pattern - only one instance of
 * environment can exist, and it can only be referred to using getter method
 */
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public class Environment {
	
	public static final double YEAR_LENGTH = 30.0;		// number of ticks in a year
	private static Environment e;
	
	// returns the base layer of the organism
	public static int findLayer(Organism o) {
		if (o.getFoodChainIdentifier() == 0) return 0;
		else {
			if (o.isACorpse()) return 1;
			else {
				if (o.getFoodChainIdentifier() == 1) return 2;
				else return 3;
			}
		}
	}
	
	private class Tile {
		private final Color[] groundColors = {Color.white, new Color(200,255,200), new Color(200,200,255)};
		
		private int groundType;
		private Organism[] layers;
		
		public Tile(int groundType) {
			this.groundType = groundType;
			layers = new Organism[4];
		}
		
		public void setGroundType(int newType) {
			groundType = newType;
		}
		
		public int getGroundType() {
			return groundType;
		}
		
		public Organism getOccupant(int layer) {
			return layers[layer];
		}
		
		// returns true if successful and false if tile is currently occupied by same organism type
		public boolean setOccupant(Organism newOccupant) {
			int layer = findLayer(newOccupant);
			
			// large organisms take up their layer and all layers above them
			if (newOccupant.isLarge()) {
				for (int i = layer; i < layers.length; i++) {
					if (layers[i] != null) return false;
				}
				for (int i = layer; i < layers.length; i++) {
					layers[i] = newOccupant;
				}
				return true;
			}
			else {
				if (layers[layer] != null) return false;
				layers[layer] = newOccupant;
				return true;
			}
		}
		
		public void removeOccupant(int layer) {
			if (layers[layer] == null) return;
			if (layers[layer].isLarge()) {
				for (int i = layer; i < layers.length; i++) {
					layers[i] = null;
				}
			}
			else {
				layers[layer] = null;
			}
		}
		
		public void removeOccupant(Organism o) {
			for (int i = 0; i < layers.length; i++) {
				if (layers[i] != null && layers[i].equals(o)) layers[i] = null;
			}
		}
		
		public boolean isOccupied(int layer, boolean isLarge) {
			if (isLarge) {
				for (int i = layer; i < layers.length; i++) {
					if (layers[i] != null) return true;
				}
				return false;
			}
			else {
				return layers[layer] != null;
			}
		}
		
		public Color getTileColor() {
			for (int i = layers.length - 1; i >= 0; i--) {
				if (layers[i] != null) return layers[i].getColor();
			}
			return groundColors[groundType];
		}
	}
	
	private int envXSize;
	private int envYSize;
	
	private Tile[][] environment;
	private ArrayList<Organism> organisms;
	private ArrayList<Organism> organismsToAdd;
	
	private double worldAge;
	
	public static void makeEnvironment(int xSize, int ySize) {
		e = new Environment(xSize, ySize);
	}
	
	public static Environment getEnvironment() {
		return e;
	}
	
	private Environment(int xSize, int ySize) {
		environment = new Tile[ySize][xSize];
		for (int i = 0; i < ySize; i++) {
			for (int j = 0; j < xSize; j++) {
				environment[i][j] = new Tile(1);
			}
		}
		organisms = new ArrayList<Organism>();
		organismsToAdd = new ArrayList<Organism>();
		envXSize = xSize;
		envYSize = ySize;
		worldAge = 0;
	}
	
	public double getWorldAge() {
		return worldAge;
	}
	
	public int getWorldWidth() {
		return envXSize;
	}
	
	public int getWorldHeight() {
		return envYSize;
	}
	
	public void generateGroundTypes() {
		for (int i = 0; i < environment.length; i++) {
			for (int j = 0; j < environment[i].length; j++) {
				if (i - j > 18) environment[i][j].setGroundType(2);
			}
		}
	}
	
	public int getGroundType(Position p) {
		return environment[p.yPosition][p.xPosition].getGroundType();
	}
	
	public ArrayList<Organism> getOrganisms() {
		return organisms;
	}
	
	public boolean addOrganism(Organism o, Position p) {
		if (p.xPosition >= envXSize || p.yPosition >= envYSize || p.xPosition < 0 || p.yPosition < 0) return false;
		if (environment[p.yPosition][p.xPosition].isOccupied(findLayer(o), o.isLarge())) return false;
		environment[p.yPosition][p.xPosition].setOccupant(o);
		o.setPosition(p);
		organismsToAdd.add(o);
		return true;
	}
	
	public boolean moveOrganism(Organism o, Position p) {
		if (p.xPosition >= envXSize || p.yPosition >= envYSize || p.xPosition < 0 || p.yPosition < 0) return false;
		if (environment[p.yPosition][p.xPosition].isOccupied(findLayer(o), o.isLarge())) return false;
		environment[p.yPosition][p.xPosition].setOccupant(o);
		environment[o.getPosition().yPosition][o.getPosition().xPosition].removeOccupant(findLayer(o));
		o.setPosition(p);
		return true;
	}
	
	public void moveToCorpseLayer(Organism o) {
		// if tile conflict, existing corpse is always replaced by new corpse
		if (environment[o.getPosition().yPosition][o.getPosition().xPosition].isOccupied(1, false)) {
			Organism oldCorpse = environment[o.getPosition().yPosition][o.getPosition().xPosition].getOccupant(1);
			environment[o.getPosition().yPosition][o.getPosition().xPosition].removeOccupant(oldCorpse);
			environment[o.getPosition().yPosition][o.getPosition().xPosition].removeOccupant(o);
			environment[o.getPosition().yPosition][o.getPosition().xPosition].setOccupant(o);
			oldCorpse.setAsMarkedForRemoval();
		}
		else {
			environment[o.getPosition().yPosition][o.getPosition().xPosition].removeOccupant(o);
			environment[o.getPosition().yPosition][o.getPosition().xPosition].setOccupant(o);
		}
	}
	
	// remove method is private to avoid ConcurrentModificationException
	private void removeOrganism(Organism o) {
		environment[o.getPosition().yPosition][o.getPosition().xPosition].removeOccupant(findLayer(o));
	}
	
	public ArrayList<Organism> resolvePerception(Organism o) {
		return organisms;
	}
	
	public void progressTime() {
		Iterator<Organism> i = organisms.iterator();
		while (i.hasNext()) {
			Organism o = i.next();
			o.nextAction();
			if (o.isMarkedForRemoval()) {
				i.remove();
				removeOrganism(o);
			}
		}
		for (Organism organism : organismsToAdd) {
			organisms.add(organism);
		}
		organismsToAdd.clear();
		worldAge  = Math.round((worldAge * YEAR_LENGTH) + 1) / YEAR_LENGTH;
	}
	
	// returns a 2D array of colors for drawing
	public Color[][] getColors() {
		Color[][] colorArray = new Color[envYSize][envXSize];
		for (int i = 0; i < envYSize; i++) {
			for (int j = 0; j < envXSize; j++) {
				colorArray[i][j] = environment[i][j].getTileColor();
			}
		}
		return colorArray;
	}
}
