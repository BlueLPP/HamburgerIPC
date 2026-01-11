package com.hamburger.ipc.bundle;

import android.os.Bundle;

import com.hamburger.ipc.server.ServerConverter;

import java.lang.reflect.Method;

public class BundleServerConverter implements ServerConverter {
    @Override
    public Object[] parameters(Method method, Bundle parameters) {
        int count = method.getParameterCount();
        Class<?>[] classes = method.getParameterTypes();
        Object[] arg = count == 0 ? null : new Object[count];
        for (int i = 0; i < count; i++) {
            arg[i] = BundleUtils.getFromBundle(parameters, Integer.toString(i), classes[i]);
        }
        return arg;
    }

    @Override
    public Bundle result(Method method, Object obj) {
        Bundle bundle = new Bundle();
        if (obj != null) {
            BundleUtils.putToBundle(bundle, BundleUtils.KEY_METHOD_RESULT, obj.getClass(), obj);
        }
        return bundle;
    }
}
