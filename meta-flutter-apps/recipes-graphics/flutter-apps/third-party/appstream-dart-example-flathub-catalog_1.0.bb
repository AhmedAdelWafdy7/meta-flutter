#
# Copyright (c) 2026 Joel Winarske. All rights reserved.
# Copyright (c) 2026 Ahmed Wafdy. All rights reserved.
#

SUMMARY = "flathub_catalog"
DESCRIPTION = "Flutter Linux desktop app demonstrating the appstream_dart package."
AUTHOR = "Joel Winarske"
HOMEPAGE = "https://github.com/meta-flutter/appstream_dart"
BUGTRACKER = "https://github.com/meta-flutter/appstream_dart/issues"
SECTION = "graphics"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2bd45dcb3c7d3bd3ad977ef0ea094f47"

SRCREV = "b132c053826a156fd1b05b9d4a90865c088a1d9a"
SRC_URI = "gitsm://github.com/meta-flutter/appstream_dart.git;branch=main;protocol=https"

S = "${WORKDIR}/git"

FLUTTER_APPLICATION_PATH = "example/flathub_catalog"
PUBSPEC_APPNAME = "flathub_catalog"
FLUTTER_APPLICATION_INSTALL_SUFFIX = "appstream-dart-example-flathub-catalog"
PUBSPEC_IGNORE_LOCKFILE = "1"

DEPENDS += " \
    sqlite3 \
"

FLUTTER_PREBUILD_CMD = "\
    flutter config --no-enable-android && \
    flutter config --no-enable-web \
"

FLUTTER_BUILD_ARGS = "bundle --target-platform linux-${@clang_build_arch(d)}"

export ANDROID_SDK_ROOT = "${WORKDIR}/fake-sdk"
export ANDROID_HOME = "${WORKDIR}/fake-sdk"

CXXFLAGS:append = " -std=c++23"

EXTRA_OECMAKE += "\
    -D BUILD_TESTING=OFF \
    -D APPSTREAM_HOOK_BUILD=ON \
"

do_compile_appstream() {
    cmake --build ${B} -j${@oe.utils.cpu_count()}
}

do_compile[prefuncs] += "do_compile_appstream"

inherit cmake pkgconfig flutter-app flutter-native-assets

do_install:append() {
    install -d ${D}${FLUTTER_INSTALL_DIR}/${FLUTTER_SDK_VERSION}/${FLUTTER_RUNTIME_MODE}/lib
    
    cp ${B}/libappstream.so \
        ${D}${FLUTTER_INSTALL_DIR}/${FLUTTER_SDK_VERSION}/${FLUTTER_RUNTIME_MODE}/lib/

    ln -sf /usr/lib/libsqlite3.so.0 \
        ${D}${FLUTTER_INSTALL_DIR}/${FLUTTER_SDK_VERSION}/${FLUTTER_RUNTIME_MODE}/lib/libsqlite3.so
}

FILES:${PN} += "\
    ${FLUTTER_INSTALL_DIR}/${FLUTTER_SDK_VERSION}/${FLUTTER_RUNTIME_MODE}/lib/libappstream.so \
    ${FLUTTER_INSTALL_DIR}/${FLUTTER_SDK_VERSION}/${FLUTTER_RUNTIME_MODE}/lib/libsqlite3.so \
"

FILES:${PN}-dbg += "\
    ${FLUTTER_INSTALL_DIR}/${FLUTTER_SDK_VERSION}/${FLUTTER_RUNTIME_MODE}/lib/.debug/libappstream.so \
"

INSANE_SKIP:${PN} += " libdir buildpaths"
INSANE_SKIP:${PN}-dbg += " libdir"