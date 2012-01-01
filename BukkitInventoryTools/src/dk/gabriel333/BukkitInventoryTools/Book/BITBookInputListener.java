package dk.gabriel333.BukkitInventoryTools.Book;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.*;

public class BITBookInputListener extends InputListener {

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		SpoutPlayer sPlayer = event.getPlayer();
		ScreenType screentype = event.getScreenType();
		String keypressed = event.getKey().name();
		if (BITConfig.DEBUG_EVENTS) {
			sPlayer.sendMessage("BITBookInputListn.key:" + keypressed
					+ " Screentype:" + screentype);
		}
		if (!(keypressed.equals(BITConfig.LIBRARY_READKEY) || keypressed
				.equals("KEY_ESCAPE")))
			return;
		ItemStack itemInHand = sPlayer.getInventory().getItemInHand();
		int id = sPlayer.getEntityId();
		if (BITBook.isWriteable(itemInHand.getType())) {
			if (keypressed.equals(BITConfig.LIBRARY_READKEY)
					&& screentype != ScreenType.CHAT_SCREEN) {
				if (!BITBook.hasPlayerOpenedBook(sPlayer)) {

					handleItemInHand(sPlayer);
				}
			} else if (keypressed.equals("KEY_ESCAPE")
					&& screentype != ScreenType.GAME_SCREEN) {
				BITInventory.openedInventories.remove(id);
				sPlayer.closeActiveWindow();
				BITBook.cleanupPopupScreen(sPlayer);
				BITBook.bitBooks.remove(BITBook.currentBookId.get(id));
				BITBook.currentBookId.put(id, (short) 1000);
				BITBook.hasOpenedBook.put(id, false);
			}
		}
	}

	private void handleItemInHand(SpoutPlayer sPlayer) {
		ItemStack itemInHand = sPlayer.getItemInHand();
		if (BITBook.isWriteable(itemInHand.getType())
				&& itemInHand.getAmount() == 1) {
			short bookId = itemInHand.getDurability();
			BITBook bitBook = new BITBook();
			int id = sPlayer.getEntityId();
			if (bookId > 1000) {
				if (BITPermissions.hasPerm(sPlayer, "book.use",
						BITPermissions.NOT_QUIET)
						|| BITPermissions.hasPerm(sPlayer, "book.admin",
								BITPermissions.NOT_QUIET)) {
					bitBook = BITBook.loadBook(sPlayer, bookId);
					if (bitBook != null) {
						BITBook.hasOpenedBook.put(id, true);
						if ((bitBook.getAuthor().equals(sPlayer.getName()) || bitBook
								.getCoAuthors().contains(sPlayer.getName()))
								&& bitBook.getMasterCopyId() == 0) {
							bitBook.openBook(sPlayer, bookId, BITBook.WRITEABLE);
						} else {
							bitBook.openBook(sPlayer,
									bitBook.getMasterCopyId(), BITBook.READONLY);
						}
					} else {
						handleItemInHand(sPlayer);
					}
				}
			} else if (bookId == 0) {
				// new book
				if (BITPermissions.hasPerm(sPlayer, "book.create",
						BITPermissions.NOT_QUIET)) {
					bookId = BITBook.getNextBookId();
					// sPlayer.sendMessage("Creating new book with id:" +
					// bookId);
					BITBook.currentBookId.put(id, bookId);
					String title = "Title";
					String author = sPlayer.getName();
					String coAuthors = "";
					int numberOfPages = 3;
					String[] pages = new String[numberOfPages];
					pages[0] = "Page 1";
					pages[1] = "Page 2";
					pages[2] = "Page 3";
					Boolean masterCopy = false;
					short masterCopyId = 0;
					Boolean forceBookToPlayerInventory = false;
					Boolean canBeMovedFromInventory = true;
					Boolean copyTheBookWhenMoved = false;
					int useCost = 0;
					bitBook.setBitBook(bookId, title, author, coAuthors,
							numberOfPages, pages, masterCopy, masterCopyId,
							forceBookToPlayerInventory,
							canBeMovedFromInventory, copyTheBookWhenMoved,
							useCost);
					BITBook.bitBooks.put(bookId, bitBook);
					BITBook.hasOpenedBook.put(id, true);
					if (bitBook.getMasterCopyId() == 0) {
						bitBook.openBook(sPlayer, bookId, BITBook.WRITEABLE);
					} else {
						bitBook.openBook(sPlayer, bitBook.getMasterCopyId(),
								BITBook.READONLY);
					}

				}
			} else {
				// TODO: edit/open/import bookworm book.
				BITMessages.sendNotification(sPlayer,
						"This is a bookworm book!");
			}
		} else {
			if (BITBook.isWriteable(itemInHand.getType())) {
				if (itemInHand.getAmount() != 1) {
					sPlayer.sendMessage("There must only be one item in the slot");
				}
			} else {
				sPlayer.sendMessage("You cant write in a :"
						+ itemInHand.getType());
			}
		}
	}
}
