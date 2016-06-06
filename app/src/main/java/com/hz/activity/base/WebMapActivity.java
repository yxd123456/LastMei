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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hz.MainApplication;
import com.hz.R;
import com.hz.activity.CrossingLineActivity;
import com.hz.activity.KuaYueXian;
import com.hz.activity.MainActivity;
import com.hz.activity.MyList;
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

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WebMapActivity extends CordovaActivity {

    private static final int CAPTURE_PHOTO = 1;
    private static final int COLUMN_PHOTO = 2;
    private SystemWebView webView;

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
    public   KuaYueXian kuaYueXian;

    /**
     * the crossing-line's list
     */
    private MyList list_kyx = new MyList();

    private CommonAdapter cross_line_adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_map);
        super.init();
        doInit();

        locationUtils = new LocationUtils(this);

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
                if(dialog == null){
                    return;
                }
                KuaYueLine kuaYueLine = null;
                Log.d("last", "取得的TAG是"+kyxTag);
                try {
                    kuaYueLine = dbUtils.findFirst(KuaYueLine.class, WhereBuilder.b("tag1", "=",
                            kyxTag.substring(kyxTag.length()-5, kyxTag.length()-1)));
                    Log.d("weixin", "2  "+(kuaYueLine == null));
                    if(kuaYueLine == null){
                        kuaYueLine = dbUtils.findFirst(KuaYueLine.class, WhereBuilder.b("tag2", "=",
                                kyxTag.substring(kyxTag.length()-5, kyxTag.length()-1)));
                        Log.d("weixin", "3  "+(kuaYueLine == null));
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                    Log.d("weixin", "err"+e.getMessage());
                }
                dialog.show();
                break;
        }
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
        webView.loadUrl("javascript:markKYX_Start()");
        v.setClickable(false);
    }

    /**
     * 设置跨越线结点
     * @param v
     */
    public void endKYX_Point(View v){
        webView.loadUrl("javascript:markKYX_End()");
        dialog.show();
    }


    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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



        /*public KuaYueXian getKuaYueXian() {
            return kuaYueXian;
        }

        public void setKuaYueXian(KuaYueXian kuaYueXian) {
            this.kuaYueXian = kuaYueXian;
        }*/

        @JavascriptInterface
        public void getListInformation(){
            Log.d("kut", "执行了"+list_kyx.size());

        }

        @JavascriptInterface
        public void goTOCLA(){
            Intent intent = new Intent(WebMapActivity.this, CrossingLineActivity.class);
            intent.putExtra(CrossingLineActivity.CROSSING_LINE, (Serializable) list_kyx);
            context.startActivity(intent);
        }

        /**
         * get the crossing-line's latlng and create it,
         * then add it to the list, so as to provide data for the adapter.
         * @param lat1
         * @param lng1
         * @param lat2
         * @param lng2
         */
        @JavascriptInterface
        public void addKYX(String lat1, String lng1, String lat2, String lng2){
            KuaYueXian kuaYueXian1 = new KuaYueXian(lat1, lng1, lat2, lng2);
            list_kyx.add(kuaYueXian1);
        }

        @JavascriptInterface
        public void addKYXAddData(String lat1, String lng1, String lat2, String lng2){
            Log.d("restoration", lat1+"\n"+lng1+"\n"+lat2+"\n"+lng2);

            kuaYueXian = new KuaYueXian(lat1, lng1, lat2, lng2);
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
        public String getGeoJson(){
            String geoJson = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, GEO_JSON, "nullllllllll");
            return geoJson;
        }

        /**
         * 编辑跨越线
         * @param tag marker的纬度，用于请求数据库获取数据
         */
        @JavascriptInterface
        public void editKuaYueLine(String tag){
            kyxTag = tag;
            handler.sendEmptyMessage(0x123);
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
        et_height_kyx = (EditText) layout_kyx.findViewById(R.id.kyx_type);
        et_comment_kyx = (EditText)layout_kyx.findViewById(R.id.et_line_comment_kyx);
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
        builderKYX.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public String newX2;
            public String newX1;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                x1 = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, "sPLat", "");
                y1 = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, "sPLong", "");
                x2 = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, "ePLat", "");
                y2 = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, "ePLong", "");
                Log.d("pdd", "&*("+x1+"   "+x2);

                if(!x1.equals("")&&!y1.equals("")&&!x2.equals("")&&!y2.equals("")) {
                    Log.d("pdd", x1+"+++++++++++++++++++++++++++++++++++++++++++++++++"+x2);
                    webView.loadUrl("javascript:test('" + x1 + "','" + y1 + "','" + x2 + "','" + y2 + "')");

                    kuaYueXian.setLineType(et_type_kyx.getText().toString());
                    kuaYueXian.setLineComment(et_comment_kyx.getText().toString());
                    kuaYueXian.setLineHeight(et_height_kyx.getText().toString());
                    kuaYueXian.setBitmaps(GalleryAdapter.list);
                    list_kyx.add(kuaYueXian);

                    try {
                        Toast.makeText(WebMapActivity.this, "空值预判" + (kuaYueXian == null), Toast.LENGTH_SHORT).show();
                        dbUtils.save(kuaYueXian);
                        Log.d("restoration", "后来呀"+kuaYueXian.getLat1()+" "+kuaYueXian.getLng1());
                        List<KuaYueXian> list = dbUtils.findAll(Selector.from(KuaYueXian.class));
                        for (int i = 0; i < list.size(); i++) {
                            Log.e("restoration", list.size()+"唉"+list.get(i).getLat1());
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                        Toast.makeText(WebMapActivity.this, "发生了异常"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                rg_kuaYueXian.setVisibility(View.INVISIBLE);
                ((RadioButton)rg_kuaYueXian.getChildAt(2)).setChecked(false);
                ((RadioButton)rg_kuaYueXian.getChildAt(1)).setClickable(true);
                iv_center.setVisibility(View.INVISIBLE);


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


}
