package com.genius.imfa.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap fileToBitmap(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    public static String fileToBase64(File file) throws IOException {
        Bitmap bitmap = fileToBitmap(file);
        return bitmapToBase64(bitmap);
    }

    public static byte[] fileToByteArray(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        return outputStream.toByteArray();
    }

    public static String byteArrayToBase64(byte[] byteArray) {
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static boolean isImageGreaterThan2MB(Context context, Uri imageUri) {
        try {
            // Open a stream to read the image file
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(imageUri, "r");
            if (parcelFileDescriptor == null) {
                // Failed to open file descriptor
                return false;
            }

            // Get the file descriptor
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            // Get the size of the file
            FileInputStream inputStream = new FileInputStream(fileDescriptor);
            long fileSize = inputStream.getChannel().size(); // Size in bytes

            // Convert bytes to megabytes
            double fileSizeInMB = fileSize / (1024.0 * 1024.0); // Size in MB

            // Compare with 2 MB
            return fileSizeInMB > 2.0;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
