package org.jndiresources.maven;

/*
 * Copyright 2008 Philippe Prados.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.jndiresources.plugin.Plugin;

public final class ManageArtifact
{
	private static final String CENTRAL_REPO="http://repo1.maven.org/maven2";

	private static HashMap caches_=new HashMap();
	
	/**
	 * Not used.
	 */
	private ManageArtifact()
	{
		
	}
	/**
	 * Load the artifact in local repository, and return the file path. Use a cache.
	 * 
	 * @param artifact The syntax is <groupid>:<artifactid>:<version>
	 * @return The path in local repository.
	 * 
	 * @throws MavenException If the artifact is not found or can't be download in local repository.
	 * @throws ArtifactNotFoundException If Artifact is not found.
	 * @throws ResourceDoesNotExistException 
	 */
	public static String getArtifact(final String artifact)
			throws MavenException, ArtifactNotFoundException, ResourceDoesNotExistException
	{
		if (artifact==null) throw new IllegalArgumentException("Artifact can't be null");
		
		
		String result=(String)caches_.get(artifact);
		if (result!=null) return result;
		if (artifact.startsWith("org.jndi-resources:jndi-resources-plugins"))
		{
			result=Plugin.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		}
		else
		{
			final StringTokenizer tokens=new StringTokenizer(artifact,":");
			result=getArtifact(tokens.nextToken(),tokens.nextToken(),tokens.nextToken(),null);
		}
		caches_.put(artifact, result);
		return result;
	}
	
	/**
	 * Load the artifact in local repository, and return the file path.
	 * 
	 * @param groupId The Maven group id.
	 * @param artifactId The Maven artifact id.
	 * @param version The Maven version.
	 * @param repositories A list of String with remoteRepositories or null.
	 * @return The path in local repository.
	 * @throws MavenException If error when dowload or identify the maven jar file. 
	 * @throws ArtifactNotFoundException If artifact is not found.
	 */
	private static MavenEmbedder maven_;
	public static String getArtifact(final String groupId,
			final String artifactId, final String version,
			final List repositories) throws MavenException,
			ArtifactNotFoundException,ResourceDoesNotExistException
	{
		// TODO : récuperer la bonne version en cas de supérieur...
		try
		{
	        if (maven_==null)
	        {
	        	maven_=new MavenEmbedder();
	        	maven_.setClassLoader(Thread.currentThread()
							.getContextClassLoader());
	        	maven_.setAlignWithUserInstallation(true);
	        	maven_.start();
	        }

	        final String scope=Artifact.RELEASE_VERSION;
	        final String type="jar";
			final ArtifactRepository localRepository=maven_.getLocalRepository();
	        
			final Artifact artifact=maven_.createArtifact(groupId, artifactId, version, scope, type);
			final List remoteRepositories=new ArrayList(); 

			if ((repositories==null) || repositories.isEmpty()) 
				remoteRepositories.add(maven_.createRepository(CENTRAL_REPO, "central"));
			else
			{
				int id=0;
				for (final Iterator i=repositories.iterator();i.hasNext();)
				{
					remoteRepositories.add(maven_.createRepository((String)i.next(),"central_"+id++));
				}
			}
			maven_.resolve(artifact, remoteRepositories, localRepository);
			return localRepository.getBasedir()+File.separatorChar+localRepository.pathOf(artifact);
		} 
		catch (ArtifactNotFoundException e)
		{
			throw (ArtifactNotFoundException)e.fillInStackTrace();
		}
		catch (Exception e)
		{
			throw new MavenException("Repository error",e);
		}
	}

	/**
	 * Load the artifact in local repository, and return the file path.
	 * 
	 * @param groupId The Maven group id.
	 * @param artifactId The Maven artifact id.
	 * @param version The Maven version.
	 * @param repositories A list of String with remoteRepositories or null.
	 * @return The path in local repository.
	 * @throws MavenException If error when dowload or identify the maven jar file. 
	 */
	public static String getArtifacts(final String groupId,
			final String artifactId, final String version,
			final List repositories) throws MavenException
	{
		try
		{
	        final String scope=Artifact.LATEST_VERSION;
	        final String type="jar";
	        
			final MavenEmbedder maven=new MavenEmbedder();
	        maven
					.setClassLoader(Thread.currentThread()
							.getContextClassLoader());
			maven.setAlignWithUserInstallation(true);
			maven.start();
			final ArtifactRepository localRepository=maven.getLocalRepository();
	        
			Artifact artifact;
			artifact=maven.createArtifact(groupId, artifactId, version, scope, type);
			final List remoteRepositories=new ArrayList(); 
			if ((repositories==null) || repositories.isEmpty()) 
				remoteRepositories.add(maven.createRepository(CENTRAL_REPO, "central"));
			else
			{
				int id=0;
				for (final Iterator i=repositories.iterator();i.hasNext();)
				{
					remoteRepositories.add(maven.createRepository((String)i.next(),"central_"+id++));
				}
			}
			maven.resolveAll(artifact, remoteRepositories, localRepository);
			return null;
		}
		catch (Exception e)
		{
			throw new MavenException("Repository error",e);
		}
	}
	
	/**
	 * Unit test.
	 * @param args The command line.
	 */
	public static void main(final String[] args)
	{
		try
		{
	        final String groupId="commons-logging";
	        final String artifactId="commons-logging";
	        final String version="1.0.4";
	        final List l=new ArrayList();
//	        l.add("http://repo1.maven.org/maven2");
//	        l.add("http://repo2.maven.org/maven2");
	        System.out.println("PATH="+getArtifact(groupId,artifactId,version,l));
	        getArtifacts(groupId, artifactId, version, l);
	        
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}

	}

}
