package com.mediatek.camera.addition.remotecamera.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v4.app.FrameMetricsAggregator;
import com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback;

/* loaded from: classes.dex */
public interface IMtkCameraService extends IInterface {
    void cameraServerExit() throws RemoteException;

    void capture() throws RemoteException;

    String getSupportedFeatureList() throws RemoteException;

    void openCamera() throws RemoteException;

    void registerCallback(ICameraClientCallback iCameraClientCallback) throws RemoteException;

    void releaseCamera() throws RemoteException;

    void sendMessage(Message message) throws RemoteException;

    void setFrameRate(int i) throws RemoteException;

    void unregisterCallback(ICameraClientCallback iCameraClientCallback) throws RemoteException;

    public static abstract class Stub extends Binder implements IMtkCameraService {
        public Stub() {
            attachInterface(this, "com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            Message message;
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
                    openCamera();
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
                    releaseCamera();
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
                    capture();
                    parcel2.writeNoException();
                    return true;
                case 4:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
                    if (parcel.readInt() != 0) {
                        message = (Message) Message.CREATOR.createFromParcel(parcel);
                    } else {
                        message = null;
                    }
                    sendMessage(message);
                    parcel2.writeNoException();
                    return true;
                case 5:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
                    registerCallback(ICameraClientCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
                    unregisterCallback(ICameraClientCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                case FrameMetricsAggregator.DELAY_INDEX /* 7 */:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
                    setFrameRate(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
                    cameraServerExit();
                    parcel2.writeNoException();
                    return true;
                case 9:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
                    String supportedFeatureList = getSupportedFeatureList();
                    parcel2.writeNoException();
                    parcel2.writeString(supportedFeatureList);
                    return true;
                case 1598968902:
                    parcel2.writeString("com.mediatek.camera.addition.remotecamera.service.IMtkCameraService");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }
}
