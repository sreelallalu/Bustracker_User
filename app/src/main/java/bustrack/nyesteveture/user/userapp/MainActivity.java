package bustrack.nyesteveture.user.userapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{
private Button check;
    private EditText triPcode,vehiCle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.tripcode);
        check=(Button)findViewById(R.id.check_vehi);
        triPcode=(EditText)findViewById(R.id.editTexttrip);
        vehiCle=(EditText)findViewById(R.id.editTextvehi);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String trip=triPcode.getText().toString();
                final String vehicle=vehiCle.getText().toString();
                if(!trip.isEmpty()||!vehicle.isEmpty())
                {
                    HashMap<String,String> hashmap=new HashMap<String, String>();
                    hashmap.put("tag","viewposition");
                    hashmap.put("tripCode",trip);
                    hashmap.put("vehicleNo",vehicle);

                    PostResponseAsyncTask task=new PostResponseAsyncTask(MainActivity.this, hashmap, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            try {
                                JSONObject js=new JSONObject(s);

                                int suc=js.getInt("success");
                                if(suc==1)
                                {
                                    String position=js.getString("position");
                                    JSONObject gh=new JSONObject(position);
                                    String lati=gh.getString("latitude");
                                    String log=gh.getString("longitude");
                                    String dis=gh.getString("distance");
                                    String delay=gh.getString("delay");
                                  Intent io=new Intent(MainActivity.this,MapActivity.class);
                                    io.putExtra("lati",lati);
                                    io.putExtra("logi",log);
                                    io.putExtra("dis",dis);
                                    io.putExtra("delay",delay);
                                    io.putExtra("tripcode",trip);
                                    io.putExtra("busid",vehicle);

                                    startActivity(io);

                                    finish();

                                }
                                else {
                                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                    task.execute("http://bustracking.nyesteventuretech.com/Service/index.php\n");
                    task.setEachExceptionsHandler(new EachExceptionsHandler() {
                        @Override
                        public void handleIOException(IOException e) {

                        }

                        @Override
                        public void handleMalformedURLException(MalformedURLException e) {

                        }

                        @Override
                        public void handleProtocolException(ProtocolException e) {

                        }

                        @Override
                        public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {

                        }
                    });


                }



            }
        });

    }


}
