package com.almuramc.digilock.listener;

import com.almuramc.digilock.Digilock;
import com.almuramc.digilock.util.BlockTools;
import com.almuramc.digilock.util.Messages;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BlockListener implements Listener {
	@EventHandler
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		// TODO: THERE IS AN MEMORY LEAK HERE!!!
		/*
				 * SpoutBlock sBlock = (SpoutBlock) event.getBlock(); if
				 * (!BlockTools.isLockable(sBlock)) return; if
				 * (BlockTools.isLocked(sBlock)) { if (G333Config.DEBUG_EVENTS)
				 * G333Messages.showInfo("BlockRedstoneEvt:" +
				 * event.getBlock().getType() + " getOC:" + event.getOldCurrent() +
				 * " getNC:" + event.getNewCurrent()); if
				 * (BlockTools.isDoubleDoor(sBlock)) {
				 *
				 * } else if (BlockTools.isDoor(sBlock)) { Door door = (Door)
				 * sBlock.getState().getData(); if (!door.isOpen()) {
				 * event.setNewCurrent(event.getOldCurrent()); } } }
				 */
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Block b = event.getBlock();
		if (b != null && (event.getBlock() instanceof SpoutBlock)) {

			if (!BlockTools.isLockable(b)) {
				return;
			}
			SpoutBlock sBlock = (SpoutBlock) b;
			if (BlockTools.isLocked(sBlock)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if (event.isCancelled() || !(event.getBlock() instanceof SpoutBlock)) {
			return;
		}
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (!BlockTools.isLockable(block)) {
			return;
		}
		if (BlockTools.isLocked(block)) {
			if (BlockTools.isDoubleDoor(block)) {
				Messages.showInfo("Tried to break doubledoor");
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		SpoutBlock blockOnTop = sBlock.getRelative(BlockFace.UP);
		if (BlockTools.isLocked(sBlock) || BlockTools.isLocked(blockOnTop)) {
			sPlayer.damage(5);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BlockTools.isLockable(sBlock)) {
			return;
		}
		if (BlockTools.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (BlockTools.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockFade(BlockFadeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BlockTools.isLockable(sBlock)) {
			return;
		}
		if (BlockTools.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockForm(BlockFormEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BlockTools.isLockable(sBlock)) {
			return;
		}
		if (BlockTools.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BlockTools.isLockable(sBlock)) {
			return;
		}
		if (BlockTools.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BlockTools.isLockable(sBlock)) {
			return;
		}
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (BlockTools.isLocked(sBlock)) {
			sPlayer.damage(10);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BlockTools.isLockable(sBlock)) {
			return;
		}
		if (BlockTools.isLocked(sBlock)) {
			if (!Digilock.getConf().useSignGUI()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BlockTools.isLockable(sBlock)) {
			return;
		}
		if (BlockTools.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BlockTools.isLockable(sBlock)) {
			return;
		}
		if (BlockTools.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}
}
