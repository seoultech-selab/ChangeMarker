package hk.ust.cse.pishon.esgen.model;

import java.io.Serializable;

import org.eclipse.jface.text.ITextSelection;

public class EditOp implements Serializable {

	private static final long serialVersionUID = 3978037116216778196L;
	public static final String OP_INSERT = "Insert";
	public static final String OP_DELETE = "Delete";
	public static final String OP_MOVE = "Move";
	public static final String OP_UPDATE = "Update";

	private String type;
	private String oldCode;
	private int oldStartPos;
	private int oldLength;
	private int oldStartLine;
	private int oldEndLine;
	private String newCode;
	private int newStartPos;
	private int newLength;
	private int newStartLine;
	private int newEndLine;


	public EditOp(String type, ITextSelection sOld, ITextSelection sNew) {
		super();
		this.type = type;
		if (sOld != null) {
			oldCode = sOld.getText();
			oldStartPos = sOld.getOffset();
			oldLength = sOld.getLength();
			//Line numbers from ITextSelection start with 0.
			oldStartLine = sOld.getStartLine()+1;
			oldEndLine = sOld.getEndLine()+1;
		}
		if (sNew != null) {
			newCode = sNew.getText();
			newStartPos = sNew.getOffset();
			newLength = sNew.getLength();
			newStartLine = sNew.getStartLine()+1;
			newEndLine = sNew.getEndLine()+1;
		}
	}


	public String getType() {
		return type;
	}


	public String getOldCode() {
		return oldCode;
	}


	public int getOldStartPos() {
		return oldStartPos;
	}


	public int getOldLength() {
		return oldLength;
	}


	public int getOldStartLine() {
		return oldStartLine;
	}


	public String getNewCode() {
		return newCode;
	}


	public int getNewStartPos() {
		return newStartPos;
	}


	public int getNewLength() {
		return newLength;
	}


	public int getNewStartLine() {
		return newStartLine;
	}


	public int getOldEndLine() {
		return oldEndLine;
	}


	public int getNewEndLine() {
		return newEndLine;
	}


	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.type);
		sb.append("\t");
		if(this.type.equals(OP_INSERT)){
			sb.append(this.newCode);
			sb.append(" (Line ");
			sb.append(this.newStartLine);
			sb.append(")");
		}else if(this.type.equals(OP_DELETE)){
			sb.append(this.oldCode);
			sb.append(" (Line ");
			sb.append(this.oldStartLine);
			sb.append(")");
		}else{
			sb.append(this.oldCode);
			sb.append(" (Line ");
			sb.append(this.oldStartLine);
			sb.append(")\n");
			sb.append("to\t");
			sb.append(this.newCode);
			sb.append(" (Line ");
			sb.append(this.newStartLine);
			sb.append(")");
		}
		return sb.toString();
	}
}
