package com.omar.legal_app.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity

@Table(name="request")
public class RequestEntity {
 @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition="TEXT")
    private String description;
    // private Date requestData;   
    // @Enumerated (EnumType.STRING)
    // private Response pendingJustication;
    //  private Date pendingData; 
    // @Enumerated (EnumType.STRING)
    //  private Response  requestResponse;
    //  private Date completionData;
    //  private Date cancelationData;
     private String fileName;
    //  @ManyToOne
    // @JoinColumn(name = "user_id")
    // private UserEntity user;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
