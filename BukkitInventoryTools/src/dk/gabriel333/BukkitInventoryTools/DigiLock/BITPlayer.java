package dk.gabriel333.BukkitInventoryTools.DigiLock;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;


public class BITPlayer {
	
	private BIT plugin;
	
	public BITPlayer(){
		super();
	}
	
	public BITPlayer(Plugin plugin) {
		plugin = this.plugin;
	}

	protected SpoutPlayer sPlayer;

	/**
	 * 
	 * Constructs a new BITPlayer
	 * 
	 */
	public BITPlayer(SpoutPlayer sPlayer) {
		this.sPlayer = sPlayer;
	}

	/**
	 * @return the SpoutPlayer
	 * 
	 */
	public void getPlayer(SpoutPlayer sPlayer) {
		this.sPlayer = sPlayer;
	}

	/**
	 * @return the SpoutPlayers name
	 * 
	 */
	public String getName() {
		return sPlayer.getName();
	}



}
