/**
 * Hook info
 * 
 * @author palm3r
 */
final class HookInfo {
	private PluginListener listener;
	private PluginListener.Priority priority;
	private PluginRegisteredListener registered;

	public HookInfo(PluginListener listener, PluginListener.Priority priority) {
		this.listener = listener;
		this.priority = priority;
	}

	public void enable(PluginLoader.Hook hook, Plugin plugin) {
		if (registered == null) {
			registered = etc.getLoader()
				.addListener(hook, listener, plugin, priority);
		}
	}

	public void disable() {
		if (registered != null) {
			etc.getLoader().removeListener(registered);
			registered = null;
		}
	}
}