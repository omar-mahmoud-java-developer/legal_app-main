package com.omar.legal_app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

    @PostMapping("/create")
    public String createRequest(@Valid @ModelAttribute ReuqesrDto reuqesrDto, BindingResult result, Model model, Principal principal) {
        if (reuqesrDto.getFile().isEmpty()) {
            result.addError(new FieldError("reuqesrDto", "file", "File is required"));
        }
        if (result.hasErrors()) {
            model.addAttribute("reuqesrDto", reuqesrDto);
            return "upload";
        }

        RequestEntity requestEntity = new RequestEntity();
        Date rDate = new Date();
        requestEntity.setDescription(reuqesrDto.getDescription());
        requestEntity.setRequestDate(rDate);
        requestEntity.setResponseDate(rDate);
        requestEntity.setFileName(reuqesrDto.getFile().getOriginalFilename());
        requestEntity.setResponse(Response.PENDING);

        // Set the user
        String username = principal.getName();
        User currentUser = userRepository.findByEmail(username);
           // .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        requestEntity.getUsers().add(currentUser);

        
 // Save the file to the server
       String uploadDir = "uploaded-files/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory!", e);
            }
        }

        try {
            Path filePath = uploadPath.resolve(reuqesrDto.getFile().getOriginalFilename());
            Files.copy(reuqesrDto.getFile().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        requestRepo.save(requestEntity);

        return "redirect:/request/list";
    }

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
            // Additional data can be set here if needed

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

            // Update the file name only if a new file is uploaded
            if (!reuqesrDto.getFile().isEmpty()) {
                requestEntity.setFileName(reuqesrDto.getFile().getOriginalFilename());
            }

            requestRepo.save(requestEntity);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return "redirect:/request/list";
        }

        return "redirect:/request/list";
    }













    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileName) {
        String uploadDir = "uploaded-files/";
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("File not found " + fileName, e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

















}
