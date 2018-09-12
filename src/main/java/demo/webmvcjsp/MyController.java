package demo.webmvcjsp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @RequestMapping("/")
    @ResponseBody
    public String handler(Model model) {
        model.addAttribute("msg", "Spring Boot web app, JAR packaging, JSP views");
        return "myView";
    }
    
    @RequestMapping("/transactions")
    public String transactionHandler(Model model) {
        //model.addAttribute("msg", "Spring Boot web app, JAR packaging, JSP views");
        return "transaction";
    }
}