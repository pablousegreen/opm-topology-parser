package com.intelmas.service;
//import static com.intelmas.service.FileProcessor.LOG;

import java.io.File;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;
import org.springframework.messaging.Message;
//import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

/** Class scanner/listener for ARNE files path 
 * @author Intelma
 *
 */
@Service
public class ArneFileProcessor extends FileProcessor{
	
//	private static final Logger LOG = LoggerFactory.getLogger("ERBS");
	
	/**
	 * Field injection value of path for ARNE files from application properties.
	 */
	@Value("${import.directory.arne:#{null}}")
	private String inputDirArne;
	
	/** Method scanner of ARNE path waiting for new files incoming.
	 * @return FileReadingMessageSource object created from file found in path and sent to the channel {@link com.intelmas.service.ArneFileProcessor.processArneChannel(Message<File>)}
	 */
	@Bean()
	@InboundChannelAdapter(value = "processArneAdapter", poller = @Poller(fixedDelay = "${importer.scan.period}", maxMessagesPerPoll = "${importer.scan.pool}"))
	public FileReadingMessageSource processArneM() {
	    FileReadingMessageSource lm = new FileReadingMessageSource();
	    
	    if(inputDirArne != null) lm.setDirectory(new File(inputDirArne));
	    Pattern pattern = getFilePattern();
	    lm.setFilter(new RegexPatternFileListFilter(pattern));
	    lm.setUseWatchService(true);
	    lm.setWatchEvents(FileReadingMessageSource.WatchEventType.CREATE);
	    return lm;
	}
	
	/** 
	 * @param message Message<File> object created from file from path and delivered from processArne adapter.
	 */
	@ServiceActivator(inputChannel = "processArneAdapter", outputChannel="afterProcessFileChannel")
    public void processArneChannel(Message<File> message) {
       
		processFile(message);
//		LOG.info("Finish Processing ERBS File {}", message.toString());
    }
	
}
