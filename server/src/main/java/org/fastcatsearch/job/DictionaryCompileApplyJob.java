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

package org.fastcatsearch.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;


import org.fastcatsearch.db.DBService;
import org.fastcatsearch.db.dao.SetDictionary;
import org.fastcatsearch.db.vo.SetDictionaryVO;
import org.fastcatsearch.ir.dictionary.ListMapDictionary;
import org.fastcatsearch.plugin.Plugin;
import org.fastcatsearch.plugin.PluginService;
import org.fastcatsearch.exception.FastcatSearchException;
import org.fastcatsearch.service.ServiceManager;

public class DictionaryCompileApplyJob extends Job {

	private static final long serialVersionUID = 8615645248824825498L;

	@Override
	public JobResult doRun() throws FastcatSearchException {
		String[] args = getStringArrayArgs();
		String category = args[0];
		String dicType = args[1];
		if(dicType.length() == 0){
			dicType = "";
		}
		String synonymDictionaryId = category + "SynonymDictionary";
		String userDictionaryId = category + "UserDictionary";
		String stopDictionaryId = category + "StopDictionary";

		DBService dbService = DBService.getInstance();
		SetDictionary synonymDictionary = dbService.getDAO(synonymDictionaryId);
		SetDictionary userDictionary = dbService.getDAO(userDictionaryId);
		SetDictionary stopDictionary = dbService.getDAO(stopDictionaryId);
		
		PluginService pluginService = ServiceManager.getInstance().getService(PluginService.class);
		Plugin plugin = pluginService.getPlugin(category);
		File pluginDir = plugin.getPluginDir();
		Map<String, String> properties= plugin.getPluginSetting().getProperties();
		String synonymDictPath = new File(pluginDir, properties.get("synonym.dict.path")).getAbsolutePath();
		String userDictPath = new File(pluginDir, properties.get("user.dict.path")).getAbsolutePath();
		String stopDictPath = new File(pluginDir, properties.get("stop.dict.path")).getAbsolutePath();

		logger.debug("compile dictType={} /  {}, {}, {}", dicType, synonymDictPath, userDictPath, stopDictPath);
		//
		// 1. synonymDictionary
		//
		if (dicType.length() == 0 || dicType.equals("synonymDict")) {
			List<SetDictionaryVO> result = synonymDictionary.selectPage(-1, -1);
			ListMapDictionary dictionary = new ListMapDictionary();
			for (int i = 0; i < result.size(); i++) {
				SetDictionaryVO vo = result.get(i);
				dictionary.addEntry(vo.keyword);
			}
			OutputStream out = null;
			try {
				out = new FileOutputStream(synonymDictPath);
				dictionary.writeTo(out);
			} catch (Exception e) {
				return new JobResult(e);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException ignore) {
					}
				}
			}
			logger.debug("Dictionary write to {}", synonymDictPath);
		}
		//
		// 2. userDictionary
		//
		if (dicType.length() == 0 || dicType.equals("userDict")) {
			List<SetDictionaryVO> result = userDictionary.selectPage(-1, -1);
			try {
				compileSetDictionary(result, userDictPath);
			} catch (Exception e) {
				return new JobResult(e);
			}
			logger.debug("Dictionary write to {}", userDictPath);
		}

		//
		// 3. stopDictionary
		//
		if (dicType.length() == 0 || dicType.equals("stopDict")) {
			List<SetDictionaryVO> result = stopDictionary.selectPage(-1, -1);
			try {
				compileSetDictionary(result, stopDictPath);
			} catch (Exception e) {
				return new JobResult(e);
			}
			logger.debug("Dictionary write to {}", stopDictPath);
		}

		logger.debug("사전컴파일후 플러그인 {}를 재로딩합니다.",category);
		plugin.reload();
		
		
		return new JobResult(0);
	}

	private void compileSetDictionary(List<SetDictionaryVO> result, String filePath) throws Exception {
		org.fastcatsearch.ir.dictionary.HashSetDictionary dictionary = new org.fastcatsearch.ir.dictionary.HashSetDictionary();
		for (int i = 0; i < result.size(); i++) {
			SetDictionaryVO vo = result.get(i);
			dictionary.addEntry(vo.keyword);
		}
		OutputStream out = null;
		try {
			out = new FileOutputStream(filePath);
			dictionary.writeTo(out);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignore) {
				}
			}
		}

	}

}
