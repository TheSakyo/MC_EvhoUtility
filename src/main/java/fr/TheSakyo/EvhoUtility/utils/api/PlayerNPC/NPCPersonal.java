package fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dependancies.org.jsoup.helper.Validate;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit.*;
import fr.TheSakyo.EvhoUtility.managers.ScoreboardManager;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms.NMSEntity;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms.NMSEntityData;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms.NMSEntityPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import com.mojang.datafixers.util.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Instance de {@link NPCPersonal} ayant la 'class' {@link NPC} étendu.<br/>
 * Ceci permet de faire une instance Personnelle du NPC pour un Joueur spécifique
 */
public class NPCPersonal extends NPC {

    private final Player player; // Variable récupérant le Joueur associé au NPC
    private final UUID gameProfileID; // Variable récupérant l'UUID du NPC
    private ServerPlayer entityNPC; // Variable récupérant l'Entité du NPC
    private NPC.Hologram npcHologram; // Variable récupérant l'hologramme (Soit : le Texte a affiché) du NPC
    private boolean canSee; // Variable vérifiant si le NPC est visible ou non
    private boolean hiddenText; // Variable vérifiant si l'hologramme (Soit : le Texte a affiché) du NPC est caché
    private boolean hiddenToPlayer; // Variable vérifiant si le joueur peut apercevoir le NPC ou non
    private boolean shownOnTabList; // Variable vérifiant si le NPC est visible dans le 'tablist'
    private String customName; // Variable récupérant un nom customisé pour le NPC
    protected NPCGlobal npcGlobal; // Variable récupérant l'instance du NPC Global du NPC Personnel actuel.

    /**
     * On instancie un nouveau {@link NPCPersonal NPC Personnel}.
     *
     * @param npcUtils La {@link NPCUtils librairie NPC}*
     * @param player Le {@link Player Joueur} associé au NPC
     * @param plugin Le plugin en question
     * @param code   Un code d'identification
     * @param world  Le monde
     * @param x      La coordonnée x
     * @param y      La coordonnée y
     * @param z      La coordonnée z
     * @param yaw    La rotation 'yaw'
     * @param pitch  La rotation 'pitch'
     */
    protected NPCPersonal(@Nonnull NPCUtils npcUtils, @Nonnull Player player, @Nonnull Plugin plugin, @Nonnull String code, @Nonnull World world, double x, double y, double z, float yaw, float pitch) {

        super(npcUtils, plugin, code, world, x, y, z, yaw, pitch); // On instancie la class parente

        // Si le Joueur est null, on renvoie une erreur
        Validate.notNull(player, "Impossible de générer une instance de NPC, Le joueur ne peut pas être nul.");

        this.player = player; // On initialise le joueur associé au NPC
        this.gameProfileID = UUID.randomUUID(); // On initialise l'UUID du NPC
        this.canSee = false; // On initialise la visibilité ou non du NPC.
        this.npcHologram = null; // On initialise l'hologramme du NPC (soit : le texte à afficher)
        this.shownOnTabList = false; // On initialise si le NPC est visible ou non dans le 'tablist'
        this.hiddenToPlayer = true; // On initialise si le Joueur peut voir le NPC ou non
        this.hiddenText = false; // On initialise si le NPC à un texte à afficher ou non (hologramme)
        this.npcGlobal = null; // On initialise l'instance du NPC Global du NPC

        npcUtils.getNPCPlayerManager(player).set(code, this); // On définit la gestion du Joueur pour le NPC en question en définissant le code d'identifiant de ce NPC.
    }

