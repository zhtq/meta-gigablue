require conf/machine/include/gigablue.inc

TARGET_ARCH = "arm"
DEFAULTTUNE = "cortexa15hf-neon-vfpv4"
IMAGE_FSTYPES =+ "tar.bz2"

require conf/machine/include/tune-cortexa15.inc

DVBMEDIASINK_CONFIG = "--with-wma --with-wmv --with-amr --with-pcm --with-dts --with-eac3 --with-h265 --with-vb6 --with-vb8 --with-vb9 --with-spark \
"

PREFERRED_PROVIDER_virtual/blindscan-dvbc = "gigablue-blindscan-utils-arm"
PREFERRED_PROVIDER_virtual/blindscan-dvbs = "gigablue-blindscan-utils-arm"

require conf/machine/include/soc-family.inc

ROOTFS_FILE = "rootfs.tar.bz2"
MTD_KERNEL = "mmcblk0p4"
MTD_ROOTFS = "mmcblk0p5"
MTD_BOOTFS = "mmcblk0p1"
MTD_BLACK  = "mmcblk0"
