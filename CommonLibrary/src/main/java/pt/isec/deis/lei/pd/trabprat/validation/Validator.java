package pt.isec.deis.lei.pd.trabprat.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validator {

    // Validade user data (Password, Username, Name, etc.)
    // Create Static functions
    private Validator() {
    }

    public static boolean Name(String Name) {
        String pattern = "[a-zA-ZáàÁÀãí ]+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(Name);
        //m.find();
        return Name.equals(m.group(0));
    }

    public static boolean Username(String Username) {
        String pattern = "[a-zA-Z0-9áàÀÁ]+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(Username);
        //m.find();
        return Username.equals(m.group(0));
    }

    public static boolean PasswordEquals(String Password, String ConfirmPassword) {
        return Password.equals(ConfirmPassword);
    }
}
