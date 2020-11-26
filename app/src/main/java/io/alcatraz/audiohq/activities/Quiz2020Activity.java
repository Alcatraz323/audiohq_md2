package io.alcatraz.audiohq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import io.alcatraz.audiohq.Constants;
import io.alcatraz.audiohq.R;
import io.alcatraz.audiohq.adapters.SimplePagerAdapter;
import io.alcatraz.audiohq.beans.preset.NormalViewPage;
import io.alcatraz.audiohq.extended.CompatWithPipeActivity;
import io.alcatraz.audiohq.utils.MD5Utils;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;

public class Quiz2020Activity extends CompatWithPipeActivity {
    private final String MATH_QUIZ_BLURRY_TOKEN_1 = "d0524d90dbcd80e46bc562d75b368c6f";
    private final String MATH_QUIZ_BLURRY_TOKEN_2 = "dd6574f332de9dfa6aaf568b18df46df";
    private final String MATH_QUIZ_BLURRY_TOKEN_3 = "2415bbcdd5ed6a6c66aeb381befc074e";
    private final String MATH_QUIZ_EXACT_TOKEN_1 = "4e502b629d15c4b58775e9dd6fde5dd9";
    private final String MATH_QUIZ_EXACT_TOKEN_2 = "80bf0d3dcfa49c6b0cced600033998f7";
    private final String MATH_QUIZ_GUESSING_TOKEN = "357b64393204e754d2717c28a792a47f";

    private final String SUDOKU_QUIZ_ROW_1_TOKEN = "842ce50feec765a3cf81f15214549471";
    private final String SUDOKU_QUIZ_ROW_2_TOKEN = "6491bcab2f606cd51cdf242ac4bc2416";
    private final String SUDOKU_QUIZ_ROW_3_TOKEN = "237363c8e521a932a879af06886274ec";
    private final String SUDOKU_QUIZ_ROW_5_TOKEN = "99de63fe34c5046d056cce790159343c";
    private final String SUDOKU_QUIZ_ROW_6_TOKEN = "f9411ca6b95f30948007690b8940c52d";
    private final String SUDOKU_QUIZ_ROW_7_TOKEN = "e79666cba57e6b3748e17760f242f891";
    private final String SUDOKU_QUIZ_ROW_8_TOKEN = "cbecf35fbba47bdfd08453bef14f1a9b";
    private final String SUDOKU_QUIZ_ROW_9_TOKEN = "71ae9da085f2f3494ae32e0b2c0dd2c8";

    private final String[] SUDOKU_KEY = {SUDOKU_QUIZ_ROW_1_TOKEN, SUDOKU_QUIZ_ROW_2_TOKEN,
            SUDOKU_QUIZ_ROW_3_TOKEN, SUDOKU_QUIZ_ROW_5_TOKEN, SUDOKU_QUIZ_ROW_6_TOKEN,
            SUDOKU_QUIZ_ROW_7_TOKEN, SUDOKU_QUIZ_ROW_8_TOKEN, SUDOKU_QUIZ_ROW_9_TOKEN};

    //Math
    EditText math_result;
    FloatingActionButton math_submit;

