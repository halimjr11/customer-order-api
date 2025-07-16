package com.halimjr11.api;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Application configuration class for JAX-RS.
 * This class registers the base package where JAX-RS resources are located.
 */
public class AppConfig extends ResourceConfig {

    /**
     * Constructor that configures the application by registering
     * the base package containing REST resource classes.
     */
    public AppConfig() {
        // Register the package that contains your JAX-RS resource classes
        packages("com.halimjr11.api");
    }
}
