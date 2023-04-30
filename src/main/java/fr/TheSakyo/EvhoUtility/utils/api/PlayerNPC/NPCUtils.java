package fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.NMSUtils;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms.NMSEntity;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms.NMSEntityData;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms.NMSEntityPlayer;
import fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms.NMSNetworkManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * {@link NPCUtils} est une bibliothèque simple pour créer des {@link NPC} et les personnalisés.
 * <p>Ressource spigot inspiré : <a href="https://www.spigotmc.org/resources/playernpc.93625/">https://www.spigotmc.org/resources/playernpc.93625/</a>
 *
 * @author SergiFerry, TheSakyo
 * @since 2021.1
 */
public class NPCUtils implements Listener {

    private static NPCUtils instance; // Variable pour initialiser la librairie NPC
    private final UtilityMain plugin; // Variable récupérant le plugin actuel
    private final HashMap<Player, PlayerManager> playerManager; // variable récupérant la gestion des joueurs pour les NPCs
    private final HashMap<String, NPCGlobal> globalNPCs; // variable récupérant les différents NPCs Globaux
    private final HashMap<Plugin, PluginManager> pluginManager; // variable récupérant la gestion du plugin pour les NPCs
    private FileConfiguration config; // Variable récupérant le fichier de configuration globale
    private boolean debug; // Variable vérifiant si on est en mode debug ou non



    /**
     * Instancie la librairie des NPCs.
     *
     * @param plugin Le Plugin a associé à la librairie
     */
    public NPCUtils(@Nonnull UtilityMain plugin) {

        instance = this; // Initialise l'instance

        this.plugin = plugin; // Initialise le plugin

        this.playerManager = new HashMap<>(); // Initialise le gestionnaire des joueurs pour les NPCs
        this.globalNPCs = new HashMap<>(); // Initialise tous les différents NPCs Globaux
        this.pluginManager = new HashMap<>(); // Initialise le gestionnaire de plugins pour les NPCs
        this.debug = false; // Initialise si on est en mode debug ou non

        /*registerPlugin(plugin); // Inscrit le gestionnaire de plugins pour les NPCs*/
        plugin.getServer().getPluginManager().registerEvents(this, plugin); // Définit cette 'class' comme étant une 'class' d'évènement

                     /* ----------------------------------------------------------------------------------------------------------------- */

        NMSUtils.loadNMS(NMSEntity.class);
        NMSUtils.loadNMS(NMSEntityData.class);
        NMSUtils.loadNMS(NMSEntityPlayer.class);
        NMSUtils.loadNMS(NMSNetworkManager.class);
    }

    /**
     * Enregistre le gestionnaire de plugins pour les NPCs à partir du Plugin précisé.
     *
     * @param plugin Le plugin en question
     */
    public void registerPlugin(@Nonnull Plugin plugin) {

        // ⬇️ Si le plugin est null ou que le gestionnaire de plugins ne contient pas le plugin récupéré, on renvoie une erreur ⬇️ //
        Validate.notNull(plugin, "Impossible d'enregistrer le gestionnaire de plugins à partir d'un plugin nul.");
        Validate.isTrue(!pluginManager.containsKey(plugin), "Ce plugin est déjà enregistré.");
        // ⬆️ Si le plugin est null ou que le gestionnaire de plugins ne contient pas le plugin récupéré, on renvoie une erreur ⬆️ //

        PluginManager pluginManager = new PluginManager(plugin, this); // Enregistre le plugin actuel dans le gestionnaire de plugins pour les NPCs
        this.pluginManager.put(plugin, pluginManager); // Ajoute le plugin dans le gestionnaire de plugins

        // Envoie un message à la console disant que la librairie NPC a bien été enregistré
        Bukkit.getServer().getConsoleSender().sendMessage(this.plugin.prefix  + ChatFormatting.GREEN + "NPCUtils a bien été enregistré !");
    }

    /**
     * Désenregistre le gestionnaire de plugins pour les NPCs à partir du Plugin précisé.
     *
     * @param plugin Le plugin en question
     *
     */
    public void unregisterPlugin(@Nonnull Plugin plugin) {

        // ⬇️ Si le plugin est null ou que le gestionnaire de plugins ne contient pas le plugin récupéré, on renvoie une erreur ⬇️ //
        Validate.notNull(plugin, "Impossible d'enregistrer le gestionnaire de plugins à partir d'un plugin nul.");
        Validate.isTrue(pluginManager.containsKey(plugin), "Ce plugin n'est pas enregistré.");
        // ⬆️ Si le plugin est null ou que le gestionnaire de plugins ne contient pas le plugin récupéré, on renvoie une erreur ⬆️ //

        PluginManager pluginManager = new PluginManager(plugin, this); // Enregistre le plugin actuel dans le gestionnaire de plugins pour les NPCs
        this.pluginManager.remove(plugin); // Ajoute le plugin dans le gestionnaire de plugins

        onDisable(plugin); // Désactive l'API

        // Envoie un message à la console disant que la librairie NPC a bien été enregistré
        Bukkit.getServer().getConsoleSender().sendMessage(this.plugin.prefix  + ChatFormatting.GREEN + "NPCUtils a bien été désenregistré !");
    }

    /**
     * Vérifie si un plugin précisé est bien enregistré dans le gestionnaire de plugins pour les NPCs.
     *
     * @param plugin Le Plugin en question
     *
     * @return Une valeur Booléenne
     */
    public boolean isRegistered(@Nonnull Plugin plugin) {

        Validate.notNull(plugin, "Impossible de vérifier le gestionnaire de plugins à partir d'un plugin nul."); // Si le plugin est null, on renvoie une erreur
        return pluginManager.containsKey(plugin); // Retourne si vrai ou faux le plugin existe dans le gestionnaire de plugins pour les NPCs
    }

    /**
     * Récupère le gestionnaire de plugins pour les NPCs à partir du Plugin précisé.
     *
     * @param plugin Le plugin en question
     *
     * @return Un gestionnaire de plugins
     */
    public PluginManager getPluginManager(@Nonnull Plugin plugin) {

        // ⬇️ Si le plugin est null ou que le gestionnaire de plugins contient le plugin récupéré, on renvoie une erreur ⬇️ //
        Validate.notNull(plugin, "Impossible d'obtenir le gestionnaire de plugin à partir d'un plugin nul.");
        Validate.isTrue(this.pluginManager.containsKey(plugin), "Ce plugin n'est pas enregistré.");
        // ⬆️ Si le plugin est null ou que le gestionnaire de plugins contient le plugin récupéré, on renvoie une erreur ⬆️ //

        return this.pluginManager.get(plugin); // On retourne le gestionnaire de plugins en question associé au plugin en question
    }

    /**
     * Récupère La liste des plugins enregistrés par le gestionnaire de plugins pour les NPCs.
     *
     * @return Une liste des plugins enregistrés par le gestionnaire de plugins pour les NPCs
     */
    public List<Plugin> getRegisteredPlugins() { return pluginManager.keySet().stream().toList(); }

    /**
     * Génère un {@link NPCPersonal NPC Personnel} pour un joueur précis.
     *
     * @param player Le Joueur associé au NPC
     * @param plugin Le Plugin en question
     * @param code Le code d'Identification du NPC
     * @param location La localisation du NPC
     *
     * @return Un {@link NPCPersonal NPC Personnel} pour le joueur demandé
     */
    public NPCPersonal generatePersonalNPC(@Nonnull Player player, @Nonnull Plugin plugin, @Nonnull String code, @Nonnull Location location) {

        // ⬇️ Vérifie si les différents paramètres ne sont pas null ainsi, on vérifie si le plugin est bien enregistrés par la librairie, sinon on renvoie des erreurs ⬇️ //
        Validate.notNull(plugin, "Vous ne pouvez pas créer un NPC avec un plugin nul.");
        Validate.notNull(player, "Vous ne pouvez pas créer un NPC avec un joueur nul.");
        Validate.notNull(code, "Vous ne pouvez pas créer un NPC avec un code nul.");
        Validate.notNull(location, "Vous ne pouvez pas créer un NPC avec une localisation nulle.");
        Validate.notNull(location.getWorld(), "Vous ne pouvez pas créer de NPC avec un monde nul.");
        Validate.isTrue(!code.toLowerCase().startsWith("global_"), "Vous ne pouvez pas créer de NPC avec un tag NPCGlobal.");
        Validate.isTrue(isRegistered(plugin), "Ce plugin n'est pas enregistré sur NPCUtils.");
        // ⬆️ Vérifie si les différents paramètres ne sont pas null ainsi, on vérifie si le plugin est bien enregistrés par la librairie, sinon on renvoie des erreurs ⬆️ //

        return generatePlayerPersonalNPC(player, plugin, a(plugin, code), location); // Retourne une instance de NPC Personnel
    }

