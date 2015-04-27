package org.home.experimental.proxy;

import java.lang.reflect.Proxy;

import org.junit.Test;

import org.home.experimental.IWorker;

/**
 * Created by mattg on 3/19/15. Shows how a Proxy object can be used together with a InvocationHandler.
 */
public class ProxyTesting {

    @Test
    public void testProxy() {

        // This handler implements InvocationHandler and contains if/else statements to return different objects
        // based on the Method passed in.
        WorkerHandler handler = new WorkerHandler();

        IWorker proxy = (IWorker) Proxy.newProxyInstance(WorkerHandler.class.getClassLoader(),
                new Class[] { IWorker.class }, handler);

        String theRole = proxy.role();
        System.out.println("role=" + theRole);
    }
}
