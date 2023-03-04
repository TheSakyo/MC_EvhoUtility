package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet;

import org.bukkit.entity.Player;

public interface ImmutablePacket {

    /**
     * Envoie ce paquet aux joueurs donnés.
     *
     * @param players Les Joueurs à qui l'envoyer
     */
    default void send(Player... players) {

        for(Player player : players){ PacketSender.getInstance().sendPacket(this, player); }
    }

    /**
     * Retourne l'objet paquet sous-jacent. L'objet paquet est immuable dans les nouvelles versions. Cette méthode est fournie comme une porte de sortie.
     *
     * @return Le paquet nms sous-jacent
     */
    Object getNmsPacket();

    /**
     * @implNote Cette méthode renvoie : <br/> {@code getNmsPacket().getClass()}
     * @return La classe du paquet NMS
     */
    default Class<?> getPacketClass() {
        return getNmsPacket().getClass();
    }
}
