package com.example.task1.controller;

import com.example.task1.service.GPTChatService;
import com.example.task1.service.StorageService;
import com.example.task1.service.TextExtractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ResumeComparisonController {

    private final StorageService storageService;
    private final TextExtractService textExtractService;
    private final GPTChatService gptChatService;

    @Autowired
    public ResumeComparisonController(StorageService storageService, TextExtractService textExtractService,
                                      GPTChatService gptChatService) {
        this.storageService = storageService;
        this.textExtractService = textExtractService;
        this.gptChatService = gptChatService;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("message", "Resumé Compare!");
        return "home";
    }

    @GetMapping("/compare")
    public String showJobListingPage(Model model) {
        model.addAttribute("header", "Upload two Resumé for comparison!");
        model.addAttribute("content", storageService.getJobListing());
        if (storageService.loadAll().findAny().isPresent()) {
            model.addAttribute("files", storageService.loadAll().map(
                            path -> MvcUriComponentsBuilder.fromMethodName(ResumeComparisonController.class,
                                    "showFile", path.getFileName().toString()).build().toUri().toString())
                    .collect(Collectors.toList()));
        }

        return "compare";
    }

    @GetMapping("/compare/{filename:.+}")
    @ResponseBody
    public ResponseEntity<String> showFile(@PathVariable String filename) throws IOException {
        File file = storageService.loadAsFile(filename);
        String content = textExtractService.extractText(file);
        content = content.replace("\n", "<br>");

        return ResponseEntity.ok().body(content);
    }

    @PostMapping(value = "/uploadJob", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String extractTextFromJobListing(@RequestParam("file") MultipartFile file,
                              Model model) throws IOException {
        if (storageService.loadAll().findAny().isPresent()) {
            storageService.deleteAll();
            storageService.init();
        }

        String text = textExtractService.extractText(file);
        text = text.replace("\n", "<br>");
        model.addAttribute("content", text);

        storageService.setJobListing(text);

        return "redirect:/compare";
    }

    @PostMapping("/uploadResume")
    public String uploadTwoResume(@RequestParam("file") MultipartFile[] files) throws IOException {
        if (files.length != 2) {
            throw new IOException("Please upload exactly 2 files");
        }
        storageService.store(files[0]);
        storageService.store(files[1]);

        return "redirect:/compare";
    }

    @PostMapping("/compare")
    public String compareTwoResume(RedirectAttributes redirectAttributes) throws IOException {
        List<Path> files = storageService.loadAll().toList();

        File file1 = storageService.loadAsFile(files.getFirst().toString());
        String resume1 = textExtractService.extractText(file1);

        File file2 = storageService.loadAsFile(files.get(1).toString());
        String resume2 = textExtractService.extractText(file2);

        String response = gptChatService.getGPTResponseFromComparingTwoResume(resume1, resume2,
                storageService.getJobListing());

        redirectAttributes.addFlashAttribute("score", response);

        return "redirect:/compare";
    }
}
