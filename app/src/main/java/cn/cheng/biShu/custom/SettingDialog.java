package cn.cheng.biShu.custom;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;

import cn.cheng.biShu.R;
import cn.cheng.biShu.bean.SysBean;
import cn.cheng.biShu.util.AdBlocker;
import cn.cheng.biShu.util.CommonUtils;

/**
 * Created by YanGeCheng on 2023/4/2.
 * （弹框）
 */
public class SettingDialog extends Dialog {

    private String type = "default";
    private Activity activity;
    private CheckBox gifTip;
    private CheckBox htmlTip;
    private CheckBox logTip;
    private CheckBox spiderTip;
    private EditText settingInput;
    private Button settingInputBtn;
    private View view_holder;
    private SysBean sysBean;
    public CallListener callListener;

    public SettingDialog(@NonNull Activity activity) {
        super(activity, R.style.dialog);
        this.activity = activity;
    }

    public SettingDialog(@NonNull Activity activity, String type) {
        super(activity, R.style.dialog);
        this.activity = activity;
        this.type = type;
    }

    public interface CallListener {
        void updateSetting();
    }

    public void setCallListener(CallListener callListener) {
        this.callListener = callListener;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定view
        setContentView(R.layout.dialog_setting);
        // 设置返回键可以关闭弹框
        setCancelable(true);
        // 设置触摸弹框以外区域可以关闭弹框
        setCanceledOnTouchOutside(true);

        // 初始化控件
        if ("default".equals(type)) {
            setSys();
            gifTip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sysBean.setFlagGif(gifTip.isChecked());
                    CommonUtils.writeObjectIntoLocal(sysBean, "SysSetting");
                    if (callListener != null) callListener.updateSetting();
                }
            });
            htmlTip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sysBean.setFlagHtml(htmlTip.isChecked());
                    CommonUtils.writeObjectIntoLocal(sysBean, "SysSetting");
                    if (callListener != null) callListener.updateSetting();
                }
            });
            logTip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sysBean.setFlagLog(logTip.isChecked());
                    CommonUtils.writeObjectIntoLocal(sysBean, "SysSetting");
                    if (callListener != null) callListener.updateSetting();
                }
            });
            spiderTip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sysBean.setFlagSpider(spiderTip.isChecked());
                    CommonUtils.writeObjectIntoLocal(sysBean, "SysSetting");
                    if (callListener != null) callListener.updateSetting();
                }
            });
        } else {
            findViewById(R.id.settingCard2).setVisibility(View.VISIBLE);
            findViewById(R.id.settingCard1).setVisibility(View.GONE);
            settingInput = findViewById(R.id.settingInput);
            settingInputBtn = findViewById(R.id.settingInputBtn);
            view_holder = findViewById(R.id.view_holder_setting);
            String hostInfo = AdBlocker.getHostInfo(activity);
            settingInput.setText(hostInfo);
            settingInputBtn.setOnClickListener(v -> {
                AdBlocker.updateHostInfo(activity, settingInput.getText().toString());
                dismiss();
                if (callListener != null) callListener.updateSetting();
            });
        }

        // view窗口显示设置
        Window window = this.getWindow();
        // window.setGravity(Gravity.TOP | Gravity.RIGHT);
        window.setGravity(Gravity.TOP | Gravity.CENTER);
        // window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // params.x = 70;
        params.y = 100;
        params.dimAmount = 0.3F;
        window.setAttributes(params);
    }

    private void hideSoftInput() {
        InputMethodManager im = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (im != null) { // 隐藏键盘
            im.hideSoftInputFromWindow(settingInput.getWindowToken(), 0);
        }
        view_holder.requestFocus();
    }

    private void setSys() {
        sysBean = CommonUtils.readObjectFromLocal("SysSetting", SysBean.class);
        if (sysBean != null) {
            boolean flagGif = sysBean.isFlagGif();
            boolean flagHtml = sysBean.isFlagHtml();
            boolean flagLog  = sysBean.isFlagLog();
            boolean flagSpider  = sysBean.isFlagSpider();
            gifTip = findViewById(R.id.gifTip);
            htmlTip = findViewById(R.id.htmlTip);
            logTip = findViewById(R.id.logTip);
            spiderTip = findViewById(R.id.spiderTip);
            if (gifTip != null) gifTip.setChecked(flagGif);
            if (htmlTip != null) htmlTip.setChecked(flagHtml);
            if (logTip != null) logTip.setChecked(flagLog);
            if (spiderTip != null) spiderTip.setChecked(flagSpider);
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        if (!"default".equals(type)) hideSoftInput(); // 隐藏键盘
        SettingDialog.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        super.dismiss();
    }

}
