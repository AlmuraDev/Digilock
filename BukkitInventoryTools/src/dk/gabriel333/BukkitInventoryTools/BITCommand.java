package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.BITMessages;
import dk.gabriel333.Library.BITPermissions;

public class BITCommand implements CommandExecutor {

	public BITCommand(BIT instance) {
		plugin=instance;
	}
	
	public BIT plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		if (BIT.isPlayer(sender)) {
			if (BITPermissions.hasPerm(sender, "admin",
					BITPermissions.NOT_QUIET)) {
				
					if (args.length == 0) {
						return false;
					} else if (args.length == 1) { 
						String action;
						action = args[0];
							if (action.equalsIgnoreCase("reload")) {
								plugin.onDisable();
								BITPermissions.permissions3=false;
								BITPermissions.permissionsBukkit=false;
								BITPermissions.permissionsex=false; 
								BITPermissions.bPermissions=false;
								plugin.onEnable();
								BITMessages.showInfo("BIT was restarted...." );	
								sPlayer.sendMessage("BIT was restarted.");
							}
					}
			}
			return true;
		} else {
			BITMessages.showWarning("You can't use /bit in the console.");
			return false;
		}
	}
}
