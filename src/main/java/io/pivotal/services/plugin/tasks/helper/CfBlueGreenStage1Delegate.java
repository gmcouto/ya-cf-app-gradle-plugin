package io.pivotal.services.plugin.tasks.helper;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.CfRouteUtil;
import io.pivotal.services.plugin.ImmutableCfProperties;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationDetail;
import org.cloudfoundry.operations.applications.ApplicationEnvironments;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.cloudfoundry.util.tuple.TupleUtils.function;

/**
 * @author Gabriel Couto
 */
public class CfBlueGreenStage1Delegate {

    private CfPushDelegate pushDelegate = new CfPushDelegate();
    private CfAppDetailsDelegate appDetailsDelegate = new CfAppDetailsDelegate();
    private CfAppEnvDelegate appEnvDelegate = new CfAppEnvDelegate();

    private static final Logger LOGGER = Logging
        .getLogger(CfBlueGreenStage1Delegate.class);

    public Mono<Void> runStage1(Project project, CloudFoundryOperations cfOperations,
                                CfProperties cfProperties) {
        
        final String greenNameString = cfProperties.name() + "-green";
        final String greenRouteString = CfRouteUtil.getTempRoute(cfOperations, cfProperties, "-green");

        Mono<Optional<ApplicationDetail>> appDetailMono = appDetailsDelegate
            .getAppDetails(cfOperations, cfProperties);

        // Get App Env Vars
        Mono<Optional<ApplicationEnvironments>> appEnvMono =
            appEnvDelegate.getAppEnv(cfOperations, cfProperties);

        Mono<ImmutableCfProperties> cfPropertiesMono = Mono.zip(appEnvMono, appDetailMono).map(function((appEnvOpt, appDetailOpt) -> {
            Map<String, String> userEnvs = mapUserEnvironmentVars(cfProperties, appEnvOpt);
            LOGGER.lifecycle(
                "Running Blue Green Deploy - deploying a 'green' app. App '{}' with route '{}'",
                cfProperties.name(),
                greenRouteString);

            return appDetailOpt.map(appDetail -> {
                printAppDetail(appDetail);
                return ImmutableCfProperties.copyOf(cfProperties)
                    .withName(greenNameString)
                    .withHost(null)
                    .withDomain(null)
                    .withRoutes(Collections.singletonList(greenRouteString));
            }).orElse(ImmutableCfProperties.copyOf(cfProperties)
                .withName(greenNameString)
                .withHost(null)
                .withDomain(null)
                .withRoutes(Collections.singletonList(greenRouteString)));
        }));

        return cfPropertiesMono.flatMap(
            withNewNameAndRoute -> pushDelegate.push(cfOperations, withNewNameAndRoute));
    }

    private Map<String, String> mapUserEnvironmentVars(CfProperties cfProperties, Optional<ApplicationEnvironments> appEnvOpt) {
        Map<String, String> userEnvs = new HashMap<>();
        if(!appEnvOpt.isPresent()){
            return userEnvs;
        }

        Optional<Map<String, Object>> userProvidedEnv = appEnvOpt.map(ApplicationEnvironments::getUserProvided);
        for (String key :
            userProvidedEnv.get().keySet()) {
            // The values for this should always be a string but are given as an object
            userEnvs.put(key, (String) userProvidedEnv.get().get(key));
        }
        if (cfProperties.environment() != null) {
            userEnvs.putAll(cfProperties.environment());
        }
        return userEnvs;
    }

    private void printAppDetail(ApplicationDetail applicationDetail) {
        LOGGER.lifecycle("Application Name: {}", applicationDetail.getName());
        LOGGER.lifecycle("Intance Count: {}", applicationDetail.getInstances());
        LOGGER
            .lifecycle("Running Instances: {}", applicationDetail.getRunningInstances());
    }
}
