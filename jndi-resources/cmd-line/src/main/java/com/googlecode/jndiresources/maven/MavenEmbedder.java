package com.googlecode.jndiresources.maven;

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
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.artifact.MavenMetadataSource;
import org.apache.maven.settings.MavenSettingsBuilder;
import org.apache.maven.settings.Settings;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Class intended to be used by clients who wish to embed Maven into their
 * applications.
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Philippe PRADOS
 */
final class MavenEmbedder
{
	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(MavenEmbedder.class);

	/**
	 * Current user home directory.
	 */
	private static final String USER_HOME = System.getProperty("user.home");

	// ----------------------------------------------------------------------
	// Embedder
	// ----------------------------------------------------------------------

	/**
	 * The singleton.
	 */
	private Embedder embedder_;

	// ----------------------------------------------------------------------
	// Components
	// ----------------------------------------------------------------------

	/**
	 * The repository factory.
	 */
	private ArtifactRepositoryFactory artifactRepositoryFactory_;

	/**
	 * The settings builder.
	 */
	private MavenSettingsBuilder settingsBuilder_;

	/**
	 * The artifact factory.
	 */
	private ArtifactFactory artifactFactory_;

	/**
	 * The artifact resolver.
	 */
	private ArtifactResolver artifactResolver_;

	/**
	 * The default artifact repository layout.
	 */
	private ArtifactRepositoryLayout defaultArtifactRepositoryLayout_;

	// TODO private ArtifactMetadataSource defaultMetadataSource_;

	// ----------------------------------------------------------------------
	// Configuration
	// ----------------------------------------------------------------------

	/**
	 * The settings.
	 */
	private Settings settings_;

	/**
	 * The local repository.
	 */
	private ArtifactRepository localRepository_;

	/**
	 * The local repository directory.
	 */
	private File localRepositoryDirectory_;

	/**
	 * The class loader.
	 */
	private ClassLoader classLoader_;

	// ----------------------------------------------------------------------
	// User options
	// ----------------------------------------------------------------------

	// release plugin uses this but in IDE there will probably always be some
	// form of interaction.
	/**
	 * The interactive mode.
	 */
	private boolean interactiveMode_;

	/**
	 * The offline mode.
	 */
	private boolean offline_;

	/**
	 * The global checksum policy.
	 */
	private String globalChecksumPolicy_;

	/**
	 * This option determines whether the embedder is to be aligned to the user
	 * installation.
	 */
	private boolean alignWithUserInstallation_;

	// ----------------------------------------------------------------------
	// Accessors
	// ----------------------------------------------------------------------

	/**
	 * Set interactive mode.
	 * 
	 * @param interactiveMode The new interactive mode.
	 */
	public void setInteractiveMode(final boolean interactiveMode)
	{
		this.interactiveMode_ = interactiveMode;
	}

	/**
	 * Get interactive mode.
	 * 
	 * @return interactive mode.
	 */
	public boolean isInteractiveMode()
	{
		return interactiveMode_;
	}

	/**
	 * Set off line mode.
	 * 
	 * @param offline The off line mode.
	 */
	public void setOffline(final boolean offline)
	{
		this.offline_ = offline;
	}

	/**
	 * Get off line mode.
	 * 
	 * @return Off line mode.
	 */
	public boolean isOffline()
	{
		return offline_;
	}

	/**
	 * Set global checksum policy.
	 * 
	 * @param globalChecksumPolicy The new checksum policy.
	 */
	public void setGlobalChecksumPolicy(final String globalChecksumPolicy)
	{
		this.globalChecksumPolicy_ = globalChecksumPolicy;
	}

	/**
	 * Get global checksum policy.
	 * 
	 * @return global checksum policy.
	 */
	public String getGlobalChecksumPolicy()
	{
		return globalChecksumPolicy_;
	}

	/**
	 * Set align with user installation.
	 * 
	 * @param alignWithUserInstallation The new align with user installation status.
	 */
	public void setAlignWithUserInstallation(final boolean alignWithUserInstallation)
	{
		this.alignWithUserInstallation_ = alignWithUserInstallation;
	}

