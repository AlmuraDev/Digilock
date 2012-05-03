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
		this.key = key;
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
				if ((Permissions.hasPerm(sPlayer, "create",
						Permissions.QUIET) || Permissions.hasPerm(sPlayer, "admin", Permissions.QUIET))) {
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
	}
}
