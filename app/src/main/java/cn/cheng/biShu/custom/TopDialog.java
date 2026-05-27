package cn.cheng.biShu.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import cn.cheng.biShu.R;

/**
 * Created by YanGeCheng on 2023/4/2.
 * （弹框）
 */
public class TopDialog extends Dialog {

    private Button catalogBtn;
    private Button readBtn;
    private Button readSetBtn;
    private Button powerSetBtn;
    private Button replaceBtn;
    private EditText replaceOld;
    private EditText replaceNew;
    private ImageButton replaceBtnBtn;
    private TouchListener touchListener;
    private boolean flagRead;
    private boolean flagReplace;

    public TopDialog(@NonNull Context context, boolean flagRead) {
        super(context, R.style.dialog);
        this.flagRead = flagRead;
    }

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定view
        setContentView(R.layout.dialog_top);
        // 设置返回键可以关闭弹框
        setCancelable(true);
        // 设置触摸弹框以外区域可以关闭弹框
        setCanceledOnTouchOutside(true);

        // 初始化控件
        catalogBtn = findViewById(R.id.catalogBtn);
        readBtn = findViewById(R.id.readBtn);
        if (flagRead) {
            readBtn.setText("停止");
        } else {
            readBtn.setText("朗读");
        }
        readSetBtn = findViewById(R.id.readSetBtn);
        powerSetBtn = findViewById(R.id.powerSetBtn);
        replaceBtn = findViewById(R.id.replaceBtn);
        replaceOld = findViewById(R.id.replaceOld);
        replaceNew = findViewById(R.id.replaceNew);
        replaceBtnBtn = findViewById(R.id.replaceBtnBtn);

        // view窗口显示设置
        Window window = this.getWindow();
        window.setGravity(Gravity.TOP | Gravity.CENTER);
        // window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = 0;
        params.y = 100;
        params.dimAmount = 0.3F;
        window.setAttributes(params);

        // 按钮触摸事件
        catalogBtn.setOnTouchListener(new FilterLongDownSlideTouchListener() {
            @Override
            public void upEvent() {
                if (touchListener != null) {
                    touchListener.catalog();
                }
            }
        });
        readBtn.setOnTouchListener(new FilterLongDownSlideTouchListener() {
            @Override
            public void upEvent() {
                if (touchListener != null) {
                    touchListener.read();
                }
            }
        });
        readSetBtn.setOnTouchListener(new FilterLongDownSlideTouchListener() {
            @Override
            public void upEvent() {
                if (touchListener != null) {
                    readBtn.setText("朗读");
                    touchListener.readSet();
                }
            }
        });
        powerSetBtn.setOnTouchListener(new FilterLongDownSlideTouchListener() {
            @Override
            public void upEvent() {
                if (touchListener != null) {
                    readBtn.setText("朗读");
                    touchListener.powerSet();
                }
            }
        });
        replaceBtn.setOnTouchListener(new FilterLongDownSlideTouchListener() {
            @Override
            public void upEvent() {
                flagReplace = !flagReplace;
                if (flagReplace) {
                    findViewById(R.id.readDialogB).setVisibility(View.GONE);
                    findViewById(R.id.replaceDialogB).setVisibility(View.VISIBLE);
                    if (touchListener != null) touchListener.replace(null, null);
                } else {
                    findViewById(R.id.readDialogB).setVisibility(View.VISIBLE);
                    findViewById(R.id.replaceDialogB).setVisibility(View.GONE);
                }
            }
        });
        replaceBtnBtn.setOnClickListener(view -> {
            String oldTxt = replaceOld.getText().toString();
            String newTxt = replaceNew.getText().toString();
            if (touchListener != null) touchListener.replace(oldTxt, newTxt);
        });
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        TopDialog.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        super.dismiss();
    }

    // Dialog设置外部点击事件
    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        if (touchListener != null) {
            touchListener.close();
        }
        super.setCanceledOnTouchOutside(cancel);
    }

    public void setOnTouchListener(TouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public interface TouchListener {
        // 关闭弹框
        void close();
        // 应用
        void catalog();
        // 朗读
        void read();
        // 朗读
        void readSet();
        // 省电策略
        void powerSet();
        // 替换
        void replace(String oldTxt, String newTxt);
    }

}
