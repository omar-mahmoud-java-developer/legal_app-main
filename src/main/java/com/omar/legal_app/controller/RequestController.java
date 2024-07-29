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
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.omar.legal_app.dto.DetilesDto;
import com.omar.legal_app.dto.RequestDto;
import com.omar.legal_app.dto.Response;
import com.omar.legal_app.entity.Comments;
import com.omar.legal_app.entity.CustomerEntity;
import com.omar.legal_app.entity.RequestDetails;
import com.omar.legal_app.entity.RequestEntity;
import com.omar.legal_app.entity.User;
import com.omar.legal_app.repository.CommentsRepository;
import com.omar.legal_app.repository.CustomerRepo;
import com.omar.legal_app.repository.DetilesRepo;
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
    private CustomerRepo customerRepo;
    
    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private DetilesRepo detailsRepo;

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
        RequestDto requestDto = new RequestDto();
        List<CustomerEntity> customers = customerRepo.findAll();
        model.addAttribute("requestDto", requestDto); // Ensure this line is present
        model.addAttribute("customers", customers);
        return "upload"; // Ensure this matches your Thymeleaf template name
    }

    @GetMapping("/delete")
    public String deleteRequest(@RequestParam int id) {
        try {
            RequestEntity requestEntity = requestRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
            commentsRepository.deleteAll(requestEntity.getComments());
            requestRepo.delete(requestEntity);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/request/list";
    }



    @PostMapping("/create")
    public String createRequest(@Valid @ModelAttribute("requestDto") RequestDto requestDto, BindingResult result,
                                @RequestParam("files") MultipartFile[] files, Model model, Principal principal) {
        // Check if files are empty
        if (files.length == 0 || (files.length == 1 && files[0].isEmpty())) {
            result.addError(new FieldError("requestDto", "files", "File is required"));
        }

        // Log any binding errors
        if (result.hasErrors()) {
            List<CustomerEntity> customers = customerRepo.findAll();
            model.addAttribute("requestDto", requestDto);
            model.addAttribute("customers", customers);
            for (FieldError error : result.getFieldErrors()) {
                System.out.println("Field Error: " + error.getField() + " - " + error.getDefaultMessage());
            }
            return "upload";
        }

        // Create the directory to store files
        String folderName = UUID.randomUUID().toString();
        Path folderPath = Paths.get("uploaded-files", folderName);
        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            System.out.println("Failed to create directory: " + e.getMessage());
            throw new RuntimeException("Failed to create directory", e);
        }

        // Save each file
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String fileName = file.getOriginalFilename();
                    Path filePath = folderPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    fileNames.add(fileName);
                } catch (IOException e) {
                    System.out.println("Failed to save file: " + e.getMessage());
                    throw new RuntimeException("Failed to save file", e);
                }
            }
        }

        // Create the request entity
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setDescription(requestDto.getDescription());
        requestEntity.setRequestDate(new Date());
        requestEntity.setFileNames(fileNames);
        requestEntity.setResponse(Response.PENDING);
        requestEntity.setFolderName(folderName);

        // Get current user
        String username = principal.getName();
        User currentUser = userRepository.findByEmail(username);
        requestEntity.getUsers().add(currentUser);

        // Find the customer
        CustomerEntity customer = customerRepo.findById(requestDto.getCustomerId())
                                              .orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + requestDto.getCustomerId()));
        requestEntity.setCustomer(customer);

        // Save the request entity
        requestEntity = requestRepo.save(requestEntity);

        // Save request details if any
        if (requestDto.getDetails() != null && !requestDto.getDetails().isEmpty()) {
            for (DetilesDto detail : requestDto.getDetails()) {
              RequestDetails requestDetails = new RequestDetails();
            requestDetails.setText(detail.getText());
            requestDetails.setCases(detail.getCases());
            requestDetails.setPriorityLevel(detail.getPriorityLevel());
            requestDetails.setStartDate(detail.getStartDate());
            requestDetails.setEndDate(detail.getEndDate());
            requestDetails.setRequestEntity(requestEntity);
            detailsRepo.save(requestDetails);
            }
        }

        return "redirect:/request/list";
    }
     @GetMapping("/details/{id}")
    public String showRequestDetails(@PathVariable int id, Model model) {
        RequestEntity requestEntity = requestRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
        model.addAttribute("request", requestEntity);
        return "requestDetails";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFolder(@RequestParam String folderName) throws IOException {
        String uploadDir = "uploaded-files/";
        Path folderPath = Paths.get(uploadDir).resolve(folderName).normalize();
        if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
            throw new IllegalArgumentException("Folder not found: " + folderName);
        }
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
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(zipFilePath));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + folderName + ".zip\"")
                .body(resource);
    }

    @PostMapping("/addComment")
    public String addComment(@RequestParam("requestId") int requestId, @RequestParam("comment") String commentText, Principal principal) {
        String adminUsername = principal.getName();
        User adminUser = userRepository.findByEmail(adminUsername);
        Optional<RequestEntity> requestOptional = requestRepo.findById(requestId);
        if (requestOptional.isEmpty()) {
            return "redirect:/request/alist";
        }
        Comments comment = new Comments();
        comment.setComments(commentText);
        comment.setUser(adminUser);
        comment.setRequestEntity(requestOptional.get());
        commentsRepository.save(comment);
        return "redirect:/request/alist";
    }

    @PostMapping("/updateResponse")
    public String updateResponse(@RequestParam("requestId") int requestId, @RequestParam("response") Response response) {
        Optional<RequestEntity> requestOptional = requestRepo.findById(requestId);
        if (requestOptional.isEmpty()) {
            return "redirect:/request/alist";
        }
        RequestEntity requestEntity = requestOptional.get();
        requestEntity.setResponse(response);
        requestEntity.setResponseDate(Date.from(Instant.now()));
        requestRepo.save(requestEntity);
        return "redirect:/request/alist";
    }

    @GetMapping("/searchCustomers")
    @ResponseBody
    public List<CustomerEntity> searchCustomers(@RequestParam String query) {
        return customerRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrCompanyContainingIgnoreCase(query, query, query, query);
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







    @GetMapping("/edit")
public String showEditForm(@RequestParam int id, Model model) {
    try {
        RequestEntity requestEntity = requestRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
        RequestDto requestDto = convertToDto(requestEntity);
        requestDto.setId(id); // Ensure the id is set in the DTO
        model.addAttribute("requestDto", requestDto);
    } catch (Exception e) {
        System.out.println("Exception: " + e.getMessage());
        return "redirect:/request/list";
    }
    return "editpage"; // Ensure this matches your Thymeleaf template name
}


    // Method to handle the form submission
    @PostMapping("/edit")
    public String editRequest(@Valid @ModelAttribute("requestDto") RequestDto requestDto, BindingResult result,
                              @RequestParam("files") MultipartFile[] files, Model model, Principal principal) {
        if (result.hasErrors()) {
            model.addAttribute("requestDto", requestDto);
            return "editDetails";
        }
    
        Optional<RequestEntity> optionalRequestEntity = requestRepo.findById(requestDto.getId());
        if (optionalRequestEntity.isEmpty()) {
            throw new IllegalArgumentException("Invalid request Id: " + requestDto.getId());
        }
    
        RequestEntity requestEntity = optionalRequestEntity.get();
        requestEntity.setDescription(requestDto.getDescription());
        requestEntity.setFolderName(requestDto.getFolderName());
    
        if (files != null && files.length > 0 && !(files.length == 1 && files[0].isEmpty())) {
            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = file.getOriginalFilename();
                        Path filePath = Paths.get("uploaded-files", requestEntity.getFolderName(), fileName);
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                        fileNames.add(fileName);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to save file", e);
                    }
                }
            }
            requestEntity.setFileNames(fileNames);
        }
    
        if (requestDto.getDetails() != null && !requestDto.getDetails().isEmpty()) {
            detailsRepo.deleteAll(requestEntity.getRequestDetails());
            for (DetilesDto detail : requestDto.getDetails()) {
                RequestDetails requestDetails = new RequestDetails();
                requestDetails.setText(detail.getText());
                requestDetails.setCases(detail.getCases());
                requestDetails.setPriorityLevel(detail.getPriorityLevel());
                requestDetails.setStartDate(detail.getStartDate());
                requestDetails.setEndDate(detail.getEndDate());
                requestDetails.setRequestEntity(requestEntity);
                detailsRepo.save(requestDetails);
            }
        }
    
        requestRepo.save(requestEntity);
        return "redirect:/request/list";
    }
    


    private RequestDto convertToDto(RequestEntity requestEntity) {
        RequestDto requestDto = new RequestDto();
  
        requestDto.setDescription(requestEntity.getDescription());
        requestDto.setFolderName(requestEntity.getFolderName());
   

        List<DetilesDto> details = requestEntity.getRequestDetails().stream()
                .map(this::convertToDetailDto)
                .collect(Collectors.toList());
        requestDto.setDetails(details);

        return requestDto;
    }

    private DetilesDto convertToDetailDto(RequestDetails requestDetails) {
        DetilesDto detailDto = new DetilesDto();
        detailDto.setText(requestDetails.getText());
        detailDto.setCases(requestDetails.getCases());
        detailDto.setPriorityLevel(requestDetails.getPriorityLevel());
        detailDto.setStartDate(requestDetails.getStartDate());
        detailDto.setEndDate(requestDetails.getEndDate());
        return detailDto;
    }
}