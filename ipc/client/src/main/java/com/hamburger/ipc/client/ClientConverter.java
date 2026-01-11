package com.hamburger.ipc.client;

import android.os.Bundle;

import java.lang.reflect.Method;

public interface ClientConverter {
    Bundle parameters(Method method, Object[] parameters);

    Object result(Method method, Bundle obj);
}
