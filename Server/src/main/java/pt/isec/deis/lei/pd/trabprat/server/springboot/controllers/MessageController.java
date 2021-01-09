package pt.isec.deis.lei.pd.trabprat.server.springboot.controllers;

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
import pt.isec.deis.lei.pd.trabprat.model.TChannel;
import pt.isec.deis.lei.pd.trabprat.model.TMessage;
import pt.isec.deis.lei.pd.trabprat.model.TUser;
import pt.isec.deis.lei.pd.trabprat.server.Main;
import pt.isec.deis.lei.pd.trabprat.server.db.DatabaseWrapper;
import pt.isec.deis.lei.pd.trabprat.server.springboot.MainRestAPI;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.IServerService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.interfaces.ITokenService;
import pt.isec.deis.lei.pd.trabprat.server.springboot.model.User;

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
            return null;
        }
        if (num == null) {
            num = 10;
        }
        User user = tokenSet.getKey();
        DatabaseWrapper db = MainRestAPI.SV_CFG.DB;
        TUser dbUser = db.getUserByUsername(user.getUsername());
        TChannel dbChannel = null;
        TUser otherUser = null;
        ArrayList<TMessage> messages = null;
        String name = null;
        if (channel != null) {
            dbChannel = db.getChannelByName(channel);
            if (dbChannel == null) {
                return null;
            }
            name = dbChannel.getCName();
            messages = db.getAllMessagesFromChannelID(dbChannel.getCID(), num);
        }
        if (dmUser != null) {
            otherUser = db.getUserByUsername(dmUser);
            if (otherUser == null) {
                return null;
            }
            name = otherUser.getUUsername();
            messages = db.getAllDMByUserIDAndOtherID(dbUser.getUID(), otherUser.getUID(), num);
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
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
}
