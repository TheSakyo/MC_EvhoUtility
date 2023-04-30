package fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit.*;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms.NMSEntity;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms.NMSEntityPlayer;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.TimerUtils;
import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.*;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.ArmorStand;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Slab;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Instance de {@link NPC} par joueur. Un {@link NPC} ne peut être vu que par un seul joueur. Ceci a des fins de personnalisation.<br/><br/>
 *
 * <p>
 * Avec cette instance, vous pouvez créer des NPCs de joueur personnalisables avec lesquels vous pouvez interagir.
 * Les NPCs ne seront visibles pour les joueurs qu'après avoir créé l'EntityPlayer, et l'avoir montré au joueur.
 * </p>
 *
 * @author SergiFerry, TheSakyo
 * @since 2021.1
 */
public abstract class NPC {

    protected final NPCUtils.PluginManager pluginManager; // La gestion du plugin de la librairie NPC
    private final String code; // Le code d'identification du NPC
    private final HashMap<String, String> customData; // Les données customisées du NPC

    private World world; // Le monde du NPC
    private Double x, y, z; // Les coordonnées 'x', 'y' et 'z' du NPC
    private Float yaw, pitch; // Les rotations 'yaw' et 'pitch' du NPC
    private Float defaultYAW, defaultPITCH; // Les rotations par défaut 'yaw' et 'pitch' du NPC

    private List<NPC.Interact.ClickAction> clickActions; // Les actions au cliqué du NPC

    private NPC.Move.Task moveTask; // La tâche de mouvement du NPC
    private final NPC.Move.Behaviour moveBehaviour; // Le Comportement de suivie du NPC
    private final NPC.Attributes attributes; // Les Attributs du NPC

    /* ------------------------------------------------------------------------------------------------------------------------------------ */
    /* ------------------------------------------------------------------------------------------------------------------------------------ */

    /**
     * Instanciation d'un nouveau NPC
     *
     * @param npcUtils La {@link NPCUtils librairie NPC}
     * @param plugin Le plugin en question
     * @param code   Un code d'identification
     * @param world  Le monde
     * @param x      La coordonnée x
     * @param y      La coordonnée y
     * @param z      La coordonnée z
     * @param yaw    La rotation 'yaw'
     * @param pitch  La rotation 'pitch'
     */
    protected NPC(@Nonnull NPCUtils npcUtils, @Nonnull Plugin plugin, @Nonnull String code, @Nonnull World world, double x, double y, double z, float yaw, float pitch) {

        Validate.notNull(npcUtils, "Impossible de générer une instance de NPC, NPCUtils ne peut pas être nulle."); // Une erreur s'affiche, si la librairie NPC est null.
        Validate.notNull(plugin, "Impossible de générer une instance de NPC, Le plugin ne peut pas être nulle."); // Une erreur s'affiche, si le plugin récupéré est null.
        Validate.notNull(code, "Impossible de générer une instance de NPC, Le code ne peut pas être nulle."); // Une erreur s'affiche, si le Code d'Identification est null.
        Validate.notNull(world, "Impossible de générer une instance de NPC, Le monde ne peut pas être nulle."); // Une erreur s'affiche, si le Monde du NPC est null.

        this.pluginManager = npcUtils.getPluginManager(plugin); // Initialise la gestion du plugin de la librairie NPC
        this.code = code; // Initialise le Code d'Identification du NPC

        this.world = world; // Initialise le Monde du NPC
        this.x = x; // Initialise la Coordonnée 'x' du NPC
        this.y = y; // Initialise la Coordonnée 'y' du NPC
        this.z = z; // Initialise la Coordonnée 'z' du NPC
        this.yaw = yaw; // Initialise la Rotation 'yaw' du NPC
        this.defaultYAW = this.yaw; // Initialise la Rotation 'yaw' par défaut du NPC
        this.pitch = pitch; // Initialise la Rotation 'pitch' du NPC
        this.defaultPITCH = this.pitch; // Initialise la Rotation 'yaw' par défaut du NPC

        this.clickActions = new ArrayList<>(); // Initialise Les Actions au Cliqués du NPC
        this.moveTask = null; // Initialise La Tâche de Mouvement du NPC
        this.moveBehaviour = new NPC.Move.Behaviour(this); // Initialise Le Comportement de Suivie du NPC
        this.customData = new HashMap<>(); // Initialise toutes les Données Customisées du NPC

        this.attributes = new NPC.Attributes(); // Initialise tous les attributs du NPC
    }


    /**
     * Récupère le plugin associé avec le NPC
     */
    public Plugin getPlugin() { return pluginManager.getPlugin(); }

    /**
     * Récupère la librairie NPC
     */
    public NPCUtils getNPCUtils() { return pluginManager.getNPCUtils(); }

    /**
     * Récupère la gestion du plugin de la librairie NPC
     */
    public NPCUtils.PluginManager getPluginManager() { return pluginManager; }

    /**
     * Récupère les différents attributs du NPC
     */
    public NPC.Attributes getAttributes() { return attributes; }

    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */

    /**
     * Met à jour le NPC
     */
    protected abstract void update();

    /**
     * Force la mise à jour du NPC
     */
    protected abstract void forceUpdate();

    /**
     * Met à jour le texte du NPC
     */
    protected abstract void updateText();

    /**
     * Force la mise à jour du NPC
     */
    protected abstract void forceUpdateText();

    /**
     * Détruit (supprime) le NPC
     */
    protected abstract void destroy();

    /**
     * Téléporte le NPC vers un endroit précis.
     *
     * @param world Le monde dans lequel téléporté le NPC
     * @param x La coordonnée 'x' dans laquelle téléporté le NPC
     * @param y La coordonnée 'y' dans laquelle téléporté le NPC
     * @param z La coordonnée 'z' dans laquelle téléporté le NPC
     *
     * @param yaw La rotation 'yaw' dans laquelle sera téléporté le NPC
     * @param pitch La rotation 'pitch' dans laquelle sera téléporté le NPC
     *
     */
    protected abstract void teleport(World world, double x, double y, double z, float yaw, float pitch);

    /**
     * Définit un Nom Customisé pour le NPC.
     *
     * @param name Le Nom Customisé en question
     *
     */
    public abstract void setCustomName(@Nullable String name);


    /**
     * Définit un Nom Customisé pour le NPC dans le 'Tablist'.
     *
     * @param name Le Nom Customisé en question à afficher dans le 'Tablist'
     *
     */
    public abstract void setCustomTabListName(@Nullable String name);

    /**
     * Le NPC va se déplacer aux coordonnées précisées pour tous les Joueurs actifs associés.
     *
     * @param moveX La Coordonnée 'X' où déplacer le NPC
     * @param moveY La Coordonnée 'Y' où déplacer le NPC
     * @param moveZ La Coordonnée 'Z' où déplacer le NPC
     *
     */
    protected abstract void move(double moveX, double moveY, double moveZ);

    /**
     * Met à jour la rotation du NPC.
     *
     */
    protected abstract void updatePlayerRotation();

    /**
     * Met à jour la {@link Location localisation} du NPC.
     *
     */
    protected abstract void updateLocation();

    /**
     * Met à jour le déplacement du NPC.
     *
     */
    protected abstract void updateMove();

    /**
     * Vide les tâches de mouvement du NPC.
     *
     */
    protected void clearMoveTask() { this.moveTask = null; }

    /**
     * Envoie un dégât au NPC. (cela n'affecte rien sur le NPC)
     *
     */
    public abstract void hit();

    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */

    /**
     * Téléporte le NPC vers une autre {@link Entity Entité}.
     *
     * @param entity L'{@link Entity Entité} dans laquelle téléporté le NPC
     */
    public void teleport(@Nonnull Entity entity) {

        Validate.notNull(entity, "L'Entité ne doit pas être null.");
        teleport(entity.getLocation());
    }

