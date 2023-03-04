package fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC;

import dependancies.org.jsoup.helper.Validate;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.TimerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Instance de {@link NPCGlobal} ayant la 'class' {@link NPC} étendu.<br/>
 * Ceci permet de faire une instance Globale du NPC
 */
public class NPCGlobal extends NPC {

    private static final Integer LOOK_TICKS = 2; // Variable récupérant le nombre ticks de regard du NPC

    protected final HashMap<Player, NPCPersonal> players; // Variable récupérant le Joueur associé à son NPC
    private final HashMap<UUID, NPC.Attributes> customAttributes; // Variable récupérant les Attributs customisés d'un NPC
    private Visibility visibility; // Variable récupérant la visibilité du NPC
    private Predicate<Player> visibilityRequirement; // Variable récupérant l'éxigence de la visiilité du Joueur
    private Entity nearestEntity, nearestPlayer; // Variable récupérant l'Entité ou le Joueur le plus proche
    private Long lastNearestEntityUpdate, lastNearestPlayerUpdate; // Variable récupérant la dernière mise à jour de l'Entité ou du Joueur le plus proche
    private boolean autoCreate, autoShow; // Variable vérifiant l'auto création et l'auto affichage du NPC
    private boolean ownPlayerSkin; // Variable vérifiant si le NPC obtient un Skin par Joueur associé ou non
    private boolean resetCustomAttributes; // Variable vérifiant si le NPC doit réinitialiser ses attributs customisés
    private List<String> selectedPlayers; // Variable récupérant les Joueurs séléctionnées
    protected boolean persistent; // Variable vérifiant la persistance du NPC
    private String customName; // Variable récupérant un nom customisé pour le NPC
    protected PersistentManager persistentManager; // Variable récupérant la gestion de persistance du NPC

    /**
     * On instancie un nouveau {@link NPCGlobal NPC Global}.
     *
     * @param npcUtils La {@link NPCUtils librairie NPC}
     * @param plugin Le plugin en question
     * @param code   Un code d'identification
     * @param visibility  La {@link Visibility Visibilité} du NPC
     * @param visibilityRequirement L'Éxigence du Joueur pour la visibilité du NPC
     * @param world  Le monde
     * @param x      La coordonée x
     * @param y      La coordonée y
     * @param z      La coordonée z
     * @param yaw    La rotation 'yaw'
     * @param pitch  La rotation 'pitch'
     */
    protected NPCGlobal(@Nonnull NPCUtils npcUtils, @Nonnull Plugin plugin, @Nonnull String code, @Nonnull Visibility visibility, @Nullable Predicate<Player> visibilityRequirement, @Nonnull World world, double x, double y, double z, float yaw, float pitch) {

        super(npcUtils, plugin, code, world, x, y, z, yaw, pitch); // On instancie la class parente

        // Si la visibilité du NPC est null, on envoie une erreur
        Validate.notNull(visibility, "Impossible de générer l'instance NPCGlobal NPC, la visibilité ne peut pas être nulle.");

        this.players = new HashMap<>(); // Initialise les joueurs
        this.customAttributes = new HashMap<>(); // Initialise les attributs customisés
        this.visibility = visibility; // Initialise la visibilité
        this.visibilityRequirement = visibilityRequirement; // Initialise les éxigences de la visibilité
        this.autoCreate = true; // Initialise l'auto création du NPC
        this.autoShow = true; // Initialise l'affichage automatique du NPC pour les Joueurs
        this.resetCustomAttributes = false; // Initialise la réinitialisation des attributs customisés
        this.persistent = false; // Initialise la persistance du NPC
        this.selectedPlayers = new ArrayList<>(); // Initialise les Joueurs sélectionnés

        np(null); // Initialise le Joueur le plus proche du NPC
        ne(null); // Initialise l'Entité le plus proche du NPC

        checkVisiblePlayers(); // Génère la visibilité du NPC pour ses Joueurs associés
    }

    /**
     * On instancie un nouveau NPC Globale.
     *
     * @param npcUtils La {@link NPCUtils librairie NPC}
     * @param plugin Le plugin en question
     * @param code   Un code d'identification
     * @param visibility  La {@link Visibility Visibilité} du NPC
     * @param visibilityRequirement L'Éxigence du Joueur pour la visibilité du NPC
     * @param location La {@link Location Localisation} du NPC
     */
    protected NPCGlobal(@Nonnull NPCUtils npcUtils, @Nonnull Plugin plugin, @Nonnull String code, @Nonnull Visibility visibility, @Nullable Predicate<Player> visibilityRequirement, @Nonnull Location location) { this(npcUtils, plugin, code, visibility, visibilityRequirement, location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()); }

                            /****************************************************************************************/

    // ÉNUMÉRATION DE LA VISIBILITÉ DU NPC //

    public enum Visibility { EVERYONE, SELECTED_PLAYERS; }

    // ÉNUMÉRATION DE LA VISIBILITÉ DU NPC //

                            /****************************************************************************************/
    /**
     * Définit la {@link Visibility Visibilité} du NPC.
     *
     * @param visibility La {@link Visibility Visibilité} en question
     */
    public void setVisibility(Visibility visibility) {

        if(this.visibility.equals(visibility)) return;
        this.visibility = visibility;
        checkVisiblePlayers();
    }

    /**
     * Définit l'éxigence pour voir le NPC.
     *
     * @param visibilityRequirement L'Éxigence en question
     */
    public void setVisibilityRequirement(Predicate<Player> visibilityRequirement) {

        this.visibilityRequirement = visibilityRequirement;
        checkVisiblePlayers();
    }

    /**
     * Vérifie la visibilité du NPC pour les joueurs.
     *
     */
    private void checkVisiblePlayers() {

        Set<Player> playerSet = new HashSet<>(players.keySet());
        playerSet.stream().filter(x-> !meetsVisibilityRequirement(x)).forEach(this::removePlayer);

        if(visibility.equals(Visibility.EVERYONE)) addPlayers((Collection<Player>) Bukkit.getServer().getOnlinePlayers());
        else if(visibility.equals(Visibility.SELECTED_PLAYERS)) Bukkit.getServer().getOnlinePlayers().stream().filter(x-> !players.containsKey(x) && selectedPlayers.contains(x.getName())).forEach(this::addPlayer);
    }

    /**
     * Récupère la {@link Visibility Visibilité} du NPC.
     *
     */
    public Visibility getVisibility() { return visibility; }

    /**
     * Vérifie si le Joueur demandé répond à l'éxigence pour la visiblité du NPC.
     *
     * @param player Le Joueur a vérifié
     *
     * @return Une valeur Booléenne
     */
    public boolean meetsVisibilityRequirement(@Nonnull Player player) {

        Validate.notNull(player, "Impossible de vérifier un joueur nul.");
        if(visibilityRequirement == null) return true;
        return visibilityRequirement.test(player);
    }

    /**
     * Récupère l'éxigence de visibilité du NPC.
     *
     */
    public Predicate<Player> getVisibilityRequirement() { return visibilityRequirement; }

    /**
     * Vérifie s'il y a une éxigence de visibilité pour le NPC.
     *
     * @return Une valeur Booléenne
     */
    public boolean hasVisibilityRequirement() { return visibilityRequirement != null; }

    /**
     * Ajoute des Joueurs pour le NPC en question.
     *
     * @param players Les Joueurs à ajouter
     */
    public void addPlayers(@Nonnull Collection<Player> players) { addPlayers(players, false); }

    /**
     * Ajoute des Joueurs pour le NPC en question.
     *
     * @param players Les Joueurs à ajouter
     * @param ignoreVisibilityRequirement Doit-on ignorer pour ses joueurs l'éxigence de visibilité ?
     */
    public void addPlayers(@Nonnull Collection<Player> players, boolean ignoreVisibilityRequirement) {

        Validate.notNull(players, "Impossible d'ajouter une collection de Joueurs nulle.");
        players.forEach(x-> addPlayer(x, ignoreVisibilityRequirement));
    }

    /**
     * Ajoute un Joueur pour le NPC en question.
     *
     * @param player Le Joueur à ajouter
     */
    public void addPlayer(@Nonnull Player player) { addPlayer(player, false); }

    /**
     * Ajoute un Joueur pour le NPC en question.
     *
     * @param player Le Joueur à ajouter
     * @param ignoreVisibilityRequirement Doit-on ignorer pour le joueur l'éxigence de visibilité ?
     */
    public void addPlayer(@Nonnull Player player, boolean ignoreVisibilityRequirement) {

        Validate.notNull(player, "Impossible d'ajouter un joueur nul."); // Si le Joueur est null, on affiche une Erreur

        if(players.containsKey(player)) return; // Si le Joueur est déjà ajouté, on ne fait rien

        // Si on n'ignore pas l'éxigence de visibilité et que le Joueur ne répond pas à l'éxigence demandé, on ne fait rien
        if(!ignoreVisibilityRequirement && !meetsVisibilityRequirement(player)) return;

        // On génère un NPC Personnel pour le Joueur
        NPCPersonal npcPersonal = getNPCUtils().generatePlayerPersonalNPC(player, getPlugin(), getPlugin().getName().toLowerCase() + "." + "global_" + getSimpleCode(), getLocation());

        npcPersonal.npcGlobal = this; // On définit son instance NPC Globale au NPC en question
        players.put(player, npcPersonal); // On ajoute ensuite le NPC Personnel généré au Joueur en question

        // Si dans la liste des joueurs sélectionnée, on ne trouve pas le nom du Joueur on l'ajoute
        if(!selectedPlayers.contains(player.getName())) selectedPlayers.add(player.getName());

        // Si dans la liste des attributs customisés, on ne trouve pas l'UUID du Joueur on l'ajoute
        if(!customAttributes.containsKey(player.getUniqueId())) customAttributes.put(player.getUniqueId(), new Attributes(null));

        updateAttributes(player); // On recharge les attributs pour le Joueur

        if(autoCreate) npcPersonal.create(); // Si le NPC permet son auto création, on crée le NPC Personnel généré
        if(autoCreate && autoShow) npcPersonal.show(); // Si le NPC permet son auto création et son affichage automatique, on crée le NPC Personnel généré et on l'affiche
    }

    /**
     * Supprime des Joueurs pour le NPC en question.
     *
     * @param players Les Joueurs à supprimer
     */
    public void removePlayers(@Nonnull Collection<Player> players) {

        Validate.notNull(players, "Impossible de supprimer une collection de Joueurs nulle");
        players.forEach(this::removePlayer);
    }

