final class HookListener {

	private final PluginListener listener;
	private final PluginListener.Priority priority;
	private PluginRegisteredListener registered;

	public HookListener(PluginListener listener, PluginListener.Priority priority) {
		this.listener = listener;
		this.priority = priority;
	}

	public boolean isEnabled() {
		return registered != null;
	}

	public void enable(PluginLoader.Hook hook, Plugin plugin) {
		if (registered == null) {
			registered =
				etc.getLoader().addListener(hook, listener, plugin, priority);
		}
	}

	public void disable() {
		if (registered != null) {
			etc.getLoader().removeListener(registered);
			registered = null;
		}
	}

}