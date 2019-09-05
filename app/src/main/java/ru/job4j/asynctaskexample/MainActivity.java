package ru.job4j.asynctaskexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button button;
    SampleAsyncTask sampleAsyncTask;
    private int initialCount=30;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        button = findViewById(R.id.buttonStart);
        if (savedInstanceState != null) {
            count = savedInstanceState.getInt("count", -1);
            if (count > 0) {
                sampleAsyncTask=new SampleAsyncTask(this,count);
                sampleAsyncTask.execute(initialCount);
            }
        }
    }

    public void startAsyncTask(View view) {
        progressBar.setVisibility(View.VISIBLE);
        if (sampleAsyncTask == null) {
            sampleAsyncTask = new SampleAsyncTask(this,0);
            sampleAsyncTask.execute(initialCount);
        }
    }

    private static class SampleAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<MainActivity> activityWeakReference;
        public int count;

        SampleAsyncTask(MainActivity act, int count) {
            activityWeakReference = new WeakReference<>(act);
            this.count=count;
        }

        @Override
        protected String doInBackground(Integer... integers) {
            while (count < integers[0]) {
                publishProgress((count * 100) / integers[0]);
                Log.i("asyncTask","asyncTask hashcode: "+ this.hashCode()+", count "+count);
                if(isCancelled())
                    break;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
            }
            return "Finish";
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityWeakReference.get();
            activity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MainActivity activity = activityWeakReference.get();
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            activity.progressBar.setProgress(0);
            activity.progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            MainActivity activity = activityWeakReference.get();
            activity.progressBar.setProgress(values[0]);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("count", sampleAsyncTask.count);
        sampleAsyncTask.cancel(true);
    }
}
