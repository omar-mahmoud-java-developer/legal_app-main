package com.omar.legal_app.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
        requestEntity.setFileName(reuqesrDto.getFile().getOriginalFilename());

        // Set the user
        String username = principal.getName();
        User currentUser = userRepository.findByEmail(username);
           // .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        requestEntity.getUsers().add(currentUser);

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
}
