package org.jamesdbloom.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author jamesdbloom
 */
@Configuration
@ImportResource(value = {"classpath:/config/security-context.xml"})
public class RootConfiguration {
}
