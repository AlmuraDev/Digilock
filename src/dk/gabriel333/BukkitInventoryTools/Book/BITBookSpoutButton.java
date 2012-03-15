package dk.gabriel333.BukkitInventoryTools.Book;

import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BITBookSpoutButton extends GenericButton {
    public BITBookSpoutButton() {
        super();
    }

    public BITBookSpoutButton(String name) {
        super(name);
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        UUID uuid = this.getId();
        SpoutPlayer sPlayer = event.getPlayer();
        ItemStack itemInHand = sPlayer.getInventory().getItemInHand();
        int entId = sPlayer.getEntityId();
        String buttonString = BITBook.BITButtons.get(this.getId());
        if (buttonString.equals("saveBookButton")) {
            BITBook.popupScreen.get(entId).close();
            BITBook.cleanupPopupScreen(sPlayer);
            BITBook.BITButtons.remove(uuid);
            int i = BITBook.currentPageNo.get(entId);
            BITBook.bodytextGUI2.get(entId)[i] = BITBook.bodytextGUI.get(entId)
                                                 .getText();

            BITBook.saveBook(sPlayer, BITBook.currentBookId.get(entId));
            itemInHand.setDurability(BITBook.currentBookId.get(entId));
            sPlayer.closeActiveWindow();
            BITBook.hasOpenedBook.put(entId, false);

        } else if (buttonString.equals("cancelBookButton")
                   || buttonString.equals("returnBookButton")) {
            BITBook.popupScreen.get(entId).close();
            BITBook.cleanupPopupScreen(sPlayer);
            BITBook.BITButtons.remove(uuid);
            BITBook.bitBooks.remove(BITBook.currentBookId.get(entId));
            BITBook.currentBookId.put(entId, (short) 1000);
            sPlayer.closeActiveWindow();
            BITBook.hasOpenedBook.put(entId, false);

        } else if (buttonString.equals("nextPageButton")) {
            if (validateFields(sPlayer)) {
                BITBook.showNextPage(sPlayer);

            }
        } else if (buttonString.equals("previousPageButton")) {
            if (validateFields(sPlayer)) {
                BITBook.showPreviousPage(sPlayer);

            }
        } else if (buttonString.equals("masterCopyButton")) {
            if (validateFields(sPlayer)) {
                if (BITBook.masterCopyGUI.get(entId)) {
                    BITBook.masterCopyGUI.put(entId, false);
                } else {
                    BITBook.masterCopyGUI.put(entId, true);
                }
                BITBook.masterCopyButtonGUI.get(entId).setText(
                    "Master:" + BITBook.masterCopyGUI.get(entId));
                BITBook.masterCopyButtonGUI.get(entId).setDirty(true);
            }
        } else if (buttonString.equals("forceBookToPlayerInventoryButton")) {
            if (validateFields(sPlayer)) {
                if (BITBook.forceBookToPlayerInventoryGUI.get(entId)) {
                    BITBook.forceBookToPlayerInventoryGUI.put(entId, false);
                } else {
                    BITBook.forceBookToPlayerInventoryGUI.put(entId, true);
                }
                BITBook.forceBookToPlayerInventoryButtonGUI
                .get(entId)
                .setText(
                    "Force:"
                    + BITBook.forceBookToPlayerInventoryGUI
                    .get(entId));
                BITBook.forceBookToPlayerInventoryButtonGUI.get(entId)
                .setDirty(true);
            }
        } else if (buttonString.equals("canBeMovedFromInventoryButton")) {
            if (validateFields(sPlayer)) {
                if (BITBook.canBeMovedFromInventoryGUI.get(entId)) {
                    BITBook.canBeMovedFromInventoryGUI.put(entId, false);
                } else {
                    BITBook.canBeMovedFromInventoryGUI.put(entId, true);
                }
                BITBook.canBeMovedFromInventoryButtonGUI.get(entId).setText(
                    "Moved:"
                    + BITBook.canBeMovedFromInventoryGUI
                    .get(entId));
                BITBook.canBeMovedFromInventoryButtonGUI.get(entId).setDirty(
                    true);
            }
        } else if (buttonString.equals("copyTheBookWhenMovedButton")) {
            if (validateFields(sPlayer)) {
                if (BITBook.copyTheBookWhenMovedGUI.get(entId)) {
                    BITBook.copyTheBookWhenMovedGUI.put(entId, false);
                } else {
                    BITBook.copyTheBookWhenMovedGUI.put(entId, true);
                }
                BITBook.copyTheBookWhenMovedButtonGUI.get(entId).setText(
                    "Copy:" + BITBook.copyTheBookWhenMovedGUI.get(entId));
                BITBook.copyTheBookWhenMovedButtonGUI.get(entId)
                .setDirty(true);
            }
        }

        // ************************************
        // This only happens if I have forgot to handle a button
        // ************************************
        else {
            if (BITConfig.DEBUG_GUI)
                sPlayer.sendMessage("BITBookSpoutListener: Unknown button:"
                                    + buttonString);
        }
    }

    private boolean validateFields(SpoutPlayer sPlayer) {
        int entId = sPlayer.getEntityId();
        if (BITBook.useCostGUI.get(entId).getText().equals("")) {
            BITBook.useCostGUI.get(entId).setText("0");
            BITBook.popupScreen.get(entId).setDirty(true);
        }
        int useCost = Integer.valueOf(BITBook.useCostGUI.get(entId).getText());

        if (useCost > BITConfig.BOOK_USEMAXCOST) {
            BITMessages.sendNotification(sPlayer, "Cost must be less "
                                         + BITConfig.BOOK_USEMAXCOST);
            BITBook.useCostGUI.get(entId).setText(
                String.valueOf(BITConfig.BOOK_USEMAXCOST));
            BITBook.popupScreen.get(entId).setDirty(true);
            return false;
        } else if (useCost < 0) {
            BITMessages.sendNotification(sPlayer, "Cost must be >= 0");
            BITBook.useCostGUI.get(entId).setText("0");
            BITBook.popupScreen.get(entId).setDirty(true);
            return false;
        }

        return true;
    }
}
