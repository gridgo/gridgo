package io.gridgo.bean.test.support;

import java.util.List;

import io.gridgo.utils.pojo.IgnoreNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@IgnoreNull
@NoArgsConstructor
public class ChildFoo extends Foo {

    private String ignoredString;

    private List<Object> ignoredList;
}
