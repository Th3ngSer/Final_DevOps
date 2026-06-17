package com.example.demo1.controllers;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
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

import java.awt.Color;
import java.io.ByteArrayOutputStream;

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

    @PostMapping("/profiles/{id}/update")
    public String updateProfile(@PathVariable Long id,
                                @RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String department,
                                @RequestParam ProfileType profileType,
                                @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                                Model model) {
        try {
            profileService.updateProfile(id, fullName, email, department, profileType, photoFile);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("profile", new Profile());
            model.addAttribute("profiles", profileService.getAllProfiles());
            model.addAttribute("profileTypes", ProfileType.values());
            model.addAttribute("error", "Could not update profile: " + e.getMessage());
            return "index";
        }
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

    @GetMapping("/profiles/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        return profileService.getProfileById(id)
                .map(profile -> {
                    byte[] pdf = buildProfilePdf(profile);
                    String filename = "id-card-" + profile.getRegistrationNumber() + ".pdf";
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                            .contentType(MediaType.APPLICATION_PDF)
                            .body(pdf);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<byte[]> imageResponse(byte[] image, MediaType mediaType) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(mediaType)
                .body(image);
    }

    private byte[] buildProfilePdf(Profile profile) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 42, 42, 42, 42);

        try {
            PdfWriter.getInstance(document, output);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, Color.BLACK);
            Font labelFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.DARK_GRAY);
            Font valueFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);

            Paragraph title = new Paragraph("ID Card Detail", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable card = new PdfPTable(2);
            card.setWidthPercentage(100);
            card.setWidths(new float[]{1.2f, 2.8f});
            card.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

            PdfPCell photoCell = new PdfPCell();
            photoCell.setBorder(PdfPCell.NO_BORDER);
            photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            if (profile.getPhoto() != null) {
                Image photo = Image.getInstance(profile.getPhoto());
                photo.scaleToFit(110, 110);
                photoCell.addElement(photo);
            } else {
                photoCell.addElement(new Paragraph("No Photo", valueFont));
            }
            card.addCell(photoCell);

            PdfPCell detailCell = new PdfPCell();
            detailCell.setBorder(PdfPCell.NO_BORDER);
            detailCell.addElement(new Paragraph(profile.getFullName(), new Font(Font.HELVETICA, 18, Font.BOLD)));
            detailCell.addElement(new Paragraph("Registration: " + profile.getRegistrationNumber(), valueFont));
            detailCell.addElement(new Paragraph("Profile Type: " + profile.getProfileType(), valueFont));
            detailCell.addElement(new Paragraph("Department: " + profile.getDepartment(), valueFont));
            detailCell.addElement(new Paragraph("Email: " + profile.getEmail(), valueFont));
            card.addCell(detailCell);
            document.add(card);

            PdfPTable codeTable = new PdfPTable(2);
            codeTable.setWidthPercentage(100);
            codeTable.setSpacingBefore(28);
            codeTable.setWidths(new float[]{1f, 1f});

            codeTable.addCell(codeCell("QR Code", profile.getQrCode(), labelFont));
            codeTable.addCell(codeCell("Barcode", profile.getBarcode(), labelFont));
            document.add(codeTable);

            Paragraph note = new Paragraph("Scan the QR code to view this card's profile information.", valueFont);
            note.setSpacingBefore(18);
            note.setAlignment(Element.ALIGN_CENTER);
            document.add(note);
        } catch (Exception e) {
            throw new IllegalStateException("Could not build PDF", e);
        } finally {
            document.close();
        }

        return output.toByteArray();
    }

    private PdfPCell codeCell(String label, byte[] imageBytes, Font labelFont) throws Exception {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(14);
        cell.addElement(new Phrase(label, labelFont));

        if (imageBytes != null) {
            Image image = Image.getInstance(imageBytes);
            image.scaleToFit(170, 90);
            image.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(image);
        }

        return cell;
    }
}