    /**
     * Génère un {@link NPCPersonal NPC Personnel} pour un joueur précis.
     *
     * @param player Le Joueur associé au NPC
     * @param plugin Le Plugin en question
     * @param code Le code d'Identification du NPC
     * @param location La localisation du NPC
     *
     * @return Un {@link NPCPersonal NPC Personnel} pour le joueur demandé
     */
    protected NPCPersonal generatePlayerPersonalNPC(Player player, Plugin plugin, String code, Location location) {

        NPCPersonal old = getNPCPlayerManager(player).getNPC(code); // On récupère le NPC associé au Joueur actuel

        // Si le NPC associé au Joueur actuel éxiste, on renvoie une erreur
        Validate.isTrue(old == null, "NPC personnel avec code " + code + " pour le joueur " + player.getName() + " existe déjà.");

        return new NPCPersonal(this, player, plugin, code, location); // Retourne une instance de NPC Personnel
    }

    /**
     * Récupère un {@link NPCPersonal NPC Personnel} d'un joueur précis.
     *
     * @param player Le Joueur associé au NPC
     * @param plugin Le Plugin en question
     * @param id L'Identification du NPC
     *
     * @return Un {@link NPCPersonal NPC Personnel} pour le joueur demandé
     */
    public NPCPersonal getPersonalNPC(@Nonnull Player player, @Nonnull Plugin plugin, @Nonnull String id) {

        // ⬇️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬇️ //
        Validate.notNull(player, "Le joueur ne doit pas être nul.");
        Validate.notNull(plugin, "Le plugin ne doit pas être nul.");
        Validate.notNull(id, "L'identifiant du NPC ne doit pas être nul.");
        // ⬆️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬆️ //

        return getNPCPlayerManager(player).getNPC(a(plugin, id)); // Retourne le NPC Personnel demandé
    }

    /**
     * Récupère tous les {@link NPCPersonal NPCs Personnels} d'un joueur précis.
     *
     * @param player Le Joueur associé au NPC
     * @param plugin Le Plugin en question
     *
     * @return Tous les {@link NPCPersonal NPCs Personnels} pour le joueur demandé
     */
    public Set<NPCPersonal> getPersonalNPCs(@Nonnull Player player, @Nonnull Plugin plugin) {

        // ⬇️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬇️ //
        Validate.notNull(player, "Le joueur ne doit pas être nul.");
        Validate.notNull(plugin, "Le plugin ne doit pas être nul.");
        // ⬆️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬆️ //

        return getNPCPlayerManager(player).getNPCs(plugin); // La liste des NPCs Personnels demandée
    }

    /**
     * Récupère tous les {@link NPCPersonal NPCs Personnels} de tous les joueurs.
     *
     * @param plugin Le Plugin en question
     *
     * @return Tous les {@link NPCPersonal NPCs Personnels} du Serveur
     */
    public Set<NPCPersonal> getPersonalNPCs(@Nonnull Plugin plugin) {

        // Si le plugin est null, on renvoie une erreur
        Validate.notNull(plugin, "Le plugin ne doit pas être nul.");

        Set<NPCPersonal> npcs = new HashSet<>(); // On initialise la liste des NPCs Personnels
        Bukkit.getServer().getOnlinePlayers().forEach(x-> npcs.addAll(getPersonalNPCs(x, plugin))); // On ajoute à la liste les NPC associés à chaque joueur

        return npcs; // Retourne la liste en question
    }

    /**
     * Récupère tous les {@link NPCPersonal NPCs Personnels} d'un joueur précis dans un monde spécifié.
     *
     * @param player Le Joueur associé au NPC
     * @param world Le Monde en question
     *
     * @return Tous les {@link NPCPersonal NPCs Personnels} pour le joueur demandé du monde spécifié
     */
    public Set<NPCPersonal> getPersonalNPCs(@Nonnull Player player, @Nonnull World world) {

        // ⬇️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬇️ //
        Validate.notNull(player, "Le joueur ne doit pas être nul.");
        Validate.notNull(world, "Le plugin ne doit pas être nul.");
        // ⬆️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬆️ //

        return getNPCPlayerManager(player).getNPCs(world); // La liste des NPCs Personnels demandée
    }


    /**
     * Vérifie si l'identification du NPC en question correspond à un {@link NPCPersonal NPC Personnel} associé du Joueur précisé.
     *
     * @param player Le Joueur associé au NPC
     * @param plugin Le Plugin en question
     * @param id L'Identification du NPC
     *
     * @return Une valeur Booléenne
     */
    public boolean hasPersonalNPC(@Nonnull Player player, @Nonnull Plugin plugin, @Nonnull String id) {

        // ⬇️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬇️ //
        Validate.notNull(player, "Le joueur ne doit pas être nul.");
        Validate.notNull(plugin, "Le plugin ne doit pas être nul.");
        Validate.notNull(id, "L'identifiant du NPC ne doit pas être nul.");
        // ⬆️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬆️ //

        return getPersonalNPC(player, plugin, id) != null; // Retourne la vérification si le NPC demandé est bien un NPC Personnel du Joueur en question
    }

    /**
     * Supprime un {@link NPCPersonal NPC Personnel} associé du Joueur précisé.
     *
     * @param player Le Joueur associé au NPC
     * @param plugin Le Plugin en question
     * @param id L'Identification du NPC
     */
    public void removePersonalNPC(@Nonnull Player player, @Nonnull Plugin plugin, @Nonnull String id) {

        // ⬇️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬇️ //
        Validate.notNull(player, "Le joueur ne doit pas être nul.");
        Validate.notNull(player, "Le plugin ne doit pas être nul.");
        Validate.notNull(id, "L'identifiant du NPC ne doit pas être nul.");
        // ⬆️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬆️ //

        removePersonalNPC(getPersonalNPC(player, plugin, id)); // Supprime le NPC Personnel associé au Joueur en question
    }

    /**
     * Supprime un {@link NPCPersonal NPC Personnel} précis.
     *
     * @param npc Le {@link NPCPersonal NPC Personnel} en question
     */
    public void removePersonalNPC(@Nonnull NPCPersonal npc) {

        // Si le NPC est null, on renvoie une erreur
        Validate.notNull(npc, "Le NPC n'a pas été trouvé.");

        // Si le NPC est un NPC Global et que le NPC Global contient un joueur étant le Joueur associé de ce NPC Personnel, on enlève alors le joueur du NPC Global
        if(npc.hasNPCGlobal() && npc.getNPCGlobal().hasPlayer(npc.getPlayer())) { npc.getNPCGlobal().removePlayer(npc.getPlayer()); return; }
        npc.destroy(); // On détruit le NPC

        getNPCPlayerManager(npc.getPlayer()).removeNPC(npc.getCode()); // On retire le NPC Personnel de la liste des NPCs Personnels du Joueur associé
    }

                                        /* ---------------------------------------------------------------*/

    /**
     * Génère un {@link NPCGlobal NPC Global}.
     *
     * @param plugin Le Plugin en question
     * @param code Le code d'Identification du NPC
     * @param visibility  La {@link NPCGlobal.Visibility Visibilité} du NPC
     * @param visibilityRequirement L'Éxigence du Joueur pour la visibilité du NPC
     * @param location La localisation du NPC
     *
     * @return Un {@link NPCGlobal NPC Global}
     */
    public NPCGlobal generateGlobalNPC(@Nonnull Plugin plugin, @Nonnull String code, @Nonnull NPCGlobal.Visibility visibility, @Nullable Predicate<Player> visibilityRequirement, @Nonnull Location location) {

        // ⬇️ Vérifie si les différents paramètres ne sont pas null ainsi, on vérifie si le plugin est bien enregistrés par la librairie, sinon on renvoie des erreurs ⬇️ //
        Validate.notNull(plugin, "Vous ne pouvez pas créer un NPC avec un plugin nul.");
        Validate.notNull(code, "Vous ne pouvez pas créer un NPC avec un joueur nul.");
        Validate.notNull(visibility, "Vous ne pouvez pas créer un NPC avec une visibilité nulle.");
        Validate.notNull(location, "Vous ne pouvez pas créer un NPC avec une localisation nulle.");
        Validate.notNull(location.getWorld(), "Vous ne pouvez pas créer de NPC avec un monde nul.");
        Validate.isTrue(isRegistered(plugin), "Ce plugin n'est pas enregistré sur NPCUtils.");
        // ⬆️ Vérifie si les différents paramètres ne sont pas null ainsi, on vérifie si le plugin est bien enregistrés par la librairie, sinon on renvoie des erreurs ⬆️ //

        return generatePlayerGlobalNPC(plugin, a(plugin, code), visibility, visibilityRequirement, location); // Retourne une instance de NPC Global
    }

