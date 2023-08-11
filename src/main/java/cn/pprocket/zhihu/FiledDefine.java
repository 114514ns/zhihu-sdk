package cn.pprocket.zhihu;

import java.util.HashMap;
import java.util.Map;

public class FiledDefine {
    public static Map<FILED,String> common = new HashMap<>();
    public static Map<FILED,String> recommend  = new HashMap<>();
    static {
        common.put(FILED.CREATE_TIME,"createdTime");
        common.put(FILED.LIKE,"voteupCount");
        recommend.put(FILED.CREATE_TIME,"created_time");
        recommend.put(FILED.LIKE,"voteup_count");
    }
    public static String get(FILED filed) {
        String type = "common";
        StackTraceElement[] frames = new Throwable().getStackTrace();
        for (StackTraceElement frame : frames) {
            if (frame.toString().contains("getRecommend")) {
                type = "recommend";
                break;
            }
        }
        if (type.equals("common")) {
            return common.get(filed);
        } else if (type.equals("recommend")) {
            return recommend.get(filed);
        }
        return null;
    }
}
enum FILED {
    CREATE_TIME,
    LIKE,

}