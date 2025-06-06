SUMMARY = "Linux kernel for ${MACHINE}"
LICENSE = "GPLv2"
SECTION = "kernel"

COMPATIBLE_MACHINE = "^(gbquad4k|gbue4k|gbquad4kpro)$"

MODULE = "linux-4.1.20"

inherit kernel machine_kernel_pr


SRC_DATE = "20180206"
SRC_DATE_gbquad4kpro = "20250410"

SRC_NAME = "legacy"
SRC_NAME_gbquad4kpro = "pro"

SRC_URI[legacy.md5sum] = "6036c5d722071e72d5d66dbf7ee74992"
SRC_URI[legacy.sha256sum] = "eff7eecf55dd75ecb44bd8b8fe16f588d19c1eac92125eaed2b6834348d12def"
SRC_URI[pro.md5sum] = "7854cbc1984e9723c7d46d6923de9295"
SRC_URI[pro.sha256sum] = "6a97857446c41b94de5a5fc618afa68a493b7cc6f7f0bca14b880d95be7966ad"

LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI += "https://source.mynonpublic.com/gigablue/linux/gigablue-linux-${PV}-${SRC_DATE}.tar.gz;name=${SRC_NAME} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'initrd', 'file://defconfig_initrd' , 'file://defconfig', d)} \
    file://initramfs-subdirboot.cpio.gz;unpack=0 \
    file://gbfindkerneldevice.py \
    file://0002-linux_dvb-core.patch \
    file://0002-bcmgenet-recovery-fix.patch \
    file://0002-linux_4_1_1_9_dvbs2x.patch \
    file://0002-linux_dvb_adapter.patch \
    file://0002-linux_rpmb_not_alloc.patch \
    file://0001-regmap-add-regmap_write_bits.patch \
    file://0003-Add-support-for-dvb-usb-stick-Hauppauge-WinTV-soloHD.patch \
    file://0004-af9035-add-USB-ID-07ca-0337-AVerMedia-HD-Volar-A867.patch \
    file://0005-Add-support-for-EVOLVEO-XtraTV-stick.patch \
    file://0006-dib8000-Add-support-for-Mygica-Geniatech-S2870.patch \
    file://0007-dib0700-add-USB-ID-for-another-STK8096-PVR-ref-desig.patch \
    file://0008-add-Hama-Hybrid-DVB-T-Stick-support.patch \
    file://0009-Add-Terratec-H7-Revision-4-to-DVBSky-driver.patch \
    file://0010-media-Added-support-for-the-TerraTec-T1-DVB-T-USB-tu.patch \
    file://0011-media-tda18250-support-for-new-silicon-tuner.patch \
    file://0012-media-dib0700-add-support-for-Xbox-One-Digital-TV-Tu.patch \
    file://0013-mn88472-Fix-possible-leak-in-mn88472_init.patch \
    file://0014-staging-media-Remove-unneeded-parentheses.patch \
    file://0015-staging-media-mn88472-simplify-NULL-tests.patch \
    file://0016-mn88472-fix-typo.patch \
    file://0017-mn88472-finalize-driver.patch \
    file://0018-Add-support-for-dvb-usb-stick-Hauppauge-WinTV-dualHD.patch \
    file://0001-dvb-usb-fix-a867.patch \
    file://0001-Support-TBS-USB-drivers-for-4.1-kernel.patch \
    file://0001-TBS-fixes-for-4.1-kernel.patch \
    file://0001-STV-Add-PLS-support.patch \
    file://0001-STV-Add-SNR-Signal-report-parameters.patch \
    file://blindscan2.patch \
    file://0001-stv090x-optimized-TS-sync-control.patch \
    file://kernel-add-support-for-gcc6.patch \
    file://kernel-add-support-for-gcc7.patch \
    file://kernel-add-support-for-gcc8.patch \
    file://kernel-add-support-for-gcc9.patch \
    file://0002-log2-give-up-on-gcc-constant-optimizations.patch \
    file://0003-uaccess-dont-mark-register-as-const.patch \
    file://make-yyloc-declaration-extern.patch \
    file://add-partition-specific-uevent-callbacks-for-partition-info.patch \
    file://move-default-dialect-to-SMB3.patch \
    file://linux3.4-ARM-8933-1-replace-Sun-Solaris-style-flag-on-section.patch \
"

S = "${WORKDIR}/linux-${PV}"
B = "${WORKDIR}/build"

export OS = "Linux"
KERNEL_IMAGETYPE = "zImage"
KERNEL_OBJECT_SUFFIX = "ko"
KERNEL_IMAGEDEST = "tmp"
KERNEL_OUTPUT = "arch/${ARCH}/boot/${KERNEL_IMAGETYPE}"

FILES_${KERNEL_PACKAGE_NAME}-image = "/${KERNEL_IMAGEDEST}/zImage /${KERNEL_IMAGEDEST}/gbfindkerneldevice.py"

kernel_do_configure_prepend() {
        install -d ${B}/usr
        install -m 0644 ${WORKDIR}/initramfs-subdirboot.cpio.gz ${B}/
        if [ -e ${WORKDIR}/defconfig_initrd ]; then
            mv ${WORKDIR}/defconfig_initrd ${WORKDIR}/defconfig
        fi
}
kernel_do_install_append() {
        install -d ${D}/${KERNEL_IMAGEDEST}
        install -m 0755 ${KERNEL_OUTPUT} ${D}/${KERNEL_IMAGEDEST}
        install -m 0755 ${WORKDIR}/gbfindkerneldevice.py ${D}/${KERNEL_IMAGEDEST}
}

kernel_do_compile() {
        unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS MACHINE
        oe_runmake ${KERNEL_IMAGETYPE_FOR_MAKE} ${KERNEL_ALT_IMAGETYPE} CC="${KERNEL_CC}" LD="${KERNEL_LD}" EXTRA_CFLAGS="-Wno-attribute-alias"
        if test "${KERNEL_IMAGETYPE_FOR_MAKE}.gz" = "${KERNEL_IMAGETYPE}"; then
                gzip -9c < "${KERNEL_IMAGETYPE_FOR_MAKE}" > "${KERNEL_OUTPUT}"
        fi
}

pkg_postinst_kernel-image () {
    if [ "x$D" == "x" ]; then
        if [ -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE} ] ; then
            ${PYTHON_PN} /${KERNEL_IMAGEDEST}/gbfindkerneldevice.py
            dd if=/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE} of=/dev/kernel
        fi
    fi
    rm -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}
    true
}

pkg_postrm_kernel-image () {
}

FILESEXTRAPATHS_prepend := "${THISDIR}/linux-gigablue-${KV}:"

do_rm_work() {
}
