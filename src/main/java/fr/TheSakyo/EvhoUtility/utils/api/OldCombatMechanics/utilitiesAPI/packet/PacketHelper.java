package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.type.ClassType;

public class PacketHelper {
    private static final Class<?> NMS_PACKET_CLASS = Reflector.getClass(ClassType.NMS, "network.protocol.Packet");

    public static Class<?> getPacketClass(PacketType type, String name) {

        return Reflector.getClass(ClassType.NMS, "network.protocol.game.Packet" + type.prefix + name);
    }

    /**
     * Vérifie si un objet donné est un paquet NMS.
     *
     * @param object L'Objet à vérifier
     * @return Vrai s'il s'agit d'un paquet HMS
     */
    public static boolean isNmsPacket(Object object) {

        if(object == null) return false;
        return Reflector.inheritsFrom(object.getClass(), NMS_PACKET_CLASS);
    }

    /**
     * Enveloppe un paquet nms dans un paquet {@link ImmutablePacket} trivial.
     *
     * @param nmsPacket Le paquet nms à emballer
     * @return L'Emballage {@link ImmutablePacket}
     */
    public static ImmutablePacket wrap(Object nmsPacket) { return () -> nmsPacket; }

    /**
     * The type of a packet (in / out).
     */
    public enum PacketType {

        PlayOut("PlayOut"), PlayIn("PlayIn");

        public String prefix;

        PacketType(String prefix){
            this.prefix = prefix;
        }
    }
}
