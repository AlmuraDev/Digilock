package dk.gabriel333.digilock.listener;

import dk.gabriel333.digilock.Digilock;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import org.getspout.spoutapi.player.SpoutPlayer;

public class InventoryListener implements Listener {
	public Digilock plugin;

	public InventoryListener(Digilock plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (sPlayer.isSpoutCraftEnabled()) {
			//TODO use inventory API
		}
	}
}