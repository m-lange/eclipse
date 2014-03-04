package eu.martinlange.launchpad.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FolderContentProvider implements ITreeContentProvider {

	protected static final Object[] EMPTY = new Object[0];

	protected ConfigurationTree fInput;


	@Override
	public Object[] getChildren(Object parentElement) {
		
		if (parentElement instanceof ConfigurationTree)
			return filter((ConfigurationTree) parentElement).toArray(new ConfigurationTreeNode[0]);
		
		if (parentElement instanceof ConfigurationTreeNode) {
			ConfigurationTreeNode data = (ConfigurationTreeNode) parentElement;
			return filter(data.getChildren()).toArray(new ConfigurationTreeNode[0]);
		}
				
		return EMPTY;
	}


	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}


	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ConfigurationTree)
			return filter((ConfigurationTree) element).size() > 0;
					
		if (element instanceof ConfigurationTreeNode) {
			return filter(((ConfigurationTreeNode) element).getChildren()).size() > 0;
		}
		
		return false;
	}


	@Override
	public Object getParent(Object element) {
		if (element instanceof ConfigurationTreeNode) {
			ConfigurationTreeNode data = (ConfigurationTreeNode) element;
			ConfigurationTreeNode parent = data.getParent();
			return parent == null ? fInput : parent;
		}

		return null;
	}


	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof ConfigurationTree) {
			fInput = (ConfigurationTree) newInput;
		}
	}


	@Override
	public void dispose() {
	}
	
	
	private Collection<ConfigurationTreeNode> filter(Collection<ConfigurationTreeNode> input) {
		List<ConfigurationTreeNode> result = new ArrayList<ConfigurationTreeNode>();
		for(ConfigurationTreeNode e : input) {
			if (e.getData() instanceof String)
				result.add(e);
		}
		return result;
	}
	
	
	private Collection<ConfigurationTreeNode> filter(ConfigurationTreeNode[] input) {
		List<ConfigurationTreeNode> result = new ArrayList<ConfigurationTreeNode>();
		for(ConfigurationTreeNode e : input) {
			if (e.isEditable() && e.getData() instanceof String)
				result.add(e);
		}
		return result;
	}

}
