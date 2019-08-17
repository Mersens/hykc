
#指定代码的压缩级别
-optimizationpasses 5

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
-printmapping priguardMapping.txt


# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#不混淆内部类
-keepattributes InnerClasses

# 枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#######################Demo里不能被混淆的########################################
-keep public class com.webank.testcloud.SlipButton{
    public <methods>;
}
-keep public class com.webank.testcloud.SlipButton$*{
    *;
}

-keep public class com.webank.testcloud.SignUseCase{
    *;
}

-keep public class com.webank.testcloud.SignUseCase$*{
    *;
}
#######################Demo里不能被混淆的########################################

#云刷脸（动作活体）的混淆规则
-include webank-cloud-face-action-proguard-rules.pro

######################云产品依赖的第三方库 混淆规则-BEGIN###########################

## support:appcompat-v7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

##########################云产品依赖的第三方库 混淆规则-END##############################


#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt

