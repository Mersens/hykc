package com.tuoying.hykc.utils;

import java.util.HashMap;
import java.util.Map;

public class CarInfoUtils {
    private static CarInfoUtils mInstance;
    private Map<String, String> carflMap = new HashMap<>();
    private Map<String, String> carlxMap = new HashMap<>();

    private CarInfoUtils() {
        init();
    }

    public static CarInfoUtils getInstance() {
        if (mInstance == null) {
            mInstance = new CarInfoUtils();
        }

        return mInstance;

    }

    private void init() {
        initCarFlMap();
        initCarLxMap();
    }

    private void initCarFlMap() {

        carflMap.put("Q11", "重型半挂牵引车");
        carflMap.put("H11", "重型普通货车");
        carflMap.put("H39", "轻型仓栅式货车");
        carflMap.put("H19", "重型仓栅式货车");
        carflMap.put("H01", "普通货车");
        carflMap.put("H02", "厢式货车");
        carflMap.put("H04", "罐式货车");
        carflMap.put("Q00", "牵引车");
        carflMap.put("G01", "普通挂车");
        carflMap.put("G03", "罐式挂车");
        carflMap.put("G05", "集装箱挂车");
        carflMap.put("H09", "仓栅式货车");
        carflMap.put("H03", "封闭货车");
        carflMap.put("H05", "平板货车");
        carflMap.put("H06", "集装箱车");
        carflMap.put("H07", "自卸货车");
        carflMap.put("H08", "特殊结构货车");
        carflMap.put("Z00", "专项作业车");
        carflMap.put("G02", "厢式挂车");
        carflMap.put("G07", "仓栅式挂车");
        carflMap.put("G04", "平板挂车");
        carflMap.put("G06", "自卸挂车");
        carflMap.put("G09", "专项作业挂车");
        carflMap.put("X91", "车辆运输车");
        carflMap.put("X92", "车辆运输车(单排)");
    }

    private void initCarLxMap() {
        carlxMap.put("1", "大型汽车号牌(黄底黑字)");
        carlxMap.put("2", "小型汽车号牌(蓝底白字)");
        carlxMap.put("99", "其他号牌");
    }

    public String getFlById(String id) {
        return carflMap.get(id);

    }

    public String getLxById(String id) {
        return carlxMap.get(id);
    }

    public String getLxIdByValue(String value) {
        String key = null;
        for (Map.Entry<String, String> entry : carlxMap.entrySet()) {
            if (value.equals(entry.getValue())) {
                key = entry.getKey();
            }
        }
        return key;

    }

    public String getFlIdByValue(String value) {
        String key = null;
        for (Map.Entry<String, String> entry : carflMap.entrySet()) {
            if (value.equals(entry.getValue())) {
                key = entry.getKey();
            }
        }
        return key;

    }

}
