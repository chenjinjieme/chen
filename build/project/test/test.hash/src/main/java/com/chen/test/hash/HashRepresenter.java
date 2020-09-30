package com.chen.test.hash;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class HashRepresenter extends Representer {
    public Node represent(Object data) {
        if (data instanceof Directory) return new MappingNode(Tag.MAP, ((Directory) data).entrySet().stream().sorted(Map.Entry.<String, Path>comparingByValue(Comparator.comparingInt(path -> path instanceof Directory ? -1 : 1)).thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))).map(entry -> new NodeTuple(represent(entry.getKey()), represent(entry.getValue()))).collect(Collectors.toCollection(() -> new ArrayList<>(((Directory) data).size()))), DumperOptions.FlowStyle.BLOCK);
        else if (data instanceof File) return new ScalarNode(Tag.STR, ((File) data).hash(), null, null, DumperOptions.ScalarStyle.PLAIN);
        else return new ScalarNode(Tag.STR, data.toString(), null, null, DumperOptions.ScalarStyle.PLAIN);
    }
}
