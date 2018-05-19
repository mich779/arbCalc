package com.romanobori.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyHandler {


    public static Properties loadProps(String fileName) throws IOException {
        Properties defaultProps = new Properties();

        defaultProps.load(new FileInputStream(fileName));

        return defaultProps;
    }
}
