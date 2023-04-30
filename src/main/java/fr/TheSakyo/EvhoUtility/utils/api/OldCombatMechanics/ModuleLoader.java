package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module.Module;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.EventRegistry;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;

import java.util.ArrayList;
import java.util.List;

public class ModuleLoader {

    private static EventRegistry eventRegistry;
    private static final List<Module> modules = new ArrayList<>();

    public static void initialise(UtilityMain plugin) { ModuleLoader.eventRegistry = new EventRegistry(plugin); }

    public static void toggleModules() { modules.forEach(module -> setState(module, module.isEnabled())); }

    private static void setState(Module module, boolean state) {

        if(state) {

            if(eventRegistry.registerListener(module)) { Messenger.debug(module.getClass().getSimpleName() + " Activé"); }

        } else {

            if(eventRegistry.unregisterListener(module)) { Messenger.debug(module.getClass().getSimpleName() + " Désactivé"); }
        }
    }

    public static void addModule(Module module) { modules.add(module); }

    public static List<Module> getModules() { return modules; }
}
