package fr.TheSakyo.EvhoUtility.utils.api.AnvilGui;

import dependancies.org.jsoup.helper.Validate;
import fr.TheSakyo.EvhoUtility.utils.api.AnvilGui.version.VersionMatcher;
import fr.TheSakyo.EvhoUtility.utils.api.AnvilGui.version.VersionWrapper;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.function.BiFunction;
import java.util.function.Consumer;


/**
 * Un gui d'enclume, utilisé pour recueillir les données d'un utilisateur.
 *
 */
public class AnvilUtils {

	/**
     * L'objet {@link VersionWrapper} local pour la version du serveur.
     */
    private static final VersionWrapper WRAPPER = new VersionMatcher().match();
    

    /**
     * Le {@link Plugin} auquel est associée cette interface graphique d'enclume.
     */
    private final Plugin plugin;
    
    
    /**
     * Le joueur qui a l'interface graphique ouverte
     */
    private final Player player;
    
    
    /**
     * Le titre de l'inventaire de l'enclume
     */
    private final String inventoryTitle;
    
    
    /**
     * L'Item qui se trouve dans l'emplacement {@link Slot#INPUT_LEFT}.
     */
    private ItemStack inputLeft;
    
    
    /**
     * L'Item qui se trouve dans l'emplacement {@link Slot#INPUT_RIGHT}.
     */
    private final ItemStack inputRight;
    
    
    /**
     * Un état qui décide où l'interface graphique de l'enclume peut être fermée par l'utilisateur.
     */
    private final boolean preventClose;
    
    
    /**
     * Un {@link Consumer} qui est appelé lorsque l'interface graphique de l'enclume est fermée.
     */
    private final Consumer<Player> closeListener;
    
    
    /**
     * Une {@link BiFunction} qui est appelée lorsque l'emplacement {@link Slot#OUTPUT} a été cliqué.
     */
    private final BiFunction<Player, String, Response> completeFunction;
    
    

    /**
     * Un {@link Consumer} qui est appelé lorsque l'emplacement {@link Slot#INPUT_LEFT} a été cliqué.
     */
    private final Consumer<Player> inputLeftClickListener;
    
    
    /**
     * Un {@link Consumer} qui est appelé lorsque l'emplacement {@link Slot#INPUT_RIGHT} a été cliqué.
     */
    private final Consumer<Player> inputRightClickListener;
    
    

    /**
     * L'identifiant du conteneur de l'inventaire, utilisé pour les méthodes du NMS.
     */
    private int containerId;
    
    
    /**
     * L'inventaire qui est utilisé du côté de Bukkit.
     */
    private Inventory inventory;
    
    
    /**
     * La classe du titulaire de l'écouteur
     */
    private final ListenUp listener = new ListenUp();
    
    

    /**
     * Représente l'état d'ouverture de l'inventaire
     */
    private boolean open;

    
    /**
     * Créer une enclume customisée et l'ouvre pour le joueur.
     *
     * @param plugin Une instance de {@link org.bukkit.plugin.java.JavaPlugin}.
     * @param player Le {@link Player} pour lequel l'inventaire doit être ouvert.
     * @param inventoryTitle Ce à quoi le texte doit déjà correspondre.
     * @param itemText Le nom de l'objet dans le premier emplacement de l'enclumeGui
     * @param inputLeft Matériau de l'objet dans le premier emplacement de l'enclumeGUI
     * @param preventClose Indique si l'on veut impeacher la fermeture de l'inventaire.
     * @param closeListener Un {@link Consumer} lorsque l'inventaire se ferme.
     * @param completeFunction Une {@link BiFunction} qui est appelée lorsque le joueur clique sur l'emplacement {@link Slot#OUTPUT}.
     */
    private AnvilUtils(
            Plugin plugin,
            Player player,
            String inventoryTitle,
            String itemText,
            ItemStack inputLeft,
            ItemStack inputRight,
            boolean preventClose,
            Consumer<Player> closeListener,
            Consumer<Player> inputLeftClickListener,
            Consumer<Player> inputRightClickListener,
            BiFunction<Player, String, Response> completeFunction
    ) {
        this.plugin = plugin;
        this.player = player;
        this.inventoryTitle = inventoryTitle;
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
        this.preventClose = preventClose;
        this.closeListener = closeListener;
        this.inputLeftClickListener = inputLeftClickListener;
        this.inputRightClickListener = inputRightClickListener;
        this.completeFunction = completeFunction;

        if (itemText != null) {
        	
            if(inputLeft == null) { this.inputLeft = new ItemStack(Material.PAPER); }

            ItemMeta paperMeta = this.inputLeft.getItemMeta();
            paperMeta.displayName(CustomMethod.StringToComponent(itemText));
            this.inputLeft.setItemMeta(paperMeta);
        }

        openInventory();
    }
    
    

