package com.tuoying.hykc.app;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018/3/21.
 */

public class Constants {
    public static final String WEBSERVICE_URL = "http://tuoying.huoyunkuaiche.com/";
    //public static final String WEBSERVICE_URL="http://ewytek.cn/";
    public static final String TRAILERINFO_URL = "http://39.105.210.202:8080/";
    public static final String ETC_URL = "http://39.105.210.202:63688/";
    //public static final String TRAILERINFO_URL = "http://192.168.1.44:8080/";
    public static final String APPUPDATEURL=WEBSERVICE_URL+"searchversion.jsp?app=driver";
    public static final String MQTT_URL = "tcp://59.110.159.178:1883";
    public static final String YDIp="http://122.114.76.37:8080";
    public static final String AppId = "driver";
    public static final int TIMER_DELAY = 10000;
    public static final String UPDATEAPP_LOCATION = Environment.getExternalStorageDirectory() +
            "/cityfreight/AllenVersionPath/";
    public static final String SFZ_Z = "SFZ_Z";//身份证正面
    public static final String SFZ_F = "SFZ_F";//身份证反面
    public static final String JSZ = "JSZ";//驾驶证
    public static final String XSZ = "XSZ";//行驶证
    public static final String DLYSZ = "DLYSZ";//道路运输证
    public static final String CYZGZ = "CYZGZ";//从业资格证
    public static final int LOC_RADIUS = 3000;
    public static final String PAYTYPE_WX = "wx";
    public static final String VERSIONTIME = "2019.07.16";
    public static final String PICTUREFILE = Environment.getExternalStorageDirectory().toString() + File.separator +
            "hykctemp";
    public static final String UPLOADE_URL = WEBSERVICE_URL + "files/image_upload_new.jsp";
    public static final String ZFB_NOTIFY_URL = WEBSERVICE_URL + "zfb/zfb_notify_url.jsp";
    public static final String ZFB_APP_ID = "2018060660284806";
    public static final String wxAPP_ID = "wx9e92b64666bcd9cb";
    public static final String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCcJocc8OYD/u1D5BnotVumRcwEAi1d3Ecs3LfHkWQtZ04RYvq2ewq1KROL+FsV4WHEJDKlL8JUW/kxtZyLjSKOOikRu+9PiQDTKqkBScmv33BoHtPIpFSqYzKDbIP6+P3AwnFwlskOLtBBX86YzBhknkeNMsUCwOZ5V1Mhgi5p39cXJxWC1CE1im8lPGHUWMQ65hbmfl+wTlU2MIW4gcshq/biV5bi61NNPw8bcmXO7yJjwJ4m0nJbZQmsPsB64nEPTVtOGrh7jddTZJ8qSXtscsv8G4ExRsreMK6/DWJTAIf5+EZN3C/xoIjo6FvYkVJ8qCOZ/1Tv1ZODvdDiBH2zAgMBAAECggEAPKK1auac20ZeCjQEX5o1Oa7210OlLEKCnQgkkWmC9iKcbSH7a2sCMHD/9yV88VUw6sKHrB+MnceNT+FPJd2rYoFxO0K4XjE8UcPKFwb5O8NPWg9GfC5b9kJI9kHoSkfv+a9H78mNz2UcV5aFmk3+1uujq0/gsv4HQW1IicWMkm3PFRdZhHFrNlsnpzFuH9tzWrM8+w3LDpQVforabseF/D+BbAVwPqcB1f8KBySQEAqKfGmKLvZ9sxuCHGIuDIftMpHbXpe3OdEGY2xPc+B6x/zbXFhgKOl/HnWN6c3DRSduOxuLRHCuEtiHsesPh5142W/PjquVWudzTIo40C356QKBgQDST49vo+GCquFAOcaO7KpnQYzQp9gbtxm7krEgHS8MmEIGfmZ4s6Pd70/9ZKo3BrUFtzo9XlylhSBBsWO+h81zegZrIyhUCmskX8CsS5/gntnyJsSz6uaMnWXwssqNePJvho9agVfnXJKYpxphzOE5nI8teI2noOtYthX6cf5L7QKBgQC+EteHVTS2bL2frnCCYx+b6ev7obdQommAX5VlnETs1QZgA3bVfrmoPKip5ltO+dqLA+ZolYNNnE4A3dTWPqC6Pa4MBH05GdkiEAfo2UqgvlSlJoz8hUzwYlz9xSo5v2vDoAZ3c+omNEcPXbYukZZw8h4AUN9VYoCf8KMBb+z8HwKBgArdpXGFLA+OOJkS4xlbTOqMznOxWE8GRMQWLiSUnWuvKiofubeewGalCKL04n55UDz9XHrzNyIuIJ6ERb0AUzMWKOK8LEc7SpHUiKXeOmJmkqgaLKuHBWZac3veIOzyRMRSVXG9oQcJF2HfsFNBPJm3ZwZYZik0/TPWJIiN/y95AoGBAIVpVD55X/ZrfO2H0xCx4o3dMvX8HF9MoCCq9oHLndVd8IuT4uUXfdjIOtqGBbD5TpwksKTXzMG85ENXcPywGMCganmk7QAXVIDEH8kgO3F4JpGE7ZCRP9+6Kus0aN7/rPQybleJ1N/b8cSnUih9p8q/Jq+yFBAqg32/uapYlU6hAoGBAKGA9V4J0AVYC+uYVknaNQ+4goBrfNG4QOhgA/nvcqF0n8/epORZJJloRVfM0P/WKVYd6w3nkB5TzqaZma2t9yTbOOO2riU/zvF2sVrufQtn9QurdjhE7nCJLTuTqAwv45tQeUmP6+/6Efpw2zOMg/KaE7lpFNMxMJKPuTkKpsnE";
/*       //总公司测试
    public static final String ENTERPRISECODE="E0000074";
    public static final String APPIDENTITY="660f712cdb0b11e79148246e965b4750";
    public static final String APPKEY="8a82286bdb0b11e78c190242ac120002";
    public static final String ALCT_URL="https://oapi-staging.alct56.com";*/

    //总公司正式
    public static String ALCT_URL = "https://oapi.alct56.com";
    public static String ENTERPRISECODE = "E0018821";
    public static String APPIDENTITY = "85d0100a001211e8a48d6c0b84d5a88f";
    public static String APPKEY = "812345cb001311e8a48d6c0b84d5a88f";

}
