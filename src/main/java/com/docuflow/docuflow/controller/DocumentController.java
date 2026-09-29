package com.docuflow.docuflow.controller;

import com.docuflow.docuflow.domain.Document;
import com.docuflow.docuflow.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
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
}