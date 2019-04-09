package io.gridgo.format;

public interface FormatTransformer {

    /**
     * Transform an object to another object
     * 
     * @param source object to be transformed
     * @return transformed object
     */
    Object transform(Object source);
}
