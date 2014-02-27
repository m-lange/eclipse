package eu.martinlange.launchpad.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;
import eu.martinlange.launchpad.ui.dialogs.NewFolderDialog;
import eu.martinlange.launchpad.ui.views.LaunchpadView;

public class NewFolderAction extends Action {

	private LaunchpadView fPart;


	public NewFolderAction(LaunchpadView part) {
		super("New Folder...", IAction.AS_PUSH_BUTTON);

		fPart = part;
		setImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_NEWFOLDER));
		setDisabledImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_NEWFOLDER_DISABLED));
	}


	@Override
	public void run() {
		NewFolderDialog dialog = new NewFolderDialog(fPart.getSite().getShell());
		if (dialog.open() == Dialog.OK) {
			fPart.refresh();
		}
	}

}
