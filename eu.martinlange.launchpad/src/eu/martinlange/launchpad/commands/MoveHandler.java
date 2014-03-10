package eu.martinlange.launchpad.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import eu.martinlange.launchpad.model.TreeNode;
import eu.martinlange.launchpad.ui.dialogs.MoveDialog;

public class MoveHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
		{
			Object data = ((IStructuredSelection) selection).getFirstElement();
			if ( !(data instanceof TreeNode) )
				return null;
			
			TreeNode node = (TreeNode) data;

			MoveDialog dialog = new MoveDialog(HandlerUtil.getActiveShell(event), node);
			if (dialog.open() == Dialog.OK) {
//				fPart.refresh();
			}
		}
		
		return null;
	}

}
