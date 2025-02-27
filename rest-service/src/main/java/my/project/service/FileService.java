package my.project.service;

import my.project.entity.AppDocument;
import my.project.entity.AppPhoto;
import my.project.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {

	AppDocument getDocument(String id);

	AppPhoto getPhoto(String id);

	FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
