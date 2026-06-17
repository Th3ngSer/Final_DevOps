package com.example.demo1.repositories;

import com.example.demo1.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumber(String registrationNumber);
    long countByDepartment(String department);
}