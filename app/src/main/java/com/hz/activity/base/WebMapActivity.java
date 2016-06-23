package com.hz.activity.base;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hz.MainApplication;
import com.hz.R;
import com.hz.activity.CrossingLineActivity;
import com.hz.activity.KuaYueXian;
import com.hz.activity.MainActivity;
import com.hz.activity.MyList;
import com.hz.activity.PointEditActivity;
import com.hz.activity.YeUtils;
import com.hz.adapter.CommonAdapter;
import com.hz.adapter.GalleryAdapter;
import com.hz.adapter.ViewHolder;
import com.hz.util.BitmapCompressUtils;
import com.hz.util.LocationUtils;
import com.hz.util.SharedPreferencesUtils;
import com.hz.view.KuaYueLine;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class WebMapActivity extends CordovaActivity {

    private static final String TAGS = "tags";
    public List<String> tags = new ArrayList<>();
    private static final int CAPTURE_PHOTO = 1;
    private static final int COLUMN_PHOTO = 2;
    public static final String POINT_TYPE = "point_type";
    private SystemWebView webView;
    private boolean flag_firstDo= false;

    private double[] location;

    private LocationUtils locationUtils;
    private ImageView iv_center;

    private RadioGroup rg_kuaYueXian;
    private AlertDialog dialog_photo;
    private Uri imageUri;

    private DbUtils dbUtils;
    private EditText et_type_kyx;
    private EditText et_height_kyx;
    private EditText et_comment_kyx;
    protected AlertDialog dialog;
    private View layout_kyx;

    private GridView gridView;
    private String kyxTag;
    private StringBuilder builder1;
    private Bitmap bitmapSelect;
    private String x1;
    private String y1;
    private String x2;
    private String y2;
    private AlertDialog.Builder builderKYX;
    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            handle(msg);
        }
    };
    public static final String GEO_JSON = "geo_json";
    private Context context;
    private JsInteration jsInteration;
    private LinearLayout ll_point_select;

    /**
     * the crossing-line's list
     */
    private MyList list_kyx = new MyList();

    private CommonAdapter cross_line_adapter;
    private boolean flag_dont_shut_down = false;
    private EditText et_name_kyx;
    private KuaYueXian kuaYueXian;
    private KuaYueXian newKYX;
    private LinearLayout ll_connect_line;
    public boolean flag_connect_line = false;
    private String kyxId;//跨越线ID
    private boolean flag_kyx_end_point_can_click = false;
    private String kyxX1, kyxY1, kyxX2, kyxY2;
    private KuaYueXian kuaYueXian_fromDb = null;//接受从数据库中查找到的对应跨越线

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_map);
        super.init();
        doInit();
        jsInteration = new JsInteration();
        locationUtils = new LocationUtils(this);
        tags.addAll(YeUtils.stringToList((String) SharedPreferencesUtils.getParam(context, TAGS, "")));
        webView.addJavascriptInterface(new JsInteration(), "control");
         loadUrl("file:///android_asset/index.html");
    }



    public void editKYX(View v){
        Intent intent = new Intent(WebMapActivity.this, CrossingLineActivity.class);
        intent.putExtra(CrossingLineActivity.CROSSING_LINE, (Parcelable) list_kyx);
        context.startActivity(intent);
    }

    @Override
    protected CordovaWebView makeWebView() {
        webView = (SystemWebView)findViewById(R.id.web_map);
        return new CordovaWebViewImpl(new SystemWebViewEngine(webView));
    }

    @Override
    protected void createViews() {
        if (preferences.contains("BackgroundColor")) {
            int backgroundColor = preferences.getInteger("BackgroundColor", Color.BLACK);
            // Background of activity:
            appView.getView().setBackgroundColor(backgroundColor);
        }

        appView.getView().requestFocusFromTouch();
    }

    
    /*
    重新定位
     */
    public void resetLocation(View v){
        webView.loadUrl("javascript:reset()");
    }

    private void handle(Message msg) {
        switch (msg.what){
            case 0x123:

                dialog.show();

                String id = (String) msg.obj;
                Log.d("tag66", "poly线的id"+id);
                try {
                    kuaYueXian_fromDb = dbUtils.findFirst(KuaYueXian.class, WhereBuilder.b("id", "=", id));
                } catch (DbException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "查找发生了异常", Toast.LENGTH_SHORT).show();
                }
                if(kuaYueXian_fromDb != null){
                    et_name_kyx.setText(kuaYueXian_fromDb.getLineName());
                    et_type_kyx.setText(kuaYueXian_fromDb.getLineType());
                    et_height_kyx.setText(kuaYueXian_fromDb.getLineHeight());
                    et_comment_kyx.setText(kuaYueXian_fromDb.getLineComment());
                    if(kuaYueXian_fromDb.getBitmaps() != null){
                        GalleryAdapter.list.addAll(kuaYueXian_fromDb.getBitmaps()) ;
                    }
                    GalleryAdapter.getAdapter(context, GalleryAdapter.list);
                    flag_dont_shut_down = true;//开启标签，点击确定后更新相关数据
                }else{
                    Toast.makeText(context, "查询不到", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1234:
                String str = (String) msg.obj;
                jsInteration.saveGeoJson(str);
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                //webView.loadUrl("doJSON('" + str + "')");
                break;
            case 0x125:
                String json = (String) msg.obj;
                webView.loadUrl("javascript:doJSON('" + json + "')");

                break;
            case 0x124:
                newKYX = (KuaYueXian) msg.obj;
                dialog.show();
                break;
        }
    }

    public void refresh(View v){
        Toast.makeText(context, "刷新了", Toast.LENGTH_SHORT).show();
        String json = jsInteration.getGeoJson();
        webView.loadUrl("javascript:doJSON('" + json + "')");
    }

    //+++++跨越线++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

    /**
     * 绘制跨越线
     */
    public void drawKYX(View v){
        iv_center.setVisibility(View.VISIBLE);
        rg_kuaYueXian.setVisibility(View.VISIBLE);
    }

    /**
     * 设置跨越线起点
     * @param v
     */
    public void startKYX_Point(View v){
        //生成一个随机ID，这个ID之后会设置给结束点和线段，来将跨越线的三部分关联性地组合起来
        kyxId = UUID.randomUUID().toString();
        webView.loadUrl("javascript:markKYX_Start('" + kyxId + "')");//标记跨越线开始点
        Log.d("tag22", "调用了1");
        v.setClickable(false);//不允许连续点击
        flag_kyx_end_point_can_click = true;//在确定开始点之后，再允许按下结束点按钮
    }

    /**
     * 设置跨越线结点
     * @param v
     */
    public void endKYX_Point(View v){
        if(flag_kyx_end_point_can_click){
            v.setClickable(true);
        }else{
            v.setClickable(false);
        }
        webView.loadUrl("javascript:markKYX_End('" + kyxId + "')");//标记跨越线结束点
        dialog.show();//显示跨越线属性编辑对话框
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(dialog_photo != null)
            dialog_photo.dismiss();
        if(resultCode != RESULT_OK){
            return;
        }
        if (requestCode == CAPTURE_PHOTO) {//拍照获取到图片
            if(imageUri != null){
                bitmapSelect = BitmapCompressUtils.decodeUriAsBitmap(this, imageUri);
            }
        } else if (requestCode == COLUMN_PHOTO ) {//相册中获取图片
            Uri uri = data.getData();
            if(uri == null){
               return;
            }else{
                ContentResolver cr = this.getContentResolver();
                try {
                    bitmapSelect = BitmapFactory.decodeStream(cr.openInputStream(uri));
                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(),e);
                }
            }
        } else if(requestCode == 3){
            iv_center.setVisibility(View.INVISIBLE);
            ll_point_select.setVisibility(View.INVISIBLE);
            boolean isCancel = data.getBooleanExtra(PointEditActivity.CANCEL, false);
            if(isCancel){
                return;
            }
            int type = data.getIntExtra(PointEditActivity.TYPE, 1);
            String id = data.getStringExtra(PointEditActivity.ID);
            boolean isDelete = data.getBooleanExtra(PointEditActivity.DELETE, false);
            if(isDelete){
                webView.loadUrl("javascript:deleteLiGan('" + type + "','" + id + "')");
                Log.d("tag7", "执行了webView.loadUrl(\"javascript:deleteLiGan('\" + type + \"','\" + id + \"')\");");
            } else {
                webView.loadUrl("javascript:addLiGan('" + type + "','" + id + "')");
            }
        }
        if(bitmapSelect != null){//将图片流解析为bitmap后加入集合并刷新数据
            GalleryAdapter.list.add(bitmapSelect);
            GalleryAdapter.get().notifyDataSetChanged();
        }
    }
    
    /*
    提供给JS调用的方法
     */
    private class JsInteration {

        public KuaYueXian correspondingKYX;

        /**
         * 获取所有跨越线的信息字符串，每一天跨越线的信息以#分割
         */
        @JavascriptInterface
        public String getKyx(){
            String str = (String) SharedPreferencesUtils.getParam(context, "kyx_data", "");
            Log.d("tag88", "最初的数据是"+str);
            String[] strArr = YeUtils.stringToArray(str);
            JSONArray jsonArray = JSONArray.fromObject(strArr);
            return jsonArray.toString();
        }

        /**
         * 保存所有跨越线的数据（ID，起点和结束点的坐标）
         * @param data 最新产生的一条跨越线的数据
         * @return 整体数据，每条跨越线的数据均以#来进行分割
         */
        @JavascriptInterface
        public void saveAllKyx(String data) {
            JSONArray jsonArray = JSONArray.fromObject(data);
            String[] arr = new String[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                arr[i] = jsonArray.get(i).toString();
            }
            String finalData = YeUtils.listToString(Arrays.asList(arr));
            Log.d("tag88", "最终的到的结果是"+finalData);
            SharedPreferencesUtils.setParam(context, "kyx_data", finalData);//保存全部数据
        }
        @JavascriptInterface
        public void saveKyx(String data){//保存新加的数据
            String preData = (String) SharedPreferencesUtils.getParam(context, "kyx_data", "");

            if(preData != null){//假如之前已有跨越线的数据，则不能将其替换掉，应做如下处理
                List<String> list = YeUtils.stringToList(preData);//将之前的数据转换成集合，每个集合元素代表了一条跨越线的数据
                String[] arr = new String[list.size()+1];
                for (int i = 0; i < arr.length; i++) {
                    if(i != (arr.length-1)){
                        arr[i] = list.get(i);
                    } else {
                        arr[i] = data;
                    }
                }
                List<String> newList = Arrays.asList(arr);
                String finalData = YeUtils.listToString(newList);//转换为最终需要保存的字符串
                SharedPreferencesUtils.setParam(context, "kyx_data", finalData);//保存
            } else {//如果之前没有保存任何数据，则将该条数据直接保存即可
                SharedPreferencesUtils.setParam(context, "kyx_data", data);
            }
        }

        @JavascriptInterface
        public String getPolylinesConnectData(){
            String data = getLastPolylinesData();//获取到保存在本地的最终数据
            String[] strArr = YeUtils.stringToArray(data);
            JSONArray jsonArray = JSONArray.fromObject(strArr);//将数组转换为JSON数组
            return jsonArray.toString();
        }

        @JavascriptInterface
        public String[] getPolylines(){
            String str = (String) SharedPreferencesUtils.getParam(context, TAGS, "");
            Log.d("tag11", "取出了str为"+str);
            List<String> list = YeUtils.stringToList(str);
            String[] arr = new String[list.size()];
            Log.d("tag11", "数组的长度是"+list.size());
            Log.d("tag11", "数组的第一个值是"+list.get(0));
           for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i);
            }
            return  arr;
        }

        @JavascriptInterface
        public String getLastPolylinesData(){
            String data = (String) SharedPreferencesUtils.getParam(context, "last_polylines", "");
            return data;
        }

        @JavascriptInterface
        public String getUUID(){
            return UUID.randomUUID().toString();
        }

        @JavascriptInterface
        public String getPolylinesData(){
            String data = (String) SharedPreferencesUtils.getParam(context, "polylines", "");
            return data;
        }

        @JavascriptInterface
        public void saveLastPolylinesData(String data){
            SharedPreferencesUtils.setParam(context, "last_polylines",  data);
        }

        @JavascriptInterface
        public void saveLastPolylinesData(String tag, String data){
            SharedPreferencesUtils.setParam(context, tag,  data);
        }

        @JavascriptInterface
        public String getLastPolylinesData(String tag){
            String data = (String) SharedPreferencesUtils.getParam(context, tag, "");
            return data;
        }

        @JavascriptInterface
        public void savePolylinesData(String data){
            SharedPreferencesUtils.setParam(context, "polylines",  data);
        }

        @JavascriptInterface
        public boolean isFlag_connect_line() {
            return flag_connect_line;
        }

        @JavascriptInterface
        public void handler(int what, String str){
            Message message = Message.obtain();
            message.obj = str;
            message.what = what;
            handler.sendMessage(message);
        }

        /**
         * 编辑跨越线
         */
        @JavascriptInterface
        public void toEditKYX(String lat, String lng){
            correspondingKYX = YeUtils.getCorrespondingCrossingLine(context, dbUtils, lat, lng);
            if(correspondingKYX == null){
                Toast.makeText(context, "空值", Toast.LENGTH_SHORT).show();
                return;
            }
            et_comment_kyx.setText(correspondingKYX.getLineComment());
            et_type_kyx.setText(correspondingKYX.getLineType());
            et_height_kyx.setText(correspondingKYX.getLineHeight());
            Log.d("Height", "2  "+correspondingKYX.getLineHeight());
            et_name_kyx.setText(correspondingKYX.getLineName());
            flag_dont_shut_down = true;
            List<Bitmap> list = correspondingKYX.getBitmaps();
            CommonAdapter commonAdapter = GalleryAdapter.getAdapter(context, list);
            gridView.setAdapter(commonAdapter);
            flag_dont_shut_down = true;
            Message message = Message.obtain();
            message.obj = correspondingKYX;
            message.what = 0x124;
            handler.sendMessage(message);

        }

        /**
         * get the crossing-line's latlng and create it,
         * then add it to the list, so as to provide data for the adapter.
         */
        @JavascriptInterface
        public void addKYX(String lat1, String lng1, String lat2, String lng2){
            KuaYueXian kuaYueXian1 = new KuaYueXian(lat1, lng1, lat2, lng2);
            list_kyx.add(kuaYueXian1);
        }

        @JavascriptInterface
        public void addKYXAddData(double lat1, double lng1, double lat2, double lng2){
            kyxX1 = Double.toString(lat1) ;
            kyxY1 = Double.toString(lng1);
            kyxX2 = Double.toString(lat2);
            kyxY2 = Double.toString(lng2);
            Log.d("tag100", kyxX1 + "','" + kyxY1 + "','" + kyxX2 + "','" + kyxY2 );
            kuaYueXian = new KuaYueXian(kyxX1, kyxY1, kyxX2, kyxY2);
        }

        @JavascriptInterface
        public double getX(){
            getLocation();
            return location[0];
        }

        @JavascriptInterface
        public double getY(){
            getLocation();
            return location[1];
        }

        private void getLocation() {
            locationUtils.getCurrentLocationMessage();
            locationUtils.updateData();
            location = locationUtils.getLocation();
        }

        @JavascriptInterface
        public void toast(){

        }

        @JavascriptInterface
        public void toast(String str){
            Toast.makeText(WebMapActivity.this, str, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void log(String str){
            Log.d("js", "from js: "+str);
        }

        @JavascriptInterface
        public void logd(String tag, String str){
            Log.d(tag, "from js: "+str);
        }

        /**
         * 将marker的坐标信息保存到数据库
         * @param x
         * @param key
         */
        @JavascriptInterface
        public void writePoint(String x, String key){
            SharedPreferencesUtils.setParam(WebMapActivity.this, key, x);
        }

        @JavascriptInterface
        public void saveGeoJson(String jsonStr){

            SharedPreferencesUtils.setParam(WebMapActivity.this, GEO_JSON, jsonStr);
        }

        @JavascriptInterface
        public void saveGeoJson(String tag, String jsonStr){
            SharedPreferencesUtils.setParam(WebMapActivity.this, tag, jsonStr);
        }

        @JavascriptInterface
        public String getGeoJson(String tag){
            String geoJson = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, tag, "nullllllllll");
            return geoJson;
        }

        @JavascriptInterface
        public String getGeoJson(){
            String geoJson = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, GEO_JSON, "nullllllllll");
            return geoJson;
        }

        /**
         * 编辑跨越线
         * @param tag polyline的id，用于请求数据库获取数据
         */
        @JavascriptInterface
        public void editKuaYueLine(String id){
            Log.d("tag66", "传过来的id是"+id);
            Message message = Message.obtain();
            message.obj = id;
            message.what = 0x123;
            handler.sendMessage(message);
        }

        @JavascriptInterface
        public void editPolyline( String id){
                Intent intent = new Intent(WebMapActivity.this, PointEditActivity.class);
                intent.putExtra(POINT_TYPE, 8);
                intent.putExtra(PointEditActivity.ID, id);
                startActivityForResult(intent, 3);
        }

        @JavascriptInterface
        public void editLiGan(int type, String id){
            if(!flag_connect_line){
                Intent intent = new Intent(WebMapActivity.this, PointEditActivity.class);
                intent.putExtra(POINT_TYPE, type);
                intent.putExtra(PointEditActivity.ID, id);
                startActivityForResult(intent, 3);
            } else {
                webView.loadUrl("javascript:colorPoint('" + type + "','" + id + "')");
            }
        }
    }

    private void doInit() {
        context = this;
        initView();
        initData();
        initListener();
    }

    private void initView() {
        iv_center = (ImageView) findViewById(R.id.iv_center);
        rg_kuaYueXian = (RadioGroup) findViewById(R.id.rg_kuayuexian);
        ll_point_select = (LinearLayout) findViewById(R.id.point_select);
        ll_connect_line = (LinearLayout)findViewById(R.id.ll_connect);
    }

    private void initData(){
        dbUtils = ((MainApplication)getApplication()).getDbUtils();
        doKYX_Data();//初始化跨越县数据
    }

    private void doKYX_Data() {
         /*
        获取保存的开始点和结束点的位置信息
         */
        /*
        设置跨越线对话框的Builder
         */
        builderKYX = new AlertDialog.Builder(WebMapActivity.this);
        builderKYX.setTitle("跨越线");
        layout_kyx = LayoutInflater.from(WebMapActivity.this).inflate(R.layout.kyx_edit, null);
        builderKYX.setView(layout_kyx);
        gridView = (GridView) layout_kyx.findViewById(R.id.gallery);
        et_type_kyx = (EditText) layout_kyx.findViewById(R.id.kyx_type);
        et_height_kyx = (EditText) layout_kyx.findViewById(R.id.et_line_height_kyx);
        et_comment_kyx = (EditText)layout_kyx.findViewById(R.id.et_line_comment_kyx);
        et_name_kyx = (EditText)layout_kyx.findViewById(R.id.et_line_name_kyx);
        CommonAdapter commonAdapter = GalleryAdapter.getAdapter(this);
        gridView.setAdapter(commonAdapter);
    }

    private void initListener(){
        doKYX_Listener();//初始化跨越线监听器
    }

    /**
     * 确定跨越线
     */
    private void doKYX_Listener() {

        builderKYX.setNegativeButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(flag_dont_shut_down){
                   if(kuaYueXian_fromDb != null){
                        try {
                            String id = kuaYueXian_fromDb.getId();
                            dbUtils.deleteById(KuaYueXian.class, id);
                            Toast.makeText(WebMapActivity.this, "已经在数据库中删除", Toast.LENGTH_SHORT).show();
                            String json = jsInteration.getKyx();
                            Log.d("tag88", "json是"+json);
                            webView.loadUrl("javascript:deleteJSON('"+id+"')");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else  {
                        Toast.makeText(WebMapActivity.this, "是控制", Toast.LENGTH_SHORT).show();
                    }
                    flag_dont_shut_down = false;
                    return;
                }
                rg_kuaYueXian.setVisibility(View.INVISIBLE);
                ((RadioButton)rg_kuaYueXian.getChildAt(2)).setChecked(false);
                ((RadioButton)rg_kuaYueXian.getChildAt(1)).setClickable(true);
                iv_center.setVisibility(View.INVISIBLE);
            }
        });

        builderKYX.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public String newX2;
            public String newX1;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(flag_dont_shut_down){
                    if(kuaYueXian_fromDb != null){
                        kuaYueXian_fromDb.setLineName(et_name_kyx.getText().toString());
                        kuaYueXian_fromDb.setLineType(et_type_kyx.getText().toString());
                        kuaYueXian_fromDb.setLineComment(et_comment_kyx.getText().toString());
                        kuaYueXian_fromDb.setLineHeight(et_height_kyx.getText().toString());
                        kuaYueXian_fromDb.setBitmaps(GalleryAdapter.list);
                        try {
                            dbUtils.saveOrUpdate(kuaYueXian_fromDb);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    } else  {
                        Toast.makeText(WebMapActivity.this, "是控制", Toast.LENGTH_SHORT).show();
                    }
                    flag_dont_shut_down = false;
                    return;
                }
                if(!kyxX1.equals("")&&!kyxY1.equals("")&&!kyxX2.equals("")&&!kyxY2.equals("")) {
                    Log.d("pdd", x1+"+++++++++++++++++++++++++++++++++++++++++++++++++"+x2);
                    webView.loadUrl("javascript:connectKYX('" + kyxX1 + "','" + kyxY1 + "','" + kyxX2 + "','" + kyxY2 + "','" + kyxId + "')");
                    kuaYueXian.setId(kyxId);
                    Log.d("tag66", "保存下来的ID是"+kyxId);
                    kuaYueXian.setLineName(et_name_kyx.getText().toString());
                    kuaYueXian.setLineType(et_type_kyx.getText().toString());
                    kuaYueXian.setLineComment(et_comment_kyx.getText().toString());
                    kuaYueXian.setLineHeight(et_height_kyx.getText().toString());
                    Log.d("Height", "1  "+et_height_kyx.getText().toString());
                    kuaYueXian.setBitmaps(GalleryAdapter.list);
                    list_kyx.add(kuaYueXian);

                    try {
                        dbUtils.saveOrUpdate(kuaYueXian);

                    } catch (DbException e) {
                        e.printStackTrace();
                        Toast.makeText(WebMapActivity.this, "发生了异常"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                rg_kuaYueXian.setVisibility(View.INVISIBLE);
                ((RadioButton)rg_kuaYueXian.getChildAt(2)).setChecked(false);
                ((RadioButton)rg_kuaYueXian.getChildAt(1)).setClickable(true);
                iv_center.setVisibility(View.INVISIBLE);
               /* String json = jsInteration.getGeoJson();
                Message message = Message.obtain();
                message.what = 0x125;
                message.obj = json;
                handler.sendMessage(message);*/
                //webView.loadUrl("javascript:doJSON('" + json + "')");
            }
        });
        dialog = builderKYX.create();

        et_type_kyx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(WebMapActivity.this);
                RadioGroup radioGroup = (RadioGroup) LayoutInflater.from(WebMapActivity.this).inflate(R.layout.kyx_type_select, null);
                dialog1.setView(radioGroup);
                final AlertDialog dialog2 = dialog1.create();
                dialog2.show();
                radioGroup.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                        et_type_kyx.setText("10KV线");
                    }
                });
                radioGroup.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                        et_type_kyx.setText("通信线");
                    }
                });
                radioGroup.getChildAt(4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                        et_type_kyx.setText("低压线");
                    }
                });
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(WebMapActivity.this);
                    builder.setTitle("选择获取方式");
                    View layout = LayoutInflater.from(WebMapActivity.this).inflate(R.layout.select_photo, null);
                    builder.setView(layout);
                    dialog_photo = builder.create();
                    View  select_view = layout;
                    TextView tv1 = (TextView) select_view.findViewById(R.id.method1);
                    TextView tv2 = (TextView) select_view.findViewById(R.id.method2);
                    tv1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String state = Environment.getExternalStorageState();
                            if (state.equals(Environment.MEDIA_MOUNTED)) {
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "picture.jpg");
                                imageUri = Uri.fromFile(photo);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent, 1);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    tv2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");//相片类型
                            startActivityForResult(intent, 2);
                        }
                    });
                    dialog_photo.show();
                }
            }
        });
    }



    //*******************************************************************************************************************************
    public void C(View v){
        iv_center.setVisibility(View.VISIBLE);
        ll_point_select.setVisibility(View.VISIBLE);
    }

    public void line(View v){
        ll_connect_line.setVisibility(View.VISIBLE);
        flag_connect_line = true;
    }

    public void confimConnectLine(View v){
        Toast.makeText(context, "确定了连线", Toast.LENGTH_SHORT).show();
        flag_connect_line = false;//标记设为false，再次点击时为编辑删除
        ll_connect_line.setVisibility(View.GONE);//隐藏确定取消按钮
        //将坐标数组转换为字符串保存到本地
        String lastData = jsInteration.getPolylinesData();//获取用户此次操作所保存的全部数据（点的坐标）
        Log.d("tag18", "这次保存的连线数据是"+lastData);
        /**
         * 将数据保存在最终的地方,但是上次保存的记录又会被覆盖，如果将这次与上次的数据混合在一起，则会产生多余的连线，
         * 因此必须想办法将前后的数据都进行保存，但是不产生干扰。
         */
        String preData = jsInteration.getLastPolylinesData();//取出上次保存的连线数据
        Log.d("tag18", "之前保存的连线数据是"+preData);
        List<String> list = YeUtils.stringToList(preData);//将该数据分割为数组，每个元素代表了一组连线数据
        //Log.d("tag18", list.size()+" "+list.get(0));
        String[] arr = new String[list.size()+1];
        String uuid = UUID.randomUUID().toString();
        for (int i = 0; i < arr.length; i++) {
            if(i != (arr.length-1)){
                arr[i] = "{id:"+uuid+",points:"+list.get(i)+"}";
                Log.d("want", "关键位1 "+arr[i]);
            } else {
                arr[i] = "{id:"+uuid+",points:"+lastData+"}";
                Log.d("want", "关键位2 "+arr[i]);
            }
        }
        List<String> newList = Arrays.asList(arr);
        String newData = YeUtils.listToString(newList);//将该数组转换为字符串
        Log.d("tag18", "最终保存的连线数据是"+newData);
        jsInteration.saveLastPolylinesData(newData);//保存最终生成的数据
        /*
        然而最终生成的数据并不能直接在JS中使用，接下来在JS接口中写方法
         */

        //Log.d("tag11", "将数据根据tag保存进了本地");
        //tags.add(tag);
        //SharedPreferencesUtils.setParam(context, TAGS, YeUtils.listToString(tags));
        //Log.d("tag11", "如今tags集合在本地保存为"+YeUtils.listToString(tags));
        //根据坐标数组来绘制线条
        //String[] arr = jsInteration.getPolylines();
        //JSONArray jsonArray = JSONArray.fromObject(arr);
        //Log.d("tag11", "jsonArray是" + jsonArray);
        webView.loadUrl("javascript:connectLine('"+true+"')");//将此次选择的点全部进行连线
        //将图标颜色变为默认
        jsInteration.savePolylinesData("");//将临时的数据存储清空
        webView.loadUrl("javascript:clearPolylines()");//清空临时数组
    }

    public void cancelConnectLine(View v){
        flag_connect_line = false;
        ll_connect_line.setVisibility(View.GONE);
        //将图标颜色变为默认

        //将临时的数据存储清空
        jsInteration.savePolylinesData("");
        webView.loadUrl("javascript:clearPolylines()");//清空临时数组
    }
    /**
     * 立杆
     */
    public void ligan(View v){
        Intent intent = new Intent(WebMapActivity.this, PointEditActivity.class);
        intent.putExtra(POINT_TYPE,  1);
        startActivityForResult(intent, 3);
    }

    /**
     *  变压箱
     */
    public void bianyabox(View v){
        Intent intent = new Intent(WebMapActivity.this, PointEditActivity.class);
        intent.putExtra(POINT_TYPE,  2);
        startActivityForResult(intent, 3);
    }

    /**
     *  户表
     */
    public void hubiao(View v){
        Intent intent = new Intent(WebMapActivity.this, PointEditActivity.class);
        intent.putExtra(POINT_TYPE,  3);
        startActivityForResult(intent, 3);
    }

    /**
     *  电缆井
     */
    public void dianlanjing(View v){
        Intent intent = new Intent(WebMapActivity.this, PointEditActivity.class);
        intent.putExtra(POINT_TYPE,  4);
        startActivityForResult(intent, 3);
    }

    /**
     *  箱式开关站
     */
    public void boxedSwitchStation(View v){
        Intent intent = new Intent(WebMapActivity.this, PointEditActivity.class);
        intent.putExtra(POINT_TYPE,  5);
        startActivityForResult(intent, 3);
    }

    /**
     *  开闭所
     */
    public void kaibisuo(View v){
        Intent intent = new Intent(WebMapActivity.this, PointEditActivity.class);
        intent.putExtra(POINT_TYPE,  6);
        startActivityForResult(intent, 3);
    }

    /**
     *  箱式变压站
     */
    public void boxedBianyaStation(View v){
        Intent intent = new Intent(WebMapActivity.this, PointEditActivity.class);
        intent.putExtra(POINT_TYPE,  7);
        startActivityForResult(intent, 3);
    }



}
