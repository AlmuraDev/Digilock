package dk.gabriel333.BITBackpack;

public class BITBackpackLanguageInterface_EN {

	public String getString(String string) {
		return parseString(string);
	}

	private String parseString(String string) {
		if (string.equalsIgnoreCase("inventoriesloaded")) { return " Inventories loaded."; }
		if (string.equalsIgnoreCase("permissionsfound")) { return " Permissions found, will use it."; }
		if (string.equalsIgnoreCase("permissionsbukkitfound")) { return " PermissionsBukkit found, will use it."; }
		if (string.equalsIgnoreCase("permissionsexfound")) { return " PermissionsEx found, will use it."; }
		if (string.equalsIgnoreCase("groupmanagerfound")) { return " GroupManager found, will use it."; }
		if (string.equalsIgnoreCase("opsystemfound")) { return " OP system detected, will use it."; }
		if (string.equalsIgnoreCase("mobarenafound")) { return " MobArena found, will use it."; }
		if (string.equalsIgnoreCase("jailfound")) { return " Jail found, will use it."; }
		if (string.equalsIgnoreCase("configreloaded1")) { return "Configuration reloaded."; }
		if (string.equalsIgnoreCase("configreloaded2")) { return "If you want to reload permissions, etc."; }
		if (string.equalsIgnoreCase("configreloaded3")) { return "please do a global reload (/reload)."; }
		if (string.equalsIgnoreCase("youvegotthebiggest")) { return "You've got the biggest "; }
		if (string.equalsIgnoreCase("!")) { return "!"; }
		if (string.equalsIgnoreCase("your")) { return "Your "; }
		if (string.equalsIgnoreCase("has")) { return " has "; }
		if (string.equalsIgnoreCase("slots")) { return " slots."; }
		if (string.equalsIgnoreCase("nextupgradecost")) { return "Next upgrade cost "; }
		if (string.equalsIgnoreCase("foryourpermissions")) { return " for your permissions!"; }
		if (string.equalsIgnoreCase("hasbeencleared")) { return " has been cleared!"; }
		if (string.equalsIgnoreCase("youdonthavearegistred")) { return "You don't have a registred "; }
		if (string.equalsIgnoreCase("usingpermissions")) { return "You're are using Permissions."; }
		if (string.equalsIgnoreCase("usingpermissionsbukkit")) { return "You're are using PermissionsBukkit."; }
		if (string.equalsIgnoreCase("usingpermissionsex")) { return "You're are using PermissionsEx."; }
		if (string.equalsIgnoreCase("usinggroupmanager")) { return "You're are using GroupManager."; }
		if (string.equalsIgnoreCase("usingeconomy")) { return "Economy system detected."; }
		if (string.equalsIgnoreCase("yourpermissionsgiveyoua")) { return "Your permissions give you a "; }
		if (string.equalsIgnoreCase("slotsbis")) { return " slots "; }
		if (string.equalsIgnoreCase("yourpersonalfilegivesyoua")) { return "Your personal file gives you a "; }
		if (string.equalsIgnoreCase("yourpermissionsallowyoutoupgradetoa")) { return "Your permissions allow you to upgrade to a "; }
		if (string.equalsIgnoreCase("playerhasgotthebiggest")) { return "Player has got the biggest "; }
		if (string.equalsIgnoreCase("players")) { return "Player's "; }
		if (string.equalsIgnoreCase("hasbis")) { return " has "; }
		if (string.equalsIgnoreCase("playernotfound")) { return "Player not found!"; }
		if (string.equalsIgnoreCase("forhispermissions")) { return " for his permissions!"; }
		if (string.equalsIgnoreCase("'s")) { return "'s "; }
		if (string.equalsIgnoreCase("playerhasalreadyhis")) { return "Player has already his "; }
		if (string.equalsIgnoreCase("opened")) { return " open!"; }
		if (string.equalsIgnoreCase("notenoughmoneyyour")) { return "You don't have enough money to upgrade your "; }
		if (string.equalsIgnoreCase("notenoughmoneyplayer")) { return "You don't have enough money to upgrade player's "; }
		if (string.equalsIgnoreCase("noaccount")) { return "Error, you don't have any account!"; }
		if (string.equalsIgnoreCase("hasbeenupgraded")) { return " has been upgraded."; }
		if (string.equalsIgnoreCase("ithasnow")) { return "It has now "; }
		if (string.equalsIgnoreCase("reloadcommand")) { return "/backpack reload : Reload the configuration."; }
		if (string.equalsIgnoreCase("infocommand")) { return "/backpack info : Show info about your "; }
		if (string.equalsIgnoreCase("upgradecommand")) { return "/backpack upgrade : Upgrade your "; }
		if (string.equalsIgnoreCase("savingallinventories")) { return " Saving all inventories."; }
		if (string.equalsIgnoreCase("isnowdisabled")) { return " Is now disabled."; }
		if (string.equalsIgnoreCase("paymentmethodwasdisabled")) { return " Payment method was disabled. No longer accepting payments."; }
		if (string.equalsIgnoreCase("paymentmethodfound")) { return " Payment method found ("; }
		if (string.equalsIgnoreCase("savinginventories")) { return " Saving inventories!"; }
		if (string.equalsIgnoreCase("yourenotallowedtomovethis")) { return "You aren't allowed to move this into your "; }
		if (string.equalsIgnoreCase("money")) { return "Money: "; }
		if (string.equalsIgnoreCase("hasbroken")) { return " has broken and your items was dropped!"; }
		if (string.equalsIgnoreCase("someoneisusingyour")) { return "Someone is using your "; }
		if (string.equalsIgnoreCase("youalreadyhaveaccesstotheworkbench")) { return "You already have access to the workench!"; }
		return "";
	}
}