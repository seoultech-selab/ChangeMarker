package hk.ust.cse.pishon.esgen.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

import hk.ust.cse.pishon.esgen.compare.CompareInput;
import hk.ust.cse.pishon.esgen.model.Change;
import hk.ust.cse.pishon.esgen.model.EditOp;
import hk.ust.cse.pishon.esgen.model.EditScript;

public class ScriptView extends ViewPart {

	public static final String ID = "hk.ust.cse.pishon.esgen.views.scriptview";

	private TableViewer viewer;
	private List<EditOp> editOps = new ArrayList<EditOp>();
	private IPartListener2 listener;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		Font font = viewer.getTable().getFont();
		FontData[] fontData = font.getFontData();
		for(FontData fd : fontData)
			fd.setHeight(12);
		viewer.getTable().setFont(new Font(viewer.getTable().getDisplay(), fontData));
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
		colOldCode.getColumn().setWidth(300);
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

		TableViewerColumn colNewCode = new TableViewerColumn(viewer, SWT.NONE);
		colNewCode.getColumn().setWidth(300);
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
		viewer.setInput(editOps);

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
					setInput(change.getScript());
				}
			}
		};

		getViewSite().getPage().addPartListener(listener);
	}

	public void addEditOp(EditOp op){
		editOps.add(op);
		viewer.refresh();
	}

	public void removeEditOp(EditOp element) {
		editOps.remove(element);
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

	public void setInput(EditScript script) {
		if (script != null) {
			editOps = script.getEditOps();
			viewer.setInput(editOps);
		}else{
			editOps = null;
			viewer.setInput(null);
		}
	}

	public void clearAll() {
		editOps.clear();
		viewer.refresh();
	}

}
