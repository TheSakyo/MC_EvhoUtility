package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.type;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;

public enum ClassType {

    NMS {

        @Override
        public String qualifyClassName(String partialName) {

            if(Reflector.versionIsNewerOrEqualAs(1, 17, 0)) { return "net.minecraft." + partialName; }

            // FIXME: Suppose que les noms de classe sont en majuscules et supprime le nom du paquet précédent.
            // entity.foo.Bar.Test
            // ^^^^^^^^^^ ^^^^^^^^^
            //   Group 1   Group 2
            String className = partialName.replaceAll("([a-z.]+)\\.([a-zA-Z.]+)", "$2");

            return "net.minecraft.server." + Reflector.getVersion() + "." + className;
        }
    },

    CRAFTBUKKIT {

        @Override
        public String qualifyClassName(String partialName) {

            return String.format("%s.%s.%s", "org.bukkit.craftbukkit", Reflector.getVersion(), partialName);
        }
    };

    public abstract String qualifyClassName(String partialName);
}
