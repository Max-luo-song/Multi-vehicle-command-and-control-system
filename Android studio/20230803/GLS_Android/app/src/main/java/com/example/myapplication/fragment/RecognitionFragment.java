package com.example.myapplication.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DictionaryAdapter;
import com.example.myapplication.R;
import com.example.myapplication.VideoPlayer;
import com.example.myapplication.message.YoloImage;
import com.example.myapplication.message.YoloList;
import com.example.myapplication.rosbridge.ROSBridgeClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class RecognitionFragment extends Fragment {

    private VideoPlayer videoPlayer;

    public RecognitionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //对视频流信息进行初始化
        videoPlayer = new VideoPlayer(getActivity());

    }
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recognition, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //获取视频流
        videoPlayer.GetVideo(view);
    }

}
//public class RecognitionFragment extends Fragment {
//    private ImageView imageView;
//    private Button getImgBtn;
//    private ListView listView;
//
//    public RecognitionFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_recognition, container, false);
//    }
//
//
//    public void FindViewById(View view) {
//        imageView = view.findViewById(R.id.image_view);
//        getImgBtn = view.findViewById(R.id.get_img_btn);
//        listView = view.findViewById(R.id.list_view);
//    }
//
//    // 解码YoloImage消息
//    private Bitmap DecodeImage(YoloImage yoloImage){
//        // Base64字符串解码为二进制数组
//        byte[] imageData = Base64.decode(yoloImage.data, Base64.DEFAULT);
//        // 将二进制数组转为Bitmap
//        Bitmap bitmap = Bitmap.createBitmap(yoloImage.width, yoloImage.height, Config.ARGB_8888);
//        int index = 0;
//        for (int y = 0; y < yoloImage.height; y++) {
//            for (int x = 0; x < yoloImage.width; x++) {
//                int b = imageData[index] & 0xFF;
//                int g = imageData[index + 1] & 0xFF;
//                int r = imageData[index + 2] & 0xFF;
//                int color = Color.rgb(r, g, b);
//                bitmap.setPixel(x, y, color);
//                index += 3;
//            }
//        }
//        return bitmap;
//    }
//
//    //解码YoloList消息
//    Map<String, List<Double>> DecodeDictionary(YoloList yoloList){
//        String yoloStr = yoloList.data;
//        Map<String, List<Double>> yoloMapCar1 = new HashMap<>();
//        //将字符串转为哈希表
//        if(yoloStr.length() > 2) {
//            String cleanedStr = yoloStr.replace("]', '", "&").replace("[", "").replace("]", "").replace("'", "");
//            String[] pairsStr = cleanedStr.split("&");
//            for (String pair : pairsStr) {
//                // 分割键值对为键和值
//                String[] keyValue = pair.split(":");
//                // 获取键和值
//                String key = keyValue[0].trim();
//                List<Double> values = new ArrayList<>();
//                // 提取值中的浮点数
//                String[] floatStrings = keyValue[1].split(", ");
//                for (String floatString : floatStrings) {
//                    values.add(Double.parseDouble(floatString.trim()));
//                }
//                // 将键值对添加到字典
//                yoloMapCar1.put(key, values);
//            }
//        }
//        return yoloMapCar1;
//    }
//
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
//        super.onViewCreated(view, savedInstanceState);
//        FindViewById(view);
//        // 由于需要断连，故专门建立一个client
//        ROSBridgeClient img_client_car1 = new ROSBridgeClient("ws://192.168.31.39:7070");
//
//        Context that = getContext();
//        getImgBtn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                // 连接
////                img_client_car1.connect();
//
//                // 订阅图像
//                com.example.myapplication.Topic<YoloImage> imgTopicCar1 = new com.example.myapplication.Topic<YoloImage>("/yolo_image", YoloImage.class, img_client_car1);
//                imgTopicCar1.subscribe();
//                YoloImage imgCar1;
//
//                // 订阅list
//                com.example.myapplication.Topic<YoloList> listTopicCar1 = new com.example.myapplication.Topic<YoloList>("/yolo_list", YoloList.class, img_client_car1);
//                listTopicCar1.subscribe();
//                YoloList listCar1;
//
//                try {
//                    // 获取图像
//                    imgCar1 = imgTopicCar1.take();
//                    Bitmap bit_map_car1 = DecodeImage(imgCar1);
//                    imageView.setImageBitmap(bit_map_car1);
//
//
//                    // 获取列表
//                    listCar1 = listTopicCar1.take();
//                    Map<String, List<Double>> map_car1 = DecodeDictionary(listCar1);
//                    DictionaryAdapter dic_adapter_car1 = new DictionaryAdapter(that, map_car1);
//                    listView.setAdapter(dic_adapter_car1);
//                    // 关闭连接
////                    img_client_car1.disconnect();
//
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//    }
//}