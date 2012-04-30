package com.almuramc.digilock.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.almuramc.digilock.Digilock;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.player.SpoutPlayer;

public class LockInventory {
	private Digilock plugin;
	protected SpoutBlock sBlock;
	protected String name;
	protected String owner;
	protected String coOwners;
	protected Inventory inventory;
	protected int useCost;
	public static Map<Integer, LockInventory> openedInventories = new HashMap<Integer, LockInventory>();
	// USERDATA FOR THE PINCODEPOPUP
	public static Map<Integer, Integer> userno = new HashMap<Integer, Integer>();
	public static Map<Integer, PopupScreen> popupScreen = new HashMap<Integer, PopupScreen>();
	// Parameters for getPincode & setPincode
	public static Map<Integer, GenericTextField> ownerGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> coOwnersGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> useCostGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, SpoutBlock> clickedBlock = new HashMap<Integer, SpoutBlock>();

	public LockInventory(Digilock plugin) {
		this.plugin = plugin;
	}

	/**
	 * Constructs a new BITInventory
	 */
	LockInventory(SpoutBlock sBlock, String owner, String name, String coowners,
				  Inventory inventory, int useCost) {
		this.sBlock = sBlock;
		this.name = name;
		this.owner = owner;
		this.coOwners = coowners;
		this.inventory = inventory;
		this.useCost = useCost;
	}

