package com.coremedia.iso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class PropertyBoxParserImpl extends AbstractBoxParser {
    Properties mapping;

    /* renamed from: p */
    Pattern f66p = Pattern.compile("(.*)\\((.*?)\\)");

    public PropertyBoxParserImpl(String... strArr) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().getResourceAsStream("/isoparser-default.properties"));
        try {
            this.mapping = new Properties();
            try {
                this.mapping.load(bufferedInputStream);
                Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("isoparser-custom.properties");
                while (resources.hasMoreElements()) {
                    bufferedInputStream = new BufferedInputStream(resources.nextElement().openStream());
                    try {
                        this.mapping.load(bufferedInputStream);
                    } finally {
                        bufferedInputStream.close();
                    }
                }
                for (String str : strArr) {
                    this.mapping.load(new BufferedInputStream(getClass().getResourceAsStream(str)));
                }
                try {
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        } catch (Throwable th) {
            try {
            } catch (IOException e3) {
                e3.printStackTrace();
            }
            throw th;
        }
    }
}
