package eu.martinlange.launchpad.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FolderContentProvider implements ITreeContentProvider {

	protected static final Object[] EMPTY = new Object[0];

	protected ElementTree fInput;


	@Override
	public Object[] getChildren(Object parentElement) {
		
		if (parentElement instanceof ElementTree)
			return filter((ElementTree) parentElement).toArray(new ElementTreeData[0]);
		
		if (parentElement instanceof ElementTreeData) {
			ElementTreeData data = (ElementTreeData) parentElement;
			return filter(data.getChildren()).toArray(new ElementTreeData[0]);
		}
				
		return EMPTY;
	}


	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}


	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ElementTree)
			return filter((ElementTree) element).size() > 0;
					
		if (element instanceof ElementTreeData) {
			return filter(((ElementTreeData) element).getChildren()).size() > 0;
		}
		
		return false;
	}


	@Override
	public Object getParent(Object element) {
		if (element instanceof ElementTreeData) {
			ElementTreeData data = (ElementTreeData) element;
			ElementTreeData parent = data.getParent();
			return parent == null ? fInput : parent;
		}

		return null;
	}


	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof ElementTree) {
			fInput = (ElementTree) newInput;
		}
	}


	@Override
	public void dispose() {
	}
	
	
	private Collection<ElementTreeData> filter(Collection<ElementTreeData> input) {
		List<ElementTreeData> result = new ArrayList<ElementTreeData>();
		for(ElementTreeData e : input) {
			if (e.getData() instanceof String)
				result.add(e);
		}
		return result;
	}
	
	
	private Collection<ElementTreeData> filter(ElementTreeData[] input) {
		List<ElementTreeData> result = new ArrayList<ElementTreeData>();
		for(ElementTreeData e : input) {
			if (e.isEditable() && e.getData() instanceof String)
				result.add(e);
		}
		return result;
	}

}
