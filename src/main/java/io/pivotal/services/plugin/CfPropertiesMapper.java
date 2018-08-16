/*
 * Copyright 2013-2016 the original author or authors.
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

package io.pivotal.services.plugin;

import org.cloudfoundry.operations.applications.ApplicationManifest;
import org.cloudfoundry.operations.applications.ApplicationManifestUtils;
import org.cloudfoundry.operations.applications.Route;
import org.gradle.api.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Responsible for mapping plugin extension provided via a block
 * which looks like this:
 * <pre>
 *     cfConfig {
 *       ccHost = "api.local.pcfdev.io"
 *       ccUser = "admin"
 *       ccPassword = "admin"
 *       org = "pcfdev-org"
 *       space = "pcfdev-space"
 *       ....
 *       }
 * </pre>
 * OR overridden via command line flags say:
 * <pre>
 *    ./gradlew cf-push -Pcf.name=newname
 * </pre>
 * <p>
 * and providing a structure with the final values
 *
 * @author Biju Kunjummen
 * @author Gabriel Couto
 */
public class CfPropertiesMapper {

    private final Project project;

    private final Map<String, String> systemEnv;

    private ApplicationManifest manifest;

    public CfPropertiesMapper(Project project) {
        this(project, System.getenv());
    }

    //For Tests
    CfPropertiesMapper(Project project, Map<String, String> systemEnv) {
        this.project = project;
        this.systemEnv = systemEnv;
    }

    public CfProperties getProperties() {
        refreshManifest();
        return ImmutableCfProperties.builder()
            .name(getCfApplicationName())
            .ccHost(getCcHost())
            .ccUser(getCcUser())
            .ccPassword(getCcPassword())
            .ccToken(getCcToken())
            .org(getOrg())
            .space(getSpace())
            .manifestPath(getManifestPath())
            .filePath(getFilePath())
            .host(getAppHostName())
            .domain(getAppDomain())
            .path(getCfPath())
            .addAllRoutes(getAppRoutes())
            .state(this.getExtension().getState())
            .buildpack(this.getBuildpack())
            .command(this.getCommand())
            .console(this.getExtension().getConsole())
            .debug(this.getExtension().getDebug())
            .detectedStartCommand(this.getExtension().getDetectedStartCommand())
            .diskQuota(this.getDiskQuota())
            .enableSsh(this.getExtension().getEnableSsh())
            .environment(getEnvironment())
            .timeout(this.getTimeout())
            .healthCheckType(this.getHealthCheckType())
            .instances(this.getInstances())
            .memory(this.getMemory())
            .ports(this.getExtension().getPorts())
            .services(getServices())
            .stagingTimeout(this.getStagingTimeout())
            .startupTimeout(this.getStartupTimeout())
            .cfServices(this.getCfServices())
            .cfUserProvidedServices(this.getCfUserProvidedServices())
            .cfProxySettings(this.getCfProxySettings())
            .build();
    }

    private void refreshManifest() {
        String manifestPath = getManifestPath();
        manifest =  manifestPath != null ? ApplicationManifestUtils.read(new File(manifestPath).toPath()).get(0) : null;
    }

    private List<CfServiceDetail> getCfServices() {
        return mapCfServices(this.getExtension().getCfServices());
    }

    List<CfServiceDetail> mapCfServices(List<CfService> cfServices) {
        List<CfServiceDetail> serviceDetails = new ArrayList<>();
        if (cfServices != null) {
            for (CfService cfService : cfServices) {
                CfServiceDetail cfServiceDetail = ImmutableCfServiceDetail.builder()
                    .instanceName(cfService.getInstanceName())
                    .name(cfService.getName())
                    .plan(cfService.getPlan())
                    .parameters(cfService.getParameters())
                    .tags(cfService.getTags())
                    .bindParameters(cfService.getBindParameters())
                    .completionTimeout((cfService.getCompletionTimeout() != null)
                        ? cfService.getCompletionTimeout()
                        : DefaultProperties.SERVICE_CREATION_COMPLETION_TIMEOUT)
                    .build();

                serviceDetails.add(cfServiceDetail);
            }
        }
        return serviceDetails;
    }