    /**
     * Téléporte le NPC vers une autre {@link Location localisation}.
     *
     * @param location La {@link Location localisation} dans laquelle téléporté le NPC
     */
    public void teleport(@Nonnull Location location) {

        Validate.notNull(location, "Le lieu doit être différent de zéro.");
        teleport(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Téléporte le NPC vers des coordonnées précis.
     *
     * @param x La coordonnée 'x' dans laquelle téléporté le NPC
     * @param y La coordonnée 'y' dans laquelle téléporté le NPC
     * @param z La coordonnée 'z' dans laquelle téléporté le NPC
     *
     */
    public void teleport(double x, double y, double z) { teleport(world, x, y, z); }

    /**
     * Téléporte le NPC vers des coordonnées précis dans un monde précis.
     *
     * @param world Le monde dans lequel téléporté le NPC
     * @param x La coordonnée 'x' dans laquelle téléporté le NPC
     * @param y La coordonnée 'y' dans laquelle téléporté le NPC
     * @param z La coordonnée 'z' dans laquelle téléporté le NPC
     *
     */
    public void teleport(World world, double x, double y, double z) { teleport(world, x, y, z, yaw, pitch); }

    /**
     * Téléporte le NPC vers des coordonnées précis avec une rotation précise.
     *
     * @param x La coordonnée 'x' dans laquelle téléporté le NPC
     * @param y La coordonnée 'y' dans laquelle téléporté le NPC
     * @param z La coordonnée 'z' dans laquelle téléporté le NPC
     *
     * @param yaw La rotation 'yaw' dans laquelle sera téléporté le NPC
     * @param pitch La rotation 'pitch' dans laquelle sera téléporté le NPC
     *
     */
    public void teleport(double x, double y, double z, float yaw, float pitch) {

        this.defaultYAW = yaw;
        this.defaultPITCH = pitch;

        teleport(this.world, x, y, z, yaw, pitch);
    }

    /* ------------------------------------------------------- */

    /**
     * Ajoute un {@link ItemStack Item} à un {@link EquipmentSlot emplacement} précis du NPC.
     *
     * @param slot Un {@link EquipmentSlot emplacement} du NPC
     * @param itemStack L'{@link ItemStack item} a ajouté
     *
     */
    public void setItem(@Nonnull EquipmentSlot slot, @Nullable ItemStack itemStack) {

        Validate.notNull(slot, "Échec de la mise en place de l'élément, EquipmentSlot ne peut pas être nul.");
        if(itemStack == null) itemStack = new ItemStack(Material.AIR);
        attributes.slots.put(slot, itemStack);
    }


    /**
     * Ajoute un {@link ItemStack Item} comme casque pour le NPC.
     *
     * @param itemStack L'{@link ItemStack item} a ajouté
     *
     */
    public void setHelmet(@Nullable ItemStack itemStack) { setItem(EquipmentSlot.HEAD, itemStack); }

    /**
     * Ajoute un {@link ItemStack Item} comme plastron pour le NPC.
     *
     * @param itemStack L'{@link ItemStack item} a ajouté
     *
     */
    public void setChestPlate(@Nullable ItemStack itemStack) { setItem(EquipmentSlot.CHEST, itemStack); }

    /**
     * Ajoute un {@link ItemStack Item} comme jambière pour le NPC.
     *
     * @param itemStack L'{@link ItemStack item} a ajouté
     *
     */
    public void setLeggings(@Nullable ItemStack itemStack) { setItem(EquipmentSlot.LEGS, itemStack); }

    /**
     * Ajoute un {@link ItemStack Item} comme botte pour le NPC.
     *
     * @param itemStack L'{@link ItemStack item} a ajouté
     *
     */
    public void setBoots(@Nullable ItemStack itemStack) { setItem(EquipmentSlot.FEET, itemStack); }

    /**
     * Ajoute un {@link ItemStack Item} dans la main droite du NPC.
     *
     * @param itemStack L'{@link ItemStack item} a ajouté
     *
     */
    public void setItemInRightHand(@Nullable ItemStack itemStack) { setItem(EquipmentSlot.MAINHAND, itemStack); }

    /**
     * Ajoute un {@link ItemStack Item} dans la main gauche du NPC.
     *
     * @param itemStack L'{@link ItemStack item} a ajouté
     *
     */
    public void setItemInLeftHand(@Nullable ItemStack itemStack) { setItem(EquipmentSlot.OFFHAND, itemStack); }

    /**
     * Supprime un {@link ItemStack Item} dans un {@link EquipmentSlot emplacement} précis du NPC.
     *
     @param slot Un {@link EquipmentSlot emplacement} du NPC
     *
     */
    public void clearEquipment(@Nonnull EquipmentSlot slot) { setItem(slot, null); }


    /**
     * Supprime tous les {@link ItemStack Items} dans les {@link EquipmentSlot emplacements} possible du NPC.
     *
     */
    public void clearEquipment() { Arrays.stream(EquipmentSlot.values()).forEach(this::clearEquipment); }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Ajoute des {@link ItemStack Items} dans les {@link EquipmentSlot emplacements} possible du NPC.
     *
     */
    protected void setSlots(HashMap<EquipmentSlot, ItemStack> slots) { attributes.setSlots(slots); }

    /**
     * Récupère les {@link ItemStack Items} dans les {@link EquipmentSlot emplacements} possible du NPC.
     *
     */
    protected HashMap<EquipmentSlot, ItemStack> getSlots() { return attributes.slots; }

    /* ------------------------------------------------------- */

    /**
     * Le NPC va regarder vers l'{@link Entity Entité} demandé
     *
     @param entity L'{@link Entity Entité} auquel le NPC va regarder
     *
     */
    public void lookAt(@Nonnull Entity entity) { lookAt(entity.getLocation(), false); }

    /**
     * Le NPC va regarder vers une {@link Location localisation} demandé.
     *
     * @param location La {@link Location localisation} dans laquelle le NPC va regarder
     *
     */
    public void lookAt(@Nonnull Location location) { lookAt(location, false); }

    /**
     * Le NPC va regarder vers l'{@link Entity Entité} demandé
     *
     * @param entity L'{@link Entity Entité} auquel le NPC va regarder
     * @param forceLook Doit-on forçer la rotation par défaut du NPC ?
     *
     */
    public void lookAt(@Nonnull Entity entity, boolean forceLook) {

        Validate.notNull(entity, "Échec de la définition de la direction de recherche. L'Entité ne peut pas être nulle.");
        lookAt(entity.getLocation(), forceLook);
    }

    /**
     * Le NPC va regarder vers une {@link Location localisation} demandé.
     *
     * @param location La {@link Location localisation} dans laquelle le NPC va regarder
     * @param forceLook Doit-on forçer la rotation par défaut du NPC ?
     *
     */
    public void lookAt(@Nonnull Location location, boolean forceLook) {

        Validate.notNull(location, "Échec de la définition de la direction de recherche. L'Emplacement ne peut pas être nul.");
        Validate.isTrue(location.getWorld().getName().equals(getWorld().getName()), "L'emplacement doit être dans le même monde que le NPC.");

        Location npcLocation = new Location(world, x, y, z, yaw, pitch);
        Vector dirBetweenLocations = location.toVector().subtract(npcLocation.toVector());

        npcLocation.setDirection(dirBetweenLocations);
        lookAt(npcLocation.getYaw(), npcLocation.getPitch(), forceLook);
    }

    /**
     * Le NPC va regarder à un endroit en fonction de la rotation 'yaw' et 'pitch' demandé.
     *
     * @param yaw La rotation 'yaw' dans laquelle le NPC va se tourner
     * @param pitch La rotation 'pitch' dans laquelle le NPC va se tourner
     * @param forceLook Doit-on forçer la rotation par défaut du NPC ?
     *
     */
    public abstract void lookAt(float yaw, float pitch, boolean forceLook);

    /* ------------------------------------------------------- */

    /**
     * Change le skin du NPC en utilisant la 'class' {@link NPC.Skin}.
     *
     * @param npcSkin Le nouveau {@link NPC.Skin Skin} du NPC.
     *
     */
    public void setSkin(@Nullable NPC.Skin npcSkin) { attributes.setSkin(npcSkin); }

    /**
     * Change le skin du NPC en utilisant un Pseudonyme Minecraft.
     *
     * @param playerName Le Pseudo du Skin qu'il faut récupérer.
     *
     */
    public void setSkin(@Nullable String playerName) { setSkin(playerName, (Consumer<Skin>) null); }

    /**
     * Change le skin du NPC en utilisant la texture et la signature du Skin en question.
     *
     * @param texture La Texture du Skin
     * @param signature La Signature du Skin
     *
     */
    public void setSkin(@Nonnull String texture, @Nonnull String signature) { setSkin(new NPC.Skin(texture, signature)); }

    /**
     * Change le skin du NPC en utilisant un Pseudonyme Minecraft et en effectuant une action si le skin a bien été changé.
     *
     * @param playerName Le Pseudo du Skin qui doit être récupéré.
     * @param finishAction L'Action a effectuer.
     *
     */
    public void setSkin(@Nullable String playerName, Consumer<Skin> finishAction) {

        if(playerName == null) { setSkin(Skin.STEVE); return; }

        /*************************************/

        NPC.Skin.fetchSkinAsync(this.getPlugin(), playerName, (skin) -> {

            setSkin(skin);
            if(finishAction != null) getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> finishAction.accept(skin));
        });
    }

    /**
     * Change le skin du NPC en précisant le Joueur en ligne.
     *
     * @param playerSkin Le Joueur en question.
     *
     */
    public void setSkin(@Nullable Player playerSkin) {

        if(playerSkin == null) { setSkin(Skin.STEVE); return; }

        Validate.isTrue(playerSkin.isOnline(), "Échec de la mise en place du skin du NPC. Le Joueur doit être en ligne.");
        setSkin(playerSkin.getName(), (Consumer<Skin>) null);
    }

    /**
     * Change le skin du NPC en précisant le Joueur en ligne et en effectuant une action si le skin a bien été changé.
     *
     * @param playerSkin Le Joueur en question.
     * @param finishAction L'Action a effectuer.
     *
     */
    public void setSkin(@Nullable Player playerSkin, Consumer<Skin> finishAction) {

        if(playerSkin == null) { setSkin(Skin.STEVE); return; }

        Validate.isTrue(playerSkin.isOnline(), "Échec de la mise en place du skin du NPC. Le Joueur doit être en ligne.");
        setSkin(playerSkin.getName(), finishAction);
    }

    /**
     * Récupère le Skin du NPC avec {@link NPC.Skin.Parts}.
     *
     */
    public NPC.Skin getSkin() { return attributes.skin; }

    /**
     * Supprime le Skin du NPC avec {@link NPC.Skin.Parts}.
     *
     */
    public void clearSkin() { setSkin((NPC.Skin) null); }

    /**
     * Définit les parties du Skin du NPC avec {@link NPC.Skin.Parts}.
     *
     * @param skinParts Les Parties du Skin à définir
     *
     */
    protected void setSkinParts(NPC.Skin.Parts skinParts) { attributes.setSkinParts(skinParts); }

    /**
     * Récupère les parties du Skin du NPC ({@link NPC.Skin.Parts}).
     *
     */
    public NPC.Skin.Parts getSkinParts() { return attributes.skinParts; }

    /**
     * Définit la visibilité d'une partie du Skin du NPC avec {@link NPC.Skin.Part}.
     *
     * @param part La Partie du Skin à définir
     * @param visible Doit-ont les voir ?
     *
     */
    public void setSkinVisiblePart(NPC.Skin.Part part, boolean visible) { attributes.skinParts.setVisible(part, visible); }

    /* ------------------------------------------------------- */

    /**
     * Définit la {@link Pose posture} du NPC.
     *
     * @param pose La {@link Pose posture} à définir
     */
    public void setPose(Pose pose) { attributes.setPose(pose); }

    /**
     * Récupère la {@link Pose posture} du NPC.
     *
     */
    public Pose getPose() { return attributes.pose; }

    /**
     * Réinitialise la {@link Pose posture} du NPC.
     *
     */
    public void resetPose() { setPose(Pose.STANDING); }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Définit la {@link Pose posture} du NPC étant accroupi.
     *
     * @param b Le NPC doit-il être accroupi ?
     */
    public void setCrouching(boolean b) {

        if(b) setPose(Pose.CROUCHING);
        else if(getPose().equals(Pose.CROUCHING)) resetPose();
    }

    /**
     * Définit la {@link Pose posture} du NPC étant en nage.
     *
     * @param b Le NPC doit-il nager ?
     */
    public void setSwimming(boolean b) {

        if(b) setPose(Pose.SWIMMING);
        else if(getPose().equals(Pose.SWIMMING)) resetPose();
    }

    /**
     * Définit la {@link Pose posture} du NPC étant en train de dormir.
     *
     * @param b Le NPC doit-il dormir ?
     */
    public void setSleeping(boolean b) {

        if(b) setPose(Pose.SLEEPING);
        else if(getPose().equals(Pose.SLEEPING)) resetPose();
    }

    /* ------------------------------------------------------- */

    /**
     * Supprime le {@link String texte} du NPC.
     *
     */
    public void clearText() { setText(new ArrayList<>()); }

    /**
     * Ajoute une liste de {@link String texte} au NPC.
     *
     * @param text La liste de {@link String texte} a affiché
     *
     */
    public void setText(@Nonnull List<String> text) { attributes.setText(text); }

    /**
     * Ajoute plusieurs {@link String textes} au NPC.
     *
     * @param text Les {@link String textes} a affiché
     *
     */
    public void setText(@Nonnull String... text) { setText(Arrays.asList(text)); }

    /**
     * Ajoute un {@link String texte} au NPC.
     *
     * @param text Les {@link String textes} a affiché
     *
     */
    public void setText(@Nonnull String text) { setText(List.of(text)); }


    /**
     * Récupère le texte du NPC.
     *
     */
    public List<String> getText() { return attributes.text; }


    /* ------------------------------------------------------- */

    /**
     * Réinitialise l'opacité de toutes les lignes affichant le texte du NPC.
     *
     */
    public void resetLinesOpacity() { attributes.resetLinesOpacity(); }

    /**
     * Réinitialise l'opacité de la ligne précisée affichant le texte du NPC.
     *
     * @param line La ligne en question
     *
     */
    public void resetLineOpacity(int line) { attributes.resetLineOpacity(line); }

    /**
     * Récupère l'opacité de toutes les lignes affichant le texte du NPC.
     *
     */
    protected HashMap<Integer, NPC.Hologram.Opacity> getLinesOpacity() { return attributes.linesOpacity; }

    /**
     * Récupère l'opacité de la ligne précisée affichant le texte du NPC.
     *
     * @param line La ligne en question
     *
     */
    public NPC.Hologram.Opacity getLineOpacity(int line) { return attributes.getLineOpacity(line); }

    /**
     * Définit une opacité de la ligne précisée affichant le texte du NPC.
     *
     * @param line La ligne en question
     * @param textOpacity L'Opacité à mettre
     *
     */
    public void setLineOpacity(int line, @Nullable NPC.Hologram.Opacity textOpacity) { attributes.setLineOpacity(line, textOpacity); }

    /**
     * Définit une opacité de toutes les lignes affichant le texte du NPC.
     *
     * @param linesOpacity L'Opacité à mettre
     *
     */
    protected void setLinesOpacity(HashMap<Integer, NPC.Hologram.Opacity> linesOpacity) { attributes.setLinesOpacity(linesOpacity); }

    /**
     * Récupère l'opacité du texte du NPC.
     *
     */
    public NPC.Hologram.Opacity getTextOpacity() { return attributes.textOpacity; }

    /**
     * Définit une opacité pour le texte du NPC.
     *
     * @param textOpacity L'Opacité à mettre
     *
     */
    public void setTextOpacity(@Nullable NPC.Hologram.Opacity textOpacity) { attributes.setTextOpacity(textOpacity); }

    /**
     * Réinitialise l'opacité pour le texte du NPC.
     *
     */
    public void resetTextOpacity() { setTextOpacity(NPC.Hologram.Opacity.LOWEST); }

    /* ------------------------------------------------------- */

    /**
     * Définit un {@link Vector vecteur} pour l'alignement du texte du NPC.
     *
     * @param vector Le {@link Vector vecteur} en question.
     *
     */
    public void setTextAlignment(@Nonnull Vector vector) { attributes.setTextAlignment(vector); }

    /**
     * Récupère le {@link Vector vecteur} pour l'alignement du texte du NPC.
     *
     */
    public Vector getTextAlignment() { return attributes.textAlignment; }

    /**
     * Réinitialise le {@link Vector vecteur} pour l'alignement du texte du NPC.
     *
     */
    public void resetTextAlignment() { setTextAlignment(null); }

    /* ------------------------------------------------------- */

    /**
     * Définit une couleur {@link ChatFormatting} de surbrillance pour le NPC.
     *
     * @param color La {@link ChatFormatting couleur} qui doit briller
     *
     */
    public void setGlowingColor(@Nullable ChatFormatting color) { attributes.setGlowingColor(color); }


    /**
     * Récupère la couleur {@link ChatFormatting} de surbrillance du NPC.
     *
     */
    public ChatFormatting getGlowingColor() { return attributes.glowingColor; }

    /**
     * Définit une couleur {@link ChatFormatting} de surbrillance pour le NPC et définit si on doit faire sur briller le NPC ou pas.
     *
     * @param glowing Le NPC doit sur briller ?
     * @param color La {@link ChatFormatting couleur} qui doit briller.
     *
     */
    public void setGlowing(boolean glowing, @Nullable ChatFormatting color) {

        setGlowing(glowing);
        setGlowingColor(color);
    }

    /**
     * Définit si oui ou non, on doit afficher le NPC en surbrillance.
     *
     * @param glowing Doit-ont affiché faire surbriller le NPC ?
     *
     */
    public void setGlowing(boolean glowing) { attributes.setGlowing(glowing); }

    /**
     * Vérifie le NPC est en surbrillance.
     *
     */
    public boolean isGlowing() { return attributes.glowing; }

    /* ------------------------------------------------------- */

    /**
     * Le NPC va suivre une {@link Entity Entité} avec une coordonnée minimale et une coordonnée maximale.
     *
     * @param entity L'{@link Entity Entité} à suivre
     * @param min La coordonnée minimale auquel le NPC peut suivre
     * @param max La coordonnée maximale auquel le NPC peut suivre
     */
    public Move.Behaviour follow(Entity entity, double min, double max) { return moveBehaviour.setFollowEntity(entity, min, max); }

    /**
     * Le NPC va suivre une {@link Entity Entité} avec une coordonnée minimale.
     *
     * @param entity L'{@link Entity Entité} à suivre
     * @param min La coordonnée minimale auquel le NPC peut suivre
     */
    public Move.Behaviour follow(Entity entity, double min) { return moveBehaviour.setFollowEntity(entity, min); }

    /**
     * Le NPC va suivre une {@link Entity Entité}.
     *
     * @param entity L'{@link Entity Entité} à suivre
     */
    public Move.Behaviour follow(Entity entity) { return moveBehaviour.setFollowEntity(entity); }

    /**
     * Le NPC va suivre un autre {@link NPC}.
     *
     * @param npc Le {@link NPC} à suivre
     */
    public Move.Behaviour follow(NPC npc) {

        Validate.isTrue(!npc.equals(this), "Le NPC ne peut pas se suivre lui-même.");
        return moveBehaviour.setFollowNPC(npc);
    }

    /**
     * Le NPC va suivre un autre {@link NPC} avec une coordonnée minimale et une coordonnée maximale.
     *
     * @param npc Le {@link NPC} à suivre
     * @param min La coordonnée minimale auquel le NPC peut suivre
     * @param max La coordonnée maximale auquel le NPC peut suivre
     */
    public Move.Behaviour follow(NPC npc, double min, double max) {

        Validate.isTrue(!npc.equals(this), "Le NPC ne peut pas se suivre lui-même.");
        return moveBehaviour.setFollowNPC(npc, min, max);
    }

    /**
     * Récupère le déplacement de suivi actuel du NPC.
     *
     */
    protected Move.Behaviour getMoveBehaviour() { return moveBehaviour; }

    /**
     * Récupère le type de déplacement de suivi actuel du NPC.
     *
     */
    public Move.Behaviour.Type getMoveBehaviourType() { return moveBehaviour.getType(); }

    /**
     * Annule le mouvement de suivi du NPC.
     *
     */
    public void cancelMoveBehaviour() { moveBehaviour.cancel(); }

    /* ------------------------------------------------------- */

    /**
     * Déplace un NPC dans une liste de {@link Location lieux} avec un type de mouvement (normal, répétitif ou aller-retour).
     *
     * @param type Le type de mouvement (normal, répétitif ou aller-retour)
     * @param locations Les {@link Location lieux} où le NPC va se déplacer
     *
     */
    public Move.Path setPath(Move.Path.Type type, List<Location> locations) { return getMoveBehaviour().setPath(locations, type).start(); }

    /**
     * Déplace un NPC dans plusieurs {@link Location lieux} avec un type de mouvement (normal, répétitif ou aller-retour).
     *
     * @param type Le type de mouvement (normal, répétitif ou aller-retour)
     * @param locations Les {@link Location lieux} où le NPC va se déplacer
     *
     */
    public Move.Path setPath(Move.Path.Type type, Location... locations) { return setPath(type, Arrays.stream(locations).toList()); }

    /**
     * Déplace un NPC dans une liste de {@link Location lieux} avec un type de mouvement répétitif.
     *
     * @param locations Les {@link Location lieux} où le NPC va se déplacer
     *
     */
    public Move.Path setRepetitivePath(List<Location> locations) { return setPath(Move.Path.Type.REPETITIVE, locations); }

    /**
     * Déplace un NPC dans une plusieurs {@link Location lieux} avec un type de mouvement répétitif.
     *
     * @param locations Les {@link Location lieux} où le NPC va se déplacer
     *
     */
    public Move.Path setRepetitivePath(Location... locations) { return setRepetitivePath(Arrays.stream(locations).toList()); }

    /* ------------------------------------------------------- */

    /**
     * Définit le {@link GazeTrackingType type de suivie du regard} du NPC.
     *
     * @param followLookType Le {@link GazeTrackingType type de suivie du regard} en question
     *
     */
    public void setGazeTrackingType(@Nullable GazeTrackingType followLookType) { attributes.setGazeTrackingType(followLookType); }

    /**
     * Récupère le {@link GazeTrackingType type de suivie du regard} du NPC.
     *
     */
    public GazeTrackingType getGazeTrackingType() { return attributes.gazeTrackingType; }

    /* ------------------------------------------------------- */

    /**
     * Définit la distance dans laquelle le NPC disparaîtra de la vue du Joueur.
     *
     * @param hideDistance La distance en question
     *
     */
    public void setHideDistance(double hideDistance) { attributes.setHideDistance(hideDistance); }

    /**
     * Récupère la distance dans laquelle le NPC disparaîtra de la vue du Joueur.
     *
     */
    public Double getHideDistance() { return attributes.hideDistance; }

    /* ------------------------------------------------------- */

    /**
     * Définit la distance dans laquelle le NPC peut être vue.
     *
     * @param lineSpacing La distance en question
     *
     */
    public void setLineSpacing(double lineSpacing) { attributes.setLineSpacing(lineSpacing); }

    /**
     * Récupère la distance dans laquelle le NPC peut être vue.
     *
     */
    public Double getLineSpacing() { return attributes.lineSpacing; }

    /**
     * Réinitialise la distance dans laquelle le NPC peut être vue.
     *
     */
    public void resetLineSpacing() { setLineSpacing(NPC.Attributes.getDefault().getLineSpacing()); }

    /* ------------------------------------------------------- */

    /**
     * Définit le temps d'Intéraction (en milliseconde) avec le NPC.
     *
     * @param milliseconds Le temps en question
     *
     */
    public void setInteractCooldown(long milliseconds) { attributes.setInteractCooldown(milliseconds); }

    /**
     * Récupère le temps d'Intéraction avec le NPC.
     *
     */
    public Long getInteractCooldown() { return attributes.interactCooldown; }

    /**
     * Réinitialise le temps d'Intéraction avec le NPC.
     *
     */
    public void resetInteractCooldown() { setInteractCooldown(NPC.Attributes.getDefaultInteractCooldown()); }

    /* ------------------------------------------------------- */

    /**
     * Ajoute une action customisée lorsqu'on clique sur le NPC.
     *
     * @param clickType Le type de clique (Gauche ou Droit)
     * @param customAction L'Action a effectuer.
     *
     */
    public Interact.Actions.Custom addCustomClickAction(@Nullable NPC.Interact.ClickType clickType, @Nonnull BiConsumer<NPC, Player> customAction) { return (Interact.Actions.Custom) addClickAction(new Interact.Actions.Custom(this, clickType,customAction)); }

    /**
     * Ajoute une action customisée lorsqu'on clique sur le NPC.
     *
     * @param customAction L'Action a effectuer.
     *
     */
    public Interact.Actions.Custom addCustomClickAction(@Nonnull BiConsumer<NPC, Player> customAction) { return addCustomClickAction(Interact.ClickType.EITHER, customAction); }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Envoie un message lorsqu'on clique sur le NPC.
     *
     * @param clickType Le type de clique (Gauche ou Droit)
     * @param message   Le Message à envoyer
     */
    public void addMessageClickAction(@Nullable Interact.ClickType clickType, @Nonnull String... message) { addClickAction(new Interact.Actions.Message(this, clickType, message)); }

    /**
     * Envoie un message lorsqu'on clique sur le NPC.
     *
     * @param message Le Message à envoyer
     */
    public void addMessageClickAction(@Nonnull String... message) { addMessageClickAction(Interact.ClickType.EITHER, message); }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Envoie une commande lorsqu'on clique sur le NPC.
     *
     * @param clickType Le type de clique (Gauche ou Droit)
     * @param command La commande qu'il faut effectuer.
     *
     */
    public Interact.Actions.PlayerCommand addRunPlayerCommandClickAction(@Nullable NPC.Interact.ClickType clickType, @Nonnull String command) { return (Interact.Actions.PlayerCommand) addClickAction(new Interact.Actions.PlayerCommand(this, clickType, command)); }

    /**
     * Envoie une commande lorsqu'on clique sur le NPC.
     *
     * @param command La commande qu'il faut effectuer.
     *
     */
    public Interact.Actions.PlayerCommand addRunPlayerCommandClickAction(@Nonnull String command) { return addRunPlayerCommandClickAction(Interact.ClickType.EITHER, command); }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Envoie une commande à partir de la console lorsqu'on clique sur le NPC.
     *
     * @param clickType Le type de clique (Gauche ou Droit)
     * @param command La commande a effectué à partir de la console
     *
     */
    public Interact.Actions.ConsoleCommand addRunConsoleCommandClickAction(@Nullable NPC.Interact.ClickType clickType, @Nonnull String command) { return (Interact.Actions.ConsoleCommand) addClickAction(new Interact.Actions.ConsoleCommand(this, clickType, command)); }

    /**
     * Envoie une commande à partir de la console lorsqu'on clique sur le NPC.
     *
     * @param command La commande a effectué à partir de la console
     *
     */
    public Interact.Actions.ConsoleCommand addRunConsoleCommandClickAction(@Nonnull String command) { return addRunConsoleCommandClickAction(Interact.ClickType.EITHER, command); }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Connecte le joueur à un serveur précis lorsqu'on clique sur le NPC.
     *
     * @param clickType Le type de clique (Gauche ou Droit)
     * @param server Le nom du Serveur en question
     *
     */
    public Interact.Actions.BungeeServer addConnectBungeeServerClickAction(@Nullable NPC.Interact.ClickType clickType, @Nonnull String server) { return (Interact.Actions.BungeeServer) addClickAction(new Interact.Actions.BungeeServer(this, clickType, server)); }

    /**
     * Connecte le joueur à un serveur précis lorsqu'on clique sur le NPC.
     *
     * @param server Le nom du Serveur en question
     *
     */
    public Interact.Actions.BungeeServer addConnectBungeeServerClickAction(@Nonnull String server) { return addConnectBungeeServerClickAction(Interact.ClickType.EITHER, server); }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Envoie un message au niveau de la barre d'action du Joueur lorsqu'on clique sur le NPC.
     *
     * @param clickType Le type de clique (Gauche ou Droit)
     * @param message Le Message à envoyer
     *
     */
    public Interact.Actions.ActionBar addActionBarMessageClickAction(@Nullable NPC.Interact.ClickType clickType, @Nonnull String message) { return (Interact.Actions.ActionBar) addClickAction(new Interact.Actions.ActionBar(this, clickType, message)); }

    /**
     * Envoie un message au niveau de la barre d'action du Joueur lorsqu'on clique sur le NPC.
     *
     * @param message Le Message à envoyer
     *
     */
    public Interact.Actions.ActionBar addActionBarMessageClickAction(@Nonnull String message) { return addActionBarMessageClickAction(Interact.ClickType.EITHER, message); }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Envoie un Message de type 'Titre' au Joueur lorsqu'on clique sur le NPC.
     *
     * @param clickType Le type de clique (Gauche ou Droit)
     * @param title Le Titre principal à envoyé
     * @param subtitle Le Sous-Titre à envoyer
     * @param fadeIn Le Temps d'apparition
     * @param stay Le Temps d'affichage
     * @param fadeOut Le Temps de disparition
     *
     */
    public Interact.Actions.Title addTitleMessageClickAction(@Nullable NPC.Interact.ClickType clickType, @Nonnull String title, @Nonnull String subtitle, int fadeIn, int stay, int fadeOut) { return (Interact.Actions.Title) addClickAction(new Interact.Actions.Title(this, clickType, title, subtitle, fadeIn, stay, fadeOut)); }

    /**
     * Envoie un Message de type 'Titre' au Joueur lorsqu'on clique sur le NPC.
     *
     * @param title Le Titre principal à envoyé
     * @param subtitle Le Sous-Titre à envoyer
     * @param fadeIn Le Temps d'apparition
     * @param stay Le Temps d'affichage
     * @param fadeOut Le Temps de disparition
     *
     */
    public Interact.Actions.Title addTitleMessageClickAction(@Nonnull String title, @Nonnull String subtitle, int fadeIn, int stay, int fadeOut) { return addTitleMessageClickAction(Interact.ClickType.EITHER, title, subtitle, fadeIn, stay, fadeOut); }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Téléporte le Joueur lorsqu'on clique sur le NPC.
     *
     * @param clickType Le type de clique (Gauche ou Droit)
     * @param location La {@link Location localisation} où le Joueur sera téléporté
     *
     */
    public Interact.Actions.TeleportToLocation addTeleportToLocationClickAction(@Nullable NPC.Interact.ClickType clickType, @Nonnull Location location) { return (Interact.Actions.TeleportToLocation) addClickAction(new Interact.Actions.TeleportToLocation(this, clickType, location)); }

    /**
     * Téléporte le Joueur lorsqu'on clique sur le NPC.
     *
     * @param location La {@link Location localisation} où le Joueur sera téléporté
     *
     */
    public Interact.Actions.TeleportToLocation addTeleportToLocationClickAction(@Nonnull Location location) { return addTeleportToLocationClickAction(Interact.ClickType.EITHER, location); }


    /* ----------------------------------- */

    /**
     * Réinitialise les actions de cliqué sur le NPC.
     *
     * @param clickType Le type de clique (Gauche ou Droit)
     *
     */
    public void resetClickActions(@Nonnull NPC.Interact.ClickType clickType) {

        Validate.notNull(clickType, "Le type de clic ne peut pas être nul");
        List<NPC.Interact.ClickAction> remove = this.clickActions.stream().filter(x-> x.getClickType() != null && x.getClickType().equals(clickType) || clickType.equals(Interact.ClickType.EITHER)).toList();
        clickActions.removeAll(remove);

        if(clickType != Interact.ClickType.EITHER) {

            Interact.ClickType inverse = null;
            if(clickType.equals(Interact.ClickType.RIGHT_CLICK)) inverse = Interact.ClickType.LEFT_CLICK;
            if(clickType.equals(Interact.ClickType.LEFT_CLICK)) inverse = Interact.ClickType.RIGHT_CLICK;
            final Interact.ClickType inverseFinal = inverse;
            this.clickActions.stream().filter(x-> x.getClickType().equals(Interact.ClickType.EITHER)).forEach(x-> x.clickType = inverseFinal);
        }
    }

    /**
     * Réinitialise les actions de cliqué sur le NPC.
     *
     */
    public void resetClickActions() { this.clickActions = new ArrayList<>(); }

    /**
     * Définit des actions de cliqué sur le NPC.
     *
     * @param clickActions Les Actions en question
     *
     */
    protected void setClickActions(@Nonnull List<NPC.Interact.ClickAction> clickActions) { this.clickActions = clickActions; }

    /**
     * Récupères les actions de cliqué demandées sur le NPC.
     *
     * @param clickType Le type de clique (Gauche ou Droit)
     *
     */
    public List<NPC.Interact.ClickAction> getClickActions(@Nonnull NPC.Interact.ClickType clickType) { return this.clickActions.stream().filter(x-> x.getClickType() != null && x.getClickType().equals(clickType)).collect(Collectors.toList()); }

    /**
     * Récupères les actions de cliqué sur le NPC.
     *
     */
    public List<NPC.Interact.ClickAction> getClickActions() { return clickActions; }

    /* ----------------------------------- */

    /**
     * Supprime une action de cliqué sur le NPC.
     *
     * @param clickAction L'Action en question
     *
     */
    public void removeClickAction(NPC.Interact.ClickAction clickAction) { if(this.clickActions.contains(clickAction)) clickActions.remove(clickAction); }

    /* ------------------------- */
    /* ------------------------- */

    /**
     * Ajoute une action de cliqué sur le NPC.
     *
     * @param clickAction L'Action en question
     *
     */
    protected Interact.ClickAction addClickAction(@Nonnull NPC.Interact.ClickAction clickAction) {

        this.clickActions.add(clickAction);
        return clickAction;
    }

    /* ------------------------------------------------------- */

    /**
     * Déplace le NPC vers une {@link Location localisation} précise en précisant s'il doit regarder vers cette {@link Location localisation} pendant son déplacement.
     *
     * @param end La {@link Location localisation} en question
     * @param lookToEnd Le NPC doit regarder vers sa {@link Location localisation} ?
     *
     */
    public Move.Task goTo(@Nonnull Location end, boolean lookToEnd) {

        Validate.notNull(end, "Impossible de déplacer un NPC vers un emplacement nul.");
        Validate.isTrue(end.getWorld().getName().equals(world.getName()), "Impossible de déplacer un NPC dans un autre monde.");

        if(this.moveTask == null) {

            this.moveTask = new Move.Task(this, end, lookToEnd);
            return this.moveTask.start();
        }
        return null;
    }

    /**
     * Déplace le NPC vers une {@link Location localisation} précise.
     *
     * @param end La {@link Location localisation} en question
     */
    public void goTo(@Nonnull Location end) { goTo(end, true); }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Déplace le NPC vers une {@link Location localisation} précise en précisant s'il doit regarder vers cette {@link Location localisation} pendant son déplacement.
     * On définit également sa vitesse de déplacement.
     *
     * @param end La {@link Location localisation} en question
     * @param lookToEnd Le NPC doit regarder vers sa {@link Location localisation} ?
     * @param moveSpeed La vitesse de déplacement du NPC
     *
     */
    public Move.Task goTo(@Nonnull Location end, boolean lookToEnd, @Nullable Move.Speed moveSpeed) {

        setMoveSpeed(moveSpeed);
        return goTo(end, lookToEnd);
    }

    /**
     * Déplace le NPC vers une {@link Location localisation} précise.
     * On définit également sa vitesse de déplacement.
     *
     * @param end La {@link Location localisation} en question
     * @param moveSpeed La vitesse de déplacement du NPC
     *
     */
    public Move.Task goTo(@Nonnull Location end, @Nullable Move.Speed moveSpeed) {

        setMoveSpeed(moveSpeed);
        return goTo(end, true);
    }

    /* ----------------------------------- */
    /* ----------------------------------- */

    /**
     * Récupère la tâche de déplacement actuel du NPC.
     *
     */
    public Move.Task getMoveTask() { return moveTask; }

    /**
     * Annule le déplacement du NPC.
     *
     */
    public void cancelMove() {

        if(this.moveTask != null) moveTask.cancel(Move.Task.CancelCause.CANCELLED);
        clearMoveTask();
    }

    /* ------------------------------------------------------- */

    /**
     * Définit la {@link Move.Speed vitesse de déplacement} du NPC.
     *
     * @param moveSpeed La {@link Move.Speed vitesse de déplacement} en question
     *
     */
    public void setMoveSpeed(@Nullable Move.Speed moveSpeed) { attributes.setMoveSpeed(moveSpeed); }

    /**
     * Définit la {@link Double vitesse de déplacement} du NPC.
     *
     * @param moveSpeed La {@link Double vitesse de déplacement} en question
     *
     */
    public void setMoveSpeed(double moveSpeed) { attributes.setMoveSpeed(moveSpeed); }

    /**
     * Récupère {@link Double vitesse de déplacement} du NPC.
     *
     */
    public double getMoveSpeed() { return attributes.moveSpeed; }

    /* ------------------------------------------------------- */

    /**
     * Définit si oui ou non, le NPC doit brûler.
     *
     * @param animation L'Animation a joué.
     *
     */
    public abstract void playAnimation(NPC.Animation animation);

    /* ------------------------------------------------------- */

    /**
     * Définit si oui ou non, le NPC doit brûler.
     *
     * @param onFire Le NPC doit-il brûler ?
     *
     */
    public void setOnFire(boolean onFire) { attributes.setOnFire(onFire); }

    /**
     * Vérifie si le NPC est en feu.
     *
     */
    public boolean isOnFire() { return attributes.onFire; }

    /* ------------------------------------------------------- */

    /**
     * Définit le temps (en ticks) que le NPC brûlera.
     *
     * @param ticks Le temps en ticks que le NPC brûlera.
     *
     */
    public void setFireTicks(@Nonnull Integer ticks) {

        setOnFire(true);
        update();

        Bukkit.getScheduler().runTaskLater(pluginManager.getPlugin(), ()->{

            if(isOnFire()) { setOnFire(false); update(); }
        }, ticks.longValue());
    }

    /* ------------------------------------------------------- */

    /**
     * Définit si on affiche le NPC sur le 'Tablist'
     *
     * @param show Doit-on afficher le NPC dans le 'Tablist' ?
     *
     */
    public void setShowOnTabList(boolean show) { attributes.setShowOnTabList(show); }

    /**
     * Vérifie si le NPC est affiché sur le 'Tablist'
     *
     *
     */
    public boolean isShowOnTabList() { return attributes.showOnTabList; }


    /**
     * Définit un Nom Customisé pour le NPC dans le 'Tablist' et définit également s'il sera affiché ou non.
     *
     * @param name Le Nom Customisé en question
     * @param show Le Nom sera-t'il affiché ?
     *
     */
    public void setCustomTabListName(@Nullable String name, boolean show) {

        setCustomTabListName(name);
        setShowOnTabList(show);
    }

    /**
     * Récupère le Nom Customisé du NPC dans le 'Tablist'.
     *
     */
    public String getCustomTabListName() { return attributes.customTabListName; }

    /**
     * Réinitialise le Nom Customisé du NPC dans le 'Tablist'.
     *
     */
    public void resetCustomTabListName() { setCustomTabListName(null); }

    /* ------------------------------------------------------- */

    /**
     * Définit une donnée customisée du NPC en précisant une clé et sa valeur.
     *
     * @param key La clé de la donnée à définir.
     * @param value La valeur de la donnée à définir.
     */
    public void setCustomData(String key, String value) {

        if(customData.containsKey(key.toLowerCase()) && value == null) {

            customData.remove(key.toLowerCase());
            return;
        }

        customData.put(key.toLowerCase(), value);
    }

    /**
     * Récupère une donnée customisée du NPC en fonction de la clé demandé.
     *
     * @param key La clé de la donnée qu'il faut récupérer.
     */
    public String getCustomData(String key) {

        if(!customData.containsKey(key.toLowerCase())) return null;
        return customData.get(key.toLowerCase());
    }

    /**
     * Récupère la liste des données customisées du NPC.
     *
     */
    public Set<String> getCustomDataKeys() { return customData.keySet(); }


    /**
     * Vérifie si le NPC à une donnée customisée en fonction de la clé demandé.
     *
     * @param key La clé de la donnée qu'il faut vérifier
     */
    public boolean hasCustomData(String key) { return customData.containsKey(key.toLowerCase()); }

    /* ------------------------------------------------------- */

    /**
     * Vérifie si les collisions sont activées sur le NPC.
     *
     */
    public boolean isCollidable() { return attributes.collidable; }

    /**
     * Active ou non les collisions avec le NPC.
     *
     * @param collidable Une valeur Booléenne
     */
    public void setCollidable(boolean collidable) { attributes.setCollidable(collidable); }

    /* ------------------------------------------------------- */

    /**
     * Récupère l'Équipement(s) du NPC.
     *
     */
    protected HashMap<EquipmentSlot, ItemStack> getEquipment() { return attributes.slots; }

    /**
     * Récupère un Équipement précis pour le NPC.
     *
     * @param slot L'Emplacement de l'Équipement en question
     *
     */
    public ItemStack getEquipment(EquipmentSlot slot) { return attributes.slots.get(slot); }

    /* ------------------------------------------------------- */

    /**
     * Récupère lo {@link Location localisation} du NPC.
     *
     */
    public Location getLocation() { return new Location(getWorld(), getX(), getY(), getZ(), getYaw(), getPitch()); }

    /**
     * Récupère le monde du NPC.
     *
     */
    public World getWorld() { return world; }

    /**
     * Récupère la coordonnée 'x' du NPC.
     *
     */
    public Double getX() { return x; }

    /**
     * Récupère la coordonnée 'y' du NPC.
     *
     */
    public Double getY() { return y; }

    /**
     * Récupère la coordonnée 'z' du NPC.
     *
     */
    public Double getZ() { return z; }

    /**
     * Récupère la rotation 'yaw' du NPC.
     *
     */
    public Float getYaw() { return yaw; }

    /**
     * Récupère la rotation 'pitch' du NPC.
     *
     */
    public Float getPitch() { return pitch; }


    /**
     * Récupère la rotation 'yaw' par défaut du NPC.
     *
     */
    public Float getDefaultYAW() { return defaultYAW; }

    /**
     * Récupère la rotation 'pitch' par défaut du NPC.
     *
     */
    public Float getDefaultPITCH() { return defaultPITCH; }

    /* --------------------------------- */

    /**
     * Définit le monde du NPC.
     *
     * @param world Le Monde en question.
     *
     */
    protected void setWorld(World world) { this.world = world; }

    /**
     * Définit la coordonnée 'x' du NPC.
     *
     * @param x La coordonnée 'x' en question.
     *
     */
    protected void setX(double x) { this.x = x; }

    /**
     * Définit la coordonnée 'y' du NPC.
     *
     * @param y La coordonnée 'y' en question.
     *
     */
    protected void setY(double y) { this.y = y; }

    /**
     * Définit la coordonnée 'z' du NPC.
     *
     * @param z La coordonnée 'z' en question.
     *
     */
    protected void setZ(double z) { this.z = z; }

    /**
     * Définit la rotation 'yaw' du NPC.
     *
     * @param yaw La rotation 'yaw' en question.
     *
     */
    protected void setYaw(float yaw) { this.yaw = yaw; }

    /**
     * Définit la rotation 'pitch' du NPC.
     *
     * @param pitch La rotation 'pitch' en question.
     *
     */
    protected void setPitch(float pitch) { this.pitch = pitch; }

    /**
     * Définit la rotation 'yaw' par défaut du NPC.
     *
     */
    protected void setDefaultYAW(float defaultYAW) { this.defaultYAW = defaultYAW; }

    /**
     * Définit la rotation 'pitch' par défaut du NPC.
     *
     */
    protected void setDefaultPITCH(float defaultPITCH) { this.defaultPITCH = defaultPITCH; }

    /* ------------------------------------------------------- */

    /**
     * Récupère le code d'identification du NPC
     *
     */
    public String getCode() { return code; }

    /**
     * Récupère le code d'identification simplifié du NPC
     *
     */
    public String getSimpleCode() { return this.code.replaceFirst(getPlugin().getName().toLowerCase() + "\\.", ""); }

    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */

    /************************************/
    /* QUELQUE CLASS UTILE POUR LE NPC */
    /***********************************/

    /**
     * SKIN POUR LE NPC
     *
     */
    public static class Skin {

        protected static final Skin STEVE; // Variable récupérant le SKIN STEVE
        protected static final Skin ALEX; // Variable récupérant le SKIN ALEX
        protected static final HashMap<String, NPC.Skin> SKIN_CACHE; // Variable récupérant un cache des Skins chargés
        protected static final List<String> LOCAL_SKIN_NAMES; // Variable récupérant le nom local de chaques Skins chargés

        static {

            SKIN_CACHE = new HashMap<>(); // On Initialise la variable récupérant le cache des Skins

            // ** Skin Par Défaut (Steve) ** //
            STEVE = new Skin("ewogICJ0aW1lc3RhbXAiIDogMTY1NjUzMDcyOTgyNiwKICAicHJvZmlsZUlkIiA6ICJjMDZmODkwNjRjOGE0OTExOWMyOWVhMWRiZDFhYWI4MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU3RldmUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE0YWY3MTg0NTVkNGFhYjUyOGU3YTYxZjg2ZmEyNWU2YTM2OWQxNzY4ZGNiMTNmN2RmMzE5YTcxM2ViODEwYiIKICAgIH0KICB9Cn0=",
                    "D5KDlE7KmMYeo+n0bY7kRjxdoZ8ondpgLC0tVcDW/wER9tRAWGlkaUyC4cUjkiYtMFANOxnPNz42iWg+gKAX/qE3lKoJpFw8LmgC587QpEDZTsIwzrIriDDiUc+RQ83VNzy9lkrzm+/llFhuPmONhWIeoVgXQYnJXFXOjTA3uiqHq6IJR4fZzD+0lSpr8jm0X1B+XAiAV7xbzMjg2woC3ur7+81Ub27MNGdAmI5eh50rqqjIHx+kRHJPPB3klbAdkTkcnF2rhDuP9jLtJbb17L+40yR8MH3G1AsRBg+N9MlGb4qF3fK9m2lDNxrGpVe+5fj4ffHnTJ680X9O8cnGxtHFyHm3I65iIhVgFY/DQQ6XSxLgPrmdyVOV98OATc7g2/fFpiI6aRzFrXvCLzXcBzmcayhv8BgG8yBlHdYmMZScjslLKjgWB9mgtOh5ZFFb3ZRkwPvdKUqCQHDPovo9K3LwyAtg9QwJ7u+HN03tpDWllXIjT3mKrtsfWMorNNQ5Bh1St0If4Dg00tpW/DUwNs+oua0PhN/DbFEe3aog2jVfzy3IAXqW2cqiZlnRSm55vMrr1CI45PgjP2LS1c9OYJJ3k+ov4IdvBpDTiG9PfsPWcwtqm8ujxy/TqIWfSajL/RP+TFDoN/F8j8HhHU8wwA9JXJekmvUExEOxPWwisLA=");
            STEVE.playerName = "MHF_Steve"; // Nom du Skin
            STEVE.playerUUID = "c06f89064c8a49119c29ea1dbd1aab82"; // UUID du Skin
            STEVE.obtainedFrom = ObtainedFrom.MINECRAFT_ORIGINAL; // La façon dont elle a été obtenue
            SKIN_CACHE.put(STEVE.playerName.toLowerCase(), STEVE); // On l'ajoute dans le cache des Skins
            // ** Skin Par Défaut (Steve) ** //


            // ** Skin Par Défaut (Alex) ** //
            ALEX = new Skin("ewogICJ0aW1lc3RhbXAiIDogMTY1NjU3Nzg5MTQ2NywKICAicHJvZmlsZUlkIiA6ICI2YWI0MzE3ODg5ZmQ0OTA1OTdmNjBmNjdkOWQ3NmZkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQWxleCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84M2NlZTVjYTZhZmNkYjE3MTI4NWFhMDBlODA0OWMyOTdiMmRiZWJhMGVmYjhmZjk3MGE1Njc3YTFiNjQ0MDMyIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                    "d2dYqFQpP2ZjeUROSm23WTdWhWnuaW68v8Biw4towx04vJMRls95W/gmIFGa2Tq171yXHlE8kpP2KFe3jAC7qukkXjDiXSRdCSOYZPA7N91Uw6amyt7x5IKZ90QK8BxE1mCjV7KJNGZ28u8klbf1QUOB4fE27gEfQYGyEcSrkPa4e/QzmOGYbnyiIt36np/qBtWHf87brRdVeKRNfO/ExCJkKbwpKfyGf06luCAfUW9wuHkFURux9naU+ilk2ZHUsPVBdkmfOXZrdxxdpDE19W5VkFryMbtVP5XNEBVC7SAsllHXrf8nskgk+m57bCPMP6RF8k+h+mXIJMuQd7yd7azOAnyLlOoufyY1hs1Po+EGDOSQUUHQKTi7AEYp2C71DpkqpGuPCbL/DkxchblYW5iuIek+BmO3wXbmBPv+0gWkiP/c1n605X0g+h4oO5yQqyI8Fki9F2Hb8T5QeHmC3+yzVVf7gOQ6MB7bBt+uX9wcl5yYBDHbmYGZtbNko7dq584FZKRRWeVhxdcDUXfdfzKmNR73BUIEqzeyOh2hUrk47VHK5d5FajKzgi9j5U8D0EJKjVMPZiulcF0J/ZQ4EOxUkOTNPuphiu43j1C7NXZ4RaPFrSrg7QMsObitqLUP5Pmq15Edg7vpvYME8Fe5Ia8sXLbNDHd3AWuXnfpeAUE=");
            ALEX.playerName = "MHF_Alex"; // Nom du Skin
            ALEX.playerUUID = "6ab4317889fd490597f60f67d9d76fd9"; // UUID du Skin
            ALEX.obtainedFrom = ObtainedFrom.MINECRAFT_ORIGINAL; // La façon dont elle a été obtenue
            SKIN_CACHE.put(ALEX.playerName.toLowerCase(), ALEX); // On l'ajoute dans le cache des Skins
            // ** Skin Par Défaut (Alex) ** //

            LOCAL_SKIN_NAMES = new ArrayList<>(); // On Initialise la variable récupérant le nom local de chaque Skin

            // ⬇️ On essaie de charger les Skins locales ⬇️ //
            Bukkit.getScheduler().runTaskAsynchronously(UtilityMain.getInstance(), () -> {

                File folder = new File(getSkinsFolderPath()); // On récupère le dossier des Skins locales
                if(!folder.exists() || !folder.isDirectory()) return; // On vérifie si le dossier est bien là

                // ⬇️ Pour chaque fichier, on récupère le nom de chaque Skin ⬇️ //
                for(File skin : folder.listFiles()) {

                    if(!skin.isDirectory()) continue; // Si le fichier en question n'est pas un répertoire, on continue
                    LOCAL_SKIN_NAMES.add(skin.getName()); // On ajoute le nom local du Skin dans la liste à la variable récupérant le nom de chaque Skin
                }
                // ⬆️ Pour chaque fichier, on récupère le nom de chaque Skin ⬆️ //
            });
            // ⬆️ On essaie de charger les Skins locales ⬆️ //
        }

        private final String texture;  // Texture du Skin
        private final String signature; // Signature du Skin
        private String textureID; // Identifiant du Skin
        private String playerName; // Pseudonyme du joueur ayant le Skin
        private String playerUUID; // UUID du joueur ayant le Skin
        private ChatFormatting[][] avatar;  // Avatar du Skin
        private ChatFormatting mostCommonColor; // La Couleur la plus commune du Skin
        private ObtainedFrom obtainedFrom; // La façon comment le Skin est obtenu.
        private String lastUpdate; // La dernière mise à jour du Skin

        /**
         * On instancie un nouveau {@link Skin}.
         *
         * @param texture La Texture du Skin
         * @param signature la Signature du Skin
         */
        protected Skin(String texture, String signature) {

            this.texture = texture;
            this.signature = signature;
            this.textureID = null;
            this.playerName = null;
            this.playerUUID = null;
            this.avatar = null;
            this.mostCommonColor = null;
            this.obtainedFrom = ObtainedFrom.NONE;
            resetLastUpdate();
        }

        /**
         * On instancie un nouveau {@link Skin}.
         *
         * @param data Tableau stockant la Texture et la Signature du Skin
         */
        protected Skin(String[] data) { this(data[0], data[1]); }


        /**
         * On applique le Skin au {@link NPC} en question.
         *
         * @param npc Le {@link NPC} en question
         */
        public void applyNPC(NPC npc) { applyNPC(npc, false); }


        /**
         * On applique le Skin au {@link NPC} en précisant si on souhaite forcer la mise à jour ou non.
         *
         * @param npc Le {@link NPC} en question
         */
        public void applyNPC(NPC npc, boolean forceUpdate) {

            npc.setSkin(this);
            if(forceUpdate) npc.forceUpdate();
        }

        /**
         * On applique le Skin à plusieurs {@link NPC NPCs}
         *
         * @param npcs Les {@link NPC NPCs} en question
         */
        public void applyNPCs(Collection<NPC> npcs) { applyNPCs(npcs, false); }

        /**
         * On applique le Skin à plusieurs {@link NPC NPCs} en précisant si on souhaite forcer la mise à jour ou non.
         *
         * @param npcs Les {@link NPC NPCs} en question
         */
        public void applyNPCs(Collection<NPC> npcs, boolean forceUpdate) { npcs.forEach(x-> applyNPC(x, forceUpdate)); }


        /**
         * Récupère la Texture du Skin
         *
         * @return La Texture du Skin
         */
        public String getTexture() { return texture; }

        /**
         * Récupère la Signature du Skin
         *
         * @return La Signature du Skin
         */
        public String getSignature() { return signature; }

        /**
         * Récupère le Pseudonyme du joueur ayant ce Skin
         *
         * @return Le Pseudonyme du joueur ayant ce Skin
         */
        public String getPlayerName() { return playerName; }

        /**
         * Récupère l'{@link UUID} du joueur ayant ce Skin
         *
         * @return L'{@link UUID} du joueur ayant ce Skin
         */
        public String getPlayerUUID() { return playerUUID; }

        /**
         * Récupère la {@link ObtainedFrom façon dont a été obtenue} ce Skin
         *
         * @return La {@link ObtainedFrom façon dont a été obtenue} dont a été obtenue ce Skin
         */
        public ObtainedFrom getObtainedFrom() { return obtainedFrom; }

        /**
         * Récupère la dernière mise à jour de ce Skin
         *
         * @return la dernière mise à jour du Skin
         */
        public String getLastUpdate() { return lastUpdate; }

        /**
         * Le Skin peut-il être supprimé ?
         *
         * @return Une valeur Booléenne
         */
        public boolean canBeDeleted() { return isMinecraftOriginal(); }

        /**
         * Le Skin peut-il être mis à jour ?
         *
         * @return Une valeur Booléenne
         */
        public boolean canBeUpdated() { return isMinecraftOriginal(); }

        /**
         * Le Skin est-il un Skin Original de Minecraft
         *
         * @return Une valeur Booléenne
         */
        public boolean isMinecraftOriginal() { return !obtainedFrom.equals(ObtainedFrom.MINECRAFT_ORIGINAL); }

        /**
         * Le Type du Skin (s'il s'agit d'un Skin de Joueur ou d'une Texture customisé)
         *
         * @return Le Type du Skin
         */
        public Type getType() { return playerName != null ? Type.PLAYER_SKIN : Type.CUSTOM_TEXTURE; }


        /**
         * Réinitialise la dernière mise à jour du Skin
         *
         */
        private void resetLastUpdate() { this.lastUpdate = TimerUtils.getCurrentDate(); }

        /**
         * Récupère le fichier de l'Avatar du Skin
         *
         */
        private File getAvatarFile() { return getAvatarFile(this.playerName); }

        /**
         * Récupère le fichier de la Texture du Skin
         *
         */
        private File getTextureFile() { return getTextureFile(this.playerName); }

        /**
         * Récupère le fichier des données du Skin
         *
         */
        private File getDataFile() { return getDataFile(this.playerName); }

        /**
         * Récupère dossier des Skins
         *
         */
        private String getSkinFolderPath() { return getSkinFolderPath(this.playerName); }

        /**
         * Récupère le fichier de l'avatar d'un Skin en question en précisant le Pseudonyme associé au Skin.
         *
         * @param playerName Pseudonyme associé au Skin
         *
         */
        private static File getAvatarFile(String playerName) { return new File(getSkinFolderPath(playerName) + "/avatar.png"); }

        /**
         * Récupère le fichier de la texture d'un Skin en question en précisant le Pseudonyme associé au Skin.
         *
         * @param playerName Pseudonyme associé au Skin
         *
         */
        private static File getTextureFile(String playerName) { return new File(getSkinFolderPath(playerName) + "/texture.png"); }

        /**
         * Récupère le fichier les données d'un Skin en question en précisant le Pseudonyme associé au Skin.
         *
         * @param playerName Pseudonyme associé au Skin
         *
         */
        private static File getDataFile(String playerName) { return new File(getSkinFolderPath(playerName) + "/data.yml"); }

        /**
         * Récupère le chemin du dossier d'un Skin en question en le Pseudonyme associé au Skin.
         *
         * @param playerName Pseudonyme associé au Skin
         *
         */
        private static String getSkinFolderPath(String playerName) { return getSkinsFolderPath() + playerName.toLowerCase(); }

        /**
         * Récupère le chemin parent du dossier des Skins.
         *
         */
        private static String getSkinsFolderPath() { return "plugins/EvhoUtility/PlayerNPC/persistent/skins/"; }

        /**
         * Récupère la Couleur la plus commune du Skin.
         *
         */
        public ChatFormatting getMostCommonColor() { return mostCommonColor; }

        /* -------------------------------------------------- */

        /************************************/
        /* ÉNUMÉRATION POUR LE TYPE DE SKIN */
        /************************************/

        public enum Type { PLAYER_SKIN, CUSTOM_TEXTURE }

        /************************************/
        /* ÉNUMÉRATION POUR LE TYPE DE SKIN */
        /************************************/


        /*******************************************************/
        /* ÉNUMÉRATION POUR LA FAÇON DONT LE SKIN A ÉTÉ OBTENU */
        /******************************************************/

        public enum ObtainedFrom {

            MOJANG_API(ChatFormatting.GREEN + "Mojang API"), // Obtenue par l'API Mojang
            GAME_PROFILE(ChatFormatting.GREEN + "Game Profile"), // Obtenue par le Profil de Jeux
            MINECRAFT_ORIGINAL(ChatFormatting.GREEN + "Minecraft"), // Obtenue par Minecraft
            NONE(ChatFormatting.RED + "Unknown"); // Inconnue

            private final String title;

            ObtainedFrom(String title) { this.title = title; }

            public String getTitle() { return title; }
        }

        /*******************************************************/
        /* ÉNUMÉRATION POUR LA FAÇON DONT LE SKIN A ÉTÉ OBTENU */
        /******************************************************/

        /* -------------------------------------------------- */

        /**
         * On charge l'Avatar du Skin actuel.
         *
         */
        protected void loadAvatar() {

            if(playerName == null) return;
            if(!getAvatarFile().exists()) downloadAvatar(true);
            if(loadAvatarPixels()) return;

            downloadAvatar(false);
            loadAvatarPixels();
        }

        /**
         * On charge les pixels de l'Avatar du Skin actuel.
         *
         */
        protected boolean loadAvatarPixels() {

            try {

                BufferedImage bufferedImage = ImageIO.read(getAvatarFile()); // Récupère le fichier de l'avatar du Skin actuel
                ChatFormatting[][] avatarData = new ChatFormatting[8][8]; // Récupère la donnée de l'avatar
                Map<Integer, Integer> m = new HashMap<>(); // Variable récupérant chaque couleur avec un numéro itéré

                boolean loaded = false; // Définit par défaut sur faux le rechargement de l'avatar en question

                // ⬇️ on boucle sur chaque pixel pour vérifier si le pixel n'est pas en gris ⬇️ //
                for(int y = 0; y < 8; y++) {

                    for(int x = 0; x < 8; x++) {

                        int color = bufferedImage.getRGB(x, y); // Récupère la variable de couleur RGB pour chaque pixel
                        int[] rgb = ColorUtils.getRGB(color); // Convertie ces variables de couleurs RGB

                        // Si les variables de couleurs RED, GREEN et BLUE sont tous supérieurs à 0, on définit le chargement sur 'vrai'
                        if(rgb[0] > 0 && rgb[1] > 0 && rgb[2] > 0) loaded = true;

                        // Définit les pixels en questions du donné de l'avatar par la couleur RGB récupéré
                        avatarData[x][y] = ColorUtils.convertChatFormatting(ColorUtils.getColorFromRGB(rgb));

                        // ⬇️ Si la couleur RGB actuel n'est pas grise, on récupère ou définit le numéro associé à cette couleur ⬇️ //
                        if(!ColorUtils.isGray(rgb)) {

                            Integer counter = m.get(color); // Récupère le numéro associé à cette couleur
                            if(counter == null) counter = 0; // Si le numéro associé est null, on la définit à zéro
                            counter++; // On itère le numéro
                            m.put(color, counter); // On ajoute le numéro à la couleur
                        }
                        // ⬆️ Si la couleur RGB actuel n'est pas grise, on récupère ou définit le numéro associé à cette couleur ⬆️ //
                    }
                }
                // ⬆️ on boucle sur chaque pixel pour vérifier si le pixel n'est pas en gris ⬆️ //

                this.avatar = avatarData; // On recharge l'avatar du Skin
                java.awt.Color mostCommon = ColorUtils.getMostCommonColour(m); // On Récupère la couleur la plus commune

                // Si la couleur la plus commune n'est pas null, on recharge cette couleur
                if(mostCommon != null) this.mostCommonColor = ColorUtils.convertChatFormatting(mostCommon);
                return loaded; // On retourne un booléen du rechargement

                // Sinon, on affiche une erreur
            } catch(Exception e) {

                NPCUtils.printError(e); // Affiche l'erreur de l'exception
                return false; // On retourne faux
            }
        }

        /**
         * On télécharge un Avatar pour le Skin en utilisant uuid pour être plus précis ou son pseudonyme.
         *
         * @param uuid Téléchargeons-nous l'Avatar avec son UUID ?
         *
         */
        protected void downloadAvatar(boolean uuid) {

            if(playerUUID == null || playerName == null) return;

            // ⬇️ On essaie de télécharger le Skin sur Minotar.net, sinon on affiche une erreur ⬇️ //
            try {

                URL url = new URL("https://minotar.net/helm/" + (uuid ? playerUUID : playerName) + "/8.png");
                BufferedImage bufferedImage = ImageIO.read(url);

                getAvatarFile().mkdirs();
                ImageIO.write(bufferedImage, "png", getAvatarFile());

            } catch(Exception e) { NPCUtils.printError(e); }
            // ⬆️ On essaie de télécharger le Skin sur Minotar.net, sinon on affiche une erreur ⬆️ //
        }

        /**
         * Récupère l'Avatar du Skin actuel.
         *
         */
        public ChatFormatting[][] getAvatar() {

            if(avatar == null) loadAvatar();
            return avatar;
        }

        /**
         * Récupère les données de Texture du Skin actuel.
         *
         */
        public String[] getTextureData() { return new String[]{texture, signature}; }

        /**
         * Récupère l'identifiant de Texture du Skin actuel.
         *
         */
        public String getTextureID() { return textureID; }

        /**
         * On crée une Texture customisée pour le Skin
         *
         */
        public static Skin createCustomTextureSkin(String texture, String value) { return new Skin(texture, value); }


        /**
         * Essaie de recharger le Skin actuel en précisant le Joueur en ligne.
         *
         * @param plugin Le plugin sur lequel travaillé avec le NPC
         * @param player Le Joueur en ligne
         * @param action L'Action a éffectué pour la recharge du Skin
         *
         */
        public static void fetchSkinAsync(Plugin plugin, Player player, Consumer<NPC.Skin> action) { fetchSkinAsync(plugin, player.getName(), action); }

        /**
         * Essaie de recharger le Skin actuel en précisant le nom d'un joueur ou son UUID.
         *
         * @param plugin Le plugin sur lequel travaillé avec le NPC
         * @param playerName Le nom d'un joueur ou son UUID
         * @param action L'Action a éffectué pour la recharge du Skin
         *
         */
        public static void fetchSkinAsync(Plugin plugin, String playerName, Consumer<NPC.Skin> action) { fetchSkinAsync(plugin, playerName, false, action); }

        /**
         * Essaie de recharger le Skin actuel en précisant le nom d'un joueur en forçant ou non le téléchargement du Skin.
         *
         * @param plugin Le plugin sur lequel travaillé avec le NPC
         * @param playerName Le nom d'un joueur ou son UUID
         * @param forceDownload Téléchargeons-nous le Skin dans son répertoire correspondant ?
         * @param action L'Action a éffectué pour la recharge du Skin
         *
         */
        public static void fetchSkinAsync(Plugin plugin, String playerName, boolean forceDownload, Consumer<NPC.Skin> action) {

            final NPCUtils.PluginManager pluginManager = NPCUtils.getInstance().getPluginManager(plugin); // On récupère la gestion du plugin de la librairie NPC
            final String playerNameLowerCase = playerName.toLowerCase(); // On récupère le nom du Joueur auquel récupère le Skin en remplaçant chaque majuscule en minuscule
            final String possibleUUID = playerName.length() >= 32 ? playerName.replaceAll("-", "") : null; // On récupère l'UUID si le nom du Joueur est plutôt une UUID

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()-> {

                /* ⬇️ On vérifie si on n'a pas demandé de téléchargé le Skin et qu'il n'y a pas de UUID a récupéré,
                   alors on récupère le Skin demandé en fonction des données récupéré ⬇️ */
                if(!forceDownload && possibleUUID == null) {

                    // ⬇️ Si le cache contient le nom du Joueur étant associé au Skin, alors on récupère le Skin en fonction du nom récupéré en cache pour le rechargement ⬇️ //
                    if(SKIN_CACHE.containsKey(playerNameLowerCase)) {

                        Skin skin = SKIN_CACHE.get(playerNameLowerCase); // Récupère le Skin en fonction du nom récupéré en cache
                        NPCUtils.SkinUpdateFrequency frequency = pluginManager.getSkinUpdateFrequency(); // On Récupère la fréquence de mise à jour du Skin

                        /* On vérifie si la date entre la dernière mise à jour du Skin et la date actuelle a une fréquence de mise à jour inférieure à celle actuelle,
                           alors on accepte l'action de rechargement du Skin */
                        if(TimerUtils.getBetweenDatesString(skin.getLastUpdate(), TimerUtils.getCurrentDate(), TimerUtils.DATE_FORMAT_LARGE, frequency.timeUnit()) < frequency.value()) {

                            action.accept(skin); // On accepte l'action pour la recharge du Skin.
                            return; // On sort

                            // Sinon, on supprime le nom du Joueur étant associé au Skin du cache
                        } else SKIN_CACHE.remove(playerNameLowerCase);
                    }
                    // ⬆️ Si le cache contient le nom du Joueur ayant associé au Skin, alors on récupère le Skin en fonction du nom récupéré en cache pour le rechargement ⬆️ //


                    /* ⬇️ On vérifie si le fichier de données du Skin existe, alors on recharge le skin à partir de son fichier de configuration,
                        si le skin n'est pas à jour ⬇️ */
                    if(getDataFile(playerNameLowerCase).exists()) {

                        YamlConfiguration config = loadConfig(playerNameLowerCase); // On recharge le fichier de configuration en question
                        String lastUpdate = config.getString("lastUpdate"); // On récupère la dernière mise à jour du Skin enregistré.
                        NPCUtils.SkinUpdateFrequency frequency = pluginManager.getSkinUpdateFrequency(); // On Récupère la fréquence de mise à jour du Skin

                        /* On vérifie si la date entre la dernière mise à jour du Skin enregistré et la date actuelle a une fréquence de mise à jour inférieure à celle actuelle,
                           alors on recharge le Skin et son fichier de configuration en acceptant l'action de rechargement du Skin */
                        if(TimerUtils.getBetweenDatesString(lastUpdate, TimerUtils.getCurrentDate(), TimerUtils.DATE_FORMAT_LARGE, frequency.timeUnit()) < frequency.value()) {

                            // Récupère le Skin en fonction des données dans le fichier de configuration
                            Skin skin = new Skin(config.getString("texture.value"), config.getString("texture.signature"));

                            skin.playerName = config.getString("player.name"); // On ajoute le pseudonyme au Skin en question récupère dans le fichier de configuration
                            skin.playerUUID = config.getString("player.id"); // On ajoute l'UUID au Skin en question récupère dans le fichier de configuration
                            skin.textureID = config.getString("texture.id"); // On ajoute l'identifiant de texture au Skin en question récupère dans le fichier de configuration

                            // On ajoute la façon dont le Skin en question a été obtenu récupérer dans le fichier de configuration
                            skin.obtainedFrom = ObtainedFrom.valueOf(config.getString("obtainedFrom"));

                            skin.lastUpdate = config.getString("lastUpdate"); // On ajoute la dernière mise à jour du Skin en question récupéré dans le fichier de configuration

                            SKIN_CACHE.put(playerNameLowerCase, skin); // On ajoute le Skin dans le cache
                            LOCAL_SKIN_NAMES.remove(playerName.toLowerCase()); // On supprime le nom local du Skin actuel
                            action.accept(skin); // On accepte l'action pour la recharge du Skin.

                            return; // On sort
                        }
                    }
                    /* ⬆️ On vérifie si le fichier de données du Skin existe, alors on recharge le skin à partir de son fichier de configuration,
                        si le skin n'est pas à jour ⬆️ */
                }
                /* ⬆️ On vérifie si on n'a pas demandé de téléchargé le Skin et qu'il n'y a pas de UUID a récupéré,
                   alors on récupère le Skin demandé en fonction des données récupéré ⬆️ */


                // On récupère le joueur en ligne par la variable 'playerName'.
                Player player = possibleUUID == null ? Bukkit.getServer().getPlayerExact(playerName) : null;

                // Si le Serveur est en mode en ligne (acceptant que les premiums) et que le Joueur n'est pas null, alors on recharge le skin en fonction du joueur récupéré
                if(Bukkit.getServer().getOnlineMode() && player != null) {

                    // Récupère le Skin en fonction du joueur récupéré
                    Skin skin = new Skin(getSkinGameProfile(player));

                    skin.playerName = player.getName();  // On ajoute le pseudonyme au Skin en question par le nom du joueur récupéré
                    skin.playerUUID = player.getUniqueId().toString().replaceAll("-", ""); // On ajoute l'UUID au Skin en question par l'UUID du joueur récupéré
                    skin.obtainedFrom = ObtainedFrom.GAME_PROFILE; // On définit la façon dont le Skin en question a été obtenu par le profile de jeux.

                    // ⬇️ On essaie de sauvegarder le Skin, sinon, on affiche une erreur ⬇️ //
                    try { skin.saveSkin(); }
                    catch(IOException e) { NPCUtils.printError(e); }
                    // ⬆️ On essaie de sauvegarder le Skin, sinon, on affiche une erreur ⬆️ //

                    // On vérifie si le fichier d'avatar existe pour le Skin, alors on supprime ce fichier
                    if(skin.getAvatarFile().exists()) skin.getAvatarFile().delete();

                    SKIN_CACHE.put(playerNameLowerCase, skin); // On ajoute le Skin dans le cache
                    action.accept(skin); // On accepte l'action pour la recharge du Skin.

                    // Sinon, on essaie de recharger le skin en fonction de l'UUID récupéré
                } else {

                    // On essaie de recharger le skin en fonction de l'UUID récupéré
                    try {

                        String uuid = possibleUUID == null ? PlayerEntity.getUUIDByPlayerName(playerName, null).toString() : possibleUUID; // On récupère l'UUID par la variable 'playerName'
                        HashMap<String, String> data = getProfileMojangServer(uuid); // On récupère les données depuis le serveur mojang correspondant à l'UUID récupéré

                        // Récupère le Skin en fonction des données récupéré depuis le serveur mojang
                        Skin skin = new Skin(data.get("texture.value"), data.get("texture.signature"));

                        skin.playerName = data.get("name"); // On ajoute le pseudonyme au Skin en question récupère depuis le serveur mojang
                        skin.playerUUID = data.get("id"); // On ajoute l'UUID au Skin en question récupère depuis le serveur mojang
                        skin.obtainedFrom = ObtainedFrom.MOJANG_API; // On définit la façon dont le Skin en question a été obtenu par l'API Mojang.
                        skin.saveSkin();  // On sauvegarde le Skin

                        // On vérifie si le fichier d'avatar existe pour le Skin, alors on supprime ce fichier
                        if(skin.getAvatarFile().exists()) skin.getAvatarFile().delete();

                        SKIN_CACHE.put(playerNameLowerCase, skin); // On ajoute le Skin dans le cache
                        action.accept(skin); // On accepte l'action pour la recharge du Skin.

                        // Si une exception ce passe, on affiche l'erreur et on annule l'action pour la recharge du Skin.
                    } catch(Exception e) {

                        NPCUtils.printError(e); // Affiche l'erreur de l'exception
                        action.accept(null); // On annule l'action pour la recharge du Skin.
                    }
                }
            });
        }

        /**
         * Supprime le Skin actuel.
         *
         */
        public void delete() {

            // On vérifie si le Skin ne peut pas être alors, on affiche une erreur disant que le Skin ne peut pas être supprimé
            if(!canBeDeleted()) throw new IllegalStateException("Ce Skin ne peut pas être supprimée.");

            String playerNameLowerCase = playerName.toLowerCase(); // On récupère le nom du Joueur du Skin en remplaçant chaque majuscule en minuscule

            // Si le nom du Joueur du Skin existe dans le cache, alors on lui enlève
            SKIN_CACHE.remove(playerNameLowerCase);

            File folder = new File(getSkinFolderPath(playerNameLowerCase) + "/"); // On récupère le dossier du Skin en question à partir de son Nom

            // ⬇️ On essaie de supprimer le répertoire entièrement, sinon, on affiche une erreur ⬇️ //
            try { FileUtils.deleteDirectory(folder); }
            catch(IOException e) { NPCUtils.printError(e); }
            // ⬆️ On essaie de supprimer le répertoire entièrement, sinon, on affiche une erreur ⬆️ //

            // Le nom local du Skin actuel contient bien le nom du Skin, alors on lui enlève
            LOCAL_SKIN_NAMES.remove(playerNameLowerCase);
        }

        /**
         * Recharge le dossier de configuration du Skin.
         *
         * @param playerName Le nom du joueur du Skin (nom du fichier censé être enregistré)
         *
         * @return Le fichier de configuration en question rechargé
         */
        private static YamlConfiguration loadConfig(String playerName) {

            File file = getDataFile(playerName); // On récupère le fichier de donnée du Skin à partir de son nom récupéré

            // ⬇️ On vérifie si le fichier n'éxiste pas, alors on essaie de le créer ⬇️ //
            if(!file.exists()) {

                // ⬇️ On essaie de créer le fichier, sinon, on ne fait rien ⬇️ //
                try { file.createNewFile(); }
                catch(Exception ignored) {}
                // ⬆️ On essaie de créer le fichier, sinon, on ne fait rien ⬆️ //
            }
            // ⬆️ On vérifie si le fichier n'éxiste pas, alors on essaie de le créer ⬆️ //

            return YamlConfiguration.loadConfiguration(file); // On retourne le fichier en question rechargé
        }

        /**
         * Sauvegarde le Skin dans un fichier de configuration.
         *
         */
        private void saveSkin() throws IOException {

            File file = getDataFile(); // On récupère le fichier de donnée du Skin

            // ⬇️ On vérifie si le fichier n'éxiste pas, alors on essaie de le créer ⬇️ //
            if(!file.exists()) {

                // ⬇️ On essaie de créer le fichier, sinon, on ne fait rien ⬇️ //
                try { file.createNewFile(); }
                catch(Exception ignored) {}
                // ⬆️ On essaie de créer le fichier, sinon, on ne fait rien ⬆️ //
            }
            // ⬆️ On vérifie si le fichier n'éxiste pas, alors on essaie de le créer ⬆️ //

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file); // On recharge le fichier en question

            config.set("player.name", this.playerName); // On ajoute le pseudonyme au Skin en question dans le fichier
            config.set("player.id", this.playerUUID); // On ajoute l'UUID au Skin en question dans le fichier
            config.set("texture.value", this.texture); // On ajoute la Texture du Skin en question dans le fichier
            config.set("texture.signature", this.signature); // On ajoute la Signature du Skin en question dans le fichier
            config.set("obtainedFrom", this.obtainedFrom.name()); // On ajoute la façon dont le Skin en question a été obtenu dans le fichier

            resetLastUpdate(); // On réinitialise la dernière mise à jour du Skin.

            config.set("lastUpdate", this.lastUpdate); // On ajoute la dernière mise à jour du Skin en question dans le fichier

            String textureURL = null; // variable permettant de récupérer l'URL de la texture du Skin

            // ⬇️ On essaie de récupérer en ligne l'Identifiant de la Texture du Skin, sinon, on affiche une erreur ⬇️ //
            try {

                byte[] decodedBytes = Base64.getDecoder().decode(this.texture); // On récupère les bits décodés de la texture en question
                String decodedTexture = new String(decodedBytes); // On a converti les bits décodés en chaîne de caractère décodé

                // On récupère le JSON depuis la chaîne de caractère décodé
                JsonObject textureJSON = JsonParser.parseString(decodedTexture).getAsJsonObject();

                // On récupère le JSON de la Texture le JSON de la chaîne de caractère décodé
                JsonObject textureElement = textureJSON.get("textures").getAsJsonObject();

                // On récupère alors l'URL de la Texture du Skin à partir de la valeur 'url' du JSON Skin étant enregistré dans le JSON de la Texture
                textureURL = textureElement.get("SKIN").getAsJsonObject().get("url").getAsString();

                // ⬇️ Si l'URL de la Texture n'est pas nulle, on enregistre alors l'Identifiant de la Texture du Skin récupéré dans les serveurs Minecraft ⬇️ //
                if(textureURL != null) {

                    // On récupère donc l'Identifiant de la Texture depuis l'URL de la Texture du Skin
                    String textureID = textureURL.replaceFirst("http://textures.minecraft.net/texture/", "");
                    config.set("texture.id", textureID); // On ajoute l'Identifiant de la Texture du Skin en question dans le fichier
                    this.textureID = textureID; // On définit l'Identifiant de la Texture du Skin en question par celle récupérée
                }
                // ⬆️ Si l'URL de la Texture n'est pas nulle, on enregistre alors l'Identifiant de la Texture du Skin récupéré dans les serveurs Minecraft ⬆️ //

            } catch(Exception e) { NPCUtils.printError(e); }
            // ⬆️ On essaie de récupérer en ligne l'Identifiant de la Texture du Skin, sinon, on affiche une erreur ⬆️ //

            config.save(file); // On sauvegarde le fichier en question

            if(textureURL == null) return; // On sort si l'URL de la texture du Skin est nulle

            final String urlSkin = textureURL; // On réattribue dans une nouvelle variable l'URL de la texture du Skin

            // ⬇️ On essaie enregistre l'image récupérée depuis l'URL de la texture du Skin dans son fichier correspondant ⬇️ //
            Bukkit.getScheduler().runTaskAsynchronously(UtilityMain.getInstance(), () -> {

                // ⬇️ On essaie enregistre l'image récupérée depuis l'URL de la texture du Skin dans son fichier correspondant, sinon on affiche une erreur ⬇️ //
                try {

                    URL url = new URL(urlSkin); // On récupère une instance de l'URL avec la variable récupérant l'URL de la texture du Skin
                    BufferedImage bufferedImage = ImageIO.read(url); // On récupère l'image depuis cette URL
                    getTextureFile().mkdirs(); // On crée le répertoire du fichier en question
                    ImageIO.write(bufferedImage, "png", getTextureFile()); // On enregistre l'image récupérée dans le fichier

                } catch(Exception e) { NPCUtils.printError(e); /* Une exception est survenue, on affiche donc l'erreur de l'exception */ }
            });
            // ⬆️ On essaie enregistre l'image récupérée depuis l'URL de la texture du Skin dans son fichier correspondant, sinon on affiche une erreur ⬆️ //
        }

        /**
         * Récupère une liste de suggestion de nom de Skins
         *
         * @return une liste de suggestion de nom de Skins
         */
        public static List<String> getSuggestedSkinNames() {

            List<String> suggested = new ArrayList<>(); // Une liste récupérant une liste de suggestion de nom de Skins

            // On ajoute à la liste le nom de tous les joueurs en ligne
            Bukkit.getServer().getOnlinePlayers().forEach(x-> suggested.add(x.getName().toLowerCase()));

            // On ajoute à la liste également le nom de tous les Skins en cache, si elle n'y est pas encore
            Skin.SKIN_CACHE.keySet().stream().filter(x -> !suggested.contains(x)).forEach(x-> suggested.add(SKIN_CACHE.get(x).getPlayerName().toLowerCase()));

            // On ajoute à la liste également le nom locaux des Skins enregistrés, si elle n'y est pas encore
            Skin.LOCAL_SKIN_NAMES.stream().filter(x -> !suggested.contains(x)).forEach(x-> suggested.add(x.toLowerCase()));

            return suggested; // On retourne la liste de suggestion de nom de Skins
        }


        /**
         * Récupère le Profil de Jeux de Skin associé à un joueur précis ou le Profil de Jeux d'un Steve en cas d'exception.
         *
         * @param player Le joueur en question
         *
         * @return le Profil de Jeux associé à un joueur précis ou le Profil de Jeux d'un Steve en cas d'exception
         */
        private static String[] getSkinGameProfile(Player player) {

            /* ⬇️ On essaie de récupérer le Profil de Jeux de Skin du joueur pour la retourner,
               sinon on affiche une erreur et on retourne le Profil de Jeux d'un Steve ⬇️ */
            try {

                ServerPlayer p = NMSCraftPlayer.getEntityPlayer(player); // On récupère l'Entité du joueur récupéré
                GameProfile profile = NMSEntityPlayer.getGameProfile(p); // On récupère ensuite son Profil de Jeux
                Property property = profile.getProperties().get("textures").iterator().next(); // On récupère la propriété de son Skin
                String texture = property.getValue(); // On récupère sa Texture
                String signature = property.getSignature(); // On récupère sa signature

                return new String[]{ texture, signature }; // On retourne alors les données du Skin depuis le Profil de Jeux du joueur

            } catch(Exception e) {

                NPCUtils.printError(e); // Une exception s'est produite, on affiche l'erreur
                return NPC.Skin.getSteveSkin().getTextureData(); // On retourne alors les données du Skin depuis le Profil de Jeux d'un Steve
            }
            /* ⬆️ On essaie de récupérer le Profil de Skin de Jeux du joueur pour la retourner,
              sinon on affiche une erreur et on retourne le Profil de Jeux d'un Steve ⬆️ */
        }

        /**
         * Récupère le Profil de Jeux à partir d'une UUID depuis le serveur de Mojang.
         *
         * @param uuid L'UUID en question
         *
         * @return le Profil de Jeux associé à une UUID précis
         */
        private static HashMap<String, String> getProfileMojangServer(String uuid) throws IOException {

            HashMap<String, String> data = new HashMap<>(); // Variable récupérant les données du Profil de Jeux souhaité

            // On récupère l'URL du serveur de mojang pour effectuer la recherche
            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream()); // On définit l'URL pouvant être lue.

            JsonObject profile = JsonParser.parseReader(reader2).getAsJsonObject(); // On récupère le JSON récupéré depuis l'URL pouvant être lue
            JsonObject property = profile.get("properties").getAsJsonArray().get(0).getAsJsonObject();  // On récupère la catégorie 'properties' récupéré depuis le JSON

            // On ajoute à la variable récupérant les données du Profil de Jeux souhaité depuis la catégorie 'properties' l'UUID du Profil de Jeux
            data.put("id", profile.get("id").getAsString());

            // On ajoute à la variable récupérant les données du Profil de Jeux souhaité depuis la catégorie 'properties' le Pseudonyme du Profil de Jeux
            data.put("name", profile.get("name").getAsString());

            // On ajoute à la variable récupérant les données du Profil de Jeux souhaité depuis la catégorie 'properties' la Texture du Skin du Profil de Jeux
            data.put("texture.value", property.get("value").getAsString());

            // On ajoute à la variable récupérant les données du Profil de Jeux souhaité depuis la catégorie 'properties' la Signature du Skin du Profil de Jeux
            data.put("texture.signature", property.get("signature").getAsString());

            return data; // On retourne les données du Profil de Jeux récupéré
        }

