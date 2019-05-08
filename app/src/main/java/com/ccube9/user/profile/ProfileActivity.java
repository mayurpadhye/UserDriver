package com.ccube9.user.profile;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ccube9.user.R;
import com.ccube9.user.home.HomeActivity;
import com.ccube9.user.network.BaseUrl;
import com.ccube9.user.util.CustomUtil;
import com.ccube9.user.util.PrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TextView tv_change,tv_f_name,tv_l_name,tv_email,tv_mobile,tv_title;
    ImageView iv_back,prof_pic;
    Dialog change_pass_dialog;
    FloatingActionButton fb_edit;
    StringRequest stringRequest;
    RequestQueue requestQueue;
    EditText et_old_password,et_new_pass,et_c_password;
    Button btn_change_pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        requestQueue= Volley.newRequestQueue(ProfileActivity.this);
        onClick();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
        }

    private void setData() {
      tv_f_name.setText(PrefManager.getUserFirstName(ProfileActivity.this));
      tv_l_name.setText(PrefManager.getUserLastName(ProfileActivity.this));
      tv_email.setText(PrefManager.getUserEmail(ProfileActivity.this));
      tv_mobile.setText(PrefManager.getContact(ProfileActivity.this));

        String img=PrefManager.getProfPic(ProfileActivity.this);
        Log.d("dffddsf",img);
        if(!img.equals("null")){
           Picasso.with(this).load(BaseUrl.IMAGE_URL.concat(img)).fit().into(prof_pic);
       }else{
            Picasso.with(this).load("http://driver.ccube9.com/public/profile/placeholder-profile.jpg").fit().into(prof_pic);
       }

    }

    private void onClick() {



      iv_back.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
              finish();
          }
      });
        fb_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
            }
        });

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                change_pass_dialog=new Dialog(ProfileActivity.this);
                change_pass_dialog.setContentView(R.layout.dialog_change_password);
                et_old_password=change_pass_dialog.findViewById(R.id.et_old_password);
                et_new_pass=change_pass_dialog.findViewById(R.id.et_new_pass);
                et_c_password=change_pass_dialog.findViewById(R.id.et_c_password);
                btn_change_pass=change_pass_dialog.findViewById(R.id.btn_change_pass);

                change_pass_dialog.show();

                btn_change_pass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passValidation();
                    }
                });
            }
        });

    }


    public void passValidation()
    {
        if (et_old_password.getText().toString().trim().length()==0)
        {
            et_old_password.setError(getResources().getString(R.string.enter_old_pass));
            return;
        }
        if (et_old_password.getText().toString().trim().length()<8)
        {
            et_old_password.setError(getResources().getString(R.string.eight_cchar_pass));
            return;
        }

        if (et_new_pass.getText().toString().trim().length()==0)
        {
            et_new_pass.setError(getResources().getString(R.string.enter_new_pass));
            return;

        }
        if (et_new_pass.getText().toString().trim().length()<8)
        {
            et_new_pass.setError(getResources().getString(R.string.eight_cchar_pass));
            return;
        }

        if (et_c_password.getText().toString().trim().length()==0)
        {
            et_c_password.setError(getResources().getString(R.string.enter_confirm_pass));
            return;
        }
        else  if (!et_new_pass.getText().toString().trim().equals(et_c_password.getText().toString().trim()))
            {
                Toast.makeText(this, ""+getResources().getString(R.string.pass_not_matched), Toast.LENGTH_SHORT).show();
                 return;

            }
        else

        {

            CustomUtil.ShowDialog(ProfileActivity.this);
            stringRequest =new StringRequest(Request.Method.POST, BaseUrl.BASE_URL.concat("user/change"), new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    CustomUtil.DismissDialog(ProfileActivity.this);
                    Log.d("sads",response);
                    JSONObject jsonObject= null;

                    try {
                        jsonObject = new JSONObject(response);
                        if(jsonObject.getString("status").equals("0")){
                            Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                        }
                        else if(jsonObject.getString("status").equals("1")){
                            Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            change_pass_dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    CustomUtil.DismissDialog(ProfileActivity.this);
                    Log.d("sads",volleyError.toString());
                    requestQueue.cancelAll(stringRequest);

                    String message = null;
                    if (volleyError instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                    } else if (volleyError instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    if (message != null) {
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(ProfileActivity.this, "An error occured", Toast.LENGTH_SHORT).show();


                }
            }){



                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> param=new HashMap<>();

                    param.put("language","en");
                    param.put("user_type","1");
                    param.put("old_password",et_old_password.getText().toString());
                    param.put("password",et_new_pass.getText().toString());
                    param.put("password_confirmation",et_c_password.getText().toString());
                    //param.put("email",PrefManager.getUserEmail(ProfileActivity.this));
                    param.put("user_id",PrefManager.getUserId(ProfileActivity.this));
                    param.put("api_token",PrefManager.getApiToken(ProfileActivity.this));

                    return param;}


            };

            requestQueue.add(stringRequest);
        }
    }

    public void changePassword()
    {
//        CustomUtil.ShowDialog(ProfileActivity.this);
//        ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
//        Call<UserProfile> call3 = apiInterface.change_password(PrefManager.getUserId(ProfileActivity.this),et_new_pass.getText().toString().trim(),et_old_password.getText().toString());
//        call3.enqueue(new Callback<UserProfile>() {
//            @Override
//            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
//                UserProfile userList = response.body();
//                String status = userList.status;
//
//
//                if (status.equals("1"))
//                {
//                    String message = userList.message;
//                    Toast.makeText(ProfileActivity.this, ""+message, Toast.LENGTH_SHORT).show();
//
//                }
//                else
//                {
//                    String message = userList.message;
//                    Toast.makeText(ProfileActivity.this, ""+message, Toast.LENGTH_SHORT).show();
//
//                }
//                change_pass_dialog.dismiss();
//                CustomUtil.DismissDialog(ProfileActivity.this);
//            }
//
//            @Override
//            public void onFailure(Call<UserProfile> call, Throwable t) {
//                call.cancel();
//                change_pass_dialog.dismiss();
//                CustomUtil.DismissDialog(ProfileActivity.this);
//            }
//        });
    }//changePaswordClose


    public void initView()
    {

        prof_pic=findViewById(R.id.profpic);
        tv_change=findViewById(R.id.tv_change);
        fb_edit=findViewById(R.id.fb_edit);
        tv_f_name=findViewById(R.id.tv_f_name);
        tv_l_name=findViewById(R.id.tv_l_name);
        tv_email=findViewById(R.id.tv_email);
        tv_mobile=findViewById(R.id.tv_mobile);
        tv_title=findViewById(R.id.tv_title);
        iv_back=findViewById(R.id.iv_back);
        tv_title.setText(getResources().getString(R.string.profile));


    }//initViewClose
}
