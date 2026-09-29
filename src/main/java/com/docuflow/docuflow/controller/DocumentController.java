package com.docuflow.docuflow.controller;

import com.docuflow.docuflow.domain.Document;
import com.docuflow.docuflow.domain.DocumentVersion;
import com.docuflow.docuflow.repository.DocumentVersionRepository;
import com.docuflow.docuflow.service.DocumentService;
import com.docuflow.docuflow.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;


@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final FileStorageService fileStorageService;
    private final DocumentVersionRepository documentVersionRepository;

    public DocumentController(DocumentService documentService,
                              FileStorageService fileStorageService,
                              DocumentVersionRepository documentVersionRepository) {
        this.documentService = documentService;
        this.fileStorageService = fileStorageService;
        this.documentVersionRepository = documentVersionRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("documents", documentService.findAll());
        return "documents/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("document", new Document());
        return "documents/form";
    }

    @PostMapping
    public String create(@ModelAttribute Document document) {
        documentService.save(document);
        return "redirect:/documents";
    }

    // 여기부터 새로 추가
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("document", documentService.findById(id));
        List<DocumentVersion> versions =
                documentVersionRepository.findByDocumentIdOrderByVersionNumberDesc(id);
        model.addAttribute("versions", versions);
        return "documents/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("document", documentService.findById(id));
        return "documents/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Document document) {
        documentService.update(id, document);
        return "redirect:/documents/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        documentService.delete(id);
        return "redirect:/documents";
    }

    @PostMapping("/{id}/versions")
    public String uploadVersion(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Document document = documentService.findById(id);

        List<DocumentVersion> existingVersions =
                documentVersionRepository.findByDocumentIdOrderByVersionNumberDesc(id);
        int nextVersionNumber = existingVersions.isEmpty() ? 1 : existingVersions.get(0).getVersionNumber() + 1;

        String storedFileName = fileStorageService.store(file);

        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setVersionNumber(nextVersionNumber);
        version.setOriginalFileName(file.getOriginalFilename());
        version.setStoredFileName(storedFileName);
        documentVersionRepository.save(version);

        return "redirect:/documents/" + id;
    }

    @GetMapping("/versions/{versionId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long versionId) throws MalformedURLException {
        DocumentVersion version = documentVersionRepository.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("버전을 찾을 수 없습니다."));

        Path filePath = fileStorageService.load(version.getStoredFileName());
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + version.getOriginalFileName() + "\"")
                .body(resource);
    }
}