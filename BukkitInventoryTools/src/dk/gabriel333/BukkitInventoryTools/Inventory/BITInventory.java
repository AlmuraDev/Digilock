package dk.gabriel333.BukkitInventoryTools.Inventory;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.material.Item;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.Book.BITBook;
import dk.gabriel333.BukkitInventoryTools.DigiLock.BITDigiLock;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;

public class BITInventory {

	@SuppressWarnings("unused")
	private BIT plugin;

	public BITInventory(BIT plugin) {
		this.plugin = plugin;
	}

	protected SpoutBlock sBlock;
	protected String name;
	protected String owner;
	protected String coOwners;
	protected Inventory inventory;
	protected int useCost;

	/**
	 * Constructs a new BITInventory
	 * 
	 */
	BITInventory(SpoutBlock sBlock, String owner, String name, String coowners,
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

	public static Map<Integer, BITInventory> openedInventories = new HashMap<Integer, BITInventory>();

	// USERDATA FOR THE PINCODEPOPUP
	public static Map<Integer, Integer> userno = new HashMap<Integer, Integer>();
	public static Map<Integer, PopupScreen> popupScreen = new HashMap<Integer, PopupScreen>();
	// Parameters for getPincode & setPincode
	public static Map<Integer, GenericTextField> ownerGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> coOwnersGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> useCostGUI = new HashMap<Integer, GenericTextField>();

	public static HashMap<UUID, String> BITBookButtons = new HashMap<UUID, String>();
	public static Map<Integer, SpoutBlock> clickedBlock = new HashMap<Integer, SpoutBlock>();

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

	public void openBitInventory(SpoutPlayer sPlayer, BITInventory bitInventory) {
		int id = sPlayer.getEntityId();
		openedInventories.put(id, bitInventory);
		sPlayer.openInventoryWindow(bitInventory.getInventory());
	}

	public void closeBitInventory(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		BITInventory bitInventory = openedInventories.get(id);
		saveBitInventory(sPlayer, bitInventory);
		openedInventories.remove(id);
	}

	public static void saveBitInventory(SpoutPlayer sPlayer, BITInventory inv) {
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
		boolean createBookshelf = true;
		int cost = BITConfig.BOOKSHELF_COST;
		if (isBitInventoryCreated(block)) {
			for (int i = 0; i < inventory.getSize(); i++) {
				query = "UPDATE " + BIT.bitInventoryTable + " SET owner='"
						+ owner + "', coowners='" + coowners + "', usecost="
						+ useCost + ", slotNo=" + i + ", itemstack_type="
						+ inventory.getItem(i).getTypeId()
						+ ", itemstack_amount="
						+ inventory.getItem(i).getAmount()
						+ ", itemstack_durability="
						+ inventory.getItem(i).getDurability() + " WHERE x = "
						+ block.getX() + " AND y = " + block.getY()
						+ " AND z = " + block.getZ() + " AND world='"
						+ block.getWorld().getName() + "' AND slotno=" + i
						+ ";";
				if (BITConfig.DEBUG_SQL)
					sPlayer.sendMessage(ChatColor.YELLOW
							+ "Updating Bookshelf: " + query);
				if (BITConfig.STORAGE_TYPE.equals("MYSQL")) {
					try {
						BIT.manageMySQL.insertQuery(query);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					BIT.manageSQLite.insertQuery(query);
				}
			}
			BITMessages.sendNotification(sPlayer, "Bookshelf updated.");
		} else {
			// NEW BOOKSHELF
			if (BIT.useEconomy) {
				if (BIT.plugin.Method.hasAccount(sPlayer.getName()) && cost > 0) {
					if (BIT.plugin.Method.getAccount(sPlayer.getName())
							.hasEnough(cost)) {
						BIT.plugin.Method.getAccount(sPlayer.getName())
								.subtract(cost);
						sPlayer.sendMessage("Your account ("
								+ BIT.plugin.Method.getAccount(
										sPlayer.getName()).balance()
								+ ") has been deducted "
								+ BIT.plugin.Method.format(cost) + ".");
					} else {
						sPlayer.sendMessage("You dont have enough money ("
								+ BIT.plugin.Method.getAccount(
										sPlayer.getName()).balance()
								+ "). Cost is:"
								+ BIT.plugin.Method.format(cost));
						createBookshelf = false;
					}
				}
			}
			if (createBookshelf) {
				for (int i = 0; i < inventory.getSize(); i++) {
					query = "INSERT INTO "
							+ BIT.bitInventoryTable
							+ " (x, y, z, world, owner, name, coowners, usecost, "
							+ "slotno, itemstack_type, itemstack_amount, itemstack_durability "
							+ ") VALUES (" + block.getX() + ", " + block.getY()
							+ ", " + block.getZ() + ", '"
							+ block.getWorld().getName() + "', '" + owner
							+ "', '" + name + "', '" + coowners + "', "
							+ useCost + ", " + i + ", "
							+ inventory.getItem(i).getTypeId() + ","
							+ inventory.getItem(i).getAmount() + ","
							+ inventory.getItem(i).getDurability() + " );";
					// sPlayer.sendMessage("Insert:" + query);
					if (BITConfig.DEBUG_SQL)
						sPlayer.sendMessage(ChatColor.YELLOW
								+ "Insert to bookshelf: " + query);
					if (BITConfig.STORAGE_TYPE.equals("MYSQL")) {
						try {
							BIT.manageMySQL.insertQuery(query);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					} else {
						BIT.manageSQLite.insertQuery(query);
					}
				}
				BITMessages.sendNotification(sPlayer, "Bookshelf created.");
			} else {
				sPlayer.sendMessage("You dont have enough money. Cost is:"
						+ cost);
			}
		}
	}

	public static Boolean isBitInventoryCreated(SpoutBlock block) {
		String query = "SELECT * FROM " + BIT.bitInventoryTable
				+ " WHERE (x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + " AND world='"
				+ block.getWorld().getName() + "');";
		ResultSet result = null;
		if (BITConfig.STORAGE_TYPE.equals("MYSQL")) {
			try {
				result = BIT.manageMySQL.sqlQuery(query);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else { // SQLLITE
			result = BIT.manageSQLite.sqlQuery(query);
		}
		try {
			if (result != null && result.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static BITInventory loadBitInventory(SpoutPlayer sPlayer,
			SpoutBlock sBlock) {
		int size = loadBitInventorySize(sBlock);
		String name = "Bookshelf";
		Inventory inventory = SpoutManager.getInventoryBuilder().construct(
				size, name);
		String owner = sPlayer.getName();
		String coOwners = "";
		int useCost = 0;
		String query = "SELECT * FROM " + BIT.bitInventoryTable
				+ " WHERE (x = " + sBlock.getX() + " AND y = " + sBlock.getY()
				+ " AND z = " + sBlock.getZ() + " AND world='"
				+ sBlock.getWorld().getName() + "');";
		// sPlayer.sendMessage("select:" + query);
		ResultSet result = null;
		if (BITConfig.STORAGE_TYPE.equals("MYSQL")) {
			try {
				result = BIT.manageMySQL.sqlQuery(query);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else { // SQLLITE
			result = BIT.manageSQLite.sqlQuery(query);
			// sPlayer.sendMessage("Result:" + result.toString());
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
		} catch (SQLException e) {
			e.printStackTrace();
		}

		short bookId = 0;
		if (inventory.contains(Material.BOOK)) {
			BITBook bitBook = new BITBook();
			for (int j = 0; j < inventory.getSize(); j++) {
				if (inventory.getItem(j).getType() == Material.BOOK) {
					bookId = inventory.getItem(j).getDurability();
					if (bookId > 1000) {
						bitBook = BITBook.loadBook(sPlayer, bookId);
						Item item = (Item) inventory.getItem(j);
						item.setName(bitBook.getTitle()+" written by "+bitBook.getAuthor());
						//BITBook.setBookName(bookId, bitBook.getTitle(),
						//		bitBook.getAuthor());
					}
				}
			}
		}
		BITInventory inv = new BITInventory(sBlock, owner, name, coOwners,
				inventory, useCost);

		return inv;
	}

	public static String loadBitInventoryName(SpoutPlayer sPlayer,
			SpoutBlock block) {
		String name = "";
		String query = "SELECT * FROM " + BIT.bitInventoryTable
				+ " WHERE (x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + " AND world='"
				+ block.getWorld().getName() + "');";
		ResultSet result = null;
		if (BITConfig.STORAGE_TYPE.equals("MYSQL")) {
			try {
				result = BIT.manageMySQL.sqlQuery(query);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else { // SQLLITE
			result = BIT.manageSQLite.sqlQuery(query);
		}
		try {
			if (result != null && result.next()) {
				name = result.getString("name");
				return name;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int loadBitInventorySize(SpoutBlock block) {
		int i = 0;
		String query = "SELECT * FROM " + BIT.bitInventoryTable
				+ " WHERE (x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + " AND world='"
				+ block.getWorld().getName() + "');";
		ResultSet result = null;
		if (BITConfig.STORAGE_TYPE.equals("MYSQL")) {
			try {
				result = BIT.manageMySQL.sqlQuery(query);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else { // SQLLITE
			result = BIT.manageSQLite.sqlQuery(query);
		}
		try {

			while (result != null && result.next()) {
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ((((i - 1) / 9) * 9) + 9);
	}

	public void RemoveBitInventory(SpoutPlayer sPlayer, int destroycost) {
		boolean deleteInventory = true;
		if (BIT.useEconomy) {
			if (BIT.plugin.Method.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.Method.getAccount(sPlayer.getName()).hasEnough(
						destroycost)
						|| destroycost < 0) {
					BIT.plugin.Method.getAccount(sPlayer.getName()).subtract(
							destroycost);
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + ") has been deducted "
							+ BIT.plugin.Method.format(destroycost) + ".");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + "). Cost is:"
							+ BIT.plugin.Method.format(destroycost));
					deleteInventory = false;
				}
			}
		}
		String query = "DELETE FROM " + BIT.bitInventoryTable + " WHERE (x = "
				+ sBlock.getX() + " AND y = " + sBlock.getY() + " AND z = "
				+ sBlock.getZ() + " AND world='" + sBlock.getWorld().getName()
				+ "');";
		if (deleteInventory) {
			if (BITConfig.DEBUG_SQL)
				sPlayer.sendMessage(ChatColor.YELLOW + "Removing Bookshelf: "
						+ query);
			if (BITConfig.STORAGE_TYPE.equals("MYSQL")) {
				try {
					BIT.manageMySQL.deleteQuery(query);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else { // SQLLITE
				BIT.manageSQLite.deleteQuery(query);
			}
			BITMessages.sendNotification(sPlayer, "Bookshelf removed.");
		} else {
			BITMessages.sendNotification(sPlayer, "You need more money ("
					+ destroycost + ")");
		}

	}

	public boolean isCoowner(SpoutPlayer sPlayer) {
		if (coOwners.toLowerCase().contains(sPlayer.getName().toLowerCase())
				|| coOwners.toLowerCase().contains("everyone"))
			return true;
		return false;
	}

	public boolean isOwner(SpoutPlayer sPlayer) {
		if (owner.toLowerCase().equals(sPlayer.getName().toLowerCase()))
			return true;
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

	public static void removeBookshelfAndDropItems(SpoutPlayer sPlayer,
			SpoutBlock sBlock) {
		World world = sBlock.getWorld();
		Location location = sBlock.getLocation();
		BITInventory bitInventory = BITInventory.loadBitInventory(sPlayer,
				sBlock);
		if (bitInventory != null) {
			for (int i = 0; i < bitInventory.getSize(); i++) {
				ItemStack itemstack = bitInventory.getInventory()
						.getItem(i);
				if (itemstack.getAmount() != 0) {
					world.dropItemNaturally(location, itemstack);
				}
			}
			bitInventory.RemoveBitInventory(sPlayer,
					BITConfig.BOOKSHELF_DESTROYCOST);
		}
		
		if (BITConfig.BOOKSHELF_RECOVER_ON_BREAK) {
			ItemStack item = new ItemStack(Material.BOOKSHELF, 1);
			world.dropItemNaturally(location, item);
		}
	}

	/**
	 * CreateBookshelf: Open GenericPopup and enter a ask if the Bookshelf
	 * inventory is going to be created - showing the price.
	 * 
	 * @param sPlayer
	 *            SpoutPlayer
	 * @param sBlock
	 *            SpoutBlock
	 */
	public static void setBookshelfInventory(SpoutPlayer sPlayer,
			SpoutBlock sBlock) {
		int id = sPlayer.getEntityId();
		int height = 20;
		int x, y, w1, w2, w3, w4;
		addUserData(id);
		clickedBlock.put(id, sBlock);
		if (BITInventory.isBitInventoryCreated(sBlock)) {
			BITDigiLock digilock = BITDigiLock.loadDigiLock(sBlock);
			ownerGUI.get(id).setText(digilock.getOwner());
			coOwnersGUI.get(id).setText(digilock.getCoOwners());
			useCostGUI.get(id).setText(Integer.toString(digilock.getUseCost()));
		} else {
			ownerGUI.get(id).setText(sPlayer.getName());
			coOwnersGUI.get(id).setText("");
			useCostGUI.get(id).setText("0");
		}

		// GenericTexture
		GenericTexture genericTexture = new GenericTexture();
		genericTexture.setUrl(getTextureUrl(sBlock));
		genericTexture.setHeight(150).setWidth(150).setX(1).setY(1);
		genericTexture.setMaxHeight(40).setMaxWidth(40);
		popupScreen.get(id).attachWidget(BIT.plugin, genericTexture);

		// itemwidget
		x = 170;
		y = 50;
		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(
				BITDigiLock.getPincodeBlock(sBlock)));
		itemwidget.setX(x + 2 * height).setY(y);
		itemwidget.setHeight(height * 2).setWidth(height * 2)
				.setDepth(height * 2);
		popupScreen.get(id).attachWidget(BIT.plugin, itemwidget);
		y = y + 3 * height;

		// costToCreateLabel
		GenericLabel costToCreate = new GenericLabel("CostToCreate: "
				+ String.valueOf(BITConfig.BOOKSHELF_COST));
		costToCreate.setAuto(true).setX(175).setY(y).setHeight(10)
				.setWidth(140);
		costToCreate.setTooltip("The cost to create a new Bookshelf");
		popupScreen.get(id).attachWidget(BIT.plugin, costToCreate);

		// first row -------- x=20-170-------------------------------------
		x = 10;
		w1 = 60;
		w2 = 80;
		w3 = 50;
		w4 = 50;

		y = 170;
		// ownerButton
		GenericButton ownerButton = new GenericButton("Owner");
		ownerButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		ownerButton.setTooltip("Set Owner");
		popupScreen.get(id).attachWidget(BIT.plugin, ownerButton);
		BITBookButtons.put(ownerButton.getId(), "OwnerButton");
		// owner1
		ownerGUI.get(id).setTooltip("Owner of the Bookshelf");
		ownerGUI.get(id).setCursorPosition(1).setMaximumCharacters(20);
		ownerGUI.get(id).setX(x + w1 + 1).setY(y);
		ownerGUI.get(id).setHeight(height).setWidth(w2);
		popupScreen.get(id).attachWidget(BIT.plugin, ownerGUI.get(id));

		// useCostButton
		GenericButton useCostButton = new GenericButton("Use cost");
		useCostButton.setAuto(false).setX(x + w1 + w2 + 10 + w1 + w3 + 10)
				.setY(y).setHeight(height).setWidth(w1);
		useCostButton.setTooltip("Set cost");
		popupScreen.get(id).attachWidget(BIT.plugin, useCostButton);
		BITBookButtons.put(useCostButton.getId(), "UseCostButton");
		// useCost1
		useCostGUI.get(id).setTooltip("This is the cost to use the Bookshelf");
		useCostGUI.get(id).setCursorPosition(1).setMaximumCharacters(4);
		useCostGUI.get(id).setX(x + w1 + w2 + 10 + w1 + w3 + 10 + w1 + 1)
				.setY(y);
		useCostGUI.get(id).setHeight(height).setWidth(w4);
		popupScreen.get(id).attachWidget(BIT.plugin, useCostGUI.get(id));
		y = y + height + 1;

		// setCoOwnerButton
		GenericButton CoOwnerButton = new GenericButton("CoOwners");
		CoOwnerButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		CoOwnerButton.setTooltip("CoOwners must be seperated by a comma.");
		popupScreen.get(id).attachWidget(BIT.plugin, CoOwnerButton);
		BITBookButtons.put(CoOwnerButton.getId(), "CoOwnerButton");
		// listOfCoOwners
		coOwnersGUI.get(id).setX(x + w1 + 1).setY(y).setWidth(340)
				.setHeight(height);
		coOwnersGUI.get(id).setMaximumCharacters(200);
		coOwnersGUI.get(id).setText(coOwnersGUI.get(id).getText());
		popupScreen.get(id).attachWidget(BIT.plugin, coOwnersGUI.get(id));
		y = y + height;

		// Second row ------------X=170-270-370------------------------------
		y = 110;
		x = 180;
		w1 = 80;
		w2 = 80;
		y = y + height;

		// CreateBookshelfButton
		GenericButton CreateBookshelfButton = new GenericButton("Create");
		CreateBookshelfButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		CreateBookshelfButton
				.setTooltip("Press Create to create the Bookshelf.");
		popupScreen.get(id).attachWidget(BIT.plugin, CreateBookshelfButton);
		BITBookButtons.put(CreateBookshelfButton.getId(),
				"CreateBookshelfButton");

		// cancelButton
		GenericButton cancelButton2 = new GenericButton("Cancel");
		cancelButton2.setAuto(false).setX(x + w1 + 10).setY(y)
				.setHeight(height).setWidth(w1);
		popupScreen.get(id).attachWidget(BIT.plugin, cancelButton2);
		BITBookButtons.put(cancelButton2.getId(), "setPincodeCancel");

		// removeBookshelfButton
		if (BITInventory.isBitInventoryCreated(sBlock)) {
			GenericButton removeBookshelfButton = new GenericButton("Remove");
			removeBookshelfButton.setAuto(false).setX(x - w1 - 10).setY(y)
					.setHeight(height).setWidth(w1);
			removeBookshelfButton
					.setTooltip("Press Remove to remove the BookshelfInventory.");
			removeBookshelfButton.setEnabled(true);
			BITBookButtons.put(removeBookshelfButton.getId(),
					"removeBookshelfButton");
			popupScreen.get(id).attachWidget(BIT.plugin, removeBookshelfButton);
		}

		// Open Window
		// popupScreen.get(id).setDirty(true);
		popupScreen.get(id).setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(popupScreen.get(id));

	}

	private static String getTextureUrl(SpoutBlock sBlock) {
		switch (sBlock.getTypeId()) {
		case 23:
			return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Dispenser.png";
			// Dispenser - looks nice.
		case 47:
			return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Bookshelf.png";
			// Bookshelf - looks nice.
		case 54:
			return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Chest.png";
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
		return "http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Textures/Chest.png";
	}

	public static void removeUserData(int id) {
		if (userno.containsKey(id)) {
			// BITInventory
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
			popupScreen.get(id).removeWidgets(BIT.plugin);
			popupScreen.get(id).setDirty(true);
			sPlayer.getMainScreen().removeWidgets(BIT.plugin);
			clickedBlock.remove(id);
		}
	}

}
