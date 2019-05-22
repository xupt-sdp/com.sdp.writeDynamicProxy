package com.sdp.DoProxy;/**
 * ClassName:MyTest
 * Package:com.sdp.DoProxy
 * Description:Everything Is
 * Possible!
 *
 * @Date:2019/5/22 16:34
 * @Author:"15029155474@163.com"
 */


import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 ClassName:MyTest
 */

public class MyTest {
    @Test
    public void fun1() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        //生成被代理的对象
        YAAG yaag = new YAAG();

        //根据被代理的对象生成代理对象
        Person proxyInstance = (Person) SDPProxyUtil.newProxyInstance
                (new SDPClassLoader(),yaag.getClass().getInterfaces(),new SDPInvocationHandler(yaag));

        System.out.println(proxyInstance.say());
        System.out.println("________________________________________________________");
        proxyInstance.eat();

    }

}
