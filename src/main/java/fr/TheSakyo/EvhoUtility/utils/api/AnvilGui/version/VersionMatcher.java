package fr.TheSakyo.EvhoUtility.utils.api.AnvilGui.version;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.List;



/**
 * Fait correspondre la version du NMS du serveur à son {@link VersionWrapper}.
 *
 */
public class VersionMatcher {

	/**
     * La version du serveur
     */
    private final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
    
    
    
    /**
     * Tous les {@link VersionWrapper}s disponibles
     */
    private final List<Class<? extends VersionWrapper>> version = List.of(Wrapper1_20_R1.class);

    
    
    /**
     * Fait correspondre la version du serveur à son {@link VersionWrapper}.
     *
     * @return Le {@link VersionWrapper} de ce serveur.
     * @throws RuntimeException si AnvilGUI ne prend pas en charge cette version du serveur.
     */
	public VersionWrapper match() {
    	
        try {
        	
            return version.stream()
                .filter(version -> version.getSimpleName().substring(7).equals(serverVersion))
                .findFirst().orElseThrow(() -> new RuntimeException("La version de votre serveur n'est pas supportée par AnvilUtils !"))
                .getDeclaredConstructor().newInstance();
            
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) { throw new RuntimeException(ex); }
    }

}