	/**
	 * Get align with user installation status.
	 * @return align with user installation status.
	 */
	public boolean isAlignWithUserInstallation()
	{
		return alignWithUserInstallation_;
	}

	/**
	 * Set the class loader to use with the maven embedder.
	 * 
	 * @param classLoader The class loader.
	 */
	public void setClassLoader(final ClassLoader classLoader)
	{
		this.classLoader_ = classLoader;
	}

	/**
	 * Get class loader.
	 * 
	 * @return The class loader.
	 */
	public ClassLoader getClassLoader()
	{
		return classLoader_;
	}

	/**
	 * Get local repository.
	 * 
	 * @return Local repository.
	 */
	public ArtifactRepository getLocalRepository()
	{
		return localRepository_;
	}

	// ----------------------------------------------------------------------
	// Artifacts
	// ----------------------------------------------------------------------

	/**
	 * Create artifact.
	 * 
	 * @param groupId The group id.
	 * @param artifactId The artifact id.
	 * @param version The artifact version.
	 * @param scope The artifact scope.
	 * @param type The artifact type.
	 * 
	 * @return The artifact.
	 */
	public Artifact createArtifact(final String groupId, final String artifactId, final String version,
			final String scope, final String type)
	{
		return artifactFactory_.createArtifact(
			groupId, artifactId, version, scope, type);
	}

	/**
	 * Resolve artifact.
	 * 
	 * @param artifact The artifact to resolve.
	 * @param remoteRepositories A list of remote repositories.
	 * @param localRepository The local repository.
	 * 
	 * @throws ArtifactResolutionException If it's impossible to resolve artifact.
	 * @throws ArtifactNotFoundException If the artifact is not found.
	 */
	public void resolve(final Artifact artifact, final List remoteRepositories,
			final ArtifactRepository localRepository) throws ArtifactResolutionException,
			ArtifactNotFoundException
	{
		artifactResolver_.resolve(
			artifact, remoteRepositories, localRepository);
	}

	/**
	 * Resolve all artifact.
	 * 
	 * @param artifact The artifact to resolve.
	 * @param remoteRepositories A list of remote repositories.
	 * @param localRepository The local repository.
	 * 
	 * @throws ArtifactResolutionException If it's impossible to resolve artifact.
	 * @throws ArtifactNotFoundException If the artifact is not found.
	 */
	public void resolveAll(final Artifact artifact, final List remoteRepositories,
			final ArtifactRepository localRepository) throws ArtifactResolutionException,
			ArtifactNotFoundException
	{
		final Set set = new HashSet();
		set.add(artifact);
		final ArtifactResolutionResult result = artifactResolver_.resolveTransitively(
			set, artifact, remoteRepositories, localRepository, new MavenMetadataSource()); 
		// TODO : J'ai ajouté maven-project pour cela :-( Est-ce vraiement nécessaire ?
		for (final Iterator i = result.getArtifacts().iterator(); i.hasNext();)
		{
			System.out.println(i.next());
		}
	}

	// ----------------------------------------------------------------------
	// Local Repository
	// ----------------------------------------------------------------------

	/**
	 * Default local repository id.
	 */
	private static final String DEFAULT_LOCAL_REPO_ID = "local";

	/**
	 * Default layout id.
	 */
	private static final String DEFAULT_LAYOUT_ID = "default";

	/**
	 * Create a local repository.
	 * 
	 * @param settings The settings.
	 * 
	 * @return The local repository.
	 * 
	 * @throws ComponentLookupException If error.
	 */
	private ArtifactRepository createLocalRepository(final Settings settings) throws ComponentLookupException
	{
		return createLocalRepository(
			settings.getLocalRepository(), DEFAULT_LOCAL_REPO_ID);
	}

	/**
	 * Create local repository.
	 * 
	 * @param url The url to local the repository.
	 * @param repositoryId The repository id.
	 * 
	 * @return The local repository.
	 * 
	 * @throws ComponentLookupException If error.
	 */
	private ArtifactRepository createLocalRepository(final String url, final String repositoryId)
			throws ComponentLookupException
	{
		if (!url.startsWith("file:"))
		{
			return createRepository(
				"file://" + url, repositoryId);
		}

		return createRepository(
			url, repositoryId);
	}

