package fr.TheSakyo.EvhoUtility.managers;

import dependancies.emoji4j.EmojiUtils;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.runnable.RunAnimationNPC;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPC;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPCGlobal;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPCUtils;
import fr.TheSakyo.EvhoUtility.utils.entity.entities.NPCEntity;
import io.netty.util.internal.StringUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;


public class NPCManager {

	private static final UtilityMain mainInstance = UtilityMain.getInstance(); //Récupère la class "Main".

	// Récupère la section de l'Équipement du 'NPC' en question
	public static Map<String, ConfigurationSection> EquipmentSection = new HashMap<>();


	/***********************************/
	/* PARTIE CONFIGURATION DES 'NPC' */
	/*********************************/

	/**
	 * Il enregistre les données du 'NPC' dans le fichier de configuration des 'NPCs'.
	 *
	 * @param id L'Identifiant du 'NPC'.
	 * @param Pose La Posture du 'NPC'.
	 * @param SkinStatus Le Status du Skin du 'NPC'.
	 * @param Equipments Équipements du 'NPC'.
	 * @param Anim L'Animation que le 'NPC' joue actuellement.
	 * @param skin {@link NPC.Skin} du 'NPC'.
	 * @param interactSender Le Nom personnalisé qui enverra le Message d'interaction du 'NPC'.
	 * @param interactMessage Le Message d'interaction du 'NPC'.
	 * @param canWatch Si le 'NPC' doit suivre du regard les joueurs proches de lui.
	 */
	public static void save_NPC(String id, Location location, String Pose, Map<NPC.Skin.Part, String> SkinStatus, Map<EquipmentSlot, ItemStack> Equipments,
								String Anim, NPC.Skin skin, String interactSender, String interactMessage, boolean canWatch, List<Player> players) {

		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".InteractEvent.Message", interactMessage);
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".InteractEvent.Sender", interactSender);
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Location.x", location.getX());
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Location.y", location.getY());
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Location.z", location.getZ());
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Location.Yaw", location.getYaw());
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Location.Pitch", location.getPitch());
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".World", location.getWorld().getName());
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Skin.Texture", skin.getTexture());
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Skin.Signature", skin.getSignature());
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".FollowPlayerWithHead", canWatch);
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Data.Pose", Pose);
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.CAPE", SkinStatus.get(NPC.Skin.Part.CAPE));
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.JACKET", SkinStatus.get(NPC.Skin.Part.JACKET));
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.LEFT_SLEEVE", SkinStatus.get(NPC.Skin.Part.LEFT_SLEEVE));
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.RIGHT_SLEEVE", SkinStatus.get(NPC.Skin.Part.RIGHT_SLEEVE));
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.LEFT_PANTS", SkinStatus.get(NPC.Skin.Part.LEFT_PANTS));
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.RIGHT_PANTS", SkinStatus.get(NPC.Skin.Part.RIGHT_PANTS));
		ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.HAT", SkinStatus.get(NPC.Skin.Part.HAT));

						// Sauvegarde l'équipement du 'NPC' //

		saveEquipment(id, Equipments.get(EquipmentSlot.HEAD), "HEAD", false);
		saveEquipment(id, Equipments.get(EquipmentSlot.CHEST), "CHESTPLATE", false);
		saveEquipment(id, Equipments.get(EquipmentSlot.LEGS), "LEGGINGS", false);
		saveEquipment(id, Equipments.get(EquipmentSlot.FEET), "BOOTS", false);
		saveEquipment(id, Equipments.get(EquipmentSlot.OFFHAND), "OFFHAND", false);
		saveEquipment(id, Equipments.get(EquipmentSlot.MAINHAND), "MAINHAND", false);

						// Sauvegarde l'équipement du 'NPC' //

		if(Anim != null) ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Animation", Anim);
		if(players != null) ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Players", players);

		ConfigFile.saveConfig(mainInstance.NPCConfig);
	}

	/**
	 * Sauvegarde toutes les données de l'Équipement du 'NPC' dans le fichier de configuration correspondant
	 *
	 * @param id L'ID du 'NPC' dont on souhaite décharger l'équipement.
	 * @param itemStack L'{@link ItemStack} de l'Équipement a sauvegardé
	 * @param SLOT <u>L'emplacement de l'équipement où on souhaite décharger l'élément :</u><br/>{@code HEAD, CHESTPLATE, LEGGINGS, BOOTS, OFFHAND, MAINHAND}.
	 * @param saveConfig Définit si on enregistre les modifications dans le fichier de configuration ou pas.
	 */
	public static void saveEquipment(String id, ItemStack itemStack, String SLOT, boolean saveConfig) {

		for(int i = 0; i < itemStack.serialize().keySet().size(); i++) {

			String data = itemStack.serialize().keySet().stream().toList().get(i);
			ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Data.Equipment." + SLOT + ".key." + i, data);
		}

						/* --------------------------------------------------------------------------- */

		for(int i = 0; i < itemStack.serialize().values().size(); i++) {

			Object data = itemStack.serialize().values().stream().toList().get(i);
			ConfigFile.set(mainInstance.NPCConfig, "NPC." + id + ".Data.Equipment." + SLOT + ".value." + i, data);
		}

		if(saveConfig) ConfigFile.saveConfig(mainInstance.NPCConfig);
	}

	/**
	 * Il supprime le 'NPC' avec l'ID donné du fichier de configuration des 'NPCs'.
	 *
	 * @param id L'Identifiant qui correspond au niveau du fichier de configuration du 'NPC'.
	 */
	public static void unSave_NPC(String id) {

		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".InteractEvent.Message");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".InteractEvent.Sender");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".InteractEvent");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Location.x");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Location.y");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Location.z");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Location.Yaw");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Location.Pitch");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".World");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Skin.Texture");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Skin.Signature");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Skin");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".FollowPlayerWithHead");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.Pose");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.CAPE");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.JACKET");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.LEFT_SLEEVE");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.RIGHT_SLEEVE");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.LEFT_PANTS");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.RIGHT_PANTS");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus.HAT");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.SkinStatus");

						// Supprime la sauvegarde de l'équipement du 'NPC' //

		EquipmentSection.put(id, ConfigFile.getConfigurationSection(mainInstance.NPCConfig, "NPC." + id + ".Data.Equipment"));

		unSaveEquipment(id, getEquipment(id, "HEAD"), "HEAD", false);
		unSaveEquipment(id, getEquipment(id, "CHESTPLATE"), "CHESTPLATE", false);
		unSaveEquipment(id, getEquipment(id, "LEGGINGS"), "LEGGINGS", false);
		unSaveEquipment(id, getEquipment(id, "BOOTS"), "BOOTS", false);
		unSaveEquipment(id, getEquipment(id, "OFFHAND"), "OFFHAND", false);
		unSaveEquipment(id, getEquipment(id, "MAINHAND"), "MAINHAND", false);

		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.Equipment");

						// Supprime la sauvegarde de l'équipement du 'NPC' //

		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Animation");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id);

		ConfigFile.saveConfig(mainInstance.NPCConfig);
	}


	/**
	 * Supprime toutes les données de l'Équipement du 'NPC' sauvegardé dans le fichier de configuration correspondant
	 *
	 * @param id L'ID du 'NPC' dont on souhaite décharger l'équipement.
	 * @param itemStackData {@code Map<String, Object>} qui contient les clés et les valeurs de l'{@link ItemStack} [{@link #getEquipment(String, String)}].
	 * @param SLOT <u>L'emplacement de l'équipement où on souhaite décharger l'élément :</u><br/>{@code HEAD, CHESTPLATE, LEGGINGS, BOOTS, OFFHAND, MAINHAND}.
	 * @param saveConfig Définit si on enregistre les modifications dans le fichier de configuration ou pas.
	 */
	public static void unSaveEquipment(String id, Map<String, Object> itemStackData, String SLOT, boolean saveConfig) {

		for(int i = 0; i < itemStackData.keySet().size(); i++) {

			ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.Equipment." + SLOT + ".key." + i);
		}
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.Equipment." + SLOT + ".key");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.Equipment." + SLOT);

						/* --------------------------------------------------------------------------- */

		for(int i = 0; i < itemStackData.values().size(); i++) {

			ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.Equipment." + SLOT + ".value." + i);
		}
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.Equipment." + SLOT + ".value");
		ConfigFile.removeKey(mainInstance.NPCConfig, "NPC." + id + ".Data.Equipment." + SLOT);

								/* --------------------------------------------------------------------------- */

		if(saveConfig) ConfigFile.saveConfig(mainInstance.NPCConfig);
	}


	/**
	 * Récupère toutes les données de l'Équipement du 'NPC' sauvegardé dans le fichier de configuration correspondant
	 *
	 * @param id L'ID du 'NPC' dont on souhaite décharger l'équipement.
	 * @param SLOT <u>L'emplacement de l'équipement où on souhaite décharger l'élément :</u><br/>{@code HEAD, CHESTPLATE, LEGGINGS, BOOTS, OFFHAND, MAINHAND}.
	 */
	public static Map<String, Object> getEquipment(String id, String SLOT) {

		Map<String, Object> equipmentData = new HashMap<>();

		String path = "NPC." + id + ".Data.Equipment." + SLOT;
		EquipmentSection.put(id, ConfigFile.getConfigurationSection(mainInstance.NPCConfig, path));

		for(int i = 0; i < EquipmentSection.get(id).getKeys(false).size(); i++) {

			String dataKey = ConfigFile.getString(mainInstance.NPCConfig, path + ".key." + i);
			Object dataObject = ConfigFile.get(mainInstance.NPCConfig, path + ".value." + i);

			equipmentData.putIfAbsent(dataKey, dataObject);
		}

		return equipmentData;
	}

	/**
	 * Recharge tous les 'NPC' enregistrés dans le fichier de configuration des 'NPCs'.
	 *
	 * @param p Le Joueur qui recharge les 'NPC' pour qu'il reçoive les modifications des NPCs.
	 * @param reloadByConfig Si oui ou non, on recharge le 'NPC' par le fichier de configuration
	 * @param forceLoad Si oui ou non, on doit forcer le rechargement du 'NPC'
	 * @param debugMode Doit-on informer des informations à la console
	 */
	public static void loadNPC(Player p, boolean reloadByConfig, boolean forceLoad, boolean debugMode) {

		for(String key : mainInstance.npcKeys.getKeys(false)) {

			NPCGlobal npc = (NPCGlobal)getNPCByConfig(key).get(0); // Récupère le NPC Global
			String npcReg = "NPC." + key; // Récupère la section du NPC en question

													/* --------------------------------------------------- */
													/* --------------------------------------------------- */

			if(reloadByConfig || mainInstance.NPCS.get(key.toUpperCase()) == null) {

				if(!getEquipment(key, "MAINHAND").isEmpty()) npc.setItem(EquipmentSlot.MAINHAND, ItemStack.deserialize(getEquipment(key, "MAINHAND")));
				if(!getEquipment(key, "OFFHAND").isEmpty()) npc.setItem(EquipmentSlot.OFFHAND, ItemStack.deserialize(getEquipment(key, "OFFHAND")));

				if(!getEquipment(key, "HEAD").isEmpty()) npc.setItem(EquipmentSlot.HEAD, ItemStack.deserialize(getEquipment(key, "HEAD")));
				if(!getEquipment(key, "CHESTPLATE").isEmpty()) npc.setItem(EquipmentSlot.CHEST, ItemStack.deserialize(getEquipment(key, "CHESTPLATE")));
				if(!getEquipment(key, "LEGGINGS").isEmpty()) npc.setItem(EquipmentSlot.LEGS, ItemStack.deserialize(getEquipment(key, "LEGGINGS")));
				if(!getEquipment(key, "BOOTS").isEmpty()) npc.setItem(EquipmentSlot.FEET, ItemStack.deserialize(getEquipment(key, "BOOTS")));

				 						/* --------------------------------------------------- */

				String texture = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".Skin.Texture"); // Récupère la Texture du Skin Enregistrer
				String signature = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".Skin.Signature"); // Récupère la Signature du Skin Enregistrer

				NPC.Skin skinNPC; // Variable permettant de récupérer le Skin du NPC

				// ⬇️ Créer une texture customisée à partir du Skin enregistré, sinon on récupère le skin par défaut du NPC ⬇️ //
				if(texture != null && signature != null) skinNPC = NPC.Skin.createCustomTextureSkin(texture, signature);
				else skinNPC = npc.getSkin();
				// ⬆️ Créer une texture customisée à partir du Skin enregistré, sinon on récupère le skin par défaut du NPC ⬆️ //

				npc.setSkin(skinNPC); // Ajoute le Skin au NPC

				 						/* --------------------------------------------------- */

				String CAPE = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".Data.SkinStatus.CAPE");
				String JACKET = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".Data.SkinStatus.JACKET");
				String LEFT_SLEEVE = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_SLEEVE");
				String RIGHT_SLEEVE = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_SLEEVE");
				String LEFT_PANTS = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".Data.SkinStatus.LEFT_PANTS");
				String RIGHT_PANTS = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".Data.SkinStatus.RIGHT_PANTS");
				String HAT = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".Data.SkinStatus.HAT");

				if(CAPE != null) npc.getSkinParts().setCape(Boolean.parseBoolean(CAPE));
				if(JACKET != null) npc.getSkinParts().setJacket(Boolean.parseBoolean(JACKET));
				if(LEFT_SLEEVE != null) npc.getSkinParts().setLeftSleeve(Boolean.parseBoolean(LEFT_SLEEVE));
				if(RIGHT_PANTS != null) npc.getSkinParts().setRightSleeve(Boolean.parseBoolean(RIGHT_SLEEVE));
				if(LEFT_PANTS != null) npc.getSkinParts().setLeftPants(Boolean.parseBoolean(LEFT_PANTS));
				if(RIGHT_PANTS != null) npc.getSkinParts().setRightPants(Boolean.parseBoolean(RIGHT_PANTS));
				if(HAT != null) npc.getSkinParts().setHat(Boolean.parseBoolean(HAT));

				 						/* --------------------------------------------------- */

				String pose = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".Data.Pose");
				if(pose != null) npc.setPose(Pose.valueOf(pose));

				 						/* --------------------------------------------------- */

				if(ConfigFile.getBoolean(mainInstance.NPCConfig, npcReg + ".FollowPlayerWithHead")) npc.setGazeTrackingType(NPC.GazeTrackingType.NEAREST_PLAYER);

				 						/* --------------------------------------------------- */

				String interactSender = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".InteractEvent.Sender");
				String interactMessage = ConfigFile.getString(mainInstance.NPCConfig, npcReg + ".InteractEvent.Message");

				npc.resetClickActions(); // Réinitialise les messages customisés

				if(interactSender != null && interactMessage != null) {

					String customName = ChatFormatting.DARK_GRAY.toString() + ChatFormatting.ITALIC.toString() + interactSender;
					String format = ChatFormatting.WHITE.toString() + ChatFormatting.ITALIC.toString() + " >> " + ChatFormatting.GRAY.toString() +
							ChatFormatting.ITALIC.toString() + interactMessage;

					String messageConverted = EmojiUtils.emojify(customName + format); // Essaie de convertir le texte à envoyer pour le NPC

					npc.setCustomName(interactSender); // Ajoute le nom customisé du NPC
					npc.addMessageClickAction(messageConverted); // Redéfinit le message customisé en question

				} else {

					String customName = ChatFormatting.DARK_GRAY.toString() + ChatFormatting.ITALIC.toString() + "Inconnu(e)";
					String format = ChatFormatting.WHITE.toString() + ChatFormatting.ITALIC.toString() + " >> " + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "...";

					npc.setCustomName(interactSender); // Ajoute le nom customisé du NPC
					npc.addMessageClickAction(customName + format); // Redéfinit le message customisé en question du NPC

					ConfigFile.set(mainInstance.NPCConfig, npcReg + ".InteractEvent.Sender", "Inconnu(e)");
					ConfigFile.set(mainInstance.NPCConfig, npcReg + ".InteractEvent.Message", "...");
				}

				npc.setSkin(skinNPC); // Ajoute le Skin au NPC (une deuxième fois, pour s'assurer que le NPC a le bon Skin
			}

			npc.getNPCUtils().getPluginManager(npc.getPlugin()).setUpdateGazeTicks(1); // Change le nombre de ticks pour le mouvement du NPC

				 						/* --------------------------------------------------- */
				 						/* --------------------------------------------------- */

			if(p == null) npc.forceUpdate(); // On force la mise à jour du NPC pour tous les Joueurs si le Joueur en paramètre est 'null'
			else {

				// Si le Joueur demandé ne répond pas à l'éxigence pour la visibilité du NPC et qu'il fait partie des joueurs associés au NPC, on le retire
				if(npc.hasPlayer(p) && npc.meetsVisibilityRequirement(p)) npc.removePlayer(p);
				if(!npc.hasPlayer(p)) npc.addPlayer(p); // Si le Joueur demandé ne fait pas partie des joueurs associés au NPC, on lui met

				npc.forceUpdate(p); // Sinon, on force la mise à jour du NPC le Joueur en question
			}

				 						/* --------------------------------------------------- */
				 						/* --------------------------------------------------- */

			// Initialise tous les Animations du NPC //
			RunAnimationNPC runnableNPC = NPCEntity.runAnimation(npc);
			BukkitTask task = runnableNPC.getTask();
			runnableNPC.updateConfig(reloadByConfig);
			if(task == null || task.isCancelled()) runnableNPC.run();
			// Initialise tous les Animations du NPC //

			mainInstance.NPCS.putIfAbsent(key.toUpperCase(), npc);
		}

		NPCUtils.getInstance().onEnable(mainInstance, debugMode); // Active la librairie NPC
		if(forceLoad) loadNPC(p, reloadByConfig, false, false); // Recharge encore une fois les NPCs, si on a demandé de forcer le rechargement des NPCs
	}

	/**
	 * Décharge tous les 'NPC' enregistrés dans le Serveur.
	 *
	 * @param p Le Joueur qui décharge les 'NPC' pour qu'il reçoive les modifications des NPCs.
	 * @param unSaveConfig Si oui ou non, on décharge le 'NPC' dans le fichier de configuration
	 */
	public static void unloadNPC(Player p, boolean unSaveConfig) {

		for(String key : mainInstance.npcKeys.getKeys(false)) {

			NPCGlobal npc = (NPCGlobal)getNPCByConfig(key).get(0); // Récupère le NPC Global
            mainInstance.NPCS.remove(key.toUpperCase());

													/* ---------------------------------- */

			if(unSaveConfig) unSave_NPC(npc.getCode()); // Si cela a été demandé, on décharge le NPC dans son fichier de configuration

													/* ---------------------------------- */

			if(p == null) NPCUtils.getInstance().removeGlobalNPC(npc); // Supprime le NPC Global si le Joueur en paramètre est 'null'
			else {

				// Si le Joueur demandé ne répond pas à l'éxigence pour la visibilité du NPC et qu'il fait partie des joueurs associés au NPC, on le retire
				if(npc.hasPlayer(p) && npc.meetsVisibilityRequirement(p)) npc.removePlayer(p);
			}

			NPCUtils.getInstance().onDisable(mainInstance); // Désactive la librairie NPC
		}
	}


	/**
	 * Renvoie le 'NPC' en question à partir de son ID enregistré dans le fichier de configuration des 'NPCs'.
	 *
	 * @param key L'Identifiant dans le fichier de configuration du 'NPC'.
	 *
	 * @return Le 'NPC' en question à partir de son ID enregistré dans le fichier de configuration des 'NPCs'
	 */
	private static List<Object> getNPCByConfig(String key) {

		NPCGlobal npc; // Permettra de récupérer le 'NPC'
		if(StringUtil.isNullOrEmpty(key)) return null;

		final double x = ConfigFile.getDouble(mainInstance.NPCConfig, "NPC." + key + ".Location.x");
		final double y = ConfigFile.getDouble(mainInstance.NPCConfig, "NPC." + key + ".Location.y");
		final double z = ConfigFile.getDouble(mainInstance.NPCConfig, "NPC." + key + ".Location.z");
		final float Yaw = Float.parseFloat(ConfigFile.getString(mainInstance.NPCConfig, "NPC." + key + ".Location.Yaw"));
		final float Pitch = Float.parseFloat(ConfigFile.getString(mainInstance.NPCConfig, "NPC." + key + ".Location.Pitch"));
		final String worldName = ConfigFile.getString(mainInstance.NPCConfig, "NPC." + key + ".World");

		final Location loc = new Location(Bukkit.getServer().getWorld(worldName), x, y, z, Yaw, Pitch);

		if(mainInstance.NPCS.containsKey(key.toUpperCase())) npc = mainInstance.NPCS.get(key.toUpperCase());
		else {

			NPCGlobal npcGlobal = NPCUtils.getInstance().getGlobalNPC(mainInstance, key.toUpperCase());
			if(npcGlobal != null) npc = npcGlobal;
			else { npc = NPCUtils.getInstance().generateGlobalNPC(mainInstance, key.toUpperCase(), loc); /* Créer une nouvelle instance du nouveau 'NPC' Global */ }
		}

		return List.of(npc, loc);
	}

	/**
	 * Renvoie tous les 'NPCs' d'un joueur (si le joueur est null, on récupère tous les 'NPCs') dans le fichier de configuration des 'NPCs'.
	 *
	 *
	 * @return Tous les 'NPCs' demandé depuis le fichier de configuration des 'NPCs'
	 */
	@SuppressWarnings("unchecked")
	public static Map<NPCGlobal, Location> getNPCsByConfig(Player p) {

		Map<NPCGlobal, Location> npcs = new HashMap<>(); // Liste récupérant les NPCs à récupérer
		ConfigurationSection npcKeys = ConfigFile.getConfigurationSection(mainInstance.NPCConfig, "NPC"); // On récupère la section de configuration des NPCs

		// Si la section de configuration des NPCs est null, on retourne donc 'null'
		if(npcKeys == null) return null;

		// Sinon, on essaie de vérifier dans la section de configuration chaque NPCs en fonction de la demande faite
		else {

			// ⬇️ Pour tous les NPCs enregistrés, on vérifie si l'un est associé au joueur en question, alors on récupère dans la liste le NPC en question ⬇️ //
			npcKeys.getKeys(false).forEach(key -> {

				// ⬇️ On vérifie si le NPC ayant l'identifiant en question est bien enregistré, alors on vérifie s'il est bien associé au joueur en question pour le récupérer ⬇️ //
				if(ConfigFile.getConfigurationSection(mainInstance.NPCConfig,"NPC." + key) != null) {

					/* ⬇️ Récupère une liste de joueurs du NPC en question enregistrés dans le fichier et on vérifie si elle a bien des enregistrements à l'intérieur,
						   pour pouvoir ensuite continuer la vérification ⬇️ */
					List<Object> listNPC = getNPCByConfig(key);
					NPCGlobal npc = (NPCGlobal)listNPC.get(0); // Récupère le NPC Global
					Location location = (Location)listNPC.get(1); // Récupère le NPC Global

					// Si le joueur récupère en paramètre n'est pas null, on récupère tous les NPCs associé à ce joueur en question
					if(p != null) {

						List<Player> playerList = (List<Player>)ConfigFile.getList(mainInstance.NPCConfig, "NPC." + key + ".Players");

						// Si le fichier de configuration contient une liste de Joueurs pour le NPC actuel, on vérifie si le joueur s'il est associé au NPC
						if(playerList != null && !playerList.isEmpty()) {

							// ⬇️ Si la liste des joueurs associée à ce NPC contient le joueur récupéré en paramètre, on l'ajoute donc à la liste des NPCs à récupérer //
							if(playerList.contains(p)) {

								// Ajoute le NPC en question dans la liste des NPCs à récupérer avec sa localisation, s'il n'y est pas déjà
								npcs.putIfAbsent(npc, location);
							}
							// ⬆️ Si la liste des joueurs associée à ce NPC contient le joueur récupéré en paramètre, on l'ajoute donc à la liste des NPCs à récupérer ⬆️ //

						// Sinon, on vérifie si le npc contient le joueur en question comme joueur associé
						} else {

							// si le NPC contient le joueur en question, ajoute le NPC en question dans la liste des NPCs à récupérer avec sa localisation, s'il n'y est pas déjà
							if(npc.hasPlayer(p)) { npcs.putIfAbsent(npc, location); }
						}

					// Sinon, on récupère tous les NPCs enregistrés un par un avec leur localisation
					} else npcs.putIfAbsent(npc, location);

					/* ⬆️ Récupère une liste de joueurs du NPC en question enregistrés dans le fichier et on vérifie si elle a bien des enregistrements à l'intérieur,
						   pour pouvoir ensuite continuer la vérification ⬆️ */
				}
				// ⬆️ On vérifie si le NPC ayant l'identifiant en question est bien enregistré, alors on vérifie s'il est bien associé au joueur en question pour le récupérer ⬆️ //
			});
			// ⬆️ Pour tous les NPCs enregistrés, on vérifie si l'un est associé au joueur en question, alors on récupère dans la liste le NPC en question ⬆️ //

			return npcs; // Retourne la liste en question
		}
	}


	/***********************************/
	/* PARTIE CONFIGURATION DES 'NPC' */
	/*********************************/
}
