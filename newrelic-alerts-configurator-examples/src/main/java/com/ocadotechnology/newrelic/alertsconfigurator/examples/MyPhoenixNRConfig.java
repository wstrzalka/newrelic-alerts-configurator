package com.ocadotechnology.newrelic.alertsconfigurator.examples;

import com.ocadotechnology.newrelic.alertsconfigurator.Configurator;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.ApplicationConfiguration;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.PolicyConfiguration;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.ApmAppCondition;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.Condition;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.ServersMetricCondition;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.UserDefinedConfiguration;
import com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.terms.*;
import jersey.repackaged.com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import static com.ocadotechnology.newrelic.alertsconfigurator.configuration.condition.UserDefinedConfiguration.ValueFunction.MAX;

public class MyPhoenixNRConfig {

    public static final String DEV_API_KEY = "4a0a89ae455c603c76a99ca436218d63";
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


//
//    "settings": {
//        "app_apdex_threshold": 0.5,
//                "end_user_apdex_threshold": 7,
//                "enable_real_user_monitoring": true,
//                "use_server_side_config": true
//    },

    private static ApmAppCondition pendingContainerMoveBundles(String zoneId, float treshold) {
        return ApmAppCondition.builder()
                .application(APPLICATION_NAME)
                .conditionScope(ApmAppCondition.ConditionScope.APPLICATION)
                .conditionName("Pending Container Moves Bundles " + zoneId)
                .enabled(true)
                .metric(ApmAppCondition.Metric.USER_DEFINED)
                .conditionScope(ApmAppCondition.ConditionScope.APPLICATION)
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.CRITICAL)
                        .thresholdTerm(treshold)
                        .timeFunctionTerm(TimeFunctionTerm.ANY)
                        .build())
                .userDefinedConfiguration(UserDefinedConfiguration.builder()
                        .metric("Custom/phoenixcoretest:PendingContainerMovesBundlesEvent/" +
                                zoneId)
                        .valueFunction(MAX)
                        .build()
                ).build();
    }


    private static ApmAppCondition pendingTaskBundles(String zoneId, float treshold) {
        return ApmAppCondition.builder()
                .application(APPLICATION_NAME)
                .conditionScope(ApmAppCondition.ConditionScope.APPLICATION)
                .conditionName("Pending Task Bundles " + zoneId)
                .enabled(true)
                .metric(ApmAppCondition.Metric.USER_DEFINED)
                .conditionScope(ApmAppCondition.ConditionScope.APPLICATION)
                .term(TermsConfiguration.builder()
                        .durationTerm(DurationTerm.DURATION_5)
                        .operatorTerm(OperatorTerm.ABOVE)
                        .priorityTerm(PriorityTerm.CRITICAL)
                        .thresholdTerm(treshold)
                        .timeFunctionTerm(TimeFunctionTerm.ANY)
                        .build())
                .userDefinedConfiguration(UserDefinedConfiguration.builder()
                        .metric("Custom/phoenixcoretest:PendingTaskBundlesEvent/" + zoneId)
                        .valueFunction(MAX).build()).build();
    }

    public static PolicyConfiguration applicationConfiguration() {
        List<Condition> conditions = new ArrayList<>();

        conditions.add(ServersMetricCondition.builder()
                .servers(ImmutableList.of("ldhfdb06"))
                .conditionName("Fullest Disk % (High)")
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

//
//        conditions.add(ApmAppCondition.builder()
//                .application(APPLICATION_NAME)
//                .conditionScope(ApmAppCondition.ConditionScope.APPLICATION)
//                .conditionName("Postgres servers dbcore03 | dbcore04 Fullest Disk % (High)")
//                .enabled(true)
//                .metric(ApmAppCondition.Metric.FULLEST_DISK_PERCENTAGE)
//                .term(TermsConfiguration.builder()
//                        .durationTerm(DurationTerm.DURATION_5)
//                        .operatorTerm(OperatorTerm.ABOVE)
//                        .priorityTerm(PriorityTerm.CRITICAL)
//                        .thresholdTerm(90f)
//                        .timeFunctionTerm(TimeFunctionTerm.ALL)
//                        .build())
//                .term(TermsConfiguration.builder()
//                        .durationTerm(DurationTerm.DURATION_5)
//                        .operatorTerm(OperatorTerm.ABOVE)
//                        .priorityTerm(PriorityTerm.WARNING)
//                        .thresholdTerm(70f)
//                        .timeFunctionTerm(TimeFunctionTerm.ALL)
//                        .build()).build());


        conditions.add(ApmAppCondition.builder()
                .application(APPLICATION_NAME)
                .conditionScope(ApmAppCondition.ConditionScope.APPLICATION)
                .conditionName("Error percentage (High)")
                .enabled(true)
                .metric(ApmAppCondition.Metric.ERROR_PERCENTAGE)
                .conditionScope(ApmAppCondition.ConditionScope.APPLICATION)
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

//        conditions.add(ApmJvmCondition.builder()
//                .application(APPLICATION_NAME)
//                .conditionName("PS MarkSweep (High)")
//                .enabled(true)
//                .metric(ApmJvmCondition.Metric.GC_CPU_TIME)
//                .gcMetric(ApmJvmCondition.GcMetric.GC_MARK_SWEEP)
//                .violationCloseTimer(ViolationCloseTimer.DURATION_24)
//                .term(TermsConfiguration.builder()
//                        .durationTerm(DurationTerm.DURATION_60)
//                        .operatorTerm(OperatorTerm.ABOVE)
//                        .priorityTerm(PriorityTerm.WARNING)
//                        .thresholdTerm(10f)
//                        .timeFunctionTerm(TimeFunctionTerm.ALL)
//                        .build())
//                .term(TermsConfiguration.builder()
//                        .durationTerm(DurationTerm.DURATION_5)
//                        .operatorTerm(OperatorTerm.ABOVE)
//                        .priorityTerm(PriorityTerm.CRITICAL)
//                        .thresholdTerm(50f)
//                        .timeFunctionTerm(TimeFunctionTerm.ALL)
//                        .build()).build());
//
        conditions.add(pendingContainerMoveBundles("NON_FOOD_INDUCT_ZONE", 1000f));
        conditions.add(pendingContainerMoveBundles("FREEZER_SCANNING_RIG_ZONE", 1000f));
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


        return PolicyConfiguration.builder()
                .policyName("test policy for phoenix core")
                .incidentPreference(PolicyConfiguration.IncidentPreference.PER_POLICY)
                .condition(Defaults.apdexCondition(APPLICATION_NAME))
//                .condition(Defaults.diskSpaceCondition("app-1-host"))
                .conditions(conditions)
                .channel(Defaults.teamEmailChannel())
                .channel(Defaults.slackChannel())
                .build();
    }
}
