package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.PacketHelper;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.PacketSender;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import io.netty.channel.*;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Un simple injecteur de paquets, pour modifier les paquets envoyés et reçus
 */
class PacketInjector extends ChannelDuplexHandler {

    private volatile boolean isClosed;

    private Channel channel;

    // Il y a beaucoup plus de lectures que d'écritures, donc les performances devraient être correctes.
    private final List<PacketListener> packetListeners = new CopyOnWriteArrayList<>();

    private final WeakReference<Player> playerWeakReference;

    private boolean detectedNonNMSPacket = false;

    /**
     * Doit être détaché manuellement !
     *
     * @param player Le joueur à attacher dedans
     */
    PacketInjector(Player player) {
        Objects.requireNonNull(player, "player can not be null!");

        playerWeakReference = new WeakReference<>(player);

        try { attach(player); }
        catch(Exception e) { throw new RuntimeException(e); }
    }

    /**
     * S'attache à un joueur
     *
     * @param player Le joueur à rattacher
     */
    private void attach(Player player) throws Exception {

        Object playerConnection = PacketSender.getInstance().getConnection(player);

        if(playerConnection == null) {

            debug("Impossible d'obtenir la connexion pour le joueur(%s) ! Le chargement de 'PacketSender' a-t-il échoué ? (%d)", player.getName(), hashCode());
            return;
        }

        Object manager = Reflector.getDeclaredFieldValueByType(playerConnection, "NetworkManager");

        channel = (Channel) Reflector.getDeclaredFieldValueByType(manager, "Channel");

        // Supprimer l'ancien écouteur, s'il n'a pas été correctement nettoyé.
        if(channel.pipeline().get("ocm_handler") != null) {

            debug("Un vieil auditeur s'est attardé autour");

            ChannelHandler old = channel.pipeline().get("ocm_handler");

            if(old instanceof PacketInjector) {

                debug("vieil auditeur détaché");

                ((PacketInjector)old).detach();
            }

            // remove old
            channel.pipeline().remove("ocm_handler");
        }

        try { channel.pipeline().addBefore("packet_handler", "ocm_handler", this); }
        catch (NoSuchElementException e) { throw new NoSuchElementException("Aucun gestionnaire de base trouvé. Le joueur a-t-il été déconnecté instantanément ?"); }
    }

    /**
     * Supprime ce gestionnaire
     */
    void detach() {

        debug("Détachement de l'injecteur... (%d)", hashCode());

        if(channel == null) {

            debug("Impossible de détacher l'injecteur car il n'a jamais été complètement fixé ! (%d)", hashCode());
            return;
        }
        if(isClosed || !channel.isOpen()) {

            debug("Fermé(%b) ou canal déjà fermé(%b) ! (%d)", isClosed, !channel.isOpen(), hashCode());
            return;
        }

        channel.eventLoop().submit(() -> {

            channel.pipeline().remove(this);

            // Éfface le canal qu'après le dernier accès
            channel = null;
            isClosed = true;

            // Efface les références. Probablement pas nécessaire, mais on n'est pas sûr le canal.
            playerWeakReference.clear();
            packetListeners.clear();

            debug("Injecteur détaché avec succès (%d)", hashCode());
        });
    }

    /**
     * Ajoute un {@link PacketListener}
     *
     * @param packetListener Le {@link PacketListener} a ajouté
     *
     * @throws IllegalStateException Si le canal est déjà fermé
     */
    void addPacketListener(PacketListener packetListener) {

        Objects.requireNonNull(packetListener, "'packetListener' ne peut être nul");

        if(isClosed) { throw new IllegalStateException("Le canal est déjà fermé. L'ajout d'un auditeur n'est pas valide"); }

        // Évite les enregistrements en double
        packetListeners.remove(packetListener);
        packetListeners.add(packetListener);
    }

    /**
     * Supprime un {@link PacketListener}
     *
     * @param packetListener Le {@link PacketListener} a supprimé
     */
    void removePacketListener(PacketListener packetListener) { packetListeners.remove(packetListener); }

    /**
     * Retourne le nombre d'auditeurs
     *
     * @return Le nombre d'auditeurs
     */
    int getListenerAmount() { return packetListeners.size(); }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {

        if(playerWeakReference == null || playerWeakReference.get() == null) {

            debug("'playerWeakReference' ou sa valeur est nulle. Cela ne devrait PAS se produire à ce stade. " + "(write@%d)", hashCode());
            detach();

                            /* ------------------- */

            super.write(channelHandlerContext, packet, channelPromise);
            return;
        }

        if (PacketHelper.isNmsPacket(packet)) {

           // renvoie les paquets non-nms, mais se plaint au moins une fois
            if(!detectedNonNMSPacket) { Messenger.warn("Réception d'un paquet qui n'est pas NMS : %s %s", packet.getClass(), packet); detectedNonNMSPacket = true; }

                            /* ------------------- */

            super.write(channelHandlerContext, packet, channelPromise);
            return;
        }

        PacketEvent event = new PacketEvent(PacketHelper.wrap(packet), PacketEvent.ConnectionDirection.TO_CLIENT, playerWeakReference.get());

        for(PacketListener packetListener : packetListeners) {

            try { if(!isClosed) { packetListener.onPacketSend(event); } }
            catch(Exception e) { Messenger.warn(e, "Erreur dans l'écoute d'un paquet (envoi)."); }
        }

        // Laisser passer
        if(!event.isCancelled()) { super.write(channelHandlerContext, packet, channelPromise); }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {

        if (playerWeakReference == null || playerWeakReference.get() == null) {
            debug("'playerWeakReference' ou sa valeur est nulle. Cela ne devrait PAS se produire à ce stade. " + "(read@%d)", hashCode());
            detach();

                            /* ------------------- */

            super.channelRead(channelHandlerContext, packet);
            return;
        }

        if (PacketHelper.isNmsPacket(packet)) {

            debug("Reçoit un paquet QUI N'EST PAS UN PAQUET :" + packet.getClass() + " " + packet);
            return;
        }

        PacketEvent event = new PacketEvent(PacketHelper.wrap(packet), PacketEvent.ConnectionDirection.TO_SERVER, playerWeakReference.get());

        for(PacketListener packetListener : packetListeners) {

            try {

                if(!isClosed) { packetListener.onPacketReceived(event); }

            } catch(Exception e) { Messenger.warn(e, "Erreur dans l'écoute d'un paquet (réception)."); }
        }

        // Laisser passer
        if(!event.isCancelled()) { super.channelRead(channelHandlerContext, packet); }
    }

    private static void debug(String message, Object... formatArgs) { Messenger.debug("PacketInjector: " + message, formatArgs); }
}
