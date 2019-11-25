package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import java.io.*;
import java.util.Properties;

public class ApolloUtil {

    public static String APOLLO_USER;
    public static String PORTAL_URL;
    public static String APOLLO_ENV;

    static{
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream("/root/sentinel/sentinel-apollo.properties"));
            Properties p = new Properties();
            p.load(in);
            APOLLO_USER = p.getProperty("APOLLO_USER");
            PORTAL_URL = p.getProperty("PORTAL_URL");
            APOLLO_ENV = p.getProperty("APOLLO_ENV");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static final String APOLLO_TOKEN = "7541522955101f5bfde0cf009b67b2cf95b76ede";

    public static final String APOLLO_CLUSTER_NAME = "default";

    public static final String APOLLO_NAMESPACE_NAME = "application";

    public static final String FLOW_DATA_ID_POSTFIX = "ndf.sentinel.flow.rules";

    public static final String DEGRADE_DATA_ID_POSTFIX = "ndf.sentinel.degrade.rules";

    public static final String PARAMFLOW_DATA_ID_POSTFIX = "ndf.sentinel.paramFlow.rules";

    public static final String GATEWAY_FLOW_DATA_ID_POSTFIX = "ndf.sentinel.gatewayFlow.rules";

    public static final String GATEWAY_API_DATA_ID_POSTFIX = "ndf.sentinel.gatewayApi.rules";

    public static final String SYSTEM_DATA_ID_POSTFIX = "ndf.sentinel.system.rules";
}