    /**
     * Génère un {@link NPCGlobal NPC Global}.
     *
     * @param plugin Le Plugin en question
     * @param code Le code d'Identification du NPC
     * @param visibility  La {@link NPCGlobal.Visibility Visibilité} du NPC
     * @param location La localisation du NPC
     *
     * @return Un {@link NPCGlobal NPC Global}
     */
    public NPCGlobal generateGlobalNPC(@Nonnull Plugin plugin, @Nonnull String code, @Nonnull NPCGlobal.Visibility visibility, @Nonnull Location location) {

        return generateGlobalNPC(plugin, code, visibility, null, location);
    }

    /**
     * Génère un {@link NPCGlobal NPC Global}.
     *
     * @param plugin Le Plugin en question
     * @param code Le code d'Identification du NPC
     * @param visibilityRequirement L'Éxigence du Joueur pour la visibilité du NPC
     * @param location La localisation du NPC
     *
     * @return Un {@link NPCGlobal NPC Global}
     */
    public NPCGlobal generateGlobalNPC(@Nonnull Plugin plugin, @Nonnull String code, @Nullable Predicate<Player> visibilityRequirement, @Nonnull Location location) {

        return generateGlobalNPC(plugin, code, NPCGlobal.Visibility.EVERYONE, visibilityRequirement, location);
    }

    /**
     * Génère un {@link NPCGlobal NPC Global}.
     *
     * @param plugin Le Plugin en question
     * @param code Le code d'Identification du NPC
     * @param location La localisation du NPC
     *
     * @return Un {@link NPCGlobal NPC Global}
     */
    public NPCGlobal generateGlobalNPC(@Nonnull Plugin plugin, @Nonnull String code, @Nonnull Location location) {

        return generateGlobalNPC(plugin, code, NPCGlobal.Visibility.EVERYONE, null, location);
    }

    /**
     * Génère un {@link NPCGlobal NPC Global}.
     *
     * @param plugin Le Plugin en question
     * @param code Le code d'Identification du NPC
     * @param visibility  La {@link NPCGlobal.Visibility Visibilité} du NPC
     * @param visibilityRequirement L'Éxigence du Joueur pour la visibilité du NPC
     * @param location La localisation du NPC
     *
     * @return Un {@link NPCGlobal NPC Global}
     */
    private NPCGlobal generatePlayerGlobalNPC(Plugin plugin, String code, NPCGlobal.Visibility visibility, Predicate<Player> visibilityRequirement, Location location) {

        // Si le NPC Global actuel éxiste, on renvoie une erreur
        Validate.isTrue(!globalNPCs.containsKey(code), "Le NPC NPCGlobal avec le code  " + code + " existe déjà.");

        // On instancie un nouveau NPC Globale
        NPCGlobal npcGlobal = new NPCGlobal(this, plugin, code, visibility, visibilityRequirement, location);
        globalNPCs.put(code, npcGlobal); // On l'ajoute à la liste des NPCs Global

        return npcGlobal; // On retourne le NPC en question
    }

    /**
     * Récupère un {@link NPCGlobal NPC Global} en fonction de son code d'identification.
     *
     * @param plugin Le Plugin en question
     * @param id L'Identification du NPC
     *
     * @return Le {@link NPCGlobal NPC Global} en question
     */
    public NPCGlobal getGlobalNPC(@Nonnull Plugin plugin, @Nonnull String id) {

        // ⬇️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬇️ //
        Validate.notNull(plugin, "Le plugin ne doit pas être nul.");
        Validate.notNull(id, "L'identifiant du NPC ne doit pas être nul");
        // ⬆️ Vérifie si les différents paramètres ne sont pas null, sinon on renvoie des erreurs ⬆️ //

        return globalNPCs.get(a(plugin, id)); // Retourne le NPC Global en question en fonction de son code d'identification
    }

    /**
     * Récupère tous les {@link NPCGlobal NPC Globaux}.
     *
     * @return Tous les {@link NPCGlobal NPC Globaux}
     */
    public Set<NPCGlobal> getAllGlobalNPCs() { return Set.copyOf(globalNPCs.values()); }

    /**
     * Récupère tous les {@link NPCGlobal NPC Globaux} d'un plugin en question.
     *
     * @return Tous les {@link NPCGlobal NPC Globaux} d'un plugin demandé
     */
    public Set<NPCGlobal> getAllGlobalNPCs(@Nonnull Plugin plugin) {

        // Si le plugin est null, on renvoie une erreur
        Validate.notNull(plugin, "Le plugin ne doit pas être nul.");

        Set<NPCGlobal> npcs = new HashSet<>(); // Initialise la liste des NPCs

        // On ajoute à la liste tous les NPCs associé au plugin récupéré
        globalNPCs.keySet().stream().filter(x-> x.startsWith(plugin.getName().toLowerCase() + ".")).forEach(x-> npcs.add(getGlobalNPC(plugin, x)));

        return npcs; // On retourne la liste des NPCs Globaux
    }

    /**
     * Supprime un {@link NPCGlobal NPC Global}.
     *
     * @param npc Le {@link NPCGlobal NPC Global} en question
     */
    public void removeGlobalNPC(@Nonnull NPCGlobal npc) {

        // Si le NPC est null, on renvoie une erreur
        Validate.notNull(npc, "Le NPC n'a pas été trouvé.");

        npc.destroy(); // On détruit le NPC
        globalNPCs.remove(npc.getCode()); // On supprime le NPC en question de la liste des NPC globaux
    }

    /**
     * Supprime un {@link NPC} qu'il soit un {@link NPCPersonal NPC Personnel} ou un {@link NPCGlobal NPC Global}.
     *
     * @param npc Le {@link NPC} à supprimer.
     */
    public void removeNPC(@Nonnull NPC npc) {

        // Si le NPC est null, on renvoie une erreur
        Validate.notNull(npc, "Le NPC n'a pas été trouvé.");

        // Si le NPC est une instance de NPC Personnel, on appelle la méthode de suppression du NPC Personnel
        if(npc instanceof NPCPersonal) removePersonalNPC((NPCPersonal) npc);

        // Sinon, si le NPC est une instance de NPC Globale, on appelle la méthode de suppression du NPC Globale
        else if(npc instanceof NPCGlobal) removeGlobalNPC((NPCGlobal) npc);
    }

    /**
     * Définit si on active le mode de débogage ou non.
     *
     * @param debug Doit-on activer le mode de débogage ?
     */
    public void setDebug(boolean debug) {

        // Si le mode de débogage est déjà égal au mode définit, on ne fait rien
        if(this.debug == debug) return;

        this.debug = debug; // Définit le mode de débogage
        saveConfig(); // Sauvegarde le fichier de configuration
    }

    /**
     * Vérifie si le mode de débogage est activé.
     *
     * @return Une valeur Booléenne
     */
    public boolean isDebug() { return debug; }

    /**
     * Récupère Les {@link NPC.Attributes attributs} par défaut d'un {@link NPC}
     *
     * @return Les {@link NPC.Attributes attributs} par défaut d'un {@link NPC}.
     */
    public NPC.Attributes getDefaults() { return NPC.Attributes.getDefault(); }

    /**
     * Récupère le plugin associé à la librairie.
     *
     * @return Le plugin associé à la librairie
     */
    protected Plugin getPlugin() { return plugin; }

    /**
     * Construit une chaîne de caractère avec le nom du Plugin concaténé avec le Code d'Identification.
     *
     * @param plugin Le Plugin en question
     * @param code Le Code d'Identification en question
     *
     * @return La Chaîne de caractère demandé
     */
    private String a(Plugin plugin, String code) {

        // On construit la chaîne le caractère
        String b = plugin.getName().toLowerCase() + ".";
        if(code == null) return b; // Si le code d'identification est null, on retourne la chaîne de caractère à moitié remplie
        return b + code; // Sinon, on retourne la chaîne de caractère demandé
    }

    /**
     * Vérification de l'existence du fichier de configuration.
     *
     * @return Le fichier en question
     */
    private File checkFileExists() {

        File file = new File("plugins/EvhoUtility/PlayerNPC/config.yml");  // Récupère le fichier de configuration en question

        // ⬇️ Si le fichier n'éxiste pas, on essaie de le créer ⬇️ //
        if(!file.exists()) {

            // ⬇️ On essaie de créer le fichier, sinon on affiche une erreur ⬇️ //
            try { file.createNewFile(); }
            catch(Exception e) { printError(e); }
            // ⬆️ On essaie de créer le fichier, sinon on affiche une erreur ⬆️ //
        }
        // ⬆️ Si le fichier n'éxiste pas, on essaie de le créer ⬆️ //

        return file; // Retourne le fichier en question
    }

