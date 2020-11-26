package io.alcatraz.audiohq;

import io.alcatraz.audiohq.activities.AboutActivity;
import io.alcatraz.audiohq.utils.SharedPreferenceUtil;

public class Easter2020 {
    private AboutActivity activity;
    private int[] target_1 = {0, 1, 1, 1, 1, 0, 0, 0, 1};
    private int[] target_2 = {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1};
    private int[] target;
    private int current = 0;
    private boolean showQuiz;

    public Easter2020(AboutActivity activity) {
        this.activity = activity;
        SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
        showQuiz = (boolean) spfu.get(activity, Constants.PREF_SHOW_ANNIVERSARY_2020_QUIZ_CONTENT, Constants.DEFAULT_VALUE_PREF_SHOW_ANNIVERSARY_2020_QUIZ_CONTENT);
        if (showQuiz) {
            target = target_2;
        } else {
            target = target_1;
        }
    }

    public void shortClick() {
        int expect = target[current];
        if (expect == 0) {
            if (current == target.length - 1) {
                showEaster();
                activity.clearAnniversaryCode();
                activity.finish();
            } else {
                current++;
            }
        } else {
            clearCounter();
        }
    }

    public void longClick() {
        int expect = target[current];
        if (expect == 1) {
            if (current == target.length - 1) {
                showEaster();
                activity.clearAnniversaryCode();
                activity.finish();
            } else {
                current++;
            }
        } else {
            clearCounter();
        }
    }

    public void showEaster() {
        if(!showQuiz) {
            SharedPreferenceUtil spfu = SharedPreferenceUtil.getInstance();
            spfu.put(activity, Constants.PREF_SHOW_ANNIVERSARY_2020_QUIZ_CONTENT, true);
            activity.toast(R.string.morse_2020_success_1);
        }else {
            activity.toast(R.string.quiz_2020_quiz_toast_hint);
        }
        activity.finish();
    }

    public void clearCounter() {
        current = 0;
        activity.clearAnniversaryCode();
        if(!showQuiz) {
            activity.toast(R.string.morse_2020_failure);
        }
    }
}
