package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.UserBox;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes3.dex */
public class PropertyBoxParserImpl extends AbstractBoxParser {
    static String[] EMPTY_STRING_ARRAY = new String[0];
    String clazzName;
    Properties mapping;
    String[] param;
    Pattern constuctorPattern = Pattern.compile("(.*)\\((.*?)\\)");
    StringBuilder buildLookupStrings = new StringBuilder();

    public PropertyBoxParserImpl(String... customProperties) {
        InputStream customIS = getClass().getResourceAsStream("/isoparser-default.properties");
        try {
            Properties properties = new Properties();
            this.mapping = properties;
            try {
                properties.load(customIS);
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                Enumeration<URL> enumeration = (cl == null ? ClassLoader.getSystemClassLoader() : cl).getResources("isoparser-custom.properties");
                while (enumeration.hasMoreElements()) {
                    URL url = enumeration.nextElement();
                    customIS = url.openStream();
                    try {
                        this.mapping.load(customIS);
                        customIS.close();
                    } finally {
                        customIS.close();
                    }
                }
                for (String customProperty : customProperties) {
                    this.mapping.load(getClass().getResourceAsStream(customProperty));
                }
                try {
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        } catch (Throwable e3) {
            try {
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            throw e3;
        }
    }

    public PropertyBoxParserImpl(Properties mapping) {
        this.mapping = mapping;
    }

    @Override // com.coremedia.iso.AbstractBoxParser
    public Box createBox(String type, byte[] userType, String parent) {
        invoke(type, userType, parent);
        try {
            Class<?> cls = Class.forName(this.clazzName);
            String[] strArr = this.param;
            if (strArr.length > 0) {
                Class[] constructorArgsClazz = new Class[strArr.length];
                Object[] constructorArgs = new Object[strArr.length];
                int i = 0;
                while (true) {
                    String[] strArr2 = this.param;
                    if (i < strArr2.length) {
                        if ("userType".equals(strArr2[i])) {
                            constructorArgs[i] = userType;
                            constructorArgsClazz[i] = byte[].class;
                        } else if (CommonProperties.TYPE.equals(this.param[i])) {
                            constructorArgs[i] = type;
                            constructorArgsClazz[i] = String.class;
                        } else if ("parent".equals(this.param[i])) {
                            constructorArgs[i] = parent;
                            constructorArgsClazz[i] = String.class;
                        } else {
                            throw new InternalError("No such param: " + this.param[i]);
                        }
                        i++;
                    } else {
                        return (Box) cls.getConstructor(constructorArgsClazz).newInstance(constructorArgs);
                    }
                }
            } else {
                return (Box) cls.newInstance();
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        } catch (InstantiationException e3) {
            throw new RuntimeException(e3);
        } catch (NoSuchMethodException e4) {
            throw new RuntimeException(e4);
        } catch (InvocationTargetException e5) {
            throw new RuntimeException(e5);
        }
    }

    public void invoke(String type, byte[] userType, String parent) {
        String constructor;
        String[] strArr;
        if (userType != null) {
            if (!UserBox.TYPE.equals(type)) {
                throw new RuntimeException("we have a userType but no uuid box type. Something's wrong");
            }
            Properties properties = this.mapping;
            constructor = properties.getProperty("uuid[" + Hex.encodeHex(userType).toUpperCase() + "]");
            if (constructor == null) {
                Properties properties2 = this.mapping;
                constructor = properties2.getProperty(String.valueOf(parent) + "-uuid[" + Hex.encodeHex(userType).toUpperCase() + "]");
            }
            if (constructor == null) {
                constructor = this.mapping.getProperty(UserBox.TYPE);
            }
        } else {
            constructor = this.mapping.getProperty(type);
            if (constructor == null) {
                StringBuilder sb = this.buildLookupStrings;
                sb.append(parent);
                sb.append('-');
                sb.append(type);
                String lookup = sb.toString();
                this.buildLookupStrings.setLength(0);
                constructor = this.mapping.getProperty(lookup);
            }
        }
        if (constructor == null) {
            constructor = this.mapping.getProperty("default");
        }
        if (constructor == null) {
            throw new RuntimeException("No box object found for " + type);
        } else if (!constructor.endsWith(")")) {
            this.param = EMPTY_STRING_ARRAY;
            this.clazzName = constructor;
        } else {
            Matcher m = this.constuctorPattern.matcher(constructor);
            boolean matches = m.matches();
            if (!matches) {
                throw new RuntimeException("Cannot work with that constructor: " + constructor);
            }
            this.clazzName = m.group(1);
            if (m.group(2).length() == 0) {
                this.param = EMPTY_STRING_ARRAY;
                return;
            }
            if (m.group(2).length() <= 0) {
                strArr = new String[0];
            } else {
                strArr = m.group(2).split(",");
            }
            this.param = strArr;
        }
    }
}
