package com.clouweth.tabatatimer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AddTimer extends AppCompatActivity {
    TextView counter;
    EditText unit_of_ex;
    EditText count_of_rounds;
    EditText rest_between_rounds;
    EditText rest_between_ex;
    EditText time_of_ex;
    SQLiteDatabase db;
    LinearLayout containerLayout;
    int counterFields = 1;
    LinearLayout.LayoutParams layoutParams;
    int dp10;
    Bundle parameter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timer);
        counter = (TextView)findViewById(R.id.counter);
        unit_of_ex = (EditText) findViewById(R.id.unit_of_ex);
        parameter = getIntent().getExtras();
        count_of_rounds = (EditText) findViewById(R.id.count_of_rounds);
        rest_between_rounds = (EditText) findViewById(R.id.rest_between_rounds);
        rest_between_ex = (EditText) findViewById(R.id.rest_between_ex);
        time_of_ex = (EditText) findViewById(R.id.time_of_ex);
        containerLayout = (LinearLayout)findViewById(R.id.mlayout);
        dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        containerLayout.setPadding(0, 0,0, dp10);
        if (!parameter.get("edit").toString().equals("new")) {
            System.out.println("new");
            counterFields = 0;
            fillingFields(parameter.get("edit").toString());
        } else {
            createField(containerLayout);
        }
        counter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (counterFields == 5) {
                    Intent intent = new Intent(AddTimer.this, MainActivityTg.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    public void saveToDb(View view) {
        if (!parameter.get("edit").toString().equals("new")) {
            //удалить весь таймер и записать новый
            db = getBaseContext().openOrCreateDatabase("tabatatimer.db", MODE_PRIVATE, null);
            db.execSQL("DELETE FROM timers WHERE name_of_group = '" + parameter.get("edit").toString() + "'");
            db.close();
        }
        writeToDb();
    }
    private void goExercises() {
        Intent intent = new Intent(this, Exercises.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
    public void addField(View view) {
        //если больше 30 полей то не добавляем
        if (counterFields > 30)
            return;
        //инкрементим счетчик
        counterFields++;
        //создаем поле для отображения количества полей
        //TextView textView = (TextView)findViewById(R.id.counter);
        //устанавливаем значение счетчика в поле
        counter.setText(String.valueOf(counterFields));
        createField(containerLayout);
    }
    public void removeField(View view) {
        if (counterFields < 2)
            return;
        EditText editText = (EditText)findViewById(counterFields);
        containerLayout.removeView(editText);
        counterFields--;
        //TextView textView = (TextView)findViewById(R.id.counter);
        counter.setText(String.valueOf(counterFields));
    }
    public void createField(View view) {
        //создаем editText с стилем в конструкторе
        //EditText editText = new EditText(new ContextThemeWrapper(this, R.style.ItemDb), null, 0);
        EditText editText = new EditText(this);
        //добавляем editText в контейнер LinearLayout
        containerLayout.addView(editText);
        //устанавливаем текст по левому краю
        editText.setGravity(Gravity.START);
        //layoutParams = (LinearLayout.LayoutParams) editText.getLayoutParams();
        //layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp10, dp10, dp10, 0);
        editText.setLayoutParams(layoutParams);
        editText.setTag("EditText" + counterFields);
        editText.setId(counterFields);
        editText.setTextColor(getResources().getColor(R.color.letters));
        editText.setBackgroundResource(R.drawable.round_angle);
        editText.setPadding(10, 10, 10, 10);
        editText.setTextSize(24);
    }
    public boolean checkEmptyFields() {
        ArrayList<String> fields = new ArrayList<String>();
        fields.add(unit_of_ex.getText().toString());
        fields.add(time_of_ex.getText().toString());
        fields.add(rest_between_rounds.getText().toString());
        fields.add(rest_between_ex.getText().toString());
        fields.add(count_of_rounds.getText().toString());
        for (int i = 1; i <= counterFields; i++) {
            EditText editText = (EditText)findViewById(counterFields);
            fields.add(editText.getText().toString());
        }
        System.out.println(fields);
        return !fields.contains("");
    }
    public boolean checkNameTimer() {
        return !((ArrayList<String>) parameter.get("cash")).contains(unit_of_ex.getText().toString());
    }
    public void fillingFields(String name) {
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("tabatatimer.db", MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM TIMERS WHERE NAME_OF_GROUP = '" + name + "'", null);
        cursor.moveToFirst();
        unit_of_ex.setText(cursor.getString(0));
        time_of_ex.setText(cursor.getString(2));
        rest_between_rounds.setText(cursor.getString(5));
        rest_between_ex.setText(cursor.getString(6));
        count_of_rounds.setText(cursor.getString(3));
        cursor.moveToNext();
        do {
            addField(containerLayout);
            EditText editText = (EditText)findViewById(counterFields);
            editText.setText(cursor.getString(1));
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
    }
    public void writeToDb() {
        if (checkEmptyFields() && checkNameTimer()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name_of_group", unit_of_ex.getText().toString());
            contentValues.put("time", Integer.parseInt(time_of_ex.getText().toString()));
            contentValues.put("rest_rounds", Integer.parseInt(rest_between_rounds.getText().toString()));
            contentValues.put("rest_ex", Integer.parseInt(rest_between_ex.getText().toString()));
            contentValues.put("count_of_rounds", Integer.parseInt(count_of_rounds.getText().toString()));
            db = getBaseContext().openOrCreateDatabase("tabatatimer.db", MODE_PRIVATE, null);
            db.insert("timers", null, contentValues);
            System.out.println(counterFields);
            for (int i = 1; i <= counterFields; i++) {
                EditText editText = (EditText)findViewById(i);
                System.out.println("INSERT INTO timers (name_of_group, name_of_ex) VALUES ( '" + unit_of_ex.getText().toString() +
                        "', '" + editText.getText().toString() + "');");
                db.execSQL("INSERT INTO timers (name_of_group, name_of_ex) VALUES ( '" + unit_of_ex.getText().toString() +
                        "', '" + editText.getText().toString() + "');");
                System.out.println(editText.getText().toString());
            }
            db.close();
            goExercises();
        } else if(!checkEmptyFields()) {
            Toast.makeText(getApplicationContext(), "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
        } else if(!checkNameTimer()) {
            Toast.makeText(getApplicationContext(), "Таймер с таким именем уже существует", Toast.LENGTH_SHORT).show();
        }
    }
}