package eu.martinlange.tools.eclipse.console;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import eu.martinlange.tools.eclipse.Plugin;
import eu.martinlange.tools.eclipse.console.model.PatternDefinition;
import eu.martinlange.tools.eclipse.console.model.StyleDefinition;
import eu.martinlange.tools.eclipse.console.model.StyleDefinitionCache;
import eu.martinlange.tools.eclipse.preferences.IPreferenceConstants;
import eu.martinlange.tools.eclipse.preferences.PreferenceStore;

public class ConsoleLineStyleListener implements LineStyleListener, LineBackgroundListener, IPropertyChangeListener {

	private PreferenceStore fStore;
	private TreeMap<PatternDefinition, Pattern> fPattern;


	public ConsoleLineStyleListener() {
		fStore = Plugin.getDefault().getPreferenceStore();
		fStore.addPropertyChangeListener(this);

		fPattern = new TreeMap<PatternDefinition, Pattern>(new Comparator<PatternDefinition>() {
			@Override
			public int compare(PatternDefinition o1, PatternDefinition o2) {
				return o1.index - o2.index;
			}
		});

		reloadPattern();
	}


	@Override
	public void lineGetStyle(LineStyleEvent event) {
		if (event.lineText.length() == 0 || fPattern.size() == 0)
			return;

		synchronized (fPattern) {

			List<StyleRange> ranges = new ArrayList<StyleRange>();

			for (Entry<PatternDefinition, Pattern> e : fPattern.entrySet()) {
				Matcher m = e.getValue().matcher(event.lineText);
				if (m.find()) {
					int n = m.groupCount();
					for (int i = 0; i <= n; i++) {
						int start = event.lineOffset + m.start(i);
						int length = m.group(i).length();
						if (i > 0)
							length = length - 1;

						StyleRange style = lineGetStyle(e.getKey(), i, start, length);
						if (style != null)
							ranges.add(style);
					}

					if (ranges.size() > 0)
						event.styles = ranges.toArray(new StyleRange[0]);

					return;
				}
			}

		}
	}


	@Override
	public void lineGetBackground(LineBackgroundEvent event) {
		if (event.lineText.length() == 0 || fPattern.size() == 0)
			return;

		synchronized (fPattern) {

			for (Entry<PatternDefinition, Pattern> e : fPattern.entrySet()) {
				Matcher m = e.getValue().matcher(event.lineText);
				if (m.find()) {
					StyleRange style = lineGetStyle(e.getKey(), 0, 0, 0);
					event.lineBackground = style.background;
					return;
				}
			}

		}
	}


	protected StyleRange lineGetStyle(PatternDefinition e, int index, int start, int length) {
		if (index >= e.styles.size()) return null;

		StyleDefinition styledef = StyleDefinitionCache.INSTANCE.getById(e.styles.get(index));

		StyleRange style = new StyleRange();
		style = (StyleRange) styledef.getAdapter(StyleRange.class);
		style.start = start;
		style.length = length;

		return style;
	}


	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(IPreferenceConstants.PATTERN)) {
			reloadPattern();
		}
	}


	protected void reloadPattern() {
		List<PatternDefinition> pattern = fStore.getList(IPreferenceConstants.PATTERN, PatternDefinition.class);

		synchronized (fPattern) {

			fPattern.clear();
			for (PatternDefinition e : pattern) {
				int flags = Pattern.DOTALL | e.flags;
				Pattern p = Pattern.compile(e.regex, flags);
				fPattern.put(e, p);
			}

		}
	}

}
