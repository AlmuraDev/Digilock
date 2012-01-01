package dk.gabriel333.BukkitInventoryTools;

import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;

public class BITKeyboardListener extends InputListener {

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		BIT.holdingKey.put(event.getPlayer().getEntityId(), event.getKey()
				.name());
	}

	@Override
	public void onKeyReleasedEvent(KeyReleasedEvent event) {
		BIT.holdingKey.put(event.getPlayer().getEntityId(), "");
	}

}
