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

import java.util.ArrayList;
import java.util.List;

import static com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.ApmAppCondition.ConditionScope.APPLICATION;
import static com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.UserDefinedConfiguration.ValueFunction.MAX;

public class MyPhoenixNRConfig {

    public static final String DEV_API_KEY = "d0b3226882780f6ba1d43e2fc8d2580e";
    public static final String APPLICATION_NAME = "phoenixcore:andover";

    public static void main(String[] args) {

        ApplicationConfiguration applicationConfig = ApplicationConfiguration.builder()
                .applicationName("phoenixcore:andover")
                .appApdexThreshold(0.5f)
                .enableRealUserMonitoring(true)
                .endUserApdexThreshold(7f)
                .build();

        Configurator configurator = new Configurator(DEV_API_KEY);
        configurator.setApplicationConfigurations(ImmutableList.of(applicationConfig));
        configurator.setPolicyConfigurations(ImmutableList.of(applicationConfiguration()));
        configurator.sync();
    }


    private static ApmAppCondition pendingContainerMoveBundles(String zoneId, float treshold) {
        return ApmAppCondition.builder()
                .application(APPLICATION_NAME)
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
                        .metric("Custom/" + APPLICATION_NAME + ":PendingContainerMovesBundlesEvent/" +
                                zoneId)
                        .valueFunction(MAX)
                        .build()
                ).build();
    }


    private static ApmAppCondition pendingTaskBundles(String zoneId, float treshold) {
        return ApmAppCondition.builder()
                .application(APPLICATION_NAME)
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
                        .metric("Custom/" + APPLICATION_NAME + ":PendingTaskBundlesEvent/" + zoneId)
                        .valueFunction(MAX).build()).build();
    }

    public static PolicyConfiguration applicationConfiguration() {
        List<Condition> conditions = new ArrayList<>();

        conditions.add(ServersMetricCondition.builder()
                .servers(ImmutableList.of("dbcore03"))
                .conditionName("Fullest Disk % for dbcore03")
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
                        .build()).build());

        conditions.add(ServersMetricCondition.builder()
                .servers(ImmutableList.of("dbcore04"))
                .conditionName("Fullest Disk % for dbcore04")
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
                        .build()).build());


        conditions.add(ApmAppCondition.builder()
                .application(APPLICATION_NAME)
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
                        .build()).build());

        conditions.add(ApmJvmCondition.builder()
                .application(APPLICATION_NAME)
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
                        .build()).build());

        conditions.add(pendingContainerMoveBundles("NON_FOOD_INDUCT_ZONE", 1000f));
        conditions.add(pendingContainerMoveBundles("FREEZER_SCANNING_RIG_ZONE", 1000f));
        conditions.add(pendingContainerMoveBundles("FREEZER_LOGICAL_ZONE", 1000f));
        conditions.add(pendingContainerMoveBundles("FRAME_LOADING_ZONE", 1000f));
        conditions.add(pendingContainerMoveBundles("EXTERNAL_TOTE_LOGICAL_ZONE", 1000f));
        conditions.add(pendingContainerMoveBundles("EXTERNAL_TOTES_ZONE", 2000f));
        conditions.add(pendingContainerMoveBundles("CHILL_RECEIVE_ZONE", 1000f));
        conditions.add(pendingContainerMoveBundles("CHILL_RAINBOW_ZONE", 3000f));
        conditions.add(pendingContainerMoveBundles("CHILL_DECANT_ZONE", 1000f));
        conditions.add(pendingContainerMoveBundles("BAGGING_ZONE", 1000f));
        conditions.add(pendingContainerMoveBundles("AMBIENT_RECEIVE_ZONE", 1000f));
        conditions.add(pendingContainerMoveBundles("AMBIENT_RAINBOW_ZONE", 3000f));
        conditions.add(pendingContainerMoveBundles("AMBIENT_DECANT_ZONE", 1000f));
        conditions.add(pendingTaskBundles("FREEZER_ZONE", 2000f));
        conditions.add(pendingTaskBundles("FRAME_LOADING_ZONE", 2000f));
        conditions.add(pendingTaskBundles("DISPATCH_ZONE", 1000f));
        conditions.add(pendingTaskBundles("AMBIENT_DECANT_ZONE", 3000f));
        conditions.add(pendingTaskBundles("CHILL_DECANT_ZONE", 3000f));
        conditions.add(pendingContainerMoveBundles("DISPATCH_ZONE", 2000f));
        conditions.add(pendingTaskBundles("AMBIENT_RAINBOW_ZONE", 4000f));
        conditions.add(pendingTaskBundles("CHILL_RAINBOW_ZONE", 4000f));
        conditions.add(pendingContainerMoveBundles("FREEZER_ZONE", 2000f));


        List<NrqlCondition> nrqlConditions = new ArrayList<>();

        nrqlConditions.add(
                NrqlCondition.builder()
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
                                .query("SELECT count(*) FROM Transaction WHERE appName='phoenixcore:andover' AND messageType =  'ContainerDeliveredToTransitionLocationNotification' AND module='BRIDGE_FOR_CORE' AND camel_deliveryCount=0 AND camel_coreProcessingTime/1000 > 10")
                                .sinceValue(SinceValue.SINCE_3_MINUTES)
                                .build())
                        .build());

        nrqlConditions.add(
                NrqlCondition.builder()
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
                                .query("SELECT min(0.0001+instancesUp) FROM `phoenixcore:andover:instancesBehindLoadBalancer`")
                                .sinceValue(SinceValue.SINCE_5_MINUTES)
                                .build())
                        .build());

        return PolicyConfiguration.builder()
                .policyName("Test policy for phoenix core from DSL")
                .incidentPreference(PolicyConfiguration.IncidentPreference.PER_POLICY)
                .condition(Defaults.apdexCondition(APPLICATION_NAME))
                .conditions(conditions)
                .nrqlConditions(nrqlConditions)
                .channel(Defaults.teamEmailChannel())
                .channel(Defaults.slackChannel())
                .build();
    }
}
