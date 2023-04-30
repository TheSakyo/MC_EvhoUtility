package fr.TheSakyo.EvhoUtility.utils.api.AnvilGui.version;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;



/**
 * Enveloppe les versions pour pouvoir utiliser facilement différentes versions du serveur NMS.
 *
 */
public interface VersionWrapper {

	/**
     * Obtient le prochain identifiant de conteneur NMS disponible pour le joueur.
     *
     * @param player Joueur pour lequel il faut obtenir le prochain identifiant de conteneur.
     * @param container Conteneur pour lequel un nouvel identifiant est généré.
     * @return le prochain identifiant de conteneur NMS disponible.
     */
    int getNextContainerId(Player player, Object container);

    
    /**
     * Ferme l'inventaire en cours pour le joueur
     *
     * @param player Joueur qui a besoin de fermer son inventaire actuel.
     */
    void handleInventoryCloseEvent(Player player);

    
    /**
     * Envoie PacketPlayOutOpenWindow au lecteur avec l'id du conteneur et le titre de la fen�tre.
     *
     * @param player Joueur auquel envoyer le paquet.
     * @param containerId L'Identifiant du conteneur à ouvrir
     * @param inventoryTitle Titre de l'inventaire à ouvrir (fonctionne uniquement à Minecraft 1.14 et plus)
     */
    void sendPacketOpenWindow(Player player, int containerId, String inventoryTitle);

    
    /**
     * Envoie PacketPlayOutCloseWindow au joueur avec l'ID du conteneur
     *
     * @param player Joueur auquel envoyer le paquet.
     * @param containerId L'Identifiant du conteneur à fermer
     */
    void sendPacketCloseWindow(Player player, int containerId);

    
    /**
     * Définit le conteneur actif du lecteur NMS comme celui par défaut.
     *
     * @param player Joueur dont il faut définir le conteneur actif.
     */
    void setActiveContainerDefault(Player player);

    
    /**
     * Définit le conteneur actif du lecteur NMS à celui fourni
     *
     * @param player Joueur dont le conteneur actif doit être défini.
     * @param container Conteneur à définir comme actif
     */
    void setActiveContainer(Player player, Object container);
    

    /**
     * Définit le windowId du conteneur fourni.
     *
     * @param container Conteneur dont il faut définir l'identifiant de fenêtre.
     * @param containerId Nouveau 'windowId'
     */
    void setActiveContainerId(Object container, int containerId);
    

    /**
     * Ajoute un écouteur d'emplacement au conteneur fourni pour le lecteur.
     *
     * @param container Conteneur auquel ajouter le slot listener.
     * @param player Joueur utiliser comme écouteur.
     */
    void addActiveContainerSlotListener(Object container, Player player);
    

    /**
     * Récupère le conteneur {@link Inventory} du DDN fourni.
     *
     * @param container Conteneur du SGEN dont on veut obtenir le {@link Inventory}.
     * @return L'inventaire du conteneur du DDN.
     */
    Inventory toBukkitInventory(Object container);
    

    /**
     * Créer un nouveau ContainerAnvil
     *
     * @param player Joueur dont on veut récupèrer le conteneur
     * @param title Titre de l'inventaire de l'enclume
     * @return L'Instance du conteneur
     */
    Object newContainerAnvil(Player player, String title);

}
