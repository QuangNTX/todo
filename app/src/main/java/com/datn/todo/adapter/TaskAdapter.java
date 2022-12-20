package com.datn.todo.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.todo.R;
import com.datn.todo.activity.MainActivity;
import com.datn.todo.bottomSheetFragment.CreateTaskBottomSheetFragment;
import com.datn.todo.database.DatabaseClient;
import com.datn.todo.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> implements Filterable {

    private MainActivity context;
    private LayoutInflater inflater;
    private List<Task> taskListAll;
    private List<Task> taskList;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
    Date date = null;
    String outputDateString = null;
    CreateTaskBottomSheetFragment.setRefreshListener setRefreshListener;

    private TaskFilter filter = new TaskFilter();

    private OnTaskItemClickListener onTaskItemClickListener;

    public void setOnTaskItemClickListener(OnTaskItemClickListener onTaskItemClickListener) {
        this.onTaskItemClickListener = onTaskItemClickListener;
    }

    public TaskAdapter(MainActivity context, List<Task> taskList, CreateTaskBottomSheetFragment.setRefreshListener setRefreshListener) {
        this.context = context;
        this.taskList = taskList;
        this.taskListAll = taskList;
        this.setRefreshListener = setRefreshListener;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.item_task, viewGroup, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTaskTitle());
        holder.description.setText(task.getTaskDescrption());
        holder.time.setText(task.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTaskItemClickListener.onTaskItemClick(task);
            }
        });

        if (task.isComplete()) {
            holder.status.setText("Completed");
            holder.status.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));
        } else {
            holder.status.setText("Upcoming");
            holder.status.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));

        }
        holder.options.setOnClickListener(view -> showPopUpMenu(view, position));

        try {
            date = inputDateFormat.parse(task.getDate());
            outputDateString = dateFormat.format(date);

            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPopUpMenu(View view, int position) {
        final Task task = taskList.get(position);
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
//                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
//                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete)
//                            .setPositiveButton(R.string.yes, (dialog, which) -> {
//                        deleteTaskFromId(task.getTaskId(), position);
//                    }).setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    showConfirmDeleteDialog(task, position);
                    break;
                case R.id.menuUpdate:
                    CreateTaskBottomSheetFragment createTaskBottomSheetFragment = new CreateTaskBottomSheetFragment();
                    createTaskBottomSheetFragment.setTaskId(task.getTaskId(), true, context, context);
                    createTaskBottomSheetFragment.show(context.getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
                    break;
                case R.id.menuComplete:
//                    AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
//                    completeAlertDialog.setTitle(R.string.confirmation)
//                            .setMessage(R.string.sureToMarkAsComplete)
//                            .setPositiveButton(R.string.yes, (dialog, which) -> showCompleteDialog(task, position))
//                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    showConfirmCompleteDialog(task, position);
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    public void showCompleteDialog(Task task, int position) {
        Dialog dialog = new Dialog(context, R.style.AppTheme);
        dialog.setContentView(R.layout.dialog_completed_theme);
        Button close = dialog.findViewById(R.id.closeButton);
        close.setOnClickListener(view -> {
            updateTaskFromId(task, position);
            dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public void showConfirmCompleteDialog(Task task, int position){
        Dialog dialog = new Dialog(context, R.style.AppTheme);
        dialog.setContentView(R.layout.layout_confirm_complete_task_dialog);
        TextView buttonCancel = dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        Button buttonComplete = dialog.findViewById(R.id.buttonComplete);
        buttonComplete.setOnClickListener(view -> {
            showCompleteDialog(task, position);
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public void showConfirmDeleteDialog(Task task, int position){
        Dialog dialog = new Dialog(context, R.style.AppTheme);
        dialog.setContentView(R.layout.layout_confirm_delete_task_dialog);
        TextView buttonCancel = dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(view -> {
            deleteTaskFromId(task.getTaskId(), position);
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private void updateTaskFromId(Task task, int position) {
        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase().dataBaseAction().updateAnExistingRow(task.getTaskId(), task.getTaskTitle(), task.getTaskDescrption(), task.getDate(), task.getTime(), true);

                return taskList;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                notifyAtPosition(position);
                setRefreshListener.refresh();
            }
        }
        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    private void notifyAtPosition(int position) {
        notifyItemChanged(position);
        notifyItemRangeChanged(position, taskList.size());
    }

    private void deleteTaskFromId(int taskId, int position) {
        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase().dataBaseAction().deleteTaskFromId(taskId);

                return taskList;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                removeAtPosition(position);
                setRefreshListener.refresh();
            }
        }
        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    private void removeAtPosition(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, taskList.size());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class TaskFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Task> result = new ArrayList<>();
            for (Task task : taskListAll) {
                if (task.getTaskTitle().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                        task.getTaskDescrption().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                    result.add(task);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.count = result.size();
            filterResults.values = result;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            taskList = (ArrayList<Task>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.day)
        TextView day;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.month)
        TextView month;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.options)
        ImageView options;
        @BindView(R.id.time)
        TextView time;

        TaskViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
            day = view.findViewById(R.id.day);
            date = view.findViewById(R.id.date);
            month = view.findViewById(R.id.month);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            status = view.findViewById(R.id.status);
            options = view.findViewById(R.id.options);
            time = view.findViewById(R.id.time);
        }
    }

    public interface OnTaskItemClickListener {
        void onTaskItemClick(Task item);
    }
}
