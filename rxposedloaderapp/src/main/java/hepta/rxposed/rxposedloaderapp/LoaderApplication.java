package hepta.rxposed.rxposedloaderapp;

import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import hepta.rxposed.manager.IRxposedService;

public class LoaderApplication extends Application{


    private static final String TAG = "LoaderApplication";
    private static Context SystemContext = null;
    private static  int currentUid ;
    private static  String currentName = "hepta.rxposed.rxposedloaderapp" ;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate() {
        super.onCreate();


//        callaidl();
//        callpm();

//        LoadFramework();
        GetConfigByProvider(getApplicationContext());

    }


    //能不能直接修改context ，然后进行 contentprovider请求
    private void testContentSesolver() {
        Context context = getApplicationContext();
        ContentResolver contentResolver =  getSystemContext().getContentResolver();
        ContentResolver contentResolver1 = context.getContentResolver();
        Log.e("rzx","callpm");
        try {
            Class<?> contextcls = Class.forName("android.content.Context");
            Field field  = contextcls.getDeclaredField("mPackageName");
//            Class<?> mApplicationContentResolverClass = Class.forName("android.app.ContextImpl$ApplicationContentResolver");
//            Field field = mApplicationContentResolverClass.getField("mPackageName");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public native Context getApplicationContext();

    //onServiceConnected 调用时机会延迟，这个方案不太好
    private void callaidl() {
        Intent intent = new Intent();
        intent.setAction("com.example.aidl");
        intent.setPackage("com.example.android.codelabs.navigation");
        bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
        Log.e("rzx","bindService before");

    }
    IRxposedService iRxposedService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iRxposedService = IRxposedService.Stub.asInterface(iBinder);
            try {
                String string = iRxposedService.getConfig("wrewrew");
                Log.e("rzx","ffffffffffffffff"+string);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    static {
//        Thread.dumpStack();
        System.loadLibrary("Loadtest");

//        SystemContext = getSystemContext();
//        currentUid = android.os.Process.myUid();
//        if(RxposedContext != null){
//            LoadFramework();
//            String name = RxposedContext.getPackageManager().getNameForUid(currentUid);
//            Log.w(TAG," get RxConfigPrvider is:"+name);
//        }else {
//            Log.w(TAG," get getSystemContext is: null");
//
//        }



//        try {
//            customLoadApkAction();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }



    private static void LoadFramework() {
        ConfigSysContext();
        if(GetConfigByProvider(SystemContext)){

        }
    }

    private static void ConfigSysContext() {
        try {
            Class<?> mContextImplClass = Class.forName("android.app.ContextImpl");
            Field mOpPackageName =  mContextImplClass.getField("mOpPackageName");
            mOpPackageName.setAccessible(true);
            mOpPackageName.set(String.class,currentName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

//        RxposedContext.getOpPackageName()
    }

    private static boolean GetConfigByProvider(Context SystemContext) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri uri = Uri.parse("content://com.rxposed.qmulkt.Provider");
            ContentResolver contentResolver = SystemContext.getContentResolver();
            Bundle bundle =contentResolver.call(uri,"getConfig",currentName,null);
//            ContentProviderClient contentProviderClient = RxposedContext.getContentResolver().acquireContentProviderClient(uri);
            String enableUidList_str =  bundle.getString("enableUidList");
            if(enableUidList_str.equals("null")){
                Log.w(TAG," get RxConfigPrvider is null");
                return true;
            }
            String[] app_vec =   enableUidList_str.split("\\|");
            for(String app :app_vec){
                String[] appinfo_vec = app.split(":");

//                GetAppInfoNative(appinfo_vec)
            }

        }

        Log.w(TAG," android version not support rxposed");
        return true;

    }

    private static void GetAppInfoNative(String[] appinfo_vec) {

    }


    private static Context getSystemContext() {
        Context context = null;
        try {
            Method method = Class.forName("android.app.ActivityThread").getMethod("currentActivityThread");
            method.setAccessible(true);
            Object activityThread = method.invoke(null);
            context = (Context) activityThread.getClass().getMethod("getSystemContext").invoke(activityThread);

        } catch (final Exception e) {
            Log.e(TAG, "getSystemContext:"+e.toString());
        }
        return context;
    }

    private static ApplicationInfo GetAppInfobyUID(int uid) {
        ApplicationInfo applicationInfo = null;
        Context context = getSystemContext();
        String pkgName = context.getPackageManager().getNameForUid(uid);
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(pkgName,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationInfo;
    }



    //经过测试在android 10 可以用
    public static void customLoadApkAction() throws Exception {
        //获取 ActivityThread 类
        Class<?> mActivityThreadClass = Class.forName("android.app.ActivityThread");
        //获取 ActivityThread 类
        Class<?> mLoadedApkClass = Class.forName("android.app.LoadedApk");
        //获取 ActivityThread 的 currentActivityThread() 方法
        Method currentActivityThread = mActivityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThread.setAccessible(true);
        //获取 ActivityThread 实例
        Object mActivityThread = currentActivityThread.invoke(null);
        //final ArrayMap<String, WeakReference<LoadedApk>> mPackages = new ArrayMap<>();
        //获取 mPackages 属性
//        Field mPackagesField = mActivityThreadClass.getDeclaredField("mPackages");
//        mPackagesField.setAccessible(true);
        //获取 mPackages 属性的值
//        ArrayMap<String, Object> mPackages = (ArrayMap<String, Object>) mPackagesField.get(mActivityThread);
//        if (mPackages.size() >= 2) {
//            return;
//        }
        //自定义一个 LoadedApk，系统是如何创建的我们就如何创建
        //执行下面的方法会返回一个 LoadedApk，我们就仿照系统执行此方法
        /*
              this.packageInfo = client.getPackageInfoNoCheck(activityInfo.applicationInfo,
                    compatInfo);
              public final LoadedApk getPackageInfo(ApplicationInfo ai, CompatibilityInfo compatInfo,
                    int flags)
         */
        Class<?> mCompatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
        Method getLoadedApkMethod = mActivityThreadClass.getDeclaredMethod("getPackageInfoNoCheck",
                ApplicationInfo.class, mCompatibilityInfoClass);

        /*
             public static final CompatibilityInfo DEFAULT_COMPATIBILITY_INFO = new CompatibilityInfo() {};
         */
        //以上注释是获取默认的 CompatibilityInfo 实例
        Field mCompatibilityInfoDefaultField = mCompatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
        Object mCompatibilityInfo = mCompatibilityInfoDefaultField.get(null);

        //获取一个 ApplicationInfo实例
        ApplicationInfo applicationInfo = SystemContext.getPackageManager().getApplicationInfo(currentName,0);
//        applicationInfo.uid = context.getApplicationInfo().uid;
        //执行此方法，获取一个 LoadedApk
        Object mLoadedApk = getLoadedApkMethod.invoke(mActivityThread, applicationInfo, mCompatibilityInfo);

        Class<?> mContextImplClass = Class.forName("android.app.ContextImpl");
        Method createAppContext = mContextImplClass.getDeclaredMethod("createAppContext",mActivityThreadClass,mLoadedApkClass);
        createAppContext.setAccessible(true);

        Object context =  createAppContext.invoke(null,mActivityThread,mLoadedApk);

        Uri uri = Uri.parse("content://com.rxposed.qmulkt.Provider");
        ContentResolver contentResolver = ((Context)context).getContentResolver();
        Bundle bundle = contentResolver.call(uri,"getConfig",currentName,null);
        String enableUidList_str =  bundle.getString("enableUidList");
        if(enableUidList_str.equals("null")){
            Log.w(TAG," get RxConfigPrvider is null");
        }
        String[] app_vec =   enableUidList_str.split("\\|");
        for(String app :app_vec){
            String[] appinfo_vec = app.split(":");
            Log.e(TAG,appinfo_vec.toString());
//                GetAppInfoNative(appinfo_vec)
        }

    }
}


