package eu.martinlange.launchpad.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import eu.martinlange.launchpad.model.TreeNode;
import eu.martinlange.launchpad.ui.dialogs.RenameDialog;
import eu.martinlange.launchpad.ui.views.LaunchpadPart;

public class RenameHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ISelection selection = HandlerUtil.getCurrentSelection(event);

		TreeNode node = null;
		node = (TreeNode) Platform.getAdapterManager().getAdapter(selection, TreeNode.class);

		if (node == null)
			return null;

		RenameDialog dialog = new RenameDialog(HandlerUtil.getActiveShell(event), node);
		if (dialog.open() == Dialog.OK) {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IViewPart view = page.findView(LaunchpadPart.VIEW_ID);
			if (view instanceof LaunchpadPart)
				((LaunchpadPart) view).refresh();
		}

		return null;
	}

}
