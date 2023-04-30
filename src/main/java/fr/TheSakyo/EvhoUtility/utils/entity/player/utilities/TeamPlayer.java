package fr.TheSakyo.EvhoUtility.utils.entity.player.utilities;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;

import java.util.HashMap;
import java.util.Map;


/*************************************************************/
/* PARTIE TEAMS DES GROUPES LUCKPERMS CHARGÉ DANS LE SERVEUR */
/*************************************************************/
public class TeamPlayer {

    private static final UtilityMain mainInstance = UtilityMain.getInstance(); // Instance de la 'Class' "Main"
    public static Map<Group, PlayerTeam> groupTeams = new HashMap<>(); // Liste de tous les groupes du Serveur.

    public static Map<String, PlayerTeam> customTeams = new HashMap<>(); // Liste de tous les teams personnalisé.

    						/* ---------------------------------------------------*/
							/* ---------------------------------------------------*/

    /**
     * On charge tous les groupes du serveur et pour chaque groupe, il crée une équipe avec le nom du groupe et la couleur de celle-ci.
     */
    public static void loadTeams() {

        GroupManager groupManager = mainInstance.luckApi.getGroupManager(); // Récupère la Class pour gérer les Groupes "LuckPerms"
		groupManager.loadAllGroups(); // On Charge tous les Groupes du Serveur

        /*****************************************************/

		// _-_ ⬇️ On Boucle sur tous les Groupes du Serveur et on leur attribue à chacun une 'Team' ⬇️ _-_ //
		for(Group group : groupManager.getLoadedGroups()) {

			String displayName = group.getDisplayName(); // Récupère le Nom d'Affichage du groupe

			while(displayName == null) displayName = group.getDisplayName(); // Récupère le Nom d'Affichage du groupe en question tant qu'elle est null

			char firstChar = displayName.charAt(0); // Récupère le premier caractère du nom d'affichage du Groupe

			String groupTeam = displayName.replaceFirst(String.valueOf(firstChar), ""); // Récupère le nom de la Team qui sera le groupe en question

			String groupColor = group.getCachedData().getMetaData().getSuffix(); // Récupère la couleur du groupe en question

            while(groupColor == null) groupColor = group.getCachedData().getMetaData().getSuffix(); // Récupère la couleur du groupe en question tant qu'elle est null

			// Récupère le nom d'affichage du Groupe en remplaçant quelque caractères
			String groupDisplayName = groupTeam.replaceFirst("_", "");

													/* ------------------------- */

			// On vérifie si le groupe a un poids, si c'est le cas, on lui ajoute au début du nom de Team.
			if(group.getWeight().isPresent()) groupTeam = firstChar + groupTeam;

			// Sinon, on lui ajoute le poids	étant 0 au début du nom de Team.
			else groupTeam = "y" + groupTeam;

													/* ------------------------- */

            // Récupère le Préfix à la 'Team' adapté avec le Groupe en replacement notamment certains caractères
			String prefix = groupColor + ChatFormatting.BOLD + groupDisplayName.replace("+", ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "+") +  ChatFormatting.WHITE + " | ";

            Component playerPrefix = CraftChatMessage.fromString(ColorUtils.format(prefix))[0];
            Component displayNameComponent = CraftChatMessage.fromString(groupDisplayName)[0];

            ChatFormatting formattingColor = ColorUtils.getLastChatFormattingByString(groupColor);
            Team.Visibility visibility = Team.Visibility.ALWAYS;

            if(!groupTeams.containsKey(group)) {

                // ⬇️ Recharge une 'Team' pour le Groupe en question ⬇️ //
                PlayerTeam playerTeam = new PlayerTeam(((CraftScoreboard)Bukkit.getServer().getScoreboardManager().getMainScoreboard()).getHandle(), groupTeam);
                playerTeam.setPlayerPrefix(playerPrefix); // Ajoute le Préfix à la Team
                playerTeam.setDisplayName(displayNameComponent); // Ajoute un Nom d'Affichage pour la Team

                // À partir de la Team, on définit sa couleur étant la couleur du grade
                playerTeam.setColor(formattingColor);

                playerTeam.setNameTagVisibility(visibility); // Visibilité de la Team
                // ⬆️ Recharge une 'Team' pour le Groupe en question ⬆️ //

                groupTeams.putIfAbsent(group, playerTeam); // Ajoute la nouvelle team du Groupe

            } else {

                // ⬇️ Recharge une 'Team' pour le Groupe en question ⬇️ //
                PlayerTeam playerTeam = groupTeams.get(group);

                if(playerTeam.getPlayerPrefix() != playerPrefix) playerTeam.setPlayerPrefix(playerPrefix); // Ajoute le Préfix à la Team
                if(playerTeam.getDisplayName() != displayNameComponent) playerTeam.setDisplayName(displayNameComponent); // Ajoute un Nom d'Affichage pour la Team

                // À partir de la Team, on définit sa couleur étant la couleur du grade
                if(playerTeam.getColor() != formattingColor) playerTeam.setColor(formattingColor);

                if(playerTeam.getNameTagVisibility() != visibility) playerTeam.setNameTagVisibility(visibility); // Visibilité de la Team
                // ⬆️ Recharge une 'Team' pour le Groupe en question ⬆️ //

                groupTeams.replace(group, playerTeam); // Remplace la team du Groupe
            }
										/* ----------------------------------------- */
		}
		// _-_ ⬆️ On Boucle sur tous les Groupes du Serveur et on leur attribue à chacun une 'Team' ⬆️ _-_ //

                     /* ----------------------------------------------------------------------------- */

        // ⬇️ Recharge la 'Team' personnalisé "custom" ⬇️ //
        PlayerTeam customTeam; //Permettra de récupérer la 'Team' personnalisé

        // Vérifie si la team existe, si c'est le cas, on la récupère sinon, on crée une nouvelle //
        if(customTeams.containsKey("custom")) customTeam = customTeams.get("custom");
        else customTeam = new PlayerTeam(((CraftScoreboard)Bukkit.getServer().getScoreboardManager().getMainScoreboard()).getHandle(), "z_custom");
        // Vérifie si la team existe, si c'est le cas, on la récupère sinon, on crée une nouvelle //

        customTeam.setNameTagVisibility(Team.Visibility.NEVER); // Visibilité de la Team


        customTeams.putIfAbsent("custom", customTeam); // Définit la team customisé, si elle existe pas
        customTeams.replace("custom", customTeam); // Remplace la team customisé
        // ⬆️ Recharge la 'Team' personnalisé "custom" ⬆️ //
    }
}
/*************************************************************/
/* PARTIE TEAMS DES GROUPES LUCKPERMS CHARGÉ DANS LE SERVEUR */
/*************************************************************/
