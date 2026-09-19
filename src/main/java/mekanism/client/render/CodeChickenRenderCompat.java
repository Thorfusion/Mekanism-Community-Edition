package mekanism.client.render;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import codechicken.lib.render.CCRenderState;

/**
 * Bridges the legacy CodeChickenLib static render state and GTNH's
 * per-thread render state without linking Mekanism to either field layout.
 */
final class CodeChickenRenderCompat
{
    private static final Field USE_NORMALS = getField("useNormals");
    private static final Field HAS_COLOUR = getField("hasColour");
    private static final boolean INSTANCE_FIELDS = !Modifier.isStatic(USE_NORMALS.getModifiers());
    private static final Method INSTANCE_METHOD = getInstanceMethod();
    private static final ThreadLocal<Object> RENDER_STATE = new ThreadLocal<Object>()
    {
        @Override
        protected Object initialValue()
        {
            return getRenderState();
        }
    };

    private CodeChickenRenderCompat()
    {
    }

    static void setUseNormals(boolean value)
    {
        setBoolean(USE_NORMALS, value);
    }

    static void setHasColour(boolean value)
    {
        setBoolean(HAS_COLOUR, value);
    }

    private static Field getField(String name)
    {
        try
        {
            return CCRenderState.class.getField(name);
        }
        catch(ReflectiveOperationException e)
        {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Method getInstanceMethod()
    {
        if(!INSTANCE_FIELDS)
        {
            return null;
        }

        try
        {
            return CCRenderState.class.getMethod("instance");
        }
        catch(ReflectiveOperationException e)
        {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Object getRenderState()
    {
        try
        {
            return INSTANCE_METHOD.invoke(null);
        }
        catch(ReflectiveOperationException e)
        {
            throw new IllegalStateException("Unable to obtain the current CodeChicken render state", e);
        }
    }

    private static void setBoolean(Field field, boolean value)
    {
        try
        {
            field.setBoolean(Modifier.isStatic(field.getModifiers()) ? null : RENDER_STATE.get(), value);
        }
        catch(IllegalAccessException e)
        {
            throw new IllegalStateException("Unable to update CodeChicken render state field " + field.getName(), e);
        }
    }
}
