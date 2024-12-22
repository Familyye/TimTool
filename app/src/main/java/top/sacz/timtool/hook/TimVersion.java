package top.sacz.timtool.hook;

public class TimVersion {

    public static final long TIM_4_0_95_BETA = 4001;
    public static final long TIM_4_0_95 = 4008;
    public static final long TIM_4_0_96 = 4010;
    public static final long TIM_4_0_97 = 4011;
    public static final long TIM_4_0_98 = 4012;

    public static String getAppName() {
        return HookEnv.getAppName();
    }

    public static String getVersionName() {
        return HookEnv.getVersionName();
    }

    public static int getVersionCode() {
        return HookEnv.getVersionCode();
    }

    public static boolean isQQ() {
        return HookEnv.isQQ();
    }

    public static boolean isTim() {
        return HookEnv.isTim();
    }

    public static long getQQVersion() {
        if (HookEnv.isQQ()) {
            return HookEnv.getVersionCode();
        }
        return 0;
    }

    public static String getQQVersionName() {
        if (HookEnv.isQQ()) {
            return HookEnv.getVersionName();
        }
        return "";
    }

    public static long getTimVersion() {
        if (HookEnv.isTim()) {
            return HookEnv.getVersionCode();
        }
        return 0;
    }

    public static String getTimVersionName() {
        if (HookEnv.isTim()) {
            return HookEnv.getVersionName();
        }
        return "";
    }

}
