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

import java.util.Iterator;
import java.util.LinkedHashSet;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.checks.impl.CheckPropertyExists;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.NotReplacedPropertyDefect;
import com.geewhiz.pacify.defect.PropertyNotDefinedInResolverDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.google.inject.Inject;

public class PreConfigure extends Replacer {

    @Inject
    public PreConfigure(PropertyResolveManager propertyResolveManager) {
        super(propertyResolveManager);
    }

    @Override
    protected Validator createValidator() {
        Validator validator = super.createValidator();

        Iterator<PMarkerCheck> checks = validator.getPMarkerChecks().iterator();
        while (checks.hasNext()) {
            PMarkerCheck check = checks.next();
            // remove the CheckPropertyExists
            if (check instanceof CheckPropertyExists) {
                checks.remove();
            }
        }

        return validator;
    }

    @Override
    public LinkedHashSet<Defect> doReplacement(EntityManager entityManager) {
        LinkedHashSet<Defect> defects = super.doReplacement(entityManager);

        Iterator<Defect> defectIter = defects.iterator();
        while (defectIter.hasNext()) {
            Defect defect = defectIter.next();
            if (defect instanceof NotReplacedPropertyDefect) {
                defectIter.remove();
            }
            if (defect instanceof PropertyNotDefinedInResolverDefect) {
                defectIter.remove();
            }
        }

        return defects;
    }

}
