#!/bin/sh
set -eu

if [ -d /usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ]; then
  export JAVA_HOME=/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
fi

configuration="${CONFIGURATION:-Debug}"
sdk_name="${SDK_NAME:-iphonesimulator}"

case "$configuration" in
  Release)
    gradle_build_type="Release"
    framework_dir_name="releaseFramework"
    ;;
  *)
    gradle_build_type="Debug"
    framework_dir_name="debugFramework"
    ;;
esac

if echo "$sdk_name" | grep -q "^iphonesimulator"; then
  ./gradlew ":shared:link${gradle_build_type}FrameworkIosSimulatorArm64" ":shared:link${gradle_build_type}FrameworkIosX64"
  source_framework="shared/build/bin/iosSimulatorArm64/${framework_dir_name}/Shared.framework"
  x64_framework="shared/build/bin/iosX64/${framework_dir_name}/Shared.framework"
else
  gradle_task=":shared:link${gradle_build_type}FrameworkIosArm64"
  ./gradlew "$gradle_task"
  source_framework="shared/build/bin/iosArm64/${framework_dir_name}/Shared.framework"
fi

destination_dir="shared/build/xcode-frameworks/${configuration}/${sdk_name}"
mkdir -p "$destination_dir"
rm -rf "${destination_dir}/Shared.framework"
cp -R "$source_framework" "$destination_dir/"

if echo "$sdk_name" | grep -q "^iphonesimulator"; then
  lipo -create \
    "${source_framework}/Shared" \
    "${x64_framework}/Shared" \
    -output "${destination_dir}/Shared.framework/Shared"
fi
