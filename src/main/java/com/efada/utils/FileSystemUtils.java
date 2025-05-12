package com.efada.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileSystemUtils {

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

}
