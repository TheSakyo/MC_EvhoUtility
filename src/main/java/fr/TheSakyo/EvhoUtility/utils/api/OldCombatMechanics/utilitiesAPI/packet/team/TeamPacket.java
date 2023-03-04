package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.team;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.ImmutablePacket;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.teams.CollisionRule;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.teams.TeamAction;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

public abstract class TeamPacket implements ImmutablePacket {

    private final Object nmsPacket;

    protected TeamPacket(Object nmsPacket){ this.nmsPacket = nmsPacket; }

    @Override
    public Object getNmsPacket() { return nmsPacket; }

    public abstract TeamPacket withCollisionRule(CollisionRule collisionRule);

    public abstract Collection<String> getPlayerNames();

    public abstract TeamPacket withAction(TeamAction action);

    public abstract TeamAction getAction();

    public abstract String getName();

    public Optional<TeamPacket> adjustedTo(TeamPacket incoming, Player target) {

        switch(incoming.getAction()) {

            case REMOVE_PLAYER: {

                if(incoming.getPlayerNames().contains(target.getName())) return Optional.empty();
                return Optional.of(incoming);
            }

            case UPDATE:
            case ADD_PLAYER:
            case CREATE:
                return Optional.of(incoming);

            case DISBAND: return Optional.empty();
        }

        throw new IllegalArgumentException("Action 'Team' inconnu");
    }

    public static TeamPacket create(TeamAction action, CollisionRule collisionRule, String name, Collection<Player> players) {

        if(Reflector.versionIsNewerOrEqualAs(1, 17, 0)) { return V17TeamPacket.create(action, collisionRule, name, players); }
        return PreV17TeamPacket.create(action, collisionRule, name, players);
    }

    public static TeamPacket from(Object nmsPacket) {

        if(Reflector.versionIsNewerOrEqualAs(1, 17, 0)){ return new V17TeamPacket(nmsPacket); }
        return new PreV17TeamPacket(nmsPacket);
    }
}
