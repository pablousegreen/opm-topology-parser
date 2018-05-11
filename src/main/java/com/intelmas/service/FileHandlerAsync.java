package com.intelmas.service;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class FileHandlerAsync {

	private static final Logger LOG = LoggerFactory.getLogger(FileProcessor.class);
	
	private int maxRetry = 1000;
	
	@Autowired
	@Qualifier("arneParser")
	private TopologyParserService arneParser;
	
	@Autowired
	@Qualifier("fqdnParser")
	private TopologyParserService fqdnParser;
	
	@Autowired
	@Qualifier("remedyParser")
	private TopologyParserService remedyParser;
	
	@Async
	public void processFile(Message<File> message){
		
		
		File originalFile = message.getPayload();
		if(originalFile == null || originalFile.length() == 0){
			if(originalFile.length() == 0){
//				processFileAfter(originalFile, true);
				LOG.info("File is empty, remove the file");
			}
			return;
		}
		
		processFile(originalFile);
	}
	
	@Async
	public void processFile(File originalFile){
		
		// Check if file has finished being transferred
		long fileLength = 0L;
		int retry = 0;
		
		while(retry++ < maxRetry){
			
			if(originalFile.length() == fileLength) break;
			
			//file has not yet finished transferred. Wait for another one second
			fileLength = originalFile.length();
			try { Thread.sleep(100); } catch (InterruptedException e) {}
		}
		
		String ext1 = FilenameUtils.getExtension(originalFile.getAbsolutePath());
		
		switch(ext1){
		case "csv":
			System.out.println("CSV");
			remedyParser.parser(originalFile);
			break;
		case "xml":
			System.out.println("XML");
			arneParser.parser(originalFile);
			break;
		case "txt":
			System.out.println("TXT");
			fqdnParser.parser(originalFile);
			break;
		}
		
//		processFileAfter(originalFile, true);
	}
	
	
}
