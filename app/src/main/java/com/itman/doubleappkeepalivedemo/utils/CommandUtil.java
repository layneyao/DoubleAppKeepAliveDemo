package com.itman.doubleappkeepalivedemo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.itman.doubleappkeepalivedemo.APP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class CommandUtil {


    /**
     * 普通安装
     *
     * @param context
     * @param apkPath
     */
    public static void normalInstall(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            File file = (new File(apkPath));
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.mangechargespot.app", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }


    // 判断是否有root权限
    public static boolean hasRootPerssion() {
        PrintWriter PrintWriter = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }


    /**
     * 执行命令但不关注结果输出
     */
    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 静默安装
     *
     * @param type    apk类型 0:C 1:B
     * @param apkPath
     * @return
     */
    public static boolean clientInstall(int type, String apkPath) {
        Log.e("csTest", "APP -> INSTALL 安装 " + (type == 0 ? "C" : "B") + "-> " + apkPath);
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
                Log.e("csTest", "APP -> INSTALL 安装" + (type == 0 ? "C" : "B") + "成功");
                if (type == 0) {
                    //启动
                    boolean isAppInstalled = LauncherUtils.isAppInstalled();
                    if (isAppInstalled) {
                        Log.e("csTest", "APP -> START ****** 检测到C APP已安装，立即启动C ******");
                        LauncherUtils.startCSActivity(APP.getInstance());
                    }
                }
            } else {
                if (msg.contains("INSTALL_FAILED_VERSION_DOWNGRADE") && type == 0) {
                    Log.e("csTest", "APP -> INSTALL  安装" + (type == 0 ? "C" : "B") + "版本低于已安装版本，开始卸载重装:" + msg);
                    if (slientUninstall(type)) {
                        clientInstall(type, apkPath);
                    }
                } else {
                    Log.e("csTest", "APP -> INSTALL 安装" + (type == 0 ? "C" : "B") + "失败:" + msg);
                }
            }
        } catch (Exception e) {
            Log.e("csTest", "APP -> INSTALL 安装" + (type == 0 ? "C" : "B") + "失败:" + e.getMessage());
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
            }
        }
        return result;
    }


    private static void shell(String s) throws IOException {
        Process proc = Runtime.getRuntime().exec("su");
        proc.getOutputStream().write(s.getBytes());
        proc.getOutputStream().flush();
        proc.getOutputStream().close();
    }


    /**
     * @param type apk类型 0:C 1:B
     *             静默卸载
     */

    public static boolean slientUninstall(int type) {
        boolean flag = false;
        String cmd = "pm uninstall " + LauncherUtils.homePackName;
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg;
        StringBuilder errorMsg;
        try {
            //卸载也需要root权限
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            //执行命令
            process.waitFor();
            //获取返回结果
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
            if (successMsg != null && successMsg.toString().contains("Success")) {
                Log.e("csTest", "APP -> UNINSTALL 卸载" + (type == 0 ? "C" : "B") + "成功");
                flag = true;
            } else {
                Log.e("csTest", "APP -> UNINSTALL 卸载" + (type == 0 ? "C" : "B") + "失败");
                flag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("csTest", "APP -> UNINSTALL 卸载" + (type == 0 ? "C" : "B") + "失败");
            flag = false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    private static boolean returnResult(int value) {
        // 代表成功
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }
    }

    /**
     * reboot
     */
    public static void reboot() {
//        String cmd = "su -c reboot";
//        try {
//            Runtime.getRuntime().exec(cmd);
//        } catch (IOException e) {
//            LogUtil.e("REBOOT_ERROR");
//        }
        PowerManager pManager = (PowerManager) APP.getInstance().getSystemService(Context.POWER_SERVICE);
        pManager.reboot("");
    }


    /**
     * 重启APK
     *
     * @param context
     * @param type
     * @return
     */
    public static boolean reBootApk(Context context, int type) {
        try {
            Intent intent = null;
            if (type == 0) {
                intent = context.getPackageManager().getLaunchIntentForPackage(LauncherUtils.homePackName);
            } else if (type == 1) {
                intent = context.getPackageManager().getLaunchIntentForPackage(LauncherUtils.bootPackName);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
//            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            Log.e("csTest", "APP -> reBootApk Exception" + e.toString());
            return false;
        }
        return true;
    }
}
