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
	
	/*
	 * Movement types are as follows:
	 * 0 = land movement, 1 = water movement, 2 = air movement
	 * 
	 * Tile movement modifiers are offset by 1 from movement types
	 * 
	 * Moving onto tiles that contain a large creature has an additional movement penalty
	 * This is stored as movement modifier 0
	 * 
	 * Large creatures may also be subject to an additional movement penalty
	 * 
	 * Water value determines how easily plants can thrive
	 */
	
	public static final int MOVEMENT_TYPES = 3;

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
		
		private double[] speedModifiers;
		private double[] largeSpeedModifiers;
		private double waterValue;
		private Organism[] layers;
		private Color color;
		
		// instantiation of Tile parameters handled by Environment.generateGroundTypes()
		public Tile() {
			speedModifiers = new double[MOVEMENT_TYPES + 1];
			largeSpeedModifiers = new double[MOVEMENT_TYPES + 1];
			layers = new Organism[4];
		}
		
		public void setWaterVal(double newValue) {
			waterValue = newValue;
		}
		
		public void setSpeedModifiers(double[] newMods) {
			for (int i = 0; i < speedModifiers.length; i++) {
				speedModifiers[i] = newMods[i];
			}
		}
		
		public void setLargeSpeedModifiers(double[] newMods) {
			for (int i = 0; i < largeSpeedModifiers.length; i++) {
				largeSpeedModifiers[i] = newMods[i];
			}
		}
		
		public void setColor(Color newColor) {
			color = newColor;
		}
		
		public boolean hasLargeOccupant() {
			for (int i = 0; i < layers.length; i++) {
				if (layers[i] != null && layers[i].isLarge()) return true;
			}
			return false;
		}
		
		public Organism getOccupant(int layer) {
			return layers[layer];
		}
		
		// returns true if successful and false if tile is currently occupied by same organism type
		public boolean setOccupant(Organism newOccupant) {
			int layer = findLayer(newOccupant);
		
			if (layers[layer] != null) return false;
			layers[layer] = newOccupant;
			return true;
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
		
		public boolean isOccupied(int layer) {
			return layers[layer] != null;
		}
		
		public Color getTileColor() {
			for (int i = layers.length - 1; i >= 0; i--) {
				if (layers[i] != null) return layers[i].getColor();
			}
			return color;
		}
		
		public double getMovementModifier(int type, boolean isLarge) {
			double mod = 0.0;
			mod = speedModifiers[type + 1];
			if (isLarge) mod *= largeSpeedModifiers[type + 1];
			if (hasLargeOccupant()) {
				mod *= speedModifiers[0];
				if (isLarge) mod *= largeSpeedModifiers[0];
			}
			return mod;
		}
	}
	
	private int envXSize;
	private int envYSize;
	
	private Tile[][] environment;
	private OrganismList organisms;
	
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
				environment[i][j] = new Tile();
			}
		}
		organisms = new OrganismList();
		envXSize = xSize;
		envYSize = ySize;
		worldAge = 0;
		
		generateGroundTypes();
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
	
	private void generateGroundTypes() {
		double[] landMovement       = {0.50, 1.00, 0.00, 1.00};
		double[] waterMovement      = {0.50, 0.00, 1.00, 1.00};
		double[] largeMovement      = {0.00, 1.00, 1.00, 1.00};
		double[] coastMovement      = {0.50, 0.34, 1.00, 1.00};
		double[] coastLargeMovement = {0.00, 1.00, 0.00, 1.00};
		double aquaticWaterVal = 1.0;
		double landWaterStartingVal = 1.24;
		Color coastColor = new Color(150,150,255);
		Color deepWaterColor = new Color(100,100,200);
		Color goodLandColor = new Color(165,130,60);
		for (int i = 0; i < environment.length; i++) {
			for (int j = 0; j < environment[i].length; j++) {
				if (j < 15) {
					environment[i][j].setSpeedModifiers(waterMovement);
					environment[i][j].setLargeSpeedModifiers(largeMovement);
					environment[i][j].setWaterVal(aquaticWaterVal);
					environment[i][j].setColor(deepWaterColor);
				}
				if (j >= 15 && j < 20) {
					environment[i][j].setSpeedModifiers(coastMovement);
					environment[i][j].setLargeSpeedModifiers(coastLargeMovement);
					environment[i][j].setWaterVal(aquaticWaterVal);
					environment[i][j].setColor(coastColor);
				}
				if (j >= 20) {
					environment[i][j].setSpeedModifiers(landMovement);
					environment[i][j].setLargeSpeedModifiers(largeMovement);
					double w = Math.max(landWaterStartingVal - (0.005 * (j - 20)), 0);
					environment[i][j].setWaterVal(w);
					int r = Math.min((int) Math.round(goodLandColor.getRed() + (85 * (1 - w))), 255);
					int g = Math.min((int) Math.round(goodLandColor.getGreen() + (120 * (1 - w))), 255);
					int b = Math.min((int) Math.round(goodLandColor.getBlue() + (60 * (1 - w))), 255);
					
					environment[i][j].setColor(new Color(r,g,b));
				}
			}
		}
		
	}
	
	public OrganismList getOrganisms() {
		return organisms;
	}
	
	// determines the highest possible movement speed onto a given tile
	// if it returns 0, the organism cannot move onto that tile
	private double findHighestMovement(Organism o, Position p) {
		double highestMovement, d;
		highestMovement = d = 0.0;
		for (int i = 0; i < MOVEMENT_TYPES; i++) {
			d = o.getWalkingSpeed(i) * environment[p.yPosition][p.xPosition].getMovementModifier(i, o.isLarge());
			if (d > highestMovement) highestMovement = d;
		}
		return highestMovement;
	}
	
	// returns false if position p is surrounded by tiles that are inaccessible to organism o
	public boolean canReach(Organism o, Position p) {
		if (p.xPosition + 1 < envXSize && findHighestMovement(o,new Position(p.xPosition + 1, p.yPosition)) > 0) return true;
		if (p.xPosition - 1 >= 0 && findHighestMovement(o,new Position(p.xPosition - 1, p.yPosition)) > 0) return true;
		if (p.yPosition + 1 < envXSize && findHighestMovement(o,new Position(p.xPosition, p.yPosition + 1)) > 0) return true;
		if (p.yPosition - 1 >= 0 && findHighestMovement(o,new Position(p.xPosition, p.yPosition - 1)) > 0) return true;
		return false;
	}
	
	public boolean addOrganism(Organism o, Position p) {
		if (p.xPosition >= envXSize || p.yPosition >= envYSize || p.xPosition < 0 || p.yPosition < 0) return false;
		if (environment[p.yPosition][p.xPosition].isOccupied(findLayer(o))) return false;
		
		double highestMovement = findHighestMovement(o,p);
		if (highestMovement == 0) return false;
		
		environment[p.yPosition][p.xPosition].setOccupant(o);
		o.setPosition(p);
		organisms.add(o);
		return true;
	}
	
	public boolean moveOrganism(Organism o, Position p) {
		if (p.xPosition >= envXSize || p.yPosition >= envYSize || p.xPosition < 0 || p.yPosition < 0) return false;
		if (environment[p.yPosition][p.xPosition].isOccupied(findLayer(o))) return false;
		
		double highestMovement = findHighestMovement(o,p);
		if (highestMovement == 0) return false;
		
		if (o.getRemainingMovement() > (int) highestMovement) o.setRemainingMovement((int) highestMovement); 
		
		if (highestMovement + o.getMovementPoints() < 1) {
			o.setMovementPoints(o.getMovementPoints() + highestMovement);
		}
		else {
			o.setMovementPoints(0);
			environment[p.yPosition][p.xPosition].setOccupant(o);
			environment[o.getPosition().yPosition][o.getPosition().xPosition].removeOccupant(findLayer(o));
			o.setPosition(p);
		}
		if (highestMovement >= 1) o.setMovementPoints(0);
		return true;
	}
	
	public void moveToCorpseLayer(Organism o) {
		// if tile conflict, existing corpse is always replaced by new corpse
		if (environment[o.getPosition().yPosition][o.getPosition().xPosition].isOccupied(1)) {
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
	
	private void removeOrganism(Organism o) {
		environment[o.getPosition().yPosition][o.getPosition().xPosition].removeOccupant(findLayer(o));
	}
	
	public OrganismList resolvePerception(Organism o) {
		return organisms.copy();
	}
	
	public void progressTime() {
		organisms.startIteration();
		while(! organisms.endOfList()) {
			organisms.getCurrentOrganism().nextAction();
			if (organisms.getCurrentOrganism().isMarkedForRemoval()) {
				removeOrganism(organisms.getCurrentOrganism());
				organisms.removeCurrent();
			}
			else {organisms.next();}
		}
		worldAge = Math.round((worldAge * YEAR_LENGTH) + 1) / YEAR_LENGTH;
	}
	
	public double getWaterValue(Position p) {
		return environment[p.yPosition][p.xPosition].waterValue;
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
