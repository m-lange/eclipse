package eu.martinlange.launchpad.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ConfigurationTreeNode implements Iterable<ConfigurationTreeNode> {

	private ConfigurationTreeNode parent;
	private List<ConfigurationTreeNode> children;

	private String id;
	private Object data;
	private boolean editable;


	public ConfigurationTreeNode() {
		id = UUID.randomUUID().toString();
		children = new ArrayList<ConfigurationTreeNode>();
		editable = true;
	}


	public ConfigurationTreeNode(String text) {
		this();
		setData(text);
	}


	public ConfigurationTreeNode(ILaunchConfiguration configuration) {
		this();
		setData(configuration);
	}


	public boolean add(ConfigurationTreeNode element) {
		if (element == null)
			throw new IllegalArgumentException("element");

		if (!children.contains(element)) {
			element.setParent(this);
			return children.add(element);
		}
		return false;
	}


	public boolean add(int index, ConfigurationTreeNode element) {
		if (element == null)
			throw new IllegalArgumentException("element");

		if (!children.contains(element)) {
			element.setParent(this);
			children.add(index, element);
			return true;
		}
		return false;
	}


	public int indexOf(ConfigurationTreeNode element) {
		return children.indexOf(element);
	}


	public boolean remove(ConfigurationTreeNode element) {
		if (element == null)
			throw new IllegalArgumentException("element");

		return children.remove(element);
	}


	public boolean hasChildren() {
		return children.size() > 0;
	}


	public ConfigurationTreeNode[] getChildren() {
		return children.toArray(new ConfigurationTreeNode[0]);
	}


	public ConfigurationTreeNode getParent() {
		return parent;
	}


	public void setParent(ConfigurationTreeNode parent) {
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
		if (!(obj instanceof ConfigurationTreeNode))
			return false;

		ConfigurationTreeNode e = (ConfigurationTreeNode) obj;

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
	public Iterator<ConfigurationTreeNode> iterator() {
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
