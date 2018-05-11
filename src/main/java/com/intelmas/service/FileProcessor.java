package com.intelmas.service;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class FileProcessor {
	
	private static final Logger LOG = LoggerFactory.getLogger(FileProcessor.class);
	
	protected DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
	
	
	@Autowired
	private FileHandlerAsync handler;
	
	private int maxRetry = 1000;
	
	@ServiceActivator(inputChannel = "processFileChannel", outputChannel="afterProcessFileChannel")
    public void processFileChannel(Message<File> message) {
		processFile(message);
    }
	
	protected Pattern getFilePattern(){
		Pattern pattern = Pattern.compile(".+(\\.xml|\\.zip|\\.csv|\\.txt)");
		return pattern;
	}
	
	@Async
	public void processFile(Message<File> message){
		
		
		
		
		File originalFile = message.getPayload();
		if(originalFile == null || originalFile.length() == 0){
			if(originalFile.length() == 0){
				processFileAfter(originalFile, true);
				LOG.info("File is empty, remove the file");
			}
			return;
		}
		
		handler.processFile(message);
//		this.processFile(originalFile);
		
	}
	
//	@Async
//	public void processFile(File originalFile){
//		Instant startTime = Instant.now();
//		
//		// Check if file has finished being transferred
//		long fileLength = 0L;
//		int retry = 0;
//		
//		while(retry++ < maxRetry){
//			
//			if(originalFile.length() == fileLength) break;
//			
//			//file has not yet finished transferred. Wait for another one second
//			fileLength = originalFile.length();
//			try { Thread.sleep(1000); } catch (InterruptedException e) {}
//		}
//		
//		if(StringUtils.endsWith( originalFile.getName(), ".zip")){
//			// process zip file
//			try(ZipInputStream zis =
//		    		new ZipInputStream(new FileInputStream(originalFile));){
//				ZipEntry ze = zis.getNextEntry();
//				while(ze != null){
//					String fileName = ze.getName();
//					byte[] fileContent = IOUtils.toByteArray(zis);
//					
//					processByteArray(startTime, originalFile.getParent() + File.separator + fileName, fileContent);
//					
//					ze = zis.getNextEntry();
//				}
//				
//			}catch(Exception e){ 
//				LOG.error("Error processing ZIP file [Exception:{}]", e.toString());
//				return;
//			}
//		}else{
//			// process xml file
//			String absolutePath = originalFile.getAbsolutePath();
//			try(InputStream inputStream= new BufferedInputStream(new FileInputStream(originalFile));){
//    			byte[] fileContent = IOUtils.toByteArray(inputStream);
//    			processByteArray(startTime, absolutePath, fileContent);
//    		}catch(Exception e){
//    			LOG.error("Error processing XML file [Queue:{}][Exception:{}]", queue.getName(), e.toString());
//    		}
//		}
//        
//		processFileAfter(originalFile, true);
//	}
	
//	protected void processByteArray(Instant startTime, String absolutePath, byte[] fileContent) throws Exception{
//		FileMessage fileMessage = new FileMessage();
//		fileMessage.setFileName(absolutePath);
//		fileMessage.setFileContent(fileContent);
//		// rabbitTemplate.convertAndSend(queue.getName(), fileMessage);
//		
//		long elapsedTime = Instant.now().getEpochSecond() - startTime.getEpochSecond();
//		LOG.info("Sending the file to the broker [file:{}][size:{}][broker_queue:{}][elapsed_time:{} seconds]", absolutePath, fileContent.length, queue.getName(), elapsedTime);
//	}
	
	
	private void processFileAfter(File originalFile, boolean isPositive) {
		 try {
			 originalFile.delete();
		 }
		 catch (Exception e) {
			 originalFile.renameTo(
						new File(originalFile.getAbsolutePath() + ".failed"));
		 }
	}
	
}
