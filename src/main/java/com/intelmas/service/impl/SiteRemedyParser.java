package com.intelmas.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.intelmas.dto.model.NodeTopologyEntity;
import com.intelmas.dto.model.NodesEntityKey;
import com.intelmas.dto.model.NodesOssEntity;
import com.intelmas.dto.model.RegionsEntity;
import com.intelmas.repository.cassandra.NodesNameRepository;
import com.intelmas.repository.cassandra.NodesOssRepository;
import com.intelmas.repository.cassandra.RegionsRepository;
import com.intelmas.service.TopologyParserService;

/** Class implementation of parser for Remedy files type.
 * @author Intelma
 *
 */
@Component("remedyParser")
public class SiteRemedyParser implements TopologyParserService {

	@Autowired
	private NodesOssRepository nodesRepository;

	@Autowired
	private NodesNameRepository nodesNameRepository;
	
	@Autowired
	private RegionsRepository regionsRepository;

	@Value("${cassandra.insert.size}")
	private int insertSize;

	// @Value("${remedy.path}")
	// private String remedyPath;

	@Override
	public void parser(File originalFile) {

		if (originalFile.isFile() && !originalFile.getName().contains("PROCESSED") || !originalFile.getName().contains("processed")) {
			List<NodesOssEntity> empList = parseFile(originalFile);
			List<NodeTopologyEntity> nodesName = new ArrayList<>();
			List<RegionsEntity> regionsList = new ArrayList<RegionsEntity>();
			
			System.out.println("nodesOssEntity: "+empList.size());

			for (NodesOssEntity nodesOssEntity : empList) {
				nodesName.add(nodesOssEntity.buildNode());
				
				RegionsEntity regions = new RegionsEntity();
				
				regions.setRegion_organisation(nodesOssEntity.getKey().getNode_organisation());
				regions.setRegion_name(nodesOssEntity.getNode_region());
				
//				System.out.println(regions.toString());
				if(regions.getRegion_name() != null && 
						!regions.getRegion_name().isEmpty() && 
							!regionsList.contains(regions))regionsList.add(regions);
				
//				System.out.println("regionsList: "+regionsList.size());
			}
			
			for (List<RegionsEntity> partition : Lists.partition(regionsList, insertSize)) {
				regionsRepository.save(partition);
			}

			for (List<NodesOssEntity> partition : Lists.partition(empList, insertSize)) {
				nodesRepository.save(partition);
			}

			for (List<NodeTopologyEntity> partition : Lists.partition(nodesName, insertSize)) {
				nodesNameRepository.save(partition);
			}

			if (empList != null && !empList.isEmpty()) {
				System.out.println("originalFile: " + originalFile);
				try {
					originalFile.renameTo(new File(originalFile.getAbsolutePath() + ".processed"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * @param fileName File object with CSV format to parse.
	 * @return List with NodesOssEntity objects with the values extracted from the current line analized.
	 */
	private List<NodesOssEntity> parseFile(File fileName) {
		List<NodesOssEntity> nodeList = new ArrayList<NodesOssEntity>();
		
		NodesOssEntity node = null;
		String[] lineSplit = null;

		Charset charset = Charset.forName("ISO-8859-1");
		try {
			if (fileName.isFile()) {
				int lineNumber = 0;

				List<String> lines = Files.readAllLines(fileName.toPath(), charset);

				List<NodesOssEntity> existedNodes = new ArrayList<>();
				nodesRepository.findAll().forEach(existedNodes::add);

				for (String line : lines) {
					
					lineNumber++;
					node = new NodesOssEntity();
					lineSplit = line.split(";");
					if (lineNumber > 1 && lineSplit.length >= 3) {
						String node_name = lineSplit[2];

						List<NodesOssEntity> matchNode = existedNodes.stream()
								.filter(param -> node_name.equals(param.getKey().getNode_name()))
								.collect(Collectors.toList());

						if (matchNode != null && !matchNode.isEmpty()) {

							NodesEntityKey key = new NodesEntityKey();

							key.setNode_organisation(matchNode.get(0).getKey().getNode_organisation());
							key.setNode_name((matchNode.get(0).getKey().getNode_name()));
							key.setNode_type(matchNode.get(0).getKey().getNode_type());

							node = matchNode.get(0);
							node.setNode_region("R" + lineSplit[0].substring(7, 8));
							
							nodeList.add(node);
						}
					}
				}
			} else {
				System.out.println("No file(s) to be readed");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return nodeList;
	}
}
