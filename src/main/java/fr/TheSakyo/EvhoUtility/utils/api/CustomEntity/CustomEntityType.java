package fr.TheSakyo.EvhoUtility.utils.api.CustomEntity;

import fr.TheSakyo.EvhoUtility.utils.api.CustomEntity.listeners.CustomEntityListener;
import fr.TheSakyo.EvhoUtility.utils.api.CustomEntity.listeners.PlayerPacketListener;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.Skin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param <T> Le type de {@link Entity} qui sera la véritable représentation interne comprise par le serveur.
 */
public abstract class CustomEntityType<T extends Entity> {

    /**
     * Le {@link JavaPlugin} récupéré par le ClassLoader. Il est utilisé pour enregistrer les auditeurs et
     * pour gérer les données persistantes sur les entités.
     */
    private static JavaPlugin plugin = null;

    /**
     * La {@link NamespacedKey} utilisée pour l'interface avec {@link PersistentDataContainer}.
     */
    private static NamespacedKey entityKey = null;

    /**
     * Le registre interne des {@link CustomEntityType} en tant que {@link ConcurrentHashMap} pour un accès multithreading sûr.
     */
    @NotNull
    private static final Map<String, CustomEntityType<?>> REGISTRY = new ConcurrentHashMap<>();

    @NotNull
    private final String id;

    @Nullable
    private final Skin skin;

    @NotNull
    private final Class<T> internalEntityClass;

    @NotNull
    private final EntityType internalType;

    @NotNull
    private final Class<? extends Entity> displayEntityClass;

    @NotNull
    private final EntityType displayType;

    /**
     * Ni la classe Interne ni la class d'Affichage ne peuvent être des entités qui ne peuvent pas apparaître.<br>
     * Vous pouvez vérifier si l'entité que vous utilisez peut apparaître en vérifiant la variable "independent" de '{@link EntityType}', elle doit être 'true/unset'.
     *
     * @param id L'Identifiant à utiliser dans le registre.
     * @param skin Le Skin à utiliser pour l'affichage, ne fonctionne que si l'entité d'affichage est un {@link Player Joueur}.
     * @param internalEntityClass La {@link Class<T> classe} du type de l'{@link Entity entité} à créer réellement sur le serveur.
     * @param displayEntityClass La {@link Class<T> classe} du type de l'{@link Entity entité} à envoyer au client.
     */
    public CustomEntityType(@NotNull String id, @Nullable Skin skin, @NotNull Class<T> internalEntityClass, @NotNull Class<? extends Entity> displayEntityClass) {

        this.id = id;
        this.skin = skin;
        this.internalEntityClass = internalEntityClass;
        this.displayEntityClass = displayEntityClass;

        EntityType internalType = getEntityType(internalEntityClass);
        EntityType displayType = getEntityType(displayEntityClass);

        if(internalType == null) {

            throw new IllegalArgumentException("La Class<Entity> interne de CustomEntityType[%s] \"%s\" n'a pas d'EntityType.".formatted(this.id, internalEntityClass));
        }
        this.internalType = internalType;

                                                            /* -------------------------------------- */

        if(!this.internalType.isSpawnable()) {

            throw new IllegalArgumentException("L'EntityType internet de CustomEntityType[%s]  \"%s\" ne peut pas spawn.".formatted(this.id, internalType));
        }
                                                    /* --------------------------------------------------- */
        if(displayType == null) {

            throw new IllegalArgumentException("La Class<Entity> d'affichage de CustomEntityType[%s] \"%s\" n'a pas d'EntityType.".formatted(this.id, displayEntityClass));
        }
        this.displayType = displayType;
    }

    /**
     * Ni la classe Interne ni la class d'Affichage ne peuvent être des entités qui ne peuvent pas apparaître.<br>
     * Vous pouvez vérifier si l'entité que vous utilisez peut apparaître en vérifiant la variable "independent" de '{@link EntityType}', elle doit être 'true/unset'.
     *
     * @param id L'Identifiant à utiliser dans le registre.
     * @param internalEntityClass La {@link Class<T> classe} du type de l'{@link Entity entité} à créer réellement sur le serveur.
     * @param displayEntityClass La {@link Class<T> classe} du type de l'{@link Entity entité} à envoyer au client.
     */
    public CustomEntityType(@NotNull String id, @NotNull Class<T> internalEntityClass, @NotNull Class<? extends Entity> displayEntityClass) { this(id, null, internalEntityClass, displayEntityClass); }

    @NotNull
    public String getID() { return id; }

    @NotNull
    public EntityType getInternalType() { return internalType; }

    @NotNull
    public EntityType getDisplayType() { return displayType; }

    @NotNull
    public Class<? extends Entity> getInternalEntityClass() { return internalEntityClass; }

    @NotNull
    public Class<? extends Entity> getDisplayEntityClass() { return displayEntityClass; }

    @Nullable
    public Skin getSkin() { return skin; }

