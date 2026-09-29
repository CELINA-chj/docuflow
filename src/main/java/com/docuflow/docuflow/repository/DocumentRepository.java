package com.docuflow.docuflow.repository;

import com.docuflow.docuflow.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}