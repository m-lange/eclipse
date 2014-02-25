/*******************************************************************************
 * Copyright (c) 2011 Google, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *******************************************************************************/
package eu.martinlange.console.swt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class SWTResourceManager {

	public static final int TOP_LEFT = 1;
	public static final int TOP_RIGHT = 2;
	public static final int BOTTOM_LEFT = 3;
	public static final int BOTTOM_RIGHT = 4;
	protected static final int LAST_CORNER_KEY = 5;

	private static final int MISSING_IMAGE_SIZE = 10;

	private static Map<RGB, Color> colorMap = new HashMap<RGB, Color>();
	private static RGB[] customColors = null;
	private static Map<String, Image> imageMap = new HashMap<String, Image>();
	@SuppressWarnings("unchecked")
	private static Map<Image, Map<Image, Image>>[] decoratedImageMap = new Map[LAST_CORNER_KEY];
	private static Map<String, Font> fontMap = new HashMap<String, Font>();
	private static Map<Font, Font> fontToBoldFontMap = new HashMap<Font, Font>();
	private static Map<Integer, Cursor> idToCursorMap = new HashMap<Integer, Cursor>();


	public static Color getColor(int systemColorID) {
		Display display = Display.getCurrent();
		return display.getSystemColor(systemColorID);
	}


	public static Color getColor(int r, int g, int b) {
		return getColor(new RGB(r, g, b));
	}


	public static Color getColor(RGB rgb) {
		Color color = colorMap.get(rgb);
		if (color == null) {
			Display display = Display.getCurrent();
			color = new Color(display, rgb);
			colorMap.put(rgb, color);
		}
		return color;
	}


	public static void setCustomColors(RGB[] rgbs) {
		customColors = rgbs;
	}


	public static RGB[] getCustomColors() {
		return customColors;
	}


	public static void disposeColors() {
		for (Color color : colorMap.values())
			color.dispose();
		colorMap.clear();
	}


	protected static Image getImage(InputStream stream) throws IOException {
		try {
			Display display = Display.getCurrent();
			ImageData data = new ImageData(stream);
			if (data.transparentPixel > 0)
				return new Image(display, data, data.getTransparencyMask());
			return new Image(display, data);
		} finally {
			stream.close();
		}
	}


	public static Image getImage(String path) {
		Image image = imageMap.get(path);
		if (image == null) {
			try {
				image = getImage(new FileInputStream(path));
				imageMap.put(path, image);
			} catch (Exception e) {
				image = getMissingImage();
				imageMap.put(path, image);
			}
		}
		return image;
	}


	public static Image getImage(Class<?> clazz, String path) {
		String key = clazz.getName() + '|' + path;
		Image image = imageMap.get(key);
		if (image == null) {
			try {
				image = getImage(clazz.getResourceAsStream(path));
				imageMap.put(key, image);
			} catch (Exception e) {
				image = getMissingImage();
				imageMap.put(key, image);
			}
		}
		return image;
	}


	private static Image getMissingImage() {
		Image image = new Image(Display.getCurrent(), MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
		GC gc = new GC(image);
		gc.setBackground(getColor(SWT.COLOR_RED));
		gc.fillRectangle(0, 0, MISSING_IMAGE_SIZE, MISSING_IMAGE_SIZE);
		gc.dispose();
		return image;
	}


	public static Image decorateImage(Image baseImage, Image decorator) {
		return decorateImage(baseImage, decorator, BOTTOM_RIGHT);
	}


	public static Image decorateImage(final Image baseImage, final Image decorator, final int corner) {
		if (corner <= 0 || corner >= LAST_CORNER_KEY)
			throw new IllegalArgumentException("Wrong decorate corner");

		Map<Image, Map<Image, Image>> cornerDecoratedImageMap = decoratedImageMap[corner];
		if (cornerDecoratedImageMap == null) {
			cornerDecoratedImageMap = new HashMap<Image, Map<Image, Image>>();
			decoratedImageMap[corner] = cornerDecoratedImageMap;
		}

		Map<Image, Image> decoratedMap = cornerDecoratedImageMap.get(baseImage);
		if (decoratedMap == null) {
			decoratedMap = new HashMap<Image, Image>();
			cornerDecoratedImageMap.put(baseImage, decoratedMap);
		}

		Image result = decoratedMap.get(decorator);
		if (result == null) {
			Rectangle bib = baseImage.getBounds();
			Rectangle dib = decorator.getBounds();

			result = new Image(Display.getCurrent(), bib.width, bib.height);

			GC gc = new GC(result);
			gc.drawImage(baseImage, 0, 0);
			if (corner == TOP_LEFT)
				gc.drawImage(decorator, 0, 0);
			else if (corner == TOP_RIGHT)
				gc.drawImage(decorator, bib.width - dib.width, 0);
			else if (corner == BOTTOM_LEFT)
				gc.drawImage(decorator, 0, bib.height - dib.height);
			else if (corner == BOTTOM_RIGHT)
				gc.drawImage(decorator, bib.width - dib.width, bib.height - dib.height);
			gc.dispose();
			decoratedMap.put(decorator, result);
		}
		return result;
	}


	public static void disposeImages() {
		for (Image image : imageMap.values())
			image.dispose();
		imageMap.clear();

		for (int i = 0; i < decoratedImageMap.length; i++) {
			Map<Image, Map<Image, Image>> cornerDecoratedImageMap = decoratedImageMap[i];
			if (cornerDecoratedImageMap != null) {
				for (Map<Image, Image> decoratedMap : cornerDecoratedImageMap.values()) {
					for (Image image : decoratedMap.values())
						image.dispose();
					decoratedMap.clear();
				}
				cornerDecoratedImageMap.clear();
			}
		}
	}


	public static Font getFont(String name, int height, int style) {
		return getFont(name, height, style, false, false);
	}


	public static Font getFont(String name, int size, int style, boolean strikeout, boolean underline) {
		String fontName = name + '|' + size + '|' + style + '|' + strikeout + '|' + underline;
		Font font = fontMap.get(fontName);
		if (font == null) {
			FontData fontData = new FontData(name, size, style);
			if (strikeout || underline) {
				try {
					Class<?> logFontClass = Class.forName("org.eclipse.swt.internal.win32.LOGFONT"); //$NON-NLS-1$
					Object logFont = FontData.class.getField("data").get(fontData); //$NON-NLS-1$
					if (logFont != null && logFontClass != null) {
						if (strikeout)
							logFontClass.getField("lfStrikeOut").set(logFont, Byte.valueOf((byte) 1)); //$NON-NLS-1$
						if (underline)
							logFontClass.getField("lfUnderline").set(logFont, Byte.valueOf((byte) 1)); //$NON-NLS-1$
					}
				} catch (Throwable e) {
					System.err.println("Unable to set underline or strikeout" + " (probably on a non-Windows platform). " + e); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			font = new Font(Display.getCurrent(), fontData);
			fontMap.put(fontName, font);
		}
		return font;
	}


	public static Font getBoldFont(Font baseFont) {
		Font font = fontToBoldFontMap.get(baseFont);
		if (font == null) {
			FontData fontDatas[] = baseFont.getFontData();
			FontData data = fontDatas[0];
			font = new Font(Display.getCurrent(), data.getName(), data.getHeight(), SWT.BOLD);
			fontToBoldFontMap.put(baseFont, font);
		}
		return font;
	}


	public static void disposeFonts() {
		for (Font font : fontMap.values())
			font.dispose();
		fontMap.clear();
		for (Font font : fontToBoldFontMap.values())
			font.dispose();
		fontToBoldFontMap.clear();
	}


	public static Cursor getCursor(int id) {
		Integer key = Integer.valueOf(id);
		Cursor cursor = idToCursorMap.get(key);
		if (cursor == null) {
			cursor = new Cursor(Display.getDefault(), id);
			idToCursorMap.put(key, cursor);
		}
		return cursor;
	}


	public static void disposeCursors() {
		for (Cursor cursor : idToCursorMap.values())
			cursor.dispose();
		idToCursorMap.clear();
	}


	public static void dispose() {
		disposeColors();
		disposeImages();
		disposeFonts();
		disposeCursors();
	}

}