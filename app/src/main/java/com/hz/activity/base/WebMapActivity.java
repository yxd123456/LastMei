package com.hz.activity.base;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.hz.activity.LineAttributeActivity;
import com.hz.adapter.CommonAdapter;
import com.hz.adapter.GalleryAdapter;
import com.hz.adapter.ViewHolder;
import com.hz.common.Constans;
import com.hz.fragment.ProjectListFragment;
import com.hz.greendao.dao.MapLineEntity;
import com.hz.greendao.dao.ProjectEntity;
import com.hz.helper.DataBaseManagerHelper;
import com.hz.helper.SharedPreferencesHelper;
import com.hz.util.BitmapCompressUtils;
import com.hz.util.LocationUtils;
import com.hz.util.SharedPreferencesUtils;
import com.hz.util.okhttp_extend.FileUtil;
import com.hz.view.DialogUtils;
import com.hz.view.KuaYueLine;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class WebMapActivity extends CordovaActivity {

    private SystemWebView webView;

    private double[] location;

    private LocationUtils locationUtils;
    private ImageView iv_center;

    private RadioGroup kuaYueXian;
    private AlertDialog dialog_photo;
    private Uri imageUri;

    private DbUtils dbUtils;
    private EditText et_type_kyx;
    private EditText et_height_kyx;
    private EditText et_comment_kyx;
    protected AlertDialog dialog;
    protected Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x123:
                    if(dialog == null){
                        dialog = DialogUtils.getDialog(WebMapActivity.this, "跨越线", R.layout.kyx_edit, new DialogUtils.DoBuilderListener() {
                            @Override
                            public void doSomething(AlertDialog.Builder builder) {
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String x1 = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, "sPLat", "");
                                        String y1 = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, "sPLong", "");
                                        String x2 = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, "ePLat", "");
                                        String y2 = (String) SharedPreferencesUtils.getParam(WebMapActivity.this, "ePLong", "");
                                        webView.loadUrl("javascript:test('" + x1 +"','" + y1 +"','" + x2 +"','" + y2 +"')");
                                        kuaYueXian.setVisibility(View.INVISIBLE);
                                        ((RadioButton)kuaYueXian.getChildAt(2)).setChecked(false);
                                        ((RadioButton)kuaYueXian.getChildAt(1)).setClickable(true);
                                        iv_center.setVisibility(View.INVISIBLE);
                                        KuaYueLine kuaYueLine = new KuaYueLine();
                                        kuaYueLine.setType(et_type_kyx.getText().toString());
                                        kuaYueLine.setComment(et_comment_kyx.getText().toString());
                                        kuaYueLine.setHeight(et_height_kyx.getText().toString());
                                        kuaYueLine.setList(GalleryAdapter.list);
                                        try {
                                            dbUtils.save(kuaYueLine);
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });

                    }
                    dialog.show();
                    break;
            }
        }
    };


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

    private void doInit() {
        iv_center = (ImageView) findViewById(R.id.iv_center);
        kuaYueXian = (RadioGroup) findViewById(R.id.rg_kuayuexian);
        dbUtils = ((MainApplication)getApplication()).getDbUtils();
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

    private class JsInteration {

        private static final String GEO_JSON = "geo_json";

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

        @JavascriptInterface
        public void editKuaYueLine(){
           /* AlertDialog.Builder builder = new AlertDialog.Builder(WebMapActivity.this);
            builder.setTitle("跨越线");
            View layout = LayoutInflater.from(WebMapActivity.this).inflate(R.layout.kyx_edit, null);
            builder.setView(layout);
            builder.show();*/
            handler.sendEmptyMessage(0x123);
        }
    }

    public void resetLocation(View v){
        webView.loadUrl("javascript:reset()");
    }

    public void A(View v){
        iv_center.setVisibility(View.VISIBLE);
        webView.loadUrl("");
    }

    //+++++跨越线++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

    /**
     * 绘制跨越线
     */
    public void E(View v){
        iv_center.setVisibility(View.VISIBLE);
        kuaYueXian.setVisibility(View.VISIBLE);
    }

    /**
     * 设置跨越线起点
     * @param v
     */
    public void sP(View v){
        webView.loadUrl("javascript:markKYX_Start()");
        v.setClickable(false);
    }

    /**
     * 设置跨越线结点
     * @param v
     */
    public void eP(View v){
        webView.loadUrl("javascript:markKYX_End()");

        View view = DialogUtils.getView();
        assert view != null;
        GridView gridView = (GridView) view.findViewById(R.id.gallery);
        et_type_kyx = (EditText) view.findViewById(R.id.kyx_type);
        et_height_kyx = (EditText) view.findViewById(R.id.kyx_type);
        et_comment_kyx = (EditText)view.findViewById(R.id.et_line_comment_kyx);

        CommonAdapter commonAdapter = GalleryAdapter.getAdapter(this);
        gridView.setAdapter(commonAdapter);
        dialog.show();

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
                    dialog_photo = DialogUtils.getDialogNoButton(WebMapActivity.this, "选择获取方式", R.layout.select_photo);
                    View  select_view = DialogUtils.getView();
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

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dialog_photo.dismiss();
        if(resultCode != RESULT_OK){
            return;
        }
        if (requestCode == 1) {
            //to do find the path of pic
            if(imageUri != null){
                Bitmap bitmap = BitmapCompressUtils.decodeUriAsBitmap(this, imageUri);
                if(bitmap != null){
                    GalleryAdapter.list.add(bitmap);
                    GalleryAdapter.get().notifyDataSetChanged();
                }

            }
        } else if (requestCode == 2 ) {
            Uri uri = data.getData();
            if(uri == null){
               return;
            }else{
                ContentResolver cr = this.getContentResolver();
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    if(bitmap != null){
                        GalleryAdapter.list.add(bitmap);
                        GalleryAdapter.get().notifyDataSetChanged();
                    }
                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(),e);
                }
            }
        }
    }




}
