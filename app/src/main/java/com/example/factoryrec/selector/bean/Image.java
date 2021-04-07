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
package com.example.factoryrec.selector.bean;

/**
 * 图片实体
 *
 * @author yangcheng
 */
public class Image {

    public String path;
    public String name;
    public long time;

    public Image(String path, String name, long time) {

        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {

        try {
            Image other = (Image) o;
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
