package eu.martinlange.launchpad.ui.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import eu.martinlange.launchpad.model.ElementTree;
import eu.martinlange.launchpad.model.ElementTreeData;

public class LaunchpadDropListener extends ViewerDropAdapter {

	private TreeViewer fViewer;


	public LaunchpadDropListener(TreeViewer viewer) {
		super(viewer);
		fViewer = viewer;
	}


	@Override
	public boolean performDrop(Object data) {
		ElementTreeData element = (ElementTreeData) data;
		ElementTreeData target = (ElementTreeData) getCurrentTarget();

		if (target != null && target.equals(element))
			return false;

		element.getParent().remove(element);

		switch (getCurrentLocation()) {
		case LOCATION_BEFORE: {
			int idx = target.getParent().indexOf(target);
			target.getParent().add(idx, element);
			break;
		}
		case LOCATION_AFTER: {
			int idx = target.getParent().indexOf(target);
			target.getParent().add(idx + 1, element);
			break;
		}
		case LOCATION_ON:
			target.add(element);
			break;
		case LOCATION_NONE:
			ElementTree.INSTANCE.getRoot().add(element);
			break;
		}
		fViewer.refresh(true);
		return true;
	}


	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		return true;
	}

}
