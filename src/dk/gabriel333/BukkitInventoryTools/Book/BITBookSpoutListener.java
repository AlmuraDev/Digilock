package dk.gabriel333.BukkitInventoryTools.Book;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;

public class BITBookSpoutListener extends SpoutListener {

	public void onCustomEvent(Event event) {
		if (event instanceof ButtonClickEvent) {
			Button button = ((ButtonClickEvent) event).getButton();
			UUID uuid = button.getId();
			SpoutPlayer sPlayer = ((ButtonClickEvent) event).getPlayer();
			ItemStack itemInHand = sPlayer.getInventory().getItemInHand();
			int id = sPlayer.getEntityId();
			if (BITBook.BITButtons.get(uuid) == "saveBookButton") {
				BITBook.popupScreen.get(id).close();
				BITBook.cleanupPopupScreen(sPlayer);
				BITBook.BITButtons.remove(uuid);
				int i = BITBook.currentPageNo.get(id);
				BITBook.bodytextGUI2.get(id)[i] = BITBook.bodytextGUI.get(id)
						.getText();

				BITBook.saveBook(sPlayer, BITBook.currentBookId.get(id));
				itemInHand.setDurability(BITBook.currentBookId.get(id));
				sPlayer.closeActiveWindow();
				BITBook.hasOpenedBook.put(id, false);

			} else if (BITBook.BITButtons.get(uuid) == "cancelBookButton"
					|| BITBook.BITButtons.get(uuid) == "returnBookButton") {
				BITBook.popupScreen.get(id).close();
				BITBook.cleanupPopupScreen(sPlayer);
				BITBook.BITButtons.remove(uuid);
				BITBook.bitBooks.remove(BITBook.currentBookId.get(id));
				BITBook.currentBookId.put(id, (short) 1000);
				sPlayer.closeActiveWindow();
				BITBook.hasOpenedBook.put(id, false);

			} else if ((BITBook.BITButtons.get(uuid) == "nextPageButton")) {
				if (validateFields(sPlayer)) {
					BITBook.showNextPage(sPlayer);

				}
			} else if ((BITBook.BITButtons.get(uuid) == "previousPageButton")) {
				if (validateFields(sPlayer)) {
					BITBook.showPreviousPage(sPlayer);

				}
			} else if ((BITBook.BITButtons.get(uuid) == "masterCopyButton")) {
				if (validateFields(sPlayer)) {
					if (BITBook.masterCopyGUI.get(id)) {
						BITBook.masterCopyGUI.put(id, false);
					} else {
						BITBook.masterCopyGUI.put(id, true);
					}
					BITBook.masterCopyButtonGUI.get(id).setText(
							"Master:" + BITBook.masterCopyGUI.get(id));
					BITBook.masterCopyButtonGUI.get(id).setDirty(true);
				}
			} else if ((BITBook.BITButtons.get(uuid) == "forceBookToPlayerInventoryButton")) {
				if (validateFields(sPlayer)) {
					if (BITBook.forceBookToPlayerInventoryGUI.get(id)) {
						BITBook.forceBookToPlayerInventoryGUI.put(id, false);
					} else {
						BITBook.forceBookToPlayerInventoryGUI.put(id, true);
					}
					BITBook.forceBookToPlayerInventoryButtonGUI
							.get(id)
							.setText(
									"Force:"
											+ BITBook.forceBookToPlayerInventoryGUI
													.get(id));
					BITBook.forceBookToPlayerInventoryButtonGUI.get(id)
							.setDirty(true);
				}
			} else if ((BITBook.BITButtons.get(uuid) == "canBeMovedFromInventoryButton")) {
				if (validateFields(sPlayer)) {
					if (BITBook.canBeMovedFromInventoryGUI.get(id)) {
						BITBook.canBeMovedFromInventoryGUI.put(id, false);
					} else {
						BITBook.canBeMovedFromInventoryGUI.put(id, true);
					}
					BITBook.canBeMovedFromInventoryButtonGUI.get(id).setText(
							"Moved:"
									+ BITBook.canBeMovedFromInventoryGUI
											.get(id));
					BITBook.canBeMovedFromInventoryButtonGUI.get(id).setDirty(
							true);
				}
			} else if ((BITBook.BITButtons.get(uuid) == "copyTheBookWhenMovedButton")) {
				if (validateFields(sPlayer)) {
					if (BITBook.copyTheBookWhenMovedGUI.get(id)) {
						BITBook.copyTheBookWhenMovedGUI.put(id, false);
					} else {
						BITBook.copyTheBookWhenMovedGUI.put(id, true);
					}
					BITBook.copyTheBookWhenMovedButtonGUI.get(id).setText(
							"Copy:" + BITBook.copyTheBookWhenMovedGUI.get(id));
					BITBook.copyTheBookWhenMovedButtonGUI.get(id)
							.setDirty(true);
				}
			}

			// ************************************
			// This only happens if I have forgot to handle a button
			// ************************************
			else {
				if (BITConfig.DEBUG_GUI)
					sPlayer.sendMessage("BITBookSpoutListener: Unknow button:"
							+ BITBook.BITButtons.get(uuid));
			}
		}
	}

	private boolean validateFields(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		if (BITBook.useCostGUI.get(id).getText().equals("")) {
			BITBook.useCostGUI.get(id).setText("0");
			BITBook.popupScreen.get(id).setDirty(true);
		}
		int useCost = Integer.valueOf(BITBook.useCostGUI.get(id).getText());

		if (useCost > BITConfig.BOOK_USEMAXCOST) {
			BITMessages.sendNotification(sPlayer, "Cost must be less "
					+ BITConfig.BOOK_USEMAXCOST);
			BITBook.useCostGUI.get(id).setText(
					String.valueOf(BITConfig.BOOK_USEMAXCOST));
			BITBook.popupScreen.get(id).setDirty(true);
			return false;
		} else if (useCost < 0) {
			BITMessages.sendNotification(sPlayer, "Cost must be >= 0");
			BITBook.useCostGUI.get(id).setText("0");
			BITBook.popupScreen.get(id).setDirty(true);
			return false;
		}

		return true;
	}
}
