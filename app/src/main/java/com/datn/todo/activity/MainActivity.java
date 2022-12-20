package com.datn.todo.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.todo.R;
import com.datn.todo.adapter.TaskAdapter;
import com.datn.todo.bottomSheetFragment.CreateTaskBottomSheetFragment;
import com.datn.todo.bottomSheetFragment.ShowCalendarViewBottomSheet;
import com.datn.todo.broadcastReceiver.AlarmBroadcastReceiver;
import com.datn.todo.database.DatabaseClient;
import com.datn.todo.model.Task;
import com.datn.todo.util.ToDoExtractor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements CreateTaskBottomSheetFragment.setRefreshListener, TaskAdapter.OnTaskItemClickListener {

    RecyclerView taskRecycler;
    TaskAdapter taskAdapter;
    List<Task> tasks = new ArrayList<>();
    ImageView buttonSort;
    FloatingActionButton buttonVoice;
    FloatingActionButton buttonAdd;
    FloatingActionButton buttonKeyBoard;
    SearchView searchView;

    LinearLayout emptyState;

    private Boolean isButtonOpening = false;

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        buttonSort = findViewById(R.id.buttonSort);
        buttonVoice = findViewById(R.id.buttonVoice);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonKeyBoard = findViewById(R.id.buttonKeyBoard);
        emptyState = findViewById(R.id.emptyState);
        searchView = findViewById(R.id.searchView);

        setUpAdapter();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ComponentName receiver = new ComponentName(this, AlarmBroadcastReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        handleVisibilityChildButtons(isButtonOpening);

        buttonAdd.setOnClickListener(view -> {
            isButtonOpening = !isButtonOpening;
            handleVisibilityChildButtons(isButtonOpening);
            if (isButtonOpening) {
                buttonAdd.setRotation(45f);
            } else {
                buttonAdd.setRotation(0f);
            }
        });

        buttonKeyBoard.setOnClickListener(view -> {
            CreateTaskBottomSheetFragment createTaskBottomSheetFragment = new CreateTaskBottomSheetFragment();
            createTaskBottomSheetFragment.setTaskId(0, false, this, MainActivity.this);
            createTaskBottomSheetFragment.show(getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
        });
        buttonVoice.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech your task...");
            try {
                startActivityForResult(intent, 7899);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(getApplicationContext(), "Speech input isn't supported!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        getSavedTasks();

        buttonSort.setOnClickListener(view -> {
//            ShowCalendarViewBottomSheet showCalendarViewBottomSheet = new ShowCalendarViewBottomSheet();
//            showCalendarViewBottomSheet.show(getSupportFragmentManager(), showCalendarViewBottomSheet.getTag());
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        taskAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7899 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            Task task = new Task();
            if (result == null) return;
            String todo = result.get(0);
            if (ToDoExtractor.INSTANCE.extractVietnameseTimeAndDate(todo) != null) {
                task.setTaskTitle("To Do");
                task.setTaskDescrption(ToDoExtractor.INSTANCE.extractVietnameseDesc(todo));

                String[] timeAndDate = ToDoExtractor.INSTANCE.extractVietnameseTimeAndDate(todo).split("/");
                task.setDate(timeAndDate[1]);
                task.setTime(timeAndDate[0]);

                createTask(task);
            } else if (ToDoExtractor.INSTANCE.extractTimeAndDate(todo) != null) {
                task.setTaskTitle("To Do");
                task.setTaskDescrption(ToDoExtractor.INSTANCE.extractDesc(todo));

                String[] timeAndDate = ToDoExtractor.INSTANCE.extractTimeAndDate(todo).split("/");
                task.setDate(timeAndDate[1]);
                task.setTime(timeAndDate[0]);

                createTask(task);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).setMessage("Please set time and date by syntax:\n" +
                                "hour {your_hour} minute {your_minute} day {your_day} month {your_month} year {your_year}")
                        .setTitle("We need time and date for your ToDo!")
                        .setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create();
                alertDialog.show();
            }
        }
    }

    private void handleVisibilityChildButtons(Boolean visibility) {
        buttonVoice.setVisibility(visibility ? View.VISIBLE : View.GONE);
        buttonKeyBoard.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void setUpAdapter() {
        taskAdapter = new TaskAdapter(this, tasks, this);
        taskAdapter.setOnTaskItemClickListener(this);
        taskRecycler = findViewById(R.id.taskRecycler);
        taskRecycler.setAdapter(taskAdapter);
    }

    private void getSavedTasks() {

        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                tasks = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction()
                        .getAllTasksList();
                return tasks;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                emptyState.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
                setUpAdapter();
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    @Override
    public void refresh() {
        getSavedTasks();
    }

    private void createTask(Task task) {
        class saveTaskInBackend extends AsyncTask<Void, Void, Void> {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(MainActivity.this).getAppDatabase()
                        .dataBaseAction()
                        .insertDataIntoTaskList(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    createAnAlarm(task);
                }
                getSavedTasks();
                Toast.makeText(MainActivity.this, "Your event is been added", Toast.LENGTH_SHORT).show();
            }
        }
        saveTaskInBackend st = new saveTaskInBackend();
        st.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createAnAlarm(Task task) {
        try {
            String[] items1 = task.getDate().split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            String[] itemTime = task.getTime().split(":");
            String hour = itemTime[0];
            String min = itemTime[1];

            Calendar cur_cal = new GregorianCalendar();
            cur_cal.setTimeInMillis(System.currentTimeMillis());

            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            cal.set(Calendar.MINUTE, Integer.parseInt(min));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DATE, Integer.parseInt(dd));

            Intent alarmIntent = new Intent(this, AlarmBroadcastReceiver.class);
            alarmIntent.putExtra("TITLE", task.getTaskTitle());
            alarmIntent.putExtra("DESC", task.getTaskDescrption());
            alarmIntent.putExtra("DATE", task.getDate());
            alarmIntent.putExtra("TIME", task.getTime());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, CreateTaskBottomSheetFragment.count, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                CreateTaskBottomSheetFragment.count++;

                PendingIntent intent = PendingIntent.getBroadcast(this, CreateTaskBottomSheetFragment.count, alarmIntent, 0);
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
                CreateTaskBottomSheetFragment.count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskItemClick(Task task) {
        Intent intent = new Intent(this, DetailTaskActivity.class);
        intent.putExtra("TITLE", task.getTaskTitle());
        intent.putExtra("DESC", task.getTaskDescrption());
        intent.putExtra("DATE", task.getDate());
        intent.putExtra("TIME", task.getTime());
        startActivity(intent);
    }
}
