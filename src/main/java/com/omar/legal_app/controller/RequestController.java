package com.omar.legal_app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.omar.legal_app.entity.Comments;
import com.omar.legal_app.entity.CustomerEntity;
import com.omar.legal_app.entity.RequestEntity;
import com.omar.legal_app.entity.Response;
import com.omar.legal_app.entity.ReuqesrDto;
import com.omar.legal_app.entity.User;
import com.omar.legal_app.repository.CommentsRepository;
import com.omar.legal_app.repository.CustomerRepo;
import com.omar.legal_app.repository.RequestRepo;
import com.omar.legal_app.repository.UserRepository;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/request")
public class RequestController {

    @Autowired
    private RequestRepo requestRepo;

    @Autowired
    private UserRepository userRepository;
    @Autowired
     private  CustomerRepo customerRepo;
    private final CommentsRepository commentsRepository;



    public RequestController(CommentsRepository commentsRepository, CustomerRepo customerRepo, RequestRepo requestRepo, UserRepository userRepository) {
        this.commentsRepository = commentsRepository;
        this.customerRepo = customerRepo;
        this.requestRepo = requestRepo;
        this.userRepository = userRepository;
    }



    @GetMapping("/list")
    public String showRequestList(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByEmail(username);
        List<RequestEntity> userRequests = requestRepo.findByUsers(currentUser);
        model.addAttribute("requests", userRequests);
    
      

        return "userRequest"; 
    }

    @GetMapping("/alist")
    public String showRequestAllList(Model model) {
       
        List<RequestEntity> requests = requestRepo.findAll(); 
        model.addAttribute("requests", requests);
   
        return "adminRequest"; // Ensure this is the correct view name
    }
    

    @GetMapping("/create")
    public String showRequest(Model model) {
        ReuqesrDto reuqesrDto = new ReuqesrDto();
        List<CustomerEntity> customers = customerRepo.findAll();
        model.addAttribute("reuqesrDto", reuqesrDto);
        model.addAttribute("customers", customers);
        return "upload";
    }


    
   

    @GetMapping("/delete")
    public String deleteRequest(@RequestParam int id) {
        try {
            RequestEntity requestEntity = requestRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
    
            // Delete all associated comments first
            commentsRepository.deleteAll(requestEntity.getComments());
    
            // Then delete the request entity
            requestRepo.delete(requestEntity);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/request/list";
    }
    
    @GetMapping("/edit")
    public String showEditRequestPage(@RequestParam int id, Model model) {
        try {
            RequestEntity requestEntity = requestRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
            ReuqesrDto reuqesrDto = new ReuqesrDto();
            reuqesrDto.setDescription(requestEntity.getDescription());
            // If needed, additional data from the entity can be set here
            model.addAttribute("reuqesrDto", reuqesrDto);
            model.addAttribute("requestId", id);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/request/list";
        }
        return "editpage";
    }

    @PostMapping("/edit")
public String updateRequest(@RequestParam int id, @Valid @ModelAttribute ReuqesrDto reuqesrDto, BindingResult result, Model model) {
    if (result.hasErrors()) {
        model.addAttribute("reuqesrDto", reuqesrDto);
        model.addAttribute("requestId", id);
        return "editpage";
    }

    try {
        RequestEntity requestEntity = requestRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));

        // Update the description if it's provided in the DTO
        if (reuqesrDto.getDescription() != null) {
            requestEntity.setDescription(reuqesrDto.getDescription());
        }

        // Update the file names only if new files are uploaded
        if (!reuqesrDto.getFiles().isEmpty()) {
            List<String> fileNames = new ArrayList<>();
            String folderName = requestEntity.getFolderName();
            Path folderPath = Paths.get("uploaded-files", folderName);

            // Create the directory if it doesn't exist
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            for (MultipartFile file : reuqesrDto.getFiles()) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                fileNames.add(fileName);
                try {
                    Path filePath = folderPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save file", e);
                }
            }

            // Set the file names in the request entity
            requestEntity.setFileNames(fileNames);
        }

        requestRepo.save(requestEntity);
    } catch (Exception e) {
        System.out.println("Exception: " + e.getMessage());
        return "redirect:/request/list";
    }

    return "redirect:/request/list";
}