    private List<CfUserProvidedServiceDetail> getCfUserProvidedServices() {
        return mapCfUserProvidedServices(this.getExtension().getCfUserProvidedServices());
    }

    List<CfUserProvidedServiceDetail> mapCfUserProvidedServices(List<CfUserProvidedService> cfUserProvidedServices) {
        List<CfUserProvidedServiceDetail> serviceDetails = new ArrayList<>();
        if (cfUserProvidedServices != null) {
            for (CfUserProvidedService service : cfUserProvidedServices) {
                CfUserProvidedServiceDetail cfServiceDetail = ImmutableCfUserProvidedServiceDetail.builder()
                    .instanceName(service.getInstanceName())
                    .credentials(service.getCredentials())
                    .completionTimeout((service.getCompletionTimeout() != null)
                        ? service.getCompletionTimeout()
                        : DefaultProperties.SERVICE_CREATION_COMPLETION_TIMEOUT)
                    .build();
                serviceDetails.add(cfServiceDetail);
            }
        }

        return serviceDetails;
    }

    private CfProxySettingsDetail getCfProxySettings() {
        final CfProxySettings proxySettings = this.getExtension().getCfProxySettings();
        if (proxySettings == null) {
            return null;
        }
        return ImmutableCfProxySettingsDetail.builder()
            .proxyHost(getStringPropertyFromProject(PropertyNameConstants.CF_PROXY_HOST)
                .orElse(proxySettings.getProxyHost()))
            .proxyPort(getIntegerPropertyFromProject(PropertyNameConstants.CF_PROXY_PORT)
                .orElse(proxySettings.getProxyPort()))
            .proxyUser(getStringPropertyFromProject(PropertyNameConstants.CF_PROXY_USER)
                .orElse(proxySettings.getProxyUser()))
            .proxyPassword(getStringPropertyFromProject(PropertyNameConstants.CF_PROXY_PASSWORD)
                .orElse(proxySettings.getProxyPassword()))
            .build();
    }

    CfPluginExtension getExtension() {
        return this.project.getExtensions().findByType(CfPluginExtension.class);
    }


