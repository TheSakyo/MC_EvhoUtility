package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.particle;


import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.ImmutablePacket;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.type.ClassType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

class V13ParticlePacket extends ParticlePacket {

    protected V13ParticlePacket(ImmutablePacket packet) { super(packet); }

    @Override
    public String getParticleName() { return PacketAccess.getParticleName(getNmsPacket()); }

    private static class PacketAccess {

        private static final Field PARTICLE_PARAM_FIELD;
        private static Method PARTICLE_PARAM_NAME_METHOD;

        static {

            PARTICLE_PARAM_FIELD = Reflector.getFieldByType(PACKET_CLASS, "ParticleParam");

            Class<?> particleParamClass = Reflector.getClass(ClassType.NMS, "core.particles.ParticleParam");

            for(Method method : particleParamClass.getMethods()) { if(method.getReturnType() == String.class) { PARTICLE_PARAM_NAME_METHOD = method; } }

            Objects.requireNonNull(PARTICLE_PARAM_NAME_METHOD);
        }

        public static String getParticleName(Object nmsPacket) {

            Object particleParam = Reflector.getUnchecked(() -> PARTICLE_PARAM_FIELD.get(nmsPacket));
            return Reflector.invokeMethod(PARTICLE_PARAM_NAME_METHOD, particleParam);
        }
    }
}
