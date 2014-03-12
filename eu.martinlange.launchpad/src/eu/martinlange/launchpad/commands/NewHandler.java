package eu.martinlange.launchpad.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import eu.martinlange.launchpad.model.TreeNode;
import eu.martinlange.launchpad.ui.dialogs.NewDialog;
import eu.martinlange.launchpad.ui.views.LaunchpadPart;

public class NewHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
		{
			Object data = ((IStructuredSelection) selection).getFirstElement();
			if ( !(data instanceof TreeNode) )
				return null;
			
			TreeNode node = (TreeNode) data;

			NewDialog dialog = new NewDialog(HandlerUtil.getActiveShell(event), node);
			if (dialog.open() == Dialog.OK) {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IViewPart view = page.findView(LaunchpadPart.VIEW_ID);
				if (view instanceof LaunchpadPart)
					((LaunchpadPart) view).refresh();
			};
		}
		
		return null;
	}

}
