package pt.isec.deis.lei.pd.trabprat.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validator {

    // Validade user data (Password, Username, Name, etc.)
    // Create Static functions
    private Validator() {
    }

    public static boolean Name(String Name) {
        String pattern = "^([a-zA-ZáàÁÀãíìÍÌÓÒÚÙîÎóú ]{2,50})$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(Name);
        if(m.find()){
            return Name.equals(m.group(0));
        }else{
            return false;
        }
    }

    public static boolean Username(String Username) {
        String pattern = "^[a-zA-Z0-9]{4,25}$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(Username);
        if(m.find()){
            return Username.equals(m.group(0));
        }else{
            return false;
        }
    }

    public static boolean Password(String Password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{6,255}$"; 
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(Password);
        if(m.find()){
            return Password.equals(m.group(0));
        }else{
            return false;
        }
    }

    public static boolean PasswordEquals(String Password, String ConfirmPassword) {
        return Password.equals(ConfirmPassword);
    }
}
