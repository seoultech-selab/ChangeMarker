package hk.ust.cse.pishon.esgen.views;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import hk.ust.cse.pishon.esgen.compare.CompareInput;
import hk.ust.cse.pishon.esgen.model.Change;
import hk.ust.cse.pishon.esgen.model.EditOp;
import hk.ust.cse.pishon.esgen.model.EditScript;
import hk.ust.cse.pishon.esgen.model.Node;
import hk.ust.cse.pishon.esgen.model.ScriptItem;
import hk.ust.cse.pishon.esgen.tree.Tree;
import hk.ust.cse.pishon.esgen.tree.TreeBuilder;
import hk.ust.cse.pishon.esgen.tree.TreeNode;

public class ChangeView extends ViewPart {
	public static final String ID = "hk.ust.cse.pishon.esgen.views.changeview";
	public static final String METADATA_PATH = ".metadata" + File.separator + "changemarker";
	public static final String CHANGE_SAVE_FILE = "changes.obj";
	public static final String SCRIPT_SAVE_FILE = "scripts.obj";
	private TableViewer viewer;
	private List<Change> changes;
	private Map<String, Node> scripts;
	private Map<String, TreeMap<EditOp, List<String>>> statMap = new HashMap<>();
	private Map<String, Tree> oldTrees = new HashMap<>();
	private Map<String, Tree> newTrees = new HashMap<>();
	private String changePath;

	public String getChangePath() {
		return changePath;
	}

	public void setChangePath(String changePath) {
		this.changePath = changePath;
		readSaved();
		viewer.setInput(changes);
	}

	class ViewLabelProvider extends LabelProvider  {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}

