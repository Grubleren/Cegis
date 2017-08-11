LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
APP_OPTIM := release 
LOCAL_MODULE    := resampling
LOCAL_C_INCLUDES := ../Calculations/srcWin32/
LOCAL_SRC_FILES := onload.cpp
LOCAL_SRC_FILES += resampling.cpp

LOCAL_NEON_CFLAGS := -mfloat-abi=softfp -mfpu=neon -march=armv7 -O3

TARGET-process-src-files-tags += $(call add-src-files-target-cflags, $(LOCAL_SRC_FILES), $(LOCAL_NEON_CFLAGS))

include $(BUILD_SHARED_LIBRARY)
