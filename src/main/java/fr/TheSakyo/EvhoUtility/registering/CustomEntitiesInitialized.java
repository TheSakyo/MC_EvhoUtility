package fr.TheSakyo.EvhoUtility.registering;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.managers.HologramManager;
import fr.TheSakyo.EvhoUtility.managers.NPCManager;

public class CustomEntitiesInitialized {

    /* Récupère la class "Main" */
	 private final UtilityMain main;
	 public CustomEntitiesInitialized(UtilityMain pluginMain) { this.main = pluginMain; }
	 /* Récupère la class "Main" */

                        /* ---------------------------------------------------------------------------- */

    /*public static NPC NPCTest;*/

                        /* ---------------------------------------------------------------------------- */

    /*************************************/
	/* CHARGEMENT DES ENTITÉS CUSTOMISÉ */
	/************************************/

    /**
     * Permet d'initialiser les Entités customisés enregistrés dans le Serveur (Déchargement + Rechargement).
     *
     * @param pluginDisabling Vrai (true) si le plugin actuel est en train de se désactiver || Faux (false) si le plugin actuel est en train de s'activer.
     */
    public void init(boolean pluginDisabling) {

        // Si plugin actuel n'est pas en désactivation, on recharge toutes les entités customisées
        if(!pluginDisabling) {

            // Recharge tous les 'Hologramme(s)' enregistré(s) dans le fichier de configuration "holograms.yml" (s'il y en n'a)
            if(!main.holoKeys.getKeys(false).isEmpty() || !main.HOLOGRAMS.isEmpty()) { HologramManager.loadHolograms(null, false); }

            //Recharge tous les 'NPC(s)' enregistré(s) dans le fichier de configuration "NPC.yml" (s'il y en n'a)
            if(!main.npcKeys.getKeys(false).isEmpty() || !main.NPCS.isEmpty()) { NPCManager.loadNPC(null, true, true, true); }

        // Sinon, si plugin actuel est en désactivation, on décharge donc toutes les entités customisées
        } else {

            /* Supprime les 'Hologramme(s)' du Serveur */
            if(!main.holoKeys.getKeys(false).isEmpty() || !main.HOLOGRAMS.isEmpty()) { HologramManager.unloadHolograms(null, false); }
            /* Supprime les 'Hologramme(s)' du Serveur */

            /* Supprime les 'NPC(s)' du Serveur */
            if(!main.npcKeys.getKeys(false).isEmpty() || !main.NPCS.isEmpty()) { NPCManager.unloadNPC(null, false); }
            /* Supprime les 'NPC(s)' du Serveur */
        }

    }

    /*************************************/
	/* CHARGEMENT DES ENTITÉS CUSTOMISÉ */
	/************************************/
}
