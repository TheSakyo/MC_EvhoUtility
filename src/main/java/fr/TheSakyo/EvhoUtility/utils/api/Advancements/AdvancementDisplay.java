package fr.TheSakyo.EvhoUtility.utils.api.Advancements;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;

public class AdvancementDisplay {
	
	// Variables Utiles //
	
	@SerializedName("icon")
	private Material iconID;
	private transient ItemStack icon;
	private JSONMessage title, description;
	private AdvancementFrame frame;
	private boolean showToast;
	private boolean announceChat;
	private transient AdvancementVisibility vis;
	private String backgroundTexture;
	private float x = 0, y = 0, tabWidth = 0, tabHeight = 0;
	private transient Advancement positionOrigin;
	
	@SerializedName("visibility")
	public String visibilityIdentifier = "VANILLA";

	private static boolean announceAdvancementMessages = true;
	
	// Variables Utiles //
	
	
	
	
	/**
	 * 
	 * @return <b>true</b> si les messages d'achievement doivent être affichés par défaut<br><b>false</b> si tous les messages d'achievements seront cachés.
	 */
	public static boolean isAnnounceAdvancementMessages() {
		return announceAdvancementMessages;
	}
	
	
	
  /****************************/
  /* CONSTRUCTEUR DE MATÉRIEL */
  /****************************/
	
	/**
	 * 
	 * @param icon Icône {@link Material}
	 * @param title Titre {@link JSONMessage}
	 * @param description Déscription {@link JSONMessage}
	 * @param frame cadre {@link AdvancementFrame}
	 * @param showToast Faut-il afficher les messages de toast
	 * @param announceChat Les achievements doivent-elles être annoncées dans le chat ?
	 * @param visibility Lorsqu'un achievement est visible
	 */
	public AdvancementDisplay(Material icon, String title, String description, AdvancementFrame frame, boolean showToast, boolean announceChat, AdvancementVisibility visibility) {
		this.icon = new ItemStack(icon);
		this.iconID = icon;
		this.title = new JSONMessage("{\"text\":\"" + title.replaceAll("\"", "\\\"") + "\"}");
		this.description = new JSONMessage("{\"text\":\"" + description.replaceAll("\"", "\\\"") + "\"}");
		this.frame = frame;
		this.showToast = showToast;
		this.announceChat = announceChat;
		setVisibility(visibility);
	}
	
	
	/**
	 * 
	 * @param icon Icône {@link Material}
	 * @param title Titre {@link JSONMessage}
	 * @param description D�scription {@link JSONMessage}
	 * @param frame cadre {@link AdvancementFrame}
	 * @param backgroundTexture url de la texture de fond
	 * @param showToast Faut-il afficher les messages de toast
	 * @param announceChat Les achievements doivent-elles être annoncées dans le chat ?
	 * @param visibility Lorsqu'un achievement est visible
	 */
	public AdvancementDisplay(Material icon, String title, String description, AdvancementFrame frame, String backgroundTexture, boolean showToast, boolean announceChat, AdvancementVisibility visibility) {
		this.icon = new ItemStack(icon);
		this.iconID = icon;
		this.title = new JSONMessage("{\"text\":\"" + title.replaceAll("\"", "\\\"") + "\"}");
		this.description = new JSONMessage("{\"text\":\"" + description.replaceAll("\"", "\\\"") + "\"}");
		this.frame = frame;
		this.backgroundTexture = backgroundTexture;
		this.showToast = showToast;
		this.announceChat = announceChat;
		setVisibility(visibility);
	}
	
  /****************************/
  /* CONSTRUCTEUR DE MATÉRIEL */
  /****************************/
	
	
	
	
  /****************************/
  /* CONSTRUCTEUR D'ITEMSTACK */ 
  /****************************/
	
	/**
	 * 
	 * @param icon Icône {@link ItemStack}
	 * @param title Titre Title {@link String}
	 * @param description Déscription {@link String}
	 * @param frame cadre {@link AdvancementFrame}
	 * @param showToast Faut-il afficher les messages de toast
	 * @param announceChat Les achievements doivent-elles être annoncées dans le chat ?
	 * @param visibility Lorsqu'un achievement est visible
	 */
	public AdvancementDisplay(ItemStack icon, String title, String description, AdvancementFrame frame, boolean showToast, boolean announceChat, AdvancementVisibility visibility) {
		this.icon = icon;
		this.iconID = icon.getType();
		this.title = new JSONMessage("{\"text\":\"" + title.replaceAll("\"", "\\\"") + "\"}");
		this.description = new JSONMessage("{\"text\":\"" + description.replaceAll("\"", "\\\"") + "\"}");
		this.frame = frame;
		this.showToast = showToast;
		this.announceChat = announceChat;
		setVisibility(visibility);
	}
	
	
	

