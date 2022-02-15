package hk.ust.cse.pishon.esgen.tree;

import java.io.Serializable;

public class Tree implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3895521194568109722L;
	private TreeNode root;

	public Tree(){
		this.root = new TreeNode();
	}

	public Tree(TreeNode root){
		this.root = root;
	}

	public TreeNode getRoot() {
		return root;
	}

	public TreeNode getNode(int startPos, int length) {
		TreeNode target = null;
		for(TreeNode child : root.children) {
			target = getNode(child, startPos, length);
			if(target != null)
				return target;
		}
		return null;
	}

	private TreeNode getNode(TreeNode node, int startPos, int length) {
		int endPos = startPos + length;
		if(inRange(node, startPos, endPos)) {
			TreeNode deeperNode = null;
			for(TreeNode child : node.children) {
				if(inRange(child, startPos, endPos)) {
					deeperNode = getNode(child, startPos, length);
					break;
				}
			}
			return deeperNode == null ? node : deeperNode;
		}
		return null;
	}

	private boolean inRange(TreeNode node, int startPos, int endPos) {
		return node.startPos <= startPos
				&& node.endPos >= endPos;
	}
}
