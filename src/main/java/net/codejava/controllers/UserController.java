package net.codejava.controllers;

import net.codejava.entity.Message;
import net.codejava.entity.Role;
import net.codejava.entity.User;
import net.codejava.repository.MessageRepo;
import net.codejava.repository.UserRepo;
import net.codejava.service.UserSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
//@PreAuthorize("hasAuthority('ADMIN')")
//@PreAuthorize("hasAuthority('SUPER_ADMIN')")
//@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
//@PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")

public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private UserSevice userSevice;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    public String userList(Model model,@AuthenticationPrincipal User user, Principal principal) {
        model.addAttribute("users", userRepo.findAll());

            User user1 = userRepo.findByUsername(principal.getName());
         //   String role = user1.getRoles().stream().sorted().collect(Collectors.toList()).get(0).getName();
          //  String role2 = user1.getRoles().stream().sorted().collect(Collectors.toList()).get(1).getName();
           // String role3 = user1.getRoles().stream().sorted().collect(Collectors.toList()).get(2).getName();
          //  System.out.println("ROLE  ---- "+role+role2+role3);
            model.addAttribute("userAdmin",user1);


        return "userList";
    }

    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model,Principal principal)
    {


            User user1 = userRepo.getUserByUsername(principal.getName());
            model.addAttribute("user",user1);
            model.addAttribute("roles",user1.getRoles());


        return "userEdit";
    }

    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user
    ) {
        user.setUsername(username);

//        Set<String> roles = Arrays.stream(Role)
//                .map(Role::name)
//                .collect(Collectors.toSet());
        System.out.println(form.keySet()+"  KEY SET   ");
        System.out.println(Arrays.stream(form.keySet().toArray())+"  KEY SET   ");

          //Polucit' vse ego roli;
        user.getRoles().clear();

//        for (String key : form.keySet()) {
//            if (roles.contains(key)) {
//                user.getRoles().add(Role.values());
//            }
//        }
        //        for (String key : form.keySet()) {
//            if (roles.contains(key)) {
//                user.getRoles().add(Role.values());
//            }
//        }


        userRepo.save(user);

        return "redirect:/user";
    }
    @GetMapping("/userProfile")
    public String profile(@AuthenticationPrincipal User user, Model model, Principal principal)
    {
       // System.out.println(user+" U "+user+" CU"+user.getId()+"   ID   ");

        User userfd = userRepo.findByUsername(principal.getName());
        model.addAttribute("username",userfd.getUsername());
        model.addAttribute("password",userfd.getPassword());
        model.addAttribute("email",userfd.getEmail());
        return "userProfile";
    }
    @PostMapping("/userProfile")
    public String updateProfile(@AuthenticationPrincipal User user,
                                @RequestParam String email,
                                @RequestParam String password, Principal principal
    )
    {
        User user1 = userRepo.findByUsername(principal.getName());
        userSevice.updateProfile(user1, password , email);
        return "redirect:/user/userProfile";
    }
    @GetMapping("/myMes/{u.id}")
    public String mymes(
            @AuthenticationPrincipal User curentuUser, Principal principal,
           // @PathVariable User user,
            Model model
    ) {
        User userDB = userRepo.getUserByUsername(principal.getName());
        Set<Message> m = userDB.getMessages();
        if (m==null || m.isEmpty())
        {
            model.addAttribute("messages","NO Messages");
        }

        if (userDB.isAdmin())
        {
            Iterable<Message> messages = messageRepo.findAll();
            model.addAttribute("messages",messages);
        }
        else
        {
            model.addAttribute("messages", m);
        }
        if (curentuUser !=null)  model.addAttribute("isCurrentUser", curentuUser.equals(userDB));
        else model.addAttribute("isCurrentUser",null);
        return "myMes";
    }


    @GetMapping("/del-user-messages/{id}")
    public String delete(
            @PathVariable("id") Long id,
            Model model)
    {
        Message message = messageRepo.findById(id).get();
        String filename = message.getFilename();
        System.out.println("FILENAME++++++++++++++++  "+filename);
        File file = new File(message.getFilename());
        if(file.delete()){
            System.out.println(file.getName() + " is deleted!");
            //removeFileCheck="true";
        }else{
            System.out.println("Delete operation is failed.");
        }
       // String uuidFile = UUID.randomUUID().toString();
       // String resultFilename = uuidFile + "." + file.getOriginalFilename();
       // file.transferTo(new File(uploadPath + "/" + resultFilename));
       // File file = message.getFilename();
        System.out.println("Del User Messages id" +id);
       // Long id = null;
        messageRepo.deleteById(id);
        return "/myMes";
    }
