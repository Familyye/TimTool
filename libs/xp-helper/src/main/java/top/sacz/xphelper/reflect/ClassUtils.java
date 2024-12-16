package top.sacz.xphelper.reflect;


import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import top.sacz.xphelper.exception.ReflectException;

public class ClassUtils {
    private static final Object[][] baseTypes = {{"int", int.class}, {"boolean", boolean.class}, {"byte", byte.class}, {"long", long.class}, {"char", char.class}, {"double", double.class}, {"float", float.class}, {"short", short.class}, {"void", void.class}};
    private static ClassLoader classLoader;//宿主应用类加载器

    public static ClassLoader getModuleClassLoader() {
        return ClassUtils.class.getClassLoader();
    }

    public static ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * 获取基本类型
     */
    private static Class<?> getBaseTypeClass(String baseTypeName) {
        if (baseTypeName.length() == 1) return findSimpleType(baseTypeName.charAt(0));
        for (Object[] baseType : baseTypes) {
            if (baseTypeName.equals(baseType[0])) {
                return (Class<?>) baseType[1];
            }
        }
        throw new ReflectException(baseTypeName + " <-不是基本的数据类型");
    }

    /**
     * conversion base type
     *
     * @param simpleType Smali Base Type V,Z,B,I...
     */
    private static Class<?> findSimpleType(char simpleType) {
        switch (simpleType) {
            case 'V':
                return void.class;
            case 'Z':
                return boolean.class;
            case 'B':
                return byte.class;
            case 'S':
                return short.class;
            case 'C':
                return char.class;
            case 'I':
                return int.class;
            case 'J':
                return long.class;
            case 'F':
                return float.class;
            case 'D':
                return double.class;
        }
        throw new RuntimeException("Not an underlying type");
    }

    /**
     * 排除常用类
     */
    public static boolean isCommonlyUsedClass(String name) {
        return name.startsWith("androidx.") || name.startsWith("android.") || name.startsWith("kotlin.") || name.startsWith("kotlinx.") || name.startsWith("com.tencent.mmkv.") || name.startsWith("com.android.tools.r8.") || name.startsWith("com.google.android.") || name.startsWith("com.google.gson.") || name.startsWith("com.google.common.") || name.startsWith("com.microsoft.appcenter.") || name.startsWith("org.intellij.lang.annotations.") || name.startsWith("org.jetbrains.annotations.");
    }

    /**
     * 获取类
     */
    public static Class<?> findClass(String className) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void intiClassLoader(ClassLoader loader) {
        if (loader == null) throw new ReflectException("类加载器为Null 无法设置");
        //如果我们自己重写了 就不再次继承
        if (loader instanceof CacheClassLoader) {
            classLoader = loader;
            return;
        }
        classLoader = new CacheClassLoader(loader);
    }


    private static class CacheClassLoader extends ClassLoader {
        private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();

        public CacheClassLoader(ClassLoader classLoader) {
            super(classLoader);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            Class<?> clazz = CLASS_CACHE.get(name);
            if (clazz != null) {
                return clazz;
            }
            try {
                //可能是数组类型的
                if (name.startsWith("[")) {
                    int index = name.lastIndexOf('[');
                    //获取原类型
                    try {
                        clazz = getBaseTypeClass(name.substring(index + 1));
                    } catch (Exception e) {
                        clazz = super.loadClass(name.substring(index + 1));
                    }
                    //转换数组类型
                    for (int i = 0; i < name.length(); i++) {
                        char ch = name.charAt(i);
                        if (ch == '[') {
                            clazz = Array.newInstance(clazz, 0).getClass();
                        } else {
                            break;
                        }
                    }
                    CLASS_CACHE.put(name, clazz);
                    return clazz;
                }
                //可能是基础类型
                try {
                    clazz = getBaseTypeClass(name);
                } catch (Exception e) {
                    //因为默认的ClassLoader.load() 不能加载"int"这种类型
                    clazz = super.loadClass(name);
                }
                CLASS_CACHE.put(name, clazz);
                return clazz;
            } catch (Throwable throwable) {
                throw new ReflectException("没有找到类: " + name);
            }
        }

    }
}