    /**
     * Supprime un Joueur pour le NPC en question.
     *
     * @param player Le Joueur à supprimer
     */
    public void removePlayer(@Nonnull Player player) {

        Validate.notNull(player, "Impossible de vérifier un joueur nul."); // Si le Joueur est null, on affiche une erreur

        if(!players.containsKey(player)) return; // Si le joueur n'est pas associé au NPC actuel, on ne fait rien

        NPCPersonal personalNPC = getPersonal(player); // On récupère le NPC Personnel du Joueur en question

        // Si on a accepté la réinitialisation des attributs customisés du NPC, on enlève alors les attributs customisés pour le Joueur
        if(resetCustomAttributes) customAttributes.remove(player.getUniqueId());

        // Si le joueur est parmis les Joueurs sélectionner du NPC, on lui enlève
        if(selectedPlayers.contains(player.getName())) selectedPlayers.remove(player.getName());

        players.remove(player); // On ajoute ensuite le NPC Personnel généré au Joueur en question
        getNPCUtils().removePersonalNPC(personalNPC); // On Supprime ensuite le NPC Personnel du Joueur en question
    }

    /**
     * Vérifie si le Joueur est associé au NPC en question.
     *
     * @param player Le Joueur a vérifié
     *
     * @return Une valeur Booléenne
     */
    public boolean hasPlayer(@Nonnull Player player) {

        Validate.notNull(player, "Impossible de vérifier un joueur nul."); // Si le Joueur est null, on affiche une erreur
        return players.containsKey(player);
    }

    /**
     * Récupères tous les Joueurs sélectionnés pour le NPC actuel.
     *
     *
     * @return Une liste de joueurs sélectionnés
     */
    public List<String> getSelectedPlayers() { return selectedPlayers; }

    /**
     * Ajoute un Joueur à sélectionner pour le NPC en question.
     *
     * @param playerName Le Nom du Joueur à ajouter
     */
    public void addSelectedPlayer(String playerName) { if(!CustomMethod.containsIgnoreCase(selectedPlayers, playerName)) selectedPlayers.add(playerName); }

    /**
     * Supprime un Joueur à sélectionner pour le NPC en question.
     *
     * @param playerName Le Nom du Joueur à supprimer
     */
    public void removeSelectedPlayer(String playerName) { if(CustomMethod.containsIgnoreCase(selectedPlayers, playerName)) selectedPlayers.remove(playerName); }

    /**
     * Vérifie si un Joueur est sélectionné pour le NPC actuel.
     *
     * @param playerName Le Nom du Joueur à vérifier
     *
     * @return Une valeur Booléenne
     */
    public boolean hasSelectedPlayer(String playerName) { return CustomMethod.containsIgnoreCase(selectedPlayers, playerName); }

    /**
     * Récupères tous les Joueurs associés au NPC actuel.
     *
     *
     * @return Une liste de joueurs associés au NPC actuel
     */
    public Set<Player> getPlayers() { return players.keySet(); }

    /**
     * Pour tous les Joueurs actifs, on effectue une action consacrée à leur NPC.
     *
     * @param action L'Action a affectué
     */
    protected void forEachActivePlayer(BiConsumer<Player, NPCPersonal> action) { players.keySet().stream().filter(this::isActive).forEach(x-> action.accept(x, getPersonal(x))); }

    /**
     * Récupère Le Management de Persistance du NPC.
     *
     * @return Le Management de Persistance du NPC
     */
    public PersistentManager getPersistentManager() { return persistentManager; }

    /**
     * Vérifie si le joueur demandé est actif.
     *
     * @param player Le Joueur a vérifié
     *
     * @return Une valeur Booléenne
     */
    protected boolean isActive(Player player) {

        if(!player.isOnline()) return false; // Si le Joueur est hors-ligne, on retourne faux
        if(!hasPlayer(player)) return false; // Si le Joueur ne fait pas partie des Joueurs associés au NPC, on retourne faux

        NPCPersonal personalNPC = getPersonal(player); // On Récupère le NPC Personnel du Joueur en question
        if(!personalNPC.isCreated()) return false; // Si le NPC Personnel du Joueur en question n'est pas créé, on retourne faux

        return true; // Sinon, on retourne vrai
    }

    /**
     * Créer le NPC pour un Joueur précis.
     *
     * @param player Le Joueur en question
     *
     */
    public void create(@Nonnull Player player) {

        Validate.notNull(player, "Le joueur ne peut pas être nul."); // Si le Joueur est null, on renvoie une erreur
        updateAttributes(player); // Met à jour les attributs du NPC pour le Joueur
        getPersonal(player).create(); // Créer le NPC Personnel pour le Joueur
    }

    /**
     * Détruit le NPC pour un Joueur précis.
     *
     * @param player Le Joueur en question
     *
     */
    public void destroy(@Nonnull Player player) {

        Validate.notNull(player, "Le joueur ne peut pas être nul."); // Si le Joueur est null, on renvoie une erreur
        getPersonal(player).destroy(); // Détruit le NPC Personnel du Joueur affichera
    }

    /**
     * Affiche le NPC pour un Joueur précis.
     *
     * @param player Le Joueur en question
     *
     */
    public void show(@Nonnull Player player) {

        Validate.notNull(player, "Le joueur ne peut pas être nul."); // Si le Joueur est null, on renvoie une erreur
        getPersonal(player).show(); // Affiche le NPC Personnel pour le Joueur
    }

    /**
     * Cache le NPC pour un Joueur précis.
     *
     * @param player Le Joueur en question
     *
     */
    public void hide(@Nonnull Player player) {

        Validate.notNull(player, "Le joueur ne peut pas être nul."); // Si le Joueur est null, on renvoie une erreur
        getPersonal(player).hide(); // Cache le NPC Personnel pour le Joueur
    }

    /**
     * Met à jour le NPC pour un Joueur précis.
     *
     * @param player Le Joueur en question
     *
     */
    public void update(@Nonnull Player player) {

        Validate.notNull(player, "Le joueur ne peut pas être nul."); // Si le Joueur est null, on renvoie une erreur
        updateAttributes(player); // Met à jour les attributs du NPC pour le Joueur
        getPersonal(player).update(); // Met à jour le NPC Personnel pour le Joueur
    }

    /**
     * Force la mise à jour du NPC pour un Joueur précis.
     *
     * @param player Le Joueur en question
     *
     */
    public void forceUpdate(@Nonnull Player player) {

        Validate.notNull(player, "Le joueur ne peut pas être nul."); // Si le Joueur est null, on renvoie une erreur
        updateAttributes(player); // Met à jour les attributs du NPC pour le Joueur
        getPersonal(player).forceUpdate(); // Force la mise à jour du NPC Personnel pour le Joueur
    }

    /**
     * Met à jour le {@link fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPC.Hologram Texte} que le NPC affiche pour un Joueur précis.
     *
     * @param player Le Joueur en question
     *
     */
    public void updateText(@Nonnull Player player) {

        Validate.notNull(player, "Le joueur ne peut pas être nul."); // Si le Joueur est null, on renvoie une erreur
        updateAttributes(player); // Met à jour les attributs du NPC pour le Joueur
        getPersonal(player).updateText(); // Met à jour le Texte que le NPC Personnel du Joueur affichera
    }

    /**
     * Force la mise à jour du {@link fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPC.Hologram Texte} que le NPC affiche pour un Joueur précis.
     *
     * @param player Le Joueur en question
     *
     */
    public void forceUpdateText(@Nonnull Player player) {

        Validate.notNull(player, "Le joueur ne peut pas être nul."); // Si le Joueur est null, on renvoie une erreur
        updateAttributes(player); // Met à jour les attributs du NPC pour le Joueur
        getPersonal(player).forceUpdateText(); // Force la mise à jour du Texte que le NPC Personnel du Joueur affichera
    }

    /**
     * Vérifie si le NPC peut se créer automatiquement.
     *
     * @return Une valeur Booléenne
     */
    public boolean isAutoCreate() { return autoCreate; }

    /**
     * Définit si le NPC peut se créer automatiquement.
     *
     * @param autoCreate Le NPC peut-il se créer automatiquement ?
     *
     */
    public void setAutoCreate(boolean autoCreate) { this.autoCreate = autoCreate; }

    /**
     * Vérifie si le NPC peut s'afficher automatiquement.
     *
     * @return Une valeur Booléenne
     */
    public boolean isAutoShow() { return autoShow; }

    /**
     * Définit si le NPC peut s'afficher automatiquement.
     *
     * @param autoShow Le NPC peut-il s'afficher automatiquement ?
     *
     */
    public void setAutoShow(boolean autoShow) { this.autoShow = autoShow; }

    /**
     * Vérifie si le NPC est persitant.
     *
     * @return Une valeur Booléenne
     */
    public boolean isPersistent() { return persistent; }

    /**
     * Vérifie si le NPC peut être persitant.
     *
     * @return Une valeur Booléenne
     */
    public boolean canBePersistent() { return getPlugin().equals(UtilityMain.getInstance()); }

    /**
     * Définit si le NPC soit persitant.
     *
     * @param persistent Le NPC doit-il être persitant ?
     *
     */
    public void setPersistent(boolean persistent) {

        // Si le NPC ne peut pas être persistant, on renvoie une erreur
        Validate.isTrue(canBePersistent(), "Ce NPC ne peut pas être persistant car il n'est pas pris en charge par l'API PlayerNPC du plugin EvhoUtility.");

        if(persistent == this.persistent) return; // Si la valeur récupérée est déjà celle de base, on ne fait rien

        this.persistent = persistent; // On définit la persistance du NPC

        // ⬇️ Si le NPC est persistant, alors on recharge la gestion de persistance du NPC, sinon, on le supprime ⬇️ //
        if(persistent) {

            persistentManager = PersistentManager.getPersistent(getPlugin(),getSimpleCode());
            PersistentManager.getPersistent(getPlugin(),getSimpleCode()).save();
            
        } else PersistentManager.getPersistent(getPlugin(), getSimpleCode()).remove();
        // ⬆️ Si le NPC est persistant, alors on recharge la gestion de persistance du NPC, sinon, on le supprime ⬆️ //
    }

    /**
     * Définit un Texte Customisée qu'affichera le NPC pour un Joueur.
     *
     * @param player Le Joueur qui verra le changement
     * @param lines Les différents textes à afficher
     */
    public void setCustomText(Player player, List<String> lines) { getCustomAttributes(player).setText(lines); }

    /**
     * Réinitialise le Texte Customisée qu'affichera le NPC pour le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomText(Player player) { getCustomAttributes(player).text = null; }

    /**
     * Définit un Skin Customisé qu'affichera le NPC pour un Joueur.
     *
     * @param player Le Joueur qui verra le changement
     * @param skin Le {@link fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.NPC.Skin Skin} en question
     */
    public void setCustomSkin(Player player, NPC.Skin skin) { getCustomAttributes(player).setSkin(skin); }

    /**
     * Réinitialise le Skin Customisé qu'affichera le NPC pour le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomSkin(Player player) { getCustomAttributes(player).skin = null; }

    /**
     * Définissons-nous une Collision Customisé du NPC pour un Joueur ?
     *
     * @param player Le Joueur qui verra le changement
     * @param collidable Activons-nous les collisions du NPC ?
     */
    public void setCustomCollidable(Player player, boolean collidable) { getCustomAttributes(player).setCollidable(collidable); }

