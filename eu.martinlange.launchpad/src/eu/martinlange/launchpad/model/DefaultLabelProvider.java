package eu.martinlange.launchpad.model;

import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;

public class DefaultLabelProvider extends LabelProvider implements ILabelProvider {

	private static IDebugModelPresentation fDelegate;

	static {
		fDelegate = DebugUITools.newDebugModelPresentation();
	}
	
	
	@Override
	public String getText(Object element) {
		if (element instanceof ElementTreeData) {
			ElementTreeData e = (ElementTreeData) element;
			if (e.getType() == String.class)
				return (String) e.getData();
			
			return fDelegate.getText(e.getData());
		}
		return fDelegate.getText(element);
	}
	
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof ElementTreeData) {
			ElementTreeData e = (ElementTreeData) element;
			if (e.getType() == String.class)
				return Plugin.getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			
			return fDelegate.getImage(e.getData());
		}
		
		return fDelegate.getImage(element);
	}

}
