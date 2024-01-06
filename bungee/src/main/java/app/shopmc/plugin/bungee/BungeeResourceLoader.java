package app.shopmc.plugin.bungee;

import app.shopmc.plugin.resource.ResourceLoader;
import app.shopmc.plugin.resource.ResourceLoaderException;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;

public class BungeeResourceLoader extends ResourceLoader<Configuration> {

    public BungeeResourceLoader(final Class<?> loadingClass, final File dataFolder) {
        super(loadingClass, dataFolder.toPath());
    }

    @Override
    protected Configuration loadResource(final Path resourcePath) throws ResourceLoaderException {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(resourcePath.toFile());
        } catch (final Exception exception) {
            throw new ResourceLoaderException(ResourceLoaderException.Reason.FILE_NOT_LOADED, exception);
        }
    }

}