package org.jianjian.ballgame;

import com.cmcm.ballgame.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PrepareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);
        final EditText initSpeedEditText = (EditText) findViewById(R.id.initSpeedEditText);
        final EditText slowRateEditText = (EditText) findViewById(R.id.slowRateEditText);
        final Button startButton = (Button) findViewById(R.id.startButton);
        final Button clearButton = (Button) findViewById(R.id.clearButton);
        final TextView recordTextView = (TextView) findViewById(R.id.recordText);
        // 设置开始按钮事件
        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrepareActivity.this,
                        GameActivity.class);
                float initSpeed = 0;
                float slowRate = 0;
                try {
                    initSpeed = Float.parseFloat(initSpeedEditText.getText()
                            .toString());
                    slowRate = Float.parseFloat(slowRateEditText.getText()
                            .toString());
                } catch (Exception e) {
                    Toast.makeText(PrepareActivity.this, "输入格式有误！请输入浮点数",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (initSpeed < 0 || initSpeed > 250) {
                    Toast.makeText(PrepareActivity.this, "初始速度0~250之间",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                slowRate = slowRate / 100;// 单位由每1s转换为每10ms
                intent.putExtra("initSpeed", initSpeed);
                intent.putExtra("slowRate", slowRate);
                startActivity(intent);
            }
        });
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences perference = getSharedPreferences("record",
                        MODE_PRIVATE); // 获取记录文件
                Editor editor = perference.edit();
                editor.putInt("count", 0); // 写入初始值
                editor.commit();
                recordTextView.setText(" 你还没有玩过本游戏哦！");
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        TextView recordTextView = (TextView) findViewById(R.id.recordText);
        // 读取游戏记录
        int count = getSharedPreferences("record", MODE_PRIVATE).getInt(
                "count", 0);
        if (count == 0) {
            recordTextView.setText(" 你还没有玩过本游戏哦！");
        } else {
            recordTextView.setText(" 最高记录" + count);
        }
    }

}