        /**
         * Récupère le Skin par défaut Steve
         *
         * @return le Skin par défaut Steve
         */
        public static Skin getSteveSkin() { return STEVE; }

        /**
         * Récupère le Skin par défaut Alex
         *
         * @return le Skin par défaut Alex
         */
        public static Skin getAlexSkin() { return ALEX; }


        /* -------------------------------------------------- */


        /****************************************************/
        /* ÉNUMÉRATION POUR LES DIFFÉRENTES PARTIES DU SKIN */
        /****************************************************/

        public enum Part { CAPE, JACKET, LEFT_SLEEVE, RIGHT_SLEEVE, LEFT_PANTS, RIGHT_PANTS, HAT, }

        /****************************************************/
        /* ÉNUMÉRATION POUR LES DIFFÉRENTES PARTIES DU SKIN */
        /****************************************************/

        /* --------------------------------- */

        /**
         * LES DIFFÉRENTES PARTIES DU SKIN (BRAS ; JAMBES ; TÊTE ; TORSE ; CAPE)
         *
         * @since 2022.2
         */
        public static class Parts implements Cloneable {

            private final HashMap<Part, Boolean> parts; // Variable stockant les différentes parties du Skin

            /**
             * On instancie {@link Parts}.
             *
             */
            protected Parts() {

                this.parts = new HashMap<>(); // On initialise la variable stockant les différentes parties du Skin
                enableAll(); // On ajoute toutes les parties au Skin
            }

            /**
             * Ajoute toutes les {@link Parts parties} au Skin.
             *
             */
            public void enableAll() { Arrays.stream(Part.values()).forEach(x-> parts.put(x, true)); }

            /**
             * Enlève toutes les {@link Parts parties} au Skin.
             *
             */
            public void disableAll() { Arrays.stream(Part.values()).forEach(x-> parts.put(x, false)); }

            /**
             * Récupère toute les {@link Parts parties} visibles pour le Skin.
             *
             * @return Les {@link Parts parties} visibles pour le Skin
             */
            public List<Part> getVisibleParts() { return Arrays.stream(Part.values()).filter(this::isVisible).collect(Collectors.toList()); }

            /**
             * Récupère toute les {@link Parts parties} invisibles pour le Skin.
             *
             * @return Les {@link Parts parties} invisibles pour le Skin
             */
            public List<Part> getInvisibleParts() { return Arrays.stream(Part.values()).filter(x-> !isVisible(x)).collect(Collectors.toList()); }

            /**
             * Définit la visibilité de la {@link Parts partie} souhaité pour le Skin.
             *
             * @param part La {@link Part partie} en question
             * @param visible Doit-elle être visible ?
             *
             */
            public void setVisible(Part part, boolean visible) { parts.put(part, visible); }

            /**
             * Vérifie qu'elle {@link Parts partie} est visible pour le Skin.
             *
             * @param part La {@link Part partie} en question
             *
             * @return Une valeur Booléenne
             */
            public boolean isVisible(Part part) { return parts.get(part); }

            /**
             * Vérifie si la {@link Part Cape} est visible pour le Skin.
             *
             * @return Une valeur Booléenne
             */
            public boolean isCape() { return isVisible(Part.CAPE); }

            /**
             * Définit la visibilité de la {@link Part Cape} pour le Skin.
             *
             * @param cape La {@link Part Cape} doit-elle être visible ?
             *
             */
            public void setCape(boolean cape) { setVisible(Part.CAPE, cape); }

            /**
             * Vérifie si le {@link Part Torse} est visible pour le Skin.
             *
             * @return Une valeur Booléenne
             */
            public boolean isJacket() { return isVisible(Part.JACKET); }

            /**
             * Définit la visibilité du {@link Part Torse} pour le Skin.
             *
             * @param jacket Le {@link Part Torse} doit-elle être visible ?
             *
             */
            public void setJacket(boolean jacket) { setVisible(Part.JACKET, jacket); }

            /**
             * Vérifie si le {@link Part Bras Gauche} est visible pour le Skin.
             *
             * @return Une valeur Booléenne
             */
            public boolean isLeftSleeve() { return parts.get(Part.LEFT_SLEEVE); }

            /**
             * Définit la visibilité de le {@link Part Bras Gauche} pour le Skin.
             *
             * @param leftSleeve Le {@link Part Bras Gauche} doit-elle être visible ?
             *
             */
            public void setLeftSleeve(boolean leftSleeve) { setVisible(Part.LEFT_SLEEVE, leftSleeve); }

            /**
             * Vérifie si le {@link Part Bras Droit} est visible pour le Skin.
             *
             * @return Une valeur Booléenne
             */
            public boolean isRightSleeve() { return isVisible(Part.RIGHT_SLEEVE); }

            /**
             * Définit la visibilité de le {@link Part Bras Droit} pour le Skin.
             *
             * @param rightSleeve Le {@link Part Bras Droit} doit-elle être visible ?
             *
             */
            public void setRightSleeve(boolean rightSleeve) { setVisible(Part.RIGHT_SLEEVE, rightSleeve); }

            /**
             * Vérifie si la {@link Part Jambe Gauche} est visible pour le Skin.
             *
             * @return Une valeur Booléenne
             */
            public boolean isLeftPants() { return isVisible(Part.LEFT_PANTS); }

            /**
             * Définit la visibilité de la {@link Part Jambe Gauche} pour le Skin.
             *
             * @param leftPants La {@link Part Jambe Gauche} doit-elle être visible ?
             *
             */
            public void setLeftPants(boolean leftPants) { setVisible(Part.LEFT_PANTS, leftPants); }

            /**
             * Vérifie si la {@link Part Jambe Droite} est visible pour le Skin.
             *
             * @return Une valeur Booléenne
             */
            public boolean isRightPants() { return isVisible(Part.RIGHT_PANTS); }

