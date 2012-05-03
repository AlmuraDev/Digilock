package com.almuramc.digilock.listener;

import com.almuramc.digilock.Digilock;
import com.almuramc.digilock.LockCore;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;

public class KeyboardListener implements Listener {
	@EventHandler
	public void onKeyPressedEvent(KeyPressedEvent event) {
		Digilock.holdingKey.put(event.getPlayer().getEntityId(), event.getKey()
				.name());
	}

	@EventHandler
	public void onKeyReleasedEvent(KeyReleasedEvent event) {
		Digilock.holdingKey.put(event.getPlayer().getEntityId(), "");
	}
}
