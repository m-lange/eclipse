package eu.martinlange.tools.eclipse.console.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import eu.martinlange.tools.eclipse.console.model.StyleDefinition;
import eu.martinlange.tools.eclipse.swt.FillChooser;
import eu.martinlange.tools.eclipse.swt.SWTResourceManager;

public class StyleDefinitionDialog extends Dialog {

	public static final int IDX_FOREGROUND = 0;
	public static final int IDX_BACKGROUND = 1;
	public static final int IDX_UNDERLINE = 2;
	public static final int IDX_STRIKEOUT = 3;
	public static final int IDX_BORDER = 4;
	public static final int IDX_FONT = 5;
	public static final int IDX_LAST = 6;

	private StyledText fName;
	private Label fFontLabel;
	private Button fChangeFontButton;
	private Button[] fEnableButton = new Button[IDX_LAST];
	private FillChooser[] fFillChooser = new FillChooser[IDX_LAST];
	private CCombo fBorderStyle;
	private CCombo fUnderlineStyle;
	private StyledText fPreviewer;

	private String fTitle;
	private StyleDefinition fStyle;


	protected StyleDefinitionDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		fTitle = "";
		fStyle = null;
	}


	public void setText(String title) {
		fTitle = title;
	}


	public String getText() {
		return fTitle;
	}


	public void setStyle(StyleDefinition style) {
		fStyle = style;
	}


	public StyleDefinition getStyle() {
		return fStyle;
	}


	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(fTitle);
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 7;
		layout.marginRight = 10;
		layout.marginLeft = 10;
		layout.marginBottom = 10;
		layout.marginHeight = 10;
		container.setLayout(layout);

		Label label = new Label(container, SWT.NONE);
		label.setText("Name:");

		fName = new StyledText(container, SWT.BORDER | SWT.SINGLE);
		fName.setRightMargin(5);
		fName.setLeftMargin(5);
		fName.setText("New Style");
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
		fName.setLayoutData(gd);

		fEnableButton[IDX_FONT] = new Button(container, SWT.CHECK);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd.verticalIndent = 15;
		fEnableButton[IDX_FONT].setLayoutData(gd);
		fEnableButton[IDX_FONT].setText("Font");
		fEnableButton[IDX_FONT].setSelection(false);

		fFontLabel = new Label(container, SWT.NONE);
		fFontLabel.setEnabled(false);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd.widthHint = 200;
		gd.verticalIndent = 15;
		fFontLabel.setLayoutData(gd);
		fFontLabel.setText("Default");

		fChangeFontButton = new Button(container, SWT.NONE);
		fChangeFontButton.setEnabled(false);
		fChangeFontButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeFontPressed();
			}
		});
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd.verticalIndent = 15;
		fChangeFontButton.setLayoutData(gd);
		fChangeFontButton.setText("Change ...");

		fEnableButton[IDX_FOREGROUND] = new Button(container, SWT.CHECK);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd.verticalIndent = 5;
		fEnableButton[IDX_FOREGROUND].setLayoutData(gd);
		fEnableButton[IDX_FOREGROUND].setText("Foreground");
		fEnableButton[IDX_FOREGROUND].setSelection(false);

		fFillChooser[IDX_FOREGROUND] = new FillChooser(container, SWT.NONE);
		fFillChooser[IDX_FOREGROUND].setEnabled(false);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd.widthHint = 75;
		gd.verticalIndent = 5;
		fFillChooser[IDX_FOREGROUND].setLayoutData(gd);
		fFillChooser[IDX_FOREGROUND].setColor(SWTResourceManager.getColor(0, 0, 0));

		fEnableButton[IDX_BACKGROUND] = new Button(container, SWT.CHECK);
		fEnableButton[IDX_BACKGROUND].setText("Background");
		fEnableButton[IDX_BACKGROUND].setSelection(false);

		fFillChooser[IDX_BACKGROUND] = new FillChooser(container, SWT.NONE);
		fFillChooser[IDX_BACKGROUND].setEnabled(false);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd.widthHint = 75;
		fFillChooser[IDX_BACKGROUND].setLayoutData(gd);
		fFillChooser[IDX_BACKGROUND].setColor(SWTResourceManager.getColor(255, 255, 255));

		fEnableButton[IDX_UNDERLINE] = new Button(container, SWT.CHECK);
		fEnableButton[IDX_UNDERLINE].setText("Underline");
		fEnableButton[IDX_UNDERLINE].setSelection(false);

		fFillChooser[IDX_UNDERLINE] = new FillChooser(container, SWT.NONE);
		fFillChooser[IDX_UNDERLINE].setEnabled(false);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd.widthHint = 75;
		fFillChooser[IDX_UNDERLINE].setLayoutData(gd);
		fFillChooser[IDX_UNDERLINE].setColor(SWTResourceManager.getColor(0, 0, 0));

		fUnderlineStyle = new CCombo(container, SWT.BORDER);
		fUnderlineStyle.setEnabled(false);
		fUnderlineStyle.setVisibleItemCount(5);
		fUnderlineStyle.setItems(new String[] { "Single", "Double", "Error", "Squiggle", "Link" });
		fUnderlineStyle.select(0);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd.widthHint = 75;
		fUnderlineStyle.setLayoutData(gd);

		fEnableButton[IDX_STRIKEOUT] = new Button(container, SWT.CHECK);
		fEnableButton[IDX_STRIKEOUT].setText("Strikeout");
		fEnableButton[IDX_STRIKEOUT].setSelection(false);

		fFillChooser[IDX_STRIKEOUT] = new FillChooser(container, SWT.NONE);
		fFillChooser[IDX_STRIKEOUT].setEnabled(false);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd.widthHint = 75;
		fFillChooser[IDX_STRIKEOUT].setLayoutData(gd);
		fFillChooser[IDX_STRIKEOUT].setColor(SWTResourceManager.getColor(0, 0, 0));

		fEnableButton[IDX_BORDER] = new Button(container, SWT.CHECK);
		fEnableButton[IDX_BORDER].setText("Border");
		fEnableButton[IDX_BORDER].setSelection(false);

		fFillChooser[IDX_BORDER] = new FillChooser(container, SWT.NONE);
		fFillChooser[IDX_BORDER].setEnabled(false);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd.widthHint = 75;
		fFillChooser[IDX_BORDER].setLayoutData(gd);
		fFillChooser[IDX_BORDER].setColor(SWTResourceManager.getColor(0, 0, 0));

		fBorderStyle = new CCombo(container, SWT.BORDER);
		fBorderStyle.setEnabled(false);
		fBorderStyle.setVisibleItemCount(3);
		fBorderStyle.setItems(new String[] { "Solid", "Dash", "Dot" });
		fBorderStyle.select(0);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd.widthHint = 75;
		fBorderStyle.setLayoutData(gd);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		Group fPreviewGroup = new Group(container, SWT.NONE);
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.marginWidth = 5;
		fillLayout.marginHeight = 5;
		fPreviewGroup.setLayout(fillLayout);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gd.verticalIndent = 10;
		fPreviewGroup.setLayoutData(gd);
		fPreviewGroup.setText("Preview");

		fPreviewer = new StyledText(fPreviewGroup, SWT.NONE);
		fPreviewer.setEnabled(false);
		fPreviewer.setBottomMargin(5);
		fPreviewer.setTopMargin(5);
		fPreviewer.setRightMargin(10);
		fPreviewer.setLeftMargin(10);
		fPreviewer.setText("This line is unstyled.\r\nThis line previews the style.\r\nThis line is unstyled.");
		fPreviewer.setFont(StyleDefinition.DEFAULT_FONT);

		for (int i = 0; i < IDX_LAST; i++) {
			fEnableButton[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateEnableState();
				}
			});
		}

		for (int i = 0; i <= IDX_BORDER; i++) {
			fFillChooser[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updatePreviewControl();
				}
			});
		}
		fUnderlineStyle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePreviewControl();
			}
		});
		fBorderStyle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePreviewControl();
			}
		});

		updateControls();
		updateEnableState();

		return container;
	}


	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}


	@Override
	protected void okPressed() {
		updateModel();
		super.okPressed();
	}


	protected void changeFontPressed() {
		FontDialog dialog = new FontDialog(getShell());
		dialog.setEffectsVisible(false);
		dialog.setText("Font");

		if (fFontLabel.getData() != null) {
			FontData[] fontData = (FontData[]) fFontLabel.getData();
			dialog.setFontList(fontData);
		}

		if (dialog.open() != null) {
			FontData[] fontData = dialog.getFontList();
			fFontLabel.setData(fontData);
			updatePreviewControl();
		}
	}


	protected void updateEnableState() {
		fChangeFontButton.setEnabled(fEnableButton[IDX_FONT].getSelection());
		fFontLabel.setEnabled(fEnableButton[IDX_FONT].getSelection());
		if (fFontLabel.isEnabled() && fFontLabel.getData() == null)
			fFontLabel.setData(StyleDefinition.DEFAULT_FONT.getFontData());

		for (int i = IDX_FOREGROUND; i <= IDX_BORDER; i++)
			fFillChooser[i].setEnabled(fEnableButton[i].getSelection());
		fUnderlineStyle.setEnabled(fEnableButton[IDX_UNDERLINE].getSelection());
		fBorderStyle.setEnabled(fEnableButton[IDX_BORDER].getSelection());

		updatePreviewControl();
	}


	private void updateControls() {
		if (fStyle == null) return;

		fName.setText(fStyle.name);

		fEnableButton[IDX_FONT].setSelection(fStyle.fontData != null && fStyle.fontData.length > 0);
		if (fEnableButton[IDX_FONT].getSelection())
			fFontLabel.setData(fStyle.fontData);

		fEnableButton[IDX_FOREGROUND].setSelection(fStyle.foreground != null);
		if (fEnableButton[IDX_FOREGROUND].getSelection())
			fFillChooser[IDX_FOREGROUND].setColor(SWTResourceManager.getColor(fStyle.foreground));

		fEnableButton[IDX_BACKGROUND].setSelection(fStyle.background != null);
		if (fEnableButton[IDX_BACKGROUND].getSelection())
			fFillChooser[IDX_BACKGROUND].setColor(SWTResourceManager.getColor(fStyle.background));

		fEnableButton[IDX_UNDERLINE].setSelection(fStyle.underline);
		if (fEnableButton[IDX_UNDERLINE].getSelection()) {
			fFillChooser[IDX_UNDERLINE].setColor(SWTResourceManager.getColor(fStyle.underlineColor));
			fUnderlineStyle.select(fStyle.underlineStyle);
		}

		fEnableButton[IDX_STRIKEOUT].setSelection(fStyle.strikeout);
		if (fEnableButton[IDX_STRIKEOUT].getSelection()) {
			fFillChooser[IDX_STRIKEOUT].setColor(SWTResourceManager.getColor(fStyle.strikeoutColor));
		}

		fEnableButton[IDX_BORDER].setSelection(fStyle.border);
		if (fEnableButton[IDX_BORDER].getSelection()) {
			fFillChooser[IDX_BORDER].setColor(SWTResourceManager.getColor(fStyle.borderColor));
			fBorderStyle.select(fStyle.borderStyle);
		}

		updatePreviewControl();
	}


	private void updateModel() {
		if (fStyle == null) return;

		fStyle.name = fName.getText();

		if (fEnableButton[IDX_FONT].getSelection())
			fStyle.fontData = (FontData[]) fFontLabel.getData();
		else
			fStyle.fontData = null;

		if (fEnableButton[IDX_FOREGROUND].getSelection())
			fStyle.foreground = fFillChooser[IDX_FOREGROUND].getColor().getRGB();
		else
			fStyle.foreground = null;

		if (fEnableButton[IDX_BACKGROUND].getSelection())
			fStyle.background = fFillChooser[IDX_BACKGROUND].getColor().getRGB();
		else
			fStyle.background = null;

		if (fEnableButton[IDX_UNDERLINE].getSelection()) {
			fStyle.underline = true;
			fStyle.underlineColor = fFillChooser[IDX_UNDERLINE].getColor().getRGB();
			fStyle.underlineStyle = fUnderlineStyle.getSelectionIndex();
		} else {
			fStyle.underline = false;
			fStyle.underlineColor = null;
			fStyle.underlineStyle = -1;
		}

		if (fEnableButton[IDX_STRIKEOUT].getSelection()) {
			fStyle.strikeout = true;
			fStyle.strikeoutColor = fFillChooser[IDX_STRIKEOUT].getColor().getRGB();
		} else {
			fStyle.strikeout = false;
			fStyle.strikeoutColor = null;
		}

		if (fEnableButton[IDX_BORDER].getSelection()) {
			fStyle.border = true;
			fStyle.borderStyle = fBorderStyle.getSelectionIndex();
			fStyle.borderColor = fFillChooser[IDX_BORDER].getColor().getRGB();
		} else {
			fStyle.border = false;
			fStyle.borderStyle = -1;
			fStyle.borderColor = null;
		}
	}


	public void updatePreviewControl() {
		StyleRange range = new StyleRange();
		range.start = fPreviewer.getText().indexOf("\r\n") + 1;
		range.length = fPreviewer.getText().indexOf("\r\n", range.start) - range.start;
		if (fEnableButton[IDX_FONT].getSelection() && fFontLabel.getData() != null) {
			FontData fontData = ((FontData[]) fFontLabel.getData())[0];
			range.font = SWTResourceManager.getFont(fontData.getName(), fontData.getHeight(), fontData.getStyle());
		}
		if (fEnableButton[IDX_FOREGROUND].getSelection())
			range.foreground = fFillChooser[IDX_FOREGROUND].getColor();
		if (fEnableButton[IDX_BACKGROUND].getSelection())
			range.background = fFillChooser[IDX_BACKGROUND].getColor();
		if (fEnableButton[IDX_UNDERLINE].getSelection()) {
			range.underline = true;
			range.underlineColor = fFillChooser[IDX_UNDERLINE].getColor();
			switch (fUnderlineStyle.getSelectionIndex()) {
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
		if (fEnableButton[IDX_STRIKEOUT].getSelection()) {
			range.strikeout = true;
			range.strikeoutColor = fFillChooser[IDX_STRIKEOUT].getColor();
		}
		if (fEnableButton[IDX_BORDER].getSelection()) {
			range.borderColor = fFillChooser[IDX_BORDER].getColor();
			switch (fBorderStyle.getSelectionIndex()) {
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
		fPreviewer.setStyleRange(range);

		StringBuffer desc = new StringBuffer();
		if (fFontLabel.getData() != null) {
			FontData[] fontData = (FontData[]) fFontLabel.getData();
			for (FontData e : fontData) {
				desc.append(e.getName());
				desc.append(" ");
				desc.append(e.getHeight());
				int style = e.getStyle();
				if ((style & SWT.BOLD) != 0)
					desc.append(" Bold");
				if ((style & SWT.ITALIC) != 0)
					desc.append(" Italic");
			}
		} else {
			desc.append("Default");
		}
		fFontLabel.setText(desc.toString());
	}

}
