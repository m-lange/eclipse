package eu.martinlange.console.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import eu.martinlange.console.Plugin;
import eu.martinlange.console.internal.IPreferenceConstants;
import eu.martinlange.console.internal.PreferenceStore;

public class StyleDefinitionCache implements IPropertyChangeListener {

	public static final StyleDefinitionCache INSTANCE = new StyleDefinitionCache();

	protected PreferenceStore fStore;
	protected List<StyleDefinition> fStyles;


	protected StyleDefinitionCache() {
		fStore = Plugin.getDefault().getPreferenceStore();
		fStore.addPropertyChangeListener(this);

		reloadStyles();
	}


	public StyleDefinition getById(String id) {
		for (StyleDefinition e : fStyles) {
			if (e.id.equals(id))
				return e;
		}
		return StyleDefinition.DEFAULT;
	}


	public StyleDefinition[] getStyles() {
		return fStyles.toArray(new StyleDefinition[0]);
	}


	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(IPreferenceConstants.STYLES)) {
			reloadStyles();
		}
	}


	protected void reloadStyles() {
		fStyles = fStore.getList(IPreferenceConstants.STYLES, StyleDefinition.class);

		Collections.sort(fStyles, new Comparator<StyleDefinition>() {
			@Override
			public int compare(StyleDefinition o1, StyleDefinition o2) {
				return o1.name.compareTo(o2.name);
			}
		});

		fStyles.add(0, StyleDefinition.DEFAULT);
	}

}
