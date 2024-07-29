package com.omar.legal_app.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile
;



public class RequestDto {
    private Integer id;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    private String description;
    private Integer customerId; 
    private List<MultipartFile> files = new ArrayList<>();
    private Response response;

    private String folderName;
    private List<DetilesDto> details = new ArrayList<>();
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    public List<MultipartFile> getFiles() {
        return files;
    }
    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }
    public Response getResponse() {
        return response;
    }
    public void setResponse(Response response) {
        this.response = response;
    }
    public String getFolderName() {
        return folderName;
    }
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    public List<DetilesDto> getDetails() {
        return details;
    }
    public void setDetails(List<DetilesDto> details) {
        this.details = details;
    }
 
 
}
