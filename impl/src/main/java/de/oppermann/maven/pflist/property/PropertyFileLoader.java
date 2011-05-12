package de.oppermann.maven.pflist.property;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

/**
 * User: sop
 * Date: 12.05.11
 * Time: 15:04
 */
public class PropertyFileLoader {

    final String IMPORT_STRING = "#!import";

    public PropertyFileLoader() {
    }

    public Properties loadProperties(URL propertyFilePathURL) {
        Properties properties = new Properties();
        try {
            File resultFile = getResultFile();

            BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile));

            parsePropertyFile(bw, propertyFilePathURL);

            try {
                properties.load(new FileReader(resultFile));
            } finally {
                bw.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while reading the PropertyFiles from classpath.", e);
        }
        return properties;
    }

    private void parsePropertyFile(BufferedWriter bw, URL propertyFilePathURL) throws IOException {
        InputStream is = getInputStreamFor(propertyFilePathURL);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        for (String line; (line = br.readLine()) != null;) {
            if (line.startsWith(IMPORT_STRING)) {
                String[] includes = line.substring(IMPORT_STRING.length()).trim().split(" ");
                for (String include : includes) {
                    URI includeURI = null;
                    try {
                        includeURI = propertyFilePathURL.toURI().resolve(include);
                        URL url = includeURI.toURL();
                        parsePropertyFile(bw, url);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
                continue;
            }
            bw.write(line);
            bw.newLine();
        }
        bw.flush();
    }

    private File getResultFile() throws IOException {
        File resultFile = File.createTempFile(UUID.randomUUID().toString(), "tmp");
        resultFile.deleteOnExit();
        return resultFile;
    }

    private InputStream getInputStreamFor(URL propertyFilePathURL) throws FileNotFoundException {
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
