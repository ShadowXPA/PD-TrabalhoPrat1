package pt.isec.deis.lei.pd.trabprat.client.thread.tcp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import pt.isec.deis.lei.pd.trabprat.client.App;
import pt.isec.deis.lei.pd.trabprat.client.controller.ServerController;
import pt.isec.deis.lei.pd.trabprat.client.dialog.ClientDialog;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.FileChunk;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.LoginPackage;
import pt.isec.deis.lei.pd.trabprat.model.Server;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TChannelMessage;
import pt.isec.deis.lei.pd.trabprat.model.TChannelUser;
import pt.isec.deis.lei.pd.trabprat.model.TDirectMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;

public class TCPHandler implements Runnable {

    private final Socket socket; //Socket para o TCP
    private final ObjectOutputStream oOS; //ObjectOutputStream para o TCP
    private final ObjectInputStream oIS;  //ObjectInputStream para o TCP
    private final Command command;  //Comando proveniente do server

    public TCPHandler(Socket socket, ObjectOutputStream oOS, ObjectInputStream oIS, Command command) throws IOException {
        this.socket = socket;
        this.oOS = oOS;
        this.oIS = oIS;
        this.command = command;
    }

    @Override
    public void run() {
        try {
            switch (command.CMD) {
                case ECommand.CMD_SERVER_SHUTDOWN: {
                    ClientDialog.ShowDialog(Alert.AlertType.WARNING, "Warning Dialog", "Server shutdown", "Server has shutdown!\nPlease come back another time.");
                    synchronized (App.CL_CFG.Stage) {
                        App.CL_CFG.Stage.wait();
                    }
                    Platform.exit();
                    break;
                }
                case ECommand.CMD_SERVICE_UNAVAILABLE: {
                    if (command.Body instanceof String) {
                        ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error", (String) command.Body);
                    }
                    break;
                }
                case ECommand.CMD_CREATED: {
                    if (command.Body instanceof TUser) {
                        //o utilizador foi criado com sucesso
                        TUser user = (TUser) command.Body;
                        System.out.println("Utilizador criado!");
                        // Send File to server
                        ServerController.SendFile(user.getUPhoto(), user.getUUsername(), null);
                        ClientDialog.ShowDialog(Alert.AlertType.INFORMATION, "Information Dialog", "User", "The user has been successfully created!");
                    } else if (command.Body instanceof ArrayList<?>) {
                        if (((ArrayList<?>) command.Body).get(0) instanceof TChannelMessage
                                && App.CL_CFG.SelectedChannel instanceof TChannel
                                && ((TChannel) App.CL_CFG.SelectedChannel).equals(((ArrayList<TChannelMessage>) command.Body).get(0).getCID())) {
                            //Mensagem do canal foi criada com sucesso
                            synchronized (App.CL_CFG.LockCM) {
                                App.CL_CFG.ChannelMessage = (ArrayList<TChannelMessage>) command.Body;
                                App.CL_CFG.DirectMessages = null;
                                App.CL_CFG.LockCM.notifyAll();
                            }
                        } else if (((ArrayList<?>) command.Body).get(0) instanceof TChannel) {
                            //Canal criado com sucesso
                            synchronized (App.CL_CFG.LockCL) {
                                App.CL_CFG.ChannelsList = (ArrayList<TChannel>) command.Body;
                                App.CL_CFG.LockCL.notifyAll();
                            }
                        } else if (((ArrayList<?>) command.Body).get(0) instanceof TUser) {
                            //Atualizar os DM Users
                            synchronized (App.CL_CFG.LockDMUsers) {
                                App.CL_CFG.DMUsers = (ArrayList<TUser>) command.Body;
                                App.CL_CFG.LockDMUsers.notifyAll();
                            }
                        }
                    } else if (command.Body instanceof GenericPair<?, ?>) {
                        //Criar mensagem DM
                        var body = (GenericPair<ArrayList<TDirectMessage>, ArrayList<TUser>>) command.Body;
                        if (App.CL_CFG.SelectedChannel instanceof TUser
                                && (((TUser) App.CL_CFG.SelectedChannel).equals(body.key.get(0).getUID())
                                || (((TUser) App.CL_CFG.SelectedChannel).equals(body.key.get(0).getMID().getMUID())))) {
                            synchronized (App.CL_CFG.LockCM) {
                                App.CL_CFG.DirectMessages = body.key;
                                App.CL_CFG.ChannelMessage = null;
                                App.CL_CFG.LockCM.notifyAll();
                            }
                        } else {
                            synchronized (App.CL_CFG.LockDMUsers) {
                                App.CL_CFG.DMUsers = body.value;
                                App.CL_CFG.LockDMUsers.notifyAll();
                            }
                        }
                    }
                    break;
                }
                case ECommand.CMD_BAD_REQUEST: {
                    //Mostrar as mensagens de erro provenientes do servidor
                    if (command.Body instanceof String) {
                        ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error", (String) command.Body);
                    }
                    break;
                }
                case ECommand.CMD_LOGIN: {
                    LoginPackage LP = (LoginPackage) command.Body;
                    synchronized (App.CL_CFG) {
                        App.CL_CFG.OnlineUsers = LP.Users;
                        App.CL_CFG.ChannelsList = LP.Channels;
                        App.CL_CFG.MyUser = LP.LoginAuthor;
                        App.CL_CFG.DMUsers = LP.DMUsers;
                        App.CL_CFG.ChannelUsers = LP.ChannelUsers;
                        App.CL_CFG.setLogin();
                        App.CL_CFG.notifyAll();
                    }
                    break;
                }
                case ECommand.CMD_UNAUTHORIZED: {
                    //Não autorizado
                    if (command.Body instanceof String) {
                        ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error", (String) command.Body);
                    }
                    synchronized (App.CL_CFG) {
                        App.CL_CFG.notifyAll();
                    }
                    break;
                }
                case ECommand.CMD_FORBIDDEN: {
                    ClientDialog.ShowDialog(Alert.AlertType.ERROR, "Error Dialog", "Error", "Command Forbidden");
                    break;
                }
                case ECommand.CMD_GET_CHANNEL_MESSAGES: {
                    //Mensagens de um canal
                    synchronized (App.CL_CFG.LockCM) {
                        App.CL_CFG.ChannelMessage = (ArrayList<TChannelMessage>) command.Body;
                        App.CL_CFG.DirectMessages = null;
                        App.CL_CFG.LockCM.notifyAll();
                    }
                    break;
                }
                case ECommand.CMD_GET_DM_MESSAGES: {
                    //Mensagens dos DM
                    synchronized (App.CL_CFG.LockCM) {
                        App.CL_CFG.DirectMessages = (ArrayList<TDirectMessage>) command.Body;
                        App.CL_CFG.ChannelMessage = null;
                        App.CL_CFG.LockCM.notifyAll();
                    }
                    break;
                }
                case ECommand.CMD_DOWNLOAD: {
                    FileChunk fc = (FileChunk) command.Body;
                    if (fc.getFilePart() == null) {
                        // Done downloading...
                        ClientDialog.ShowDialog(Alert.AlertType.INFORMATION,
                                "Information Dialog", "Download",
                                "The requested file has been successfully downloaded to your Downloads folder:\n'"
                                + fc.getUsername() + "'");
                    } else {
                        try {
                            // Write File
                            String home = System.getProperty("user.home");
                            //Os downloads serão colocados na pasta de transferencias
                            try ( FileOutputStream f = new FileOutputStream(home + "/Downloads/" + fc.getUsername(), true)) {
                                if (fc.getLength() > 0) {
                                    synchronized (f.getChannel()) {
                                        while (f.getChannel().position() < fc.getOffset()) {
                                            f.getChannel().wait(10);
                                        }
                                    }
                                    f.write(fc.getFilePart(), 0, fc.getLength());
                                }
                            }
                        } catch (Exception ex) {
                            ExceptionHandler.ShowException(ex);
                        }
                    }
                    break;
                }
                case ECommand.CMD_DELETE_CHANNEL: {
                    App.CL_CFG.SelectedChannel = null;
                    synchronized (App.CL_CFG.LockCM) {
                        App.CL_CFG.ChannelMessage = null;
                        App.CL_CFG.LockCM.notifyAll();
                    }
                }
                case ECommand.CMD_UPDATE_CHANNEL: {
                    if (command.Body instanceof ArrayList<?>) {
                        if (((ArrayList<?>) command.Body).get(0) instanceof TChannel) {
                            synchronized (App.CL_CFG.LockCL) {
                                App.CL_CFG.ChannelsList = (ArrayList<TChannel>) command.Body;
                                if (App.CL_CFG.SelectedChannel != null) {
                                    App.CL_CFG.SelectedChannel = App.CL_CFG.GetChannelByCName(((TChannel) App.CL_CFG.SelectedChannel).getCName());
                                }
                                App.CL_CFG.LockCL.notifyAll();
                            }
                        }
                    }
                    break;
                }
                case ECommand.CMD_UPDATE_CHANNEL_USERS: {
                    synchronized (App.CL_CFG.LockCU) {
                        App.CL_CFG.ChannelUsers = (ArrayList<TChannelUser>) command.Body;
                    }
                    break;
                }
                case ECommand.CMD_ONLINE_USERS: {
                    if (command.Body instanceof ArrayList<?>) {
                        if (((ArrayList<?>) command.Body).get(0) instanceof TUser) {
                            synchronized (App.CL_CFG.LockOUsers) {
                                App.CL_CFG.OnlineUsers = (ArrayList<TUser>) command.Body;
                                App.CL_CFG.OnlineUsers.remove(App.CL_CFG.MyUser);
                                App.CL_CFG.LockOUsers.notifyAll();
                            }
                        }
                    }
                    break;
                }
                case ECommand.CMD_SEARCH_USERS: {
                    synchronized (App.CL_CFG.LockFo) {
                        App.CL_CFG.FoundUsers = (ArrayList<TUser>) command.Body;
                        App.CL_CFG.LockFo.notifyAll();
                    }
                    break;
                }
                case ECommand.CMD_UPDATE_SERVERS: {
                    synchronized (App.CL_CFG.LockSL) {
                        App.CL_CFG.ServerList = (ArrayList<Server>) command.Body;
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        } catch (Exception ex) {
            ExceptionHandler.ShowException(ex);
        }
    }
}
