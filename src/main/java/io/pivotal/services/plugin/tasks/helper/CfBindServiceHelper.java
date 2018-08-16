package io.pivotal.services.plugin.tasks.helper;

import java.util.List;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.CfServiceDetail;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.services.BindServiceInstanceRequest;
import org.cloudfoundry.operations.services.ServiceKey;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

/**
 * Responsible for creating a Service Instance
 *
 * @author Gabriel Couto (@gmcouto)
 */
public class CfBindServiceHelper {

    private CfServicesDetailHelper servicesDetailHelper = new CfServicesDetailHelper();
    private static final Logger LOGGER = Logging.getLogger(CfBindServiceHelper.class);

    public Mono<List<ServiceKey>> bindService(CloudFoundryOperations cfOperations,
        CfProperties cfProperties, CfServiceDetail cfServiceDetail) {
        LOGGER.info("Binding service instance '{}' to application '{}' ", cfServiceDetail.instanceName(), cfProperties.name());
        return cfOperations.services().bind(
            BindServiceInstanceRequest.builder()
                .applicationName(cfProperties.name())
                .serviceInstanceName(cfServiceDetail.instanceName())
                .parameters(cfServiceDetail.bindParameters())
                .build())
            .then(servicesDetailHelper.getServiceKeys(cfOperations, cfServiceDetail.instanceName()));
    }
}
