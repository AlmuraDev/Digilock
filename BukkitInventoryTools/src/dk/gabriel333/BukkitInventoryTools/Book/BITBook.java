package dk.gabriel333.BukkitInventoryTools.Book;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.material.Item;
import org.getspout.spoutapi.packet.PacketItemName;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;

public class BITBook {

	private BIT plugin;

	public BITBook() {
		super();
	}

	public BITBook(Plugin plugin) {
		plugin = this.plugin;
	}

	protected short bookId;
	protected String title;
	protected String author;
	protected String coAuthors;
	protected int numberOfPages;
	protected String[] bodytext;
	protected Boolean masterCopy;
	protected short masterCopyId;
	protected Boolean forceBookToPlayerInventory;
	protected Boolean canBeMovedFromInventory;
	protected Boolean copyTheBookWhenMoved;
	protected int useCost;
	
	final public static Boolean WRITEABLE=true;
	final public static Boolean READONLY=false;

	/**
	 * Contructs a new BITBook
	 * 
	 * @param bookId
	 * @param title
	 * @param author
	 * @param coAuthors
	 * @param numberOfPages
	 * @param bodytext
	 * @param masterCopy
	 * @param masterCopyId
	 * @param forceBookToPlayerInventory
	 * @param canBeMovedFromInventory
	 * @param copyTheBookWhenMoved
	 * @param useCost
	 */
	BITBook(short bookId, String title, String author, String coAuthors,
			int numberOfPages, String[] bodytext, Boolean masterCopy,
			short masterCopyId, Boolean forceBookToPlayerInventory,
			Boolean canBeMovedFromInventory, Boolean copyTheBookWhenMoved,
			int useCost) {
		this.bookId = bookId;
		this.title = title;
		this.author = author;
		this.coAuthors = coAuthors;
		this.numberOfPages = numberOfPages;
		this.bodytext = bodytext;
		this.masterCopy = masterCopy;
		this.masterCopyId = masterCopyId;
		this.forceBookToPlayerInventory = forceBookToPlayerInventory;
		this.canBeMovedFromInventory = canBeMovedFromInventory;
		this.copyTheBookWhenMoved = copyTheBookWhenMoved;
		this.useCost = useCost;
		setBookName(bookId, title, author);
	}

	public static HashMap<Short, BITBook> bitBooks = new HashMap<Short, BITBook>();

	public static Map<Integer, PopupScreen> popupScreen = new HashMap<Integer, PopupScreen>();
	public static Map<UUID, String> BITButtons = new HashMap<UUID, String>();
	public static Map<Integer, Integer> userno = new HashMap<Integer, Integer>();

	// Parameters for the bookPopupScreen
	public static Map<Integer, Short> currentBookId = new HashMap<Integer, Short>();
	public static Map<Integer, Boolean> hasOpenedBook = new HashMap<Integer, Boolean>();

	public static Map<Integer, GenericTextField> titleGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, Integer> currentPageNo = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> numberOfPagesGUI = new HashMap<Integer, Integer>();
	public static Map<Integer, GenericTextField> bodytextGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, String[]> bodytextGUI2 = new HashMap<Integer, String[]>();
	public static Map<Integer, GenericTextField> authorGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> coAuthorsGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, Boolean> masterCopyGUI = new HashMap<Integer, Boolean>();
	public static Map<Integer, GenericButton> masterCopyButtonGUI = new HashMap<Integer, GenericButton>();
	public static Map<Integer, Short> masterCopyIdGUI = new HashMap<Integer, Short>();
	public static Map<Integer, Boolean> forceBookToPlayerInventoryGUI = new HashMap<Integer, Boolean>();
	public static Map<Integer, GenericButton> forceBookToPlayerInventoryButtonGUI = new HashMap<Integer, GenericButton>();
	public static Map<Integer, GenericButton> canBeMovedFromInventoryButtonGUI = new HashMap<Integer, GenericButton>();
	public static Map<Integer, Boolean> canBeMovedFromInventoryGUI = new HashMap<Integer, Boolean>();
	public static Map<Integer, GenericButton> copyTheBookWhenMovedButtonGUI = new HashMap<Integer, GenericButton>();
	public static Map<Integer, Boolean> copyTheBookWhenMovedGUI = new HashMap<Integer, Boolean>();
	public static Map<Integer, GenericTextField> useCostGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericLabel> pageNoLabelGUI = new HashMap<Integer, GenericLabel>();

