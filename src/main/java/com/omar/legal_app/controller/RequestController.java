package com.omar.legal_app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;

import com.omar.legal_app.entity.RequestEntity;
import com.omar.legal_app.entity.Response;
import com.omar.legal_app.entity.ReuqesrDto;
import com.omar.legal_app.entity.User;
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

    @GetMapping("/list")
    public String showRequestList(Model model, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByEmail(username);
            //.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<RequestEntity> userRequests = requestRepo.findByUsers(currentUser);
        model.addAttribute("request", userRequests);
        return "om"; // Ensure this is the correct view name
    }

    @GetMapping("/alist")
    public String showRequestAllList(Model model) {
       

        List<RequestEntity> userRequests = requestRepo.findAll();
        model.addAttribute("request", userRequests);
        return "request"; // Ensure this is the correct view name
    }
    
    @GetMapping("/create")
    public String showRequest(Model model) {
        ReuqesrDto reuqesrDto = new ReuqesrDto();
        model.addAttribute("reuqesrDto", reuqesrDto);
        return "upload";
    }



    
    // @PostMapping("/create")
    // public String createRequest(@Valid @ModelAttribute ReuqesrDto reuqesrDto, BindingResult result, Model model, Principal principal) {
    //     if (reuqesrDto.getFiles().isEmpty()) {
    //         result.addError(new FieldError("reuqesrDto", "files", "File is required"));
    //     }
    //     if (result.hasErrors()) {
    //         model.addAttribute("reuqesrDto", reuqesrDto);
    //         return "upload";
    //     }
    
    //     // Save each file to the server and collect their filenames
    //     List<String> fileNames = new ArrayList<>();
    //     for (MultipartFile file : reuqesrDto.getFiles()) {
    //         if (file.isEmpty()) {
    //             result.addError(new FieldError("reuqesrDto", "files", "File is required"));
    //             return "upload";
    //         }
    //         try {
    //             String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
    //             Path filePath = Paths.get("uploaded-files").resolve(fileName);
    //             Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    //             fileNames.add(fileName);
    //         } catch (IOException e) {
    //             throw new RuntimeException("Failed to save file", e);
    //         }
    //     }
    
    //     // Create and save the request entity
    //     RequestEntity requestEntity = new RequestEntity();
    //     Date requestDate = new Date();
    //     requestEntity.setDescription(reuqesrDto.getDescription());
    //     requestEntity.setRequestDate(requestDate);
    //     requestEntity.setResponseDate(requestDate);
    //     requestEntity.setFileNames(fileNames);
    //     requestEntity.setResponse(Response.PENDING);
    
    //     // Set the user
    //     String username = principal.getName();
    //     User currentUser = userRepository.findByEmail(username);
    //     requestEntity.getUsers().add(currentUser);
    
    //     requestRepo.save(requestEntity);
    
    //     return "redirect:/request/list";
    // }
    
    @GetMapping("/delete")
    public String deleteRequest(@RequestParam int id) {
        try {
            RequestEntity rEntity = requestRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
            requestRepo.delete(rEntity);
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
            RequestEntity requestEntity = requestRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
            // Update the description
            requestEntity.setDescription(reuqesrDto.getDescription());

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
        model.addAttribute("reuqesrDto", reuqesrDto);
        return "upload";
    }

    String folderName = UUID.randomUUID().toString();
    Path folderPath = Paths.get("uploaded-files", folderName);
    try {
        Files.createDirectories(folderPath);
    } catch (IOException e) {
        throw new RuntimeException("Failed to create directory", e);
    }

    // Save each file to the server and collect their filenames
    List<String> fileNames = new ArrayList<>();
    for (MultipartFile file : reuqesrDto.getFiles()) {
        if (file.isEmpty()) {
            result.addError(new FieldError("reuqesrDto", "files", "File is required"));
            return "upload";
        }
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            fileNames.add(fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    // Create and save the request entity
    RequestEntity requestEntity = new RequestEntity();
    Date requestDate = new Date();
    requestEntity.setDescription(reuqesrDto.getDescription());
    requestEntity.setRequestDate(requestDate);
    requestEntity.setResponseDate(requestDate);
    requestEntity.setFileNames(fileNames);
    requestEntity.setResponse(Response.PENDING);
    requestEntity.setFolderName(folderName);  // Save the folder name

    // Set the user
    String username = principal.getName();
    User currentUser = userRepository.findByEmail(username);
    requestEntity.getUsers().add(currentUser);

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

}