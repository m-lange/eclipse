package eu.martinlange.launchpad.ui.views;

import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;
import eu.martinlange.launchpad.model.TreeNode;

public class LaunchpadLabelProvider extends LabelProvider implements ILabelProvider {

	private static IDebugModelPresentation fDelegate;

	static {
		fDelegate = DebugUITools.newDebugModelPresentation();
	}


	@Override
	public String getText(Object element) {
		if (element instanceof TreeNode) {
			TreeNode e = (TreeNode) element;
			if (e.getData() instanceof String)
				return (String) e.getData();

			return fDelegate.getText(e.getData());
		}
		return fDelegate.getText(element);
	}


	@Override
	public Image getImage(Object element) {
		if (element instanceof TreeNode) {
			TreeNode e = (TreeNode) element;
			if (e.getData() instanceof String)
				return Plugin.getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);

			return fDelegate.getImage(e.getData());
		}

		return fDelegate.getImage(element);
	}

}
