package eu.martinlange.tools.eclipse.console.model;

import java.util.Arrays;
import java.util.UUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;
import eu.martinlange.tools.eclipse.swt.SWTResourceManager;

@XmlRootElement(name = "style")
@XmlAccessorType(XmlAccessType.FIELD)
public class StyleDefinition implements IAdaptable {

	public static final StyleDefinition DEFAULT;
	public static final Font DEFAULT_FONT;

	static {
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();
		FontRegistry fontRegistry = currentTheme.getFontRegistry();
		DEFAULT_FONT = fontRegistry.get("org.eclipse.debug.ui.consoleFont");

		DEFAULT = new StyleDefinition();
		DEFAULT.id = "default";
		DEFAULT.name = "Default";
		DEFAULT.fontData = DEFAULT_FONT.getFontData();
	}

	@XmlElement(name = "id")
	public String id;

	@XmlElement(name = "name")
	public String name;

	@XmlElement(name = "font")
	@XmlJavaTypeAdapter(FontDataAdapter.class)
	public FontData[] fontData;

	@XmlElement(name = "foreground")
	@XmlJavaTypeAdapter(ColorAdapter.class)
	public RGB foreground;

	@XmlElement(name = "background")
	@XmlJavaTypeAdapter(ColorAdapter.class)
	public RGB background;

	@XmlElement(name = "underline")
	public boolean underline;

	@XmlElement(name = "underlineColor")
	@XmlJavaTypeAdapter(ColorAdapter.class)
	public RGB underlineColor;

	@XmlElement(name = "underlineStyle")
	public int underlineStyle;

	@XmlElement(name = "strikeout")
	public boolean strikeout;

	@XmlElement(name = "strikeoutColor")
	@XmlJavaTypeAdapter(ColorAdapter.class)
	public RGB strikeoutColor;

	@XmlElement(name = "border")
	public boolean border;

	@XmlElement(name = "borderColor")
	@XmlJavaTypeAdapter(ColorAdapter.class)
	public RGB borderColor;

	@XmlElement(name = "borderStyle")
	public int borderStyle;


	public StyleDefinition() {
		this.id = UUID.randomUUID().toString();
		this.name = "";
		this.fontData = null;
		this.foreground = null;
		this.background = null;
		this.underline = false;
		this.underlineColor = null;
		this.underlineStyle = -1;
		this.strikeout = false;
		this.strikeoutColor = null;
		this.border = false;
		this.borderColor = null;
		this.borderStyle = -1;
	}


	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter != StyleRange.class)
			return null;

		StyleRange range = new StyleRange();
		range.start = 0;
		range.length = 0;

		if (fontData != null && fontData.length >= 1)
			range.font = SWTResourceManager.getFont(fontData[0].getName(), fontData[0].getHeight(), fontData[0].getStyle());

		if (foreground != null)
			range.foreground = SWTResourceManager.getColor(foreground);

		if (background != null)
			range.background = SWTResourceManager.getColor(background);

		if (underline) {
			range.underline = true;
			range.underlineColor = SWTResourceManager.getColor(underlineColor);
			switch (underlineStyle) {
			case 1:
				range.underlineStyle = SWT.UNDERLINE_DOUBLE;
				break;
			case 2:
				range.underlineStyle = SWT.UNDERLINE_ERROR;
				break;
			case 3:
				range.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
				break;
			case 4:
				range.underlineStyle = SWT.UNDERLINE_LINK;
				break;
			default:
				range.underlineStyle = SWT.UNDERLINE_SINGLE;
				break;
			}
		}

		if (strikeout) {
			range.strikeout = true;
			range.strikeoutColor = SWTResourceManager.getColor(strikeoutColor);
		}

		if (border) {
			range.borderColor = SWTResourceManager.getColor(borderColor);
			switch (borderStyle) {
			case 1:
				range.borderStyle = SWT.BORDER_DASH;
				break;
			case 2:
				range.borderStyle = SWT.BORDER_DOT;
				break;
			default:
				range.borderStyle = SWT.BORDER_SOLID;
				break;
			}
		}

		return range;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((background == null) ? 0 : background.hashCode());
		result = prime * result + (border ? 1231 : 1237);
		result = prime * result + ((borderColor == null) ? 0 : borderColor.hashCode());
		result = prime * result + borderStyle;
		result = prime * result + Arrays.hashCode(fontData);
		result = prime * result + ((foreground == null) ? 0 : foreground.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (strikeout ? 1231 : 1237);
		result = prime * result + ((strikeoutColor == null) ? 0 : strikeoutColor.hashCode());
		result = prime * result + (underline ? 1231 : 1237);
		result = prime * result + ((underlineColor == null) ? 0 : underlineColor.hashCode());
		result = prime * result + underlineStyle;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		StyleDefinition other = (StyleDefinition) obj;
		if (background == null) {
			if (other.background != null) return false;
		} else if (!background.equals(other.background)) return false;
		if (border != other.border) return false;
		if (borderColor == null) {
			if (other.borderColor != null) return false;
		} else if (!borderColor.equals(other.borderColor)) return false;
		if (borderStyle != other.borderStyle) return false;
		if (!Arrays.equals(fontData, other.fontData)) return false;
		if (foreground == null) {
			if (other.foreground != null) return false;
		} else if (!foreground.equals(other.foreground)) return false;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (strikeout != other.strikeout) return false;
		if (strikeoutColor == null) {
			if (other.strikeoutColor != null) return false;
		} else if (!strikeoutColor.equals(other.strikeoutColor)) return false;
		if (underline != other.underline) return false;
		if (underlineColor == null) {
			if (other.underlineColor != null) return false;
		} else if (!underlineColor.equals(other.underlineColor)) return false;
		if (underlineStyle != other.underlineStyle) return false;
		return true;
	}

}
