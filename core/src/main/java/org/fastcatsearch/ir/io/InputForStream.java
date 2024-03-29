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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputForStream extends Input {

	private InputStream is;
	
	public InputForStream(InputStream is){
		this.is = new BufferedInputStream(is);
	}
	@Override
	public int readBytes(BytesBuffer dst) throws IOException {
		int len = 0;
		while(dst.remaining() > 0){
			int n = is.read(dst.bytes, dst.offset, dst.remaining());
			dst.pos(dst.offset + n);
			len += n;
		}
		return len;
	}

	@Override
	public int readByte() throws IOException {
		return is.read();
	}

	@Override
	public long position() throws IOException {
		throw new IOException("지원되지 않는 오퍼레이션입니다."); 
	}

	@Override
	public void position(long p) throws IOException {
		throw new IOException("지원되지 않는 오퍼레이션입니다."); 
	}

	@Override
	public void close() throws IOException {
		is.close();
	}

	@Override
	public long size() throws IOException {
		throw new IOException("지원되지 않는 오퍼레이션입니다."); 
	}


}
