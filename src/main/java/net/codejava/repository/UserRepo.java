package net.codejava.repository;

import net.codejava.entity.AuthenticationType;
import net.codejava.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface UserRepo extends CrudRepository<User,Long>
{

    @Modifying
    @Query("UPDATE User u SET u.authType = ?2 where u.username = ?1")
    public void updateAuthenticationType(String username, AuthenticationType authType);

    User getUserByUsername(String username);

    User findByUsername(String username);

    Optional<User> findByEmail(String email);

    User findByActivationCode(String code);
}
