package eu.martinlange.launchpad.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;

public class CollapseAllAction extends Action {

	private AbstractTreeViewer fViewer;


	public CollapseAllAction(AbstractTreeViewer viewer) {
		super("Collapse All", IAction.AS_PUSH_BUTTON);
		
		fViewer = viewer;
		setImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		setDisabledImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL_DISABLED));
	}


	@Override
	public void run() {
		fViewer.collapseAll();
	}

}