	/**
	 * Create a repository.
	 * 
	 * @param url The url of repository.
	 * @param repositoryId The repository id.
	 * 
	 * @return The repository.
	 * @throws ComponentLookupException If error.
	 */
	public ArtifactRepository createRepository(final String url, final String repositoryId)
			throws ComponentLookupException
	{

		final String updatePolicyFlag = ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS;

		final String checksumPolicyFlag = ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN;

		final ArtifactRepositoryPolicy snapshotsPolicy = new ArtifactRepositoryPolicy(true, updatePolicyFlag,
				checksumPolicyFlag);

		final ArtifactRepositoryPolicy releasesPolicy = new ArtifactRepositoryPolicy(true, updatePolicyFlag,
				checksumPolicyFlag);

		return artifactRepositoryFactory_.createArtifactRepository(
			repositoryId, url, defaultArtifactRepositoryLayout_, snapshotsPolicy, releasesPolicy);
	}

	// ----------------------------------------------------------------------
	// Lifecycle
	// ----------------------------------------------------------------------

	/**
	 * Start the maven embedded.
	 * @throws DuplicateRealmException If error.
	 * @throws PlexusContainerException If error.
	 * @throws ComponentLookupException If error.
	 * @throws IOException If error.
	 * @throws XmlPullParserException If error.
	 */
	public void start() throws DuplicateRealmException, PlexusContainerException, ComponentLookupException,
			IOException, XmlPullParserException
	{
		detectUserInstallation();

		// ----------------------------------------------------------------------
		// Set the maven.home system property which is need by components like
		// the plugin registry builder.
		// ----------------------------------------------------------------------

		if (classLoader_ == null)
		{
			throw new IllegalStateException(
					"A classloader must be specified using setClassLoader(ClassLoader).");
		}

		embedder_ = new Embedder();

		embedder_.setLoggerManager(new MavenEmbedderLoggerManager(new PlexusLogger()));

		final ClassWorld classWorld = new ClassWorld();

		classWorld.newRealm(
			"plexus.core", classLoader_);

		embedder_.start(classWorld);

		// ----------------------------------------------------------------------
		// Artifact related components
		// ----------------------------------------------------------------------
		artifactRepositoryFactory_ = (ArtifactRepositoryFactory) embedder_
				.lookup(ArtifactRepositoryFactory.ROLE);

		artifactFactory_ = (ArtifactFactory) embedder_.lookup(ArtifactFactory.ROLE);

		artifactResolver_ = (ArtifactResolver) embedder_.lookup(ArtifactResolver.ROLE);

		defaultArtifactRepositoryLayout_ = (ArtifactRepositoryLayout) embedder_.lookup(
			ArtifactRepositoryLayout.ROLE, DEFAULT_LAYOUT_ID);

		// TODO : gestion Artifact
		// defaultMetadatSource=(ArtifactMetadataSource)embedder.lookup(ArtifactMetadataSource.ROLE);
		// defaultMetadatSource=new ArtifactRepositoryMetadata(artifact);
		createMavenSettings();
		localRepository_ = createLocalRepository(settings_);
	}

	/**
	 * Logger.
	 */
	private static class PlexusLogger implements org.codehaus.plexus.logging.Logger
	{

		/**
		 * {@inheritDoc}
		 */
		public void debug(final String message)
		{
			LOG.debug(message);
		}

		/**
		 * {@inheritDoc}
		 */
		public void debug(final String message, final Throwable throwable)
		{
			LOG.debug(
				message, throwable);
		}

		/**
		 * {@inheritDoc}
		 */
		public void error(final String message)
		{
			LOG.error(message);
		}

		/**
		 * {@inheritDoc}
		 */
		public void error(final String message, final Throwable throwable)
		{
			LOG.error(
				message, throwable);
		}

		/**
		 * {@inheritDoc}
		 */
		public void fatalError(final String message)
		{
			LOG.fatal(message);
		}

		/**
		 * {@inheritDoc}
		 */
		public void fatalError(final String message, final Throwable throwable)
		{
			LOG.fatal(
				message, throwable);
		}

		/**
		 * {@inheritDoc}
		 */
		public void info(final String message)
		{
			LOG.info(message);
		}

