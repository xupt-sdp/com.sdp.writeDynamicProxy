package com.sdp.DoProxy;/**
 * ClassName:SDPClassLoader
 * Package:com.sdp.DoProxy
 * Description:Everything Is
 * Possible!
 *
 * @Date:2019/5/22 16:21
 * @Author:"15029155474@163.com"
 */

import java.io.*;

/**
 ClassName:SDPClassLoader
 */

//自己手写的类加载器
public class SDPClassLoader extends ClassLoader {

    //当前类所对应的绝对路径
    private File dir = null;

    public SDPClassLoader() {
        this.dir = new File(SDPClassLoader.class.getResource("").getPath());
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        //获取当前包对应当前项目的(src)的相对路径
        String className = SDPClassLoader.class.getPackage().getName() + "." + name;
        //创建文件输入流，读入动态生成的.class文件
        FileInputStream  fis = null;

        File classFile = new File(dir+"\\"+name);

        try {
            //将字节码从文件读入内存
            fis = new FileInputStream(classFile);
            //将字节码从内存写入JVM虚拟机
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int len = 0;

            byte[] car = new byte[1024];

            while((len = fis.read(car)) != -1){
                bos.write(car,0,len);
            }
            //返回生成的Class 类对象
            return defineClass(className.replace(".class",""),bos.toByteArray(),0,bos.size());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis!=null){
                try {
                    //释放资源
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            classFile.delete();
        }

        return super.findClass(name);
    }
}
