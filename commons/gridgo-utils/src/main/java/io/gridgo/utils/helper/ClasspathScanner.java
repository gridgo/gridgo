package io.gridgo.utils.helper;

public interface ClasspathScanner {

    void scan(String packageName, ClassLoader... classLoaders);
}
