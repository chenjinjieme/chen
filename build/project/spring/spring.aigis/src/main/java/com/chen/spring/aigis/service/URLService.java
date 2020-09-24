package com.chen.spring.aigis.service;

import com.chen.spring.aigis.dao.PNGDao;
import com.chen.spring.aigis.dao.URLDao;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

@Service
public class URLService {
    private final PNGDao pngDao;
    private final URLDao urlDao;

    public URLService(PNGDao pngDao, URLDao urlDao) {
        this.pngDao = pngDao;
        this.urlDao = urlDao;
    }

    public ResponseEntity<?> add(MultipartFile file) throws IOException {
        var matcher = Pattern.compile("\\w+\\.\\w+=\\w{40}/\\w{32}").matcher(new String(file.getBytes()));
        var map = new TreeMap<String, Map<String, String>>();
        for (; matcher.find(); ) {
            var split = matcher.group().split("=");
            var url = map.computeIfAbsent(split[0].substring(split[0].lastIndexOf(".") + 1), k -> new TreeMap<>());
            url.put(split[0], split[1]);
        }
        var options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        try (var writer = new FileWriter(new ClassPathResource("aigis.yml").getFile())) {
            new Yaml(options).dump(map, writer);
        }
        return ResponseEntity.ok("");
    }

    @Transactional
    public ResponseEntity<?> update() throws IOException {
        var all = new HashMap<String, Object>();
        urlDao.getAll().forEach(url -> all.put(url.get("name").toString(), url.get("url")));
        try (var reader = new FileReader(new ClassPathResource("aigis.yml").getFile())) {
            Map<String, Map<String, String>> load = new Yaml().load(reader);
            load.forEach((extension, urls) -> urls.forEach((name, url) -> {
                if (!all.containsKey(name)) {
                    urlDao.add(Map.of("name", name, "url", url));
                    if (extension.equals("png")) if (name.indexOf("card") == 4) pngDao.add(Map.of("character", name.substring(0, 3), "type", 0, "index", name.charAt(9)));
                    else if (name.indexOf("card") == 5) pngDao.add(Map.of("character", name.substring(0, 4), "type", 0, "index", name.charAt(10)));
                    else if (name.indexOf("Allev") == 0) pngDao.add(Map.of("character", name.substring(11, 15), "type", 1, "index", name.charAt(16)));
                    else if (name.indexOf("HarlemCG") == 0) if (name.charAt(12) == '_') pngDao.add(Map.of("character", name.substring(9, 12), "type", 2, "index", name.charAt(13)));
                    else pngDao.add(Map.of("character", name.substring(9, 13), "type", 2, "index", name.charAt(14)));
                    else if (name.indexOf("R18ev") == 0) pngDao.add(Map.of("character", name.substring(11, 15), "type", 3, "index", name.charAt(16)));
                    else if (name.indexOf("Promotion") == 0) pngDao.add(Map.of("character", name.substring(9, 13), "type", 4, "index", name.charAt(19)));
                } else if (!all.get(name).equals(url)) urlDao.update(Map.of("name", name, "url", url));
            }));
        }
        return ResponseEntity.ok("");
    }
}
