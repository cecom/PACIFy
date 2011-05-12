package de.oppermann.maven.pflist.utils;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

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
}
