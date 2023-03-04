package fr.TheSakyo.EvhoUtility.utils.custom.enums.AINSI;


/**
 * Énumération {@link AinsiColor} permettant facilement de récupéré un code couleur primaire 'AINSI'.
 *
 */
public enum AinsiColor {

    // ⬇️ Listes des Codes Couleurs AINSI' disponibles ⬇️ //

    BLACK((char)27 + "[30m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    RED((char)27 + "[31m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    GREEN((char)27 + "[32m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    YELLOW((char)27 + "[33m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BLUE((char)27 + "[34m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    MAGENTA((char)27 + "[35m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    CYAN((char)27 + "[36m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    WHITE((char)27 + "[37m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_BLACK((char)27 + "[90m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_RED((char)27 + "[91m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_GREEN((char)27 + "[92m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_YELLOW((char)27 + "[93m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_BLUE((char)27 + "[94m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_MAGENTA((char)27 + "[95m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_CYAN((char)27 + "[96m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_WHITE((char)27 + "[97m") {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    RESET((char)27 + "[39m") {
        @Override
        public String toString() { return super.getColorCode(); }
    };

    // ⬆️ Listes des Codes Couleurs AINSI' disponibles ⬆️ //

    private final String colorCode; // Permet de récupèrer un code couleur 'AINSI' spécial.

    /**
     * Récupère la couleur 'AINSI' en question de {@link AinsiColor}.
     *
     * @param colorCode Un Code Couleur 'AINSI' a interprété
     *
     * @return Une instance d'un code couleur 'AINSI' spécial.
     */
    AinsiColor(String colorCode) { this.colorCode = colorCode; }

    /**
     * Récupère la couleur 'AINSI' en question de {@link AinsiColor}.
     *
     * @return Un code couleur 'AINSI' spécial.
     */
    public String getColorCode() { return this.colorCode; }
}
