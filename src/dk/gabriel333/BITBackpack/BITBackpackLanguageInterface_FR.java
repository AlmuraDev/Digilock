package dk.gabriel333.BITBackpack;

public class BITBackpackLanguageInterface_FR {

	public String getString(String string) {
		return parseString(string);
	}

	private String parseString(String string) {
		if (string.equalsIgnoreCase("inventoriesloaded")) { return " Inventaires chargés."; }
		if (string.equalsIgnoreCase("permissionsfound")) { return " Permissions détecté."; }
		if (string.equalsIgnoreCase("permissionsbukkitfound")) { return " PermissionsBukkit détecté."; }
		if (string.equalsIgnoreCase("permissionsexfound")) { return " PermissionsEx détecté."; }
		if (string.equalsIgnoreCase("groupmanagerfound")) { return " GroupManager détecté."; }
		if (string.equalsIgnoreCase("opsystemfound")) { return " Système OP détecté."; }
		if (string.equalsIgnoreCase("mobarenafound")) { return " MobArena détecté."; }
		if (string.equalsIgnoreCase("jailfound")) { return " Jail détecté."; }
		if (string.equalsIgnoreCase("configreloaded1")) { return "Configuration rechargée."; }
		if (string.equalsIgnoreCase("configreloaded2")) { return "Si vous voulez recharger les permissions, etc."; }
		if (string.equalsIgnoreCase("configreloaded3")) { return "faites un rechargement global (/reload)."; }
		if (string.equalsIgnoreCase("youvegotthebiggest")) { return "Vous avez le plus gros "; }
		if (string.equalsIgnoreCase("!")) { return " !"; }
		if (string.equalsIgnoreCase("your")) { return "Votre "; }
		if (string.equalsIgnoreCase("has")) { return " a "; }
		if (string.equalsIgnoreCase("slots")) { return " places."; }
		if (string.equalsIgnoreCase("nextupgradecost")) { return "La prochaine amélioration coûte "; }
		if (string.equalsIgnoreCase("foryourpermissions")) { return " pour vos permissions !"; }
		if (string.equalsIgnoreCase("hasbeencleared")) { return " a été vidé !"; }
		if (string.equalsIgnoreCase("youdonthavearegistred")) { return "Vous n'avez pas de "; }
		if (string.equalsIgnoreCase("usingpermissions")) { return "Vous utilisez Permissions."; }
		if (string.equalsIgnoreCase("usingpermissionsbukkit")) { return "Vous utilisez PermissionsBukkit."; }
		if (string.equalsIgnoreCase("usingpermissionsex")) { return "Vous utilisez PermissionsEx."; }
		if (string.equalsIgnoreCase("usinggroupmanager")) { return "Vous utilisez GroupManager."; }
		if (string.equalsIgnoreCase("usingeconomy")) { return "Système économique détecté."; }
		if (string.equalsIgnoreCase("yourpermissionsgiveyoua")) { return "Vos permissions vous autorisent à avoir "; }
		if (string.equalsIgnoreCase("slotsbis")) { return " places dans votre "; }
		if (string.equalsIgnoreCase("yourpersonalfilegivesyoua")) { return "Votre fichier personel vous autorise à avoir "; }
		if (string.equalsIgnoreCase("yourpermissionsallowyoutoupgradetoa")) { return "Vos permissions vous autorisent à améliorer jusqu'à avoir "; }
		if (string.equalsIgnoreCase("playerhasgotthebiggest")) { return "Le joueur a le plus gros "; }
		if (string.equalsIgnoreCase("players")) { return "Le "; }
		if (string.equalsIgnoreCase("hasbis")) { return "du joueur a "; }
		if (string.equalsIgnoreCase("playernotfound")) { return "Joueur introuvable !"; }
		if (string.equalsIgnoreCase("forhispermissions")) { return " pour ses permissions !"; }
		if (string.equalsIgnoreCase("frenchonly")) { return "Le sac à dos de "; }
		if (string.equalsIgnoreCase("'s")) { return ""; }
		if (string.equalsIgnoreCase("playerhasalreadyhis")) { return "Le joueur a déjà son "; }
		if (string.equalsIgnoreCase("opened")) { return " d'ouvert !"; }
		if (string.equalsIgnoreCase("notenoughmoneyyour")) { return "Vous n'avez pas asseez d'argent pour améliorer votre "; }
		if (string.equalsIgnoreCase("notenoughmoneyplayer")) { return "Vous n'avez pas asseez d'argent pour améliorer son "; }
		if (string.equalsIgnoreCase("noaccount")) { return "Erreur, vous n'avez pas de compte !"; }
		if (string.equalsIgnoreCase("hasbeenupgraded")) { return " a été amélioré."; }
		if (string.equalsIgnoreCase("ithasnow")) { return "Il a maintenant "; }
		if (string.equalsIgnoreCase("reloadcommand")) { return "/backpack reload : Recharger la configuration."; }
		if (string.equalsIgnoreCase("infocommand")) { return "/backpack info : Afficher des informations sur votre "; }
		if (string.equalsIgnoreCase("upgradecommand")) { return "/backpack upgrade : Améliorer votre "; }
		if (string.equalsIgnoreCase("savingallinventories")) { return " Sauvegarde de tous les inventaires."; }
		if (string.equalsIgnoreCase("isnowdisabled")) { return " Est maintenant désactivé."; }
		if (string.equalsIgnoreCase("paymentmethodwasdisabled")) { return " Méthode de paiement désactivée."; }
		if (string.equalsIgnoreCase("paymentmethodfound")) { return " Méthode de paiement détectée ("; }
		if (string.equalsIgnoreCase("savinginventories")) { return " Sauvegardes des inventaires !"; }
		if (string.equalsIgnoreCase("yourenotallowedtomovethis")) { return "Vous n'êtes pas autorisé à mettre ça dans votre "; }
		if (string.equalsIgnoreCase("money")) { return "Argent : "; }
		if (string.equalsIgnoreCase("hasbroken")) { return " est cassé !"; }
		if (string.equalsIgnoreCase("someoneisusingyour")) { return "Quelqu'un utilise votre "; }
		if (string.equalsIgnoreCase("youalreadyhaveaccesstotheworkbench")) { return "Vous avez déjà accès à la table de crafting !"; }
		return "";
	}
}