            /**
             * Définit la visibilité de la {@link Part Jambe Droite} pour le Skin.
             *
             * @param rightPants La {@link Part Jambe Droite} doit-elle être visible ?
             *
             */
            public void setRightPants(boolean rightPants) { setVisible(Part.RIGHT_PANTS, rightPants); }

            /**
             * Vérifie si la {@link Part tête} est visible pour le Skin.
             *T
             * @return Une valeur Booléenne
             */
            public boolean isHat() { return isVisible(Part.HAT); }

            /**
             * Définit la visibilité de la {@link Part Tête} pour le Skin.
             *
             * @param hat La {@link Part Tête} doit-elle être visible ?
             *
             */
            public void setHat(boolean hat) { setVisible(Part.HAT, hat);  }

            /**
             * On récupère un clone des parties du Skin du NPC.
             *
             * @return un clone des parties du Skin du NPC
             */
            @Override
            public NPC.Skin.Parts clone() {

                NPC.Skin.Parts parts = new NPC.Skin.Parts(); // On instancie la class récupérant les parties du Skin
                Arrays.stream(Part.values()).forEach(x-> parts.setVisible(x, isVisible(x))); // On définit la visibilité de chaque partie
                return parts; // On retourne le clone
            }

        }

    }

    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */

    /**
     * MOUVEMENTS POUR LE NPC
     *
     * @since 2022.2
     */
    public static class Move {

        /**
         * Constructeur de la 'class' {@link Move}
         */
        private Move() {}

        /* -------------------------------------------------- */


        /*********************************************/
        /* ÉNUMÉRATION POUR LA VITESSE DU MOUVEMENT */
        /*******************************************/
        public enum Speed {

            SLOW(0.1),
            NORMAL(0.15),
            SPRINT(0.2);

            private final double speed;

            Speed(double speed) { this.speed = speed; }

            public double doubleValue() { return speed; }
        }
        /*********************************************/
        /* ÉNUMÉRATION POUR LA VITESSE DU MOUVEMENT */
        /*******************************************/


        /* --------------------------------- */

        /**
         * LE CHEMIN DU MOUVEMENT
         *
         */
        protected static class Path {


            private final NPC npc; // Variable récupérant le NPC qui effectuera le mouvement
            private Location start; // Variable récupérant la localisation de départ
            private final List<Location> locations; // Variable récupérant la différente localisation pour effectuer le chemin
            private int actual; // Variable récupérant l'itération du stade de mouvement du NPC
            private final Type type; // Variable récupérant le type de mouvement

            /**
             * On instancie un nouveau {@link Path Chemin}.
             *
             * @param npc Le NPC qui effectuera le {@link Path Chemin}
             * @param locations Une liste de {@link Location localisation} qu'empruntera le NPC
             * @param type Le {@link Type Type} de mouvement
             */
            private Path(NPC npc, List<Location> locations, Type type) {

                this.npc = npc; // On Initialise le NPC
                this.locations = locations; // On Initialise tous les localisations
                this.type = type; // On Initialise le type de mouvement
                this.actual = -1; // On Initialise l'itération du stade de mouvement du NPC
            }

            /**
             * Permet de démarrer le chemin du NPC.
             *
             * @return L'Objet {@link Path}
             */
            public Path start() {

                this.start = npc.getLocation();
                next();

                return this;
            }

            /**
             * Vérifie la nouvelle {@link Location localisation} du NPC pour vérifier si le NPC a atteint la fin.
             *
             */
            private void next() {

                actual++; // On effectue une itération à la variable récupérant l'itération du stade de mouvement du NPC à chaque mouvement

                // ⬇️ Si l'itération est inférieure à la taille de localisation, on continue le chemin ⬇️ //
                if(actual >= locations.size()) {

                    // Si le type de mouvement est un aller-retour et que l'itération est égale à la taille de localisation, le NPC effectuera donc le sens inverse
                    if(type.isBackToStart() && actual == locations.size()) { go(start); return; }
                    else finish(); // Sinon, on arrête le mouvement du NPC
                }
                // ⬆️ Si l'itération est inférieure à la taille de localisation, on continue le chemin ⬆️ //

                go(locations.get(actual)); // On continue le chemin pour NPC vers la localisation suivante récupéré en fonction de l'itération
            }

            /**
             * Arrête le mouvement du NPC.
             *
             */
            private void finish() {

                // Si le type de mouvement est répétitif, on réattribue l'itération à '-1' et on recommence le chemin et on sort de la méthode avec le 'return'
                if(type.isRepetitive()) { actual = -1; start(); return; }
                npc.getMoveBehaviour().cancel(); // On annule le mouvement du NPC
            }

            /**
             * Permet de déplacer le NPC vers une {@link Location localisation} précise.
             *
             * @param location la {@link Location localisation} en question à déplacer
             */
            private void go(Location location) {

                if(npc.moveTask == null) npc.goTo(location);
                else npc.moveTask.end = location;
            }

            /* --------------------------------- */

            /******************************************/
            /* ÉNUMÉRATION POUR LE TYPE DE MOUVEMENT */
            /****************************************/
            public enum Type {

                NORMAL(false, false),
                REPETITIVE(true, false),
                BACK_TO_START(false, true);

                private final boolean repetitive;
                private final boolean backToStart;

                Type(boolean repetitive, boolean backToStart) {

                    this.repetitive = repetitive;
                    this.backToStart = backToStart;
                }

                public boolean isRepetitive() { return repetitive; }

                public boolean isBackToStart() { return backToStart; }
            }
            /******************************************/
            /* ÉNUMÉRATION POUR LE TYPE DE MOUVEMENT */
            /****************************************/

        }

        /* --------------------------------- */

        /**
         * LE COMPORTEMENT DE SUIVIE DU MOUVEMENT
         *
         * @since 2022.2
         */
        public static class Behaviour {

            private final NPC npc; // Variable récupérant le NPC qui effectuera le mouvement
            private NPC.Move.Behaviour.Type type; // Variable récupérant le type de suivie
            private Integer taskID; // Variable récupérant l'identifiant de la tâche actuel

            private Double followMinDistance; // Variable récupérant la distance minimale de suivie
            private Double followMaxDistance; // Variable récupérant la distance maximale de suivie
            private Entity followEntity; // Variable récupérant l'Entité à suivre
            private NPC followNPC; // Variable récupérant le NPC à suivre
            private NPC.Move.Path path; // Variable récupérant le chemin de mouvement du NPC

            /**
             * On instancie un nouveau {@link Behaviour comportement de suivie}.
             *
             * @param npc Le NPC qui effectuera le {@link Behaviour comportement de suivie}
             */
            protected Behaviour(NPC npc) {

                this.npc = npc; // On initialise le NPC
                this.type = Type.NONE; // On initialise le type de suivie
                this.taskID = null; // On initialise l'identifiant de la tâche actuel

                this.followMinDistance = 5.0; // On initialise la distance minimale de suivie
                this.followMaxDistance = 50.0; // On initialise la distance maximale de suivie
                this.followEntity = null; // On initialise l'Entité à suivre
                this.followNPC = null; // On initialise le NPC à suivre
                this.path = null; // On initialise le chemin de mouvement du NPC
            }

            /**
             * Déplace le NPC en vérifiant pour chaque tick le type de suivie et l'{@link Entity Entité}, du {@link Player Joueur} ou du {@link NPC} à suivre.
             *
             */
            protected void tick() {

                // Si le type de suivie est null ou à un chemin customisé, on ne fait rien.
                if(type == null || type.equals(Type.NONE) || type.equals(Type.CUSTOM_PATH)) return;

                /* ⬇️ Si le type de suivie est une Entité, un Joueur, ou un NPC, alors,
                   ainsi, on récupère l'Entité en question, le Joueur en question ou le NPC en question pour ensuite effectuer le déplacement ⬇️ */
                if(type.equals(Type.FOLLOW_ENTITY) || type.equals(Type.FOLLOW_PLAYER) || type.equals(Type.FOLLOW_NPC)) {

                    if(type.equals(Type.FOLLOW_ENTITY) && followEntity == null) return; // Si le type est une Entité et que l'Entité en question est null, on ne fait rien
                    if(type.equals(Type.FOLLOW_NPC) && followNPC == null) return; // Si le type est une NPC et que le NPC en question est null, on ne fait rien
                    Location target = getLocation(); // Récupère la localisation de l'entité du NPC

                    // Si la localisation cible du NPC est null alors, on ne fait rien
                    if(target == null) return;

                    // Si le monde de la localisation cible du NPC n'est pas égal à la localisation de NPC lui-même, on téléporte le NPC à la localisation cible et on sort
                    if(!target.getWorld().equals(npc.getWorld())) { npc.teleport(target); return; }

                    // Si la tâche de déplacement actuel du NPC est null (il ne se déplace pas), on envoie le NPC à la localisation cible définit et on sort.
                    if(npc.getMoveTask() == null) { npc.goTo(target, true); return; }

                    /* Si la distance entre la localisation cible et la localisation actuelle du NPC est inférieur la distance minimale de suivie,
                       on met le déplacement du NPC en pause et on sort */
                    if(target.distance(npc.getLocation()) <= followMinDistance) { npc.getMoveTask().pause(); return; }

                    /* Si la distance entre la localisation cible et la localisation actuelle du NPC est supérieur la distance maximale de suivie,
                       on téléporte le NPC à la localisation cible */
                    if(target.distance(npc.getLocation()) >= followMaxDistance) { npc.teleport(target); return; }

                    // Si la tâche de déplacement actuel du NPC est en pause, alors on reprend la tâche de déplacement actuel du NPC
                    if(npc.getMoveTask().isPaused()) npc.getMoveTask().resume();
                    npc.getMoveTask().end = target; // On définit fin de la tâche de déplacement actuel du NPC par la localisation cible du NPC
                }
                /* ⬆️ Si le type de suivie est une Entité, un Joueur, ou un NPC, alors,
                   ainsi, on récupère l'Entité en question, le Joueur en question ou le NPC en question pour ensuite effectuer le déplacement ⬆️ */
            }

            /********************************************************/
            /********************************************************/

            @Nullable
            private Location getLocation() {

                Location targetLocation = null; // Variable permettant de définir la localisation cible du NPC

                /**********************/

                // Si le type est une Entité, on définit la localisation cible du NPC à la localisation où se trouve cette Entité
                if(type.equals(Type.FOLLOW_ENTITY)) targetLocation = followEntity.getLocation();

                    /* Si le type est un Joueur et que le Joueur est celui associé au NPC alors,
                       on définit la localisation cible du NPC à la localisation où se trouve ce Joueur */
                if(type.equals(Type.FOLLOW_PLAYER) && npc instanceof NPCPersonal) targetLocation = ((NPCPersonal)npc).getPlayer().getLocation();

                // Si le type est autre NPC alors, on définit la localisation cible du NPC à la localisation où se trouve l'autre NPC
                if(type.equals(Type.FOLLOW_NPC)) targetLocation = followNPC.getLocation();

                /**********************/

                return targetLocation; // Renvoie la localisation cible
            }

            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

            /**
             * Définit le type de suivie étant le chemin à faire.
             *
             * @param locations Une liste de {@link Location localisation} qu'empruntera le NPC
             * @param type Le {@link Type Type} de mouvement
             *
             * @return L'Objet {@link Path}
             */
            public Move.Path setPath(List<Location> locations, Move.Path.Type type) {

                setType(Type.CUSTOM_PATH);
                this.path = new Path(npc, locations, type);
                return this.path;
            }

            /**
             * Définit l'{@link Entity Entité} auquel le NPC va suivre.
             *
             * @param entity L'{@link Entity Entité} à suivre
             *
             * @return L'Objet {@link Behaviour}
             */
            public Move.Behaviour setFollowEntity(Entity entity) { return setFollowEntity(entity, followMinDistance, followMaxDistance); }

            /**
             * Définit l'{@link Entity Entité} auquel le NPC va suivre en définissant une distance minimale de suivie.
             *
             * @param entity L'{@link Entity Entité} à suivre
             * @param followMinDistance La distance minimale de suivie
             *
             * @return L'Objet {@link Behaviour}
             */
            public Move.Behaviour setFollowEntity(Entity entity, double followMinDistance) { return setFollowEntity(entity, followMinDistance, followMaxDistance); }

            /**
             * Définit l'{@link Entity Entité} auquel le NPC va suivre en définissant une distance minimale et maximale de suivie.
             *
             * @param entity L'{@link Entity Entité} à suivre
             * @param followMinDistance La distance minimale de suivie
             * @param followMaxDistance La distance maximale de suivie
             *
             * @return L'Objet {@link Behaviour}
             */
            public Move.Behaviour setFollowEntity(Entity entity, double followMinDistance, double followMaxDistance) {

                setType(Type.FOLLOW_ENTITY);
                this.followEntity = entity;
                this.followMinDistance = followMinDistance;
                this.followMaxDistance = followMaxDistance;
                return this;
            }

            /**
             * Définit le {@link NPC} auquel le NPC va suivre.
             *
             * @param npc Le {@link NPC} à suivre
             *
             * @return L'Objet {@link Behaviour}
             */
            public Move.Behaviour setFollowNPC(NPC npc) { return setFollowNPC(npc, followMinDistance, followMaxDistance); }

            /**
             * On définit le {@link NPC} auquel le NPC va suivre en définissant une distance minimale de suivie.
             *
             * @param npc le {@link NPC} à suivre
             * @param followMinDistance La distance minimale de suivie
             *
             * @return L'Objet {@link Behaviour}
             */
            public Move.Behaviour setFollowNPC(NPC npc, double followMinDistance) { return setFollowNPC(npc, followMinDistance, followMaxDistance); }

            /**
             * Définit le {@link NPC} auquel le NPC va suivre en définissant une distance minimale et maximale de suivie.
             *
             * @param npc Le {@link NPC} à suivre
             * @param followMinDistance La distance minimale de suivie
             * @param followMaxDistance La distance maximale de suivie
             *
             * @return L'Objet {@link Behaviour}
             */
            public Move.Behaviour setFollowNPC(NPC npc, double followMinDistance, double followMaxDistance) {

                if(npc.equals(this.npc)) return this;
                if(npc instanceof NPCPersonal && this.npc instanceof NPCGlobal) return this;
                if(npc instanceof NPCPersonal && this.npc instanceof NPCPersonal && !((NPCPersonal) npc).getPlayer().equals(((NPCPersonal) this.npc).getPlayer())) return this;
                setType(Type.FOLLOW_NPC);
                this.followNPC = npc;
                this.followMinDistance = followMinDistance;
                this.followMaxDistance = followMaxDistance;
                return this;
            }

            /**
             * Le NPC va suivre son Joueur associé.
             *
             *
             * @return L'Objet {@link Behaviour}
             */
            public Move.Behaviour setFollowPlayer() { return setFollowPlayer(followMinDistance, followMaxDistance); }

            /**
             * Le NPC va suivre son Joueur associé en définissant une distance minimale et maximale de suivie.
             *
             * @param followMinDistance La distance minimale de suivie
             * @param followMaxDistance La distance maximale de suivie
             *
             * @return L'Objet {@link Behaviour}
             */
            public Move.Behaviour setFollowPlayer(double followMinDistance, double followMaxDistance) {

                setType(Type.FOLLOW_PLAYER);
                this.followMinDistance = followMinDistance;
                this.followMaxDistance = followMaxDistance;
                return this;
            }

            /**
             * On annule le type de suivie du NPC
             *
             * @return L'Objet {@link Behaviour}
             */
            public Move.Behaviour cancel() { return setType(Type.NONE); }

            /**
             * On démarre le temps pour déplacer le NPC
             */
            private void startTimer() {

                if(taskID != null) return;
                taskID = Bukkit.getScheduler().runTaskTimer(UtilityMain.getInstance(), this::tick, 20L, 20L).getTaskId();
            }

            /**
             * On finit le temps de déplacement du NPC
             */
            private void finishTimer() {

                if(taskID == null) return;
                Bukkit.getScheduler().cancelTask(taskID);
            }

            /**
             * Définit le type de suivie du NPC
             *
             * @return L'Objet {@link Behaviour}
             */
            private Move.Behaviour setType(Type type) {

                this.type = type;
                if(type == Type.NONE) finishTimer();
                else startTimer();
                return this;
            }

            /**
             * Récupère le type de suivie du NPC
             *
             * @return L'Objet {@link Behaviour}
             */
            public Type getType() { return type; }

            /**
             * Récupère le NPC qui se déplace
             *
             * @return Le NPC en question
             */
            public NPC getNPC() { return npc; }

            /* --------------------------------- */

            /**************************************/
            /* ÉNUMÉRATION POUR LE TYPE DE SUIVIE */
            /*************************************/

            public enum Type { NONE, FOLLOW_PLAYER, FOLLOW_ENTITY, FOLLOW_NPC, CUSTOM_PATH }

            /**************************************/
            /* ÉNUMÉRATION POUR LE TYPE DE SUIVIE */
            /*************************************/
        }

        /* --------------------------------- */

        /**
         * LES TÂCHES DE MOUVEMENT
         *
         * @since 2022.2
         */
        public static class Task {

            private final NPC npc; // Variable récupérant le NPC qui effectuera le mouvement
            private Integer taskID; // Variable récupérant l'identifiant de la tâche actuel
            private Location start; // Variable récupérant la localisation de départ
            private Location end; // Variable récupérant la localisation d'arrivée
            private boolean pause; // Variable vérifiant si le tâche est en pause
            private final boolean lookToEnd; // Variable vérifiant si le NPC doit regarder sa localisation d'arrivée pendant son déplacement
            private GazeTrackingType lastGazeTrackingType;  // Variable récupérant le type de suivie du regard du NPC pendant son déplacement
            private Pose lastNPCPose; // Variable récupérant la dernière posture du NPC
            private boolean checkSwimming; // Variable vérifiant si pendant le déplacement, on effectue une vérification du NPC étant en Nage
            private boolean checkSlabCrouching; // Variable vérifiant si pendant le déplacement, on effectue une vérification du NPC étant Accroupie sur une Dalle
            private boolean checkSlowness;  // Variable vérifiant si pendant le déplacement, on effectue une vérification du NPC étant Lent
            private boolean checkLadders; // // Variable vérifiant si pendant le déplacement, on effectue une vérification du NPC étant en Montée

            /**
             * On instancie une nouvelle {@link Task tâche de mouvement}.
             *
             * @param npc Le NPC qui effectuera la {@link Task tâche de mouvement}
             * @param end La {@link Location localisation} de fin du mouvement du NPC
             * @param lookToEnd Le NPC doit-il regarder ça {@link Location localisation} de fin pendant son déplacement
             */
            private Task(NPC npc, Location end, boolean lookToEnd) {

                this.npc = npc; // On initialise le NPC
                this.end = end; // On initialise la localisation d'arrivée
                this.lookToEnd = lookToEnd; // On initialise si le NPC doit regarder localisation d'arrivée pendant son déplacement

                this.pause = false; // La tache ne sera plus en pause.

                setPerformanceOptions(PerformanceOptions.ALL); // On définit tous les options de performances pour le déplacement du NPC.
            }

            /**
             * Démarre la {@link Task tâche} de mouvement pour le NPC
             *
             * @return La {@link Task tâche} actuelle
             */
            protected Task start() {

                start = npc.getLocation(); // On vérifie la localisation de départ du NPC

                this.lastGazeTrackingType = npc.getGazeTrackingType(); // On récupère le type de suivie du mouvement du NPC
                this.lastNPCPose = npc.getPose(); // On récupère la dernière posture du NPC

                // Si le NPC doit regarder sa localisation d'arrivée pendant son déplacement, on ne définit rien sur le type de suivie du regard du NPC
                if(lookToEnd) npc.setGazeTrackingType(NPC.GazeTrackingType.NONE);

                // ⬇️ On définit l'Identifiant de la tâche actuel par l'identifiant de la tâche en cours ⬇️ //
                taskID = Bukkit.getScheduler().runTaskTimer(UtilityMain.getInstance(), ()-> {

                    if(pause) return; // Si une pause a été effectuée à la tâche actuelle, on ne fait rien
                    tick(); // On effectue le déplacement du NPC

                },1 ,1).getTaskId();
                // ⬆️ On définit l'Identifiant de la tâche actuel par l'identifiant de la tâche en cours ⬆️ //

                // On définit un évènement pour le NPC de départ de son mouvement
                NPC.Events.StartMove npcStartMoveEvent = new NPC.Events.StartMove(npc, start, end, taskID);

                // Si l'évènement a été annulé
                if(npcStartMoveEvent.isCancelled()) {

                    cancel(NPC.Move.Task.CancelCause.CANCELLED); // On annule la tâche en précisant que c'est l'évènement qui a été annulé
                    npc.teleport(start); // On téléporte le NPC à sa localisation de départ.
                }

                return this; // On retourne l'instance en question
            }


            /**
             * Déplace le NPC en vérifiant pour chaque tick le mouvement du NPC.
             *
             */
            private void tick() {

                // ⬇️ Si le NPC est une instance d'un NPC d'un joueur unique et que le NPC n'a pas encore été créer, on annule la tâche en précisant qu'il y a une erreur ⬇️ //
                if(npc instanceof NPCPersonal && !((NPCPersonal) npc).isCreated()) {

                    cancel(CancelCause.ERROR);
                    return;
                }
                // ⬆️ Si le NPC est une instance d'un NPC d'un joueur unique et que le NPC n'a pas encore été créer, on annule la tâche en précisant qu'il y a une erreur ⬆️ //

                /*  ⬇️ Si la coordonnée 'X' du NPC est égal à la coordonnée 'X' de la localisation d'arrivée et de même pour la coordonnée 'Z',
                   on annule la tâche en précisant que c'est un succès ⬇️ */
                if(npc.getX().equals(end.getX()) && npc.getZ().equals(end.getZ())) {

                    cancel(NPC.Move.Task.CancelCause.SUCCESS);
                    return;
                }
                /* ⬆️ Si la coordonnée 'X' du NPC est égal à la coordonnée 'X' de la localisation d'arrivée et de même pour la coordonnée 'Z',
                   on annule la tâche en précisant que c'est un succès ⬆️ */

                double moveX, moveY, moveZ; // Permettra de récupérer les coordonnées de mouvement du NPC

                // On définit la coordonnée de mouvement 'X' du NPC par la différente entre la coordonnée 'X' du NPC et la coordonnée 'X' de la localisation d'arrivée
                moveX = compareCoordinate(npc.getX(), end.getX());

                // On définit la coordonnée de mouvement 'Z' du NPC par la différente entre la coordonnée 'X' du NPC et la coordonnée 'Z' de la localisation d'arrivée
                moveZ = compareCoordinate(npc.getZ(), end.getZ());

                /* ⬇️ Si la coordonnée de mouvement 'X' et 'Z' du NPC sont égales à 0, on récupère la coordonnée de mouvement 'X' et 'Z' du NPC divisé par 1.7
                   dans le cas si la valeur absolute de ses coordonnées sont inférieures à la vitesse de déplacement du NPC ⬇️ */
                if(moveX != 0.00 && moveZ != 0.00) {

                    if(Math.abs(moveX) > npc.getMoveSpeed() / 1.7) moveX = moveX / 1.7;
                    if(Math.abs(moveZ) > npc.getMoveSpeed() / 1.7) moveZ = moveZ / 1.7;
                }
                /* ⬆️ Si la coordonnée de mouvement 'X' et 'Z' du NPC sont égales à 0, on récupère la coordonnée de mouvement 'X' et 'Z' du NPC divisé par 1.7
                   dans le cas si la valeur absolute de ses coordonnées sont inférieures à la vitesse de déplacement du NPC ⬆️ */

                Location from = newLocation(npc.getX(), npc.getY() + 0.1, npc.getZ()); // On récupère la localisation actuelle NPC déplacé
                Location to = newLocation(npc.getX() + moveX, npc.getY() + 0.1, npc.getZ() + moveZ); // On récupère la localisation où le NPC doit se déplacer
                double locY = npc.getY(); // On récupère la coordonnée 'Y' du NPC

                // On récupère le bloc de la localisation actuelle où se situe le NPC
                Block blockInLegFrom = from.getBlock();

                // On récupère le bloc sous les pieds du NPC à partir du bloc de la localisation actuelle où se situe le NPC
                Block blockInFootFrom = blockInLegFrom.getRelative(BlockFace.DOWN);

                // On récupère le bloc de la localisation où le NPC doit se déplacer
                Block blockInLeg = to.getBlock();

                // On récupère le bloc sous ses pieds à partir du bloc de la localisation où le NPC doit se déplacer
                Block blockInFoot = blockInLeg.getRelative(BlockFace.DOWN);

                boolean uppingLadder = false; // On définit la montée du NPC sur faux.
                boolean falling = false; // On définit la chute du NPC sur faux.
                boolean jumpingBlock = false; // On définit le saut du NPC sur faux.

                // Si le bloc de la localisation où le NPC doit se déplacer est un solide, ou que c'est un escalier, alors on essaie de faire sauter le NPC
                if(blockInLeg.getType().isSolid() || isStair(blockInLegFrom)) {

                    double jump = 1.0; // on définit le saut à une valeur de '1.0'

                    // Si le type de bloc est un escalier ou une dalle, on définit le saut à une valeur de '0.5'
                    if(isStair(blockInLeg) || isSlab(blockInLeg)) jump = 0.5;

                        /* Sinon, si le NPC est en montée et que le bloc de la localisation actuelle où se situe le NPC est un bloc d'escalade alors,
                       on définit la montée du NPC sur vrai. */
                    else if(checkLadders && isLadder(blockInLegFrom)) uppingLadder = true;

                    else jumpingBlock = true; // Sinon, on définit le saut du NPC sur vrai

                    locY = blockInLeg.getY() + jump; // On remplace alors la coordonnée 'Y' du NPC par la coordonnée du bloc + la valeur du saut.


                    // Sinon, on vérifie le bloc sous les pieds du NPC une fois déplacé
                } else {

                    // Si le bloc sous les pieds du NPC une fois déplacé est bien un solid
                    if(blockInFoot.getType().isSolid()) {

                        // ** AJUSTEMENT DE L'AIR ENTRE LE SOL ET LES PIEDS DU NPC ** //

                        // On vérifie si le type de bloc est une dalle, on remplace alors la coordonnée 'Y' du NPC par la coordonnée 'Y' du bloc + une valeur de '0.5'.
                        if(isSlab(blockInFoot)) locY = blockInFoot.getY() + 0.5;

                            // Sinon, on remplace alors la coordonnée 'Y' du NPC par la coordonnée 'Y' de la localisation du bloc actuel où se situe le NPC
                        else locY = blockInLeg.getY();

                        // ** AJUSTEMENT DE L'AIR ENTRE LE SOL ET LES PIEDS DU NPC ** //

                        // Sinon, il s'agit d'une chute
                    } else {

                        // ** AJUSTEMENT DE LA CHUTE ** //

                        /* ⬇️ Si le bloc sous les pieds du NPC à partir du bloc de sa localisation actuelle n'est pas solid alors, on remplace la coordonnée 'Y' du NPC
                           par la coordonnée 'Y' du bloc sous les pieds du NPC une fois déplacé moins la valeur '0.1' et on définit la chute du NPC sur vrai ⬇️ */
                        if(!blockInFootFrom.getType().isSolid()) {

                            // on remplace donc la coordonnée 'Y' du NPC par la coordonnée 'Y' du bloc sous les pieds du NPC une fois déplacé moins la valeur '0.1'
                            locY = blockInFoot.getY() - 0.1;

                            falling = true; // On définit donc la chute du NPC sur vrai
                        }
                        /* ⬆️ Si le bloc sous les pieds du NPC à partir du bloc de sa localisation actuelle n'est pas solid alors, on remplace la coordonnée 'Y' du NPC
                           par la coordonnée 'Y' du bloc sous les pieds du NPC une fois déplacé moins la valeur '0.1' et on définit la chute du NPC sur vrai ⬆️ */

                        // ** AJUSTEMENT DE LA CHUTE ** //
                    }
                }

                /* ⬇️ Si le NPC est en train de nager, on vérifie alors sa posture et le type de bloc de la localisation où le NPC doit se déplacer
                      et le type de bloc sous les pieds du NPC à partir du bloc de la localisation où le NPC doit se déplacer ⬇️ */
                if(checkSwimming) {

                    /* Si le type de bloc de la localisation où le NPC doit se déplacer et le type de bloc sous les pieds du NPC à partir du bloc de la localisation
                       où le NPC doit se déplacer est de l'eau, on vérifie alors sa posture */
                    if(blockInLeg.getType().equals(Material.WATER) && blockInFoot.getType().equals(Material.WATER)) {

                        // ⬇️ Si la posture du NPC n'est pas en nage, alors on lui met + on met à jour le NPC ⬇️ //
                        if(!npc.getPose().equals(Pose.SWIMMING)) {

                            npc.setSwimming(true); // Définit la posture de nage du NPC
                            npc.update(); // Met à jour le NPC
                        }
                        // ⬆️ Si la posture du NPC n'est pas en nage, alors on lui met + on met à jour le NPC ⬆️ //

                        // Sinon, si la posture du NPC est en nage, on vérifie la dernière posture du NPC + on met à jour le NPC
                    } else if(npc.getPose().equals(Pose.SWIMMING)) {

                        // Si la dernière posture du NPC était accroupie, on lui remet la posture accroupie
                        if(lastNPCPose.equals(Pose.CROUCHING)) npc.setPose(Pose.CROUCHING);

                            // Sinon, on définit la posture du NPC étant debout
                        else npc.setPose(Pose.STANDING);

                        npc.update(); // Met à jour le NPC
                    }
                }
                /* ⬆️ Si le NPC est en train de nager, on vérifie alors sa posture et le type de bloc de la localisation où le NPC doit se déplacer
                      et le type de bloc sous les pieds du NPC à partir du bloc de la localisation où le NPC doit se déplacer ⬆️ */


                // ⬇️ Si le NPC est accroupie sur une dalle, on vérifie alors sa posture et la face de la dalle que le bloc de la localisation où le NPC doit se déplacer ⬇️ //
                if(checkSlabCrouching) {

                    /* Si le type de bloc de la localisation où le NPC doit se déplacer est un solide étant une dalle partie haute et d'une face de haut ou de même,
                       mais une dalle partie basse alors, on vérifie la posture du NPC */
                    if((!blockInLeg.getType().isSolid() && isSlab(blockInLeg.getRelative(BlockFace.UP), Slab.Type.TOP))
                            || (isSlab(blockInLeg) && isSlab(blockInLeg.getRelative(BlockFace.UP).getRelative(BlockFace.UP), Slab.Type.BOTTOM))) {

                        // ⬇️ Si la posture du NPC n'est pas accroupie, alors on lui met + on met à jour le NPC ⬇️ //
                        if(!npc.getPose().equals(Pose.CROUCHING)) {

                            npc.setPose(Pose.CROUCHING); // Définit la posture du NPC étant accroupie
                            npc.update(); // Met à jour le NPC
                        }
                        // ⬆️ Si la posture du NPC n'est pas accroupie, alors on lui met + on met à jour le NPC ⬆️ //

                        // Sinon, on vérifie la posture du NPC étant accroupie
                    } else {

                        // ⬇️ Si la posture du NPC est accroupie et sa dernière position de même, on lui remet la posture debout + on met à jour le NPC ⬇️ //
                        if(npc.getPose().equals(Pose.CROUCHING) && lastNPCPose != Pose.CROUCHING) {

                            npc.setPose(Pose.STANDING); // Définit la posture du NPC étant debout
                            npc.update(); // Met à jour le NPC
                        }
                        // ⬆️ Si la posture du NPC est accroupie et sa dernière position de même, on lui remet la posture debout + on met à jour le NPC ⬆️ //
                    }
                }
                // ⬆️ Si le NPC est accroupie sur une dalle, on vérifie alors sa posture et la face de la dalle que le bloc de la localisation où le NPC doit se déplacer ⬆️ //


                /* ⬇️ Si le NPC est lent, on vérifie alors sa posture et le type de bloc de la localisation où le NPC doit se déplacer
                      et le type de bloc sous les pieds du NPC à partir du bloc de la localisation où le NPC doit se déplacer ⬇️ */
                if(checkSlowness) {

                    /* ⬇️ Si le type de bloc de la localisation où le NPC doit se déplacer est une toile d'araignée d'une face de haut,
                       on redéfinit la coordonnée de mouvement 'X' et 'Z' du NPC par celle-ci divisé par '4' ⬇️ */
                    if(blockInLeg.getType().equals(Material.COBWEB) || blockInLeg.getRelative(BlockFace.UP).getType().equals(Material.COBWEB)) {

                        moveX = moveX / 4;
                        moveZ = moveZ / 4;
                    }
                    /* ⬆️ Si le type de bloc de la localisation où le NPC doit se déplacer est une toile d'araignée d'une face de haut,
                       on redéfinit la coordonnée de mouvement 'X' et 'Z' du NPC par celle-ci divisé par '4' ⬆️ */


                    /* ⬇️ Si le type de bloc sous les pieds du NPC à partir du bloc de la localisation où le NPC doit se déplacer est du sable des âmes,
                       on redéfinit la coordonnée de mouvement 'X' et 'Z' du NPC par celle-ci divisé par '2' ⬇️ */
                    if(blockInFoot.getType().equals(Material.SOUL_SAND)) {

                        moveX = moveX / 2;
                        moveZ = moveZ / 2;
                    }
                    /* ⬆️ Si le type de bloc sous les pieds du NPC à partir du bloc de la localisation où le NPC doit se déplacer est du sable des âmes,
                       on redéfinit la coordonnée de mouvement 'X' et 'Z' du NPC par celle-ci divisé par '2' ⬆️ */
                }
                /* ⬆️ Si le NPC est lent, on vérifie alors sa posture et le type de bloc de la localisation où le NPC doit se déplacer
                      et le type de bloc sous les pieds du NPC à partir du bloc de la localisation où le NPC doit se déplacer ⬆️ */


                /* ⬇️ Si la posture du NPC est de la nage alors, on redéfinit la coordonnée de mouvement 'X' et 'Z' du NPC par celle-ci multiplié par '3'
                   et remplace donc la coordonnée 'Y' du NPC par la coordonnée 'Y' de la localisation du bloc actuel où se situe le NPC + une valeur de '0.5' ⬇️ */
                if(npc.getPose().equals(Pose.SWIMMING)) {

                    moveX = moveX * 3;
                    moveZ = moveZ * 3;
                    locY = blockInLeg.getY() + 0.5;
                }
                /* ⬆️ Si la posture du NPC est de la nage alors, on redéfinit la coordonnée de mouvement 'X' et 'Z' du NPC par celle-ci multiplié par '3'
                   et remplace donc la coordonnée 'Y' du NPC par la coordonnée 'Y' de la localisation du bloc actuel où se situe le NPC + une valeur de '0.5' ⬆️ */


                // ⬇️ Si la posture du NPC est accroupie, on redéfinit alors la coordonnée de mouvement 'X' et 'Z' du NPC par celle-ci divisé par '3' ⬇️ //
                if(npc.getPose().equals(Pose.CROUCHING)) {

                    moveX = moveX / 3;
                    moveZ = moveZ / 3;
                }
                // ⬆️ Si la posture du NPC est accroupie, on redéfinit alors la coordonnée de mouvement 'X' et 'Z' du NPC par celle-ci divisé par '3' ⬆️ //


                moveY = locY - npc.getY(); // On définit la coordonnée de mouvement 'Y' du NPC par la coordonnée 'Y' du NPC moins la coordonnée 'Y' actuel du NPC

                if(moveY > 1.0) moveY = 1.0; // Si la coordonnée de mouvement 'Y' du NPC est inférieur à une valeur de '1.0', on la redéfinit à '1.0'
                if(moveY < -1.0) moveY = -1.0; // Si la coordonnée de mouvement 'Y' du NPC est inférieur à une valeur de '-1.0', on la redéfinit à '-1.0'

                // ⬇️ Si le NPC est en pleine montée alors, on redéfinit la coordonnée de mouvement 'X' et 'Z' par '0.00', ainsi, on vérifie la coordonnée de mouvement 'Y' ⬇️ //
                if(uppingLadder) {

                    moveX = 0.00; // On redéfinit la coordonnée de mouvement 'X' par une valeur de '0.00'
                    moveZ = 0.00; // On redéfinit la coordonnée de mouvement 'Z' par une valeur de '0.00'

                    if(moveY > 0.01) moveY = 0.05; // On redéfinit la coordonnée de mouvement 'Y' par une valeur de '0.05' si celle-ci est supérieure à '0.01'
                    else if(moveY < -0.01) moveY = -0.05; // On redéfinit la coordonnée de mouvement 'Y' par une valeur de '-0.05' si celle-ci est inférieure à '-0.01'
                }
                // ⬆️ Si le NPC est en pleine montée alors, on redéfinit la coordonnée de mouvement 'X' et 'Z' par '0.00', ainsi, on vérifie la coordonnée de mouvement 'Y' ⬆️ //


                /* ⬇️ Si le NPC est en plein saut, on redéfinit donc la coordonnée de mouvement 'X' et 'Z' par celle-ci divisé par '2',
                   ainsi, on vérifie la coordonnée de mouvement 'Y' ⬇️ */
                if(jumpingBlock) {

                    moveX = moveX / 2; // On redéfinit la coordonnée de mouvement 'X' par celle-ci divisé par '2'
                    moveZ = moveZ / 2; // On redéfinit la coordonnée de mouvement 'X' par celle-ci divisé par '2'

                    if(moveY > 0.2) moveY = 0.2; // On redéfinit la coordonnée de mouvement 'Y' par une valeur de '0.2' si celle-ci est supérieure à '0.2'
                }
                /* ⬆️ Si le NPC est en plein saut, on redéfinit donc la coordonnée de mouvement 'X' et 'Z' par celle-ci divisé par '2',
                   ainsi, on vérifie la coordonnée de mouvement 'Y' ⬆️ */


                /* ⬇️ Si le NPC est en pleine chûte, on redéfinit donc la coordonnée de mouvement 'X' et 'Z' par celle-ci divisé par '4',
                   ainsi, on vérifie la coordonnée de mouvement 'Y' ⬇️ */
                if(falling) {

                    moveX = moveX / 4; // On redéfinit la coordonnée de mouvement 'X' par celle-ci divisé par '4'
                    moveZ = moveZ / 4; // On redéfinit la coordonnée de mouvement 'X' par celle-ci divisé par '4'

                    if(moveY < -0.4) moveY = -0.4; // On redéfinit la coordonnée de mouvement 'Y' par une valeur de '-0.4' si celle-ci est supérieure à '-0.4'
                }
                /* ⬆️ Si le NPC est en pleine chûte, on redéfinit donc la coordonnée de mouvement 'X' et 'Z' par celle-ci divisé par '4',
                   ainsi, on vérifie la coordonnée de mouvement 'Y' ⬆️ */


                /* ⬇️ Si le NPC est une instance de NPC d'un NPC Personnel pour un Joueur,
                   alors on envoie des messages d'information au Joueur en question ⬇️ */
                if(npc instanceof NPCPersonal npcPersonal) {

                    // ⬇️ Envoie des messages d'information au Joueur ⬇️ //
                    npcPersonal.getPlayer().sendMessage("", "", "", "", "", "", "", "", "");
                    npcPersonal.getPlayer().sendMessage("Bloc dans la Jambe " + blockInLeg.getType() + " " + blockInLeg.getType().isSolid());
                    npcPersonal.getPlayer().sendMessage("Bloc dans le Pied " + blockInFoot.getType() + " " + blockInFoot.getType().isSolid());
                    npcPersonal.getPlayer().sendMessage("Mouvement Coordonnée 'Y' " + moveY);
                    // ⬆️ Envoie des messages d'information au Joueur ⬆️ //

                    // ⬇️ Si le NPC est en Montée, en Saut ou en Chute en informe donc au Joueur ⬇️ //
                    if(uppingLadder) npcPersonal.getPlayer().sendMessage(ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + "MONTÉE");
                    if(jumpingBlock) npcPersonal.getPlayer().sendMessage(ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + "SAUT");
                    if(falling) npcPersonal.getPlayer().sendMessage(ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + "CHÛTE");
                    // ⬆️ Si le NPC est en Montée, en Saut ou en Chute en informe donc au Joueur ⬆️ //

                }
                /* ⬇️ Si le NPC est une instance de NPC d'un NPC Personnel pour un Joueur,
                   alors on envoie des messages d'information au Joueur en question ⬇️ */


                /* ⬇️ Si le NPC doit regarder sa localisation d'arrivée pendant son déplacement que le monde du NPC est le même que le monde de sa localisation d'arrivée,
                   on effectue donc une rotation du NPC pour qu'il regarde à un endroit précis ⬇️ */
                if(lookToEnd && npc.getLocation().getWorld().equals(end.getWorld())) {

                    npc.lookAt(end); // Le NPC va regarder vers la localisation d'arrivée
                    npc.updatePlayerRotation(); // On met à jour la Rotation de la Tête du NPC
                }
                /* ⬆️ Si le NPC doit regarder sa localisation d'arrivée pendant son déplacement que le monde du NPC est le même que le monde de sa localisation d'arrivée,
                   on effectue donc une rotation du NPC pour qu'il regarde à un endroit précis ⬆️ */

                npc.move(moveX, moveY, moveZ); // On déplace donc le NPC vers les coordonnées de mouvements en question
            }


            /**
             * Vérifie si le {@link Block bloc} précisé est un bloc qui peut servir d'escalade.
             *
             * @param block Le {@link Block bloc} en question
             *
             * @return Une valeur Booléenne
             */
            private boolean isLadder(Block block) { return block.getType().name().equals("LADDER") || block.getType().name().equals("VINE"); }

            /**
             * Vérifie si le {@link Block bloc} précisé est bien un escalier.
             *
             * @param block Le {@link Block bloc} en question
             *
             * @return Une valeur Booléenne
             */
            private boolean isStair(Block block) { return block.getType().name().contains("_STAIRS"); }

            /**
             * Vérifie si le {@link Block bloc} précisé est bien une dalle.
             *
             * @param block Le {@link Block bloc} en question
             *
             * @return Une valeur Booléenne
             */
            private boolean isSlab(Block block) { return isSlab(block, Slab.Type.BOTTOM); }

            /**
             * Vérifie si le {@link Block bloc} précisé est bien une dalle avec sa partie haute ou basse.
             *
             * @param block Le {@link Block bloc} en question
             * @param type Le {@link Slab.Type partie} de la dalle
             *
             * @return Une valeur Booléenne
             */
            private boolean isSlab(Block block, Slab.Type type) { return (block.getType().name().contains("_SLAB") && ((Slab) block.getBlockData()).getType().equals(type)) || block.getType().name().contains("_BED"); }


            /**
             * Vérifie si pendant le déplacement, on vérifie si le NPC précisé est en train de nager.
             *
             * @return Une valeur Booléenne
             */
            public boolean isCheckSwimming() { return checkSwimming; }

            /**
             * Définit si on doit vérifier pendant le déplacement, si le NPC doit-être en nage ou pas.
             *
             * @param checkSwimming Doit-on effectuer cette vérification lors du déplacement du NPC ?
             */
            public void setCheckSwimming(boolean checkSwimming) { this.checkSwimming = checkSwimming; }

            /**
             * Vérifie si pendant le déplacement, on vérifie si le NPC précisé est accroupie sur une dalle.
             *
             * @return Une valeur Booléenne
             */
            public boolean isCheckSlabCrouching() { return checkSlabCrouching; }

            /**
             * Définit si on doit vérifier pendant le déplacement, si le NPC doit-être accroupie sur une dalle ou pas.
             *
             * @param checkSlabCrouching Doit-on effectuer cette vérification lors du déplacement du NPC ?
             */
            public void setCheckSlabCrouching(boolean checkSlabCrouching) { this.checkSlabCrouching = checkSlabCrouching; }

            /**
             * Vérifie si pendant le déplacement, on vérifie si le NPC précisé est lent.
             *
             * @return Une valeur Booléenne
             */
            public boolean isCheckSlowness() { return checkSlowness; }

            /**
             * Définit si on doit vérifier pendant le déplacement, si le NPC doit-être en lent ou pas.
             *
             * @param checkSlowness Doit-on effectuer cette vérification lors du déplacement du NPC ?
             */
            public void setCheckSlowness(boolean checkSlowness) { this.checkSlowness = checkSlowness; }

            /**
             * Vérifie si pendant le déplacement, on vérifie si le NPC précisé est montée.
             *
             * @return Une valeur Booléenne
             */
            public boolean isCheckLadders() { return checkLadders; }

            /**
             * Définit si on doit vérifier pendant le déplacement, si le NPC doit-être en montée ou pas.
             *
             * @param checkLadders Doit-on effectuer cette vérification lors du déplacement du NPC ?
             */
            public void setCheckLadders(boolean checkLadders) { this.checkLadders = checkLadders; }

            /**
             * Définit les options de performance du NPC.
             *
             * @param performanceOptions Les {@link PerformanceOptions options de performance} en question
             */
            public void setPerformanceOptions(PerformanceOptions performanceOptions) {

                // Si les options de performance récupérée sont null, on définit sur toutes les options
                if(performanceOptions == null) performanceOptions = PerformanceOptions.ALL;

                // ⬇️ On boucle dur les choix possibles des options de performance activée puis on définit ses options ⬇️ //
                switch(performanceOptions) {

                    // Si on a choisi d'activée toutes les options, on effectue toute vérification lors du déplacement du NPC
                    case ALL -> {

                        setCheckLadders(true); // On définit la vérification de montée du NPC sur 'vrai'
                        setCheckSlowness(true); // On définit la vérification de lenteur du NPC sur 'vrai'
                        setCheckSlabCrouching(true); // On définit la vérification d'accroupie sur une dalle du NPC sur 'vrai'
                        setCheckSwimming(true);// On définit la vérification de nage du NPC sur 'vrai'

                        // Sinon, si on a choisi d'activée aucune options, on n'effectue pas toute vérification lors du déplacement du NPC
                    } case NONE -> {

                        setCheckLadders(false); // On définit la vérification de montée du NPC sur 'faux'
                        setCheckSlowness(false); // On définit la vérification de lenteur du NPC sur 'faux'
                        setCheckSlabCrouching(false); // On définit la vérification d'accroupie sur une dalle du NPC sur 'faux'
                        setCheckSwimming(false);// On définit la vérification de nage du NPC sur 'faux'
                    }

                }
                // ⬆️ On boucle dur les choix possibles des options de performance activée puis on définit ses options ⬆️ //

            }

            /* ------------------------------------------ */

            /**
             * Cette méthode privée sert à comparer deux coordonnées en fonction de la vitesse de déplacement du NPC.
             *
             * @param coordinateA Une coordonnée 'A'
             * @param coordinateB Une coordonnée 'B'
             *
             *
             * @return La coordonnée de comparaison entre les deux coordonnées 'A' et 'B'
             */
            private double compareCoordinate(double coordinateA, double coordinateB) {

                double comparisonCoordinate = 0.00; // Définit la coordonnée de comparaison sur '0.00'
                double moveSpeed = npc.getMoveSpeed(); // Récupère la vitesse de déplacement du NPC

                /* Si la coordonnée 'B' est inférieur à la coordonnée 'A', on définit la coordonnée de comparaison moins la vitesse de déplacement du NPC
                   et on effectue une nouvelle vérification */
                if(coordinateB < coordinateA) {

                    comparisonCoordinate = -moveSpeed; // Définit la coordonnée de comparaison sur moins la vitesse de déplacement du NPC

                    /* Définit la coordonnée de comparaison sur la coordonnée 'B' moins la coordonnée 'A',
                       si la coordonnée 'B' moins la coordonnée 'A' est inférieur à moins la coordonnée de comparaison */
                    if(coordinateB - coordinateA < -comparisonCoordinate) comparisonCoordinate = coordinateB - coordinateA;

                /* Sinon, si la coordonnée 'B' est supérieur à la coordonnée 'A', on définit la coordonnée de comparaison à la vitesse de déplacement du NPC
                   et on effectue une nouvelle vérification */
                } else if(coordinateB > coordinateA) {

                    comparisonCoordinate = moveSpeed; // Définit la coordonnée de comparaison sur la vitesse de déplacement du NPC

                    /* Définit la coordonnée de comparaison sur la coordonnée 'B' moins la coordonnée 'A',
                       si la coordonnée 'B' moins la coordonnée 'A' est supérieur à la coordonnée de comparaison */
                    if(coordinateB - coordinateA > comparisonCoordinate) comparisonCoordinate = coordinateB - coordinateA;
                }

                if(comparisonCoordinate > moveSpeed) comparisonCoordinate = moveSpeed; // Si la coordonnée de comparaison est supérieure à la vitesse de déplacement du NPC, on lui met égal
                if(comparisonCoordinate < -moveSpeed) comparisonCoordinate = -moveSpeed; // Si la coordonnée de comparaison est inférieure à moins la vitesse de déplacement du NPC, on lui met égal

                return comparisonCoordinate; // On retourne alors la coordonnée de comparaison
            }

            /**
             * Cette méthode privée sert à récupérer une nouvelle {@link Location localisation} dans le même monde du NPC en question en fonction des coordonnées données.
             *
             * @param x La Coordonnée 'x'
             * @param y La Coordonnée 'y'
             * @param z La Coordonnée 'z'
             *
             * @return La nouvelle {@link Location localisation} en question
             */
            private Location newLocation(double x, double y, double z) { return new Location(npc.getWorld(), x, y, z); }

            /* ------------------------------------------ */

            /**
             * Met en pause la {@link Task tâche} de mouvement pour le NPC.
             *
             */
            public void pause() {

                this.pause = true; // Met en pause la tâche

                // Si le NPC doit regarder sa localisation d'arrivée pendant son déplacement, on définit son dernier type de suivie du regard
                if(lookToEnd) npc.setGazeTrackingType(lastGazeTrackingType);
            }

            /**
             * Reprend la {@link Task tâche} de mouvement pour le NPC.
             *
             */
            public void resume() {

                this.pause = false; // Enlève la tache étant en pause

                // Si le NPC doit regarder sa localisation d'arrivée pendant son déplacement, on retire son type de suivie du regard
                if(lookToEnd) npc.setGazeTrackingType(NPC.GazeTrackingType.NONE);
            }

            /**
             * Vérifie sur la {@link Task tâche} de mouvement pour le NPC est en pause.
             *
             * @return Une valeur Booléenne
             */
            public boolean isPaused() { return pause; }

            /**
             * Vérifie sur la {@link Task tâche} de mouvement pour le NPC est en cours.
             *
             * @return Une valeur Booléenne
             */
            public boolean hasStarted() { return taskID != null && start != null; }

            /**
             * Annule la {@link Task tâche} de mouvement pour le NPC.
             *
             */
            public void cancel() { cancel(NPC.Move.Task.CancelCause.CANCELLED); }

            /**
             * Annule la {@link Task tâche} de mouvement pour le NPC en précisant une {@link CancelCause cause}.
             *
             * @param cancelCause La {@link CancelCause cause} en question
             *
             */
            protected void cancel(NPC.Move.Task.CancelCause cancelCause) {

                if(taskID == null) return; // Si l'identifiant de la tâche en cours est null, on fait rien

                // On définit un évènement pour le NPC de fin de son mouvement
                NPC.Events.FinishMove npcFinishMoveEvent = new NPC.Events.FinishMove(npc, start,end, taskID, cancelCause);

                /* ⬇️ Si le NPC doit regarder sa localisation d'arrivée pendant son déplacement, on définit son dernier type de suivie du regard et que la raison de
                  l'annulation de la tâche de mouvement est un succès alors, on définit le type de suivie du regard du NPC ainsi, on met à jour son mouvement actuel ⬇️ */
                if(lookToEnd && cancelCause.equals(CancelCause.SUCCESS)) {

                    npc.setGazeTrackingType(lastGazeTrackingType); // Définit son dernier type de suivie du regard
                    npc.updateMove(); // Met à jour le mouvement du NPC
                }
                /* ⬆️ Si le NPC doit regarder sa localisation d'arrivée pendant son déplacement, on définit son dernier type de suivie du regard et que la raison de
                  l'annulation de la tâche de mouvement est un succès alors, on définit le type de suivie du regard du NPC ainsi, on met à jour son mouvement actuel ⬆️ */

                Bukkit.getScheduler().cancelTask(taskID); // On annule la tâche en cours

                this.taskID = null; // Définit l'identifiant de la tâche en cours sur null

                npc.updateLocation(); // Met à jour la localisation du NPC
                npc.clearMoveTask(); // On supprime la tâche de mouvement du NPC

                // Si le type de suivie du NPC est un type customisé et que le chemin de mouvement du NPC n'est pas null, on donne au npc le chemin de mouvement suivant
                if(npc.getMoveBehaviour().getType().equals(Behaviour.Type.CUSTOM_PATH) && npc.getMoveBehaviour().path != null) npc.getMoveBehaviour().path.next();
            }

            /**
             * Récupère le NPC qui effectue le mouvement.
             *
             *
             * @return Le NPC en question
             */
            public NPC getNPC() { return npc; }

            /* --------------------------------- */

            /**************************************************/
            /* ÉNUMÉRATION POUR LES PERFORMANCES DE MOUVEMENT */
            /*************************************************/

            public enum PerformanceOptions { ALL,  NONE, }

            /**************************************************/
            /* ÉNUMÉRATION POUR LES PERFORMANCES DE MOUVEMENT */
            /*************************************************/


            /**************************************************************/
            /* ÉNUMÉRATION POUR LA CAUSE D'ANNULATION DU MOUVEMENT DU NPC */
            /*************************************************************/

            public enum CancelCause { SUCCESS, CANCELLED, ERROR, }

            /**************************************************************/
            /* ÉNUMÉRATION POUR LA CAUSE D'ANNULATION DU MOUVEMENT DU NPC */
            /*************************************************************/
        }
    }

    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */

    /**
     * INTÉRACTION POUR LE NPC
     *
     */
    public static class Interact {

        /**
         * Constructeur de la 'class' {@link Interact}
         */
        private Interact() {}

        /* --------------------------------- */

        /**
         * LES ACTIONS AU CLIQUÉ LORS DE L'INTÉRACTION
         *
         */
        public abstract static class ClickAction {

            private final NPC npc; // Variable récupérant le NPC qui effectuera le mouvement
            private final NPC.Interact.Actions.Type actionType; // Variable récupérant le type d'action a effectué
            protected NPC.Interact.ClickType clickType; // Variable récupérant le type de cliqué effectué
            protected BiConsumer<NPC, Player> action; // Variable enregistrant une action spécifique

            /**
             * On instancie une nouvelle {@link ClickAction action au cliqué}.
             *
             * @param npc Le NPC cible avec qui intéragir
             * @param actionType Le {@link NPC.Interact.Actions.Type le type d'action} a effectué
             * @param clickType Le {@link NPC.Interact.ClickType type de cliqué} effectué
             */
            protected ClickAction(NPC npc, NPC.Interact.Actions.Type actionType, NPC.Interact.ClickType clickType) {

                this.npc = npc; // On initialise le NPC
                this.actionType = actionType; // On initialise le type d'action a effectué

                if(clickType == null) clickType = ClickType.EITHER; // On définit le clic gauche et droit, si type de cliqué récupéré est null
                this.clickType = clickType; // On Initialise type de cliqué
            }

            /**
             * On récupère la chaîne de caractère en replacement les mots récupéré avec le {@link Placeholders placeholder}
             *
             * @param player Le joueur qui effectue l'action
             * @param s Le message qu'il faut remplacer.
             *
             * @return La chaîne de caractère remplacé
             */
            public String getReplacedString(Player player, String s) { return NPC.Placeholders.replace(npc, player, s); }

            /**
             * Récupère le NPC cible avec qui intéragir
             *
             * @return Le NPC cible avec qui intéragir
             */
            public NPC getNPC() { return npc; }

            /**
             * Récupère le {@link NPC.Interact.Actions.Type le type d'action} a effectué
             *
             * @return Le {@link NPC.Interact.Actions.Type le type d'action} a effectué
             */
            public NPC.Interact.Actions.Type getActionType() { return actionType; }

            /**
             * Récupère le {@link NPC.Interact.ClickType type de cliqué} effectué
             *
             * @return Le {@link NPC.Interact.ClickType type de cliqué} effectué
             */
            public NPC.Interact.ClickType getClickType() {

                if(clickType == null) return ClickType.EITHER;
                return clickType;
            }

            /**
             * Éxécute l'action a effectué pour le joueur qui l'a déclenché.
             *
             * @param player Le joueur qui effectue l'action
             *
             */
            protected void execute(Player player) { action.accept(npc, player); }
        }

        /* --------------------------------- */

        /**
         * LES ACTIONS LORS DE L'INTÉRACTION
         *
         */
        public static class Actions {

            /**
             * Constructeur de la 'class' {@link Interact}
             */
            private Actions() {}

            /* -------------------------- */
            /* -------------------------- */

            /**
             * ACTION ÉTANT CUSTOMISÉE
             *
             */
            public static class Custom extends ClickAction {

                /**
                 * On instancie une nouvelle {@link Custom action customisé}.
                 *
                 * @param npc Le NPC cible avec qui intéragir
                 * @param clickType Le {@link NPC.Interact.ClickType type de cliqué} effectué
                 * @param customAction L'{@link Custom Action customisé] a effectué
                 */
                protected Custom(NPC npc, NPC.Interact.ClickType clickType, BiConsumer<NPC, Player> customAction) {

                    super(npc, NPC.Interact.Actions.Type.CUSTOM_ACTION, clickType); // On instancie la 'class' parente
                    super.action = customAction; // On initialise l'action spécifique
                }

            }
            /* -------------------------- */
            /* -------------------------- */

            /**
             * ACTION ÉTANT UN MESSAGE
             *
             */
            public static class Message extends ClickAction {

                private final String[] messages; // Variable récupérant les messages à envoyer

                /**
                 * On instancie une nouvelle {@link Message action de message}.
                 *
                 * @param npc Le NPC cible avec qui intéragir
                 * @param clickType Le {@link NPC.Interact.ClickType type de cliqué} effectué
                 * @param message Les messages à envoyer
                 */
                protected Message(NPC npc, NPC.Interact.ClickType clickType, String... message) {

                    super(npc, NPC.Interact.Actions.Type.SEND_MESSAGE, clickType); // On instancie la 'class' parente

                    this.messages = message; // On Initialise tous les messages qu'il faut envoyer

                    // On initialise l'action spécifique (On envoie le ou les messages au joueur dans le tchat)
                    super.action = (npc1, player) -> Arrays.stream(getMessages()).toList().forEach(x-> player.sendMessage(getReplacedString(player,x)));
                }

                /**
                 * Récupère les messages à envoyer.
                 *
                 */
                public String[] getMessages() { return messages; }
            }
            /* -------------------------- */
            /* -------------------------- */

            /**
             * ACTION ÉTANT UNE COMMANDE
             *
             */
            public static abstract class Command extends ClickAction {

                private final String command; // Variable récupérant la commande à envoyer

                /**
                 * On instancie une nouvelle {@link Command action de commande}.
                 *
                 * @param npc Le NPC cible avec qui intéragir
                 * @param clickType Le {@link NPC.Interact.ClickType type de cliqué} effectué
                 * @param command La commande à envoyer
                 */
                protected Command(NPC npc, NPC.Interact.Actions.Type actionType, NPC.Interact.ClickType clickType, String command) {

                    super(npc, actionType, clickType); // On instancie la 'class' parente
                    this.command = command; // On Initialise la commande à envoyé
                }

                /**
                 * Récupère la commande à envoyer.
                 *
                 */
                protected String getCommand() { return command; }
            }
            /* ------------------- */
            /* ------------------- */

            /**
             * L'ACTION DE LA COMMANDE EXÉCUTÉE PAR LE JOUEUR
             *
             */
            public static class PlayerCommand extends NPC.Interact.Actions.Command {

                /**
                 * On instancie une nouvelle {@link PlayerCommand action de commande éxécuté par le Joueur}.
                 *
                 * @param npc Le NPC cible avec qui intéragir
                 * @param clickType Le {@link NPC.Interact.ClickType type de cliqué} effectué
                 * @param command La commande à envoyer
                 */
                protected PlayerCommand(NPC npc, NPC.Interact.ClickType clickType, String command) {

                    super(npc, NPC.Interact.Actions.Type.RUN_PLAYER_COMMAND, clickType, command); // On instancie la 'class' parente

                    // On initialise l'action spécifique (le joueur va éxécuté la commande en question)
                    super.action = (npc1, player) -> Bukkit.getServer().dispatchCommand(player, getReplacedString(player, super.getCommand()));
                }

            }
            /* ------------------- */
            /* ------------------- */

            /**
             * L'ACTION DE LA COMMANDE EXÉCUTÉE PAR LA CONSOLE
             *
             */
            public static class ConsoleCommand extends NPC.Interact.Actions.Command {

                /**
                 * On instancie une nouvelle {@link ConsoleCommand action de commande éxécuté par la Console}.
                 *
                 * @param npc Le NPC cible avec qui intéragir
                 * @param clickType Le {@link NPC.Interact.ClickType type de cliqué} effectué
                 * @param command La commande à envoyer
                 */
                protected ConsoleCommand(NPC npc, NPC.Interact.ClickType clickType, String command) {

                    super(npc, NPC.Interact.Actions.Type.RUN_CONSOLE_COMMAND, clickType, command); // On instancie la 'class' parente

                    // On initialise l'action spécifique (la console va éxécuté la commande en question)
                    super.action = (npc1, player) -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), getReplacedString(player, super.getCommand()));
                }

            }
            /* -------------------------- */
            /* -------------------------- */

            /**
             * ACTION ÉTANT UN TITRE
             *
             */
            public static class Title extends ClickAction {

                private final String title; // Variable récupérant le Titre Principal
                private final String subtitle; // Variable récupérant le Sous-Titre
                private final Integer fadeIn; // Variable récupérant le temps d'apparition
                private final Integer stay; // Variable récupérant le temps d'affichage
                private final Integer fadeOut; // Variable récupérant le temps de disparition

                /**
                 * On instancie une nouvelle {@link Title action de Titre}.
                 *
                 * @param npc Le NPC cible avec qui intéragir
                 * @param clickType Le {@link NPC.Interact.ClickType type de cliqué} effectué
                 * @param title Le Titre à afficher
                 * @param subtitle Le Sous-Titre à afficher
                 * @param fadeIn Le temps d'apparition du titre et du sous-titre
                 * @param stay Le temps d'affichage du titre et du sous-titre
                 * @param fadeOut Le temps de disparition du titre et du sous-titre
                 */
                protected Title(NPC npc, NPC.Interact.ClickType clickType, String title, String subtitle, Integer fadeIn, Integer stay, Integer fadeOut) {

                    super(npc, NPC.Interact.Actions.Type.SEND_TITLE_MESSAGE, clickType); // On instancie la 'class' parente

                    this.title = title; // On initialise le Titre Principal
                    this.subtitle = subtitle; // On initialise le Sous-Titre
                    this.fadeIn = fadeIn; // On initialise le temps d'apparition
                    this.stay = stay; // On initialise le temps d'affichage
                    this.fadeOut = fadeOut; // On initialise le temps de disparition


                    // ⬇️ On initialise l'action spécifique (on crée un titre et on l'envoie au joueur) ⬇️ //
                    super.action = (npc1, player) -> {

                        net.kyori.adventure.title.Title adventureTitle = net.kyori.adventure.title.Title.title(CustomMethod.StringToComponent(ChatFormatting.WHITE + getReplacedString(player, getTitle())), CustomMethod.StringToComponent(getReplacedString(player, getSubtitle())));
                        NMSCraftPlayer.getEntityPlayer(player).connection.send(new ClientboundSetTitlesAnimationPacket(getFadeIn(), getStay(), getFadeOut()));
                        player.showTitle(adventureTitle);
                    };
                    // ⬆️ On initialise l'action spécifique (on crée un titre et on l'envoie au joueur) ⬆️ //

                }

                /**
                 * Récupère le Titre Principal à afficher.
                 *
                 * @return Le Titre Principal à afficher
                 */
                public String getTitle() { return title; }

                /**
                 * Récupère le Sous-Titre à afficher.
                 *
                 * @return Le Sous-Titre à afficher
                 */
                public String getSubtitle() { return subtitle; }

                /**
                 * Récupère le Temps d'Apparition du Titre et du Sous-Titre.
                 *
                 * @return Le Temps d'Apparition du Titre et du Sous-Titre
                 */
                public Integer getFadeIn() { return fadeIn; }

                /**
                 * Récupère le Temps d'Affichage du Titre et du Sous-Titre.
                 *
                 * @return Le Temps d'Affichage du Titre et du Sous-Titre
                 */
                public Integer getStay() { return stay; }

                /**
                 * Récupère le Temps de Disparition du Titre et du Sous-Titre.
                 *
                 * @return Le Temps de Disparition du Titre et du Sous-Titre
                 */
                public Integer getFadeOut() { return fadeOut; }

            }
            /* -------------------------- */
            /* -------------------------- */

            /**
             * ACTION ÉTANT UN MESSAGE DANS LA BARRE D'ACTION
             *
             */
            public static class ActionBar extends ClickAction{

                private final String message; // Variable récupérant le message à envoyer

                /**
                 * On instancie une nouvelle {@link ActionBar action de message dans la barre d'action}.
                 *
                 * @param npc Le NPC cible avec qui intéragir
                 * @param clickType Le {@link NPC.Interact.ClickType type de cliqué} effectué
                 * @param message Le message à envoyer
                 */
                public ActionBar(NPC npc, NPC.Interact.ClickType clickType, String message) {

                    super(npc, NPC.Interact.Actions.Type.SEND_ACTIONBAR_MESSAGE, clickType); // On instancie la 'class' parente

                    this.message = message; // On initialise le message à envoyer

                    // On initialise l'action spécifique (le message va être envoyé au joueur au niveau de sa barre d'action)
                    super.action = (npc1, player) -> CustomMethod.sendActionBar(player, getReplacedString(player, getMessage()));
                }

                /**
                 * Récupère le message à envoyer.
                 *
                 */
                public String getMessage() { return message; }
            }
            /* -------------------------- */
            /* -------------------------- */

            /**
             * ACTION ÉTANT UNE REQUÊTE BUNGEECORD POUR SE CONNECTER SUR UN AUTRE SERVEUR
             *
             */
            public static class BungeeServer extends ClickAction {

                private final String server; // Variable récupérant le nom du Serveur cible pour la connection à celle-ci

                /**
                 * On instancie une nouvelle {@link BungeeServer action de requête bungeecord de connexion vers un autre serveur}.
                 *
                 * @param npc Le NPC cible avec qui intéragir
                 * @param clickType Le {@link NPC.Interact.ClickType type de cliqué} effectué
                 * @param server Le nom du serveur où connecter le Joueur
                 */
                protected BungeeServer(NPC npc, NPC.Interact.ClickType clickType, String server) {

                    super(npc, NPC.Interact.Actions.Type.CONNECT_BUNGEE_SERVER, clickType); // On instancie la 'class' parente

                    this.server = server; // On initialise le nom du Serveur cible pour la connection à celle-ci


                    // ⬇️ On initialise l'action spécifique (On exécute la requête au Serveur Proxy pour connecter le joueur dans le serveur demandé) ⬇️ //
                    super.action = (npc1, player) -> {

                        if(!Bukkit.getServer().getMessenger().isOutgoingChannelRegistered(UtilityMain.getInstance(), "BungeeCord")) Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(UtilityMain.getInstance(), "BungeeCord");
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Connect");
                        out.writeUTF(getReplacedString(player, server));
                        player.sendPluginMessage(UtilityMain.getInstance(), "BungeeCord", out.toByteArray());
                    };
                    // ⬆️ On initialise l'action spécifique (On exécute la requête au Serveur Proxy pour connecter le joueur dans le serveur demandé) ⬆️ //
                }

                /**
                 * Récupère le Nom du Serveur àoù le Joueur sera connecté.
                 *
                 */
                public String getServer() { return server; }
            }
            /* -------------------------- */
            /* -------------------------- */

            /**
             * ACTION ÉTANT UNE TÉLÉPORTATION
             *
             */
            public static class TeleportToLocation extends ClickAction {


                private final Location location; // Variable récupérant la localisation cible de téléportation

                /**
                 * On instancie une nouvelle {@link TeleportToLocation action de téléportation} vers une {@link Location localisation} précise.
                 *
                 * @param npc Le NPC cible avec qui intéragir
                 * @param clickType Le {@link NPC.Interact.ClickType type de cliqué} effectué
                 * @param location La {@link Location localisation} où le Joueur sera téléporté
                 */
                public TeleportToLocation(NPC npc, NPC.Interact.ClickType clickType, Location location) {

                    super(npc, Type.TELEPORT_TO_LOCATION, clickType); // On instancie la 'class' parente

                    this.location = location; // On initialise la localisation cible de téléportation

                    // On initialise l'action spécifique (le joueur sera téléporter à l'endroit demandé)
                    super.action = (npc1, player) -> player.teleport(getLocation());
                }

                /**
                 * Récupère la {@link Location localisation} où le Joueur sera téléporté.
                 *
                 */
                public Location getLocation() { return location; }
            }

            /**************************************************/
            /* ÉNUMÉRATION POUR LE TYPE D'ACTION A ÉFFECTUÉ  */
            /*************************************************/

            public enum Type {

                RUN_PLAYER_COMMAND, RUN_CONSOLE_COMMAND,
                SEND_MESSAGE, SEND_ACTIONBAR_MESSAGE, SEND_TITLE_MESSAGE,
                CONNECT_BUNGEE_SERVER, TELEPORT_TO_LOCATION, CUSTOM_ACTION
            }

            /**************************************************/
            /* ÉNUMÉRATION POUR LE TYPE D'ACTION A ÉFFECTUÉ  */
            /*************************************************/
        }

        /***********************************************************************/
        /* ÉNUMÉRATION POUR LE TYPE DE CLIQUÉ ÉFFECTUÉ POUR DÉMARRER L'ACTION  */
        /***********************************************************************/

        public enum ClickType {

            RIGHT_CLICK, LEFT_CLICK, EITHER;

            public boolean isRightClick() { return this.equals(RIGHT_CLICK); }

            public boolean isLeftClick() { return this.equals(LEFT_CLICK); }

            public ClickType getInvert() {

                if(this.equals(RIGHT_CLICK)) return LEFT_CLICK;
                if(this.equals(LEFT_CLICK)) return RIGHT_CLICK;
                return null;
            }

        }

        /***********************************************************************/
        /* ÉNUMÉRATION POUR LE TYPE DE CLIQUÉ ÉFFECTUÉ POUR DÉMARRER L'ACTION  */
        /***********************************************************************/

    }

    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */
    /**
     * HOLOGRAMMES POUR LE NPC (Texte)
     *
     */
    public static class Hologram {
        private final NPC npc; // Variable récupérant le NPC qui effectuera le mouvement
        private final Player player; // Variable récupérant le joueur qui recevra l'hologramme
        private Location location; // Variable récupérant la localisation de l'hologramme
        private HashMap<Integer, List<ArmorStand>> lines; // Variable récupérant chaques lignes de l'hologramme
        private boolean canSee; // Variable vérifiant si on peut voir l'hologramme ou non

        /**
         * On instancie un nouvel {@link Hologram hologramme} pour l'affichage du texte du NPC.
         *
         * @param npc Le NPC cible qui aura l'hologramme
         * @param player Le Joueur qui recevra l'hologramme
         */
        protected Hologram(NPC npc, Player player) {

            this.npc = npc; // On initialise le NPC
            this.player = player; // On initialise le Joueur
            this.canSee = false; // On définit le fait qu'on puisse voir l'hologramme sur faux

            create(); // On passe à la création de l'hologramme
        }

        /**
         * Créer un {@link Hologram hologramme} pour l'affichage du texte du NPC.
         *
         */
        private void create() {

            this.lines = new HashMap<>(); // Variable récupérant toutes les lignes de l'hologramme

            // Variable récupérant la localisation actuelle du NPC
            this.location = new Location(npc.getWorld(), npc.getX(), npc.getY(), npc.getZ()).add(npc.getTextAlignment());

            // ⬇️ On récupère tous les textes à afficher, pour chaque texte, on crée une nouvelle ligne ⬇️ //
            for(int i = 1; i <= getText().size(); i++) {

                createLine(); // On crée une nouvelle ligne
                setLine(i, getText().get(i-1)); // On ajoute à la ligne en question le texte actuel a affiché
            }
            // ⬆️ On récupère tous les textes à afficher, pour chaque texte, on crée une nouvelle ligne ⬆️ //
        }

        /**
         * Créer une Ligne pour l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC.
         *
         */
        protected void createLine() {

            int line = 1; // On définit la première ligne

            // ⬇️ On vérifie toutes les lignes (100 ligne max), on vérifie si ligne éxiste déjà, sinon on ajoute une nouvelle ⬇️ //
            for(int i = 1; i < 100; i++) {

                if(lines.containsKey(i)) continue;
                line = i;
                break;
            }
            // ⬆️ On vérifie toutes les lignes (100 ligne max), on vérifie si ligne éxiste déjà, sinon on ajoute une nouvelle ⬆️ //

            // On récupère l'opacité du texte a affiché dans la ligne
            NPC.Hologram.Opacity textOpacity = getLinesOpacity().getOrDefault(line, npc.getTextOpacity());
            ServerLevel world = null; // Variable récupérant le Monde Minecraft où se situe le NPC

            // ⬇️ On essaie de récupérer le Monde Minecraft où se situe le NPC depuis le NMS, sinon on ignore ⬇️ //
            try { world = (ServerLevel)NMSCraftWorld.getCraftWorldGetHandle().invoke(NMSCraftWorld.getCraftWorldClass().cast(location.getWorld()), new Object[0]); }
            catch(Exception ignored) {}
            // ⬆️ On essaie de récupérer le Monde Minecraft où se situe le NPC depuis le NMS, sinon on ignore ⬆️ //

            Validate.notNull(world, "Erreur à NMSCraftWorld"); // Si le Monde Minecraft n'a pas pû être récupéré, on envoie une erreur
            List<ArmorStand> armorStands = new ArrayList<>(); // On récupère une liste de Portes Armure qui nous servira de faire la ligne


            // ⬇️ Allant de 1 à la valeur correspondant à l'opacité du texte a affiché, on ajoute à chaque porte-armure à la ligne pour gruger l'opacité de la ligne ⬇️ //
            for(int i = 1; i <= textOpacity.getTimes(); i++) {

                // On crée un nouveau Porte-Armure
                ArmorStand armor = new ArmorStand(world, location.getX(), location.getY() + (npc.getLineSpacing() * ((getText().size() - line))), location.getZ());

                armor.setCustomNameVisible(true); // On accepte l'affichage Nom Customisé du Porte-Armure
                armor.setNoGravity(true); // On Enlève la Gravité au Porte-Armure

                NMSEntity.setCustomName(armor, "§f"); // On Ajoute un Nom Customisé au Porte-Armure

                armor.setInvisible(true); // Le Porte-Armure sera invisible.
                armor.setMarker(true); // Le Porte-Armure aura un marqueur.

                armorStands.add(armor); // On ajoute le Porte-Armure à la liste des Portes Armure qui nous sert de faire ligne
            }
            // ⬆️ Allant de 1 à la valeur correspondant à l'opacité du texte a affiché, on ajoute à chaque porte-armure à la ligne pour gruger l'opacité de la ligne ⬆️ //

            lines.put(line, armorStands); // On ajoute le porte-armure à la ligne en question
        }

        /**
         * Ajoute un Texte à la Ligne de l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC.
         *
         */
        protected void setLine(int line, String text) {

            if(!lines.containsKey(line)) return; // Si on ne trouve pas la ligne demandée, on ne fait rien

            // On récupère le texte à afficher tous en remplaçant quelque mots récupérés avec le système de placeholder
            String replacedText = NPC.Placeholders.replace(npc, player, text);

            // ⬇️ On récupère donc tous les Portes Armure de la ligne actuelle puis on change leur Nom Customisé pour afficher le Texte ⬇️ //
            for(ArmorStand as : lines.get(line)) {

                as.setNoGravity(true); // On Enlève la Gravité au Porte-Armure.
                as.setInvisible(true); // Le Porte-Armure sera invisible.

                NMSEntity.setCustomName(as, replacedText); // On Ajoute le Texte comme Nom Customisé au Porte-Armure

                // On accepte l'affichage Nom Customisé du Porte-Armure, si le Texte n'est pas null ou que le Texte n'est pas vide
                as.setCustomNameVisible(!text.isEmpty());
            }
            // ⬆️ On récupère donc tous les Portes Armure de la ligne actuelle puis on change leur Nom Customisé pour afficher le Texte ⬆️ //
        }

        /**
         * Récupère le Texte de la Ligne en question de l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC.
         *
         * @param line La Ligne a récupéré le Texte
         *
         * @return Le Texte de la Ligne en question de l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC.
         */
        protected String getLine(int line) {

            if(!lines.containsKey(line)) return ""; // Si la ligne n'éxiste pas, on ne retourne rien

            // On retourne le Texte correspondant à la ligne (soit le nom customisé d'un Porte-Armure associé a cette ligne)
            return lines.get(line).get(0).getCustomName().getString();
        }

        /**
         * Vérifie si la Ligne en question de l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC éxiste.
         *
         * @param line La Ligne qu'il faut vérifier
         *
         * @return Une valeur Booléenne
         */
        protected boolean hasLine(int line) { return lines.containsKey(line); }

        /**
         * Affiche l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC.
         *
         */
        protected void show() {

            if(canSee) return; // Si l'hologramme est déjà visible par le joueur, on ne fait rien

            // ⬇️ Si le NPC est un NPC personnel pour un Joueur spécifique, on vérifie si le Joueur peut voir le NPC, son Texte et sa position ⬇️ //
            if(npc instanceof NPCPersonal npcPersonal) {

                if(npcPersonal.isHiddenText()) return; // On ne fait rien, si le npc à un texte caché
                if(!npcPersonal.isInRange()) return; // On ne fait rien, si le npc n'est pas à côté du Joueur
                if(!npcPersonal.isShownOnClient()) return; // On ne fait rien, si le npc n'est pas visible par le Joueur
            }
            // ⬆️ Si le NPC est un NPC personnel pour un Joueur spécifique, on vérifie si le Joueur peut voir le NPC, son Texte et sa position ⬆️ //

            // ⬇️ Pour toutes les lignes de l'hologramme, on essaie récupère chaque porte-armure associée à cette même ligne et on essaie de l'afficher pour le Joueur ⬇️ //
            for(Integer line : lines.keySet()) {

                // ⬇️ Pour chaque porte-armure associée à la ligne actuelle, on essaie de l'afficher via les paquets en NMS pour le Joueur en question ⬇️ //
                for(ArmorStand armor : lines.get(line)) {

                    NMSCraftPlayer.sendPacket(player, new ClientboundAddEntityPacket(armor));
                    NMSCraftPlayer.sendPacket(getPlayer(), new ClientboundSetEntityDataPacket(armor.getId(), armor.getEntityData().getNonDefaultValues()));
                }
                // ⬆️ Pour chaque porte-armure associée à la ligne actuelle, on essaie de l'afficher via les paquets en NMS pour le Joueur en question ⬆️ //
            }
            // ⬆️ Pour toutes les lignes de l'hologramme, on essaie récupère chaque porte-armure associée à cette même ligne et on essaie de l'afficher pour le Joueur ⬆️ //

            canSee = true; // On définit la visibilité de l'hologramme sur 'vrai'
        }

        /**
         * Cache l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC.
         *
         */
        protected void hide() {

            if(!canSee) return; // Si l'hologramme n'est pas visible par le joueur, on ne fait rien

            // ⬇️ Pour toutes les lignes de l'hologramme, on essaie récupère chaque porte-armure associée à cette même ligne et on essaie de le détruire pour le Joueur ⬇️ //
            for(Integer in : lines.keySet()) {

                for(ArmorStand armor : lines.get(in)) { NMSCraftPlayer.sendPacket(player, new ClientboundRemoveEntitiesPacket(armor.getId())); }
            }
            // ⬆️ Pour toutes les lignes de l'hologramme, on essaie récupère chaque porte-armure associée à cette même ligne et on essaie de le détruire pour le Joueur ⬆️ //

            canSee = false; // On définit la visibilité de l'hologramme sur 'faux'
        }

        /**
         * Déplace l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC en définissant un {@link Vector Vecteur}.
         *
         * @param vector Le {@link Vector Vecteur} en question
         *
         */
        protected void move(Vector vector) {

            this.location.add(vector); // On ajoute le Vecteur à la localisation de l'hologramme

            // On récupère la connection du Joueur pour envoyer un paquet
            ServerGamePacketListenerImpl playerConnection = NMSCraftPlayer.getPlayerConnection(getPlayer());

            // ⬇️ Pour toutes les lignes de l'hologramme, on essaie récupère chaque porte-armure associée à cette même ligne et on essaie de le déplacer pour le Joueur ⬇️ //
            for(Integer in : lines.keySet()) {

                // ⬇️ Pour chaque porte-armure associée à la ligne actuelle, on essaie de le déplacer via les paquets en NMS pour le Joueur en question ⬇️ //
                for(ArmorStand armor : lines.get(in)) {

                    Location location = armor.getBukkitEntity().getLocation(); // On récupère la localisation exacte du porte-armure (soit la ligne)

                    double fx = location.getX() + vector.getX(); // On ajoute à sa coordonnée 'x' un la coordonné 'x' du Vecteur
                    double fy = location.getY() + vector.getY(); // On ajoute à sa coordonnée 'y' un la coordonné 'y' du Vecteur
                    double fz = location.getZ() + vector.getZ(); // On ajoute à sa coordonnée 'z' un la coordonné 'z' du Vecteur

                    armor.moveTo(fx, fy, fz); // On déplace le porte-armure (soit la ligne) sur les nouvelles coordonnées définit
                    playerConnection.send(new ClientboundTeleportEntityPacket(armor)); // On envoie le paquet au Joueur en question
                }
                // ⬇️ Pour chaque porte-armure associée à la ligne actuelle, on essaie de le déplacer via les paquets en NMS pour le Joueur en question ⬇️ //
            }
            // ⬆️ Pour toutes les lignes de l'hologramme, on essaie récupère chaque porte-armure associée à cette même ligne et on essaie de le déplacer pour le Joueur ⬆️ //
        }

        /**
         * Met à jour l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC.
         * L'{@link Hologram hologramme} sera Caché et de nouveau visible
         *
         */
        protected void update() { hide(); if(canSee) show(); }

        /**
         * Force la Mise à jour l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC.
         * L'{@link Hologram hologramme} sera Caché, Recréer et de nouveau visible
         *
         */
        protected void forceUpdate() { hide(); create(); if(canSee) show(); }

        /**
         * Supprime l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC.
         * L'{@link Hologram hologramme} sera Caché et toutes les lignes seront supprimées
         *
         */
        protected void removeHologram() { hide(); lines.clear(); }

        /**
         * Vérifie l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC peut être vue.
         *
         * @return Une valeur Booléenne
         */
        protected boolean canSee() { return canSee; }

        /**
         * Récupère le Joueur qui recevra l'{@link Hologram hologramme} actuel pour l'affichage du texte du NPC.
         *
         */
        protected Player getPlayer() { return this.player; }

        /**
         * Récupère les Textes affichés de l'{@link Hologram hologramme} actuel.
         *
         */
        protected List<String> getText() { return npc.getText(); }

        /**
         * Récupère l'Opacité de chaque ligne de l'{@link Hologram hologramme} actuel.
         *
         */
        protected HashMap<Integer, NPC.Hologram.Opacity> getLinesOpacity() { return npc.getLinesOpacity(); }

        /**
         * Récupère le NPC associé à l'{@link Hologram hologramme} actuel.
         *
         */
        protected NPC getNpc() { return npc; }


        /*********************************************************************************************************************************/
        /* ÉNUMÉRATION POUR LE TYPE D'OPACITÉ DE LA LIGNE DE L'HOLOGRAMME (Soit le nombre de portes armure à créer sur une même ligne)  */
        /********************************************************************************************************************************/
        public enum Opacity {

            LOWEST(1),
            LOW(2),
            MEDIUM(3),
            HARD(4),
            HARDER(6),
            FULL(10);

            private final int times;

            Opacity(int times) { this.times = times; }

            private int getTimes() { return times; }

            public static Opacity getOpacity(String name) { return Arrays.stream(Opacity.values()).filter(x-> x.name().equalsIgnoreCase(name)).findAny().orElse(null); }
        }
        /*********************************************************************************************************************************/
        /* ÉNUMÉRATION POUR LE TYPE D'OPACITÉ DE LA LIGNE DE L'HOLOGRAMME (Soit le nombre de portes armure à créer sur une même ligne)  */
        /********************************************************************************************************************************/
    }

    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */

    /**
     * 'PLACEHOLDER' POUR LE NPC
     *
     * @since 2022.2
     */
    public static class Placeholders {

        /**
         * Constructeur de la 'class' {@link Placeholders}
         */
        private Placeholders() {}

        private static final HashMap<String, BiFunction<NPC, Player, String>> placeholders; // Variable récupérant tous les 'placeholders'

        /**
         * On Initialise tous les mots remplaçables et leur valeur remplaçait associé
         *
         */
        static {

            placeholders = new HashMap<>();
            addPlaceholder("playerName", (npc, player) -> player.getName());
            addPlaceholder("playerDisplayName", (npc, player) -> CustomMethod.ComponentToString(player.displayName()));
            addPlaceholder("playerUUID", (npc, player) -> player.getUniqueId().toString());
            addPlaceholder("playerWorld", (npc, player) -> player.getWorld().getName());
            addPlaceholder("npcCode", (npc, player) -> npc.getCode());
            addPlaceholder("npcSimpleCode", (npc, player) -> npc.getSimpleCode());
            addPlaceholder("npcWorld", (npc, player) -> npc.getWorld().getName());
            addPlaceholder("npcTabListName", (npc, player) -> npc.getCustomTabListName());
            addPlaceholder("npcPluginName", (npc, player) -> npc.getPlugin().getPluginMeta().getName());
            addPlaceholder("npcPluginVersion", (npc, player) -> npc.getPlugin().getPluginMeta().getVersion());
            addPlaceholder("serverOnlinePlayers", (npc, player) -> "" + npc.getPlugin().getServer().getOnlinePlayers().size());
            addPlaceholder("serverMaxPlayers", (npc, player) -> "" + npc.getPlugin().getServer().getMaxPlayers());
        }

        /**
         * Récupère le format de la chaîne de caractère en question généré par le {@link Placeholders 'placeholder'}.
         *
         * @param s La chaîne de caractère qu'il faut vérifier le format généré par le {@link Placeholders 'placeholder'}
         *
         * @return Le format de la chaîne de caractère en question généré par le {@link Placeholders 'placeholder'}
         */
        public static String format(String s) { return "{" + s + "}"; }

        /**
         * Récupère tous les mots remplaçables par le {@link Placeholders 'placeholder'}.
         *
         * @return Tous les mots remplaçables par le {@link Placeholders 'placeholder'}
         */
        public static Set<String> getAllPlaceholders() { return getAllPlaceholders(null); }

        /**
         * Récupère tous les mots remplaçables par le {@link Placeholders 'placeholder'} depuis le Texte du NPC actuel.
         *
         * @param npc Le NPC cible
         *
         * @return Tous les mots remplaçables par le {@link Placeholders 'placeholder'} depuis le Texte du NPC actuel
         */
        public static Set<String> getAllPlaceholders(NPC npc) {

            Set<String> list = new HashSet<>(placeholders.keySet()); // On initialise la liste des mots remplaçables
            if(npc == null) return list; // Si le NPC est null, on retourne la liste initialisée

            NPC customDataNPC = npc; // On récupère le NPC en paramètre sur une autre variable 'customDataNPC'

            // Si le NPC est un NPC Personnel spécifique pour un Joueur et qu'il est devenue Globale, on récupère plutôt le NPC Globale
            if(npc instanceof NPCPersonal npcPersonal) if(npcPersonal.hasNPCGlobal()) customDataNPC = npcPersonal.getNPCGlobal();

            // Si le NPC 'customDataNPC' a des données customisées, on lui ajoute dans la liste des mots remplaçable chaques données customisées du NPC.
            if(!customDataNPC.getCustomDataKeys().isEmpty()) customDataNPC.getCustomDataKeys().forEach(x-> list.add("customData:" + x));

            return list; // On retourne la liste
        }

        /**
         * On ajoute par le {@link Placeholders 'placeholder'} un nouveau mot remplaçable, on précisait le mot et sa valeur remplacée.
         *
         * @param placeholder Le mot qui sera remplacé
         * @param replacement Le remplacement en question
         *
         */
        public static void addPlaceholder(@Nonnull String placeholder, @Nonnull BiFunction<NPC, Player, String> replacement) {

            Validate.notNull(placeholder, "'Placeholder' ne peut pas être nul.");  // Si le mot qui sera remplacé est null, on affiche une erreur
            Validate.notNull(replacement, "Le remplacement ne peut pas être nul."); // Si le remplacement est null, on affiche une erreur

            // Si le mot a remplacé a déjà été réglé, on affiche l'information
            Validate.isTrue(!placeholders.containsKey(placeholder), "Placeholder \"" + placeholder + "\" réglé précédemment");

            placeholders.put(placeholder, replacement); // On définit le nouveau mot remplaçable
        }

        /**
         * Vérifie si le mot remplaçable par le {@link Placeholders 'placeholder'} éxiste déjà.
         *
         * @param placeholder Le mot en question
         *
         * @return Une valeur Booléenne
         */
        public static boolean existsPlaceholder(@Nonnull String placeholder) {

            Validate.notNull(placeholder, "'Placeholder' ne peut pas être nul."); // Si le mot en question est null, on affiche une erreur*
            return placeholders.containsKey(placeholder); // On retourne la vérification du mot en question, s'il existe dans le 'placeholder'
        }

        /**
         * Remplace une chaîne de caractère en utilisant les mots remplaçable du {@link Placeholders 'placeholder'} pour le NPC et son Joueur associé.
         *
         * @param npc Le NPC cible qui contient le texte a remplacé les mots
         * @param player Le Joueur qui verra le texte avec les mots remplacé
         * @param string La chaîne de caractère contenant les mots à remplacer
         *
         * @return La même chaîne de caractère avec les mots spécifique {@link Placeholders 'placeholder'} remplacés.
         */
        public static String replace(@Nonnull NPC npc, @Nonnull Player player, @Nonnull String string) {

            Validate.notNull(npc, "Le NPC ne peut pas être nul."); // Si le NPC en question est null, on affiche une erreur
            Validate.notNull(player, "Le joueur ne peut pas être nul."); // Si le Joueur en question est null, on affiche une erreur

            // ⬇️ Pour tous les mots remplaçables du 'placeholder', on vérifie si le mot est bien un mot à remplacer, dans ce cas, on remplace le mot ⬇️ //
            for(String placeholder : placeholders.keySet()) {

                // Si la chaîne de caractère ne contient pas le mot en question à remplacer, on continue la boucle espérant trouver un mot remplaçable
                if(!string.contains("{" + placeholder + "}")) continue;

                // On change la chaîne de caractère par celle-ci ayant le mot en question remplacé par le 'placeholder'
                string = r(string, placeholder, placeholders.get(placeholder).apply(npc, player));
            }
            // ⬆️ Pour tous les mots remplaçables du 'placeholder', on vérifie si le mot est bien un mot à remplacer, dans ce cas, on remplace le mot ⬆️ //

            /**********************************************************/

            NPC customDataNPC = npc; // On récupère le NPC en paramètre sur une autre variable 'customDataNPC'

            // Si le NPC est un NPC Personnel spécifique pour un Joueur et qu'il est devenue Globale, on récupère plutôt le NPC Globale
            if(npc instanceof NPCPersonal npcPersonal) { if(npcPersonal.hasNPCGlobal()) customDataNPC = npcPersonal.getNPCGlobal(); }

            // Pour toutes les données customisées du NPC, on vérifie chaques données espérant qu'on trouve un mot à remplacer à l'intérieur //
            for(String key : customDataNPC.getCustomDataKeys()) {

                // Si la chaîne de caractère ne contient pas la donnée customisée du NPC en question à remplacer, on continue la boucle espérant trouver une donnée remplaçable
                if(!string.contains("{customData:" + key + "}")) continue;

                // On change la chaîne de caractère par celle-ci ayant la donnée customisée du NPC en question remplacé par le 'placeholder'
                string = r(string, "customData:" + key, customDataNPC.getCustomData(key));
            }
            // Pour toutes les données customisées du NPC, on vérifie chaques données espérant qu'on trouve un mot à remplacer à l'intérieur //

            return string; // On retourne la chaîne de caractère modifié
        }

        /**
         * Remplace tous les mots demandés dans une chaîne de caractère par une valeur précisé
         *
         * @param string La chaîne de caractère qu'il faut vérifier
         * @param placeHolder Le mot a remplacé dans la chaîne de caractère
         * @param value La valeur a remplacé du mot dans la chaîne de caractère
         *
         * @return La chaîne de caractère ayant subi les remplacements des mots demandés.
         */
        private static String r(String string, String placeHolder, String value) { return string.replaceAll("\\{" + placeHolder +"}", value); }
    }

    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */

    /**
     * ATTRIBUTS POUR LE NPC
     *
     * @since 2022.1
     */
    public static class Attributes {

        // ** Récupère les attributs par défaut du NPC ** //
        private static final Attributes DEFAULT = new Attributes(NPC.Skin.getSteveSkin(), new Skin.Parts(), new ArrayList<>(), new HashMap<>(), false, 50.0, false,
                ChatFormatting.WHITE, NPC.GazeTrackingType.NONE, "§8[NPC] {id}", false, 200L, 0.27,
                new Vector(0, 1.75, 0), Pose.STANDING, NPC.Hologram.Opacity.LOWEST, false, Move.Speed.NORMAL.doubleValue(), new HashMap<>());
        // ** Récupère les attributs par défaut du NPC ** //

        protected static final Double VARIABLE_MIN_LINE_SPACING = 0.27; // La distance minimale sur laquelle le NPC peut être vue
        protected static final Double VARIABLE_MAX_LINE_SPACING = 1.00; // La distance maximale sur laquelle le NPC peut être vue
        protected static final Double VARIABLE_MAX_TEXT_ALIGNMENT_XZ = 2.00; // L'Alignement des coordonnées 'X' et 'z' du Texte affiché du NPC
        protected static final Double VARIABLE_MAX_TEXT_ALIGNMENT_Y = 5.00; // L'Alignement des coordonnées 'Z' du Texte affiché du NPC

        protected NPC.Skin skin; // Le Skin du NPC
        protected NPC.Skin.Parts skinParts; // Les Parties du Skin visible du NPC
        protected List<String> text; // Les Textes affichés par le NPC
        protected HashMap<EquipmentSlot, ItemStack> slots; // L'Équipement du NPC pour chaque emplacement (tête, torse, mains, jambière, botte)
        protected Boolean collidable; // Les collisions du NPC sont activés ?
        protected Double hideDistance; // La distance à laquelle le NPC disparaîtra de la vue du Joueur
        protected Boolean glowing; // Le NPC est-il en surbrillance ?
        protected ChatFormatting glowingColor; // La Couleur de surbrillance du NPC
        protected NPC.GazeTrackingType gazeTrackingType; // Le Type de suivie du regard du NPC
        protected String customTabListName; // Le Nom Customisé du NPC
        protected Boolean showOnTabList; // Le NPC est-il visible dans le 'tablist'
        protected Long interactCooldown; // Le Temps d'Intéraction avec le NPC
        protected Double lineSpacing; // Le champ de vision du Joueur avec le NPC
        protected Vector textAlignment; // L'Alignement du Texte du NPC
        protected Pose pose; // La Posture du NPC
        protected NPC.Hologram.Opacity textOpacity; // L'Opacité du Texte du NPC
        protected Boolean onFire; // Le NPC est-il en feu ?
        protected Double moveSpeed; // La Vitesse de Déplacement du NPC
        protected HashMap<Integer, NPC.Hologram.Opacity> linesOpacity; // L'Opacité de chaques Lignes affichant du Texte par le NPC


        /**
         * On Instancie en privée de nouveaux {@link Attributes Attributs} pour le NPC.
         *
         * @param skin Le {@link Skin Skin} du NPC
         * @param parts Les {@link Skin.Part Parties du Skin} visibles du NPC
         * @param text Le ou les textes que le NPC affichera
         * @param slots En fonction de l'{@link EquipmentSlot Emplacement} du NPC, l'{@link ItemStack item} a affiché dessus.
         * @param collidable Le NPC a-t-il des collisions ?
         * @param hideDistance La Distance pour cacher le NPC au Joueur en question
         * @param glowing Le NPC est-il en surbrillance ?
         * @param glowingColor La {@link ChatFormatting Couleur de la surbrillance} du NPC
         * @param gazeTrackingType Le {@link GazeTrackingType type de suivie de regard} du NPC
         * @param customTabListName Le Nom Customisé du NPC
         * @param showOnTabList Peut-on voir le NPC dans le 'tablist'
         * @param interactCooldown Le Temps d'Intéraction avec le NPC
         * @param lineSpacing La distance du champ de vision du Joueur pour NPC
         * @param textAlignment L'{@link Vector Alignement} du texte affiché par le NPC
         * @param npcPose La {@link Pose Posture} du NPC
         * @param textOpacity L'{@link Hologram.Opacity Opacité} du texte affiché par le NPC
         * @param onFire Le NPC est-il en feu ?
         * @param moveSpeed La Vitesse de mouvement du NPC
         * @param linesOpacity L'Opacité de chaques lignes affichant du Texte par le NPC
         */
        private Attributes(NPC.Skin skin, NPC.Skin.Parts parts, List<String> text, HashMap<EquipmentSlot, ItemStack> slots, boolean collidable, Double hideDistance, boolean glowing,
                           ChatFormatting glowingColor, NPC.GazeTrackingType gazeTrackingType, String customTabListName, boolean showOnTabList, Long interactCooldown, Double lineSpacing,
                           Vector textAlignment, Pose npcPose, NPC.Hologram.Opacity textOpacity, boolean onFire, Double moveSpeed, HashMap<Integer, NPC.Hologram.Opacity> linesOpacity) {

            this.skin = skin; // Skin du NPC
            this.skinParts = parts; // Parties de Skin du NPC
            this.text = text; // Textes affichés par le NPC
            this.slots = slots; // Équipements du NPC
            this.collidable = collidable; // Les collisions du NPC
            this.hideDistance = hideDistance; // La Distance pour cacher le NPC
            this.glowing = glowing; // La Surbrillance du NPC
            this.glowingColor = glowingColor; // La Couleur de la Surbrillance du NPC
            this.gazeTrackingType = gazeTrackingType; // Le Type de Suivie du Regard du NPC
            this.customTabListName = customTabListName; // Le Nom Customisé du NPC
            this.showOnTabList = showOnTabList; // La Visibilité du NPC dans le 'tablist'
            this.interactCooldown = interactCooldown; // Le Temps d'Intéraction du NPC
            this.lineSpacing = lineSpacing; // Le Champ de Vision pour le Joueur avec le NPC
            this.textAlignment = textAlignment; // Le Texte d'Alignement du NPC
            this.pose = npcPose; // La Posture du NPC
            this.textOpacity = textOpacity; // L'Opacité du Texte affiché par le NPC
            this.onFire = onFire; // Le NPC en Feu ou pas
            this.moveSpeed = moveSpeed; // La Vitesse de Déplacement du NPC
            this.linesOpacity = linesOpacity; // L'Opacité de chaques Lignes affichant du Texte par le NPC

            // Si chaque emplacement d'équipements du NPC ne contiennent pas d'items spécifiques, on leur met de l'air (Soit : rien du tous)
            Arrays.stream(EquipmentSlot.values()).filter(x-> !slots.containsKey(x)).forEach(x-> slots.put(x, new ItemStack(Material.AIR)));
        }

        /**
         * On Instancie de nouveaux {@link Attributes Attributs} pour le NPC.
         *
         */
        @SuppressWarnings("unchecked")
        protected Attributes() {

            // ** On attribue chaque propriété du NPC ** //

            this.collidable = DEFAULT.isCollidable();
            this.text = DEFAULT.getText();
            this.hideDistance = DEFAULT.getHideDistance();
            this.glowing = DEFAULT.isGlowing();
            this.skin = DEFAULT.getSkin();
            this.skinParts = DEFAULT.getSkinParts().clone();
            this.glowingColor = DEFAULT.getGlowingColor();
            this.gazeTrackingType = DEFAULT.getGazeTrackingType();
            this.slots = (HashMap<EquipmentSlot, ItemStack>) DEFAULT.getSlots().clone();
            this.customTabListName = DEFAULT.getCustomTabListName();
            this.showOnTabList = DEFAULT.isShowOnTabList();
            this.pose = DEFAULT.getPose();
            this.lineSpacing = DEFAULT.getLineSpacing();
            this.textAlignment = DEFAULT.getTextAlignment().clone();
            this.interactCooldown = DEFAULT.getInteractCooldown();
            this.textOpacity = DEFAULT.getTextOpacity();
            this.onFire = DEFAULT.isOnFire();
            this.moveSpeed = DEFAULT.getMoveSpeed();
            this.linesOpacity = (HashMap<Integer, Hologram.Opacity>) DEFAULT.getLinesOpacity().clone();

            // ** On attribue chaque propriété du NPC ** //
        }

        /**
         * On Instancie de nouveaux {@link Attributes Attributs} pour un NPC spécifique.
         *
         * @param npc Le NPC en question
         *
         */
        @SuppressWarnings("unchecked")
        protected Attributes(NPC npc) {

            if(npc == null) return; // Si le NPC est null, on ne fait rien

            // ** On attribue chaque propriété du NPC récupéré ** //

            this.collidable = npc.getAttributes().isCollidable();
            this.text = npc.getAttributes().getText();
            this.hideDistance = npc.getAttributes().getHideDistance();
            this.glowing = npc.getAttributes().isGlowing();
            this.skin = npc.getAttributes().getSkin();
            this.skinParts = npc.getAttributes().getSkinParts();
            this.glowingColor = npc.getAttributes().getGlowingColor();
            this.gazeTrackingType = npc.getAttributes().getGazeTrackingType();
            this.slots = (HashMap<EquipmentSlot, ItemStack>) npc.getAttributes().getSlots().clone();
            this.customTabListName = npc.getAttributes().getCustomTabListName();
            this.showOnTabList = npc.getAttributes().isShowOnTabList();
            this.pose = npc.getAttributes().getPose();
            this.lineSpacing = npc.getAttributes().getLineSpacing();
            this.textAlignment = npc.getAttributes().getTextAlignment().clone();
            this.interactCooldown = npc.getAttributes().getInteractCooldown();
            this.textOpacity = npc.getAttributes().getTextOpacity();
            this.onFire = npc.getAttributes().isOnFire();
            this.moveSpeed = npc.getAttributes().getMoveSpeed();
            this.linesOpacity = (HashMap<Integer, Hologram.Opacity>) npc.getAttributes().getLinesOpacity().clone();

            // ** On attribue chaque propriété du NPC récupéré ** //
        }

        /**
         * Ajoute les attributs actuels à un {@link NPCPersonal NPC Personnel} précis.
         *
         * @param npc Le {@link NPCPersonal NPC Personnel} en question
         * @param forceUpdate Doit-on forcer la mise à jour du NPC ?
         */
        public void applyNPC(@Nonnull NPCPersonal npc, boolean forceUpdate) {

            applyNPC(npc); // On applique les attributs au NPC en question
            if(forceUpdate) npc.forceUpdate(); // Si le forçage de mise à jour a été activé, on force donc la mise à jour du NPC
        }

        /**
         * Ajoute les attributs actuels à un {@link NPCPersonal NPC Personnel} précis.
         *
         * @param npc Le {@link NPCPersonal NPC Personnel} en question
         */
        @SuppressWarnings("unchecked")
        public void applyNPC(@Nonnull NPCPersonal npc) {

            Validate.notNull(npc, "Impossible d'appliquer NPC.Attributes à un NPC nul."); // Si le NPC est null, on affiche une erreur

            npc.setSkin(this.skin); // On ajoute le Skin récupéré par l'attribut en question pour le NPC en question
            npc.setSkinParts(this.skinParts); // On ajoute les Parties de Skin récupéré par l'attribut en question pour le NPC en question
            npc.setCollidable(this.collidable); // On définit les collisions récupérées en attribut pour le NPC en question
            npc.setText(this.text); // On définit les Textes récupérés en attribut pour le NPC en question pour qu'il puisse l'affiché
            npc.setHideDistance(this.hideDistance); // On définit la distance pour cacher le NPC en question récupéré par l'attribut en question
            npc.setGlowing(this.glowing); // On définit la surbrillance du NPC en question récupéré par l'attribut en question
            npc.setGlowingColor(this.glowingColor); // On définit la Couleur de la surbrillance récupérée en attribut pour le NPC en question
            npc.setGazeTrackingType(this.gazeTrackingType); // On définit le Type de Suivie du Regard récupéré par l'attribut en question pour le NPC en question
            npc.setSlots((HashMap<EquipmentSlot, ItemStack>) this.slots.clone()); // On définit les Équipements récupérés en attribut pour le NPC en question
            npc.setCustomTabListName(this.customTabListName); // On définit le Nom Customisé récupéré par l'attribut en question pour le NPC en question
            npc.setShowOnTabList(this.showOnTabList); // On définit si le NPC en question peut être vue dans le 'tablist' par l'attribut en question récupérée
            npc.setPose(this.pose); // On définit la posture du NPC en question récupéré par l'attribut en question
            npc.setLineSpacing(this.lineSpacing); // On définit le Champ de Vision pour le Joueur associé du NPC en question récupéré par l'attribut en question
            npc.setTextAlignment(this.textAlignment.clone()); // On définit l'Alignement du Texte affiché par NPC en question récupéré par l'attribut en question
            npc.setInteractCooldown(this.interactCooldown); // On définit le Temps d'Intéraction avec le NPC en question récupéré par l'attribut en question
            npc.setTextOpacity(this.textOpacity); // On définit l'Opacité du Texte affiché par le NPC en question récupéré par l'attribut en question
            npc.setMoveSpeed(this.moveSpeed); // On définit la vitesse de mouvement du NPC en question récupéré par l'attribut en question
            npc.setOnFire(this.onFire); // On définit si le NPC en question est en feu par l'attribut en question récupérée

            // On définit l'Opacité de chaques Lignes affichant du Texte par le NPC en question récupéré par l'attribut en question
            npc.setLinesOpacity((HashMap<Integer, Hologram.Opacity>) this.linesOpacity.clone());
        }

        /**
         * Ajoute les attributs actuels à plusieurs {@link NPCPersonal NPC Personnels} précis.
         *
         * @param npc Les {@link NPCPersonal NPC Personnels} en question
         */
        public void applyNPC(@Nonnull Collection<NPCPersonal> npc) { applyNPC(npc, false); }

        /**
         * Ajoute les attributs actuels à plusieurs {@link NPCPersonal NPC Personnels} précis.
         *
         * @param npc Les {@link NPCPersonal NPC Personnels} en question
         * @param forceUpdate Doit-on forcer la mise à jour des NPCs ?
         */
        public void applyNPC(@Nonnull Collection<NPCPersonal> npc, boolean forceUpdate) {

            Validate.notNull(npc, "Impossible d'appliquer NPC.Attributes à un NPC nul."); // Si les NPCs sont null, on affiche une erreur
            npc.forEach(x-> applyNPC(x, forceUpdate)); // On force la mise à jour de chaque NPC
        }

        /**
         * Récupère les attributs par défaut.
         *
         * @return Les attributs par défaut
         */
        public static Attributes getDefault() { return DEFAULT; }

        /**
         * Récupère les attributs actuels d'un NPC en question.
         *
         * @param npc Le NPC en question
         *
         * @return Les attributs actuels d'un NPC en question
         */
        public static Attributes getNPCAttributes(@Nonnull NPC npc) {

            Validate.notNull(npc, "Impossible d'obtenir NPC.Attributes à partir d'un NPC nul."); // Si le NPC est null, on affiche une erreur
            return npc.getAttributes(); // Retourne les attributs du NPC
        }

        /**
         * Récupère l'Attribut du Skin.
         *
         * @return L'Attribut du Skin
         */
        public NPC.Skin getSkin() { return skin; }

        /**
         * Récupère l'Attribut des Parties du Skin visible.
         *
         * @return L'Attribut des Parties du Skin visible
         */
        public Skin.Parts getSkinParts() { return skinParts; }

        /**
         * Récupère l'Attribut du Skin Par Défaut.
         *
         * @return L'Attribut du Skin Par Défaut
         */
        public static NPC.Skin getDefaultSkin() { return DEFAULT.getSkin(); }

        /**
         * Change l'Attribut du Skin.
         *
         * @param skin Le nouveau Skin à attribuer
         *
         */
        protected void setSkin(@Nullable NPC.Skin skin) {

            if(skin == null) skin = NPC.Skin.getSteveSkin();
            this.skin = skin;
        }

        /**
         * Change l'Attribut des Parties du Skin visible.
         *
         * @param skinParts Les nouvelles Parties du Skin visible à attribuer
         *
         */
        protected void setSkinParts(@Nullable NPC.Skin.Parts skinParts) {

            if(skinParts == null) skinParts = new Skin.Parts();
            this.skinParts = skinParts;
        }

        /**
         * Change l'Attribut des Parties du Skin visible Par Défaut.
         *
         * @param skinParts Les nouvelles Parties du Skin visible Par Défaut à attribuer
         *
         */
        public static void setDefaultSkinParts(@Nullable NPC.Skin.Parts skinParts) { DEFAULT.setSkinParts(skinParts); }

        /**
         * Change l'Attribut du Skin visible Par Défaut.
         *
         * @param npcSkin Le nouveau Skin Par Défaut à attribuer
         *
         */
        public static void setDefaultSkin(@Nullable NPC.Skin npcSkin) { DEFAULT.setSkin(npcSkin); }

        /**
         * Récupère l'Attribut des/du Texte(s) à afficher.
         *
         * @return L'Attribut des/du Texte(s) à afficher
         */
        public List<String> getText() { return text; }

        /**
         * Récupère l'Attribut des/du Texte(s) à afficher Par Défaut.
         *
         * @return L'Attribut des/du Texte(s) à afficher Par Défaut
         */
        public static List<String> getDefaultText() { return DEFAULT.getText(); }

        /**
         * Change l'Attribut des/du Texte(s) à afficher.
         *
         * @param text Le/Les nouveau(x) Texte(s) à afficher à attribuer
         *
         */
        protected void setText(@Nullable List<String> text) {

            if(text == null) text = new ArrayList<>();
            this.text = text;
        }

        /**
         * Change l'Attribut des/du Texte(s) Par Défaut à afficher.
         *
         * @param text Le/Les nouveau(x) Texte(s) à afficher Par Défaut à attribuer
         *
         */
        public static void setDefaultText(@Nullable List<String> text) { DEFAULT.setText(text); }

        /**
         * Récupère l'Attribut des Équipements à porter.
         *
         * @return L'Attribut des Équipements à porter
         */
        protected HashMap<EquipmentSlot, ItemStack> getSlots() { return slots; }

        /** Récupère l'Attribut Par Défaut des Équipements à porter.
         *
         * @return L'Attribut Par Défaut des Équipements à porter
         */
        protected static HashMap<EquipmentSlot, ItemStack> getDefaultSlots() { return DEFAULT.getSlots(); }

        /**
         * Change l'Attribut des Équipements à porter.
         *
         * @param slots L'Attribut des Équipements en question (Il s'agit de : {@code HashMap<EquipmentSlot, ItemStack>})
         *
         */
        protected void setSlots(@Nonnull HashMap<EquipmentSlot, ItemStack> slots) { this.slots = slots; }

        /**
         * Change l'Attribut Par Défaut des Équipements à porter.
         *
         * @param slots L'Attribut Par Défaut des Équipements en question (Il s'agit de : {@code HashMap<EquipmentSlot, ItemStack>})
         *
         */
        protected static void setDefaultSlots(HashMap<EquipmentSlot, ItemStack> slots) { DEFAULT.setSlots(slots); }

        /**
         * Récupère l'Attribut de l'Équipement du casque.
         *
         * @return L'Attribut de l'Équipement du casque
         */
        public ItemStack getHelmet() { return getItem(EquipmentSlot.HEAD); }

        /**
         * Récupère l'Attribut Par Défaut de l'Équipement du casque.
         *
         * @return L'Attribut Par Défaut de l'Équipement du casque
         */
        public static ItemStack getDefaultHelmet() { return DEFAULT.getHelmet(); }

        /**
         * Change l'Attribut de l'Équipement du casque.
         *
         * @param itemStack L'Attribut de l'{@link ItemStack item} en question qui équipera le casque
         *
         */
        protected void setHelmet(@Nullable ItemStack itemStack) { setItem(EquipmentSlot.HEAD, itemStack); }

        /**
         * Change l'Attribut Par Défaut de l'Équipement du casque.
         *
         * @param itemStack L'Attribut Par Défaut de l'{@link ItemStack item} en question qui équipera le casque
         *
         */
        public static void setDefaultHelmet(@Nullable ItemStack itemStack) { DEFAULT.setHelmet(itemStack); }

        /**
         * Récupère l'Attribut de l'Équipement du plastron.
         *
         * @return L'Attribut de l'Équipement du plastron
         */
        public ItemStack getChestPlate() { return getItem(EquipmentSlot.CHEST); }

        /**
         * Récupère l'Attribut Par Défaut de l'Équipement du plastron.
         *
         * @return L'Attribut Par Défaut de l'Équipement du plastron
         */
        public static ItemStack getDefaultChestPlate() { return DEFAULT.getChestPlate(); }

        /**
         * Change l'Attribut de l'Équipement du plastron.
         *
         * @param itemStack L'Attribut de l'{@link ItemStack item} en question qui équipera le plastron
         *
         */
        protected void setChestPlate(@Nullable ItemStack itemStack) { setItem(EquipmentSlot.CHEST, itemStack); }

        /**
         * Change l'Attribut Par Défaut de l'Équipement du plastron.
         *
         * @param itemStack L'Attribut Par Défaut de l'{@link ItemStack item} en question qui équipera le plastron
         *
         */
        public static void setDefaultChestPlate(@Nullable ItemStack itemStack) { DEFAULT.setChestPlate(itemStack); }

        /**
         * Récupère l'Attribut de l'Équipement des jambières.
         *
         * @return L'Attribut de l'Équipement des jambières
         */
        public ItemStack getLeggings() { return getItem(EquipmentSlot.LEGS); }

        /**
         * Récupère l'Attribut Par Défaut de l'Équipement des jambières.
         *
         * @return L'Attribut Par Défaut de l'Équipement des jambières
         */
        public static ItemStack getDefaultLeggings() { return DEFAULT.getLeggings(); }

        /**
         * Change l'Attribut de l'Équipement des jambières.
         *
         * @param itemStack L'Attribut de l'{@link ItemStack item} en question qui équipera les jambières
         *
         */
        protected void setLeggings(@Nullable ItemStack itemStack) { setItem(EquipmentSlot.LEGS, itemStack); }

        /**
         * Change l'Attribut Par Défaut de l'Équipement des jambières.
         *
         * @param itemStack L'Attribut Par Défaut de l'{@link ItemStack item} en question qui équipera les jambières
         *
         */
        public static void setDefaultLeggings(@Nullable ItemStack itemStack) { DEFAULT.setLeggings(itemStack); }
        /**
         *
         * Récupère l'Attribut de l'Équipement des bottes.
         *
         * @return L'Attribut de l'Équipement des bottes
         */
        public ItemStack getBoots() { return getItem(EquipmentSlot.FEET); }

        /**
         * Récupère l'Attribut Par Défaut de l'Équipement des bottes.
         *
         * @return L'Attribut Par Défaut de l'Équipement des bottes
         */
        public static ItemStack getDefaultBoots() { return DEFAULT.getBoots(); }

        /**
         * Change l'Attribut de l'Équipement des bottes.
         *
         * @param itemStack L'Attribut de l'{@link ItemStack item} en question qui équipera les bottes
         *
         */
        protected void setBoots(@Nullable ItemStack itemStack) { setItem(EquipmentSlot.FEET, itemStack); }

        /**
         * Change l'Attribut Par Défaut de l'Équipement des bottes.
         *
         * @param itemStack L'Attribut Par Défaut de l'{@link ItemStack item} en question qui équipera les bottes
         *
         */
        public static void setDefaultBoots(@Nullable ItemStack itemStack) { DEFAULT.setBoots(itemStack); }

        /**
         * Change l'Attribut de l'{@link EquipmentSlot Équipement} demandé par l'{@link ItemStack item} demandé.
         *
         * @param itemStack L'Attribut de l'{@link EquipmentSlot Équipement} demandé par l'{@link ItemStack item} demandé.
         *
         */
        protected void setItem(@Nonnull EquipmentSlot slot, @Nullable ItemStack itemStack) {

            Validate.notNull(slot, "Échec de la mise en place de l'item, NPCSlot ne peut être nul."); // Si l'équipement récupéré est null, on affiche une erreur

            if(itemStack == null) itemStack = new ItemStack(Material.AIR); // Si l'Item est null, on équipe de l'air (Soit, rien du tous)
            slots.put(slot, itemStack); // On Ajoute l'item à équiper
        }

        /**
         * Change l'Attribut Par Défaut de l'{@link EquipmentSlot Équipement} demandé par l'{@link ItemStack item} demandé.
         *
         * @param itemStack L'Attribut Par Défaut de l'{@link EquipmentSlot Équipement} demandé par l'{@link ItemStack item} demandé.
         *
         */
        public static void setDefaultItem(@Nonnull EquipmentSlot slot, @Nullable ItemStack itemStack) { DEFAULT.setItem(slot, itemStack); }

        /**
         * Récupère l'Attribut de l'{@link ItemStack item} de l'{@link EquipmentSlot Équipement} demandé.
         *
         * @param slot l'{@link EquipmentSlot Équipement} à récupérer l'{@link ItemStack item}
         *
         * @return L'Attribut de l'{@link ItemStack item} de l'{@link EquipmentSlot Équipement} en question
         */
        public ItemStack getItem(@Nonnull EquipmentSlot slot) {

            Validate.notNull(slot, "Échec de l'obtention de l'item, NPCSlot ne peut être nul."); // Si l'équipement récupéré est null, on affiche une erreur
            return slots.get(slot); // On Récupère l'item de l'Équipement
        }

        /**
         * Récupère l'Attribut Par Défaut de l'{@link ItemStack item} de l'{@link EquipmentSlot Équipement} demandé.
         *
         * @param slot l'{@link EquipmentSlot Équipement} à récupérer l'{@link ItemStack item}
         *
         * @return L'Attribut Par Défaut de l'{@link ItemStack item} de l'{@link EquipmentSlot Équipement} en question
         */
        public static ItemStack getDefaultItem(@Nonnull EquipmentSlot slot) { return DEFAULT.getItem(slot); }

        /**
         * Récupère l'Attribut des collisions pour le NPC.
         *
         * @return L'Attribut des collisions pour le NPC
         */
        public boolean isCollidable() { return collidable; }

        /**
         * Récupère l'Attribut Par Défaut des collisions pour le NPC.
         *
         * @return L'Attribut Par Défaut des collisions pour le NPC
         */
        public static boolean isDefaultCollidable() { return DEFAULT.isCollidable(); }

        /**
         * Change l'Attribut des collisions pour le NPC.
         *
         * @param collidable L'Attribut des collisions pour le NPC (vrai ou faux ?)
         *
         */
        protected void setCollidable(boolean collidable) { this.collidable = collidable; }

        /**
         * Change l'Attribut Par Défaut des collisions pour le NPC.
         *
         * @param collidable L'Attribut Par Défaut des collisions pour le NPC (vrai ou faux ?)
         *
         */
        public static void setDefaultCollidable(boolean collidable) { DEFAULT.setCollidable(collidable); }

        /**
         * Récupère l'Attribut de la Distance quand le NPC sera plus visible.
         *
         * @return L'Attribut en question de la Distance quand le NPC sera plus visible
         */
        public Double getHideDistance() { return hideDistance; }

        /**
         * Récupère l'Attribut Par Défaut de la Distance quand le NPC sera plus visible.
         *
         * @return L'Attribut Par Défaut en question de la Distance quand le NPC sera plus visible
         */
        public static Double getDefaultHideDistance() { return DEFAULT.getHideDistance(); }

        /**
         * Change l'Attribut de la Distance quand le NPC sera plus visible
         * Lorsque le joueur est suffisamment loin, le NPC se cachera temporairement, pour être plus efficace.
         * Et lorsque le joueur s'approchera, le NPC se découvrira.
         *
         * @param hideDistance L'Attribut en question de la distance quand le NPC sera plus visible (en blocs)
         *
         * @see NPC.Attributes#getHideDistance()
         * @see NPC.Attributes#setDefaultHideDistance (double)
         * @see NPC.Attributes#getDefaultHideDistance()
         *
         */
        protected void setHideDistance(double hideDistance) {

            // Affiche une erreur si la distance est un nombre négatif
            Validate.isTrue(hideDistance > 0.00, "La distance cachée ne peut pas être négative ou égale à 0");

            this.hideDistance = hideDistance; // Change l'attribut de la distance
        }

        /**
         * Change l'Attribut Par Défaut de la Distance quand le NPC sera plus visible
         * Lorsque le joueur est suffisamment loin, le NPC se cachera temporairement, pour être plus efficace.
         * Et lorsque le joueur s'approchera, le NPC se découvrira.
         *
         * @param hideDistance L'Attribut Par Défaut en question de la distance quand le NPC sera plus visible (en blocs)
         *
         * @see NPC.Attributes#getHideDistance()
         * @see NPC.Attributes#setDefaultHideDistance (double)
         * @see NPC.Attributes#getDefaultHideDistance()
         *
         */
        public static void setDefaultHideDistance(double hideDistance) { DEFAULT.setHideDistance(hideDistance); }

        /**
         * Récupère l'Attribut de la surbrillance du NPC.
         *
         * @return L'Attribut de la surbrillance du NPC
         */
        public boolean isGlowing() { return glowing; }

        /**
         * Récupère l'Attribut Par Défaut de la surbrillance du NPC.
         *
         * @return L'Attribut Par Défaut de la surbrillance du NPC
         */
        public static boolean isDefaultGlowing() { return DEFAULT.isGlowing(); }

        /**
         * Change l'Attribut de la surbrillance du NPC.
         *
         * @param glowing L'Attribut de la surbrillance du NPC (vrai ou faux ?)
         *
         */
        protected void setGlowing(boolean glowing) { this.glowing = glowing; }

        /**
         * Change l'Attribut Par Défaut de la surbrillance du NPC.
         *
         * @param glowing L'Attribut Par Défaut de la surbrillance du NPC (vrai ou faux ?)
         *
         */
        public static void setDefaultGlowing(boolean glowing) { DEFAULT.setGlowing(glowing); }

        /**
         * Récupère l'Attribut de la {@link ChatFormatting couleur} de surbrillance du NPC.
         *
         * @return L'Attribut de la {@link ChatFormatting couleur} de surbrillance du NPC
         */
        public ChatFormatting getGlowingColor() { return this.glowingColor; }

        /**
         * Récupère l'Attribut Par Défaut de la {@link ChatFormatting couleur} de surbrillance du NPC.
         *
         * @return L'Attribut Par Défaut de la {@link ChatFormatting couleur} de surbrillance du NPC
         */
        public static ChatFormatting getDefaultGlowingColor() { return DEFAULT.getGlowingColor(); }

        /**
         * Change l'Attribut de la {@link ChatFormatting couleur} de surbrillance du NPC.
         *
         * @param color L'Attribut de la {@link ChatFormatting couleur} de surbrillance du NPC
         *
         */
        protected void setGlowingColor(@Nullable ChatFormatting color) {

            if(color == null) color = ChatFormatting.WHITE; // Si la couleur est null, on définit la couleur sur blanc.

            // Si la couleur n'est pas une couleur, on affiche une erreur
            Validate.isTrue(color.isColor(), "Erreur de réglage de la couleur de l'éclat. Ce n'est pas une couleur.");
            this.glowingColor = color; // Ajoute la couleur de surbrillance pour le NPC
        }

        /**
         * Change l'Attribut Par Défaut de la {@link ChatFormatting couleur} de surbrillance du NPC.
         *
         * @param color L'Attribut Par Défaut de la {@link ChatFormatting couleur} de surbrillance du NPC
         *
         */
        public static void setDefaultGlowingColor(@Nullable ChatFormatting color) { DEFAULT.setGlowingColor(color); }

        /**
         * Récupère l'Attribut du {@link GazeTrackingType type de suivie du regard} du NPC.
         *
         * @return L'Attribut du {@link GazeTrackingType type de suivie du regard} du NPC
         */
        public NPC.GazeTrackingType getGazeTrackingType() { return gazeTrackingType; }

        /**
         * Récupère l'Attribut Par Défaut du {@link GazeTrackingType type de suivie du regard} du NPC.
         *
         * @return L'Attribut Par Défaut du {@link GazeTrackingType type de suivie du regard} du NPC
         */
        public static NPC.GazeTrackingType getDefaultGazeTrackingType() { return DEFAULT.getGazeTrackingType(); }

        /**
         * Change l'Attribut du {@link GazeTrackingType type de suivie du regard} du NPC.
         *
         * @param gazeTrackingType L'Attribut en question du {@link GazeTrackingType type de suivie du regard} du NPC
         *
         */
        protected void setGazeTrackingType(@Nullable NPC.GazeTrackingType gazeTrackingType) {

            if(gazeTrackingType == null) gazeTrackingType = NPC.GazeTrackingType.NONE;
            this.gazeTrackingType = gazeTrackingType;
        }

        /**
         * Change l'Attribut Par Défaut du {@link GazeTrackingType type de suivie du regard} du NPC.
         *
         * @param followLookType L'Attribut Par Défaut en question du {@link GazeTrackingType type de suivie du regard} du NPC
         *
         */
        public static void setDefaultGazeTrackingType(@Nullable NPC.GazeTrackingType followLookType) { DEFAULT.setGazeTrackingType(followLookType); }

        /**
         * Récupère l'Attribut du Nom Customisé du NPC dans le 'tablist'.
         *
         * @return L'Attribut du Nom Customisé du NPC dans le 'tablist'
         */
        public String getCustomTabListName() { return customTabListName; }

        /**
         * Récupère l'Attribut Par Défaut du Nom Customisé du NPC dans le 'tablist'.
         *
         * @return L'Attribut Par Défaut du Nom Customisé du NPC dans le 'tablist'
         */
        public static String getDefaultTabListName() { return DEFAULT.getCustomTabListName(); }

        /**
         * Change l'Attribut du Nom Customisé du NPC dans le 'tablist'.
         *
         * @param customTabListName L'Attribut en question du Nom Customisé du NPC dans le 'tablist'
         *
         */
        protected void setCustomTabListName(String customTabListName) {

            if(customTabListName == null) customTabListName = DEFAULT.getCustomTabListName();
            this.customTabListName = customTabListName;
        }

        /**
         * Change l'Attribut Par Défaut du Nom Customisé du NPC dans le 'tablist'.
         *
         * @param customTabListName L'Attribut Par Défaut en question du Nom Customisé du NPC dans le 'tablist'
         *
         */
        public static void setDefaultCustomTabListName(@Nonnull String customTabListName) {

            Validate.isTrue(customTabListName.contains("{id}"), "L'attribut de nom de la liste d'onglets personnalisés doit contenir l'espace réservé {id}.");
            DEFAULT.setCustomTabListName(customTabListName);
        }

        /**
         * Récupère l'Attribut du NPC étant affiché dans le 'tablist'.
         *
         * @return L'Attribut du NPC étant affiché dans le 'tablist'
         */
        public boolean isShowOnTabList() { return showOnTabList; }

        /**
         * Récupère l'Attribut Par Défaut du NPC étant affiché dans le 'tablist'.
         *
         * @return L'Attribut Par Défaut du NPC étant affiché dans le 'tablist'
         */
        public boolean isDefaultShowOnTabList() { return DEFAULT.isShowOnTabList(); }

        /**
         * Change l'Attribut du NPC étant affiché dans le 'tablist'.
         *
         * @param showOnTabList L'Attribut en question du NPC étant affiché dans le 'tablist'
         *
         */
        protected void setShowOnTabList(boolean showOnTabList) { this.showOnTabList = showOnTabList; }

        /**
         * Change l'Attribut Par Défaut du NPC étant affiché dans le 'tablist'.
         *
         * @param showOnTabList L'Attribut Par Défaut en question du NPC étant affiché dans le 'tablist'
         *
         */
        public static void setDefaultShowOnTabList(boolean showOnTabList) { DEFAULT.setShowOnTabList(showOnTabList); }

        /**
         * Récupère l'Attribut du Temps d'Intéraction avec le NPC.
         *
         * @return L'Attribut du Temps d'Intéraction avec le NPC
         */
        public Long getInteractCooldown() { return interactCooldown; }

        /**
         * Récupère l'Attribut Par Défaut du Temps d'Intéraction avec le NPC.
         *
         * @return L'Attribut Par Défaut du Temps d'Intéraction avec le NPC
         */
        public static Long getDefaultInteractCooldown() { return DEFAULT.getInteractCooldown(); }

        /**
         * Change l'Attribut en question du Temps d'Intéraction avec le NPC.
         *
         * @param milliseconds L'Attribut en question du Temps d'Intéraction avec le NPC
         *
         */
        protected void setInteractCooldown(long milliseconds) {

            Validate.isTrue(milliseconds >= 0, "Erreur dans le réglage de l'interaction avec le cooldown, ne peut pas être négatif.");
            this.interactCooldown = milliseconds;
        }

        /**
         * Change l'Attribut Par Défaut en question du Temps d'Intéraction avec le NPC.
         *
         * @param interactCooldown L'Attribut Par Défaut en question du Temps d'Intéraction avec le NPC
         *
         */
        public static void setDefaultInteractCooldown(long interactCooldown) { DEFAULT.setInteractCooldown(interactCooldown); }

        /**
         * Récupère l'Attribut du Champ de Vision du Joueur pour le NPC.
         *
         * @return L'Attribut du Champ de Vision du Joueur pour le NPC
         */
        public Double getLineSpacing() { return lineSpacing; }

        /**
         * Récupère l'Attribut Par Défaut du Champ de Vision du Joueur pour le NPC.
         *
         * @return L'Attribut Par Défaut du Champ de Vision du Joueur pour le NPC
         */
        public static Double getDefaultLineSpacing() { return DEFAULT.getLineSpacing(); }

        /**
         * Change l'Attribut du Champ de Vision du Joueur pour le NPC.
         *
         * @param lineSpacing L'Attribut en question du Champ de Vision du Joueur pour le NPC
         *
         */
        protected void setLineSpacing(double lineSpacing) {

            if(lineSpacing < NPC.Attributes.VARIABLE_MIN_LINE_SPACING) lineSpacing = NPC.Attributes.VARIABLE_MIN_LINE_SPACING;
            else if(lineSpacing > NPC.Attributes.VARIABLE_MAX_LINE_SPACING) lineSpacing = NPC.Attributes.VARIABLE_MAX_LINE_SPACING;
            this.lineSpacing = lineSpacing;
        }

        /**
         * Change l'Attribut Par Défaut du Champ de Vision du Joueur pour le NPC.
         *
         * @param lineSpacing L'Attribut Par Défaut en question du Champ de Vision du Joueur pour le NPC
         *
         */
        public static void setDefaultLineSpacing(double lineSpacing) { DEFAULT.setLineSpacing(lineSpacing); }

        /**
         * Récupère l'Attribut de l'Alignment du {@link Hologram Texte} affiché par le NPC.
         *
         * @return L'Attribut de l'Alignment du {@link Hologram Texte} affiché par le NPC
         */
        public Vector getTextAlignment() { return textAlignment; }

        /**
         * Récupère l'Attribut Par Défaut de l'Alignment du {@link Hologram Texte} affiché par le NPC.
         *
         * @return L'Attribut Par Défaut de l'Alignment du {@link Hologram Texte} affiché par le NPC
         */
        public static Vector getDefaultTextAlignment() { return DEFAULT.getTextAlignment(); }

        /**
         * Change l'Attribut de l'Alignment du {@link Hologram Texte} affiché par le NPC.
         *
         * @param vector L'Attribut en question de l'{@link Vector Alignment} du {@link Hologram Texte} affiché par le NPC
         */
        protected void setTextAlignment(Vector vector) {

            if(vector == null) vector = DEFAULT.getTextAlignment(); // Si le vecteur récupéré est null, on récupère l'alignement par défaut

            /* Si la coordonnée 'X' du vecteur récupéré est supérieure à l'Alignement des coordonnées 'X' et 'Z' du Texte affiché du NPC alors,
               on définit sa coordonnée 'X' par les coordonnées 'X' et 'Z' du Texte affiché du NPC */
            if(vector.getX() > NPC.Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_XZ) vector.setX(Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_XZ);

            /* Sinon, si la coordonnée 'X' du vecteur récupéré est inférieure à l'Alignement des coordonnées 'X' et 'Z' du Texte affiché du NPC alors,
               on définit sa coordonnée 'X' par moins les coordonnées 'X' et 'Z' du Texte affiché du NPC */
            else if(vector.getX() < -NPC.Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_XZ) vector.setX(-Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_XZ);

            /* --------------------------------------------------- */

             /* Si la coordonnée 'Y' du vecteur récupéré est supérieure à l'Alignement de la coordonnée 'Y' du Texte affiché du NPC alors,
                on définit sa coordonnée 'Y' par la coordonnée 'Y' du Texte affiché du NPC */
            if(vector.getY() > NPC.Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_Y) vector.setY(Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_Y);

            /* Sinon, si la coordonnée 'Y' du vecteur récupéré est inférieure à l'Alignement de la coordonnée 'Y' du Texte affiché du NPC alors,
                on définit sa coordonnée 'Y' par moins la coordonnée 'Y' du Texte affiché du NPC */
            else if(vector.getY() < -NPC.Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_Y) vector.setY(-Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_Y);

            /* --------------------------------------------------- */

            /* Si la coordonnée 'Z' du vecteur récupéré est supérieure à l'Alignement des coordonnées 'X' et 'Z' du Texte affiché du NPC alors,
               on définit sa coordonnée 'Z' par les coordonnées 'X' et 'Z' du Texte affiché du NPC */
            if(vector.getZ() > NPC.Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_XZ) vector.setZ(Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_XZ);

            /* Sinon, si la coordonnée 'Z' du vecteur récupéré est inférieure à l'Alignement des coordonnées 'X' et 'Z' du Texte affiché du NPC alors,
               on définit sa coordonnée 'Z' par moins les coordonnées 'X' et 'z' du Texte affiché du NPC */
            else if(vector.getZ() < -NPC.Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_XZ) vector.setZ(-Attributes.VARIABLE_MAX_TEXT_ALIGNMENT_XZ);

            this.textAlignment = vector;
        }

        /**
         * Change l'Attribut Par Défaut de l'Alignment du {@link Hologram Texte} affiché par le NPC.
         *
         * @param textAlignment L'Attribut Par Défaut en question de l'{@link Vector Alignment} du {@link Hologram Texte} affiché par le NPC
         *
         */
        public static void setDefaultTextAlignment(Vector textAlignment) { DEFAULT.setTextAlignment(textAlignment); }

        /**
         * Récupère l'Attribut Par Défaut de l'{@link Hologram.Opacity Opacité} du {@link Hologram Texte} affiché par le NPC.
         *
         * @return L'Attribut Par Défaut de l'{@link Hologram.Opacity Opacité} du {@link Hologram Texte} affiché par le NPC
         */
        public static NPC.Hologram.Opacity getDefaultTextOpacity() { return DEFAULT.getTextOpacity(); }

        /**
         * Récupère l'Attribut de l'{@link Hologram.Opacity Opacité} du {@link Hologram Texte} affiché par le NPC.
         *
         * @return L'Attribut de l'{@link Hologram.Opacity Opacité} du {@link Hologram Texte} affiché par le NPC
         */
        public NPC.Hologram.Opacity getTextOpacity() { return textOpacity; }

        /**
         * Change l'Attribut Par Défaut de l'{@link Hologram.Opacity Opacité} du {@link Hologram Texte} affiché par le NPC.
         *
         * @param textOpacity L'Attribut Par Défaut en question de l'{@link Hologram.Opacity Opacité} du {@link Hologram Texte} affiché par le NPC
         *
         */
        public static void setDefaultTextOpacity(@Nullable NPC.Hologram.Opacity textOpacity) { DEFAULT.setTextOpacity(textOpacity); }

        /**
         * Change l'Attribut de l'{@link Hologram.Opacity Opacité} du {@link Hologram Texte} affiché par le NPC.
         *
         * @param textOpacity L'Attribut en question de l'{@link Hologram.Opacity Opacité} du {@link Hologram Texte} affiché par le NPC
         *
         */
        protected void setTextOpacity(@Nullable NPC.Hologram.Opacity textOpacity) {

            // Si l'Opacité du Texte affiché par le NPC est null, on lui définit une Opacité normale (taille 1)
            if(textOpacity == null) textOpacity = NPC.Hologram.Opacity.LOWEST;

            this.textOpacity = textOpacity; // Attribut l'Opacité du Texte affiché par le NPC
        }

        /**
         * Récupère l'Attribut de l'{@link Hologram.Opacity Opacité} de chaques Lignes affichant du {@link Hologram Texte} par le NPC.
         *
         * @return L'Attribut de l'{@link Hologram.Opacity Opacité} de chaques Lignes affichant du {@link Hologram Texte} par le NPC
         */
        protected HashMap<Integer, Hologram.Opacity> getLinesOpacity() { return linesOpacity; }

        /**
         * Récupère l'Attribut de l'{@link Hologram.Opacity Opacité} d'une Ligne demandé affichant du {@link Hologram Texte} par le NPC.
         *
         * @param line La Ligne en question à récupérer l'Attribut de l'{@link Hologram.Opacity Opacité}
         *
         * @return L'Attribut de l'{@link Hologram.Opacity Opacité} de la Ligne demandé affichant du {@link Hologram Texte} par le NPC
         */
        public Hologram.Opacity getLineOpacity(int line) { return linesOpacity.getOrDefault(line, Hologram.Opacity.LOWEST); }

        /**
         * Change l'Attribut de l'{@link Hologram.Opacity Opacité} d'une Ligne demandé affichant du {@link Hologram Texte} par le NPC.
         *
         * @param line La Ligne en question à changer l'Attribut de l'{@link Hologram.Opacity Opacité}
         * @param opacity L'{@link Hologram.Opacity Opacité} en question de la Ligne demandé précédemment affichant du {@link Hologram Texte} par le NPC
         *
         */
        public void setLineOpacity(int line, Hologram.Opacity opacity) {

            // Si l'Opacité de la Ligne affichant du Texte par le NPC est null, on lui définit une Opacité normale (taille 1)
            if(textOpacity == null) textOpacity = NPC.Hologram.Opacity.LOWEST;

            this.linesOpacity.put(line, opacity); // Attribut l'Opacité de la Ligne affichant du Texte par le NPC
        }

        /**
         * Change l'Attribut de l'{@link Hologram.Opacity Opacité} de chaques Lignes affichant du {@link Hologram Texte} par le NPC.
         *
         * @param linesOpacity L'Attribut en question de l'{@link Hologram.Opacity Opacité} de chaques Lignes affichant du {@link Hologram Texte} par le NPC
         *
         */
        protected void setLinesOpacity(HashMap<Integer, Hologram.Opacity> linesOpacity) { this.linesOpacity = linesOpacity; }

        /**
         * Redéfinit Par Défaut l'Attribut de l'{@link Hologram.Opacity Opacité} d'une Ligne demandé affichant du {@link Hologram Texte} par le NPC.
         *
         * @param line La Ligne en question à remettre Par Défaut l'Attribut de l'{@link Hologram.Opacity Opacité} associé
         *
         */
        public void resetLineOpacity(int line) {  linesOpacity.remove(line); }

        /**
         * Redéfinit Par Défaut l'Attribut de l'{@link Hologram.Opacity Opacité} de chaques Lignes affichant du {@link Hologram Texte} par le NPC.
         *
         */
        public void resetLinesOpacity() { linesOpacity = new HashMap<>(); }

        /**
         * Récupère l'Attribut de la {@link Pose Posture} du NPC.
         *
         * @return L'Attribut de la {@link Pose Posture} du NPC
         */
        public Pose getPose() { return pose; }

        /**
         * Récupère l'Attribut Par Défaut de la {@link Pose Posture} du NPC.
         *
         * @return L'Attribut Par Défaut de la {@link Pose Posture} du NPC
         */
        public static Pose getDefaultPose() { return DEFAULT.getPose(); }

        /**
         * Change l'Attribut de la {@link Pose Posture} du NPC.
         *
         * @param pose L'Attribut en question de la {@link Pose Posture} du NPC
         *
         */
        protected void setPose(@Nullable Pose pose) {

            if(pose == null) pose = Pose.STANDING; // Si la Posture est null, on lève le NPC
            this.pose = pose; // Attribut la Posture au NPC
        }

        /**
         * Change l'Attribut Par Défaut de la {@link Pose Posture} du NPC.
         *
         * @param npcPose L'Attribut Par Défaut en question de la {@link Pose Posture} du NPC
         *
         */
        public static void setDefaultPose(@Nullable Pose npcPose) { DEFAULT.setPose(npcPose); }

        /**
         * Récupère l'Attribut du NPC étant en Feu.
         *
         * @return L'Attribut du NPC étant en Feu
         */
        public boolean isOnFire() { return onFire; }

        /**
         * Récupère l'Attribut Par Défaut du NPC étant en Feu.
         *
         * @return L'Attribut Par Défaut du NPC étant en Feu
         */
        public static boolean isDefaultOnFire() { return DEFAULT.isOnFire(); }

        /**
         * Change l'Attribut du NPC étant en Feu.
         *
         * @param onFire L'Attribut en question du NPC étant en Feu (Vrai ou Faux ?)
         */
        protected void setOnFire(boolean onFire) { this.onFire = onFire; }

        /**
         * Change l'Attribut Par Défaut du NPC étant en Feu.
         *
         * @param onFire L'Attribut Par Défaut en question du NPC étant en Feu (Vrai ou Faux ?)
         */
        public static void setDefaultOnFire(boolean onFire) { DEFAULT.setOnFire(onFire); }

        /**
         * Récupère l'Attribut de la Vitesse de Déplacement du NPC.
         *
         * @return L'Attribut de la Vitesse de Déplacement du NPC
         */
        public double getMoveSpeed() { return moveSpeed; }

        /**
         * Récupère l'Attribut Par Défaut de la Vitesse de Déplacement du NPC.
         *
         * @return L'Attribut Par Défaut de la Vitesse de Déplacement du NPC
         */
        public static Double getDefaultMoveSpeed() { return DEFAULT.getMoveSpeed(); }

        /**
         * Change l'Attribut de la Vitesse de Déplacement du NPC.
         *
         * @param moveSpeed L'Attribut en question de la Vitesse de Déplacement du NPC
         *
         */
        protected void setMoveSpeed(double moveSpeed) {

            if(moveSpeed <= 0.00) moveSpeed = 0.1; // Si la Vitesse de Déplacement a été défini sur 0, on la définit à 0.1
            this.moveSpeed = moveSpeed; // Attribut la vitesse de Déplacement pour le NPC
        }

        /**
         * Change l'Attribut de la {@link Move.Speed Vitesse de Déplacement} du NPC.
         *
         * @param moveSpeed L'Attribut en question de la {@link Move.Speed Vitesse de Déplacement} du NPC
         */
        protected void setMoveSpeed(@Nullable Move.Speed moveSpeed) {

            if(moveSpeed == null) moveSpeed = Move.Speed.NORMAL; // Si la Vitesse de déplacement est null, on définit la vitesse à normal
            setMoveSpeed(moveSpeed.doubleValue());  // Attribut la vitesse de Déplacement pour le NPC
        }
    }

    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */

    /**
     * ÉVÈNEMENT POUR LE NPC
     *
     */
    public static class Events {

        /**
         * Constructeur de la 'class' {@link Events}
         */
        private Events() {}

        /* --------------------------------- */

        /**
         * LA PARTIE ÉVÈNEMENT GLOBAL DE L'ÉVÈNEMENT
         *
         * @since 2022.2
         */
        protected abstract static class Event extends org.bukkit.event.Event {

            private static final HandlerList HANDLERS_LIST = new HandlerList(); // La liste de gestionnaire d'évènement
            private final NPC npc; // Variable récupérant le NPC qui sera la cause de l'évènement

            /**
             * On instancie un nouvel {@link Event évènement}.
             *
             * @param npc Le NPC qui sera la cause de l'évènement
             */
            protected Event(NPC npc) { this.npc = npc; /* Initialise le NPC */ }

            /**
             * Récupère le NPC qui sera la cause de l'{@link Event évènement}.
             *
             * @return le NPC qui sera la cause de l'{@link Event évènement}
             */
            public NPC getNPC() { return npc; }

            /**
             * Récupère les gestionnaires d'{@link Event évènements}.
             *
             * @return Les gestionnaires d'{@link Event évènements}
             */
            public static HandlerList getHandlerList() { return HANDLERS_LIST; }


            /**
             * Récupère les gestionnaires d'{@link Event évènements}.
             *
             * @return Les gestionnaires d'{@link Event évènements}
             */
            @Override
            public HandlerList getHandlers() { return HANDLERS_LIST; }

            /* ------------------- */
            /* ------------------- */

            /**
             * LE JOUEUR QUI SUBIRA L'ÉVÈNEMENT
             *
             */
            protected abstract static class Player extends NPC.Events.Event {

                private final org.bukkit.entity.Player player; // Variable récupérant le Joueur qui subira l'évènement

                /**
                 * On instancie un nouveau {@link Player Joueur} étant la cible de l'évènement.
                 *
                 * @param player le Joueur en question
                 * @param npc Le NPC qui sera la cause de l'évènement
                 */
                protected Player(org.bukkit.entity.Player player, NPCPersonal npc) {

                    super(npc); // On instancie la 'class' parente
                    this.player = player; // On initialise le Joueur
                }

                /**
                 * Récupère le NPC qui sera la cause de l'{@link Event évènement} pour le joueur.
                 *
                 * @return le NPC qui sera la cause de l'{@link Event évènement}
                 */
                @Override
                public NPCPersonal getNPC() { return (NPCPersonal)super.getNPC(); }

                /**
                 * Récupère le Joueur cible l'{@link Event évènement}.
                 *
                 * @return le Joueur qui subira l'{@link Event évènement}
                 */
                public org.bukkit.entity.Player getPlayer() { return player; }

            }
        }

        /* --------------------------------- */

        /**
         * LA PARTIE ÉVÈNEMENT DE DÉBUT DE MOUVEMENT DU NPC
         *
         */
        public static class StartMove extends Event implements Cancellable {

            private final Location start; // Variable récupérant la localisation de départ
            private final Location end; // Variable récupérant la localisation d'arrivée
            private final int taskID; // Variable récupérant l'identifiant de la tâche du mouvement
            private boolean isCancelled; // Variable vérifiant si l'évènement a été annulé

            /**
             * On instancie un nouvel {@link StartMove évènement} de fin de mouvement du NPC.
             *
             * @param npc Le NPC qui sera la cause de l'évènement
             * @param start La {@link Location localisation} de départ du mouvement
             * @param end La {@link Location localisation} d'arrivée du mouvement
             * @param taskID L'Identifiant de la tâche du mouvement
             */
            protected StartMove(NPC npc, Location start, Location end, int taskID) {

                super(npc); // On instancie la 'class' parente

                this.start = start; // On Initialise la localisation de départ du mouvement
                this.end = end; // On Initialise la localisation d'arrivée du mouvement
                this.taskID = taskID; // On Initialise l'identifiant de la tâche du mouvement
                this.isCancelled = false; // On Initialise l'annulation de l'évènement

                Bukkit.getServer().getPluginManager().callEvent(this); // On fait appel à l'évènement dans la gestion de Plugin
            }

            /**
             * Récupère la {@link Location localisation} de départ du mouvement de l'{@link StartMove évènement} du NPC.
             *
             * @return la {@link Location localisation} de départ du mouvement de l'{@link StartMove évènement} du NPC
             */
            public Location getStart() { return start; }

            /**
             * Récupère la {@link Location localisation} d'arrivée du mouvement de l'{@link StartMove évènement} du NPC.
             *
             * @return la {@link Location localisation} d'arrivée du mouvement de l'{@link StartMove évènement} du NPC
             */
            public Location getEnd() { return end; }

            /**
             * Récupère l'identifiant de la tâche du mouvement de l'{@link StartMove évènement} du NPC.
             *
             * @return l'identifiant de la tâche du mouvement de l'{@link StartMove évènement} du NPC
             */
            public int getTaskID() { return taskID; }

            /**
             * Vérifie si l'{@link StartMove évènement} du NPC a été annulé.
             *
             * @return Une valeur Booléenne
             */
            @Override
            public boolean isCancelled() { return isCancelled; }

            /**
             * Définit l'annulation de l'{@link StartMove évènement} du NPC.
             *
             * @param arg Voulez-vous annuler cet {@link StartMove évènement} ?
             *
             */
            @Override
            public void setCancelled(boolean arg) { isCancelled = arg; }

        }

        /* --------------------------------- */

        /**
         * LA PARTIE ÉVÈNEMENT DE MOUVEMENT DU NPC
         *
         */
        public static class Move extends Event implements Cancellable {

            private final Location to; // Variable récupérant la localisation d'arrivée du déplacement du NPC
            private boolean isCancelled; // Variable vérifiant si l'évènement a été annulé

            /**
             * On instancie un nouvel {@link Move évènement} de mouvement du NPC.
             *
             * @param npc Le NPC qui sera la cause de l'évènement
             * @param to La {@link Location localisation} d'arrivée du déplacement
             *
             */
            protected Move(NPC npc, Location to) {

                super(npc); // On instancie la 'class' parente

                this.to = to; // On Initialise la localisation d'arrivée du déplacement
                this.isCancelled = false; // On Initialise l'annulation de l'évènement

                Bukkit.getServer().getPluginManager().callEvent(this); // On fait appel à l'évènement dans la gestion de Plugin
            }

            /**
             * Récupère la {@link Location localisation} de départ (actuel) du NPC.
             *
             * @return La {@link Location localisation} de départ (actuel) du NPC.
             */
            public Location getFrom() { return getNPC().getLocation(); }

            /**
             * Récupère la {@link Location localisation} d'arrivée (une fois déplacé) du NPC.
             *
             * @return La {@link Location localisation} d'arrivée (une fois déplacé) du NPC.
             */
            public Location getTo() { return to; }

            /**
             * Vérifie si l'{@link Move évènement} du NPC a été annulé.
             *
             * @return Une valeur Booléenne
             */
            @Override
            public boolean isCancelled() { return isCancelled; }

            /**
             * Définit l'annulation de l'{@link Move évènement} du NPC.
             *
             * @param arg Voulez-vous annuler cet {@link Move évènement} ?
             *
             */
            @Override
            public void setCancelled(boolean arg) { isCancelled = arg; }

        }

        /* --------------------------------- */

        /**
         * LA PARTIE ÉVÈNEMENT DE FIN DE MOUVEMENT DU NPC
         *
         */
        public static class FinishMove extends NPC.Events.Event {

            private final Location start; // Variable récupérant la localisation de départ
            private final Location end; // Variable récupérant la localisation d'arrivée
            private final int taskID; // Variable récupérant l'identifiant de la tâche du mouvement
            private final NPC.Move.Task.CancelCause cancelCause; // Variable récupérant la cause de l'annulation du mouvement

            /**
             * On instancie un nouvel {@link FinishMove évènement} de fin de mouvement du NPC.
             *
             * @param npc Le NPC qui sera la cause de l'évènement
             * @param start La {@link Location localisation} de départ du mouvement
             * @param end La {@link Location localisation} d'arrivée du mouvement
             * @param taskID L'Identifiant de la tâche du mouvement
             * @param cancelCause La {@link NPC.Move.Task.CancelCause cause} de l'annulation du mouvement
             */
            protected FinishMove(NPC npc, Location start, Location end, int taskID, NPC.Move.Task.CancelCause cancelCause) {

                super(npc); // On instancie la 'class' parente

                this.start = start; // On Initialise la localisation de départ du mouvement
                this.end = end; // On Initialise la localisation d'arrivée du mouvement
                this.taskID = taskID; // On Initialise l'identifiant de la tâche du mouvement
                this.cancelCause = cancelCause; // On Initialise la cause de l'annulation du mouvement

                Bukkit.getServer().getPluginManager().callEvent(this); // On fait appel à l'évènement dans la gestion de Plugin
            }

            /**
             * Récupère la {@link Location localisation} de départ du mouvement de l'{@link FinishMove évènement} du NPC.
             *
             * @return la {@link Location localisation} de départ du mouvement de l'{@link FinishMove évènement} du NPC
             */
            public Location getStart() { return start; }

            /**
             * Récupère la {@link Location localisation} d'arrivée du mouvement de l'{@link FinishMove évènement} du NPC.
             *
             * @return la {@link Location localisation} d'arrivée du mouvement de l'{@link FinishMove évènement} du NPC
             */
            public Location getEnd() { return end; }

            /**
             * Récupère l'identifiant de la tâche du mouvement de l'{@link FinishMove évènement} du NPC.
             *
             * @return l'identifiant de la tâche du mouvement de l'{@link FinishMove évènement} du NPC
             */
            public int getTaskID() { return taskID; }

            /**
             * Récupère la {@link NPC.Move.Task.CancelCause cause} de l'annulation du mouvement de l'{@link FinishMove évènement} du NPC.
             *
             * @return la {@link NPC.Move.Task.CancelCause cause} de l'annulation du mouvement de l'{@link FinishMove évènement} du NPC
             */
            public NPC.Move.Task.CancelCause getCancelCause() { return cancelCause; }

        }

        /* --------------------------------- */

        /**
         * LA PARTIE ÉVÈNEMENT DE TÉLÉPORTATION DU NPC
         *
         */
        public static class Teleport extends Event implements Cancellable {

            private final Location to; // Variable récupérant la localisation d'arrivée de la téléportation du NPC
            private boolean isCancelled; // Variable vérifiant si l'évènement a été annulé

            /**
             * On instancie un nouvel {@link Teleport évènement} de téléportation du NPC.
             *
             * @param npc Le NPC qui sera la cause de l'évènement
             * @param to La {@link Location localisation} d'arrivée du déplacement
             *
             */
            protected Teleport(NPC npc, Location to) {

                super(npc); // On instancie la 'class' parente

                this.to = to; // On Initialise la localisation d'arrivée de la téléportation
                this.isCancelled = false; // On Initialise l'annulation de l'évènement

                Bukkit.getServer().getPluginManager().callEvent(this); // On fait appel à l'évènement dans la gestion de Plugin
            }

            /**
             * Récupère la {@link Location localisation} de départ (actuel) du NPC.
             *
             * @return La {@link Location localisation} de départ (actuel) du NPC.
             */
            public Location getFrom() { return getNPC().getLocation(); }

            /**
             * Récupère la {@link Location localisation} d'arrivée (une fois téléporté) du NPC.
             *
             * @return La {@link Location localisation} d'arrivée (une fois téléporté) du NPC.
             */
            public Location getTo() { return to; }

            /**
             * Vérifie si l'{@link Teleport évènement} du NPC a été annulé.
             *
             * @return Une valeur Booléenne
             */
            @Override
            public boolean isCancelled() { return isCancelled; }

            /**
             * Définit l'annulation de l'{@link Teleport évènement} du NPC.
             *
             * @param arg Voulez-vous annuler cet {@link Teleport évènement} ?
             *
             */
            @Override
            public void setCancelled(boolean arg) { isCancelled = arg; }

        }

        /* --------------------------------- */

        /**
         * LA PARTIE ÉVÈNEMENT D'APPARITION DU NPC
         *
         */
        public static class Show extends Event.Player implements Cancellable {

            private boolean isCancelled; // Variable vérifiant si l'évènement a été annulé

            /**
             * On instancie un nouvel {@link Show évènement} de disparition du NPC.
             *
             * @param player Le Joueur qui subira l'évènement
             * @param npc Le NPC qui sera la cause de l'évènement
             *
             */
            protected Show(org.bukkit.entity.Player player, NPCPersonal npc) {

                super(player, npc); // On instancie la 'class' parente

                this.isCancelled = false; // On Initialise l'annulation de l'évènement

                Bukkit.getPluginManager().callEvent(this); // On fait appel à l'évènement dans la gestion de Plugin
            }

            /**
             * Vérifie si l'{@link Show évènement} du NPC a été annulé.
             *
             * @return Une valeur Booléenne
             */
            @Override
            public boolean isCancelled() { return isCancelled; }

            /**
             * Définit l'annulation de l'{@link Show évènement} du NPC.
             *
             * @param arg Voulez-vous annuler cet {@link Show évènement} ?
             *
             */
            @Override
            public void setCancelled(boolean arg) { isCancelled = arg; }

        }

        /* --------------------------------- */

        /**
         * LA PARTIE ÉVÈNEMENT DE DISPARITION DU NPC
         *
         */
        public static class Hide extends NPC.Events.Event.Player implements Cancellable {

            private boolean isCancelled; // Variable vérifiant si l'évènement a été annulé

            /**
             * On instancie un nouvel {@link Hide évènement} de disparition du NPC.
             *
             * @param player Le Joueur qui subira l'évènement
             * @param npc Le NPC qui sera la cause de l'évènement
             *
             */
            protected Hide(org.bukkit.entity.Player player, NPCPersonal npc) {

                super(player, npc); // On instancie la 'class' parente

                this.isCancelled = false; // On Initialise l'annulation de l'évènement

                Bukkit.getPluginManager().callEvent(this); // On fait appel à l'évènement dans la gestion de Plugin
            }

            /**
             * Vérifie si l'{@link Hide évènement} du NPC a été annulé.
             *
             * @return Une valeur Booléenne
             */
            @Override
            public boolean isCancelled() { return isCancelled; }

            /**
             * Définit l'annulation de l'{@link Hide évènement} du NPC.
             *
             * @param arg Voulez-vous annuler cet {@link Hide évènement} ?
             *
             */
            @Override
            public void setCancelled(boolean arg) { isCancelled = arg; }
        }

        /* --------------------------------- */

        /**
         * LA PARTIE ÉVÈNEMENT DE L'INTÉRACTION AVEC LE NPC
         *
         */
        public static class Interact extends NPC.Events.Event.Player implements Cancellable {

            private final NPC.Interact.ClickType clickType; // Variable récupérant le type de cliqué de l'évènement
            private boolean isCancelled; // Variable vérifiant si l'évènement a été annulé

            /**
             * On instancie un nouvel {@link Interact évènement} d'intéraction avec le NPC.
             *
             * @param player Le Joueur qui subira l'évènement
             * @param npc Le NPC qui sera la cause de l'évènement
             * @param clickType Le type de cliqué de l'évènement
             *
             */
            protected Interact(org.bukkit.entity.Player player, NPCPersonal npc, NPC.Interact.ClickType clickType) {

                super(player, npc); // On instancie la 'class' parente

                this.clickType = clickType; // On initialise le type de cliqué
                this.isCancelled = false; // On Initialise l'annulation de l'évènement

                Bukkit.getPluginManager().callEvent(this); // On fait appel à l'évènement dans la gestion de Plugin
            }

            /**
             * Récupère le {@link NPC.Interact.ClickType type de cliqué} de l'{@link Interact évènement} du NPC.
             *
             * @return Le {@link NPC.Interact.ClickType type de cliqué} de l'{@link Interact évènement} du NPC
             */
            public NPC.Interact.ClickType getClickType() { return clickType; }

            /**
             * Vérifie si le {@link NPC.Interact.ClickType type de cliqué} de l'{@link Interact évènement} du NPC est un clic droit.
             *
             * @return Une valeur Booléenne
             */
            public boolean isRightClick() { return clickType.equals(NPC.Interact.ClickType.RIGHT_CLICK); }

            /**
             * Vérifie si le {@link NPC.Interact.ClickType type de cliqué} de l'{@link Interact évènement} du NPC est un clic gauche.
             *
             * @return Une valeur Booléenne
             */
            public boolean isLeftClick() { return clickType.equals(NPC.Interact.ClickType.LEFT_CLICK); }

            /**
             * Vérifie si l'{@link Interact évènement} du NPC a été annulé.
             *
             * @return Une valeur Booléenne
             */
            @Override
            public boolean isCancelled() { return isCancelled; }

            /**
             * Définit l'annulation de l'{@link Interact évènement} du NPC.
             *
             * @param arg Voulez-vous annuler cet {@link Interact évènement} ?
             *
             */
            @Override
            public void setCancelled(boolean arg) { isCancelled = arg; }

        }
    }

    /************************************/
    /* QUELQUE CLASS UTILE POUR LE NPC */
    /***********************************/

    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */
    /* ------------------------------------------------------- */

    /**********************************/
    /* ÉNUMÉRATIONS UTILE POUR LE NPC */
    /*********************************/

    /**
     * Énumération définissant le type de suivi du regard du NPC avec {@link NPC#setGazeTrackingType(GazeTrackingType)}.
     *
     * @see NPC#setGazeTrackingType(GazeTrackingType)
     * @see NPC#getGazeTrackingType()
     *
     * @since 2021.1
     */
    public enum GazeTrackingType {

        /** Le NPC ne déplacera pas automatiquement la direction du regard. */
        NONE,

        /** Le NPC déplacera la direction du regard automatiquement vers le joueur qui voit le NPC.
         Cela signifie que chaque joueur verra le NPC se regarder lui-même. */
        PLAYER,

        /** Le NPC déplacera automatiquement la direction du regard vers le joueur le plus proche de l'emplacement du NPC.
         Cela signifie qu'un joueur peut voir son NPC regarder un autre joueur s'il est plus proche que lui. */
        NEAREST_PLAYER,

        /** Le NPC déplacera la direction du regard automatiquement vers l'entité la plus proche de l'emplacement du NPC. */
        NEAREST_ENTITY,
    }

    /* ------------------------------------------------------- */

    /**
     * Énumération définissant l'Animation du NPC.
     *
     * @since 2022.2
     */
    public enum Animation {

        SWING_MAIN_ARM(0),
        TAKE_DAMAGE(1),
        SWING_OFF_HAND(3),
        CRITICAL_EFFECT(4),
        MAGICAL_CRITICAL_EFFECT(5);

        private final int id;

        Animation(int id) { this.id = id; }

        public int getId() { return id; }

        public static Animation getAnimation(int id) { return Arrays.stream(Animation.values()).filter(x-> x.getId() == id).findAny().orElse(null); }

    }

    /**********************************/
    /* ÉNUMÉRATIONS UTILE POUR LE NPC */
    /*********************************/
}