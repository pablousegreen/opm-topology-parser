package com.intelmas.service;

import java.io.File;

import org.springframework.stereotype.Service;

/** Interface to define method parser for each type file implementation.
 * @author Intelma
 *
 */
@Service
public interface TopologyParserService {

	/**
	 * @param originalFile File object to parse data into cassandra 
	 */
	void parser(File originalFile);

}
