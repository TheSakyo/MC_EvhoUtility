package fr.TheSakyo.EvhoUtility.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serial;
import java.lang.reflect.Type;
import java.util.*;

import dependancies.org.jsoup.helper.Validate;
import fr.TheSakyo.EvhoUtility.utils.api.Advancements.AdvancementDisplay;
import fr.TheSakyo.EvhoUtility.utils.api.Advancements.AdvancementVisibility;
import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.Advancements.Advancement;
import fr.TheSakyo.EvhoUtility.utils.api.Advancements.NameKey;
import fr.TheSakyo.EvhoUtility.utils.api.Advancements.SaveMethod;
import fr.TheSakyo.EvhoUtility.utils.api.Advancements.exception.UnloadProgressFailedException;

public final class AdvancementsManager {
	
	// Variables Utiles //
	
	private static final HashMap<String, AdvancementsManager> accessible = new HashMap<>();
	
	private boolean hiddenBoolean = false;
    private final String criterionNamespace = "minecraft";
	private final String criterionKey = "impossible";
	
	private static final HashMap<NameKey, Float> smallestY = new HashMap<>();
	private static final HashMap<NameKey, Float> smallestX = new HashMap<>();
	
	private final ArrayList<UUID> playersUUID;
	private final ArrayList<Advancement> advancements = new ArrayList<>();
	
	private static Gson gson;
	private static Type progressListType;
	
	private static final Map<String, NameKey> openedTabs = new HashMap<>();
	
	public static UUID CHAT_MESSAGE_UUID = new UUID(0, 0);
	
	
	// Variables Utiles //

	
	/**
	 * 
	 * @param players Tous les joueurs qui devraient être dans le nouveau manager dès le début, peuvent être changés à tout moment.
	 */
	public AdvancementsManager(Player... players) {
		
		this.playersUUID = new ArrayList<>();
		
		if(players == null) { return; }
		for(Player player : players) { this.addPlayer(player); }
	}


  /***************************************************************/
  /* PARTIE AJOUT/SUPPRESSION ACHIEVEMENTS 'ACHIEVEMENT MANAGER' */
  /***************************************************************/	

