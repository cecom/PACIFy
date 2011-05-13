package de.oppermann.maven.pflist.property;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * User: sop
 * Date: 12.05.11
 * Time: 15:04
 */
public class PropertyFileLoaderUtils {

    public static void loadPropertyFile(PropertyFile propertyFile) {
        try {
            URL propertyFileURL = propertyFile.getPropertyFileURL();

            InputStream is = getInputStreamFor(propertyFileURL);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(byteArray));

            for (String line; (line = br.readLine()) != null;) {
                if (line.startsWith(PropertyFile.IMPORT_STRING)) {
                    String[] includes = line.substring(PropertyFile.IMPORT_STRING.length()).trim().split(" ");
                    for (String include : includes) {
                        URL parentPropertyFileURL = new URL(propertyFileURL, include);
                        PropertyFile parentPropertyFile = new PropertyFile(parentPropertyFileURL);
                        propertyFile.addParentPropertyFile(parentPropertyFile);
                    }
                    continue;
                }
                bw.write(line);
                bw.newLine();
            }
            bw.flush();

            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(byteArray.toByteArray()));
            propertyFile.setLocalProperties(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getInputStreamFor(URL propertyFilePathURL) {
        InputStream result;
        try {
            result = propertyFilePathURL.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (result == null)
            throw new RuntimeException("Couldn't find resource [" + propertyFilePathURL + "] in classpath.");
        return result;
    }

}
