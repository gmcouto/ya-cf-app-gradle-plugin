package io.pivotal.services.plugin.tasks;

import io.pivotal.services.plugin.CfAppProperties;
import io.pivotal.services.plugin.tasks.helper.CfDeleteRouteTaskDelegate;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.gradle.api.tasks.TaskAction;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Responsible for Deleting a route
 *
 * @author Biju Kunjummen
 */
public class CfDeleteRouteTask extends AbstractCfTask {

	private CfDeleteRouteTaskDelegate deleteRouteDelegate;

	@TaskAction
	public void deleteRoute() {
		CloudFoundryOperations cfOperations = getCfOperations();
		CfAppProperties cfAppProperties = getCfAppProperties();

		Mono<Void> resp = deleteRouteDelegate.deleteRoute(cfOperations, cfAppProperties);

		resp.block(Duration.ofMillis(defaultWaitTimeout));
	}


	@Override
	public String getDescription() {
		return "Delete a route from Cloud Foundry";
	}
}
