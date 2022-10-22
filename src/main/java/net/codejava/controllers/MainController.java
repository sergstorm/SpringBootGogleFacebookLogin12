package net.codejava.controllers;

import net.codejava.entity.Message;
import net.codejava.entity.User;
import net.codejava.repository.MessageRepo;
import net.codejava.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class MainController
{
    @Autowired
    private UserRepo userRepo;
        @Value("${upload.path}")
    private String uploadPath;
        @Autowired
        private MessageRepo messageRepo;

    @GetMapping("/chair")
    public String shop()
    {
        return "chair";
    }

    @GetMapping("/main")
    public String main(
            @AuthenticationPrincipal User user, Principal principal,
            @RequestParam(required = false, defaultValue = "") String filter,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication+"AUTH CUrrent");

        System.out.println("Main method USER+ Authentication "+user);
        System.out.println("Main method USER Principal"+principal.getName());

        User userFDB = userRepo.findByUsername(principal.getName());



        Iterable<Message> messages = messageRepo.findAll();

        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        } else {
            messages = messageRepo.findAll();
        }
        boolean a;
        if (user==null) a=false;
        else a  = user.isAdmin();


        //org.springframework.social.facebook.api.User userProfile = facebook.userOperations().getUserProfile();
        //System.out.println(userProfile.getId()+"ID" + userProfile.getName()+" NAME "+userProfile.getLastName());

        System.out.println("ADMIN______________________" + a);
        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);
        List<Message> messagSet = (List<Message>) messages;
        for (Message m :messagSet)
        {
            if (m==null) System.out.println("NO MESSAGE");
            else
            {
                // System.out.println(m.getAuthor().getEmail());
                // System.out.println(m.getAuthor().getId());
            }
        }
        model.addAttribute("u",userFDB);
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag, Map<String, Object> model,
            @RequestParam("file") MultipartFile file, Principal principal
    ) throws IOException {
        User user1 = userRepo.findByUsername(principal.getName());
        if ((long) user1.getMessages().size() >9)
        {
            System.out.println("MORE THAN 10 MESSAGES TO 1 ARTICLE");
            model.put("mes","MORE THAN 10 MESSAGES TO 1 ARTICLE");
            return "main";
        }
        Message message = new Message();
        //System.out.println(principal.getName()+"principal Author ID");
        //System.out.println(principal.getName()+"principal AuthorName");
        //System.out.println(message.getAuthor()+"message Author ");

        if (file != null && !file.getOriginalFilename().isEmpty()) {
             File uploadDir = new File(uploadPath);

              if (!uploadDir.exists()) {
                   uploadDir.mkdir();
              }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            message.setFilename(resultFilename);
        }
       // System.out.println(user.getEmail()+"  USER_________________FROM POST");

        message.setAuthor(user1);
        message.setText(text);
        message.setTag(tag);
        Date date = new Date(System.currentTimeMillis());
        message.setDateMessage(date);

        messageRepo.save(message);

        Iterable<Message> messages = messageRepo.findAll();
//        for (Message m: messages) {
//           // System.out.println(""+m.getAuthorName());
//           // System.out.println(""+m.getId());
//          //  System.out.println(""+m.getAuthor().getId());
//        }
        model.put("messages", messages);
        // model.put("idUser", user.getId());
        model.put("us", user);
        model.put("u", user);

        return "main";
    }

    @GetMapping("/")
    public String index(
            @AuthenticationPrincipal User user, Principal principal,
            @RequestParam(required = false, defaultValue = "") String filter,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication+"AUTH CUrrent");

        System.out.println("Main method USER+ Authentication "+user);
      //  System.out.println("Main method USER Principal"+principal.getName());
        User userFDB;
        if (principal!=null){
         userFDB = userRepo.findByUsername(principal.getName());}
        else
        {
            userFDB=null;
        }



        Iterable<Message> messages = messageRepo.findAll();

        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        } else {
            messages = messageRepo.findAll();
        }
        boolean a;
        if (user==null) a=false;
        else a  = user.isAdmin();


        //org.springframework.social.facebook.api.User userProfile = facebook.userOperations().getUserProfile();
        //System.out.println(userProfile.getId()+"ID" + userProfile.getName()+" NAME "+userProfile.getLastName());

        System.out.println("ADMIN______________________" + a);
        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);
        List<Message> messagSet = (List<Message>) messages;
        for (Message m :messagSet)
        {
            if (m==null) System.out.println("NO MESSAGE");
            else
            {
                // System.out.println(m.getAuthor().getEmail());
                // System.out.println(m.getAuthor().getId());
            }
        }
        model.addAttribute("u",userFDB);
        return "index";
    }

    @PostMapping("/")
    public String indexPost(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag, Map<String, Object> model,
            @RequestParam("file") MultipartFile file, Principal principal
    ) throws IOException {
        User user1 = userRepo.findByUsername(principal.getName());
        if ((long) user1.getMessages().size() >9)
        {
            System.out.println("MORE THAN 10 MESSAGES TO 1 ARTICLE");
            model.put("mes","MORE THAN 10 MESSAGES TO 1 ARTICLE");
            Iterable<Message> messages = messageRepo.findAll();
            model.put("messages", messages);
            model.put("us", user);
            model.put("u", user);
            return "index";
        }
        if (text.length()>2000)
        {
            System.out.println("MORE THAN 2000 Symbols in one message in your message number of symbols - "+text.length());
            model.put("mes","MORE THAN 2000 Symbols in one message i your message - "+text.length());
            Iterable<Message> messages = messageRepo.findAll();
            model.put("messages", messages);
            model.put("us", user);
            model.put("u", user);
            return "index";
        }
        Message message = new Message();

        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFilename));

            message.setFilename(resultFilename);
        }
        // System.out.println(user.getEmail()+"  USER_________________FROM POST");

        message.setAuthor(user1);
        message.setText(text);
        message.setTag(tag);
        Date date = new Date(System.currentTimeMillis());
        message.setDateMessage(date);

        messageRepo.save(message);

        Iterable<Message> messages = messageRepo.findAll();
//        for (Message m: messages) {
//           // System.out.println(""+m.getAuthorName());
//           // System.out.println(""+m.getId());
//          //  System.out.println(""+m.getAuthor().getId());
//        }
        model.put("messages", messages);
        // model.put("idUser", user.getId());
        model.put("us", user);
        model.put("u", user);

        return "index";
    }

}
