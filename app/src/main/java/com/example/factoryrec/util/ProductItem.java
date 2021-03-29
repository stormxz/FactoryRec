package com.example.factoryrec.util;

public class ProductItem {

    private static ProductItem mItem;

    private String mCustomer;           //客户
    private String mMachineType;        //机种
    private String mSN;                 //SN
    private String mBadPhenom;          //不良现象
    private String mOccTime;            //发生时间
    private String mOccSite;            //发生站点
    private String mBadPosition;        //不良位置
    private String mDisplayText;        //外观确认信息
    private String mOMText;             //OM确认信息
    private String mSignalText;         //讯号量测确认信息
    private String mConclusion;         //结论

    public static synchronized ProductItem getInstance() {
        if (mItem == null) {
            mItem = new ProductItem();
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

    public void setSignalText(String mSignalText) {
        this.mSignalText = mSignalText;
    }

    public String getConclusion() {
        return mConclusion;
    }

    public void setConclusion(String mConclusion) {
        this.mConclusion = mConclusion;
    }

}
