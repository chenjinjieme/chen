package com.chen.test.hash;

import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class HashConstructor extends BaseConstructor {
    protected Object constructObject(Node node) {
        return construct(new Directory(""), node);
    }

    private Directory construct(Directory directory, Node node) {
        ((MappingNode) node).getValue().forEach(entry -> {
            var key = ((ScalarNode) entry.getKeyNode()).getValue();
            var value = entry.getValueNode();
            if (value instanceof ScalarNode) directory.put(key, new File(key, ((ScalarNode) value).getValue()));
            else directory.put(key, construct(new Directory(key), value));
        });
        return directory;
    }
}
