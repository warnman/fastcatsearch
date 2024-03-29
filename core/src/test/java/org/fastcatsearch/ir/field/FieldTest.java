package org.fastcatsearch.ir.field;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.fastcatsearch.ir.field.AStringField;
import org.fastcatsearch.ir.field.AStringMvField;
import org.fastcatsearch.ir.field.DatetimeField;
import org.fastcatsearch.ir.field.Field;
import org.fastcatsearch.ir.field.IntField;
import org.fastcatsearch.ir.io.BytesDataInput;
import org.fastcatsearch.ir.io.BytesDataOutput;
import org.junit.Test;

public class FieldTest {

	
	private BytesDataInput write(Field field) throws IOException{
		BytesDataOutput output = new BytesDataOutput();
		field.writeTo(output);
		byte[] array = output.array();
		return new BytesDataInput(array, 0, array.length);
	}
	
	
	@Test
	public void testIntegerField() throws IOException {
		
		String value = "1231435";
		IntField field = new IntField("A", value);
		
		BytesDataInput input = write(field);
		
		IntField field2 = new IntField("A");
		field2.readFrom(input);
		
		String value2 = field2.getValue().toString();
		
		assertEquals(value, value2);
		
		System.out.println(value2);
		
	}
	
	@Test
	public void testDatetimeField() throws IOException {
		
		String value = "2013-06-12 12:30:11";
		DatetimeField field = new DatetimeField("A", value);
		BytesDataInput input = write(field);
		
		DatetimeField field2 = new DatetimeField("A");
		field2.readFrom(input);
		
		SimpleDateFormat sdfc = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = (Date) field2.getValue();
		String value2 = sdfc.format(date);
		
		assertEquals(value, value2);
		
		System.out.println(value2);
		
	}
	
	String avalue = "It really depends on what kind of Stream you're working with. " +
			"For instance System.console().readLine() (new in Java 6) is pretty easy. " +
			"Same with BufferedReader's readLine()";
	
	@Test
	public void testStringField() throws IOException {
		
		
		AStringField field = new AStringField("A", avalue);
		BytesDataInput input = write(field);
		
		AStringField field2 = new AStringField("A");
		field2.readFrom(input);
		String value2 = field2.getValue().toString();
		
		assertEquals(avalue, value2);
		
		System.out.println(value2);
		
	}
	
	@Test
	public void testFixedStringField() throws IOException {
		int size = 10;
		AStringField field = new AStringField("A", avalue, size);
		BytesDataInput input = write(field);
		
		AStringField field2 = new AStringField("A");
		field2.readFrom(input);
		String value2 = field2.getValue().toString();
		
		assertEquals(avalue.substring(0, size), value2);
		
		System.out.println(field.getValue());
		
	}
	
	@Test
	public void testFixedStringMvField() throws IOException {
		int size = 10;
		String[] values = new String[]{"123456789011", "223456789011", "323456789011"};
		AStringMvField field = new AStringMvField("A", size);
		field.addValue(values[0]);
		field.addValue(values[1]);
		field.addValue(values[2]);
		BytesDataInput input = write(field);
		
		AStringMvField field2 = new AStringMvField("A");
		field2.readFrom(input);
		Iterator<Object> iterator = field2.getValueIterator();
		int i = 0;
		while(iterator.hasNext()){
			String val = iterator.next().toString();
			if(values[i].length() > size){
				values[i] = values[i].substring(0, size);
			}
			assertEquals(values[i++], val);
		}
		
		System.out.println(field.getValue());
		
	}

}
