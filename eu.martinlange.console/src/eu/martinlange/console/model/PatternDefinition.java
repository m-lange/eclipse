package eu.martinlange.console.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "pattern")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatternDefinition {

	@XmlElement(name = "id")
	public String id;

	@XmlElement(name = "index")
	public int index;

	@XmlElement(name = "name")
	public String name;

	@XmlElement(name = "regex")
	public String regex;

	@XmlElement(name = "flags")
	public int flags;

	@XmlElement(name = "preview")
	public boolean preview;

	@XmlElement(name = "style")
	@XmlJavaTypeAdapter(StyleDefinitionListAdapter.class)
	public List<String> styles;


	public PatternDefinition() {
		this.id = UUID.randomUUID().toString();
		this.index = Integer.MAX_VALUE;
		this.name = "";
		this.regex = "";
		this.flags = 0;
		this.preview = false;
		this.styles = new ArrayList<String>();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + flags;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + index;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((regex == null) ? 0 : regex.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PatternDefinition other = (PatternDefinition) obj;
		if (flags != other.flags) return false;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		if (index != other.index) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (regex == null) {
			if (other.regex != null) return false;
		} else if (!regex.equals(other.regex)) return false;
		return true;
	}

}
