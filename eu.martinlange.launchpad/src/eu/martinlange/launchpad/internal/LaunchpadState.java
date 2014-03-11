package eu.martinlange.launchpad.internal;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IStateListener;
import org.eclipse.core.commands.State;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RadioState;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.services.IServiceLocator;

public class LaunchpadState extends AbstractSourceProvider {

	public static final String IS_CATEGORIZED = "eu.martinlange.launchpad.isCategorized";
	public static final String LAUNCH_MODE = "eu.martinlange.launchpad.launchMode";

	private String fLaunchMode;
	private boolean fCategorized;


	@Override
	public void initialize(IServiceLocator locator) {
		super.initialize(locator);

		ICommandService commandService = (ICommandService) locator.getService(ICommandService.class);
		Command command = commandService.getCommand("eu.martinlange.launchpad.command.rootMode");
		State state = command.getState(RegistryToggleState.STATE_ID);
		state.addListener(new IStateListener() {
			@Override
			public void handleStateChange(State state, Object oldValue) {
				setCategorized(((Boolean) state.getValue()).booleanValue());
			}
		});
		fCategorized = ((Boolean) state.getValue()).booleanValue();

		command = commandService.getCommand("eu.martinlange.launchpad.command.launchMode");
		state = command.getState(RadioState.STATE_ID);
		state.addListener(new IStateListener() {
			@Override
			public void handleStateChange(State state, Object oldValue) {
				setLaunchMode((String) state.getValue());
			}
		});
		fLaunchMode = (String) state.getValue();
	}


	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { IS_CATEGORIZED,
							  LAUNCH_MODE };
	}


	@Override
	public Map<String, String> getCurrentState() {
		Map<String, String> currentState = new HashMap<String, String>();

		currentState.put(IS_CATEGORIZED, fCategorized ? "true" : "false");
		currentState.put(LAUNCH_MODE, fLaunchMode);

		return currentState;
	}


	@Override
	public void dispose() {
	}


	public boolean isCategorized() {
		return fCategorized;
	}


	public void setCategorized(boolean categorized) {
		this.fCategorized = categorized;
		fireSourceChanged(ISources.WORKBENCH, IS_CATEGORIZED, new Boolean(categorized));
	}


	public String getLaunchMode() {
		return fLaunchMode;
	}


	public void setLaunchMode(String mode) {
		this.fLaunchMode = mode;
		fireSourceChanged(ISources.WORKBENCH, LAUNCH_MODE, mode);
	}
}
