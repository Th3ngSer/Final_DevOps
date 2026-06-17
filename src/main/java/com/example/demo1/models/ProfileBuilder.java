package com.example.demo1.models;

import java.util.UUID;

public class ProfileBuilder {
    private Profile profile = new Profile();

    public ProfileBuilder createDefault(String name, String email, ProfileType type, String department) {
        profile.setFullName(name);
        profile.setEmail(email);
        profile.setProfileType(type);
        profile.setDepartment(department);
        // Temporary ID, usually overwritten by the Service layer custom sequence
        profile.setRegistrationNumber("TEMP-" + UUID.randomUUID().toString().substring(0, 8));
        return this;
    }

    public Profile build() {
        return this.profile;
    }
}