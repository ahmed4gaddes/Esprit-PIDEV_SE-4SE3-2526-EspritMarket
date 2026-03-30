package tn.esprit.esprit_market.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FileUploadControllerTest {

    @Test
    void testUploadFile_EmptyFile_ReturnsBadRequest() {
        FileUploadController controller = new FileUploadController();
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);

        ResponseEntity<Map<String, String>> res = controller.uploadFile(emptyFile);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("File is empty", res.getBody().get("error"));
    }

    @Test
    void testUploadFile_NullContentType_ReturnsBadRequest() {
        FileUploadController controller = new FileUploadController();
        MockMultipartFile file = new MockMultipartFile("file", "test.xyz", null, "data".getBytes());

        ResponseEntity<Map<String, String>> res = controller.uploadFile(file);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("Invalid file type", res.getBody().get("error"));
    }

    @Test
    void testUploadFile_InvalidContentType_ReturnsBadRequest() {
        FileUploadController controller = new FileUploadController();
        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "application/octet-stream", "data".getBytes());

        ResponseEntity<Map<String, String>> res = controller.uploadFile(file);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertEquals("Only image/pdf/doc/docx files are allowed", res.getBody().get("error"));
    }

    @Test
    void testUploadFile_ValidImage_ReturnsUrl() {
        FileUploadController controller = new FileUploadController();
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", "fake image data".getBytes());

        ResponseEntity<Map<String, String>> res = controller.uploadFile(file);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody().get("url"));
        assertNotNull(res.getBody().get("filename"));
        assertTrue(res.getBody().get("filename").endsWith(".jpg"));
    }

    @Test
    void testUploadFile_ValidPdf_ReturnsUrl() {
        FileUploadController controller = new FileUploadController();
        MockMultipartFile file = new MockMultipartFile("file", "cv.pdf", "application/pdf", "fake pdf data".getBytes());

        ResponseEntity<Map<String, String>> res = controller.uploadFile(file);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody().get("url"));
    }
}
