package my.project.contoller;

import lombok.extern.slf4j.Slf4j;
import my.project.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@GetMapping("/get-doc")
	public ResponseEntity<?> getDoc(@RequestParam("id") String id) {
		//TODO добавить контроллер для обработки ошибок
		var doc = fileService.getDocument(id);
		if (doc == null) {
			return ResponseEntity.badRequest().build();
		}
		var binaryContent = doc.getBinaryContent();
		var fileSystemResource = fileService.getFileSystemResource(binaryContent);
		if (fileSystemResource == null) {
			return ResponseEntity.internalServerError().build();
		}
		return ResponseEntity.ok().
				contentType(MediaType.parseMediaType(doc.getMimeType()))
				.header("Content-Disposition", "attachment; filename=" + doc.getDocName())
				.body(fileSystemResource);
	}

	@GetMapping("/get-photo")
	public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
		//TODO добавить контроллер для обработки ошибок
		var photo = fileService.getPhoto(id);
		if (photo == null) {
			return ResponseEntity.badRequest().build();
		}
		var binaryContent = photo.getBinaryContent();
		var fileSystemResource = fileService.getFileSystemResource(binaryContent);
		if (fileSystemResource == null) {
			return ResponseEntity.internalServerError().build();
		}
		return ResponseEntity.ok().
				contentType(MediaType.IMAGE_JPEG)
				.header("Content-Disposition", "attachment")
				.body(fileSystemResource);
	}
}
