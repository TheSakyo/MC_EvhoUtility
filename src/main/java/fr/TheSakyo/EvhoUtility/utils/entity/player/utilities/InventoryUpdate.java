/*
* La licence MIT (MIT)
*
* Copyright (c) 2021 Matsubara
*
* La permission est accordée par la présente, sans frais, à toute personne obtenant une copie
* de ce logiciel et des fichiers de documentation associés (le "Logiciel"), de traiter
* du logiciel sans restriction, y compris, sans limitation, les droits
* d'utiliser, de copier, de modifier, de fusionner, de publier, de distribuer, d'octroyer des sous-licences et/ou de vendre des copies du logiciel, ainsi que d'utiliser et d'échanger des informations.
* et/ou de vendre des copies du logiciel, et d'autoriser les personnes à qui le logiciel est fourni à le faire, sous réserve de l'approbation de l'autorité compétente.
* fourni à le faire, sous réserve des conditions suivantes :
*
* L'avis de copyright ci-dessus et cet avis d'autorisation doivent être inclus dans
* toutes les copies ou parties substantielles du logiciel.
*
* LE LOGICIEL EST FOURNI "EN L'ÉTAT", SANS GARANTIE D'AUCUNE SORTE, EXPRESSE OU IMPLICITE,
* Y COMPRIS, MAIS SANS S'Y LIMITER, LES GARANTIES DE QUALITÉ MARCHANDE, D'ADÉQUATION À UN USAGE PARTICULIER
* PARTICULIER ET DE NON-VIOLATION. EN AUCUN CAS, LES AUTEURS OU LES DÉTENTEURS DE DROITS D'AUTEUR NE SERONT RESPONSABLES
* POUR TOUTE RÉCLAMATION, TOUT DOMMAGE OU TOUTE AUTRE RESPONSABILITÉ, QUE CE SOIT DANS LE CADRE D'UNE ACTION CONTRACTUELLE, DÉLICTUELLE OU AUTRE,
* LES AUTEURS OU LES DÉTENTEURS DE DROITS D'AUTEUR NE SERONT EN AUCUN CAS RESPONSABLES DE TOUTE RÉCLAMATION, DE TOUT DOMMAGE OU DE TOUTE AUTRE RESPONSABILITÉ, QU'IL S'AGISSE D'UNE ACTION CONTRACTUELLE, DÉLICTUELLE OU AUTRE, DÉCOULANT DE OU LIÉE AU LOGICIEL OU À SON UTILISATION OU À D'AUTRES TRANSACTIONS.
*/
package fr.TheSakyo.EvhoUtility.utils.entity.player.utilities;

import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import fr.TheSakyo.EvhoUtility.utils.reflections.ReflectionUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;


