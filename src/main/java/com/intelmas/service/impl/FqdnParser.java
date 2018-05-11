package com.intelmas.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.intelmas.dto.model.NodeTopologyEntity;
import com.intelmas.dto.model.NodesEntityKey;
import com.intelmas.dto.model.NodesOssEntity;
import com.intelmas.repository.cassandra.NodesNameRepository;
import com.intelmas.repository.cassandra.NodesOssRepository;
import com.intelmas.service.TopologyParserService;
import com.intelmas.utils.HelperMethods;

/** Class implementation of parser for FQDN files type.
 * @author Intelma
 *
 */
@Component("fqdnParser")
@PropertySource(value ="classpath:application.properties")
public class FqdnParser implements TopologyParserService {
	
	@Autowired
	private NodesOssRepository nodesRepository;
	
	@Autowired
	private NodesNameRepository nodesNameRepository;
	
	@Value("${cassandra.insert.size}")
	private int insertSize;

	@Override
	public void parser(File originalFile) {
//    	File[] listFiles = HelperMethods.getFilesDir(fqdnPath);
		
//    	if(listFiles != null){
//	    	for (File currentFile : listFiles) {
				System.out.println("currentFile: "+ originalFile.getName());
		    	if(originalFile.isFile() && 
		    			(!originalFile.getName().contains("PROCESSED") || !originalFile.getName().contains("processed"))){
		    	
			        List<NodesOssEntity> empList = parseFile(originalFile);
			        
			        List<NodeTopologyEntity> nodesName = new ArrayList<>();
			        
			        for (NodesOssEntity nodesOssEntity : empList) {
			        	nodesName.add(nodesOssEntity.buildNode());
					}
			        
			        for (List<NodesOssEntity> partition : Lists.partition(empList, insertSize)) {
			        	nodesRepository.save(partition);
			        }
			        
			        for (List<NodeTopologyEntity> partition : Lists.partition(nodesName, insertSize)) {
			        	nodesNameRepository.save(partition);
			        }
			        
			        originalFile.renameTo(new File(originalFile.getAbsolutePath()+".processed"));
		
			    }
//	    	}
//    	}
	}	

	/**
	 * @param fileName File object with TXT format to parse.
	 * @return
	 */
	private List<NodesOssEntity> parseFile(File fileName) {
        List<NodesOssEntity> nodeList = new ArrayList<>();
        NodesOssEntity node = null;
        Map<String, String> key_val_map = null;
        String[] lineSplit = null;
        String[] blockSplit = null;
//        String element_type = "";
        String node_version = "";
        String node_ip_address = "";
        
        Charset charset = Charset.forName("ISO-8859-1");
	    try {
		  if(fileName.isFile()){
			  String element_type = "";
			  String fqdn_name = "";
			  
			  
		      List<String> lines = Files.readAllLines(fileName.toPath(), charset);
		      
		      List<NodesOssEntity> existedNodes = new ArrayList<>();
		      nodesRepository.findAll().forEach(existedNodes::add);
		      
		      for (String line : lines) {
		    	  	node = new NodesOssEntity();
		    	  	lineSplit = line.split("@");
		    	  	blockSplit = lineSplit[0].split(",");
		    	  	element_type = lineSplit.length > 1 ? lineSplit[1] : "";
		    	  	node_version = lineSplit.length > 2 ? lineSplit[2] : "";
		    	  	node_ip_address = lineSplit.length > 5 ? lineSplit[5] : "";
		    	  	
		    	  	key_val_map = HelperMethods.mappingLine(blockSplit,"=");
		    	  	
		    	  	if (key_val_map != null){
		    	  		String tech = "";
			    	  	String node_name = key_val_map.size() == 2 ? key_val_map.get("ManagedElement") : key_val_map.get("MeContext");
			    	  	fqdn_name = blockSplit[0] + "," + blockSplit[1];
			    	  	
			        	List<NodesOssEntity> matchNode = existedNodes.stream()
			        								.filter(param -> node_name
			        											.equals(param.getKey().getNode_name()))
			        								.collect(Collectors.toList());
			        	
			        	if(matchNode != null && !matchNode.isEmpty()){
			        		
			        		switch(element_type){	
								case "RBS":
								case "RNC":
									tech = "3G";
									break;
								case "ERBS":
								case "SGSN":
									tech = "4G";
									break;
							}
							
							NodesEntityKey key = new NodesEntityKey();
							
							key.setNode_organisation(matchNode.get(0).getKey().getNode_organisation());
							key.setNode_name((matchNode.get(0).getKey().getNode_name()));
							key.setNode_type(element_type);
			        		
			        		node = matchNode.get(0);
				    	  	node.setNode_fqdn_name(fqdn_name);
				    	  	node.setNode_ip_address(node_ip_address);
				    	  	node.setKey(key);
				    	  	node.setNode_tech(tech);
				    	  	node.setNode_version(node_version);
				    	  	
				    	  	nodeList.add(node);
			        	}
		    	  	}
		      }
		  }else{
	    	    System.out.println("No file(s) to be readed");
		  }
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
        return nodeList;
    }
    
    
}
