package com.intelmas.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.intelmas.dto.model.NodeTopologyEntity;
import com.intelmas.dto.model.NodesEntityKey;
import com.intelmas.dto.model.NodesOssEntity;
import com.intelmas.dto.model.NodesTypeEntity;
import com.intelmas.dto.model.OssEntity;
import com.intelmas.repository.cassandra.NodesNameRepository;
import com.intelmas.repository.cassandra.NodesOssRepository;
import com.intelmas.repository.cassandra.NodesTypeRepository;
import com.intelmas.repository.cassandra.OssRepository;
import com.intelmas.service.TopologyParserService;

/** Class implementation of parser for ARNE files type.
 * @author Intelma
 *
 */
@Component("arneParser")
@PropertySource(value = { "classpath:application.properties" })
public class ArneParser implements TopologyParserService {

	@Autowired
	private OssRepository ossRepository;

	@Autowired
	private NodesOssRepository nodesRepository;

	@Autowired
	private NodesNameRepository nodesNameRepository;
	
	@Autowired
	private NodesTypeRepository nodesTypeRepository;

	@Value("${cassandra.insert.size}")
	private int insertSize;
	
	private static final Logger LOG = LoggerFactory.getLogger(ArneParser.class);

	@Override
	public void parser(File originalFile) {
		List<NodesTypeEntity> typeList = new ArrayList<NodesTypeEntity>();
		

		LOG.info("currentFile: " + originalFile.getName());
		if (originalFile.isFile()
				&& (!originalFile.getName().contains("processed") || !originalFile.getName().contains("PROCESSED"))) {

			OssEntity ossEntityInfo = findOssEntityPath(originalFile);

			if (ossEntityInfo != null) {
				List<NodesOssEntity> empList = parseXML(originalFile, ossEntityInfo);
				List<NodeTopologyEntity> nodesName = new ArrayList<>();
				
				for (NodesOssEntity nodesOssEntity : empList) {
					
					if(nodesOssEntity.getKey().getNode_organisation() == null){
						System.out.println("NULL VALUE ORGANISATION");
					}
					
					
					nodesName.add(nodesOssEntity.buildNode());
					
					NodesTypeEntity node_type = new NodesTypeEntity();
					
					node_type.setType_name(nodesOssEntity.getKey().getNode_type());
					node_type.setType_organisation(ossEntityInfo.getOss_organisation());
					node_type.setType_tech(ossEntityInfo.getOss_tech());
					
					if(nodesOssEntity.getKey().getNode_type() != null && 
							!nodesOssEntity.getKey().getNode_type().isEmpty() && 
								!typeList.contains(node_type))typeList.add(node_type);
				}
				
				for (List<NodesTypeEntity> partition : Lists.partition(typeList, insertSize)) {
					nodesTypeRepository.save(partition);
				}

				for (List<NodesOssEntity> partition : Lists.partition(empList, insertSize)) {
					nodesRepository.save(partition);
				}

				for (List<NodeTopologyEntity> partition : Lists.partition(nodesName, insertSize)) {
					nodesNameRepository.save(partition);
				}

				originalFile.renameTo(new File(originalFile.getAbsolutePath() + ".processed"));
			}
		}
	}

	/**
	 * @param fileName File object with XML format to parse. 
	 * @param ossEntityInfo OssEntity object with OSS information found in cassandra DB.
	 * @return List with NodesOssEntity objects extracted from XML file.
	 */
	private List<NodesOssEntity> parseXML(File fileName, OssEntity ossEntityInfo) {
		List<NodesOssEntity> nodeList = new ArrayList<>();
		List<NodesOssEntity> existNodes = new ArrayList<>();
		NodesEntityKey key = null;
		NodesOssEntity node = null;
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		try {
			nodesRepository.findAll().forEach(existNodes::add);

			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(fileName));
			int event = xmlStreamReader.getEventType();
			while (true) {
				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					switch (xmlStreamReader.getLocalName()) {
						case "ManagedElementId":
	
							List<NodesOssEntity> nodesFilter = nodeList.stream().filter(
									param -> xmlStreamReader.getAttributeValue(0).equals(param.getKey().getNode_name()))
									.collect(Collectors.toList());
	
							if (nodesFilter.size() > 0) {
								node = nodesFilter.get(0);
							} else {
								node = new NodesOssEntity();
								key = new NodesEntityKey();
								key.setNode_name(xmlStreamReader.getAttributeValue(0));
							}
	
							break;
						case "vendorName":
							key.setNode_organisation(xmlStreamReader.getAttributeValue(0));
							break;
						case "primaryType":
							key.setNode_type(xmlStreamReader.getAttributeValue(0));
							break;
						case "nodeVersion":
							node.setNode_version(xmlStreamReader.getAttributeValue(0));
							break;
						case "ipAddress":
							if (node != null) {
								node.setNode_ip_address(xmlStreamReader.getAttributeValue(0));
							}
						}
					break;
				case XMLStreamConstants.CHARACTERS:
					break;
				case XMLStreamConstants.END_ELEMENT:
					if (
					// xmlStreamReader.getLocalName().equals("Site")
					// ||
					xmlStreamReader.getLocalName().equals("ManagedElement")) {

						node.setKey(key);

						NodesOssEntity nodeFile = node;

						List<NodesOssEntity> matchNode = existNodes.stream()
								.filter(param -> param.getKey().getNode_name().equals(nodeFile.getKey().getNode_name()))
								.collect(Collectors.toList());

						if (matchNode.size() == 0) {// just new nodes
							if (key.getNode_type() != null) {
								
								key.setNode_organisation(ossEntityInfo.getOss_organisation());
								node.setNode_tech(ossEntityInfo.getOss_tech());
								node.setNode_parent(ossEntityInfo.getOss_name());
								node.setKey(key);
								
								nodeList.add(node);
							}
						}
					}
					break;
				}
				if (!xmlStreamReader.hasNext())
					break;

				event = xmlStreamReader.next();
			}

		} catch (FileNotFoundException | XMLStreamException e) {
			e.printStackTrace();
			LOG.error("Exception when executing saving documents [Exception:{}]", e.toString());
		}
		return nodeList;
	}

	/**
	 * @param currentFile File object found in ARNE path
	 * @return OssEntity object with result data from cassandra query. 
	 */
	public OssEntity findOssEntityPath(File currentFile) {

		System.out.println(currentFile.getPath());
		OssEntity OssEntityInfo = null;

		// Directory Structure: /pdata/topology/ericsson/arne/<hostname or ossname>/<filename>
		String directory = StringUtils.substringAfter(currentFile.getPath(), File.separator+"pdata"+File.separator);
		String[] filePaths = StringUtils.split(directory, File.separator);
		String oss = "";
		String tech = "";
		String vendor = "";
		
		if (filePaths.length > 1) vendor = filePaths[1];
		if (filePaths.length > 3) oss = filePaths[3];
		if (directory.contains("3g")) tech = "3G";
		if (directory.contains("4g") || directory.contains("lte")) tech = "4G";


		try {

			OssEntityInfo = ossRepository.findByName(vendor, oss);

			if (OssEntityInfo == null) {
				OssEntityInfo = new OssEntity();


				OssEntityInfo.setOss_name(oss);
				OssEntityInfo.setOss_organisation(vendor);
				OssEntityInfo.setOss_tech(tech);

				ossRepository.save(OssEntityInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return OssEntityInfo;
	}

}
