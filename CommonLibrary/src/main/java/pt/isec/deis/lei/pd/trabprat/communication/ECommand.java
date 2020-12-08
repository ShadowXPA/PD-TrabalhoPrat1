package pt.isec.deis.lei.pd.trabprat.communication;

public final class ECommand {

    private ECommand() {
    }

    // Ignore
    public static final int CMD_IGNORE = 1;

    // Alive
    public static final int CMD_ALIVE = 223;

    // Heartbeat
    public static final int CMD_HEARTBEAT = 322;

    // Standard response for successful request
    public static final int CMD_OK = 200;

    // The request has been fulfilled, resulting in the creation of an account/channel/message
    public static final int CMD_CREATED = 201;

    // The request has been accepted for processing, but the processing has not been completed
    public static final int CMD_ACCEPTED = 202;

    // The server successfully processed the request, and is not returning any content
    public static final int CMD_NO_CONTENT = 204;

    // This and all future requests should be directed to the given IP/Port
    public static final int CMD_MOVED_PERMANENTLY = 301;

    // The server cannot or will not process the request due to an apparent client error (if UDP fails, etc.)
    public static final int CMD_BAD_REQUEST = 400;

    // Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet been provided
    public static final int CMD_UNAUTHORIZED = 401;

    // The request contained valid data and was understood by the server, but the server is refusing action
    public static final int CMD_FORBIDDEN = 403;

    // The requested resource could not be found but may be available in the future
    public static final int CMD_NOT_FOUND = 404;

    // The request entity has a media type which the server or resource does not support
    public static final int CMD_UNSUPPORTED_MEDIA_TYPE = 415;

    // The server cannot handle the request (because it is overloaded or down for maintenance). Generally, this is a temporary state
    public static final int CMD_SERVICE_UNAVAILABLE = 503;

    // The server is unable to store the representation needed to complete the request
    public static final int CMD_INSUFFICIENT_STORAGE = 507;

    // The client wants to connect to the server
    public static final int CMD_CONNECT = 600;

    // The client wants to register an account to the server
    public static final int CMD_REGISTER = 601;

    // The client wants to login to an account
    public static final int CMD_LOGIN = 602;

    // The client logged out
    public static final int CMD_LOGOUT = 6021;

    // Updated online users list
    public static final int CMD_ONLINE_USERS = 603;

    // The client wants to upload a file
    public static final int CMD_UPLOAD = 700;

    // The client wants to download a file
    public static final int CMD_DOWNLOAD = 701;

    // The client wants to receive channel messages
    public static final int CMD_GET_CHANNEL_MESSAGES = 702;

    // The client wants to receive DM messages
    public static final int CMD_GET_DM_MESSAGES = 703;

    // The client wants to create a channel
    public static final int CMD_CREATE_CHANNEL = 704;

    // The client wants to update a channel
    public static final int CMD_UPDATE_CHANNEL = 705;

    // The client wants to delete a channel
    public static final int CMD_DELETE_CHANNEL = 706;

    // The client wants to create a message
    public static final int CMD_CREATE_MESSAGE = 707;

    // The client wants a list of users that fully or partially match a certain string
    public static final int CMD_SEARCH_USERS = 708;

    // The server sends an updated list of the channel users
    public static final int CMD_UPDATE_CHANNEL_USERS = 709;

    // The server updates the client's server list
    public static final int CMD_UPDATE_SERVERS = 799;

    // The server is telling other servers that it's closing
    public static final int CMD_BYE = 899;

    // The server is asking if other servers are awake
    public static final int CMD_HELLO = 900;

    // The server is asking the specific server for synchronization
    public static final int CMD_SYNC = 901;

    // The server is sending/recieving synchronization packets
    public static final int CMD_SYNC_DB = 902;
    public static final int CMD_SYNC_F = 903;

    // The server wants to close and has requested the clients to close
    public static final int CMD_SERVER_SHUTDOWN = 999;
}
