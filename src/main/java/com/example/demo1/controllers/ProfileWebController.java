package com.example.demo1.controllers;

import com.example.demo1.models.Profile;
import com.example.demo1.models.ProfileType;
import com.example.demo1.services.ProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProfileWebController {

    private final ProfileService profileService;

    public ProfileWebController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("profile", new Profile());
        model.addAttribute("profiles", profileService.getAllProfiles());
        model.addAttribute("profileTypes", ProfileType.values());
        return "index";
    }

    @PostMapping("/profiles")
    public String createProfile(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String department,
                                @RequestParam ProfileType profileType,
                                @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                                @RequestParam(value = "photo", required = false) MultipartFile legacyPhoto,
                                Model model) {
        Profile profile = new Profile();
        profile.setFullName(fullName);
        profile.setEmail(email);
        profile.setDepartment(department);
        profile.setProfileType(profileType);

        try {
            MultipartFile photo = photoFile != null ? photoFile : legacyPhoto;
            profileService.createProfile(profile, photo);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("profile", profile);
            model.addAttribute("profiles", profileService.getAllProfiles());
            model.addAttribute("profileTypes", ProfileType.values());
            model.addAttribute("error", "Could not create profile: " + e.getMessage());
            return "index";
        }
    }

    @PostMapping("/profiles/{id}/delete")
    public String deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return "redirect:/";
    }

    @GetMapping("/profiles/{id}/qr")
    public ResponseEntity<byte[]> getQrCode(@PathVariable Long id) {
        return profileService.getProfileById(id)
                .filter(profile -> profile.getQrCode() != null)
                .map(profile -> imageResponse(profile.getQrCode(), MediaType.IMAGE_PNG))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profiles/{id}/barcode")
    public ResponseEntity<byte[]> getBarcode(@PathVariable Long id) {
        return profileService.getProfileById(id)
                .filter(profile -> profile.getBarcode() != null)
                .map(profile -> imageResponse(profile.getBarcode(), MediaType.IMAGE_PNG))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profiles/{id}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        return profileService.getProfileById(id)
                .filter(profile -> profile.getPhoto() != null)
                .map(profile -> imageResponse(profile.getPhoto(), MediaType.IMAGE_JPEG))
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<byte[]> imageResponse(byte[] image, MediaType mediaType) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(mediaType)
                .body(image);
    }
}
