package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple utility class to ensure that a Listener is not registered more than once.
 */
public class EventRegistry {
    private final Plugin plugin;
    private final List<Listener> listeners = new ArrayList<>();

    public EventRegistry(Plugin plugin) { this.plugin = plugin; }

    /**
     * Enregistre un auditeur et renvoie <code>vrai</code> si l'auditeur n'a pas déjà été enregistré.
     *
     * @param listener The {@link Listener} to register.
     * @return L'enregistrement de l'auditeur est réussi ou non.
     */
    public boolean registerListener(Listener listener) {


        if(listeners.contains(listener)) { return false; }

        listeners.add(listener);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        return true;
    }

    /**
     * Désenregistre un auditeur et renvoie <code>true</code> si l'auditeur était déjà enregistré.
     *
     * @param listener The {@link Listener} to register.
     * @return Si l'auditeur a été désenregistré avec succès.
     */
    public boolean unregisterListener(Listener listener) {

        if(!listeners.contains(listener)) { return false; }

        listeners.remove(listener);
        HandlerList.unregisterAll(listener);
        return true;
    }
}