	/**
	 * 
	 * @param icon Icône {@link Material}
	 * @param title Titre {@link JSONMessage}
	 * @param description Déscription {@link JSONMessage}
	 * @param frame cadre {@link AdvancementFrame}
	 * @param backgroundTexture url de la texture de fond
	 * @param showToast Faut-il afficher les messages de toast
	 * @param announceChat Les achievements doivent-elles être annoncées dans le chat ?
	 * @param visibility Lorsqu'un achievement est visible
	 */
	public AdvancementDisplay(ItemStack icon, String title, String description, AdvancementFrame frame, String backgroundTexture, boolean showToast, boolean announceChat, AdvancementVisibility visibility) {
		this.icon = icon;
		this.iconID = icon.getType();
		this.title = new JSONMessage("{\"text\":\"" + title.replaceAll("\"", "\\\"") + "\"}");
		this.description = new JSONMessage("{\"text\":\"" + description.replaceAll("\"", "\\\"") + "\"}");
		this.frame = frame;
		this.backgroundTexture = backgroundTexture;
		this.showToast = showToast;
		this.announceChat = announceChat;
		setVisibility(visibility);
	}
	
  /****************************/
  /* CONSTRUCTEUR D'ITEMSTACK */ 
  /****************************/

	/**
	 * 
	 * @return Icône {@link ItemStack}
	 */
	public ItemStack getIcon() {
		
	  if(icon == null && iconID != null) icon = new ItemStack(iconID);
	  return icon;
	}
	
	
	
	/**
	 * 
	 * @return Titre {@link JSONMessage}
	 */
	public JSONMessage getTitle() { return title; }
	
	
	
	/**
	 * 
	 * @return Déscription {@link JSONMessage}
	 */
	public JSONMessage getDescription() { return description; }
	
	
	
	
	/**
	 * 
	 * @return Cadre {@link AdvancementFrame}
	 */
	public AdvancementFrame getFrame() { return frame; }
	
	
	
	/**
	 * 
	 * @return true si des messages toasts seront affichées
	 */
	public boolean isToastShown() { return showToast; }
	
	
	
	/**
	 * 
	 * @return true si les messages seront affichés dans le chat
	 */
	public boolean isAnnouncedToChat() { return announceChat && isAnnounceAdvancementMessages(); }
	
	
	
	/**
	 * 
	 * @return url de la texture de fond
	 */
	@Nullable
	public String getBackgroundTexture() { return backgroundTexture; }
	
	
	
	/**
	 * Définit la texture de fond
	 * 
	 * @param backgroundTexture url de la texture de fond
	 */
	public void setBackgroundTexture(@Nullable String backgroundTexture) { this.backgroundTexture = backgroundTexture; }
	
	
	
	/**
	 * Obtient la coordonnée X relative
	 * 
	 * @return coordonnée X relative
	 */
	public float getX() { return x; }
	
	
	
	/**
	 * Obtient la coordonnée Y relative
	 * 
	 * @return coordonnée Y relative
	 */
	public float getY() { return y; }
	
	
	
	/**
	 * Obtient la coordonnée X absolue
	 * 
	 * @return coordonnée X absolue
	 */
	public float generateX() {
		 
	  if(getPositionOrigin() == null) { return x; } 
		
	  else { return getPositionOrigin().getDisplay().generateX() + x; }
	}
	
	
	
	
	/**
	 * Obtient la coordonnée Y absolue
	 * 
	 * @return coordonnée Y absolue
	 */
	public float generateY() {
		 
	  if(getPositionOrigin() == null) { return y; } 
		
	  else { return getPositionOrigin().getDisplay().generateY() + y; }
	}
	
	
	
	// Obtient la longueur de l'affichage de l'achievement //
	public float getTabWidth() { return tabWidth; }
	// Obtient la longueur de l'affichage de l'achievement //
	
	
	
	// Obtient la hauteur de l'affichage de l'achievement //
	public float getTabHeight() { return tabHeight; }
	// Obtient la hauteur de l'affichage de l'achievement //
	
	
	