	public void setInventory(SpoutBlock sBlock, String owner, String name,
							 String coOwners, Inventory inventory, int useCost) {
		this.sBlock = sBlock;
		this.owner = owner;
		this.name = name;
		this.coOwners = coOwners;
		this.inventory = inventory;
		this.useCost = useCost;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public String getOwner() {
		return this.owner;
	}

	public String getCoOwners() {
		return this.coOwners;
	}

	public int getUseCost() {
		return this.useCost;
	}

	public SpoutBlock getBlock() {
		return this.sBlock;
	}

	public void openBitInventory(SpoutPlayer sPlayer, LockInventory inventory) {
		int id = sPlayer.getEntityId();
		openedInventories.put(id, inventory);
		sPlayer.openInventoryWindow(inventory.getInventory());
	}

	public void closeBitInventory(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		LockInventory bitInventory = openedInventories.get(id);
		saveBitInventory(sPlayer, bitInventory);
		openedInventories.remove(id);
	}

	public static void saveBitInventory(SpoutPlayer sPlayer, LockInventory inv) {
		if (inv != null) {
			saveBitInventory(sPlayer, inv.getBlock(), inv.getOwner(),
					inv.getName(), inv.getCoOwners(), inv.getInventory(),
					inv.getUseCost());
		}
	}

	public static void saveBitInventory(SpoutPlayer sPlayer, SpoutBlock block,
										String owner, String name, String coowners, Inventory inventory,
										int useCost) {
		String query;
		if (isBitInventoryCreated(block)) {
			for (int i = 0; i < inventory.getSize(); i++) {
				int itemid = inventory.getItem(i) != null ? 0 : inventory.getItem(i).getTypeId();
				int itemamount = inventory.getItem(i) != null ? 0 : inventory.getItem(i).getAmount();
				int itemdurability = inventory.getItem(i) != null ? 0 : inventory.getItem(i).getDurability();

				query = "UPDATE " + Digilock.getHandler().getTableName() + " SET owner='"
						+ owner + "', coowners='" + coowners + "', usecost="
						+ useCost + ", slotNo=" + i + ", itemstack_type="
						+ itemid
						+ ", itemstack_amount="
						+ itemamount
						+ ", itemstack_durability="
						+ itemdurability + " WHERE x = "
						+ block.getX() + " AND y = " + block.getY()
						+ " AND z = " + block.getZ() + " AND world='"
						+ block.getWorld().getName() + "' AND slotno=" + i
						+ ";";
				if (Digilock.getConf().getSQLType().equals("MYSQL")) {
					Digilock.getHandler().getMySQLHandler().query(query);
				} else {
					Digilock.getHandler().getSqliteHandler().query(query);
				}
			}
			Messages.sendNotification(sPlayer, "Bookshelf updated.");
		}
	}

	public static Boolean isBitInventoryCreated(SpoutBlock block) {
		String query = "SELECT * FROM " + Digilock.getHandler().getTableName()
				+ " WHERE (x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + " AND world='"
				+ block.getWorld().getName() + "');";
		ResultSet result = null;
		if (Digilock.getConf().getSQLType().equals("MYSQL")) {
			result = Digilock.getHandler().getMySQLHandler().query(query);
		} else { // SQLLITE
			result = Digilock.getHandler().getSqliteHandler().query(query);
		}
		try {
			if (result != null && result.next()) {
				result.close();
				return true;
				
			} else {
				result.close();
				return false;			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static LockInventory loadBitInventory(SpoutPlayer sPlayer,
												 SpoutBlock sBlock) {
		int size = loadBitInventorySize(sBlock);
		String name = "Bookshelf";
		Inventory inventory = SpoutManager.getInventoryBuilder().construct(
				size, name);
		String owner = sPlayer.getName();
		String coOwners = "";
		int useCost = 0;
		String query = "SELECT * FROM " + Digilock.getHandler().getTableName()
				+ " WHERE (x = " + sBlock.getX() + " AND y = " + sBlock.getY()
				+ " AND z = " + sBlock.getZ() + " AND world='"
				+ sBlock.getWorld().getName() + "');";
		// sPlayer.sendMessage("select:" + query);
		ResultSet result = null;
		if (Digilock.getConf().getSQLType().equals("MYSQL")) {
			result = Digilock.getHandler().getMySQLHandler().query(query);
		} else { // SQLLITE
			result = Digilock.getHandler().getSqliteHandler().query(query);
		}
		int i = 0;
		ItemStack itemstack;
		int itemstack_typeId;
		int itemstack_amount;
		short itemstack_durability;
		try {
			while (result != null && result.next() && i < size) {
				itemstack_typeId = result.getInt("itemstack_type");
				itemstack_amount = result.getInt("itemstack_amount");
				itemstack_durability = (short) result
						.getInt("itemstack_durability");
				if (itemstack_amount == 0) {
					inventory.clear(i);
				} else {

					itemstack = new ItemStack(itemstack_typeId,
							itemstack_amount, itemstack_durability);
					inventory.setItem(i, itemstack);
					inventory.setItem(i, itemstack);
				}
				name = result.getString("name");
				owner = result.getString("owner");
				coOwners = result.getString("coowners");
				useCost = result.getInt("usecost");
				i++;
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		LockInventory inv = new LockInventory(sBlock, owner, name, coOwners,
				inventory, useCost);

		return inv;
	}

	public static String loadBitInventoryName(SpoutPlayer sPlayer,
											  SpoutBlock block) {
		String name;
		String query = "SELECT * FROM " + Digilock.getHandler().getTableName()
				+ " WHERE (x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + " AND world='"
				+ block.getWorld().getName() + "');";
		ResultSet result = null;
		if (Digilock.getConf().getSQLType().equals("MYSQL")) {
			result = Digilock.getHandler().getMySQLHandler().query(query);
		} else { // SQLLITE
			result = Digilock.getHandler().getSqliteHandler().query(query);
		}
		try {
			if (result != null && result.next()) {
				name = result.getString("name");
				result.close();
				return name;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int loadBitInventorySize(SpoutBlock block) {
		int i = 0;
		String query = "SELECT * FROM " + Digilock.getHandler().getTableName()
				+ " WHERE (x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + " AND world='"
				+ block.getWorld().getName() + "');";
		ResultSet result = null;
		if (Digilock.getConf().getSQLType().equals("MYSQL")) {
			result = Digilock.getHandler().getMySQLHandler().query(query);
		} else { // SQLLITE
			result = Digilock.getHandler().getSqliteHandler().query(query);
		}
		try {

			while (result != null && result.next()) {
				i++;
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ((((i - 1) / 9) * 9) + 9);
	}

	public void RemoveBitInventory(SpoutPlayer sPlayer, int destroycost) {
		boolean deleteInventory = true;
		if (Digilock.getConf().useEconomy()) {
			if (Digilock.getHooks().getEconHook().hasAccount(sPlayer.getName())) {
				if (Digilock.getHooks().getEconHook().has(sPlayer.getName(), destroycost) || destroycost < 0) {
					Digilock.getHooks().getEconHook().withdrawPlayer(sPlayer.getName(), destroycost);
					sPlayer.sendMessage("Your account (" + Digilock.getHooks().getEconHook().getBalance(sPlayer.getName()) + ") has been deducted " + Digilock.getHooks().getEconHook().format(destroycost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money (" + Digilock.getHooks().getEconHook().getBalance(sPlayer.getName()) + "). Cost is:" + Digilock.getHooks().getEconHook().format(destroycost));
					deleteInventory = false;
				}
			}
		}
		String query = "DELETE FROM " + Digilock.getHandler().getTableName() + " WHERE (x = "
				+ sBlock.getX() + " AND y = " + sBlock.getY() + " AND z = "
				+ sBlock.getZ() + " AND world='" + sBlock.getWorld().getName()
				+ "');";
		if (deleteInventory) {
			if (Digilock.getConf().getSQLType().equals("MYSQL")) {
				Digilock.getHandler().getMySQLHandler().query(query);
			} else { // SQLLITE
				Digilock.getHandler().getSqliteHandler().query(query);
			}
			Messages.sendNotification(sPlayer, "Bookshelf removed.");
		} else {
			Messages.sendNotification(sPlayer, "You need more money ("
					+ destroycost + ")");
		}
	}

	public boolean isCoowner(SpoutPlayer sPlayer) {
		if (coOwners.toLowerCase().contains(sPlayer.getName().toLowerCase())
				|| coOwners.toLowerCase().contains("everyone")) {
			return true;
		}
		return false;
	}

	public boolean isOwner(SpoutPlayer sPlayer) {
		if (owner.toLowerCase().equals(sPlayer.getName().toLowerCase())) {
			return true;
		}
		return false;
	}

	public void addCoowner(String name) {
		this.coOwners = coOwners.concat("," + name);
	}

	public boolean removeCoowner(String name) {
		if (coOwners.toLowerCase().contains(name.toLowerCase())) {
			this.coOwners = coOwners.replace(name, "");
			this.coOwners = coOwners.replace(",,", ",");
			return true;
		}
		return false;
	}

	public void setUseCost(int useCost) {
		this.useCost = useCost;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return inventory.getSize();
	}

	// Updated to reflect new Images.
	private static String getTextureUrl(SpoutBlock sBlock) {
		switch (sBlock.getTypeId()) {
			case 23:
				return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Dispenser.png";
			// Dispenser - looks nice.
			case 47:
				return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Bookshelf.png";
			// Bookshelf - looks nice.
			case 54:
				return "http://minefiles.mcsnetworks.com/images/lockedbox.png";
			// Chest - looks nice.
			case 61:
				return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Furnace.png";
			// Furnace - looks nice.
			case 62:
				return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Furnace_%28Active%29.png";
			// Burning Furnace
			case 64:
				// return 324; // Wooden door
				return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Wooden_Door.png";
			case 69:
				// return 69; // Lever
				return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Lever.png";
			case 71:
				// return 330; // Iron door
				return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Iron_Door.png";
			case 77:
				// return 77; // Stone button
				return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Stone_Button.png";
			case 96:
				return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Trapdoor.png";
			// Trap_door
		}
		return "http://minefiles.mcsnetworks.com/images/lockedbox.png";
	}

	public static void removeUserData(int id) {
		if (userno.containsKey(id)) {
			userno.remove(id);
			popupScreen.remove(id);
			ownerGUI.remove(id);
			coOwnersGUI.remove(id);
			useCostGUI.remove(id);
		}
	}

	public static void addUserData(int id) {
		if (!userno.containsKey(id)) {
			// BITInventory
			userno.put(id, new Integer(id));
			popupScreen.put(id, new GenericPopup());
			ownerGUI.put(id, new GenericTextField());
			coOwnersGUI.put(id, new GenericTextField());
			useCostGUI.put(id, new GenericTextField());
			clickedBlock.put(id, null);
		}
	}

	public static void cleanupPopupScreen(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		if (popupScreen.containsKey(id)) {
			popupScreen.get(id).removeWidgets(Digilock.getInstance());
			popupScreen.get(id).setDirty(true);
			sPlayer.getMainScreen().removeWidgets(Digilock.getInstance());
			clickedBlock.remove(id);
		}
	}
}