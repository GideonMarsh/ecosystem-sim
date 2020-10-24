import java.awt.Color;
import java.util.ArrayList;

public class Organism {
	private Position position;
	private int walkingSpeed;
	
	// the target of AI behaviors such as eating
	private Organism target;
	
	private int organismType;
	private boolean isAPlant;
	private boolean isACorpse;
	private boolean isMarkedForRemoval;
	
	private int nutrition;
	private PreyValues preyValues;		// the value of eating certain prey
	private int upkeep; 				// nutrition lost each game tick
	private int hungryValue;			// the value at which organism will seek out food
	
	private int reproductionCost;		// the nutrition cost for reproduction
	private int reproductionThreshold;	// the minimum nutrition value to begin reproduction
	private int maxOffspring;			// the maximum amount of offspring this organism can have
	private int numberOfOffspring;		// the current amount of offspring this organism has had
	
	// hp values represent relative size as well as health
	private int hp;
	private int maxhp;
	private int maxhpThreshold;		// maximum total hp when an adult
	
	private double age;
	private int maxAge;
	
	// Amount of hp to remove from the target of an attack from this organism
	private int attackPower;
	
	/*
	 *  represents the organism AI's current goals
	 *  
	 *  0 = idle
	 *  1 = locating food
	 *  2 = attempting to reproduce
	 */
	private int currentBehavior;
	
	private Color color;
	
	// New organisms are created from parent(s) parameters (genes)
	public Organism(Organism parent) {
		position = new Position(0,0);
		walkingSpeed = parent.walkingSpeed;
		organismType = parent.organismType;
		isAPlant = parent.isAPlant;
		color = parent.color;
		age = 0;
		currentBehavior = 0;
		maxAge = parent.maxAge;
		maxhpThreshold = parent.maxhpThreshold;
		maxhp = maxhpThreshold / 2;
		hp = maxhp;
		preyValues = parent.preyValues.copy();
		upkeep = parent.upkeep;
		hungryValue = parent.hungryValue;
		reproductionCost = parent.reproductionCost;
		reproductionThreshold = parent.reproductionThreshold;
		maxOffspring = 3;
		numberOfOffspring = 0;
		nutrition = hungryValue;
		attackPower = 50;
	}
	