    /**
     * On instancie un nouveau {@link NPCPersonal NPC Personnel}.
     *
     * @param npcUtils La {@link NPCUtils librairie NPC}
     * @param player Le {@link Player Joueur} associé au NPC
     * @param plugin Le plugin en question
     * @param code   Un code d'identification
     * @param location La {@link Location Localisation} du NPC
     */
    protected NPCPersonal(@Nonnull NPCUtils npcUtils, @Nonnull Player player, @Nonnull Plugin plugin, @Nonnull String code, @Nonnull Location location) {
        this(npcUtils, player, plugin, code, location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Créer le NPC en question.
     *
     */
    public void create() {

        // ⬇️ Si le Skin est null ou l'Entité du NPC éxiste dèjà, on renvoie alors une erreur ⬇️ //
        Validate.notNull(super.getAttributes().skin, "Échec de la création du NPC. NPC.Skin n'a pas été configuré.");
        Validate.isTrue(entityNPC == null, "Échec de la création du NPC. Ce NPC a déjà été créé auparavant.");
        // ⬆️ Si le Skin est null ou l'Entité du NPC éxiste dèjà, on renvoie alors une erreur ⬆️ //

        MinecraftServer server = NMSCraftServer.getMinecraftServer(); // Récupère le Serveur Minecraft à partir du NMS
        ServerLevel worldServer = NMSCraftWorld.getWorldServer(super.getWorld()); // Récupère le Monde Minecraft à partir du NMS

        GameProfile gameProfile = new GameProfile(gameProfileID, getReplacedCustomName()); // On crée un nouveau profil de jeux
        entityNPC = NMSEntityPlayer.newEntityPlayer(server, worldServer, gameProfile); // On récupère l'Entité du NPC
        Validate.notNull(entityNPC, "Erreur à NMSEntityNPC"); // Si l'Entité du NPC est null, on renvoie une erreur
        entityNPC.moveTo(super.getX(), super.getY(), super.getZ(), super.getYaw(), super.getPitch()); // On déplace alors l'Entité du NPC

        this.npcHologram = new NPC.Hologram(this, player); // On crée un hologramme pour le NPC (Soit : le Texte a affiché)

        updateSkin(); // On met à jour le Skin du NPC
        updatePose(); // On met à jour la posture du NPC
        updateScoreboard(player); // On met à jour le Scoreboard du joueur pour le NPC
    }

    /**
     * Recréer le NPC en question.
     *
     */
    protected void reCreate() {
         // Si l'Entité du NPC est null, on renvoie une erreur
        Validate.notNull(entityNPC, "Échec de la recréation du NPC. Le NPC n'a pas encore été créé.");

        this.hide(); // Cache le NPC
        this.show(); // Affiche le NPC
    }

    /**
     * Détruit le NPC en question.
     *
     */
    public void destroy() {

        cancelMove(); // On annule le mouvement du NPC

        // Si l'Entité du NPC n'est pas null, on vérifie si le NPC est bien visible, alors on le cache, puis on définit l'Entité du NPC sur 'null'
        if(entityNPC != null) { if(canSee) { hide(); } entityNPC = null; }

        // Si l'hologramme du NPC (soit : le texte a affiché) n'est pas null, on supprime la supprime donc
        if(npcHologram != null) npcHologram.removeHologram();
    }

   /**
     * Met à jour le NPC en question.
     *
     */
    public void update() {

        // Si l'Entité du NPC est null, on renvoie une erreur
        Validate.notNull(entityNPC, "Échec de la mise à jour du NPC. Le NPC n'a pas encore été créé.");

        if(!canSee) return; // Si le NPC n'est pas visible, on ne fait rien

        // Si le NPC est visible pour le Joueur et qui n'est pas aux alentours de celui-ci, on cache le NPC au Joueur et on sort
        if(!hiddenToPlayer && !isInRange()) { hideToPlayer(); return; }

        // Si le NPC est visible pour le Joueur et qui est aux alentours de celui-ci + dans son champ de vision, on affiche le NPC au Joueur et on sort
        if(hiddenToPlayer && isInRange() && isInView()) { showToPlayer(); return; }

        updatePose(); // On met à jour la posture du NPC
        updateLook(); // On met à jour le type de suivie du regard du NPC
        updateSkin(); // On met à jour le Skin du NPC
        updatePlayerRotation(); // On met à jour la rotation du NPC
        updateEquipment(); // On met à jour l'Équipement du NPC
        updateMetadata(); // On met à jour les métadonnées du NPC
    }

   /**
     * Force la mise à jour du NPC en question.
     *
     */
    public void forceUpdate() {

        // Si l'Entité du NPC est null, on renvoie une erreur
        Validate.notNull(entityNPC, "Échec de la mise à jour forcée du NPC. Le NPC n'a pas encore été créé.");

        reCreate(); // On recrée le NPC
        update(); // On met à jour le NPC
        forceUpdateText(); // On force la mise à jour du texte (soit : l'hologramme) que le NPC va afficher
    }

    /**
     * Met à jour le {@link Hologram Texte} à afficher du NPC en question.
     *
     */
    public void updateText() {

        // Si l'hologramme du NPC (soit : le texte a affiché) est null, on ne fait donc rien
        if(npcHologram == null) return;

        int i = 1; // On initialise un numéro égal à '1' par défaut

        // Pour tous les textes qui doivent être affiché dans l'hologramme, on ajoute chaque texte dans une ligne itéré par le numéro initialisé plus haut
        for(String s : super.getAttributes().text) { npcHologram.setLine(i, s); i++; }

        npcHologram.update(); // Met à jour l'hologramme (soit : le texte a affiché)
    }

    /**
     * Force la mise à jour du {@link Hologram Texte} à afficher du NPC en question.
     *
     */
    public void forceUpdateText() {

        // Si l'hologramme du NPC (soit : le texte a affiché) est null, on ne fait donc rien
        if(npcHologram == null) return;
        npcHologram.forceUpdate(); // Force la mise à jour de l'hologramme (soit : le texte a affiché)
    }

    /**
     * Affiche le NPC en question.
     *
     */
    public void show() {

        // Si l'Entité du NPC est null, on renvoie une erreur
        Validate.notNull(entityNPC, "Échec de l'affichage du NPC. Le NPC n'a pas encore été créé.");

        if(canSee && !hiddenToPlayer) return; // Si le NPC est bien visible est qu'il n'est pas caché pour le joueur alors, on ne fait rien

        NPC.Events.Show npcShowEvent = new NPC.Events.Show(getPlayer(), this); // Crée l'évènement d'apparition du NPC
        if(npcShowEvent.isCancelled()) return; // Si l'évènement a été annuler, on ne fait rien

        canSee = true; // On définit la visibilité du NPC sur 'vrai'

        // Si le NPC n'est pas aux alentours du Joueur ou n'est pas dans le champ de vision de celui-ci, on cache le NPC pour le joueur et on sort
        if(!isInRange() || !isInView()) { hiddenToPlayer = true; return; }

        double hideDistance = super.getAttributes().hideDistance; // Récupère la distance à laquelle le NPC sera caché pour le Joueur
        super.getAttributes().hideDistance = 0.0; // Définit la distance sur '0'

        // ⬇️ Aprés quelque temps, on définit la distance à laquelle le NPC sera caché pour le Joueur par la distance en question ⬇️ //
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), ()-> {

            super.getAttributes().hideDistance = hideDistance; // Définit la distance à laquelle le NPC sera caché pour le Joueur par la distance en question
            showToPlayer(); // Affiche le NPC pour le joueur

        },10);
        // ⬆️ Aprés quelque temps, on définit la distance à laquelle le NPC sera caché pour le Joueur par la distance en question ⬆️ //
    }

    /**
     * Cache le NPC en question.
     *
     */
    public void hide() {

        // Si l'Entité du NPC est null, on renvoie une erreur
        Validate.notNull(entityNPC, "Échec de la dissimulation du NPC. Le NPC n'a pas encore été créé.");

        if(!canSee) return; // Si le NPC n'est pas visible alors, on ne fait rien

        NPC.Events.Hide npcHideEvent = new NPC.Events.Hide(getPlayer(), this); // Crée l'évènement de disparition du NPC
        if(npcHideEvent.isCancelled()) return; // Si l'évènement a été annuler, on ne fait rien

        hideToPlayer(); // Cache le NPC pour le joueur

        canSee = false; // On définit la visibilité du NPC sur 'faux'
    }

    /**
     * Affiche le NPC pour le Joueur associé.
     *
     */
    private void showToPlayer() {

        if(!hiddenToPlayer) return; // Si le NPC est dèjà visible par le joueur, on ne fait rien
        createPacket(); // On envoie au joueur le paquet d'apparition pour le NPC
        hiddenToPlayer = false; // On définit le fait que le NPC soit caché pour le Joueur sur 'faux'

        // On récupère le Texte à chaques lignes que doit afficher le NPC, si c'est inférieur à zéro, on met à jour le texte (soit : l'hologramme)
        if(!getText().isEmpty()) updateText();

        // ⬇️ Aprés quelque temps, on vérifie si le NPC n'est pas créé, donc on le met à jour ⬇️ //
        Bukkit.getScheduler().scheduleSyncDelayedTask(getNPCUtils().getPlugin(), () -> {

            if(!isCreated()) return; // Si le NPC est créé, on ne fait rien
            update(); // Sinon, on met à jour le NPC

        }, 1);
        // ⬆️ Aprés quelque temps, on vérifie si le NPC n'est pas créé, donc on le met à jour ⬆️ //
    }

