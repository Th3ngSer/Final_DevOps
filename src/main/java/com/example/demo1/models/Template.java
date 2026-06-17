package com.example.demo1.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
}