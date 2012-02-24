package dk.gabriel333.BukkitInventoryTools.Book;

import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BITBookSpoutListener implements Listener {

        @EventHandler
	public void onCustomEvent(ButtonClickEvent event) {
                Button button = event.getButton();
                UUID uuid = button.getId();
                SpoutPlayer sPlayer = event.getPlayer();
                ItemStack itemInHand = sPlayer.getInventory().getItemInHand();
                int id = sPlayer.getEntityId();
                if ("saveBookButton".equals(BITBook.BITButtons.get(uuid))) {
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

                } else if ("cancelBookButton".equals(BITBook.BITButtons.get(uuid))
                                || "returnBookButton".equals(BITBook.BITButtons.get(uuid))) {
                        BITBook.popupScreen.get(id).close();
                        BITBook.cleanupPopupScreen(sPlayer);
                        BITBook.BITButtons.remove(uuid);
                        BITBook.bitBooks.remove(BITBook.currentBookId.get(id));
                        BITBook.currentBookId.put(id, (short) 1000);
                        sPlayer.closeActiveWindow();
                        BITBook.hasOpenedBook.put(id, false);

                } else if (("nextPageButton".equals(BITBook.BITButtons.get(uuid)))) {
                        if (validateFields(sPlayer)) {
                                BITBook.showNextPage(sPlayer);

                        }
                } else if (("previousPageButton".equals(BITBook.BITButtons.get(uuid)))) {
                        if (validateFields(sPlayer)) {
                                BITBook.showPreviousPage(sPlayer);

                        }
                } else if (("masterCopyButton".equals(BITBook.BITButtons.get(uuid)))) {
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
                } else if (("forceBookToPlayerInventoryButton".equals(BITBook.BITButtons.get(uuid)))) {
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
                } else if (("canBeMovedFromInventoryButton".equals(BITBook.BITButtons.get(uuid)))) {
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
                } else if (("copyTheBookWhenMovedButton".equals(BITBook.BITButtons.get(uuid)))) {
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
