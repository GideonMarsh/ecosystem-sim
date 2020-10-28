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
	 * Ground types are as follows:
	 * 0 - special ground type for sharing a space with a large organism
	 * 1 - irrigated land
	 * 2 - dry land
	 * 3 - desert
	 * 4 - shallow water
	 * 5 - deep water
	 * 
	 * Large creatures are subject to an additional penalty for certain ground types
	 */
	private static final Color[] GROUND_COLORS = {Color.white, new Color(165,130,60), 
			new Color(210,190,90), new Color(255,250,120), new Color(150,150,255), 
			new Color(100,100,200)};
	
	public static final int MOVEMENT_TYPES = 3;
	private static final double[] LAND_SPEED_MODIFIER  = {0.50, 1.00, 1.00, 1.00, 0.50, 0.00};
	private static final double[] WATER_SPEED_MODIFIER = {0.50, 0.00, 0.00, 0.00, 1.00, 1.00};
	private static final double[] AIR_SPEED_MODIFIER   = {1.00, 1.00, 1.00, 1.00, 1.00, 1.00};
	private static final double[] LARGE_SPEED_MODIFIER = {0.00, 1.00, 1.00, 1.00, 0.34, 1.00};
	
	// water value determines how easily plants can thrive
	private static final double[] WATER_VALUES = {0.0, 1.2, 0.7, 0.2, 1.0, 1.0};

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
		
		private int groundType;
		private Organism[] layers;
		
		public Tile(int groundType) {
			this.groundType = groundType;
			layers = new Organism[4];
		}
		
		public void setGroundType(int newType) {
			groundType = newType;
		}
		
		public boolean hasLargeOccupant() {
			for (int i = 0; i < layers.length; i++) {
				if (layers[i] != null && layers[i].isLarge()) return true;
			}
			return false;
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
			return GROUND_COLORS[groundType];
		}
		
		// 0 = land movement, 1 = water movement, 2 = air movement
		public double getMovementModifier(int type, boolean isLarge) {
			double mod;
			switch (type) {
			case 0: 
				mod = LAND_SPEED_MODIFIER[groundType];
				if (isLarge) {mod *= LARGE_SPEED_MODIFIER[groundType];}
				if (hasLargeOccupant()) {
					mod *= LAND_SPEED_MODIFIER[0];
					if (isLarge) {mod *= LARGE_SPEED_MODIFIER[0];}
				}
				return mod; 
			case 1:
				mod = WATER_SPEED_MODIFIER[groundType];
				if (isLarge) {mod *= LARGE_SPEED_MODIFIER[groundType];}
				if (hasLargeOccupant()) {
					mod *= WATER_SPEED_MODIFIER[0];
					if (isLarge) {mod *= LARGE_SPEED_MODIFIER[0];}
				}
				return mod; 
			case 2:
				mod = AIR_SPEED_MODIFIER[groundType];
				if (isLarge) {mod *= LARGE_SPEED_MODIFIER[groundType];}
				if (hasLargeOccupant()) {
					mod *= AIR_SPEED_MODIFIER[0];
					if (isLarge) {mod *= LARGE_SPEED_MODIFIER[0];}
				}
				return mod; 
			}
			return 0.0;
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
				environment[i][j] = new Tile(1);
			}
		}
		organisms = new OrganismList();
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
				if (j < 15) environment[i][j].setGroundType(5);
				if (j >= 15 && j < 20) environment[i][j].setGroundType(4);
				if (j > 40) environment[i][j].setGroundType(2);
				if (j > 80) environment[i][j].setGroundType(3);
			}
		}
	}
	
	public int getGroundType(Position p) {
		return environment[p.yPosition][p.xPosition].getGroundType();
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
	
	// remove method is private to avoid ConcurrentModificationException
	private void removeOrganism(Organism o) {
		environment[o.getPosition().yPosition][o.getPosition().xPosition].removeOccupant(findLayer(o));
	}
	
	public OrganismList resolvePerception(Organism o) {
		return organisms.copy();
	}
	
	public void progressTime() {
		/*Iterator<Organism> i = organisms.iterator();
		while (i.hasNext()) {
			Organism o = i.next();
			o.nextAction();
			if (o.isMarkedForRemoval()) {
				i.remove();
				removeOrganism(o);
			}
		}*/
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
		return WATER_VALUES[environment[p.yPosition][p.xPosition].getGroundType()];
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
