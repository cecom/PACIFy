package com.geewhiz.pacify;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.checks.impl.CheckPlaceholderExistsInTargetFile;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PlaceholderNotDefinedDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.HashMapPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.test.TestUtil;
import com.geewhiz.pacify.utils.LoggingUtils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

public class BugFixing extends TestBase {

    @Test
    public void Bug1() {
        Logger logger = LogManager.getLogger();
        LoggingUtils.setLogLevel(logger, Level.INFO);

        File testResourceFolder = new File("src/test/resources/Bugfixing/Bug1");
        File targetResourceFolder = new File("target/test-resources/Bugfixing/Bug1");

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());
    }
    
    private LinkedHashSet<Defect> createPrepareAndExecuteValidator(File testResourceFolder, File targetResourceFolder) {
        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File packagePath = new File(targetResourceFolder, "package");

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        EntityManager entityManager = new EntityManager(packagePath);
        LinkedHashSet<Defect> defects = entityManager.initialize();

        Validator validator = new Validator(prm);
        validator.setPackagePath(packagePath);
        validator.enableMarkerFileChecks();
        validator.enablePropertyResolveChecks();

        defects.addAll(validator.validateInternal(entityManager));
        
        return defects;
    }

    private PropertyResolveManager getPropertyResolveManager(HashMapPropertyResolver hpr) {
        hpr.addProperty("bug1", "bug1Value");

        Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
        propertyResolverList.add(hpr);
        PropertyResolveManager prm = new PropertyResolveManager(propertyResolverList);
        return prm;
    }
    
}
