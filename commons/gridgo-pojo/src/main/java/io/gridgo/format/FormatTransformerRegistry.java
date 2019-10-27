package io.gridgo.format;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface FormatTransformerRegistry {

    static FormatTransformerRegistry newInstance(FormatTransformerRegistry... registrys) {
        var result = new DefaultFormatTransformerRegistry();
        if (registrys != null) {
            for (var registry : registrys) {
                result.inherit(registry);
            }
        }
        return result;
    }

    /**
     * Create new FormatTransformerRegistry, inherited from
     * CommonTextTransformerRegistry, CommonNumberTransformerRegistry,
     * CommonDateTransformerRegistry
     * 
     * @return
     */
    static FormatTransformerRegistry newwDefault() {
        return newInstance(//
                CommonTextTransformerRegistry.newInstance(), //
                CommonNumberTransformerRegistry.newInstance(), //
                CommonDateTransformerRegistry.newInstance());
    }

    default List<FormatTransformer> getChain(String... transformerNames) {
        return this.getChain(Arrays.asList(transformerNames));
    }

    Map<String, FormatTransformer> getAll();

    List<FormatTransformer> getChain(List<String> transformerNames);

    FormatTransformer addTransformer(String name, FormatTransformer transformer);

    default FormatTransformer addAlias(String name, String... chain) {
        if (name == null)
            throw new NullPointerException("Name cannot be null");
        if (chain == null || chain.length == 0)
            return null;
        var list = Arrays.stream(chain) //
                         .filter(Objects::nonNull) //
                         .map(str -> str.trim().split("\\s*>\\s*")) //
                         .flatMap(Arrays::stream) //
                         .filter(n -> !n.isBlank()) //
                         .collect(Collectors.toList());
        var combinedFormatTransformer = new CombinedFormatTransfromer();
        combinedFormatTransformer.getChain().addAll(this.getChain(list));
        return this.addTransformer(name, combinedFormatTransformer);
    }

    FormatTransformer removeTransformer(String name);

    default FormatTransformerRegistry inherit(FormatTransformerRegistry parent) {
        if (parent != null) {
            for (var entry : parent.getAll().entrySet()) {
                this.addTransformer(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }
}
