package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyDoesNotExistInTargetFile;
import de.oppermann.maven.pflist.replacer.PropertyReplacer;
import de.oppermann.maven.pflist.utils.FileUtils;
import de.oppermann.maven.pflist.xml.PFFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFListProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyExistsInTargetFile implements PFListCheck {

    public List<Defect> checkForErrors(PFList pfList) {
        List<Defect> defects = new ArrayList<Defect>();
        for (PFListProperty pfListProperty : pfList.getPfListProperties()) {
            for (PFFile pfFile : pfListProperty.getPFFiles()) {
                File file = pfList.getAbsoluteFileFor(pfFile);
                boolean exists = doesPropertyExistInFile(pfListProperty, file);
                if (exists)
                    continue;
                Defect defect = new PropertyDoesNotExistInTargetFile(pfList, pfListProperty, pfFile);
                defects.add(defect);
            }
        }
        return defects;
    }

    public boolean doesPropertyExistInFile(PFListProperty pfListProperty, File file) {
        String searchPattern = PropertyReplacer.BEGIN_TOKEN + pfListProperty.getId() + PropertyReplacer.END_TOKEN;

        String fileContent = FileUtils.getFileInOneString(file);

        Pattern pattern = Pattern.compile(Pattern.quote(searchPattern));
        Matcher matcher = pattern.matcher(fileContent);

        return matcher.find();
    }
}