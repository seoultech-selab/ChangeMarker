package hk.ust.cse.pishon.esgen.views;

import java.util.List;
import java.util.TreeMap;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import hk.ust.cse.pishon.esgen.compare.CompareInput;
import hk.ust.cse.pishon.esgen.model.Change;
import hk.ust.cse.pishon.esgen.model.EditOp;

public class ScriptStatView extends ViewPart {

	public static final String ID = "hk.ust.cse.pishon.esgen.views.scriptstatview";

	private TableViewer viewer;
	private String changeName;
	private TreeMap<EditOp, List<String>> stat = new TreeMap<>();
	private IPartListener2 listener;
	private Font font;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		TableViewerColumn colType = new TableViewerColumn(viewer, SWT.NONE);
		colType.getColumn().setWidth(50);
		colType.getColumn().setText("Type");
		colType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof EditOp){
					return ((EditOp)element).getType();
				}
				return "";
			}
		});

		TableViewerColumn colOldCode = new TableViewerColumn(viewer, SWT.NONE);
		colOldCode.getColumn().setWidth(200);
		colOldCode.getColumn().setText("Old Code");
		colOldCode.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof EditOp){
					return ((EditOp)element).getOldCode();
				}
				return "";
			}
		});

		TableViewerColumn colOldLine = new TableViewerColumn(viewer, SWT.NONE);
		colOldLine.getColumn().setWidth(50);
		colOldLine.getColumn().setText("Line#");
		colOldLine.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof EditOp){
					int line = ((EditOp)element).getOldStartLine();
					return line > 0 ? String.valueOf(line) : "";
				}
				return "";
			}
		});

		TableViewerColumn colOldPos = new TableViewerColumn(viewer, SWT.NONE);
		colOldPos.getColumn().setWidth(50);
		colOldPos.getColumn().setText("S.Pos.");
		colOldPos.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof EditOp){
					int v = ((EditOp)element).getOldStartPos();
					return v > 0 ? String.valueOf(v) : "";
				}
				return "";
			}
		});

		TableViewerColumn colOldLen = new TableViewerColumn(viewer, SWT.NONE);
		colOldLen.getColumn().setWidth(50);
		colOldLen.getColumn().setText("Length");
		colOldLen.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof EditOp){
					int v = ((EditOp)element).getOldLength();
					return v > 0 ? String.valueOf(v) : "";
				}
				return "";
			}
		});

		TableViewerColumn colNewCode = new TableViewerColumn(viewer, SWT.NONE);
		colNewCode.getColumn().setWidth(200);
		colNewCode.getColumn().setText("New Code");
		colNewCode.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof EditOp){
					return ((EditOp)element).getNewCode();
				}
				return "";
			}
		});

		TableViewerColumn colNewLine = new TableViewerColumn(viewer, SWT.NONE);
		colNewLine.getColumn().setWidth(50);
		colNewLine.getColumn().setText("Line#");
		colNewLine.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof EditOp){
					int line = ((EditOp)element).getNewStartLine();
					return line > 0 ? String.valueOf(line) : "";
				}
				return "";
			}
		});

		TableViewerColumn colNewPos = new TableViewerColumn(viewer, SWT.NONE);
		colNewPos.getColumn().setWidth(50);
		colNewPos.getColumn().setText("S.Pos.");
		colNewPos.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof EditOp){
					int v = ((EditOp)element).getNewStartPos();
					return v > 0 ? String.valueOf(v) : "";
				}
				return "";
			}
		});

		TableViewerColumn colNewLen = new TableViewerColumn(viewer, SWT.NONE);
		colNewLen.getColumn().setWidth(50);
		colNewLen.getColumn().setText("Length");
		colNewLen.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof EditOp){
					int v = ((EditOp)element).getNewLength();
					return v > 0 ? String.valueOf(v) : "";
				}
				return "";
			}
		});

		TableViewerColumn colCount = new TableViewerColumn(viewer, SWT.NONE);
		colCount.getColumn().setWidth(50);
		colCount.getColumn().setText("Count");
		colCount.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(stat.containsKey(element))
					return String.valueOf(stat.get(element).size());
				return "";
			}
		});

		TableViewerColumn colScripts = new TableViewerColumn(viewer, SWT.NONE);
		colScripts.getColumn().setWidth(200);
		colScripts.getColumn().setText("Scripts");
		colScripts.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(stat.containsKey(element)) {
					return String.join(",", stat.get(element));
				}
				return "";
			}
		});
		viewer.setInput(stat.keySet());
		
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);
		viewer.getTable().setFont(resourceManager.createFont(FontDescriptor.createFrom("Courier", 12, SWT.NORMAL)));

		//Create context menu.
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTable());
		viewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
		getSite().setSelectionProvider(viewer);
		listener = new IPartListener2() {
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				if(partRef.getPage().getActiveEditor() == null)
					setInput(null);
			}

			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
				if(partRef.getId().equals("org.eclipse.compare.CompareEditor")){
					updateInput(partRef);
				}
			}

			private void updateInput(IWorkbenchPartReference partRef) {
				IEditorInput input = partRef.getPage().getActiveEditor().getEditorInput();
				if(input instanceof CompareInput){
					Change change = ((CompareInput) input).getChange();
					setInput(change.getName());
				}
			}
		};

		getViewSite().getPage().addPartListener(listener);
	}

	public void setInput() {
		setInput(this.changeName);
	}

	public void setInput(String changeName) {
		this.changeName = changeName;
		if(changeName == null) {
			setStat(null);
		} else {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			final IWorkbenchPage page = window.getActivePage();
			ScriptList scriptList = (ScriptList)page.findView(ScriptList.ID);
			if(scriptList != null)
				setStat(scriptList.getStat(changeName));
		}
		viewer.getControl().redraw();
	}

	public String getChangeName() {
		return changeName;
	}

	private void setStat(TreeMap<EditOp, List<String>> stat) {
		this.stat.clear();
		if(stat != null)
			this.stat.putAll(stat);
		viewer.setInput(this.stat.keySet());
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		if(font != null)
			font.dispose();
		getViewSite().getPage().removePartListener(listener);
	}

	public void clearScript() {
		viewer.refresh();
	}

	public void clearAll() {
		viewer.refresh();
	}
}