    /**
     * Réinitialise la Collision Customisé du NPC pour le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomCollidable(Player player) { getCustomAttributes(player).collidable = null; }

    /**
     * Définit une Distance Customisé entre un Joueur et le NPC pour le faire disparaître.
     *
     * @param player Le Joueur qui verra le changement
     * @param hideDistance La distance en question
     */
    public void setCustomHideDistance(Player player, double hideDistance) { getCustomAttributes(player).setHideDistance(hideDistance); }

    /**
     * Réinitialise la Distance Customisé du NPC pour le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomHideDistance(Player player) { getCustomAttributes(player).hideDistance = null; }

    /**
     * Définissons-nous une Surbrillance Customisé du NPC pour un Joueur ?
     *
     * @param player Le Joueur qui verra le changement
     * @param glowing Activons-nous la surbrillance du NPC ?
     */
    public void setCustomGlowing(Player player, boolean glowing) { getCustomAttributes(player).setGlowing(glowing); }

    /**
     * Réinitialise la Surbrillance Customisé du NPC pour le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomGlowing(Player player) { getCustomAttributes(player).glowing = null; }

    /**
     * Définit une {@link ChatFormatting Couleur} de Surbrillance Customisé du NPC pour un Joueur.
     *
     * @param player Le Joueur qui verra le changement
     * @param color La {@link ChatFormatting Couleur} en question
     */
    public void setCustomGlowingColor(Player player, ChatFormatting color) { getCustomAttributes(player).setGlowingColor(color); }

    /**
     * Réinitialise la {@link ChatFormatting Couleur} de Surbrillance Customisé du NPC pour le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomGlowingColor(Player player) { getCustomAttributes(player).glowingColor = null; }

    /**
     * Définit un {@link GazeTrackingType Type de Suivie du Regard} du NPC pour un Joueur.
     *
     * @param player Le Joueur qui verra le changement
     * @param followLookType Le {@link ChatFormatting Type de Suivie du Regard} en question
     */
    public void setCustomGazeTrackingType(Player player, GazeTrackingType followLookType) { getCustomAttributes(player).setGazeTrackingType(followLookType); }

    /**
     * Réinitialise le {@link GazeTrackingType Type de Suivie du Regard} du NPC pour le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomGazeTrackingType(Player player) { getCustomAttributes(player).gazeTrackingType = null; }

    /**
     * Définit un Nom Customisé du NPC qui sera affiché au 'tablist' pour un Joueur.
     *
     * @param player Le Joueur qui verra le changement
     * @param customTabListName Le Nom Customisé qui sera affiché au 'tablist'
     */
    public void setCustomTabListName(Player player, String customTabListName) { getCustomAttributes(player).setCustomTabListName(customTabListName); }

    /**
     * Réinitialise le Nom Customisé du NPC qui sera affiché au 'tablist' pour le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomTabListName(Player player) { getCustomAttributes(player).customTabListName = null; }

    /**
     * Définit pour un Joueur en question la Vue Customisé du NPC dans le 'tablist'.
     *
     * @param player Le Joueur qui verra le changement
     * @param showOnTabList Pourra-t-on voir le NPC dans le 'tablist' ?
     */
    public void setCustomShowOnTabList(Player player, boolean showOnTabList) { getCustomAttributes(player).setShowOnTabList(showOnTabList); }

    /**
     * Réinitialise pour le Joueur en question la Vue Customisé du NPC dans le 'tablist'.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomShowOnTabList(Player player) { getCustomAttributes(player).showOnTabList = null; }

    /**
     * Définit une {@link Pose Posture} Customisé du NPC pour un Joueur.
     *
     * @param player Le Joueur qui verra le changement
     * @param pose La {@link Pose Posture} qui sera affiché au 'tablist'
     */
    public void setCustomPose(Player player, Pose pose) { getCustomAttributes(player).setPose(pose); }

    /**
     * Réinitialise la {@link Pose Posture} Customisé du NPC pour le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomPose(Player player) { getCustomAttributes(player).pose = null; }

    /**
     * Définit Un Champ de Vision Customisé pour un Joueur pour l'aperçu du NPC.
     *
     * @param player Le Joueur dont il est question
     * @param lineSpacing Le Champ de Vision à définir
     */
    public void setCustomLineSpacing(Player player, double lineSpacing) { getCustomAttributes(player).setLineSpacing(lineSpacing); }

    /**
     * Réinitialise le Champ de Vision Customisé pour le Joueur demandé pour l'aperçu du NPC.
     *
     * @param player Le Joueur dont il est question
     */
    public void resetCustomLineSpacing(Player player){ getCustomAttributes(player).lineSpacing = null; }

    /**
     * Définit un Alignement de Texte Customisé de l'affichage du Texte du NPC pour un Joueur.
     *
     * @param player Le Joueur qui verra le changement
     * @param alignment L'{@link Vector Alignement} dont il est question
     */
    public void setCustomTextAlignment(Player player, Vector alignment) { getCustomAttributes(player).setTextAlignment(alignment.clone()); }

    /**
     * Réinitialise l'Alignement de Texte Customisé de l'affichage du Texte du NPC pour le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomTextAlignment(Player player) { getCustomAttributes(player).textAlignment = null; }

    /**
     * Définit un Temps d'Intéraction Customisé du NPC pour un Joueur.
     *
     * @param player Le Joueur qui subira le changement
     * @param millis Le Temps d'Intéraction en milliseconde
     */
    public void setCustomInteractCooldown(Player player, long millis) { getCustomAttributes(player).setInteractCooldown(millis); }

    /**
     * Réinitialise le Temps d'Intéraction Customisé du NPC pour le Joueur demandé.
     *
     * @param player Le Joueur qui subira le changement
     */
    public void resetCustomInteractCooldown(Player player) { getCustomAttributes(player).interactCooldown = null; }

    /**
     * Définit une {@link NPC.Hologram.Opacity Opacité} de Texte Customisé de l'affichage du Texte du NPC pour un Joueur.
     *
     * @param player Le Joueur qui verra le changement
     * @param opacity L'{@link NPC.Hologram.Opacity Opacité} dont il est question
     */
    public void setCustomTextOpacity(Player player, NPC.Hologram.Opacity opacity) { getCustomAttributes(player).setTextOpacity(opacity); }

    /**
     * Réinitialise l'{@link NPC.Hologram.Opacity Opacité} de Texte Customisé de l'affichage du Texte du NPC le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomTextOpacity(Player player) { getCustomAttributes(player).textOpacity = null; }

    /**
     * Définit une Vitesse de Mouvement Customisée du NPC pour un Joueur.
     *
     * @param player Le Joueur qui verra le changement
     * @param moveSpeed La Vitesse de Mouvement dont il est question
     */
    public void setCustomMoveSpeed(Player player, double moveSpeed) { getCustomAttributes(player).setMoveSpeed(moveSpeed); }

    /**
     * Réinitialise une Vitesse de Mouvement Customisée du NPC le Joueur demandé.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomMoveSpeed(Player player) { getCustomAttributes(player).moveSpeed = null; }

    /**
     * Définit pour un Joueur si le NPC sera en feu.
     *
     * @param player Le Joueur qui verra le changement
     * @param onFire Le NPC doit-il brûler ?
     */
    public void setCustomOnFire(Player player, boolean onFire) { getCustomAttributes(player).setOnFire(onFire); }

   /**
     * Réinitialise pour le Joueur demandé le NPC étant en feu.
     *
     * @param player Le Joueur qui verra le changement
     */
    public void resetCustomOnFire(Player player) { getCustomAttributes(player).onFire = null; }

   /**
     * Réinitialise tous les Attributs Cutomisés du NPC pour un Joueur.
     *
     * @param player Le Joueur qen question
     */
    public void resetAllCustomAttributes(Player player) { customAttributes.put(player.getUniqueId(), new Attributes(null)); }

    /**
     * Définit si le NPC supprime tous ses Attributs Customisés quand un Joueur lui est dissocié.
     *
     * @param resetCustomAttributes Doit-on supprimer tous les Attributs Customisés du NPC quand un Joueur lui est dissocié
     *
     */
    public void setResetCustomAttributesWhenRemovePlayer(boolean resetCustomAttributes) { this.resetCustomAttributes = resetCustomAttributes; }

    /**
     * Vérifie si le NPC supprime tous ses Attributs Customisés quand un Joueur lui est dissocié.
     *
     * @return Une valeur Booléenne
     */
    public boolean isResetCustomAttributesWhenRemovePlayer() { return resetCustomAttributes; }

    /**
     * Met à jour tous les attributs du NPC pour un Joueur.
     *
     * @param player Le Joueur en question
     */
    private void updateAttributes(Player player) {

        NPCPersonal npcPersonal = getPersonal(player); // Récupère le NPC Personnel du Joueur
        NPC.Attributes A = getAttributes(); // Récupère les Attributs du NPC
        NPC.Attributes cA = getCustomAttributes(player); // Récupère les Attributs Customisés du NPC

        npcPersonal.updateGlobalLocation(this); // Met à jour la localisation globale du NPC

        // Si le NPC contient un Skin pour chaque Joueur, mais que le Nom de son Skin ne correspondant pas au Nom du Joueur ou alors est null, on redéfinit son Skin en forçant la mise à jour du NPC
        if(ownPlayerSkin && (npcPersonal.getSkin().getPlayerName() == null || !npcPersonal.getSkin().getPlayerName().equals(player.getName()))) npcPersonal.setSkin(player, skin -> npcPersonal.forceUpdate());

        // Sinon, on définit au NPC le Skin récupéré depuis ses Attibuts ou Attributs Customisés
        else npcPersonal.setSkin(cA.skin != null ? cA.skin : A.skin);

        // Définit les Parties du Skin visibles du NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setSkinParts(cA.skinParts != null ? cA.skinParts : A.skinParts);

        // Définit les Collisions du NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setCollidable(cA.collidable != null ? cA.collidable : A.collidable);

        // Définit le Texte à afficher par le NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setText(cA.text != null ? cA.text : A.text);

        // Définit la Distance avant de Cacher le NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setHideDistance(cA.hideDistance != null ? cA.hideDistance : A.hideDistance);

        // Définit la Surbrillance du NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setGlowing(cA.glowing != null ? cA.glowing : A.glowing);

        // Définit la Couleur de Surbrillance du NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setGlowingColor(cA.glowingColor != null ? cA.glowingColor : A.glowingColor);

        // Définit le Type de Suivie du Regard du NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setGazeTrackingType(cA.gazeTrackingType != null ? cA.gazeTrackingType : A.gazeTrackingType);

        // Définit les Équipements du NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setSlots((HashMap<EquipmentSlot, ItemStack>)(cA.slots != null ? cA.slots : A.slots).clone());

        // Définit le Nom Customisé du NPC dans le 'tablist' depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setCustomTabListName(cA.customTabListName != null ? cA.customTabListName : A.customTabListName);

        // Définit l'Affichage du NPC dans le 'tablist' depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setShowOnTabList(cA.showOnTabList != null ? cA.showOnTabList : A.showOnTabList);

        // Définit la Posture du NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setPose(cA.pose != null ? cA.pose : A.pose);

        // Définit le Champ de Vision vue par les Joueurs du NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setLineSpacing(cA.lineSpacing != null ? cA.lineSpacing : A.lineSpacing);

        // Définit l'Alignement du Texte affiché par le NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setTextAlignment((cA.textAlignment != null ? cA.textAlignment : A.textAlignment).clone());

        // Définit le Temps d'Intéraction du NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setInteractCooldown(cA.interactCooldown != null ? cA.interactCooldown : A.interactCooldown);

        // Définit l'Opacité du Texte affiché par le NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setTextOpacity(cA.textOpacity != null ? cA.textOpacity : A.textOpacity);

        // Définit l'Opacité de chaques Lignes afficheant du Texte par le NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setLinesOpacity((HashMap<Integer, Hologram.Opacity>) (cA.linesOpacity != null ? cA.linesOpacity : A.linesOpacity).clone());

        // Définit la Vitesse de Déplacement du NPC depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setMoveSpeed(cA.moveSpeed != null ? cA.moveSpeed : A.moveSpeed);

        // Définit le NPC étant en Feu ou non depuis ses Attibuts ou Attributs Customisés
        npcPersonal.setOnFire(cA.onFire != null ? cA.onFire : A.onFire);
    }

