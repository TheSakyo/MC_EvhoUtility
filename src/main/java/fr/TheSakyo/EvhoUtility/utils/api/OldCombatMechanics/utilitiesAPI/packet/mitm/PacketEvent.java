package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.ImmutablePacket;
import org.bukkit.entity.Player;

/**
 * Un événement de paquet
 */
public class PacketEvent {

    private ImmutablePacket packet;
    private final Player player;
    private boolean cancelled;
    private final ConnectionDirection direction;

    /**
     * @param packet    Le paquet
     * @param direction La direction dans laquelle le paquet se déplace
     * @param player    Le joueur impliqué
     */
    protected PacketEvent(ImmutablePacket packet, ConnectionDirection direction, Player player) {

        this.packet = packet;
        this.direction = direction;
        this.player = player;

        this.cancelled = false;
    }

    /**
     * Retourne le paquet
     *
     * @return Le Paquet
     */
    public ImmutablePacket getPacket() { return packet; }

    /**
     * Définit le nouveau paquet
     *
     * @param packet Le nouveau paquet
     */
    public void setPacket(ImmutablePacket packet) { this.packet = packet; }

    /**
     * Vérifie si l'événement est annulé
     *
     * @return Vrai si l'événement est annulé.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isCancelled() { return cancelled; }

    /**
     * Définit le statut des événements annulés.
     *
     * @param cancelled si c'est vrai, l'événement sera annulé.
     */
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    /**
     * Retourne le sens de la connexion
     *
     * @return La direction dans laquelle le paquet s'est déplacé
     */
    @SuppressWarnings("unused")
    public ConnectionDirection getDirection() { return direction; }

    /**
     * Retourne le Joueur concerné
     *
     * @return Le Joueur impliqué.
     */
    @SuppressWarnings("unused")
    public Player getPlayer() { return player; }

    /**
     * La direction dans laquelle le paquet s'est déplacé
     */
    public enum ConnectionDirection {
        TO_CLIENT,
        TO_SERVER
    }
}
