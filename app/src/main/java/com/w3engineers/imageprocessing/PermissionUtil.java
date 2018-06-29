package com.w3engineers.imageprocessing;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/*
 * ****************************************************************************
 * * Copyright © 2018 W3 Engineers Ltd., All rights reserved.
 * *
 * * Created by:
 * * Name : Aziz
 * * Date : 16/10/17
 * * Email : aziz@w3engineers.com
 * *
 * * Purpose : App permission util. Please don't change without discussion with CTO
 * *
 * * Last Edited by : SUDIPTA KUMAR PAIK on 08/03/18.
 * * History:
 * * 1:
 * * 2:
 * *
 * * Last Reviewed by : SUDIPTA KUMAR PAIK on 08/03/18.
 * ****************************************************************************
 */

public class PermissionUtil {
    public static final int REQUEST_CODE_PERMISSION_DEFAULT = 1;

    private static Context sContext;
    private static PermissionUtil invokePermission;
    public static final int PERMISSIONS_REQUEST = 1;

    private PermissionUtil() {

    }

    public static PermissionUtil init(Context context) {
        if (invokePermission == null) {
            invokePermission = new PermissionUtil();
        }
        sContext = context;
        return invokePermission;
    }

    public static synchronized PermissionUtil on() {
        return invokePermission;
    }

    public boolean request(String... str) {

        if (sContext == null) return false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        List<String> finalArgs = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            if (sContext.checkSelfPermission(str[i]) != PackageManager.PERMISSION_GRANTED) {
                finalArgs.add(str[i]);
            }
        }

        if (finalArgs.isEmpty()) {
            return true;
        }

        ((Activity) sContext).requestPermissions(finalArgs.toArray(new String[finalArgs.size()]), REQUEST_CODE_PERMISSION_DEFAULT);

        return false;
    }

    public boolean request(int requestCode, String... str) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        List<String> finalArgs = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            if (sContext.checkSelfPermission(str[i]) != PackageManager.PERMISSION_GRANTED) {
                finalArgs.add(str[i]);
            }
        }

        if (finalArgs.isEmpty()) {
            return true;
        }

        ((Activity) sContext).requestPermissions(finalArgs.toArray(new String[finalArgs.size()]), requestCode);

        return false;
    }

    public boolean isAllowed(String str) {
        if (sContext == null) return false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (sContext.checkSelfPermission(str) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;
    }

    public boolean requestPermission(Context context, String... str) {

        if(context == null) return false;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        List<String> finalArgs = new ArrayList<>();
        for (int i = 0; i < str.length; i++) {
            if (context.checkSelfPermission(str[i]) != PackageManager.PERMISSION_GRANTED) {
                finalArgs.add(str[i]);
            }
        }

        if (finalArgs.isEmpty()) {
            return true;
        }

        ((Activity) context).requestPermissions(finalArgs.toArray(new String[finalArgs.size()]), PERMISSIONS_REQUEST);

        return false;
    }

}
