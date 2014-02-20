package eu.martinlange.tools.eclipse.theme;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;
import eu.martinlange.tools.eclipse.Plugin;

public class ThemedRangeIndicatorInstaller implements IWindowListener, IPartListener2 {

	public static final ThemedRangeIndicatorInstaller INSTANCE = new ThemedRangeIndicatorInstaller();

	private Collection<IWorkbenchWindow> fWindows = new HashSet<IWorkbenchWindow>();
	private IThemeManager fThemeManager = null;
	private ITheme fCurrentTheme = null;


	private ThemedRangeIndicatorInstaller() {
		fThemeManager = PlatformUI.getWorkbench().getThemeManager();
		fCurrentTheme = fThemeManager.getCurrentTheme();

	}


	public void install() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) {
			Plugin.log("Can't find workbench");
			return;
		}

		workbench.addWindowListener(this);
		IWorkbenchWindow[] wnds = workbench.getWorkbenchWindows();
		for (int i = 0; i < wnds.length; i++) {
			IWorkbenchWindow window = wnds[i];
			register(window);
		}
	}


	public void uninstall() {
		for (Iterator<IWorkbenchWindow> iterator = fWindows.iterator(); iterator.hasNext();) {
			IWorkbenchWindow window = iterator.next();
			unregister(window);
		}
	}


	private void register(IWorkbenchWindow wnd) {
		wnd.getPartService().addPartListener(this);
		fWindows.add(wnd);
		IWorkbenchPage[] pages = wnd.getPages();
		for (IWorkbenchPage page : pages) {
			IEditorReference[] editorRefs = page.getEditorReferences();
			for (IEditorReference editorRef : editorRefs)
				partActivated(editorRef);
		}

		IWorkbenchPage page = wnd.getActivePage();
		if (page != null)
			activated(page.getActivePartReference());
	}


	private void unregister(IWorkbenchWindow wnd) {
		wnd.getPartService().removePartListener(this);
		fWindows.remove(wnd);
	}


	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		activated(partRef);
	}


	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		activated(partRef);
	}


	@Override
	public void windowClosed(IWorkbenchWindow window) {
		unregister(window);
	}


	@Override
	public void windowOpened(IWorkbenchWindow window) {
		register(window);
	}


	private void activated(IWorkbenchPartReference partRef) {
		try {
			if (partRef == null)
				return;

			ColorRegistry color = fCurrentTheme.getColorRegistry();

			IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (editorPart == null)
				return;

			if (editorPart instanceof AbstractTextEditor) {
				SourceViewer sv = (SourceViewer) getSourceViewer((AbstractTextEditor) editorPart);

				OverviewRuler ruler = (OverviewRuler) getOverviewRuler(sv);
				ruler.getControl().setBackground(color.get(IThemeConstants.OVERVIEW_RULER_BKGND));

				IVerticalRuler vr = (IVerticalRuler) getVerticalRuler(sv);
				vr.getControl().setBackground(color.get(IThemeConstants.VERTICAL_RULER_BKGND));

				if (isRangeIndicatorEnabled(sv)) {
					sv.removeRangeIndication();
					sv.setRangeIndicator(new ThemedRangeIndicator());
				}
			}
		} catch (Exception e) {
			Plugin.log(e);
		}
	}


	private ITextViewer getSourceViewer(AbstractTextEditor editor)
			throws Exception {
		try {
			Method method = AbstractTextEditor.class.getDeclaredMethod("getSourceViewer");
			method.setAccessible(true);
			return (ITextViewer) method.invoke(editor);
		} catch (NullPointerException e) {
			Plugin.log(e);
			return null;
		}
	}


	private IOverviewRuler getOverviewRuler(SourceViewer viewer) {
		try {
			Field f = SourceViewer.class.getDeclaredField("fOverviewRuler");
			f.setAccessible(true);
			return (IOverviewRuler) f.get(viewer);
		} catch (Exception e) {
			Plugin.log(e);
			return null;
		}
	}


	private IVerticalRuler getVerticalRuler(SourceViewer viewer) {
		try {
			Field f = SourceViewer.class.getDeclaredField("fVerticalRuler");
			f.setAccessible(true);
			return (IVerticalRuler) f.get(viewer);
		} catch (Exception e) {
			Plugin.log(e);
			return null;
		}
	}


	private boolean isRangeIndicatorEnabled(SourceViewer viewer) {
		try {
			Field f = SourceViewer.class.getDeclaredField("fRangeIndicator");
			f.setAccessible(true);
			return f.get(viewer) != null;
		} catch (Exception e) {
			Plugin.log(e);
			return false;
		}
	}


	@Override
	public void windowActivated(IWorkbenchWindow window) {
	}


	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
	}


	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}


	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
	}


	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}


	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}


	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
	}


	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

}
