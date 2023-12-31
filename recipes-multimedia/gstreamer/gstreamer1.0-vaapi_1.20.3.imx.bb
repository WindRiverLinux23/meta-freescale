SUMMARY = "VA-API support to GStreamer"
HOMEPAGE = "https://gstreamer.freedesktop.org/"
DESCRIPTION = "gstreamer-vaapi consists of a collection of VA-API \
based plugins for GStreamer and helper libraries: `vaapidecode', \
`vaapiconvert', and `vaapisink'."

REALPN = "gstreamer-vaapi"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "https://gstreamer.freedesktop.org/src/${REALPN}/${REALPN}-${@get_gst_ver('${PV}')}.tar.xz"

SRC_URI[sha256sum] = "6ee99eb316abdde9ad37002915bd8c3867918f6fdc74b7cf2ac4c1ae0d690b45"

S = "${WORKDIR}/${REALPN}-${@get_gst_ver('${PV}')}"
DEPENDS = "libva gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad"

inherit meson pkgconfig features_check upstream-version-is-even

REQUIRED_DISTRO_FEATURES ?= "opengl"

EXTRA_OEMESON += " \
    -Ddoc=disabled \
    -Dexamples=disabled \
    -Dtests=enabled \
"

# Drop .imx from PV
def get_gst_ver(v):
    return oe.utils.trim_version(v, 3)

PACKAGES =+ "${PN}-tests"

# OpenGL packageconfig factored out to make it easy for distros
# and BSP layers to pick either glx, egl, or no GL. By default,
# try detecting X11 first, and if found (with OpenGL), use GLX,
# otherwise try to check if EGL can be used.
PACKAGECONFIG_GL ?= "egl"

PACKAGECONFIG ??= "drm encoders \
                   ${PACKAGECONFIG_GL} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'wayland x11', d)}"

PACKAGECONFIG[drm] = "-Dwith_drm=yes,-Dwith_drm=no,udev libdrm"
PACKAGECONFIG[egl] = "-Dwith_egl=yes,-Dwith_egl=no,virtual/egl"
PACKAGECONFIG[encoders] = "-Dwith_encoders=yes,-Dwith_encoders=no"
PACKAGECONFIG[glx] = "-Dwith_glx=yes,-Dwith_glx=no,virtual/libgl"
PACKAGECONFIG[wayland] = "-Dwith_wayland=yes,-Dwith_wayland=no,wayland-native wayland wayland-protocols"
PACKAGECONFIG[x11] = "-Dwith_x11=yes,-Dwith_x11=no,virtual/libx11 libxrandr libxrender"

FILES:${PN} += "${libdir}/gstreamer-*/*.so"
FILES:${PN}-dbg += "${libdir}/gstreamer-*/.debug"
FILES:${PN}-dev += "${libdir}/gstreamer-*/*.a"
FILES:${PN}-tests = "${bindir}/*"
