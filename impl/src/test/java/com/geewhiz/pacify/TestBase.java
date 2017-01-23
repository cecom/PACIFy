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

package com.geewhiz.pacify;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.geewhiz.pacify.checks.Check;
import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.HashMapPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.test.TestUtil;

public abstract class TestBase {

    // public LinkedHashSet<Defect> getDefects(PMarkerCheck checker, String testFolder) {
    // File testStartPath = new File(getTargetResourceFolder(testFolder), "package");
    //
    // EntityManager entityManager = new EntityManager(testStartPath);
    //
    // LinkedHashSet<Defect> defects = entityManager.initialize();
    // for (PMarker pMarker : entityManager.getPMarkers()) {
    // defects.addAll(checker.checkForErrors(entityManager, pMarker));
    // }
    // return defects;
    // }

    public LinkedHashSet<Defect> createPrepareAndReplace(String testFolder, PropertyResolveManager propertyResolveManager) {
        return executePacify(testFolder, propertyResolveManager, false);
    }

    public LinkedHashSet<Defect> createPrepareValidateAndReplace(String testFolder, PropertyResolveManager propertyResolveManager) {
        return executePacify(testFolder, propertyResolveManager, true);
    }

    private LinkedHashSet<Defect> executePacify(String testFolder, PropertyResolveManager propertyResolveManager, boolean withValidate) {
        File testResourceFolder = getTestResourceFolder(testFolder);
        File targetResourceFolder = getTargetResourceFolder(testFolder);

        File packagePath = new File(targetResourceFolder, "package");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        Replacer replacer = createReplacer(propertyResolveManager, packagePath);

        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        // execute initialize
        defects.addAll(replacer.getEntityManager().initialize());

        // execute validation
        if (withValidate) {
            defects.addAll(replacer.validate());
        }

        // execute replacer
        defects.addAll(replacer.doReplacement());

        return defects;
    }

    public LinkedHashSet<Defect> createPrepareAndExecuteValidator(String testFolder, PropertyResolveManager propertyResolveManager,
            PMarkerCheck pMarkerChecktoExecute) {
        return createPrepareAndExecuteValidator(testFolder, propertyResolveManager, pMarkerChecktoExecute, null);
    }

    public LinkedHashSet<Defect> createPrepareAndExecuteValidator(String testFolder, PropertyResolveManager propertyResolveManager, Check checktoExecute) {
        return createPrepareAndExecuteValidator(testFolder, propertyResolveManager, null, checktoExecute);
    }

    public LinkedHashSet<Defect> createPrepareAndExecuteValidator(String testFolder, PropertyResolveManager propertyResolveManager,
            PMarkerCheck pMarkerChecktoExecute, Check checkToExecute) {
        File testResourceFolder = getTestResourceFolder(testFolder);
        File targetResourceFolder = getTargetResourceFolder(testFolder);

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File packagePath = new File(targetResourceFolder, "package");

        Validator validator = createValidator(propertyResolveManager, packagePath);
        if (checkToExecute == null && pMarkerChecktoExecute == null) {
            validator.enableMarkerFileChecks();
            validator.enablePropertyResolveChecks();
        } else {
            if (pMarkerChecktoExecute != null) {
                validator.addPMarkerCheck(pMarkerChecktoExecute);
            }
            if (checkToExecute != null) {
                validator.addCheck(checkToExecute);
            }
        }

        return validator.validateInternal();
    }

    public LinkedHashSet<Defect> createPrepareAndExecuteValidator(String testFolder, PropertyResolveManager propertyResolveManager) {
        return createPrepareAndExecuteValidator(testFolder, propertyResolveManager, null, null);
    }

    public File getTestResourceFolder(String testFolder) {
        return new File("src/test/resources/" + testFolder);
    }

    public File getTargetResourceFolder(String testFolder) {
        return new File("target/test-resources/" + testFolder);
    }

    public PropertyResolveManager createPropertyResolveManager(Map<String, String> propertiesWhichWillBeUsedWhileResolving) {
        HashMapPropertyResolver hpr = new HashMapPropertyResolver(propertiesWhichWillBeUsedWhileResolving);

        Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
        propertyResolverList.add(hpr);
        PropertyResolveManager prm = new PropertyResolveManager(propertyResolverList);
        return prm;
    }

    protected Replacer createReplacer(PropertyResolveManager propertyResolveManager, File packagePath) {
        Replacer replacer = new Replacer(propertyResolveManager);

        replacer.setPackagePath(packagePath);

        return replacer;
    }

    protected Validator createValidator(PropertyResolveManager propertyResolveManager, File packagePath) {
        Validator validator = new Validator(propertyResolveManager);
        validator.setPackagePath(packagePath);

        return validator;
    }

    public void checkIfResultIsAsExpected(String testFolder) {
        TestUtil.checkIfResultIsAsExpected(testFolder);
    }

}
