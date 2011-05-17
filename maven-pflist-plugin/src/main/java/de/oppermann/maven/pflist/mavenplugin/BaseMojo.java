package de.oppermann.maven.pflist.mavenplugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
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
     * Which property file should be used?
     *
     * @parameter expression="${pflist.usePropertyFile}"
     */
    protected String propertyFile;

    /**
     * In which jar is the propertyFile contained?
     *
     * @parameter
     * @required
     */
    protected String propertyFileArtifact;

    /**
     * Should it be skipped??
     *
     * @parameter expression="${pflist.skip}" default-value="false"
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


    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("PFList is skipped. Nothing to do.");
            return;
        }
        executePFList();
    }

    protected abstract void executePFList() throws MojoExecutionException;

    protected URL getPropertyFileURL() throws MojoExecutionException {
        if (propertyFile == null) {
            getLog().info("No pflist property file given. Nothing to do.");
            return null;
        }

        try {
            Artifact artifact = getArtifact();

            artifactResolver.resolve(artifact, remoteRepositories, localRepository);

            ClassLoader cl = new URLClassLoader(new URL[]{artifact.getFile().toURI().toURL()});

            URL propertyFileURL = cl.getResource(propertyFile);

            if (propertyFileURL == null)
                throw new MojoExecutionException("Couldn't find property file [" + propertyFile + "] in [" + propertyFileArtifact + "]... Aborting!");

            getLog().info("Loading properties from [" + propertyFileURL.getPath() + "]... ");

            return propertyFileURL;

        } catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Couldn't resolve artifact [" + propertyFileArtifact + "].", e);
        } catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Couldn't find artifact [" + propertyFileArtifact + "].", e);
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Couldn't find artifact [" + propertyFileArtifact + "].", e);
        }
    }

    private Artifact getArtifact() throws MojoExecutionException {
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