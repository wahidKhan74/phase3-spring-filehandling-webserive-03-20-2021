package com.ecommerce.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.webapp.exception.StorageException;
import com.ecommerce.webapp.service.StrorageService;

@Controller
public class FileController {

	@Autowired
	StrorageService storageService;
	
	
	@RequestMapping("/")
	public String index() {
		return "index.html";
	}
	
	@RequestMapping(value="/do-upload" , method=RequestMethod.POST, consumes= {"multipart/form-data"})
	public String upload(@RequestParam MultipartFile file) {
		storageService.uploadFile(file);
		return "redirect:/success.html";
	}
	
	@ExceptionHandler(StorageException.class)
	public String handleStorageException() {
		return  "redirect:/failure.html";
	}
	
	@RequestMapping(value="/download" , method=RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadFile(@RequestParam(value="filename",required=true) String filename) {
		
		File file ;
		InputStreamResource resource;
		
		try {
			file = new File(storageService.getFilePath(filename));
			resource = new InputStreamResource(new FileInputStream(file));
			
		} catch (FileNotFoundException e) {
			String message= "Failed to download file : "+filename;
			throw new StorageException(message);
		}
		
		// create http header meta info for download
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		
		ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.ok().headers(headers)
				.contentLength(file.length()).contentType(MediaType.parseMediaType("application/text"))
				.body(resource);
		return responseEntity;
	}
}

