package dk.gabriel333.BukkitInventoryTools.Inventory;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.DigiLock.BITDigiLock;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BITInventorySpoutListener implements Listener {
	
	@SuppressWarnings("unused")
	private BIT plugin;

	public BITInventorySpoutListener(BIT plugin) {
		this.plugin = plugin;
	}

        @EventHandler
	public void onCustomEvent(ButtonClickEvent event) {
                Button button = event.getButton();
                UUID uuid = button.getId();
                SpoutPlayer sPlayer = event.getPlayer();
                //SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
                int id = sPlayer.getEntityId();
                SpoutBlock sBlock;
                sBlock = BITDigiLock.clickedBlock.get(id);
                if (sBlock == null) {
                        sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
                }

                if (("setPincodeCancel".equals(BITInventory.BITBookButtons.get(uuid)))) {
                        sPlayer.closeActiveWindow();
                        BITInventory.cleanupPopupScreen(sPlayer);
                        BITInventory.BITBookButtons.remove(uuid);

                } else if (("OwnerButton".equals(BITInventory.BITBookButtons.get(uuid)))) {
                        if (validateSetPincodeFields(sPlayer)) {
                        }

                } else if (("CoOwnerButton".equals(BITInventory.BITBookButtons.get(uuid)))) {
                        if (validateSetPincodeFields(sPlayer)) {
                        }

                } else if (("UseCostButton".equals(BITInventory.BITBookButtons.get(uuid)))) {
                        if (validateSetPincodeFields(sPlayer)) {
                        }

                }

                else if (("CreateBookshelfButton".equals(BITInventory.BITBookButtons.get(uuid)))) {
                        if (validateSetPincodeFields(sPlayer)) {
                                String coowners = "";
                                String name = "";
                                String owner = sPlayer.getName();
                                int usecost = 0;
                                Inventory inventory = SpoutManager.getInventoryBuilder()
                                                .construct(BITConfig.BOOKSHELF_SIZE, name);
                                BITInventory.saveBitInventory(sPlayer, sBlock, owner, name,
                                                coowners, inventory, usecost);
                                sPlayer.closeActiveWindow();
                                BITInventory.cleanupPopupScreen(sPlayer);
                        }
                }

                else if (("removeBookshelfButton".equals(BITInventory.BITBookButtons.get(uuid)))) {
                        if (validateSetPincodeFields(sPlayer)) {
                        }
                }

                // ************************************
                // This only happens if I have forgot to handle a button
                // ************************************
                else {
                        if (BITConfig.DEBUG_GUI)
                                sPlayer.sendMessage("BITSpoutListener: Unknow button:"
                                                + BITInventory.BITBookButtons.get(uuid));
                }
	}

	private boolean validateSetPincodeFields(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		if (BITInventory.useCostGUI.get(id).getText().equals("")) {
			BITInventory.useCostGUI.get(id).setText("0");
			BITInventory.popupScreen.get(id).setDirty(true);
		}

		int useCost = Integer.valueOf(BITInventory.useCostGUI.get(id).getText());
		if (useCost > BITConfig.DIGILOCK_USEMAXCOST) {
			BITMessages.sendNotification(sPlayer, "Cost must be less "
					+ BITConfig.DIGILOCK_USEMAXCOST);
			BITInventory.useCostGUI.get(id).setText(
					String.valueOf(BITConfig.DIGILOCK_USEMAXCOST));
			BITInventory.popupScreen.get(id).setDirty(true);
			return false;
		} else if (useCost < 0) {
			BITMessages.sendNotification(sPlayer, "Cost must be >= 0");
			BITInventory.useCostGUI.get(id).setText("0");
			BITInventory.popupScreen.get(id).setDirty(true);
			return false;
		}

		return true;
	}
}
