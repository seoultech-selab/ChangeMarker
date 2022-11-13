package hk.ust.cse.pishon.esgen.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class ScriptItem implements Serializable {
	
	private static final long serialVersionUID = -5036650956410684940L;
	public String fileName;
	public String filePath;
	public Map<String, Change> changes;
	
	public ScriptItem(String filePath) {
		super();
		this.filePath = filePath;
		File f = new File(filePath);
		this.fileName = f.getName();
		if(fileName.endsWith(".obj")) {
			fileName = fileName.substring(0, fileName.length()-4);
		}
		List<Change> changes = null;
		if(f.exists()) {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(f));
				Object obj = ois.readObject();				
				if(obj instanceof List<?>)
					changes = (List<Change>)obj;
			} catch (Exception e) {
				e.printStackTrace();				
			} finally {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		this.changes = new TreeMap<String, Change>();
		if(changes != null)
			changes.forEach(c -> this.changes.put(c.getName(), c));
		
	}
	
	public ScriptItem(String fileName, String filePath, List<Change> changes) {
		super();
		this.fileName = fileName;
		this.filePath = filePath;
		this.changes = new TreeMap<String, Change>();
		if(changes != null)
			changes.forEach(c -> this.changes.put(c.getName(), c));
	}
}
