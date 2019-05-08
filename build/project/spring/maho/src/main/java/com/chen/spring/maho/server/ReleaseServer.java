package com.chen.spring.maho.server;

import com.chen.spring.maho.dao.ReleaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Service
public class ReleaseServer {
    @Autowired
    private ReleaseDao releaseDao;

    public void add(Path path) throws IOException {
        try (var stream = Files.list(path)) {
            for (var iterator = stream.iterator(); iterator.hasNext(); ) if (releaseDao.add(Map.of("name", iterator.next().getFileName().toString())) == 0) throw new RuntimeException();
        }
    }
}
