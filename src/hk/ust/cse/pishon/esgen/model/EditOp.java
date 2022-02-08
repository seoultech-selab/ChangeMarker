package hk.ust.cse.pishon.esgen.model;

import java.io.Serializable;

import org.eclipse.jface.text.ITextSelection;

public class EditOp implements Serializable, Comparable<EditOp> {

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

	public EditOp(EditOp op) {
		super();
		this.type = op.type;
		this.oldCode = op.oldCode;
		this.oldStartPos = op.oldStartPos;
		this.oldLength = op.oldLength;
		this.oldStartLine = op.oldStartLine;
		this.oldEndLine = op.oldEndLine;
		this.newCode = op.newCode;
		this.newStartPos = op.newStartPos;
		this.newLength = op.newLength;
		this.newStartLine = op.newStartLine;
		this.newEndLine = op.newEndLine;
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

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof EditOp) {
			EditOp op = (EditOp)obj;
			if(op.type.equals(type)) {
				boolean ret = true;
				if(!type.equals(OP_DELETE)) {
					ret = ret && newStartLine == op.newStartLine
							&& newStartPos ==  op.newStartPos
							&& newLength == op.newLength
							&& newCode.equals(op.newCode);
				}
				if(!type.equals(OP_INSERT)) {
					ret = ret && oldStartLine == op.oldStartLine
							&& oldStartPos ==  op.oldStartPos
							&& oldLength == op.oldLength
							&& oldCode.equals(op.oldCode);
				}
				return ret;
			}
		}
		return false;
	}

	@Override
	public int compareTo(EditOp op) {
		int cmp = 0;
		if(oldStartLine == op.oldStartLine
				&& oldStartPos == op.oldStartPos) {
			cmp = op.oldLength - oldLength;
		} else {
			cmp += oldStartLine - op.oldStartLine;
			cmp += oldStartPos - op.oldStartPos;
		}
		if(cmp == 0 && oldCode != null && !oldCode.equals(op.oldCode))
			cmp = 1;
		if(cmp == 0) {
			if(newStartLine == op.newStartLine
					&& newStartPos == op.newStartPos) {
				cmp = op.newLength - newLength;
			} else {
				cmp += newStartLine - op.newStartLine;
				cmp += newStartPos - op.newStartPos;
			}
			if(cmp == 0 && newCode != null && !newCode.equals(op.newCode))
				cmp = 1;
		}
		if(cmp == 0 && !type.equals(op.type))
			cmp = type.compareTo(op.type);
		return cmp;
	}


	public EditOp trim() {
		EditOp op = new EditOp(this);
		if(op.oldCode != null && op.oldCode.length() > op.oldCode.trim().length()) {
			int len = op.oldCode.length();
			op.oldStartPos += len - op.oldCode.stripLeading().length();
			op.oldLength -= len - op.oldCode.stripTrailing().length();
			op.oldCode = op.oldCode.trim();
		}
		if(op.newCode != null && op.newCode.length() > op.newCode.trim().length()) {
			int len = op.newCode.length();
			op.newStartPos += len - op.newCode.stripLeading().length();
			op.newLength -= len - op.newCode.stripTrailing().length();
			op.newCode = op.newCode.trim();
		}
		return op;
	}
}
