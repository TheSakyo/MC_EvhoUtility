package fr.TheSakyo.EvhoUtility.utils.api.Advancements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Warning;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;


public class Advancement {
	
	// Variables Utiles //
	
	public abstract static class AdvancementReward { public abstract void onGrant(Player player); }
	
	private static final HashMap<String, Advancement> advancementMap = new HashMap<>();
	
	private transient NameKey name;
	@SerializedName("name")
	private String nameKey;
	
	private final AdvancementDisplay display;
	private SaveMethod saveMethod = SaveMethod.DEFAULT;
	
	private transient Advancement parent;
	@SerializedName("parent")
	private String parentKey;
	private transient HashSet<Advancement> children = new HashSet<>();
	
	@SerializedName("criteriaAmount")
	private int criteria = 1;
	
	
	public static UUID CHAT_MESSAGE_UUID = new UUID(0, 0);

	
	private transient AdvancementReward reward;
	
	
	private transient Map<String, HashSet<String>> awardedCriteria = new HashMap<>();
	private transient Map<String, AdvancementProgress> progress = new HashMap<>();

	// Variables Utiles //
	
	
	
	
	// Méthode rapide pour recharger un achievement aprés l'avoir généré //
	private void loadAfterGSON() {
		
		this.children = new HashSet<>();
		this.name = new NameKey(nameKey);
		
		advancementMap.put(nameKey, this);
		
		this.parent = advancementMap.get(parentKey);
		
		if(this.parent != null) this.parent.addChildren(this);
		
		this.display.setVisibility(AdvancementVisibility.parseVisibility(this.display.visibilityIdentifier));
	}
	// Méthode rapide pour recharger un achievement aprés l'avoir généré //
		
			
	
	/**
	 * Génère un achievement (avec String)
	 * 
	 * @param json Représentation JSON de l'instance {@link Advancement}.
	 * @return Génère {@link Advancement}
	 */
	public static Advancement fromJSON(String json) {
		
		Gson gson = new GsonBuilder().setLenient().create();
		Advancement created = gson.fromJson(json, Advancement.class);
		
		created.loadAfterGSON();
		return created;
	}
	
	
	
	/**
	 * Génère un achievement (avec JsonElement)
	 * 
	 * @param json Représentation JSON de l'instance {@link Advancement}.
	 * @return Génère {@link Advancement}
	 */
	public static Advancement fromJSON(JsonElement json) {
		Gson gson = new GsonBuilder().setLenient().create();
		Advancement created = gson.fromJson(json, Advancement.class);
		created.loadAfterGSON();
		return created;
	}
	
	

	
	/**
	 * 
	 * Ajoute un achievements enfant (pour le traçage de ligne)
	 */
	private void addChildren(Advancement adv) { children.add(adv); }
	
	
	
	/**
	 * 
	 * @return JSON représentation de l'instance {@link Advancement} actuelle
	 */
	public String getAdvancementJSON() {
		
	  Gson gson = new Gson();
	  return gson.toJson(this);
	}
	
	
	
	/**
	 * 
	 * @param parent Achievement du parent, utilisé pour tracer des lignes entre les différents Achievement.
	 * @param name Nom unique
	 * @param display affichage d'un achievement
	 */
	public Advancement(@Nullable Advancement parent, NameKey name, AdvancementDisplay display) {
		
	  this.parent = parent;
	  
	  if(this.parent != null) this.parent.addChildren(this);
	  
	  this.parentKey = parent == null ? null : parent.getName().toString();
	  
	  this.name = name;
	  this.nameKey = name.toString();
	  this.display = display;
	}
	
	
	
	/**
	 * 
	 * @return Récupère un achievement parent
	 */
	@Nullable
	public Advancement getParent() { return parent; }
	
	
	
	/**
	 * Définit le montant du critère requis
	 * 
	 * @param criteria Critère
	 */
	public void setCriteria(int criteria) {
		
	  this.criteria = criteria;
	  
	  savedCriteria = null;
	  savedCriterionNames = null;
	  savedCriteriaRequirements = null;
	}
	
	
	
