/*
 * Copyright (c) 2013 Websquared, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     swsong - initial API and implementation
 */

package org.fastcatsearch.datasource.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fastcatsearch.datasource.SourceModifier;
import org.fastcatsearch.env.Environment;
import org.fastcatsearch.env.Path;
import org.fastcatsearch.ir.common.IRException;
import org.fastcatsearch.ir.config.DataSourceConfig;
import org.fastcatsearch.ir.config.FileSourceConfig;
import org.fastcatsearch.ir.document.Document;
import org.fastcatsearch.ir.index.DeleteIdSet;
import org.fastcatsearch.ir.io.DirBufferedReader;
import org.fastcatsearch.ir.settings.FieldSetting;
import org.fastcatsearch.ir.settings.Schema;
import org.fastcatsearch.util.HTMLTagRemover;


public class FastcatSearchCollectFileParser extends DataSourceReader {
	
	private DirBufferedReader br;
	private Document document;
	
	
	private static String DOC_START ="<doc>";
	private static String DOC_END ="</doc>";
	
	
	private static String OPEN_PATTERN = "^<([\\w]+[^>]*)>$";
	private static String CLOSE_PATTERN = "^<\\/([\\w]+[^>]*)>$";
	private Pattern OPAT;
	private Pattern CPAT;
	private int count; // how many fields are set

	public FastcatSearchCollectFileParser(Path filePath, Schema schema, FileSourceConfig config, SourceModifier sourceModifier, Boolean isFull) throws IRException {
		super(filePath, schema, config, sourceModifier);
		try {
			if(isFull){
				br = new DirBufferedReader(new File(config.getFullFilePath()), config.getFileEncoding());
			}else{
				br = new DirBufferedReader(new File(config.getIncFilePath()), config.getFileEncoding());
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
			throw new IRException(e);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new IRException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new IRException(e);
		}
		document = null;
		
		OPAT = Pattern.compile(OPEN_PATTERN);
		CPAT = Pattern.compile(CLOSE_PATTERN);
		
		deleteIdList = new DeleteIdSet(primaryKeySize);
		
	}
	
	public boolean hasNext() throws IRException{
		int size = fieldSettingList.size();
		String line = null;
		document = new Document(size);
		
		String oneDoc = readOneDoc();
		if(oneDoc == null)
			return false;
		
		BufferedReader reader = new BufferedReader(new StringReader(oneDoc));
		
		StringBuffer sb = new StringBuffer();
		
		String openTag = "";
		boolean isOpened = false;
		int tagNum = -1;
		FieldSetting fs = null;
		count = 0;
		while(true){
			try {
				line = reader.readLine();
				
				if(line == null){
					
					break;
				}
				
				if(line.length() == 0)
					continue;
				
				line = line.trim();
				
				
				if(line.length() > 1 && line.charAt(0) == '<' && line.charAt(1) != '/'){
					Matcher m = OPAT.matcher(line);
					if(m.matches()){
						String tag = m.group(1);
						int tempTagNum = schema.getFieldSequence(tag);
						if(tempTagNum < 0){
						}else{
							tagNum = tempTagNum;
							openTag = tag;
							isOpened = true;
							fs = fieldSettingList.get(tagNum);
							continue;
						}
					}
				}
				
				if(isOpened && line.startsWith("</")){
					Matcher m = CPAT.matcher(line);
					if(m.matches()){
						String closeTag = m.group(1);
						if(openTag.equals(closeTag)){
							isOpened = false;
							String targetStr = sb.toString();
							if(fs.isRemoveTag()){
								targetStr = HTMLTagRemover.clean(targetStr);
							}
							document.set(tagNum, fs.createField(targetStr));
							sb = new StringBuffer();
							count++;
							continue;
						}
					}
				}
				
//				logger.debug("DOC = "+oneDoc);
				
				
				if(sb.length() > 0)
					sb.append(Environment.LINE_SEPARATOR);
				
				sb.append(line);
			
//				logger.debug(sb.toString());
			
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				throw new IRException(e);
			}
		}
//		logger.debug("doc = "+document);
		if(document == null)
			return false;
		
		return true;
		
	}
	
	
	private String readOneDoc() throws IRException {
		try{
			StringBuffer sb = new StringBuffer();
			
			String line = br.readLine();
			
			if(line == null)
				return null;
			
			int lineNumber = 0;
			
			while(!line.equals(DOC_START)){
				//doc opened
				line = br.readLine();
				if(line == null)
					return null;
			}
			
			line = br.readLine();
			
			//doc started
			while(!line.equals(DOC_END)){
				//doc ended
				if(lineNumber >= 1){
					sb.append(Environment.LINE_SEPARATOR);
				}
				sb.append(line);
				line = br.readLine();
				lineNumber++;
			}
			return sb.toString();
		} catch (IOException e) {
			throw new IRException(e);
		}
	}

	public Document next() throws IRException{
		if(count != document.size()){
//			throw new IRException("Collect document's field count is diffent from setting's. Check field names. it's case sensitive. collect field size = "+count+", document.size()="+document.size());
			logger.warn("Collect document's field count is diffent from setting's. Check field names. it's case sensitive. collect field size = "+count+", document.size()="+document.size());
			if(hasNext() == false)
				return null;
		}
		count = 0;
		return document;
	}
	
	public void close() throws IRException{
		try {
			br.close();
		} catch (IOException e) {
			throw new IRException(e);
		}
	}
	
}
