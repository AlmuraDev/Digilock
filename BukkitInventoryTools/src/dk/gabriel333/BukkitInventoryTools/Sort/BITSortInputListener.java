package dk.gabriel333.BukkitInventoryTools.Sort;

import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.event.input.RenderDistanceChangeEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BITBackpack.BITBackpack;
import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.DigiLock.BITDigiLock;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.*;

public class BITSortInputListener extends InputListener {

	public BIT plugin;

	public BITSortInputListener(BIT plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		String keypressed = event.getKey().name();
		if (!(keypressed.equals(BITConfig.LIBRARY_SORTKEY) || keypressed
				.equals("KEY_ESCAPE")))
			return;
		SpoutPlayer sPlayer = event.getPlayer();
		ScreenType screentype = event.getScreenType();
		SpoutBlock targetblock = (SpoutBlock) sPlayer.getTargetBlock(null, 4);

		// Internal SpoutBacpack
		if (keypressed.equals(BITConfig.LIBRARY_SORTKEY)
				&& BITPermissions.hasPerm(sPlayer, "SortInventory.use",
						BITPermissions.QUIET)
				&& BITBackpack.isOpenBackpack(sPlayer)) {
			Inventory inventory = BITBackpack.getOpenedBackpack(sPlayer);
			if (inventory != null) {
				BITSortInventory.sortInventoryItems(sPlayer, inventory);
			}
			BITSortInventory.sortPlayerInventoryItems(sPlayer);
		}

		// PLAYER_INVENTORY
		if (keypressed.equals(BITConfig.LIBRARY_SORTKEY)
				&& BITPermissions.hasPerm(sPlayer, "SortInventory.use",
						BITPermissions.QUIET)
				&& screentype == ScreenType.PLAYER_INVENTORY) {
			BITSortInventory.sortPlayerInventoryItems(sPlayer);
			if (BITConfig.SORT_DISPLAYSORTARCHIEVEMENT) {
				BITMessages.sendNotification(sPlayer, "Items sorted.");
			}
		}

		// WORKBENCH - CRAFTING_INVENTORY SCREEN
		else if (keypressed.equals(BITConfig.LIBRARY_SORTKEY)
				&& BITPermissions.hasPerm(sPlayer, "SortInventory.use",
						BITPermissions.QUIET)
				&& screentype == ScreenType.WORKBENCH_INVENTORY) {
			BITSortInventory.sortPlayerInventoryItems(sPlayer);
			if (BITConfig.SORT_DISPLAYSORTARCHIEVEMENT) {
				BITMessages.sendNotification(sPlayer, "Items sorted.");
			}
		}

		// CHEST_INVENTORY SCREEN
		else if (screentype == ScreenType.CHEST_INVENTORY) {

			// CHEST or DOUBLECHEST
			if (keypressed.equals(BITConfig.LIBRARY_SORTKEY)
					&& BITPermissions.hasPerm(sPlayer, "SortInventory.use",
							BITPermissions.QUIET)
					&& BITDigiLock.isChest(targetblock)) {
				SpoutChest sChest = (SpoutChest) targetblock.getState();
				if (targetblock.getType() == Material.CHEST) {
					BITSortInventory.sortInventoryItems(sPlayer,
							sChest.getLargestInventory());
					BITSortInventory.sortPlayerInventoryItems(sPlayer);
					if (BITConfig.SORT_DISPLAYSORTARCHIEVEMENT) {
						BITMessages.sendNotification(sPlayer, "Chest sorted.");
					}
				}

			} else if (BITDigiLock.isBookshelf(targetblock)) {

				// BOOKSHELF INVENTORY
				if (keypressed.equals(BITConfig.LIBRARY_SORTKEY)
						&& BITPermissions.hasPerm(sPlayer, "SortInventory.use",
								BITPermissions.QUIET)) {
					BITSortInventory.sortPlayerInventoryItems(sPlayer);
					int id = sPlayer.getEntityId();
					BITInventory bitInventory = BITInventory.openedInventories
							.get(id);
					if (bitInventory != null) {
						Inventory inventory = bitInventory.getInventory();
						BITSortInventory.sortInventoryItems(sPlayer, inventory);
						bitInventory.setInventory(targetblock,
								bitInventory.getOwner(),
								bitInventory.getName(),
								bitInventory.getCoOwners(), inventory,
								bitInventory.getUseCost());
						BITInventory.saveBitInventory(sPlayer, bitInventory);
						BITInventory.openedInventories.remove(id);
						BITInventory.openedInventories.put(id, bitInventory);
						if (BITConfig.SORT_DISPLAYSORTARCHIEVEMENT) {
							BITMessages.sendNotification(sPlayer,
									"Bookshelf sorted.");
						}
					}
				} else if (keypressed.equals("KEY_ESCAPE")) {
					int id = sPlayer.getEntityId();
					BITInventory bitInventory = BITInventory.openedInventories
							.get(id);
					BITInventory.saveBitInventory(sPlayer, bitInventory);
					BITInventory.openedInventories.remove(id);
				}
			}
		}

		// FURNACE_INVENTORY SCREEN
		else if (keypressed.equals(BITConfig.LIBRARY_SORTKEY)
				&& BITPermissions.hasPerm(sPlayer, "SortInventory.use",
						BITPermissions.QUIET)
				&& screentype == ScreenType.FURNACE_INVENTORY) {
			BITSortInventory.sortPlayerInventoryItems(sPlayer);
			if (BITConfig.SORT_DISPLAYSORTARCHIEVEMENT) {
				BITMessages.sendNotification(sPlayer, "Inventory sorted.");
			}
		}

		// DISPENCER_INVENTORY SCREEN
		else if (keypressed.equals(BITConfig.LIBRARY_SORTKEY)
				&& BITPermissions.hasPerm(sPlayer, "SortInventory.use",
						BITPermissions.QUIET)
				&& screentype == ScreenType.DISPENSER_INVENTORY) {
			if (targetblock.getType() == Material.DISPENSER) {
				Dispenser dispenser = (Dispenser) targetblock.getState();
				Inventory inventory = dispenser.getInventory();
				BITSortInventory.sortInventoryItems(sPlayer, inventory);
				BITSortInventory.sortPlayerInventoryItems(sPlayer);
				if (BITConfig.SORT_DISPLAYSORTARCHIEVEMENT) {
					BITMessages.sendNotification(sPlayer, "Dispenser sorted.");
				}
			}
		}
	}

	@Override
	public void onKeyReleasedEvent(KeyReleasedEvent event) {

	}

	@Override
	public void onRenderDistanceChange(RenderDistanceChangeEvent event) {

	}

}
