package com.ecommerce.webapp.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.webapp.exception.StorageException;

@Service
public class StrorageService {
	
	@Value("${upload.path}")
	private String path;
	
	public void uploadFile(MultipartFile file) {
		
		if (file.isEmpty()) {
			throw new StorageException("Failed to store a empty file !");
		}
		// file upload logic
		try {
			String fileName = file.getOriginalFilename();
			InputStream ins = file.getInputStream();
			Files.copy(ins, Paths.get(path+fileName),StandardCopyOption.REPLACE_EXISTING);			
			
		} catch (Exception e) {
			String message= "Failed to upload file : "+file.getName();
			throw new StorageException(message);
		}
	}
	
	public String getFilePath(String filename) {
		return path + filename;
	}
}
