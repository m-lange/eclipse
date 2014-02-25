package eu.martinlange.console.preferences;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.dialogs.SelectionDialog;
import eu.martinlange.console.model.StyleDefinition;
import eu.martinlange.console.model.StyleDefinitionCache;

public class StyleSelectionDialog extends SelectionDialog {

	private StyledText fPattern;
	private Table fTable;
	private TableViewer fTableViewer;
	private Composite fTableComposite;


	public StyleSelectionDialog(Shell parentShell) {
		super(parentShell);
		setTitle("Select Style");
		setMessage("Enter style name prefix or pattern (* = any string, ? = any char):");
	}


	@Override
	protected Control createDialogArea(Composite parent) {

		PixelConverter converter = new PixelConverter(StyleDefinition.DEFAULT_FONT);

		Composite composite = (Composite) super.createDialogArea(parent);
		initializeDialogUnits(composite);
		createMessageArea(composite);

		fPattern = new StyledText(composite, SWT.BORDER | SWT.SINGLE);
		fPattern.setRightMargin(5);
		fPattern.setLeftMargin(5);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
		gd.widthHint = 400;
		fPattern.setLayoutData(gd);
		fPattern.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				fFilter.setPattern(fPattern.getText());
				fTableViewer.setFilters(new ViewerFilter[] { fFilter });
			}
		});

		fTableComposite = new Composite(composite, SWT.NONE);
		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 400;
		gd.heightHint = 350;
		fTableComposite.setLayoutData(gd);
		TableColumnLayout layout = new TableColumnLayout();
		fTableComposite.setLayout(layout);

		fTableViewer = new TableViewer(fTableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		fTable = fTableViewer.getTable();
		fTable.setHeaderVisible(true);

		TableColumn fNameColumn = new TableColumn(fTable, SWT.NONE);
		fNameColumn.setResizable(false);
		layout.setColumnData(fNameColumn, new ColumnWeightData(1, 100, true));
		fNameColumn.setText("Name");

		TableColumn fPreviewColumn = new TableColumn(fTable, SWT.CENTER);
		fPreviewColumn.setResizable(false);
		int widthInPixel = converter.convertWidthInCharsToPixels(DefaultLabelProvider.PREVIEW_TEXT.length());
		layout.setColumnData(fPreviewColumn, new ColumnPixelData(widthInPixel, true, true));
		fPreviewColumn.setText("Preview");
		fTableViewer.setLabelProvider(DefaultLabelProvider.getInstance());
		fTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		fTableViewer.setInput(StyleDefinitionCache.INSTANCE.getStyles());
		fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
				if (selection != null && selection.size() > 0)
					okPressed();
			}
		});
		fFilter.setIncludeLeadingWildcard(true);

		Dialog.applyDialogFont(composite);
		return composite;
	}


	@Override
	protected void okPressed() {
		IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
		if (selection != null && selection.size() > 0) {
			List<StyleDefinition> result = new ArrayList<StyleDefinition>();
			result.add((StyleDefinition) selection.getFirstElement());
			setResult(result);
		}
		super.okPressed();
	}

	private PatternFilter fFilter = new PatternFilter() {
		@Override
		protected boolean isParentMatch(Viewer viewer, Object element) {
			return false;
		};
	};

}