    /**
     * Recharge le fichier de configuration.
     *
     */
    public void loadConfig() {

        File file = checkFileExists(); // On vérifie l'existence du fichier de configuration
        this.config = YamlConfiguration.loadConfiguration(file); // On recharge ce fichier

        HashMap<String, Object> defaults = new HashMap<>(); // On initialise la liste par défaut qui sera écrit dans le fichier de configuration

        defaults.put("debug", this.debug); // On ajoute par défaut le débogage en question

        // On ajoute par défaut le nombre de tick de rotation de tête du NPC lors du suivi du regard d'une entité
        defaults.put("gazeUpdate.ticks", getPluginManager(plugin).updateGazeTicks);

        // On ajoute par défaut le type de mise à jour suivi du regard du NPC
        defaults.put("gazeUpdate.type", getPluginManager(plugin).updateGazeType.name());

        // On ajoute par défaut le nombre de tick pour la disparition du NPC dans le 'tablist'
        defaults.put("tabListHide.ticks", getPluginManager(plugin).ticksUntilTabListHide);

        // On ajoute par défaut la fréquence de mise à jour du Skin du NPC
        defaults.put("skinUpdate.frequency", getPluginManager(plugin).skinUpdateFrequency);


        boolean saveConfig = false; // Initialise un booléen étant 'faux' par défaut pour la sauvegarde du fichier

        // Pour toutes les données ajoutées dans la liste si aucune est définie dans le fichier de configuration, on la définit et active la sauvegarde du fichier
        for(String s : defaults.keySet()) { if(!config.contains(s)) { config.set(s, defaults.get(s)); saveConfig = true; } }

        // ⬇️ Si la sauvegarde du fichier est activée, on essaie alors de sauvegarde le fichier, sinon, on renvoie une erreur ⬇️ //
        if(saveConfig) {

            try { config.save(file); }
            catch (IOException e) { printError(e); }
        }
        // ⬆️ Si la sauvegarde du fichier est activée, on essaie alors de sauvegarde le fichier, sinon, on renvoie une erreur ⬆️ //

        this.debug = config.getBoolean("debug"); // On récupère le mode de débogage dans le fichier de configuration pour définir le mode débogage actuel

        // On récupère les ticks pour la disparition du NPC dans le 'tablist' pour la définir sur la donnée associée enregistrée dans le fichier
        getPluginManager(plugin).ticksUntilTabListHide = config.getInt("tabListHide.ticks");

        // On récupère la fréquence de mise à jour du Skin du NPC pour la définir sur la donnée associée enregistrée dans le fichier
        getPluginManager(plugin).skinUpdateFrequency = config.getObject("skinUpdate.frequency", SkinUpdateFrequency.class);

        // On récupère les ticks de rotation de tête du NPC lors du suivi du regard d'une entité pour la définir sur la donnée associée enregistrée dans le fichier
        getPluginManager(plugin).updateGazeTicks = config.getInt("gazeUpdate.ticks");

        // On récupère le type de mise à jour suivi du regard du NPC pour la définir sur la donnée associée enregistrée dans le fichier
        getPluginManager(plugin).updateGazeType = NPCUtils.UpdateGazeType.valueOf(config.getString("gazeUpdate.type"));

        getPluginManager(plugin).runGazeUpdate(); // Met à jour les ticks de la tâche d'exécution active
    }

    /**
     * Sauvegarde le fichier de configuration.
     *
     */
    public void saveConfig() {

        if(config == null) return; // Si le fichier de configuration est null, on ne fait rien
        File file = checkFileExists(); // On vérifie l'existence du fichier de configuration

        config.set("debug", this.debug); // On sauvegarde le mode de débogage actuel dans le fichier de configuration

        // On sauvegarde les ticks de rotation de tête du NPC lors du suivi du regard d'une entité dans le fichier de configuration
        config.set("gazeUpdate.ticks", getPluginManager(plugin).updateGazeTicks);

        // On sauvegarde le type de mise à jour suivi du regard du NPC dans le fichier de configuration
        config.set("gazeUpdate.type", getPluginManager(plugin).updateGazeType.name());

        // On sauvegarde les ticks pour la disparition du NPC dans le 'tablist' dans le fichier de configuration
        config.set("tabListHide.ticks", getPluginManager(plugin).ticksUntilTabListHide);

        // On sauvegarde la fréquence de mise à jour du NPC dans le fichier de configuration
        config.set("skinUpdate.frequency", getPluginManager(plugin).skinUpdateFrequency);

        // ⬇️ On essaie alors de sauvegarde le fichier en question sinon, on affiche une erreur ⬇️ //
        try { config.save(file); }
        catch(IOException e) { printError(e); }
        // ⬆️ On essaie alors de sauvegarde le fichier en question sinon, on affiche une erreur ⬆️ //
    }

                            /* ------------------------------------------------------------------------ */

    /**
     * Activation et initialisation du gestionnaire du plugin.
     *
     * @param plugin Le plugin principal en question
     * @param debug Doit-on informer des informations à la console
     *
     */
    public void onEnable(Plugin plugin, boolean debug) {

        getPluginManager(plugin).onEnable(debug); // Active le gestionnaire de plugin

        // ⬇️ Aprés un certain temps, on Ajoute tous les NPCs associés aux Joueurs ⬇️ //
        Bukkit.getScheduler().runTaskLater(getPlugin(), ()-> plugin.getServer().getOnlinePlayers().forEach(x-> {

            join(x); // Ajoute les NPCs associés au Joueur en question

            // Pour tous les NPC Globaux, si le NPC Global est activé, on force sa mise à jour
            for(NPCGlobal npcGlobal : getAllGlobalNPCs()) { if(npcGlobal.isActive(x)) npcGlobal.forceUpdate(); }
        }), 1L);
       // ⬆️ Aprés un certain temps, on Ajoute tous les NPCs associés aux Joueurs ⬆️ //
    }

    /**
     * Désactivation du gestionnaire du plugin.
     *
     * @param plugin Le plugin principal en question
     */
    public void onDisable(Plugin plugin) {

        // ⬇️ Essaie de sauvegarder les 'NPCs' persistant, si une exception s'oppose, on ignore ⬇️ //
        try { savePersistentNPCs(); }
        catch(NoClassDefFoundError ignored) {}
        // ⬆️ Essaie de sauvegarder les 'NPCs' persistant, si une exception s'oppose, on ignore ⬆️ //

                                     /* --------------------------------------- */

        plugin.getServer().getOnlinePlayers().forEach(this::quit); // Supprime les NPCs associés aux Joueurs
        getPluginManager(plugin).onDisable(); // Désactive le gestionnaire du plugin
    }

    /**
     * Récupère le Plugin Principal.
     *
     * @return Le Plugin Principal
     */
    private UtilityMain plugin() { return plugin; }


                            /* ------------------------------------------------------------------------ */

    /**
     * Récupère le gestionnaire du Joueur précisé (pour les NPCs).
     *
     * @param player Le Joueur en question
     *
     * @return Le gestionnaire du Joueur demandé
     */
    protected PlayerManager getNPCPlayerManager(Player player) {

        // Si le gestionnaire de joueurs contient le joueur, on retourne son gestionnaire propre à lui
        if(playerManager.containsKey(player)) return playerManager.get(player);

        // Sinon, on instancie un nouveau gestionnaire pour le Joueur en question
        PlayerManager npcPlayerManager = new PlayerManager(this, player);
        playerManager.put(player, npcPlayerManager); // Ajoute le joueur dans le gestionnaire de joueurs
        return npcPlayerManager; // Retourne le gestionnaire de joueurs en question
    }

    /**
     * Ajoute les NPCs associés au Joueur demandé.
     *
     * @param player Le Joueur en question
     */
    private void join(Player player) {

        PlayerManager npcPlayerManager = getNPCPlayerManager(player); // On récupère le gestionnaire du joueur
        npcPlayerManager.getPacketReader().inject(); // on injecte le lecteur de paquet du gestionnaire

        // ⬇️ Pour tous les NPC Globaux, on les associe pour le joueur, s'ils ont dans la liste des joueurs sélectionnés ⬇️ //
        for(NPCGlobal npcGlobal : getAllGlobalNPCs()) {

            if(npcGlobal.getVisibility().equals(NPCGlobal.Visibility.SELECTED_PLAYERS) && !npcGlobal.getSelectedPlayers().contains(player.getName())) continue;
            npcGlobal.addPlayer(player);
        }
        // ⬆️ Pour tous les NPC Globaux, on les associe pour le joueur, s'ils ont dans la liste des joueurs sélectionnés ⬆️ //
    }

    /**
     * Supprime les NPCs associés au Joueur demandé.
     *
     * @param player Le Joueur en question
     */
    private void quit(Player player) {

        // ⬇️ Pour tous les NPC Globaux, on les dissocie pour le joueur, s'ils ont dans la liste des joueurs associés ⬇️ //
        for(NPCGlobal npcGlobal : getAllGlobalNPCs()) {

            if(!npcGlobal.hasPlayer(player)) continue;
            npcGlobal.getPlayers().remove(player);
        }
        // ⬆️ Pour tous les NPC Globales, on les dissocie pour le joueur, s'ils ont dans la liste des joueurs associés ⬆️ //

        PlayerManager npcPlayerManager = getNPCPlayerManager(player); // On récupère le gestionnaire du joueur
        npcPlayerManager.destroyAll(); // On détruit tous les NPC Personnel associé au Joueur
        npcPlayerManager.getPacketReader().unInject(); // on annule injection du lecteur de paquet du gestionnaire
    }

