package net.codejava.security;

import net.codejava.entity.User;
import net.codejava.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl  implements UserDetailsService
{
    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepo.getUserByUsername(s);
        if (user == null) throw new UsernameNotFoundException("Not Found");
        return user;
    }
}
