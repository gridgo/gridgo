package io.gridgo.utils.pojo;

import java.util.List;

public interface PojoProxy {

    String[] getFields();

    List<PojoMethodSignature> getSignatures();
}
