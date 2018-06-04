package com.bfei.icrane.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author mwan
 *         Version: 1.0
 *         Date: 2017/10/08
 *         Description: Methods used to manage property file.
 *         Copyright (c) 2017 伴飞网络. All rights reserved.
 */
public class PropFileManager {
    private InputStream _fis;
    private Properties _properties;

    //Construct method
    public PropFileManager(String fileName) {
        try {
            _properties = new Properties();
            _fis = this.getClass().getClassLoader().getResourceAsStream(fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(_fis, "UTF-8"));
            _properties.load(bf);
            bf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Get property value
    public String getProperty(String propertyName) {
        return _properties.getProperty(propertyName);
    }
}
