package net.codejava.entity;

import net.codejava.entity.Message;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements UserDetails {
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String username;
	private String password;
	private boolean active;

	private String email;
	private String activationCode;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date regDate;


	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date lastVisit;

	@OneToMany(mappedBy = "author",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	private Set<Message> messages;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable( name = "users_roles",joinColumns = @JoinColumn(name = "user_id"),
	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles =new HashSet<>();

	@Enumerated(EnumType.STRING)
	@Column(name = "auth_type")
	private AuthenticationType authType;

	public User() {
	}

	public boolean isAdmin() {
		for (Role r : roles) {
			if (r.getName().equals("ADMIN")) return true;
		}
		return false;
	}

//		if(roles==null)
//		{
//			return false;
//		}
//		return roles.contains(new Role("ADMIN"));


	public boolean isSuperAdmin()
	{
		for (Role r : roles) {
			if (r.getName().equals("SUPER_ADMIN")) return true;
		}
		return false;
	}

	public AuthenticationType getAuthType() {
		return authType;
	}

	public void setAuthType(AuthenticationType authType) {
		this.authType = authType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isActive();
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = roles.stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
		return authorities;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}


	public void setName(String name) {
		this.username=name;
	}

	public void setGender(String gender) {

	}

	public void setLocale(String locale) {

	}

	public void setUserpic(String picture) {

	}

	//    public void setLastVisit(LocalDateTime lastVisit) {
//        Date date = new Date(System.currentTimeMillis());
//            this.lastVisit=date;
//    }
	public Date getLastVisit() {
		return lastVisit;
	}

	public void setLastVisit(Date lastVisit) {
		this.lastVisit = lastVisit;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}
