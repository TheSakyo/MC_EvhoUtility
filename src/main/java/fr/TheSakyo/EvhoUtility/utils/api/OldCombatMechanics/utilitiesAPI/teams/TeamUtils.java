package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.teams;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.team.TeamPacket;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Contient des méthodes utiles pour traiter avec les équipes du tableau d'affichage de Minecraft.
 */
public class TeamUtils {

    private static final AtomicInteger TEAM_NAME_COUNTER = new AtomicInteger();

    /**
     * Vérifie si un paquet d'équipe cible un joueur.
     *
     * @param packet Le paquet en question
     * @param player Le joueur à vérifier
     * @return Vrai si le paquet cible le joueur.
     */
    public static boolean targetsPlayer(TeamPacket packet, Player player){
        return packet.getPlayerNames().contains(player.getName());
    }

    /**
     * Crée un nouveau paquet d'équipe et l'envoie au joueur. L'équipe aura un nom aléatoire.
     *
     * @param player Le joueur à qui l'envoyer
     * @param collisionRule La règle de collision à utiliser
     */
    public static TeamPacket craftTeamCreatePacket(Player player, CollisionRule collisionRule){

        // À la bonne taille (10) et est unique.
        String teamName = "OCM-" + TEAM_NAME_COUNTER.getAndIncrement() + "";
        return TeamPacket.create(TeamAction.CREATE, collisionRule, teamName, Collections.singletonList(player));
    }

    /**
     * @param team Le paquet de la Team
     * @return Vrai si l'équipe a été créée par OldCombatMechanics
     */
    public static boolean isOcmTeam(TeamPacket team){
        return team.getName().startsWith("OCM-");
    }

    /**
     * Dissout une équipe.
     *
     * @param teamName Le nom de l'équipe à dissoudre
     * @param player Le joueur pour lequel l'équipe doit être dissoute
     */
    public static void disband(String teamName, Player player){
        TeamPacket packet = TeamPacket.create(
                TeamAction.DISBAND,
                CollisionRule.NEVER,
                teamName,
                Collections.singletonList(player)
        );
        packet.send(player);
    }
}
