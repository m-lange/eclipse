package eu.martinlange.launchpad.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import eu.martinlange.launchpad.model.ElementTreeData;
import eu.martinlange.launchpad.ui.dialogs.RenameFolderDialog;
import eu.martinlange.launchpad.ui.views.LaunchpadView;

public class RenameFolderAction extends Action {

	private LaunchpadView fPart;


	public RenameFolderAction(LaunchpadView part) {
		super("Rename...", IAction.AS_PUSH_BUTTON);

		fPart = part;
	}


	@Override
	public void run() {
		ISelection selection = fPart.getSelection();
		if (!(selection instanceof IStructuredSelection) || ((IStructuredSelection) selection).size() != 1)
			return;

		Object obj = ((IStructuredSelection) selection).getFirstElement();
		RenameFolderDialog dialog = new RenameFolderDialog(fPart.getSite().getShell(), (ElementTreeData) obj);
		if (dialog.open() == Dialog.OK) {
			fPart.refresh();
		}
	}


	@Override
	public boolean isEnabled() {
		ISelection selection = fPart.getSelection();
		if (!(selection instanceof IStructuredSelection) || ((IStructuredSelection) selection).size() != 1)
			return false;

		Object obj = ((IStructuredSelection) selection).getFirstElement();
		return obj instanceof ElementTreeData && ((ElementTreeData) obj).getData() instanceof String;
	}

}
