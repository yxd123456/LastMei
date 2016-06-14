package com.hz.activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hz.MainApplication;
import com.hz.R;
import com.hz.activity.base.WebMapActivity;
import com.hz.adapter.CommonAdapter;
import com.hz.adapter.GalleryAdapter;
import com.hz.adapter.ViewHolder;
import com.hz.entity.BianYaXiang;
import com.hz.entity.DianLanJing;
import com.hz.entity.HuBiao;
import com.hz.entity.KaiBiSuo;
import com.hz.entity.LiGan;
import com.hz.entity.XiangShiBianYaZhan;
import com.hz.entity.XiangShiKaiGuanZhan;
import com.hz.util.BitmapCompressUtils;
import com.hz.view.ValidaterEditText;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PointEditActivity extends AppCompatActivity {

    private static final int CAPTURE_PHOTO = 1;
    private static final int COLUMN_PHOTO = 2;
    public static final String TYPE = "type";
    public static final String ID = "id";
    public static final String DELETE = "delete";
    private Bitmap bitmapSelect;
    private LinearLayout ll_need_add_other_view;
    private int type;
    private LayoutInflater inflater;
    private ValidaterEditText
            et_point_name, //点位名称
            et_point_ganhao,//点位杆号
            et_ganta_xingshi,//杆塔形式
            et_diangan_xinghao,//电杆型号
            et_diangan_num,//电杆数量
            et_dizhi_qingkuang,//地质情况
            et_dixing_qingkuang,//地形情况
            et_shebei_anzhuang,//设备安装
            et_point_comment,//点位备注
            et_bianyaqi_rongliang,//变压器容量
            et_zhaomingbiao,//照明表
            et_donglibiao,//动力表
            et_jiehuxian_changdu;//接户线长度
    private RadioGroup rg_point_state;
    private RadioButton rb1, rb2, rb3;
    private GridView gv_photo;
    private DbUtils dbUtils;
    private LiGan liGan;
    private BianYaXiang bianYaXiang;
    private HuBiao huBiao;
    private DianLanJing dianLanJing;
    private XiangShiKaiGuanZhan xiangShiKaiGuanZhan;
    private KaiBiSuo kaiBiSuo;
    private XiangShiBianYaZhan xiangShiBianYaZhan;
    private List<String> bitmapList = new ArrayList<>();
    private CommonAdapter adapter;
    private ImageView iv_cell;
    private AlertDialog dialog_photo;
    private Uri imageUri;
    private String id;
    private boolean flag_need_set_id = false;
    private boolean flag_could_delete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_edit);
        init();
    }

    /**
     * 根据传入的类型信息来加载对应的布局
     */
    private void baseOnTypeAddLayout() {
        switch (type) {
            case 1:
                initOneView();
                et_ganta_xingshi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PointEditActivity.this);
                        View view = inflater.inflate(R.layout.ye_ganta_xingshi, null);
                        builder.setView(view);
                        ListView lv = (ListView) view.findViewById(R.id.lv_select);
                        final MyAdapter adapter = new MyAdapter(PointEditActivity.this, getModel());
                        lv.setAdapter(adapter);
                        et_ganta_xingshi.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                adapter.getFilter().filter(s.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                dialog.dismiss();
                                et_ganta_xingshi.setText(getModel().get(position));
                            }
                        });
                    }
                });
                initOneData();

                break;
            case 2:
                initTwoView();
                initTwoData();
                break;
            case 3:
                initThreeView();
                initThreeData();
                break;
            case 4:
                initFourView();
                initFourData();
                break;
            case 5:
                initFourView();
                initFiveData();
                break;
            case 6:
                initFourView();
                initSixData();
                break;
            case 7:
                initFourView();
                initSevenData();
                break;
        }
        if(gv_photo != null)
            gv_photo.setAdapter(adapter);

    }

    private void initSevenData() {
        try {
            xiangShiBianYaZhan = dbUtils.findFirst(XiangShiBianYaZhan.class, WhereBuilder.b("id", "=", id));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(xiangShiBianYaZhan != null){
            et_point_name.setText(xiangShiBianYaZhan.getDianWeiMingCheng());
            String zt = xiangShiBianYaZhan.getDianWeiZhuangTai();
            rg_point_state = (RadioGroup) findViewById(R.id.rg_point_state);
            RadioButton radioButton = null;
            if(zt != null){
                if(zt.equals("新点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(0);
                }else if(zt.equals("旧点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(1);
                }
            }
            if(radioButton != null){
                radioButton.setChecked(true);
            }
            et_point_comment.setText(xiangShiBianYaZhan.getDianWeiBeiZhu());
            if (xiangShiBianYaZhan.getBitmapList()!=null){
                bitmapList.addAll(YeUtils.stringToList(xiangShiBianYaZhan.getBitmapList()));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initSixData() {
        try {
            kaiBiSuo = dbUtils.findFirst(KaiBiSuo.class, WhereBuilder.b("id", "=", id));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(kaiBiSuo != null){
            et_point_name.setText(kaiBiSuo.getDianWeiMingCheng());
            String zt = kaiBiSuo.getDianWeiZhuangTai();
            rg_point_state = (RadioGroup) findViewById(R.id.rg_point_state);
            RadioButton radioButton = null;
            if(zt != null){
                if(zt.equals("新点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(0);
                }else if(zt.equals("旧点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(1);
                }
            }
            if(radioButton != null){
                radioButton.setChecked(true);
            }
            et_point_comment.setText(kaiBiSuo.getDianWeiBeiZhu());
            if (kaiBiSuo.getBitmapList()!=null){
                bitmapList.addAll(YeUtils.stringToList(kaiBiSuo.getBitmapList()));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initFiveData() {
        try {
            xiangShiKaiGuanZhan = dbUtils.findFirst(XiangShiKaiGuanZhan.class, WhereBuilder.b("id", "=", id));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(xiangShiKaiGuanZhan != null){
            et_point_name.setText(xiangShiKaiGuanZhan.getDianWeiMingCheng());
            String zt = xiangShiKaiGuanZhan.getDianWeiZhuangTai();
            rg_point_state = (RadioGroup) findViewById(R.id.rg_point_state);
            RadioButton radioButton = null;
            if(zt != null){
                if(zt.equals("新点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(0);
                }else if(zt.equals("旧点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(1);
                }
            }
            if(radioButton != null){
                radioButton.setChecked(true);
            }
            et_point_comment.setText(xiangShiKaiGuanZhan.getDianWeiBeiZhu());
            if (xiangShiKaiGuanZhan.getBitmapList()!=null){
                bitmapList.addAll(YeUtils.stringToList(xiangShiKaiGuanZhan.getBitmapList()));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initFourData() {
        try {
            dianLanJing = dbUtils.findFirst(DianLanJing.class, WhereBuilder.b("id", "=", id));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(dianLanJing != null){
            et_point_name.setText(dianLanJing.getDianWeiMingCheng());
            String zt = dianLanJing.getDianWeiZhuangTai();
            rg_point_state = (RadioGroup) findViewById(R.id.rg_point_state);
            RadioButton radioButton = null;
            if(zt != null){
                if(zt.equals("新点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(0);
                }else if(zt.equals("旧点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(1);
                }
            }
            if(radioButton != null){
                radioButton.setChecked(true);
            }
            et_point_comment.setText(dianLanJing.getDianWeiBeiZhu());
            if (dianLanJing.getBitmapList()!=null){
                bitmapList.addAll(YeUtils.stringToList(dianLanJing.getBitmapList()));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initThreeData() {
        try {
            huBiao = dbUtils.findFirst(HuBiao.class, WhereBuilder.b("id","=",id));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(huBiao != null){
            et_point_name.setText(huBiao.getDianWeiMingCheng());
            et_zhaomingbiao.setText(huBiao.getZhaoMingBiao());
            et_donglibiao.setText(huBiao.getDongLiBiao());
            et_jiehuxian_changdu.setText(huBiao.getJieHuXianChangDu());
            et_point_comment.setText(huBiao.getDianWeiBeiZhu());
            if (huBiao.getBitmapList()!=null){
                bitmapList.addAll(YeUtils.stringToList(huBiao.getBitmapList()));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initTwoData() {
        Toast.makeText(this, "执行了1", Toast.LENGTH_SHORT).show();
        try {
            bianYaXiang = dbUtils.findFirst(BianYaXiang.class, WhereBuilder.b("id", "=", id));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(bianYaXiang != null){
            Toast.makeText(this, "执行了2", Toast.LENGTH_SHORT).show();
            ll_need_add_other_view.addView(inflater.inflate(R.layout.ye_bianyaxiang, null));
            et_point_name.setText(bianYaXiang.getDianWeiMingCheng());
            et_bianyaqi_rongliang.setText(bianYaXiang.getBianYaQiRongLiang());
            String zt = bianYaXiang.getDianWeiZhuangtai();
            rg_point_state = (RadioGroup) findViewById(R.id.rg_point_state);
            RadioButton radioButton = null;
            if(zt != null){
                if(zt.equals("新点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(0);
                }else if(zt.equals("旧点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(1);
                }
            }
            if(radioButton != null){
                radioButton.setChecked(true);
            }
            et_point_comment.setText(bianYaXiang.getDianWeiBeiZhu());
            if (bianYaXiang.getBitmapList()!=null){
                bitmapList.addAll(YeUtils.stringToList(bianYaXiang.getBitmapList()));
                adapter.notifyDataSetChanged();
            }
        }

    }


    /**
     * 根据不同的类型信息来对用户选择的当前项进行删除
     */
    public void delete(View v) {
        switch (type) {
            case 1:
                if(flag_could_delete){
                    try {
                        dbUtils.delete(liGan);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if(flag_could_delete){
                    try {
                        dbUtils.delete(bianYaXiang);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if(flag_could_delete){
                    try {
                        dbUtils.delete(huBiao);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 4:
                if(flag_could_delete){
                    try {
                        dbUtils.delete(dianLanJing);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 5:
                if(flag_could_delete){
                    try {
                        dbUtils.delete(xiangShiKaiGuanZhan);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 6:
                if(flag_could_delete){
                    try {
                        dbUtils.delete(kaiBiSuo);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 7:
                if(flag_could_delete){
                    try {
                        dbUtils.delete(xiangShiBianYaZhan);

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        Toast.makeText(this, "type的值是" + type, Toast.LENGTH_SHORT).show();
        bundle.putString(ID, id);
        bundle.putBoolean(DELETE,  true);
        intent.putExtras(bundle);
        setResult(RESULT_OK,  intent);
        Toast.makeText(PointEditActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 根据不同的类型信息来对用户输入的数据做保存
     */
    public void confirm(View v) {
        if(id == null){
            id = UUID.randomUUID().toString();
            flag_need_set_id = true;
        }
        switch (type){
            case 1:
                if(liGan == null){
                    liGan = new LiGan();
                }
                if(flag_need_set_id){
                    liGan.setId(id);
                }
                liGan.setPointName(et_point_name.getText().toString());
                Log.d("tag5", "保存的点位名称是"+et_point_name.getText().toString());
                liGan.setPointGanHao(et_point_ganhao.getText().toString());
                liGan.setGanTaXingShi(et_ganta_xingshi.getText().toString());
                liGan.setDianGanXingHao(et_diangan_xinghao.getText().toString());
                liGan.setDianGanShuLiang(et_diangan_num.getText().toString());
                liGan.setDiZhiQingKuang(et_dizhi_qingkuang.getText().toString());
                liGan.setDiXingQingKuang(et_dixing_qingkuang.getText().toString());
                liGan.setSheBeiAnZhuang(et_shebei_anzhuang.getText().toString());
                liGan.setDianWeiZhuangTai(((RadioButton)rg_point_state.findViewById(rg_point_state.getCheckedRadioButtonId())).getText().toString());
                liGan.setDianWeiBeiZhu(et_point_comment.getText().toString());
                bitmapList.remove(0);
                liGan.setBitmapList(YeUtils.listToString(bitmapList));
                Toast.makeText(this, "保存的bitmap集合是空的吗" + (bitmapList == null), Toast.LENGTH_SHORT).show();
                try {
                    dbUtils.saveOrUpdate(liGan);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                if(bianYaXiang == null){
                    bianYaXiang = new BianYaXiang();
                }
                if(flag_need_set_id){
                    bianYaXiang.setId(id);
                }
                bianYaXiang.setDianWeiMingCheng(et_point_name.getText().toString());
                bianYaXiang.setBianYaQiRongLiang(et_bianyaqi_rongliang.getText().toString());
                bianYaXiang.setDianWeiZhuangtai(((RadioButton)rg_point_state.findViewById(rg_point_state.getCheckedRadioButtonId())).getText().toString());
                bianYaXiang.setDianWeiBeiZhu(et_point_comment.getText().toString());
                bitmapList.remove(0);
                bianYaXiang.setBitmapList(YeUtils.listToString(bitmapList));
                try {
                    dbUtils.saveOrUpdate(bianYaXiang);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                if(huBiao == null){
                    huBiao = new HuBiao();
                }
                if(flag_need_set_id){
                    huBiao.setId(id);
                }
                bitmapList.remove(0);
                huBiao.setBitmapList(YeUtils.listToString(bitmapList));
                huBiao.setDianWeiMingCheng(et_point_name.getText().toString());
                huBiao.setZhaoMingBiao(et_zhaomingbiao.getText().toString());
                huBiao.setDongLiBiao(et_donglibiao.getText().toString());
                huBiao.setJieHuXianChangDu(et_jiehuxian_changdu.getText().toString());
                huBiao.setDianWeiBeiZhu(et_point_comment.getText().toString());
                try {
                    dbUtils.saveOrUpdate(huBiao);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                if(dianLanJing == null){
                    dianLanJing = new DianLanJing();
                }
                if(flag_need_set_id){
                    dianLanJing.setId(id);
                }
                dianLanJing.setDianWeiMingCheng(et_point_name.getText().toString());
                dianLanJing.setDianWeiZhuangTai(((RadioButton)rg_point_state.findViewById(rg_point_state.getCheckedRadioButtonId())).getText().toString());
                dianLanJing.setDianWeiBeiZhu(et_point_comment.getText().toString());
                bitmapList.remove(0);
                dianLanJing.setBitmapList(YeUtils.listToString(bitmapList));
                try {
                    dbUtils.saveOrUpdate(dianLanJing);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case 5:
                if(xiangShiKaiGuanZhan == null){
                    xiangShiKaiGuanZhan = new XiangShiKaiGuanZhan();
                }
                if(flag_need_set_id){
                    xiangShiKaiGuanZhan.setId(id);
                }
                xiangShiKaiGuanZhan.setDianWeiMingCheng(et_point_name.getText().toString());
                xiangShiKaiGuanZhan.setDianWeiZhuangTai(((RadioButton)rg_point_state.findViewById(rg_point_state.getCheckedRadioButtonId())).getText().toString());
                xiangShiKaiGuanZhan.setDianWeiBeiZhu(et_point_comment.getText().toString());
                bitmapList.remove(0);
                xiangShiKaiGuanZhan.setBitmapList(YeUtils.listToString(bitmapList));
                try {
                    dbUtils.saveOrUpdate(xiangShiKaiGuanZhan);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case 6:
                if(kaiBiSuo == null){
                    kaiBiSuo = new KaiBiSuo();
                }
                if(flag_need_set_id){
                    kaiBiSuo.setId(id);
                }
                kaiBiSuo.setDianWeiMingCheng(et_point_name.getText().toString());
                kaiBiSuo.setDianWeiZhuangTai(((RadioButton)rg_point_state.findViewById(rg_point_state.getCheckedRadioButtonId())).getText().toString());
                kaiBiSuo.setDianWeiBeiZhu(et_point_comment.getText().toString());
                bitmapList.remove(0);
                kaiBiSuo.setBitmapList(YeUtils.listToString(bitmapList));
                try {
                    dbUtils.saveOrUpdate(kaiBiSuo);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case 7:
                if(xiangShiBianYaZhan == null){
                    xiangShiBianYaZhan = new XiangShiBianYaZhan();
                }
                if(flag_need_set_id){
                    xiangShiBianYaZhan.setId(id);
                }
                xiangShiBianYaZhan.setDianWeiMingCheng(et_point_name.getText().toString());
                xiangShiBianYaZhan.setDianWeiZhuangTai(((RadioButton)rg_point_state.findViewById(rg_point_state.getCheckedRadioButtonId())).getText().toString());
                xiangShiBianYaZhan.setDianWeiBeiZhu(et_point_comment.getText().toString());
                bitmapList.remove(0);
                xiangShiBianYaZhan.setBitmapList(YeUtils.listToString(bitmapList));
                try {
                    dbUtils.saveOrUpdate(xiangShiBianYaZhan);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        bundle.putString(ID, id);
        intent.putExtras(bundle);
        setResult(RESULT_OK,  intent);
        finish();
    }

    //******************************************************/
    private void initFourView() {
        ll_need_add_other_view.addView(inflater.inflate(R.layout.ye_dianlanjin, null));
        et_point_name = (ValidaterEditText) findViewById(R.id.point_name);
        rg_point_state = (RadioGroup) findViewById(R.id.rg_point_state);
        rb1 = (RadioButton) rg_point_state.getChildAt(0);
        rb1.setChecked(true);
        rb2 = (RadioButton) rg_point_state.getChildAt(1);
        et_point_comment = (ValidaterEditText) findViewById(R.id.point_comment);
        gv_photo = (GridView) findViewById(R.id.gv_photo);
    }
    private void initThreeView() {
        ll_need_add_other_view.addView(inflater.inflate(R.layout.ye_hubiao, null));
        et_point_name = (ValidaterEditText) findViewById(R.id.point_name);
        et_zhaomingbiao = (ValidaterEditText) findViewById(R.id.zhaomingbiao);
        et_donglibiao = (ValidaterEditText) findViewById(R.id.donglibiao);
        et_jiehuxian_changdu = (ValidaterEditText) findViewById(R.id.jiehuxian_changdu);
        et_point_comment = (ValidaterEditText) findViewById(R.id.point_comment);
        gv_photo = (GridView) findViewById(R.id.gv_photo);
    }
    private void initTwoView() {
        if(ll_need_add_other_view.getChildCount() == 0){
            ll_need_add_other_view.addView(inflater.inflate(R.layout.ye_bianyaxiang, null));
            Toast.makeText(this, "疑惑", Toast.LENGTH_SHORT).show();
        }
        et_point_name = (ValidaterEditText) findViewById(R.id.point_name);
        et_bianyaqi_rongliang = (ValidaterEditText) findViewById(R.id.bianyaqi_rongliang);
        rg_point_state = (RadioGroup) findViewById(R.id.rg_point_state);
        rb1 = (RadioButton) rg_point_state.getChildAt(0);
        rb1.setChecked(true);
        rb2 = (RadioButton) rg_point_state.getChildAt(1);
        et_point_comment = (ValidaterEditText) findViewById(R.id.point_comment);
        gv_photo = (GridView) findViewById(R.id.gv_photo);
    }

    private void initOneData() {;
        try {
            liGan = dbUtils.findFirst(LiGan.class, WhereBuilder.b("id", "=", id));
        } catch (DbException e) {
            e.printStackTrace();
        }
        if(liGan != null){
            flag_could_delete = true;
            et_point_name.setText(liGan.getPointName());
            et_point_ganhao.setText(liGan.getPointGanHao());
            et_ganta_xingshi.setText(liGan.getGanTaXingShi());
            et_diangan_xinghao.setText(liGan.getDianGanXingHao());
            et_diangan_num.setText(liGan.getDianGanShuLiang());
            et_dizhi_qingkuang.setText(liGan.getDiZhiQingKuang());
            et_dixing_qingkuang.setText(liGan.getDiXingQingKuang());
            et_shebei_anzhuang.setText(liGan.getSheBeiAnZhuang());
            String zt = liGan.getDianWeiZhuangTai();
            rg_point_state = (RadioGroup) findViewById(R.id.rg_point_state);
            RadioButton radioButton = null;
            if(zt != null){
                if(zt.equals("新点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(0);
                }else if(zt.equals("旧点位")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(1);
                }else if(zt.equals("拔立")){
                    radioButton = (RadioButton) rg_point_state.getChildAt(2);
                }
            }

            if(radioButton != null){
                radioButton.setChecked(true);
            }
            et_point_comment.setText(liGan.getDianWeiBeiZhu());
            Toast.makeText(this, "获取到的图片数组是空的吗？" + (liGan.getBitmapList()==null), Toast.LENGTH_SHORT).show();
            if (liGan.getBitmapList()!=null){
                bitmapList.addAll(YeUtils.stringToList(liGan.getBitmapList()));
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void initOneView() {
        ll_need_add_other_view.addView(inflater.inflate(R.layout.ye_ligan, null));
        et_point_name = (ValidaterEditText) findViewById(R.id.point_name);
        et_point_ganhao = (ValidaterEditText) findViewById(R.id.point_ganhao);
        et_ganta_xingshi = (ValidaterEditText) findViewById(R.id.ganta_xingshi);
        et_diangan_xinghao = (ValidaterEditText) findViewById(R.id.diangan_xinghao);
        et_diangan_num = (ValidaterEditText) findViewById(R.id.diangan_num);
        et_dizhi_qingkuang = (ValidaterEditText) findViewById(R.id.dizhi_qingkuang);
        et_dixing_qingkuang = (ValidaterEditText) findViewById(R.id.dixing_qingkuang);
        et_shebei_anzhuang = (ValidaterEditText) findViewById(R.id.shebei_anzhuang);
        rg_point_state = (RadioGroup) findViewById(R.id.rg_point_state);
        rb1 = (RadioButton) rg_point_state.getChildAt(0);
        rb1.setChecked(true);
        rb2 = (RadioButton) rg_point_state.getChildAt(1);
        rb3 = (RadioButton) rg_point_state.getChildAt(2);
        et_point_comment = (ValidaterEditText) findViewById(R.id.point_comment);
        gv_photo = (GridView) findViewById(R.id.gv_photo);
    }
    private void init() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_camera_enhance_black_48dp);
        bitmapList.add(YeUtils.bitmapToString(bitmap));

        adapter = new CommonAdapter(this, bitmapList, R.layout.cell_gallery, new CommonAdapter.OnGetViewHolderListener() {
            @Override
            public void setConvertView(ViewHolder holder, int position) {
                iv_cell = holder.setImageBitmap(R.id.iv_gallery, YeUtils.stringToBitmap(bitmapList.get(position)));
                if(position == 0){
                    iv_cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PointEditActivity.this);
                            builder.setTitle("选择获取方式");
                            View layout = LayoutInflater.from(PointEditActivity.this).inflate(R.layout.select_photo, null);
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
                    });
                }
            }
        });
        type = getIntent().getIntExtra(WebMapActivity.POINT_TYPE, 0);
        id = getIntent().getStringExtra(ID);
        ll_need_add_other_view = (LinearLayout) findViewById(R.id.ll_need_add_other_view);
        inflater = LayoutInflater.from(this);
        dbUtils = MainApplication.getDbUtils();
        baseOnTypeAddLayout();
    }

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
            bitmapList.add(YeUtils.bitmapToString(bitmapSelect));
            adapter.notifyDataSetChanged();
        }
    }

    private List<String> getModel() {
        String[] arr = new String[]{"11ZDD2", "11ZSD2", "11ZSD1", "11ZND2", "11ZJ1D2","11ZJ2D2","11ZJDD2","11ZSS1","12ZSS1",
                "11ZNS1", "12ZNS1", "11J1S1","12J1S1","11J2S1","12J2S1","11JDS1","12JDS1","11ZSS2","12ZSS2","11ZNS2","12ZNS2",
                "11J1S2","12J1S2","11J2S2","12J2S2","11JDS2","12JDS2","D1"};
        return Arrays.asList(arr);
    }

    class MyAdapter extends BaseAdapter implements Filterable {

        List<String> arrayList;
        List<String> mOriginalValues; // Original Values
        LayoutInflater inflater;

        public MyAdapter(Context context, List<String> arrayList) {
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView textView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.row, null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.textview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(arrayList.get(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,Filter.FilterResults results) {

                    arrayList = (List<String>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    List<String> FilteredArrList = new ArrayList<String>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<String>(arrayList); // saves the original data in mOriginalValues
                    }

                    /********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            String data = mOriginalValues.get(i);
                            if (data.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrList.add(data);
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }
    }
}