                                        /* --------------------------------------------- */

    /**
     * Effectue un évènement quand le joueur intéragit avec le NPC.
     *
     * @param event {@link NPC.Events.Interact} - Évènement quand le Joueur intéragit avec le NPC
     */
    @EventHandler
    public void onNPCInteract(NPC.Events.Interact event) {

        Player player = event.getPlayer(); // Récupère le Joueur de l'évènement
        NPC npc = event.getNPC(); // Récupère le NPC de l'évènement
        NPC.Interact.ClickType action = event.getClickType(); // Récupérer l'action de l'intéraction

                                 /* ----------------------------------------------- */
        event.setCancelled(true);
        if(npc instanceof NPCPersonal npcPersonal) {

            event.setCancelled(false);
            npcPersonal.getClickActions().forEach(clickAction -> clickAction.execute(player));
        }
    }


    /**
     * Effectue un évènement quand le joueur change de monde.
     *
     * @param event {@link PlayerChangedWorldEvent} - Évènement quand le Joueur change de Monde
     */
    @EventHandler
    private void onPlayerChangedWorld(PlayerChangedWorldEvent event) {

        Player player = event.getPlayer(); // Récupère le Joueur de l'évènement
        World from = event.getFrom(); // Récupère le monde où le Joueur est actuellement

        PlayerManager npcPlayerManager = getNPCPlayerManager(player); // Récupère le gestionnaire du Joueur en question
        npcPlayerManager.destroyWorld(from); // On détruit les NPCs dans le monde où le Joueur est actuellement
        npcPlayerManager.showWorld(event.getPlayer().getWorld()); // On crée les NPCs dans le nouveau monde où le Joueur va apparaître
    }

    /**
     * Effectue un évènement quand un plugin se désactive.
     *
     * @param event {@link PluginDisableEvent} - Évènement quand un plugin se désactive
     */
    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {

        Plugin plugin = event.getPlugin(); // Récupère le plugin de l'évènement

        Set<NPCGlobal> npcGlobals = getAllGlobalNPCs(plugin); // Récupère tous les NPC Globaux de ce plugin
        if(!npcGlobals.isEmpty()) npcGlobals.forEach(npc -> { if(npc != null) this.removeGlobalNPC(npc); }); // On Supprime tous les NPCs Globaux associés à ce plugin

        Set<NPCPersonal> npc = getPersonalNPCs(plugin); // Récupère tous les NPC Personnels de ce plugin
        if(!npc.isEmpty())  npc.forEach(this::removePersonalNPC); // On Supprime tous les NPC Personnels associés à ce plugin
    }

                                        /* --------------------------------------------- */

    /**
     * Sauvegarde les NPCs persistant.
     *
     */
    private void savePersistentNPCs() { NPCGlobal.PersistentManager.forEachPersistentManager(NPCGlobal.PersistentManager::save); }

    /**
     * Récupère la {@link NPCUtils Librairie NPC}.
     *
     * @return La {@link NPCUtils Librairie NPC}
     */
    public static NPCUtils getInstance() { return instance; }

    /***
     * Méthode pour afficher l'erreur d'une exception dans le cas le mode de débogage est activé.
     *
     * @param e l'Exception en question à déboguer
     */
    protected static void printError(Exception e) { if(NPCUtils.getInstance().isDebug()) e.printStackTrace(System.err); }

                                        /* --------------------------------------------- */

    /**
     * Énumération pour le Type de Mise à jour pour le mouvement du NPC.
     */
    public enum UpdateGazeType { MOVE_EVENT, TICKS, }

    /**
     * Énumération pour le Type d'Identification du NPC dans le tablist.
     */
    public enum TabListIDType { RANDOM_UUID, NPC_CODE, NPC_SIMPLE_CODE }

                                        /* --------------------------------------------- */

    /**
     * Le gestionnaire du plugin.
     *
     */
    public static class PluginManager implements Listener {

        private final Plugin plugin; // Variable récupérant le plugin en question
        private final NPCUtils npcUtils; // Variable récupérant la librairie NPC
        protected UpdateGazeType updateGazeType; // Variable récupérant le Type de Mise à jour pour le mouvement du NPC
        protected Integer updateGazeTicks; // Variable récupérant le nombre de ticks pour le mouvement du NPC en question
        protected Integer ticksUntilTabListHide; // Variable récupérant le nombre de ticks pour la disparition du NPC dans le 'tablist'
        protected Integer taskID; // Variable récupérant l'Identifiant de la tâche actuelle
        protected SkinUpdateFrequency skinUpdateFrequency; // Variable récupérant la fréquence de mise à jour du Skin du NPC

        /**
         * On instancie une nouvelle gestion de plugin.
         *
         * @param plugin Le Plugin en question
         * @param npcUtils La librairie NPC
         */
        protected PluginManager(Plugin plugin, NPCUtils npcUtils) {

            this.plugin = plugin; // Initialise le plugin
            this.npcUtils = npcUtils; // Initialise la librairie NPC
            this.updateGazeTicks = 5; // Initialise le nombre de ticks pour le mouvement du NPC en question
            this.ticksUntilTabListHide = 10; // Initialise le nombre de ticks pour la disparition du NPC dans le 'tablist
            this.skinUpdateFrequency = new SkinUpdateFrequency(1, TimeUnit.DAYS); // Initialise la fréquence de mise à jour du Skin du NPC
            this.updateGazeType = UpdateGazeType.MOVE_EVENT; // Initialise

            plugin.getServer().getPluginManager().registerEvents(this, plugin); // Définit cette 'class' comme étant une 'class' d'évènement
        }

        /**
         * Activation du gestionnaire de plugin.
         *
         * @param debug Doit-on informer des informations à la console
         *
         */
        protected void onEnable(boolean debug) { if(plugin.equals(npcUtils.getPlugin())) { npcUtils.loadConfig(); loadPersistentNPCs(debug); } }

        /**
         * Désactivation du gestionnaire de plugin.
         *
         */
        protected void onDisable() {}

        /**
         * Recharge tous les NPCs persistant.
         *
         * @param debug Doit-on informer des informations à la console
         *
         */
        private void loadPersistentNPCs(boolean debug) {

            // Informe à la console qu'il y a un rechargement des NPCs persistant, dans le cas si le débug est vrai
            if(debug) Bukkit.getServer().getConsoleSender().sendMessage(UtilityMain.getInstance().prefix + ChatFormatting.GRAY + "Chargement des NPCs globaux persistants");

            // Récupère le repertoire des NPC Globaux persistant
            File folder = new File("plugins/EvhoUtility/PlayerNPC/persistent/NPCGlobal/" + plugin.getName().toLowerCase() + "/");
            if(!folder.exists()) { folder.mkdirs(); } // Si le répertoire n'éxiste pas, on le crée

            boolean empty = true; // Variable booléen vérifiant si le répertoire en question est vide ou non

            // ⬇️ Pour tous les fichiers dans le répertoire en question, on récupère donc le NPC Globale en question depuis son gestionnaire et on recharge ce fichier ⬇️ //
            for(File f : folder.listFiles()) {

                if(!f.isDirectory()) continue; // Si le fichier est un autre répertoire, on continue

                try {

                    // Récupère depuis le gestionnaire des NPCs Persistants, le NPC en question
                    NPCGlobal.PersistentManager persistent = new NPCGlobal.PersistentManager(plugin, f.getName());
                    persistent.load(); // Recharge le NPC persistant

                    empty = false; // Définit le répertoire en question étant vide sur 'faux'

                } catch(Exception e) {

                    // Informe à la console qu'il y a une erreur de chargement du NPC Persistant
                    Bukkit.getServer().getConsoleSender().sendMessage(UtilityMain.getInstance().prefix + ChatFormatting.GRAY + "Erreur de chargement du NPC NPCGlobal persistant " + ChatFormatting.RED + f.getName());
                    printError(e); // Détail l'erreur de l'exception
                }
            }
            //  Pour tous les fichiers dans le répertoire en question, on récupère donc le NPC Globale en question depuis son gestionnaire et on recharge ce fichier //

            // Si le répertoire en question est vide et que le débug est vrai, on informe à la console qu'aucun NPC n'a été trouvé
            if(empty && debug) Bukkit.getServer().getConsoleSender().sendMessage(UtilityMain.getInstance().prefix + ChatFormatting.GRAY +  "Aucun NPC persistant trouvé");
        }

