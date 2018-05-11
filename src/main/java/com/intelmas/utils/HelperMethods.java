package com.intelmas.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/** Helper class with methods to use for any class to process lines to parse.
 * @author Intelma
 *
 */
public class HelperMethods {

	/**
	 * @param path String object with path to analize files insie the directory.
	 * @return 
	 */
	public static File[] getFilesDir(String path){
		
		System.out.println("PATH: "+path);
		File[] listOfFiles = null;
		File folder = new File(path);
		
		if(folder.isDirectory()){
			
			listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		        System.out.println("File " + listOfFiles[i].getName());
		      } else if (listOfFiles[i].isDirectory()) {
		        System.out.println("Directory " + listOfFiles[i].getName());
		      }
		    }
		}else{
			System.out.println("Wrong path: "+path);
		}
		 return listOfFiles;
	}
	
	/**
     * @param lineSplited String array with line splited to extract values.
     * @param splitBy String object with character to split the lines and extract parameter name and his value
     * @return
     */
    public static Map<String, String> mappingLine(String[] lineSplited, String splitBy){
		Map<String, String> mapLine = new HashMap<String, String>();
		
		if(lineSplited.length >= 2){
			for(String line : lineSplited){
				String[] keyVal = line.split(splitBy);
				mapLine.put(keyVal[0].trim(), keyVal[1]);
			}
		}else{
			return null;
		}
		
		return mapLine;
	}
}
