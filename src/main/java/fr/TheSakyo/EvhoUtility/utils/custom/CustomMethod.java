package fr.TheSakyo.EvhoUtility.utils.custom;

import java.util.*;

import com.google.common.base.Preconditions;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.UserManager;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.util.StringUtil;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.*;
import org.bukkit.block.Block;

import net.luckperms.api.model.user.User;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class CustomMethod {
	
	
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
 /* PARTIE MÉTHODES PERSONNALISER */
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

	/**
	 * Prend une chaîne et la transforme en un composant ({@link Component})
	 *
	 * @param string La chaîne à convertir en un composant
	 * @return Un objet composant.
	 */
	public static Component StringToComponent(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}


	/**
	 * Prend un composant ({@link Component}) et la transforme en chaine de caractère
	 *
	 * @param component L'Objet composant de {@link Component} à convertir en chaîne de caractère
	 * @return Une chaîne de caractère.
	 */
	public static String ComponentToString(Component component) {
		return LegacyComponentSerializer.legacyAmpersand().serialize(Component.text().append(component).build());
	}



	/********************************************/
	/* PARTIE MÉTHODE(S) DÉTÉCTION DE NOMBRE(S) */
	/********************************************/


	/* Méthode pour détecter un "string" en "byte" */
	public static boolean isByte(String string) {

	  try { Byte.parseByte(string); return true; }
	  catch(NullPointerException | NumberFormatException e) { return false; }

	}
	/* Méthode pour détecter un "string" en "byte" */


	/* Méthode pour détecter un "string" en "double" */
	public static boolean isDouble(String string) {

	  try { Double.parseDouble(string); return true; }
	  catch(NullPointerException | NumberFormatException e) { return false; }

	}
	/* Méthode pour détecter un "string" en "double" */


	/* Méthode pour détecter un "string" en "float" */
	public static boolean isFloat(String string) {

	  try { Float.parseFloat(string); return true; }
	  catch(NullPointerException | NumberFormatException e) { return false; }

	}
	/* Méthode pour détecter un "string" en "float" */


	/* Méthode pour détecter un "string" en "int" */
	public static boolean isInt(String string) {

	  try { Integer.parseInt(string); return true; }
	  catch (NullPointerException | NumberFormatException e) { return false; }

	}
	/* Méthode pour détecter un "string" en "int" */


	/* Méthode pour détecter un "string" en "long" */
	public static boolean isLong(String string) {

	  try { Long.parseLong(string); return true; }
	  catch(NullPointerException | NumberFormatException e) { return false; }

	}
	/* Méthode pour détecter un "string" en "long"  */


	/* Méthode pour détecter un "string" en "short" */
	public static boolean isShort(String string) {

	  try { Short.parseShort(string); return true; }
	  catch(NullPointerException | NumberFormatException e) { return false; }

	}
	/* Méthode pour détecter un "string" en "short"  */


	/********************************************/
	/* PARTIE MÉTHODE(S) DÉTÉCTION DE NOMBRE(S) */
	/********************************************/


	/*******************************************************************/
	/* VÉRIFIE SI LE JOUEUR EST ENTRE DEUX COORDONNÉES (REGION CUBOID) */
	/******************************************************************/

	/**
	 * Renvoie vrai si l' {@link Location emplacement} donné est dans le cuboïde défini par les deux emplacements donnés
	 *
	 * @param origin L' {@link Location emplacement} que vous souhaitez vérifier s'il se trouve dans la région.
	 * @param loc1 L' {@link Location emplacement} du premier coin du cuboïde.
	 * @param loc2 L' {@link Location emplacement} du deuxième coin du cuboïde.
	 *
	 * @return Une valeur booléenne.
	 */
	public static boolean inRegionCuboid(Location origin, Location loc1, Location loc2) {

		  return new IntRange(loc1.getX(), loc2.getX()).containsDouble(origin.getX())
                && new IntRange(loc1.getY(), loc2.getY()).containsDouble(origin.getY())
                &&  new IntRange(loc1.getZ(), loc2.getZ()).containsDouble(origin.getZ());
	}

	/*******************************************************************/
	/* VÉRIFIE SI LE JOUEUR EST ENTRE DEUX COORDONNÉES (REGION CUBOID) */
	/*******************************************************************/


	/*******************************************************************/
	/* RÉCUPÈRE UNE LOCALISATION ENTRE DEUX COORDONNÉES (REGION CUBOID) */
	/*******************************************************************/

	/**
	 * Renvoie une {@link Location emplacement} random entre deux emplacements donné formant un cuboïde
	 *
	 * @param loc1 L' {@link Location emplacement} du premier coin du cuboïde.
	 * @param loc2 L' {@link Location emplacement} du deuxième coin du cuboïde.
	 *
	 * @return Une {@link Location emplacement} random.
	 */
	public static Location getRandomLocation(Location loc1, Location loc2) {

        Location range = new Location(loc1.getWorld(), Math.abs(loc2.getX() - loc1.getX()), loc1.getY(), Math.abs(loc2.getZ() - loc1.getZ()));
        return new Location(loc1.getWorld(), (Math.random() * range.getX()) + (Math.min(loc1.getX(), loc2.getX())), range.getY(), (Math.random() * range.getZ()) + (Math.min(loc1.getZ(), loc2.getZ())));
    }

	/*******************************************************************/
	/* RÉCUPÈRE UNE LOCALISATION ENTRE DEUX COORDONNÉES (REGION CUBOID) */
	/*******************************************************************/


	/******************************************************************************************/
	/* TOUTE PETITE MÉTHODE POUR VÉRIFIER SI LES COORDONNÉES PEUT ETRE VISIBLE PAR LE JOUEUR */
	/*****************************************************************************************/
    /*public static boolean locationCanBeViewByPlayer(Location loc, Player p) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		LivingEntity playerEntity = ((CraftLivingEntity)((CraftPlayer)p).getHandle().getBukkitLivingEntity()).getHandle();
        if(playerEntity.getLevel() != ((CraftWorld)loc.getWorld()).getHandle()) return false;

		Class playerEntityClass = playerEntity.getClass().getSuperclass().getSuperclass().getSuperclass(); // Récupère la class 'Entity' du joueur

		// ⬇️ Essait de remapper les méthodes 'getX()', 'getEyeY()' et 'getZ()' de la class 'LivingEntity' du Joueur ⬇️ //
		Class<?> remappedEntityPlayer = RemapReflection.remapClassName(playerEntityClass);
		String entityx = RemapReflection.remapMethodName(playerEntityClass, "dg");
		String entityEyeY = RemapReflection.remapMethodName(playerEntityClass, "dk");
		String entityz = RemapReflection.remapMethodName(playerEntityClass, "dm");
		// ⬆️ Essait de remapper les méthodes 'getX()', 'getEyeY()' et 'getZ()' de la class 'LivingEntity' du Joueur ⬆️ //

		// ⬇️ Essait de récupérer les méthodes 'getX()', 'getEyeY()' et 'getZ()' de la class 'LivingEntity' du Joueur du remmapage éffectué  ⬇️ //
		Method playerX = remappedEntityPlayer.getDeclaredMethod(entityx);
		Method playerEyeY = remappedEntityPlayer.getDeclaredMethod(entityEyeY);
		Method playerZ = remappedEntityPlayer.getDeclaredMethod(entityz);

		playerX.setAccessible(true);
		playerEyeY.setAccessible(true);
		playerZ.setAccessible(true);
		// ⬆️ Essait de récupérer les méthodes 'getX()', 'getEyeY()' et 'getZ()' de la class 'LivingEntity' du Joueur du remmapage éffectué  ⬆️ //

        Vec3 vec3d = new Vec3((double)playerX.invoke(playerEntity), (double)playerEyeY.invoke(playerEntity), (double)playerZ.invoke(playerEntity));
        Vec3 vec3d1 = new Vec3(loc.getX(), loc.getY(), loc.getZ());
        if(vec3d1.distanceToSqr(vec3d) > 128D * 128D) return false; //Retour anticipé si la distance est supérieure à 128 blocs

		return playerEntity.level.rayTraceDirect(vec3d, vec3d1, net.minecraft.world.phys.shapes.CollisionContext.of(playerEntity)) == net.minecraft.world.phys.BlockHitResult.Type.MISS;

	}*/
	/******************************************************************************************/
	/* TOUTE PETITE MÉTHODE POUR VÉRIFIER SI LES COORDONNÉES PEUT ETRE VISIBLE PAR LE JOUEUR */
	/*****************************************************************************************/


	/****************************************************/
	/* TOUTE PETITE MÉTHODE POUR AVOIR UN RAYON DE BLOC */
	/****************************************************/
	
	/**
	 * Définit tous les blocs d'un rayon de bloc précis et un matériau {@link Material}.
	 *
	 * @param start Le bloc de départ.
	 * @param radius Le rayon de la sphère.
	 * @param material Le matériau a donné aux blocs récupérés.
	 */
	public static void setTypeBlocks(Block start, int radius, Material material) {
		
		List<Block> blocks = new ArrayList<Block>();
		
	    if(radius < 0) { blocks =  new ArrayList<Block>(0); }
	    
	    else { 
	     
	      int iterations = (radius * 2) + 1;
	      blocks = new ArrayList<Block>(iterations * iterations * iterations); 
	    }

	    for(int x = -radius; x <= radius; x++) {
	    	
	        for(int y = -radius; y <= radius; y++) {
	        	
	            for(int z = -radius; z <= radius; z++) { blocks.add(start.getRelative(x, y - 3, z)); }
	        }
	    }
	    
	   for(Block block : blocks) block.setType(material);;
	}

    /****************************************************/
	/* TOUTE PETITE MÉTHODE POUR AVOIR UN RAYON DE BLOC */
	/****************************************************/

	
	/**********************************************************************************************/
	/* PARTIE MÉTHODE(S) VÉRIFICATION DEBUT OU FIN D'UNE CHAÎNE DE CARACTÈRE EN IGNORANT LA CASE */
	/*********************************************************************************************/

	/**
	 * Retourne vrai ou faux si la chaîne de caractère commence par une liste de préfix.
	 *
     * @param str Message
     * @param prefixlist Liste de Préfix
	 *
	 * @return vrai si {@code str} commence par {@code prefixlist}, sans tenir compte de la sensibilité à la casse.
     */
    public static boolean startsWithIgnoreCase(String str, List<String> prefixlist) { 
    
     for(String prefix : prefixlist) if(str.startsWith(prefix)) return str.regionMatches(true, 0, prefix, 0, prefix.length());
	 return false;
    }
    
    
	/**
	 * Retourne vrai ou faux si la chaîne de caractère se termine par une liste de suffix.
	 *
     * @param str Message
     * @param suffixlist Liste de Suffix
	 *
     * @return vrai si {@code str} fini par {@code suffixlist}, sans tenir compte de la sensibilité à la casse.
     */
    public static boolean endsWithIgnoreCase(String str, List<String> suffixlist) { 

		for(String suffix : suffixlist) {

			int suffixLength = suffix.length();
			if(str.endsWith(suffix)) str.regionMatches(true, str.length() - suffixLength, suffix, 0, suffixLength);
		}
		return false;
   }

   /**********************************************************************************************/
   /* PARTIE MÉTHODE(S) VÉRIFICATION DEBUT OU FIN D'UNE CHAÎNE DE CARACTÈRE EN IGNORANT LA CASE */
   /*********************************************************************************************/



	/************************************************************************************************/
	/* PARTIE MÉTHODE(S) VÉRIFICATION DE CARACTÈRE DANS UNE CHAÎNE DE CARACTÈRE EN IGNORANT LA CASE */
	/************************************************************************************************/

	/**
	 * Retourne vrai ou faux si la chaîne de caractère contient la chaîne de caractère demandée.
	 *
	 * @param str Message
	 * @param searchStr La chaîne de caractère a cherché
	 *
	 * @return vrai si {@code str} contient {@code searchStr}, sans tenir compte de la sensibilité à la casse.
	 */
	public static boolean containsIgnoreCase(String str, String searchStr) {

		if(str == null || searchStr == null) return false;

		int length = searchStr.length();
		if(length == 0) return true;

		for(int i = str.length() - length; i >= 0; i--) { if(str.regionMatches(true, i, searchStr, 0, length)) return true; }
		return false;
	}

	/**
	 * Retourne vrai ou faux si plusieurs chaînes de caractères contiennnent la chaîne de caractère demandée.
	 *
	 * @param strings Message
	 * @param searchStr La chaîne de caractère a cherché
	 *
	 * @return vrai si {@code strings} contiennent {@code searchStr}, sans tenir compte de la sensibilité à la casse.
	 */
	public static boolean containsIgnoreCase(List<String> strings, String searchStr) {

		for(String string : strings) { if(containsIgnoreCase(string, searchStr)) return true; }
		return false;
	}

	/************************************************************************************************/
	/* PARTIE MÉTHODE(S) VÉRIFICATION DE CARACTÈRE DANS UNE CHAÎNE DE CARACTÈRE EN IGNORANT LA CASE */
	/************************************************************************************************/


    /***************************************************************************/
    /* PETITE MÉTHODE POUR SUPPRIMER TOUTES VALEURS VIDES OU NULLS D'UNE LISTE */
    /**************************************************************************/
		
	/**
	 * Supprime toutes les chaînes nulles ou vides d'une liste.
	 *
	 * @param array La liste cible à verrifier.
	 */
	public static void RemoveNullList(List<String> array) {
	   
	   try { array.removeIf(StringUtil::isNullOrEmpty); }
	   catch(UnsupportedOperationException ignored) {}
	}

	/***************************************************************************/
	/* PETITE MÉTHODE POUR SUPPRIMER TOUTES VALEURS VIDES OU NULLS D'UNE LISTE */
	/**************************************************************************/


	/***************************************************************************/
	/* PETITE MÉTHODE POUR VÉRIFIER SI UN JOUEUR REGARDE UN EMPLACEMENT PRÉCIS */
	/***************************************************************************/

	/**
	 * Vérifie si un joueur regarde un emplacement spécifique
	 *
	 * @param player Le joueur qui regarde l'emplacement en question.
	 * @param location L'Emplacement a vérifié
	 *
	 * @return Une valeur booléenne.
	 */
	public static boolean getLookingAt(Player player, Location location) {

		Location eye = player.getEyeLocation();
		Vector toLocation = location.toVector().subtract(eye.toVector());
		double dot = toLocation.normalize().dot(eye.getDirection());

		return dot > 0.99D;
	}
  	/***************************************************************************/
	/* PETITE MÉTHODE POUR VÉRIFIER SI UN JOUEUR REGARDE UN EMPLACEMENT PRÉCIS */
	/***************************************************************************/

	  
	/********************************************************************/
	/* PETITE MÉTHODE POUR VÉRIFIER LA PERMISSION "LUCKPERMS" DU JOUEUR */
	/********************************************************************/
		
	/**
	 * Vérifie si un joueur a une permission de l'api {@link net.luckperms.api.LuckPerms}
	 *
	 * @param p Le joueur a vérifié l'autorisation.
	 * @param permission L'Autorisation à vérifier.
	 *
	 * @return Une valeur booléenne.
	 */
	public static boolean hasLuckPermission(Player p, String permission) {

	  User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId());

	  if(user != null) if(user.getCachedData().getPermissionData().getPermissionMap().containsKey(permission)) { return true; }
	  return false;
	}
	  
	/********************************************************************/
	/* PETITE MÉTHODE POUR VERIFIER LA PERMISSION "LUCKPERMS" DU JOUEUR */
	/********************************************************************/
	
	  
  
    /*****************************************************************/
    /* PETITE MÉTHODE POUR VÉRIFIER LE GRADE DU JOUEUR (TYPE ADMIN) */
    /*****************************************************************/	
  
	/**
	 * Vérifie si un joueur à un rôle administrateur.
	 *
	 * @param p Le joueur à vérifier
	 *
	 * @return Une valeur booléenne.
	 */
	public static boolean hasAdminGrade(Player p) {

		// Définit la "Class" 'Main'
		UtilityMain main = UtilityMain.getInstance();
		
		//Si le joueur est un admin, le co-fondateur ou la fondatrice
		if(main.formatgrade.isPlayerInGroup(p, "fondateur") || main.formatgrade.isPlayerInGroup(p, "fondatrice") || main.formatgrade.isPlayerInGroup(p, "admin")) {

		    return true;
		    
		} else { return false; }
		//Vérifie si le nom du serveur actuel est le nom d'un des serveurs hub (ou pas) //

	}
	
	/*****************************************************************/
	/* PETITE MÉTHODE POUR VÉRIFIER LE GRADE DU JOUEUR (TYPE ADMIN) */
	/*****************************************************************/	


	/*********************************************************************************************/
	/* PETITE MÉTHODE VÉRIFIANT SI LE JOUEUR A LA PERMISSION DE CONTOURNEMENT (PERMISSION ADMIN) */
	/*********************************************************************************************/

	/**
	 * Vérifie si un joueur à la permission de contournement (permission administrateur)
	 *
	 * @param p Le Joueur a vérifié la permission
	 *
	 * @return Une valeur booléenne.
	 */
	public static boolean hasByPassPerm(Player p) {

		if(p.hasPermission("utility.bypass")) { return true; }
		return false;
	}

	/*********************************************************************************************/
	/* PETITE MÉTHODE VÉRIFIANT SI LE JOUEUR A LA PERMISSION DE CONTOURNEMENT (PERMISSION ADMIN) */
	/*********************************************************************************************/

	
	/******************************************************************************/
	/* PETITE MÉTHODE POUR VERIFIER SI LE NOM DU SERVEUR EST CELUI D'UN DES HUBS */
	/******************************************************************************/

	/**
	 * Vérifie si le serveur actuel est un serveur hub ou non
	 *
	 * @param hub Le nom du serveur concentrateur
	 *
	 * @return Une valeur booléenne.
	 */
	public static boolean isServerHub(String hub) {
			
		//Vérifie si le nom du serveur actuel est le nom d'un des serveurs hub (ou pas) //
		if(ConfigFile.getString(UtilityMain.getInstance().servernameconfig, "server_name").equalsIgnoreCase(hub)) { return true; }
		
		else { return false; }
		//Vérifie si le nom du serveur actuel est le nom d'un des serveurs hub (ou pas) //

	}

	/******************************************************************************/
	/* PETITE MÉTHODE POUR VERIFIER SI LE NOM DU SERVEUR EST CELUI D'UN DES HUBS */
	/******************************************************************************/


	/*********************************************************************************************/
	/* PETITE MÉTHODE EN PAQUET POUR ENVOYER UN MESSAGE A UN JOUEUR AU NIVEAU DE LA BAR D'ACTION */
	/*********************************************************************************************/
	  public static void sendActionBar(Player player, String message) {

        net.minecraft.network.chat.Component chat = net.minecraft.network.chat.Component.Serializer.fromJson("{\"text\": \"" + message + "\"}");
        ClientboundChatPacket chatPacket = new ClientboundChatPacket(chat, ChatType.GAME_INFO, null);

        CraftPlayer craftPlayer = (CraftPlayer)player;
        craftPlayer.getHandle().connection.send(chatPacket);
    }
	/*********************************************************************************************/
	/* PETITE MÉTHODE EN PAQUET POUR ENVOYER UN MESSAGE A UN JOUEUR AU NIVEAU DE LA BAR D'ACTION */
	/*********************************************************************************************/



	/***********************************************************************/
	/* PETITE MÉTHODE POUR RÉCUPÉRER LE JOUEUR A PARTIR DE L'API LUCKPERM */
	/**********************************************************************/

	/**
	 * Obtient l'utilisateur LuckPerm d'un joueur (hors ligne ou non).
	 *
	 * @param uniqueId L'UUID du joueur à obtenir.
	 *
	 * @return Un Utilisateur LuckPerm
	 */
	public static User getLuckPermUserOffline(UUID uniqueId) {

		// *** \\ ⬇️ // *** RÉCUPÉRATION DU JOUEUR AVEC LE PLUGIN LUCKPERMS tant que l'Utilisateur LuckPerms est 'NULL' *** \\ ⬇️ // *** //

			UserManager userManager = UtilityMain.getInstance().luckapi.getUserManager(); // Gestionnaire Utilisateur LuckPerms
			boolean checkContinue = true; // Variable booléenne ('Vrai' par défaut)

			@NotNull OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(uniqueId); // Récupère le Joueur Hors-Ligne
			User user = null; // Permettra de récupérer l'Utilisateur LuckPerms

			// ⬇️ - Tant que l'Utilisateur LuckPerms est 'NULL', on essaie de le récupérer - ⬇️ //
			while(user == null) {

				// Si la varibale 'checkContinue' est vrai, on peut définir l'utilisateur LuckPerms sinon on renvoie 'null' //
				if(checkContinue) { user = userManager.getUser(uniqueId); }
				else { return null; }
				// Si la varibale 'checkContinue' est vrai, on peut définir l'utilisateur LuckPerms sinon on renvoie 'null' //
			}
			// ⬆️ - Tant que l'Utilisateur LuckPerms est 'NULL', on essaie de le récupérer - ⬆️ //

		// *** \\ ⬆️ // *** RÉCUPÉRATION DU JOUEUR AVEC LE PLUGIN LUCKPERMS tant que l'Utilisateur LuckPerms est 'NULL' *** \\ ⬆️ // *** //

		return user; //Retourne l'Utilisateur LuckPerms
	}

	/**********************************************************************/
	/* PETITE MÉTHODE POUR RÉCUPÉRER LE JOUEUR A PARTIR DE L'API LUCKPERM */
	/**********************************************************************/

 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
 /* PARTIE MÉTHODES PERSONNALISER */
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
}
