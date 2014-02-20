package eu.martinlange.tools.eclipse.console.preferences;

import java.util.regex.Pattern;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import eu.martinlange.tools.eclipse.ISharedImages;
import eu.martinlange.tools.eclipse.Plugin;
import eu.martinlange.tools.eclipse.console.model.GroupDefinition;
import eu.martinlange.tools.eclipse.console.model.PatternDefinition;
import eu.martinlange.tools.eclipse.console.model.StyleDefinition;

public class DefaultLabelProvider extends StyledCellLabelProvider {

	public static final String PREVIEW_TEXT = "     AaBbCcYyZz     ";


	public static DefaultLabelProvider getInstance() {
		return new DefaultLabelProvider();
	}


	@Override
	public void update(ViewerCell cell) {

		if (cell.getElement() instanceof StyleDefinition)
			update(cell, (StyleDefinition) cell.getElement());

		else if (cell.getElement() instanceof PatternDefinition)
			update(cell, (PatternDefinition) cell.getElement());

		else if (cell.getElement() instanceof GroupDefinition)
			update(cell, (GroupDefinition) cell.getElement());

	}


	protected void update(ViewerCell cell, StyleDefinition e) {
		switch (cell.getColumnIndex()) {
		case 0:
			cell.setImage(Plugin.getSharedImages().getImage(ISharedImages.IMG_OBJ_STYLE));
			cell.setText(e.name);
			break;

		case 1:
			StyleRange[] style = new StyleRange[] { new StyleRange() };
			style[0] = (StyleRange) e.getAdapter(StyleRange.class);
			if (style[0] != null) {
				style[0].start = 0;
				style[0].length = PREVIEW_TEXT.length();
				style[0].font = StyleDefinition.DEFAULT_FONT;
				cell.setText(PREVIEW_TEXT);
				cell.setStyleRanges(style);
			}
			break;
		}
	}


	protected void update(ViewerCell cell, PatternDefinition e) {
		switch (cell.getColumnIndex()) {
		case 0:
			cell.setImage(Plugin.getSharedImages().getImage(ISharedImages.IMG_OBJ_PATTERN));
			cell.setText(e.name);
			break;

		case 1:
			boolean x = (e.flags & Pattern.COMMENTS) == Pattern.COMMENTS;
			boolean i = (e.flags & Pattern.CASE_INSENSITIVE) == Pattern.CASE_INSENSITIVE;

			StringBuilder sb = new StringBuilder();
			if (x || i)
			{
				sb.append("(?");
				if (x) sb.append("x");
				if (i) sb.append("i");
				sb.append(")");
			}
			sb.append(e.regex);

			cell.setText(sb.toString());
			cell.setFont(StyleDefinition.DEFAULT_FONT);
			break;
		}
	}


	protected void update(ViewerCell cell, GroupDefinition e) {
		switch (cell.getColumnIndex()) {
		case 0:
			cell.setImage(Plugin.getSharedImages().getImage(ISharedImages.IMG_OBJ_GROUP));
			cell.setText(e.name);
			break;

		case 1:
			cell.setText(e.style.name);
			StyleRange range = (StyleRange) e.style.getAdapter(StyleRange.class);
			range.font = StyleDefinition.DEFAULT_FONT;
			range.start = 0;
			range.length = cell.getText().length();
			cell.setStyleRanges(new StyleRange[] { range });
			break;
		}
	}

}
