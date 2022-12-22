package com.datn.todo.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.datn.todo.R;
import com.datn.todo.database.DatabaseClient;
import com.datn.todo.model.Task;

import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmActivity extends BaseActivity {

    ImageView imageView;
    TextView title;
    TextView description;
    TextView timeAndData;
    Button closeButton;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);

        imageView = findViewById(R.id.imageView);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        timeAndData = findViewById(R.id.timeAndData);
        closeButton = findViewById(R.id.closeButton);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.notification);
        mediaPlayer.start();

        if (getIntent().getExtras() != null) {
            String extraTitle = getIntent().getStringExtra("TITLE");
            String extraDesc = getIntent().getStringExtra("DESC");
            String extraDate = getIntent().getStringExtra("DATE");
            String extraTime = getIntent().getStringExtra("TIME");
            title.setText(extraTitle);
            description.setText(extraDesc);
            timeAndData.setText(extraDate + ", " + extraTime);

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    Task task = DatabaseClient.getInstance(getBaseContext())
                            .getAppDatabase().dataBaseAction()
                            .getTask(extraTitle, extraDesc, extraDate, extraTime);

                    DatabaseClient.getInstance(getBaseContext())
                            .getAppDatabase().dataBaseAction()
                            .updateAnExistingRow(task.getTaskId(), extraTitle, extraDesc, extraDate, extraTime, true);

                }
            });
        }

        Glide.with(getApplicationContext()).load(R.drawable.alert).into(imageView);
        closeButton.setOnClickListener(view -> finish());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}
