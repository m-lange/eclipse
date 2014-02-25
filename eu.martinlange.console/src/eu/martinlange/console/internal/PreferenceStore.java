package eu.martinlange.console.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import eu.martinlange.console.Plugin;

public class PreferenceStore extends ScopedPreferenceStore {

	public PreferenceStore(IScopeContext context, String qualifier) {
		super(context, qualifier);
	}


	public PreferenceStore(IScopeContext context, String qualifier, String defaultQualifierPath) {
		super(context, qualifier, defaultQualifierPath);
	}


	public <T> List<T> getList(String name, Class<?> clazz) {
		String xml = this.getString(name);
		return unmarshal(xml, clazz);
	}


	public <T> void setValue(String name, List<T> value, Class<?> clazz) {
		String xml = marshal(value, name, clazz);
		this.setValue(name, xml);
	}


	public <T> void setDefault(String name, List<T> value, Class<?> clazz) {
		String xml = marshal(value, name, clazz);
		this.setDefault(name, xml);
	}


	protected <T> String marshal(List<T> elements, String name, Class<?> clazz) {
		try {
			if (elements.size() == 0) { return String.format(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><%s/>",
					name); }

			ListWrapper<T> wrapper = new ListWrapper<T>(elements);

			JAXBContext context = JAXBContext.newInstance(ListWrapper.class, clazz);

			@SuppressWarnings("rawtypes")
			JAXBElement<ListWrapper> e = new JAXBElement<ListWrapper>(new QName(name), ListWrapper.class, wrapper);

			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			m.marshal(e, out);

			return new String(out.toByteArray());
		} catch (JAXBException e) {
			Plugin.log(e);
			return null;
		}
	}


	protected <T> List<T> unmarshal(String xml, Class<?> clazz) {
		try {
			JAXBContext context = JAXBContext.newInstance(ListWrapper.class, clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			StreamSource source = new StreamSource(in);

			@SuppressWarnings("unchecked")
			ListWrapper<T> wrapper = (ListWrapper<T>) unmarshaller.unmarshal(source, ListWrapper.class).getValue();

			return wrapper.getItems();
		} catch (JAXBException e) {
			Plugin.log(e);
			return null;
		} catch (UnsupportedEncodingException e) {
			Plugin.log(e);
			return null;
		}
	}


	static class ListWrapper<T> {

		private List<T> items;


		public ListWrapper() {
			items = new ArrayList<T>();
		}


		public ListWrapper(List<T> items) {
			this.items = items;
		}


		@XmlAnyElement(lax = true)
		public List<T> getItems() {
			return items;
		}

	}

}