    public String getCfApplicationName() {
        return firstNonEmptyOptional(() -> getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_NAME),
            () -> Optional.ofNullable(this.getExtension().getName()),
            () -> fromManifest(m -> m.getName())
        );
    }

    public String getNewName() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_NEW_NAME)
            .orElse(null);
    }

    public String getManifestPath() {
        return firstNonEmptyOptional(
            () -> getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_MANIFEST),
            () -> Optional.ofNullable(this.getExtension() != null ? this.getExtension().getManifestFile() : null)
        );
    }

    public String getHealthCheckType() {
        return firstNonEmptyOptional(
            () -> Optional.ofNullable(this.getExtension().getHealthCheckType()),
            () -> fromManifest(m -> m.getHealthCheckType() != null ? m.getHealthCheckType().getValue() : null)
        );
    }

    public String getCommand() {
        return firstNonEmptyOptional(
            () -> Optional.ofNullable(this.getExtension().getCommand()),
            () -> fromManifest(m -> m.getCommand())
        );
    }

    public String getAppHostName() {
        return firstNonEmptyOptional(() -> getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_HOST_NAME),
            () -> Optional.ofNullable(this.getExtension().getHost()),
            () -> fromManifest(m -> (m.getHosts() != null && !m.getHosts().isEmpty()) ? this.manifest.getHosts().get(0) : null)
        );
    }

    public String getAppDomain() {
        return firstNonEmptyOptional(() -> getStringPropertyFromProject(PropertyNameConstants.CF_APPLICATION_DOMAIN),
            () -> Optional.ofNullable(this.getExtension().getDomain()),
            () -> fromManifest(m -> (m.getDomains() != null && !m.getDomains().isEmpty()) ? m.getDomains().get(0) : null)
        );
    }

    public String getFilePath() {
        return firstNonEmptyOptional(() -> getStringPropertyFromProject(PropertyNameConstants.CF_FILE_PATH),
            () -> Optional.ofNullable(this.getExtension().getFilePath()),
            () -> fromManifest(m -> m.getPath() != null ? m.getPath().toString() : null)
        );
    }

    private <T> Optional<T> fromManifest(Function<ApplicationManifest, T> manifestContentExtractor) {
        if (this.manifest != null) {
            return Optional.ofNullable(manifestContentExtractor.apply(this.manifest));
        }
        return Optional.empty();
    }

    public String getCcHost() {
        return getStringPropertyFromProject(PropertyNameConstants.CC_HOST)
            .orElse(this.getExtension().getCcHost());
    }

    public String getCcUser() {
        return getPropertyFromEnvironment(PropertyNameConstants.CC_USER_ENV)
            .orElse(
                this.getStringPropertyFromProject(PropertyNameConstants.CC_USER)
                    .orElse(this.getExtension().getCcUser()));

    }

    public String getCcPassword() {
        return getPropertyFromEnvironment(PropertyNameConstants.CC_PASSWORD_ENV)
            .orElse(getStringPropertyFromProject(PropertyNameConstants.CC_PASSWORD)
                .orElse(this.getExtension().getCcPassword()));
    }

    public List<String> getServices() {
        return firstNonEmptyOptional(() -> getListPropertyFromProject(PropertyNameConstants.CF_SERVICES),
            () -> Optional.ofNullable(this.getExtension().getServices().isEmpty() ? null : this.getExtension().getServices()),
            () -> fromManifest(m -> m.getServices()),
            () -> Optional.ofNullable(Collections.emptyList())
        );
    }

    public String getCcToken() {
        return getStringPropertyFromProject(PropertyNameConstants.CC_TOKEN)
            .orElse(this.getExtension().getCcToken());
    }

    public String getBuildpack() {
        return firstNonEmptyOptional(() -> getStringPropertyFromProject(PropertyNameConstants.CF_BUILDPACK),
            () -> Optional.ofNullable(this.getExtension().getBuildpack()),
            () -> fromManifest(m -> m.getBuildpack())
        );
    }

    public String getOrg() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_ORG)
            .orElse(this.getExtension().getOrg());
    }

    public String getSpace() {
        return getStringPropertyFromProject(PropertyNameConstants.CF_SPACE)
            .orElse(this.getExtension().getSpace());
    }

    public String getCfPath() {
        return firstNonEmptyOptional(() -> getStringPropertyFromProject(PropertyNameConstants.CF_PATH),
            () -> Optional.ofNullable(this.getExtension().getPath()),
            () -> fromManifest(m -> m.getRoutePath())
        );
    }

    public List<String> getAppRoutes() {
        return firstNonEmptyOptional(() -> getListPropertyFromProject(PropertyNameConstants.CF_APPLICATION_ROUTES),
            () -> Optional.ofNullable(this.getExtension().getRoutes().isEmpty() ? null : this.getExtension().getRoutes()),
            () -> fromManifest(m -> m.getRoutes().stream().map(Route::getRoute).collect(Collectors.toList())),
            () -> Optional.ofNullable(Collections.emptyList())
        );
    }

    public Integer getInstances() {
        return firstNonEmptyOptional(() -> getIntegerPropertyFromProject(PropertyNameConstants.CF_INSTANCES),
            () -> Optional.ofNullable(this.getExtension().getInstances()),
            () -> fromManifest(m -> m.getInstances())
        );
    }

    public Integer getMemory() {
        return firstNonEmptyOptional(() -> getIntegerPropertyFromProject(PropertyNameConstants.CF_MEMORY),
            () -> Optional.ofNullable(this.getExtension().getMemory()),
            () -> fromManifest(m -> m.getMemory())
        );
    }

    public Integer getTimeout() {
        return firstNonEmptyOptional(() -> getIntegerPropertyFromProject(PropertyNameConstants.CF_HEALTH_CHECK_TIMEOUT),
            () -> Optional.ofNullable(this.getExtension().getTimeout()),
            () -> fromManifest(m -> m.getTimeout())
        );
    }

    public Integer getDiskQuota() {
        return firstNonEmptyOptional(() -> getIntegerPropertyFromProject(PropertyNameConstants.CF_DISK_QUOTA),
            () -> Optional.ofNullable(this.getExtension().getDiskQuota()),
            () -> fromManifest(m -> m.getDisk())
        );
    }

    public Integer getStagingTimeout() {
        Integer stagingTimeout = getIntegerPropertyFromProject(PropertyNameConstants.CF_STAGING_TIMEOUT)
            .orElse(this.getExtension().getStagingTimeout());

        return stagingTimeout != null ? stagingTimeout : DefaultProperties.STAGING_TIMEOUT;
    }

    public Integer getStartupTimeout() {
        Integer startupTimeout = getIntegerPropertyFromProject(PropertyNameConstants.CF_STARTUP_TIMEOUT)
            .orElse(this.getExtension().getStartupTimeout());

        return startupTimeout != null ? startupTimeout : DefaultProperties.STARTUP_TIMEOUT;
    }

    private Map<String, String> getEnvironment() {
        Map<String, String> withProperties = new HashMap<>();
        Map<String, Object> manifestEnvironment = manifest != null ? manifest.getEnvironmentVariables() : null;
        if (manifestEnvironment != null)
            manifestEnvironment.forEach((key, obj) -> withProperties.put(key, obj.toString()));
        Map<String, String> buildScriptEnvironment = this.getExtension().getEnvironment();
        if (buildScriptEnvironment != null)
            withProperties.putAll(buildScriptEnvironment);

        Map<String, ?> allProperties = this.project.getProperties();
        for (String propName : allProperties.keySet()) {
            String prefix = PropertyNameConstants.CF_ENVIRONMENT + ".";
            if (propName.startsWith(prefix)) {
                String newKey = propName.substring(prefix.length());
                withProperties.put(newKey, getStringPropertyFromProject(propName).orElse(""));
            }
        }

        return withProperties;
    }

    public Optional<String> getPropertyFromEnvironment(String propertyName) {
        if (this.systemEnv.containsKey(propertyName)) {
            return Optional.of((String) this.systemEnv.get(propertyName));
        }
        return Optional.empty();
    }

    public Optional<String> getStringPropertyFromProject(String propertyName) {
        if (this.project.hasProperty(propertyName)) {
            return Optional.of((String) this.project.property(propertyName));
        }
        return Optional.empty();
    }

    public Optional<List<String>> getListPropertyFromProject(String propertyName) {
        if (this.project.hasProperty(propertyName)) {
            String rawProperty = (String) this.project.property(propertyName);
            return Optional.of((Arrays.asList(rawProperty.split(","))));
        }
        return Optional.empty();
    }

    /**
     * Get a property value from the Project properties.
     *
     * @param propertyName name of property
     * @return value of property, @{link java.util.Optional#empty} if not available.
     */
    public Optional<Integer> getIntegerPropertyFromProject(String propertyName) {
        if (this.project.hasProperty(propertyName)) {
            return Optional.of((Integer) this.project.property(propertyName));
        }
        return Optional.empty();
    }

    static <T> T firstNonEmptyOptional(Supplier<Optional<T>>... optSup) {
        return Arrays.stream(optSup)
            .map(Supplier::get)
            .filter(opt -> opt.isPresent())
            .map(Optional::get)
            .findFirst()
            .orElse(null);
    }
}
