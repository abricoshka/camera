package com.mediatek.camera.addition.remotecamera.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface ICameraClientCallback extends IInterface {
    void cameraServerApExit() throws RemoteException;

    void onPictureTaken(byte[] bArr) throws RemoteException;

    void onPreviewFrame(byte[] bArr) throws RemoteException;

    public static abstract class Stub extends Binder implements ICameraClientCallback {
        public Stub() {
            attachInterface(this, "com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback");
        }

        public static ICameraClientCallback asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface("com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback");
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof ICameraClientCallback)) {
                return (ICameraClientCallback) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback");
                    onPreviewFrame(parcel.createByteArray());
                    parcel2.writeNoException();
                    return true;
                case 2:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback");
                    onPictureTaken(parcel.createByteArray());
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface("com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback");
                    cameraServerApExit();
                    parcel2.writeNoException();
                    return true;
                case 1598968902:
                    parcel2.writeString("com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements ICameraClientCallback {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback
            public void onPreviewFrame(byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback");
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback
            public void onPictureTaken(byte[] bArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback");
                    parcelObtain.writeByteArray(bArr);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback
            public void cameraServerApExit() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken("com.mediatek.camera.addition.remotecamera.service.ICameraClientCallback");
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
