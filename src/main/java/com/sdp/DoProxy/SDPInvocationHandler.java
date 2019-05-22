package com.sdp.DoProxy;/**
 * ClassName:SDPInvocationHandler
 * Package:com.sdp.DoProxy
 * Description:Everything Is
 * Possible!
 *
 * @Date:2019/5/22 16:20
 * @Author:"15029155474@163.com"
 */

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 ClassName:SDPInvocationHandler
 */

//自定义的方法拦截处理器
public class SDPInvocationHandler implements InvocationHandler {

    //将要被代理的对象
    private Person target;

    public Person getTarget() {
        return target;
    }

    //传入将要被代理的对象
    public SDPInvocationHandler(Person target) {
        this.target = target;
    }


    /**
     * @param proxy:代理对象
     * @param method：当前调用的方法
     * @param args：当前调用方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //根据不同的方法实现不同的增强方式
        if(method.getName().equals("say")){
            String result = (String) method.invoke(target,args);
            return "前置增强>"+result+"<后置增强";
        }

        if(method.getName().equals("eat")){
            System.out.println("前置增强！");
            method.invoke(target,args);
            System.out.println("后置增强！");
        }
        return null;
    }
}
