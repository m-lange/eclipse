package eu.martinlange.launchpad.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;
import eu.martinlange.launchpad.model.DefaultLabelProvider;
import eu.martinlange.launchpad.model.ElementTree;
import eu.martinlange.launchpad.model.ElementTreeData;

public class MoveFolderDialog extends TitleAreaDialog {

	private ElementTreeData fElement;
	
	private FilteredTree fTree;
	private TreeViewer fViewer;
	
	
	public MoveFolderDialog(Shell shell, ElementTreeData element) {
		super(shell);
		fElement = element;
	}
	
	
	@Override
	public void create() {
		super.create();
		setTitle("Folder");
		setMessage("Move to another folder.");
		setTitleImage(Plugin.getSharedImages().getImage(ISharedImages.IMG_WIZBAN_NEWFOLDER));
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
		fViewer.setLabelProvider(new DefaultLabelProvider());
		fViewer.setInput(ElementTree.INSTANCE);
		fViewer.expandToLevel(2);

		return composite;
	}
	
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		Button okButton = getButton(IDialogConstants.OK_ID);
		okButton.setFocus();
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
