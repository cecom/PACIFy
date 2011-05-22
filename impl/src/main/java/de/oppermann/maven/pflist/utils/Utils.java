package de.oppermann.maven.pflist.utils;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyNotReplacedDefect;
import de.oppermann.maven.pflist.replacer.PropertyFileReplacer;
import de.oppermann.maven.pflist.replacer.PropertyPFReplacer;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class Utils {

    public static String getJarVersion() {
        URL jarURL = Utils.class.getResource("/de/oppermann/maven/pflist/PFListPropertyReplacer.class");
        Manifest mf;
        try {
            JarURLConnection jurlConn;
            if (jarURL.getProtocol().equals("file"))
                return "Not a Jar";
            else {
                jurlConn = (JarURLConnection) jarURL.openConnection();
            }
            mf = jurlConn.getManifest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Attributes attr = mf.getMainAttributes();
        return attr.getValue("Implementation-Version");
    }

    public static List<Defect> checkFileForNotReplacedStuff(File file) {
        List<Defect> defects = new ArrayList<Defect>();

        String fileContent = de.oppermann.maven.pflist.utils.FileUtils.getFileInOneString(file);

        Pattern pattern = PropertyFileReplacer.getPattern("([^}]*)", false);
        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {
            String propertyId = matcher.group(1);
            Defect defect = new PropertyNotReplacedDefect(file, propertyId);
            defects.add(defect);
        }
        return defects;
    }
}
