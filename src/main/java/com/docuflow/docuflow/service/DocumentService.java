package com.docuflow.docuflow.service;

import com.docuflow.docuflow.domain.Document;
import com.docuflow.docuflow.repository.DocumentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    public Document save(Document document) {
        return documentRepository.save(document);
    }

    public Document findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다. id=" + id));
    }

    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    public Document update(Long id, Document updatedDocument) {
        Document document = findById(id);
        document.setTitle(updatedDocument.getTitle());
        document.setAuthor(updatedDocument.getAuthor());
        document.setContent(updatedDocument.getContent());
        return documentRepository.save(document);
    }
}

