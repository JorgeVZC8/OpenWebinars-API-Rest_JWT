package com.example.ApiRestSecurity.note.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String content;
    private String author;
    private boolean important;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
