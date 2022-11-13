package hk.ust.cse.pishon.esgen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EditScript implements Serializable {

	private static final long serialVersionUID = 7573200536663771854L;
	private List<EditOp> editOps;

	public EditScript(){
		editOps = new ArrayList<EditOp>();
	}
	
	public EditScript(List<EditOp> ops){
		editOps = new ArrayList<EditOp>(ops);
	}

	public void add(EditOp op){
		this.editOps.add(op);
	}

	public List<EditOp> getEditOps(){
		return this.editOps;
	}
	
	public int size() {
		return this.editOps.size();
	}
	
	public boolean isEmpty() {
		return this.editOps.isEmpty();
	}

	@Override
	public String toString() {
		if (editOps.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (EditOp op : editOps) {
				sb.append("\n\n");
				sb.append(op.toString());
			}
			return sb.toString().substring(2);
		}else{
			return "";
		}
	}
}
