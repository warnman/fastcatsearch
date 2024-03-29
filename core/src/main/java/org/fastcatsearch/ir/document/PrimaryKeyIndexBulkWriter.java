/*
 * Copyright 2013 Websquared, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fastcatsearch.ir.document;

import java.io.File;
import java.io.IOException;

import org.fastcatsearch.ir.io.BufferedFileOutput;
import org.fastcatsearch.ir.io.BytesBuffer;
import org.fastcatsearch.ir.io.IndexOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * write sorted keys to make a pk map.
 * @author sangwook.song
 *
 */
public class PrimaryKeyIndexBulkWriter {
	private static Logger logger = LoggerFactory.getLogger(PrimaryKeyIndexBulkWriter.class);
	
	private String filename;
	private IndexOutput output;
	private IndexOutput indexOutput;
	private int indexInterval;
	private int keyCount;
	private int keyIndexCount;
	private long pos1, pos2;
	private boolean isAppend;
	
	public PrimaryKeyIndexBulkWriter(File f, int indexInterval) throws IOException{
		this.filename = f.getName();
		this.indexInterval = indexInterval;
		output = new BufferedFileOutput(f);
		indexOutput = new BufferedFileOutput(f.getParentFile(),f.getName()+".index");
		pos1 = output.position();
		pos2 = indexOutput.position();
		
		output.writeInt(0);
		indexOutput.writeInt(0);
	}

	public PrimaryKeyIndexBulkWriter(IndexOutput output, IndexOutput indexOutput, int indexInterval, boolean isAppend) throws IOException{
		this.output = output;
		this.indexOutput = indexOutput;
		this.indexInterval = indexInterval;
		this.isAppend = isAppend;
		pos1 = output.position();
		pos2 = indexOutput.position();
		output.writeInt(0);
		indexOutput.writeInt(0);
	}
	
	
	public void close() throws IOException{
//		logger.debug(filename +" filesize=" + output.position()+", count="+keyCount);
		long t = output.position();
		output.seek(pos1);
		output.writeInt(keyCount);
		if(!isAppend)
			output.close();
		else
			output.seek(t);
		
//		logger.debug(filename +".index filesize=" + indexOutput.seek()+", count="+keyIndexCount);
		t = indexOutput.position();
		indexOutput.seek(pos2);
		indexOutput.writeInt(keyIndexCount);
		if(!isAppend)
			indexOutput.close();
		else
			indexOutput.seek(t);
		
	}
	public void write(BytesBuffer buf, int value) throws IOException{
		
		//write pkmap index
		BytesBuffer clone = new BytesBuffer(buf.array(), buf.pos(), buf.limit());
		
		if(keyCount % indexInterval == 0){
			indexOutput.writeVInt(buf.remaining());
			indexOutput.writeBytes(buf);
			indexOutput.writeLong(output.position());
			keyIndexCount++;
		}
		
		output.writeVInt(clone.remaining());
		output.writeBytes(clone);
		output.writeInt(value);
		keyCount++;
	}
	
	public int getKeyCount(){
		return keyCount;
	}
	
	public int getKeyIndexCount(){
		return keyIndexCount;
	}
}
