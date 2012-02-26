package dk.gabriel333.BukkitInventoryTools.DigiLock;

import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;
import dk.gabriel333.Library.BITPermissions;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BITDigiLockSpoutButton extends GenericButton {
    
    
    public BITDigiLockSpoutButton(String name) {
        super(name);
    }
    
        @Override
	public void onButtonClick(ButtonClickEvent event) {
		if (event instanceof ButtonClickEvent) {
			UUID uuid = this.getId();
			SpoutPlayer sPlayer = event.getPlayer();
			int entId = sPlayer.getEntityId();
			SpoutBlock sBlock;
			sBlock = BITDigiLock.clickedBlock.get(entId);
			if (sBlock == null) {
				sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 4);
			}
			if (BITDigiLock.isLockable(sBlock)) {
				BITDigiLock digilock = BITDigiLock.loadDigiLock(sBlock);
				// ************************************
				// Buttons in getPincodeWindow
				// ************************************
                                String buttonName = BITDigiLock.BITDigiLockButtons.get(uuid);
				if (buttonName.equals("getPincodeUnlock")) {
					BITDigiLock.popupScreen.get(entId).close();
					BITDigiLock.cleanupPopupScreen(sPlayer);
					if ((digilock.getPincode().equals(
							BITDigiLock.pincodeGUI.get(entId).getText()) && BITPermissions
							.hasPerm(sPlayer, "digilock.use",
									BITPermissions.QUIET))
							|| BITPermissions.hasPerm(sPlayer,
									"digilock.admin", BITPermissions.QUIET)) {
						if (BITDigiLock.isChest(digilock.getBlock())) {
							SpoutChest sChest = (SpoutChest) sBlock.getState();
							Inventory inv = sChest.getLargestInventory();
							sPlayer.openInventoryWindow(inv);

						} else if (BITDigiLock
								.isDoubleDoor(digilock.getBlock())) {
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							BITDigiLock.openDoubleDoor(sPlayer,
									digilock.getBlock(), digilock.getUseCost());

						} else if (BITDigiLock.isDoor(digilock.getBlock())) {
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							BITDigiLock.openDoor(sPlayer, digilock.getBlock(),
									digilock.getUseCost());

						} else if (BITDigiLock.isTrapdoor(sBlock)) {
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							BITDigiLock.openTrapdoor(sPlayer,
									digilock.getBlock(), digilock.getUseCost());

						} else if (BITDigiLock.isFenceGate(sBlock)) {
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							BITDigiLock.openFenceGate(sPlayer,
									digilock.getBlock(), digilock.getUseCost());

						} else if (BITDigiLock.isLever(sBlock)) {
							BITDigiLock.leverOn(sPlayer, sBlock,
									digilock.getUseCost());
							BITDigiLock.playDigiLockSound(sBlock);

						} else if (BITDigiLock.isButton(sBlock)) {
							if (!BITDigiLock.isButtonOn(digilock.getBlock())) {
								BITDigiLock.pressButtonOn(sPlayer,
										digilock.getBlock(),
										digilock.getUseCost());
								BITDigiLock.playDigiLockSound(sBlock);
							}
						} else if (BITDigiLock.isDispenser(sBlock)) {
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							Dispenser dispenser = (Dispenser) sBlock.getState();
							Inventory inv = dispenser.getInventory();
							sPlayer.openInventoryWindow(inv);

						} else if (digilock.getBlock().getType() == Material.FURNACE) {
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							Furnace furnace = (Furnace) sBlock.getState();
							Inventory inv = furnace.getInventory();
							sPlayer.openInventoryWindow(inv);

						} else if (digilock.getBlock().getType() == Material.BOOKSHELF) {
							BITInventory bitInventory = BITInventory
									.loadBitInventory(sPlayer, sBlock);
							bitInventory
									.openBitInventory(sPlayer, bitInventory);

						} else if (BITDigiLock.isSign(sBlock)) {
							if (sPlayer.isSpoutCraftEnabled()
									&& BITConfig.LIBRARY_USESIGNEDITGUI
							        && BITPermissions.hasPerm(sPlayer, "digilock.signadmin",
									BITPermissions.NOT_QUIET)) {
								Sign sign = (Sign) sBlock.getState();
								sPlayer.openSignEditGUI(sign);
							} else {

							}

						} else if (BITDigiLock.isJukebox(sBlock)) {
							ItemStack is = sPlayer.getItemInHand();
							Jukebox jukebox = (Jukebox) sBlock.getState();
							if (jukebox.isPlaying()) {
								jukebox.eject();
							} else {
								jukebox.setPlaying(is.getType());
								sPlayer.setItemInHand(null);
							}
							
						} else if (digilock.getBlock().getType() == Material.BREWING_STAND) {
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							//BlockBrewingStand bs = (BlockBrewingStand) digilock.getBlock();
							sPlayer.sendMessage("Locking brewing stand with pincode is not suported yet!");
							//TODO: open brewing stand / inventory.
						}
						
					} else {
						BITMessages.sendNotification(sPlayer, "Wrong pincode!");
						if (BITDigiLock.isDoubleDoor(digilock.getBlock())) {
							BITDigiLock.closeDoubleDoor(sPlayer,
									digilock.getBlock(), 0);
						} else if (BITDigiLock.isDoor(digilock.getBlock())) {
							BITDigiLock.closeDoor(sPlayer, digilock.getBlock(),
									0);
						} else if (BITDigiLock.isTrapdoor(digilock.getBlock())) {
							BITDigiLock.closeTrapdoor(sPlayer,
									digilock.getBlock());
						} else if (BITDigiLock.isChest(sBlock)
								|| BITDigiLock.isDispenser(sBlock)
								|| sBlock.getType() == Material.FURNACE) {
							sPlayer.closeActiveWindow();
							BITDigiLock.cleanupPopupScreen(sPlayer);
							BITDigiLock.BITDigiLockButtons.remove(uuid);
						} else if (BITDigiLock.isLever(sBlock)) {
							BITDigiLock.leverOff(sPlayer, sBlock);
						}
						sPlayer.damage(5);
					}
					BITDigiLock.cleanupPopupScreen(sPlayer);
					BITDigiLock.BITDigiLockButtons.remove(uuid);

				} else if (buttonName.equals("getPincodeCancel")) {
					sPlayer.closeActiveWindow();
					BITDigiLock.cleanupPopupScreen(sPlayer);
					BITDigiLock.BITDigiLockButtons.remove(uuid);
				}

				// ************************************
				// Buttons in sPlayer.setPincode
				// ************************************
				else if (buttonName.equals("setPincodeLock")
						&& BITPermissions.hasPerm(sPlayer, "digilock.create",
								BITPermissions.QUIET)) {
					if (validateSetPincodeFields(sPlayer)) {
						sPlayer.closeActiveWindow();
						String digiString2 = BITDigiLock.closetimerGUI.get(entId).getText();
						digiString2 = digiString2.replaceAll("[^0-9]+", "");
						
						String digiString3 = BITDigiLock.closetimerGUI.get(entId).getText();
						digiString3 = digiString3.replaceAll("[^0-9]+", "");
						
						BITDigiLock.SaveDigiLock(sPlayer, sBlock,
								BITDigiLock.pincodeGUI.get(entId).getText(),
								BITDigiLock.ownerGUI.get(entId).getText(), 
								Integer.valueOf(digiString2),
								BITDigiLock.coOwnersGUI.get(entId).getText(),
								BITDigiLock.usersGUI.get(entId).getText(), sBlock
										.getTypeId(), "", 
										Integer.valueOf(digiString3));
						BITDigiLock.cleanupPopupScreen(sPlayer);
						BITDigiLock.BITDigiLockButtons.remove(uuid);
					}

				} else if (buttonName.equals("setPincodeCancel")) {
					sPlayer.closeActiveWindow();
					BITDigiLock.cleanupPopupScreen(sPlayer);
					BITDigiLock.BITDigiLockButtons.remove(uuid);

				} else if (buttonName.equals("setPincodeRemove")) {
					sPlayer.closeActiveWindow();
					BITDigiLock.cleanupPopupScreen(sPlayer);
					BITDigiLock.BITDigiLockButtons.remove(uuid);

					if (BITDigiLock.isLocked(sBlock)) {
						digilock.RemoveDigiLock(sPlayer);
					}
					
					// Dockter 12/27/11 to add AdminOpen Button to User Interface.
				} else if (buttonName.equals("AdminOpen")) {
					BITDigiLock.popupScreen.get(entId).close();
					BITDigiLock.cleanupPopupScreen(sPlayer);
					BITDigiLock.BITDigiLockButtons.remove(uuid);
					if (BITDigiLock.isLocked(sBlock)) {
						if (BITDigiLock.isChest(digilock.getBlock())) {
							SpoutChest sChest = (SpoutChest) sBlock.getState();
							Inventory inv = sChest.getLargestInventory();
							sPlayer.openInventoryWindow(inv);
							BITDigiLock.playDigiLockSound(sBlock);
						}
					}
					

				} else if (buttonName.equals("OwnerButton")) {
					if (validateSetPincodeFields(sPlayer)) {
					}

				} else if (buttonName.equals("CoOwnerButton")) {
					if (validateSetPincodeFields(sPlayer)) {
					}

				} else if (buttonName.equals("usersButton")) {
					if (validateSetPincodeFields(sPlayer)) {
					}

				} else if (buttonName.equals("UseCostButton")) {
					if (validateSetPincodeFields(sPlayer)) {
					}

				} else if (buttonName.equals("ClosetimerButton")) {
					if (validateSetPincodeFields(sPlayer)) {
					}

				}

				// ************************************
				// This only happens if I have forgot to handle a button
				// ************************************
				else {
					if (BITConfig.DEBUG_GUI)
						sPlayer.sendMessage("BITDigiLockListener: Unknow button:"
								+ buttonName);
				}
			}
		}
	}

	private boolean validateSetPincodeFields(SpoutPlayer sPlayer) {
		int entId = sPlayer.getEntityId();
		if (BITDigiLock.closetimerGUI.get(entId).getText().equals("")) {
			BITDigiLock.closetimerGUI.get(entId).setText("0");
			BITDigiLock.popupScreen.get(entId).setDirty(true);
		}
		if (BITDigiLock.useCostGUI.get(entId).getText().equals("")) {
			BITDigiLock.useCostGUI.get(entId).setText("0");
			BITDigiLock.popupScreen.get(entId).setDirty(true);
		}
		
		String digiString = BITDigiLock.closetimerGUI.get(entId).getText();
		digiString = digiString.replaceAll("[^0-9]+", "");
		 
		String digiString1 = BITDigiLock.useCostGUI.get(entId).getText();
		digiString1 = digiString1.replaceAll("[^0-9]+", "");
		
		//int closetimer = Integer.valueOf(BITDigiLock.closetimerGUI.get(id).getText());
		int closetimer = Integer.valueOf(digiString);
		int useCost = Integer.valueOf(digiString1);
		
		//int useCost = Integer.valueOf(BITDigiLock.useCostGUI.get(id).getText());
		
		if (closetimer < 0) {
			BITMessages.sendNotification(sPlayer, "Closetimer must be > 0");
			BITDigiLock.closetimerGUI.get(entId).setText("0");
			BITDigiLock.popupScreen.get(entId).setDirty(true);
			return false;
		} else if (closetimer > 3600) {
			BITMessages.sendNotification(sPlayer, "Closetim. must be<3600");
			BITDigiLock.closetimerGUI.get(entId).setText("3600");
			BITDigiLock.popupScreen.get(entId).setDirty(true);
			return false;
		} else if (useCost > BITConfig.DIGILOCK_USEMAXCOST) {
			BITMessages.sendNotification(sPlayer, "Cost must be less "
					+ BITConfig.DIGILOCK_USEMAXCOST);
			BITDigiLock.useCostGUI.get(entId).setText(
					String.valueOf(BITConfig.DIGILOCK_USEMAXCOST));
			BITDigiLock.popupScreen.get(entId).setDirty(true);
			return false;
		} else if (useCost < 0) {
			BITMessages.sendNotification(sPlayer, "Cost must be >= 0");
			BITDigiLock.useCostGUI.get(entId).setText("0");
			BITDigiLock.popupScreen.get(entId).setDirty(true);
			return false;
		}

		return true;
	}
}
