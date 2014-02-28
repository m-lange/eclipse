package eu.martinlange.launchpad.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import eu.martinlange.launchpad.model.DefaultLabelProvider;
import eu.martinlange.launchpad.model.ElementTree;
import eu.martinlange.launchpad.model.ElementTreeData;
import eu.martinlange.launchpad.model.FolderContentProvider;
import eu.martinlange.launchpad.ui.views.LaunchpadView;

public class NewAction extends Action {

	private LaunchpadView fPart;


	public NewAction(LaunchpadView part) {
		super("New Category...", IAction.AS_PUSH_BUTTON);

		fPart = part;
		setImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_NEWFOLDER));
		setDisabledImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_NEWFOLDER_DISABLED));
	}


	@Override
	public void run() {
		ISelection selection = fPart.getSelection();
		if (!(selection instanceof IStructuredSelection) || ((IStructuredSelection) selection).size() != 1)
			return;

		Object obj = ((IStructuredSelection) selection).getFirstElement();
		NewDialog dialog = new NewDialog(fPart.getSite().getShell(), obj);
		if (dialog.open() == Dialog.OK) {
			fPart.refresh();
		}
	}


	class NewDialog extends TitleAreaDialog {

		private StyledText fName;
		private FilteredTree fTree;
		private TreeViewer fViewer;
		private Object fSelection;


		public NewDialog(Shell shell, Object selection) {
			super(shell);
			fSelection = selection;
		}


		@Override
		public void create() {
			super.create();

			setTitle("Category");
			setMessage("Create a new category.");
			setTitleImage(Plugin.getSharedImages().getImage(ISharedImages.IMG_WIZBAN_NEWFOLDER));

			getButton(IDialogConstants.OK_ID).setEnabled(isValid());
			fName.setFocus();

			if (fSelection instanceof ElementTreeData)
			{
				ElementTreeData element = (ElementTreeData) fSelection;
				if (element.getData() instanceof String)
					fViewer.setSelection(new StructuredSelection(element), true);
				else
					fViewer.setSelection(new StructuredSelection(element.getParent()), true);
			}
		}


		@Override
		protected void okPressed() {
			ISelection selection = fViewer.getSelection();
			if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
				ElementTreeData parent = (ElementTreeData) ((IStructuredSelection) selection).getFirstElement();
				parent.add(new ElementTreeData(fName.getText()));
			}

			super.okPressed();
		}


		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("New");
			newShell.setMinimumSize(300, 350);
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
			lbl.setText("Select the parent category:");
			GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
			gd.verticalIndent = 5;
			lbl.setLayoutData(gd);

			fTree = new FilteredTree(composite, SWT.FULL_SELECTION | SWT.BORDER, new PatternFilter(), true);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
			gd.verticalIndent = 3;
			gd.heightHint = 350;
			gd.widthHint = 400;
			fTree.setLayoutData(gd);

			fViewer = fTree.getViewer();
			fViewer.setContentProvider(new FolderContentProvider());
			fViewer.setLabelProvider(new DefaultLabelProvider());
			fViewer.setInput(ElementTree.INSTANCE);
			fViewer.expandToLevel(2);
			fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					getButton(IDialogConstants.OK_ID).setEnabled(isValid());
				}
			});

			lbl = new Label(composite, SWT.NONE);
			lbl.setText("Name:");
			gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd.verticalIndent = 10;
			lbl.setLayoutData(gd);

			fName = new StyledText(composite, SWT.BORDER | SWT.SINGLE);
			fName.setRightMargin(5);
			fName.setLeftMargin(5);
			gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
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
			if (fViewer == null || fViewer.getSelection().isEmpty()) return false;
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

}
