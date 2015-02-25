package com.clintonmedbery.stockquotes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;


public class MainActivity extends ActionBarActivity {

    //For log printing
    private static final String TAG ="Stock Quotes Log";

    public EditText stockText;
    public TextView symbolText;
    public TextView nameText;
    public TextView lastTradePriceText;
    public TextView lastTradeTimeText;
    public TextView changeText;
    public TextView weekText;
    public ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "STARTING APP");

        setContentView(R.layout.activity_main);
        stockText = (EditText) findViewById(R.id.stockText);
        symbolText = (TextView) findViewById(R.id.symbolText);
        nameText = (TextView) findViewById(R.id.nameText);
        lastTradePriceText = (TextView) findViewById(R.id.ltpText);
        lastTradeTimeText = (TextView) findViewById(R.id.lttText);
        changeText = (TextView) findViewById(R.id.changeText);
        weekText = (TextView) findViewById(R.id.weekText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        stockText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
        stockText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    Log.d(TAG, "TEXT: " + stockText.getText());
                    symbolText.setText("");
                    nameText.setText("");
                    lastTradePriceText.setText("");
                    lastTradeTimeText.setText("");
                    changeText.setText("");
                    weekText.setText("");
                    new LoadStocks().execute(stockText.getText().toString());

                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadStocks extends AsyncTask<String, Integer, String> {

        private Stock stock;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Load Progress Bar
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String stockSymbol = params[0];
            stock = new Stock(stockSymbol);
            try {
                stock.load();


            } catch (MalformedURLException error) {
                Log.v(TAG, "BAD URL");
            } catch (IOException error){
                Log.v(TAG, "IO EXCEPTION");
            }
            return "Task Complete";
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            String checkValidity = stock.getLastTradeTime();
            if (checkValidity.equals("N/A")){
                Context context = getApplicationContext();
                CharSequence text = "Symbol not found!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                progressBar.setVisibility(View.INVISIBLE);

            } else {
                symbolText.setText(stock.getSymbol());
                nameText.setText(stock.getName());
                lastTradePriceText.setText(stock.getLastTradePrice());
                lastTradeTimeText.setText(stock.getLastTradeTime());
                changeText.setText(stock.getChange());
                weekText.setText(stock.getRange());
                progressBar.setVisibility(View.INVISIBLE);

            }

        }
    }
}
