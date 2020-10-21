package jvm2;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyClassLoader extends ClassLoader{
    //main方法
    public static void main(String[] args) throws ClassNotFoundException {
        MyClassLoader loader = new MyClassLoader();
        //找到对应的class文件
        Class<?> testClass = loader.findClass("Hello");
        try{
            Object obj = testClass.newInstance();
            Method method = testClass.getMethod("hello");
            method.invoke(obj);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //重写findClass方法
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        String classPath =
                "file:///JavaDevelop/meta-workspace/java_advanced_geektime/jvm/src/jvm2/" + name + ".xlass";
        //name应该是包名+类名
        System.out.println(classPath);
        byte[] classBytes = null;
        Path path = null;

        try{
            //设置文件读取路径
            path = Paths.get(new URI(classPath));
            //通过Hello.xlass文件所在路径读取到字节流
            classBytes = Files.readAllBytes(path);
            //还原字节数组中的每个字节
            for(int i=0; i<classBytes.length; i++){
                classBytes[i] = (byte) (255 - classBytes[i]);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Class clazz = defineClass(name,classBytes,0,classBytes.length);
        return clazz;
    }

}