	/**
	 * 
	 * @return Récupère le montant du critère
	 */
	public int getCriteria() { return criteria; }
	
	
	
	/**
	 * 
	 * @return Récupère le nom unique
	 */
	public NameKey getName() { return name; }
	
	
	
	/**
	 * Récupère l'affichage d'un achievement
	 * 
	 * @return l'affichage d'un achievement
	 */
	public AdvancementDisplay getDisplay() { return display; }
	
	
	
	/**
	 * Définit la méthode de sauvegarde/chargement <br>{@link 'SaveMethod.DEFAULT'} - Les valeurs des critères seront sauvegardées et chargées <br>{@link 'SaveMethod.NUMBER'} - Le numéro des critères sera sauvegardés et chargés
	 * 
	 * @param saveMethod La méthode de sauvegarde/chargement
	 */
	public void setSaveMethod(SaveMethod saveMethod) { this.saveMethod = saveMethod; }
	
	
	
	/**
	 * Récupère la méthode de sauvegarde/chargement actuellement utilisée.
	 * 
	 * @return La méthode de sauvegarde/chargement
	 */
	public SaveMethod getSaveMethod() { return saveMethod; }
	
	
	
	/**
	 * Définit la récompense pour l'accomplissement d'un achievement.
	 * 
	 * @param reward récompense
	 */ 
	public void setReward(@Nullable AdvancementReward reward) { this.reward = reward; }
	
	
	
	/**
	 * @return la récompense actuellement définie
	 */
	public AdvancementReward getReward() { return reward; }
	
	
	
	/**
	 * Affiche un message achievement à chaque joueur indiquant que le joueur a terminé un achievement
	 * Note : Cela n'accorde pas un achievement
	 * 
	 * @param player Joueur qui a reçu un achievement
	 */
	public void displayMessageToEverybody(Player player) {
		
		MutableComponent message = getMessage(player);

		ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(message, false);
		PlayerEntity.sendPacket(packet);
	}
	
	
	
	/**
	 * 
	 * @param player Joueur qui a reçu un achievement
	 */
	public MutableComponent getMessage(Player player) {
		
		String translation = "chat.type.advancement." + display.getFrame().name().toLowerCase();
		
		MutableComponent title = Component.Serializer.fromJson(display.getTitle().json());
		MutableComponent description = Component.Serializer.fromJson(display.getDescription().json());
		
		Style tm = title.getStyle();
		
		AdvancementFrame frame = getDisplay().getFrame();
		ChatFormatting typeColor = frame == AdvancementFrame.CHALLENGE ? ChatFormatting.DARK_PURPLE : ChatFormatting.GREEN;
		
		String color = tm.getColor() == null ? typeColor.name().toLowerCase() : tm.getColor().format.name();
		
		return Component.Serializer.fromJson("{"
				+ "\"translate\":\"" + translation + "\","
				+ "\"with\":"
				+ "["
					+ "\"" + player.displayName().examinableName() + "\","
					+ "{"
						+ "\"text\":\"[" + title.getContents() + "]\",\"color\":\"" + color + "\",\"bold\":" + tm.isBold() + ",\"italic\":" + tm.isItalic() + ", \"strikethrough\":" + tm.isStrikethrough() + ",\"underlined\":" + tm.isUnderlined() + ",\"obfuscated\":" + tm.isObfuscated() + ","
						+ "\"hoverEvent\":"
						+ "{"
							+ "\"action\":\"show_text\","
							+ "\"value\":[\"\", {\"text\":\"" + title.getContents() + "\",\"color\":\"" + color + "\",\"bold\":" + tm.isBold() + ",\"italic\":" + tm.isItalic() + ", \"strikethrough\":" + tm.isStrikethrough() + ",\"underlined\":" + tm.isUnderlined() + ",\"obfuscated\":" + tm.isObfuscated() + "}, {\"text\":\"\\n\"}, {\"text\":\"" + description.getContents() + "\"}]"
						+ "}"
					+ "}"
				+ "]"
			+ "}");
		
	}
	
	
	
