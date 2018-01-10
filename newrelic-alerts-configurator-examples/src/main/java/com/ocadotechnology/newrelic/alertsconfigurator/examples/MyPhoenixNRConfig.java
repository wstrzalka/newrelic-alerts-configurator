package com.ocadotechnology.newrelic.alertsconfigurator.examples;

import com.ocadotechnology.newrelic.alertsconfigurator.Configurator;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.ApplicationConfiguration;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.PolicyConfiguration;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.*;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.nrql.NrqlCondition;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.nrql.NrqlConfiguration;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.nrql.SinceValue;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.terms.*;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.violationclosetimer.ViolationCloseTimer;
import jersey.repackaged.com.google.common.collect.ImmutableList;

import java.util.List;

import static com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.ApmAppCondition.ConditionScope.APPLICATION;
import static com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.UserDefinedConfiguration.ValueFunction.MAX;

public class MyPhoenixNRConfig {

    public static final String DEV_API_KEY = "d0b3226882780f6ba1d43e2fc8d2580e";
    public static final String APPLICATION_NAME = "phoenixcore:andover";

    public static void main(String[] args) {

        setuPhoenixAlers(APPLICATION_NAME, DEV_API_KEY);
    }

    private static void setuPhoenixAlers(String applicationName, String apiKey) {
        ApplicationConfiguration applicationConfig = ApplicationConfiguration.builder()
                .applicationName(applicationName)
                .appApdexThreshold(0.5f)
                .enableRealUserMonitoring(true)
                .endUserApdexThreshold(7f)
                .build();

        Configurator configurator = new Configurator(apiKey);
        configurator.setApplicationConfigurations(ImmutableList.of(applicationConfig));
        configurator.setPolicyConfigurations(ImmutableList.of(applicationConfiguration(applicationName, "dbcore03", "dbcore04")));
        configurator.sync();
    }



    public static PolicyConfiguration applicationConfiguration(String applicationName, String postgresHost1, String postgresHost2) {
        //Note: NRQL Baseline conditions are not available.

        List<Condition> conditions = ImmutableList.of(
                diskSpaceForHost(postgresHost1),
                diskSpaceForHost(postgresHost2),
                gcTime(applicationName),
                pendingContainerMoveBundles("NON_FOOD_INDUCT_ZONE", 1000f, applicationName),
                pendingContainerMoveBundles("FREEZER_SCANNING_RIG_ZONE", 1000f, applicationName),
                pendingContainerMoveBundles("FREEZER_LOGICAL_ZONE", 1000f, applicationName),
                pendingContainerMoveBundles("FRAME_LOADING_ZONE", 1000f, applicationName),
                pendingContainerMoveBundles("EXTERNAL_TOTE_LOGICAL_ZONE", 1000f, applicationName),
                pendingContainerMoveBundles("EXTERNAL_TOTES_ZONE", 2000f, applicationName),
                pendingContainerMoveBundles("CHILL_RECEIVE_ZONE", 1000f, applicationName),
                pendingContainerMoveBundles("CHILL_RAINBOW_ZONE", 3000f, applicationName),
                pendingContainerMoveBundles("CHILL_DECANT_ZONE", 1000f, applicationName),
                pendingContainerMoveBundles("BAGGING_ZONE", 1000f, applicationName),
                pendingContainerMoveBundles("AMBIENT_RECEIVE_ZONE", 1000f, applicationName),
                pendingContainerMoveBundles("AMBIENT_RAINBOW_ZONE", 3000f, applicationName),
                pendingContainerMoveBundles("AMBIENT_DECANT_ZONE", 1000f, applicationName),
                pendingTaskBundles("FREEZER_ZONE", 2000f, applicationName),
                pendingTaskBundles("FRAME_LOADING_ZONE", 2000f, applicationName),
                pendingTaskBundles("DISPATCH_ZONE", 1000f, applicationName),
                pendingTaskBundles("AMBIENT_DECANT_ZONE", 3000f, applicationName),
                pendingTaskBundles("CHILL_DECANT_ZONE", 3000f, applicationName),
                pendingContainerMoveBundles("DISPATCH_ZONE", 2000f, applicationName),
                pendingTaskBundles("AMBIENT_RAINBOW_ZONE", 4000f, applicationName),
                pendingTaskBundles("CHILL_RAINBOW_ZONE", 4000f, applicationName),
                pendingContainerMoveBundles("FREEZER_ZONE", 2000f, applicationName));

        List<NrqlCondition> nrqlConditions = ImmutableList.of(
                longCDTTLEProcessing(applicationName),
                lessThanTwoInstances(applicationName),
                circuitBreaker("NON_FOOD_INDUCT_ZONE", applicationName),
                circuitBreaker("FREEZER_SCANNING_RIG_ZONE", applicationName),
                circuitBreaker("FREEZER_LOGICAL_ZONE", applicationName),
                circuitBreaker("EXTERNAL_TOTE_LOGICAL_ZONE", applicationName),
                circuitBreaker("EXTERNAL_TOTES_ZONE", applicationName),
                circuitBreaker("CHILL_RECEIVE_ZONE", applicationName),
                circuitBreaker("BAGGING_ZONE", applicationName),
                circuitBreaker("AMBIENT_RECEIVE_ZONE", applicationName),
                circuitBreaker("FRAME_LOADING_ZONE", applicationName),
                circuitBreaker("AMBIENT_DECANT_ZONE", applicationName),
                circuitBreaker("CHILL_DECANT_ZONE", applicationName),
                circuitBreaker("DISPATCH_ZONE", applicationName),
                circuitBreaker("AMBIENT_RAINBOW_ZONE", applicationName),
                circuitBreaker("CHILL_RAINBOW_ZONE", applicationName),
                circuitBreaker("FREEZER_ZONE", applicationName));


        return PolicyConfiguration.builder()
                .policyName("Test policy for phoenix core from DSL")
                .incidentPreference(PolicyConfiguration.IncidentPreference.PER_POLICY)
                .condition(Defaults.apdexCondition(applicationName))
                .conditions(conditions)
                .nrqlConditions(nrqlConditions)
                .channel(Defaults.teamEmailChannel())
                .channel(Defaults.slackChannel())
                .build();
    }

