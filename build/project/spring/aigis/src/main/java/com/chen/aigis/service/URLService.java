package com.chen.aigis.service;

import com.chen.aigis.dao.PngDao;
import com.chen.aigis.dao.URLDao;
import com.chen.util.data.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.chen.util.data.ControllerUtil.*;

@Service
public class URLService {
    @Autowired
    private URLDao urlDao;
    @Autowired
    private PngDao pngDao;

    private enum Yml {
        INSTANCE;
        private final Yaml yaml;

        Yml() {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            yaml = new Yaml(options);
        }
    }

    public Map<String, Object> add(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream())); FileWriter writer = new FileWriter("/Users/admin/IdeaProjects/aigis/aigis/src/main/resources/aigis.yml")) {
            StringBuilder stringBuilder = new StringBuilder();
            int readSize;
            for (char[] chars = new char[1024]; (readSize = reader.read(chars)) != -1; )
                stringBuilder.append(chars, 0, readSize);
            Matcher matcher = Pattern.compile("\\w+\\.\\w+=\\w{40}/\\w{32}").matcher(stringBuilder.toString());
            Map<String, Map<String, String>> map = new TreeMap<>();
            for (String[] path; matcher.find(); ) {
                path = matcher.group().split("[.=]");
                Map<String, String> url = map.computeIfAbsent(path[1], k -> new TreeMap<>());
                url.put(path[0] + '.' + path[1], path[2]);
            }
            Yml.INSTANCE.yaml.dump(map, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return success();
    }

    @Transactional
    public Map<String, Object> update() {
        Map<String, Object> all = new HashMap<>();
        Parameter data = new Parameter();
        urlDao.getAll().forEach(url -> all.put(url.get("name").toString(), url.get("url")));
        try (FileReader reader = new FileReader("/Users/admin/IdeaProjects/aigis/aigis/src/main/resources/aigis.yml")) {
            Map<String, Map<String, String>> load = Yml.INSTANCE.yaml.load(reader);
            List<Map<String, Object>> add = new ArrayList<>();
            List<Map<String, Object>> png = new ArrayList<>();
            List<Map<String, Object>> update = new ArrayList<>();
            load.forEach((extension, urls) -> urls.forEach((name, url) -> {
                Parameter parameter;
                if (!all.containsKey(name)) {
                    execute(urlDao.add(parameter = new Parameter("name", name).add("url", url)));
                    add.add(parameter);
                    if (extension.equals("png")) {
                        if (name.indexOf("card") == 4) {
                            execute(pngDao.add(parameter = new Parameter("character", name.substring(0, 3)).add("type", 0).add("index", name.charAt(9))));
                            png.add(parameter);
                        } else if (name.indexOf("Allev") == 0) {
                            execute(pngDao.add(parameter = new Parameter("character", name.substring(12, 15)).add("type", 1).add("index", name.charAt(16))));
                            png.add(parameter);
                        } else if (name.indexOf("HarlemCG") == 0) {
                            execute(pngDao.add(parameter = new Parameter("character", name.substring(9, 12)).add("type", 2).add("index", name.charAt(13))));
                            png.add(parameter);
                        } else if (name.indexOf("R18ev") == 0) {
                            execute(pngDao.add(parameter = new Parameter("character", name.substring(12, 15)).add("type", 3).add("index", name.charAt(16))));
                            png.add(parameter);
                        } else if (name.indexOf("Promotion") == 0) {
                            execute(pngDao.add(parameter = new Parameter("character", name.substring(10, 13)).add("type", 4).add("index", name.charAt(19))));
                            png.add(parameter);
                        }
                    }
                } else if (!all.get(name).equals(url)) {
                    execute(urlDao.update(parameter = new Parameter("name", name).add("url", url)));
                    update.add(parameter);
                }
            }));
            data.add("update", update).add("png", png).add("add", add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return putData(data);
    }
}
