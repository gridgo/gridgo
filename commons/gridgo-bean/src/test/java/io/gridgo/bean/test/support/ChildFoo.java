package io.gridgo.bean.test.support;

import io.gridgo.utils.pojo.IgnoreNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
public class ChildFoo extends Foo {

    @IgnoreNull
    private String ignoreNullField;
}
