package de.oppermann.maven.pflist.xml;

import de.oppermann.maven.pflist.checker.PFListChecker;
import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.logger.Log;
import de.oppermann.maven.pflist.logger.LogLevel;
import de.oppermann.maven.pflist.property.PFProperties;
import de.oppermann.maven.pflist.replacer.PropertyReplacer;
import de.oppermann.maven.pflist.xml.utils.PFListFilesFinder;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PFManager {

    private File startPath;
    private List<PFList> pfLists;

    public PFManager(File startPath) {
        this.startPath = startPath;
    }

    public int getPFListCount() {
        return getPFLists().size();
    }

    public List<Defect> checkCorrectnessOfPFListFiles(PFProperties pfProperties) {
        PFListChecker pfListChecker = new PFListChecker(pfProperties);

        List<Defect> defects = new ArrayList<Defect>();
        for (PFList pfList : getPFLists())
            defects.addAll(pfListChecker.check(pfList));

        return defects;
    }

    public List<Defect> doReplacement(PFProperties pfProperties) {
        List<Defect> defects = new ArrayList<Defect>();
        for (PFList pfList : getPFLists()) {
            Log.log(LogLevel.INFO, "====== Replacing stuff which is configured in [" + pfList.getFile().getPath() + "] ...");
            PropertyReplacer propertyReplacer = new PropertyReplacer(pfProperties,pfList);
            defects.addAll(propertyReplacer.replace());
        }
        return defects;
    }

    public List<PFList> getPFLists() {
        if (pfLists == null) {
            pfLists = new ArrayList<PFList>();
            List<File> pfListFiles = new PFListFilesFinder(startPath).getPFListFiles();
            Serializer serializer = new Persister();
            for (File file : pfListFiles) {
                try {
                    PFList pfList = serializer.read(PFList.class, file);
                    pfList.setFile(file);
                    pfLists.add(pfList);
                } catch (Exception e) {
                    throw new RuntimeException("Couldn't read xml file [" + file.getPath() + "].", e);
                }
            }
        }
        return pfLists;
    }
}
