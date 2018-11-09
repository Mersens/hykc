package com.tuoying.hykc.view;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.QuestionEntity;
import com.tuoying.hykc.utils.Questions;

import java.util.List;

public class QuestionDialogFragment extends DialogFragment {
    private CheckBox checkBox1_true;
    private CheckBox checkBox1_false;
    private TextView mTextQuestion1;
    private Button mBtn;
    OnQuestionSelectListener listener;
    List<QuestionEntity> list = null;
    int num1 = 0;
    QuestionEntity e1;
    boolean answer1=false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        return inflater.inflate(R.layout.layout_question, null);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
        initDatas();
    }

    private void initDatas() {
        list = Questions.getQuestions();
        int count = list.size();
        num1 = (int) (Math.random() * count);
        e1 = list.get(num1);
        if("Y".equals(e1.getAnswer())){
            answer1=true;
        }
        mTextQuestion1.setText(e1.getQuestion());
    }

    private void initEvent() {
        checkBox1_true.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBox1_false.setChecked(false);
                }
            }
        });
        checkBox1_false.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBox1_true.setChecked(false);
                }
            }
        });

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox1_true.isChecked() && !checkBox1_false.isChecked()){
                    Toast.makeText(getActivity(), "请选择第一项！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(listener==null){
                    return;
                }
                boolean select1=false;

                if(checkBox1_true.isChecked()){
                    select1=true;
                }
                if(checkBox1_false.isChecked()){
                    select1=false;
                }
                if(select1==answer1){
                    listener.onQuestionSelect(true);

                }else {
                    listener.onQuestionSelect(false);
                }

            }
        });
    }

    private void initView(View view) {
        checkBox1_true = view.findViewById(R.id.checkBox1_true);
        checkBox1_false = view.findViewById(R.id.checkBox1_false);
        mTextQuestion1 = view.findViewById(R.id.tv1);
        mBtn = view.findViewById(R.id.btn_ok);

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException ignore) {
        }
    }

    public void setOnQuestionSelectListener(OnQuestionSelectListener listener) {
        this.listener = listener;

    }

    public interface OnQuestionSelectListener {
        void onQuestionSelect(boolean isTrue);
    }

    public static QuestionDialogFragment getInstance(){
        return new QuestionDialogFragment();
    }
}
