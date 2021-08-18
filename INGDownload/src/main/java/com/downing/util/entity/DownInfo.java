package com.downing.util.entity;

/**
 * @author downing
 * @desc
 * @date 2021/6/28 18:17
 */
public class DownInfo {

    // -1失败 0下载中 1成功
    private int status = -1;
    //进度
    private double progress = 0;
    //提示消息
    private String message = "下载成功";
    //耗时
    private long costTime;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }
}
