package com.hamburger.ipc;

import android.os.Bundle;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class HamburgerUtils {

    private static final String KEY_METHOD_NAME = "method";
    private static final String KEY_METHOD_COUNT = "count";
    private static final String KEY_METHOD_TYPE = "type";
    private static final String KEY_METHOD_ARG = "arg";
    private static final String KEY_METHOD_RESULT = "result";

    private static String parcelMapKey(String key) {
        return "key[" + key + "]";
    }

    private static String unParcelMapKey(String key) {
        if (key.length() <= 5) {
            throw new IllegalArgumentException("Map key error. " + key);
        }
        return key.substring(4, key.length() - 1);
    }

    static Bundle methodToBundle(Method method, Object[] args) {
        Logger.getInternalLog().log("[methodToBundle] method: " + method + ", args: " + Arrays.toString(args));
        String methodName = method.getName();
        Logger.getInternalLog().log("[methodToBundle] methodName: " + methodName);

        Bundle result = new Bundle();
        result.putString(KEY_METHOD_NAME, methodName);
        Class<?>[] parameterTypes = method.getParameterTypes();
        result.putInt(KEY_METHOD_COUNT, parameterTypes.length);

        for (int i = 0, count = parameterTypes.length; i < count; i++) {
            Class<?> type = parameterTypes[i];
            Bundle param = new Bundle();
            String typeName = type.getName();
            Logger.getInternalLog().log("[methodToBundle] typeName[" + i + "] " + typeName);
            param.putString(KEY_METHOD_TYPE, typeName);
            putToBundle(param, KEY_METHOD_ARG, type, args[i]);
            result.putBundle(Integer.toString(i), param);
        }
        return result;
    }

    private static void putToBundle(Bundle bundle, String key, Class<?> type, Object obj) {
        if (obj == null) {
            return;
        }
        // 处理基本类型及其包装类型
        if (type == int.class || type == Integer.class) {
            bundle.putInt(key, (Integer) obj);
        } else if (type == byte.class || type == Byte.class) {
            bundle.putByte(key, (Byte) obj);
        } else if (type == char.class || type == Character.class) {
            bundle.putChar(key, (Character) obj);
        } else if (type == long.class || type == Long.class) {
            bundle.putLong(key, (Long) obj);
        } else if (type == float.class || type == Float.class) {
            bundle.putFloat(key, (Float) obj);
        } else if (type == double.class || type == Double.class) {
            bundle.putDouble(key, (Double) obj);
        } else if (type == short.class || type == Short.class) {
            bundle.putShort(key, (Short) obj);
        } else if (type == boolean.class || type == Boolean.class) {
            bundle.putBoolean(key, (Boolean) obj);
        }
        // 处理String类型
        else if (type == String.class) {
            bundle.putString(key, (String) obj);
        }
        // 处理数组类型
        else if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            if (componentType == int.class) {
                bundle.putIntArray(key, (int[]) obj);
            } else if (componentType == byte.class) {
                bundle.putByteArray(key, (byte[]) obj);
            } else if (componentType == char.class) {
                bundle.putCharArray(key, (char[]) obj);
            } else if (componentType == long.class) {
                bundle.putLongArray(key, (long[]) obj);
            } else if (componentType == float.class) {
                bundle.putFloatArray(key, (float[]) obj);
            } else if (componentType == double.class) {
                bundle.putDoubleArray(key, (double[]) obj);
            } else if (componentType == short.class) {
                bundle.putShortArray(key, (short[]) obj);
            } else if (componentType == boolean.class) {
                bundle.putBooleanArray(key, (boolean[]) obj);
            } else if (componentType == String.class) {
                bundle.putStringArray(key, (String[]) obj);
            } else {
                // 处理对象数组（包括Parcelable等）
                Bundle arrayBundle = new Bundle();
                int length = Array.getLength(obj);
                arrayBundle.putInt(KEY_METHOD_COUNT, length);
                for (int i = 0; i < length; i++) {
                    putToBundle(arrayBundle, Integer.toString(i), componentType, Array.get(obj, i));
                }
                bundle.putBundle(key, arrayBundle);
            }
        }
        // 处理Collection类型
        else if (Collection.class.isAssignableFrom(type)) {
            Bundle collectionBundle = new Bundle();
            Collection<?> collection = (Collection<?>) obj;
            int length = collection.size();
            collectionBundle.putInt(KEY_METHOD_COUNT, length);
            int i = 0;
            boolean putType = false;
            for (Object item : collection) {
                String itemKey = Integer.toString(i++);
                if (item != null) {
                    Class<?> itemClass = item.getClass();
                    if (!putType) {
                        collectionBundle.putString(KEY_METHOD_TYPE, itemClass.getName());
                        putType = true;
                    }
                    putToBundle(collectionBundle, itemKey, itemClass, item);
                }
            }
            if (putType) {
                bundle.putBundle(key, collectionBundle);
            }
        }
        // 处理Map类型（转换为Bundle）
        else if (Map.class.isAssignableFrom(type)) {
            Bundle mapBundle = new Bundle();
            boolean putType = false;
            for (Map.Entry<String, ?> entry : ((Map<String, ?>) obj).entrySet()) {
                Object value = entry.getValue();
                if (value != null) {
                    Class<?> itemClass = value.getClass();
                    if (!putType) {
                        mapBundle.putString(KEY_METHOD_TYPE, itemClass.getName());
                        putType = true;
                    }
                    putToBundle(mapBundle, parcelMapKey(entry.getKey()), itemClass, value);
                }
            }
            if (putType) {
                bundle.putBundle(key, mapBundle);
            }
        }
        // 处理其他自定义对象类型
        else {
            bundle.putBundle(key, objectToBundle(obj));
        }
    }

    private static Bundle objectToBundle(Object arg) {
        Logger.getInternalLog().log("[objectToBundle] arg: " + arg);
        if (arg == null) {
            return null;
        }
        Bundle bundle = new Bundle();
        Field[] fields = arg.getClass().getDeclaredFields();
        for (Field field : fields) {
            if ((field.getModifiers() & Modifier.STATIC) != 0) {
                continue;
            }
            if ((field.getModifiers() & Modifier.PUBLIC) == 0) {
                field.setAccessible(true);
            }

            String fieldName = field.getName();
            Class<?> type = field.getType();
            Logger.getInternalLog().log("[objectToBundle] fieldName: " + fieldName + ", type: " + type);
            try {
                putToBundle(bundle, fieldName, type, field.get(arg));
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("[objectToBundle] get field error.", e);
            }

        }
        return bundle;
    }

    static Bundle methodResultToBundle(Object result) {
        Bundle bundle = new Bundle();
        if (result != null) {
            putToBundle(bundle, KEY_METHOD_RESULT, result.getClass(), result);
        }
        return bundle;
    }

    static Object invokeMethod(Object obj, Bundle bundle) {
        String methodName = bundle.getString(KEY_METHOD_NAME);
        if (methodName == null || methodName.isEmpty()) {
            throw new IllegalArgumentException("No method name. ");
        }
        int count = bundle.getInt(KEY_METHOD_COUNT);
        Class<?>[] classes = count == 0 ? null : new Class[count];
        for (int i = 0; i < count; i++) {
            Bundle paramBundle = bundle.getBundle(Integer.toString(i));
            if (paramBundle == null) {
                throw new IllegalArgumentException("Method has no parameter " + i + ".");
            }
            String type = paramBundle.getString(KEY_METHOD_TYPE);
            if (type == null || type.isEmpty()) {
                throw new IllegalArgumentException("Method parameter " + i + " name is null. ");
            }
            try {
                classes[i] = Class.forName(type);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Method parameter " + i + " type error: " + type, e);
            }
        }
        Logger.getInternalLog().log("[invokeMethod] count: " + count + ", classes: " + Arrays.toString(classes));

        Method method;
        try {
            method = obj.getClass().getMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No method error. " + methodName, e);
        }
        Object[] args = count == 0 ? null : new Object[count];
        for (int i = 0; i < count; i++) {
            Bundle paramBundle = bundle.getBundle(Integer.toString(i));
            args[i] = getFromBundle(paramBundle, KEY_METHOD_ARG, classes[i]);
        }
        try {
            Logger.getInterfaceLog().log("[request] method: " + method + ", args: " + Arrays.toString(args));
            Object result = method.invoke(obj, args);
            Logger.getInterfaceLog().log("[response] method: " + method + ", result: " + result);
            return result;
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Invoke method error. ", e);
        }
    }

    private static Object getFromBundle(Bundle bundle, String key, Class<?> type) {
        if (bundle == null || !bundle.containsKey(key)) {
            return null;
        }

        // 处理基本类型及其包装类型
        if (type == int.class || type == Integer.class) {
            return bundle.getInt(key);
        } else if (type == byte.class || type == Byte.class) {
            return bundle.getByte(key);
        } else if (type == char.class || type == Character.class) {
            return bundle.getChar(key);
        } else if (type == long.class || type == Long.class) {
            return bundle.getLong(key);
        } else if (type == float.class || type == Float.class) {
            return bundle.getFloat(key);
        } else if (type == double.class || type == Double.class) {
            return bundle.getDouble(key);
        } else if (type == short.class || type == Short.class) {
            return bundle.getShort(key);
        } else if (type == boolean.class || type == Boolean.class) {
            return bundle.getBoolean(key);
        }
        // 处理String类型
        else if (type == String.class) {
            return bundle.getString(key);
        }
        // 处理数组类型
        else if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            if (componentType == int.class) {
                return bundle.getIntArray(key);
            } else if (componentType == byte.class) {
                return bundle.getByteArray(key);
            } else if (componentType == char.class) {
                return bundle.getCharArray(key);
            } else if (componentType == long.class) {
                return bundle.getLongArray(key);
            } else if (componentType == float.class) {
                return bundle.getFloatArray(key);
            } else if (componentType == double.class) {
                return bundle.getDoubleArray(key);
            } else if (componentType == short.class) {
                return bundle.getShortArray(key);
            } else if (componentType == boolean.class) {
                return bundle.getBooleanArray(key);
            } else if (componentType == String.class) {
                return bundle.getStringArray(key);
            } else {
                // 处理对象数组
                Bundle arrayBundle = bundle.getBundle(key);
                if (arrayBundle != null) {
                    int length = arrayBundle.getInt(KEY_METHOD_COUNT, 0);
                    Object array = Array.newInstance(componentType, length);
                    for (int i = 0; i < length; i++) {
                        Object element = getFromBundle(arrayBundle, Integer.toString(i), componentType);
                        Array.set(array, i, element);
                    }
                    return array;
                }
                return null;
            }
        }
        // 处理Collection类型
        else if (Collection.class.isAssignableFrom(type)) {
            Bundle collectionBundle = bundle.getBundle(key);
            if (collectionBundle != null) {
                int length = collectionBundle.getInt(KEY_METHOD_COUNT, 0);
                Collection collection = Set.class.isAssignableFrom(type) ? new HashSet<>() : new ArrayList<>();
                String itemType = collectionBundle.getString(KEY_METHOD_TYPE);
                if (itemType == null || itemType.isEmpty()) {
                    throw new IllegalArgumentException("Collection item type is null. ");
                }
                try {
                    Class<?> itemClass = Class.forName(itemType);
                    for (int i = 0; i < length; i++) {
                        Object element = getFromBundle(collectionBundle, Integer.toString(i), itemClass);
                        collection.add(element);
                    }
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Collection '" + key + "' item type error. " + itemType, e);
                }
                return collection;
            }
            return null;
        }
        // 处理Map类型
        else if (Map.class.isAssignableFrom(type)) {
            Bundle mapBundle = bundle.getBundle(key);
            if (mapBundle != null) {
                Map map = new HashMap<>();

                String itemType = mapBundle.getString(KEY_METHOD_TYPE);
                if (itemType == null || itemType.isEmpty()) {
                    throw new IllegalArgumentException("Map item type is null. ");
                }
                try {
                    Class<?> itemClass = Class.forName(itemType);
                    for (String mapKey : mapBundle.keySet()) {
                        if (!KEY_METHOD_TYPE.equals(mapKey)) {
                            Object value = getFromBundle(mapBundle, mapKey, itemClass);
                            Logger.getInternalLog().log("map key: " + mapKey + ", value: " + value);
                            map.put(unParcelMapKey(mapKey), value);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Map '" + key + "' item type error. " + itemType, e);
                }
                return map;
            }
            return null;
        }
        // 处理其他自定义对象类型
        else {
            Bundle objBundle = bundle.getBundle(key);
            if (objBundle != null) {
                return bundleToObject(type, objBundle);
            }
            return null;
        }
    }

    private static Object bundleToObject(Class<?> type, Bundle bundle) {
        Logger.getInternalLog().log("[bundleToObject] type: " + type + ", bundle: " + bundle);
        if (bundle == null) {
            return null;
        }
        try {
            Object instance = type.newInstance();
            for (String key : bundle.keySet()) {
                try {
                    Field field = type.getDeclaredField(key);
                    field.setAccessible(true);
                    Object obj = getFromBundle(bundle, key, field.getType());
                    field.set(instance, obj);
                } catch (NoSuchFieldException e) {
                    // do nothing.
                }
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            Logger.getInternalLog().log("[bundleToObject] Error creating instance of " + type);
            throw new RuntimeException("Error creating instance of " + type, e);
        }
    }

    static Object bundleToMethodResult(Class<?> type, Bundle bundle) {
        return getFromBundle(bundle, KEY_METHOD_RESULT, type);
    }

    static String getInterfaceName(Class<?> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        Hamburger hamburger = service.getAnnotation(Hamburger.class);
        if (hamburger == null) {
            throw new IllegalArgumentException("API declarations must be use @Hamburger.");
        }
        return hamburger.name();
    }
}
