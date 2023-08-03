package com.example.myapplication.message;

@MessageType(string = "sensor_msgs/Image")
public class YoloImage extends Message {
    public Header header;
    public int height;
    public int width;
    public String encoding;
    public int is_bigendian;
    public long step;
    public String data;
    public YoloImage() {
    }

    @Override
    public String toString() {
        return "Realsense_Image{" +
                "header=" + header +
                ", height=" + height +
                ", width=" + width +
                ", encoding='" + encoding + '\'' +
                ", is_bigendian=" + is_bigendian +
                ", step=" + step +
                '}';
    }
}