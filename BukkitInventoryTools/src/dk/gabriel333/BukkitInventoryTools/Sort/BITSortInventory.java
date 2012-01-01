package dk.gabriel333.BukkitInventoryTools.Sort;

import org.bukkit.Material;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.getspout.spout.inventory.CustomMCInventory;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import de.Keyle.MyWolf.MyWolfPlugin;
import dk.gabriel333.BITBackpack.BITBackpackAPI;
import dk.gabriel333.BITBackpack.BITBackpack;
import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;
import dk.gabriel333.Library.BITPermissions;

public class BITSortInventory {

	// TODO: code should be moved to BITInventory
	public static void sortInventoryItems(SpoutPlayer sPlayer,
			Inventory inventory) {
		stackInventoryItems(sPlayer, inventory);
		orderInventoryItems(inventory, 0);
	}

	public static void sortPlayerInventoryItems(SpoutPlayer sPlayer) {
		Inventory inventory = sPlayer.getInventory();
		int i, j;
		for (i = 0; i < inventory.getSize(); i++) {
			ItemStack item1 = inventory.getItem(i);
			if ((item1.getAmount() == 64)
			// Food must be alone in slot 0-8 so you can eat it.
					|| (i < 9 && (item1.getAmount() == 0 || isTool(item1)
							|| isWeapon(item1) || isArmor(item1)
							|| isFood(item1) || isBucket(item1) || isVehicle(item1)))) {
				continue;
			} else {
				for (j = i + 1; j < inventory.getSize(); j++) {
					moveitemInventory(sPlayer, inventory, j, i);
				}
			}
		}
		orderInventoryItems(inventory, 9);
	}

	/**
	 * Method to sort the players inventory, his backpack or wolfs pack
	 * 
	 * @param sPlayer
	 * @param screentype
	 */
	public static void sortinventory(SpoutPlayer sPlayer, ScreenType screentype) {
		// sort the ordinary player inventory
		BITSortInventory.sortPlayerInventoryItems(sPlayer);

		if (BITBackpackAPI.bitBackpackEnabled) {
			Inventory inv = SpoutManager.getInventoryBuilder()
					.construct(
							BITBackpack.allowedSize(sPlayer.getWorld(),
									sPlayer, true), BITBackpack.inventoryName);
			inv = BITBackpack.getClosedBackpack(sPlayer);
			BITSortInventory.sortInventoryItems(sPlayer, inv);
			BITBackpack.inventories.put(sPlayer.getName(), inv.getContents());
		}

		// sort the players MyWolfInventory if exists and if is open.
		if (BIT.mywolf) {
			// if the wolf inventory is open then {

			CustomMCInventory inv = MyWolfPlugin.getMyWolf(sPlayer).inv;

			if (inv != null) {
				// test if myWolfInventory is opened and open it
				// this on fails... can not be cast to ... Inventory

				// BITSortInventory.sortInventoryItems(sPlayer, (Inventory)
				// inv);

			}
		}
	}

	private static void stackInventoryItems(SpoutPlayer sPlayer,
			Inventory inventory) {
		int i, j;
		for (i = 0; i < inventory.getSize(); i++) {
			for (j = i + 1; j < inventory.getSize(); j++) {
				moveitemInventory(sPlayer, inventory, j, i);
			}
		}
	}

