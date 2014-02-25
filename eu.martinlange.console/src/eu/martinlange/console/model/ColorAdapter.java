package eu.martinlange.console.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

public class ColorAdapter extends XmlAdapter<String, RGB> {

	@Override
	public String marshal(RGB value) throws Exception {
		return StringConverter.asString(value);
	}


	@Override
	public RGB unmarshal(String value) throws Exception {
		return StringConverter.asRGB(value);
	}

}
