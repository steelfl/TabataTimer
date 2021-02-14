package com.clouweth.tabatatimer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChangeTimer extends AppCompatActivity {
    SQLiteDatabase db;
    ListView list_of_ex;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_timer);
        list_of_ex = findViewById(R.id.list_of_timers);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        db = getBaseContext().openOrCreateDatabase("tabatatimer.db", MODE_PRIVATE, null);
        //db.execSQL("CREATE TABLE IF NOT EXISTS timers (name_of_group TEXT, name_of_ex TEXT, time INTEGER, count_of_rounds INTEGER)");
        //выполнить чтение из бд и заполнить результатами ListView
        Cursor read_timers = db.rawQuery("SELECT DISTINCT NAME_OF_GROUP FROM TIMERS", null);
        if(read_timers.moveToFirst()) {
            //если таблица не пуста, отобразить названия таймеров
            do {
                //произвести заполнение ListView
                adapter.add(read_timers.getString(0));
            } while (read_timers.moveToNext());
            list_of_ex.setAdapter(adapter);
            list_of_ex.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    select_timer(view);
                }
            });
        } else {
            //если таблица пуста, отобразить инфу что таймеров нет
            adapter.add("таймеров еще нет, нажмите '+' чтобы добавить");
            list_of_ex.setAdapter(adapter);
        }
        read_timers.close();
        db.close();
    }
    public void select_timer(View view) {
        final TextView textView = (TextView) view;
        db = getBaseContext().openOrCreateDatabase("tabatatimer.db", MODE_PRIVATE, null);
        db.execSQL("UPDATE timers SET default_timer = null WHERE default_timer = 1");
        db.execSQL("UPDATE timers SET default_timer = 1 WHERE name_of_group = '" + textView.getText().toString() + "'");
        db.close();
        goHome();
    }
    public void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}