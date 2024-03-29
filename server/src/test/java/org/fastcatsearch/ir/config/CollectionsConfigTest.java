package org.fastcatsearch.ir.config;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.fastcatsearch.ir.config.CollectionsConfig.Collection;
import org.junit.Test;

public class CollectionsConfigTest {

	String collectionsConfigXml = "<collections>\n" + 
			"	<collection active=\"false\">sample</collection>\n" + 
			"	<collection active=\"true\">sample2</collection>\n" + 
			"</collections>";
	@Test
	public void testRead() throws IOException {
		InputStream is = new ByteArrayInputStream(collectionsConfigXml.getBytes());
		CollectionsConfig collectionsConfig = JAXBConfigs.readConfig(is, CollectionsConfig.class);
		List<Collection> collectionList = collectionsConfig.getCollectionList();
		Collection collection = collectionList.get(0);
		assertEquals("sample", collection.getId());
		assertEquals(false, collection.isActive());
		
		collection = collectionList.get(1);
		assertEquals("sample2", collection.getId());
		assertEquals(true, collection.isActive());
	}
	
	@Test
	public void testWrite() {
		
		CollectionsConfig collectionsConfig = new CollectionsConfig();
		collectionsConfig.addCollection("test", true);
		collectionsConfig.addCollection("sample");
		collectionsConfig.addCollection("sample2", false);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JAXBConfigs.writeConfig(os, collectionsConfig, CollectionsConfig.class);
		System.out.println(os.toString());
		
		
	}

}
