package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.teams;

public enum CollisionRule {

    ALWAYS("always"),
    NEVER("never"),
    HIDE_FOR_OTHER_TEAMS("pushOtherTeams"),
    HIDE_FOR_OWN_TEAM("pushOwnTeam");

    private String name;

    CollisionRule(String name) { this.name = name; }

    public String getName() { return name; }
}