    /**
     * Ouvre l'interface graphique de l'enclume
     */
    private void openInventory() {
    	
        WRAPPER.handleInventoryCloseEvent(player);
        WRAPPER.setActiveContainerDefault(player);

        Bukkit.getPluginManager().registerEvents(listener, plugin);

        final Object container = WRAPPER.newContainerAnvil(player, inventoryTitle);

        inventory = WRAPPER.toBukkitInventory(container);
        inventory.setItem(Slot.INPUT_LEFT, this.inputLeft);
        
        if(this.inputRight != null) { inventory.setItem(Slot.INPUT_RIGHT, this.inputRight); }

        containerId = WRAPPER.getNextContainerId(player, container);
        WRAPPER.sendPacketOpenWindow(player, containerId, inventoryTitle);
        WRAPPER.setActiveContainer(player, container);
        WRAPPER.setActiveContainerId(container, containerId);
        WRAPPER.addActiveContainerSlotListener(container, player);
        open = true;
    }
    
    

    /**
     * Ferme l'inventaire s'il est ouvert.
     */
    public void closeInventory() { closeInventory(true); }
    
    

    /**
     * Ferme l'inventaire s'il est ouvert, en envoyant les paquets d'inventaire de fermeture uniquement si l'argument est vrai.
     * @param sendClosePacket Indique s'il faut envoyer l'évènement de fermeture de l'inventaire, le paquet, etc.
     */
    private void closeInventory(boolean sendClosePacket) {
    	
        if(!open) { return; }

        open = false;

        if(sendClosePacket) {
        	
            WRAPPER.handleInventoryCloseEvent(player);
            WRAPPER.setActiveContainerDefault(player);
            WRAPPER.sendPacketCloseWindow(player, containerId);
        }

        HandlerList.unregisterAll(listener);

        if(closeListener != null) { closeListener.accept(player); }
    }
    
    

    /**
     * Retourne l'inventaire Bukkit pour ce gui d'enclume.
     *
     * @return L'{@link Inventory} pour ce gui d'enclume
     */
    public Inventory getInventory() { return inventory; }

    
    
    /**
     * Contient simplement les écouteurs pour l'interface graphique.
     */
    private class ListenUp implements Listener {


        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
        	
