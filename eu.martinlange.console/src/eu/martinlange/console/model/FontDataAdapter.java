package eu.martinlange.console.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.FontData;

public class FontDataAdapter extends XmlAdapter<String, FontData[]> {

	@Override
	public String marshal(FontData[] value) throws Exception {
		return StringConverter.asString(value);
	}


	@Override
	public FontData[] unmarshal(String value) throws Exception {
		return StringConverter.asFontDataArray(value);
	}

}
