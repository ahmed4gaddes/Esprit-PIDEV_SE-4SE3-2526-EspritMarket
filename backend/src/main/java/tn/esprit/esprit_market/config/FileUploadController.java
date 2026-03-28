package tn.esprit.esprit_market.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    // Upload directory (relative to project root)
    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    public FileUploadController() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            // Validate file type (images + common document formats for CV uploads)
            String contentType = file.getContentType();
            if (contentType == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid file type"));
            }
            boolean isImage = contentType.startsWith("image/");
            boolean isPdf = "application/pdf".equals(contentType);
            boolean isWord = "application/msword".equals(contentType)
                    || "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType);
            if (!isImage && !isPdf && !isWord) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only image/pdf/doc/docx files are allowed"));
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // Save file
            Path targetPath = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Return the URL to access the file
            String fileUrl = buildPublicUploadUrl(newFilename);

            return ResponseEntity.ok(Map.of(
                    "url", fileUrl,
                    "filename", newFilename));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    private String buildPublicUploadUrl(String filename) {
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            return "/uploads/" + filename;
        }
        return publicBaseUrl.replaceAll("/+$", "") + "/uploads/" + filename;
    }
}
