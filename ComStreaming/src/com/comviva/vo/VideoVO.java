package com.comviva.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class VideoVO implements Serializable{

    /** * */
    private static final long serialVersionUID = 1L;
    
    private Integer totalCount;
    private String streamName;
    private Integer rtspCount;
    private Integer httpCount;
    private List<VideoVO> streamList;
    private String time;
    /** * @return the streamList */
    public List<VideoVO> getStreamList() {
        return streamList;
    }
    /** * @param streamList the streamList to set */
    public void setStreamList(List<VideoVO> streamList) {
        this.streamList = streamList;
    }
    /** * @return the totalCount */
    public Integer getTotalCount() {
        return totalCount;
    }
    /** * @param totalCount the totalCount to set */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    /** * @return the streamName */
    public String getStreamName() {
        return streamName;
    }
    /** * @param streamName the streamName to set */
    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }
    /** * @return the rtspCount */
    public Integer getRtspCount() {
        return rtspCount;
    }
    /** * @param rtspCount the rtspCount to set */
    public void setRtspCount(Integer rtspCount) {
        this.rtspCount = rtspCount;
    }
    /** * @return the httpCount */
    public Integer getHttpCount() {
        return httpCount;
    }
    /** * @param httpCount the httpCount to set */
    public void setHttpCount(Integer httpCount) {
        this.httpCount = httpCount;
    }
    /** * @return the time */
    public String getTime() {
        return time;
    }
    /** * @param time the time to set */
    public void setTime(String time) {
        this.time = time;
    }

    
    
}
