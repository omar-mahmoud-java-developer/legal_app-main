package com.omar.legal_app.entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.omar.legal_app.dto.Response;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
@Entity

@Table(name = "request")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;



    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public Date getRequestDate() {
      return requestDate;
    }

    public void setRequestDate(Date requestDate) {
      this.requestDate = requestDate;
    }

    public Date getResponseDate() {
      return responseDate;
    }

    public void setResponseDate(Date responseDate) {
      this.responseDate = responseDate;
    }

    public String getFolderName() {
      return folderName;
    }

    public void setFolderName(String folderName) {
      this.folderName = folderName;
    }

    public List<String> getFileNames() {
      return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
      this.fileNames = fileNames;
    }

    public Response getResponse() {
      return response;
    }

    public void setResponse(Response response) {
      this.response = response;
    }

    public Set<User> getUsers() {
      return users;
    }

    public void setUsers(Set<User> users) {
      this.users = users;
    }

    public Set<Comments> getComments() {
      return comments;
    }

    public void setComments(Set<Comments> comments) {
      this.comments = comments;
    }

    public CustomerEntity getCustomer() {
      return customer;
    }

    public void setCustomer(CustomerEntity customer) {
      this.customer = customer;
    }


    @Column(columnDefinition="TEXT")
    private String description;
    private Date requestDate;
    private Date responseDate;
    private String folderName;
    private List<String> fileNames = new ArrayList<>();  
    
    @Column(name="Response")
    private Response response;


    @ManyToMany
    @JoinTable(
        name = "user_requests",
        joinColumns = @JoinColumn(name = "request_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "requestEntity")
    private Set<Comments> comments = new HashSet<>();
 
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

 
    @OneToMany(mappedBy = "requestEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RequestDetails> requestDetails = new HashSet<>();

    public Set<RequestDetails> getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(Set<RequestDetails> requestDetails) {
        this.requestDetails = requestDetails;
    }

}