package com.omar.legal_app.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Assuming code should not be the ID
    
    private Integer code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiration;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and setters

    // Builder pattern for convenience
    public static ForgotPasswordBuilder builder() {
        return new ForgotPasswordBuilder();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static class ForgotPasswordBuilder {
        private Integer code;
        private Date expiration;
        private User user;

        public ForgotPasswordBuilder code(Integer code) {
            this.code = code;
            return this;
        }

        public ForgotPasswordBuilder expiration(Date expiration) {
            this.expiration = expiration;
            return this;
        }

        public ForgotPasswordBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ForgotPassword build() {
            ForgotPassword forgotPassword = new ForgotPassword();
            forgotPassword.code = this.code;
            forgotPassword.expiration = this.expiration;
            forgotPassword.user = this.user;
            return forgotPassword;
        }
    }
}
