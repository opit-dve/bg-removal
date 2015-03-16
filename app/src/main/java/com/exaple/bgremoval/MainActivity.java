package com.exaple.bgremoval;

import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private GraphicView mGraphicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        mGraphicView = (GraphicView) findViewById(R.id.graphic);
        mGraphicView.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.taz));

        // radio buttons
        findViewById(R.id.btn_erase).setOnClickListener(MainActivity.this);
        findViewById(R.id.btn_restore).setOnClickListener(MainActivity.this);
        findViewById(R.id.btn_clear).setOnClickListener(MainActivity.this);

        // Default to erase
        ((RadioButton)findViewById(R.id.btn_erase)).setChecked(true);
        mGraphicView.setTouchMode(GraphicView.TouchMode.ERASE);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_erase:
                mGraphicView.setTouchMode(GraphicView.TouchMode.ERASE);
            break;

            case R.id.btn_restore:
                mGraphicView.setTouchMode(GraphicView.TouchMode.RESTORE);

            case R.id.btn_clear:
                //mGraphicView.setTouchMode(GraphicView.TouchMode.CLEAR);
            break;
        }
    }
}
