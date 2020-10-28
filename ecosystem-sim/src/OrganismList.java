
public class OrganismList {
	
	private class OrganismNode {
		private Organism organism;
		private OrganismNode parent, child;
		
		public OrganismNode(Organism o) {
			organism = o;
		}
	}
	
	private OrganismNode head, tail, current;
	
	public OrganismList() {
		head = null;
		tail = null;
		current = head;
	}
	
	public void add(Organism o) {
		if (head == null) {
			head = new OrganismNode(o);
			tail = head;
		}
		else {
			tail.child = new OrganismNode(o);
			tail.child.parent = tail;
			tail = tail.child;
		}
	}
	
	// removes the current node and sets current node to the next node
	public void removeCurrent() {
		if (current == null) return;
		if (head.equals(tail)) {
			head = tail = current = null;
			return;
		}
		if (current.equals(head)) {
			head = head.child;
			head.parent.child = null;
			head.parent = null;
			current = head;
			return;
		}
		if (current.equals(tail)) {
			tail = tail.parent;
			tail.child.parent = null;
			tail.child = null;
			current = null;
			return;
		}
		current.child.parent = current.parent;
		current.parent.child = current.child;
		current = current.child;
		
	}
	
	public void startIteration() {
		current = head;
	}
	
	public void next() {
		current = current.child;
	}
	
	public boolean endOfList() {
		return current == null;
	}
	
	public Organism getCurrentOrganism() {
		return current.organism;
	}
	
	public OrganismList copy() {
		OrganismList copy = new OrganismList();
		copy.head = head;
		copy.tail = tail;
		return copy;
	}
}

