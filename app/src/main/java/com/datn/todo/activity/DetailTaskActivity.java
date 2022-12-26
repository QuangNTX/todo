package com.datn.todo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.datn.todo.R;
import com.datn.todo.database.DatabaseClient;
import com.datn.todo.model.Task;

import java.util.List;

public class DetailTaskActivity extends AppCompatActivity {

    private EditText description;
    private EditText time;
    private EditText date;
    private TextView title;
    private Toolbar toolbar;
    private ImageView buttonMore;

    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);

        description = findViewById(R.id.taskDescription);
        time = findViewById(R.id.taskTime);
        date = findViewById(R.id.taskDate);
        title = findViewById(R.id.taskTitle);
        toolbar = findViewById(R.id.toolbar);
        buttonMore = findViewById(R.id.buttonMore);

        task = new Task();
        task.setTaskTitle(getIntent().getStringExtra("DESC"));
        task.setTaskDescrption(getIntent().getStringExtra("TITLE"));
        task.setDate(getIntent().getStringExtra("DATE"));
        task.setTime(getIntent().getStringExtra("TIME"));

        time.setText(task.getTime());
        date.setText(task.getDate());
        title.setText(task.getTaskDescrption());
        description.setText(task.getTaskTitle());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpMenu(view, task);
            }
        });
    }

    private void showPopUpMenu(View view, Task task) {
        Context context = view.getContext();
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    deleteTask(this, task);
                    break;
                case R.id.menuUpdate:
                    updateTask(this, task);
                    break;
                case R.id.menuComplete:
                    completeTask(this, task);
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    /***
     *  Function delete, update, complete dont work
     */

    private void deleteTask(Context context, Task task) {
        class DeleteTasks extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase().dataBaseAction().deleteTaskFromId(task.getTaskId());
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                finishActivity();
            }
        }
        DeleteTasks deleteTasks = new DeleteTasks();
        deleteTasks.execute();
    }

    private void updateTask(Context context, Task task) {
        class UpdateTasks extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase().dataBaseAction().updateAnExistingRow(task.getTaskId(), task.getTaskTitle(), description.getText().toString(), date.getText().toString(), time.getText().toString(), task.isComplete());
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                finishActivity();
            }
        }
        UpdateTasks updateTask = new UpdateTasks();
        updateTask.execute();
    }

    private void completeTask(Context context, Task task) {
        class CompleteTasks extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase().dataBaseAction().updateAnExistingRow(task.getTaskId(), task.getTaskTitle(), task.getTaskDescrption(), task.getDate(), task.getTime(), true);

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                finishActivity();
            }
        }
        CompleteTasks completeTask = new CompleteTasks();
        completeTask.execute();
    }

    void finishActivity() {
//        this.finish()
        startActivity(new Intent(this, MainActivity.class));
    }
}