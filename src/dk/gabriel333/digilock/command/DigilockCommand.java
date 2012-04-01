package dk.gabriel333.digilock.command;

import dk.gabriel333.digilock.Digilock;
import dk.gabriel333.digilock.util.Messages;
import dk.gabriel333.digilock.util.Permissions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.getspout.spoutapi.player.SpoutPlayer;

public class DigilockCommand implements CommandExecutor {
	public DigilockCommand(Digilock instance) {
		plugin = instance;
	}

	public Digilock plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command command,
							 String label, String[] args) {
		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		if (Digilock.isPlayer(sender)) {
			if (Permissions.hasPerm(sender, "admin",
					Permissions.NOT_QUIET)) {

				if (args.length == 0) {
					return false;
				} else if (args.length == 1) {
					String action;
					action = args[0];
					if (action.equalsIgnoreCase("reload")) {
						plugin.onDisable();
						plugin.onEnable();
						Messages.showInfo("Digilock was restarted....");
						sPlayer.sendMessage("Digilock was restarted.");
					}
				}
			}
			return true;
		} else {
			Messages.showWarning("You can't use /bit in the console.");
			return false;
		}
	}
}
