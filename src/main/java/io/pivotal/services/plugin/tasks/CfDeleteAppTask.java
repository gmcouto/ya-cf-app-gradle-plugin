package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfProperties;
import io.pivotal.services.plugin.tasks.helper.CfDeleteAppDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for Deleting an app
 *
 * @author Biju Kunjummen
 */
public class CfDeleteAppTask extends AbstractCfTask {

    private CfDeleteAppDelegate deleteDelegate = new CfDeleteAppDelegate();

    @TaskAction
    public void deleteApp() {
        CloudFoundryOperations cfOperations = getCfOperations();
        CfProperties cfProperties = getCfProperties();

        Mono<Void> resp = deleteDelegate.deleteApp(cfOperations, cfProperties);
        resp.block(Duration.ofMillis(defaultWaitTimeout));
    }

    @Override
    public String getDescription() {
        return "Delete an application from Cloud Foundry";
    }
}
