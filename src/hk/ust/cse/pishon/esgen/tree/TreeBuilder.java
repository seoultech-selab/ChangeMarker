package hk.ust.cse.pishon.esgen.tree;

import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class TreeBuilder {

	public static Tree buildTree(String source) {
		Tree tree = new Tree();
		CompilationUnit cu = getCompilationUnit(source);
		NodeVisitor visitor = new NodeVisitor(tree, cu);
		cu.accept(visitor);
		return tree;
	}

	public static CompilationUnit getCompilationUnit(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setSource(source.toCharArray());
		CompilationUnit cu = (CompilationUnit)parser.createAST(null);

		return cu;
	}
}
