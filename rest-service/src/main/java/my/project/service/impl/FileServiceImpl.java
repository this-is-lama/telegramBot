package my.project.service.impl;

import lombok.extern.slf4j.Slf4j;
import my.project.dao.AppDocumentDAO;
import my.project.dao.AppPhotoDAO;
import my.project.entity.AppDocument;
import my.project.entity.AppPhoto;
import my.project.entity.BinaryContent;
import my.project.service.FileService;
import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
	private final AppDocumentDAO appDocumentDAO;
	private final AppPhotoDAO appPhotoDAO;

	public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO) {
		this.appDocumentDAO = appDocumentDAO;
		this.appPhotoDAO = appPhotoDAO;
	}

	@Override
	public AppDocument getDocument(String docId) {
		//TODO добавить дешифрование
		var id = Long.parseLong(docId);
		return appDocumentDAO.findById(id).orElse(null);
	}

	@Override
	public AppPhoto getPhoto(String photoId) {
		var id = Long.parseLong(photoId);
		return appPhotoDAO.findById(id).orElse(null);
	}

	@Override
	public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
		try {
			//TODO добавить генерацию случайных названий файлов
			File temp = File.createTempFile("tempFile", ".bin");
			temp.deleteOnExit();
			FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
			return new FileSystemResource(temp);
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}
	}
}
