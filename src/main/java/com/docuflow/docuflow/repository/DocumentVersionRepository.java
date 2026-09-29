package com.docuflow.docuflow.repository;

import com.docuflow.docuflow.domain.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    List<DocumentVersion> findByDocumentIdOrderByVersionNumberDesc(Long documentId);
}