package pt.isec.deis.lei.pd.trabprat.server.springboot.controllers;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.deis.lei.pd.trabprat.communication.Command;
import pt.isec.deis.lei.pd.trabprat.communication.ECommand;
import pt.isec.deis.lei.pd.trabprat.exception.ExceptionHandler;
import pt.isec.deis.lei.pd.trabprat.model.GenericPair;
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TDirectMessage;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.server.springboot.MainRestAPI;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.IServerService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.ITokenService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.User;
import pt.isec.deis.lei.pd.trabprat.thread.tcp.TCPHelper;

@RestController
@RequestMapping("message")
public class MessageController {

//    @Autowired
//    private IServerService SV_CFG;
//    @Autowired
//    private ITokenService tokens;
    @GetMapping("")
    public String getMessages(HttpServletRequest request, @RequestParam(value = "channel", required = false) String channel, @RequestParam(value = "user", required = false) String dmUser, @RequestParam(value = "n", required = false) Integer num) {
        String token = request.getHeader("Authorization");
        var tokenSet = MainRestAPI.getToken(token);
        if (tokenSet == null) {
            return null;
        }
        if ((channel == null && dmUser == null)
                || (channel != null && dmUser != null)) {
            return "You need to input a channel or user!";
        }
        if (num == null) {
            num = 10;
        }
        User user = tokenSet.getKey();
        DatabaseWrapper db = MainRestAPI.SV_CFG.DB;
        TUser dbUser = db.getUserByUsername(user.getUsername());
        TChannel dbChannel;
        TUser otherUser;
        ArrayList<TMessage> messages;
        String name;
        if (channel != null) {
            dbChannel = db.getChannelByName(channel);
            if (dbChannel == null) {
                return "Channel doesn't exist!";
            }
            if (db.doesUserBelongToChannel(dbChannel, dbUser)) {
                name = dbChannel.getCName();
                messages = db.getAllMessagesFromChannelID(dbChannel.getCID(), num);
            } else {
                return "User does not belong to the channel!";
            }
        } else if (dmUser != null) {
            otherUser = db.getUserByUsername(dmUser);
            if (otherUser == null) {
                return "User doesn't exist!";
            }
            if (!dbUser.equals(otherUser)) {
                name = otherUser.getUUsername();
                messages = db.getAllDMByUserIDAndOtherID(dbUser.getUID(), otherUser.getUID(), num);
            } else {
                return "You can't have direct messages with yourself...";
            }
        } else {
            return "You need to input a channel or user!";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head></head><body><h1>");
        sb.append(name);
        sb.append("</h1>");
        for (int i = messages.size() - 1; i >= 0; i--) {
            TMessage msg = messages.get(i);
            sb.append("<p>");
            sb.append(Main.sDF.format(msg.getDate())).append(" ");
            sb.append(msg.getMUID().getUName());
            sb.append(": ");
            sb.append(msg.getMText());
            sb.append("</p>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    @PostMapping("")
    public String sendMessage(HttpServletRequest request, @RequestBody String msg) {
        String token = request.getHeader("Authorization");
        var tokenSet = MainRestAPI.getToken(token);
        if (tokenSet == null) {
            return null;
        }
        if (msg.isEmpty()) {
            return null;
        }
        User user = tokenSet.getKey();
        DatabaseWrapper db = MainRestAPI.SV_CFG.DB;
        TUser dbUser = db.getUserByUsername(user.getUsername());
        TMessage newMessage = new TMessage(0, dbUser, msg, null, 0);
        StringBuilder sb = new StringBuilder();
        for (var users : MainRestAPI.SV_CFG.Clients.values()) {
            TUser userToSend = users.key;
            TDirectMessage dm = new TDirectMessage(newMessage, userToSend);
            int i = db.insertDirectMessage(dm.getUID(), newMessage);
            if (i > 0) {
                TMessage lastMessage = db.getLastMessage();
                TDirectMessage lastDM = new TDirectMessage(lastMessage, dm.getUID());

                ArrayList<TDirectMessage> dmL = db.getAllDMByUserIDAndOtherID(dm.getMID().getMUID().getUID(), dm.getUID().getUID());
                // Broadcast new message to all users
                ArrayList<TUser> dmU = db.getOtherUserFromDM(db.getAllDMByUserID(dm.getUID().getUID()), dm.getUID());
                Command sendCmd = new Command(ECommand.CMD_CREATED, new GenericPair<>(dmL, dmU));
                try {
                    TCPHelper.SendTCPCommand(users.value, sendCmd);
                } catch (IOException ex) {
                    ExceptionHandler.ShowException(ex);
                }
                // Send through multicast
                MainRestAPI.SV_CFG.MulticastMessage(new Command(ECommand.CMD_CREATED,
                        new GenericPair<>(MainRestAPI.SV_CFG.ServerID, lastDM)));
                Main.Log("[RestAPI]", "The message has been sent to '" + userToSend.getUUsername() + "'!");
                sb.append("Message sent to: '").append(userToSend.getUName()).append("'\n");
            } else {
                Main.Log("[RestAPI]", "Message couldn´t be sent to '" + userToSend.getUUsername() + "'!");
                sb.append("Message couldn´t be sent to: '").append(userToSend.getUName()).append("'\n");
            }
        }
        return sb.toString();
    }
}
