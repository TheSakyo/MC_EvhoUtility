package fr.TheSakyo.EvhoUtility.runnable;

import com.google.common.base.Splitter;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPC;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPCGlobal;
import fr.TheSakyo.EvhoUtility.utils.entity.entities.NPCEntity;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.stream.Collectors;

public class RunAnimationNPC {

    private UtilityMain mainInstance = UtilityMain.getInstance(); //Récupère la class "main" (UtilityMain)

    private NPCGlobal npc; //Le NPC sur lequel on effectue l'action
    private NPCEntity EntityNPC; // Une Instance de la 'class' NPCEntity pour récupérer les états des animations

	private BukkitTask task; // Permet de récupérer la boucle actuelle


                                        /* --------------------------------------------------------- */
                                        /* --------------------------------------------------------- */

    // Constructeur de la class "Animation" //
    public RunAnimationNPC(NPCGlobal npcGlobal, NPCEntity npc) {

        this.npc = npcGlobal; // Enregistre le NPC en question
        this.EntityNPC = npc; // Enregistre l'instance de la 'class' NPCEntity pour récupérer les états des animations
    }
    // Constructeur de la class "Animation" //


                                        /* --------------------------------------------------------- */
                                        /* --------------------------------------------------------- */

	/**
	 * Récupère la {@link BukkitRunnable boucle} en cours.
	 *
	 * @return La {@link BukkitRunnable boucle} en cours
	 */
	public BukkitTask getTask() { return task; }

										/* --------------------------------------------------------- */
                                        /* --------------------------------------------------------- */

	/**
	 * Récupère les États des {@link NPC.Animation Animations} du {@link NPCGlobal NPC Global} en question (pour la boucle).
	 *
	 * @param loadFromConfig Doit-on récupérer les animations depuis le fichier de configuration ?
	 *
	 * @return Les États des animations du {@link NPCGlobal NPC Global} demandé
	 */
	public Map<NPC.Animation, Integer> getAnimationsStatus(boolean loadFromConfig) {

		String animations = ConfigFile.getString(UtilityMain.getInstance().NPCconfig, "NPC." + this.npc.getSimpleCode() + ".Animation");
		if(loadFromConfig && animations != null) {

			Map<String, String> animationsMap = Splitter.on(";").withKeyValueSeparator(":").split(animations);
			animationsMap.forEach((animation, status) -> this.setAnimationStatus(NPC.Animation.valueOf(animation), Integer.valueOf(status)));
		}

		return this.EntityNPC.getAnimationsStatus(this.npc);
	}

											/* ---------------------------------------------- */

	/**
	 * Définit l'État d'une {@link NPC.Animation Animation} du {@link NPCGlobal NPC Global} (pour la boucle).
	 *
	 * @param animation L'{@link NPC.Animation Animation} en question
	 * @param status    L'État en question (0 = stop ; 1 = en boucle)
	 *
	 *
	 * @return Les États des animations du {@link NPCGlobal NPC Global} définit
	 */
	public Map<NPC.Animation, Integer> setAnimationStatus(NPC.Animation animation, Integer status) {

        return this.EntityNPC.setAnimationStatus(this.npc, animation, status);
	}

                                        /* --------------------------------------------------------- */
                                        /* --------------------------------------------------------- */

	/**
	 * Met à jour le fichier de configuration pour les animations et leurs états actuel pour le NPC
	 *
	 * @param reloadConfig Doit-on recharger les animations du fichier de configuration ?
	 *
	 */
	public void updateConfig(boolean reloadConfig) {

		// Récupère la liste des animations avec leurs états (boolean) en une seule phrase séparée par des <<;>>
		String animationConfig = getAnimationsStatus(reloadConfig).entrySet().stream().map(e -> e.getKey().name() + ":" + String.valueOf(e.getValue().intValue())).collect(Collectors.joining(";"));

												/* -------------------------------------------------- */

		// ⬇️ Partie Modification du fichier de configuration pour les animations du NPC ⬇️ //

		if(ConfigFile.getString(UtilityMain.getInstance().NPCconfig, "NPC." + this.npc.getSimpleCode() + ".Animation") != null) {
			ConfigFile.removeKey(UtilityMain.getInstance().NPCconfig,"NPC." + this.npc.getSimpleCode() + ".Animation");
		}
		ConfigFile.set(UtilityMain.getInstance().NPCconfig, "NPC." + this.npc.getSimpleCode() + ".Animation", animationConfig.toUpperCase());

		ConfigFile.saveConfig(UtilityMain.getInstance().NPCconfig);

		// ⬆️ Partie Modification du fichier de configuration pour les animations du NPC ⬆️ //
	}

                                        /* --------------------------------------------------------- */
                                        /* --------------------------------------------------------- */


    /***********************************/
	/* BOUCLE POUR L'ANIMATION DU NPC  */
	/**********************************/
	public void run() {

		this.task = new BukkitRunnable() {
			@Override
			public void run() {

				boolean animationisEmpty = false; // Varioble permettant de vérifier s'il y a toujours des animations en cours

				if(npc == null) this.cancel(); // Si le NPC est null, on quitte la boucle

				Map<NPC.Animation, Integer> animationsStatus = getAnimationsStatus(false); // Récupère les animations et leur status

															/* ---------------------------------------- */

				// ⬇️ Effectue l'animation en question si son status actuel est sur '1' ⬇️ //
				for(NPC.Animation animation : animationsStatus.keySet()) {

					// ** Aprés une seconde, on joue l'animation ** //
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(mainInstance, new Runnable() {

						@Override
						public void run() { if(animationsStatus.get(animation).equals(Integer.valueOf(1))) { npc.playAnimation(animation); } }

					}, 20);
					// ** Aprés une seconde, on joue l'animation ** //
				}
				// ⬆️ Effectue l'animation en question si son status actuel est sur '1' ⬆️ //

													/* ---------------------------------------- */
													/* ---------------------------------------- */

				/* ⬇️ Vérifie s'il y a toujours des animations en cours (1) ou non (2), dans le cas s'il n'y a aucune animation en cours,
				   on annule la boucle en s'aidant de la variable 'animationIsEmpty' définit plus haut ⬇️ */
				for(Integer status : animationsStatus.values()) {

					if(status.equals(Integer.valueOf(1))) { animationisEmpty = false; break; }
					else { animationisEmpty = true; }
				}
				/* ⬆️ Vérifie s'il y a toujours des animations en cours (1) ou non (2), dans le cas s'il n'y a aucune animation en cours,
				   on annule la boucle en s'aidant de la variable 'animationIsEmpty' définit plus haut ⬆️ */

				if(animationisEmpty) this.cancel(); // Si la variable 'animationIsEmpty définit plus haut montre qu'il n'y a aucune animation en cours, on annule la boucle
			}
		}.runTaskTimerAsynchronously(mainInstance, 0L, 20L);
	}
	/***********************************/
	/* BOUCLE POUR L'ANIMATION DU NPC  */
	/**********************************/
}
