package eu.martinlange.theme.internal;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

public class ThemedRangeIndicator extends Annotation implements IAnnotationPresentation, IPropertyChangeListener {

	private static PaletteData fgPaletteData;
	private static IThemeManager fThemeManager = null;
	private static ITheme fCurrentTheme = null;

	private Image fImage;


	public ThemedRangeIndicator() {
		fThemeManager = PlatformUI.getWorkbench().getThemeManager();
		fCurrentTheme = fThemeManager.getCurrentTheme();
		fThemeManager.addPropertyChangeListener(this);
	}


	public void paint(GC gc, Canvas canvas, Rectangle bounds) {
		ColorRegistry color = fCurrentTheme.getColorRegistry();
		Point canvasSize = canvas.getSize();

		int x = 0;
		int y = bounds.y;
		int w = canvasSize.x;
		int h = bounds.height;
		int b = 1;

		if (y + h > canvasSize.y)
			h = canvasSize.y - y;

		if (y < 0) {
			h = h + y;
			y = 0;
		}

		if (h <= 0)
			return;

		Image image = getImage(canvas);
		gc.drawImage(image, 0, 0, w, h, x, y, w, h);

		gc.setBackground(color.get(IThemeConstants.RANGE_INDICATOR_COLOR));
		gc.fillRectangle(x, bounds.y, w, b);
		gc.fillRectangle(x, bounds.y + bounds.height - b, w, b);
	}


	public int getLayer() {
		return IAnnotationPresentation.DEFAULT_LAYER;
	}


	private Image getImage(Control control) {
		if (fImage == null) {
			fImage = createImage(control.getDisplay(), control.getSize());
			control.addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					if (fImage != null && !fImage.isDisposed()) {
						fImage.dispose();
						fImage = null;
					}
				}
			});
		} else {
			Rectangle imageRectangle = fImage.getBounds();
			Point controlSize = control.getSize();
			if (imageRectangle.width < controlSize.x || imageRectangle.height < controlSize.y) {
				fImage.dispose();
				fImage = createImage(control.getDisplay(), controlSize);
			}
		}
		return fImage;
	}


	private static Image createImage(Display display, Point size) {
		int width = size.x;
		int height = size.y;

		if (fgPaletteData == null)
			fgPaletteData = createPalette(display);

		ImageData imageData = new ImageData(width, height, 1, fgPaletteData);

		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				imageData.setPixel(x, y, (x + y) % 2);

		return new Image(display, imageData);
	}


	private static PaletteData createPalette(Display display) {
		ColorRegistry color = fCurrentTheme.getColorRegistry();
		RGB rgbs[] = new RGB[] {
				color.getRGB(IThemeConstants.RANGE_INDICATOR_COLOR),
				color.getRGB(IThemeConstants.RANGE_INDICATOR_BKGND) };
		return new PaletteData(rgbs);
	}


	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (IThemeConstants.RANGE_INDICATOR_BKGND.equals(event.getProperty())
				|| IThemeConstants.RANGE_INDICATOR_COLOR.equals(event.getProperty())) {
			fgPaletteData = null;
			if (fImage != null)
				fImage.dispose();
			fImage = null;
		}
	}

}
