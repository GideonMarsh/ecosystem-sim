/*
 * Environment class follows Singleton design pattern - only one instance of
 * environment can exist, and it can only be referred to using getter method
 */
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public class Environment {
	
	public static final int YEAR_LENGTH = 20;		// number of ticks in a year
	public static final int SUN_BRIGHTNESS = 100; 	// for nutrition gain from photosynthesis
	private static Environment e;
	
	private class Tile {
		private final Color[] groundColors = {Color.white, new Color(200,255,200), new Color(200,200,255)};
		
		private int groundType;
		private Organism plantLayer;
		private Organism animalLayer;
		
		public Tile(int groundType) {
			this.groundType = groundType;
		}
		
		public void setGroundType(int newType) {
			groundType = newType;
		}
		
		public int getGroundType() {
			return groundType;
		}
		
		public Organism getOccupant(int layer) {
			switch (layer) {
			case 1: return plantLayer;
			case 2: return animalLayer;
			}
			return null;
		}
		
		// returns true if successful and false if tile is currently occupied by same organism type
		public boolean setOccupant(Organism newOccupant) {
			if (newOccupant.isAPlant()) {
				if (plantLayer != null) return false;
				plantLayer = newOccupant;
				return true;
			}
			else {
				if (animalLayer != null) return false;
				animalLayer = newOccupant;
				return true;
			}
			
		}
		
		public void removeOccupant(int layer) {
			switch (layer) {
			case 1: plantLayer = null;
				break;
			case 2: animalLayer = null;
				break;
			}
		}
		
		public boolean isOccupied(int layer) {
			switch (layer) {
			case 1: return plantLayer != null;
			case 2: return animalLayer != null;
			}
			return false;
		}
		
		public Color getTileColor() {
			if (animalLayer != null) return animalLayer.getColor();
			if (plantLayer != null) return plantLayer.getColor();
			return groundColors[groundType];
		}
	}
	
	private int EnvXSize;
	private int EnvYSize;
	
	private Tile[][] environment;
	private ArrayList<Organism> organisms;
	private ArrayList<Organism> organismsToAdd;
	
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
		EnvXSize = xSize;
		EnvYSize = ySize;
	}
	
	public void generateGroundTypes() {
		for (int i = 0; i < environment.length; i++) {
			for (int j = 0; j < environment[i].length; j++) {
				if (i - j > 18) environment[i][j].setGroundType(2);
			}
		}
	}
	
	public ArrayList<Organism> getOrganisms() {
		return organisms;
	}
	
	public boolean addOrganism(Organism o, Position p) {
		int layer;
		if (o.isAPlant()) layer = 1;
		else layer = 2;
		
		if (p.xPosition >= EnvXSize || p.yPosition >= EnvYSize || p.xPosition < 0 || p.yPosition < 0) return false;
		if (environment[p.yPosition][p.xPosition].isOccupied(layer)) return false;
		environment[p.yPosition][p.xPosition].setOccupant(o);
		o.setPosition(p);
		organismsToAdd.add(o);
		return true;
	}
	
	public boolean moveOrganism(Organism o, Position p) {
		int layer;
		if (o.isAPlant()) layer = 1;
		else layer = 2;
		
		if (p.xPosition >= EnvXSize || p.yPosition >= EnvYSize || p.xPosition < 0 || p.yPosition < 0) return false;
		if (environment[p.yPosition][p.xPosition].isOccupied(layer)) return false;
		environment[p.yPosition][p.xPosition].setOccupant(o);
		environment[o.getPosition().yPosition][o.getPosition().xPosition].removeOccupant(layer);
		o.setPosition(p);
		return true;
	}
	
	// remove method is private to avoid ConcurrentModificationException
	private void removeOrganism(Organism o) {
		int layer;
		if (o.isAPlant()) layer = 1;
		else layer = 2;
		
		environment[o.getPosition().yPosition][o.getPosition().xPosition].removeOccupant(layer);
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
	}
	
	// returns a 2D array of colors for drawing
	public Color[][] getColors() {
		Color[][] colorArray = new Color[EnvYSize][EnvXSize];
		for (int i = 0; i < EnvYSize; i++) {
			for (int j = 0; j < EnvXSize; j++) {
				colorArray[i][j] = environment[i][j].getTileColor();
			}
		}
		return colorArray;
	}
}
