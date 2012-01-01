package dk.gabriel333.BukkitInventoryTools.Sort;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.DigiLock.BITDigiLock;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.*;

public class BITCommandSort implements CommandExecutor {

	public BITCommandSort(BIT instance) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		// BITPlayer bPlayer = new BITPlayer(sPlayer);
		Block targetblock = sPlayer.getTargetBlock(null, 5);
		if (BIT.isPlayer(sender)) {
			if (BITPermissions.hasPerm(sender, "sortinventory.use",
					BITPermissions.NOT_QUIET)) {

				if (targetblock.getType() == Material.CHEST) {
					SpoutChest sChest = (SpoutChest) targetblock.getState();
					BITSortInventory.sortInventoryItems(sPlayer,
							sChest.getLargestInventory());
					BITMessages.sendNotification(sPlayer, "Chest sorted.");

				} else if (BITDigiLock.isDispenser((SpoutBlock) targetblock)) {
					Dispenser dispenser = (Dispenser) targetblock.getState();
					Inventory inventory = dispenser.getInventory();
					BITSortInventory.sortInventoryItems(sPlayer, inventory);
					BITSortInventory.sortPlayerInventoryItems(sPlayer);
					BITMessages.sendNotification(sPlayer, "Items sorted.");

				} else if (targetblock.getType() == Material.FURNACE) {
					BITSortInventory.sortPlayerInventoryItems(sPlayer);
					BITMessages.sendNotification(sPlayer, "Items sorted.");

				} else if (BITDigiLock.isBookshelf((SpoutBlock) targetblock)) {
					if (BITInventory
							.isBitInventoryCreated((SpoutBlock) targetblock)) {
						BITInventory bInv = BITInventory.loadBitInventory(
								sPlayer, (SpoutBlock) targetblock);
							Inventory inventory = bInv.getInventory();
							BITSortInventory.sortInventoryItems(sPlayer,
									inventory);
							BITSortInventory.sortPlayerInventoryItems(sPlayer);
							BITMessages.sendNotification(sPlayer,
									"Items sorted.");
					}
				} else {
					// player inventory
					BITSortInventory.sortinventory(sPlayer,
							ScreenType.CHAT_SCREEN);
					BITMessages.sendNotification(sPlayer, "Items sorted.");
				}
			}
			return true;
		} else {
			BITMessages.showWarning("You can't use /sort in the console.");
			return false;
		}
	}

}
