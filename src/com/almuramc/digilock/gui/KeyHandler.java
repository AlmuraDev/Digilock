package com.almuramc.digilock.gui;

import com.almuramc.digilock.Digilock;
import com.almuramc.digilock.LockCore;
import com.almuramc.digilock.util.BlockTools;
import com.almuramc.digilock.util.Messages;
import com.almuramc.digilock.util.Permissions;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

public class KeyHandler implements BindingExecutionDelegate {
	public Digilock plugin;
	public Keyboard key;

	public KeyHandler(Digilock plugin, Keyboard key) {
		this.plugin = plugin;
	}

	@Override
	public void keyPressed(KeyBindingEvent event) {
	}

	@Override
	public void keyReleased(KeyBindingEvent event) {
		SpoutPlayer sPlayer = event.getPlayer();
		ScreenType screentype = event.getScreenType();
		SpoutBlock targetblock = (SpoutBlock) sPlayer.getTargetBlock(null, 4);

		// Remove broken DigiLocks
		if (BlockTools.isLocked(targetblock) && !BlockTools.isLockable(targetblock)) {
			LockCore lock = BlockTools.loadDigiLock(targetblock);
			lock.RemoveDigiLock(sPlayer);
			sPlayer.sendMessage("Warning: You had an DigiLock on a illegal block. The DigiLock has been removed.");
		}

		// GAME_SCREEN
		else if (BlockTools.isLockable(targetblock)) {
			if (screentype == ScreenType.GAME_SCREEN) {
				if ((Permissions.hasPerm(sPlayer, "lock.create",
						Permissions.QUIET) || Permissions.hasPerm(sPlayer, "lock.admin", Permissions.QUIET))) {
					if (BlockTools.isLocked(targetblock)) {
						LockCore lock = BlockTools.loadDigiLock(targetblock);
						if (BlockTools.isDoubleDoor(targetblock)) {
							BlockTools.closeDoubleDoor(sPlayer, targetblock, 0);
						} else if (BlockTools.isDoor(targetblock)) {
							BlockTools.closeDoor(sPlayer, targetblock, 0);
						} else if (BlockTools.isTrapdoor(targetblock)) {
							BlockTools.closeTrapdoor(sPlayer, targetblock);
						}
						if (sPlayer.getName().equals(lock.getOwner())) {
							Messages.sendNotification(sPlayer, "You are the owner");
							LockCore.setPincode(sPlayer, targetblock);
						} else {
							Messages.sendNotification(sPlayer, "Locked with Digilock");
						}
					} else {
						if (sPlayer.isSpoutCraftEnabled()) {
							if (BlockTools.isDoubleDoor(targetblock)) {
								SpoutBlock leftdoor = BlockTools.getLeftDoubleDoor(targetblock);
								BlockTools.closeDoubleDoor(sPlayer, leftdoor,
										0);
								LockCore.setPincode(sPlayer, leftdoor);
							} else if (BlockTools.isDoor(targetblock)) {
								BlockTools.closeDoor(targetblock);
								LockCore.setPincode(sPlayer, targetblock);
							} else {
								LockCore.setPincode(sPlayer, targetblock);
							}
						} else {
							sPlayer.sendMessage("Install SpoutCraft or use command /dlock to create lock.");
						}
					}
				}
			}
		}
		/*
						// CUSTOM_SCREEN
						else if (screentype == ScreenType.CUSTOM_SCREEN) {
							if (keypressed.equals("KEY_ESCAPE") || keypressed.equals("KEY_E")) {
								// TODO: the lever must swing back to off, when the
								// player press ESC. Next lines does not work. :-(
								// if (BlockTools.isLever(targetblock)) {
								// if ( BlockTools.isLeverOn(targetblock)) {
								// BlockTools.leverOff(sPlayer, targetblock);
								// } else {
								// BlockTools.leverOn(sPlayer, targetblock,0);
								// }
								// sPlayer.sendMessage("setting lever to off");
								// Lever lever = (Lever) targetblock.getState().getData();
								// lever.setPowered(false);
								// }
								if (BlockTools.isLever(targetblock)) {
									Lever lever = (Lever) targetblock.getState().getData();
									// lever.setPowered(false);
									targetblock.setData((byte) (lever.getData() | 8));
								}
								sPlayer.closeActiveWindow();
								BlockTools.cleanupPopupScreen(sPlayer);

							} else if (keypressed.equals("KEY_RETURN")) {

							}
						}

						else {
							// UNHANDLED SCREENTYPE
						}
				*/
	}
	/*
		@EventHandler
		public void onKeyReleasedEvent(KeyReleasedEvent event) {
			// SpoutPlayer sPlayer = event.getPlayer();
			// Keyboard keyUp = event.getKey();
			// event.getPlayer().sendMessage(
			// "sPlayer:" + sPlayer.getName() + "Pressed key:" + keyUp);
		}

		@EventHandler
		public void onRenderDistanceChange(RenderDistanceChangeEvent event) {

		}
			   */
}
