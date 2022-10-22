package net.codejava.controllers;

import net.codejava.entity.AuthenticationType;
import net.codejava.entity.Role;
import net.codejava.entity.User;
import net.codejava.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Controller
public class RegController
{
    @Autowired
    private UserRepo userRepo;


    @GetMapping("/reg")
    public String registration() {
        return "reg";
    }

    @PostMapping("/reg")
    public String addUser(Map<String, Object> model, User user) {
        System.out.println(user+" USer "+user.getUsername());
        user.setAuthType(AuthenticationType.DATABASE);
        //user.setRoles(Collections.singleton(new Role("USER")));
        //user.setRoles(Collections.singleton(new Role("ADMIN")));
        user.setRoles(new HashSet<Role>(){{ add(new Role("USER"));add(new Role("ADMIN")); }});
        Date date = new Date(System.currentTimeMillis());
        user.setRegDate(date);
        user.setActive(true);
        userRepo.save(user);

        return "redirect:/login3";
    }
    @GetMapping("/login3")
    public String login()
    {
        return "login3";
    }
}
