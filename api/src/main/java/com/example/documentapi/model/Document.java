package com.example.documentapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "document")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Document {
    @Id
    @SequenceGenerator(name = "document_id_seq", sequenceName = "document_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_id_seq")
    private Long id;
    private String fileName;
    private String extension;
    private Long fileSize;
    private String filePath;
}