		/**
		 * {@inheritDoc}
		 */
		public void info(final String message, final Throwable throwable)
		{
			LOG.info(
				message, throwable);
		}

		/**
		 * {@inheritDoc}
		 */
		public void warn(final String message)
		{
			LOG.warn(message);
		}

		/**
		 * {@inheritDoc}
		 */
		public void warn(final String message, final Throwable throwable)
		{
			LOG.warn(
				message, throwable);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isDebugEnabled()
		{
			return LOG.isDebugEnabled();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isErrorEnabled()
		{
			return LOG.isEnabledFor(Level.ERROR);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isFatalErrorEnabled()
		{
			return LOG.isEnabledFor(Level.FATAL);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isInfoEnabled()
		{
			return LOG.isEnabledFor(Level.INFO);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isWarnEnabled()
		{
			return LOG.isEnabledFor(Level.WARN);
		}

		/**
		 * {@inheritDoc}
		 */
		public org.codehaus.plexus.logging.Logger getChildLogger(final String arg0)
		{
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getName()
		{
			return "Maven";
		}

		/**
		 * Debig level.
		 */
		private static final int DEBUG_LEVEL = 0;

		/**
		 * Info level.
		 */
		private static final int INFO_LEVEL = 1;

		/**
		 * Warn level.
		 */
		private static final int WARN_LEVEL = 2;

		/**
		 * Error level.
		 */
		private static final int ERROR_LEVEL = 3;

		/**
		 * Fatal level.
		 */
		private static final int FATAL_LEVEL = 4;

		/**
		 * Other level.
		 */
		private static final int OTHER_LEVEL = 5;

		/**
		 * {@inheritDoc}
		 */
		public int getThreshold()
		{
			final Level level = LOG.getLevel();
			if (level.equals(Level.DEBUG))
				return DEBUG_LEVEL;
			if (level.equals(Level.INFO))
				return INFO_LEVEL;
			if (level.equals(Level.WARN))
				return WARN_LEVEL;
			if (level.equals(Level.ERROR))
				return ERROR_LEVEL;
			if (level.equals(Level.FATAL))
				return FATAL_LEVEL;
			return OTHER_LEVEL;
		}
	};

	// ----------------------------------------------------------------------
	//
	// ----------------------------------------------------------------------

	/**
	 * Detect user installation.
	 */
	private void detectUserInstallation()
	{
		if (new File(USER_HOME, ".m2").exists())
		{
			alignWithUserInstallation_ = true;
		}
	}

	/**
	 * Create the Settings that will be used with the embedder. If we are
	 * aligning with the user installation then we lookup the standard settings
	 * builder and use that to create our settings. Otherwise we constructs a
	 * settings object and populate the information ourselves.
	 * 
	 * @throws XmlPullParserException If error.
	 * @throws IOException If error.
	 * @throws ComponentLookupException If error.
	 */
	private void createMavenSettings() throws ComponentLookupException, IOException, XmlPullParserException
	{
		if (alignWithUserInstallation_)
		{
			// ----------------------------------------------------------------------
			// We will use the standard method for creating the settings. This
			// method reproduces the method of building the settings from the
			// CLI
			// mode of operation.
			// ----------------------------------------------------------------------

			settingsBuilder_ = (MavenSettingsBuilder) embedder_.lookup(MavenSettingsBuilder.ROLE);

			settings_ = settingsBuilder_.buildSettings();
		}
		else
		{
			if (localRepository_ == null)
			{
				throw new IllegalArgumentException("When not aligning with a user install you must specify "
						+ "a local repository location using the "
						+ "setLocalRepositoryDirectory( File ) method.");
			}

			settings_ = new Settings();

			settings_.setLocalRepository(localRepositoryDirectory_.getAbsolutePath());

			settings_.setOffline(offline_);

			settings_.setInteractiveMode(interactiveMode_);
		}
	}

	// ----------------------------------------------------------------------
	// Lifecycle
	// ----------------------------------------------------------------------

	/**
	 * Stop the maven embedded.
	 * 
	 * @throws ComponentLifecycleException If error.
	 */
	public void stop() throws ComponentLifecycleException
	{
		embedder_.release(artifactRepositoryFactory_);
	}
}
