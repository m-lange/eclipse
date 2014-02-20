package eu.martinlange.tools.eclipse.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TypedListener;

public class FillChooser extends Composite implements SelectionListener, DisposeListener, Listener {

	private Composite innerComposite = null;
	private Composite outerComposite = null;
	private Canvas previewer = null;
	private Composite dropDownComposite = null;
	private Composite buttonComposite = null;
	private Button customButton = null;
	private Button arrowButton = null;

	private static Color[][] colorArray = null;
	private Color selectedColor = null;
	private int size = 18;
	private boolean enabled = true;
	private boolean justFocusLost = false;
	boolean isPressingKey = false;


	public FillChooser(Composite parent, int style) {
		super(parent, checkStyle(style));
		init();
		initAccessible();
	}


	static int checkStyle(int style) {
		int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
		return SWT.NO_FOCUS | (style & mask);
	}


	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}


	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		removeListener(SWT.Selection, listener);
		removeListener(SWT.DefaultSelection, listener);
	}


	private void init() {
		if (Display.getCurrent().getHighContrast()) {
			GC gc = new GC(this);
			size = gc.getFontMetrics().getHeight() + 2;
		}
		this.setSize(getParent().getClientArea().width, getParent().getClientArea().height);
		Display display = Display.getDefault();
		colorArray = this.createColorMap(display);

		FillLayout flMain = new FillLayout();
		flMain.marginHeight = 0;
		flMain.marginWidth = 0;
		setLayout(flMain);

		outerComposite = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		outerComposite.setLayout(layout);

		innerComposite = new Composite(outerComposite, SWT.BORDER);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		innerComposite.setLayout(layout);
		GridData gdContentInner = new GridData(GridData.FILL_HORIZONTAL);
		innerComposite.setLayoutData(gdContentInner);

		previewer = new Canvas(innerComposite, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = size;
		previewer.setLayoutData(gd);
		previewer.setBackground(new Color(display, 255, 0, 0));

		arrowButton = new Button(innerComposite, SWT.ARROW | SWT.DOWN);
		gd = new GridData(GridData.FILL);
		gd.verticalAlignment = GridData.BEGINNING;
		gd.widthHint = size - 2;
		gd.heightHint = size;
		arrowButton.setLayoutData(gd);
		arrowButton.addSelectionListener(this);

		addDisposeListener(this);

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				handleEventCanvas(event);
			}
		};

		int[] textEvents = { SWT.KeyDown, SWT.MouseDown, SWT.Traverse, SWT.FocusIn, SWT.FocusOut };
		for (int e : textEvents) {
			previewer.addListener(e, listener);
		}
	}


	public void setColor(Color color) {
		selectedColor = color;
		previewer.setBackground(color);
		previewer.redraw();
	}


	public Color getColor() {
		return selectedColor;
	}


	@Override
	public void setEnabled(boolean bState) {
		arrowButton.setEnabled(bState);
		previewer.setEnabled(bState);
		previewer.redraw();
		this.enabled = bState;
		if (enabled) {
			setColor(selectedColor);
		} else {
			previewer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
			previewer.redraw();
		}
	}


	@Override
	public boolean isEnabled() {
		return this.enabled;
	}


	public Point getPreferredSize() {
		return new Point(140, 24);
	}


	@Override
	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.FocusOut:
			if (event.widget instanceof ColorSelectionCanvas)
				((ColorSelectionCanvas) event.widget).redraw();

			if (isPopupControl(event.widget)) {
				Control control = isPressingKey ? getDisplay().getFocusControl() : getDisplay().getCursorControl();
				isPressingKey = false;
				if (control != null) {
					if (isPopupControl(control) || SWT.getPlatform().indexOf("win32") == 0 && (control.equals(previewer) || control.equals(arrowButton)))
						return;

					if (control.equals(previewer) || control.equals(arrowButton))
						justFocusLost = true;
				}
				dropDownComposite.getShell().close();
			}
			break;

		case SWT.Traverse:
			switch (event.detail) {
			case SWT.TRAVERSE_TAB_NEXT:
			case SWT.TRAVERSE_TAB_PREVIOUS:
				isPressingKey = true;
				event.doit = true;
			}
			break;
		}
	}


	void handleEventCanvas(Event event) {
		switch (event.type) {
		case SWT.FocusIn: {
			previewer.redraw();
			break;
		}
		case SWT.FocusOut: {
			previewer.redraw();
			break;
		}
		case SWT.KeyDown: {
			if (event.keyCode == SWT.KEYPAD_CR || event.keyCode == SWT.CR || event.keyCode == ' ') {
				event.doit = true;
				toggleDropDown();
			}
			break;
		}
		case SWT.MouseDown:
			if (!enabled)
				return;
			toggleDropDown();
			break;
		case SWT.Traverse: {
			switch (event.detail) {
			case SWT.TRAVERSE_ESCAPE:
				getShell().close();
				break;
			case SWT.TRAVERSE_RETURN:
			case SWT.TRAVERSE_TAB_NEXT:
			case SWT.TRAVERSE_TAB_PREVIOUS:
			case SWT.TRAVERSE_ARROW_PREVIOUS:
			case SWT.TRAVERSE_ARROW_NEXT:
				event.doit = true;
				previewer.redraw();
			}
			break;
		}
		}
	}


	@Override
	public void widgetDisposed(DisposeEvent e) {
		if (colorArray != null) {
			for (Color[] array : colorArray) {
				for (Color color : array) {
					color.dispose();
				}
			}
			colorArray = null;
		}
	}


	@Override
	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();
		if (source.equals(arrowButton)) {
			toggleDropDown();
		} else if (source.equals(this.customButton)) {
			ColorDialog dialog = new ColorDialog(this.getShell(), SWT.APPLICATION_MODAL);
			dialog.setRGBs(SWTResourceManager.getCustomColors());
			dialog.setText("Color");
			dropDownComposite.getShell().close();
			if (selectedColor != null)
				dialog.setRGB(selectedColor.getRGB());
			RGB rgb = dialog.open();
			if (rgb != null)
				setColorToModel(SWTResourceManager.getColor(rgb));
			SWTResourceManager.setCustomColors(dialog.getRGBs());
		}
	}


	private void toggleDropDown() {
		if (justFocusLost) {
			justFocusLost = false;
			return;
		}

		if (dropDownComposite == null || dropDownComposite.isDisposed() || !dropDownComposite.isVisible()) {

			Point ptScreen = new Point(0, 0);
			try {
				Composite c = previewer;
				while (!(c instanceof Shell)) {
					ptScreen.x += c.getLocation().x;
					ptScreen.y += c.getLocation().y;
					c = c.getParent();
				}

				Point pLoc = previewer.getShell().toDisplay(ptScreen);

				createDropDownComponent(pLoc.x, pLoc.y + previewer.getSize().y + 1);
				dropDownComposite.setFocus();

			} catch (Exception e) {
			}

		} else {

			dropDownComposite.getShell().close();

		}
	}


	private void createDropDownComponent(int x, int y) {
		if (!enabled)
			return;

		int height = 170;
		int width = 190;

		Shell shell = new Shell(this.getShell(), SWT.NO_FOCUS);
		shell.setLayout(new FillLayout());
		shell.setSize(width, height);

		if ((getStyle() & SWT.RIGHT_TO_LEFT) != 0)
			x -= width;
		shell.setLocation(x, y);

		dropDownComposite = new Composite(shell, SWT.NO_FOCUS);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		layout.horizontalSpacing = 1;
		layout.verticalSpacing = 4;
		dropDownComposite.setLayout(layout);

		if (colorArray == null)
			colorArray = createColorMap(getDisplay());
		ColorSelectionCanvas cnv = new ColorSelectionCanvas(dropDownComposite, SWT.BORDER, colorArray);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = size * colorArray.length;
		gd.heightHint = size * colorArray[0].length;
		cnv.setLayoutData(gd);
		cnv.addListener(SWT.Traverse, this);
		cnv.addListener(SWT.FocusOut, this);

		buttonComposite = new Composite(dropDownComposite, SWT.NO_FOCUS);
		layout = new GridLayout();
		layout.marginHeight = 3;
		layout.marginWidth = 4;
		layout.horizontalSpacing = 1;
		layout.verticalSpacing = 4;
		layout.numColumns = 2;
		buttonComposite.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		buttonComposite.setLayoutData(gd);

		customButton = new Button(buttonComposite, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 22;
		gd.horizontalSpan = 2;
		customButton.setLayoutData(gd);
		customButton.setText("Custom");
		customButton.addSelectionListener(this);
		customButton.addListener(SWT.FocusOut, this);
		customButton.addListener(SWT.KeyDown, this);
		customButton.addListener(SWT.Traverse, this);

		shell.pack();
		shell.layout();
		shell.open();
	}


	private boolean isPopupControl(Object control) {
		return control != null && control instanceof Control && ((Control) control).getShell() == dropDownComposite.getShell();
	}


	private void setColorToModel(Color color) {
		setColor(color);

		Event e = new Event();
		e.time = 0;
		e.stateMask = 0;
		e.data = color;
		notifyListeners(SWT.Selection, e);
	}


	private void initAccessible() {
		getAccessible().addAccessibleListener(new AccessibleAdapter() {
			public void getHelp(AccessibleEvent e) {
				e.result = getToolTipText();
			}
		});

		getAccessible().addAccessibleControlListener(
				new AccessibleControlAdapter() {
					public void getChildAtPoint(AccessibleControlEvent e) {
						Point pt = toControl(new Point(e.x, e.y));
						if (getBounds().contains(pt)) {
							e.childID = ACC.CHILDID_SELF;
						}
					}


					public void getLocation(AccessibleControlEvent e) {
						Rectangle location = getBounds();
						Point pt = toDisplay(new Point(location.x, location.y));
						e.x = pt.x;
						e.y = pt.y;
						e.width = location.width;
						e.height = location.height;
					}


					public void getChildCount(AccessibleControlEvent e) {
						e.detail = 0;
					}


					public void getRole(AccessibleControlEvent e) {
						e.detail = ACC.ROLE_COMBOBOX;
					}


					public void getState(AccessibleControlEvent e) {
						e.detail = ACC.STATE_NORMAL;
					}
				});
	}


	protected void fireValueChange(Object oldValue, Object newValue) {

	}


	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}


	private Color[][] createColorMap(Display display) {
		return new Color[][] {
				new Color[] {
						new Color(display, 192, 0, 0),
						new Color(display, 255, 255, 255),
						new Color(display, 242, 242, 242),
						new Color(display, 216, 216, 216),
						new Color(display, 191, 191, 191),
						new Color(display, 165, 165, 165),
						new Color(display, 127, 127, 127)
				},
				new Color[] {
						new Color(display, 255, 0, 0),
						new Color(display, 0, 0, 0),
						new Color(display, 127, 127, 127),
						new Color(display, 89, 89, 89),
						new Color(display, 63, 63, 63),
						new Color(display, 38, 38, 38),
						new Color(display, 12, 12, 12)
				},
				new Color[] {
						new Color(display, 255, 192, 0),
						new Color(display, 238, 236, 225),
						new Color(display, 221, 217, 195),
						new Color(display, 196, 189, 151),
						new Color(display, 147, 137, 83),
						new Color(display, 73, 68, 41),
						new Color(display, 29, 27, 16)
				},
				new Color[] {
						new Color(display, 255, 255, 0),
						new Color(display, 198, 217, 240),
						new Color(display, 141, 179, 226),
						new Color(display, 84, 141, 212),
						new Color(display, 31, 73, 125),
						new Color(display, 23, 54, 93),
						new Color(display, 15, 36, 62)
				},
				new Color[] {
						new Color(display, 146, 208, 80),
						new Color(display, 219, 229, 141),
						new Color(display, 184, 204, 228),
						new Color(display, 149, 179, 215),
						new Color(display, 79, 129, 189),
						new Color(display, 54, 96, 146),
						new Color(display, 36, 64, 97)
				},
				new Color[] {
						new Color(display, 0, 176, 80),
						new Color(display, 242, 220, 219),
						new Color(display, 229, 185, 183),
						new Color(display, 217, 150, 148),
						new Color(display, 192, 80, 77),
						new Color(display, 149, 55, 52),
						new Color(display, 99, 36, 35)
				},
				new Color[] {
						new Color(display, 0, 176, 240),
						new Color(display, 235, 241, 221),
						new Color(display, 215, 227, 188),
						new Color(display, 195, 214, 155),
						new Color(display, 155, 187, 89),
						new Color(display, 118, 149, 60),
						new Color(display, 79, 97, 40)
				},
				new Color[] {
						new Color(display, 0, 112, 192),
						new Color(display, 229, 224, 236),
						new Color(display, 204, 193, 217),
						new Color(display, 178, 162, 199),
						new Color(display, 128, 100, 162),
						new Color(display, 95, 73, 122),
						new Color(display, 63, 49, 81)
				},
				new Color[] {
						new Color(display, 0, 32, 96),
						new Color(display, 219, 238, 243),
						new Color(display, 183, 221, 232),
						new Color(display, 146, 205, 220),
						new Color(display, 75, 172, 198),
						new Color(display, 49, 133, 155),
						new Color(display, 32, 88, 103)
				},
				new Color[] {
						new Color(display, 112, 48, 160),
						new Color(display, 253, 234, 218),
						new Color(display, 251, 213, 181),
						new Color(display, 250, 192, 143),
						new Color(display, 247, 150, 70),
						new Color(display, 227, 108, 9),
						new Color(display, 151, 72, 6)
				}
		};
	}


	private class ColorSelectionCanvas extends Canvas implements Listener {

		int ROW_SIZE = 7;
		int COLUMN_SIZE = 10;
		final Color[][] colorMap;


		public ColorSelectionCanvas(Composite parent, int iStyle, final Color[][] colorMap) {
			super(parent, iStyle);
			this.colorMap = colorMap;
			COLUMN_SIZE = colorMap.length;
			ROW_SIZE = colorMap[0].length;

			this.addListener(SWT.Paint, this);
			this.addListener(SWT.KeyDown, this);
			this.addListener(SWT.MouseDown, this);
			this.addListener(SWT.FocusIn, this);
		}


		void paintControl(PaintEvent e) {
			Color black = new Color(this.getDisplay(), 0, 0, 0);
			Color white = new Color(this.getDisplay(), 255, 255, 255);
			GC gc = e.gc;
			gc.setForeground(black);

			int cx = this.getSize().x / COLUMN_SIZE;
			int cy = this.getSize().y / ROW_SIZE;
			for (int column = 0; column < COLUMN_SIZE; column++) {
				for (int row = 0; row < ROW_SIZE; row++) {
					gc.setBackground(colorMap[column][row]);
					gc.fillRectangle(column * cx, row * cy, cx, cy);
					if (selectedColor != null && selectedColor.equals(colorMap[column][row])) {
						selectedColor = colorMap[column][row];

						if (isFocusControl())
							gc.setLineStyle(SWT.LINE_DOT);
						gc.drawRectangle(column * cx, row * cy, cx - 2, cy - 2);
						gc.setForeground(white);
						gc.drawRectangle(column * cx + 1, row * cy + 1, cx - 3, cy - 3);
						gc.setForeground(black);
					}
				}
			}

			black.dispose();
			white.dispose();
			gc.dispose();
		}


		public Color getColorAt(int x, int y) {
			int cx = this.getSize().x / COLUMN_SIZE;
			int cy = this.getSize().y / ROW_SIZE;
			int column = x / cx;
			int row = y / cy;
			return colorMap[column][row];
		}


		public void handleEvent(Event event) {
			switch (event.type) {
			case SWT.Paint:
				paintControl(new PaintEvent(event));
				break;
			case SWT.FocusIn:
				redraw();
				break;
			case SWT.MouseDown:
				if (!enabled)
					return;
				setColorToModel(getColorAt(event.x, event.y));
				dropDownComposite.getShell().close();
				break;
			}
		}
	}

}
