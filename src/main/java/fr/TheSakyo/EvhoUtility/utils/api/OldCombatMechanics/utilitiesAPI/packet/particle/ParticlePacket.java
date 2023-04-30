package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.particle;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.ImmutablePacket;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.PacketHelper;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;

import java.util.Optional;

public abstract class ParticlePacket implements ImmutablePacket {

    protected static final Class<?> PACKET_CLASS = PacketHelper.getPacketClass(PacketHelper.PacketType.PlayOut, "WorldParticles");

    private final Object nmsPacket;

    protected ParticlePacket(ImmutablePacket packet) { this.nmsPacket = packet.getNmsPacket(); }

    @Override
    public Object getNmsPacket() { return nmsPacket; }

    /**
     * @return La particule pour laquelle ce paquet est destiné
     */
    public abstract String getParticleName();

    /**
     * Crée un nouveau {@link ParticlePacket} à partir d'un {@link ImmutablePacket} donné, s'il est du bon type.
     *
     * @param packet Le paquet d'origine
     * @return Un paquet de particules, si le type est correct.
     */
    public static Optional<ParticlePacket> from(ImmutablePacket packet) {

        if(packet.getPacketClass() != PACKET_CLASS) { return Optional.empty(); }

        try {

            if(Reflector.versionIsNewerOrEqualAs(1, 13, 0)) { return Optional.of(new V13ParticlePacket(packet)); }
            return Optional.of(new PreV13ParticlePacket(packet));

        } catch(NoClassDefFoundError ignored) {}

        return Optional.empty();
    }
}
