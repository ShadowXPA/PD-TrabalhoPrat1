package pt.isec.deis.lei.pd.trabprat.observer.rmi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import pt.isec.deis.lei.pd.trabprat.encryption.AES;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.rmi.RemoteObserverRMI;
import pt.isec.deis.lei.pd.trabprat.rmi.RemoteServerRMI;

public class ObserverObject extends UnicastRemoteObject implements RemoteObserverRMI {

    private static final SimpleDateFormat sDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private final BufferedWriter out;
    private final BufferedReader in;
    private final String serverHost;
    private int loggedService;
    private TUser user;
    private ArrayList<RemoteServerRMI> services;

    public void initialize() throws IOException {
        boolean reading = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < services.size(); i++) {
            sb.append("[").append(i).append("] - ");
            sb.append(services.get(i));
            sb.append("\n");
        }
        while (reading) {
            writeLine(sb.toString());
            writeLine("Command:");
            String command = in.readLine();
            if (command.equals("exit")) {
                reading = false;
            } else {
                handleCommand(command);
            }
        }
    }

    private void handleCommand(String command) throws IOException {
        String[] cmd = command.split(" ", 3);
        switch (cmd[0].toLowerCase()) {
            case "help": {
                handleHelp();
                break;
            }
            case "register": {
                if (this.loggedService == -1) {
                    handleRegister(Integer.parseInt(cmd[1]), cmd[2]);
                } else {
                    writeLine("User is logged in.");
                }
                break;
            }
            case "login": {
                if (this.loggedService != -1) {
                    handleLogin(Integer.parseInt(cmd[1]), cmd[2]);
                } else {
                    writeLine("User is already logged in.");
                }
                break;
            }
            case "logout": {
                if (this.loggedService != -1) {
                    handleLogout();
                } else {
                    writeLine("User is not logged in.");
                }
                break;
            }
            case "message": {
                if (this.loggedService != -1) {
                    // TODO: Send message to service?
                    // Send DM to everybody logged in?
//                    handleMessage(cmd[1], cmd[2]);
                } else {
                    writeLine("User is not logged in.");
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    private void handleHelp() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Help\n");
        sb.append("help - Show this.\n");
        sb.append("register [service] [Username] [Name] - Registers a user.\n");
        sb.append("login [service] [Username] [Password] - Logins into a server.\n");
        sb.append("logout - Logs out of a server.\n");
        sb.append("message [service] [Message] - Sends a message to a server.\n");
//        sb.append("file [service] [Path] - Sends a file to a server.\n");
        writeLine(sb.toString());
    }

    private void handleRegister(int service, String command) throws IOException {
        RemoteServerRMI server = services.get(service);
        String[] usernameName = command.split(" ", 2);
        writeLine("Password:");
        String password = in.readLine();
        try {
            password = AES.Encrypt(password);
            TUser registeredUser = new TUser(0, usernameName[1], usernameName[0], password, null, 0);
            server.registerUser(registeredUser);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private void handleLogin(int service, String command) throws RemoteException {
        RemoteServerRMI server = services.get(service);
        String[] usernamePassword = command.split(" ", 2);
        try {
            usernamePassword[1] = AES.Encrypt(usernamePassword[1]);
            TUser loggedUser = new TUser(0, null, usernamePassword[0], usernamePassword[1], null, 0);
            this.loggedService = service;
            server.addObserver(this, loggedUser);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private void handleLogout() throws RemoteException {
        RemoteServerRMI server = services.get(this.loggedService);
        server.removeObserver(this, this.user);
        this.user = null;
        this.loggedService = -1;
    }

    @Override
    public void notifyObserver(String message) throws RemoteException {
        try {
            writeLine(message);
        } catch (IOException ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    @Override
    public void notifyAuthentication(TUser user) throws RemoteException {
        this.user = user;
        try {
            if (this.user == null) {
                writeLine("Authentication failed!");
                this.loggedService = -1;
            } else {
                writeLine("Authenticated as: " + user);
            }
        } catch (IOException ex) {
            ExceptionHandler.ShowException(ex);
        }
    }

    private void writeLine(String message) throws IOException {
        Date d = new Date();
        String formattedDate = sDF.format(d);
        out.write("[");
        out.write(formattedDate);
        out.write("]: ");
        out.write(message);
        out.newLine();
        out.flush();
    }

    public ObserverObject(String serverHost, InputStream inStream, OutputStream outStream) throws RemoteException, IOException {
        this.serverHost = serverHost;
        this.loggedService = -1;
        this.user = null;
        this.services = new ArrayList<>();
        this.in = new BufferedReader(new InputStreamReader(inStream));
        this.out = new BufferedWriter(new OutputStreamWriter(outStream));
        Registry reg = LocateRegistry.getRegistry(this.serverHost);
        String[] registries = reg.list();
        for (String registry : registries) {
            try {
                this.services.add((RemoteServerRMI) reg.lookup(registry));
            } catch (NotBoundException | AccessException ex) {
                writeLine(ex.getMessage());
            }
        }
    }
}
