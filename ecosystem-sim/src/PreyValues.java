/*
 * Prey values represent how nutritious certain food is when consumed by a specific organism
 * Nutrition is obtained directly from the hp of the organism, which is modified by the prey value
 * Nutrition obtained = hp "eaten" * prey value
 * 
 * Prey type 0 is reserved for photosynthesis
 * Instead of taking hp from prey, photosynthesis generates nutrition based on the consumer's hp
 * Higher hp means larger and healthier, so more nutrition is gained
 * Prey value for photosynthesis represents hp dependence
 * 0 hp dependence means photosynthesis produces flat nutrition value regardless of hp
 * higher hp dependence means more nutrition is generated at higher hp, but less at lower hp
 */
import java.util.ArrayList;

public class PreyValues {
	
	private class PreyValue {
		private int preyType;
		private float preyValue;
		
		public PreyValue(int type, float value) {
			preyType = type;
			preyValue = value;
		}
		
		public int getPreyType() {
			return preyType;
		}
		
		public float getPreyValue() {
			return preyValue;
		}
		
		public void setPreyValue(float newValue) {
			preyValue = newValue;
		}
	}
	
	private ArrayList<PreyValue> values;
	
	public PreyValues() {
		values = new ArrayList<PreyValue>();
	}
	
	public boolean isPrey(int preyType) {
		for (PreyValue p : values) {
			if (p.getPreyType() == preyType) return true;
		}
		return false;
	}
	
	public void addPreyValue(int preyType, float preyValue) {
		values.add(new PreyValue(preyType, preyValue));
	}
	
	// helper method for finding a specific PreyValue object by preyType
	private PreyValue findPreyValue(int preyType) {
		for (PreyValue p : values) {
			if (p.getPreyType() == preyType) return p;
		}
		return null;
	}
	
	public void removePreyValue(int preyType) {
		values.remove(findPreyValue(preyType));
	}
	
	// returns -1 if prey value not found
	public float getPreyValue(int preyType) {
		PreyValue p = findPreyValue(preyType);
		if (p == null) return -1;
		return p.getPreyValue();
	}
	
	public void modifyPreyValue(int preyType, float newValue) {
		PreyValue p = findPreyValue(preyType);
		if (p == null) return;
		p.setPreyValue(newValue);
	}
	
	// returns a distinct copy of this PreyValues object
	public PreyValues copy() {
		PreyValues p = new PreyValues();
		for (PreyValue v : values) {
			p.addPreyValue(v.preyType, v.preyValue);
		}
		return p;
	}
}