		@Override
		public String getText(Object element) {
			if(element instanceof Change){
				Change c = (Change)element;
				if(c.getScript().isEmpty())
					return c.getName();
				else
					return c.getName() + " - checked";
			}
			return super.getText(element);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IWorkbenchPage page = window.getActivePage();
		viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new ViewLabelProvider());
		// Provide the input to the ContentProvider
		loadChangePath();
		readSaved();
		viewer.setInput(changes);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				if(selection.getFirstElement() instanceof Change){
					Change change = (Change)selection.getFirstElement();
					CompareInput input = new CompareInput(change);
					CompareUI.openCompareEditorOnPage(input, page);
					IViewPart scriptViewer = page.findView(ScriptView.ID);
					if(scriptViewer != null){
						((ScriptView)scriptViewer).setInput(change.getName(), change.getScript());
					}
					ScriptStatView scriptStatViewer = (ScriptStatView)page.findView(ScriptStatView.ID);
					if(scriptStatViewer != null){
						scriptStatViewer.setInput(change.getName());
					}
					MultiScriptView multiScriptViewer = (MultiScriptView)page.findView(MultiScriptView.ID);
					if(multiScriptViewer != null)
						multiScriptViewer.setInput(change.getName());
				}
			}
		});
		page.addPartListener(new IPartListener2(){
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				if(partRef.getId().equals(ChangeView.ID)){
					saveChanges();
					partRef.getPage().closeAllEditors(false);
					IViewPart scriptViewer = page.findView(ScriptView.ID);
					if(scriptViewer != null)
						((ScriptView)scriptViewer).setInput(null, null);
					IViewPart scriptStatViewer = page.findView(ScriptStatView.ID);
					if(scriptStatViewer != null)
						((ScriptStatView)scriptStatViewer).setInput(null);
					IViewPart multiScriptViewer = page.findView(MultiScriptView.ID);
					if(multiScriptViewer != null)
						((MultiScriptView)multiScriptViewer).setInput(null);
				}
			}
		});
	}
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		saveChanges();
	}

	protected void saveChanges() {
		if(changePath == null)
			return;
		File changeSaveFile = getSaveFile(CHANGE_SAVE_FILE);
		File scriptSaveFile = getSaveFile(SCRIPT_SAVE_FILE);
		File saveMetaDir = changeSaveFile.getParentFile();
		File pathFile = getChangePathFile();
		File workspaceMetaDir = pathFile.getParentFile();
		if(!saveMetaDir.exists()){
			if(!saveMetaDir.mkdirs())
				return;
		}
		if(!workspaceMetaDir.exists()){
			if(!workspaceMetaDir.mkdirs())
				return;
		}
		ObjectOutputStream oos = null;
		BufferedWriter writer = null;
		ObjectOutputStream oos2 = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(changeSaveFile));
			oos.writeObject(changes);
			oos.flush();
			oos2 = new ObjectOutputStream(new FileOutputStream(scriptSaveFile));
			oos2.writeObject(scripts);
			oos2.flush();
			writer = new BufferedWriter(new FileWriter(pathFile));
			writer.write(changePath);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(oos != null)
					oos.close();
				if(oos2 != null)
					oos2.close();
				if(writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private File getChangePathFile(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath path = workspace.getRoot().getLocation();
		File rootDir = path.toFile();
		File pathFile = new File(rootDir.getAbsolutePath() + File.separator + METADATA_PATH + File.separator + "change_path");
		return pathFile;
	}

	private File getSaveFile(String fileName){
		File saveFile = new File(changePath + File.separator + METADATA_PATH + File.separator + fileName);
		return saveFile;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void refresh() {
		viewer.refresh();
	}

	public void updateInput(Map<String, Change> updates) {
		//Update changes only if they're already on the list.
		List<Change> newInput = new ArrayList<>();
		for(Change c : changes)
			if(updates.containsKey(c.getName()))
				newInput.add(updates.get(c.getName()));
		setInput(newInput);
	}

	public void setInput(List<Change> newInput){
		changes.clear();
		changes.addAll(newInput);
		viewer.setInput(changes);
	}

	public List<Change> getInput(){
		List<Change> changes = new ArrayList<Change>(this.changes);
		return changes;
	}

	public Map<String, Node> getAllScripts() {
		return scripts;
	}

	public Node getScripts(String changeName) {
		scripts.putIfAbsent(changeName, new Node(changeName));
		return scripts.get(changeName);
	}

	public void setScripts(String changeName, Node n) {
		scripts.put(changeName, n);
	}

	public void setScripts(File f) {
		scripts = (Map<String, Node>)readFile(f);
	}

	public void setScripts(Map<String, Node> map) {
		if(scripts == null)
			scripts = new HashMap<>();
		scripts.putAll(map);
	}

	public void setScripts(List<ScriptItem> items) {
		//In order to update change w/o scripts only, identify such changes first.
		Set<String> targetChanges = new HashSet<>();
		for(String changeName : scripts.keySet()) {
			if(!scripts.get(changeName).hasChildren())
				targetChanges.add(changeName);
		}

		updateScripts(items, targetChanges);
		//
		//		for(String changeName : targetChanges) {
		//			Node n = scripts.get(changeName);
		//			for(ScriptItem item : items) {
		//				EditScript script = item.changes.get(changeName).getScript();
		//				Node s = new Node(item.fileName);
		//				n.addChild(s);
		//				//Sort by line, pos.
		//				List<EditOp> editOps = script.getEditOps();
		//				editOps.sort((op1, op2) -> EditOp.compare(op1, op2));
		//				for(EditOp op : editOps) {
		//					op = op.trim();
		//					s.addChild(new Node(op));
		//				}
		//			}
		//		}
	}

	private void readSaved() {
		File changeSaveFile = getSaveFile(CHANGE_SAVE_FILE);
		File scriptSaveFile = getSaveFile(SCRIPT_SAVE_FILE);
		if(changeSaveFile.exists()){
			changes = (List<Change>)readFile(changeSaveFile);
		}else{
			changes = new ArrayList<>();
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath path = workspace.getRoot().getLocation();
			File rootDir = changePath == null ? path.toFile() : new File(changePath);
			if(rootDir.exists() && rootDir.isDirectory()){
				for(File dir : rootDir.listFiles()){
					if(dir.isDirectory() && !dir.getName().startsWith(".")){
						Change change = readChange(dir);
						if(change != null)
							changes.add(change);
					}
				}
			}
		}
		if(scriptSaveFile.exists()) {
			scripts = (Map<String, Node>)readFile(scriptSaveFile);
			if(scripts == null)
				scripts = new HashMap<>();
		} else {
			scripts = new HashMap<>();
			for(Change c : changes)
				scripts.put(c.getName(), new Node(c.getName()));
		}
		Collections.sort(changes, (c1, c2) -> c1.getName().compareTo(c2.getName()));
	}

	private Object readFile(File changeSaveFile) {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		Object obj = null;
		try {
			fis = new FileInputStream(changeSaveFile);
			in = new ObjectInputStream(fis);
			obj = in.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null)
					in.close();
				if(fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

	private void loadChangePath() {
		File pathFile = getChangePathFile();
		if(pathFile.exists()) {
			BufferedReader br = null;
			String savedPath = null;
			try {
				br = new BufferedReader(new FileReader(pathFile));
				savedPath = br.readLine();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(br != null)
						br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(savedPath != null && (new File(savedPath)).exists())
				changePath = savedPath;
		}
	}

	private Change readChange(File dir) {
		String name = dir.getName();
		File oldDir = new File(dir.getAbsolutePath() + File.separator + "old");
		File newDir = new File(dir.getAbsolutePath() + File.separator + "new");
		if(oldDir.exists() && newDir.exists()
				&& oldDir.isDirectory() && newDir.isDirectory()){
			File[] oldFiles = oldDir.listFiles();
			File[] newFiles = newDir.listFiles();
			if(oldFiles.length >= 1 && newFiles.length >= 1){
				File oldFile = getJavaFile(oldFiles);
				File newFile = getJavaFile(newFiles);
				return new Change(name, oldFile.getAbsolutePath(), newFile.getAbsolutePath());
			}
		}

		return null;
	}

	private File getJavaFile(File[] files) {
		for(File f : files){
			if(f.isFile() && f.getName().endsWith(".java"))
				return f;
		}
		return null;
	}

	private void updateScripts(List<ScriptItem> items, Set<String> targetChanges) {
		statMap.clear();
		for(String changeName : targetChanges) {
			statMap.putIfAbsent(changeName, new TreeMap<EditOp, List<String>>());
			TreeMap<EditOp, List<String>> stat = statMap.get(changeName);
			Node n = scripts.get(changeName);

			for(ScriptItem item : items) {
				Change c = item.changes.get(changeName);
				oldTrees.putIfAbsent(changeName, TreeBuilder.buildTree(c.getOldFile().getContent()));
				newTrees.putIfAbsent(changeName, TreeBuilder.buildTree(c.getNewFile().getContent()));
				EditScript script = c.getScript();
				List<EditOp> editOps =  new ArrayList<>();
				for(EditOp op : script.getEditOps()) {
					op = op.trim();
					//					List<EditOp> ops = slice(op, changeName, c);
					//					editOps.addAll(ops);
					editOps.add(op);
				}

				Node s = new Node(item.fileName);
				n.addChild(s);
				//Sort by line, pos.
				editOps.sort((op1, op2) -> EditOp.compare(op1, op2));

				//Update stat., add node.
				for(EditOp op : editOps) {
					s.addChild(new Node(op));
					stat.putIfAbsent(op, new ArrayList<String>());
					stat.get(op).add(item.fileName);
				}
			}
		}
	}

	private static final int FULLY_INCLD = 0;
	private static final int POSTFIX_INCLD = 1;
	private static final int PREFIX_INCLD = 2;
	private static final int NOT_INCLD = 3;

	private int checkOverlap(TreeNode n, int startPos, int endPos) {
		if(n.startPos >= startPos && n.endPos <= endPos) {
			return FULLY_INCLD;
		}else if(n.startPos < startPos && n.endPos > startPos && n.endPos < endPos) {
			return POSTFIX_INCLD;
		}else if(n.startPos < endPos && n.startPos > startPos && n.endPos > endPos) {
			return PREFIX_INCLD;
		}
		return NOT_INCLD;
	}

	private List<EditOp> slice(EditOp op, String changeName, Change change) {
		List<EditOp> ops = new ArrayList<>();
		Tree t = null;
		TreeNode n = null;
		int startPos = -1;
		int len = 0;
		int sLine = 0;
		int eLine = 0;
		String code = null;

		//Only slice insert/delete operations.
		if(op.getType().equals(EditOp.OP_INSERT)) {
			startPos = op.getNewStartPos();
			len = op.getNewLength();
			sLine = op.getNewStartLine();
			eLine = op.getNewEndLine();
			code = op.getNewCode();
			t = newTrees.get(changeName);
		} else if(op.getType().equals(EditOp.OP_DELETE)) {
			startPos = op.getOldStartPos();
			len = op.getOldLength();
			sLine = op.getOldStartLine();
			eLine = op.getOldEndLine();
			code = op.getOldCode();
			t = oldTrees.get(changeName);
		}
		if(t != null && startPos >= 0 && len > 0 && code != null) {
			n = t.getNode(startPos, len);
			if(n != null) {
				int endPos = startPos + len;
				if(n.startPos == startPos && n.endPos == endPos) {
					ops.add(op);
					return ops;
				}

				int cLen = 0;
				for(TreeNode c : n.children) {
					switch(checkOverlap(c, startPos, endPos)) {
					case FULLY_INCLD:
						cLen = c.endPos - c.startPos;
						ops.add(new EditOp(op.getType(), c.startPos, cLen,
								c.startLine, c.endLine, code.substring(c.startPos - startPos, c.endPos - startPos)));
						break;
					case POSTFIX_INCLD:
						cLen = c.endPos - startPos;
						ops.add(new EditOp(op.getType(), startPos, cLen,
								sLine, c.endLine, code.substring(0, cLen)));
						break;
					case PREFIX_INCLD:
						cLen = endPos - c.startPos;
						ops.add(new EditOp(op.getType(), c.startPos, cLen,
								c.startLine, eLine, code.substring(c.startPos - startPos)));
						break;
					}
				}
			}
		}
		if(ops.size() == 0)
			ops.add(op);
		return ops;
	}

	public TreeMap<EditOp, List<String>> getStat(String changeName) {
		return statMap.get(changeName);
	}
}