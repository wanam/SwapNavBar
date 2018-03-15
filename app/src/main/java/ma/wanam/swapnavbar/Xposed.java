package ma.wanam.swapnavbar;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * Hooks based on
 * https://android.googlesource.com/platform/frameworks/base/+/6fc4600/packages/SystemUI/src/com/android/systemui/statusbar/phone/NavigationBarInflaterView.java#175
 */
public class Xposed implements IXposedHookLoadPackage {

    public static final String COM_ANDROID_SYSTEMUI = "com.android.systemui";
    public static final String STATUSBAR_PHONE_NAVIGATION_BAR_INFLATER_VIEW = "com.android.systemui.statusbar.phone.NavigationBarInflaterView";
    public static final String MA_WANAM_SWAPNAVBAR = "ma.wanam.swapnavbar";
    public static final String GRAVITY_SEPARATOR = ";";

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {

        if (lpparam.packageName.equals(COM_ANDROID_SYSTEMUI)) {
            try {
                XposedHelpers.findAndHookMethod(STATUSBAR_PHONE_NAVIGATION_BAR_INFLATER_VIEW,
                        lpparam.classLoader, "inflateLayout",
                        String.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                try {
                                    String newLayout = (param.args[0] == null ?
                                            (String) XposedHelpers.callMethod(param.thisObject, "getDefaultLayout") :
                                            (String) param.args[0]);

                                    String[] sets = newLayout.split(GRAVITY_SEPARATOR, 3);

                                    // Did replace string ids instead of buttons to keep the current padding
                                    sets[0] = sets[0].replace("left", "right").replace("back", "recent");
                                    sets[2] = sets[2].replace("right", "left").replace("recent", "back");
                                    param.args[0] = sets[0] + GRAVITY_SEPARATOR + sets[1] + GRAVITY_SEPARATOR + sets[2];

                                } catch (Throwable t) {
                                    XposedBridge.log(t);
                                }
                            }
                        });

            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }

        if (lpparam.packageName.equals(MA_WANAM_SWAPNAVBAR)) {
            try {
                XposedHelpers.findAndHookMethod(MA_WANAM_SWAPNAVBAR + ".XChecker", lpparam.classLoader,
                        "isEnabled", XC_MethodReplacement.returnConstant(Boolean.TRUE));
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }
    }

    private void debug(String msg) {
        if (BuildConfig.DEBUG) {
            XposedBridge.log(msg);
        }
    }

}