    /**
     * Cache le NPC pour le Joueur associé.
     *
     */
    private void hideToPlayer() {

        if(hiddenToPlayer) return; // Si le NPC n'est pas visible par le joueur, on ne fait rien

        // ⬇️ Si le NPC est visible dans le 'tablist', alors on envoie le paquet au joueur pour le cacher du 'tablist' ⬇️ //
        if(shownOnTabList) {

            // Envoie le paquet au joueur pour cacher le NPC du 'tablist'
            NMSCraftPlayer.sendPacket(player, new ClientboundPlayerInfoRemovePacket(List.of(entityNPC.getUUID())));

            shownOnTabList = false; // Définit la visibilité du NPC sur le 'tablist' sur faux
        }
        // ⬆️ Si le NPC est visible dans le 'tablist', alors on envoie le paquet au joueur pour le cacher du 'tablist' ⬆️ //

        NMSCraftPlayer.sendPacket(player, new ClientboundRemoveEntitiesPacket(NMSEntity.getEntityID(entityNPC))); // Envoie le paquet au joueur pour détruire le NPC
        if(npcHologram != null) npcHologram.hide(); // Si l'hologramme du NPC (soit : le texte a affiché) n'est pas null, on cache alors cet hologramme
        hiddenToPlayer = true; // Définit la visibilité du NPC pour son joueur associé sur 'vrai'
    }

    /**
     * Définit si on doit afficher le {@link Hologram Texte} par le NPC en question.
     *
     * @param hide Doit-on afficher le {@link Hologram Texte} ?
     *
     */
    public void setHideText(boolean hide) {

        boolean a = hiddenText; // On définit dans une variable à par si le texte affiché par le NPC (soit : l'hologramme) est actuellement caché ou non
        this.hiddenText = hide; // On définit si le texte affiché par le NPC (soit : l'hologramme) sera donc caché ou non

        // Si le booléen récupéré en paramètre est égal au booléen vérifiant si le texte affiché par le NPC (soit : l'hologramme) est caché ou non sont égaux, on ne fait rien
        if(a == hide) return;

        // Si l'hologramme du NPC (soit : le texte a affiché) est null, on ne fait rien
        if(npcHologram == null) return;


        if(hide) hideText(); // Si le booléen récupéré en paramètre est bien vrai, on cache alors l'hologramme du NPC (soit : le texte a affiché)
        else showText(); // Sinon on l'affiche
    }

    private void hideText() {

        Validate.notNull(npcHologram, "Échec de la mise à jour du texte du NPC. Le NPCHologramme n'a pas encore été créé.");
        npcHologram.hide();
        hiddenText = true;
    }

    private void showText() {

        Validate.notNull(npcHologram, "Échec de la mise à jour du texte du NPC. Le NPCHologramme n'a pas encore été créé.");

        if(hiddenText) return;
        npcHologram.show();
        hiddenText = false;
    }

    /**
     * Met à jour la {@link Location Localisation} du NPC.
     *
     */
    @Override
    protected void updateLocation() { updateLocation(player); }

    /**
     * Met à jour la {@link Location Localisation} du NPC pour un joueur spécifique.
     *
     * @param player Le Joueur en question
     *
     */
    protected void updateLocation(Player player) {

        if(entityNPC == null) return;
        NMSCraftPlayer.sendPacket(player, new ClientboundTeleportEntityPacket(entityNPC));
    }

   /**
     * Met à jour la {@link Location Localisation} du NPC par celle d'un {@link NPCGlobal NPC Global}.
     *
     * @param npcGlobal Le {@link NPCGlobal NPC Global} en question à récupérer la {@link Location Localisation}
     *
     */
    protected void updateGlobalLocation(NPCGlobal npcGlobal) {

        super.setX(npcGlobal.getX()); // Change la coordonnée 'X' du NPC par celle du NPC Global en question
        super.setY(npcGlobal.getY()); // Change la coordonnée 'Y' du NPC par celle du NPC Global en question
        super.setZ(npcGlobal.getZ()); // Change la coordonnée 'Z' du NPC par celle du NPC Global en question

        // ⬇️ Si le Type de Suivie du Regard du NPC est égal à rien du tous, on change la rotation du NPC par celle du NPC Global en question ⬇️ //
        if(getGazeTrackingType().equals(GazeTrackingType.NONE)) {

            super.setYaw(npcGlobal.getDefaultYAW()); // Change la rotation 'yaw' du NPC par celle du NPC Global en question
            super.setPitch(npcGlobal.getDefaultPITCH()); // Change la rotation 'pitch' du NPC par celle du NPC Global en question
        }
        // ⬆️ Si le Type de Suivie du Regard du NPC est égal à rien du tous, on change la rotation du NPC par celle du NPC Global en question ⬆️ //
    }

    /**
     * Force le NPC en question à un endroit préçis.
     *
     * @param world Le Monde où téléporter le NPC
     * @param x La Coordonnée 'X' où téléporter le NPC
     * @param y La Coordonnée 'Y' où téléporter le NPC
     * @param z La Coordonnée 'Z' où téléporter le NPC
     * @param yaw La Rotation 'yaw' du NPC
     * @param pitch La Rotation 'pitch' du NPC
     *
     */
    public void teleport(World world, double x, double y, double z, float yaw, float pitch) {

        // Si l'Entité du NPC est null, on renvoie une erreur
        Validate.notNull(entityNPC, "Échec du déplacement du NPC. Le NPC n'a pas encore été créé.");

        NPC.Events.Teleport npcTeleportEvent = new NPC.Events.Teleport(this, new Location(world, x, y, z, yaw, pitch)); // On crée l'évènement de téléportation du NPC
        if(npcTeleportEvent.isCancelled()) return; // Si l'évènement a été annuler, on ne fait rien

        super.setX(x); // Change la coordonnée 'X' du NPC
        super.setY(y); // Change la coordonnée 'Y' du NPC
        super.setZ(z); // Change la coordonnée 'Z' du NPC

        super.setYaw(yaw); // Change la rotation 'yaw' du NPC
        super.setDefaultYAW(yaw); // On change la rotation 'yaw' par défaut du NPC
        super.setPitch(pitch); // On change la rotation 'pitch' du NPC
        super.setDefaultPITCH(yaw); // On change la rotation 'yaw' par défaut du NPC

        if(!super.getWorld().equals(world)) changeWorld(world); // Si le monde du NPC n'est pas égal au monde où le NPC sera téléporté, on change alors le monde du NPC

        boolean show = canSee; // On définit dans une variable si le NPC est visible

        if(npcHologram != null) npcHologram.hide();  // Si l'hologramme du NPC (soit : le texte a affiché) n'est pas null, on cache alors cet hologramme

        reCreate(); // On recrée le NPC

        // Si l'hologramme du NPC (soit : le texte a affiché) n'est pas null, on force alors la mise à jour du texte (soit : l'hologramme) que le NPC doit afficher
        if(npcHologram != null) forceUpdateText();

        if(show) show(); // Si le NPC est bien visible, on peut afficher de nouveau le NPC

        // Si l'hologramme du NPC (soit : le texte a affiché) n'est pas null, on cache alors le texte (soit : l'hologramme) que le NPC doit afficher
        else if(npcHologram != null) hideText();
    }