    /**
     * Créer le NPC pour tous ses Joueurs associés.
     *
     */
    public void createAllPlayers() { players.forEach((player, npc)-> { if(!npc.isCreated()) create(player); }); }

    /**
     * Affiche le NPC pour tous les Joueurs actifs associés.
     *
     */
    public void show() { forEachActivePlayer((player, npc) -> show(player)); }

    /**
     * Cache le NPC pour tous les Joueurs actifs associés.
     *
     */
    public void hide() { forEachActivePlayer((player, npc) -> hide(player)); }

    /**
     * Met à jour le NPC pour tous les Joueurs actifs associés.
     *
     */
    @Override
    public void update() { forEachActivePlayer((player, npc) -> update(player)); }

    /**
     * Force la mise à jour du NPC pour tous les Joueurs actifs associés.
     *
     */
    @Override
    public void forceUpdate() { forEachActivePlayer((player, npc) -> forceUpdate(player)); }

    /**
     * Met à jour le Texte affiché par le NPC pour tous les Joueurs actifs associés.
     *
     */
    @Override
    public void updateText() { forEachActivePlayer((player, npc) -> updateText(player)); }

    /**
     * Force la mise à jour le Texte affiché par le NPC pour tous les Joueurs actifs associés.
     *
     */
    @Override
    public void forceUpdateText() { forEachActivePlayer((player, npc) -> forceUpdateText(player)); }

    /**
     * Détruit le NPC pour tous les Joueurs associés
     *
     */
    @Override
    public void destroy() {

        Set<Player> playerSet = new HashSet<>(players.keySet()); // Récupère tous les Joueurs
        playerSet.forEach((player) -> getNPCUtils().removePersonalNPC(getPersonal(player))); // On leur supprime à chacun leur NPC Personnel
    }

    /**
     * Définit un Nom Customisé du NPC pour tous ses Joueurs actifs associés.
     *
     * @param name Le Nom Customisé en question
     *
     */
    @Override
    public void setCustomName(@Nullable String name) {

        if(name == null) return;
        forEachActivePlayer((player, npc) -> npc.setCustomName(name));
        this.customName = name;
    }

   /**
     * Récupère le Nom Customisé du {@link NPCGlobal NPC Global} actuel.
     *
     * @return lL Nom Customisé du {@link NPCGlobal NPC Global} actuel
     */
    public String getCustomName() { return this.customName; }

    /**
     * Définit le Nom Customisé du NPC dans le 'tablist' pour tous ses Joueurs actifs associés.
     *
     * @param name Le Nom Customisé en question à afficher dans le 'tablist'
     *
     */
    @Override
    public void setCustomTabListName(@Nullable String name) { forEachActivePlayer((player, npc) -> setCustomTabListName(name)); }


    /**
     * Téléporte le NPC aux coordonnées précisées pour tous les Joueurs actifs associés.
     *
     * @param world Le Monde où téléporter le NPC
     * @param x La Coordonnée 'X' où téléporter le NPC
     * @param y La Coordonnée 'Y' où téléporter le NPC
     * @param z La Coordonnée 'Z' où téléporter le NPC
     * @param yaw La rotation 'yaw' du NPC
     * @param pitch La rotation 'pitch' du NPC
     *
     */
    @Override
    public void teleport(World world, double x, double y, double z, float yaw, float pitch) {


        NPC.Events.Teleport npcTeleportEvent = new NPC.Events.Teleport(this, new Location(world, x, y, z, yaw, pitch)); // Créer l'évènement de téléportation du NPC
        if(npcTeleportEvent.isCancelled()) return; // Si l'évènement a été annulé, alors on ne téléporte pas le NPC

        super.setWorld(world); // On change l'attribut du monde du NPC
        super.setX(x); // On change l'attribut la coordonée 'X' du NPC
        super.setY(y); // On change l'attribut la coordonée 'Y' du NPC
        super.setZ(z); // On change l'attribut la coordonée 'Z' du NPC

        super.setYaw(yaw); // On change l'attribut de la rotation 'yaw' du NPC
        super.setDefaultYAW(yaw); // On change l'attribut de la rotation 'yaw' par défaut du NPC
        super.setPitch(pitch); // On change l'attribut de la rotation 'pitch' du NPC
        super.setDefaultPITCH(yaw); // On change l'attribut de la rotation 'yaw' par défaut du NPC

        forEachActivePlayer((player, npc)-> npc.teleport(world, x, y, z, yaw, pitch));  // Effectue le téléportement pour tous les Joueurs actifs associés au NPC
    }

    /**
     * Le NPC va regarder à un endroit en fonction de la rotation 'yaw' et 'pitch' demandé.
     *
     * @param yaw La rotation 'yaw' dans laquelle le NPC va se tourner
     * @param pitch La rotation 'pitch' dans laquelle le NPC va se tourner
     *
     */
    public void lookAt(float yaw, float pitch) { lookAt(yaw, pitch, false); }

    /**
     * Le NPC va regarder à un endroit en fonction de la rotation 'yaw' et 'pitch' demandé.
     *
     * @param yaw La rotation 'yaw' dans laquelle le NPC va se tourner
     * @param pitch La rotation 'pitch' dans laquelle le NPC va se tourner
     * @param forcelook Doit-on forçer la rotation par défaut du NPC ?
     *
     */
    @Override
    public void lookAt(float yaw, float pitch, boolean forcelook) {

        super.setYaw(yaw); // Définit rotation 'yaw' dans laquelle le NPC va se tourner
        super.setPitch(pitch); // Définit rotation 'pitch' dans laquelle le NPC va se tourner

        if(getGazeTrackingType().equals(GazeTrackingType.NONE)) {

            super.setDefaultYAW(yaw); // Définit rotation 'yaw' par défaut dans laquelle le NPC va se tourner
            super.setDefaultPITCH(pitch); // Définit rotation 'yaw' par défaut dans laquelle le NPC va se tourner
        }

        // si on a demandé de forcçer la rotation par défaut du NPC, alors on le fait
        if(forcelook) {

            super.setDefaultYAW(yaw); // Définit rotation 'yaw' par défaut dans laquelle le NPC va se tourner
            super.setDefaultPITCH(pitch); // Définit rotation 'yaw' par défaut dans laquelle le NPC va se tourner
        }

        forEachActivePlayer((player, npc)-> npc.lookAt(yaw, pitch)); // Effectue la rotation de la tête du NPC pour tous les Joueurs actifs associés
    }

    /**
     * Le NPC va jouer une {@link Animation Animation} pour tous les Joueurs actifs associés.
     *
     * @param animation L'{@link Animation} a joué
     */
    @Override
    public void playAnimation(Animation animation) { forEachActivePlayer((player, npc) -> playAnimation(player, animation)); }

    /**
     * Le NPC va jouer une {@link Animation Animation} pour un Joueur.
     *
     * @param player Le joueur qui verra l'{@link Animation}
     * @param animation L'Animation a joué
     */
    public void playAnimation(Player player, Animation animation) { getPersonal(player).playAnimation(animation); }

    /**
     * Le NPC va recevoir un dégat :).<br/><br/>
     *
     * Une {@link Animation Animation} est jouée :<br/> {@code Animation.TAKE_DAMAGE}<br/><br/>
     * Un Son est jouée :<br/> {@code Sound.ENTITY_PLAYER_ATTACK_WEAK}
     */
    @Override
    public void hit() {

        playAnimation(Animation.TAKE_DAMAGE);
        forEachActivePlayer((player, npc) -> player.playSound(getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 1.0F, 1.0F));
    }

    /**
     * Le NPC va se déplacer aux coordonnées précisées pour tous les Joueurs actifs associés.
     *
     * @param moveX La Coordonnée 'X' où déplacer le NPC
     * @param moveY La Coordonnée 'Y' où déplacer le NPC
     * @param moveZ La Coordonnée 'Z' où déplacer le NPC
     *
     */
    @Override
    protected void move(double moveX, double moveY, double moveZ) {

        // Si les coordonnées récupérées sont supérieures à 8 blocs, on affiche une erreur
        Validate.isTrue(Math.abs(moveX) < 8 && Math.abs(moveY) < 8 && Math.abs(moveZ) < 8, "Les NPC ne peuvent pas se déplacer de 8 blocs ou plus à la fois, utilisez la téléportation à la place.");

        // On crée un évènement de mouvemen pour le NPC
        NPC.Events.Move npcMoveEvent = new NPC.Events.Move(this, new Location(super.getWorld(), super.getX() + moveX, super.getY() + moveY, super.getZ() + moveZ));
        if(npcMoveEvent.isCancelled()) return; // Si l'èvènement a été annulé, le NPC se déplacera pas

        super.setX(super.getX() + moveX); // Définit la nouvelle coordonnée 'X' au NPC
        super.setY(super.getY() + moveY); // Définit la nouvelle coordonnée 'Y' au NPC
        super.setZ(super.getZ() + moveZ); // Définit la nouvelle coordonnée 'Z' au NPC

        forEachActivePlayer((player, npc) -> npc.move(moveX, moveY, moveZ)); // Déplace le NPC en question pour tous les Joueurs actifs associés
    }

    /**
     * Met à jour la rotation du NPC pour tous ses Joueurs actifs associés.
     *
     */
    @Override
    protected void updatePlayerRotation() { forEachActivePlayer((player, npc) -> updatePlayerRotation(player)); }

