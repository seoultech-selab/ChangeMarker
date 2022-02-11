package hk.ust.cse.pishon.esgen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
	
	private static final long serialVersionUID = -1887877964675687159L;

	public Node parent;
	public Object value;
	public List<Node> children;
	
	public Node(Object value) {
		parent = null;
		this.value = value;
		children = new ArrayList<>();
	}
	
	public void addChild(Node c) {
		children.add(c);
		c.parent = this;
	}
	
	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public Object[] getChildren() {
		return children.toArray();
	}
	
	public int size() {
		return children.size();
	}
	
	public Node copy() {
		Node n = new Node(value != null ? value : null);
		for(Node c : children) {
			n.addChild(c.copy());
		}
		return n;
	}
}