/**
* Une classe utilitaire pour mettre à jour l'inventaire d'un joueur.
* Ceci est utile pour changer le titre d'un inventaire.
*/
public final class InventoryUpdate {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
	public InventoryUpdate(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	

    private static ReflectionUtils reflection = new ReflectionUtils(); // Class 'ReflectionUtils'

    //Variables CLASS
    private static Class<?> ENTITY_PLAYER_CLASS;
    private static Class<?> CONTAINER_CLASS;
    private final static Class<?> CONTAINERS_CLASS;

    //Variables METHOD
    private static Method getBukkitView;


    //Variables FIELD
    private static Field activeContainerField;
    
    /* ------------------------------------------------------------------------------------------------------------------------ */

    static {

        //Vérifie si on utilise des conteneurs, sinon on le définit étant 'null' (Cela évite d'entraîner des erreurs sur les anciennes versions)
        CONTAINERS_CLASS = useContainers() ? reflection.getNMSClass("world.inventory.Containers") : null;

        // ⬇️ On essaie de remapper quelque class du 'NMS' en fonction de la version du Serveur ⬇️ //
        try { init(reflection.noException().getNMSClass(reflection.getVersion() + ".EntityPlayer"), reflection.noException().getNMSClass(reflection.getVersion() + ".Container")); }
        catch(Exception ignored) { init(reflection.getNMSClass("server.level.EntityPlayer"), reflection.getNMSClass("world.inventory.Container")); }
        // ⬆️ On essaie de remapper quelque class du 'NMS' en fonction de la version du Serveur ⬆️ //
    }

                 /* ---------------------------------------------------------------------------------------------- */

    /**
     * Il initialise les variables qui seront utilisées pour mettre à jour un inventaire au joueur
     *
     * @param entity_player_class 'class' de l'entité joueur.
     * @param container_class 'class' du conteneur.
     */
    private static void init(final Class<?> entity_player_class, final Class<?> container_class) {

        ENTITY_PLAYER_CLASS = entity_player_class; // Définit la class entité du Joueur
        CONTAINER_CLASS = container_class; // Définit la class du Conteneur

                             /* ---------------------------------------------------- */

        // ⬇️ On essaie de remapper quelque méthodes et attribue du 'NMS' en fonction de la version du Serveur ⬇️ //
        try {

        	//Initialisation de la méthode de vue
            getBukkitView = CONTAINER_CLASS.getMethod("getBukkitView");

            // ⬇️ Initialisation du champ qui récupère le conteneur ⬇️ //
            try { activeContainerField = ENTITY_PLAYER_CLASS.getField("activateContainer"); }
            catch(NoSuchFieldException e1) {

                try { activeContainerField = ENTITY_PLAYER_CLASS.getField("bU"); }
                catch(NoSuchFieldException e2) { activeContainerField = ENTITY_PLAYER_CLASS.getField("bT"); }
            }
            // ⬆️ Initialisation du champ qui récupère le conteneur ⬆️ //

        } catch(NoSuchMethodException | NoSuchFieldException e) { e.printStackTrace(); }
        // ⬆️ On essaie de remapper quelque méthodes et attribue du 'NMS' en fonction de la version du Serveur ⬆️ //
    }

  /* ------------------------------------------------------------------------------------------------------------------------ */
    
    /**
     * Met à jour l'inventaire au joueur, afin de pouvoir changer le titre.
     *
     * @param player L'Inventaire sera mis à jour.
     * @param newTitle Nouveau titre de l'inventaire.
     */
    public void updateInventory(@NotNull Player player, String newTitle) {

        // Envoit un message d'erreur si le joueur est null
        Objects.requireNonNull(player, main.prefix + ChatColor.RED + "Impossible de mettre à jour l'inventaire pour un joueur nul");

        try {

        	//Obtient l'Entité du Joueur à partir du joueur récupéré en paramètre
        	CraftPlayer cp = (CraftPlayer)player;
        	  
            ServerPlayer serverPlayer = cp.getHandle();

            //Obtient la variable "ActiveContainer" de l'Entité du Joueur
            Object activeContainer = activeContainerField.get(serverPlayer);

            //Obtient la vue de l'inventaire à partir "d'activeContainer"
            Object bukkitView = getBukkitView.invoke(activeContainer);

            if(!(bukkitView instanceof InventoryView view)) { return; }

            InventoryType type = view.getTopInventory().getType();

            int size = view.getTopInventory().getSize();

            //Obtient le conteneur, vérifie qu'il n'est pas nul
            Containers container = Containers.getType(type, size);
            if(container == null) { return; }

            //Si le conteneur a été ajouté dans une version plus récente que la version actuelle, renvoyer
            if(container.getContainerVersion() > getVersion() && useContainers()) {
               
               Bukkit.getLogger().warning(main.prefix + ChatColor.RED + "Ce conteneur ne fonctionne pas sur votre version actuelle.");
               return;
            }

            ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(serverPlayer.nextContainerCounter(), (MenuType<?>)container.getObject(), Component.nullToEmpty(newTitle));
            PlayerEntity.sendPacket(packet, player);
            serverPlayer.inventoryMenu.sendAllDataToRemote();

        } catch(Exception e) { e.printStackTrace(); }
    }

       

    /**
     * Les conteneurs ont été ajoutés dans la version 1.14, une chaîne était utilisée dans les versions précédentes.
     *
     * @return L'Utilisation ou non de conteneurs.
     */
    private static boolean useContainers() { return getVersion() > 13; }

    
    
    
    /**
     * Obtient la version actuelle du serveur.
     *
     * @return La version du serveur.
     */
    private static int getVersion() { return Integer.parseInt(reflection.getpackageNameVersion().split("_")[1]); }

    
    
    /**
     * Une classe d'énumération pour les conteneurs de produits nécessaires.
     */
    public enum Containers {
    	
        GENERIC_9X1(14, "minecraft:chest", "CHEST"),
        GENERIC_9X2(14, "minecraft:chest", "CHEST"),
        GENERIC_9X3(14, "minecraft:chest", "CHEST", "ENDER_CHEST", "BARREL"),
        GENERIC_9X4(14, "minecraft:chest", "CHEST"),
        GENERIC_9X5(14, "minecraft:chest", "CHEST"),
        GENERIC_9X6(14, "minecraft:chest", "CHEST"),
        GENERIC_3X3(14, null, "DISPENSER", "DROPPER"),
        ANVIL(14, "minecraft:anvil", "ANVIL"),
        BEACON(14, "minecraft:beacon", "BEACON"),
        BREWING_STAND(14, "minecraft:brewing_stand", "BREWING"),
        ENCHANTMENT(14, "minecraft:enchanting_table", "ENCHANTING"),
        FURNACE(14, "minecraft:furnace", "FURNACE"),
        HOPPER(14, "minecraft:hopper", "HOPPER"),
        MERCHANT(14, "minecraft:villager", "MERCHANT"),
        
        //Pour une raison inconnue, lors de la mise à jour de la boîte de shulker, la taille de l'inventaire devient un peu plus grande.
        SHULKER_BOX(14, "minecraft:blue_shulker_box", "SHULKER_BOX"),

        //Ajouté en 1.14, il ne fonctionne donc qu'avec les conteneurs.
        BLAST_FURNACE(14, null, "BLAST_FURNACE"),
        CRAFTING(14, null, "WORKBENCH"),
        GRINDSTONE(14, null, "GRINDSTONE"),
        LECTERN(14, null, "LECTERN"),
        LOOM(14, null, "LOOM"),
        SMOKER(14, null, "SMOKER"),
        
        //CARTOGRAPHIE en 1.14, TABLE DE CARTOGRAPHIE en 1.15 & 1.16 (conteneur), handle dans getObject().
        CARTOGRAPHY_TABLE(14, null, "CARTOGRAPHY"),
        STONECUTTER(14, null, "STONECUTTER"),

        //Ajouté en 1.14, fonctionnel depuis 1.16.
        SMITHING(16, null, "SMITHING");

        private final int containerVersion;
        private final String minecraftName;
        private final String[] inventoryTypesNames;

        Containers(int containerVersion, String minecraftName, String... inventoryTypesNames) {
        	
           this.containerVersion = containerVersion;
           this.minecraftName = minecraftName;
           this.inventoryTypesNames = inventoryTypesNames;
        }
        
        

        /**
         * Obtient le conteneur en fonction de l'inventaire ouvert actuel du joueur.
         *
         * @param type Type d'inventaire.
         * @return Le Conteneur.
         */
        public static Containers getType(InventoryType type, int size) {
        	
            if(type == InventoryType.CHEST || type == InventoryType.PLAYER) { return Containers.valueOf("GENERIC_9X" + size / 9); }
            
            for(Containers container : Containers.values()) {
            	
                for(String bukkitName : container.getInventoryTypesNames()) {
                	
                    if(bukkitName.equalsIgnoreCase(type.toString())) { return container; }
                }
            }
            return null;
        }
        
        

        /**
         * Obtient l'objet de l'énumération des conteneurs.
         *
         * @return Un objet "Conteneur" si 1.14, sinon, un String.
         */
        public Object getObject() {

            try {

                if(!useContainers()) { return getMinecraftName(); }

                String name = (getVersion() == 14 && this == CARTOGRAPHY_TABLE) ? "CARTOGRAPHY" : name();
                Field field = CONTAINERS_CLASS.getSuperclass().getDeclaredField(name);

                return field.get(null);

            } catch(NoSuchFieldException e) { return null; }
            catch(IllegalAccessException e) { e.printStackTrace(); return null; }
        }
        
        

        /**
         * Obtient la version dans laquelle le conteneur d'inventaire a été ajouté.
         *
         * @return La version.
         */
        public int getContainerVersion() { return containerVersion; }
        
        

        /**
         * Obtient le nom de l'inventaire de Minecraft pour les anciennes versions.
         *
         * @return Le nom de l'inventaire.
         */
        public String getMinecraftName() { return minecraftName; }
        
        

        /**
         * Obtient les noms des types d'inventaire de l'inventaire.
         *
         * @return Noms de bukkit.
         */
        public String[] getInventoryTypesNames() { return inventoryTypesNames; }
    }
}
