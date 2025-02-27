package my.project.service.impl;



import lombok.extern.slf4j.Slf4j;
import my.project.dao.AppDocumentDAO;
import my.project.dao.BinaryContentDAO;
import my.project.entity.enums.AppDocument;
import my.project.entity.enums.BinaryContent;
import my.project.exceptions.UploadFileException;
import my.project.service.FileService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
	@Value("${token}")
	private String token;
	@Value("${service.file_info.uri}")
	private String fileInfoUri;
	@Value("${service.file_storage.uri}")
	private String fileStorageUri;
	private final AppDocumentDAO appDocumentDAO;
	private final BinaryContentDAO binaryContentDAO;

	public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO) {
		this.appDocumentDAO = appDocumentDAO;
		this.binaryContentDAO = binaryContentDAO;
	}


	public AppDocument processDoc(Message telegramMessage) {
		String fileId = telegramMessage.getDocument().getFileId();
		ResponseEntity<String> response = getFilePath(fileId);
		if (response.getStatusCode() == HttpStatus.OK) {
			JSONObject jsonObject = new JSONObject(response.getBody());
			String filePath = String.valueOf(jsonObject
					.getJSONObject("result")
					.getString("file_path"));
			byte[] fileInByte = downloadFile(filePath);
			BinaryContent transientBinaryContent = BinaryContent.builder()
					.fileAsArrayOfBytes(fileInByte)
					.build();
			BinaryContent persistentBinaryContent = binaryContentDAO.save(transientBinaryContent);
			Document telegramDoc = telegramMessage.getDocument();
			AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
			return appDocumentDAO.save(transientAppDoc);
		} else {
			throw new UploadFileException("Bad response from telegram service: " + response);
		}
	}

	private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
		return AppDocument.builder()
				.telegramFileId(telegramDoc.getFileId())
				.docName(telegramDoc.getFileName())
				.binaryContent(persistentBinaryContent)
				.mimeType(telegramDoc.getMimeType())
				.fileSize(telegramDoc.getFileSize())
				.build();
	}

	private ResponseEntity<String> getFilePath(String fileId) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> request = new HttpEntity<>(headers);

		return restTemplate.exchange(
				fileInfoUri,
				HttpMethod.GET,
				request,
				String.class,
				token, fileId
		);
	}

	private byte[] downloadFile(String filePath) {
		String fullUri = fileStorageUri.replace("{token}", token)
				.replace("{filePath}", filePath);
		URL urlObj;
		try {
			urlObj = new URL(fullUri);
		} catch (MalformedURLException e) {
			throw new UploadFileException(e);
		}

		//TODO подумать над оптимизацией
		try (InputStream is = urlObj.openStream()) {
			return is.readAllBytes();
		} catch (IOException e) {
			throw new UploadFileException(urlObj.toExternalForm(), e);
		}
	}


}
