package com.omar.legal_app.entity;
import java.util.Date;

import com.omar.legal_app.dto.Cases;
import com.omar.legal_app.dto.PriorityLevel;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "RequestDetiles")
public class RequestDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String text ;

    @Enumerated(EnumType.STRING)
    private Cases cases;
    @Enumerated(EnumType.STRING)
    private PriorityLevel priorityLevel;
    @NotNull
    private Date startDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    @NotNull
    private Date endDate;
  
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Cases getCases() {
        return cases;
    }

    public void setCases(Cases cases) {
        this.cases = cases;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevel = priorityLevel;
    }


    public RequestEntity getRequestEntity() {
        return requestEntity;
    }

    public void setRequestEntity(RequestEntity requestEntity) {
        this.requestEntity = requestEntity;
    }
    @ManyToOne
    @JoinColumn(name = "request_id")
    private RequestEntity requestEntity;



}