package com.omar.legal_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
@Data
@Entity
@Table(name = "comments")

public class Comments {
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


        @Column(columnDefinition = "TEXT")
    private String comments;


        @ManyToOne
    private User user;

    @ManyToOne
    private RequestEntity requestEntity;

 


}
