package io.pivotal.services.plugin;

import io.pivotal.services.plugin.tasks.*;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CfPushClientPluginTests {

    private Project project;

    @Before
    public void before() {
        this.project = ProjectBuilder.builder().build();
        this.project.getPluginManager().apply("cf-app");
    }

    @Test
    public void testValidateCount() {
        assertThat(this.project.getTasks().size()).isEqualTo(16);
        assertThat(this.project.getTasks().getAt("cf-push").getGroup()).isEqualTo("Cloud Foundry");
    }

    @Test
    public void testCfPushTaskMeta() {
        assertThat(project.getTasks().getAt("cf-push") instanceof CfPushTask).isTrue();
        assertThat(project.getTasks().getAt("cf-push").getDescription()).isEqualTo("Pushes an Application to Cloud Foundry");
    }

    @Test
    public void testCfMapRoute() {
        assertThat(project.getTasks().getAt("cf-map-route") instanceof CfMapRouteTask).isTrue();
        assertThat(project.getTasks().getAt("cf-map-route").getDescription()).isEqualTo("Add a route for an application");
    }

    @Test
    public void testCfUnmapRoute() {
        assertThat(project.getTasks().getAt("cf-unmap-route") instanceof CfUnMapRouteTask).isTrue();
        assertThat(project.getTasks().getAt("cf-unmap-route").getDescription()).isEqualTo("Remove an existing route for an application");
    }

    @Test
    public void testCfDeleteRoute() {
        assertThat(project.getTasks().getAt("cf-delete-route") instanceof CfDeleteRouteTask).isTrue();
        assertThat(project.getTasks().getAt("cf-delete-route").getDescription()).isEqualTo("Delete a route from Cloud Foundry");
    }

    @Test
    public void testCfDeleteApp() {
        assertThat(project.getTasks().getAt("cf-delete-app") instanceof CfDeleteAppTask).isTrue();
        assertThat(project.getTasks().getAt("cf-delete-app").getDescription()).isEqualTo("Delete an application from Cloud Foundry");
    }

    @Test
    public void testCfRenameApp() {
        assertThat(project.getTasks().getAt("cf-rename-app") instanceof CfRenameAppTask).isTrue();
        assertThat(project.getTasks().getAt("cf-rename-app").getDescription()).isEqualTo("Rename an Application");
    }

    @Test
    public void testCfStopApp() {
        assertThat(project.getTasks().getAt("cf-stop-app") instanceof CfAppStopTask).isTrue();
        assertThat(project.getTasks().getAt("cf-stop-app").getDescription()).isEqualTo("Stop an Application");
    }

    @Test
    public void testCfStartApp() {
        assertThat(project.getTasks().getAt("cf-start-app") instanceof CfAppStartTask).isTrue();
        assertThat(project.getTasks().getAt("cf-start-app").getDescription()).isEqualTo("Start an Application");
    }

    @Test
    public void testCfRestartApp() {
        assertThat(project.getTasks().getAt("cf-restart-app") instanceof CfAppRestartTask).isTrue();
        assertThat(project.getTasks().getAt("cf-restart-app").getDescription()).isEqualTo("Restart an Application");
    }

    @Test
    public void testCfRestageTask() {
        assertThat(project.getTasks().getAt("cf-restage-app") instanceof CfRestageTask).isTrue();
        assertThat(project.getTasks().getAt("cf-restage-app").getDescription()).isEqualTo("Restage an Application");
    }

    @Test
    public void testCfBlueGreenStage1Task() {
        assertThat(project.getTasks().getAt("cf-push-blue-green-1") instanceof CfBlueGreenStage1Task).isTrue();
        assertThat(project.getTasks().getAt("cf-push-blue-green-1").getDescription()).isEqualTo("Push an Application in a Blue-Green no downtime mode - Stage 1");
    }

    @Test
    public void testCfBlueGreenStage2Task() {
        assertThat(project.getTasks().getAt("cf-push-blue-green") instanceof CfBlueGreenStage2Task).isTrue();
        assertThat(project.getTasks().getAt("cf-push-blue-green").getDescription()).isEqualTo("Push an Application in a Blue-Green no downtime mode - Stage 2");
    }

    @Test
    public void testCfAutopilotTask() {
        assertThat(project.getTasks().getAt("cf-push-autopilot") instanceof CfAutoPilotTask).isTrue();
        assertThat(project.getTasks().getAt("cf-push-autopilot").getDescription()).isEqualTo("Push an Application in a no downtime Autopilot mode");
    }

    @Test
    public void testCfPushAutopilot() {
        assertThat(project.getTasks().getAt("cf-push-autopilot") instanceof CfAutoPilotTask).isTrue();
        assertThat(project.getTasks().getAt("cf-push-autopilot").getDescription())
            .isEqualTo("Push an Application in a no downtime Autopilot mode");
    }

}