@PostMapping("/create")
public String createRequest(@Valid @ModelAttribute ReuqesrDto reuqesrDto, BindingResult result, Model model, Principal principal) {
    if (reuqesrDto.getFiles().isEmpty()) {
        result.addError(new FieldError("reuqesrDto", "files", "File is required"));
    }
    if (result.hasErrors()) {
        List<CustomerEntity> customers = customerRepo.findAll();
        model.addAttribute("reuqesrDto", reuqesrDto);
        model.addAttribute("customers", customers);
        return "upload";
    }

    String folderName = UUID.randomUUID().toString();
    Path folderPath = Paths.get("uploaded-files", folderName);
    try {
        Files.createDirectories(folderPath);
    } catch (IOException e) {
        throw new RuntimeException("Failed to create directory", e);
    }

    List<String> fileNames = new ArrayList<>();
    for (MultipartFile file : reuqesrDto.getFiles()) {
        if (file.isEmpty()) {
            result.addError(new FieldError("reuqesrDto", "files", "File is required"));
            return "upload";
        }
        try {
            String fileName = file.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            fileNames.add(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    RequestEntity requestEntity = new RequestEntity();
    requestEntity.setDescription(reuqesrDto.getDescription());
    requestEntity.setRequestDate(new Date());
    requestEntity.setFileNames(fileNames);
    requestEntity.setResponse(Response.PENDING);
    requestEntity.setFolderName(folderName);
    requestEntity.setPriorityLevel(reuqesrDto.getPriorityLevel());
    requestEntity.setCases(reuqesrDto.getCases());
    requestEntity.setStartDate(reuqesrDto.getStartDate());
    requestEntity.setEndDate(reuqesrDto.getEndDate());

    String username = principal.getName();
    User currentUser = userRepository.findByEmail(username);
    requestEntity.getUsers().add(currentUser);
    CustomerEntity customer = customerRepo.findById(reuqesrDto.getCustomerId()).orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + reuqesrDto.getCustomerId()));
    requestEntity.setCustomer(customer);

    requestRepo.save(requestEntity);

    return "redirect:/request/list";
}

@GetMapping("/download")
public ResponseEntity<Resource> downloadFolder(@RequestParam String folderName) throws IOException {
    String uploadDir = "uploaded-files/";
    Path folderPath = Paths.get(uploadDir).resolve(folderName).normalize();

    if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
        throw new IllegalArgumentException("Folder not found: " + folderName);
    }

    // Compress the folder into a zip file
    Path zipFilePath = Paths.get(uploadDir).resolve(folderName + ".zip");
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
        Files.walk(folderPath)
             .filter(path -> !Files.isDirectory(path))
             .forEach(path -> {
                 ZipEntry zipEntry = new ZipEntry(folderPath.relativize(path).toString());
                 try {
                     zipOutputStream.putNextEntry(zipEntry);
                     Files.copy(path, zipOutputStream);
                     zipOutputStream.closeEntry();
                 } catch (IOException e) {
                     throw new RuntimeException("Failed to add file to zip: " + path, e);
                 }
             });
    }

    // Prepare the zip file as a resource for download
    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(zipFilePath));

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + folderName + ".zip\"")
            .body(resource);
}
















@PostMapping("/addComment")
public String addComment(@RequestParam("requestId") int requestId,
                         @RequestParam("comment") String commentText,
                         Principal principal) {

    // Retrieve the logged-in user (admin)
    String adminUsername = principal.getName();
    User adminUser = userRepository.findByEmail(adminUsername);

    // Find the RequestEntity by ID
    Optional<RequestEntity> requestOptional = requestRepo.findById(requestId);
    if (requestOptional.isEmpty()) {
        // Handle case where request is not found
        return "redirect:/request/alist";
    }

    // Create a new comment
    Comments comment = new Comments();
    comment.setComments(commentText);
    comment.setUser(adminUser); // Set the admin as the user for the comment
    comment.setRequestEntity(requestOptional.get()); // Set the request entity for the comment

    // Save the comment
    commentsRepository.save(comment);

    return "redirect:/request/alist";
}




@PostMapping("/updateResponse")
public String updateResponse(@RequestParam("requestId") int requestId,
                             @RequestParam("response") Response response) {
    // Find the RequestEntity by ID
    Optional<RequestEntity> requestOptional = requestRepo.findById(requestId);
    if (requestOptional.isEmpty()) {
        // Handle case where request is not found
        return "redirect:/request/alist";
    }

    // Update the response status
    RequestEntity requestEntity = requestOptional.get();
    requestEntity.setResponse(response);
    requestEntity.setResponseDate(Date.from(Instant.now()));
    
      
    requestRepo.save(requestEntity);

    return "redirect:/request/alist";
}


@GetMapping("/searchCustomers")
@ResponseBody
public List<CustomerEntity> searchCustomers(@RequestParam String query) {
    return customerRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);
}
@GetMapping("/customerDetails")
@ResponseBody
public CustomerEntity getCustomerDetails(@RequestParam int customerId) {
    return customerRepo.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Invalid customer Id: " + customerId));
}


@GetMapping("/details")
@ResponseBody
public RequestEntity getRequestDetails(@RequestParam int requestId) {
    return requestRepo.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Invalid request Id: " + requestId));
}


}