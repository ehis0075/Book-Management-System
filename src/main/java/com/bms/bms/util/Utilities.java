package com.bms.bms.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class Utilities {
    static Gson gson = new GsonBuilder().create();

    public static <T> T map(Object object, Class<T> clazz) {
        String json = gson.toJson(object);
        return gson.fromJson(json, clazz);
    }

    public static String mapToJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T mapToObject(String object, Class<T> clazz) {
        return gson.fromJson(object, clazz);
    }

    public static String generateRRN() {
        String timeString = Long.toString(System.currentTimeMillis());
        return timeString.substring(0, 12);
    }

    public static String generateFile(List<String> value) {
        String filepath = "myfile.csv";
        try {
            FileWriter file = new FileWriter(filepath);
            BufferedWriter br = new BufferedWriter(file);

            StringBuilder sb = new StringBuilder();

            for (String element : value) {
                sb.append(element);
            }

            br.write(sb.toString());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filepath;
    }

    public static String generateZippedFile(List<String> value, String filepath) {
        try {
            StringBuilder sb = new StringBuilder();

            File f = new File(filepath);
            ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(f.toPath()));
            ZipEntry e = new ZipEntry("colmobile.csv");
            out.putNextEntry(e);

            for (String element : value) {
                sb.append(element);
            }

            byte[] data = sb.toString().getBytes();
            out.write(data, 0, data.length);
            out.closeEntry();

            out.close();
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
        return filepath;
    }

    public static String getSQLDate() {
        return new java.sql.Date(new Date().getTime()).toString();
    }

    public static String dateFullFormat(String inputDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleDateFormat("MMM d yyyy h:mm a").format(date);
    }
}
