
public class Environment {
	
	private class Tile {
		private int groundType;
		private Organism plantLayer;
		private Organism animalLayer;
		
		public Tile(int groundType) {
			this.groundType = groundType;
		}
		
		public void setGroundType(int newType) {
			groundType = newType;
		}
		
		// returns true if successful and false if tile is currently occupied
		public boolean setPlantLayer(Organism newOccupant) {
			if (plantLayer != null) return false;
			plantLayer = newOccupant;
			return true;
		}
		
		// returns true if successful and false if tile is currently occupied
		public boolean setAnimalLayer(Organism newOccupant) {
			if (animalLayer != null) return false;
			animalLayer = newOccupant;
			return true;
		}
		
		public void clearPlantLayer() {
			plantLayer = null;
		}
		
		public void clearAnimalLayer() {
			animalLayer = null;
		}
	}
	
	private Tile[][] environment;
	
	public Environment(int size) {
		environment = new Tile[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				environment[i][j] = new Tile(1);
			}
		}
	}
	
	
}
