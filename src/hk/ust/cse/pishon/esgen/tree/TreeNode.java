package hk.ust.cse.pishon.esgen.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8283187975699974276L;
	public int type;
	public String label;
	public int startPos;
	public int endPos;
	public int startLine;
	public int endLine;
	public List<TreeNode> children = new ArrayList<>();
	public TreeNode parent;

	public TreeNode(){
		this(-1, -1, 0, 0, 0, "", null);
	}

	public TreeNode(int type, int startPos, int endPos, int startLine, int endLine, String label){
		this(type, startPos, endPos, startLine, endLine, label, null);
	}

	public TreeNode(int type, int startPos, int endPos, int startLine, int endLine, String label, TreeNode parent){
		this.type = type;
		this.startPos = startPos;
		this.endPos = endPos;
		this.startLine = startLine;
		this.endLine = endLine;
		this.label = label;
		this.parent = parent;
	}

	public void addChild(TreeNode child) {
		children.add(child);
		child.parent = this;
	}

	public String getTreeString() {
		StringBuffer sb = new StringBuffer();
		getTreeString(this, sb, 0);
		return sb.toString();
	}

	private void getTreeString(TreeNode node, StringBuffer sb, int indent) {
		for(int i=0; i<indent; i++)
			sb.append("  ");
		sb.append(node.label);
		sb.append("\n");
		for(TreeNode child : node.children)
			getTreeString(child, sb, indent+1);
	}
}
