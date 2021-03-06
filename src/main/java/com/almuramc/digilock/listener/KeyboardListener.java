package com.almuramc.digilock.listener;

import java.util.logging.Logger;

import com.almuramc.digilock.Digilock;
import com.almuramc.digilock.LockCore;

import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.keyboard.Keyboard;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class KeyboardListener implements Listener {
	@EventHandler
	public void onKeyPressedEvent(KeyPressedEvent event) {		
		if (event.getKey().equals(Keyboard.MOUSE_LEFT) || event.getKey().equals(Keyboard.MOUSE_RIGHT)) {			
			return;
		}
		Digilock.holdingKey.put(event.getPlayer().getEntityId(), event.getKey());	
	}

	@EventHandler
	public void onKeyReleasedEvent(KeyReleasedEvent event) {		
		if (event.getKey().equals(Keyboard.MOUSE_LEFT) || event.getKey().equals(Keyboard.MOUSE_RIGHT)) {			
			return;
		}
		Digilock.holdingKey.put(event.getPlayer().getEntityId(), Keyboard.CHAR_NONE);
	}
}
