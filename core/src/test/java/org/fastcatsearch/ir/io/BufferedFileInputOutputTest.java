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

package org.fastcatsearch.ir.io;

import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;

public class BufferedFileInputOutputTest extends TestCase{
	public void _testInOut() throws IOException{
		String filename = "test.inout";
		int TRY = 1000;
		long[] writePos = new long[TRY];
		
		int DATA_SIZE = 6256;
		byte[] writeBuf = new byte[ DATA_SIZE];
		for(int i=0;i< DATA_SIZE;i++)
			writeBuf[i] = (byte) i;
			         
		BufferedFileOutput bfo = new BufferedFileOutput(filename);
		for (int i = 0; i < TRY; i++) {
			writePos[i] = bfo.position();
			bfo.writeBytes(writeBuf, 0, writeBuf.length);
		}
		
		bfo.close();
		
		
		byte[] readBuf2 = new byte[ DATA_SIZE];
		BytesBuffer readBuffer = new BytesBuffer(readBuf2);
		
		
		BufferedFileInput bfi = new BufferedFileInput(filename);
		
		
		for(int i=0;i<TRY;i++){
			
			bfi.seek(writePos[i]);
			bfi.readBytes(readBuffer);
			System.out.println(writePos[i]+ " : "+readBuf2.length);
			
			for(int k=0;k< DATA_SIZE;k++){
				assertEquals(writeBuf[k] , readBuf2[k]);
//				if(writeBuf[k] != readBuf2[k]){
//					throw new IOException(k+") Diff "+writeBuf[k]+" : "+readBuf2[k]);
//				}
			}
			readBuffer.clear();
			
		}
		
//		System.out.println("pos="+writePos[TRY - 1]);
	}
	
	public void testInOut2() throws IOException{
		String filename = "test.inout";
		
		int[] eachCount = new int[]{1099,999,1080,922,876,1180}; 
//		int[] eachCount = new int[]{1,1,1,1,1,4911,1,1,1,1,1}; 
		int TRY = eachCount.length;
		byte[][] writeBuf = new byte[TRY][];
		Random r = new Random(System.currentTimeMillis());
		
		for(int i=0;i< TRY;i++){
			int size=eachCount[i];
			writeBuf[i] = new byte[size];
			r.nextBytes(writeBuf[i]);
//			for(int j=0;j<writeBuf[i].length;j++)
//				System.out.print(writeBuf[i][j]+", ");
//			System.out.println();
		}
		
		BufferedFileOutput bfo = new BufferedFileOutput(filename);
		for (int i = 0; i < TRY; i++) {
			int size=writeBuf[i].length;
			bfo.writeVInt(size);
			bfo.writeBytes(writeBuf[i], 0, size);
		}
		bfo.close();
		
		BufferedFileInput bfi = new BufferedFileInput(filename);
		int totalCount = 0;
		for(int i=0;i<TRY;i++){
//			int size=writeBuf[i].length;
			int size = bfi.readVInt();
			byte[] data = new byte[size];
			System.out.println("size:"+size);
			BytesBuffer readBuffer = new BytesBuffer(data);
			bfi.readBytes(readBuffer);
			for(int k=0;k< size;k++){
				totalCount++;
				if(writeBuf[i][k] != data[k])
					System.out.println(totalCount+"] "+writeBuf[i][k]+" : "+ data[k]);
//				assertEquals(writeBuf[i][k], data[k]);
			}
			readBuffer.clear();
			
		}
		bfi.close();
	}
	
	public void _testVB() throws IOException{
		String filename = "test.vbInout";
		int TRY = 10000;
		
		BufferedFileOutput bfo = new BufferedFileOutput(filename);
		for (int i = 0; i < TRY; i++) {
			bfo.writeVInt(i);
		}
		bfo.close();
		
		BufferedFileInput bfi = new BufferedFileInput(filename);
		for(int i=0;i<TRY;i++){
			assertEquals(i, bfi.readVInt());
		}
	}
	
	
	
	public void _testCompressRate() throws IOException{
		String filename = "test.inout1";
		String filename2 = "test.inout.comp";
		int TRY = 10000;
		long[] writePos = new long[TRY];
		long[] writePos2 = new long[TRY];
		Random r = new Random(System.currentTimeMillis());
		int DATA_SIZE = 256;
		byte[] writeBuf = new byte[ DATA_SIZE];
		for(int i=0;i< DATA_SIZE;i++)
			writeBuf[i] = (byte) ((i%1==0)? 1: 0);
			         
		long st = System.currentTimeMillis();
		//plain write
		BufferedFileOutput bfo = new BufferedFileOutput(filename);
		for (int i = 0; i < TRY; i++) {
			writePos[i] = bfo.position();
			bfo.writeBytes(writeBuf, 0, writeBuf.length);
		}
		bfo.close();
		System.out.println("plain write time = "+(System.currentTimeMillis() - st));
		
		st = System.currentTimeMillis();
		//compressed write
		bfo = new BufferedFileOutput(filename2);
		for (int i = 0; i < TRY; i++) {
			writePos2[i] = bfo.position();
			bfo.writeBytes(new BytesBuffer(writeBuf, 0, writeBuf.length)); //압축 . 
		}
		bfo.close();
		System.out.println("compressed write time = "+(System.currentTimeMillis() - st));
		
		
		
		
		byte[] readBuf2 = new byte[ DATA_SIZE];
		BytesBuffer readBuffer = new BytesBuffer(readBuf2);
		
		st = System.currentTimeMillis();
		readBuffer.clear();
		BufferedFileInput bfi = new BufferedFileInput(filename);
		for(int i=0;i<TRY;i++){
			
			bfi.seek(writePos[i]);
			bfi.readBytes(readBuffer);
			
			for(int k=0;k< DATA_SIZE;k++){
				if(writeBuf[k] != readBuf2[k]){
					throw new IOException(k+") Diff "+writeBuf[k]+" : "+readBuf2[k]);
				}
			}
			readBuffer.clear();
		}
		System.out.println("plain read time = "+(System.currentTimeMillis() - st));
		bfi.close();
		
		
		st = System.currentTimeMillis();
		readBuffer.clear();
		bfi = new BufferedFileInput(filename2);
		for(int i=0;i<TRY;i++){
			
			bfi.seek(writePos2[i]);
			bfi.readBytes(readBuffer);
			
			for(int k=0;k< DATA_SIZE;k++){
				if(writeBuf[k] != readBuf2[k]){
					throw new IOException(k+") Diff "+writeBuf[k]+" : "+readBuf2[k]);
				}
			}
			readBuffer.clear();
		}
		
		System.out.println("compressed read time = "+(System.currentTimeMillis() - st));
		bfi.close();
		
		System.out.println("#plain pos="+writePos[TRY - 1]);
		System.out.println("#compressed pos="+writePos2[TRY - 1]);
		
		
		
	}
}
