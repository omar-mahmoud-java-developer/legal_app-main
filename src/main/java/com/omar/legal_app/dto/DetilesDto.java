package com.omar.legal_app.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class DetilesDto {
    private String text;
    private Cases cases;
    private PriorityLevel priorityLevel;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
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

}