	/**
	 * Obtient la visibilitée de l'achievement {@link AdvancementVisibility}
	 * 
	 * @return Lorsqu'un achievement est visible.
	 */
	public AdvancementVisibility getVisibility() { return vis != null ? vis : AdvancementVisibility.VANILLA; }
	
	
	
	/**
	 * 
	 * @param player Joueur à vérifier
	 * @param advancement Achievement à vérifier (car {@link AdvancementDisplay} n'est pas lié à un achievement)
	 * @return true s'il doit être actuellement visible
	 */
	public boolean isVisible(Player player, Advancement advancement) {
		
	  AdvancementVisibility visibility = getVisibility();
	  return visibility.isVisible(player, advancement) || advancement.isGranted(player) || (visibility.isAlwaysVisibleWhenAdvancementAfterIsVisible() && advancement.isAnythingGrantedAfter(player));
	}
	
	
	
	/**
	 * 
	 * @return l'origine des coordonnées de l'achievement
	 */
	public Advancement getPositionOrigin() { return positionOrigin; }
	
	
	
	/**
	 * Change l'icône
	 * 
	 * @param icon Nouveau matériau d'icône à afficher
	 */
	public void setIcon(Material icon) {
	  
	  this.icon = new ItemStack(icon);
	  this.iconID = icon;
	}
	
	
	
	/**
	 * Change l'icône
	 * 
	 * @param icon Nouveau item d'icône à afficher
	 */
	public void setIcon(ItemStack icon) {
		
	  this.icon = icon;
	  this.iconID = icon.getType();
	}
	
	
	
	/**
	 * Change le titre
	 * 
	 * @param title Nouveau titre à afficher
	 */
	public void setTitle(String title) { this.title = new JSONMessage("{\"text\":\"" + title.replaceAll("\"", "\\\"") + "\"}"); }
	
	
	
	/**
	 * Change la déscription
	 * 
	 * @param description Nouvelle déscription à afficher
	 */
	public void setDescription(String description) { this.description = new JSONMessage("{\"text\":\"" + description.replaceAll("\"", "\\\"") + "\"}"); }
	
	
	
	/**
	 * Change le cadre
	 * 
	 * @param frame Nouveau cadre
	 */
	public void setFrame(AdvancementFrame frame) { this.frame = frame; }
	
	
	
	/**
	 * Change le message toast
	 * 
	 * @param showToast Nouveau message toast à afficher
	 */
	public void setShowToast(boolean showToast) { this.showToast = showToast; }
	
	
	
	/**
	 * Change le message à afficher dans le chat
	 * 
	 * @param announceChat Nouveau message à afficher dans le chat
	 */
	public void setAnnounceChat(boolean announceChat) { this.announceChat = announceChat; }
	
	
	
	/**
	 * Change la visibilitée
	 * 
	 * @param visibility Nouveau type de visibilité
	 */
	public void setVisibility(AdvancementVisibility visibility) {
		
	  this.vis = visibility;
	  this.visibilityIdentifier = getVisibility().getName();
	}
	
	
	
	/**
	 * Change les coordonnées relatives
	 * 
	 * @param x coordonnées X relatives
	 * @param y coordonnées Y relatives
	 */
	public void setCoordinates(float x, float y) {
		
	  this.x = x;
	  this.y = y;
	}
	
	
	
	/**
	 * Change la coordonnée X relative
	 * 
	 * @param x coordonnées X relatives
	 */
	public void setX(float x) { this.x = x; }
	
	
	
	/**
	 * Change la coordonnée Y relative
	 * 
	 * @param y coordonnées Y relatives
	 */
	public void setY(float y) { this.y = y; }
	
	
	
	// Change la longueur de l'affichage de l'achievement //
	public void setTabHeight(float tabHeight) { this.tabHeight = tabHeight; }
	// Change la longueur de l'affichage de l'achievement //
	
	
	
	// Change la hauteur de l'affichage de l'achievement //
	public void setTabWidth(float tabWidth) { this.tabWidth = tabWidth; }
	// Change la hauteur de l'affichage de l'achievement //
	
	
	
	/**
	 * Change l'origine des coordonnées de l'achievements
	 * 
	 * @param positionOrigin Nouvelle origine de la position
	 */
	public void setPositionOrigin(Advancement positionOrigin) { this.positionOrigin = positionOrigin; }
}