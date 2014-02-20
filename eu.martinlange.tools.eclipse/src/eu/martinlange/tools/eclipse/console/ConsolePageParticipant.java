package eu.martinlange.tools.eclipse.console;

import java.lang.reflect.Field;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.AbstractConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

public class ConsolePageParticipant implements IConsolePageParticipant {

	private ConsoleLineStyleListener fListener = new ConsoleLineStyleListener();


	@Override
	public void init(IPageBookViewPage page, IConsole console) {
		if (page.getControl() instanceof StyledText) {
			StyledText viewer = (StyledText) page.getControl();
			viewer.addLineStyleListener(fListener);
			viewer.addLineBackgroundListener(fListener);
		}

		if (console instanceof org.eclipse.debug.ui.console.IConsole) {
			ILaunch launch = ((org.eclipse.debug.ui.console.IConsole) console).getProcess().getLaunch();
			setName(console, String.format("<%s> %s", launch.getLaunchMode(), console.getName()));
			ConsolePlugin.getDefault().getConsoleManager().refresh(console);
		}

	}


	@Override
	public void dispose() {
	}


	@Override
	public void activated() {
	}


	@Override
	public void deactivated() {
	}


	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}


	private void setName(IConsole console, String name) {
		try {
			if (console instanceof AbstractConsole)
			{
				Field f = AbstractConsole.class.getDeclaredField("fName");
				f.setAccessible(true);
				f.set((AbstractConsole) console, name);
			}
		} catch (Exception e) {
			ConsolePlugin.log(e);
		}
	}

}
