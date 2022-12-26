package com.datn.todo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.datn.todo.R;
import com.datn.todo.model.Task;

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

        buttonMore.setVisibility(View.GONE);
        buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUpMenu(view, task);
            }
        });
    }

    private void showPopUpMenu(View view, Task task) {
//        Context context = view.getContext();
//        PopupMenu popupMenu = new PopupMenu(context, view);
//        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
//        popupMenu.setOnMenuItemClickListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.menuDelete:
//                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
//                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).setPositiveButton(R.string.yes, (dialog, which) -> {
//                        deleteTaskFromId(task.getTaskId(), position);
//                    }).setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
//                    break;
//                case R.id.menuUpdate:
//                    CreateTaskBottomSheetFragment createTaskBottomSheetFragment = new CreateTaskBottomSheetFragment();
//                    createTaskBottomSheetFragment.setTaskId(task.getTaskId(), true, context, context);
//                    createTaskBottomSheetFragment.show(getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
//                    break;
//                case R.id.menuComplete:
//                    AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
//                    completeAlertDialog.setTitle(R.string.confirmation).setMessage(R.string.sureToMarkAsComplete).setPositiveButton(R.string.yes, (dialog, which) -> showCompleteDialog(task, position)).setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
//                    break;
//            }
//            return false;
//        });
//        popupMenu.show();
    }
}