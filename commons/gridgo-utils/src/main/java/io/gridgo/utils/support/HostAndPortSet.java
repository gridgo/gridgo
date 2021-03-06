package io.gridgo.utils.support;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import io.gridgo.utils.StringUtils;

public class HostAndPortSet extends HashSet<HostAndPort> {

    private static final long serialVersionUID = 5876970618893492552L;

    public HostAndPortSet() {
        //
    }

    public HostAndPortSet(String multiValueCommaSeparated) {
        this.addAll(HostAndPort.parse(multiValueCommaSeparated));
    }

    public HostAndPortSet(HostAndPort... sources) {
        if (sources != null) {
            for (HostAndPort value : sources) {
                this.add(value);
            }
        }
    }

    public HostAndPort getFirst() {
        return this.iterator().next();
    }

    @Override
    public String toString() {
        return StringUtils.implodeWithGlue(",", this);
    }

    public <T> List<T> convert(Function<HostAndPort, T> processor) {
        List<T> list = new LinkedList<>();
        for (HostAndPort entry : this) {
            list.add(processor.apply(entry));
        }
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        HostAndPortSet other = null;
        if (obj instanceof HostAndPortSet) {
            other = (HostAndPortSet) obj;
        } else if (obj instanceof String) {
            other = new HostAndPortSet((String) obj);
        } else if (obj instanceof HostAndPort) {
            if (this.size() == 1) {
                return this.getFirst().equals(obj);
            }
            return false;
        }

        if (other != null) {
            return this.compareTwoSet(other);
        }

        return false;
    }

    private boolean compareTwoSet(HostAndPortSet other) {
        if (this.size() != other.size()) {
            return false;
        }

        var iterator = this.iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (!other.contains(entry)) {
                return false;
            }
        }

        return true;
    }
}
