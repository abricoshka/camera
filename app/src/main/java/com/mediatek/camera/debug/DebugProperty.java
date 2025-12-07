package com.mediatek.camera.debug;

import android.os.SystemProperties;
import com.mediatek.camera.util.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* loaded from: classes.dex */
public class DebugProperty {
    private static final String TAG = DebugProperty.class.getSimpleName();
    private static int sApiByUserXml = -1;
    private static int sApiByUserCommand = SystemProperties.getInt("mtk.camera.app.api.version", 0);
    private static final int sApiByProject = SystemProperties.getInt("ro.mtk_camera_app_api_version", 0);

    public static boolean isApi2Enable() {
        parseApiFromXml();
        return (sApiByUserXml > 0 || sApiByUserCommand > 0) ? sApiByUserXml == 2 || sApiByUserCommand == 2 : sApiByProject > 0 && sApiByProject == 2;
    }

    private static synchronized void parseApiFromXml() {
        FileInputStream fileInputStream;
        if (-1 == sApiByUserXml) {
            try {
                fileInputStream = new FileInputStream("/data/local/tmp/mtk_camera_app_api_version.xml");
            } catch (FileNotFoundException e) {
                Log.m36w(TAG, "parseApiFromXml with FileNotFoundException.");
                e.printStackTrace();
                fileInputStream = null;
            }
            sApiByUserXml = readApiFromXml(fileInputStream);
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:7:0x001b. Please report as an issue. */
    private static int readApiFromXml(InputStream inputStream) throws XmlPullParserException, IOException {
        int i = 0;
        if (inputStream != null) {
            try {
                XmlPullParser xmlPullParserNewPullParser = XmlPullParserFactory.newInstance().newPullParser();
                xmlPullParserNewPullParser.setInput(inputStream, "UTF-8");
                int eventType = xmlPullParserNewPullParser.getEventType();
                while (true) {
                    int i2 = eventType;
                    int iIntValue = i;
                    i = i2;
                    if (i != 1) {
                        switch (i) {
                            case 2:
                                try {
                                    if ("api".equals(xmlPullParserNewPullParser.getName())) {
                                        iIntValue = Integer.valueOf(xmlPullParserNewPullParser.nextText()).intValue();
                                    }
                                    i = iIntValue;
                                    eventType = xmlPullParserNewPullParser.next();
                                } catch (IOException e) {
                                    i = iIntValue;
                                    e = e;
                                    Log.m36w(TAG, "readApiFromXml with IOException.");
                                    e.printStackTrace();
                                    Log.m34i(TAG, "readApiFromXml api:" + i);
                                    return i;
                                } catch (XmlPullParserException e2) {
                                    i = iIntValue;
                                    e = e2;
                                    Log.m36w(TAG, "readApiFromXml with XmlPullParserException.");
                                    e.printStackTrace();
                                    Log.m34i(TAG, "readApiFromXml api:" + i);
                                    return i;
                                }
                            default:
                                i = iIntValue;
                                eventType = xmlPullParserNewPullParser.next();
                        }
                    } else {
                        i = iIntValue;
                    }
                }
            } catch (IOException e3) {
                e = e3;
            } catch (XmlPullParserException e4) {
                e = e4;
            }
        }
        Log.m34i(TAG, "readApiFromXml api:" + i);
        return i;
    }
}
