package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import java.io.*;
import java.util.Properties;

public class ApolloUtil {

    public static String APOLLO_USER = "";
    public static String PORTAL_URL = "http://";
    public static String APOLLO_ENV = "";

    public static final String APOLLO_TOKEN = "";

    public static final String APOLLO_CLUSTER_NAME = "default";

    public static final String APOLLO_NAMESPACE_NAME = "application";

    public static final String FLOW_DATA_ID_POSTFIX = "sentinel.flow.rules";

    public static final String DEGRADE_DATA_ID_POSTFIX = "sentinel.degrade.rules";

    public static final String PARAMFLOW_DATA_ID_POSTFIX = "sentinel.paramFlow.rules";

    public static final String GATEWAY_FLOW_DATA_ID_POSTFIX = "sentinel.gatewayFlow.rules";

    public static final String GATEWAY_API_DATA_ID_POSTFIX = "sentinel.gatewayApi.rules";

    public static final String SYSTEM_DATA_ID_POSTFIX = "sentinel.system.rules";
}
