package io.pivotal.services.plugin;

import org.cloudfoundry.Nullable;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

/**
 * Details of a service instance
 *
 * @author Biju Kunjummen
 * @author Gabriel Couto (@gmcouto)
 */

@Value.Immutable(copy = true)
public abstract class CfServiceDetail {
    public abstract String name();
    public abstract String plan();
    public abstract String instanceName();
    
    @Nullable
    public abstract Integer completionTimeout();
    @Nullable
    public abstract List<String> tags();
    @Nullable
    public abstract Map<String, Object> parameters();
    @Nullable
    public abstract Map<String, Object> bindParameters();
}
