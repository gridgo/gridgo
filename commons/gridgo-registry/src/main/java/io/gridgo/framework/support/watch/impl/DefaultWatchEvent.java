package io.gridgo.framework.support.watch.impl;

import io.gridgo.framework.support.watch.WatchEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DefaultWatchEvent implements WatchEvent {

    private String changedKey;

    private Object newValue;
}
