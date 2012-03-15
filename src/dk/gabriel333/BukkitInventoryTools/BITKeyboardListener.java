package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;

public class BITKeyboardListener implements Listener {

    @EventHandler
    public void onKeyPressedEvent(KeyPressedEvent event) {
        BIT.holdingKey.put(event.getPlayer().getEntityId(), event.getKey()
                           .name());
    }

    @EventHandler
    public void onKeyReleasedEvent(KeyReleasedEvent event) {
        BIT.holdingKey.put(event.getPlayer().getEntityId(), "");
    }

}
