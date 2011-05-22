package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyDoesNotExistInTargetFile;
import de.oppermann.maven.pflist.model.PFPropertyEntity;
import de.oppermann.maven.pflist.replacer.PropertyFileReplacer;
import de.oppermann.maven.pflist.utils.FileUtils;
import de.oppermann.maven.pflist.model.PFFileEntity;
import de.oppermann.maven.pflist.model.PFListEntity;

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

    public List<Defect> checkForErrors(PFListEntity pfListEntity) {
        List<Defect> defects = new ArrayList<Defect>();

        for (PFPropertyEntity pfPropertyEntity : pfListEntity.getPfPropertyEntities()) {
            for (PFFileEntity pfFileEntity : pfPropertyEntity.getPFFileEntities()) {
                File file = pfListEntity.getAbsoluteFileFor(pfFileEntity);
                boolean exists = doesPropertyExistInFile(pfPropertyEntity, file);
                if (exists)
                    continue;
                Defect defect = new PropertyDoesNotExistInTargetFile(pfListEntity, pfPropertyEntity, pfFileEntity);
                defects.add(defect);
            }
        }

        return defects;
    }

    public boolean doesPropertyExistInFile(PFPropertyEntity pfPropertyEntity, File file) {
        String fileContent = FileUtils.getFileInOneString(file);

        Pattern pattern = PropertyFileReplacer.getPattern(pfPropertyEntity.getId(), true);
        Matcher matcher = pattern.matcher(fileContent);

        return matcher.find();
    }
}