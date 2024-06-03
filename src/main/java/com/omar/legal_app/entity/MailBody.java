package com.omar.legal_app.entity;

import lombok.Builder;

@Builder
public class MailBody {
    private String to;
    private String subject;
    private String text;

    // Constructors, getters, setters (or use @Data to generate them)

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}