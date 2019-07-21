package io.gridgo.connector;

import java.util.Optional;

/**
 * Represents a datasource provider
 *
 * @param <T> the type of the datasource
 */
public interface DataSourceProvider<T> {

    public Optional<T> getDataSource();
}
