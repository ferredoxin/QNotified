#!/usr/bin/env sh
set -e
if [ ! -d "$ANDROID_HOME" ]; then
  echo >&2 "Fatal: ANDROID_HOME not found. Aborting."
  exit 1
fi
command -v javac >/dev/null 2>&1 || {
  echo >&2 "Fatal: command javac not found. Aborting."
  exit 1
}
command -v git >/dev/null 2>&1 || {
  echo >&2 "Fatal: command git not found. Aborting."
  exit 1
}
command -v java >/dev/null 2>&1 || {
  echo >&2 "Fatal: command java not found. Aborting."
  exit 1
}
command -v keytool >/dev/null 2>&1 || {
  echo >&2 "Fatal: command keytool not found. Aborting."
  exit 1
}

TMPVAR=$(grep --color=none minSdkVersion app/build.gradle)
MIN_SDK=${TMPVAR#*'minSdkVersion '}
TMPVAR=$(grep --color=none targetSdkVersion app/build.gradle)
TARGET_SDK=${TMPVAR#*'targetSdkVersion '}
TMPVAR=$(grep --color=none versionCode app/build.gradle)
VER_CODE=${TMPVAR#*'versionCode '}
TMPVAR=$(grep --color=none versionName app/build.gradle)
TMPVAR=${TMPVAR#*versionName }
TMPVAR=${TMPVAR#*'"'}
VER_NAME=${TMPVAR%'"'*}
CMT_ID=$(git rev-parse --short HEAD)
VER_NAME="$VER_NAME.$CMT_ID"
echo "VER_NAME=$VER_NAME,VER_CODE=$VER_CODE,MIN_SDK=$MIN_SDK,TARGET_SDK=$TARGET_SDK."

if [ ! -d out ]; then
  mkdir "out"
fi
if [ ! -d out/gen ]; then
  mkdir "out/gen"
fi
rm -f out/*.dex
rm -f out/*.apk
rm -f out/*.ap_

mkdir -p out/gen/nil/nadph/qnotified
echo "package nil.nadph.qnotified;" >out/gen/nil/nadph/qnotified/BuildConfig.java
echo "public final class BuildConfig {" >>out/gen/nil/nadph/qnotified/BuildConfig.java
echo "  public static final int VERSION_CODE = $VER_CODE;" >>out/gen/nil/nadph/qnotified/BuildConfig.java
echo "  public static final String VERSION_NAME = \"$VER_NAME\";" >>out/gen/nil/nadph/qnotified/BuildConfig.java
echo "}" >>out/gen/nil/nadph/qnotified/BuildConfig.java

ANDROID_JAR="$ANDROID_HOME"/platforms/android-29/android.jar
AAPT="$ANDROID_HOME"/build-tools/29.0.3/aapt
DX="$ANDROID_HOME"/build-tools/29.0.3/dx
ZIPALIGN="$ANDROID_HOME"/build-tools/29.0.3/zipalign
APKSIGNER="$ANDROID_HOME"/build-tools/29.0.3/apksigner

XPOSED_INIT=$(cat app/src/main/assets/xposed_init)
echo "nil.nadph.qnotified.HookEntry" >app/src/main/assets/xposed_init
$AAPT package -f --version-code "$VER_CODE" --version-name "$VER_NAME" --target-sdk-version "$TARGET_SDK" --min-sdk-version "$MIN_SDK" -M app/src/main/AndroidManifest.xml  -A app/src/main/assets/ -I "$ANDROID_JAR" -S app/src/main/res -m -J out/gen -F out/res.ap_
echo -n "$XPOSED_INIT" >app/src/main/assets/xposed_init
find app/src/main/java -name "*.java" > out/sources.txt
find out/gen -name "*.java" >> out/sources.txt
#-Xlint:deprecation -Xlint:unchecked
javac -g -encoding UTF-8 -source 8 -target 8 -bootclasspath "$ANDROID_JAR" -cp app/lib/api-82.jar:app/lib/qqstub.jar -sourcepath app/src/main/java -d out @out/sources.txt
$DX --dex --output=out/classes.dex out/
java -cp "$ANDROID_HOME"/tools/lib/sdklib-26.0.0-dev.jar com.android.sdklib.build.ApkBuilderMain out/outpu_.apk -v -u -z out/res.ap_ -f out/classes.dex
$ZIPALIGN -v 4 out/outpu_.apk out/output.apk
if [ ! -e out/debug.keystore ]; then
  keytool -genkey -v -keystore out/debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
fi
$APKSIGNER sign --ks out/debug.keystore --ks-key-alias androiddebugkey --ks-pass pass:android out/output.apk
