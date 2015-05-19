package com.geewhiz.pacify.mavenplugin;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

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
	 * @parameter expression="${skipPacify}" default-value="false"
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
			getLog().info("Pacify is skipped.");
			return;
		}

		executePacify();
	}

	protected abstract void executePacify() throws MojoExecutionException;

	protected URL getPropertyFileURL(String propertyFileArtifact, String propertyFile) throws MojoExecutionException {
		if (propertyFile == null) {
			throw new MojoExecutionException("You didn't define the propertyFile... Aborting!");
		}

		try {
			Artifact artifact = getArtifact(propertyFileArtifact);

			artifactResolver.resolve(artifact, remoteRepositories, localRepository);

			ClassLoader cl = new URLClassLoader(new URL[] { artifact.getFile().toURI().toURL() });

			URL propertyFileURL = cl.getResource(propertyFile);

			if (propertyFileURL == null) {
				throw new MojoExecutionException("Couldn't find property file [" + propertyFile + "] in ["
				        + propertyFileArtifact + "]... Aborting!");
			}

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
				throw new MojoExecutionException("Couldn't parse propertyFileArtifact [" + propertyFileArtifact
				        + "] string.");
		}
		return artifactFactory.createArtifactWithClassifier(groupId, artifactId, version, type, classifier);
	}

}