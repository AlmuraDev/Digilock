package com.almuramc.digilock.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.almuramc.digilock.Digilock;
import com.almuramc.digilock.LockCore;
import com.almuramc.digilock.util.BlockTools;
import com.almuramc.digilock.util.LockInventory;
import com.almuramc.digilock.util.Messages;
import com.almuramc.digilock.util.Permissions;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.player.SpoutPlayer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LockButton extends GenericButton {
	public LockButton(String name) {
		super(name);
	}			
	@Override
	public void onButtonClick(ButtonClickEvent event) {
		if (event instanceof ButtonClickEvent) {
			UUID uuid = this.getId();
			SpoutPlayer sPlayer = event.getPlayer();			
			int entId = sPlayer.getEntityId();
			//SpoutBlock sBlock;
			Block sBlock;
			sBlock = LockCore.clickedBlock.get(entId);
			if (sBlock == null) {
				sBlock = (Block) sPlayer.getTargetBlock(null, 4);
			}
			if (BlockTools.isLockable(sBlock)) {
				LockCore lock = BlockTools.loadDigiLock(sBlock);
				// ************************************
				// Buttons in getPincodeWindow
				// ************************************
				String buttonName = LockCore.BITDigiLockButtons.get(uuid);
				if (buttonName.equals("getPincodeUnlock")) {			
					LockCore.cleanupPopupScreen(sPlayer);
					if ((lock.getPincode().equals(
							LockCore.pincodeGUI.get(entId).getText()) && Permissions
							.hasPerm(sPlayer, "use",
									Permissions.QUIET))
							|| Permissions.hasPerm(sPlayer,
							"admin", Permissions.QUIET)) {
						if (BlockTools.isChest(lock.getBlock())) {
							Chest sChest = (Chest) sBlock.getState();
							Inventory inv = sChest.getInventory();							
							sPlayer.openInventory(inv);
							
						} else if (BlockTools.isDoubleDoor(lock.getBlock())) {
							BlockTools.playDigiLockSound(lock.getBlock());
							BlockTools.changeDoorStates(true, sPlayer, lock.getUseCost(), sBlock, BlockTools.getDoubleDoor(sBlock));
						} else if (BlockTools.isDoor(lock.getBlock())) {
							BlockTools.playDigiLockSound(lock.getBlock());
							BlockTools.openDoor(sPlayer, lock.getBlock(),
									lock.getUseCost());
						} else if (BlockTools.isTrapdoor(sBlock)) {
							BlockTools.playDigiLockSound(lock.getBlock());
							BlockTools.openTrapdoor(sPlayer,
									lock.getBlock(), lock.getUseCost());
						} else if (BlockTools.isFenceGate(sBlock)) {
							BlockTools.playDigiLockSound(lock.getBlock());
							BlockTools.openFenceGate(sPlayer,
									lock.getBlock(), lock.getUseCost());
						} else if (BlockTools.isLever(sBlock)) {
							BlockTools.leverOn(sPlayer, sBlock,
									lock.getUseCost());
							BlockTools.playDigiLockSound(sBlock);
						} else if (BlockTools.isButton(sBlock)) {
							if (!BlockTools.isButtonOn(lock.getBlock())) {
								BlockTools.pressButtonOn(sPlayer,
										lock.getBlock(),
										lock.getUseCost());
								BlockTools.playDigiLockSound(sBlock);
							}
						} else if (BlockTools.isDispenser(sBlock)) {
							BlockTools.playDigiLockSound(lock.getBlock());
							Dispenser dispenser = (Dispenser) sBlock.getState();
							Inventory inv = dispenser.getInventory();
							sPlayer.openInventory(inv);
						} else if (lock.getBlock().getType() == Material.FURNACE) {
							BlockTools.playDigiLockSound(lock.getBlock());
							Furnace furnace = (Furnace) sBlock.getState();
							Inventory inv = furnace.getInventory();
							sPlayer.openInventory(inv);
						} else if (lock.getBlock().getType() == Material.BOOKSHELF) {
							LockInventory bitInventory = LockInventory
									.loadBitInventory(sPlayer, sBlock);
							bitInventory
									.openBitInventory(sPlayer, bitInventory);
						} else if (BlockTools.isSign(sBlock)) {
							if (sPlayer.isSpoutCraftEnabled()
									&& Digilock.getConf().useSignGUI()
									&& Permissions.hasPerm(sPlayer, "signedit",
									Permissions.NOT_QUIET)) {
								Sign sign = (Sign) sBlock.getState();

								if (Digilock.getHooks().isResidencyAvailable()) {
									ClaimedResidence res = Residence.getResidenceManager().getByLoc(sBlock.getLocation());
									boolean canBuild = true;
									if (res != null) {
										canBuild = res.getPermissions().playerHas(sPlayer.getName(), "build", true);
									}
									if (canBuild) {
										sPlayer.openSignEditGUI(sign);
									} else {
										sPlayer.sendMessage("Residence is currently restricting your Sign Editing Abilities.");
									}
								} else {
									sPlayer.openSignEditGUI(sign);
								}
							} else {

							}
						} else if (BlockTools.isJukebox(sBlock)) {
							ItemStack is = sPlayer.getItemInHand();
							Jukebox jukebox = (Jukebox) sBlock.getState();
							if (jukebox.isPlaying()) {
								jukebox.eject();
							} else {
								jukebox.setPlaying(is.getType());
								sPlayer.setItemInHand(null);
							}
						} else if (lock.getBlock().getType() == Material.BREWING_STAND) {
							BlockTools.playDigiLockSound(lock.getBlock());
							//BlockBrewingStand bs = (BlockBrewingStand) lock.getBlock();
							sPlayer.sendMessage("Locking brewing stand with pincode is not suported yet!");
							//TODO: open brewing stand / inventory.
						}
					} else {
						Messages.sendNotification(sPlayer, "Wrong pincode!");
						if (BlockTools.isDoubleDoor(lock.getBlock())) {
							BlockTools.changeDoorStates(true, sPlayer, 0, sBlock, BlockTools.getDoubleDoor(sBlock));
						} else if (BlockTools.isDoor(lock.getBlock())) {
							BlockTools.closeDoor(sPlayer, lock.getBlock(),
									0);
						} else if (BlockTools.isTrapdoor(lock.getBlock())) {
							BlockTools.closeTrapdoor(sPlayer,
									lock.getBlock());
						} else if (BlockTools.isChest(sBlock)
								|| BlockTools.isDispenser(sBlock)
								|| sBlock.getType() == Material.FURNACE) {
							//sPlayer.closeActiveWindow();
							sPlayer.getMainScreen().closePopup();
							LockCore.cleanupPopupScreen(sPlayer);
							LockCore.BITDigiLockButtons.remove(uuid);
						} else if (BlockTools.isLever(sBlock)) {
							BlockTools.leverOff(sPlayer, sBlock);
						}
						sPlayer.damage(5);
					}
					LockCore.cleanupPopupScreen(sPlayer);					
					LockCore.BITDigiLockButtons.remove(uuid);
					
				} else if (buttonName.equals("getPincodeCancel")) {					
					LockCore.cleanupPopupScreen(sPlayer);
					LockCore.BITDigiLockButtons.remove(uuid);
					sPlayer.closeActiveWindow();
					
				// ************************************
				// Buttons in sPlayer.setPincode
				// ************************************
				} else if (buttonName.equals("setPincodeLock")
						&& Permissions.hasPerm(sPlayer, "create",
						Permissions.QUIET)) {
					if (validateSetPincodeFields(sPlayer)) {
						//sPlayer.closeActiveWindow();
						sPlayer.getMainScreen().closePopup();
						String digiString2 = LockCore.closetimerGUI.get(entId).getText();
						digiString2 = digiString2.replaceAll("[^0-9]+", "");

						String digiString3 = LockCore.useCostGUI.get(entId).getText();
						digiString3 = digiString3.replaceAll("[^0-9]+", "");

						LockCore.SaveDigiLock(sPlayer, sBlock,
								LockCore.pincodeGUI.get(entId).getText(),
								LockCore.ownerGUI.get(entId).getText(),
								Integer.valueOf(digiString2),
								LockCore.coOwnersGUI.get(entId).getText(),
								LockCore.usersGUI.get(entId).getText(), sBlock
								.getTypeId(), "",
								Integer.valueOf(digiString3));
						LockCore.cleanupPopupScreen(sPlayer);
						LockCore.BITDigiLockButtons.remove(uuid);						
					}
				} else if (buttonName.equals("setPincodeCancel")) {
					//sPlayer.closeActiveWindow();
					sPlayer.getMainScreen().closePopup();
					LockCore.cleanupPopupScreen(sPlayer);
					LockCore.BITDigiLockButtons.remove(uuid);
				} else if (buttonName.equals("setPincodeRemove")) {
					
					LockCore.cleanupPopupScreen(sPlayer);					
					LockCore.BITDigiLockButtons.remove(uuid);

					if (BlockTools.isLocked(sBlock)) {
						lock.RemoveDigiLock(sPlayer);
					}
					sPlayer.getMainScreen().closePopup();
					//sPlayer.closeActiveWindow();					
					// Dockter 12/27/11 to add AdminOpen Button to User Interface.
				} else if (buttonName.equals("AdminOpen")) {
					LockCore.cleanupPopupScreen(sPlayer);
					LockCore.BITDigiLockButtons.remove(uuid);
					if (BlockTools.isLocked(sBlock)) {
						if (BlockTools.isChest(lock.getBlock())) {
							if (sBlock.getState() instanceof Chest) {
								Chest sChest = (Chest) (sBlock.getState());
								Inventory inv = sChest.getInventory();
								sPlayer.openInventory(inv);
								BlockTools.playDigiLockSound(sBlock);								
													
							}
						}
					}
					//LockCore.removeUserData(sPlayer.getEntityId());
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
				}
			}			
		}
	}

	private boolean validateSetPincodeFields(SpoutPlayer sPlayer) {
		int entId = sPlayer.getEntityId();
		if (LockCore.closetimerGUI.get(entId).getText().equals("")) {
			LockCore.closetimerGUI.get(entId).setText("0");
			LockCore.popupScreen.get(entId).setDirty(true);
		}
		if (LockCore.useCostGUI.get(entId).getText().equals("")) {
			LockCore.useCostGUI.get(entId).setText("0");
			LockCore.popupScreen.get(entId).setDirty(true);
		}

		String digiString = LockCore.closetimerGUI.get(entId).getText();
		digiString = digiString.replaceAll("[^0-9]+", "");

		String digiString1 = LockCore.useCostGUI.get(entId).getText();
		digiString1 = digiString1.replaceAll("[^0-9]+", "");

		//int closetimer = Integer.valueOf(BlockTools.closetimerGUI.get(id).getText());
		int closetimer = Integer.valueOf(digiString);
		int useCost = Integer.valueOf(digiString1);

		//int useCost = Integer.valueOf(BlockTools.useCostGUI.get(id).getText());

		if (closetimer < 0) {
			Messages.sendNotification(sPlayer, "Closetimer must be > 0");
			LockCore.closetimerGUI.get(entId).setText("0");
			LockCore.popupScreen.get(entId).setDirty(true);
			return false;
		} else if (closetimer > 3600) {
			Messages.sendNotification(sPlayer, "Closetim. must be<3600");
			LockCore.closetimerGUI.get(entId).setText("3600");
			LockCore.popupScreen.get(entId).setDirty(true);
			return false;
		} else if (useCost > Digilock.getConf().getLockMaxCost()) {
			Messages.sendNotification(sPlayer, "Cost must be less "
					+ Digilock.getConf().getLockMaxCost());
			LockCore.useCostGUI.get(entId).setText(
					String.valueOf(Digilock.getConf().getLockMaxCost()));
			LockCore.popupScreen.get(entId).setDirty(true);
			return false;
		} else if (useCost < 0) {
			Messages.sendNotification(sPlayer, "Cost must be >= 0");
			LockCore.useCostGUI.get(entId).setText("0");
			LockCore.popupScreen.get(entId).setDirty(true);
			return false;
		}

		return true;
	}
}
