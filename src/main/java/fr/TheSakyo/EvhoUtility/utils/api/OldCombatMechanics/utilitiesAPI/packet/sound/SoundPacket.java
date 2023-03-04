package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.sound;


import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.ImmutablePacket;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.PacketHelper;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.type.ClassType;

import java.lang.reflect.Field;
import java.util.Optional;

public class SoundPacket implements ImmutablePacket {
    private static final Class<?> PACKET_CLASS = PacketHelper.getPacketClass(PacketHelper.PacketType.PlayOut, "NamedSoundEffect");

    private final Object nmsPacket;

    private SoundPacket(ImmutablePacket packet) { this.nmsPacket = packet.getNmsPacket(); }

    public String getSoundName() { return PacketAccess.getSoundName(getNmsPacket()); }

    @Override
    public Object getNmsPacket() { return nmsPacket; }

    /**
     * Convertit {@link ImmutablePacket} donné en un {@link SoundPacket}, le cas échéant.
     *
     * @param packet Le paquet à convertir
     * @return Le paquet de sons ou vide s'il n'est pas du bon type.
     */
    public static Optional<SoundPacket> from(ImmutablePacket packet) {

        if(packet.getPacketClass() == PACKET_CLASS){ return Optional.of(new SoundPacket(packet)); }
        return Optional.empty();
    }

    private static class PacketAccess {
        private static final Class<?> SOUND_EFFECT_CLASS = Reflector.getClass(ClassType.NMS, "sounds.SoundEffect");

        private static final Field SOUND_EFFECT_FIELD;
        private static final Field MINECRAFT_KEY_FIELD;

        static {

            SOUND_EFFECT_FIELD = Reflector.getFieldByType(PACKET_CLASS, "SoundEffect");
            MINECRAFT_KEY_FIELD = Reflector.getFieldByType(SOUND_EFFECT_CLASS, "MinecraftKey");
        }

        public static String getSoundName(Object packet) {

            Object soundEffect = Reflector.getUnchecked(() -> SOUND_EFFECT_FIELD.get(packet));
            Object minecraftLey = Reflector.getUnchecked(() -> MINECRAFT_KEY_FIELD.get(soundEffect));

            return minecraftLey.toString();
        }
    }
}
