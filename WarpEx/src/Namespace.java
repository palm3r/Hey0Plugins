public enum Namespace {

	Personal("%s"), Global("*"), Secret("!");
	private String format;

	private Namespace(String format) {
		this.format = format;
	}

	public String get(Player player) {
		return String.format(format, player.getName());
	}

}
