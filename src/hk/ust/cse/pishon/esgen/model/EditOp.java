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

	public EditOp(String type, int startPos, int len, int startLine, int endLine, String code) {
		if(type != null && type.equals(OP_INSERT)) {
			this.type = type;
			this.newStartPos = startPos;
			this.newLength = len;
			this.newStartLine = startLine;
			this.newEndLine = endLine;
			this.newCode = code;
		} else {
			this.type = type;
			this.oldStartPos = startPos;
			this.oldLength = len;
			this.oldStartLine = startLine;
			this.oldEndLine = endLine;
			this.oldCode = code;
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

	public static int compare(EditOp op1, EditOp op2) {
		int line1 = op1.getType().equals(EditOp.OP_INSERT) ? op1.getNewStartLine() : op1.getOldStartLine();
		int line2 = op2.getType().equals(EditOp.OP_INSERT) ? op2.getNewStartLine() : op2.getOldStartLine();
		int cmp = Integer.compare(line1, line2);
		if(cmp == 0) {		
			String type1 = op1.getType();
			String type2 = op2.getType();
			int cmpType = type1.compareTo(type2);
			if (cmpType == 0) {
				int pos1 = op1.getType().equals(EditOp.OP_INSERT) ? op1.getNewStartPos() : op1.getOldStartPos();
				int pos2 = op2.getType().equals(EditOp.OP_INSERT) ? op2.getNewStartPos() : op2.getOldStartPos();
				cmp = Integer.compare(pos1, pos2);
			} else {
				cmp = cmpType;
			}
		}
		return cmp;
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

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = 31 * hashCode + type.hashCode();
		hashCode = 31 * hashCode + oldStartPos;
		hashCode = 31 * hashCode + oldLength;
		hashCode = 31 * hashCode + newStartPos;
		hashCode = 31 * hashCode + newLength;
		return hashCode;
	}

	public EditOp trim() {
		EditOp op = new EditOp(this);
		if(op.oldCode != null) {
			String trimmed = op.oldCode.trim();
			if(op.oldCode.length() > trimmed.length()) {
				op.oldStartPos += op.oldCode.length() - op.oldCode.stripLeading().length();
				op.oldLength = trimmed.length();
				op.oldCode = trimmed;
			}
		}
		if(op.newCode != null) {
			String trimmed = op.newCode.trim();
			if(op.newCode.length() > trimmed.length()) {
				op.newStartPos += op.newCode.length() - op.newCode.stripLeading().length();
				op.newLength = trimmed.length();
				op.newCode = trimmed;
			}
		}
		return op;
	}
}
