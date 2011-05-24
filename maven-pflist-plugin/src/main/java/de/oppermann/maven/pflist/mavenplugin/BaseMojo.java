package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.logger.Log;
import de.oppermann.maven.pflist.logger.LogLevel;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * User: sop
 * Date: 14.05.11
 * Time: 11:07
 */
public abstract class BaseMojo extends AbstractMojo {

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Should it be skipped??
     *
     * @parameter expression="${skipPFList}" default-value="false"
     */
    protected boolean skip;

    /**
     * @component
     */
    private org.apache.maven.artifact.factory.ArtifactFactory artifactFactory;

    /**
     * @component
     */
    private org.apache.maven.artifact.resolver.ArtifactResolver artifactResolver;

    /**
     * @parameter default-value="${localRepository}"
     */
    private org.apache.maven.artifact.repository.ArtifactRepository localRepository;

    /**
     * @parameter default-value="${project.remoteArtifactRepositories}"
     */
    private java.util.List remoteRepositories;


    /**
     * @parameter expression="${logLevel}" default-value="ERROR"
     * @required
     */
    private String logLevel;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("PFList is skipped.");
            return;
        }

        Log.getInstance().setLogLevel(LogLevel.valueOf(logLevel.toUpperCase()));
        executePFList();
    }

    protected abstract void executePFList() throws MojoExecutionException;

    protected URL getPropertyFileURL(String propertyFileArtifact,String propertyFile) throws MojoExecutionException {
        if (propertyFile == null)
            throw new MojoExecutionException("You didn't define the propertyFile... Aborting!");

        try {
            Artifact artifact = getArtifact(propertyFileArtifact);

            artifactResolver.resolve(artifact, remoteRepositories, localRepository);

            ClassLoader cl = new URLClassLoader(new URL[]{artifact.getFile().toURI().toURL()});

            URL propertyFileURL = cl.getResource(propertyFile);

            if (propertyFileURL == null)
                throw new MojoExecutionException("Couldn't find property file [" + propertyFile + "] in [" + propertyFileArtifact + "]... Aborting!");

            return propertyFileURL;

        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Couldn't resolve artifact [" + propertyFileArtifact + "].", e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Couldn't find artifact [" + propertyFileArtifact + "].", e);
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Couldn't find artifact [" + propertyFileArtifact + "].", e);
        }
    }

    private Artifact getArtifact(String propertyFileArtifact) throws MojoExecutionException {
        String[] artifactParts = propertyFileArtifact.split(":");

        String groupId;
        String artifactId;
        String type;
        String version;
        String classifier = null;

        switch (artifactParts.length) {
            case 5:
                classifier = artifactParts[4];
            case 4:
                groupId = artifactParts[0];
                artifactId = artifactParts[1];
                type = artifactParts[2];
                version = artifactParts[3];
                break;
            default:
                throw new MojoExecutionException("Couldn't parse propertyFileArtifact [" + propertyFileArtifact + "] string.");
        }
        return artifactFactory.createArtifactWithClassifier(groupId, artifactId, version, type, classifier);
    }
}