    /**
     * Met à jour la rotation du NPC pour un Joueur en question.
     *
     * @param player Le Joueur en question
     *
     */
    protected void updatePlayerRotation(Player player) { getPersonal(player).updatePlayerRotation(); }

    /**
     * Met à jour la {@link Location Localisation} du NPC pour tous ses Joueurs actifs associés.
     *
     */
    @Override
    protected void updateLocation() { forEachActivePlayer((player, npc) -> npc.updateLocation()); }

    /**
     * Met à jour le mouvement du NPC pour tous ses Joueurs actifs associés.
     *
     */
    @Override
    protected void updateMove() { forEachActivePlayer((player, npc) -> npc.updateMove());  }

    /**
     * Le NPC va jeter un {@link ItemStack item} précis.
     *
     * @param itemStack L'{@link ItemStack Item} en question
     *
     * @return L'{@link ItemStack Item} jeté
     */
    public Item dropItem(ItemStack itemStack) {

        // Si l'item est null ou alors est de l'air, on retourne null
        if(itemStack == null || itemStack.getType().equals(Material.AIR)) return null;

        // Sinon, on retourne l'item jeté
        return getWorld().dropItemNaturally(getLocation(), itemStack);
    }

   /**
     * Le NPC va jeter un {@link ItemStack item} qu'il aurait équipé.
     *
     * @param slot L'{@link EquipmentSlot Équipement} auquel recherché L'{@link ItemStack Item} a jeté
     *
     * @return L'{@link ItemStack Item} jeté équipé par le NPC
     */
    public Item dropItemInSlot(EquipmentSlot slot) {

        ItemStack itemStack = getEquipment(slot); // Récupère l'item associé à l'équipement en question du NPC

        // Si l'item est null ou alors est de l'air, on retourne null
        if(itemStack == null || itemStack.getType().equals(Material.AIR)) return null;

        clearEquipment(slot); // On enlève l'item à l'équipement en question du NPC
        Item item = dropItem(itemStack); // On jette l'item récupéré
        update(); // On met à jour le NPC

        return item; // On retourne l'item jeté
    }

    /**
     * Le NPC va jeter un {@link ItemStack item} de sa main principale.
     *
     * @return L'{@link ItemStack Item} de la main principale du NPC
     */
    public Item dropItemInHand() { return dropItemInSlot(EquipmentSlot.MAINHAND); }

    /**
     * Définit si le NPC va obtenir un {@link Skin Skin} par Joueurs associés.
     *
     * @param ownPlayerSkin Le NPC doit-il recevoir un Skin par Joueurs associés ?
     *
     */
    public void setOwnPlayerSkin(boolean ownPlayerSkin) { this.ownPlayerSkin = ownPlayerSkin; }

    /**
     * Vérifie si le NPC obtient un {@link Skin Skin} par Joueurs associés.
     *
     * @return Une valeur Booléenne
     */
    public boolean isOwnPlayerSkin() { return ownPlayerSkin; }

    /**
     * Définit au NPC qu'il va obtenir un {@link Skin Skin} par Joueurs associés.
     *
     */
    public void setOwnPlayerSkin() { setOwnPlayerSkin(true); }

    /**
     * Récupère le NPC Personnel d'un Joueur
     *
     * @param player Le Joueur en question
     *
     * @return le NPC Personnel du Joueur demandé
     */
    public NPCPersonal getPersonal(Player player) {

        // Si le Joueur n'est pas associé au NPC, on affiche une erreur
        Validate.isTrue(players.containsKey(player), "Le joueur n'est pas ajouté à ce NPC Globale");
        return players.get(player); // Récupère le NPC Personnel du Joueur en question
    }

    /**
     * Récupère Les Attributs Customisés d'un NPC d'un Joueur
     *
     * @param player Le Joueur en question
     *
     * @return Les Attributs Customisés d'un NPC d'un Joueur
     */
    public NPC.Attributes getCustomAttributes(Player player) {

        // Si le Joueur n'est pas associé au NPC, on affiche une erreur
        Validate.isTrue(customAttributes.containsKey(player.getUniqueId()), "Le joueur n'est pas ajouté à ce NPC Globale");
        return customAttributes.get(player.getUniqueId()); // Récupère les attributs customisés du NPC par rapport au Joueur en question
    }

    /**
     * Définit le Joueur le plus proche du NPC
     *
     * @param entity L'Entité en question (soit le Joueur)
     *
     */
    protected void np(Entity entity) {

        this.nearestPlayer = entity; // Définit le Joueur le plus proche
        this.lastNearestPlayerUpdate = System.currentTimeMillis(); // Définit la dernière mise à jour actualisée de ce joueur étant proche du NPC
    }

    /**
     * Récupère le Joueur le plus proche du NPC
     *
     * @return L'Entité en question le plus proche (soit le Joueur)
     */
    protected Entity np() {

        // Si le joueur n'est plus proche du NPC, on définit le Joueur le plus proche sur null
        if(System.currentTimeMillis() - lastNearestPlayerUpdate > LOOK_TICKS * (1000 / 20)) nearestPlayer = null;
        return nearestPlayer; // Récupère le Joueur le plus proche
    }

    /**
     * Définit l'Entité le plus proche du NPC
     *
     * @param entity L'Entité en question
     *
     */
    protected void ne(Entity entity) {

        this.nearestEntity = entity; // Définit l'Entité le plus proche
        this.lastNearestEntityUpdate = System.currentTimeMillis();  // Définit la dernière mise à jour actualisée de ce joueur étant proche du NPC
    }

    /**
     * Récupère l'Entité le plus proche du NPC
     *
     * @return L'Entité en question le plus proche
     */
    protected Entity ne() {

        // Si l'Entité n'est plus proche du NPC, on définit l'Entité le plus proche sur null
        if(System.currentTimeMillis() - lastNearestEntityUpdate > LOOK_TICKS * (1000 / 20)) nearestEntity = null;
        return nearestEntity; // Récupère l'Entité le plus proche
    }

                                            /* ------------------------------------------------------- */
                                            /* ------------------------------------------------------- */
                                            /* ------------------------------------------------------- */

    /**
     * LA GESTION DE PERSISTANCE DU {@link NPCGlobal}.
     *
     */
    public static class PersistentManager {

        private static HashMap<Plugin, HashMap<String, PersistentManager>> PERSISTENT_DATA; // Variable récupérant les données de persistance

        /**
         * On initialise les données de persistance.
         */
        static { PERSISTENT_DATA = new HashMap<>(); }

        /**
         * Récupère un gestionnaire de persistance par son identifiant.
         *
         * @param plugin Le Plugin associé
         * @param id L'Identifiant en question
         *
         * @return Un {@link PersistentManager Gestionnaire de Persistance}
         */
        public static PersistentManager getPersistent(Plugin plugin, String id) {

            checkExistPlugin(plugin); // On vérifie si le plugin est associé au gestionnaire de persistance

            // On vérifie si la donnée de persistance par rapport au plugin contient bien l'identifiant en question, alors on retourne le gestionnaire de persistance
            if(PERSISTENT_DATA.get(plugin).containsKey(id)) return PERSISTENT_DATA.get(plugin).get(id);
            else return new PersistentManager(plugin, id); // Sinon, on crée un nouveau gestionnaire de persitance
        }

        /**
         * Définit au gestionnaire de persistance une donnée globale de persistance.
         *
         * @param plugin Le Plugin associé
         * @param id L'Identifiant du gestionnaire de persistance
         * @param globalPersistentData La donnée globale de persistance
         *
         */
        private static void setPersistentData(Plugin plugin, String id, PersistentManager globalPersistentData) {

            checkExistPlugin(plugin); // On vérifie si le plugin est associé au gestionnaire de persistance

            // On ajoute au gestionnaire ayant l'identifiant récupéré, la donnée globale de persistance dont il est question
            PERSISTENT_DATA.get(plugin).put(id, globalPersistentData);
        }

        /**
         * Vérifie si un plugin est associé au gestionnaire de persistance.
         *
         * @param plugin Le Plugin associé à vérifier.
         *
         */
        private static void checkExistPlugin(Plugin plugin) { if(!PERSISTENT_DATA.containsKey(plugin)) PERSISTENT_DATA.put(plugin, new HashMap<>()); }

        /**
         * Définit une action pour toutes les données globales persitantes du NPC.
         *
         * @param action L'Action à faire
         *
         */
        protected static void forEachGlobalPersistent(Consumer<NPCGlobal> action) { PERSISTENT_DATA.forEach((x, y) -> forEachGlobalPersistent(x, action)); }

        /**
         * Définit une action pour toutes les données globales persitantes du NPC associé au plugin spécifié.
         *
         * @param action Le Plugin associé
         * @param action l'Action à faire
         *
         */
        protected static void forEachGlobalPersistent(Plugin plugin, Consumer<NPCGlobal> action) { PERSISTENT_DATA.get(plugin).values().stream().filter(x-> x.npcGlobal != null).forEach(x -> action.accept(x.npcGlobal)); }

        /**
         * Définit une action pour toutes les données persitantes du NPC.
         *
         * @param action L'Action à faire
         *
         */
        protected static void forEachPersistentManager(Consumer<NPCGlobal.PersistentManager> action) { PERSISTENT_DATA.forEach((x, y) -> forEachPersistentManager(x, action)); }

        /**
         * Définit une action pour toutes les données persitantes du NPC associé au plugin spécifié.
         *
         * @param action Le Plugin associé
         * @param action l'Action à faire
         *
         */
        protected static void forEachPersistentManager(Plugin plugin, Consumer<NPCGlobal.PersistentManager> action) { PERSISTENT_DATA.get(plugin).values().forEach(action); }

                                                         /* -------------------------------------------------------------------- */

        private Plugin plugin; // Variable récupérant le plugin associé
        private String id; // Variable récupérant l'identifiant du gestionnaire persistant
        private NPCGlobal npcGlobal;  // Variable récupérant le NPC Global associé.
        private File file;  // Variable récupérant le fichier associé au gestionnaire
        private FileConfiguration config; // Variable récupérant le fichier de configuration associé au fichier
        private LastUpdate lastUpdate; // Variable récupérant la dernière mise à jour

        protected PersistentManager(Plugin plugin, String simpleID) {

            this.plugin = plugin; // On initialise le plugin
            this.id = simpleID; // On initialise l'identifiant
            this.file = new File(getFilePath()); // On initialise le fichier associé au gestionnaire
            this.lastUpdate = new LastUpdate(); // On initialise la dernière mise à jour


            setPersistentData(plugin, id, this); // On définit la donnéer persistante en fonction du gestionnaire persistant instancié
        }

