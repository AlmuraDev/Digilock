package dk.gabriel333.BITBackpack;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import dk.gabriel333.BukkitInventoryTools.BIT;

public class BITBackpackAPI {
	BIT	plugin;
	public static boolean bitBackpackEnabled = true;

	/**
	 * Primary constructor. If 'spoutBackpackEnabled' equal true and 'plugin' is initialized, the server is running
	 * SpoutBackpack.
	 */
	public BITBackpackAPI() {
		Plugin SpoutBackpackPlugin = (Plugin) Bukkit.getServer().getPluginManager().getPlugin("BukkitInventoryTools");
		if (SpoutBackpackPlugin == null) { return; }
		bitBackpackEnabled = true;
		plugin = (BIT) SpoutBackpackPlugin;
	}

	/**
	 * Return if or not the player's Backpack is open.
	 * 
	 * @param player
	 *            The player you want to get the inventory.
	 * @return open Player's Backpack opened or not.
	 */
	public boolean isOpenSpoutBackpack(Player player) {
		return BITBackpack.isOpenBackpack(player);
	}

	/**
	 * Get the opened SpoutBackpack inventory of a player.
	 * 
	 * @param player
	 *            The player you want to get the inventory.
	 * @return inventory The SpoutBackpack inventory of this player.
	 */
	public Inventory getOpenedSpoutBackpack(Player player) {
		return BITBackpack.getOpenedBackpack(player);
	}

	/**
	 * Get the closed SpoutBackpack inventory of a player.
	 * 
	 * @param player
	 *            The player you want to get the inventory.
	 * @return inventory The SpoutBackpack inventory of this player.
	 */
	public Inventory getClosedSpoutBackpack(Player player) {
		return BITBackpack.getClosedBackpack(player);
	}

	/**
	 * Set the closed SpoutBackpack inventory of a player.
	 * 
	 * @param player
	 *            The player you want to set the inventory.
	 * @param inventory
	 *            The SpoutBackpack inventory of this player.
	 */
	public void setClosedSpoutBackpack(Player player, Inventory inventory) {
		BITBackpack.setClosedBackpack(player, inventory);
		return;
	}

	/**
	 * Check if the server is running SpoutBackpack.
	 * 
	 * @return true if SpoutBackpack is running on the server.
	 */
	public boolean hasSpoutBackpack() {
		return bitBackpackEnabled;
	}
}
