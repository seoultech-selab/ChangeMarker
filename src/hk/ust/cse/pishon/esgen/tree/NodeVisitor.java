package hk.ust.cse.pishon.esgen.tree;

import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;

public class NodeVisitor extends ASTVisitor {
	private Tree tree;
	private Stack<TreeNode> nodeStack;
	private CompilationUnit cu;
	private static final boolean ENABLE_GUMTREE_AST = false;

	public NodeVisitor(Tree tree, CompilationUnit cu){
		this.tree = tree;
		this.cu = cu;
		this.nodeStack = new Stack<TreeNode>();
		this.nodeStack.add(tree.getRoot());
	}

	public Tree getTree(){
		return tree;
	}

	public CompilationUnit getCU() {
		return cu;
	}

	@Override
	public void postVisit(ASTNode node) {
		//If ignore.expr.stmt is set, check whether node is ExpressionStatement.
		if(ENABLE_GUMTREE_AST ||
				!(node instanceof ExpressionStatement)){
			nodeStack.pop();
		}
	}

	@Override
	public void preVisit(ASTNode node) {
		//Ignore ExpressionStatement if ignore.expr.stmt is set.
		if(ENABLE_GUMTREE_AST ||
				!(node instanceof ExpressionStatement)){
			nodeStack.push(getTreeNode(node));
		}
	}

	@Override
	public boolean visit(QualifiedName node){
		return false;
	}

	@Override
	public boolean visit(SimpleType node){
		if(ENABLE_GUMTREE_AST){
			return super.visit(node);
		}
		return false;
	}

	@Override
	public boolean visit(QualifiedType node){
		if(ENABLE_GUMTREE_AST){
			return super.visit(node);
		}
		return false;
	}

	@Override
	public boolean visit(PrimitiveType node){
		if(ENABLE_GUMTREE_AST){
			return super.visit(node);
		}
		return false;
	}

	private TreeNode getTreeNode(ASTNode node) {
		int startPos = node.getStartPosition();
		int endPos = node.getStartPosition()+node.getLength();
		int startLine = cu.getLineNumber(startPos);
		int endLine = cu.getLineNumber(endPos);
		TreeNode treeNode = new TreeNode(node.getNodeType(), startPos, endPos, startLine, endLine, getLabel(node));
		if(!nodeStack.isEmpty()){
			nodeStack.peek().addChild(treeNode);
		}
		return treeNode;
	}

	private String getLabel(ASTNode node) {
		String label = node.getClass().getSimpleName();
		return label;
	}
}