	/**
	 * Envoie un message de toast, que le joueur l'ait ou non dans l'un de ses gestionnaires d'un achievement
	 * 
	 * @param player Joueur qui doit voir le message Toast
	 */
	public void displayToast(Player player) {
		
		ResourceLocation notName = new ResourceLocation("eu.endercentral", "notification");
		
		AdvancementDisplay display = getDisplay();
		
		AdvancementRewards advRewards = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], null);
		ItemStack icon = CraftItemStack.asNMSCopy(display.getIcon());
		
		ResourceLocation backgroundTexture = null;
		boolean hasBackgroundTexture = display.getBackgroundTexture() != null;
		
		if(hasBackgroundTexture) { backgroundTexture = new ResourceLocation(display.getBackgroundTexture()); }
		
		Map<String, Criterion> advCriteria = new HashMap<>();
		
		advCriteria.put("for_free", new Criterion(new CriterionTriggerInstance() {
			
			@Override
			public JsonObject serializeToJson(SerializationContext arg0) { return null; }
			
			@Override
			public ResourceLocation getCriterion() { return new ResourceLocation("minecraft", "impossible"); }
			
		}));
		
		ArrayList<String[]> fixedRequirements = new ArrayList<>();
		
		fixedRequirements.add(new String[] {"for_free"});

		String[][]  advRequirements = Arrays.stream(fixedRequirements.toArray()).toArray(String[][]::new);

		DisplayInfo saveDisplay = new DisplayInfo(icon, display.getTitle().getBaseComponent(), display.getDescription().getBaseComponent(), backgroundTexture, display.getFrame().getNMS(), true, display.isAnnouncedToChat(), true);
		net.minecraft.advancements.Advancement saveAdv = new net.minecraft.advancements.Advancement(notName, getParent() == null ? null : getParent().getSavedAdvancement(), saveDisplay, advRewards, advCriteria, advRequirements, false);
		
		HashMap<ResourceLocation, AdvancementProgress> prg = new HashMap<>();
		
		AdvancementProgress advPrg = new AdvancementProgress();
		advPrg.update(advCriteria, advRequirements);
		advPrg.getCriterion("for_free").grant();
		prg.put(notName, advPrg);
		
		ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, List.of(saveAdv), new HashSet<>(), prg);
		PlayerEntity.sendPacket(packet, player);

		HashSet<ResourceLocation> rm = new HashSet<>();
		
		rm.add(notName);
		prg.clear();

		packet = new ClientboundUpdateAdvancementsPacket(false, new ArrayList<>(), rm, prg);
		PlayerEntity.sendPacket(packet, player);
	}
	
	
	
	/**
	 * 
	 * @return Tous les achievements enfants directs
	 */
	@SuppressWarnings("unchecked")
	public HashSet<Advancement> getChildren() { return (HashSet<Advancement>)children.clone(); }
	
	
	
  /*******************************/
  /* PARTIE LIGNES 'ACHIEVEMENT' */ 
  /*******************************/	
	
	/**
	 * 
	 * @return Root {@link Advancement}
	 */
	public Advancement getRootAdvancement() {
		
		if(parent == null) { return this; } 
		
		else { return parent.getRootAdvancement(); }
	}
	
	
	
	/**
	 * 
	 * @return Nom unique de l'onglet d'un achievement
	 */
	public NameKey getTab() { return getRootAdvancement().getName(); }
	
	
	
	/**
	 * 
	 * @return Tous les achievements parents et les achievements enfants
	 */
	public List<Advancement> getRow() {
		
		List<Advancement> row = new ArrayList<>();
		
		row.add(this);
		
		if(getParent() != null) {
			
		  for(Advancement untilRow : getParent().getRowUntil()) { if(!row.contains(untilRow)) row.add(untilRow); }
			
		  Collections.reverse(row);
		}
		
		for(Advancement child : getChildren()) {
			
		  for(Advancement afterRow : child.getRowAfter()) { if(!row.contains(afterRow)) row.add(afterRow); }
		}
		return row;
	}
	
	
	
	/**
	 * 
	 * @return Tous les achievements parents
	 */
	public List<Advancement> getRowUntil() {
		
		List<Advancement> row = new ArrayList<>();
		
		row.add(this);
		
		if(getParent() != null) {
			
		  for(Advancement untilRow : getParent().getRowUntil()) { if(!row.contains(untilRow)) row.add(untilRow); }
		}
		return row;
	}
	
	
	
	/**
	 * 
	 * @return Tous les achievements enfants
	 */
	public List<Advancement> getRowAfter() {
		List<Advancement> row = new ArrayList<>();
		
		row.add(this);
		
		for(Advancement child : getChildren()) {
			
		  for(Advancement afterRow : child.getRowAfter()) { if(!row.contains(afterRow)) row.add(afterRow); }
		}
		return row;
	}

	
	
	/**
	 * 
	 * @param player Joueur à vérifier
	 * @return true si un achievement parent est accordé
	 */
	public boolean isAnythingGrantedUntil(Player player) {
		
		for(Advancement until : getRowUntil()) { if(until.isGranted(player)) return true; }
		return false;
	}
	
	
	
	/**
	 * 
	 * @param player Joueur à vérifier
	 * @return true si un achievement enfant est accordé
	 */
	public boolean isAnythingGrantedAfter(Player player) {
		
		for(Advancement after : getRowAfter()) { if(after.isGranted(player)) return true; }
		return false;
	}
	
  /*******************************/
  /* PARTIE LIGNES 'ACHIEVEMENT' */ 
  /*******************************/		
	
	
	
	
  /***********************************/
  /* PARTIE SAUVEGARDE 'ACHIEVEMENT' */ 
  /***********************************/	
	
	// Variables Utiles pour la partie de sauvegarde //
	
	private transient Map<String, Criterion> savedCriteria = null;
	
	@SerializedName("criteria")
	private Set<String> savedCriterionNames = null;
	
	@SerializedName("criteriaRequirements")
	
	private String[][] savedCriteriaRequirements = null;
	
	private transient net.minecraft.advancements.Advancement savedAdvancement = null;
	
	private transient HashMap<String, Boolean> savedHiddenStatus;
	
	// Variables Utiles pour la partie de sauvegarde //
	
	
	
	
	// Sauvegarde d'un achievement du joueur ayant un statut caché //
	@Warning(reason = "A utiliser que si vous savez ce que vous faites !")
	public void saveHiddenStatus(Player player, boolean hidden) {
		
	  if(savedHiddenStatus == null) savedHiddenStatus = new HashMap<>();
	  savedHiddenStatus.put(player.getUniqueId().toString(), hidden);
	}
	// Sauvegarde d'un achievement du joueur ayant un statut caché //



	// Récupère un achievement du joueur ayant un statut caché sauvegardé //
	public boolean getHiddenStatus(Player player) {
		
	  if(savedHiddenStatus == null) savedHiddenStatus = new HashMap<>();
	
	  if(!savedHiddenStatus.containsKey(player.getUniqueId().toString())) savedHiddenStatus.put(player.getUniqueId().toString(), getDisplay().isVisible(player, this));
     
	  return savedHiddenStatus.get(player.getUniqueId().toString());
	}
	// Récupère un achievement du joueur ayant un statut caché sauvegardé //
	
	
	
	
	// Sauvegarde les critères d'un achievement //
	@Warning(reason = "A utiliser que si vous savez ce que vous faites !")
	public void saveCriteria(Map<String, Criterion> save) {

	  savedCriteria = save;
	  savedCriterionNames = save.keySet();
	}
	// Sauvegarde les critères d'un achievement //
	
	
	
	// Récupère les critères d'un achievement sauvegardé //
	public Map<String, Criterion> getSavedCriteria() { return savedCriteria; }
	// Récupère les critères d'un achievement sauvegardé //
	
	
	
	// Sauvegarde les critères requis d'un achievement //
	@Warning(reason = "A utiliser que si vous savez ce que vous faites !")
	public void saveCriteriaRequirements(String[][] save) { savedCriteriaRequirements = save; }
	// Sauvegarde les critères requis d'un achievement //
	
	
	
	// Récupère les critères requis d'un achievement sauvegardé //
	public String[][] getSavedCriteriaRequirements() { return savedCriteriaRequirements; }
	// Récupère les critères requis d'un achievement sauvegardé //
	
	
	
	// Sauvegarde d'un achievement //
	@Warning(reason = "Insécurité")
	public void saveAdvancement(net.minecraft.advancements.Advancement save) { savedAdvancement = save; }
	// Sauvegarde d'un achievement //



	// Récupère un achievement sauvegardé //
	public net.minecraft.advancements.Advancement getSavedAdvancement() { return savedAdvancement; }
	// Récupère un achievement sauvegardé //
	
  /***********************************/
  /* PARTIE SAUVEGARDE 'ACHIEVEMENT' */ 
  /***********************************/		
	
	
	
	
  /*****************************************/
  /* PARTIE ACTION DU JOUEUR 'ACHIEVEMENT' */ 
  /*****************************************/

  	// Méthode rapide de récupèration du critère d'attribution d'un achievement //
	public Map<String, HashSet<String>> getAwardedCriteria() {
		
	  if(this.awardedCriteria == null) this.awardedCriteria = new HashMap<>();
	  return this.awardedCriteria;
	}
	// Méthode rapide de récupèration du critère d'attribution d'un achievement //



	// Récupère le critère d'un achievement attribué au joueur définit //
	public HashSet<String> getAwardedCriteria(Player player) {
		
	  if(!getAwardedCriteria().containsKey(player.getUniqueId().toString())) getAwardedCriteria().put(player.getUniqueId().toString(), new HashSet<>());
	  return getAwardedCriteria().get(player.getUniqueId().toString());
	}
	// Récupère le critère d'un achievement attribué au joueur définit //



	// Récupère le critère d'un achievement attribué au joueur définit (avec son uuid) //
	public HashSet<String> getAwardedCriteria(UUID uuid) {
		
	  if(!getAwardedCriteria().containsKey(uuid.toString())) getAwardedCriteria().put(uuid.toString(), new HashSet<>());
	  return getAwardedCriteria().get(uuid.toString());
	}
	// Récupère le critère d'un achievement attribué au joueur définit (avec son uuid) //

	
	
	// Méthode rapide d'ajout de critère d'attribution à un achievement //
	@Warning(reason = "Insécurité")
	public void setAwardedCriteria(Map<String, HashSet<String>> awardedCriteria) { this.awardedCriteria = awardedCriteria; }
	// Méthode rapide d'ajout de critère d'attribution à un achievement //



	// Ajoute au joueur définit un critère d'attribution à un achievement //
	@Warning(reason = "Insécurité")
	public void setAwardedCriteria(Player player, HashSet<String> awardedCriteria) {
		
		if(this.awardedCriteria == null) this.awardedCriteria = new HashMap<>();
		this.awardedCriteria.put(player.getUniqueId().toString(), awardedCriteria);
	}
	// Ajoute au joueur définit un critère d'attribution à un achievement //
	
	
	
	// Ajoute au joueur définit un critère d'attribution à un achievement (avec son uuid) //
	@Warning(reason = "Insécurité")
	public void setAwardedCriteria(UUID uuid, HashSet<String> awardedCriteria) { 
		
		if(this.awardedCriteria == null) this.awardedCriteria = new HashMap<>();
		this.awardedCriteria.put(uuid.toString(), awardedCriteria);
	}
	// Ajoute au joueur définit un critère d'attribution à un achievement (avec son uuid) //



	// Enlève le critère d'un achievement attribué au joueur définit //
	@Warning(reason = "A utiliser que si vous savez ce que vous faites !")
	public void unsetAwardedCriteria(Player player) {
		
	  if(this.awardedCriteria == null) this.awardedCriteria = new HashMap<>();
	  this.awardedCriteria.remove(player.getUniqueId().toString());
	}
	// Enlève le critère d'un achievement attribué au joueur définit //
	
	
	
	// Enlève le critère d'un achievement attribué au joueur définit (avec son uuid) //
	@Warning(reason = "A utiliser que si vous savez ce que vous faites !")
	public void unsetAwardedCriteria(UUID uuid) {
		
	  if(this.awardedCriteria == null) this.awardedCriteria = new HashMap<>();
	  this.awardedCriteria.remove(uuid.toString());
	}
	// Enlève le critère d'un achievement attribué au joueur définit (avec son uuid) //
	
	
	
	// Obtient la réalisation d'un achievement du joueur définit //
	public AdvancementProgress getProgress(Player player) {
	 
	  if(this.progress == null) progress = new HashMap<>();	
	  return this.progress.containsKey(player.getUniqueId().toString()) ? this.progress.get(player.getUniqueId().toString()) : new AdvancementProgress();
	}
	// Obtient la réalisation d'un achievement du joueur définit //
	
	
	
	// Obtient la réalisation d'un achievement du joueur définit (avec son uuid) //
	public AdvancementProgress getProgress(UUID uuid) {

      if(this.progress == null) progress = new HashMap<>();
      return this.progress.containsKey(uuid.toString()) ? this.progress.get(uuid.toString()) : new AdvancementProgress();
	}
	// Obtient la réalisation d'un achievement du joueur définit (avec son uuid) //
	
	
	
	// Ajoute une réalisation d'un achievement au joueur définit //
	@Warning(reason = "A utiliser que si vous savez ce que vous faites !")
	public void setProgress(Player player, AdvancementProgress progress) {
	
	  if(this.progress == null) this.progress = new HashMap<>();
	  this.progress.put(player.getUniqueId().toString(), progress);
	}
	// Ajoute une réalisation d'un achievement au joueur définit //
	
	
	
	// Ajoute une réalisation d'un achievement au joueur définit (avec son uuid) //
	@Warning(reason = "A utiliser que si vous savez ce que vous faites !")
	public void setProgress(UUID uuid, AdvancementProgress progress) {
	
	  if(this.progress == null) this.progress = new HashMap<>();
	  this.progress.put(uuid.toString(), progress);
	}
	// Ajoute une réalisation d'un achievement au joueur définit (avec son uuid) //



	// Enlève la réalisation d'un achievement du joueur définit //
	@Warning(reason = "A utiliser que si vous savez ce que vous faites !")
	public void unsetProgress(Player player) {
	
	  if(this.progress == null) this.progress = new HashMap<>();
	  this.progress.remove(player.getUniqueId().toString());
	}
	// Enlève la réalisation d'un achievement du joueur définit //
		
	
	
	// Enlève la réalisation d'un achievement du joueur définit (avec son uuid) //
	@Warning(reason = "A utiliser que si vous savez ce que vous faites !")
	public void unsetProgress(UUID uuid) {
	
	  if(this.progress == null) this.progress = new HashMap<>();
	  this.progress.remove(uuid.toString());
	}
	// Enlève la réalisation d'un achievement du joueur définit (avec son uuid) //
	
	
	
	// Vérifie si le joueur définit à réaliser le progrès d'un achievement //
	public boolean isDone(Player player) { return getProgress(player).isDone(); }
	// Vérifie si le joueur définit à réaliser le progrès d'un achievement //
	
	
	
	// Vérifie si le joueur définit (avec son uuid) a réalisé le progrès d'un achievement //
	public boolean isDone(UUID uuid) { return getProgress(uuid).isDone(); }
	// Vérifie si le joueur définit (avec son uuid) a réalisé le progrès d'un achievement //
	
	
	
	/**
	 * 
	 * @param player Joueur à vérifier
	 * @return true si un achievement est accordé
	 */
	public boolean isGranted(Player player) { return getProgress(player).isDone(); }
	
	
  /*****************************************/
  /* PARTIE ACTION DU JOUEUR 'ACHIEVEMENT' */ 
  /*****************************************/
	
	
	
	/**
	 * 
	 * @param key Clé à vérifier
	 * @return true {@link Advancement} si le nom et la clé partagent le même espace de noms et le même nom
	 */
	public boolean hasName(NameKey key) {
	
	  return key.getNamespace().equalsIgnoreCase(name.getNamespace()) && key.getKey().equalsIgnoreCase(name.getKey());
	}
	
	
	/**
	 * 
	 * @return L'Achievement en format "string"
	 */
	@Override
	public String toString() { return "Advancement " + getAdvancementJSON(); }
	
}