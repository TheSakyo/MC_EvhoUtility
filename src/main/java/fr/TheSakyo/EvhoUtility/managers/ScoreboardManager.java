package fr.TheSakyo.EvhoUtility.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

            /* ---------------------------------------- */

    // Variable "Map" Scoreboard par joueur //
    public static Map<UUID, Scoreboard> scoreboardPlayers = new HashMap<UUID, Scoreboard>();
    // Variable "Map" Scoreboard par joueur //

            /* ---------------------------------------- */

    // ~~~~ Méthode pour remettre à zéro un Scoreboard ~~~~ //
    private static synchronized void resetScoreBoard(Scoreboard board) {

        board.clearSlot(DisplaySlot.SIDEBAR);
        board.clearSlot(DisplaySlot.BELOW_NAME);
        board.clearSlot(DisplaySlot.PLAYER_LIST);

        if(board.getEntries() != null) {

            for(String str : board.getEntries()) { board.resetScores(str); }
        }

        if(board.getObjectives() != null) {

            for(Objective objs : board.getObjectives()) { board.getObjective(objs.getName()).unregister(); }
        }

        if(board.getTeams() != null) {

            for(Team teams : board.getTeams()) board.getTeam(teams.getName()).unregister();
        }
    }
    // ~~~~ Méthode pour remettre à zéro un Scoreboard ~~~~ //


    // ~~~~ Partie Récupération et/ou Actualisation du Scoreboard ~~~~ //
    public static synchronized Scoreboard getScoreboard(Player p) {

        Scoreboard board = null; // Variable "board" étant 'NULL par défaut, pour récupérer le scoreboard du Joueur

        //Vérifie si le joueur ne contient aucun Scoreboard, on essaie de lui en créer un
        if(!scoreboardPlayers.containsKey(p.getUniqueId())) {

            // Définit a la variable "Board" un Nouveau Scoreboard
            board = Bukkit.getScoreboardManager().getNewScoreboard();

            // Variable "board" récupérant le nouveau Scoreboard
            scoreboardPlayers.put(p.getUniqueId(), board); //Ajoute au Joueur le Scoreboard créer

        // Sinon, on récupère son Scoreboard
        } else { board = scoreboardPlayers.get(p.getUniqueId()); }

        return board; // On retourne le Scoreboard du Joueur
    }
     // ~~~~ Partie Récupération et/ou Actualisation du Scoreboard ~~~~ //


     // ~~~~ Partie Récupération/Actualisation du Scoreboard ~~~~ //
    public static synchronized void update(Player p, Scoreboard board) {

                    /* ----------------------------------------- */

        /*// ~~ ⬇️ Récupèration ou Ajout de la Team pour les NPC ⬇️ ~~ //

        //Cache le Nom du 'npc' en question au-dessus de sa tête pour le Joueur en question
        for(NPCEntity npc : UtilityMain.getInstance().npclist.values()) { npc.hideNameTagVisibility(p, board); }

        // ~~ ⬆️ Récupèration ou Ajout de la Team pour les NPC ⬆️ ~~ //*/

            /* ----------------------------------------- */

        scoreboardPlayers.replace(p.getUniqueId(), board); //On remplace le Scoreboard du Joueur

            /* ----------------------------------------- */

        p.setScoreboard(board); //On lui ajoute le nouveau Scoreboard Modifié
    }
    // ~~~~ Partie Récupération/Actualisation du Scoreboard ~~~~ //



    // ~~~ ### Partie Création d'un nouveau Scoreboard pour le Joueur ### ~~~ //
    public static synchronized void makeScoreboard(Player p, boolean reset) {

            /* ------------------------------------------------ */

        try {

            Scoreboard scoreboard = getScoreboard(p); // Essait de récupérer le Scoreboard du Joueur

            if(reset == true) resetScoreBoard(scoreboard); //Si le paramètre 'reset' est vrai, on rafraichit le Scoreboard

            update(p, scoreboard);  //Actualise le Scoreboard du Joueur

        } catch(Exception ignored) {} //S'il y a une exception, on ingore

            /* ------------------------------------------------ */
    }
    // ~~~ ### Partie Création d'un nouveau Scoreboard pour le Joueur ### ~~~ //

                /* ---------------------------------------- */
}
