#云刷脸依赖的webank normal库混淆规则
-include webank-cloud-normal-proguard-rules.pro

#不混淆内部类
-keepattributes InnerClasses

######################云刷脸混淆规则 faceverify-BEGIN###########################
-keep public class com.webank.faceaction.tools.WbCloudFaceVerifySdk{
    public <methods>;
    public static final *;
}
-keep public class com.webank.faceaction.tools.WbCloudFaceVerifySdk$*{
    *;
}
-keep public class com.webank.faceaction.tools.ErrorCode{
    *;
}
-keep public class com.webank.faceaction.ui.FaceVerifyStatus{

}
-keep public class com.webank.faceaction.ui.FaceVerifyStatus$Mode{
    *;
}
-keep public class com.webank.faceaction.tools.IdentifyCardValidate{
    public <methods>;
}
-keep public class com.tencent.youtulivecheck.**{
    *;
}
-keep public class com.webank.faceaction.Request.*$*{
    *;
}
-keep public class com.webank.faceaction.Request.*{
    *;
}
#######################云刷脸混淆规则 faceverify-END#############################

######################云刷脸依赖的第三方库 混淆规则-BEGIN###########################

## support:appcompat-v7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
##########################云刷脸依赖的第三方库 混淆规则-END##############################









