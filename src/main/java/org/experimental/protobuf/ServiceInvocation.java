package org.experimental.protobuf;

import java.util.Arrays;

public class ServiceInvocation {
    private String serviceId;
    private int version;
    private String methodName;
    private Class<?>[] argTypes;
    private Object[] args;

    public ServiceInvocation(String serviceId, int version, String methodName, Class<?>[] argTypes, Object[] args) {
        this.serviceId = serviceId;
        this.version = version;
        this.methodName = methodName;
        this.argTypes = argTypes;
        this.args = args;
    }

    public String getServiceId() {
        return serviceId;
    }

    public int getVersion() {
        return version;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "ServiceInvocation{" +
            "serviceId='" + serviceId + '\'' +
            ", version=" + version +
            ", methodName='" + methodName + '\'' +
            ", argTypes=" + Arrays.toString(argTypes) +
            ", args=" + Arrays.toString(args) +
            '}';
    }
}
