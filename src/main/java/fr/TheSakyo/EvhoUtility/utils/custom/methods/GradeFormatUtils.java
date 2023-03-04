package fr.TheSakyo.EvhoUtility.utils.custom.methods;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.luckperms.api.model.group.Group;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import net.luckperms.api.model.user.User;

import java.util.HashMap;
import java.util.List;


public class GradeFormatUtils {

	/* Récupère la class "Main" */
	private UtilityMain main;
	public GradeFormatUtils(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */

	public HashMap<User, Group> playersGroup = new HashMap<User, Group>();
	public HashMap<User, String> playersGroupPrefix = new HashMap<User, String>();

	
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
     * @param group Le nom du groupe a vérifié.
	 *
     * @return Une valeur booléene
     */
    public boolean isPlayerInGroup(Player p, String group) {

		// Récupère l'Utilisateur LuckPerms du Joueur
    	User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId());

		// Si l'Utilisateur LuckPerms et le Nom du Groupe n'est pas 'NULL', on peut continuer les Vérifications
    	if(user != null && group != null) {

			// Si le nom du Groupe du Joueur est égal au Nom du Groupe récupéré en Paramètre, on retourne 'Vrai'
    		if(user.getPrimaryGroup().equalsIgnoreCase(group)) { return true; }
			// Sinon, le Joueur n'a pas le Groupe demandé, on retourne 'Faux'
    		else { return false; }

    	// Sinon, on retourne 'Faux'
    	} else { return false; }
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
	 * @param prefix Voulez-vous récupéreé ou non le préfixe de groupe du Joueur.
     * @param group Voulez-vous récupéreé ou non le groupe du Joueur.
	 *
     * @return Une valeur booléene
     */
	public List<String> getPlayerGroup(Player p, boolean prefix, boolean group) {

		// Récupère l'Utilisateur LuckPerms du Joueur
		User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId());

		// Si le 'boolean' "group" en paramètre est 'Vrai', on retourne le Groupe LuckPerms du Joueur.
		if(group == true) return List.of(user.getCachedData().getMetaData().getPrimaryGroup());

		// Sinon, si le 'boolean' "prefix" en paramètre est 'Vrai', on retourne le Préfix du Groupe du Joueur.
		else if(prefix == true) return List.of(user.getCachedData().getMetaData().getPrefix());

		// Sinon Si les deux 'boolean' en paramètre sont 'Vrai', on retourne le Groupe LuckPerms du Joueur et son Préfix.
		else if(group == true && prefix == true) { return List.of(user.getCachedData().getMetaData().getPrimaryGroup(), user.getCachedData().getMetaData().getPrefix()); }

		return null; // Sinon, on retourne faux
	}

	/**********************************************************************************/
	/* PETITE METHODE POUR RÉCUPÉRER LE GRADE DU JOUEUR (Compatible Plugin LuckPerms) */
	/**********************************************************************************/

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
	/* PARTIE MÉTHODE POUR LE FORMAT DU GRADE DU JOUEUR */
	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
}
