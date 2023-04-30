package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.particle;


import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.ImmutablePacket;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;

import java.lang.reflect.Field;

public class PreV13ParticlePacket extends ParticlePacket {

    protected PreV13ParticlePacket(ImmutablePacket packet) {
        super(packet);
    }

    @Override
    public String getParticleName() {
        return PacketAccess.getParticleName(getNmsPacket());
    }

    private static class PacketAccess {

        private static final Field PARTICLE_PARAM_FIELD;

        static { PARTICLE_PARAM_FIELD = Reflector.getFieldByType(PACKET_CLASS, "EnumParticle"); }

        public static String getParticleName(Object nmsPacket) { return Reflector.getUnchecked(() -> PARTICLE_PARAM_FIELD.get(nmsPacket)).toString(); }
    }

}
