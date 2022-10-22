package net.codejava.service;

import net.codejava.entity.AuthenticationType;
import net.codejava.entity.Role;
import net.codejava.entity.User;
import net.codejava.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Service
public class UserSevice implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

//    @Autowired
//    private MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //User userD = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userD= userRepo.findByUsername(username);
        String u = userD.getUsername();
        System.out.println(u+"              j          jopopopopoj");

        if (userRepo.findByUsername(username)==null)
        {
            throw new UsernameNotFoundException("User not Found_+_+_+_+_+__+_+_+_+_+_+_");
        }
        return userD;
    }

    public boolean addUser(User user) {
        User userFromDb = userRepo.findByUsername(user.getUsername());
        //User userFromDbEmail = userRepo.findByEmail(user.getEmail()).orElse(null);
        User userFromDbEmail = userRepo.findByEmail(user.getEmail()).orElse(null);
          if (userFromDbEmail!=null)
          {
              return false;
          }

        if (userFromDb != null) {
            return false;
        }

        user.setActive(false);
        user.setRoles(Collections.singleton(new Role("USER")));
       // user.setRoles(new Role("USER"));
        if (user.getUsername().equals("admin"))
        {
            user.setRoles(Collections.singleton(new Role("ADMIN")));
        }
        user.setActivationCode(UUID.randomUUID().toString());

        userRepo.save(user);

        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to Sweater. Please, visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );

           // mailSender.send(user.getEmail(), "Activation code", message);
        }

        return true;
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);

        if (user == null) {
            return false;
        }
        user.setActive(true);
        user.setActivationCode(null);
       // SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        user.setRegDate(date);
        user.setAuthType(AuthenticationType.valueOf("EMAIL"));
        userRepo.save(user);

        return true;
    }
    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();
        boolean isEmailChanged =  (userEmail!=null && !email.equals(userEmail))
                || (!userEmail.equals(email) && userEmail!=null);
        if (isEmailChanged)
        {
            user.setEmail(email);
            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
                user.setActive(false);
            }
        }
        if (!StringUtils.isEmpty(password))
        {
            user.setPassword(password);
//              user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepo.save(user);
        if (isEmailChanged)
        {
            String message2 = String.format(
                    "Hello, %s! \n" +
                            "Email is changed to Sweater. Please, visit next link: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );
           // mailSender.send(user.getEmail(),"New Email",message2);
            //sendMessage(user);
        }

    }

    public void updateAuthenticationType(String username, String oauth2ClientName) {
        AuthenticationType authType = AuthenticationType.valueOf(oauth2ClientName.toUpperCase());
       // userRepo.updateAuthenticationType(username, authType);
        System.out.println("Updated user's authentication type to " + authType);
        //User user = userRepo.getUserByUsername(username);
        //userRepo.save(user);
        //  System.out.println(user.getUsername()+user.getPassword()+user.getAuthType()+" USER ");
    }
}