        /**
         * Recharge le NPC avec le gestionnaire de persistance.
         */
        public void load() {

            checkFileExists(); // Vérifie l'éxistance du fichier de configuration

            this.config = YamlConfiguration.loadConfiguration(file); // On récupère le fichier de configuration
            if(npcGlobal != null) NPCUtils.getInstance().removeGlobalNPC(npcGlobal); // Si le NPC Global est pas null, on le supprime

            Location location = config.getLocation("location"); // On récupère la localisation du NPC
            Visibility visibility = Visibility.EVERYONE; // On définit la visibilité du NPC sur 'TOUT LE MONDE'
            String visibilityPermission = null; // On définit la permission de visibilité du NPC sur NULL

            // On vérifie si le fichier de configuration contient la visibilité du NPC, alors on redéfinit sa visibilité
            if(config.contains("visibility.type")) visibility = Visibility.valueOf(config.getString("visibility.type"));

            // On vérifie si le fichier de configuration contient la permission de visibilité du NPC, alors on redéfinit sa permission de visibilité
            if(config.contains("visibility.requirement")) visibilityPermission = config.getString("visibility.requirement");
            String finalVisibilityPermission = visibilityPermission; // On redéfinit la permission de visibilité du NPC dans une autre variable

            // On régénère le NPC Global en précisant les informations actuel
            npcGlobal = NPCUtils.getInstance().generateGlobalNPC(plugin, id, visibility, visibilityPermission != null ? (player -> player.hasPermission(finalVisibilityPermission)) : null, location);
            npcGlobal.persistent = true; // On définit sa persistance sur vrai
            npcGlobal.persistentManager = this; // On définit son gestionnaire de persistance par celui-ci

            // ⬇️ Vérifie si le fichier de configuration contient un Skin customisé, alors on récupère au NPC son Skin customisé ⬇️ //
            if(config.contains("skin.custom.enabled") && config.getBoolean("skin.custom.enabled") && config.contains("skin.custom.texture") && config.contains("skin.custom.signature")) {

                String texture = config.getString("skin.custom.texture"); // Récupère la texture du Skin customisé
                String signature = config.getString("skin.custom.signature"); // Récupère la signature du Skin customisé

                // Si la taille de la signature est inférieure à '684' ou égal à '0', on redéfinit la texture et la signature par le Skin Steve de Base
                if(signature.length() < 684 || texture.length() == 0) { texture = Skin.STEVE.getTexture(); signature = Skin.STEVE.getSignature(); }
                npcGlobal.setSkin(texture, signature); // Ajoute le Skin au NPC
            }
            // ⬆️ Vérifie si le fichier de configuration contient un Skin customisé, alors on récupère au NPC son Skin customisé ⬆️ //

            // Sinon, si le fichier de configuration contient un Skin de Joueur, on récupère le Skin du Joueur et l'attribut au NPC
            else if(config.contains("skin.player")) npcGlobal.setSkin(config.getString("skin.player"), skin -> npcGlobal.forceUpdate());

            // ⬇️ Si le fichier de configuration contient un hologramme (pour afficher du texte), on essaie alors d'attribuer le texte au NPC ⬇️ //
            if(config.contains("hologram.text")) {

                List<String> lines = config.getStringList("hologram.text"); // Récupère toutes les lignes du texte

                // ⬇️ Pour toutes les lignes du texte, on vérifie le code couleur de chaque mot et attribut le texte au NPC ⬇️ //
                if(lines != null && lines.size() > 0) {

                    List<String> coloredLines = new ArrayList<>(); // Liste récupérant les couleurs dans une ligne
                    lines.forEach(x-> coloredLines.add(x.replaceAll("&", "§"))); // On récupère le texte en convertissant les codes couleurs
                    npcGlobal.setText(coloredLines); // On ajoute le texte récupéré au NPC.
                }
                // ⬆️ Pour toutes les lignes du texte, on vérifie le code couleur de chaque mot et attribut le texte au NPC ⬆️ //
            }
            // ⬆️ Si le fichier de configuration contient un hologramme (pour afficher du texte), on essaie alors d'attribuer le texte au NPC ⬆️ //

            // Si le fichier de configuration contient une opacité du texte, on l'ajoute donc au NPC
            if(config.contains("hologram.textOpacity")) npcGlobal.setTextOpacity(Hologram.Opacity.valueOf(config.getString("hologram.textOpacity")));

            // ⬇️ Si le fichier de configuration contient une opacité de chaques lignes, pour toutes les lignes, on l'ajoute donc au NPC ⬇️ //
            if(config.getConfigurationSection("hologram.linesOpacity") != null) {

                for(String line : config.getConfigurationSection("hologram.linesOpacity").getKeys(false)) npcGlobal.setLineOpacity(Integer.valueOf(line), Hologram.Opacity.valueOf(config.getString("hologram.linesOpacity." + line)));
            }
            //  ⬆️ Si le fichier de configuration contient une opacité de chaques lignes, pour toutes les lignes, on l'ajoute donc au NPC  ⬆️ //

            // Si le fichier de configuration contient une visibilité que des Joueurs sélectionnés associés au NPC, alors on définit cette visibilité au NPC
            if(config.contains("visibility.selectedPlayers") && npcGlobal.getVisibility().equals(Visibility.SELECTED_PLAYERS)) npcGlobal.selectedPlayers = config.getStringList("visibility.selectedPlayers");

            // Si le fichier de configuration contient un alignement spécifique pour le Texte qu'il affiche, alors on définit cet alignement au NPC
            if(config.contains("hologram.alignment")) npcGlobal.setTextAlignment(config.getVector("hologram.alignment"));

             // Si le fichier de configuration contient que le NPC peut avoir un Skin par Joueurs associés, alors on le définit au NPC
            if(config.contains("skin.ownPlayer")) npcGlobal.setOwnPlayerSkin(config.getBoolean("skin.ownPlayer"));

            // Pour toutes les parties de Skin visibles du NPC, si le fichier de configuration contient une en particulère, on l'informe au NPC
            Arrays.stream(Skin.Part.values()).filter(x-> config.contains("skin.parts." + x.name().toLowerCase())).forEach(x-> npcGlobal.getSkinParts().setVisible(x, config.getBoolean("skin.parts." + x.name().toLowerCase())));

            // Si le fichier de configuration contient une couleur de surbrillance spécifique pour le NPC, on la définit au NPC
            if(config.contains("glow.color")) npcGlobal.setGlowingColor(ChatFormatting.valueOf(config.getString("glow.color")));

            // Si le fichier de configuration accepte la surbrillance du NPC ou non, on l'indique au NPC
            if(config.contains("glow.enabled")) npcGlobal.setGlowing(config.getBoolean("glow.enabled"));

            // Si le fichier de configuration contient une posture en question pour le NPC, on la définit au NPC
            if(config.contains("pose")) npcGlobal.setPose(Pose.valueOf(config.getString("pose")));

            // Si le fichier de configuration accepte la collision du NPC ou non, on l'indique au NPC
            if(config.contains("collidable")) npcGlobal.setCollidable(config.getBoolean("collidable"));

            // Si le fichier de configuration accepte l'affichage du NPC au 'tablist' ou non, on l'indique au NPC
            if(config.contains("tabList.show")) npcGlobal.setShowOnTabList(config.getBoolean("tabList.show"));

            // Si le fichier de configuration contient un Nom Customisé dans le 'tablist' pour le NPC, on le définit au NPC
            if(config.contains("tabList.name")) npcGlobal.setCustomTabListName(config.getString("tabList.name").replaceAll("&", "§"));

            // Si le fichier de configuration accepte le NPC étant en feu ou non, on l'indique au NPC
            if(config.contains("onFire")) npcGlobal.setOnFire(config.getBoolean("onFire"));

            // Si le fichier de configuration contient une vitesse de déplacement spécifique pour le NPC, on la définit au NPC
            if(config.contains("move.speed")) npcGlobal.setMoveSpeed(config.getDouble("move.speed"));

            // Si le fichier de configuration contient un temps d'intéraction spécifique pour le NPC, on le définit au NPC
            if(config.contains("interact.cooldown")) npcGlobal.setInteractCooldown(config.getLong("interact.cooldown"));

            // Si le fichier de configuration contient un type de suivie du regard pour le NPC, on le définit au NPC
            if(config.contains("gazeTracking.type")) npcGlobal.setGazeTrackingType(GazeTrackingType.valueOf(config.getString("gazeTracking.type")));

            // Si le fichier de configuration contient une distance spécifique pour cacher le NPC, on la définit au NPC
            if(config.contains("distance.hide")) npcGlobal.setHideDistance(config.getDouble("distance.hide"));

            // ⬇️ Pour tous les équipements du NPC, si on obtient à partir du fichier de configuration un item en particulier, on le définit au NPC ⬇️ //
            for(EquipmentSlot slot : EquipmentSlot.values()) {

                // Si le fichier de configuration ne contient pas l'équipement à récupérer en continu la boucle
                if(!config.contains("slots." + slot.name().toLowerCase())) continue;
                ItemStack item = null; // On définit l'item à ajouter sur null

                // ⬇️ On essaie de récupérer l'item depuis le fichier de configuration, sinon on définit sur le fichier de configuration un item d'air (Soit : rien) ⬇️ //
                try { item = config.getItemStack("slots." + slot.name().toLowerCase()); }
                catch(Exception e) { config.set("slots." + slot.name().toLowerCase(), new ItemStack(Material.AIR)); }
                // ⬆️ On essaie de récupérer l'item depuis le fichier de configuration, sinon on définit sur le fichier de configuration un item d'air (Soit : rien) ⬆️ //

                npcGlobal.setItem(slot, item); // Définit l'item pour l'équipement associé au NPC
            }
            // ⬆️ Pour tous les équipements du NPC, si on obtient à partir du fichier de configuration un item en particulier, on le définit au NPC ⬆️ //

            // Pour tous les équipements du NPC, si on obtient à partir du fichier de configuration un item en particulier, on le définit au NPC
            Arrays.stream(EquipmentSlot.values()).filter(x-> config.contains("slots." + x.name().toLowerCase())).forEach(x-> npcGlobal.setItem(x, config.getItemStack("slots." + x.name().toLowerCase())));

            // ⬇️ Si le fichier de configuration conctient des données customisé pour le NPC, on lui ajoute au NPC ⬇️ //
            if(config.getConfigurationSection("customData") != null) {

                for(String keys : config.getConfigurationSection("customData").getKeys(false)) npcGlobal.setCustomData(keys, config.getString("customData." + keys));
            }
            // ⬆️ Si le fichier de configuration conctient des données customisé pour le NPC, on lui ajoute au NPC ⬆️ //

            // ⬇️ Si le fichier de configuration contient des actions d'intéraction pour le NPC, on ajoute donc ses actions d'intéraction au NPC ⬇️ //
            if(config.getConfigurationSection("interact.actions") != null) {

                // ⬇️ Pour toutes les actons d'intéractions récupéré dans le fichier de configuration, on essaie d'ajouter ses actions au NPC ⬇️ //
                for(String keys : config.getConfigurationSection("interact.actions").getKeys(false)) {

                    // On récupère le type d'action actuel dans le fichier de configuration
                    Interact.Actions.Type actionType = Interact.Actions.Type.valueOf(config.getString("interact.actions." + keys + ".type"));

                    // On récupère le type de cliqué actuel dans le fichier de configuration
                    Interact.ClickType clickType = Interact.ClickType.valueOf(config.getString("interact.actions." + keys + ".click"));

                    // Si le type d'action actuel est un message à énvoyé, on récupère le message et ajoute l'action en question au NPC
                    if(actionType.equals(Interact.Actions.Type.SEND_MESSAGE)) {

                        List<String> message = config.getStringList("interact.actions." + keys + ".messages");
                        String[] messages = new String[message.size()];

                        for(int i = 0; i < message.size(); i++) messages[i] = message.get(i).replaceAll("&", "§"); // Récupère le Message
                        npcGlobal.addMessageClickAction(clickType, messages); // Ajoute d'envoi du message par le tchat au NPC

                    // Sinon, si le type d'action actuel est un message à énvoyé dans la barre d'action, on récupère le message et ajoute l'action en question au NPC
                    } else if(actionType.equals(Interact.Actions.Type.SEND_ACTIONBAR_MESSAGE)) {

                        String message = config.getString("interact.actions." + keys + ".message").replaceAll("&", "§"); // Récupère le Message
                        npcGlobal.addActionBarMessageClickAction(clickType, message); // Ajoute d'envoi du message d'action au NPC

                    // Sinon, si le type d'action actuel est une connexion BungeeCord dans un serveur, on récupère le nom du serveur et ajoute l'action en question au NPC
                    } else if(actionType.equals(Interact.Actions.Type.CONNECT_BUNGEE_SERVER)) {

                        String server = config.getString("interact.actions." + keys + ".server"); // Récupère le nom du Serveur
                        npcGlobal.addConnectBungeeServerClickAction(clickType, server); // Ajoute l'action de connexion à ce serveur au NPC

                    // Sinon, si le type d'action actuel est une commande à énvoyé par la console, on récupère la commande et ajoute l'action en question au NPC
                    } else if(actionType.equals(Interact.Actions.Type.RUN_CONSOLE_COMMAND)) {

                        String command = config.getString("interact.actions." + keys + ".command"); // Récupère la commande
                        npcGlobal.addRunConsoleCommandClickAction(clickType, command); // Ajoute l'action d'envoi de la commande par la console au NPC

                    // Sinon, si le type d'action actuel est une commande à énvoyé par le joueur, on récupère la commande et ajoute l'action en question au NPC
                    } else if(actionType.equals(Interact.Actions.Type.RUN_PLAYER_COMMAND)) {

                        String command = config.getString("interact.actions." + keys + ".command"); // Récupère la commande
                        npcGlobal.addRunPlayerCommandClickAction(clickType, command); // Ajoute l'action d'envoi de la commande par le joueur au NPC

                    // Sinon, si le type d'action actuel est un titre à énvoyé, on récupère le titre, sous-titre, et temps, et ajoute l'action en question au NPC
                    } else if(actionType.equals(Interact.Actions.Type.SEND_TITLE_MESSAGE)) {

                        // Récupère le titre principal
                        String title = config.getString("interact.actions." + keys + ".title").replaceAll("&", "§");

                        // Récupère le sous-titre
                        String subtitle = config.getString("interact.actions." + keys + ".subtitle").replaceAll("&", "§");


                        int fadeIn = config.getInt("interact.actions." + keys + ".fadeIn"); // Récupère le temps d'apparition
                        int stay = config.getInt("interact.actions." + keys + ".stay"); // Récupère le temps d'affichage
                        int fadeOut = config.getInt("interact.actions." + keys + ".fadeOut"); // Récupère le temps de disparition

                        npcGlobal.addTitleMessageClickAction(clickType, title, subtitle, fadeIn, stay, fadeOut); // Ajoute l'action d'envoi du titre au NPC

                    // Sinon, si le type d'action actuel est une téléportation, on récupère la localisation et ajoute l'action en question au NPC
                    } else if(actionType.equals(Interact.Actions.Type.TELEPORT_TO_LOCATION)) {

                        Location location1 = config.getLocation("interact.actions." + keys + ".location"); // Récupère la localisation de la téléportation
                        npcGlobal.addTeleportToLocationClickAction(clickType, location1); // Ajoute l'action de téléportation au NPC
                    }
                }
                // ⬇️ Pour toutes les actons d'intéractions récupéré dans le fichier de configuration, on essaie d'ajouter ses actions au NPC ⬇️ //
            }
            // ⬆️ Si le fichier de configuration contient des actions d'intéraction pour le NPC, on ajoute donc ses actions d'intéraction au NPC ⬆️ //

            npcGlobal.forceUpdate(); // On force la mise à jour du NPC
            this.lastUpdate.load(); // On charge la dernière mise à jour du NPC


            // On envoie un message à la console disant que le NPC a été rechargé
            Bukkit.getServer().getConsoleSender().sendMessage(UtilityMain.getInstance().prefix + ChatColor.GRAY + "Persistent NPCGlobal NPC " + ChatColor.GREEN + npcGlobal.getCode() + ChatColor.GRAY + " has been loaded.");
        }

