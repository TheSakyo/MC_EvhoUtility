package fr.TheSakyo.EvhoUtility.utils.custom.enums.AINSI;

/**
 * Énumération {@link AinsiBackgroundColor} permettant facilement de récupéré un code couleur de fond 'AINSI'.
 *
 */
public enum AinsiBackgroundColor {

    // ⬇️ Listes des Codes Couleurs d'arrière-plan AINSI' disponibles ⬇️ //

    BLACK((char)27 + "[40m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    RED((char)27 + "[41m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    GREEN((char)27 + "[42m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    YELLOW((char)27 + "[43m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BLUE((char)27 + "[44m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    MAGENTA((char)27 + "[45m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    CYAN((char)27 + "[46m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    WHITE((char)27 + "[47m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_BLACK((char)27 + "[100m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_RED((char)27 + "[101m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_GREEN((char)27 + "[102m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_YELLOW((char)27 + "[103m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_BLUE((char)27 + "[104m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_MAGENTA((char)27 + "[105m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_CYAN((char)27 + "[106m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    BRIGHT_WHITE((char)27 + "[107m")  {
        @Override
        public String toString() { return super.getColorCode(); }
    },
    RESET((char)27 + "[49m") {
        @Override
        public String toString() { return super.getColorCode(); }
    };

    // ⬆️ Listes des Codes Couleurs d'arrière-plan AINSI' disponibles ⬆️ //

    private final String colorCode; // Permet de récupèrer un code couleur d'arrière-plan 'AINSI' spécial.

    /**
     * Récupère la couleur 'AINSI' en question de {@link AinsiBackgroundColor}.
     *
     * @param colorCode Un Code Couleur d'arrière-plan 'AINSI' qu'il faut interpréter.
     */
    AinsiBackgroundColor(String colorCode) { this.colorCode = colorCode; }

    /**
     * Récupère la couleur d'arrière-plan 'AINSI' en question de {@link AinsiBackgroundColor}.
     *
     * @return Un code couleur d'arrière-plan 'AINSI' spécial.
     */
    public String getColorCode() { return this.colorCode; }
}
