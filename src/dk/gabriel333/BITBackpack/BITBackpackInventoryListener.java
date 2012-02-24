package dk.gabriel333.BITBackpack;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventorySlotType;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.BITConfig;

public class BITBackpackInventoryListener extends InventoryListener {
	
	@SuppressWarnings("unused")
	private BIT plugin;

	public void SBInventoryListener(BIT plugin) {
	  this.plugin = plugin;
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = event.getPlayer();
		if (!BITBackpack.openedInventoriesOthers.containsKey(player.getName())) {
			if (BITBackpack.openedInventories.containsKey(player.getName())) {
				if (BITBackpack.widgets.containsKey(player.getName())
						&& BITConfig.SBP_useWidget) {
					BITBackpack.widgets.get(player.getName()).setVisible(false)
							.setDirty(true);
				}
				BITBackpack.openedInventories.remove(player.getName());
			}
			Inventory inv = event.getInventory();
			if (inv.getName().equals(BITBackpack.inventoryName)
					&& inv.getSize() == BITBackpack.allowedSize(
							player.getWorld(), player, true)) {
				BITBackpack.inventories.put(player.getName(), inv.getContents());
			}
		} else {
			if (BITBackpack.openedInventories.containsKey(BITBackpack.openedInventoriesOthers
					.get(player.getName()))) {
				BITBackpack.openedInventories.remove(BITBackpack.openedInventoriesOthers
						.get(player.getName()));
			}
			Inventory inv = event.getInventory();
			if (inv.getName().equals(BITBackpack.inventoryName)
					&& inv.getSize() == BITBackpack.allowedSize(
							Bukkit.getServer()
									.getPlayer(
											BITBackpack.openedInventoriesOthers
													.get(player.getName()))
									.getWorld(),
							Bukkit.getServer().getPlayer(
									BITBackpack.openedInventoriesOthers.get(player
											.getName())), true)) {
				BITBackpack.inventories.put(
						BITBackpack.openedInventoriesOthers.get(player.getName()),
						inv.getContents());
				BITBackpack.openedInventoriesOthers.remove(player.getName());
			}
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = event.getPlayer();
		if (BITBackpack.openedInventoriesOthers
				.containsKey(event.getPlayer().getName())) {
			player = Bukkit.getServer().getPlayer(
					BITBackpack.openedInventoriesOthers.get(event.getPlayer()));
		}
		InventorySlotType clickedSlotType = event.getSlotType();
		Inventory inv = event.getInventory();
		String invName = inv.getName();
		// ItemStack clickedItem = event.getItem();
		ItemStack placedItem = event.getCursor();
		// int slot = event.getSlot();
		// try {
		if (invName.equals("Backpack")
				&& clickedSlotType == InventorySlotType.CONTAINER
				&& (BITBackpack.openedInventories.containsKey(player.getName()) || BITBackpack.openedInventoriesOthers
						.containsKey(player.getName()))) {
			// 1=blacklist
			if (BITConfig.SBP_blackOrWhiteList == 1) {
				if (placedItem != null) {
					if (BITConfig.blacklist.contains(String.valueOf(placedItem
							.getTypeId()))) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED
								+ BIT.li.getMessage("yourenotallowedtomovethis")
								+ BITBackpack.inventoryName + "!");
						return;
					}
				}
				// 2=whitelist
			} else if (BITConfig.SBP_blackOrWhiteList == 2) {
				if (placedItem != null) {
					if (!BITConfig.whitelist.contains(String
							.valueOf(placedItem.getTypeId()))) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED
								+ BIT.li.getMessage("yourenotallowedtomovethis")
								+ BITBackpack.inventoryName + "!");
						return;
					}
				}
			}
		}
		// if (clickedSlotType == InventorySlotType.CONTAINER
		// && invName.equals(BITBackpack.inventoryName) && placedItem != null) {
		// ItemStack is = inv.getItem(slot);
		// is.setAmount(is.getAmount() - clickedItem.getAmount());
		// SpoutBackpack.updateInventory(player, inv.getContents());
		// }
		// } catch (NullPointerException e) {
		// }
	}
}