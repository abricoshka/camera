package com.mediatek.camera.setting.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public class PreferenceInflater {
    private AttributeSet mAttrs;
    private Context mContext;
    private SharedPreferencesTransfer mPrefTransfer;
    private static final String PACKAGE_NAME = PreferenceInflater.class.getPackage().getName();
    private static final Class<?>[] CTOR_SIGNATURE = {Context.class, AttributeSet.class, SharedPreferencesTransfer.class};
    private static final HashMap<String, Constructor<?>> sConstructorMap = new HashMap<>();

    public PreferenceInflater(Context context, SharedPreferencesTransfer sharedPreferencesTransfer) {
        this.mContext = context;
        this.mPrefTransfer = sharedPreferencesTransfer;
    }

    public CameraPreference inflate(int i) {
        return inflate(this.mContext.getResources().getXml(i));
    }

    private CameraPreference newPreference(String str, Object[] objArr) {
        String str2 = PACKAGE_NAME + "." + str;
        if (str2.equals(PreferenceGroup.class.getName())) {
            return new PreferenceGroup(this.mContext, this.mAttrs, this.mPrefTransfer);
        }
        if (str2.equals(IconListPreference.class.getName())) {
            return new IconListPreference(this.mContext, this.mAttrs, this.mPrefTransfer);
        }
        if (str2.equals(ListPreference.class.getName())) {
            return new ListPreference(this.mContext, this.mAttrs, this.mPrefTransfer);
        }
        if (str2.equals(RecordLocationPreference.class.getName())) {
            return new RecordLocationPreference(this.mContext, this.mAttrs, this.mPrefTransfer);
        }
        return null;
    }

    private CameraPreference inflate(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlPullParser);
        this.mAttrs = attributeSetAsAttributeSet;
        ArrayList arrayList = new ArrayList();
        Object[] objArr = {this.mContext, attributeSetAsAttributeSet, this.mPrefTransfer};
        try {
            int next = xmlPullParser.next();
            while (next != 1) {
                if (next == 2) {
                    CameraPreference cameraPreferenceNewPreference = newPreference(xmlPullParser.getName(), objArr);
                    int depth = xmlPullParser.getDepth();
                    if (depth > arrayList.size()) {
                        arrayList.add(cameraPreferenceNewPreference);
                    } else {
                        arrayList.set(depth - 1, cameraPreferenceNewPreference);
                    }
                    if (depth > 1) {
                        ((PreferenceGroup) arrayList.get(depth - 2)).addChild(cameraPreferenceNewPreference);
                    }
                }
                next = xmlPullParser.next();
            }
            if (arrayList.size() == 0) {
                throw new InflateException("No root element found");
            }
            return (CameraPreference) arrayList.get(0);
        } catch (IOException e) {
            throw new InflateException(xmlPullParser.getPositionDescription(), e);
        } catch (XmlPullParserException e2) {
            throw new InflateException(e2);
        }
    }
}
