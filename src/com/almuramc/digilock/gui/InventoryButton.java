package com.almuramc.digilock.gui;

import java.util.UUID;

import com.almuramc.digilock.LockCore;
import com.almuramc.digilock.util.Config;
import com.almuramc.digilock.util.LockInventory;
import com.almuramc.digilock.util.Messages;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.player.SpoutPlayer;

public class InventoryButton extends GenericButton {
	public InventoryButton(String name) {
		super(name);
	}

	@Override
	public void onButtonClick(ButtonClickEvent event) {
		UUID uuid = this.getId();
		SpoutPlayer sPlayer = event.getPlayer();
		//SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
		SpoutBlock sBlock;
		sBlock = LockCore.clickedBlock.get(sPlayer.getEntityId());
		if (sBlock == null) {
			sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
		}
	}

	private boolean validateSetPincodeFields(SpoutPlayer sPlayer) {
		int entId = sPlayer.getEntityId();
		if (LockInventory.useCostGUI.get(entId).getText().equals("")) {
			LockInventory.useCostGUI.get(entId).setText("0");
			LockInventory.popupScreen.get(entId).setDirty(true);
		}

		int useCost = Integer.valueOf(LockInventory.useCostGUI.get(entId).getText());
		if (useCost > Config.DIGILOCK_USEMAXCOST) {
			Messages.sendNotification(sPlayer, "Cost must be less "
					+ Config.DIGILOCK_USEMAXCOST);
			LockInventory.useCostGUI.get(entId).setText(
					String.valueOf(Config.DIGILOCK_USEMAXCOST));
			LockInventory.popupScreen.get(entId).setDirty(true);
			return false;
		} else if (useCost < 0) {
			Messages.sendNotification(sPlayer, "Cost must be >= 0");
			LockInventory.useCostGUI.get(entId).setText("0");
			LockInventory.popupScreen.get(entId).setDirty(true);
			return false;
		}

		return true;
	}
}
