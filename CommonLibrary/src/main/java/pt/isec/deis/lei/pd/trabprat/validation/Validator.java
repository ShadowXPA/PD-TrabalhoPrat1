package pt.isec.deis.lei.pd.trabprat.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Validator {

    // Validade user data (Password, Username, Name, etc.)
    // Create Static functions
    private Validator() {
    }

    public static boolean Name(String Name) {
        if(!(Name.length()> 1 && Name.length() < 51)){
            return false;
        }
        String pattern = "([a-zA-ZáàÁÀãíìÍÌÓÒÚÙîÎóú ]+)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(Name);
        if(m.find()){
            return Name.equals(m.group(0));
        }else{
            return false;
        }
    }

    public static boolean Username(String Username) {
        if(!(Username.length()> 3 && Username.length() < 26)){
            return false;
        }
        String pattern = "[a-zA-Z0-9]+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(Username);
        if(m.find()){
            return Username.equals(m.group(0));
        }else{
            return false;
        }
    }

    public static boolean Passowrd(String Password) {
        if(!(Password.length()> 5 && Password.length() < 256)){
            return false;
        }
        String pattern = "[A-Z]+[a-z]+[0-9]+.+"; // mal
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
