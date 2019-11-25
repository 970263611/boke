/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule.apollo;

/**
 * @author hantianwei@gmail.com
 * @since 1.5.0
 */
public final class ApolloConfigUtil {



    private ApolloConfigUtil() {
    }

    public static String getFlowDataId(String appName) {
        return ApolloUtil.FLOW_DATA_ID_POSTFIX;
    }

    public static String getDegradeDataId(String appName) {
        return ApolloUtil.DEGRADE_DATA_ID_POSTFIX;
    }

    public static String getParamFlowDataId(String appName) {
        return ApolloUtil.PARAMFLOW_DATA_ID_POSTFIX;
    }

    public static String getGateWayFlowDataId(String appName) {
        return ApolloUtil.GATEWAY_FLOW_DATA_ID_POSTFIX;
    }

    public static String getGateWayApiDataId(String appName) {
        return ApolloUtil.GATEWAY_API_DATA_ID_POSTFIX;
    }

    public static String getSystemDataId(String appName) {
        return ApolloUtil.SYSTEM_DATA_ID_POSTFIX;
    }
}
