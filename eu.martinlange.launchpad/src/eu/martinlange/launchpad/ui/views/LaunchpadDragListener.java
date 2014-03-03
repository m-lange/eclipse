package eu.martinlange.launchpad.ui.views;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import eu.martinlange.launchpad.model.ElementTreeData;

public class LaunchpadDragListener implements DragSourceListener {

	private TreeViewer fViewer;


	public LaunchpadDragListener(TreeViewer viewer) {
		fViewer = viewer;
	}


	@Override
	public void dragStart(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		Object obj = selection.getFirstElement();
		
		if ( !(obj instanceof ElementTreeData) || !((ElementTreeData) obj).isEditable())
			event.doit = false;
	}


	@Override
	public void dragFinished(DragSourceEvent event) {
	}


	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) fViewer.getSelection();
		Object obj = selection.getFirstElement();

		if (LaunchpadTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = obj;
		}
	}

}
