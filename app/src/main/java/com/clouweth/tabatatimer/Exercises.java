package com.clouweth.tabatatimer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

public class Exercises extends AppCompatActivity {
    SQLiteDatabase db;
    ListView list_of_ex;
    ArrayAdapter<String> adapter;
    ArrayList<String> cash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        cash = new ArrayList<>();
        list_of_ex = findViewById(R.id.list_of_ex);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        db = getBaseContext().openOrCreateDatabase("tabatatimer.db", MODE_PRIVATE, null);
        /*db.execSQL("CREATE TABLE IF NOT EXISTS timers (name_of_group TEXT, name_of_ex TEXT, time INTEGER, count_of_rounds INTEGER, default_timer INTEGER, " +
                "rest_rounds INTEGER, rest_ex INTEGER)");*/
        /*File f = getApplicationContext().getDatabasePath("tabatatimer.db");
        long dbSize = f.length();
        System.out.println(dbSize);*/
        //выполнить чтение из бд и заполнить результатами listview
        Cursor read_timers = db.rawQuery("SELECT DISTINCT NAME_OF_GROUP FROM TIMERS", null);
        if(read_timers.moveToFirst()) {
            //если таблица не пуста, отобразить названия таймеров
            do {
                //произвести заполнение listview
                adapter.add(read_timers.getString(0));
                cash.add(read_timers.getString(0));
                //System.out.println(cash);
            } while (read_timers.moveToNext());
            list_of_ex.setAdapter(adapter);
            list_of_ex.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showPopupMenu(view);
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
    public void addEx(View view) {
        Intent intent = new Intent(this, AddTimer.class);
        intent.putExtra("edit", "new");
        intent.putExtra("cash", cash);
        startActivity(intent);
    }
    public void showPopupMenu(View view) {
        final TextView textView = (TextView) view;
        PopupMenu popupMenu = new PopupMenu(list_of_ex.getContext(), view);
        popupMenu.inflate(R.menu.options_ex);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        editTimer(textView);
                        //Toast.makeText(getApplicationContext(), "edit " + textView.getText(), Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.delete:
                        //отобразить диалоговое окно
                        deleteDialog(textView);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
    public void deleteTimer(TextView textView) {
        db = getBaseContext().openOrCreateDatabase("tabatatimer.db", MODE_PRIVATE, null);
        db.execSQL("DELETE FROM timers WHERE name_of_group ='" + textView.getText() + "'");
        Toast.makeText(getApplicationContext(), "таймер '" + textView.getText() + "' удалён", Toast.LENGTH_SHORT).show();
        adapter.remove(textView.getText().toString());
        adapter.notifyDataSetChanged();
        //db.execSQL("VACUUM");
        /*File f = getApplicationContext().getDatabasePath("tabatatimer.db");
        long dbSize = f.length();
        System.out.println(dbSize);*/
    }
    public void editTimer(TextView textView) {
        //запустить addtimer с предустановленными полями для правки
        Intent intent = new Intent(this, AddTimer.class);
        intent.putExtra("edit", textView.getText().toString());
        startActivity(intent);
    }
    private void deleteDialog(final TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Клунин питух");
        builder.setMessage("Удалить таймер '" + textView.getText() + "'?");
        //если нет то закрыть диалоговое окно
        builder.setNegativeButton("отмена", null);
        //если да то удалить
        builder.setPositiveButton("да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTimer(textView);
            }
        });
        builder.show();
    }
}