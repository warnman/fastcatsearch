package org.fastcatsearch.ir.field;

import java.io.IOException;

import org.fastcatsearch.ir.io.DataInput;
import org.fastcatsearch.ir.io.DataOutput;

public class LongField extends NumericField {
	
	public LongField(String id){
		super(id);
	}
	
	public LongField(String id, String data){
		super(id, data);
	}
	
	@Override
	protected Long parseNumber(String data){
		return Long.valueOf(data);
	}
	
	@Override
	public void readFrom(DataInput input) throws IOException {
		fieldsData = Long.valueOf(input.readLong());
	}
	
	@Override
	public void writeFixedDataTo(DataOutput output) throws IOException {
		output.writeLong(((Long)fieldsData).longValue());
	}

	@Override
	public FieldDataWriter getDataWriter() throws IOException {
		throw new IOException("싱글밸류필드는 writer를 지원하지 않습니다.");
	}

}
