package fr.TheSakyo.EvhoUtility.utils.entity.entities;


import fr.TheSakyo.EvhoUtility.runnable.RunAnimationNPC;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPC;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPCGlobal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.util.HashMap;
import java.util.Map;

public class NPCEntity {


    private static final Map<NPCGlobal, RunAnimationNPC> animationRunnable = new HashMap<>(); // Variable récupérant une tâche répétant les animations d'un NPC associé
    private final Map<NPCGlobal, Map<NPC.Animation, Integer>> animations = new HashMap<>(); //Liste des animations d'un NPC associé avec leurs états (boolean)

                        				/* --------------------------------------------------------- */
                                        /* --------------------------------------------------------- */

	/**
	 * Récupère les États des {@link NPC.Animation Animations} d'un {@link NPCGlobal NPC Global} en question (pour la boucle).
	 *
	 * @param npc Le {@link NPCGlobal NPC Global} en question
	 *
	 * @return les États des animations du {@link NPCGlobal NPC Global} demandé
	 */
	public Map<NPC.Animation, Integer> getAnimationsStatus(NPCGlobal npc) {

		Map<NPC.Animation, Integer> animationsStatus = new HashMap<>();

		animationsStatus.putIfAbsent(NPC.Animation.SWING_MAIN_ARM, 0);
		animationsStatus.putIfAbsent(NPC.Animation.TAKE_DAMAGE, 0);
		animationsStatus.putIfAbsent(NPC.Animation.SWING_OFF_HAND, 0);
		animationsStatus.putIfAbsent(NPC.Animation.CRITICAL_EFFECT, 0);
		animationsStatus.putIfAbsent(NPC.Animation.MAGICAL_CRITICAL_EFFECT, 0);

									/* ----------------------------------- */


									/* ----------------------------------- */

		animations.putIfAbsent(npc, animationsStatus);
		if(animations.containsKey(npc)) return animations.get(npc);

		return null;
	}

											/* ---------------------------------------------- */

	/**
	 * Définit l'État d'une {@link NPC.Animation Animation} du {@link NPCGlobal NPC Global} (pour la boucle).
	 *
	 * @param npc       Le {@link NPCGlobal NPC Global} en question
	 * @param animation L'{@link NPC.Animation Animation} en question
	 * @param status    L'État en question (0 = stop ; 1 = en boucle)
	 */
	public void setAnimationStatus(NPCGlobal npc, NPC.Animation animation, Integer status) {

		if(animations.containsKey(npc)) {

			Map<NPC.Animation, Integer> animationsStatus = animations.get(npc);

			animationsStatus.putIfAbsent(animation, status);
			if(animationsStatus.containsKey(animation)) animationsStatus.replace(animation, status);

			animations.replace(npc, animationsStatus);
		}

		/****************************/

		getAnimationsStatus(npc);
	}

                                        /* --------------------------------------------------------- */
                                        /* --------------------------------------------------------- */

	/**
	 * Récupère la {@link RunAnimationNPC Boucle} des {@link NPC.Animation Animations} du {@link NPCGlobal NPC Global}.
	 *
	 * @param npc Le {@link NPCGlobal NPC Global}
	 *
	 * @return La {@link RunAnimationNPC Boucle} des {@link NPC.Animation Animations} du {@link NPCGlobal NPC Global}
	 */
    public static RunAnimationNPC runAnimation(NPCGlobal npc) {

		RunAnimationNPC animationTask = null; // Variable permettant de récupérer la Boucle des Animations du NPC Global

		// ⬇️ Vérification de l'existence de la boucle en fonction du NPC Global récupéré ⬇️ //
		animationRunnable.putIfAbsent(npc, new RunAnimationNPC(npc, new NPCEntity()));
		if(animationRunnable.containsKey(npc)) animationTask = animationRunnable.get(npc);
		// ⬆️ Vérification de l'existence de la boucle en fonction du NPC Global récupéré ⬆️ //

												/* --------------------------- */

		return animationTask; // On retourne la boucle en question
    }

											/* --------------------------------------------------------- */
											/* --------------------------------------------------------- */

    /**
     * Vérifie si le {@link NPCGlobal NPC Global} en question est créé ou non
     *
     * @param npc Le {@link NPCGlobal NPC Global} en question
     *
     * @return Une valeur Booléenne
     */
	public static boolean isCreated(NPCGlobal npc) {

		boolean isCreated = true; // Variable permettant de vérifier plus tard si le NPC en question est déjà créé ou pas

		/*********************************/

		// ⬇️ Pour tous les joueurs en ligne, on vérifie si leur NPC Personnel sont déjà créés, si c'est le cas sur un seul NPC, on retourne donc vrai ⬇️ //
		for(Player players : Bukkit.getServer().getOnlinePlayers()) {

            isCreated = npc.getPersonal(players).isCreated();
            break;
		}
		// ⬆️ Pour tous les joueurs en ligne, on vérifie si leur NPC Personnel sont déjà créés, si c'est le cas sur un seul NPC, on retourne donc vrai ⬆️ //

		/*********************************/

		return isCreated; // Retourne le booléen vérifiant si le NPC en question est déjà créé ou pas
	}
												/* --------------------------------------------------------- */
												/* --------------------------------------------------------- */

	/**
	 * Recharge la création du {@link NPCGlobal NPC Global} pour chaque joueur
	 *
	 * @param npc Le {@link NPCGlobal NPC Global} en question
	 *
	 */
	public static void checkToCreate(NPCGlobal npc) {

		if(npc == null) return;

		// ⬇️ Pour tous les joueurs en ligne, on vérifie si son NPC associé est créé ou non, alors on lui crée ⬇️ //
		for(Player players : Bukkit.getServer().getOnlinePlayers()) {

			if(isCreated(npc) && !npc.getPersonal(players).isCreated()) { npc.getPersonal(players).create(); }
			else if(!isCreated(npc)) npc.getPersonal(players).create();
		}
		// ⬆️ Pour tous les joueurs en ligne, on vérifie si son NPC associé est créé ou non, alors on lui crée ⬆️ //
	}

													/* --------------------------------------------------------- */

	/**
	 * Recharge la déstruction du {@link NPCGlobal NPC Global} pour chaque joueur
	 *
	 * @param npc Le {@link NPCGlobal NPC Global} en question
	 *
	 */
	public static void checkToDestroy(NPCGlobal npc) {

		if(npc == null) return;

		// ⬇️ Pour tous les joueurs en ligne, on vérifie si son NPC associé est détruit ou non, alors on lui détruit ⬇️ //
		for(Player players : Bukkit.getServer().getOnlinePlayers()) {

			if(!isCreated(npc) && npc.getPersonal(players).isCreated()) { npc.getPersonal(players).destroy(); }
			else if(isCreated(npc)) npc.getPersonal(players).destroy();
		}
		// ⬆️ Pour tous les joueurs en ligne, on vérifie si son NPC associé est détruit ou non, alors on lui détruit ⬆️ //
	}
}