            if(event.getInventory().equals(inventory) && (event.getRawSlot() < 3 || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))) {
                
            	event.setCancelled(true);
            	
                final Player clicker = (Player) event.getWhoClicked();
                
                if(event.getRawSlot() == Slot.OUTPUT) {
                	
                    final ItemStack clicked = inventory.getItem(Slot.OUTPUT);
                    if(clicked == null || clicked.getType() == Material.AIR) return;
                    
                    AnvilUtils.Response.getItem(clicked);

                    final Response response = completeFunction.apply(clicker, clicked.hasItemMeta() ? CustomMethod.ComponentToString(clicked.getItemMeta().displayName()) : "");
                    
                    if(response.getText() != null) {
                    	
                        final ItemMeta meta = clicked.getItemMeta();
                        meta.displayName(CustomMethod.StringToComponent(response.getText()));
                        clicked.setItemMeta(meta);
                        inventory.setItem(Slot.INPUT_LEFT, clicked);
                        
                    } else if(response.getInventoryToOpen() != null) { clicker.openInventory(response.getInventoryToOpen()); } 
                    
                    else { closeInventory(); }
                    
                } else if(event.getRawSlot() == Slot.INPUT_LEFT) {
                	
                    if(inputLeftClickListener != null) { inputLeftClickListener.accept(player); }
                    
                } else if (event.getRawSlot() == Slot.INPUT_RIGHT) {
                	
                    if(inputRightClickListener != null) { inputRightClickListener.accept(player); }
                }
            }
        }
        

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent event) {
        	
            if(event.getInventory().equals(inventory)) {
            	
                for(int slot : Slot.values()) {
                	
                    if(event.getRawSlots().contains(slot)) {
                    	
                       event.setCancelled(true);
                       break;
                    }
                }
            }
        }

        
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
        	
            if(open && event.getInventory().equals(inventory)) {
            	
                closeInventory(false);
                
                if(preventClose) { Bukkit.getScheduler().runTask(plugin, AnvilUtils.this::openInventory); }
            }
        }

    }
    

    /**
     * Une classe de construction pour un objet {@link AnvilUtils}.
     */
    public static class Builder {

    	/**
         * Un {@link Consumer} qui est appelé lorsque l'interface graphique de l'enclume est fermée.
         */
        private Consumer<Player> closeListener;
        
        
        /**
         * Un état qui décide où l'interface graphique de l'enclume peut être fermée par l'utilisateur.
         */
        private boolean preventClose = false;
        
        
        /**
         * Un {@link Consumer} qui est appelé lorsque l'emplacement {@link Slot#INPUT_LEFT} a été cliqué.
         */
        private Consumer<Player> inputLeftClickListener;
        
        
        /**
         * Un {@link Consumer} qui est appelé lorsque l'emplacement {@link Slot#INPUT_RIGHT} a été cliqué.
         */
        private Consumer<Player> inputRightClickListener;
        
        
        /**
         * Une {@link BiFunction} qui est appelée lorsque le slot de sortie de l'enclume a été cliqué.
         */
        private BiFunction<Player, String, Response> completeFunction;
        
        
        /**
         * Le {@link Plugin} auquel est associée cette interface graphique d'enclume.
         */
        private Plugin plugin;
        
        
        /**
         * Le texte qui sera affiché à l'utilisateur.
         */
        private String title = "Réparation et nom";
        
        
        /**
         * Le texte de départ de l'item
         */
        private String itemText;
        
        
        /**
         * Un {@link ItemStack} à placer dans l'emplacement d'entrée gauche.
         */
        private ItemStack itemLeft;
        
        
        /**
         * Un {@link ItemStack} à placer dans le bon emplacement d'entrée droit.
         */
        private ItemStack itemRight;
        
        

        /**
         * Empêche la fermeture de l'interface graphique de l'enclume par l'utilisateur.
         *
         * @return L'Instance de {@link Builder}
         */
        public Builder preventClose() {
        	
          preventClose = true;
          return this;
        }

        
        /**
         * Écoute quand l'inventaire est fermé
         *
         * @param closeListener Un {@link Consumer} qui est appelé lorsque l'interface graphique de l'enclume est fermée
         * @return L'Instance de {@link Builder}
         * @throws IllegalArgumentException lorsque le closeListener est nul.
         */
        public Builder onClose(Consumer<Player> closeListener) {
        	
           Validate.notNull(closeListener, "closeListener ne peut pas être nul");
           this.closeListener = closeListener;
           return this;
        }

        
        /**
         * Écoute lorsque le premier emplacement d'entrée est cliqué.
         *
         * @param inputLeftClickListener Un {@link Consumer} qui est appelé lorsque le premier emplacement d'entrée est cliqué.
         * @return L'Instance de {@link Builder}
         */
        public Builder onLeftInputClick(Consumer<Player> inputLeftClickListener) {
        	
           this.inputLeftClickListener = inputLeftClickListener;
           return this;
        }

        
        /**
         * Écoute lorsque le deuxième emplacement d'entrée est cliqué.
         *
         * @param inputRightClickListener Un {@link Consumer} qui est appelé lorsque le deuxième emplacement d'entrée est cliqué.
         * @return L'Instance de {@link Builder}
         */
        public Builder onRightInputClick(Consumer<Player> inputRightClickListener) {
        	
           this.inputRightClickListener = inputRightClickListener;
           return this;
        }
        
        

        /**
         * Gère l'emplacement de sortie de l'inventaire lorsqu'il est cliqué.
         *
         * @param completeFunction Une {@link BiFunction} qui est appelée lorsque l'utilisateur clique sur l'emplacement de sortie.
         * @return L'Instance de {@link Builder}
         * @throws IllegalArgumentException lorsque la completeFunction n'est nulle.
         */
        public Builder onComplete(BiFunction<Player, String, Response> completeFunction) {
        	
           Validate.notNull(completeFunction, "La fonction complète ne peut pas être nulle");
           this.completeFunction = completeFunction;
           return this;
        }

        
        
        /**
         * Définit le plugin pour les {@link AnvilUtils}
         *
         * @param plugin Le {@link Plugin} auquel est associé ce GUI d'enclume
         * @return L'Instance de {@link Builder}
         * @throws IllegalArgumentException si le plugin est nul
         */
        public Builder plugin(Plugin plugin) {
        	
           Validate.notNull(plugin, "Le plugin ne peut pas être nul");
           this.plugin = plugin;
           return this;
        }
        
        

        /**
         * Définit le texte initial de l'élément qui est affiché à l'utilisateur.
         *
         * @param text Le nom initial de l'élément dans l'enclume
         * @return L'Instance de {@link Builder}
         * @throws IllegalArgumentException si le texte est nul
         */
        public Builder text(String text) {
        	
           Validate.notNull(text, "Le texte ne peut pas être nul");
           this.itemText = text;
           return this;
        }

        
        
        /**
         * Définit le titre de l'AnvilGUI qui doit être affiché à l'utilisateur.
         *
         * @param title Le titre qui doit être affiché à l'utilisateur.
         * @return L'Instance de {@link Builder}
         * @throws IllegalArgumentException si le titre est nul.
         */
        public Builder title(String title) {
        	
           Validate.notNull(title, "Le titre ne peut pas être nul");
           this.title = title;
           return this;
        }

        
 
        /**
         * Définit le {@link ItemStack} à placer dans le premier emplacement.
         *
         * @param item Le {@link ItemStack} à placer dans le premier emplacement.
         * @return L'Instance de {@link Builder}
         * @throws IllegalArgumentException si le {@link ItemStack} est nul.
         */
        public Builder itemLeft(ItemStack item) {
        	
           Validate.notNull(item, "L'Item ne peut pas être nul");
           this.itemLeft = item;
           return this;
        }

        
        
        /**
         * Définit la {@link ItemStack} à placer dans le deuxième emplacement.
         *
         * @param item Le {@link ItemStack} à placer dans le second emplacement.
         * @return L'instance de {@link Builder}.
         */
        public Builder itemRight(ItemStack item) {
        	
           this.itemRight = item;
           return this;
        }
        
        

        /**
         * Crée l'interface graphique de l'enclume et l'ouvre pour le joueur.
         *
         * @param player Le {@link Player} pour lequel le GUI de l'enclume doit s'ouvrir.
         * @throws IllegalArgumentException lorsque la fonction onComplete, le plugin ou le joueur est nul.
         */
        public void open(Player player) {
        	
            Validate.notNull(plugin, "Le plugin ne peut pas être nul");
            Validate.notNull(completeFunction, "La fonction complète ne peut pas être nulle");
            Validate.notNull(player, "Le joueur ne peut pas être nul");
            new AnvilUtils(plugin, player, title, itemText, itemLeft, itemRight, preventClose, closeListener, inputLeftClickListener, inputRightClickListener, completeFunction);
        }
    }
    
    

    /**
     * Représente une réponse lorsque le joueur clique sur l'item de sortie dans l'interface graphique de l'enclume.
     */
    public static class Response {

    	/**
         * Le texte qui doit être affiché à l'utilisateur.
         */
        private final String text;
        private final Inventory openInventory;
        
        
        /**
         * L'Item que doit recevoir l'utilisateur.
         */
        private static ItemStack itemstackoutput;

        
        
        /**
         * Crée une réponse à l'entrée de l'utilisateur
         *
         * @param text Le texte qui doit être affiché à l'utilisateur, qui peut être null pour fermer l'inventaire.
         */
        private Response(String text, Inventory openInventory, ItemStack itemstackoutput) {
        	
           this.text = text;
           this.openInventory = openInventory;
           AnvilUtils.Response.itemstackoutput = itemstackoutput;
        }

        
        
        /**
         * Obtient le texte qui doit être affiché à l'utilisateur.
         *
         * @return Le texte qui doit être affiché à l'utilisateur.
         */
        public String getText() {
            return text;
        }
        
        
        /**
         * Obtient l'item que doit recevoir l'utilisateur.
         *
         * @return L'Item que doit recevoir l'utilisateur.
         */
        public static ItemStack getItemOutput() {
            return itemstackoutput;
        }
        

        /**
         * Obtient l'inventaire qui doit être ouvert
         *
         * @return L'inventaire qui devrait être ouvert.
         */
        public Inventory getInventoryToOpen() { return openInventory; }
        
        

        /**
         * Retourne un objet {@link Response} pour la fermeture de l'interface graphique de l'enclume.
         *
         @return un objet {@link Response} pour la fermeture de l'interface graphique de l'enclume. 
         */
        public static Response close() { return new Response(null, null, null); }
        
        

        /**
         * Retourne un objet {@link Response} lorsque l'interface graphique de l'enclume doit afficher du texte à l'utilisateur.
         *
         * @param text Le texte qui doit être affiché à l'utilisateur.
         * @return Un objet {@link Response} lorsque l'interface graphique de l'enclume doit afficher du texte à l'utilisateur.
         */
        public static Response text(String text) { return new Response(text, null, null); }
        
        

        /**
         * Retourne un objet {@link Response} lorsque l'interface graphique de l'enclume doit ouvrir l'inventaire fourni.
         *
         * @param inventory L'inventaire à ouvrir
         * @return Un objet {@link Response} lorsque l'interface graphique de l'enclume doit ouvrir l'inventaire fourni.
         */
        public static Response openInventory(Inventory inventory) { return new Response(null, inventory, null); }



        /**
         * Retourne un objet {@link Response} lorsque l'interface graphique de l'enclumedoit récupérer un item fourni.
         *
         * @param itemstackoutput Item que doit recevoir l'utilisateur.
         * @return Un objet {@link Response} lorsque l'interface graphique de l'enclume doit récupérer un item fourni.
         */
        @SuppressWarnings("all")
        private static Response getItem(ItemStack item) { return new Response(null, null, item); }

    }
    
    

    /**
     * Classe enveloppant les constantes magiques des numéros de slot dans une interface graphique d'enclume.
     */
    public static class Slot {

        private static final int[] values = new int[]{Slot.INPUT_LEFT, Slot.INPUT_RIGHT, Slot.OUTPUT};

        
        /**
         * L'emplacement à l'extrême gauche, où la première entrée est insérée. Une {@link ItemStack} est toujours insérée
         * ici pour être renommé
         */
        public static final int INPUT_LEFT = 0;
        
        
        /**
         * Non utilisé, mais dans une vraie enclume, vous pouvez mettre le deuxième élément que vous voulez combiner ici.
         */
        public static final int INPUT_RIGHT = 1;
        
        
        /**
         * L'emplacement de sortie, où un élément est placé lorsque deux éléments sont combinés à partir de {@link #INPUT_LEFT} et de
         * {@link #INPUT_RIGHT} ou {@link #INPUT_LEFT} est renommé
         */
        public static final int OUTPUT = 2;
        
        

        /**
         * Obtient toutes les valeurs de l'enclume
         *
         * @return Le tableau contenant tous les emplacements d'enclume possibles.
         */
        public static int[] values() { return values; }
    }

}
