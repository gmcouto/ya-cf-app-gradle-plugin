package io.pivotal.services.plugin;


import java.util.List;
import java.util.Map;

public class CfService {
    private String name;
    private String plan;
    private String instanceName;
    private Integer completionTimeout;
    private List<String> tags;
    private Map<String, ? extends Object> parameters;
    private Map<String, ? extends Object> bindParameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Integer getCompletionTimeout() {
        return completionTimeout;
    }

    public void setCompletionTimeout(Integer completionTimeout) {
        this.completionTimeout = completionTimeout;
    }
    
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<String, ? extends Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, ? extends Object> parameters) {
        this.parameters = parameters;
    }

    public Map<String, ? extends Object> getBindParameters() {
        return bindParameters;
    }

    public void setBindParameters(Map<String, ? extends Object> bindParameters) {
        this.bindParameters = bindParameters;
    }
}
