package pt.isec.deis.lei.pd.trabprat.validation;

public final class Validator {

    // Validade user data (Password, Username, Name, etc.)
    // Create Static functions
    private Validator() {
    }

    public static boolean Name() {

        return false;
    }

    public static boolean Username() {

        return false;
    }

    public static boolean PasswordEquals(String Password, String ConfirmPassword) {
        return Password.equals(ConfirmPassword);
    }
}
