package eu.martinlange.console.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import eu.martinlange.console.Plugin;
import eu.martinlange.console.model.GroupDefinition;
import eu.martinlange.console.model.PatternDefinition;
import eu.martinlange.console.model.StyleDefinition;
import eu.martinlange.console.model.StyleDefinitionCache;

public class PatternDefinitionDialog extends Dialog implements ModifyListener {

	private String SASH_WEIGHT_0 = "SASH_WEIGHT_0";
	private String SASH_WEIGHT_1 = "SASH_WEIGHT_1";
	
	private SashForm fSash;
	private StyledText fName;
	private StyledText fRegex;
	private Button fPreview;
	private Button fComments;
	private Button fCaseInsensitive;
	private Table fTable;
	private TableViewer fTableViewer;

	private String fTitle;
	private PatternDefinition fPattern;
	private List<GroupDefinition> fGroups;


	protected PatternDefinitionDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		fTitle = "";
		fPattern = null;
		fGroups = new ArrayList<GroupDefinition>();
	}


	public void setText(String title) {
		fTitle = title;
	}


	public String getText() {
		return fTitle;
	}


	public void setPattern(PatternDefinition pattern) {
		fPattern = pattern;
	}


	public PatternDefinition getPattern() {
		return fPattern;
	}


	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(fTitle);
		newShell.setMinimumSize(400, 400);
	}


	@Override
	protected Control createDialogArea(Composite parent) {

		PixelConverter converter = new PixelConverter(parent.getFont());

		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);

		fSash = new SashForm(container, SWT.VERTICAL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 400;
		fSash.setLayoutData(gd);

		Composite topComp = new Composite(fSash, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 7;
		layout.marginRight = 10;
		layout.marginLeft = 10;
		layout.marginBottom = 0;
		layout.marginTop = 10;
		topComp.setLayout(layout);

		Label lblName = new Label(topComp, SWT.NONE);
		lblName.setText("Name:");

		fName = new StyledText(topComp, SWT.BORDER | SWT.SINGLE);
		fName.setRightMargin(5);
		fName.setLeftMargin(5);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		fName.setLayoutData(gd);

		Label lblPattern = new Label(topComp, SWT.NONE);
		gd = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd.verticalIndent = 15;
		lblPattern.setLayoutData(gd);
		lblPattern.setText("Pattern:");

		fRegex = new StyledText(topComp, SWT.BORDER | SWT.V_SCROLL);
		fRegex.setWordWrap(true);
		fRegex.setWrapIndent(10);
		fRegex.setTopMargin(3);
		fRegex.setBottomMargin(3);
		fRegex.setRightMargin(5);
		fRegex.setLeftMargin(5);
		fRegex.setFont(StyleDefinition.DEFAULT_FONT);
		fRegex.addModifyListener(this);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.widthHint = 450;
		gd.heightHint = 50;
		gd.verticalIndent = 15;
		fRegex.setLayoutData(gd);

		new Label(topComp, SWT.NONE);
		fPreview = new Button(topComp, SWT.CHECK);
		fPreview.setText("Preview style in text field");
		fPreview.setSelection(false);
		fPreview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePreviewControl();
				super.widgetSelected(e);
			}
		});

		Label lblFlags = new Label(topComp, SWT.NONE);
		gd = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd.verticalIndent = 10;
		lblFlags.setLayoutData(gd);
		lblFlags.setText("Flags:");

		fComments = new Button(topComp, SWT.CHECK);
		fComments.setText("Permit whitespaces and comments in pattern.");
		fComments.setSelection(true);
		gd = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd.verticalIndent = 10;
		fComments.setLayoutData(gd);

		new Label(topComp, SWT.NONE);
		fCaseInsensitive = new Button(topComp, SWT.CHECK);
		fCaseInsensitive.setText("Enables case-insensitive matching.");
		fCaseInsensitive.setSelection(false);

		Composite bottomComp = new Composite(fSash, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 7;
		layout.marginRight = 10;
		layout.marginLeft = 10;
		layout.marginBottom = 10;
		layout.marginTop = 0;
		bottomComp.setLayout(layout);

		Label lblGroups = new Label(bottomComp, SWT.NONE);
		gd = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd.verticalIndent = 5;
		lblGroups.setLayoutData(gd);
		lblGroups.setText("Groups:");

		Composite composite = new Composite(bottomComp, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 100;
		gd.widthHint = 450;
		gd.verticalIndent = 5;
		composite.setLayoutData(gd);
		TableColumnLayout tclayout = new TableColumnLayout();
		composite.setLayout(tclayout);

		fTable = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		fTableViewer = new TableViewer(fTable);
		fTable = fTableViewer.getTable();
		fTable.setHeaderVisible(true);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(fTableViewer, SWT.NONE);
		TableColumn fRangeColumn = tableViewerColumn.getColumn();
		fRangeColumn.setResizable(false);
		int widthInPixel = converter.convertWidthInCharsToPixels("Whole line".length() + 10);
		tclayout.setColumnData(fRangeColumn, new ColumnPixelData(widthInPixel, true, true));
		fRangeColumn.setText("Group");

		tableViewerColumn = new TableViewerColumn(fTableViewer, SWT.NONE);
		TableColumn fStyleColumn = tableViewerColumn.getColumn();
		fStyleColumn.setResizable(false);
		tclayout.setColumnData(fStyleColumn, new ColumnWeightData(1, 100, true));
		fStyleColumn.setText("Style");

		fTableViewer.setLabelProvider(DefaultLabelProvider.getInstance());
		fTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		setupEditingSupport(tableViewerColumn);

		fSash.setSashWidth(3);
		fSash.setWeights(new int[] { 50, 50 });
		
		IDialogSettings settings = getDialogBoundsSettings();
		if (settings != null) {
			try {
				int[] weights = new int[2];
				weights[0] = settings.getInt(SASH_WEIGHT_0);
				weights[1] = settings.getInt(SASH_WEIGHT_1);
				fSash.setWeights(weights);
			} catch (NumberFormatException e) {
			}
		}

		updateControls();
		updatePreviewControl();

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


	private void updateControls() {
		if (fPattern == null)
			return;

		if (fPattern.styles.size() > 0) {
			resizeGroupCache(fPattern.styles.size());
			for (int i = 0; i < fPattern.styles.size(); i++) {
				GroupDefinition group = fGroups.get(i);
				group.style = StyleDefinitionCache.INSTANCE.getById(fPattern.styles.get(i));
			}
		}
		fName.setText(fPattern.name);
		fRegex.setText(fPattern.regex);

		fPreview.setSelection(fPattern.preview);
		fComments.setSelection((fPattern.flags & Pattern.COMMENTS) == Pattern.COMMENTS);
		fCaseInsensitive.setSelection((fPattern.flags & Pattern.CASE_INSENSITIVE) == Pattern.CASE_INSENSITIVE);
	}


	private void updateModel() {
		if (fPattern == null)
			return;

		fPattern.name = fName.getText();
		fPattern.regex = fRegex.getText();

		fPattern.preview = fPreview.getSelection();

		fPattern.flags = 0;
		if (fComments.getSelection())
			fPattern.flags |= Pattern.COMMENTS;
		if (fCaseInsensitive.getSelection())
			fPattern.flags |= Pattern.CASE_INSENSITIVE;

		fPattern.styles.clear();
		for (TableItem e : fTable.getItems()) {
			GroupDefinition group = (GroupDefinition) e.getData();
			fPattern.styles.add(group.style.id);
		}
	}


	@Override
	public void modifyText(ModifyEvent event) {
		try {
			List<GroupDefinition> groups = new ArrayList<GroupDefinition>();

			String text = fRegex.getText();
			int n = Pattern.compile(text).matcher("").groupCount();
			resizeGroupCache(n);
			for (int i = 0; i <= n; i++)
				groups.add(fGroups.get(i));

			fTableViewer.setInput(groups.toArray(new GroupDefinition[0]));
			fTableViewer.refresh(true);

			updatePreviewControl();
		} catch (PatternSyntaxException e) {
		}
	}


	protected void resizeGroupCache(int newSize) {
		for (int i = 0; i <= newSize; i++) {
			if (fGroups.size() <= i) {
				String name = i == 0 ? "Whole line" : String.format("Group %d", i);
				fGroups.add(new GroupDefinition(name, StyleDefinition.DEFAULT));
			}
		}
	}


	protected void setupEditingSupport(ViewerColumn column) {
		column.setEditingSupport(new EditingSupport(fTableViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof GroupDefinition && value instanceof StyleDefinition) {
					GroupDefinition e = (GroupDefinition) element;
					e.style = (StyleDefinition) value;
					getViewer().refresh(true);
					updatePreviewControl();
				}
			}


			@Override
			protected Object getValue(Object element) {
				if (element instanceof GroupDefinition) {
					GroupDefinition e = (GroupDefinition) element;
					return e.style;
				}
				return null;
			}


			@Override
			protected CellEditor getCellEditor(Object element) {
				Table parent = ((TableViewer) getViewer()).getTable();
				CellEditor cellEditor = createCellEditor();
				cellEditor.create(parent);
				return cellEditor;
			}


			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
	}


	protected CellEditor createCellEditor() {
		return new CellEditor() {

			private Composite fParent;
			private StyleDefinition fStyle;


			@Override
			protected void doSetValue(Object value) {
				Assert.isTrue(value instanceof StyleDefinition);
				fStyle = (StyleDefinition) value;
			}


			@Override
			protected Object doGetValue() {
				return fStyle;
			}


			@Override
			protected void doSetFocus() {
				// ignore
			}


			@Override
			protected Control createControl(Composite parent) {
				fParent = parent;
				return null;
			}


			@Override
			public void activate() {
				StyleSelectionDialog dialog = new StyleSelectionDialog(fParent.getShell());
				if (dialog.open() == Dialog.OK && dialog.getResult() != null) {
					doSetValue(dialog.getResult()[0]);
				}
				dialog = null;

				fireApplyEditorValue();
			}


			@Override
			public void activate(ColumnViewerEditorActivationEvent activationEvent) {
				if (activationEvent.eventType != ColumnViewerEditorActivationEvent.TRAVERSAL) {
					super.activate(activationEvent);
				}
			}
		};
	}


	protected void updatePreviewControl() {
		if (fPreview.getSelection()) {
			StyleRange[] ranges = new StyleRange[fTable.getItemCount()];
			for (int i = 0; i < ranges.length; i++) {
				GroupDefinition group = (GroupDefinition) fTable.getItem(i).getData();
				ranges[i] = (StyleRange) group.style.getAdapter(StyleRange.class);
			}

			String text = fRegex.getText();

			StyleRange range = (StyleRange) ranges[0].clone();
			range.start = 0;
			range.length = text.length();
			fRegex.setStyleRange(range);

			int n = 1;
			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);
				if (ch == '(') {
					range = (StyleRange) ranges[n++].clone();
					range.start = i;
					range.length = 1;

					int skip = 1;
					for (int j = i + 1; j < text.length(); j++) {
						ch = text.charAt(j);
						if (ch == '(')
							skip++;
						else if (ch == ')')
							skip--;

						range.length++;
						if (skip == 0) {
							fRegex.setStyleRange(range);
							break;
						}
					}
				}
			}

			int lineCount = fRegex.getLineCount();
			fRegex.setLineBackground(0, lineCount, ranges[0].background);
		} else {
			fRegex.setStyleRange(null);

			int lineCount = fRegex.getLineCount();
			fRegex.setLineBackground(0, lineCount, fRegex.getBackground());
		}
	}
	

	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = Plugin.getDefault().getDialogSettings().getSection(getClass().getName());
		if (settings == null)
			settings = Plugin.getDefault().getDialogSettings().addNewSection(getClass().getName());
		return settings;
	}


	protected int getDialogBoundsStrategy() {
		return DIALOG_PERSISTLOCATION | DIALOG_PERSISTSIZE;
	}
	
	
	@Override
	public boolean close() {
		IDialogSettings settings = getDialogBoundsSettings();
		if (settings != null) {
			int[] weights = fSash.getWeights();
			if (weights.length == 2) {
				settings.put(SASH_WEIGHT_0, weights[0]);
				settings.put(SASH_WEIGHT_1, weights[1]);
			}
		}
		
		return super.close();
	}

}