	private static void moveitemInventory(SpoutPlayer p, Inventory inventory,
			int fromslot, int toslot) {
		int from_amt, to_amt, total_amt;
		ItemStack fromitem, toitem;
		fromitem = inventory.getItem(fromslot);
		toitem = inventory.getItem(toslot);
		from_amt = fromitem.getAmount();
		to_amt = toitem.getAmount();
		total_amt = from_amt + to_amt;
		if ((from_amt == 0 && to_amt == 0) || from_amt == 0) {
			// Dont do anything
			return;
		} else if (to_amt == 0 && from_amt > 0) {
			to_amt = total_amt;
			from_amt = 0;
			if (BITConfig.DEBUG_SORTINVENTORY) {
				BITMessages.showInfo("1) (from,to)=(" + fromslot + ">"
						+ toslot + ") To_amt=" + to_amt + " from_amt="
						+ from_amt + " total_amt=" + total_amt);
			}
			inventory.setItem(toslot, fromitem);
			inventory.getItem(toslot).setAmount(to_amt);
			inventory.clear(fromslot);
			return;
		} else {
			// Here is to_amt > and from_amt>0 so move all what's possible if
			// it is the same kind of item.
			if (BITPermissions.hasPerm(p, "sortinventory.stack.*",
					BITPermissions.QUIET)) {
				// okay...
				if (BITConfig.DEBUG_PERMISSIONS)
					p.sendMessage("You have permission to stack all items!");
			} else if ((isTool(fromitem) && !BITPermissions.hasPerm(p,
					"sortinventory.stack.tools", BITPermissions.QUIET))
					|| (isWeapon(fromitem) && !BITPermissions.hasPerm(p,
							"sortinventory.stack.weapons",
							BITPermissions.QUIET))
					|| (isBucket(fromitem) && !BITPermissions.hasPerm(p,
							"sortinventory.stack.buckets",
							BITPermissions.QUIET))
					|| (isArmor(fromitem) && !BITPermissions.hasPerm(p,
							"sortinventory.stack.armor", BITPermissions.QUIET))
					|| (isFood(fromitem) && !BITPermissions.hasPerm(p,
							"sortinventory.stack.food", BITPermissions.QUIET))
					|| (isVehicle(fromitem) && !BITPermissions.hasPerm(p,
							"sortinventory.stack.vehicles",
							BITPermissions.QUIET))) {
				if (BITConfig.DEBUG_PERMISSIONS)
					p.sendMessage("You dont have permission to stack:"
							+ fromitem.getType());
				return;
			}
			if (fromitem.getTypeId() == toitem.getTypeId()
					&& fromitem.getDurability() == toitem.getDurability()) {
				if (fromitem.getData() != null && toitem.getData() != null) {
					if (!fromitem.getData().equals(toitem.getData())) {
						// DONT MOVE ANYTHING
						// G333Messages.showInfo("DONT do anything");
						return;
					}
				}
				if (total_amt > 64) {
					to_amt = 64;
					from_amt = total_amt - 64;
					if (BITConfig.DEBUG_SORTINVENTORY) {
						BITMessages.showInfo("4) To_amt=" + to_amt
								+ " from_amt=" + from_amt + " total_amt="
								+ total_amt);
					}
					fromitem.setAmount(from_amt);
					toitem.setAmount(to_amt);
					return;
				} else {
					// total_amt is <= 64 so everything goes to toslot
					if (BITConfig.DEBUG_SORTINVENTORY) {
						BITMessages.showInfo("5) To_amt=" + to_amt
								+ " from_amt=" + from_amt + " total_amt="
								+ total_amt);
					}
					inventory.setItem(toslot, fromitem);
					inventory.getItem(toslot).setAmount(total_amt);
					inventory.clear(fromslot);
					return;
				}
			}
		}
	}

	public static void orderInventoryItems(Inventory inventory, int startslot) {
		int n = startslot;
		for (int m = 0; m < BITConfig.SORTSEQ.length; m++) {
			Material mat = Material.matchMaterial(BITConfig.SORTSEQ[m]);
			if (mat == null) {
				BITMessages.showError("Configuration error i config.yml.");
				BITMessages.showError(" Unknown material in SORTSEQ:"
						+ BITConfig.SORTSEQ[m]);
			} else if (inventory.contains(mat)) {
				for (int i = n; i < inventory.getSize(); i++) {
					if (inventory.getItem(i).getType() == mat) {
						n++;
						continue;
					} else {
						for (int j = i + 1; j < inventory.getSize(); j++) {
							if (inventory.getItem(j).getType() == mat) {
								switchInventoryItems(inventory, i, j);
								n++;
								break;
							}
						}
					}
				}

			}
		}
	}

	private static void switchInventoryItems(Inventory inventory, int slot1,
			int slot2) {
		ItemStack item = inventory.getItem(slot1);
		inventory.setItem(slot1, inventory.getItem(slot2));
		inventory.setItem(slot2, item);
	}

	// ********************************************************************
	// ********************************************************************
	// *****************INVENTORY TOOLS************************************
	// ********************************************************************
	// ********************************************************************

	// Check if it is a tool
	public static boolean isTool(ItemStack item) {
		for (String i : BITConfig.tools) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

	// Check if it is a weapon
	public static boolean isWeapon(ItemStack item) {
		for (String i : BITConfig.weapons) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

	// Check if it is a armor
	public static boolean isArmor(ItemStack item) {
		for (String i : BITConfig.armors) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

	// Check if it is food
	public static boolean isFood(ItemStack item) {
		for (String i : BITConfig.foods) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

	// Check if it is a vehicles
	public static boolean isVehicle(ItemStack item) {
		for (String i : BITConfig.vehicles) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

	// Check if it is buckets
	public static boolean isBucket(ItemStack item) {
		for (String i : BITConfig.buckets) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

}
