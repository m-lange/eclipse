package eu.martinlange.launchpad.model;

import java.util.ArrayList;
import java.util.List;

public class ElementTreeData {

	private ElementTreeData parent;
	private List<ElementTreeData> children;

	private Object data;


	public ElementTreeData() {
		children = new ArrayList<ElementTreeData>();
	}


	public boolean add(ElementTreeData element) {
		if (element == null)
			throw new IllegalArgumentException("element");

		element.setParent(this);
		return children.add(element);
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


	public Class<?> getType() {
		return data.getClass();
	}

}
