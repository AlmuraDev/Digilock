package dk.gabriel333.digilock;

import java.util.HashMap;
import java.util.Map;

import com.garbagemule.MobArena.MobArenaHandler;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailAPI;
import com.tommytony.war.War;

import dk.gabriel333.digilock.command.DigilockCommand;
import dk.gabriel333.digilock.command.LockCommand;
import dk.gabriel333.digilock.gui.KeyHandler;
import dk.gabriel333.digilock.listener.BlockListener;
import dk.gabriel333.digilock.listener.PlayerListener;
import dk.gabriel333.digilock.util.Config;
import dk.gabriel333.digilock.util.LockPlugin;
import dk.gabriel333.digilock.util.Messages;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.keyboard.KeyBindingManager;
import org.getspout.spoutapi.keyboard.Keyboard;

public class Digilock extends JavaPlugin {
	public static Digilock plugin;
	// Hook into vault
	public static Boolean useEconomy = false;
	public Economy economy = null;
	// BITBackpack
	public static MobArenaHandler mobArenaHandler;
	public static int saveTaskId;
	// Hook into WAR 1.6
	public static Boolean warIsEnabled;
	public static War war;
	public static Map<Integer, String> holdingKey = new HashMap<Integer, String>();
	public static Map<Integer, Integer> userno = new HashMap<Integer, Integer>();

	@Override
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = this.getDescription();
		LockPlugin.setupPlugin(this);
		Config.bitSetupConfig();
		registerEvents();
		addCommands();
		setupMobArena();
		setupJail();
		setupWar();
		Messages.showInfo("Digilock version " + pdfFile.getVersion() + " is enabled!");
	}

	private void setupWar() {
		Plugin warPlugin = this.getServer().getPluginManager().getPlugin("War");
		if (warPlugin != null) {
			war = new War();
			Messages.showInfo("War v." + warPlugin.getDescription().getVersion() + " detected.");
			warIsEnabled = true;
		}
	}

	@Override
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTask(saveTaskId);

		PluginDescriptionFile pdfFile = this.getDescription();
		Messages.showInfo(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
	}

	public void registerEvents() {
		// Register our events
		PluginManager pm = getServer().getPluginManager();

		// LockCore Listeners
		pm.registerEvents(new BlockListener(), this);

		// Priority.Normal, this);
		pm.registerEvents(new BlockListener(), this);
		pm.registerEvents(new PlayerListener(), this);

		// Register keys
		KeyBindingManager kbm = SpoutManager.getKeyBindingManager();
		kbm.registerBinding("Digilock.Lock", Keyboard.KEY_L, "The key to lock chests", new KeyHandler(plugin, Keyboard.KEY_L), plugin);
	}

	public void addCommands() {
		// Register commands
		getCommand("Bit").setExecutor(new DigilockCommand(this));
		getCommand("Digilock").setExecutor(new LockCommand());
	}

	private Boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			Messages.showInfo("Vault not detected, economy support will be disabled.");
			return false;
		}
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
			Messages.showInfo("Vault is detected, using it for economy.");
		} else {
			Messages.showInfo("Vault is detected, but you don't have an economy plugin.");
		}
		return (economy != null);
	}

	public static boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player) {
			return true;
		}
		return false;
	}

	public static void removeUserData(int id) {
		if (userno.containsKey(id)) {
			// lock
			holdingKey.remove(id);
			userno.remove(id);
		}
	}

	public static void addUserData(int id) {
		if (!userno.containsKey(id)) {
			// lock
			userno.put(id, new Integer(id));
			holdingKey.put(id, "");
		}
	}

	private void setupMobArena() {
		if (mobArenaHandler != null) {
			return;
		}
		Plugin mobArenaPlugin = Bukkit.getServer().getPluginManager().getPlugin("MobArena");
		if (mobArenaPlugin == null) {
			return;
		}
		mobArenaHandler = new MobArenaHandler();
		Messages.showInfo("MobArena detected.");
	}

	// long delay = 20L * 60 * saveTime;
	public static JailAPI jail;

	private void setupJail() {
		Plugin jailPlugin = getServer().getPluginManager().getPlugin("Jail");
		if (jailPlugin != null) {
			jail = ((Jail) jailPlugin).API;
			Messages.showInfo("Jail detected.");
		}
	}
}
