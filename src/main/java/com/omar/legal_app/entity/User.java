package com.omar.legal_app.entity;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String email;
	private String password;
	private String role;
	private String fullname;

    @ManyToMany(mappedBy = "users")
private Set<RequestEntity> requestEntities = new HashSet<>();
 @OneToMany(mappedBy = "user")
    private Set<Comments> comments = new HashSet<>();

	
	public User() {
		super();
	}

	public User(String email, String password, String role, String fullname) {
		
		this.email = email;
		this.password = password;
		this.role = role;
		this.fullname = fullname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

    public Set<Comments> getComments() {
        return comments;
    }

    public void setComments(Set<Comments> comments) {
        this.comments = comments;
    }

    public Set<RequestEntity> getRequestEntities() {
        return requestEntities;
    }

    public void setRequestEntities(Set<RequestEntity> requestEntities) {
        this.requestEntities = requestEntities;
    }
	
	
	
	
	
	
	
	
	

}