        /**
         * Sauvegarde le NPC avec le gestionnaire de persistance.
         */
        public void save() {

            // Si le NPC Globale est null, on essaie de le récupérer par son identifiant
            if(npcGlobal == null) npcGlobal = NPCUtils.getInstance().getGlobalNPC(plugin, id);
            if(npcGlobal == null || !npcGlobal.isPersistent()) return; // Si le NPC Globale est null ou n'est pas persistant, on ne fait rien

            checkFileExists(); // Vérifie l'éxistance du fichier de configuration

            // Si le fichier de configuration est null, on le recharge
            if(config == null) config = YamlConfiguration.loadConfiguration(file);

            // Si le fichier de configuration contient le fait de désactiver la sauvegarde de celui-ci, on s'arrête là
            if(config.contains("disableSaving") && config.getBoolean("disableSaving")) return;

            // On définit un en tête au fichier de configuration
            config.options().header("NPC NPCGlobal persistant " + npcGlobal.getCode());

            config.set("location", npcGlobal.getLocation()); // On sauvegarde dans le fichier la localisation du NPC
            config.set("visibility.type", npcGlobal.getVisibility().name()); // On sauvegarde dans le fichier la visibilité du NPC

            // On sauvegarde dans le fichier la permission de visibilité du NPC
            config.set("visibility.requirement", npcGlobal.getVisibilityRequirement() != null && npcGlobal.getCustomDataKeys().contains("visibilityrequirementpermission") ? npcGlobal.getCustomData("visibilityrequirementpermission") : null);

            // Si la visibilité du NPC est pour les joueurs sélectionnés, on l'indique dans le fichier de configuration
            if(npcGlobal.getVisibility().equals(Visibility.SELECTED_PLAYERS)) config.set("visibility.selectedPlayers", npcGlobal.selectedPlayers);
            else config.set("visibility.selectedPlayers", null); // Sinon, on l'indique sur null

            config.set("skin.player", npcGlobal.getSkin().getPlayerName());  // On sauvegarde dans le Skin du NPC

            // ⬇️ Si le fichier de configuration ne contient pas le Skin Custom pour le NPC, on l'initialise par défaut ⬇️ //
            if(!config.contains("skin.custom")) {

                config.set("skin.custom.enabled", false);
                config.set("skin.custom.texture", "");
                config.set("skin.custom.signature", "");
            }
            // ⬆️ Si le fichier de configuration ne contient pas le Skin Custom pour le NPC, on l'initialise par défaut ⬆️ //

            config.set("skin.ownPlayer", npcGlobal.isOwnPlayerSkin()); // On sauvegarde dans le fichier si le NPC peut avoir un Skin par Joueurs associés ou pas

            // On sauvegarde dans le fichier les parties du Skin visible du NPC
            Arrays.stream(Skin.Part.values()).forEach(x-> config.set("skin.parts." + x.name().toLowerCase(), npcGlobal.getSkinParts().isVisible(x)));
            config.set("customData", null); // On définit par défaut dans le fichier les données customisées du NPC sur 'null'

            // Si le NPC contient des données customisées, on l'ajoute dans le fichier de configuration
            for(String keys : npcGlobal.getCustomDataKeys()) config.set("customData." + keys, npcGlobal.getCustomData(keys));

            List<String> lines = npcGlobal.getText(); // Récupère toutes les lignes du texte affiché par le NPC

            // Si les lignes ne sont pas null, et que la taille est supérieure à 0, on sauvegarde chaque ligne dans le fichier
            if(lines != null && lines.size() > 0) {

                List<String> coloredLines = new ArrayList<>(); // Liste récupérant les couleurs pour une ligne
                lines.forEach(x-> coloredLines.add(x.replaceAll("§", "&"))); // On récupère le texte en convertissant les codes couleurs
                config.set("hologram.text", coloredLines); // On sauvegarde le texte récupéré du NPC

            } else config.set("hologram.text", lines); // Sinon, on sauvegarde la seule ligne actuelle

            config.set("hologram.lineSpacing", npcGlobal.getLineSpacing()); // Sauvegarde dans le fichier le champ de vision pour un joueur et le NPC
            config.set("hologram.textOpacity", npcGlobal.getTextOpacity().name()); // Sauvegarde dans le fichie l'opacité du texte affiché par le NPC
            config.set("hologram.linesOpacity", null); // Définit par défaut dans le fichier l'opacité de chaques lignes afficheant du texte par le NPC sur 'null'

            // Si le NPC contient une opacité sur ses lignes de textes, on effectue la sauvegarde de celui-ci dans le fichier de configuration
            for(Integer line : npcGlobal.getLinesOpacity().keySet()) config.set("hologram.linesOpacity." + line, npcGlobal.getLineOpacity(line).name());

            config.set("hologram.alignment", npcGlobal.getTextAlignment()); // Sauvegarde dans le fichier l'alignement du texte affiché par le NPC
            config.set("gazeTracking.type", npcGlobal.getGazeTrackingType().name());  // Sauvegarde dans le fichier le type de suivie de regard du NPC
            config.set("pose", npcGlobal.getPose().name()); // Sauvegarde dans le fichier la posture actuel du NPC
            config.set("collidable", npcGlobal.isCollidable()); // Sauvegarde dans le fichier si le NPC à la collision activée ou non
            config.set("distance.hide", npcGlobal.getHideDistance()); // Sauvegarde dans le fichier la distance auquel le NPC sera caché
            config.set("glow.enabled", npcGlobal.isGlowing()); // Sauvegarde dans le fichier si le NPC à la surbrillance activée ou non
            config.set("glow.color", npcGlobal.getGlowingColor().name()); // Sauvegarde dans le fichier la couleur de surbrillance actuel du NPC
            config.set("tabList.show", npcGlobal.isShowOnTabList()); // Sauvegarde dans le fichier si le NPC est affiché dans le 'tablist' ou non

            // Sauvegarde dans le fichier le nom customisé du NPC dans le 'tablist' en convertissant les codes couleurs
            config.set("tabList.name", npcGlobal.getCustomTabListName().replaceAll("§", "&"));

            config.set("move.speed", npcGlobal.getMoveSpeed()); // Sauvegarde dans le fichier la vitesse actuel de déplacement du NPC

            // Pour tous les équipements du NPC, on sauvegarde l'item et son équipement associé dans le fichier de configuration
            Arrays.stream(EquipmentSlot.values()).forEach(x-> config.set("slots." + x.name().toLowerCase(), npcGlobal.getSlots().get(x)));

            config.set("onFire", npcGlobal.isOnFire()); // Sauvegarde dans le fichier si le NPC est en feu ou non
            config.set("interact.cooldown", npcGlobal.getInteractCooldown()); // Sauvegarde dans le fichier le temps d'intéraction actuel avec le NPC
            config.set("interact.actions", null); // Définit par défaut dans le fichier les actions d'intéractions du NPC sur 'null'

            int clickActionID = 0; // Définit l'identifiant de l'action de cliqué à '0' par défaut

            // ⬇️ Pour toutes les actions de cliqué du NPC, on récupère chaque type d'action ainsi que son type de cliqué, et on sauvegarde le tous dans le fichier ⬇️ //
            for(Interact.ClickAction clickAction : npcGlobal.getClickActions()) {

                // Si l'action de cliqué du NPC est une action customisée, on continue
                if(clickAction.getActionType().equals(Interact.Actions.Type.CUSTOM_ACTION)) continue;
                clickActionID++; // On effectue une itération sur l'identifiant de l'action de cliqué

                // On sauvegarde le type d'action actuel associé à son identifiant d'action de cliqué itéré plus haut
                config.set("interact.actions." + clickActionID + ".type", clickAction.getActionType().name());

                // On sauvegarde le type de cliqué actuel associé à son identifiant d'action de cliqué itéré plus haut
                config.set("interact.actions." + clickActionID + ".click", clickAction.clickType.name());

                // Si l'action actuelle est un message, on sauvegarde dans le fichier de configuration l'action en question
                if(clickAction instanceof Interact.Actions.Message castAction) {

                    String[] messages = new String[castAction.getMessages().length]; // Récupère le message ou les messages envoyés lors de l'action

                    // pour tous les messages, on enregistre chaque message en convertissant les codes couleurs associés
                    for(int i = 0; i < castAction.getMessages().length; i++) messages[i] = castAction.getMessages()[i].replaceAll("§", "&");

                    // On sauvegarde le ou les messages en question
                    config.set("interact.actions." + clickActionID + ".messages", messages);

                // Sinon, si l'action actuelle est un titre, on sauvegarde dans le fichier de configuration l'action en question
                } else if(clickAction instanceof Interact.Actions.Title castAction) {

                    // Sauvegarde le titre principal qui est envoyé lors de l'action en convertissant les codes couleurs
                    config.set("interact.actions." + clickActionID + ".title", castAction.getTitle().replaceAll("§", "&"));

                    // Sauvegarde le sous-titre qui est envoyé lors de l'action en convertissant les codes couleurs
                    config.set("interact.actions." + clickActionID + ".subtitle", castAction.getSubtitle().replaceAll("§", "&"));

                    // Sauvegarde le temps d'apparition qui est envoyé lors de l'action
                    config.set("interact.actions." + clickActionID + ".fadeIn", castAction.getFadeIn());

                    // Sauvegarde le temps d'affichage qui est envoyé lors de l'action
                    config.set("interact.actions." + clickActionID + ".stay", castAction.getStay());

                    // Sauvegarde le temps de disparition qui est envoyé lors de l'action
                    config.set("interact.actions." + clickActionID + ".fadeOut", castAction.getFadeOut());

                // Sinon, si l'action actuelle est un message depuis la barre d'action, on sauvegarde dans le fichier de configuration l'action en question
                } else if(clickAction instanceof Interact.Actions.ActionBar castAction) config.set("interact.actions." + clickActionID + ".message", castAction.getMessage().replaceAll("§", "&"));

                // Sinon, si l'action actuelle est une connexion de serveur BungeeCord, on sauvegarde dans le fichier de configuration l'action en question
                else if(clickAction instanceof Interact.Actions.BungeeServer castAction) config.set("interact.actions." + clickActionID + ".server", castAction.getServer());

                // Sinon, si l'action actuelle est une commande effectuée par la console, on sauvegarde dans le fichier de configuration l'action en question
                else if(clickAction instanceof Interact.Actions.ConsoleCommand castAction) config.set("interact.actions." + clickActionID + ".command", castAction.getCommand());

                // Sinon, si l'action actuelle est une commande effectuée par le joueur, on sauvegarde dans le fichier de configuration l'action en question
                else if(clickAction instanceof Interact.Actions.PlayerCommand castAction) config.set("interact.actions." + clickActionID + ".command", castAction.getCommand());

                // Sinon, si l'action actuelle est une téléportation vers une localisation, on sauvegarde dans le fichier de configuration l'action en question
                else if(clickAction instanceof Interact.Actions.TeleportToLocation castAction) config.set("interact.actions." + clickActionID + ".location", castAction.getLocation());
            }
            // ⬆️ Pour toutes les actions de cliqué du NPC, on récupère chaque type d'action ainsi que son type de cliqué, et on sauvegarde le tous dans le fichier ⬆️ //

            // Si le fichier de configuration ne contient pas le fait de désactiver la sauvegarde de celui-ci, on la définit sur 'faux'
            if(!config.contains("disableSaving")) config.set("disableSaving", false);

            // ⬇️ On essaie de sauvegarder le fichier ⬇️ //
            try { config.save(file); }
            catch (Exception ignored){}
            // ⬆️ On essaie de sauvegarder le fichier ⬆️ //

            this.lastUpdate.save(); // On sauvegarde la dernière mise à jour du NPC

            // On envoie un message à la console disant que le NPC a été sauvegardé
            UtilityMain.getInstance().console.sendMessage(ChatColor.GRAY + "Le NPC NPCGlobal persistant " + ChatColor.GREEN + npcGlobal.getCode() + ChatColor.GRAY + " a été sauvegardé.");
        }

