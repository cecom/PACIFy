package de.oppermann.maven.pflist.mavenplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.Properties;

/**
 * User: sop
 * Date: 10.05.11
 * Time: 10:08
 *
 * @goal loadPropertyFile
 */
public class LoadPropertyFileMojo extends AbstractMojo {

    final String IMPORT_STRING = "#!import";

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Which property file should be used?
     *
     * @parameter
     * @required
     */
    private String propertyFilePath;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Properties properties = getPropertiesFromFile();

        project.getProperties().putAll(properties);
    }

    private Properties getPropertiesFromFile() throws MojoExecutionException {
        Properties properties = new Properties();
        try {
            File resultFile = File.createTempFile("LoadPropertyFileMojo", "tmp");
            resultFile.deleteOnExit();

            InputStream is = this.getClass().getClassLoader().getResourceAsStream(propertyFilePath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile));

            parsePropertyFile(bw, is);

            try {
                properties.load(new FileReader(resultFile));
            } finally {
                if (is != null) {
                    is.close();
                }
                if (bw != null) {
                    bw.close();
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error while reading the PropertyFiles from classpath.", e);
        }
        return properties;
    }

    private void parsePropertyFile(BufferedWriter bw, InputStream parseStream) throws IOException, MojoExecutionException {
        InputStreamReader isr = new InputStreamReader(parseStream);
        BufferedReader br = new BufferedReader(isr);

        for (String line; (line = br.readLine()) != null;) {
            if (line.startsWith(IMPORT_STRING)) {
                String[] propertyFiles = line.substring(IMPORT_STRING.length()).trim().split(" ");
                for (String propertyFile : propertyFiles) {
                    InputStream propertyFileInputStream = this.getClass().getClassLoader().getResourceAsStream(propertyFile);
                    if (propertyFileInputStream == null)
                        throw new MojoExecutionException("Couldnt find resource [" + propertyFile + "] in classpath.");
                    parsePropertyFile(bw, propertyFileInputStream);
                }
                continue;
            }
            bw.write(line);
            bw.newLine();
        }
        bw.flush();
    }
}