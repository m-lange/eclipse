package eu.martinlange.launchpad.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ElementTreeData implements Iterable<ElementTreeData> {

	private ElementTreeData parent;
	private List<ElementTreeData> children;

	private Object data;


	public ElementTreeData() {
		children = new ArrayList<ElementTreeData>();
	}


	public ElementTreeData(String text) {
		this();
		setData(text);
	}


	public ElementTreeData(ILaunchConfiguration configuration) {
		this();
		setData(configuration);
	}


	public boolean add(ElementTreeData element) {
		if (element == null)
			throw new IllegalArgumentException("element");

		element.setParent(this);
		return children.add(element);
	}


	public boolean remove(ElementTreeData element) {
		if (element == null)
			throw new IllegalArgumentException("element");

		return children.remove(element);
	}


	public boolean hasChildren() {
		return children.size() > 0;
	}


	public ElementTreeData[] getChildren() {
		return children.toArray(new ElementTreeData[0]);
	}


	public ElementTreeData getParent() {
		return parent;
	}


	public void setParent(ElementTreeData parent) {
		if (parent == null)
			throw new IllegalArgumentException("parent");

		this.parent = parent;
	}


	public Object getData() {
		return data;
	}


	public void setData(Object data) {
		this.data = data;
	}


	public boolean add(ILaunchConfiguration configuration) {
		return false;
	}


	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ElementTreeData))
			return false;

		ElementTreeData e = (ElementTreeData) obj;

		if (getData() instanceof String && e.getData() instanceof String) {
			String s1 = (String) getData();
			String s2 = (String) e.getData();
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
	public Iterator<ElementTreeData> iterator() {
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
