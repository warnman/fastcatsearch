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

package org.fastcatsearch.ir.query;

import org.fastcatsearch.ir.settings.FieldSetting;
import org.fastcatsearch.ir.sort.DataAscSortFunction;
import org.fastcatsearch.ir.sort.DataDescSortFunction;
import org.fastcatsearch.ir.sort.NumericAscSortFunction;
import org.fastcatsearch.ir.sort.NumericDescSortFunction;
import org.fastcatsearch.ir.sort.SortFunction;

public class Sort {
	private String fieldId;
	boolean asc;
	
	public Sort(String fieldId){
		this(fieldId, true);
	}
	public Sort(String fieldId, boolean asc){
		this.fieldId = fieldId;
		this.asc = asc;
	}
	
	public String toString(){
		return "[SORT]"+fieldId+":"+asc;
	}
	
	public String fieldId(){
		return fieldId;
	}
	public boolean asc(){
		return asc;
	}
	public SortFunction createSortFunction(FieldSetting fieldSetting) {
		if(fieldSetting.isNumericField()){
			//데이터가 int, long등의 숫자형일 경우 byte[] 의 비교방식이 달라진다.
			if(asc){
				return new NumericAscSortFunction();
			}else{
				return new NumericDescSortFunction();
			}
		}else{
			if(asc){
				return new DataAscSortFunction();
			}else{
				return new DataDescSortFunction();
			}
		}
	}
}
