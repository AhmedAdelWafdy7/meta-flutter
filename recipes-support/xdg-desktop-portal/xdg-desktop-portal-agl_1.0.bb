#
# Copyright (c) 2026 Ahmed Wafdy. All rights reserved.
#
SUMMARY = "XDG Desktop Portal backend for AGL (Automotive Grade Linux)"
DESCRIPTION = "A xdg-desktop-portal backend for AGL that provides portal APIs \
               for sandboxed applications to access desktop features."
AUTHOR = "Ahmed Wafdy <ahmedadelwafdy782@gmail.com>"
HOMEPAGE = "https://github.com/AhmedAdelWafdy7/xdg-desktop-portal-agl"
BUGTRACKER = "https://github.com/AhmedAdelWafdy7/xdg-desktop-portal-agl/issues"
SECTION = "support"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1803fa9c2c3ce8cb06b4861d75310742"

SRC_URI = "git://github.com/AhmedAdelWafdy7/xdg-desktop-portal-agl.git;protocol=https;branch=yocto"
SRCREV = "4a4c447e5f9bedb5a67ad137200ff892a9644e10"
PV = "0.1.0+git${SRCPV}"
S = "${WORKDIR}/git"

DEPENDS = "xdg-desktop-portal"
RDEPENDS:${PN} = "xdg-desktop-portal"

inherit cargo

do_fetch[network] = "1"
do_compile[network] = "1"
CARGO_BUILD_FLAGS:remove = "--frozen"
CARGO_DISABLE_BITBAKE_VENDORING = "1"

do_install() {
    # binary
    install -d ${D}${libexecdir}
    install -m 0755 ${B}/target/${RUST_TARGET_SYS}/release/xdg-desktop-portal-agl \
        ${D}${libexecdir}/

    # systemd user services
    install -d ${D}${systemd_user_unitdir}
    install -m 0644 ${S}/data/xdg-desktop-portal-agl.service.in \
        ${D}${systemd_user_unitdir}/xdg-desktop-portal-agl.service
    install -m 0644 ${S}/data/weston-environment.service \
        ${D}${systemd_user_unitdir}/weston-environment.service

    install -d ${D}${systemd_user_unitdir}/default.target.wants
    ln -sf ../weston-environment.service \
        ${D}${systemd_user_unitdir}/default.target.wants/weston-environment.service

    # enable portal under xdg-desktop-portal.service
    install -d ${D}${systemd_user_unitdir}/xdg-desktop-portal.service.wants
    ln -sf ../xdg-desktop-portal-agl.service \
        ${D}${systemd_user_unitdir}/xdg-desktop-portal.service.wants/xdg-desktop-portal-agl.service

    # portal descriptor
    install -d ${D}${datadir}/xdg-desktop-portal/portals
    install -m 0644 ${S}/data/agl.portal \
        ${D}${datadir}/xdg-desktop-portal/portals/

    # portal config
    install -d ${D}${datadir}/xdg-desktop-portal
    install -m 0644 ${S}/data/agl-portals.conf \
        ${D}${datadir}/xdg-desktop-portal/

    # D-Bus activation
    install -d ${D}${datadir}/dbus-1/services
    install -m 0644 ${S}/data/org.freedesktop.impl.portal.desktop.agl.service \
        ${D}${datadir}/dbus-1/services/

    # profile.d
    install -d ${D}${sysconfdir}/profile.d
    install -m 0755 ${S}/data/agl-portal.sh \
        ${D}${sysconfdir}/profile.d/
}

FILES:${PN} += " \
    ${libexecdir}/xdg-desktop-portal-agl \
    ${systemd_user_unitdir}/xdg-desktop-portal-agl.service \
    ${systemd_user_unitdir}/weston-environment.service \
    ${systemd_user_unitdir}/default.target.wants/weston-environment.service \
    ${systemd_user_unitdir}/xdg-desktop-portal.service.wants/xdg-desktop-portal-agl.service \
    ${datadir}/xdg-desktop-portal/portals/agl.portal \
    ${datadir}/xdg-desktop-portal/agl-portals.conf \
    ${datadir}/dbus-1/services/org.freedesktop.impl.portal.desktop.agl.service \
    ${sysconfdir}/profile.d/agl-portal.sh \
"