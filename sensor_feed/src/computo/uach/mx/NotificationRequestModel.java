package computo.uach.mx;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class NotificationRequestModel {

    @SerializedName("data")
    private NotificationData mData;
    @SerializedName("to")
    private String mTo;

    @SerializedName("title")
    private String mTitle;

    public NotificationData getData() {
        return mData;
    }

    public void setData(NotificationData data) {
        mData = data;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        mTo = to;
    }
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}