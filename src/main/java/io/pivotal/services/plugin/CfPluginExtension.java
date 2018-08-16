package io.pivotal.services.plugin;

import groovy.lang.Closure;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A holder for properties set via a Gradle cfConfig closure
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
 *
 * @author Biju Kunjummen
 * @author Gabriel Couto
 */
public class CfPluginExtension {

    private Project project;

    public CfPluginExtension(Project project) {
        this.project = project;
    }

    private String ccHost;
    private String ccUser;
    private String ccPassword;
    private String ccToken;

    private String org;
    private String space;

    private String name;
    private String filePath;
    private String manifestFile;
    private String host;
    private String domain;
    private String path;
    private List<String> routes = new ArrayList<>();


    private String state;
    private String buildpack;
    private String command;
    private Boolean console;
    private Boolean debug;
    private String detectedStartCommand;
    private Integer diskQuota;
    private Boolean enableSsh;
    private Map<String, String> environment;
    private Integer timeout;
    private Integer stagingTimeout;
    private Integer startupTimeout;
    private String healthCheckType;
    private Integer instances;
    private Integer memory;
    private List<Integer> ports;
    private List<String> services = new ArrayList<>();

    private ApplicationDetail applicationDetail;

    private List<CfService> cfServices = new ArrayList<>();
    private List<CfUserProvidedService> cfUserProvidedServices = new ArrayList<>();
    private CfProxySettings cfProxySettings;

    public void cfService(Closure closure) {
        CfService cfService = new CfService();
        this.project.configure(cfService, closure);
        this.cfServices.add(cfService);
        if(!cfService.isRequiresBindParameters())
            this.services.add(cfService.getInstanceName());
    }

    public void cfUserProvidedService(Closure closure) {
        CfUserProvidedService cfUserProvidedService = new CfUserProvidedService();
        this.project.configure(cfUserProvidedService, closure);
        this.cfUserProvidedServices.add(cfUserProvidedService);
        this.services.add(cfUserProvidedService.getInstanceName());
    }

    public void cfProxySettings(Closure closure) {
        this.cfProxySettings = new CfProxySettings();
        this.project.configure(cfProxySettings, closure);
    }

    public String getCcHost() {
        return ccHost;
    }

    public void setCcHost(String ccHost) {
        this.ccHost = ccHost;
    }

    public String getCcUser() {
        return ccUser;
    }

    public void setCcUser(String ccUser) {
        this.ccUser = ccUser;
    }

    public String getCcPassword() {
        return ccPassword;
    }

    public void setCcPassword(String ccPassword) {
        this.ccPassword = ccPassword;
    }

    public String getCcToken() {
        return ccToken;
    }

    public void setCcToken(String ccToken) {
        this.ccToken = ccToken;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManifestFile() {
        return manifestFile;
    }

    public void setManifestFile(String manifestFile) {
        this.manifestFile = manifestFile;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getRoutes() {
        return routes;
    }

    public void setRoutes(List<String> routes) {
        this.routes = routes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBuildpack() {
        return buildpack;
    }

    public void setBuildpack(String buildpack) {
        this.buildpack = buildpack;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Boolean getConsole() {
        return console;
    }

    public void setConsole(Boolean console) {
        this.console = console;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public String getDetectedStartCommand() {
        return detectedStartCommand;
    }

    public void setDetectedStartCommand(String detectedStartCommand) {
        this.detectedStartCommand = detectedStartCommand;
    }

    public Integer getDiskQuota() {
        return diskQuota;
    }

    public void setDiskQuota(Integer diskQuota) {
        this.diskQuota = diskQuota;
    }

    public Boolean getEnableSsh() {
        return enableSsh;
    }

    public void setEnableSsh(Boolean enableSsh) {
        this.enableSsh = enableSsh;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getStagingTimeout() {
        return stagingTimeout;
    }

    public void setStagingTimeout(Integer stagingTimeout) {
        this.stagingTimeout = stagingTimeout;
    }

    public Integer getStartupTimeout() {
        return startupTimeout;
    }

    public void setStartupTimeout(Integer startupTimeout) {
        this.startupTimeout = startupTimeout;
    }

    public String getHealthCheckType() {
        return healthCheckType;
    }

    public void setHealthCheckType(String healthCheckType) {
        this.healthCheckType = healthCheckType;
    }

    public Integer getInstances() {
        return instances;
    }

    public void setInstances(Integer instances) {
        this.instances = instances;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public ApplicationDetail getApplicationDetail() {
        return applicationDetail;
    }

    public void setApplicationDetail(ApplicationDetail applicationDetail) {
        this.applicationDetail = applicationDetail;
    }

    public List<CfService> getCfServices() {
        return cfServices;
    }

    public void setCfServices(List<CfService> cfServices) {
        this.cfServices = cfServices;
    }

    public List<CfUserProvidedService> getCfUserProvidedServices() {
        return cfUserProvidedServices;
    }

    public void setCfUserProvidedServices(List<CfUserProvidedService> cfUserProvidedServices) {
        this.cfUserProvidedServices = cfUserProvidedServices;
    }

    public CfProxySettings getCfProxySettings() {
        return cfProxySettings;
    }

    public void setCfProxySettings(CfProxySettings cfProxySettings) {
        this.cfProxySettings = cfProxySettings;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CfPluginExtension{");
        sb.append("project=").append(project);
        sb.append(", ccHost='").append(ccHost).append('\'');
        sb.append(", ccUser='").append(ccUser).append('\'');
        sb.append(", ccToken='").append(ccToken).append('\'');
        sb.append(", org='").append(org).append('\'');
        sb.append(", space='").append(space).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", manifest='").append(manifestFile).append('\'');
        sb.append(", filePath='").append(filePath).append('\'');
        sb.append(", host='").append(host).append('\'');
        sb.append(", domain='").append(domain).append('\'');
        sb.append(", path='").append(path).append('\'');
        sb.append(", routes='").append(routes.toString()).append('\'');
        sb.append(", state='").append(state).append('\'');
        sb.append(", buildpack='").append(buildpack).append('\'');
        sb.append(", command='").append(command).append('\'');
        sb.append(", console=").append(console);
        sb.append(", debug=").append(debug);
        sb.append(", detectedStartCommand='").append(detectedStartCommand).append('\'');
        sb.append(", diskQuota=").append(diskQuota);
        sb.append(", enableSsh=").append(enableSsh);
        sb.append(", environment=").append(environment);
        sb.append(", timeout=").append(timeout);
        sb.append(", stagingTimeout=").append(stagingTimeout);
        sb.append(", startupTimeout=").append(startupTimeout);
        sb.append(", healthCheckType='").append(healthCheckType).append('\'');
        sb.append(", instances=").append(instances);
        sb.append(", memory=").append(memory);
        sb.append(", ports=").append(ports);
        sb.append(", services=").append(services);
        sb.append(", applicationDetail=").append(applicationDetail);
        sb.append(", cfServices=").append(cfServices);
        sb.append(", cfUserProvidedServices=").append(cfUserProvidedServices);
        sb.append(", cfProxySettings=").append(cfProxySettings);
        sb.append('}');
        return sb.toString();
    }
}
