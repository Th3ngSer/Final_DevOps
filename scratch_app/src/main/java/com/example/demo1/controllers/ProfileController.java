package com.example.demo1.controllers;

import com.example.demo1.models.Profile;
import com.example.demo1.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    // CRUD: Create (Accepts Profile details and an Image file)
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Profile> createProfile(
            @RequestPart("profile") Profile profile,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try {
            Profile savedProfile = profileService.createProfile(profile, photo);
            return ResponseEntity.ok(savedProfile);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // CRUD: Read All
    @GetMapping
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    // CRUD: Read One
    @GetMapping("/{id}")
    public ResponseEntity<Profile> getProfileById(@PathVariable Long id) {
        return profileService.getProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CRUD: Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return ResponseEntity.ok().build();
    }
}