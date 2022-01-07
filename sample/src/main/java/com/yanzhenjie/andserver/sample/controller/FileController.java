package com.yanzhenjie.andserver.sample.controller;

import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PathVariable;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/file")
public class FileController {
    private static final String TAG = "FileController";
    private static final String ROOT = Environment.getExternalStorageDirectory().toString();

    @GetMapping(path = "/root")
    public List<FileInfo> listRoot() {
        return listDir(ROOT);
    }

    @PostMapping(path = "/list")
    public List<FileInfo> list1(@RequestBody String request) throws Exception {
        PathInfo pathInfo = JSON.parseObject(request, PathInfo.class);
        String path = pathInfo.getPath();
        File testFile = new File(ROOT, path);
        if (!testFile.exists()) {
            throw new FileNotFoundException("访问文件不存在");
        }
        if (!testFile.isDirectory()) {
            throw new IllegalAccessException("不是文件夹");
        }
        return listDir(testFile.getPath());
    }

    @GetMapping(path = "/download/{path}")
    public FileBody download(@PathVariable(name = "path") String path) throws Exception {
        File testFile = new File(ROOT, path);
        if (!testFile.exists()) {
            throw new FileNotFoundException("访问文件不存在");
        }
        if (!testFile.isFile()) {
            throw new IllegalAccessException("不是文件");
        }
        return new FileBody(testFile);
    }


    private List<FileInfo> listDir(String path) {
        File[] root = new File(path).listFiles();
        if (root == null) {
            return Collections.emptyList();
        }
        Arrays.sort(root, (f1, f2) -> (int) (f1.lastModified() - f2.lastModified()));
        List<FileInfo> all = new ArrayList<>();
        List<FileInfo> files = new ArrayList<>();
        for (File file : root) {
            if (file.isDirectory()) {
                all.add(new FileInfo(FileInfo.TYPE_FOLDER, file.getName(), file.getPath().replace(ROOT, ""), file.lastModified()));
            } else {
                files.add(new FileInfo(FileInfo.TYPE_FILE, file.getName(), file.getPath().replace(ROOT, ""), file.lastModified()));
            }
        }
        all.addAll(files);
        return all;
    }


    public static class FileInfo {
        public static final int TYPE_FOLDER = 0;
        public static final int TYPE_FILE = 1;
        private int type;
        private String name;
        private String path;
        private long lastModified;

        public FileInfo() {
        }

        public FileInfo(String name, String path, long lastModified) {
            this.name = name;
            this.path = path;
            this.lastModified = lastModified;
        }

        public FileInfo(int type, String name, String path, long lastModified) {
            this.type = type;
            this.name = name;
            this.path = path;
            this.lastModified = lastModified;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }
    }

    public static class PathInfo {
        private String path;

        public PathInfo() {
        }

        public PathInfo(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