    /**
     * Met à jour le Déplacement du NPC.
     *
     */
    protected void updateMove() {

        if(player == null) return; // Si le joueur associé est null, on ne fait rien
        if(entityNPC == null) return; // Si l'Entité du NPC est null, on ne fait rien
        if(!canSee) return; // Si le NPC n'est pas visible, on ne fait rien

        // Si le NPC est visible pour le Joueur et qui n'est pas aux alentours de celui-ci, on cache le NPC au Joueur et on sort
        if(!hiddenToPlayer && !isInRange()) { hideToPlayer(); return; }

        // Si le NPC est visible pour le Joueur et qui est aux alentours de celui-ci + dans son champ de vision, on affiche le NPC au Joueur et on sort
        if(hiddenToPlayer && isInRange() && isInView()) { showToPlayer(); return; }

        updateLook(); // On met à jour le type de suivie du regard du NPC
        updatePlayerRotation(); // On met à jour la rotation du NPC
    }

    /**
     * Déplace le NPC aux coordonnées précisées.
     *
     * @param x La Coordonnée 'X' où déplacer le NPC
     * @param y La Coordonnée 'Y' où déplacer le NPC
     * @param z La Coordonnée 'Z' où déplacer le NPC
     */
    protected void move(double x, double y, double z) {

        // Si les coordonnées sont supérieurs à '8', on renvoie une erreur disant qu'il faudrait mieux téléporter le NPC
        Validate.isTrue(x < 8 && y < 8 && z < 8, "Les NPCs ne peuvent pas se déplacer de 8 blocs ou plus à la fois, utilisez la téléportation à la place.");

        // Créer l'évènement de déplacement du NPC
        NPC.Events.Move npcMoveEvent = new NPC.Events.Move(this, new Location(super.getWorld(), super.getX() + x, super.getY() + y, super.getZ() + z));
        if(npcMoveEvent.isCancelled()) return; // Si l'évènement a été annuler, on ne fait rien

        super.setX(super.getX() + x); // Change la coordonnée 'X' du NPC par le déplacement en question
        super.setY(super.getY() + y); // Change la coordonnée 'Y' du NPC par le déplacement en question
        super.setZ(super.getZ() + z); // Change la coordonnée 'Z' du NPC par le déplacement en question

        entityNPC.moveTo(super.getX(), super.getY(), super.getZ()); // Déplace l'Entité du NPC
        if(npcHologram != null) npcHologram.move(new Vector(x, y, z)); // Déplace également l'hologramme (soit : le texte affiché) du NPC
        movePacket(x, y, z); // Envoie au Joueur associé le paquet de déplacement du NPC
    }

    /**
     * Envoie au Joueur associé, le paquet de déplacement du NPC.
     *
     * @param x La Coordonnée 'X' où déplacer le NPC
     * @param y La Coordonnée 'Y' où déplacer le NPC
     * @param z La Coordonnée 'Z' où déplacer le NPC
     *
     */
    protected void movePacket(double x, double y, double z) {

        Validate.isTrue(x < 8); // On Valide l'exécution de la méthode, si la coordonnée 'X' est inférieur à '8'
        Validate.isTrue(y < 8); // On Valide l'exécution de la méthode, si la coordonnée 'Y' est inférieur à '8'
        Validate.isTrue(z < 8); // On Valide l'exécution de la méthode, si la coordonnée 'Z' est inférieur à '8'

        // Envoie au Joueur associé le paquet de déplacement du NPC
        NMSCraftPlayer.sendPacket(player, new ClientboundMoveEntityPacket.Pos(NMSEntity.getEntityID(entityNPC), (short) (x * 4096), (short) (y * 4096), (short) (z * 4096), true));
    }

    /**
     * Change le Monde actuel du NPC.
     *
     */
    protected void changeWorld(World world) {

        // Attribute au NPC associé avec le Joueur en question le nouveau monde où il doit se trouver
        super.getPluginManager().getNPCUtils().getNPCPlayerManager(player).changeWorld(this, super.getWorld(), world);
        super.setWorld(world); // Change le Monde du NPC
    }

    /**
     * Le NPC va suivre son joueur associé.
     *
     * @return {@link Move.Behaviour Le Comportement de Suivie} du NPC
     */
    public Move.Behaviour followPlayer() { return super.getMoveBehaviour().setFollowPlayer(); }

    /**
     * Le NPC va suivre son joueur associé avec une distance minimale et maximale.
     *
     * @param min La distance minimale
     * @param max La distance maximale
     *
     * @return {@link Move.Behaviour Le Comportement de Suivie} du NPC
     */
    public Move.Behaviour followPlayer(double min, double max) { return super.getMoveBehaviour().setFollowPlayer(min, max); }

    /**
     * Le NPC va jouer une {@link Animation Animation} demandée.
     *
     * @param animation L'Animation a joué.
     */
    public void playAnimation(NPC.Animation animation) {

        ClientboundAnimatePacket packet = new ClientboundAnimatePacket(entityNPC, animation.getId()); // Récupère le paquet pour effectuer l'animation du NPC
        NMSCraftPlayer.sendPacket(player, packet); // Envoie au Joueur associé le paquet d'animation en question du NPC
    }

    /**
     * Définit un Nom Customisé du NPC
     *
     * @param name Le Nom Customisé en question
     *
     */
    @Override
    public void setCustomName(@Nullable String name) {

        if(name == null) return;
        this.customName = name;
    }

