/** ************************************************************************/
/*                                                                         */
/* Copyright (c) 2016 YULONG Company */
/* 宇龙计算机通信科技（深圳）有限公司 版权所有 2015 */
/*                                                                         */
/* PROPRIETARY RIGHTS of YULONG Company are involved in the */
/* subject matter of this material. All manufacturing, reproduction, use, */
/* and sales rights pertaining to this subject matter are governed by the */
/* license agreement. The recipient of this software implicitly accepts */
/* the terms of the license. */
/* 本软件文档资料是宇龙公司的资产,任何人士阅读和使用本资料必须获得 */
/* 相应的书面授权,承担保密责任和接受相应的法律约束. */
/*                                                                         */
/** ************************************************************************/
package com.example.factoryrec.selector.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间处理工具
 *
 * @author yangcheng
 */
public class TimeUtils {

    public static String timeFormat(long timeMillis, String pattern) {

        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(new Date(timeMillis));
    }

    public static String formatPhotoDate(long time) {

        return timeFormat(time, "yyyy-MM-dd");
    }

    public static String formatPhotoDate(String path) {

        File file = new File(path);
        if (file.exists()) {
            long time = file.lastModified();
            return formatPhotoDate(time);
        }
        return "1970-01-01";
    }
}
