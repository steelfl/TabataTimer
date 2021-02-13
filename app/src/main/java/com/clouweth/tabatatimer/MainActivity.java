package com.clouweth.tabatatimer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView repetitions;//круг
    TextView residue;//осталось
    TextView approach;//подход
    TextView timer;
    ImageView pikachu;
    TextView name;
    Integer sec_on;//секунды в цикле
    Integer time_on;//общее время в цикле
    Integer time;//длительность упражнения
    Integer rest_ex;//отдых между упражнениями
    Integer rest_rounds;
    Integer count_of_rounds;//количество кругов
    Integer count_of_ex;//количество упражнений
    //Integer counter_ex;
    //Integer counter_rs;
    Integer round_now;
    Integer ex_now;
    Integer time_summary;//общее время
    ArrayList<String> exes;//массив с именами упражнений
    Boolean doing;
    Button pause;
    Button start;
    SQLiteDatabase db;
    LinearLayout pause_reset;
    LinearLayout start_block;
    Thread thread;
    String switcher;
    String name_of_timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        repetitions = (TextView) findViewById(R.id.repetitions);
        residue = (TextView) findViewById(R.id.residue);
        approach = (TextView) findViewById(R.id.approach);
        timer = (TextView) findViewById(R.id.timer);
        pikachu = (ImageView) findViewById(R.id.pikachu);
        name = (TextView) findViewById(R.id.name);
        doing = false;
        pause = (Button) findViewById(R.id.buttonPause);
        start = (Button) findViewById(R.id.buttonStart);
        pause_reset = (LinearLayout) findViewById(R.id.pause_reset);
        start_block = (LinearLayout) findViewById(R.id.start);
        exes = new ArrayList<String>();
        fillDisplay();
    }
    public void start(View view) {
        pause_reset.setVisibility(View.VISIBLE);
        start_block.setVisibility(View.INVISIBLE);
        System.out.println(doing);
        if (!doing) {
            doing = true;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println(doing + switcher + ex_now + round_now + count_of_ex + count_of_rounds);
                    running();
                    /*for (int i = 1; i <= count_of_rounds; i++) {
                        for (int j = 1; j <= count_of_ex; j++) {
                            if (j == count_of_ex && doing) {
                                System.out.println("start ex " + j);
                                approach.setText(j + "/" + count_of_ex.toString());
                                running();
                                System.out.println("end round");
                            } else if (doing) {
                                System.out.println("start ex " + j);
                                approach.setText(j + "/" + count_of_ex.toString());
                                running();
                                sec_on = rest_ex;
                                System.out.println("rest after ex" + j);
                                running();
                                sec_on = time;
                                System.out.println("end of ex" + j);
                            }
                        }
                        if (i == count_of_rounds && doing) {
                            System.out.println("end timer");
                            round_now = 1;
                            sec_on = time;
                            time_on = time_summary;
                            doing = false;
                            setFields();
                            pause_reset.post(new Runnable() {
                                @Override
                                public void run() {
                                    pause_reset.setVisibility(View.INVISIBLE);
                                }
                            });
                            start_block.post(new Runnable() {
                                @Override
                                public void run() {
                                    start_block.setVisibility(View.VISIBLE);
                                }
                            });
                        } else if (doing) {
                            System.out.println("rest between rounds");
                            sec_on = rest_rounds;
                            running();
                            repetitions.setText(i + 1 + "/" + count_of_rounds.toString());
                            sec_on = time;
                            round_now++;
                        }
                    }*/
                }
            };
            thread = new Thread(runnable);
            thread.start();
        }
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            timer.setText(toTime(sec_on));
            residue.setText(toTime(time_on));
        }
    };
    Handler resetUi = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            reset(pause_reset);
        }
    };

    public void running() {
        while (doing && time_on >= 0) {
            handler.sendEmptyMessage(0);
            //System.out.println(sec_on);
            try {
                thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("error");
            }
            sec_on -= 1;
            time_on -= 1;
            if (sec_on < 0 && switcher.equals("exercise") && ex_now < count_of_ex) {
                System.out.println("запускаем отдых между упражнениями");
                sec_on = rest_ex;
                switcher = "rest_ex";
                ex_now++;
                name.post(new Runnable() {
                    @Override
                    public void run() {
                        name.setText(name_of_timer + "\n" + "отдых между упражнениями");
                    }
                });
            } else if (sec_on < 0 && switcher.equals("exercise") && ex_now.equals(count_of_ex) && round_now < count_of_rounds) {
                System.out.println("запускаем отдых между кругами");
                sec_on = rest_rounds;
                switcher = "rest_rounds";
                name.post(new Runnable() {
                    @Override
                    public void run() {
                        name.setText(name_of_timer + "\n" + "отдых между кругами");
                    }
                });
            } else if (sec_on < 0 && switcher.equals("rest_ex")) {
                System.out.println("запускаем упражнение в круге");
                sec_on = time;
                switcher = "exercise";
                //ex_now++;
                name.post(new Runnable() {
                    @Override
                    public void run() {
                        name.setText(name_of_timer + "\n" + exes.get(ex_now - 1));
                        approach.setText(ex_now + "/" + count_of_ex.toString());
                    }
                });
            } else if (sec_on < 0 && switcher.equals("rest_rounds") && round_now < count_of_rounds) {
                System.out.println("запускаем новый круг");
                sec_on = time;
                switcher = "exercise";
                round_now++;
                ex_now = 1;
                name.post(new Runnable() {
                    @Override
                    public void run() {
                        repetitions.setText(round_now + "/" + count_of_rounds.toString());
                        approach.setText(ex_now + "/" + count_of_ex.toString());
                        name.setText(name_of_timer + "\n" + exes.get(ex_now - 1));
                    }
                });
            } else if (sec_on < 0 && switcher.equals("exercise") && ex_now.equals(count_of_ex) && round_now.equals(count_of_rounds)) {
                resetUi.sendEmptyMessage(0);
            }
        }
    }
    public void pause(View view) {
        try {
            doing = false;
        } catch (Exception e) {
            System.out.println("error");
        }
        pause_reset.setVisibility(View.INVISIBLE);
        start_block.setVisibility(View.VISIBLE);
    }
    public void reset(View view) {
        pause(view);
        setFields();
    }
    public void select_ex(View view) {
        Intent intent = new Intent(this, ChangeTimer.class);
        startActivity(intent);
    }
    public void list_of_ex(View view) {
        Intent intent = new Intent(this, Exercises.class);
        startActivity(intent);
    }
    public void fillDisplay() {
        db = getBaseContext().openOrCreateDatabase("tabatatimer.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS timers (name_of_group TEXT, name_of_ex TEXT, time INTEGER, count_of_rounds INTEGER, default_timer INTEGER, " +
                "rest_rounds INTEGER, rest_ex INTEGER)");
        Cursor cursor = db.rawQuery("SELECT * FROM timers WHERE default_timer = 1", null);
        /*db.execSQL("SELECT COUNT(name_of_group) FROM timers where name_of_group = '" + name.getText().toString() + "'");*/
        if (cursor.moveToFirst()) {
            count_of_ex = cursor.getCount() - 1;
            cursor.moveToFirst();
            time = cursor.getInt(2);
            count_of_rounds = cursor.getInt(3);
            rest_rounds = cursor.getInt(5);
            System.out.println(rest_rounds);
            rest_ex = cursor.getInt(6);
            time_summary = (count_of_ex * (time+1) + (rest_ex+1) * (count_of_ex - 1)) * count_of_rounds + (rest_rounds+1) * (count_of_rounds - 1) - 1;
            System.out.println(time_summary);
            cursor.moveToPosition(1);
            name_of_timer = cursor.getString(0);
            do {
                exes.add(cursor.getString(1));
                System.out.println(exes);
            } while (cursor.moveToNext());
            setFields();
        } else {
            name.setText("таймер не выбран");
            start.setEnabled(false);
        }
        cursor.close();
        db.close();
    }
    public void setFields() {
        round_now = 1;
        ex_now = 1;
        repetitions.setText(round_now + "/" + count_of_rounds.toString());
        approach.setText(ex_now + "/" + count_of_ex.toString());
        timer.setText(toTime(time));
        residue.setText(toTime(time_summary));
        sec_on = time;
        time_on = time_summary;
        switcher = "exercise";
        name.setText(name_of_timer + "\n" + exes.get(0));
    }
    public String plusZero(Integer i) {
        if (i > 9) {
            return i.toString();
        } else {
            return "0" + i.toString();
        }
    }
    public String toTime(Integer k) {
        int min_to_time = k/60;
        int sec_to_time = k%60;
        return plusZero(min_to_time) + ":" + plusZero(sec_to_time);
    }
}