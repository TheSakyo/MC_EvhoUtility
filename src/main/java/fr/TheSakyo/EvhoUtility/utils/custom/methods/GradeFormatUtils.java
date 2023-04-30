package fr.TheSakyo.EvhoUtility.utils.custom.methods;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;

import net.luckperms.api.model.user.User;

import java.util.HashMap;
import java.util.List;


public class GradeFormatUtils {


	public HashMap<User, Group> playersGroup = new HashMap<>();
	public HashMap<User, String> playersGroupPrefix = new HashMap<>();

	
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
 /* PARTIE METHODE POUR LE FORMAT DU GRADE DU JOUEUR */ 
 /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */	
	
	/*********************************************************************************/
	/* PETITE METHODE POUR DÉTECTER LE GRADE DU JOUEUR (Compatible Plugin LuckPerms) */
	/*********************************************************************************/

    /**
     * Vérifie si un joueur a le grade demandé en grade principal
     *
     * @param p Le joueur à vérifier
     * @param group Le nom du groupe qu'il faut vérifier.
	 *
     * @return Une valeur booléenne
     */
    public boolean isPlayerInGroup(Player p, String group) {

		// Récupère l'Utilisateur LuckPerms du Joueur
    	User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId());

		// Si l'Utilisateur LuckPerms et le Nom du Groupe n'est pas 'NULL', on peut continuer les Vérifications
    	if(user != null && group != null) {

			// On vérifie le nom du Groupe du Joueur est égal au Nom du Groupe récupéré en paramètre
            return user.getPrimaryGroup().equalsIgnoreCase(group);

    	// Sinon, on retourne 'Faux'
    	} else return false;
    }

	/*********************************************************************************/
	/* PETITE MÉTHODE POUR DÉTECTER LE GRADE DU JOUEUR (Compatible Plugin LuckPerms) */
	/*********************************************************************************/


	/**********************************************************************************/
	/* PETITE METHODE POUR RÉCUPÉRER LE GRADE DU JOUEUR (Compatible Plugin LuckPerms) */
	/**********************************************************************************/

	/**
     * Récupère une liste récupérant le préfix et/ou le groupe LuckPerm d'un joueur
     *
     * @param p Le joueur à vérifier
	 * @param prefix Voulez-vous récupérer ou non le préfixe de groupe du Joueur.
     * @param group Voulez-vous récupérer ou non le groupe du Joueur.
	 *
     * @return Une valeur booléenne
     */
	public List<String> getPlayerGroup(Player p, boolean prefix, boolean group) {

		// Récupère l'Utilisateur LuckPerms du Joueur
		User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId());
		boolean hasGroupAndPrefix = group && prefix;

		/**********************************************************/

		// Si les deux 'boolean' en paramètre sont 'Vrai', on renvoie le Groupe LuckPerms du Joueur et son Préfix.
		if(hasGroupAndPrefix) return List.of(user.getCachedData().getMetaData().getPrimaryGroup(), user.getCachedData().getMetaData().getPrefix());

		// ⬇️ Sinon, on vérifie pour chaque 'boolean', on renvoie uniquement le Groupe LuckPerms du Joueur ou son Préfix. ⬇️ //
		else {

			// Si le 'boolean' "group" en paramètre est 'Vrai', on renvoie le Groupe LuckPerms du Joueur.
			if(group) return List.of(user.getCachedData().getMetaData().getPrimaryGroup());

			// Sinon, si le 'boolean' "prefix" en paramètre est 'Vrai', on renvoie le Préfix du Groupe du Joueur.
			else if(prefix) return List.of(user.getCachedData().getMetaData().getPrefix());
		}
		// ⬆️ Sinon, on vérifie pour chaque 'boolean', on renvoie uniquement le Groupe LuckPerms du Joueur ou son Préfix. ⬆️ //

		/**********************************************************/

		return null; // Sinon, on renvoie faux
	}

	/**********************************************************************************/
	/* PETITE METHODE POUR RÉCUPÉRER LE GRADE DU JOUEUR (Compatible Plugin LuckPerms) */
	/**********************************************************************************/

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	/* PARTIE MÉTHODE POUR LE FORMAT DU GRADE DU JOUEUR */
	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
}
