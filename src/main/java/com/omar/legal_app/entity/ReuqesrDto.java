package com.omar.legal_app.entity;

import org.springframework.web.multipart.MultipartFile;




public class ReuqesrDto {

   private String description;
   private Response response;

     private MultipartFile file;



    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public MultipartFile getFile() {
      return file;
    }

    public void setFile(MultipartFile file) {
      this.file = file;
    }


   
//      @Enumerated (EnumType.STRING)
//      private Response pendingJustication;
//     @CreatedDate

//       private Date pendingData;
      
//      @Enumerated (EnumType.STRING)
//       private Response  requestResponse;
//     @CreatedDate

//        private Date completionData;
//     @CreatedDate

//      private Date cancelationData;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

   


}
