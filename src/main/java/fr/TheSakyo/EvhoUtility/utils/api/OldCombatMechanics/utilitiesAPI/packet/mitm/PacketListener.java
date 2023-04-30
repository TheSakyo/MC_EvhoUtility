package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm;

/**
 * Écoute un paquet
 */
public interface PacketListener {

    /**
     * Appelé lorsqu'un paquet est reçu
     *
     * @param packetEvent {@link PacketEvent}
     */
    void onPacketReceived(PacketEvent packetEvent);

    /**
     * Appelé lorsqu'un paquet est envoyé
     *
     * @param packetEvent {@link PacketEvent}
     */
    void onPacketSend(PacketEvent packetEvent);
}