    /**
     * Ceci sera appelé chaque fois que ce {@link CustomEntityType} sera cliqué du bouton droit ou gauche de la souris par un {@link Player}.<br>
     * <b>Note</b>: Ceci sera appelé deux fois pour chaque main dans l'événement {@link PlayerInteractAtEntityEvent}.<br><br>.
     * Dans la plupart des cas, il suffit de vérifier si {@link PlayerInteractAtEntityEvent event}.getHand() == {@link EquipmentSlot EquipmentSlot.HAND}.
     *
     * @param entity L'{@link T Entité} qui a fait l'objet d'un clic.
     * @param player Le {@link Player Joueur} qui a fait un clic droit sur l'entité.
     * @param event  L'Instance de l'{@link PlayerInteractAtEntityEvent évènement} d'intéraction.
     */
    public void onClick(@NotNull T entity, @NotNull Player player, @NotNull PlayerInteractAtEntityEvent event) {}

    /**
     * Cette fonction sera appelée chaque fois que ce {@link CustomEntityType} est endommagé.
     *
     * @param entity L'{@link T Entité} qui a été endommagé.
     * @param event  L'Instance de l'{@link EntityDamageEvent évènement} de dommage.
     */
    public void onDamage(@NotNull T entity, @NotNull EntityDamageEvent event) {}

    /**
     * Cette fonction sera appelée chaque fois que ce {@link CustomEntityType} inflige des dommages à une autre {@link Entity}.
     *
     * @param entity  L'{@link T Entité} qui a infligé des dégâts.
     * @param damaged L'{@link Entity Entité} qui a été endommagé.
     * @param event  L'Instance de l'{@link EntityDamageByEntityEvent évènement} de dommage.
     */
    public void onDamageToEntity(@NotNull T entity, @NotNull Entity damaged, @NotNull EntityDamageByEntityEvent event) {}

    /**
     * Cette fonction sera appelée chaque fois que ce {@link CustomEntityType} est endommagée par une autre {@link Entity}.
     *
     * @param entity  L'{@link T Entité} qui a été endommagé.
     * @param damager L'{@link Entity Entité} qui a infligé des dégâts.
     * @param event   L'Instance de l'{@link EntityDamageByEntityEvent évènement} de dommage.
     */
    public void onDamageByEntity(@NotNull T entity, @NotNull Entity damager, @NotNull EntityDamageByEntityEvent event) {}

    /**
     * Cette fonction sera appelée chaque fois que ce {@link CustomEntityType} est endommagé par un {@link Block}.
     *
     * @param entity L'{@link T Entité} that was damaged.
     * @param block  Le {@link Block bloc} qui a endommagé l'entité.
     * @param event  L'Instance de l'{@link EntityDamageByBlockEvent évènement} de dommage.
     */
    public void onDamageByBlock(@NotNull T entity, @Nullable Block block, @NotNull EntityDamageByBlockEvent event) {}

    /**
     * Ceci sera appelé chaque fois que ce {@link CustomEntityType} meurt.
     *
     * @param entity L'{@link T Entité} that died.
     * @param event  L'Instance de l'{@link EntityDeathEvent évènement} de mort.
     */
    public void onDeath(@NotNull T entity, @NotNull EntityDeathEvent event) {}

    /**
     * Cette fonction sera appelée chaque fois que ce {@link CustomEntityType} est créé.
     *
     * @param entity L'{@link T Entité} qui a apparu.
     */
    public void onSpawn(@NotNull T entity) {}

    /**
     * Cette fonction sera appelée chaque fois que ce {@link CustomEntityType} est en train de se créer.
     *
     * @param entity - L'{@link Entity Entité} qui a apparu.
     */
    public void onPreSpawn(@NotNull T entity) {}

    /**
     * Fait apparaître le {@link CustomEntityType} à l'{@link Location emplacement} donné et permet de près spawn {@link Consumer<T>} à être passé.
     *
     * @param location         L'{@link Location Emplacement} à apparaître pour l'{@link T entité}.
     * @param preSpawnFunction Passe à la fonction world.spawn() qui est exécutée avant que l'{@link Entity entité} ne soit dans le monde.
     *
     * @return L'{@link T Entité}.
     */
    @NotNull
    public final T spawn(Location location, Consumer<T> preSpawnFunction) {
        World world = location.getWorld();
        T entity = world.spawn(location, this.internalEntityClass, CreatureSpawnEvent.SpawnReason.CUSTOM, (preEntity) -> {
            CustomEntityType.set(preEntity, this);
            preSpawnFunction.accept(preEntity);
            onPreSpawn(preEntity);
        });
        this.onSpawn(entity);
        return entity;
    }

    /**
     * Fait apparaître le {@link CustomEntityType} l'{@link Location emplacement} donné.
     *
     * @param location  L'{@link Location Emplacement} à apparaître pour l'{@link T entité}.
     * @return L'{@link T Entité}.
     */
    @NotNull
    public final T spawn(Location location) { return this.spawn(location, (entity) -> {}); }