    /**
     * Définit un Nom Customisé dans le 'tablist' pour le NPC en question.
     *
     * @param name Le Nom Customisé en question à afficher dans le 'tablist'
     *
     */
    @Override
    public void setCustomTabListName(@Nullable String name) {


        if(name == null) name = Attributes.getDefaultTabListName(); // Si le Nom Customisé en question est null, on la redéfinit par l'attribut associé par défaut
        final String finalName = getReplacedCustomName(name); // On récupère le Nom Customisé en vérifiant la taille de ce nom

        // Si le Nom Customisé est supérieur à 16 caractères, on renvoie une erreur
        Validate.isTrue(finalName.length() <= 16, "Erreur dans la configuration du nom dans le Tablist. Le nom doit comporter 16 caractères ou moins.");

        // si le Nom Customisé ne contient pas de UUID a remplacé et que le nom en question éxiste sur un autre NPC, on renvoie donc une erreur
        if(!name.contains("{id}")) Validate.isTrue(getNPCUtils().getNPCPlayerManager(player).getNPCs().stream().filter(x-> x != this && x.getReplacedCustomName().equals(finalName)).findAny().orElse(null) == null, "Erreur dans la définition du nom dans le 'tablist'. Il y a déjà un autre NPC avec ce nom.");
        getAttributes().setCustomTabListName(finalName); // Définit le Nom Customisé en question au NPC
    }

    /**
     * Le NPC va perdre des dégâts (aucun n'impacte sur le NPC).<br/>
     *
     * Une animation et un son seront joués :)
     *
     */
    @Override
    public void hit() {

        player.playSound(getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0F, 1.0F);
        playAnimation(Animation.TAKE_DAMAGE);
    }

    /**
     * Le NPC va effectuer une rotation pour regarder à un endroit demandé en fonction de la rotation spécifié.
     *
     * @param yaw La Rotation 'yaw' du NPC
     * @param pitch La Rotation 'pitch' du NPC
     *
     */
    public void lookAt(float yaw, float pitch) { lookAt(yaw, pitch, false); }


    /**
     * Le NPC va effectuer une rotation pour regarder à un endroit demandé en fonction de la rotation spécifié.
     *
     * @param yaw La Rotation 'yaw' du NPC
     * @param pitch La Rotation 'pitch' du NPC
     * @param forceLook Doit-on forçer la rotation par défaut du NPC ?
     *
     */
    public void lookAt(float yaw, float pitch, boolean forceLook) {

        // Si l'Entité du NPC est null, on renvoie une erreur
        Validate.notNull(entityNPC, "Impossible de définir la direction du regard. Le NPC n'a pas encore été créé.");

                                /* ----------------------------------- */

        if(getGazeTrackingType().equals(GazeTrackingType.NONE)) {

            super.setYaw(yaw); // Change la rotation 'yaw' du NPC
            super.setPitch(pitch); // Change la rotation 'pitch' du NPC

            super.setDefaultYAW(yaw); // Change la rotation Par Défaut 'yaw' par défaut du NPC
            super.setDefaultPITCH(pitch); // Change la rotation Par Défaut 'pitch' par défaut du NPC

        } else {

            super.setYaw(yaw); // Change la rotation 'yaw' du NPC
            super.setPitch(pitch); // Change la rotation 'pitch' du NPC

            if(forceLook) {

                super.setDefaultYAW(yaw); // Change la rotation Par Défaut 'yaw' par défaut du NPC
                super.setDefaultPITCH(pitch); // Change la rotation Par Défaut 'pitch' par défaut du NPC
            }

        }

        entityNPC.setYRot(yaw); // Change la rotation de la coordonnée 'Y' de l'Entité du NPC
        entityNPC.setXRot(pitch); // Change la rotation de la coordonnée 'X' de l'Entité du NPC
    }


    /**
     * Met à jour le Type de Suivie du Regard du NPC.
     *
     */
    private void updateLook() {

        // Si le monde où se trouve le joueur n'est pas le même où se trouve le NPC, on ne fait rien
        if(!player.getWorld().getName().equals(getWorld().getName())) return;

        // Vérifie si le type de suivie du regard du NPC est un Joueur, alors il va suivre le joueur
        if(getGazeTrackingType().equals(GazeTrackingType.PLAYER)) lookAt(player);

        // ⬇️ Sinon, si c'est un joueur aux alentours de lui ou une entité, on effectue la vérification du joueur ou de l'entité proche pour qu'il le suive du regard ⬇️ //
        else if(getGazeTrackingType().equals(GazeTrackingType.NEAREST_PLAYER) || getGazeTrackingType().equals(GazeTrackingType.NEAREST_ENTITY)) {

            // Vérifie si le type de suivie du regard du NPC est joueur aux alentours de lui-même
            final boolean isNearest_PlayerGazeTracking = getGazeTrackingType().equals(GazeTrackingType.NEAREST_PLAYER);

            // ⬇️ Si le NPC actuel est un NPC Globale, on vérifie l'Entité le plus proche de ce NPC ⬇️ //
            if(hasNPCGlobal()) {

                Entity near; // Variable permettant de récupérer l'Entité le plus proche

                // Si le type de suivie du regard du NPC sont les joueurs aux alentours de lui-même, alors on définit l'Entité le plus proche par le Joueur proche de lui
                if(isNearest_PlayerGazeTracking) near = getNPCGlobal().np();
                else near = getNPCGlobal().ne(); // Sinon, on définit l'Entité le plus proche par l'Entité en question le plus proche de lui

                if(near != null) { lookAt(near); return; } // Si l'Entité le plus proche n'est pas null, le NPC va regarder l'Entité en question et on sort
            }
            // ⬆️ Si le NPC actuel est un NPC Globale, on vérifie l'Entité le plus proche de ce NPC ⬆️ //

            Entity near = null; // Variable permettant de récupérer l'Entité le plus proche
            /*double hideDistance = getHideDistance(); // On récupère la distance à laquelle le NPC doit être caché du joueur*/
            double distance = 5; // On récupère la distance à laquelle le NPC doit regarder l'entité le plus proche
            final Location npcLocation = getLocation(); // On récupère la localisation actuelle du NPC

            // ⬇️ Pour toutes les entités proches, en fonction la distance auquel le NPC doit être caché du joueur, on récupère l'Entité en question à suivre du regard ⬇️ //
            for(Entity entities : super.getWorld().getNearbyEntities(npcLocation, distance, distance, distance)) {

                // Si le type de suivie du regard du NPC sont les joueurs aux alentours de lui-même, mais que l'entité n'est pas joueur, alors on continue
                if(isNearest_PlayerGazeTracking && !(entities instanceof Player)) continue;

                double distanceEntity = entities.getLocation().distance(npcLocation); // Récupère la distance entre le NPC et l'Entité
                if(distanceEntity > distance) continue; // Si la distance de l'entité est plus loin que la distance à laquelle le NPC doit regarder l'entité, alors on continue
                near = entities; // On définit l'Entité le plus proche étant l'entité en question
                distance = distanceEntity; // On définit la distance à laquelle le NPC doit regarder l'entité par la distance entre le NPC et l'Entité
            }
            // ⬆️ Pour toutes les entités proches, en fonction la distance auquel le NPC doit être caché du joueur, on récupère l'Entité en question à suivre du regard ⬆️ //

            if(near == null) { lookAt(getDefaultYAW(), getDefaultPITCH()); return; } // Si l'Entité le plus proche est null, on redéfinit la rotation du NPC par défaut et on ne fait rien
            lookAt(near); // Sinon, on regarde l'Entité en question

            // ⬇️ Si le NPC actuel est un NPC Globale, on vérifie l'Entité le plus proche à suivre du regard ⬇️ //
            if(hasNPCGlobal()) {

                // Si le type de suivie du regard du NPC sont les joueurs aux alentours de lui-même, alors on définit l'Entité le plus proche par le Joueur proche de lui
                if(isNearest_PlayerGazeTracking) getNPCGlobal().np(near);
                else getNPCGlobal().ne(near); // Sinon, on définit l'Entité le plus proche par l'Entité en question le plus proche de lui
            }
            // ⬆️ Si le NPC actuel est un NPC Globale, on vérifie l'Entité le plus proche à suivre du regard ⬆️ //
        }
        // ⬆️ Sinon, si c'est un joueur aux alentours de lui ou une entité, on effectue la vérification du joueur ou de l'entité proche pour qu'il le suive du regard ⬆️ //
    }

