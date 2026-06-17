package com.example.demo1.models;

import jakarta.persistence.*;

@Entity
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String registrationNumber;
    
    private String fullName;
    private String email;
    private String department;
    
    @Enumerated(EnumType.STRING)
    private ProfileType profileType;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo; // Storing photo directly in database as BLOB
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] qrCode; // Storing generated QR code
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] barcode; // Storing generated Barcode

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public byte[] getQrCode() {
        return qrCode;
    }

    public void setQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }

    public byte[] getBarcode() {
        return barcode;
    }

    public void setBarcode(byte[] barcode) {
        this.barcode = barcode;
    }
}