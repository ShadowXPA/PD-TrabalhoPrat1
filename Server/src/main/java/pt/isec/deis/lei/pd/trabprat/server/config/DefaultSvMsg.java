package pt.isec.deis.lei.pd.trabprat.server.config;

public final class DefaultSvMsg {

    public static final String SV_INTERNAL_ERROR = "The server encountered an internal error.\nPlease try again later!";
    public static final String SV_USER_EXISTS = "Name already exists!";
    public static final String SV_USERNAME_EXISTS = "Username already exists!";
    public static final String SV_USERNAME_NOT_EXISTS = "Username doesn't exist!";
    public static final String SV_USER_USERNAME_EXISTS = "Name or Username already exists!";
    public static final String SV_PASSWORD_DOES_NOT_MATCH = "Passwords do not match!";
    public static final String SV_USER_LOGGED_IN = "User is already logged in!";
    public static final String SV_CREATE_CHANNEL_FAIL = "Could not create channel.\nPlease try again later!";
    public static final String SV_UPDATE_CHANNEL_FAIL = "Could not update channel.\nPlease try again later!";
    public static final String SV_DELETE_CHANNEL_FAIL = "Could not delete channel.\nPlease try again later!";
    public static final String SV_DOWNLOAD_FILE_FAIL = "Could not download file.\nPlease try again later!";
    public static final String SV_DOWNLOAD_FILE_FAIL2 = "Could not download file.\nFile may not exist.\nPlease try again later!";
    public static final String SV_MESSAGE_FAIL = "Could not send message.";

    private DefaultSvMsg() {
    }
}
