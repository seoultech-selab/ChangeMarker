package hk.ust.cse.pishon.esgen.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.compare.CompareUI;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import hk.ust.cse.pishon.esgen.compare.CompareInput;
import hk.ust.cse.pishon.esgen.model.Change;
import hk.ust.cse.pishon.esgen.model.EditOp;
import hk.ust.cse.pishon.esgen.model.Node;

public class MultiScriptView extends ViewPart {

	public static final String ID = "hk.ust.cse.pishon.esgen.views.multiscriptview";

	private TreeViewer viewer;
	private String changeName;
	private Node scripts = null;
	private Node curr;
	private IPartListener2 listener;

	@Override
	public void createPartControl(Composite parent) {
		ResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);
		
		viewer = new TreeViewer(parent);
		viewer.setAutoExpandLevel(2);
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		viewer.setContentProvider(new ITreeContentProvider() {			
			@Override
			public boolean hasChildren(Object element) {
				if(element instanceof Node) {
					return ((Node)element).hasChildren();
				}
				return false;
			}
			
			@Override
			public Object getParent(Object element) {
				if(element instanceof Node) {
					return ((Node)element).getParent();
				}
				return null;
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return getChildren(inputElement);
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				if(parentElement instanceof Node) {
					return ((Node)parentElement).getChildren();
				}
				return null;
			}
		});
	
		viewer.getTree().setFont(resourceManager.createFont(FontDescriptor.createFrom("Courier", 12, SWT.NORMAL)));
		
		TreeViewerColumn colName = new TreeViewerColumn(viewer, SWT.NONE);
		colName.getColumn().setWidth(80);
		colName.getColumn().setText("Name");
		colName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof Node) {
					Node n = (Node)element;
					if(n.value instanceof String)
						return n ==  curr ? n.value + " - current" : (String)n.value;
				}
				return "";
			}
		});

		TreeViewerColumn colType = new TreeViewerColumn(viewer, SWT.NONE);
		colType.getColumn().setWidth(50);
		colType.getColumn().setText("Type");
		colType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof Node) {
					Node n = (Node)element;
					if(n.value instanceof EditOp)
						return ((EditOp)n.value).getType();
				}
				return "";
			}
		});

		TreeViewerColumn colOldCode = new TreeViewerColumn(viewer, SWT.NONE);
		colOldCode.getColumn().setWidth(200);
		colOldCode.getColumn().setText("Old Code");
		colOldCode.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof Node) {
					Node n = (Node)element;
					if(n.value instanceof EditOp)
						return ((EditOp)n.value).getOldCode();
				}
				return "";
			}
		});

		TreeViewerColumn colOldLine = new TreeViewerColumn(viewer, SWT.NONE);
		colOldLine.getColumn().setWidth(50);
		colOldLine.getColumn().setText("Line#");
		colOldLine.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {				
				if(element instanceof Node) {
					Node n = (Node)element;
					if(n.value instanceof EditOp) {
						int line = ((EditOp)n.value).getOldStartLine();
						return line > 0 ? String.valueOf(line) : "";
					}					
				}
				return "";
			}
		});

		TreeViewerColumn colNewCode = new TreeViewerColumn(viewer, SWT.NONE);
		colNewCode.getColumn().setWidth(200);
		colNewCode.getColumn().setText("New Code");
		colNewCode.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof Node) {
					Node n = (Node)element;
					if(n.value instanceof EditOp)
						return ((EditOp)n.value).getNewCode();
				}
				return "";
			}
		});

		TreeViewerColumn colNewLine = new TreeViewerColumn(viewer, SWT.NONE);
		colNewLine.getColumn().setWidth(50);
		colNewLine.getColumn().setText("Line#");
		colNewLine.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof Node) {
					Node n = (Node)element;
					if(n.value instanceof EditOp) {
						int line = ((EditOp)n.value).getNewStartLine();
						return line > 0 ? String.valueOf(line) : "";
					}					
				}
				return "";
			}
		});
		viewer.setInput(scripts);
		
		//Double-click to select a script.
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IWorkbenchPage page = window.getActivePage();		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				if(selection.getFirstElement() instanceof Node){
					Node n = (Node)selection.getFirstElement();
					if(n.value instanceof String) {
						MultiScriptView multiScriptViewer = (MultiScriptView)page.findView(MultiScriptView.ID);
						if(multiScriptViewer != null)
							multiScriptViewer.setCurrent(n);
					}											
				}
			}
		});

		//Create context menu.
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(menu);
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

	protected void setCurrent(Node n) {
		if(scripts == null) {
			scripts = new Node(changeName);
			scripts.addChild(n);
			curr = n;
		} else {
			curr = n;
		}
		viewer.refresh();
	}

	public void addEditOp(EditOp op){
		if(curr == null)
			createNewScript();
		curr.addChild(new Node(op));
		viewer.refresh();
	}
	
	public void createNewScript() {
		int num = scripts != null ? scripts.size() : 0;
		curr = new Node("Script"+num);
		scripts.addChild(curr);
	}
	
	public void removeEditOp(Node n) {
		if(curr != null && n != null) {
			n.getParent().children.remove(n);
		}
		viewer.refresh();
	}
	
	public void refresh() {
		viewer.refresh();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		getViewSite().getPage().removePartListener(listener);
	}
	
	public String getChangeName() {
		return changeName;
	}

	public void setInput(String changeName) {
		this.changeName = changeName;
		if(changeName == null) {
			changeName = null;
			scripts = null;
		} else {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			final IWorkbenchPage page = window.getActivePage();	
			ChangeView changeView = (ChangeView)page.findView(ChangeView.ID);		
			if(changeView != null) {
				Node n = changeView.getScripts(changeName);
				if(n != null)
					scripts = n;
			}
			curr = scripts.hasChildren() ? scripts.children.get(scripts.size()-1) : null;
		}
		viewer.setInput(scripts);
	}

	public void clearAll() {
		scripts = null;
		viewer.refresh();
	}

	public void removeScript(Node n) {
		scripts.children.remove(n);
		viewer.refresh();
	}

}