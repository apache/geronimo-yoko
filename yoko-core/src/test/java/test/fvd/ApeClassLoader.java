package test.fvd;

import testify.util.Throw;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public final class ApeClassLoader extends URLClassLoader {
    private final Set<String> skipClassNames = new HashSet<>();

    public ApeClassLoader() {
        super(((URLClassLoader)ApeClassLoader.class.getClassLoader()).getURLs(), 
                ApeClassLoader.class.getClassLoader().getParent());
    }

    public ApeClassLoader doNotLoad(Class<?>... types) {
        for (Class<?> type : types)
            skipClassNames.add(type.getName());
        return this;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (skipClassNames.contains(name))
            throw new ClassNotFoundException(name);
        return super.findClass(name);
    }

    /**
     * Call this method to re-invoke the calling method , but on the target class loader.
     * The calling method must be public and static and must NOT be overloaded.
     * The caller should test for the result of this call and exit early if it is <code>true</code>.
     * @return true if this method succeeds in invoking the calling method on an aped class and false if it is already in such an invocation.
     */
    public boolean apeInvoke(Object...args) {
        if (alreadyAped()) {
            System.out.println("already in an invocation of an aped class method " + this.getClass().getClassLoader());
            return false;
        }
        System.out.println("aping invocation from class loader " + this.getClass().getClassLoader());
        invokeApedMethod(args);
        return true;
    }

    private static boolean alreadyAped() {
        String expectedLoader = ApeClassLoader.class.getName();
        String actualLoader = ApeClassLoader.class.getClassLoader().getClass().getName();
        return expectedLoader.equals(actualLoader);
    }

    private void invokeApedMethod(Object...args) {
        final StackTraceElement frame = getCallingStackFrame();
        final String className = frame.getClassName();
        final String methodName = frame.getMethodName();
        final ClassLoader oldTCCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this);
        try {
            Class<?> targetClass = Class.forName(className, true, this);
            final List<Method> methods = Arrays.stream(targetClass.getDeclaredMethods())
                    .filter(m -> Modifier.isStatic(m.getModifiers()))
                    .filter(m -> Modifier.isPublic(m.getModifiers()))
                    .filter(m -> m.getName().equals(methodName))
                    .collect(Collectors.toList());
            assertThat("There should be exactly one public static method matching " + className + "." + methodName, methods, hasSize(1));
            Method m = methods.get(0);
            m.invoke(null, args);
        } catch (ClassNotFoundException e) {
            throw new Error("Failed to load mirrored class " + className, e);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new Error("Failed to invoke method main(String[]) for mirrored class" + className, e);
        } catch (InvocationTargetException e) {
            throw Throw.andThrowAgain(e.getTargetException());
        } finally {
            Thread.currentThread().setContextClassLoader(oldTCCL);
        }
    }

    private static StackTraceElement getCallingStackFrame() {
        StackTraceElement[] frames = new Throwable().getStackTrace();
        int i = 0;
        // find this class in the stack
        while (!!!frames[i].getClassName().equals(ApeClassLoader.class.getName())) i++;
        // find the next class down in the stack
        while (frames[i].getClassName().equals(ApeClassLoader.class.getName())) i++;
        StackTraceElement frame = frames[i];
        return frame;
    }
}