    //Sudoku
    EditText row_1;
    EditText row_2;
    EditText row_3;
    EditText row_5;
    EditText row_6;
    EditText row_7;
    EditText row_8;
    EditText row_9;
    List<EditText> et_rows = new LinkedList<>();
    FloatingActionButton sudoku_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2020_quiz);
        initViews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        ViewPager pager = findViewById(R.id.quiz_2020_pager);
        Toolbar toolbar = findViewById(R.id.quiz_2020_toolbar);
        setSupportActionBar(toolbar);
        View root1 = getLayoutInflater().inflate(R.layout.quiz_2020_math, null);
        View root2 = getLayoutInflater().inflate(R.layout.quiz_2020_sudoku, null);
        bindViews(root1, root2);
        NormalViewPage page1 = new NormalViewPage();
        page1.setTitle(getString(R.string.preset_tab_all_apps));
        page1.setView(root1);
        NormalViewPage page2 = new NormalViewPage();
        page2.setTitle(getString(R.string.preset_tab_has_set));
        page2.setView(root2);
        List<NormalViewPage> pages = new LinkedList<>();
        pages.add(page1);
        pages.add(page2);
        SimplePagerAdapter adapter = new SimplePagerAdapter(this, pages);
        pager.setAdapter(adapter);
    }

    private void bindViews(View root1, View root2) {
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        math_result = root1.findViewById(R.id.quiz_2020_math_answer);
        math_submit = root1.findViewById(R.id.quiz_2020_math_submit);
        row_1 = root2.findViewById(R.id.quiz_2020_sudoku_row_1);
        row_2 = root2.findViewById(R.id.quiz_2020_sudoku_row_2);
        row_3 = root2.findViewById(R.id.quiz_2020_sudoku_row_3);
        row_5 = root2.findViewById(R.id.quiz_2020_sudoku_row_5);
        row_6 = root2.findViewById(R.id.quiz_2020_sudoku_row_6);
        row_7 = root2.findViewById(R.id.quiz_2020_sudoku_row_7);
        row_8 = root2.findViewById(R.id.quiz_2020_sudoku_row_8);
        row_9 = root2.findViewById(R.id.quiz_2020_sudoku_row_9);
        et_rows.add(row_1);
        et_rows.add(row_2);
        et_rows.add(row_3);
        et_rows.add(row_5);
        et_rows.add(row_6);
        et_rows.add(row_7);
        et_rows.add(row_8);
        et_rows.add(row_9);
        sudoku_submit = root2.findViewById(R.id.quiz_2020_sudoku_submit);
        bindListeners();
    }

    private void bindListeners() {
        math_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = math_result.getText().toString().trim().replaceAll("[\t\n ]", "");
                if (!TextUtils.isEmpty(result)) {
                    String encrypt_1 = MD5Utils.digest(result);
                    switch (encrypt_1) {
                        case MATH_QUIZ_BLURRY_TOKEN_1:
                        case MATH_QUIZ_BLURRY_TOKEN_2:
                        case MATH_QUIZ_BLURRY_TOKEN_3:
                        case MATH_QUIZ_EXACT_TOKEN_1:
                        case MATH_QUIZ_EXACT_TOKEN_2:
                            toast(R.string.quiz_2020_success);
                            passQuiz();
                            return;
                        case MATH_QUIZ_GUESSING_TOKEN:
                            toast(R.string.quiz_2020_guess_success);
                            passQuiz();
                            return;
                    }
                }
                toast(R.string.quiz_2020_failure);
            }
        });

        sudoku_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSudoku()){
                    toast(R.string.quiz_2020_success);
                    passQuiz();
                }else {
                    toast(R.string.quiz_2020_failure);
                }
            }
        });

    }

    private boolean checkSudoku() {
        for (int i = 0; i < et_rows.size(); i++) {
            String result = et_rows.get(i).getText().toString().trim().replaceAll("[\t\n ]", "");
            if (!TextUtils.isEmpty(result)) {
                String encrypt_1 = MD5Utils.digest(result);
                String encrypt_2 = MD5Utils.digest(encrypt_1);
                if(encrypt_2.equals(SUDOKU_KEY[i])) {
                    if (i != et_rows.size() - 1) {
                        continue;
                    } else {
                        return true;
                    }
                }
            }
            break;
        }
        return false;
    }

    private void passQuiz() {
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        spfu.put(this, Constants.PREF_SHOW_THEME_SETTINGS, true);
        spfu.put(this, Constants.PREF_SHOW_ANNIVERSARY_2020_FOREGROUND, true);
        spfu.put(this, Constants.PREF_SHOW_ANNIVERSARY_2020_QUIZ_CONTENT, false);
        restartToApply();
    }

    private void restartToApply() {
        finish();
        Intent it = new Intent(this, SetupActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
    }
}
