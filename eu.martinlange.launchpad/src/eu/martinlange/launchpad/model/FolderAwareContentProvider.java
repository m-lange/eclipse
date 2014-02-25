package eu.martinlange.launchpad.model;

import org.eclipse.debug.core.ILaunchManager;

public class FolderAwareContentProvider extends DefaultContentProvider {

	private ElementTree fElementTree;
	
	
	public FolderAwareContentProvider(ElementTree model) {
		fElementTree = model;
	}
	
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ILaunchManager)
			return fElementTree.toArray(new ElementTreeData[0]);
		
		else if (parentElement instanceof ElementTreeData) {
			ElementTreeData data = (ElementTreeData) parentElement;
			return data.getChildren();
		}
		
		return super.getChildren(parentElement);
	}
	
	
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ILaunchManager)
			return fElementTree.size() > 0;
		
		else if (element instanceof ElementTreeData) {
			ElementTreeData data = (ElementTreeData) element;
			return data.hasChildren();
		}
		
		return super.hasChildren(element);
	}
	
	
	@Override
	public Object getParent(Object element) {
		if (element instanceof ElementTreeData) {
			ElementTreeData data = (ElementTreeData) element;
			ElementTreeData parent = data.getParent();
			
			if (parent == null)
				return fLaunchManager;
			else
				return parent;
		}

		return super.getParent(element);
	}
	
}
