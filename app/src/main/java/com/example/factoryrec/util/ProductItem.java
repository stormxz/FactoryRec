package com.example.factoryrec.util;

import java.lang.ref.SoftReference;
import java.util.List;

public class ProductItem {

    private static ProductItem mItem;
    private static SoftReference<ProductItem> sr;

    private String mCustomer;           //客户
    private String mMachineType;        //机种
    private String mSN;                 //SN
    private String mBadPhenom;          //不良现象一级选项
    private String mBadPhenom2;         //不良现象二级选项
    private String mOccDate;            //发生日期
    private String mOccTime;            //发生时间
    private String mOccSite;            //发生站点
    private String mBadPosition;        //不良位置
    private String mDisplayText;        //外观确认信息
    private String mOMText;             //OM确认信息
    private String mSignalText;         //讯号量测确认信息
    private String mTitle;              //标题
    private String mFooter;             //页脚/水印
    private String mLogo_Pic;           //Logo图片
    private String mConclusion;         //结论

    private String mConfirm_1 = "外观确认";     //确认页面一
    private String mConfirm_2 = "OM确认";     //确认页面二
    private String mConfirm_3 = "讯号量测确认";     //确认页面三

    private List<String> mHome_BadPic;        //主页不良图片
    private List<String> mDisplay_BadPic;     //外观不良图片
    private List<String> mOM_BadPic;          //OM不良图片
    private List<String> mSignal_BadPic;      //讯号量测不良图片

    public static synchronized ProductItem getInstance() {
        if (mItem == null) {
            sr = new SoftReference(new ProductItem());
            mItem = sr.get();
        }
        return mItem;
    }

    public String getCustomer() {
        return mCustomer;
    }

    public void setCustomer(String mCustomer) {
        this.mCustomer = mCustomer;
    }

    public String getMachineType() {
        return mMachineType;
    }

    public void setMachineType(String mMachineType) {
        this.mMachineType = mMachineType;
    }

    public String getSN() {
        return mSN;
    }

    public void setSN(String mSN) {
        this.mSN = mSN;
    }

    public String getBadPhenom() {
        return mBadPhenom;
    }

    public void setBadPhenom(String mBadPhenom) {
        this.mBadPhenom = mBadPhenom;
    }

    public String getBadPhenom2() {
        return mBadPhenom2;
    }

    public void setBadPhenom2(String mBadPhenom2) {
        this.mBadPhenom2 = mBadPhenom2;
    }

    public String getOccDate() {
        return mOccDate;
    }

    public void setOccDate(String mOccDate) {
        this.mOccDate = mOccDate;
    }

    public String getOccTime() {
        return mOccTime;
    }

    public void setOccTime(String mOccTime) {
        this.mOccTime = mOccTime;
    }

    public String getOccSite() {
        return mOccSite;
    }

    public void setOccSite(String mOccSite) {
        this.mOccSite = mOccSite;
    }

    public String getBadPosition() {
        return mBadPosition;
    }

    public void setBadPosition(String mBadPosition) {
        this.mBadPosition = mBadPosition;
    }

    public String getDisplayText() {
        return mDisplayText;
    }

    public void setDisplayText(String mDisplayText) {
        this.mDisplayText = mDisplayText;
    }

    public String getOMText() {
        return mOMText;
    }

    public void setOMText(String mOMText) {
        this.mOMText = mOMText;
    }

    public String getSignalText() {
        return mSignalText;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setFooter(String mFooter) {
        this.mFooter = mFooter;
    }

    public String getFooter() {
        return mFooter;
    }

    public void setSignalText(String mSignalText) {
        this.mSignalText = mSignalText;
    }

    public String getConclusion() {
        return mConclusion;
    }

    public void setConclusion(String mConclusion) {
        this.mConclusion = mConclusion;
    }

    public String getConfirm_1() {
        return mConfirm_1;
    }

    public void setConfirm_1(String confirm_1) {
        this.mConfirm_1 = confirm_1;
    }

    public String getConfirm_2() {
        return mConfirm_2;
    }

    public void setConfirm_2(String confirm_2) {
        this.mConfirm_2 = confirm_2;
    }

    public String getConfirm_3() {
        return mConfirm_3;
    }

    public void setConfirm_3(String confirm_3) {
        this.mConfirm_3 = confirm_3;
    }

    public List<String> getHome_BadPic() {
        return mHome_BadPic;
    }

    public void setHome_BadPic(List<String> home_badPic) {
        this.mHome_BadPic = home_badPic;
    }

    public List<String> getDisplay_BadPic() {
        return mDisplay_BadPic;
    }

    public void setDisplay_BadPic(List<String> display_badPic) {
        this.mDisplay_BadPic = display_badPic;
    }

    public List<String> getOM_BadPic() {
        return mOM_BadPic;
    }

    public void setOM_BadPic(List<String> OM_badPic) {
        this.mOM_BadPic = OM_badPic;
    }

    public List<String> getSignal_BadPic() {
        return mSignal_BadPic;
    }

    public void setSignal_BadPic(List<String> signal_badPic) {
        this.mSignal_BadPic = signal_badPic;
    }

    public String getLogo_Pic() {
        return mLogo_Pic;
    }

    public void setLogo_Pic(String logo_pic) {
        this.mLogo_Pic = logo_pic;
    }

    public void releaseProductItem() {
        mItem = null;
    }

}
