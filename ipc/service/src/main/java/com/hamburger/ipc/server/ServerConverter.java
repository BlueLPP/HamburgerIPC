package com.hamburger.ipc.server;

import android.os.Bundle;

import java.lang.reflect.Method;

public interface ServerConverter {
    Object[] parameters(Method method, Bundle parameters);

    Bundle result(Method method, Object obj);
}
