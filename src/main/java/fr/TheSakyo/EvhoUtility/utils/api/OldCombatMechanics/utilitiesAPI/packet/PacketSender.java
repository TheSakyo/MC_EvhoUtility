package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.type.ClassType;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A Packet sender
 */
public class PacketSender {
    private static final PacketSender instance = new PacketSender();

    private static final Method GET_HANDLE;
    private static final Method SEND_PACKET;
    private static final Field PLAYER_CONNECTION_FIELD;

    static {

        Class<?> craftPlayer = Reflector.getClass(ClassType.CRAFTBUKKIT, "entity.CraftPlayer");
        Class<?> playerConnection = Reflector.getClass(ClassType.NMS, "server.network.PlayerConnection");
        Class<?> entityPlayer = Reflector.getClass(ClassType.NMS, "server.level.EntityPlayer");

        GET_HANDLE = Reflector.getMethod(craftPlayer, "getHandle");
        SEND_PACKET = Reflector.versionIsNewerOrEqualAs(1, 18, 0)
                ? Reflector.getMethod(playerConnection, "a", "Packet")
                : Reflector.getMethod(playerConnection, "sendPacket");
        PLAYER_CONNECTION_FIELD = Reflector.getFieldByType(entityPlayer, "PlayerConnection");

    }

    private PacketSender() {}

    /**
     * @return une instance de {@link PacketSender}
     */
    public static PacketSender getInstance() {
        return instance;
    }

    /**
     * Envoie un paquet à un joueur
     *
     * @param packet {@link ImmutablePacket} à envoyer
     * @param player Le joueur à qui l'envoyer
     */
    public void sendPacket(ImmutablePacket packet, Player player) {
        Reflector.invokeMethod(SEND_PACKET, getConnection(player), packet.getNmsPacket());
    }

    /**
     * Retourne la connexion du Joueur
     *
     * @param player Le joueur pour lequel il faut obtenir la connexion
     * @return La Connexion du Joueur
     */
    public Object getConnection(Player player) {
        Object handle = Reflector.invokeMethod(GET_HANDLE, player);

        return Reflector.getFieldValue(PLAYER_CONNECTION_FIELD, handle);
    }
}