	/**
	 * Ajoute des achievements ou met à jour un achievement
	 * 
	 * @param player Joueur auquel on doit lui ajouter un achievement
	 * @param advancementsAdded Tableau de tous les achievements qui devraient être ajoutés
	 * Si vous voulez mettre à jour l'affichage d'un achievement, le tableau doit avoir une longueur de 1.
	 */
	public void addAdvancement(Player player, Advancement... advancementsAdded) {
		
		HashMap<UUID, Collection<net.minecraft.advancements.Advancement>> advancementsList = new HashMap<>();
		
		Set<ResourceLocation> remove = new HashSet<>();
		HashMap<UUID, Map<ResourceLocation, AdvancementProgress>> progressList = new HashMap<>();
		
		HashSet<NameKey> updatedTabs = new HashSet<>();
		
		for(Advancement adv : advancementsAdded) {
			
			float smallestY = getSmallestY(adv.getTab());
			float y = adv.getDisplay().generateY();
			
			if(y < smallestY) {
				
				smallestY = y;
				updatedTabs.add(adv.getTab());
				AdvancementsManager.smallestY.put(adv.getTab(), smallestY);
			}
			
			float smallestX = getSmallestX(adv.getTab());
			float x = adv.getDisplay().generateY();
			
			if(x < smallestX) {
				smallestX = x;
				updatedTabs.add(adv.getTab());
				AdvancementsManager.smallestX.put(adv.getTab(), smallestX);
			}
		}
		
		for(NameKey key : updatedTabs) { update(player, key); }
		
		for(Advancement advancement : advancementsAdded) {
			
			if(advancements.contains(advancement)) { remove.add(advancement.getName().getResourceLocation()); }
			
			else { advancements.add(advancement); }

			/************************************************/

			AdvancementDetails advancementDetails = prepareAdvancementsDetail(advancement);

			/***************************************/

			DisplayInfo saveDisplay = new DisplayInfo(advancementDetails.icon, advancementDetails.display.getTitle().getBaseComponent(), advancementDetails.display.getDescription().getBaseComponent(), advancementDetails.backgroundTexture,
					advancementDetails.display.getFrame().getNMS(), advancementDetails.display.isToastShown(), advancementDetails.display.isAnnouncedToChat(), true);
			saveDisplay.setLocation(advancementDetails.display.generateX() - getSmallestY(advancement.getTab()), advancementDetails.display.generateY() - getSmallestX(advancement.getTab()));

			/***************************************/

			net.minecraft.advancements.Advancement saveAdv = new net.minecraft.advancements.Advancement(advancement.getName().getResourceLocation(), advancement.getParent() == null ? null : advancement.getParent().getSavedAdvancement(), saveDisplay,
					advancementDetails.advRewards, advancementDetails.advancementData.advCriteria, advancementDetails.advancementData.advRequirements, false);
			advancement.saveAdvancement(saveAdv);

			Map<ResourceLocation, AdvancementProgress> prgs = progressList.containsKey(player.getUniqueId()) ? progressList.get(player.getUniqueId()) : new HashMap<>();
			checkAwarded(player, advancement);

			/************************************************/

			boolean showToast = advancementDetails.display.isToastShown() && getCriteriaProgress(player, advancement) < advancement.getSavedCriteria().size();
			
			Collection<net.minecraft.advancements.Advancement> advs = advancementsList.containsKey(player.getUniqueId()) ? advancementsList.get(player.getUniqueId()) : new ArrayList<>();
			
			boolean hidden = !advancementDetails.display.isVisible(player, advancement);
			advancement.saveHiddenStatus(player, hidden);
			
			if(!hidden || hiddenBoolean) {
				
				DisplayInfo advDisplay = new DisplayInfo(advancementDetails.icon, advancementDetails.display.getTitle().getBaseComponent(), advancementDetails.display.getDescription().getBaseComponent(), advancementDetails.backgroundTexture,
						advancementDetails.display.getFrame().getNMS(), showToast, advancementDetails.display.isAnnouncedToChat(), hidden && hiddenBoolean);
				advDisplay.setLocation(advancementDetails.display.generateX() - getSmallestX(advancement.getTab()), advancementDetails.display.generateY() - getSmallestY(advancement.getTab()));

				/***************************************/

				net.minecraft.advancements.Advancement adv = new net.minecraft.advancements.Advancement(advancement.getName().getResourceLocation(), advancement.getParent() == null ? null : advancement.getParent().getSavedAdvancement(), advDisplay,
						advancementDetails.advRewards, advancementDetails.advancementData.advCriteria, advancementDetails.advancementData.advRequirements, false);
				advs.add(adv);
				advancementsList.put(player.getUniqueId(), advs);

				/************************************************/

				AdvancementProgress advPrg = advancement.getProgress(player);
				advPrg.update(advancementDetails.advancementData.advCriteria, advancementDetails.advancementData.advRequirements);
				
				for(String criterion : advancement.getAwardedCriteria().get(player.getUniqueId().toString())) {
					
				  CriterionProgress criteriaPrg = advPrg.getCriterion(criterion);
				  if(criteriaPrg != null) criteriaPrg.grant();
				}
				
				advancement.setProgress(player, advPrg);
				
				prgs.put(advancement.getName().getResourceLocation(), advPrg);
				
				progressList.put(player.getUniqueId(), prgs);
			}
		}
			
		//Packet
		ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, advancementsList.get(player.getUniqueId()), remove, progressList.get(player.getUniqueId()));
		PlayerEntity.sendPacket(packet, player);
	}
	
	
	
	/**
	 * Supprime un achievement du gestionnaire
	 * 
	 * @param player Joueur auquel on doit lui retirer un achievement
	 * @param advancementsRemoved Tableau des achievements qui devraient être supprimés
	 */
	public void removeAdvancement(Player player, Advancement... advancementsRemoved) {
		
		Collection<net.minecraft.advancements.Advancement> advs = new ArrayList<>();
		
		Set<ResourceLocation> remove = new HashSet<>();
		Map<ResourceLocation, AdvancementProgress> prgs = new HashMap<>();
		
		for(Advancement advancement : advancementsRemoved) {
			
			if(advancements.contains(advancement)) {
				
			  advancements.remove(advancement);
			
			  remove.add(advancement.getName().getResourceLocation());
			}
		}
		
		//Packet
		ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, advs, remove, prgs);
		PlayerEntity.sendPacket(packet, player);
	}
	
  /***************************************************************/
  /* PARTIE AJOUT/SUPPRESSION ACHIEVEMENTS 'ACHIEVEMENT MANAGER' */ 
  /***************************************************************/	

	
	
  /**********************************************************/
  /* PARTIE AJOUT/SUPPRESSION JOUEURS 'ACHIEVEMENT MANAGER' */ 
  /**********************************************************/

  	/**
	 * Vérifie si le joueur est dans le gestionnaire
	 *
	 * @param player Joueur qu'il faut vérifier
	 *
	 * @return Une Valeur Booléenne
	 */
	public boolean hasPlayer(Player player) {

	  Validate.notNull(player);
	  return this.getPlayers().contains(player.getUniqueId());
	}

	/**
	 * Ajoute un joueur au gestionnaire
	 * 
	 * @param player Joueur à ajouter
	 */
	public void addPlayer(Player player) {
		
	  Validate.notNull(player);
	  addPlayer(player, null);
	}
	
	
	private void addPlayer(Player player, NameKey tab) {
		
		if(!playersUUID.contains(player.getUniqueId())) { playersUUID.add(player.getUniqueId()); }
		
		Collection<net.minecraft.advancements.Advancement> advs = new ArrayList<>();
		Set<ResourceLocation> remove = new HashSet<>();
		Map<ResourceLocation, AdvancementProgress> prgs = new HashMap<>();
		
		for(Advancement advancement : advancements) {
			
			boolean isTab = tab != null && advancement.getTab().isSimilar(tab);
			
			if(isTab) { remove.add(advancement.getName().getResourceLocation()); }
			
			if(tab == null || isTab) {
				
				//Critère
				checkAwarded(player, advancement);
				
				AdvancementDisplay display = advancement.getDisplay();
				
				boolean showToast = display.isToastShown() && getCriteriaProgress(player, advancement) < advancement.getSavedCriteria().size();
				
				ItemStack icon = CraftItemStack.asNMSCopy(display.getIcon());
				
				ResourceLocation backgroundTexture = null;
				boolean hasBackgroundTexture = display.getBackgroundTexture() != null;
				
				if(hasBackgroundTexture) { backgroundTexture = new ResourceLocation(display.getBackgroundTexture()); }
				
				boolean hidden = !display.isVisible(player, advancement);
				advancement.saveHiddenStatus(player, hidden);
				
				if(!hidden || hiddenBoolean) {
					
					DisplayInfo advDisplay = new DisplayInfo(icon, display.getTitle().getBaseComponent(), display.getDescription().getBaseComponent(), backgroundTexture, display.getFrame().getNMS(), showToast, display.isAnnouncedToChat(), hidden && hiddenBoolean);
					advDisplay.setLocation(display.generateX() - getSmallestX(advancement.getTab()), display.generateY() - getSmallestY(advancement.getTab()));
					
					AdvancementRewards advRewards = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], null);
					AdvancementData advancementData = handleAdvancement(advancement);

					/************************************************/

					net.minecraft.advancements.Advancement adv = new net.minecraft.advancements.Advancement(advancement.getName().getResourceLocation(), advancement.getParent() == null ? null : advancement.getParent().getSavedAdvancement(), advDisplay, advRewards, advancementData.advCriteria, advancementData.advRequirements, false);
					advs.add(adv);
					
					AdvancementProgress advPrg = advancement.getProgress(player);
					advPrg.update(advancement.getSavedCriteria(), advancement.getSavedCriteriaRequirements());
					
					/************************************************/

					for(String criterion : advancement.getAwardedCriteria().get(player.getUniqueId().toString())) {
						
					  CriterionProgress criteriaPrg = advPrg.getCriterion(criterion);
					  if(criteriaPrg != null) criteriaPrg.grant();
					}
					
					advancement.setProgress(player, advPrg);
					
					prgs.put(advancement.getName().getResourceLocation(), advPrg);
				}
			}
			
		}
		
		//Packet
		ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, advs, remove, prgs);
		PlayerEntity.sendPacket(packet, player);
	}
	
	
	
	/**
	 * Supprime un joueur du gestionnaire
	 * 
	 * @param player Joueur à supprimer
	 */
	public void removePlayer(Player player) {

        playersUUID.remove(player.getUniqueId());
		
		Collection<net.minecraft.advancements.Advancement> advs = new ArrayList<>();
		
		Set<ResourceLocation> remove = new HashSet<>();
		Map<ResourceLocation, AdvancementProgress> prgs = new HashMap<>();
		
		for(Advancement advancement : advancements) { remove.add(advancement.getName().getResourceLocation()); }
		
		//Packet
		ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, advs, remove, prgs);
		PlayerEntity.sendPacket(packet, player);
	}

  /**********************************************************/
  /* PARTIE AJOUT/SUPPRESSION JOUEURS 'ACHIEVEMENT MANAGER' */
  /**********************************************************/

	
	
	
  /*********************************************/
  /* PARTIE ONGLET ACTIF 'ACHIEVEMENT MANAGER' */ 
  /*********************************************/
	
	/**
	 *
	 * Récupère l'onglet actif
	 * 
	 * @param player Joueur à vérifier
	 * @return Tablist actif
	 */
	private static NameKey getActiveTab(Player player) { return openedTabs.get(player.getName()); }
	
	
	
	/**
	 * Efface le tablist actif
	 * 
	 * @param player Joueur dont la tablist doit être effacée
	 */
	private static void clearActiveTab(Player player) { setActiveTab(player, null, true); }
	
	
	
	
	// Méthode rapide qui créer un onglet actif //
	private static void setActiveTab(Player player, NameKey rootAdvancement, boolean update) {
		
		if(update) {

			ClientboundSelectAdvancementsTabPacket packet = new ClientboundSelectAdvancementsTabPacket(rootAdvancement == null ? null : rootAdvancement.getResourceLocation());
			PlayerEntity.sendPacket(packet, player);
		}
		openedTabs.put(player.getName(), rootAdvancement);
	}
	// Méthode rapide qui créer un onglet actif //
	
  
	
	/**
	 * Définit le tablist actif
	 * 
	 * @param player Joueur dont le tablist doit être modifiée
	 * @param rootAdvancement Nom du tablist à modifier
	 */
	public static void setActiveTab(Player player, String rootAdvancement) { setActiveTab(player, new NameKey(rootAdvancement), true); }

  /*********************************************/
  /* PARTIE ONGLET ACTIF 'ACHIEVEMENT MANAGER' */
  /*********************************************/
	
	
	
	
  /********************************************/
  /* PARTIE MISE A JOUR 'ACHIEVEMENT MANAGER' */ 
  /********************************************/	
	
	
	/**
	 * Met à jour/rafraîchit le joueur
	 * 
	 * @param player Joueur à mettre à jour
	 * @param tab Tablist à mettre à jour
	 */
	private void update(Player player, NameKey tab) {
		
		if(playersUUID.contains(player.getUniqueId())) {
			
			NameKey rootAdvancement = getActiveTab(player);
			clearActiveTab(player);
			addPlayer(player, tab);

			/*****************************************************/
			
			Bukkit.getScheduler().runTaskLater(UtilityMain.getInstance(), () -> setActiveTab(player, rootAdvancement, true), 5);
		}
	}

	
	// Méthode rapide pour mettre à jour une progression d'un achievement //
	private void updateProgress(Player player, boolean alreadyGranted, boolean fireEvent, Advancement... advancementsUpdated) {
		
		if(playersUUID.contains(player.getUniqueId())) {
			
			Collection<net.minecraft.advancements.Advancement> advs = new ArrayList<>();
			
			Set<ResourceLocation> remove = new HashSet<>();
			Map<ResourceLocation, AdvancementProgress> prgs = new HashMap<>();
			
			for(Advancement advancement : advancementsUpdated) {
				
				if(advancements.contains(advancement)) {
					
					checkAwarded(player, advancement);
					
					AdvancementProgress advPrg = advancement.getProgress(player);
					boolean hidden = advancement.getHiddenStatus(player);
					
					
					advPrg.update(advancement.getSavedCriteria(), advancement.getSavedCriteriaRequirements());
					
					HashSet<String> awarded = advancement.getAwardedCriteria(player);
					
					for(String criterion : advancement.getSavedCriteria().keySet()) {

                        CriterionProgress criteriaPrg = advPrg.getCriterion(criterion);

						/*******************************/

						if(awarded.contains(criterion) && criteriaPrg != null) criteriaPrg.grant();
						else if(criteriaPrg != null) criteriaPrg.revoke();
					}
					
					advancement.setProgress(player, advPrg);
					prgs.put(advancement.getName().getResourceLocation(), advPrg);
					
					if(hidden && advPrg.isDone()) {
						
						AdvancementDetails advancementDetails = prepareAdvancementsDetail(advancement);

						/************************************************/

						DisplayInfo advDisplay = new DisplayInfo(advancementDetails.icon, advancementDetails.display.getTitle().getBaseComponent(), advancementDetails.display.getDescription().getBaseComponent(),  advancementDetails.backgroundTexture,
								advancementDetails.display.getFrame().getNMS(), advancementDetails.display.isToastShown(), advancementDetails.display.isAnnouncedToChat(), hiddenBoolean);
						advDisplay.setLocation(advancementDetails.display.generateX() - getSmallestX(advancement.getTab()), advancementDetails.display.generateY() - getSmallestY(advancement.getTab()));

						/************************************************/

						net.minecraft.advancements.Advancement adv = new net.minecraft.advancements.Advancement(advancement.getName().getResourceLocation(), advancement.getParent() == null ? null : advancement.getParent().getSavedAdvancement(), advDisplay,
								advancementDetails.advRewards, advancementDetails.advancementData.advCriteria, advancementDetails.advancementData.advRequirements, false);
						advs.add(adv);
					}
					
					if(!alreadyGranted) {
						
						if(advPrg.isDone()) { grantAdvancement(player, advancement, true, false, fireEvent); }
					}
				}
			}
			
			ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, advs, remove, prgs);
			PlayerEntity.sendPacket(packet, player);
		}
	}
	// Méthode rapide pour mettre à jour une progression d'un achievement //
	

	/**
	 * Met à jour la progression d'un achievement d'un joueur
	 * 
	 * @param player Joueur à mettre à jour
	 * @param advancementsUpdated Tableau d'un achievement à mettre à jour.
	 */
	public void updateProgress(Player player, Advancement... advancementsUpdated) { updateProgress(player, false, true, advancementsUpdated); }

	
	
	/**
	 * Met à jour toutes les visibilités éventuellement affectées pour tous les parents et enfants.
	 * 
	 * @param player Joueur à mettre à jour
	 * @param from Achievement à vérifier à partir de
	 */
	private void updateAllPossiblyAffectedVisibilities(Player player, Advancement from) {
		
		List<Advancement> updated = from.getRow();
		
		for(Advancement adv : updated) { updateVisibility(player, adv); }
	}
	
	
	
	/**
	 * Met à jour la visibilité
	 * 
	 * @param player Joueur à mettre à jour
	 * @param advancement Achievement à mettre à jour
	 */
	private void updateVisibility(Player player, Advancement advancement) {
		
		if(playersUUID.contains(player.getUniqueId())) {
			
			Collection<net.minecraft.advancements.Advancement> advs = new ArrayList<>();
			
			Set<ResourceLocation> remove = new HashSet<>();
			Map<ResourceLocation, AdvancementProgress> prgs = new HashMap<>();
			
			if(advancements.contains(advancement)) {
				
				checkAwarded(player, advancement);
				
				AdvancementDisplay display = advancement.getDisplay();
				boolean hidden = !display.isVisible(player, advancement);
				
				if(hidden == advancement.getHiddenStatus(player)) { return; }
				
				advancement.saveHiddenStatus(player, hidden);
				
				if(!hidden || hiddenBoolean) {
					
					remove.add(advancement.getName().getResourceLocation());
					
					AdvancementRewards advRewards = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], null);

					ItemStack icon = CraftItemStack.asNMSCopy(display.getIcon());

					ResourceLocation backgroundTexture = null;
					boolean hasBackgroundTexture = display.getBackgroundTexture() != null;
					if(hasBackgroundTexture) { backgroundTexture = new ResourceLocation(display.getBackgroundTexture()); }

					/************************************************/

					AdvancementDetails advancementDetails = prepareAdvancementsDetail(advancement);

					/************************************************/

					boolean showToast = display.isToastShown();

					DisplayInfo advDisplay = new DisplayInfo(advancementDetails.icon, advancementDetails.display.getTitle().getBaseComponent(), advancementDetails.display.getDescription().getBaseComponent(), backgroundTexture,
							advancementDetails.display.getFrame().getNMS(), showToast, advancementDetails.display.isAnnouncedToChat(), hidden && hiddenBoolean);
					advDisplay.setLocation(advancementDetails.display.generateX() - getSmallestX(advancement.getTab()), advancementDetails.display.generateY() - getSmallestY(advancement.getTab()));


					/************************************/

					net.minecraft.advancements.Advancement adv = new net.minecraft.advancements.Advancement(advancement.getName().getResourceLocation(), advancement.getParent() == null ? null : advancement.getParent().getSavedAdvancement(), advDisplay, advRewards,
							advancementDetails.advancementData.advCriteria, advancementDetails.advancementData.advRequirements, false);
					advs.add(adv);
					
					AdvancementProgress advPrg = advancement.getProgress(player);
					advPrg.update(advancementDetails.advancementData.advCriteria, advancementDetails.advancementData.advRequirements);

					/************************************************/

					for(String criterion : advancement.getAwardedCriteria().get(player.getUniqueId().toString())) {
						
					  CriterionProgress criteriaPrg = advPrg.getCriterion(criterion);
					  if(criteriaPrg != null) criteriaPrg.grant();
					}
					
					advancement.setProgress(player, advPrg);
					prgs.put(advancement.getName().getResourceLocation(), advPrg);
				}
			}
			
			//Packet
			ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, advs, remove, prgs);
			PlayerEntity.sendPacket(packet, player);
		}
	}

  /********************************************/
  /* PARTIE MISE A JOUR 'ACHIEVEMENT MANAGER' */
  /********************************************/		
	
	
	
	
  /*******************************************************/
  /* PARTIE RÉCUPERATION/CRÉATION 'ACHIEVEMENT MANAGER' */
  /******************************************************/
	
	/**
	 * 
	 * @return Tous les joueurs qui ont été ajoutés au gestionnaire
	 */
	public ArrayList<UUID> getPlayers() {

		Iterator<UUID> it = playersUUID.iterator();

		while(it.hasNext()) {

			Player p = Bukkit.getServer().getPlayer(it.next());
			if(p == null || !p.isOnline()) { it.remove(); }
		}
		return playersUUID;
	}

	
	
	/**
	 * Obtient la cordonnée Y la plus petite d'un achievement
	 * 
	 * @return La cordonnée Y la plus petite
	 */
	private static float getSmallestY(NameKey key) { return smallestY.containsKey(key) ? smallestY.get(key) : 0; }
	
	
	
	/**
	 * Obtient la cordonnée X la plus petite d'un achievement
	 * 
	 * @return La cordonnée X la plus petite
	 */
	private static float getSmallestX(NameKey key) { return smallestX.containsKey(key) ? smallestX.get(key) : 0; }
	
	
	
	/**
	 * Définit le booléen qui est transmis via le paquet d'un achievement lorsqu'un achievement est caché Défaut : false
	 * Lorsqu'il est défini à true, les achievements cachés qui n'ont pas encore été accordés, auront une ligne dessinée vers eux même s'ils ne sont pas encore affichés, alors qu'ils devraient être visibles (selon leur {@link AdvancementVisibility})
	 * Peut être utilisé pour créer un onglet d'un achievement vide où il n'y a aucun achievement visible et aucune ligne visible, quand l'onglet a seulement un achievement caché comme racine
	 * 
	 * @param hiddenBoolean Le nouveau 'hiddenBoolean'
	 */
	public void setHiddenBoolean(boolean hiddenBoolean) { this.hiddenBoolean = hiddenBoolean; }
	
	
	
	/**
	 * Obtient le booléen qui est passé via le paquet d'un achievement lorsqu'un achievement est caché Default : false
	 * 
	 * @return un achievement caché (vrai ou faux)
	 */
	public boolean getHiddenBoolean() { return hiddenBoolean; }

	
	
	/**
	 * 
	 * @return Une liste de tous les achievements réalisés par le gestionnaire
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Advancement> getAdvancements() { return (ArrayList<Advancement>)advancements.clone(); }
	

	/**
	 * 
	 * @param namespace Espace de nom à vérifier
	 * @return Une liste de tous les achievements dans le gestionnaire avec un espace de nom spécifié
	 */
	public ArrayList<Advancement> getAdvancements(String namespace) {
		
		ArrayList<Advancement> advancements = getAdvancements();

		advancements.removeIf(adv -> !adv.getName().getNamespace().equalsIgnoreCase(namespace));
		return advancements;
	}

	
	/**
	 * 
	 * @param name Nom à vérifier
	 * @return Un achievement correspondant au nom donné ou null s'il n'existe pas dans l'AdvancementsManager
	 */
	public Advancement getAdvancement(NameKey name) {
		
		for(Advancement advancement : advancements) {
			
			if(advancement.hasName(name)) { return advancement; }
		}
		return null;
	}

	
	
	/**
	 * 
	 * @param player Joueur
	 * @param advancement Achievements
	 * @return Le critère de progression
	 */
	private int getCriteriaProgress(Player player, Advancement advancement) {
		
	  checkAwarded(player, advancement);
	  return advancement.getAwardedCriteria(player).size();
	}
	
	

	
	private String getSavePath(Player player, String namespace) {
	
	  return getSaveDirectory(namespace) + (UtilityMain.useUUID ? player.getUniqueId() : player.getName()) + ".json";
	}
	
	
	
	private String getSaveDirectory(String namespace) {
	
	  return UtilityMain.getInstance().getDataFolder().getAbsolutePath() + File.separator + "utils" + File.separator + "advancements" + File.separator + "saved_data" + File.separator + namespace + File.separator;
	}
	
	
	
	private File getSaveFile(Player player, String namespace) {
		
	  File file = new File(getSaveDirectory(namespace));
	  file.mkdirs();

	  return new File(getSavePath(player, namespace));
	}
	

	private String getSavePath(UUID uuid, String namespace) { return getSaveDirectory(namespace) + uuid + ".json"; }

	
	private HashMap<String, List<String>> getProgress(Player player, String namespace) {
		
		File saveFile = getSaveFile(player, namespace);
		
		try {
			
			FileReader os = new FileReader(saveFile);
			JsonElement element = JsonParser.parseReader(os);
			os.close();

			/****************************/

			check();
            return gson.fromJson(element, progressListType);
			
		} catch(Exception ex) {

			ex.printStackTrace(System.err);
			return new HashMap<>();
		}
	}


	private File getSaveFile(UUID uuid, String namespace) {
		
		File file = new File(getSaveDirectory(namespace));
		file.mkdirs();

		return new File(getSavePath(uuid, namespace));
	}

	
	
	// SAUVEGARDE/CHARGEMENT EN-LIGNE //
	
	/**
	 * 
	 * @param player Joueur à vérifier
	 * @return Une représentation sous forme de chaîne JSON de la progression d'un joueur
	 */
	public String getProgressJSON(Player player) {
		
		Map<String, List<String>> prg = new HashMap<>();

		/****************************************/

		for(Advancement advancement : getAdvancements()) { handleAdvancementSaveMethod(advancement, player, prg); }

		/****************************************/

		check();
        return gson.toJson(prg);
	}
	
	
	
	/**
	 * 
	 * @param player Joueur à vérifier
	 * @param namespace Espace de nom à vérifier
	 * @return Une représentation sous forme de chaîne JSON de la progression d'un joueur dans un espace de nom spécifié
	 */
	public String getProgressJSON(Player player, String namespace) {
		
		HashMap<String, List<String>> prg = new HashMap<>();

		/****************************************/

		for(Advancement advancement : getAdvancements()) {
			
			String anotherNamespace = advancement.getName().getNamespace();
			if(namespace.equalsIgnoreCase(anotherNamespace)) { handleAdvancementSaveMethod(advancement, player, prg); }
		}

		/****************************************/

		check();
        return gson.toJson(prg);
	}
	
	// SAUVEGARDE/CHARGEMENT EN-LIGNE //
	
	
	/**
	 * Renvoie le nom unique si AdvancementsManager est accessible.
	 * 
	 * @return Nom ou null si non accessible
	 */
	public String getName() {
		
		for(String name : accessible.keySet()) { if(accessible.get(name).equals(this)) return name; }
		return null;
	}
	
  /*******************************************************/
  /* PARTIE RÉCUPERATION/CRÉATION 'ACHIEVEMENT MANAGER' */
  /******************************************************/

	
	
  /*******************************************************/
  /* PARTIE AFFECTATION/RÉVOCATION 'ACHIEVEMENT MANAGER' */
  /*******************************************************/
	
	
	private void grantAdvancement(Player player, Advancement advancement, boolean alreadyGranted, boolean updateProgress, boolean fireEvent) {
		
		checkAwarded(player, advancement);
		Map<String, HashSet<String>> awardedCriteria = advancement.getAwardedCriteria();
		
		HashSet<String> awarded = advancement.getAwardedCriteria(player);
        awarded.addAll(advancement.getSavedCriteria().keySet());

		/**********************************/

		awardedCriteria.put(player.getUniqueId().toString(), awarded);
		advancement.setAwardedCriteria(awardedCriteria);

		/**********************************/
		
		if(updateProgress) {
			
		  updateProgress(player, alreadyGranted, false, advancement);
		  updateAllPossiblyAffectedVisibilities(player, advancement);
		}
	}
	
	
	/**
	 * Accorde des critères pour un achievement
	 * 
	 * @param player Récepteur
	 * @param advancement Achievement
	 * @param criteria Tableau de critères à accorder
	 */
	private void grantCriteria(Player player, Advancement advancement, String... criteria) {
		
		checkAwarded(player, advancement);
		
		Map<String, HashSet<String>> awardedCriteria = advancement.getAwardedCriteria();
		
		HashSet<String> awarded = advancement.getAwardedCriteria(player);

		Collections.addAll(awarded, criteria);
		
		awardedCriteria.put(player.getUniqueId().toString(), awarded);
		
		advancement.setAwardedCriteria(awardedCriteria);
		
		updateProgress(player, false, true, advancement);
		updateAllPossiblyAffectedVisibilities(player, advancement);
		
	}
	
	
	
	/**
	 * D�finit la progression des critères pour un achievement
	 * Peut ne pas fonctionner comme prévu lors de l'utilisation des fonctionnalités pour les experts
	 * 
	 * @param player Récepteur
	 * @param advancement Achievement
	 * @param progress Progression
	 */
	private void setCriteriaProgress(Player player, Advancement advancement, int progress) {
		
		checkAwarded(player, advancement);
		Map<String, HashSet<String>> awardedCriteria = advancement.getAwardedCriteria();
		
		HashSet<String> awarded = advancement.getAwardedCriteria(player);
		
		
		int difference = Math.abs(awarded.size() - progress);
		
		if(awarded.size() > progress) {
			
			//Compte à rebours de haut en bas
			int i = 0;
			for(String criterion : advancement.getSavedCriteria().keySet()) {
				
				if(i >= difference) break;
				
				if(awarded.contains(criterion)) { awarded.remove(criterion); i++; }
			}
			
		} else if(awarded.size() < progress) {
			
			//Compte à rebours de bas en haut
			int i = 0;
			for(String criterion : advancement.getSavedCriteria().keySet()) {
				
				if(i >= difference) break;
				
				if(!awarded.contains(criterion)) { awarded.add(criterion); i++; }
			}
		}
		
		awardedCriteria.put(player.getUniqueId().toString(), awarded);
		advancement.setAwardedCriteria(awardedCriteria);
		
		updateProgress(player, false, true, advancement);
		updateAllPossiblyAffectedVisibilities(player, advancement);
	}
	
  /*******************************************************/
  /* PARTIE AFFECTATION/RÉVOCATION 'ACHIEVEMENT MANAGER' */
  /*******************************************************/	
	
	
	
	
  /********************************************************/
  /* PARTIE CHARGEMENT/DÉCHARGEMENT 'ACHIEVEMENT MANAGER' */
  /********************************************************/
	
	// SAUVEGARDE/CHARGEMENT EN-LIGNE //
	
	
	/**
	 * Sauvegarde la progression
	 * 
	 * @param player Joueur à vérifier
	 * @param namespace Espace de nom à vérifier
	 */
	public void saveProgress(Player player, String namespace) {
		
		File saveFile = getSaveFile(player, namespace);
		
		String json = getProgressJSON(player, namespace);
		
		try {
			
			if(!saveFile.exists()) { saveFile.createNewFile(); }
			
			FileWriter w = new FileWriter(saveFile);
			
			w.write(json);
			w.close();
			
		} catch (Exception e) { e.printStackTrace(System.err); }
	}
	
	
	
	/**
	 * Charge tous les progréssion
	 * 
	 * @param player Joueur à vérifier
	 * @param namespace Espace de nom à vérifier
	 */
	public void loadProgress(Player player, String namespace) {
		
		File saveFile = getSaveFile(player, namespace);
		
		if(saveFile.exists() && saveFile.isFile()) {
			
			HashMap<String, List<String>> prg = getProgress(player, namespace);

			/******************************************/

			for(Advancement advancement : advancements) {
				
				if(advancement.getName().getNamespace().equalsIgnoreCase(namespace)) {
					
					checkAwarded(player, advancement);
					String nameKey = advancement.getName().toString();

					/****************************/

					if(prg.containsKey(nameKey)) {
						
						List<String> loaded = prg.get(nameKey);
						SaveMethod saveMethod = advancement.getSaveMethod();

						/****************************/

						if(saveMethod == SaveMethod.NUMBER) {
							
							if(loaded.size() == 2) {
								
								if(loaded.get(0).equals("NUM")) {
									
									try {
										
										int progress = Integer.parseInt(loaded.get(1));
										setCriteriaProgress(player, advancement, progress);

									//Utilise la méthode de chargement par défaut
									} catch(NumberFormatException e) { saveMethod = SaveMethod.DEFAULT; }

								//Utilise la méthode de chargement par défaut
								} else saveMethod = SaveMethod.DEFAULT;

							//Utilise la méthode de chargement par défaut
							} else saveMethod = SaveMethod.DEFAULT;
						}

						/****************************/

						if(saveMethod == SaveMethod.DEFAULT) { grantCriteria(player, advancement, loaded.toArray(new String[0])); }
					}
				}
			}
		}
	}
	

	/**
	 * Charge tous les progréssions
	 * 
	 * @param player Joueur à vérifier
	 * @param advancementsLoaded Tableau d'un achievement à vérifier, tous les achievements qui ne sont pas dans le même espace de nom que le premier seront ignorés
	 */
	public void loadProgress(Player player, Advancement... advancementsLoaded) {
		
		if(advancementsLoaded.length == 0) return;
		List<Advancement> advancements = Arrays.asList(advancementsLoaded);

		/*********************************/

		String namespace = advancements.get(0).getName().getNamespace();
		loadProgress(player, namespace);
	}

	// SAUVEGARDE/CHARGEMENT EN-LIGNE //

	
	
	// DÉCHARGEMENT DE LA PROGRESSION //
	
	/**
	 * Décharge la progression de tous les achievements dans le gestionnaire.
	 * Ne fonctionne pas pour les joueurs en ligne !
	 * 
	 * @param uuid UUID du joueur affecté
	 */
	public void unloadProgress(UUID uuid) {
		
		if(isOnline(uuid)) { throw new UnloadProgressFailedException(uuid); } 
		
		else {
			
			for(Advancement advancement : getAdvancements()) {
							
			  advancement.unsetProgress(uuid);
			  advancement.unsetAwardedCriteria(uuid);
			}
		}
	}
	
	
	
	/**
	 * Décharge la progression pour tous les achievements dans le gestionnaire avec un espace de nom spécifié
	 * Ne fonctionne pas pour les joueurs en ligne !
	 * 
	 * @param uuid UUID du joueur affecté
	 * @param namespace Espace de nom spécifique
	 */
	public void unloadProgress(UUID uuid, String namespace) {
		
		if(isOnline(uuid)) { throw new UnloadProgressFailedException(uuid); } 
		
		else {
			
			for(Advancement advancement : getAdvancements(namespace)) {
				
			  advancement.unsetProgress(uuid);
			  advancement.unsetAwardedCriteria(uuid);
			}
		}
	}
	
	
	/**
	 * Décharge la progression pour les achievements donnés
	 * Ne fonctionne pas pour les joueurs en ligne !
	 * 
	 * @param uuid UUID du joueur affecté
	 * @param advancements Achievements spécifiques
	 */
	public void unloadProgress(UUID uuid, Advancement... advancements) {
		
		if(isOnline(uuid)) { throw new UnloadProgressFailedException(uuid); }
		
		else {
			
			for(Advancement advancement : advancements) {
				
			  advancement.unsetProgress(uuid);
			  advancement.unsetAwardedCriteria(uuid);
			}
		}
	}

	// DÉCHARGEMENT DE LA PROGRESSION //
	
  /********************************************************/
  /* PARTIE CHARGEMENT/DÉCHARGEMENT 'ACHIEVEMENT MANAGER' */
  /********************************************************/

	

	
  /*********************************************/
  /* PARTIE VÉRIFICATION 'ACHIEVEMENT MANAGER' */
  /*********************************************/
	
	// Vérifie les achievements attribués à un joueur //
	private void checkAwarded(Player player, Advancement advancement) { this.checkAwarded(player.getUniqueId(), advancement); }
	// Vérifie les achievements attribués à un joueur //

	// Vérifie les achievements attribués à un joueur (avec paramètre uuid) //
	private void checkAwarded(UUID uuid, Advancement advancement) {

		Map<Advancement, Map<Map<String, Criterion>, String[][]>> initializedAdvancementCriteria = initAdvancementCriteria(advancement);
		if(!initializedAdvancementCriteria.keySet().isEmpty()) advancement = initializedAdvancementCriteria.keySet().iterator().next();

		/*******************************************/
		
		Map<String, HashSet<String>> awardedCriteria = advancement.getAwardedCriteria();
		if(!awardedCriteria.containsKey(uuid.toString())) { awardedCriteria.put(uuid.toString(), new HashSet<>()); }
	}
	// Vérifie les achievements attribués à un joueur (avec paramètre uuid) //

	// Méthode rapide de vérification (pour les class "getProgressJSON") //
	private static void check() {
		
		if(gson == null) { gson = new Gson(); }
		
		if(progressListType == null) {
			
		   progressListType = new TypeToken<HashMap<String, List<String>>>() { 
			 @Serial
			 private static final long serialVersionUID = 5832697137241815078L;
		   }.getType();
		}
	}
	// Méthode rapide de vérification (pour les class "getProgressJSON") //
	
	
	
	// Vérifie si le joueur est en ligne avec son uuid //
	private boolean isOnline(UUID uuid) {
		
	  Player player = Bukkit.getServer().getPlayer(uuid);
	  return player != null && player.isOnline();
	}
	// Vérifie si le joueur est en ligne avec son uuid //

  /*********************************************/
  /* PARTIE VÉRIFICATION 'ACHIEVEMENT MANAGER' */
  /*********************************************/

  /********************/
  /* MÉTHODES UTILES */
  /******************/
  
  private Map<Advancement, Map<Map<String, Criterion>, String[][]>> initAdvancementCriteria(Advancement advancement) {

	  String[][] advRequirements;
	  Map<String, Criterion> advCriteria = new HashMap<>();

	  /***********************/

	  if(advancement.getSavedCriteria() == null) {

		  for(int i = 0; i < advancement.getCriteria(); i++) {

              String criterionPrefix = "criterion.";
              advCriteria.put(criterionPrefix + i, new Criterion(new CriterionTriggerInstance() {

				  @Override
				  public JsonObject serializeToJson(SerializationContext arg0) { return null; }

				  @Override
				  public ResourceLocation getCriterion() { return new ResourceLocation(criterionNamespace, criterionKey); }

			  }));
		  }

		  advancement.saveCriteria(advCriteria);

	  } else { advCriteria = advancement.getSavedCriteria(); }

	  /***********************************************/

	  if(advancement.getSavedCriteriaRequirements() == null) {

		  ArrayList<String[]> fixedRequirements = new ArrayList<>();

		  for(String name : advCriteria.keySet()) { fixedRequirements.add(new String[] {name}); }

		  advRequirements = Arrays.stream(fixedRequirements.toArray()).toArray(String[][]::new);
		  advancement.saveCriteriaRequirements(advRequirements);

	  } else { advRequirements = advancement.getSavedCriteriaRequirements(); }

	  /***********************/

	  Map<Advancement, Map<Map<String, Criterion>, String[][]>> advancementMap = new HashMap<>();
	  Map<Map<String, Criterion>, String[][]> advancementCriteriaMap = new HashMap<>();

	  advancementCriteriaMap.putIfAbsent(advCriteria, advRequirements);
	  advancementMap.putIfAbsent(advancement, advancementCriteriaMap);

	  /***********************/

	  return advancementMap;
  }

  private void handleAdvancementSaveMethod(Advancement advancement, Player player, Map<String, List<String>> prg) {

	  String nameKey = advancement.getName().toString();
	  SaveMethod saveMethod = advancement.getSaveMethod();

	  /************************************************/

	  if(saveMethod == SaveMethod.NUMBER) {

		  int criteriaProgress = getCriteriaProgress(player, advancement);
		  ArrayList<String> progress = new ArrayList<>();

		  progress.add("NUM"); //Indicateur de la méthode d'enregistrement des numéros
		  progress.add("" + criteriaProgress);
		  prg.put(nameKey, progress);

	  } else {

		  ArrayList<String> progress = new ArrayList<>(advancement.getAwardedCriteria(player));
		  prg.put(nameKey, progress);
	  }
  }

  /********************************************************************/
  /********************************************************************/
  /********************************************************************/

  private AdvancementData handleAdvancement(Advancement advancement) {

	  String[][] advRequirements = new String[0][];
	  Map<String, Criterion> advCriteria = new HashMap<>();

	  /**********************************************************/

	  Map<Advancement, Map<Map<String, Criterion>, String[][]>> initializedAdvancementCriteria = initAdvancementCriteria(advancement);

	  /**********************************************************/

	  if (!initializedAdvancementCriteria.keySet().isEmpty()) {
		  advancement = initializedAdvancementCriteria.keySet().iterator().next();

		  if(initializedAdvancementCriteria.get(advancement) != null && initializedAdvancementCriteria.get(advancement).isEmpty()) {

			  Map<Map<String, Criterion>, String[][]> advCriteriaRequirements = initializedAdvancementCriteria.get(advancement);

			  if(!advCriteriaRequirements.keySet().isEmpty()) {
				  advCriteria = advCriteriaRequirements.keySet().iterator().next();
				  if (advCriteriaRequirements.get(advCriteria) != null)
					  advRequirements = advCriteriaRequirements.get(advCriteria);
			  }
		  }
	  }

	  /**********************************************************/

	  return new AdvancementData(advCriteria, advRequirements);
  }

  private AdvancementDetails prepareAdvancementsDetail(Advancement advancement) {

	  AdvancementDisplay display = advancement.getDisplay();
	  AdvancementRewards advRewards = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], null);
	  ItemStack icon = CraftItemStack.asNMSCopy(display.getIcon());

	  /************************************************/

	  ResourceLocation backgroundTexture = null;
	  boolean hasBackgroundTexture = display.getBackgroundTexture() != null;

	  if(hasBackgroundTexture) { backgroundTexture = new ResourceLocation(display.getBackgroundTexture()); }

	  /************************************************/

	  AdvancementData advancementData = handleAdvancement(advancement);
	  return new AdvancementDetails(display, advRewards, icon, backgroundTexture, advancementData);
  }
  /********************/
  /* MÉTHODES UTILES */
  /******************/

  private record AdvancementDetails(AdvancementDisplay display, AdvancementRewards advRewards, ItemStack icon, ResourceLocation backgroundTexture, AdvancementData advancementData) {}
  private record AdvancementData(Map<String, Criterion> advCriteria, String[][] advRequirements) {}
}

