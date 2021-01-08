package pt.isec.deis.lei.pd.trabprat.server.springboot.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    @GetMapping("{channel}")
    public String getMessages(HttpServletRequest request, @PathVariable("channel") String channel, @RequestParam(value = "n", required = false) Integer num) {
        String token = request.getHeader("Authorization");
        var tokenSet = MainRestAPI.getToken(token);
        if (tokenSet == null) {
            return null;
        }
        User user = tokenSet.getKey();
        StringBuilder sb = new StringBuilder();
        if (num == null) {
            num = 10;
        }

        return sb.toString();
    }

    @PostMapping("")
    public String sendMessage(HttpServletRequest request, @RequestBody String msg) {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
}
