import java.awt.Color;
import java.util.ArrayList;

public class Organism {
	private Position position;
	private int walkingSpeed;
	
	private int organismType;
	private boolean isAPlant;
	private boolean isACorpse;
	private boolean isMarkedForRemoval;
	
	private int nutrition;
	private PreyValues preyValues;	// the value of eating certain prey
	
	// hp values represent relative size as well as health
	private int hp;
	private int maxhp;
	private int maxhpThreshold;		// maximum total hp when an adult
	
	private double age;
	private int maxAge;
	
	private Color color;
	
	private ArrayList<Organism> mentalMap;
	
	// New organisms are created from parent(s) parameters (genes)
	public Organism(Organism parent) {
		position = new Position(0,0);
		walkingSpeed = parent.walkingSpeed;
		mentalMap = new ArrayList<Organism>();
		organismType = parent.organismType;
		isAPlant = parent.isAPlant;
		color = parent.color;
		age = 0;
		maxAge = parent.maxAge;
		maxhpThreshold = parent.maxhpThreshold;
		maxhp = maxhpThreshold / 2;
		hp = maxhp;
		preyValues = parent.preyValues.copy();
	}
	
	// Creates a new organism from nothing of the specified type
	public Organism(int type) {
		position = new Position(0,0);
		mentalMap = new ArrayList<Organism>();
		age = 0;
		preyValues = new PreyValues();

		switch (type) {
		case 2:
			walkingSpeed = 1;
			organismType = type;
			isAPlant = false;
			color = new Color(0,0,200);
			maxAge = 20;
			maxhpThreshold = 200;
			maxhp = maxhpThreshold / 2;
			hp = maxhp;
			
			preyValues.addPreyValue(1, 1.0f);
			break;
			
		case 1:
		default:
			walkingSpeed = 0;
			organismType = type;
			isAPlant = true;
			color = new Color(0,150,0);
			maxAge = 30;
			maxhpThreshold = 500;
			maxhp = maxhpThreshold / 2;
			hp = maxhp;
			
			preyValues.addPreyValue(0, 1.0f);
		}
	}
	
	public int getOrganismType() {
		return organismType;
	}
	