    /**
     * Définit comment le joueur demandé doit intéragir avec le NPC ({@link Interact.ClickType type de cliqué}).
     *
     * @param player Le Joueur en question
     * @param clickType Le type de cliqué que doit effectuer le joueur
     */
    protected void interact(@Nonnull Player player, @Nonnull NPC.Interact.ClickType clickType) {

        // Créer l'évènement d'intéraction avec le NPC
        NPC.Events.Interact npcInteractEvent = new NPC.Events.Interact(player, this, clickType);
        if(npcInteractEvent.isCancelled()) return; // Si l'évènement a été annuler, on ne fait rien

        // ⬇️ Si le NPC actuel est un NPC Globale, on récupère le type de cliqué de ce NPC pour chaque action d'intéraction avec le Joueur ⬇️ //
        if(hasNPCGlobal()) {

            // Récupère le type de cliqué du NPC Globale en question pour chaque action avec le Joueur
            getNPCGlobal().getClickActions(clickType).forEach(x-> x.execute(player));

            // Récupère les deux types de cliqué du NPC Globale en question en cas, pour chaque action avec le Joueur
            getNPCGlobal().getClickActions(Interact.ClickType.EITHER).forEach(x-> x.execute(player));
        }
        // ⬆️ Si le NPC actuel est un NPC Globale, on récupère le type de cliqué de ce NPC pour chaque action d'intéraction avec le Joueur ⬆️ //

                                      /* ---------------------------------------------------- */

        getClickActions(clickType).forEach(x-> x.execute(player)); // Récupère le type de cliqué pour chaque action avec le Joueur associé
        getClickActions(Interact.ClickType.EITHER).forEach(x-> x.execute(player)); // Récupère les deux types de cliqué en cas, pour chaque action avec le Joueur associé
    }

    /**
     * Met à jour la Rotation du NPC.
     *
     */
    @Override
    protected void updatePlayerRotation() {

        if(entityNPC == null) return; // Si l'Entité du NPC est null, on ne fait rien

        if(getGazeTrackingType().equals(GazeTrackingType.NONE)) {

            // Envoie au Joueur associé le paquet de rotation du NPC
            NMSCraftPlayer.sendPacket(player, new ClientboundMoveEntityPacket.Rot(NMSEntity.getEntityID(entityNPC), (byte)((super.getDefaultYAW() * 256 / 360)), (byte)((super.getDefaultPITCH() * 256 / 360)), false));

            // Envoie au Joueur associé le paquet de rotation de la tête du NPC
            NMSCraftPlayer.sendPacket(player, new ClientboundRotateHeadPacket(entityNPC, (byte)(super.getDefaultYAW() * 256 / 360)));

        } else {

            // Envoie au Joueur associé le paquet de rotation du NPC
            NMSCraftPlayer.sendPacket(player, new ClientboundMoveEntityPacket.Rot(NMSEntity.getEntityID(entityNPC), (byte)((super.getYaw() * 256 / 360)), (byte)((super.getPitch() * 256 / 360)), false));

            // Envoie au Joueur associé le paquet de rotation de la tête du NPC
            NMSCraftPlayer.sendPacket(player, new ClientboundRotateHeadPacket(entityNPC, (byte)(super.getYaw() * 256 / 360)));

        }
    }

    /**
     * Met à jour le Skin du NPC.
     *
     */
    protected void updateSkin() {

        GameProfile gameProfile = NMSEntityPlayer.getGameProfile(entityNPC); // Récupère le profil de Jeux du NPC
        gameProfile.getProperties().get("textures").clear(); // Supprime la texture du NPC

        // Réattribuât la texture du NPC en question
        gameProfile.getProperties().put("textures", new Property("textures", super.getAttributes().skin.getTexture(), super.getAttributes().skin.getSignature()));
    }

    /**
     * Met à jour la posture du NPC.
     *
     */
    protected void updatePose() {

        // Si la posture du NPC est endormie alors, on indique au NPC qu'il doit effectuer une posture endormie à partir de ses coordinates
        if(getPose().equals(Pose.SLEEPING)) entityNPC.setSleepingPos(new BlockPos(super.getX().intValue(), super.getY().intValue(), super.getZ().intValue()));
        entityNPC.setPose(getPose());  // Sinon, on définit la posture du NPC
    }

    /**
     * Met à jour l'Équipement du NPC.
     *
     */
    protected void updateEquipment() {

        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment = new ArrayList<>(); // Variable récupérant la liste des Items pour chaques équipements

        // ⬇️ Pour chaques équipements, on met à jour les items en question équipés ⬇️ //
        for(EquipmentSlot slot : EquipmentSlot.values()) {

            // Si l'équipement du NPC ne contient pas l'équipement récupéré, on équipe alors de l'air (soit : rien du tous)
            if(!getSlots().containsKey(slot) || getSlots().get(slot) == null) getSlots().put(slot, new ItemStack(Material.AIR));

            ItemStack item = getSlots().get(slot); // On récupère l'item associé à cet équipement en question récupéré
            net.minecraft.world.item.ItemStack craftItem = null; // Définit l'item depuis le NMS sur null


            equipment.add(new Pair<>(slot, net.minecraft.world.item.ItemStack.fromBukkitCopy(item))); // On ajoute l'équipement récupéré au NPC
        }
        // ⬆️ Pour chaques équipements, on met à jour les items en question équipés ⬆️ //

        ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(NMSEntity.getEntityID(entityNPC), equipment); // Récupère le paquet d'équipement du NPC
        NMSCraftPlayer.sendPacket(player, packet); // Envoie au Joueur associé le paquet d'équipement du NPC
    }

