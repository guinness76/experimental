package org.experimental.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by mattg on 3/19/15.
 */
public class WorkerHandler implements InvocationHandler {

    /**
     * Dynamically return an object based on the passed Method and other arguments.
     * 
     * @param proxy
     *            the interface used when the Proxy was created
     * @param method
     *            the requested method
     * @param args
     *            any other method args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.getName().equals("role")) {
            return "proxy role";
        } else {
            return null;
        }

    }
}
