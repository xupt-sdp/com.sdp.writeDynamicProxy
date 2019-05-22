package com.sdp.DoProxy;/**
 * ClassName:SDPProxyUtil
 * Package:com.sdp.DoProxy
 * Description:Everything Is
 * Possible!
 *
 * @Date:2019/5/22 16:21
 * @Author:"15029155474@163.com"
 */

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


/**
 ClassName:SDPProxyUtil
 */

//自定义的代理生成工具
public class SDPProxyUtil implements Serializable {


    private static final String ln = "\r\n";
    /**
     *
     * @param loader:类加载器，我们将传入我们自定义的类加载器
     * @param interfaces：被代理对象所实现的所有接口
     * @param h：方法调用拦截处理器
     * @return
     */
    public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, SDPInvocationHandler h) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {


        /**
         * 1.通过手写生成源代码
         */
        String source = getProxySource(interfaces);


        /**
         * 2.将生成的源代码保存到磁盘(.java格式)
         */
        String path = SDPProxyUtil.class.getResource("").getPath();

        File file = new File(path + "$Proxy0.java");

        FileOutputStream fos = new FileOutputStream(file);

        byte[] bytes = source.getBytes();

        fos.write(bytes);

        fos.close();

        /**
         *
         *   3.将磁盘上的.java文件通过自定义的类加载器编译成.class文件
         */

        //使用JDK自带的工具类获取我们的Javac编译器;
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        //标准的文件管理
        StandardJavaFileManager standardFileManager = javaCompiler.getStandardFileManager(null, null, null);
        //对指定文件进行编译
        Iterable javaFileObjects = standardFileManager.getJavaFileObjects(file);
        //创建编译任务
        JavaCompiler.CompilationTask task =
                        javaCompiler.getTask(null,standardFileManager,null,null,null,javaFileObjects);
        //开启任务
        task.call();
        //关闭管理器
        standardFileManager.close();
        /**
         * 4.将.class文件动态加载进虚拟机中
         */
        Class<?> proxyClazz = loader.loadClass(file.getName().replace("java","class"));

        /**
         * 5.返回被代理后的结果对象
         */

        //使用反射获取构造器创建代理对象实例
        Constructor<?> constructor = proxyClazz.getConstructor(new Class<?>[]{Person.class});

        Person proxyPerson = (Person) constructor.newInstance(h.getTarget());

        //删除生成的.java文件
        file.delete();

        return proxyPerson;
    }


    //手写.java代码，注意一定要细心，否则很容易出错
    private static  String  getProxySource(Class<?>[] interfaces){

        StringBuffer src = new StringBuffer();
        src.append("package com.sdp.DoProxy;" + ln);
        src.append("import java.lang.reflect.Method;" + ln);

        src.append("public class $Proxy0 extends SDPInvocationHandler implements ");

                //遍历接口使代理类实现当前所有接口
                for(int i = 0;i<interfaces.length;i++){
                    if(i<interfaces.length-1){
                        src.append(interfaces[i].getName()+",");
                    }else{
                        src.append(interfaces[i].getName()+"{" + ln);
                    }
                }

        src.append("public $Proxy0(Person target) {" + ln);
        src.append("super(target);" + ln);
        src.append("}" + ln);


        //遍历当前所有接口，为所有接口中的所有方法生成一个代理方法
        for(Class<?> clazz : interfaces){
            for (Method m : clazz.getMethods()) {
                src.append("public final " + m.getReturnType().getName() + " " + m.getName() + "(){" + ln);

                src.append("try{" + ln);
                src.append("Method m = " + clazz.getName() + ".class.getMethod(\"" +m.getName()+"\",new Class[]" +
                        Arrays.toString(m.getParameterTypes()).replace('[','{').replace(']','}')+
                        ");" + ln);
                src.append("Object result = this.invoke(this,m,new Object[]{");

                for(int i =0 ;i < m.getParameterCount() ;i++){
                    if(i<m.getParameterCount()-1){
                        src.append("var"+i+",");
                    }else{
                        src.append("var"+i);
                    }

                }
                src.append("});"+ln);

                if(!m.getReturnType().getName().contains("void")){
                    src.append("return ("+m.getReturnType().getName()+")result;");
                }

                src.append("}catch(Throwable e){e.printStackTrace();}" + ln);

                if(!m.getReturnType().getName().contains("void")){
                    src.append("return null;"+ln);
                }

                src.append("}" + ln);
            }
        }
        src.append("}"+ln);


        return src.toString();
    }
}
