package dk.gabriel333.BITBackpack;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.BITConfig;

public class BITBackpackPlayerListener extends PlayerListener {
	private BIT plugin;

	public BITBackpackPlayerListener(BIT plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			Player player = event.getPlayer();
			BITBackpack.loadInventory(player, player.getWorld());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {

		if (((event.getFrom().getWorld().getName() != event.getTo().getWorld()
				.getName()) || plugin.portals.contains(event.getPlayer()))) {
			String path = "SBP.InventoriesShare."
					+ event.getTo().getWorld().getName();
			if (!BITConfig.config.contains(path)) {
				BITConfig.addBooleanParmToConfig(path, true);
			}
			// TODO: check for null = no backpack
			if (!BITConfig.getBooleanParm(path, 
					BITConfig.getBooleanParm("SBP.InventoriesShareDefault",false))) {
				try {
					Player player = event.getPlayer();
					if (BITBackpack.inventories.containsKey(player.getName())) {
						BITBackpackInventorySaveTask.saveInventory(player,
								event.getFrom().getWorld());
						BITBackpack.inventories.remove(player.getName());
					}
					BITBackpack.loadInventory(player, event.getTo().getWorld());
				} catch (Exception e) {
					e.printStackTrace();
				}
				plugin.portals.remove(event.getPlayer());
			}
		}
	}

	@Override
	public void onPlayerPortal(PlayerPortalEvent event) {
		plugin.portals.add(event.getPlayer());
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		try {
			Player player = event.getPlayer();
			BITBackpackInventorySaveTask.saveInventory(player,
					player.getWorld());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		try {
			Player player = event.getPlayer();
			BITBackpackInventorySaveTask.saveInventory(player,
					player.getWorld());
			if (BITBackpack.inventories.containsKey(player)) {
				BITBackpack.inventories.remove(player);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
