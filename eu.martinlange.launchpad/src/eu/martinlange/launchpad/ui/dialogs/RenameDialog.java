package eu.martinlange.launchpad.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.model.TreeNode;

public class RenameDialog extends Dialog {

	private TreeNode fNode;

	private StyledText fName; 


	public RenameDialog(Shell shell, TreeNode node) {
		super(shell);
		fNode = node;
	}


	@Override
	public void create() {
		super.create();

		getButton(IDialogConstants.OK_ID).setEnabled(isValid());
		fName.selectAll();
		fName.setFocus();
	}


	@Override
	protected void okPressed() {
		fNode.setData(fName.getText());
		super.okPressed();
	}


	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Rename");
		newShell.setMinimumSize(300, 150);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		initializeDialogUnits(composite);

		Label lbl = new Label(composite, SWT.NONE);
		lbl.setText("New Name:");
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd.verticalIndent = 10;
		lbl.setLayoutData(gd);

		fName = new StyledText(composite, SWT.BORDER | SWT.SINGLE);
		fName.setText(fNode.toString());
		fName.setRightMargin(5);
		fName.setLeftMargin(5);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd.widthHint = 300;
		gd.verticalIndent = 10;
		gd.horizontalIndent = 5;
		fName.setLayoutData(gd);
		fName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
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
		if (fName == null || fName.getText().length() == 0) return false;
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
