package eu.martinlange.launchpad.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;
import eu.martinlange.launchpad.model.FolderContentProvider;
import eu.martinlange.launchpad.model.TreeModel;
import eu.martinlange.launchpad.model.TreeNode;
import eu.martinlange.launchpad.ui.views.LaunchpadLabelProvider;

public class MoveDialog extends TitleAreaDialog {

	private TreeNode fElement;

	private FilteredTree fTree;
	private TreeViewer fViewer;


	public MoveDialog(Shell shell, TreeNode element) {
		super(shell);
		fElement = element;
	}


	@Override
	public void create() {
		super.create();

		setTitle("Category");
		setMessage("Move to another category.");
		setTitleImage(Plugin.getSharedImages().getImage(ISharedImages.IMG_WIZBAN_NEWFOLDER));

		getButton(IDialogConstants.OK_ID).setEnabled(isValid());
		fTree.setFocus();

		fViewer.setSelection(new StructuredSelection(fElement.getParent()), true);
	}


	@Override
	protected void okPressed() {
		ISelection selection = fViewer.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			TreeNode element = (TreeNode) ((IStructuredSelection) selection).getFirstElement();
			fElement.getParent().remove(fElement);
			element.add(fElement);
		}

		super.okPressed();
	}


	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Move");
		newShell.setMinimumSize(300, 300);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		initializeDialogUnits(composite);

		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText(String.format("Choose destination for '%s':", fElement.toString()));
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd.verticalIndent = 5;
		lbl.setLayoutData(gd);

		fTree = new FilteredTree(composite, SWT.FULL_SELECTION | SWT.BORDER, new PatternFilter(), true);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.verticalIndent = 3;
		gd.heightHint = 400;
		gd.widthHint = 400;
		fTree.setLayoutData(gd);

		fViewer = fTree.getViewer();
		fViewer.setContentProvider(new FolderContentProvider());
		fViewer.setLabelProvider(new LaunchpadLabelProvider());
		fViewer.setInput(TreeModel.INSTANCE);
		fViewer.expandToLevel(2);
		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getButton(IDialogConstants.OK_ID).setEnabled(isValid());
			}
		});

		return composite;
	}


	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}


	protected boolean isValid() {
		if (fViewer == null || fViewer.getSelection().isEmpty()) return false;
		
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		TreeNode parent = (TreeNode) selection.getFirstElement();
		
		while (parent != null) {
			if (parent == fElement)
				return false;
			parent = parent.getParent();
		}
		
		return true;
	}


	@Override
	protected boolean isResizable() {
		return true;
	}


	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = Plugin.getDefault().getDialogSettings().getSection(getClass().getName());
		if (settings == null)
			settings = Plugin.getDefault().getDialogSettings().addNewSection(getClass().getName());
		return settings;
	}


	protected int getDialogBoundsStrategy() {
		return DIALOG_PERSISTLOCATION | DIALOG_PERSISTSIZE;
	}

}