	// Creates a new organism from nothing of the specified type
	public Organism(int type) {
		position = new Position(0,0);
		age = 0;
		currentBehavior = 0;
		preyValues = new PreyValues();
		numberOfOffspring = 0;

		switch (type) {
		case 2:
			walkingSpeed = 1;
			organismType = type;
			isAPlant = false;
			color = new Color(0,0,200);
			maxAge = 10;
			maxhpThreshold = 200;
			maxhp = maxhpThreshold / 2;
			hp = maxhp;
			
			preyValues.addPreyValue(1, 1.5f);
			
			upkeep = 5;
			hungryValue = upkeep * 50;
			reproductionCost = maxhpThreshold / 2;
			reproductionThreshold = reproductionCost + hungryValue;
			maxOffspring = 3;
			
			attackPower = 30;
			break;
		
		case 3:
			walkingSpeed = 1;
			organismType = type;
			isAPlant = false;
			color = new Color(200,0,0);
			maxAge = 10;
			maxhpThreshold = 200;
			maxhp = maxhpThreshold / 2;
			hp = maxhp;
			
			preyValues.addPreyValue(2, 1.5f);
			
			upkeep = 5;
			hungryValue = upkeep * 50;
			reproductionCost = maxhpThreshold / 2;
			reproductionThreshold = reproductionCost + hungryValue;
			maxOffspring = 2;
			
			attackPower = 30;
			break;
			
		case 1:
		default:
			walkingSpeed = 0;
			organismType = type;
			isAPlant = true;
			color = new Color(0,150,0);
			maxAge = 20;
			maxhpThreshold = 400;
			maxhp = maxhpThreshold / 2;
			hp = maxhp;
			
			preyValues.addPreyValue(0, 1.0f);
			
			upkeep = 10;
			hungryValue = upkeep * 50;
			reproductionCost = maxhpThreshold / 2;
			reproductionThreshold = reproductionCost + hungryValue;
			maxOffspring = 20;
			
			attackPower = 1;
		}
		nutrition = hungryValue;
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
			
			// decide what current behavior of organism should be
			if (nutrition < hungryValue) {
				currentBehavior = 1;
			}
			else {
				// organisms can only reproduce during adulthood
				if (maxhp == maxhpThreshold && numberOfOffspring < maxOffspring) {
					// organisms will only reproduce if they have sufficient nutrition
					if (nutrition < reproductionThreshold) {
						currentBehavior = 1;
					}
					else {
						currentBehavior = 2;
					}
				}
				else {
					currentBehavior = 0;
				}
			}
			
			// perform actions according to current behavior
			perceive();
			move();
			if (isAPlant) photosynthesize();
			else {
				if (currentBehavior == 1 && target != null && position.isWithinRange(target.position, 1)) {
					if (target.isAPlant || target.isACorpse) {
						eat(target);
					}
					else {
						target.loseHP(attackPower);
					}
				}
			}
			if (currentBehavior == 2) {
				reproduce();
			}
		}
	}
	
	private void move() {
		if (walkingSpeed != 0) {
			switch (currentBehavior) {

			case 1:
				if (target == null) {
					for (int i = 0; i < walkingSpeed; i++) {
						Environment.getEnvironment().moveOrganism(this, position.randomWithinDistance(1));
					}
					return;
				}
				
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
					if (position.isWithinRange(target.position, 1)) break;
					
					xDif = target.position.xPosition - position.xPosition;
					yDif = target.position.yPosition - position.yPosition;
					
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
				break;
			case 2:
				// if reproduce sexually, look for mate
				break;
			default:
				for (int i = 0; i < walkingSpeed; i++) {
					Environment.getEnvironment().moveOrganism(this, position.randomWithinDistance(1));
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
		if (expendEnergy(upkeep)) return;
		
		// Organisms can expend nutrition to recover hp slowly over time, if nutrition is high enough
		if (hp < maxhp && nutrition > (hungryValue / 2) + (upkeep * 3)) {
			if (expendEnergy(upkeep * 3)) return;
			hp = Math.min(maxhp, hp + maxhp / 20);
		}
		
		if (! isACorpse && age > maxAge) {
			this.die();
			return;
		}
		if (age < maxAge / 5) {
			int formerMaxhp = maxhp;
			maxhp = (int) Math.round((maxhpThreshold / 2.0) + (maxhpThreshold / 2.0) * (age / (maxAge / 5.0)));
			hp += maxhp - formerMaxhp;
		}
		else {
			if (age >= maxAge * (4.0/5.0)) {
				maxhp = (int) Math.round((maxhpThreshold / 2.0) + (maxhpThreshold / 2.0) * ((maxAge - age) / (maxAge / 5.0)));
				if (hp > maxhp) hp = maxhp;
			}
			else {
				maxhp = maxhpThreshold;
			}
		}
	}
	
	private void reproduce() {
		if (isAPlant) {
			Organism offspring = new Organism(this);
			
			Environment.getEnvironment().addOrganism(offspring, position.randomWithinDistance(2));
			expendEnergy(reproductionCost);
			numberOfOffspring++;
		}
		else {
			Organism offspring = new Organism(this);
			
			for (int i = 0; i < 4; i++) {
				if (Environment.getEnvironment().addOrganism(offspring, position.randomWithinDistance(2))) {
					expendEnergy(reproductionCost);
					numberOfOffspring++;
					break;
				}
			}
		}
	}
	
	private void eat(Organism prey) {
		nutrition += prey.takeHP(maxhpThreshold / 5) * preyValues.getPreyValue(prey.getOrganismType());
	}
	
	private void photosynthesize() {
		nutrition += ((hp / 10) * preyValues.getPreyValue(0)) + Math.max(0, maxhpThreshold * (1 - (preyValues.getPreyValue(0)/2)) / 50);
		if (nutrition > maxhp * 2) nutrition = maxhp * 2;
	}
	
	// updates the mental map of the organism
	private void perceive() {
		ArrayList<Organism> mentalMap = Environment.getEnvironment().resolvePerception(this);
		
		target = null;
		switch (currentBehavior) {
		case 1:
			for (Organism organism : mentalMap) {
				if (preyValues.isPrey(organism.getOrganismType())) {
					if (target == null) target = organism;
					else {
						if (position.closerThan(organism.position, target.position)) {
							if (preyValues.getPreyValue(target.getOrganismType()) <= preyValues.getPreyValue(organism.getOrganismType()) || (organism.isACorpse && ! target.isACorpse)) {
								target = organism;
							}
						}
					}
				}
			}
			break;
		case 2:
			break;
		default:
		}
	}
	
	// subtracts energy spent from nutrition value
	// if nutrition value reaches 0, all other expenditures come directly from hp
	// returns true if organism dies, false if otherwise
	private boolean expendEnergy(int amount) {
		nutrition -= amount;
		if (nutrition < 0) {
			hp += nutrition;
			nutrition = 0;
		}
		if (hp <= 0) {
			die();
			return true;
		}
		return false;
	}
}
