package dk.gabriel333.BukkitInventoryTools.DigiLock;

import org.bukkit.inventory.Inventory;
import org.bukkit.material.Lever;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.event.input.RenderDistanceChangeEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.*;

public class BITDigiLockInputListener extends InputListener {

	public BIT plugin;

	public BITDigiLockInputListener(BIT plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		SpoutPlayer sPlayer = event.getPlayer();
		ScreenType screentype = event.getScreenType();
		String keypressed = event.getKey().name();
		if (!(keypressed.equals(BITConfig.LIBRARY_LOCKKEY)
				|| keypressed.equals("KEY_ESCAPE") || keypressed
					.equals("KEY_RETURN")))
			return;
		SpoutBlock targetblock = (SpoutBlock) sPlayer.getTargetBlock(null, 4);
		// External SpoutBackpack
		
		// Remove broken DigiLocks
		if (BITDigiLock.isLocked(targetblock)
				&& !BITDigiLock.isLockable(targetblock)) {
			BITDigiLock digilock = BITDigiLock.loadDigiLock(targetblock);
			digilock.RemoveDigiLock(sPlayer);
			sPlayer.sendMessage("Warning: You had an DigiLock on a illegal block. The DigiLock has been removed.");
			sPlayer.sendMessage("Make a ticket and tell the developer how it happened on:");
			sPlayer.sendMessage("http://dev.bukkit.org/server-mods/bukkitinventorytools/tickets/");
		}

		// GAME_SCREEN
		else if (BITDigiLock.isLockable(targetblock)) {
			if (screentype == ScreenType.GAME_SCREEN) {
				if (keypressed.equals(BITConfig.LIBRARY_LOCKKEY)
						&& (BITPermissions.hasPerm(sPlayer, "digilock.create",
								BITPermissions.QUIET) || BITPermissions
								.hasPerm(sPlayer, "digilock.admin",
										BITPermissions.QUIET))) {
					if (BITDigiLock.isLocked(targetblock)) {
						BITDigiLock digilock = BITDigiLock
								.loadDigiLock(targetblock);
						if (BITDigiLock.isDoubleDoor(targetblock)) {
							BITDigiLock
									.closeDoubleDoor(sPlayer, targetblock, 0);
						} else if (BITDigiLock.isDoor(targetblock)) {
							BITDigiLock.closeDoor(sPlayer, targetblock, 0);
						} else if (BITDigiLock.isTrapdoor(targetblock)) {
							BITDigiLock.closeTrapdoor(sPlayer, targetblock);
						}
						if (sPlayer.getName().equals(digilock.getOwner())) {
							BITMessages.sendNotification(sPlayer,
									"You are the owner");
							BITDigiLock.setPincode(sPlayer, targetblock);
						} else {
							BITMessages.sendNotification(sPlayer,
									"Locked with Digilock");
						}
					} else { // TARGETBLOCK IS NOT LOCKED
						if (sPlayer.isSpoutCraftEnabled()) {
							if (BITDigiLock.isBookshelf(targetblock)) {
								if (!BITInventory
										.isBitInventoryCreated(targetblock)) {
									String coowners = "";
									String name = "";
									String owner = sPlayer.getName();
									int usecost = 0;
									Inventory inventory = SpoutManager
											.getInventoryBuilder().construct(
													BITConfig.BOOKSHELF_SIZE,
													name);
									BITInventory.saveBitInventory(sPlayer,
											targetblock, owner, name, coowners,
											inventory, usecost);
								}
							} else if (BITDigiLock.isDoubleDoor(targetblock)) {
								SpoutBlock leftdoor = BITDigiLock
										.getLeftDoubleDoor(targetblock);
								BITDigiLock.closeDoubleDoor(sPlayer, leftdoor,
										0);
								BITDigiLock.setPincode(sPlayer, leftdoor);
							} else if (BITDigiLock.isDoor(targetblock)) {
								BITDigiLock.closeDoor(targetblock);
								BITDigiLock.setPincode(sPlayer, targetblock);
							} else {
								BITDigiLock.setPincode(sPlayer, targetblock);
							}
						} else {
							sPlayer.sendMessage("Install SpoutCraft or use command /dlock to create lock.");
						}

					}
				}
			}
		}

		// CUSTOM_SCREEN
		else if (screentype == ScreenType.CUSTOM_SCREEN) {
			if (keypressed.equals("KEY_ESCAPE") || keypressed.equals("KEY_E")) {
				// TODO: the lever must swing back to off, when the
				// player press ESC. Next lines does not work. :-(
				// if (BITDigiLock.isLever(targetblock)) {
				// if ( BITDigiLock.isLeverOn(targetblock)) {
				// BITDigiLock.leverOff(sPlayer, targetblock);
				// } else {
				// BITDigiLock.leverOn(sPlayer, targetblock,0);
				// }
				// sPlayer.sendMessage("setting lever to off");
				// Lever lever = (Lever) targetblock.getState().getData();
				// lever.setPowered(false);
				// }
				if (BITDigiLock.isLever(targetblock)) {
					Lever lever = (Lever) targetblock.getState().getData();
					// lever.setPowered(false);
					targetblock.setData((byte) (lever.getData() | 8));
				}
				sPlayer.closeActiveWindow();
				BITDigiLock.cleanupPopupScreen(sPlayer);

			} else if (keypressed.equals("KEY_RETURN")) {

			}
		}

		else {
			// UNHANDLED SCREENTYPE
		}

	}

	@Override
	public void onKeyReleasedEvent(KeyReleasedEvent event) {
		// SpoutPlayer sPlayer = event.getPlayer();
		// Keyboard keyUp = event.getKey();
		// event.getPlayer().sendMessage(
		// "sPlayer:" + sPlayer.getName() + "Pressed key:" + keyUp);
	}

	@Override
	public void onRenderDistanceChange(RenderDistanceChangeEvent event) {

	}

}
