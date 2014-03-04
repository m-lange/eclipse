package eu.martinlange.launchpad.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FolderContentProvider implements ITreeContentProvider {

	protected static final Object[] EMPTY = new Object[0];

	protected TreeModel fInput;


	@Override
	public Object[] getChildren(Object parentElement) {
		
		if (parentElement instanceof TreeModel)
			return filter((TreeModel) parentElement).toArray(new TreeNode[0]);
		
		if (parentElement instanceof TreeNode) {
			TreeNode data = (TreeNode) parentElement;
			return filter(data.getChildren()).toArray(new TreeNode[0]);
		}
				
		return EMPTY;
	}


	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}


	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TreeModel)
			return filter((TreeModel) element).size() > 0;
					
		if (element instanceof TreeNode) {
			return filter(((TreeNode) element).getChildren()).size() > 0;
		}
		
		return false;
	}


	@Override
	public Object getParent(Object element) {
		if (element instanceof TreeNode) {
			TreeNode data = (TreeNode) element;
			TreeNode parent = data.getParent();
			return parent == null ? fInput : parent;
		}

		return null;
	}


	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof TreeModel) {
			fInput = (TreeModel) newInput;
		}
	}


	@Override
	public void dispose() {
	}
	
	
	private Collection<TreeNode> filter(Collection<TreeNode> input) {
		List<TreeNode> result = new ArrayList<TreeNode>();
		for(TreeNode e : input) {
			if (e.getData() instanceof String)
				result.add(e);
		}
		return result;
	}
	
	
	private Collection<TreeNode> filter(TreeNode[] input) {
		List<TreeNode> result = new ArrayList<TreeNode>();
		for(TreeNode e : input) {
			if (e.isEditable() && e.getData() instanceof String)
				result.add(e);
		}
		return result;
	}

}
