package io.gridgo.connector;

/**
 * Represents a datasource provider
 *
 * @param <T> the type of the datasource
 */
public interface DataSourceProvider<T> {

    public T getDataSource();
}