	public void setOrganismType(int newType) {
		organismType = newType;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public void setPosition(Position newPosition){;
		position = newPosition;
	}
	
	public boolean isAPlant() {
		return isAPlant;
	}
	
	public boolean isMarkedForRemoval() {
		return isMarkedForRemoval;
	}
	
	// Attempts to lower the organism's hp by specified amount
	// If organism hp is less than amount, only remove available hp and kill organism
	// Returns actual hp loss of organism
	public int takeHP(int amount) {
		if (hp > amount) {
			hp -= amount;
			return amount;
		}
		else {
			int hpTaken = hp;
			die();
			return hpTaken;
		}
	}
	
	public void loseHP(int amount) {
		hp -= amount;
		if (hp <= 0) die();
	}
	
	// The AI that determines and executes Organism behavior
	// Behavior is split up among helper methods
	public void nextAction() {
		growOlder();
		if (! isACorpse) {
			perceive();
			move();
			if (isAPlant && Math.random() > 0.96) reproduce();
		}
	}
	
	private void move() {
		if (! isAPlant) {
			if (mentalMap == null || walkingSpeed == 0) return;
			
			Position destination = null;
			for (Organism organism : mentalMap) {
				if (organism.getOrganismType() == 1) {
					destination = position.closest(destination,organism.getPosition());
				}
			}
			
			if (destination == null) return;
			
			/*
			 * Organisms move cardinally one square at a time towards their destinations
			 * Their moves are chosen sequentially from the chosenMoves array
			 * If no valid moves are found, organism remains stationary
			 * If the organism is alinged with its destination on one axis, it will first try
			 * to move directly towards its destination, and if it fails it will attempt to
			 * move to the side.
			 * If the organism is not aligned with its destination on an axis, it will first
			 * randomly choose which axis to move along and then try to move along that axis
			 * first and the other axis second; if neither work, then it tries the opposite
			 * direction on the non-chosen axis
			 */
			int xDif, yDif, rand;
			Position[] chosenMoves = new Position[3];
			
			rand = Math.round(Math.random()) == 0 ? 1 : -1;
			
			for (int i = 0; i < walkingSpeed; i++) {
				if (position.sameAs(destination)) break;
				
				xDif = destination.xPosition - position.xPosition;
				yDif = destination.yPosition - position.yPosition;
				
				if (xDif == 0) {
					chosenMoves[0] = new Position(position.xPosition, position.yPosition + Integer.signum(yDif));
					chosenMoves[1] = new Position(position.xPosition + rand, position.yPosition);
					chosenMoves[2] = new Position(position.xPosition - rand, position.yPosition);
				}
				if (yDif == 0) {
					chosenMoves[0] = new Position(position.xPosition + Integer.signum(xDif), position.yPosition);
					chosenMoves[1] = new Position(position.xPosition, position.yPosition + rand);
					chosenMoves[2] = new Position(position.xPosition, position.yPosition - rand);
				}
				if (xDif != 0 && yDif != 0) {
					if (rand == 1) {
						chosenMoves[0] = new Position(position.xPosition, position.yPosition + Integer.signum(yDif));
						chosenMoves[1] = new Position(position.xPosition + Integer.signum(xDif), position.yPosition);
						chosenMoves[2] = new Position(position.xPosition - Integer.signum(xDif), position.yPosition);
					}
					else {
						chosenMoves[0] = new Position(position.xPosition + Integer.signum(xDif), position.yPosition);
						chosenMoves[1] = new Position(position.xPosition, position.yPosition + Integer.signum(yDif));
						chosenMoves[2] = new Position(position.xPosition, position.yPosition - Integer.signum(yDif));
					}
				}
				
				for (int j = 0; j < chosenMoves.length; j++) {
					if (Environment.getEnvironment().moveOrganism(this, chosenMoves[j])) break;
				}
			}
		}
		else {
			if (mentalMap == null) return;
			for (Organism organism : mentalMap) {
				if (! organism.equals(this) && organism.getPosition().sameAs(position)) {
					/*Position newPosition;
					do {
						newPosition = new Position((int) Math.round(Math.random() * 49),(int) Math.round(Math.random() * 49));
					}
					while (! Environment.getEnvironment().moveOrganism(this, newPosition));
					break;*/
					die();
				}
			}
		}
	}
	
	private void die() {
		if (isAPlant || isACorpse) {
			isMarkedForRemoval = true;
		}
		else {
			isACorpse = true;
			hp = maxhp;
			color = Color.black;
		}
	}
	
	private void growOlder() {
		age  = ((age * Environment.YEAR_LENGTH) + 1) / Environment.YEAR_LENGTH;
		
		if (! isACorpse && age > maxAge) {
			this.die();
			return;
		}
		if (age < maxAge / 5) {
			maxhp = (int) Math.round((maxhpThreshold / 2.0) + (maxhpThreshold / 2.0) * (age / (maxAge / 5.0)));
		}
		else {
			if (age >= maxAge * (4.0/5.0)) {
				maxhp = (int) Math.round((maxhpThreshold / 2.0) + (maxhpThreshold / 2.0) * ((maxAge - age) / (maxAge / 5.0)));
			}
			else {
				maxhp = maxhpThreshold;
			}
		}
	}
	
	private void reproduce() {
		Organism offspring = new Organism(this);
		
		Environment.getEnvironment().addOrganism(offspring, position.randomWithinDistance(2));
	}
	
	private void eat(Organism prey) {
		int hpTaken = prey.takeHP(maxhpThreshold / 5);
		nutrition += hpTaken * preyValues.getPreyValue(prey.getOrganismType());
	}
	
	private void photosynthesize() {
		nutrition += Environment.SUN_BRIGHTNESS * (hp / maxhp) * preyValues.getPreyValue(0) + (Environment.SUN_BRIGHTNESS / preyValues.getPreyValue(0));
	}
	
	// updates the mental map of the organism
	private void perceive() {
		mentalMap = Environment.getEnvironment().resolvePerception(this);
	}
}
