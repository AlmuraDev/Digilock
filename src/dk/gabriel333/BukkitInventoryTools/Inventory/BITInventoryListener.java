package dk.gabriel333.BukkitInventoryTools.Inventory;

import java.util.HashMap;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;
import dk.gabriel333.Library.BITPermissions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import org.getspout.spoutapi.player.SpoutPlayer;

public class BITInventoryListener implements Listener {
	public BIT plugin;
	public BITInventoryListener(BIT plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (sPlayer.isSpoutCraftEnabled()) {
			if (BITConfig.SORT_DISPLAYSORTARCHIEVEMENT && BITPermissions.hasPerm(sPlayer, "sortinventory.use", BITPermissions.QUIET)) {
				BITMessages.sendNotification(sPlayer, "Sort:" + BITConfig.LIBRARY_SORTKEY);
			}
		}
	}
}