        /**
         * Récupère le Plugin actuel.
         *
         * @return Le Plugin actuel
         */
        public Plugin getPlugin() { return plugin; }

        /**
         * Récupère la {@link NPCUtils Librairie NPC}.
         *
         * @return La {@link NPCUtils Librairie NPC}
         */
        public NPCUtils getNPCUtils() { return npcUtils; }

        /**
         * Récupère le {@link UpdateGazeType Type de Mise à jour pour le suivi de regard} du NPC.
         *
         * @return Le {@link UpdateGazeType Type de Mise à jour pour le suivi de regard} du NPC
         */
        public UpdateGazeType getUpdateGazeType() { return updateGazeType; }

       /**
         * Récupère Le nombre de ticks pour le mouvement du NPC en question.
         *
         * @return Le nombre de ticks pour le mouvement du NPC en question
         */
        public Integer getUpdateGazeTicks() { return updateGazeTicks; }

        /**
         * Récupère le nombre de ticks pour la disparition du NPC dans le 'tablist'.
         *
         * @return Le nombre de ticks pour la disparition du NPC dans le 'tablist'
         */
        public Integer getTicksUntilTabListHide() { return ticksUntilTabListHide; }

        /**
         * Récupère l'Identifiant de la Tâche actuel
         *
         * @return L'Identifiant de la Tâche actuel
         */
        public Integer getTaskID() { return taskID; }

        /**
         * Construit une chaîne de caractère avec le nom du Plugin en question concaténé avec le Code d'Identification spécifié.
         *
         * @param simpleCode Le Code d'Identification en question
         *
         * @return La Chaîne de caractère demandé
         */
        public String getCode(String simpleCode) {

            // On construit la chaîne le caractère
            String b = plugin.getName().toLowerCase() + ".";
            if(simpleCode == null) return b; // Si le code d'identification est null, on retourne la chaîne de caractère à moitié remplie
            return b + simpleCode; // Sinon, on retourne la chaîne de caractère demandé
        }

        /**
         * Récupère la {@link SkinUpdateFrequency fréquence de mise à jour du Skin} du NPC.
         *
         * @return la {@link SkinUpdateFrequency fréquence de mise à jour du Skin} du NPC
         */
        public SkinUpdateFrequency getSkinUpdateFrequency() { return skinUpdateFrequency; }

        /**
         * Définit le {@link UpdateGazeType Type de Mise à jour pour le suivi de regard} du NPC.
         *
         * @param updateGazeType Le {@link UpdateGazeType Type de Mise à jour pour le suivi de regard} du NPC en question
         *
         */
        public void setUpdateGazeType(@Nonnull UpdateGazeType updateGazeType) {

            // Si le Type de Mise à jour pour le mouvement du NPC est null, on affiche une erreur
            Validate.notNull(updateGazeType, "Le type de gaze de mise à jour ne doit pas être nul");

            // Si le Type de Mise à jour pour le mouvement du NPC est égal au Type de Mise à jour pour le mouvement du NPC récupéré, on ne fait rien
            if(this.updateGazeType.equals(updateGazeType)) return;

            // Définit le Type de Mise à jour pour le mouvement du NPC par le Type de Mise à jour pour le mouvement du NPC récupéré en paramètre
            this.updateGazeType = updateGazeType;

            // Si le plugin actuel de la librairie NPC est égal au plugin en question, on sauvegarde la configuration de la librairie NPC
            if(plugin.equals(npcUtils.getPlugin())) npcUtils.saveConfig();
            runGazeUpdate(); //  Met à jour les ticks de la tâche d'exécution active
        }

        /**
         * Définit le nombre de ticks pour le mouvement du NPC en question
         *
         * @param ticks Le nombre de ticks en question
         */
        public void setUpdateGazeTicks(Integer ticks) {

            if(ticks < 1) ticks = 1; // Si le tick est inférieur à '1', on la définit à '1'

            // Si le tick est égal au nombre de ticks actuel pour le mouvement du NPC en question, on ne fait donc rien
            if(ticks.equals(this.updateGazeTicks)) return;

            // Remplace le nombre de tick actuel pour le mouvement du NPC en question, par le tick récupéré en paramètre
            this.updateGazeTicks = ticks;

            // Si le plugin actuel de la librairie NPC est égal au plugin en question, on sauvegarde la configuration de la librairie NPC
            if(plugin.equals(npcUtils.getPlugin())) npcUtils.saveConfig();

            // Met à jour les ticks de la tâche d'exécution active, si le type de mise à jour est bien en ticks
            if(this.updateGazeType.equals(UpdateGazeType.TICKS)) runGazeUpdate();
        }

        /**
         * Met à jour les ticks de la tâche d'exécution active.
         *
         */
        private void runGazeUpdate() {

            // Si l'Identifiant de la tâche actuelle, n'est pas null, on annule la tâche en cours
            if(taskID != null) plugin.getServer().getScheduler().cancelTask(taskID);

            /*  ⬇️ Si Type de Mise à jour pour le mouvement du NPC est bien en ticks, on définit alors une tâche qui mettra à jour
               le déplacement du NPC à chaque ticks récupéré dynamiquement par le nombre de ticks pour le mouvement du NPC en question ⬇️  */
            if(updateGazeType.equals(UpdateGazeType.TICKS)) {

                // ⬇️ Définit l'identifiant de la tâche actuelle par la boucle de mise à jour du mouvement du NPC à chaque ticks ⬇️ //
                taskID = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> Bukkit.getOnlinePlayers().forEach(x-> npcUtils.getNPCPlayerManager(x).updateMove(plugin)), 0L, updateGazeTicks).getTaskId();
                // ⬆️ Définit l'identifiant de la tâche actuelle par la boucle de mise à jour du mouvement du NPC à chaque ticks ⬆️ //
            }
            /* ⬆️ Si Type de Mise à jour pour le mouvement du NPC est bien en ticks, on définit alors une tâche qui mettra à jour
               le déplacement du NPC à chaque ticks récupéré dynamiquement par le nombre de ticks pour le mouvement du NPC en question ⬆️ */
        }

        /**
         * Définit la {@link SkinUpdateFrequency fréquence de mise à jour du Skin} du NPC.
         *
         * @param skinUpdateFrequency  La {@link SkinUpdateFrequency fréquence de mise à jour du Skin} du NPC en question
         *
         */
        public void setSkinUpdateFrequency(SkinUpdateFrequency skinUpdateFrequency) {

            this.skinUpdateFrequency = skinUpdateFrequency;
            if(plugin.equals(npcUtils.getPlugin())) npcUtils.saveConfig();
        }

        /**
         * Récupère le nombre de ticks pour la disparition du NPC dans le 'tablist'.
         *
         * @param ticksUntilTabListHide Le nombre de ticks pour la disparition du NPC dans le 'tablist' en question
         *
         */
        public void setTicksUntilTabListHide(Integer ticksUntilTabListHide) {

            if(ticksUntilTabListHide < 1) ticksUntilTabListHide = 1; // Si le tick est inférieur à '1', on la définit à '1'

            // Si le tick est égal au nombre de ticks actuel pour la disparition du NPC en question dans le 'tablist', on ne fait donc rien
            if(ticksUntilTabListHide.equals(this.ticksUntilTabListHide)) return;

            // Remplace le nombre de tick actuel pour la disparition du NPC en question dans le 'tablist', par le tick récupéré en paramètre
            this.ticksUntilTabListHide = ticksUntilTabListHide;

            // Si le plugin actuel de la librairie NPC est égal au plugin en question, on sauvegarde la configuration de la librairie NPC
            if(plugin.equals(npcUtils.getPlugin())) npcUtils.saveConfig();
        }
                                        /* --------------------------------------------- */

        /**
         * Effectue un évènement quand le joueur se déplace.
         *
         * @param event {@link PlayerMoveEvent} - Évènement au déplacement du Joueur
         */
        @EventHandler
        private void onMove(PlayerMoveEvent event) {

            // Si le type de mise à jour pour le mouvement du NPC n'est pas lors d'un mouvement du Joueur, on ne fait rien
            if(!getUpdateGazeType().equals(UpdateGazeType.MOVE_EVENT)) return;

            // Si le Joueur n'a pas bougé d'un bloc, on ne fait rien
            if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) return;

            Player player = event.getPlayer(); // On récupère le Joueur de l'évènement en question

