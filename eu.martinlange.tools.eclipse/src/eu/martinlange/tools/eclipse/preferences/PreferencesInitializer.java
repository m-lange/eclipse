package eu.martinlange.tools.eclipse.preferences;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.swt.graphics.RGB;
import eu.martinlange.tools.eclipse.Plugin;
import eu.martinlange.tools.eclipse.console.model.PatternDefinition;
import eu.martinlange.tools.eclipse.console.model.StyleDefinition;

public class PreferencesInitializer extends AbstractPreferenceInitializer {

	public PreferencesInitializer() {
	}


	@Override
	public void initializeDefaultPreferences() {
		PreferenceStore store = Plugin.getDefault().getPreferenceStore();

		List<PatternDefinition> pattern = new ArrayList<PatternDefinition>();
		store.setDefault("pattern", pattern, PatternDefinition.class);

		List<StyleDefinition> styles = new ArrayList<StyleDefinition>();

		StyleDefinition e = new StyleDefinition();
		e.name = "Error";
		e.foreground = new RGB(156, 0, 6);
		e.background = new RGB(255, 199, 206);
		e.underline = true;
		e.underlineColor = new RGB(156, 0, 6);
		e.underlineStyle = 2;
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Warning";
		e.foreground = new RGB(156, 101, 0);
		e.background = new RGB(255, 235, 156);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Information";
		e.foreground = new RGB(0, 97, 0);
		e.background = new RGB(198, 239, 206);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 1 - 40% opacity";
		e.foreground = new RGB(0, 0, 0);
		e.background = new RGB(184, 204, 228);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 1 - 80% opacity";
		e.foreground = new RGB(255, 255, 255);
		e.background = new RGB(79, 129, 189);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 2 - 40% opacity";
		e.foreground = new RGB(0, 0, 0);
		e.background = new RGB(230, 184, 183);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 2 - 80% opacity";
		e.foreground = new RGB(255, 255, 255);
		e.background = new RGB(192, 80, 77);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 3 - 40% opacity";
		e.foreground = new RGB(0, 0, 0);
		e.background = new RGB(216, 228, 188);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 3 - 80% opacity";
		e.foreground = new RGB(255, 255, 255);
		e.background = new RGB(155, 187, 89);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 4 - 40% opacity";
		e.foreground = new RGB(0, 0, 0);
		e.background = new RGB(204, 192, 218);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 4 - 80% opacity";
		e.foreground = new RGB(255, 255, 255);
		e.background = new RGB(128, 100, 162);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 5 - 40% opacity";
		e.foreground = new RGB(0, 0, 0);
		e.background = new RGB(183, 222, 232);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 5 - 80% opacity";
		e.foreground = new RGB(255, 255, 255);
		e.background = new RGB(75, 172, 198);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 6 - 40% opacity";
		e.foreground = new RGB(0, 0, 0);
		e.background = new RGB(252, 213, 180);
		styles.add(e);

		e = new StyleDefinition();
		e.name = "Accent 6 - 80% opacity";
		e.foreground = new RGB(255, 255, 255);
		e.background = new RGB(247, 150, 70);
		styles.add(e);

		store.setDefault("styles", styles, StyleDefinition.class);

	}

}
