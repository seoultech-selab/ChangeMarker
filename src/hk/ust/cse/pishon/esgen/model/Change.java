package hk.ust.cse.pishon.esgen.model;

import java.io.Serializable;

import hk.ust.cse.pishon.esgen.compare.CompareItem;

public class Change implements Serializable {

	private static final long serialVersionUID = -5334869474891888849L;
	private String name;
	private CompareItem oldFile;
	private CompareItem newFile;
	private EditScript script;

	public Change(String name, String oldFilePath, String newFilePath) {
		super();
		this.name = name;
		this.oldFile = new CompareItem(oldFilePath);
		this.newFile = new CompareItem(newFilePath);
		this.script = new EditScript();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CompareItem getOldFile() {
		return oldFile;
	}

	public void setOldFile(CompareItem oldFile) {
		this.oldFile = oldFile;
	}

	public CompareItem getNewFile() {
		return newFile;
	}

	public void setNewFile(CompareItem newFile) {
		this.newFile = newFile;
	}

	@Override
	public String toString(){
		return this.name;
	}

	public EditScript getScript(){
		return this.script;
	}
}