            // ⬇️ On essaie de mettre à jour les mouvements des NPCs pour le joueur en question ⬇️ //
            try { npcUtils.getNPCPlayerManager(player).updateMove(plugin); }
            catch(NoClassDefFoundError ignored) {}
           // ⬆️ On essaie de mettre à jour les mouvements des NPCs pour le joueur en question ⬆️ //

        }
    }

                                        /* --------------------------------------------- */
                                        /* --------------------------------------------- */

    /**
     * Permet de mettre en place la fréquence de mise à jour du Skin du NPC
     *
     * @param value La valeur de la fréquence
     * @param timeUnit L'Unité de temps de la fréquence
     */
    public record SkinUpdateFrequency(Integer value, TimeUnit timeUnit) implements ConfigurationSerializable {

        /**
         * Met en place la fréquence de mise à jour du Skin du NPC
         *
         * @param value La valeur de la fréquence
         * @param timeUnit L'Unité de temps de la fréquence
         */
        public SkinUpdateFrequency  {

            Objects.requireNonNull(value); // La valeur de la fréquence ne peut pas être nul !
            Objects.requireNonNull(timeUnit); // L'Unité de temps de la fréquence ne peut pas être nul !

            // Si l'unité de temps ne correspond pas à des secondes, des heures, de minutes ou des jours, on renvoie une erreur
            Validate.isTrue(timeUnit.equals(TimeUnit.SECONDS) || timeUnit.equals(TimeUnit.MINUTES) || timeUnit.equals(TimeUnit.HOURS) || timeUnit.equals(TimeUnit.DAYS), "L'unité de temps doit être des secondes, des minutes, des heures ou des jours.");
        }

        /**
         * Effectue une sérialisation pour la {@link SkinUpdateFrequency fréquence de mise à jour du Skin}  du NPC.
         *
         * @return La {@link SkinUpdateFrequency fréquence de mise à jour du Skin} du NPC sérialisée
         */
        @Override
        public Map<String, Object> serialize() {

            Map<String, Object> hash = new HashMap<>(); // Initialise une sorte de dictionnaire stockant la valeur de fréquence ainsi que l'unité de temps

            hash.put("value", value); // Ajoute au dictionnaire la valeur de fréquence actuel
            hash.put("timeUnit", timeUnit.name()); // Ajoute au dictionnaire l'unité de temps actuel

            return hash; // Retourne le dictionnaire
        }

        /**
         * Effectue une désérialisation pour la {@link SkinUpdateFrequency fréquence de mise à jour du Skin} du NPC sérialisée.
         *
         * @param map La sérialisation de la {@link SkinUpdateFrequency fréquence de mise à jour du Skin} du NPC
         *
         * @return Une instance de la {@link SkinUpdateFrequency fréquence de mise à jour du Skin} du NPC
         */
        public static SkinUpdateFrequency deserialize(Map<String, Object> map) { return new SkinUpdateFrequency((Integer) map.get("value"), TimeUnit.valueOf((String) map.get("timeUnit"))); }
    }

                                        /* --------------------------------------------- */
                                        /* --------------------------------------------- */

    /**
     * Le gestionnaire des Joueurs.
     *
     */
    protected static class PlayerManager {
        private final NPCUtils npcUtils; // Variable récupérant la librairie NPC
        private final Player player; // Variable récupérant le Joueur en question
        private final HashMap<String, NPCPersonal> npcs; // Variable récupérant les NPC Personnels du Joueur
        private final PlayerManager.PacketReader packetReader; // Variable récupérant le lecteur de paquet du Joueur
        private final Map<World, Set<NPCPersonal>> hidden; // Variable récupérant les NPCs cachés pour le Joueur
        private final Long lastEnter; // Variable récupérant la dernière entrée du Joueur

        /**
         * Instancie le gestionnaire des joueurs.
         *
         * @param npcUtils La librairie NPC en question
         * @param player Le Joueur en question
         *
         */
        protected PlayerManager(NPCUtils npcUtils, Player player) {

            this.npcUtils = npcUtils; // Initialise la librairie NPC en question
            this.player = player; // Initialise le joueur en question

            this.npcs = new HashMap<>(); // Initialise la liste des NPCs

            this.packetReader = new PlayerManager.PacketReader(this); // Initialise le lecteur de paquet du joueur

            this.hidden = new HashMap<>(); // Initialise la liste des NPCs cachés

            this.lastEnter = System.currentTimeMillis(); // Initialise la dernière entrée du Joueur
        }

        /**
         * Enregistre un nouveau {@link NPCPersonal NPC Personnel} associé au Joueur en question.
         *
         * @param s Le nom de l'Enregistrement
         * @param npc Le {@link NPCPersonal NPC Personnel} en question
         */
        protected void set(String s, NPCPersonal npc) { npcs.put(s, npc); }

        /**
         * Récupère un {@link NPCPersonal NPC Personnel} associé au Joueur en question.
         *
         * @param entityID L'Identifiant de l'Entité du {@link NPCPersonal NPC Personnel} en question
         *
         * @return Le {@link NPCPersonal NPC Personnel} demandé
         */
        protected NPCPersonal getNPC(Integer entityID) { return npcs.values().stream().filter(x-> x.isCreated() && NMSEntity.getEntityID(x.getEntity()).equals(entityID)).findAny().orElse(null); }

        /**
         * Désenregistre un {@link NPCPersonal NPC Personnel} associé au Joueur en question.
         *
         * @param s Le nom de l'Enregistrement du {@link NPCPersonal NPC Personnel} en question
         */
        protected void removeNPC(String s) { npcs.remove(s); }

        /**
         * Met à jour le mouvement des NPCs associé au Joueur en question à partir d'un plugin précisé.
         *
         * @param plugin Le Plugin en question
         */
        protected void updateMove(Plugin plugin) { getNPCs(getPlayer().getWorld()).stream().filter(x-> x.getPlugin().equals(plugin) && x.isCreated()).forEach(NPCPersonal::updateMove); }

        /**
         * Détruit les NPCs associé au Joueur en question étant dans un monde demandé.
         *
         * @param world Le monde en question
         */
        protected void destroyWorld(World world) {

            Set<NPCPersonal> allNPC = new HashSet<>(); // Initialise une liste récupérant les NPCs dans un monde précis

            // On ajoute à la liste tous les NPCs du monde récupéré en paramètre
            npcs.values().stream().filter(x-> x.canSee() && x.getWorld().getName().equals(world.getName())).forEach(x-> { x.hide(); allNPC.add(x); });

            hidden.put(world, allNPC); // On ajoute tous les NPCs de la liste pour le monde actuel dans la liste des NPCs cachés pour le Joueur
        }

        /**
         * Affiche les NPCs associé au Joueur en question étant dans un monde demandé.
         *
         * @param world Le monde en question
         */
        protected void showWorld(World world) {

            // Si le monde actuel récupérant tous les NPCs de ce monde est introuvable dans la liste des NPCs cachés pour le Joueur, on ne fait donc rien
            if(!hidden.containsKey(world)) return;

            // On récupère tous les NPCs en question, et on les affiche pour le Joueur
            hidden.get(world).stream().filter(NPCPersonal::isCreated).forEach(NPCPersonal::show);
            hidden.remove(world); // On les enlève également de la liste
        }

        /**
         * Déplace un {@link NPCPersonal NPC Personnel} associé au Joueur en question d'un monde 'A' vers un monde 'B'.
         *
         * @param npc Le {@link NPCPersonal NPC Personnel} en question
         * @param from Le Monde actuel du NPC
         * @param to Le nouveau Monde du NPC
         *
         */
        protected void changeWorld(NPCPersonal npc, World from, World to) {

            /* Si le monde où se situe le NPC actuellement récupérant tous les NPCs de ce même monde est introuvable dans la liste des NPCs cachés pour le Joueur,
               on ne fait donc rien */
            if(!hidden.containsKey(from)) return;

            /* Si le monde où se situe le NPC actuellement récupérant tous les NPCs de ce même monde ne trouve pas dans la liste des NPCs cachés pour le Joueur
               le NPC en question, on ne fait donc rien */
            if(!hidden.get(from).contains(npc)) return;

            hidden.get(from).remove(npc); // Sinon, on récupère tous le NPC en question, et on le supprime de la list
            npc.show(); // Ensuite on l'affiche
        }

        /**
         * Détruit tous les NPCs associés au Joueur en question.
         *
         */
        protected void destroyAll() {

            Set<NPCPersonal> destroy = new HashSet<>(npcs.values()); // Initialise une liste récupérant tous les enregistrements des NPCs associé au Joueur
            destroy.stream().filter(NPCPersonal::isCreated).forEach(NPCPersonal::destroy); // On vérifie alors si chaque NPC est créé, donc on les détruit
            npcs.clear(); // Ensuite, on supprime la liste des retirements des NPCs associé au Joueur
        }

        /**
         * Récupère tous les NPCs associés au Joueur en question étant dans le monde spécifié.
         *
         * @param world Le Monde en question
         *
         * @return Tous les NPCs associés au Joueur en question étant dans le monde demandé
         */
        protected Set<NPCPersonal> getNPCs(World world) {

            // Si le Monde est null, on renvoie une erreur
            Validate.notNull(world, "Le monde ne doit pas être nul.");

            // On retourne tous les NPCs associés au Joueur en question étant dans le monde récupéré en paramètre
            return npcs.values().stream().filter(x-> x.getWorld().equals(world)).collect(Collectors.toSet());
        }

        /**
         * Récupère tous les NPCs associés au Joueur en question étant géré par le plugin spécifié.
         *
         * @param plugin Le plugin en question
         *
         * @return Tous les NPCs associés au Joueur en question étant géré par le plugin demandé
         */
        protected Set<NPCPersonal> getNPCs(Plugin plugin) {

            // Si le plugin est null, on renvoie une erreur
            Validate.notNull(plugin, "Le plugin ne doit pas être nul.");

            // On retourne tous les NPCs associés au Joueur en question étant géré par le plugin récupéré en paramètre
            return npcs.values().stream().filter(x-> x.getPlugin().equals(plugin)).collect(Collectors.toSet());
        }

        /**
         * Récupère tous les NPCs associés au Joueur en question.
         *
         * @return Tous les NPCs associés au Joueur en question
         */
        protected Set<NPCPersonal> getNPCs() { return new HashSet<>(npcs.values()); }

        /**
         * Récupère un {@link NPCPersonal NPC Personnel} associé au Joueur en question en fonction de son enregistrement.
         *
         * @param s Le nom de l'enregistrement en question
         *
         * @return Le {@link NPCPersonal NPC Personnel} associé au Joueur en question dépendant de l'enregistrement demandé
         */
        protected NPCPersonal getNPC(String s) {

            if(!npcs.containsKey(s)) return null; // Si le nom de l'enregistrement actuel est introuvable, on retourne 'null'
            return npcs.get(s); // Sinon, on retourne le NPC étant associé à l'enregistrement en question
        }

        /**
         * Récupère la {@link NPCUtils librairie NPC} associé.
         *
         * @return La {@link NPCUtils librairie NPC} associé
         */
        protected NPCUtils getNPCLib() { return npcUtils; }

        /**
         * Récupère le Joueur en question.
         *
         * @return le Joueur en question
         */
        protected Player getPlayer() { return player; }

        /**
         * Récupère le {@link PlayerManager.PacketReader Lecteur de Paquets} du Joueur en question.
         *
         * @return le {@link PlayerManager.PacketReader Lecteur de Paquets} du Joueur en question
         */
        protected PlayerManager.PacketReader getPacketReader() { return packetReader; }

        /**
         * Récupère la dernière entrée du Joueur en question.
         *
         * @return La dernière entrée du Joueur en question
         */
        protected Long getLastEnter() { return lastEnter; }

                                                        /* --------------------------------- */

        /**
         * Lecteur de Paquets de Joueurs
         *
         */
        protected static class PacketReader {

            private final HashMap<NPC, Long> lastClick; // Variable récupérant les dernières cliques effectuées
            private final PlayerManager npcPlayerManager; // Variable récupérant le gestionnaire des Joueurs pour les NPCs
            private Channel channel; // Variable récupérant le canal de la connexion du joueur

            /**
             * Instancie un nouveau {@link PlayerManager.PacketReader Lecteur de Paquets} pour le Joueur.
             *
             * @param npcPlayerManager Le Gestionnaire de joueur en question
             */
            protected PacketReader(PlayerManager npcPlayerManager) {

                this.npcPlayerManager = npcPlayerManager; // On initialise le gestionnaire des Joueurs pour les NPCs
                this.lastClick = new HashMap<>(); // On Initialise la liste des dernières cliques effectuées
            }

            /**
             * Injecte le lecteur de paquets du Joueur.
             *
             */
            protected void inject() {

                if(channel != null) return; // Si le canal n'est pas null, on ne fait rien

                // On récupère le canal de la connexion du joueur
                channel = NMSNetworkManager.getChannel(NMSNetworkManager.getNetworkManager(npcPlayerManager.getPlayer()));
                if(channel.pipeline() == null) return; // Si le pipeline du canal est null, on ne fait rien
                if(channel.pipeline().get("PacketInjector") != null) return; // Si 'PacketInjector' récupérer dans le canal n'est pas null, on ne fait rien

                // ⬇️ On enregistre 'PacketInjector' sur le canal pour décoder chaque paquet envoyé par le joueur, ensuite, on lit le paquet en question ⬇️ //
                channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<ServerboundInteractPacket>() {

                    @Override
                    protected void decode(ChannelHandlerContext channel, ServerboundInteractPacket packet, List<Object> arg) {

                        arg.add(packet);
                        readPacket(packet);
                    }
                });
                 // ⬆️ On enregistre 'PacketInjector' sur le canal pour décoder chaque paquet envoyé par le joueur, ensuite, on lit le paquet en question ⬆️ //
            }

            /**
             * Annule l'injection du lecteur de paquets du Joueur.
             *
             */
            protected void unInject() {

                if(channel == null) return; // Si le canal est null, on ne fait rien
                if(channel.pipeline() == null) return; // Si le pipeline du canal est null, on ne fait rien
                if(channel.pipeline().get("PacketInjector") == null) return; // Si 'PacketInjector' récupérer dans le canal est null, on ne fait rien
                channel.pipeline().remove("PacketInjector"); // On supprime 'PacketInjector' du canal
                channel = null; // On définit le canal actuel sur 'null'
            }

            /**
             * Permet de lire un Paquet précis.
             *
             */
            private void readPacket(Packet<?> packet) {

                // Si le paquet actuel est null, on ne fait donc rien
                if(packet == null) return;

                // Si le paquet actuel n'est pas un paquet d'intéraction, on ne fait donc rien
                if(!packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) return;

                int id = (int)NMSUtils.getValue(packet, "a"); // On récupère l'identification du paquet
                NPC.Interact.ClickType clickType; // initialise le type de cliqué pour l'intéraction du NPC

                // ⬇️ On essaie de récupérer le type de cliqué en fonction de la main du Joueur en question, sinon on définit le type de cliqué en tant que clique gauche ⬇️ //
                try {

                    Object action = NMSUtils.getValue(packet, "b");
                    InteractionHand hand = (InteractionHand)NMSUtils.getValue(action, "a");
                    if(hand != null) clickType = NPC.Interact.ClickType.RIGHT_CLICK;
                    else clickType = NPC.Interact.ClickType.LEFT_CLICK;

                } catch(Exception e) { clickType = NPC.Interact.ClickType.LEFT_CLICK; }
                 // ⬆️ On essaie de récupérer le type de cliqué en fonction de la main du Joueur en question, sinon on définit le type de cliqué en tant que clique gauche ⬆️ //

                interact(id, clickType); // Enregistre l'intéraction du NPC
            }

            /**
             * Enregistre une intéraction du NPC en récupérant son avec son Joueur associé en récupérant l'identifiant du NPC en question.
             *
             * @param id l'Identifiant du NPC en question
             * @param clickType Le type de cliqué avec lequel on peut intéragir avec le NPC
             */
            private void interact(Integer id, NPC.Interact.ClickType clickType) {

                // récupère le NPC Personnel à partir de l'identifiant récupéré en paramètre
                NPCPersonal npc = getNPCLib().getNPCPlayerManager(getPlayerManager().getPlayer()).getNPC(id);

                if(npc == null) return; // Si le NPC est null, on ne fait rien
                interact(npc, clickType); // Sinon, on enregistre l'intéraction avec le NPC récupéré
            }

            /**
             * Enregistre une intéraction du NPC avec son Joueur associé.
             *
             * @param npc Le NPC en question
             * @param clickType Le type de cliqué avec lequel on peut intéragir avec le NPC
             */
            private void interact(NPCPersonal npc, NPC.Interact.ClickType clickType) {

                if(npc == null) return; // Si le NPC est null, on ne fait rien

                // On vérifie si les dernières cliques effectuées au NPC sont inférieures à son temps d'intéraction moins le temps actuel, alors on ne fait rien
                if(lastClick.containsKey(npc) && System.currentTimeMillis() - lastClick.get(npc) < npc.getInteractCooldown()) return;
                lastClick.put(npc, System.currentTimeMillis()); // On définit ajoute un cliqué effectué au NPC

                // Aprés quelque temps, on ajoute l'intéraction au NPC
                Bukkit.getScheduler().scheduleSyncDelayedTask(npcPlayerManager.getNPCLib().getPlugin(), ()-> npc.interact(npcPlayerManager.getPlayer(), clickType), 1);
            }

            /**
             * Récupère le {@link PlayerManager Gestionnaire du Joueur} en question
             *
             * @return le {@link PlayerManager Gestionnaire du Joueur}
             */
            protected PlayerManager getPlayerManager() { return npcPlayerManager; }

            /**
             * Récupère la {@link NPCUtils librairie NPC} associé.
             *
             * @return La {@link NPCUtils librairie NPC} associé
             */
            protected NPCUtils getNPCLib() { return npcPlayerManager.getNPCLib(); }

        }

    }
}