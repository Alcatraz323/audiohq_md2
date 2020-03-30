package io.alcatraz.audiohq;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Easter {
    private Context context;
    private int[] target = {0,1,0,1,0,0,1,0,1,0};
    private int current = 0;
    public Easter(Context context){
        this.context = context;
    }

    public void shortClick(){
        int expect = target[current];
        if(expect == 0){
            if(current == target.length-1){
                showEaster();
                clearCounter();
            }else {
                current++;
            }
        }else {
            clearCounter();
        }
    }

    public void longClick(){
        int expect = target[current];
        if(expect == 1){
            if(current == target.length-1){
                showEaster();
                clearCounter();
            }else {
                current++;
            }
        }else {
            clearCounter();
        }
    }

    public void showEaster(){
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://alcatraz323.github.io/audiohq_easter")));
    }

    public void clearCounter(){
        current = 0;
    }
}
