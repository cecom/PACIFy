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
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.geewhiz.pacify;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.DefectMessage;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.FilterManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.utils.DefectUtils;
import com.geewhiz.pacify.utils.FileUtils;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Inject;

public class Replacer {

	private Logger logger = LogManager.getLogger(Replacer.class.getName());

	private PropertyResolveManager propertyResolveManager;
	private File packagePath;
	private File copyDestination;

	private EntityManager entityManager;

	@Inject
	public Replacer(PropertyResolveManager propertyResolveManager) {
		this.propertyResolveManager = propertyResolveManager;
	}

	public void execute() {
		logger.info("== Executing {} [Version={}]", getClass().getSimpleName(), Utils.getJarVersion());
		logger.info("   [PackagePath={}]", getPackagePath().getAbsolutePath());

		DefectUtils.abortIfDefectExists(getEntityManager().initialize());

		logger.info("== Found [{}] pacify marker files", getEntityManager().getPMarkerCount());
		logger.info("== Validating...");

		DefectUtils.abortIfDefectExists(validate());

		logger.info("== Replacing...");
		DefectUtils.abortIfDefectExists(doReplacement());

		logger.info("== Successfully finished");
	}

	private File prepareAndGetPathToConfigure() {
		if (copyDestination == null) {
			return getPackagePath();
		}

		File result = null;
		logger.info("   [Destination={}]", getCopyDestination().getAbsolutePath());
		try {
			result = createCopy();
		} catch (DefectException e) {
			DefectUtils.abortIfDefectExists(new LinkedHashSet<Defect>(Arrays.asList(e)));
		}

		return result;
	}

	public File getPackagePath() {
		return packagePath;
	}

	public void setPackagePath(File packagePath) {
		this.packagePath = packagePath;
	}

	public File getCopyDestination() {
		return copyDestination;
	}

	public void setCopyDestination(File copyDestination) {
		this.copyDestination = copyDestination;
	}

	public EntityManager getEntityManager() {
		if (entityManager == null) {
			entityManager = createEntityManager(prepareAndGetPathToConfigure());
		}
		return entityManager;
	}

	private File createCopy() throws DefectException {
		try {
			if (getCopyDestination().exists()) {
				if (!getCopyDestination().isDirectory()) {
					throw new DefectMessage("Destination directory [" + getCopyDestination().getAbsolutePath()
							+ "] is not a directory.");
				}
				if (getCopyDestination().list().length > 0) {
					throw new DefectMessage(
							"Destination directory [" + getCopyDestination().getAbsolutePath() + "] is not empty.");
				}
				if (!getCopyDestination().canWrite()) {
					throw new DefectMessage(
							"Destination directory [" + getCopyDestination().getAbsolutePath() + "] is not writable.");
				}
			}

			FileUtils.copyDirectory(getPackagePath(), getCopyDestination());
			return getCopyDestination();
		} catch (IOException e) {
			logger.debug(e);
			throw new DefectMessage("Error while copy [" + getPackagePath().getAbsolutePath() + "] to ["
					+ getCopyDestination().getAbsolutePath() + "].");
		}
	}

	public LinkedHashSet<Defect> doReplacement() {
		LinkedHashSet<Defect> defects = getEntityManager().initialize();

		FilterManager filterManager = new FilterManager(getEntityManager(), propertyResolveManager);

		defects.addAll(filterManager.doFilter());

		return defects;
	}

	protected LinkedHashSet<Defect> validate() {
		return createValidator().validateInternal();
	}

	protected Validator createValidator() {
		Validator validator = new Validator(propertyResolveManager);

		validator.setPackagePath(packagePath);
		validator.enableMarkerFileChecks();
		validator.enablePropertyResolveChecks();
		validator.setEntityManager(getEntityManager());

		return validator;
	}

	protected EntityManager createEntityManager(File pathToConfigure) {
		return new EntityManager(pathToConfigure);
	}
}
