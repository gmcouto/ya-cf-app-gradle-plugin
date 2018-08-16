package io.pivotal.services.plugin.tasks.helper;

import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.services.GetServiceInstanceRequest;
import org.cloudfoundry.operations.services.ListServiceKeysRequest;
import org.cloudfoundry.operations.services.ServiceInstance;
import org.cloudfoundry.operations.services.ServiceKey;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Responsible for getting the service instance given a service name
 *  @author Gabriel Couto (@gmcouto)
 */
public class CfServicesDetailHelper {
    private static final Logger LOGGER = Logging.getLogger(CfServicesDetailHelper.class);

    public Mono<Optional<ServiceInstance>> getServiceInstanceDetail(CloudFoundryOperations cfOperations,
                                                                    String serviceName) {

        LOGGER.lifecycle("Checking details of service '{}'", serviceName);
        Mono<ServiceInstance> serviceInstanceMono = cfOperations.services()
            .getInstance(
                GetServiceInstanceRequest.builder()
                    .name(serviceName)
                    .build());

        return serviceInstanceMono
            .map(serviceInstance -> Optional.ofNullable(serviceInstance))
            .onErrorResume(Exception.class, e -> Mono.just(Optional.empty()));
    }

    public Mono<List<ServiceKey>> getServiceKeys(CloudFoundryOperations cfOperations, String instanceName) {
        LOGGER.lifecycle("Checking keys of service instance '{}'", instanceName);
        return cfOperations.services()
            .listServiceKeys(
                ListServiceKeysRequest.builder()
                    .serviceInstanceName(instanceName)
                    .build()).collectList();
    }

}