	public void setBitBook(short bookId, String title, String author,
			String coAuthors, int numberOfPages, String[] bodytext,
			Boolean masterCopy, short masterCopyId,
			Boolean forceBookToPlayerInventory,
			Boolean canBeMovedFromInventory, Boolean copyTheBookWhenMoved,
			int useCost) {
		this.bookId = bookId;
		this.title = title;
		this.author = author;
		this.coAuthors = coAuthors;
		this.numberOfPages = numberOfPages;
		this.bodytext = bodytext;
		this.masterCopy = masterCopy;
		this.masterCopyId = masterCopyId;
		this.forceBookToPlayerInventory = forceBookToPlayerInventory;
		this.canBeMovedFromInventory = canBeMovedFromInventory;
		this.copyTheBookWhenMoved = copyTheBookWhenMoved;
		this.useCost = useCost;
		setBookName(bookId, title, author);
	}

	public short getBookId() {
		return bookId;
	}

	public String getAuthor() {
		return author;
	}

	public String getCoAuthors() {
		return coAuthors;
	}

	public String getTitle() {
		return title;
	}

	public int getNumberOfPages() {
		return numberOfPages;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setBookId(short bookId) {
		this.bookId = bookId;
	}

	public void setMasterCopyId(short masterCopyId) {
		this.masterCopyId = masterCopyId;
	}

	public int getUseCost() {
		return useCost;
	}

	public Boolean getCopyTheBookWhenMoved() {
		return copyTheBookWhenMoved;
	}

	public Boolean getCanBeMovedFromInventory() {
		return canBeMovedFromInventory;
	}

	public Boolean getForceBookToPlayerInventory() {
		return forceBookToPlayerInventory;
	}

	public short getMasterCopyId() {
		return masterCopyId;
	}

	public Boolean getMasterCopy() {
		return masterCopy;
	}

	public String[] getBodytext() {
		return bodytext;
	}

	public String getBodytext(int i) {
		return bodytext[i];
	}

	protected final static Material writeableMaterials[] = { Material.BOOK };

	// Material.MAP, Material.SIGN,Material.SIGN_POST, Material.WALL_SIGN, ,
	// Material.PAPER, Material.PAINTING,

	/**
	 * Check if the Block is made of a writeable material.
	 * 
	 * @param block
	 * @return true or false
	 */
	public static boolean isWriteable(Block block) {
		for (Material i : writeableMaterials) {
			if (i == block.getType())
				return true;
		}
		return false;
	}

	public static boolean isWriteable(Material material) {
		for (Material i : writeableMaterials) {
			if (i == material)
				return true;
		}
		return false;
	}

	public static boolean hasPlayerOpenedBook(SpoutPlayer sPlayer) {
		if (BITBook.hasOpenedBook.containsKey(sPlayer.getEntityId()))
			return BITBook.hasOpenedBook.get(sPlayer.getEntityId());
		else
			return false;
	}

	public static short getNextBookId() {
		short nextId = getMaxBookId();
		nextId++;
		return nextId;
	}

	private static short getMaxBookId() {
		String query = "SELECT MAX(bookId) as max FROM " + BIT.bookTable + " ;";
		short max = 1000;
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
				max = (short) result.getInt("max");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (max < 1000)
			max = 1000;
		return max;
	}

	public static boolean isWritten(SpoutPlayer sPlayer, short bookId) {
		String query = "SELECT * FROM " + BIT.bookTable + " WHERE (bookId = "
				+ bookId + ");";
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
				if (BITConfig.DEBUG_SQL)
					sPlayer.sendMessage(ChatColor.YELLOW + "IsWritten: "
							+ query + "(true)");
				return true;
			} else {
				if (BITConfig.DEBUG_SQL)
					sPlayer.sendMessage(ChatColor.YELLOW + "IsWritten: "
							+ query + "(false)");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void saveBook(SpoutPlayer sPlayer, short bookId) {
		int id = sPlayer.getEntityId();
		bitBooks.put(
				bookId,
				new BITBook(bookId, titleGUI.get(id).getText(), authorGUI.get(
						id).getText(), coAuthorsGUI.get(id).getText(),
						numberOfPagesGUI.get(id), bodytextGUI2.get(id),
						masterCopyGUI.get(id), masterCopyIdGUI.get(id),
						forceBookToPlayerInventoryGUI.get(id),
						canBeMovedFromInventoryGUI.get(id),
						copyTheBookWhenMovedGUI.get(id), Integer
								.valueOf(useCostGUI.get(id).getText())));
		String query = "";
		boolean createBook = true;
		int cost = BITConfig.BOOK_COST;
		if (isWritten(sPlayer, bookId)) {
			for (int i = 0; i < bitBooks.get(bookId).getNumberOfPages(); i++) {
				query = "UPDATE "
						+ BIT.bookTable
						+ " SET bookid="
						+ bookId
						+ ", title='"
						+ bitBooks.get(bookId).getTitle()
						+ "', author='"
						+ bitBooks.get(bookId).getAuthor()
						+ "', coauthors='"
						+ bitBooks.get(bookId).getCoAuthors()
						+ "', numberofpages="
						+ bitBooks.get(bookId).getNumberOfPages()
						+ ", pageno="
						+ i
						+ ", bodytext='"
						+ bitBooks.get(bookId).getBodytext(i)
						+ "', mastercopy='"
						+ convertBooleanToInt(bitBooks.get(bookId)
								.getMasterCopy())
						+ "', mastercopyid="
						+ bitBooks.get(bookId).getMasterCopyId()
						+ ", forcebook='"
						+ convertBooleanToInt(bitBooks.get(bookId)
								.getForceBookToPlayerInventory())
						+ "', moved='"
						+ convertBooleanToInt(bitBooks.get(bookId)
								.getCanBeMovedFromInventory())
						+ "', copy='"
						+ convertBooleanToInt(bitBooks.get(bookId)
								.getCopyTheBookWhenMoved()) + "', usecost="
						+ bitBooks.get(bookId).getUseCost() + " WHERE bookid="
						+ bookId + " AND pageno=" + i + ";";
				if (BITConfig.DEBUG_SQL)
					sPlayer.sendMessage(ChatColor.YELLOW + "Updating book: "
							+ query);
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
			ItemStack item = sPlayer.getItemInHand();
			item.setDurability(bookId);
			sPlayer.setItemInHand(item);
			BITMessages.sendNotification(sPlayer, "Book updated.");
		} else {
			// NEW BOOK
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
						createBook = false;
					}
				}
			}
			if (createBook) {
				for (int i = 0; i < bitBooks.get(bookId).getNumberOfPages(); i++) {
					query = "INSERT INTO "
							+ BIT.bookTable
							+ "(bookid,title,"
							+ "author,coauthors,numberofpages,pageno,bodytext,mastercopy,"
							+ "mastercopyid,forcebook,moved,copy,usecost) VALUES ("
							+ bookId
							+ ", '"
							// + bitBooks.get(bookId).getTitle()
							+ titleGUI.get(id).getText()
							+ "', '"
							// + bitBooks.get(bookId).getAuthor()
							+ authorGUI.get(id).getText()
							+ "', '"
							// + bitBooks.get(bookId).getCoAuthors()
							+ coAuthorsGUI.get(id).getText()
							+ "', "
							// + bitBooks.get(bookId).getNumberOfPages()
							+ numberOfPagesGUI.get(id)
							+ ", "
							+ i
							+ ", '"
							// + bitBooks.get(bookId).getBodytext(i)
							+ bodytextGUI2.get(id)[i]
							+ "', '"
							// + bitBooks.get(bookId).getMasterCopy()
							+ convertBooleanToInt(masterCopyGUI.get(id))
							+ "', "
							// + bitBooks.get(bookId).getMasterCopyId()
							+ masterCopyIdGUI.get(id)
							+ ", '"
							// +
							// bitBooks.get(bookId).getForceBookToPlayerInventory()
							+ convertBooleanToInt(forceBookToPlayerInventoryGUI
									.get(id))
							+ "', '"
							// +bitBooks.get(bookId).getCanBeMovedFromInventory()
							+ convertBooleanToInt(canBeMovedFromInventoryGUI
									.get(id))
							+ "', '"
							// + bitBooks.get(bookId).getCopyTheBookWhenMoved()
							+ convertBooleanToInt(copyTheBookWhenMovedGUI
									.get(id)) + "', "
							// + bitBooks.get(bookId).getUseCost()
							+ Integer.valueOf(useCostGUI.get(id).getText())
							+ " );";
					if (BITConfig.DEBUG_SQL)
						sPlayer.sendMessage(ChatColor.YELLOW
								+ "Insert to bookTable: " + query);
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
				BITMessages.sendNotification(sPlayer, "Book created.");
				ItemStack item = sPlayer.getItemInHand();
				item.setDurability(bookId);
				sPlayer.setItemInHand(item);
				BITBook.currentBookId.put(id, (short) bookId);
			} else {
				sPlayer.sendMessage("You dont have enough money. Cost is:"
						+ cost);
			}
		}
		// BITBook.currentBookId.put(id, (short) 1000);
	}

	private static int convertBooleanToInt(Boolean b) {
		if (b)
			return 1;
		else
			return 0;
	}

	public static BITBook loadBook(SpoutPlayer sPlayer, short bookId) {
		boolean bookFound = false;
		String resTitle = "";
		String resAuthor = "";
		String resCoAuthors = "";
		int resNumberOfPages = 0;
		String resBodytext[] = new String[10];
		int resPageno;
		Boolean resMasterCopy = false;
		short resMasterCopyId = 0;
		Boolean resForceBookToPlayerInventory = false;
		Boolean resCanBeMovedFromInventory = true;
		Boolean resCopyTheBookWhenMoved = false;
		int resUseCost = 0;
		String query = "select * FROM " + BIT.bookTable + " WHERE bookId="
				+ bookId + ";";
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
				resTitle = result.getString("title");
				resAuthor = result.getString("author");
				resCoAuthors = result.getString("coauthors");
				resNumberOfPages = result.getInt("numberofpages");
				resPageno = result.getInt("pageno");
				resBodytext[resPageno] = result.getString("bodytext");
				resMasterCopy = result.getBoolean("mastercopy");
				resMasterCopyId = (short) result.getInt("mastercopyid");
				resForceBookToPlayerInventory = result.getBoolean("forcebook");
				resCanBeMovedFromInventory = result.getBoolean("moved");
				resCopyTheBookWhenMoved = result.getBoolean("copy");
				resUseCost = result.getInt("usecost");
				bookFound = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int id = sPlayer.getEntityId();
		if (bookFound) {
			bitBooks.put(bookId, new BITBook(bookId, resTitle, resAuthor,
					resCoAuthors, resNumberOfPages, resBodytext, resMasterCopy,
					resMasterCopyId, resForceBookToPlayerInventory,
					resCanBeMovedFromInventory, resCopyTheBookWhenMoved,
					resUseCost));
			BITBook.currentBookId.put(id, bookId);
			setBookName(bookId, resTitle, resAuthor);
		} else {
			if (bookId >= 1000) {
				ItemStack itemStack = sPlayer.getItemInHand();
				BITMessages.showWarning("BITBook:Bookdata missing in DB (ID:"
						+ bookId + ")");
				itemStack.setDurability((short) 0);
				BITBook.currentBookId.put(id, (short) 1000);
				setBookName(bookId, "Book", "");
				return null;
			}
		}
		return bitBooks.get(bookId);
	}

	public void removeBook(SpoutPlayer sPlayer, short bookId, int destroycost) {
		boolean deleteBook = true;
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
					deleteBook = false;
				}
			}
		}
		String query = "DELETE FROM " + BIT.bookTable + " WHERE (bookid = "
				+ bookId + ");";
		if (deleteBook) {
			if (BITConfig.DEBUG_SQL)
				sPlayer.sendMessage(ChatColor.YELLOW + "Removing book: "
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
			bitBooks.remove(bookId);
			BITMessages.sendNotification(sPlayer, "Book removed.");
		} else {
			BITMessages.sendNotification(sPlayer, "You need more money ("
					+ destroycost + ")");
		}
	}

	public void openBook(SpoutPlayer sPlayer, int bookId, Boolean writeable) {
		int y = 20, itemHeight = 20;
		int x = 100;
		int textFieldHeight = 100, textFieldWidth = 320;
		int buttonHeight = 18, buttonWidth = 80;
		int textFieldHeight2 = 15, textFieldWidth2 = 76;

		int id = sPlayer.getEntityId();
		addUserData(id);
		titleGUI.get(id).setText(title);
		authorGUI.get(id).setText(author);
		coAuthorsGUI.get(id).setText(coAuthors);
		currentPageNo.put(id, 0);
		numberOfPagesGUI.put(id, getNumberOfPages());
		for (int i = 0; i < getNumberOfPages(); i++) {
			bodytextGUI2.get(id)[i] = getBodytext(i);
			bodytextGUI.get(id).setText(bodytextGUI2.get(id)[i]);
		}
		useCostGUI.get(id).setText(String.valueOf(useCost));
		masterCopyGUI.put(id,masterCopy);
		forceBookToPlayerInventoryGUI.put(id, forceBookToPlayerInventory);
		canBeMovedFromInventoryGUI.put(id, canBeMovedFromInventory);
		copyTheBookWhenMovedGUI.put(id, copyTheBookWhenMoved);	

		// ItemWidget
		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(340));
		itemwidget.setX(y).setY(y);
		itemwidget.setHeight(itemHeight).setWidth(itemHeight)
				.setDepth(itemHeight);
		itemwidget.setTooltip("BITBook");
		popupScreen.get(id).attachWidget(BIT.plugin, itemwidget);

		// Label - PageNo
		pageNoLabelGUI.get(id).setX(x + textFieldWidth - 2 * buttonHeight - 5)
				.setY(y + 2 * itemHeight - buttonHeight - 1)
				.setHeight(buttonHeight);
		pageNoLabelGUI.get(id).setText("Page:" + (currentPageNo.get(id) + 1));
		popupScreen.get(id).attachWidget(BIT.plugin, pageNoLabelGUI.get(id));

		// + Button
		GenericButton nextPageBookButton = new GenericButton("+");
		nextPageBookButton.setAuto(false)
				.setX(x + textFieldWidth - 2 * buttonHeight - 5)
				.setY(y + 2 * itemHeight + 10 - buttonHeight - 1)
				.setHeight(buttonHeight).setWidth(buttonHeight);
		nextPageBookButton.setTooltip("Next page ("
				+ String.valueOf(currentPageNo.get(id) + 1) + ")");
		popupScreen.get(id).attachWidget(BIT.plugin, nextPageBookButton);
		BITButtons.put(nextPageBookButton.getId(), "nextPageButton");

		// - Button
		GenericButton previousPageBookButton = new GenericButton("-");
		previousPageBookButton.setAuto(false)
				.setX(x + textFieldWidth - buttonHeight)
				.setY(y + 2 * itemHeight + 10 - buttonHeight - 1)
				.setHeight(buttonHeight).setWidth(buttonHeight);
		previousPageBookButton.setTooltip("Previous page ("
				+ String.valueOf(currentPageNo.get(id) - 1) + ")");
		popupScreen.get(id).attachWidget(BIT.plugin, previousPageBookButton);
		BITButtons.put(previousPageBookButton.getId(), "previousPageButton");

		// Title
		titleGUI.get(id).getText();
		titleGUI.get(id).setTooltip("Title of the book");
		titleGUI.get(id).setCursorPosition(1).setMaximumCharacters(30);
		titleGUI.get(id).setX(x).setY(y + 2 * itemHeight + 7 - buttonHeight);
		titleGUI.get(id).setHeight(textFieldHeight2)
				.setWidth(textFieldWidth2 * 2);
		popupScreen.get(id).attachWidget(BIT.plugin, titleGUI.get(id));

		y = y + 2 * itemHeight + 10;

		bodytextGUI.get(id)
				.setText(bodytextGUI2.get(id)[currentPageNo.get(id)]);
		bodytextGUI.get(id).setTooltip("Enter the text in the book.");
		bodytextGUI.get(id).setX(x).setY(y);
		bodytextGUI.get(id).setHeight(textFieldHeight).setWidth(textFieldWidth);
		// page.setMarginLeft(20).setMarginBottom(20);
		bodytextGUI.get(id).setCursorPosition(1).setMaximumCharacters(1000);
		bodytextGUI.get(id).setMaximumLines(8);
		bodytextGUI.get(id).setFocus(true);
		popupScreen.get(id).attachWidget(BIT.plugin, bodytextGUI.get(id));
		y = y + textFieldHeight + 10;

		// SaveButton
		if (writeable) { //READ_WRITE
			GenericButton saveButton = new GenericButton("Save");
			saveButton.setAuto(false)
					.setX(x + textFieldWidth - 2 * (buttonWidth - 20) - 5)
					.setY(y).setHeight(buttonHeight).setWidth(buttonWidth - 20);
			BITButtons.put(saveButton.getId(), "saveBookButton");
			popupScreen.get(id).attachWidget(BIT.plugin, saveButton);

			// cancelButton
			GenericButton cancelBookButton = new GenericButton("Cancel");
			cancelBookButton.setAuto(false)
					.setX(x + textFieldWidth - buttonWidth + 20).setY(y)
					.setHeight(buttonHeight).setWidth(buttonWidth - 20);
			popupScreen.get(id).attachWidget(BIT.plugin, cancelBookButton);
			BITButtons.put(cancelBookButton.getId(), "cancelBookButton");
		} else { //READONLY
			GenericButton returnBookButton = new GenericButton("Return");
			returnBookButton.setAuto(false)
					.setX(x + textFieldWidth - buttonWidth + 20).setY(y)
					.setHeight(buttonHeight).setWidth(buttonWidth - 20);
			popupScreen.get(id).attachWidget(BIT.plugin, returnBookButton);
			BITButtons.put(returnBookButton.getId(), "returnBookButton");
		}

		if (writeable) { //READ_WRITE

		x = 10;
		y = 67;

		// masterCopyButton
		masterCopyButtonGUI.get(id).setText("Master:" + masterCopyGUI.get(id));
		masterCopyButtonGUI.get(id).setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		// masterCopyButtonGUI.get(id).setTooltip(
		// "The masterCopy keeps all copies updated aumatically.");
		masterCopyButtonGUI.get(id).setTooltip("NOT IMPLEMENTET YET!");

		popupScreen.get(id).attachWidget(BIT.plugin,
				masterCopyButtonGUI.get(id));
		BITButtons.put(masterCopyButtonGUI.get(id).getId(), "masterCopyButton");
		y = y + buttonHeight + 1;

		// forceBookToPlayerInventoryButton
		forceBookToPlayerInventoryButtonGUI.get(id).setText(
				"Force:" + forceBookToPlayerInventoryGUI.get(id));
		forceBookToPlayerInventoryButtonGUI.get(id).setAuto(false).setX(x)
				.setY(y).setHeight(buttonHeight).setWidth(buttonWidth);
		// forceBookToPlayerInventoryButtonGUI.get(id).setTooltip(
		// "Force this book in to all players inventory.");
		forceBookToPlayerInventoryButtonGUI.get(id).setTooltip(
				"NOT IMPLEMENTET YET!");

		popupScreen.get(id).attachWidget(BIT.plugin,
				forceBookToPlayerInventoryButtonGUI.get(id));
		BITButtons.put(forceBookToPlayerInventoryButtonGUI.get(id).getId(),
				"forceBookToPlayerInventoryButton");
		y = y + buttonHeight + 1;

		// canBeMovedFromInventoryButton
		canBeMovedFromInventoryButtonGUI.get(id).setText(
				"Moved:" + canBeMovedFromInventoryGUI.get(id));
		canBeMovedFromInventoryButtonGUI.get(id).setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		canBeMovedFromInventoryButtonGUI.get(id).setTooltip(
				"If false, only the Author can move the book.");
		popupScreen.get(id).attachWidget(BIT.plugin,
				canBeMovedFromInventoryButtonGUI.get(id));
		BITButtons.put(canBeMovedFromInventoryButtonGUI.get(id).getId(),
				"canBeMovedFromInventoryButton");
		y = y + buttonHeight + 1;

		// copyTheBookWhenMovedButton
		copyTheBookWhenMovedButtonGUI.get(id).setText(
				"Copy:" + copyTheBookWhenMovedGUI.get(id));
		copyTheBookWhenMovedButtonGUI.get(id).setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		copyTheBookWhenMovedButtonGUI
				.get(id)
				.setTooltip(
						"Specifies if the book is being copied or moved if the player moves the book.");
		popupScreen.get(id).attachWidget(BIT.plugin,
				copyTheBookWhenMovedButtonGUI.get(id));
		BITButtons.put(copyTheBookWhenMovedButtonGUI.get(id).getId(),
				"copyTheBookWhenMovedButton");
		y = y + buttonHeight + 1;
		
		}

		// useCost
		useCostGUI.get(id).getText();
		useCostGUI.get(id).setTooltip("Enter the cost to read the book.");
		useCostGUI.get(id).setCursorPosition(1).setMaximumCharacters(20);
		useCostGUI.get(id).setX(x + 2).setY(y);
		useCostGUI.get(id).setHeight(textFieldHeight2)
				.setWidth(textFieldWidth2);
		popupScreen.get(id).attachWidget(BIT.plugin, useCostGUI.get(id));
		y = y + buttonHeight + 2;

		// Author
		authorGUI.get(id).getText();
		authorGUI.get(id).setTooltip("Author of the book.");
		authorGUI.get(id).setCursorPosition(1).setMaximumCharacters(20);
		authorGUI.get(id).setX(x + 2).setY(y);
		authorGUI.get(id).setHeight(textFieldHeight2).setWidth(textFieldWidth2);
		popupScreen.get(id).attachWidget(BIT.plugin, authorGUI.get(id));
		y = y + buttonHeight + 2;

		// coAuthors
		coAuthorsGUI.get(id).getText();
		coAuthorsGUI.get(id).setTooltip("coAuthors of the book.");
		coAuthorsGUI.get(id).setCursorPosition(1).setMaximumCharacters(50);
		coAuthorsGUI.get(id).setX(x + 2).setY(y);
		coAuthorsGUI.get(id).setHeight(textFieldHeight2)
				.setWidth(textFieldWidth2 * 3 + 40);
		popupScreen.get(id).attachWidget(BIT.plugin, coAuthorsGUI.get(id));

		// Open Window
		popupScreen.get(id).setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(popupScreen.get(id));

	}

	public static void cleanupPopupScreen(SpoutPlayer sPlayer) {
		int playerId = sPlayer.getEntityId();
		if (popupScreen.containsKey(playerId)) {
			popupScreen.get(playerId).removeWidgets(BIT.plugin);
			popupScreen.get(playerId).setDirty(true);
			sPlayer.getMainScreen().removeWidgets(BIT.plugin);
		}
	}

	public static void removeUserDataxx(int id) {
		if (userno.containsKey(id)) {
			// BITBook
			popupScreen.remove(id);
			titleGUI.remove(id);
			currentPageNo.remove(id);
			numberOfPagesGUI.remove(id);
			bodytextGUI.remove(id);
			bodytextGUI2.remove(id);
			authorGUI.remove(id);
			coAuthorsGUI.remove(id);
			masterCopyGUI.remove(id);
			masterCopyButtonGUI.remove(id);
			masterCopyIdGUI.remove(id);
			forceBookToPlayerInventoryGUI.remove(id);
			forceBookToPlayerInventoryButtonGUI.remove(id);
			canBeMovedFromInventoryGUI.remove(id);
			canBeMovedFromInventoryButtonGUI.remove(id);
			copyTheBookWhenMovedGUI.remove(id);
			copyTheBookWhenMovedButtonGUI.remove(id);
			useCostGUI.remove(id);
			// currentBookId.remove(id);
			// currentBookId.put(id, (short) 1000);
			pageNoLabelGUI.remove(id);
		}
	}

	public static void addUserData(int id) {
		if (!userno.containsKey(id)) {
			// BITBook
			popupScreen.put(id, new GenericPopup());
			titleGUI.put(id, new GenericTextField());
			currentPageNo.put(id, 0);
			numberOfPagesGUI.put(id, 0);
			bodytextGUI.put(id, new GenericTextField());
			bodytextGUI2.put(id, new String[10]);
			authorGUI.put(id, new GenericTextField());
			coAuthorsGUI.put(id, new GenericTextField());
			masterCopyGUI.put(id, false);
			masterCopyButtonGUI.put(id, new GenericButton());
			masterCopyIdGUI.put(id, (short) 0);
			forceBookToPlayerInventoryGUI.put(id, false);
			forceBookToPlayerInventoryButtonGUI.put(id, new GenericButton());
			canBeMovedFromInventoryGUI.put(id, true);
			canBeMovedFromInventoryButtonGUI.put(id, new GenericButton());
			copyTheBookWhenMovedGUI.put(id, false);
			copyTheBookWhenMovedButtonGUI.put(id, new GenericButton());
			useCostGUI.put(id, new GenericTextField());
			pageNoLabelGUI.put(id, new GenericLabel());
			// currentBookId is set when the book is created.
			// currentBookId.put(id, 0);
			// currentBookId.put(id, (short) 1000);
		}
	}

	public static void showNextPage(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		int i = currentPageNo.get(id);
		bodytextGUI2.get(id)[i] = bodytextGUI.get(id).getText();
		if (i == (numberOfPagesGUI.get(id) - 1)) {
			i = 0;
		} else {
			i++;
		}
		currentPageNo.put(id, i);
		bodytextGUI.get(id).setText(bodytextGUI2.get(id)[i]);
		pageNoLabelGUI.get(id).setText("Page:" + (i + 1));
		bodytextGUI.get(id).setFocus(true);
		popupScreen.get(id).setDirty(true);

	}

	public static void showPreviousPage(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		int i = currentPageNo.get(id);
		bodytextGUI2.get(id)[i] = bodytextGUI.get(id).getText();
		if (i == 0) {
			i = numberOfPagesGUI.get(id) - 1;
		} else {
			i--;
		}
		currentPageNo.put(id, i);
		bodytextGUI.get(id).setText(bodytextGUI2.get(id)[i]);
		pageNoLabelGUI.get(id).setText("Page:" + (i + 1));
		bodytextGUI.get(id).setFocus(true);
		popupScreen.get(id).setDirty(true);

	}

	public static void setBookName(short bookId, String title, String author) {
		// TODO: use getMaterialManager
		 //String str = "Book:"+title;
		 //SpoutManager.getMaterialManager().setItemName(org.getspout.spoutapi.material.Material, "BBoook");
		 //SpoutManager.getItemManager().setItemName(Material.BOOK,
		 //bookId,"Book:" + title);
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			SpoutPlayer sp = (SpoutPlayer) p;
			if (sp.isSpoutCraftEnabled()) {
				
				sp.sendPacket(new PacketItemName(Material.BOOK.getId(), bookId,
						title + " written by " + author));
				sp.sendPacket(new PacketItemName(Material.BOOK.getId(),
						(short) 0, "Book")); // don't know why this is needed
			}
		}
	}

	public void updateAllBooknames(SpoutPlayer sPlayer, Inventory inventory) {
		ItemStack itemStack;
		for (int i = 0; i < inventory.getSize(); i++) {
			itemStack = inventory.getItem(i);
			if (itemStack.getType() == Material.BOOK) {
				Item item = (Item) itemStack;
				item.setName("BBBBoook");
				
			}
		}
	}

}