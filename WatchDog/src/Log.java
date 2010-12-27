import java.util.*;
import org.apache.commons.lang.builder.*;

public class Log extends DataRow {

	@Column(index = "log_time_idx")
	public Long time;

	@Column(index = "log_action_idx")
	public String action;

	@Column(index = "log_player_idx")
	public String player;

	@Column(index = "log_target_idx")
	public String target_id, target_name;

	@Column(index = "log_location_idx")
	public Double x, y, z;

	@Column(index = "log_flag_idx")
	public Boolean denied, kicked, banned;

	public Log() {
	}

	public static String getColor(boolean denied, boolean kicked, boolean banned) {
		String color = Colors.Yellow;
		if (denied) {
			color = Colors.Gold;
		}
		if (kicked) {
			color = Colors.Rose;
		}
		if (banned) {
			color = Colors.Red;
		}
		return color;
	}

	public String getColor() {
		return getColor(denied, kicked, banned);
	}

	public static String getMessage(String player, String action, String target, boolean denied, boolean kicked,
		boolean banned) {
		StringBuilder sb = new StringBuilder();
		sb.append(player);
		sb.append(" ");
		sb.append(action + (target != null ? " " + target : ""));
		// sb.append(String.format(" (%d,%d,%d)", x, y, z));
		if (denied) {
			sb.append(" DENIED");
		}
		if (kicked) {
			sb.append(" KICKED");
		}
		if (banned) {
			sb.append(" BANNED");
		}
		return sb.toString();
	}

	public String getMessage() {
		return getMessage(player, action, target_name, denied, kicked, banned);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(getId()).append(new Date(time)).append(action).append(player).append(
			target_id).append(target_name).append(x).append(y).append(z).append(denied).append(kicked).append(banned).toString();
	}

}
