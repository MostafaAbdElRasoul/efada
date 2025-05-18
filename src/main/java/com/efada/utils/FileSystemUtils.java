package com.efada.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileSystemUtils {

	@Value("${attachment.path}")
	private String attachmentsPath;
	
	@Value("${errorLog.path}")
	private String errorLogPath;
	
	public String createErrorLogFile(Exception exception, Long key) {
		try {
		String fileName = constructExceptionFileName(key);
		System.out.println("fileName : "+fileName);
		System.out.println("errorLogPath : "+errorLogPath);
		File errorLogDirectory = new File(errorLogPath);
		if(!errorLogDirectory.exists())
			errorLogDirectory.mkdirs();
		Path file = Paths.get(errorLogPath+"/"+fileName+".txt");
		Files.write(file, getStackTraceLines(exception), StandardCharsets.UTF_8);
		return fileName;
		} catch(Exception ex ) {
			ex.printStackTrace();
		}
		return null;
	}

	private String constructExceptionFileName(Long key) {
		String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
		return timestamp +"__"+ key;
	}
	
	private List<String> getStackTraceLines(Exception ex) {
	    List<String> stackTraceLines = new ArrayList<>();

	    // Add root exception info
	    stackTraceLines.add(ex.getClass().getName() + ": " + ex.getMessage());
	    for (StackTraceElement element : ex.getStackTrace()) {
	        stackTraceLines.add("\tat " + element.toString());
	    }

	    // Add causes, if any
	    Throwable cause = ex.getCause();
	    while (cause != null) {
	        stackTraceLines.add("Caused by: " + cause.getClass().getName() + ": " + cause.getMessage());
	        for (StackTraceElement element : cause.getStackTrace()) {
	            stackTraceLines.add("\tat " + element.toString());
	        }
	        cause = cause.getCause();
	    }

	    return stackTraceLines;
	}
	
	public void saveToFileSystem(MultipartFile file , String fileName) throws IOException {
		InputStream stream = file.getInputStream();		
		Path path = getFilePath(attachmentsPath);
		Files.copy(stream, path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
	}
	
	private Path getFilePath(String attachmentsPath)
	{
		System.out.println("filePath : "+attachmentsPath);
		File filePath = new File(attachmentsPath);
		if(!filePath.exists())
			filePath.mkdirs();
		Path path = Paths.get(attachmentsPath);
		return path;
	}
	
	public void deleteFile(String fileName) throws IOException 
	{
		Files.deleteIfExists(getFilePath(attachmentsPath).resolve(fileName));
	}
	
	public byte [] getFileBytes(String fileName) throws IOException
	{
		
		String location = attachmentsPath+"/"+fileName;
		File file = new File(location);
		return Files.readAllBytes(file.toPath());
	}
	

	public  String getFileExtension(MultipartFile file) {
		if (!file.getContentType().startsWith("image/")) {
		    throw new IllegalArgumentException("INVALID_FILE_TYPE");
		}
		String originalFileName = file.getOriginalFilename();
		String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
		return fileExtension;
	}
}
