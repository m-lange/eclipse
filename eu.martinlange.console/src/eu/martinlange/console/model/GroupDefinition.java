package eu.martinlange.console.model;

public class GroupDefinition {

	public String name;
	public StyleDefinition style;


	public GroupDefinition() {
	}


	public GroupDefinition(String name, StyleDefinition style) {
		this.name = name;
		this.style = style;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((style == null) ? 0 : style.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GroupDefinition other = (GroupDefinition) obj;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (style == null) {
			if (other.style != null) return false;
		} else if (!style.equals(other.style)) return false;
		return true;
	}

}
