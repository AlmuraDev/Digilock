package dk.gabriel333.Library;

import org.bukkit.plugin.Plugin;

public class BITPlugin {
	
	public static String PLUGIN_NAME; 
	public static String PLUGIN_FOLDER;

	public static void setupPlugin(Plugin plugin) {
		PLUGIN_NAME=plugin.getDescription().getName();
		PLUGIN_FOLDER = plugin.getDataFolder().toString();
		BITPermissions.setupPermissions(plugin);
	}
	

}
