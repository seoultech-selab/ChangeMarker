package hk.ust.cse.pishon.esgen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ScriptData implements Serializable {

	private static final long serialVersionUID = -4528628685158295289L;
	private List<Change> changes;
	private TreeMap<String, TreeMap<String, EditScript>> scripts;
	private Map<String, Node> nodeCache;
	private List<ScriptItem> scriptItems;
	
	public ScriptData() {
		changes = new ArrayList<>();
		scripts = new TreeMap<>();
		nodeCache = new HashMap<>();
		scriptItems  = new ArrayList<>();
	}
	
	public void loadChanges(List<Change> newChanges) {
		clearAll();
		changes.addAll(newChanges);
		Collections.sort(changes, (c1, c2) -> c1.getName().compareTo(c2.getName()));
	}
	
	public List<Change> getChanges() {
		return changes;
	}
	
	public List<ScriptItem> getScriptItems() {
		return scriptItems;
	}
	
	public Node getNode(String changeName) {
		if(!nodeCache.containsKey(changeName))
			createNode(changeName);
		return nodeCache.get(changeName); 
	}
	
	public void setNode(String changeName, Node n) {
		nodeCache.put(changeName, n);
	}

	private void createNode(String changeName) {
		Node n = new Node(changeName);
		if(scripts.containsKey(changeName)) {
			//Create combined node from scripts.			
			Map<String, EditScript> map = scripts.get(changeName);
			for(String scriptName : map.keySet()) {
				EditScript script = map.get(scriptName);
				Node s = new Node(scriptName);
				n.addChild(s);
				createOpNodes(s, script.getEditOps());
			}
		}
		nodeCache.put(changeName, n);
	}

	private void createOpNodes(Node s, List<EditOp> editOps) {
		List<EditOp> trimmed =  new ArrayList<>();
		for(EditOp op : editOps) {
			op = op.trim();
			trimmed.add(op);
		}

		//Sort by line, pos.
		trimmed.sort((op1, op2) -> EditOp.compare(op1, op2));

		//Update stat., add node.
		for(EditOp op : trimmed) {
			s.addChild(new Node(op));
		}
	}

	public void loadNodes(Map<String, Node> nodes) {
		for(String changeName : nodes.keySet()) {
			Node n = nodes.get(changeName);
			scripts.put(changeName, createScriptEntry(n));
		}
		nodeCache.putAll(nodes);
	}

	public TreeMap<String, EditScript> createScriptEntry(Node n) {
		//Node n's tree structure must be change_name - script_name - edit_ops.
		TreeMap<String, EditScript> scriptEntry = new TreeMap<>();
		for(Node s : n.children) {
			if(s.value instanceof String) {
				EditScript script = new EditScript();
				for(Node c : s.children) {
					if(c.value instanceof EditOp) {
						script.add((EditOp)c.value);
					}
				}
				scriptEntry.put((String)s.value, script);
			}
		}
		return scriptEntry;
	}

	public void loadScriptItems(List<ScriptItem> items) {
		clearAll();
		Map<String, Change> changeMap = new HashMap<>();
		for(ScriptItem item : items) {
			scriptItems.add(new ScriptItem(item.fileName, item.filePath, null));
			for(String changeName : item.changes.keySet()) {
				Change c = item.changes.get(changeName);				
				changeMap.putIfAbsent(changeName, c.copyWithoutScript());
				scripts.putIfAbsent(changeName, new TreeMap<>());				
				EditScript script = c.getScript();
				scripts.get(changeName).put(item.fileName, script);				
			}
		}
		changes.addAll(changeMap.values());
		Collections.sort(changes, (c1, c2) -> c1.getName().compareTo(c2.getName()));
	}
	
	public void loadWebScripts(ScriptItem item, Map<String, Map<String, List<EditOp>>> webScripts) {
		scriptItems.clear();
		scripts.clear();
		nodeCache.clear();
		scriptItems.add(item);
		for(String userName : webScripts.keySet()) {
			Map<String, List<EditOp>> items = webScripts.get(userName);
			for(String changeName : items.keySet()) {				
				List<EditOp> ops = items.get(changeName);
				if(ops.size() > 0) {
					scripts.putIfAbsent(changeName, new TreeMap<>());
					TreeMap<String, EditScript> map = scripts.get(changeName);
					map.put(userName, new EditScript(ops));
				}
			}
		}
		filterChanges();
	}
	
	private void filterChanges() {
		List<Change> filtered = new ArrayList<>();
		for(Change c : changes) {
			if(scripts.containsKey(c.getName())) {
				filtered.add(c);
			}
		}
		changes = filtered;
	}

	public void clearAll() {
		scripts.clear();
		changes.clear();
		nodeCache.clear();
		scriptItems.clear();
	}
}
