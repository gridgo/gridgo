package io.gridgo.framework.support.watch;

public interface WatchEvent {

    public String getChangedKey();

    public Object getNewValue();
}
