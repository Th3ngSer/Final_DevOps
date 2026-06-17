package com.example.demo1.services;

import com.example.demo1.models.Profile;
import com.example.demo1.repositories.ProfileRepository;
import com.example.demo1.utils.BarcodeGenerator;
import com.example.demo1.utils.QRCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    // 1. Generate Unique ID (YEAR-DEPT-###)
    private String generateUniqueId(String department) {
        long currentCount = profileRepository.countByDepartment(department) + 1;
        String year = String.valueOf(Year.now().getValue());
        return String.format("%s-%s-%03d", year, department.toUpperCase(), currentCount);
    }

    // 2. Create Profile with Photo, QR, and Barcode
    public Profile createProfile(Profile profile, MultipartFile photo) throws Exception {
        // Set unique ID
        String regNumber = generateUniqueId(profile.getDepartment());
        profile.setRegistrationNumber(regNumber);

        // Handle Photo
        if (photo != null && !photo.isEmpty()) {
            profile.setPhoto(photo.getBytes());
        }

        // QR scanners will show all card details, while the barcode keeps the short ID.
        profile.setQrCode(QRCodeGenerator.generateQRCodeImage(buildQrPayload(profile), 240, 240));
        profile.setBarcode(BarcodeGenerator.generateCode128BarcodeImage(regNumber, 300, 100));

        return profileRepository.save(profile);
    }

    private String buildQrPayload(Profile profile) {
        return String.join("\n",
                "ID CARD INFORMATION",
                "Registration: " + profile.getRegistrationNumber(),
                "Full Name: " + profile.getFullName(),
                "Email: " + profile.getEmail(),
                "Department: " + profile.getDepartment(),
                "Profile Type: " + profile.getProfileType());
    }

    // 3. Read (List all)
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    // 4. Read (Single by ID)
    public Optional<Profile> getProfileById(Long id) {
        return profileRepository.findById(id);
    }

    // 5. Delete Profile
    public void deleteProfile(Long id) {
        profileRepository.deleteById(id);
    }
}