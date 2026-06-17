package com.example.demo1.repositories;

import com.example.demo1.models.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    boolean existsByName(String name);
}