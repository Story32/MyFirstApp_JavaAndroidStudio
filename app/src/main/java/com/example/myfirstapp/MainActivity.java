package com.example.myfirstapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnAddTask;
    private Button btnShowTasks;
    private ListView taskListView;
    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text_task);
        btnAddTask = findViewById(R.id.btn_add_task);
        btnShowTasks = findViewById(R.id.btn_show_tasks);
        taskListView = findViewById(R.id.list_view_tasks);

        adapter = new TaskAdapter(this, tasks);
        taskListView.setAdapter(adapter);

        taskListView.setVisibility(View.GONE);

        loadTasksLocally();

        btnAddTask.setOnClickListener(view -> {
            String taskName = editText.getText().toString().trim();
            if (!taskName.isEmpty()) {
                Task task = new Task(taskName);
                tasks.add(task);
                adapter.notifyDataSetChanged();
                editText.setText("");

                saveTasksLocally();
            }
        });

        btnShowTasks.setOnClickListener(view -> {

            if (taskListView.getVisibility() == View.VISIBLE) {
                taskListView.setVisibility(View.GONE);
            } else {
                taskListView.setVisibility(View.VISIBLE);
            }
        });
    }

    // Gem opgaverne i en fil
    private void saveTasksLocally() {
        try {

            FileOutputStream fos = openFileOutput("tasks.txt", Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(fos);

            for (Task task : tasks) {
                writer.println(task.getName());
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasksLocally() {
        try {

            FileInputStream fis = openFileInput("tasks.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = reader.readLine()) != null) {
                // Opret en ny opgave oprettes
                tasks.add(new Task(line));
            }

            // Adapteren opdateres for at vise opgaverne
            adapter.notifyDataSetChanged();

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class TaskAdapter extends ArrayAdapter<Task> {
        //Denne varibel holder styr på farveskiftet
        private boolean isDarkGreen = true;

        public TaskAdapter(MainActivity context, List<Task> tasks) {
            super(context, 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
            }

            Task currentTask = getItem(position);

            CheckBox checkBox = convertView.findViewById(R.id.checkbox_task);
            checkBox.setText(currentTask.getName());
            checkBox.setChecked(currentTask.isCompleted());

            // Farven på teksten skiftes mellem lys- og mørkegrøn
            int textColor = isDarkGreen ? Color.parseColor("#006400") : Color.parseColor("#228B22");
            checkBox.setTextColor(textColor);


            isDarkGreen = !isDarkGreen;

            checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
                currentTask.setCompleted(checked);
                if (checked) {
                    tasks.remove(currentTask);
                    notifyDataSetChanged();

                    saveTasksLocally();
                    visBesked("Godt klaret!");
                }
            });

            return convertView;
        }
    }

    private void visBesked(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }, 4000);
    }

    private class Task {
        private String name;
        private boolean completed;

        public Task(String name) {
            this.name = name;
            this.completed = false;
        }

        public String getName() {
            return name;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}
