package com.example.myapplication.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.DictionaryAdapter;
import com.example.myapplication.MainActivity;
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

    private ImageView imageView;
    private Button getImgBtn;
    private ListView listView;
    private VideoPlayer videoPlayer;

    public Spinner spinner;
    public Map<String, ROSBridgeClient> mapCar;
    public  ROSBridgeClient clientCar;
    public RecognitionFragment() {
        // Required empty public constructor
    }
    public void FindViewById(View view) {
        imageView = view.findViewById(R.id.image_view);
        getImgBtn = view.findViewById(R.id.get_img_btn);
        listView = view.findViewById(R.id.list_view);
    }

    // 解码YoloImage消息
    private Bitmap DecodeImage(YoloImage yoloImage){
        // Base64字符串解码为二进制数组
        byte[] imageData = Base64.decode(yoloImage.data, Base64.DEFAULT);
        // 将二进制数组转为Bitmap
        Bitmap bitmap = Bitmap.createBitmap(yoloImage.width, yoloImage.height, Config.ARGB_8888);
        int index = 0;
        for (int y = 0; y < yoloImage.height; y++) {
            for (int x = 0; x < yoloImage.width; x++) {
                int b = imageData[index] & 0xFF;
                int g = imageData[index + 1] & 0xFF;
                int r = imageData[index + 2] & 0xFF;
                int color = Color.rgb(r, g, b);
                bitmap.setPixel(x, y, color);
                index += 3;
            }
        }
        return bitmap;
    }

    //解码YoloList消息
    Map<String, List<Double>> DecodeDictionary(YoloList yoloList){
        String yoloStr = yoloList.data;
        Map<String, List<Double>> yoloMapCar = new HashMap<>();
        //将字符串转为哈希表
        if(yoloStr.length() > 2) {
            String cleanedStr = yoloStr.replace("],", "&").replace("[", "").replace("]", "");
            String[] pairsStr = cleanedStr.split("&");
            for (String pair : pairsStr) {
                // 分割键值对为键和值
                String[] keyValue = pair.split(":");
                // 获取键和值
                String key = keyValue[0].trim();
                List<Double> values = new ArrayList<>();
                // 提取值中的浮点数
                String[] floatStrings = keyValue[1].split(",");
                for (String floatString : floatStrings) {
                    values.add(Double.parseDouble(floatString.trim()));
                }
                // 将键值对添加到字典
                yoloMapCar.put(key, values);
            }
        }
        return yoloMapCar;
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
        super.onViewCreated(view, savedInstanceState);
        FindViewById(view);
        /*******多车spinner选择模块**********/
        mapCar = new HashMap<String, ROSBridgeClient>() {
            {
                put("车1", MainActivity.clientCar1);  // 设置名称为车1的原因是前面是HashMap顺序不定，保证Car的position后续是第一个
                put("车2", MainActivity.clientCar2);
                put("车3", MainActivity.clientCar3);
            }
        };
        List<String> keys = new ArrayList<>(mapCar.keySet());
        // 配置adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, keys);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.cars_spinner_jwd);
        // adapter放入spinner
        spinner.setAdapter(spinnerAdapter);
        clientCar = mapCar.get("车1");    // 需要预先设置一下
        clientCar.connect();
        // 下拉菜单项选择事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {    // 此处的view1是有问题的，要使用全局的view才能正常显示，做区别写成了view1
                Log.i("RecongnitionFragment","enter onItemSelected");
                String selectedKey = keys.get(position);// position为选择第几个
                clientCar = mapCar.get(selectedKey);
                clientCar.connect();
                //获取视频流
                if (selectedKey.equals("车1")) {
                    videoPlayer.GetVideo(view, MainActivity.car1IpEdit.getText().toString());
                }
                else if (selectedKey.equals("车2")) {
                    videoPlayer.GetVideo(view, MainActivity.car2IpEdit.getText().toString());
                }
                else if (selectedKey.equals("车3")) {
                    videoPlayer.GetVideo(view, MainActivity.car3IpEdit.getText().toString());
                }
                // 由于需要断连，故专门建立一个client
                Context that = getContext();
                getImgBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        // 连接
                        clientCar.connect();

                        // 订阅图像
                        com.example.myapplication.Topic<YoloImage> imgTopicCar = new com.example.myapplication.Topic<YoloImage>("/yolo_image", YoloImage.class, clientCar);
                        imgTopicCar.subscribe();
                        YoloImage imgCar;

                        // 订阅list
                        com.example.myapplication.Topic<YoloList> listTopicCar = new com.example.myapplication.Topic<YoloList>("/yolo_list", YoloList.class, clientCar);
                        listTopicCar.subscribe();
                        YoloList listCar;

                        try {
                            // 获取图像
                            imgCar = imgTopicCar.take();
                            Bitmap bit_map_car = DecodeImage(imgCar);
                            imageView.setImageBitmap(bit_map_car);


                            // 获取列表
                            listCar = listTopicCar.take();
                            Map<String, List<Double>> map_car = DecodeDictionary(listCar);
                            DictionaryAdapter dic_adapter_car = new DictionaryAdapter(that, map_car);
                            listView.setAdapter(dic_adapter_car);
                            // 关闭连接
                            clientCar.disconnect();

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 无点击时默认选择car1
                Log.i("RecongnitionFragment","enter onNothingSelected");
                clientCar = mapCar.get("车1");
                clientCar.connect();
            }
        });
        Log.i("RecongnitionFragment","after spinner");
        /*******多车spinner选择模块结束**********/
    }
}