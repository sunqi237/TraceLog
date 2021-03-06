package com.lib.utils.print;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ethanco.tracelog.TraceLog;
import com.ethanco.tracelog.logs.DiskLogTrace;
import com.ethanco.tracelog.logs.IndexLog;
import com.ethanco.tracelog.utils.TranceFormat;


/**
 * Log
 */
public class L {
    public static boolean isDebug = true; //是否是调试状态
    private static int minLevel = 0x1;


    public static final int V = 0x1;
    public static final int D = 0x2;
    public static final int I = 0x3;
    public static final int W = 0x4;
    public static final int E = 0x5;
    public static final int A = 0x6;

    private static TraceLog traceLog;
    private static boolean autoPrintStatckInfo = false;

    /**
     * 是否自动打印堆栈信息 (Verbose级别)
     *
     * @param autoPrintStatckInfo
     */
    public static void setAutoPrintStatckInfo(boolean autoPrintStatckInfo) {
        L.autoPrintStatckInfo = autoPrintStatckInfo;
    }

    /**
     * 初始化
     *
     * @param enable 是否打印日志
     */
    public static void init(Context context, boolean enable) {
        init(context, enable, false);
    }

    /**
     * 初始化
     *
     * @param enable 是否打印日志
     */
    public static void init(Context context, boolean enable, boolean saveToDisk) {
        isDebug = enable;
        TraceLog.Builder builder = new TraceLog.Builder()
                .addTrace(new IndexLog());
        if (saveToDisk) {
            builder.addTrace(new DiskLogTrace(context, 1024 * 1024 * 100,
                    "Log", "Log", false));
        }
        traceLog = builder.build();
    }

    /**
     * 初始化
     *
     * @param enable
     * @param _traceLog
     */
    public static void init(boolean enable, TraceLog _traceLog) {
        isDebug = enable;
        traceLog = _traceLog;
    }

    /**
     * 允许打印的最低级别
     *
     * @param _level
     */
    public static void setMinLevel(int _level) {
        minLevel = _level;
    }

    private static void error(String tag, String msg, Throwable tr) {
        if (checkLog(E, msg)) return;
        traceLog.t(tag).e(tr, msg);
    }

    private static void print(int type, String TAG, String message) {
        if (checkLog(type, message)) return;

        switch (type) {
            case V:
                traceLog.t(TAG).v(message);
                break;
            case D:
                traceLog.t(TAG).d(message);
                break;
            case I:
                traceLog.t(TAG).i(message);
                break;
            case W:
                traceLog.t(TAG).w(message);
                break;
            case E:
                traceLog.t(TAG).e(message);
                break;
            case A:
                traceLog.t(TAG).wtf(message);
                break;
            default:
                throw new IllegalArgumentException("not support is type:" + type);
        }
        if (autoPrintStatckInfo && type > V) {
            traceLog.t(TAG).v(new StringBuilder(message).append(getStackInfo()));
        }
    }

    private static boolean checkLog(int type, String msg) {
        if (!isDebug) {
            return true;
        }
        if (type < minLevel) {
            return true;
        }
        if (TextUtils.isEmpty(msg)) {
            return true;
        }
        return false;
    }

    public static void e(String TAG, String msg) {
        print(E, TAG, msg);
    }

    public static void e(String TAG, String msg, Throwable tr) {
        error(TAG, msg, tr);
    }

    public static void w(String TAG, String msg) {
        print(W, TAG, msg);
    }

    public static void i(String TAG, String msg) {
        print(I, TAG, msg);
    }

    public static void d(String TAG, String msg) {
        print(D, TAG, msg);
    }

    public static void v(String TAG, String msg) {
        print(V, TAG, msg);
    }

    public static String getStackInfo() {
        return TranceFormat.getStackInfo("");
    }

    public static String getStackInfo(String prefix) {
        return TranceFormat.getStackInfo(prefix);
    }

    public static String getStackInfo(Throwable tr) {
        return Log.getStackTraceString(tr);
    }
}