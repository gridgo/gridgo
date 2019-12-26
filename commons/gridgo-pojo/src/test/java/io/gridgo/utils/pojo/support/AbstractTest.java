package io.gridgo.utils.pojo.support;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class AbstractTest {

    // The custom CL
    private URLClassLoader cl;
    // The previous context CL
    private ClassLoader old;

//    @Before
    public void init() throws Exception {
        System.out.println("init...");
        // Provide the URL corresponding to the folder that contains the class
        // `javassist.MyClass`
        this.cl = new URLClassLoader(new URL[] { new File("target/classes").toURI().toURL(),
                new File("target/test-classes").toURI().toURL() }) {

            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                try {
                    // Try to find the class for this CL
                    return findClass(name);
                } catch (ClassNotFoundException e) {
                    // Could not find the class so load it from the parent
                    return super.loadClass(name, resolve);
                }
            }
        };
        // Get the current context CL and store it into old
        this.old = Thread.currentThread().getContextClassLoader();
        // Set the custom CL as new context CL
        Thread.currentThread().setContextClassLoader(cl);
    }

//    @After
    public void restore() throws Exception {
        // Restore the context CL
        Thread.currentThread().setContextClassLoader(old);
        // Close the custom CL
        cl.close();
    }
}
