package eu.martinlange.console.commands;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenConsolePreferencesCommandHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		String[] pages = {
				"eu.martinlange.console.preferencePages.Pattern",
				"eu.martinlange.console.preferencePages.Styles"
		};

		PreferenceManager manager = new PreferenceManager();
		List<?> nodes = PlatformUI.getWorkbench().getPreferenceManager().getElements(PreferenceManager.POST_ORDER);

		for (Iterator<?> i = nodes.iterator(); i.hasNext();) {
			IPreferenceNode node = (IPreferenceNode) i.next();
			for (String id : pages) {
				if (node.getId().equals(id))
					manager.addToRoot(node);
			}
		}

		if (manager.getElements(PreferenceManager.PRE_ORDER).size() == 0)
			return null;

		PreferenceDialog dialog = new PreferenceDialog(HandlerUtil.getActiveShell(event), manager);
		dialog.create();
		dialog.setMessage(manager.getRootSubNodes()[0].getLabelText());
		dialog.open();

		return null;

	}

}
