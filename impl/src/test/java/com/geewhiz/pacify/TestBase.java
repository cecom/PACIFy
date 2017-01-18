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

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.property.resolver.HashMapPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.test.TestUtil;

public abstract class TestBase {

    public LinkedHashSet<Defect> getDefects(PMarkerCheck checker, File testStartPath) {
        EntityManager entityManager = new EntityManager(testStartPath);

        LinkedHashSet<Defect> defects = entityManager.initialize();
        for (PMarker pMarker : entityManager.getPMarkers()) {
            defects.addAll(checker.checkForErrors(entityManager, pMarker));
        }
        return defects;
    }

    public LinkedHashSet<Defect> createPrepareAndExecutePacify(String testFolder, Map<String, String> propertiesWhichWillBeUsedWhileResolving) {
        File testResourceFolder = getTestResourceFolder(testFolder);
        File targetResourceFolder = getTargetResourceFolder(testFolder);

        File packagePath = new File(targetResourceFolder, "package");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        PropertyResolveManager propertyResolveManager = createPropertyResolveManager(propertiesWhichWillBeUsedWhileResolving);
        Replacer replacer = createReplacer(propertyResolveManager, packagePath);

        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        // execute initialize
        defects.addAll(replacer.getEntityManager().initialize());

        // execute validation
        defects.addAll(replacer.validate());

        // execute replacer
        defects.addAll(replacer.doReplacement());

        return defects;
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

    public Replacer createReplacer(PropertyResolveManager propertyResolveManager, File packagePath) {
        Replacer replacer = new Replacer(propertyResolveManager);

        replacer.setPackagePath(packagePath);

        return replacer;
    }

}
