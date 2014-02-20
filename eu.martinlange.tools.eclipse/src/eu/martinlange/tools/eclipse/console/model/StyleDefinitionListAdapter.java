package eu.martinlange.tools.eclipse.console.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StyleDefinitionListAdapter extends XmlAdapter<String, List<String>> {

	private static final String DELIMITER = ";";


	@Override
	public String marshal(List<String> value) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String e : value) {
			if (sb.length() > 0)
				sb.append(DELIMITER);
			sb.append(e);
		}
		return sb.toString();
	}


	@Override
	public List<String> unmarshal(String value) throws Exception {
		List<String> result = new ArrayList<String>();
		for (String e : value.split(DELIMITER)) {
			result.add(e);
		}
		return result;
	}

}