    private static ApmAppCondition pendingContainerMoveBundles(String zoneId, float treshold, String applicationName) {
        return ApmAppCondition.builder()
                .application(applicationName)
                .conditionScope(APPLICATION)
                .conditionName("Pending Container Moves Bundles " + zoneId)
                .enabled(true)
                .metric(ApmAppCondition.Metric.USER_DEFINED)
                .conditionScope(APPLICATION)
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.CRITICAL)
                        .thresholdTerm(treshold)
                        .timeFunctionTerm(TimeFunctionTerm.ANY)
                        .build())
                .userDefinedConfiguration(UserDefinedConfiguration.builder()
                        .metric("Custom/" + applicationName + ":PendingContainerMovesBundlesEvent/" +
                                zoneId)
                        .valueFunction(MAX)
                        .build()
                ).build();
    }

    private static ApmAppCondition pendingTaskBundles(String zoneId, float treshold, String applicationName) {
        return ApmAppCondition.builder()
                .application(applicationName)
                .conditionScope(APPLICATION)
                .conditionName("Pending Task Bundles " + zoneId)
                .enabled(true)
                .metric(ApmAppCondition.Metric.USER_DEFINED)
                .conditionScope(APPLICATION)
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.CRITICAL)
                        .thresholdTerm(treshold)
                        .timeFunctionTerm(TimeFunctionTerm.ANY)
                        .build())
                .userDefinedConfiguration(UserDefinedConfiguration.builder()
                        .metric("Custom/" + applicationName + ":PendingTaskBundlesEvent/" + zoneId)
                        .valueFunction(MAX).build()).build();
    }

    private static NrqlCondition lessThanTwoInstances(final String applicationName) {
        return NrqlCondition.builder()
                .conditionName("Phoenix core running on less than 2 instances")
                .enabled(true)
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.BELOW)
                        .priorityTerm(PriorityTerm.CRITICAL)
                        .thresholdTerm(2f)
                        .timeFunctionTerm(TimeFunctionTerm.ALL)
                        .build())
                .valueFunction(NrqlCondition.ValueFunction.SINGLE_VALUE)
                .nrql(NrqlConfiguration.builder()
                        .query("SELECT min(0.0001+instancesUp) FROM `" + applicationName + ":instancesBehindLoadBalancer`")
                        .sinceValue(SinceValue.SINCE_5_MINUTES)
                        .build())
                .build();
    }

    private static NrqlCondition longCDTTLEProcessing(final String applicationName) {
        return NrqlCondition.builder()
                .conditionName("Long processing of CDTTLE->CDTTLN")
                .enabled(true)
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.CRITICAL)
                        .thresholdTerm(500f)
                        .timeFunctionTerm(TimeFunctionTerm.ANY)
                        .build())
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.WARNING)
                        .thresholdTerm(5f)
                        .timeFunctionTerm(TimeFunctionTerm.ANY)
                        .build())
                .valueFunction(NrqlCondition.ValueFunction.SINGLE_VALUE)
                .nrql(NrqlConfiguration.builder()
                        .query("SELECT count(*) FROM Transaction WHERE appName='" + applicationName + "' AND messageType =  " +
                                "'ContainerDeliveredToTransitionLocationNotification' AND module='BRIDGE_FOR_CORE' AND camel_deliveryCount=0 " +
                                "AND camel_coreProcessingTime/1000 > 10")
                        .sinceValue(SinceValue.SINCE_3_MINUTES)
                        .build())
                .build();
    }

    private static ApmJvmCondition gcTime(String applicationName) {
        return ApmJvmCondition.builder()
                .application(applicationName)
                .conditionName("PS MarkSweep (High)")
                .enabled(true)
                .metric(ApmJvmCondition.Metric.GC_CPU_TIME)
                .gcMetric(ApmJvmCondition.GcMetric.GC_MARK_SWEEP)
                .violationCloseTimer(ViolationCloseTimer.DURATION_24)
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_60)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.WARNING)
                        .thresholdTerm(10f)
                        .timeFunctionTerm(TimeFunctionTerm.ALL)
                        .build())
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.CRITICAL)
                        .thresholdTerm(50f)
                        .timeFunctionTerm(TimeFunctionTerm.ALL)
                        .build()).build();
    }

    private static ApmAppCondition errorPercentage(String applicationName) {
        return ApmAppCondition.builder()
                .application(applicationName)
                .conditionScope(APPLICATION)
                .conditionName("Error percentage (High)")
                .enabled(true)
                .metric(ApmAppCondition.Metric.ERROR_PERCENTAGE)
                .conditionScope(APPLICATION)
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.WARNING)
                        .thresholdTerm(1f)
                        .timeFunctionTerm(TimeFunctionTerm.ALL)
                        .build())
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.CRITICAL)
                        .thresholdTerm(5f)
                        .timeFunctionTerm(TimeFunctionTerm.ALL)
                        .build()).build();
    }

    private static ServersMetricCondition diskSpaceForHost(String postgresHost1) {
        return ServersMetricCondition.builder()
                .server(postgresHost1)
                .conditionName("Fullest Disk % for " + postgresHost1)
                .enabled(true)
                .metric(ServersMetricCondition.Metric.FULLEST_DISK_PERCENTAGE)
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.CRITICAL)
                        .thresholdTerm(90f)
                        .timeFunctionTerm(TimeFunctionTerm.ALL)
                        .build())
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.WARNING)
                        .thresholdTerm(70f)
                        .timeFunctionTerm(TimeFunctionTerm.ALL)
                        .build()).build();
    }

    private static NrqlCondition circuitBreaker(String zone, final String applicationName) {
        return NrqlCondition.builder()
                .conditionName(zone + " DOWN")
                .enabled(true)
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.WARNING)
                        .thresholdTerm(1f)
                        .timeFunctionTerm(TimeFunctionTerm.ALL)
                        .build())
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_120)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.CRITICAL)
                        .thresholdTerm(1000f)
                        .timeFunctionTerm(TimeFunctionTerm.ALL)
                        .build())
                .valueFunction(NrqlCondition.ValueFunction.SINGLE_VALUE)
                .nrql(NrqlConfiguration.builder()
                        .query("SELECT count(*) from `" + applicationName + ":CircuitBrakerRetryCounterEvent` where zoneId = '" +zone+"'")
                        .sinceValue(SinceValue.SINCE_3_MINUTES)
                        .build())
                .build();
    }
}
