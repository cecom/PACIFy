package com.geewhiz.pacify.model;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.geewhiz.pacify.checker.PFListChecker;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.logger.Log;
import com.geewhiz.pacify.logger.LogLevel;
import com.geewhiz.pacify.model.utils.PFListFilesFinder;
import com.geewhiz.pacify.property.PropertyContainer;
import com.geewhiz.pacify.replacer.PropertyPFReplacer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PFEntityManager {

    private File startPath;
    private List<PFListEntity> pfListEntities;

    public PFEntityManager(File startPath) {
        this.startPath = startPath;
    }

    public int getPFListCount() {
        return getPFLists().size();
    }

    public List<Defect> checkCorrectnessOfPFListFiles(PropertyContainer propertyContainer) {
        PFListChecker pfListChecker = new PFListChecker(propertyContainer);

        List<Defect> defects = new ArrayList<Defect>();
        for (PFListEntity pfListEntity : getPFLists())
            defects.addAll(pfListChecker.check(pfListEntity));

        return defects;
    }

    public List<Defect> doReplacement(PropertyContainer propertyContainer) {
        List<Defect> defects = new ArrayList<Defect>();
        for (PFListEntity pfListEntity : getPFLists()) {
            Log.log(LogLevel.INFO, "====== Replacing stuff which is configured in [" + pfListEntity.getFile().getPath() + "] ...");
            PropertyPFReplacer propertyReplacer = new PropertyPFReplacer(propertyContainer, pfListEntity);
            defects.addAll(propertyReplacer.replace());
        }
        return defects;
    }

    public List<PFListEntity> getPFLists() {
        if (pfListEntities == null) {
            pfListEntities = new ArrayList<PFListEntity>();
            List<File> pfListFiles = new PFListFilesFinder(startPath).getPFListFiles();
            Serializer serializer = new Persister();
            for (File file : pfListFiles) {
                try {
                    PFListEntity pfListEntity = serializer.read(PFListEntity.class, file);
                    pfListEntity.setFile(file);
                    pfListEntities.add(pfListEntity);
                } catch (Exception e) {
                    throw new RuntimeException("Couldn't read xml file [" + file.getPath() + "].", e);
                }
            }
        }
        return pfListEntities;
    }
}