    /**
     * Une fonction de spawn dynamique pour quand vous voulez juste spawn une entité qui ressemble à une autre entité sur le client.<br><br>.
     * <b>Note:</b> les entités apparûs avec cette fonction ne seront PAS persistantes !<br>
     * Il s'agit d'une fonctionnalité expérimentale, à utiliser à vos propres risques.
     *
     * @param internal Le {@link EntityType type d'entité internet} à utiliser pour cette {@link Entity entité}.
     * @param display  Le {@link EntityType type d'entité d'affichage} que les clients verront.
     * @param location  L'{@link Location Emplacement} à apparaître pour l'{@link Entity entité}.
     *
     * @return L'{@link Entity Entité} qui est apparu, ou null si l'affichage ou l'interne n'ont pas de classe d'entité.
     */
    public static Entity spawn(EntityType internal, EntityType display, Location location) {

        String dynamicID = internal.name() + "-" + display.name();

        if(internal.getEntityClass() == null) return null;
        if(display.getEntityClass() == null) return null;


        CustomEntityType<?> type = new CustomEntityType(dynamicID, internal.getEntityClass(), display.getEntityClass()) {};
        register(type);

        return location.getWorld().spawn(location, internal.getEntityClass(), (entity) -> set(entity, type));
    }

    /**
     * Ceci sera invoqué lorsque register() est appelé et que le {@link JavaPlugin plugin} n'est pas initialisé.
     * Ceci est responsable de l'enregistrement des événements et de la construction de la clé, ceci existe uniquement de cette façon
     * pour permettre l'initialisation statique de {@link CustomEntityType} en dehors de 'onEnable'.
     */
    private static void initialize() {

        plugin = JavaPlugin.getProvidingPlugin(CustomEntityType.class);
        entityKey = new NamespacedKey(plugin, "custom_entity");

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerPacketListener(plugin), plugin);
        pluginManager.registerEvents(new CustomEntityListener(), plugin);
    }

    /**
     * Ceci enregistrera le {@link CustomEntityType} et DOIT être appelé dans le 'onEnable' de votre plugin avant de faire quoi que ce soit d'autre.
     * Elle est également responsable de l'initialisation correcte des {@link Listener auditeurs}.
     *
     * @param type Le {@link CustomEntityType} à enregistrer.
     */
    public static void register(CustomEntityType<?> type) {

        if(plugin == null) initialize();
        REGISTRY.put(type.getID(), type);
    }

    /**
     * Obtient le {@link CustomEntityType} enregistré pour l'{@link String identifiant} donnée.
     *
     * @param id Le {@link String} ID du {@link CustomEntityType} à récupérer dans le registre.
     *
     * @return Le {@link CustomEntityType} pour l'ID donné, ou null s'il n'existe pas.
     */
    @Nullable
    public static CustomEntityType<?> get(String id) { return REGISTRY.get(id); }

    /**
     * Une méthode pratique pour obtenir le {@link CustomEntityType} à partir des métadonnées {@link Entity}.
     *
     * @param entity L'{@link Entity Entité} à partir de laquelle obtenir le {@link CustomEntityType}
     *
     * @return Le {@link CustomEntityType} approprié, ou null s'il n'en a pas, ou si l'ID est invalide.
     */
    @Nullable
    public static CustomEntityType<?> get(Entity entity) {
        PersistentDataContainer data = entity.getPersistentDataContainer();

        if(!data.has(entityKey, PersistentDataType.STRING)) return null;
        String customEntityID = data.get(entityKey, PersistentDataType.STRING);

        //TODO: On pourrait peut-être passer un booléen pour savoir si l'on doit lancer une exception ou autre chose lorsque l'on renvoie un résultat nul ?
        return REGISTRY.get(customEntityID);
    }

    /**
     * Une méthode pratique pour donner un {@link CustomEntityType} à une {@link Entity}.
     *
     * @param entity L'{@link Entity Entité} devant recevoir le {@link CustomEntityType}.
     * @param customEntityType Le {@link CustomEntityType} à donner à l'{@link Entity entité}.
     */
    public static void set(Entity entity, CustomEntityType<?> customEntityType) {

        PersistentDataContainer data = entity.getPersistentDataContainer();
        data.set(entityKey, PersistentDataType.STRING, customEntityType.getID());
    }

    /**
     * Fonction pratique permettant de récupérer le {@link EntityType type d'entité} d'une {@link Class<Entity> classe d'Entité}.
     *
     * @param clazz La {@link Class<Entity> classe} pour laquelle trouver le {@link EntityType type d'entité}.
     *
     * @return Le {@link EntityType type d'entité} approprié, ou null si non trouvé.
     */
    @Nullable
    private static EntityType getEntityType(Class<? extends Entity> clazz) {
        for (EntityType entityType : EntityType.values()) {
            if (entityType.getEntityClass() != clazz) {
                continue;
            }

            return entityType;
        }

        return null;
    }
}
