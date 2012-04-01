package dk.gabriel333.digilock.util;

import org.bukkit.plugin.Plugin;

public class LockPlugin {
	public static String PLUGIN_NAME;
	public static String PLUGIN_FOLDER;

	public static void setupPlugin(Plugin plugin) {
		PLUGIN_NAME = plugin.getDescription().getName();
		PLUGIN_FOLDER = plugin.getDataFolder().toString();
		Permissions.setupPermissions(plugin);
	}
}
