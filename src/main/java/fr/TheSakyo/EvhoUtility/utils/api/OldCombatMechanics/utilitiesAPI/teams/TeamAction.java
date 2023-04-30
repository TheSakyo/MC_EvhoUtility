package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.teams;

/**
 * Les différentes actions qu'un paquet {@code PacketPlayOutScoreboardTeam} peut représenter.
 */
public enum TeamAction {
    CREATE(0),
    DISBAND(1),
    UPDATE(2),
    ADD_PLAYER(3),
    REMOVE_PLAYER(4);

    private final int minecraftId;

    TeamAction(int minecraftId) { this.minecraftId = minecraftId; }

    public int getMinecraftId() { return minecraftId; }

    public static TeamAction fromId(int id) {

        for(TeamAction rule : values()) {

            if(rule.getMinecraftId() == id) { return rule; }
        }
        return null;
    }
}
