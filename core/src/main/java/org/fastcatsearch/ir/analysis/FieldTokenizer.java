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

package org.fastcatsearch.ir.analysis;

import org.fastcatsearch.ir.io.CharVector;

@TokenizerAttributes ( name="Field" )
public class FieldTokenizer extends Tokenizer{
	protected boolean finished;
	
	public FieldTokenizer(){ }
	
	public boolean nextToken(CharVector token){
		if(finished) return false;
		
		input.copy(token);
		finished = true;
		return true;
	}
	
	public boolean nextToken(CharVector token, int[] seq) {
		seq[0] = 0;
		return nextToken(token);
	}
	
	protected void init(){
		finished = false;
	};
	
	public void setInput(char[] input) {
		if(input == null || input.length == 0){
			finished = true;
			return;
		}
		
		this.input = new CharVector(input, 0, input.length).trim();
		
		init();
	}
	
}
