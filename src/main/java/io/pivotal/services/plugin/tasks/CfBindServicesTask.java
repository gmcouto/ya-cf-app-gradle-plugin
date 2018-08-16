package io.pivotal.services.plugin.tasks;

import java.util.List;
import java.util.stream.Collectors;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.tasks.helper.CfBindServiceHelper;
import io.pivotal.services.plugin.tasks.helper.CfCreateUserProvidedServiceHelper;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Responsible for binding a list of services
 *
 * @author Gabriel Couto (@gmcouto)
 */
public class CfBindServicesTask extends AbstractCfTask {

    private CfBindServiceHelper bindServiceHelper = new CfBindServiceHelper();

    @TaskAction
    public void cfRebindServicesTask() {
        CloudFoundryOperations cfOperations = getCfOperations();
        CfProperties cfProperties = getCfProperties();

        List<Mono<Void>> createServicesResult = cfProperties.cfServices()
            .stream()
            .filter(cfServiceDetail -> cfServiceDetail.bindParameters() != null && !cfServiceDetail.bindParameters().isEmpty())
            .map(service -> bindServiceHelper.bindService(cfOperations, cfProperties, service).then())
            .collect(Collectors.toList());

        Flux.merge(createServicesResult).toIterable().forEach(r -> {
        });
    }

    @Override
    public String getDescription() {
        return "Bind a set of services";
    }
}
