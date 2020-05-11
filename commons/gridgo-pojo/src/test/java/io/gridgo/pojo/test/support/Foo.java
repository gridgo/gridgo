package io.gridgo.pojo.test.support;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Foo<E> extends Box<String, E> implements GenericInterface<E> {

    private List<E> theList;
}