        /**
         * Vérifie si le fichier de configuration éxiste bien, sinon on le recrée.
         *
         */
        private void checkFileExists() {

            boolean exist = file.exists(); // Vérifie si le fichier de configuration éxiste

            // ⬇️ Si le fichier de configuration n'éxiste pas, essait de créer un nouveau fichier ⬇️ //
            if(!exist) {

                try { file.createNewFile(); }
                catch(Exception ignored){}
            }
            // ⬆️ Si le fichier de configuration n'éxiste pas, essait de créer un nouveau fichier ⬆️ //
        }

       /**
         * Supprime le fichier de configuration.
         *
         */
        public void remove() {

            config = null; // On définit le fichier de configuration sur 'null'

            if(file.exists()) file.delete(); // Si le fichier éxiste, on le supprime
            File folder = new File(getFolderPath()); // On récupère le répertoire du fichier
            if(folder.exists()) folder.delete(); // Si le dossier éxiste, on le supprime

            this.npcGlobal = null; // On définit le NPC Global du 'null'
        }

       /**
         * Définit si on désactive la sauvegarde de ce NPC ou non.
         *
         */
        public void setDisableSaving(boolean b) {

            checkFileExists(); // Vérifie l'éxistance du fichier de configuration

            if(config == null) config  = YamlConfiguration.loadConfiguration(file); // Si le fichier de configuration est null, on le recharge
            config.set("disableSaving", b); // On définit dans le fichier de configuration le fait de désactiver la sauvegarde de celui-ci sur le booléen en question

            // ⬇️ On essaie de sauvegarder le fichier ⬇️ //
            try { config.save(file); }
            catch(Exception ignored){}
            // ⬆️ On essaie de sauvegarder le fichier ⬆️ //
        }

        /**
         * Récupère le répertoire du fichier.
         *
         * @return Le répertoire du fichier
         */
        public String getFilePath() { return getFolderPath() + "/data.yml"; }

        /**
         * Récupère le répertoire du dossier en question.
         *
         * @return Le répertoire du dossier en question
         */
        public String getFolderPath() { return "plugins/EvhoUtility/PlayerNPC/persistent/NPCGlobal/" + plugin.getName().toLowerCase() + "/" + id; }

        /**
         * Récupère le {@link NPCGlobal NPC Global} actuel.
         *
         * @return Le {@link NPCGlobal NPC Global} actuel
         */
        public NPCGlobal getNPCGlobal() { return npcGlobal; }

        /**
         * Définit le {@link NPCGlobal NPC Global}.
         *
         * @param NPCGlobal Le {@link NPCGlobal NPC Global} en question
         *
         */
        protected void setNPCGlobal(NPCGlobal NPCGlobal) { this.npcGlobal = NPCGlobal; }

       /**
         * Vérifie si le fichier de configuration est bien rechargé.
         *
         * @return Une valeur Booléenne
         */
        public boolean isLoaded() { return config != null; }

        /**
         * Récupère le fichier de configurationa actuel
         *
         * @return Le fichier de configuration
         */
        public FileConfiguration getConfig() { return this.config; }

        /**
         * Récupère la {@link LastUpdate dernière mise à jour} persistante du NPC
         *
         * @return La {@link LastUpdate dernière mise à jour} persistante du NPC
         */
        public LastUpdate getLastUpdate() { return lastUpdate; }

                                         /* ---------------------------------------------------------------------- */

        /**
         * La dernière mise à jour persistante du NPC
         *
         *
         */
        public static class LastUpdate {

            private Type type; // Récupère le type de mise à jour
            private String time; // Récupère la date de la dernière mise à jour

            /**
             * Constructeur de la 'class' {@link LastUpdate}
             *
             */
            private LastUpdate() {}

            /**
             * Recharge la dernière mise à jour du NPC Persistant
             *
             */
            protected void load() { type = Type.LOAD; time(); }

            /**
             * Sauvegarde la dernière mise à jour du NPC Persistant
             *
             */
            protected void save() { type = Type.SAVE; time(); }

            /**
             * Change la date de la dernière mise à jour du NPC Persistant
             */
            private void time() { time = TimerUtils.getCurrentDate(); }

            /**
             * Récupère le type de la dernière mise à jour du NPC Persistant
             *
             * @return Le type de la dernière mise à jour du NPC Persistant
             */
            public Type getType() { return type; }

            /**
             * Récupère la date de la dernière mise à jour du NPC Persistant
             *
             * @return La date de la dernière mise à jour du NPC Persistant
             */
            public String getTime() { return time; }

            /************************************************************/
            /* ÉNUMÉRATIONS UTILE POUR LE TYPE DE DERNIÈRE MISE À JOUR */
            /**********************************************************/

            public enum Type { SAVE, LOAD; }

            /************************************************************/
            /* ÉNUMÉRATIONS UTILE POUR LE TYPE DE DERNIÈRE MISE À JOUR */
            /**********************************************************/
        }
    }
}