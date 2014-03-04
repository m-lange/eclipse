package eu.martinlange.launchpad.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.eclipse.debug.core.ILaunchConfiguration;

public class TreeNode implements Iterable<TreeNode> {

	private TreeNode parent;
	private List<TreeNode> children;

	private String id;
	private Object data;
	private boolean editable;


	public TreeNode() {
		id = UUID.randomUUID().toString();
		children = new ArrayList<TreeNode>();
		editable = true;
	}


	public TreeNode(String text) {
		this();
		setData(text);
	}


	public TreeNode(ILaunchConfiguration configuration) {
		this();
		setData(configuration);
	}


	public boolean add(TreeNode element) {
		if (element == null)
			throw new IllegalArgumentException("element");

		if (!children.contains(element)) {
			element.setParent(this);
			return children.add(element);
		}
		return false;
	}


	public boolean add(int index, TreeNode element) {
		if (element == null)
			throw new IllegalArgumentException("element");

		if (!children.contains(element)) {
			element.setParent(this);
			children.add(index, element);
			return true;
		}
		return false;
	}


	public int indexOf(TreeNode element) {
		return children.indexOf(element);
	}


	public boolean remove(TreeNode element) {
		if (element == null)
			throw new IllegalArgumentException("element");

		return children.remove(element);
	}


	public boolean hasChildren() {
		return children.size() > 0;
	}


	public TreeNode[] getChildren() {
		return children.toArray(new TreeNode[0]);
	}


	public TreeNode getParent() {
		return parent;
	}


	public void setParent(TreeNode parent) {
		if (parent == null)
			throw new IllegalArgumentException("parent");

		this.parent = parent;
	}


	public String getId() {
		return id;
	}


	public Object getData() {
		return data;
	}


	public void setData(Object data) {
		this.data = data;
	}
	
	
	public boolean isEditable() {
		return editable;
	}
	
	
	protected void setEditable(boolean editable) {
		this.editable = editable;
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TreeNode))
			return false;

		TreeNode e = (TreeNode) obj;

		if (getData() instanceof String && e.getData() instanceof String) {
			String s1 = (String) getId();
			String s2 = (String) e.getId();
			return s1.equals(s2);
		}

		if (getData() instanceof ILaunchConfiguration && e.getData() instanceof ILaunchConfiguration) {
			ILaunchConfiguration c1 = (ILaunchConfiguration) getData();
			ILaunchConfiguration c2 = (ILaunchConfiguration) e.getData();
			return c1.getName().equals(c2.getName());
		}

		return false;
	}


	@Override
	public Iterator<TreeNode> iterator() {
		return children.iterator();
	}


	@Override
	public String toString() {
		if (data instanceof String)
			return (String) data;
		if (data instanceof ILaunchConfiguration)
			return ((ILaunchConfiguration) data).getName();

		return super.toString();
	}

}
