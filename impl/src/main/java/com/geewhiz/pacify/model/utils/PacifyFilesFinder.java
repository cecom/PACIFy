/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
 * %%
 * Copyright (C) 2011 - 2017 Sven Oppermann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.geewhiz.pacify.model.utils;



import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacifyFilesFinder {
	private static CMFileFilter CMFileFilter = new CMFileFilter();
	private static DirFilter dirFilter = new DirFilter();

	private File folderToCheck;

	public PacifyFilesFinder(File folderToCheck) {
		this.folderToCheck = folderToCheck;
	}

	public List<File> getPacifyFiles() {
		List<File> pMarkerFiles = new ArrayList<File>();
		addPMarkerFilesFor(pMarkerFiles, folderToCheck);
		return pMarkerFiles;
	}

	private void addPMarkerFilesFor(List<File> pMarkerFiles, File folderToCheck) {
		if (folderToCheck == null) {
			throw new IllegalArgumentException("Folder is null.... Aborting!");
		}
		if (!folderToCheck.exists()) {
			throw new IllegalArgumentException("Folder [" + folderToCheck.getAbsolutePath()
			        + "] does not exist... Aborting!");
		}

		pMarkerFiles.addAll(Arrays.asList(folderToCheck.listFiles(CMFileFilter)));

		File[] subFolders = folderToCheck.listFiles(dirFilter);
		for (File subFolder : subFolders) {
			addPMarkerFilesFor(pMarkerFiles, subFolder);
		}
	}
}