    /**
     * Met à jour le Scoreboard du Joueur demandé pour le NPC.
     *
     * @param player Le Joueur en question
     *
     */
    protected void updateScoreboard(Player player) {

        GameProfile gameProfile = NMSEntityPlayer.getGameProfile(entityNPC); // Récupère le profil de jeux du NPC
        Scoreboard scoreboard = null; // Instancie un scoreboard étant null par défaut

        // ⬇️ On essaie de récupérer le Scoreboard du Joueur associé depuis le NMS ⬇️ //
        try { scoreboard = (Scoreboard)NMSCraftScoreboard.getCraftScoreBoardGetHandle().invoke(NMSCraftScoreboard.getCraftScoreBoardClass().cast(ScoreboardManager.getScoreboard(player))); }
        catch (Exception ignored) {}
        // ⬆️ On essaie de récupérer le Scoreboard du Joueur associé depuis le NMS ⬆️ //

        Validate.notNull(scoreboard, "Erreur à NMSCraftScoreboard"); // Si une erreur est survenue sur le Scoreboard récupéré, on renvoie une erreur

        // On instancie une nouvelle team pour le scoreboard en question
        PlayerTeam scoreboardTeam = scoreboard.getPlayerTeam(getShortUUID()) == null ? new PlayerTeam(scoreboard, getShortUUID()) : scoreboard.getPlayerTeam(getShortUUID());
        scoreboardTeam.setNameTagVisibility(Team.Visibility.NEVER); // On a défini qu'il n'y aura aucune visibilité à cette team
        scoreboardTeam.setColor(getGlowingColor()); // On a défini également la couleur de cette team

        Team.CollisionRule collisionRule = Team.CollisionRule.NEVER; // On récupère les règles de collisions de cette team à 'jamais'

        // Si le NPC a les collisions activées, on définit la collision de cette team à 'toujours'
        if(isCollidable()) collisionRule = Team.CollisionRule.ALWAYS;

        scoreboardTeam.setCollisionRule(collisionRule); // Définit la collision récupérée pour la team en question
        scoreboard.addPlayerToTeam(gameProfile.getName(), scoreboardTeam); // Ajoute le NPC dans la team en question

        // ⬇️ Envoie au Joueur associé les paquets pour initialiser la team en question ⬇️ //
        NMSCraftPlayer.sendPacket(player, ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(scoreboardTeam, true));
        NMSCraftPlayer.sendPacket(player, ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(scoreboardTeam, false));
        // ⬆️ Envoie au Joueur associé les paquets pour initialiser la team en question ⬆️ //
    }

    /**
     * Met à jour les métadonnées du NPC.
     *
     */
    @SuppressWarnings("unchecked")
    protected void updateMetadata() {

        SynchedEntityData synchedEntityData = NMSEntity.getSynchedEntityData(entityNPC);  // On récupère les métadonnées actuelles du NPC

        /************************************************/

        entityNPC.setGlowingTag(isGlowing()); // On définit la surbrillance du NPC
        Map<Integer, SynchedEntityData.DataItem<?>> map = null; // Initialise une variable pour récupérer chaque donnée des métadonnées

        /************************************************/

        // ⬇️ On essaie de récupérer chaque donnée des métadonnées depuis le NMS ⬇️ //
        try { map = (Map<Integer, SynchedEntityData.DataItem<?>>)FieldUtils.readDeclaredField(synchedEntityData, "e", true); }
        catch(IllegalAccessException ignored) {}

        // ⬆️ On essaie de récupérer chaque donnée des métadonnées depuis le NMS ⬆️ //

        /************************/

        // Si la variable récupérant chaque donnée des métadonnées est null, alors on ne fait rien
        if(map == null) return;

        /************************************************/

        //http://wiki.vg/Entities#Entity
        //https://wiki.vg/Entity_metadata#Entity_Metadata_Format

        // ⬇️ ~~ Métadonnées concernant les Entités ~~ ⬇️ //

        SynchedEntityData.DataItem<?> item = map.get(0);

        /************************/

        byte b = (byte) (Byte)item.getValue();
        byte bitMaskIndex = (byte)0x40;
        /************************/

        if(isGlowing()) b = (byte)(b | bitMaskIndex);
        else b = (byte)(b & ~(1));

        /************************/

        bitMaskIndex = (byte)0x01;

        /************************/

        if(isOnFire()) b = (byte)(b | bitMaskIndex);
        else b = (byte)(b & ~(1 << bitMaskIndex));

        /************************/

        NMSEntityData.set(synchedEntityData, EntityDataSerializers.BYTE.createAccessor(0), b); // On redéfinit les métadonnées en question au NPC

        // ⬆️ ~~ Métadonnées concernant les Entités ~~ ⬆️ //

        /************************************************/

        // ⬇️ ~~ Métadonnées concernant les Joueurs (Soit : le Skin du NPC) ~~ ⬇️ //
        b = 0x00;
        NPC.Skin.Parts parts = getSkinParts();

        /************************/

        if(parts.isVisible(Skin.Part.CAPE)) b = (byte)(b | 0x01);
        if(parts.isVisible(Skin.Part.JACKET)) b = (byte)(b | 0x02);
        if(parts.isVisible(Skin.Part.LEFT_SLEEVE)) b = (byte)(b | 0x04);
        if(parts.isVisible(Skin.Part.RIGHT_SLEEVE)) b = (byte)(b | 0x08);
        if(parts.isVisible(Skin.Part.LEFT_PANTS)) b = (byte)(b | 0x10);
        if(parts.isVisible(Skin.Part.RIGHT_PANTS)) b = (byte)(b | 0x20);
        if(parts.isVisible(Skin.Part.HAT)) b = (byte)(b | 0x40);

        NMSEntityData.set(synchedEntityData, EntityDataSerializers.BYTE.createAccessor(17), b); // On redéfinit les métadonnées en question au NPC

        // ⬆️ ~~ Métadonnées concernant les Joueurs (Soit : le Skin du NPC) ~~ ⬆️ //

        /************************************************/

        // Récupère le paquet de métadonnées du NPC
        ClientboundSetEntityDataPacket metadataPacket = new ClientboundSetEntityDataPacket(NMSEntity.getEntityID(entityNPC), synchedEntityData.getNonDefaultValues());
        NMSCraftPlayer.sendPacket(player, metadataPacket); // Envoie au Joueur associé le paquet de métadonnées du NPC
    }

