package com.hz.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by asus on 2016/5/23.
 */
public class DialogUtils {

    private static View layout;

    private static AlertDialog alertDialog;

    public static AlertDialog getDialog(Context ctx, String title, int layoutId, DoBuilderListener listener){
        if(alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(title);
            layout = LayoutInflater.from(ctx).inflate(layoutId, null);
            builder.setView(layout);
            listener.doSomething(builder);
            alertDialog = builder.create();
        }
        return alertDialog;
    }

    public static AlertDialog getDialog(){
        return alertDialog;
    }

    public static AlertDialog getDialogNoButton(Context ctx, String title, int layoutId){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        layout = LayoutInflater.from(ctx).inflate(layoutId, null);
        builder.setView(layout);
        return builder.create();
    }

    public static View getView(){
        if(layout != null){
            return layout;
        } else {
            return null;
        }
    }

    public interface DoBuilderListener{
        void doSomething(AlertDialog.Builder builder);
    }

}