//    @PostMapping("/del-user-messages/{id")
//    public String deleteP(
//            @PathVariable("id") Long id,Principal principal
//    ){
//        User user1 = userRepo.findByUsername(principal.getName());
//
//        System.out.println("POSTMAPPING   "+id);
//
//        messageRepo.deleteById(id);
//        return "redirect:/user/myMes/"+user1.getId();
//    }

    @GetMapping("/editMes/{id}")
    public String Edit(@PathVariable("id") Long id, Model model,
                       @AuthenticationPrincipal User user,
                       Principal principal)
    {
        String mes = "Hello to message editor";
        System.out.println("Hello to message editor");
        Message editMes = messageRepo.findById(id).orElse(null);
        System.out.println("Edit mes ------------------------------"+ editMes);
        Optional<Message> emes = messageRepo.findById(id);
        model.addAttribute("eMes",editMes);
        model.addAttribute("emes",emes);
        model.addAttribute("emes",mes);
        User userFD = userRepo.findByUsername(principal.getName());
        model.addAttribute("u",userFD);
        return "/mesEdit";
    }
    @PostMapping("/editMes/{id}")
    public String updateMes(
            @PathVariable("id") Long id,
            @RequestParam String text, @RequestParam String tag, @RequestParam("file") MultipartFile file,Principal principal,
            @AuthenticationPrincipal User user, Model model
    ) throws IOException {
        User userddb = userRepo.getUserByUsername(principal.getName());
        Message editMes = messageRepo.findById(id).orElse(new Message());  //CHECK THIS GET();

        model.addAttribute("eMes",editMes);
        model.addAttribute("u",userddb);
        if (!StringUtils.isEmpty(text))
        {
            editMes.setText(text);
        }
        if (!StringUtils.isEmpty(tag))
        {
            editMes.setTag(tag);
        }
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();

             file.transferTo(new File(uploadPath + "/" + resultFilename));

            editMes.setFilename(resultFilename);

            System.out.println("EditMes+++++++++++++++++++++"+editMes);
        }
        System.out.println("OK IT's WORK_____________________________________________________________________");
        messageRepo.save(editMes);
        return "redirect:/user/myMes/"+userddb.getId();
    }

    //Soobchenie conctretnogo polzovatelya dlya admina vnachale
    @GetMapping("/userMes/{user}")
    public String userMes(
            @AuthenticationPrincipal User curentuUser,
            @PathVariable User user,
            Principal principal,
            //@PathVariable Long id,
            Model model
    ) {
        System.out.println(principal.getName()+" NAme USers Principal MEssages");
      //  System.out.println(curentuUser.getUsername()+" NAme Currennt AUTHENTIC ");
        System.out.println(user.getUsername()+" NAme USers Path variable");
        System.out.println(user.getId()+"USER_ID");
       Set<Message> messages = user.getMessages();
        model.addAttribute("messages", messages);
        if (curentuUser!=null)
            model.addAttribute("isCurrentUser", curentuUser.equals(user));
        else model.addAttribute("isCurrentUser",principal.getName().equals(user.getUsername()));
        return "userMes";
    }
}
