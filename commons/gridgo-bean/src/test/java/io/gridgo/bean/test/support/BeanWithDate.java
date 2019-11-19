package io.gridgo.bean.test.support;

import java.sql.Date;

import com.dslplatform.json.CompiledJson;

import lombok.Data;

@Data
@CompiledJson
public class BeanWithDate {

    private Date date;
}
