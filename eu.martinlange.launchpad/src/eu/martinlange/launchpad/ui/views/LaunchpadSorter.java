package eu.martinlange.launchpad.ui.views;

import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import eu.martinlange.launchpad.model.ElementTreeData;

public class LaunchpadSorter extends ViewerSorter {

	public LaunchpadSorter() {
	}


	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {

		if (!(obj1 instanceof ElementTreeData) || !(obj2 instanceof ElementTreeData)) {
			String s1 = getLabel(viewer, obj1);
			String s2 = getLabel(viewer, obj2);
			return s1.compareTo(s2);
		}

		ElementTreeData e1 = (ElementTreeData) obj1;
		ElementTreeData e2 = (ElementTreeData) obj2;

		if (!e1.isEditable())
			return 1;

		if (!e2.isEditable())
			return -1;

		if (e1.getData() instanceof String && !(e2.getData() instanceof String))
			return -1;

		if (e2.getData() instanceof String && !(e1.getData() instanceof String))
			return 1;

		String s1 = getLabel(viewer, obj1);
		String s2 = getLabel(viewer, obj2);

		return s1.compareTo(s2);
	}


	private String getLabel(Viewer viewer, Object obj) {
		String text;
		if (viewer == null || !(viewer instanceof ContentViewer)) {
			text = obj.toString();
		} else {
			IBaseLabelProvider prov = ((ContentViewer) viewer).getLabelProvider();
			if (prov instanceof ILabelProvider) {
				ILabelProvider lprov = (ILabelProvider) prov;
				text = lprov.getText(obj);
			} else {
				text = obj.toString();
			}
		}
		if (text == null) {
			text = "";
		}
		return text;
	}

}
