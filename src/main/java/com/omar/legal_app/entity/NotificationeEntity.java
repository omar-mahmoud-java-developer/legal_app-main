package com.omar.legal_app.entity;


import jakarta.persistence.*;
@Entity
public class NotificationeEntity {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	
	private String messgeString;
	
	@ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessgeString() {
        return messgeString;
    }

    public void setMessgeString(String messgeString) {
        this.messgeString = messgeString;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}