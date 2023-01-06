package hk.ust.cse.pishon.esgen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScriptGroup implements Serializable {

	private static final long serialVersionUID = 3784812655919859072L;
	private String groupName;
	private List<String> scriptNames;
	
	public ScriptGroup(String groupName) {
		super();
		this.groupName = groupName;
		this.scriptNames = new ArrayList<>();
	}
	
	public void addScript(String scriptName) {
		scriptNames.add(scriptName);
	}
	
	public void addScriptAll(List<String> scriptNameList) {
		scriptNames.addAll(scriptNameList);
	}

	public String getName() {
		return groupName;
	}

	public void setName(String groupName) {
		this.groupName = groupName;
	}

	public List<String> getScriptNames() {
		return scriptNames;
	}

	public void setScriptNames(List<String> scriptNames) {
		this.scriptNames = scriptNames;
	}
	
	public int size() {
		return scriptNames.size();
	}
	
	public String getAllNames() {
		return String.join(",", scriptNames);
	}

	@Override
	public String toString() {
		return groupName + " (" + scriptNames.size() + ")";
	}	
}
