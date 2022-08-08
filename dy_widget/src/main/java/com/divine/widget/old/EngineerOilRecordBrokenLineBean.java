package com.divine.widget.old;

/**
 * author: Divine
 * <p>
 * date: 2019/3/4
 */
public class EngineerOilRecordBrokenLineBean  {
    private String id;
    private String deviceId;
    private String machineKey;
    private String presentTs;
    private String percentage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMachineKey() {
        return machineKey;
    }

    public void setMachineKey(String machineKey) {
        this.machineKey = machineKey;
    }

    public String getPresentTs() {
        return presentTs;
    }

    public void setPresentTs(String presentTs) {
        this.presentTs = presentTs;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "EngineerOilRecord{" +
                "id='" + id + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", machineKey='" + machineKey + '\'' +
                ", presentTs='" + presentTs + '\'' +
                ", percentage='" + percentage + '\'' +
                '}';
    }
}