                            /* -------------------------------------------------------------*/

    /**
     * Envoie le paquet de création du NPC pour son Joueur associé.
     *
     */
    private void createPacket() {

        // ⬇️ On essaie d'envoyer au Joueur associé les paquets d'affichage dans le 'tablist' et de création du NPC ⬇️ //
        try {

            NMSCraftPlayer.sendPacket(player, new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, entityNPC));
            NMSCraftPlayer.sendPacket(player, new ClientboundAddPlayerPacket(entityNPC));

        } catch(Exception e) { return; }
        // ⬆️ On essaie d'envoyer au Joueur associé les paquets d'affichage dans le 'tablist' et de création du NPC ⬆️ //

        shownOnTabList = true; // Définit la visibilité du NPC dans le 'tablist' "vrai"
        updatePlayerRotation(); // Met à jour la rotation du NPC
        if(isShowOnTabList()) return; // Si la visibilité du NPC dans le 'tablist' est "vrai", alors on sort

        // ⬇️ Aprés quelque temps, on vérifie la création du NPC ainsi, on retire l'affichage du NPC dans le 'tablist' ⬇️ //
        Bukkit.getScheduler().scheduleSyncDelayedTask(getNPCUtils().getPlugin(), ()-> {

            if(!isCreated()) return; // Si le NPC n'est pas créé, alors on ne fait rien

            // Envoie au Joueur associé le paquet d'affichage dans le 'tablist' sur 'faux'
            NMSCraftPlayer.sendPacket(player, new ClientboundPlayerInfoRemovePacket(List.of(entityNPC.getUUID())));
            shownOnTabList = false; // Définit donc la visibilité du NPC dans le 'tablist' "faux"

        }, pluginManager.getTicksUntilTabListHide());
        // ⬆️ Aprés quelque temps, on vérifie la création du NPC ainsi, on retire l'affichage du NPC dans le 'tablist' ⬆️ //
    }

                            /* -------------------------------------------------------------*/

    /**
     * Récupère le Nom Customisé du NPC en faisant attention à la taille maximale de celui-ci
     *
     * @return Le Nom Customisé du NPC en faisant attention à la taille maximale de celui-ci.
     */
    protected String getReplacedCustomName() { return getReplacedCustomName(super.getAttributes().getCustomTabListName()); }

    /**
     * Récupère une chaîne de caractère demandé en faisant attention à la taille maximale de celui-ci.
     *
     * @return La chaîne de caractère en faisant attention à la taille maximale de celui-ci
     */
    protected String getReplacedCustomName(String name) {

        String id = getShortUUID();
        String replaced = name.replaceAll("\\{id}", id);
        if(replaced.length() > 16) replaced = replaced.substring(0, 15);
        return replaced;
    }

    /***************/
    /* LES GETTERS */
    /**************/

    /**
     * Récupère l'{@link ServerPlayer Entité} du NPC.
     *
     * @return l'{@link ServerPlayer Entité} du NPC
     */
    public ServerPlayer getEntity() { return this.entityNPC; }

    /**
     * Récupère l'{@link UUID} court du NPC.
     *
     * @return l'{@link UUID} court du NPC
     */
    public String getShortUUID() { return gameProfileID.toString().split("-")[1]; }

    /**
     * Récupère le Joueur associé au NPC actuel.
     *
     * @return Le Joueur associé au NPC actuel
     */
    public Player getPlayer() { return player; }


    /**
     * Récupère le Nom Customisé du NPC actuel.
     *
     * @return lL Nom Customisé du NPC actuel
     */
    public String getCustomName() { return customName; }

    /**
     * Récupère l'{@link Hologram Hologramme} associé avec le NPC.
     *
     * @return Une valeur Booléenne
     */
    protected NPC.Hologram getHologram() { return npcHologram; }

    /**
     * Récupère le {@link NPCGlobal NPC Global} du NPC actuel.
     *
     * @return le {@link NPCGlobal NPC Global} Globale du NPC actuel
     */
    public NPCGlobal getNPCGlobal() { return npcGlobal; }

    /**
     * Vérifie si le NPC est un {@link NPCGlobal NPC Global}.
     *
     * @return Une valeur Booléenne
     */
    public boolean hasNPCGlobal() { return npcGlobal != null; }

    /**
     * Vérifie si le NPC est dans le champ de vision du Joueur associé.
     *
     * @return Une valeur Booléenne
     */
    public boolean isInView() { return isInView(60.0D); }

    /**
     * Vérifie si le NPC est dans la mesure demandée du champ de vision du Joueur associé.
     *
     * @param fov La mesure en question qu'il faut vérifier.
     *
     * @return Une valeur Booléenne
     */
    public boolean isInView(double fov) {

        // On vectorise la normalisation de la localisation du NPC moins la localisation d'où il regarde
        Vector dir = getLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();

        // On retourne si le champ de vision calculé en fonction de la mesure demandée est vrai ou fausse
        return dir.dot(player.getEyeLocation().getDirection()) >= Math.cos(Math.toRadians(fov));
    }

    /**
     * Vérifie si le NPC est dans l'alentour du Joueur associé.
     *
     * @return Une valeur Booléenne
     */
    public boolean isInRange() {

        // Si le NPC n'est pas dans le monde que le Joueur associé, on retourne faux
        if(!getWorld().getName().equals(player.getWorld().getName())) return false;

        // On retourne alors si la distance entre la localisation du joueur est inférieure à la distance auquel le NPC soit caché
        return getLocation().distance(player.getLocation()) < getHideDistance();
    }

    /**
     * Vérifie si le NPC est visible par le client (le joueur).
     *
     * @return Une valeur Booléenne
     */
    public boolean isShownOnClient() { return canSee && !hiddenToPlayer; }

    /**
     * Vérifie si le NPC est visible.
     *
     * @return Une valeur Booléenne
     */
    public boolean canSee() { return canSee; }

    /**
     * Vérifie si le {@link Hologram Texte} a affiché par le NPC est caché.
     *
     * @return Une valeur Booléenne
     */
    public boolean isHiddenText() { return hiddenText; }

    /**
     * Vérifie si le NPC est bien créé.
     *
     * @return Une valeur Booléenne
     */
    public boolean isCreated() { return entityNPC != null; }

    /**
     * Vérifie si le NPC peut être créé.
     *
     * @return Une valeur Booléenne
     */
    public boolean canBeCreated() { return getSkin() != null && entityNPC == null; }

}