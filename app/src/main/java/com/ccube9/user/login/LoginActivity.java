package com.ccube9.user.login;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
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
import com.ccube9.user.registration.ChooseRegistrationRoleActivity;
import com.ccube9.user.registration.SignUpActivity;
import com.ccube9.user.util.CustomUtil;
import com.ccube9.user.util.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    TextView tv_title,forgot_pass;
    ImageView iv_back;
    EditText et_email, et_password;
    Button btn_connect;
    TextView tv_register;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        requestQueue= Volley.newRequestQueue(LoginActivity.this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ChooseRegistrationRoleActivity.class));
                finish();
            }
        });


        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
//                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                finish();
            }
        });
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                 final Dialog  dialog=new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.dialog_forgot_pass);
                dialog.show();

                 Button email_sub=dialog.findViewById(R.id.btn_submit);
                 final EditText et_dialog_mail=dialog.findViewById(R.id.et_email);

                email_sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (!isValidMail(et_dialog_mail.getText().toString().trim(),et_dialog_mail)) {
                            if (et_dialog_mail.getText().toString().trim().length() == 0) {
                                et_dialog_mail.setError(getResources().getString(R.string.enter_email_id));
                            } else {
                                et_dialog_mail.setError(getResources().getString(R.string.email_error));
                            }

                            return;
                        }
                        else {
                            CustomUtil.ShowDialog(LoginActivity.this);
                            stringRequest = new StringRequest(Request.Method.POST, BaseUrl.BASE_URL.concat("user/forget"), new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    CustomUtil.DismissDialog(LoginActivity.this);
                                    Log.d("fghfghgf", response);

                                    try {
                                        JSONObject jsonObject=new JSONObject(response);
                                        if  (jsonObject.getString("status").equals("1")){
                                            Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                        else if(jsonObject.getString("status").equals("0")){
                                            Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    CustomUtil.DismissDialog(LoginActivity.this);
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
                                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(LoginActivity.this, "An error occured", Toast.LENGTH_SHORT).show();


                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> param = new HashMap<>();

                                    param.put("language", "en");
                                    param.put("user_type", "1");
                                    param.put("email", et_dialog_mail.getText().toString());
                                    return param;
                                }
                            };

                            requestQueue.add(stringRequest);
                        }
                    }
                });
            }
        });

    }

    public void initView() {
        tv_title = findViewById(R.id.tv_title);
        tv_register = findViewById(R.id.tv_register);
        iv_back = findViewById(R.id.iv_back);
        et_email = findViewById(R.id.et_email);
        btn_connect = findViewById(R.id.btn_connect);
        et_password = findViewById(R.id.et_password);
        forgot_pass= findViewById(R.id.forgot_pass);
        tv_title.setText(getResources().getString(R.string.login));

    }//initViewClose

    public void submitForm() {
        if (!isValidMail(et_email.getText().toString().trim(),et_email)) {
            if (et_email.getText().toString().trim().length() == 0) {
                et_email.setError(getResources().getString(R.string.enter_email_id));
            } else {
                et_email.setError(getResources().getString(R.string.email_error));
            }

            return;
        }

        if (!validatePassword(et_password.getText().toString().trim())) {
            et_password.setError(getResources().getString(R.string.password_error));
            return;
        }


        if(checkPermission()==false){
            requestPermission();
        }else {
            Login();
        }
    }//subMitFormClose

    public void Login() {


       CustomUtil.ShowDialog(LoginActivity.this);
//        ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
//        Call<LoginDataModel> call3 = apiInterface.doUserLogin(et_email.getText().toString().trim(), et_password.getText().toString());
//        call3.enqueue(new Callback<LoginDataModel>() {
//            @Override
//            public void onResponse(Call<LoginDataModel> call, Response<LoginDataModel> response) {
//                LoginDataModel userList = response.body();
//                String status = userList.status;
//
//
//                if (status.equals("1")) {
//                    String message = userList.message;
//                    Toast.makeText(LoginActivity.this, "" + userList.data.get(0).user_id, Toast.LENGTH_SHORT).show();
//                    PrefManager.setUserId(LoginActivity.this, "" + userList.data.get(0).user_id);
//                    PrefManager.setUserName(LoginActivity.this, "" + userList.data.get(0).first_name+" "+userList.data.get(0).last_name);
//                    PrefManager.setUserEmail(LoginActivity.this, "" + userList.data.get(0).email_id);
//                    PrefManager.setUserMobile(LoginActivity.this, "" + userList.data.get(0).phone_no);
//                    PrefManager.setUserFirstName(LoginActivity.this, "" + userList.data.get(0).first_name);
//                    PrefManager.setUserLastName(LoginActivity.this, "" + userList.data.get(0).last_name);
//                    PrefManager.setIsLogin(LoginActivity.this, true);
//                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                    finish();
//
//                }
//
//                CustomUtil.DismissDialog(LoginActivity.this);
//            }
//
//            @Override
//            public void onFailure(Call<LoginDataModel> call, Throwable t) {
//                call.cancel();
//                CustomUtil.DismissDialog(LoginActivity.this);
//            }
//        });


        stringRequest =new StringRequest(Request.Method.POST, BaseUrl.BASE_URL.concat("login"), new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                CustomUtil.DismissDialog(LoginActivity.this);
                Log.d("sads",response);
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("0")){
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    }
                    else if(jsonObject.getString("status").equals("1")){
                        Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            JSONObject jsonObject1=jsonObject.getJSONObject("data");
                    PrefManager.setUserId(LoginActivity.this, jsonObject1.getString("id"));
                    //PrefManager.setUserName(LoginActivity.this,  jsonObject1.getString(""));
                    PrefManager.setUserEmail(LoginActivity.this,  jsonObject1.getString("email"));
                    //PrefManager.setUserMobile(LoginActivity.this,  jsonObject1.getString(""));
                    PrefManager.setUserFirstName(LoginActivity.this,  jsonObject1.getString("first_name"));
                    PrefManager.setUserLastName(LoginActivity.this,  jsonObject1.getString("last_name"));
                    PrefManager.setProfPic(LoginActivity.this,  jsonObject1.getString("profile_pic"));
                    PrefManager.setContact(LoginActivity.this,  jsonObject1.getString("phone"));
                    PrefManager.setApiToken(LoginActivity.this,  jsonObject1.getString("api_token"));
                    PrefManager.setIsLogin(LoginActivity.this, true);

                            finish();
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("sads", String.valueOf(volleyError));
                CustomUtil.DismissDialog(LoginActivity.this);

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
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(LoginActivity.this, "An error occured", Toast.LENGTH_SHORT).show();


            }
        }){



            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();

                param.put("language","en");
                param.put("user_type","1");
                param.put("email",et_email.getText().toString());
                param.put("password",et_password.getText().toString());


                return param;}


        };

        requestQueue.add(stringRequest);



    }//LoginClose

    private boolean isValidMail(String email,EditText editText) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        if (!check) {
            editText.setError("Not Valid Email");
        }
        return check;
    }

    public boolean validatePassword(String password) {
        if (password.length() > 0) {
            return true;
        } else
            return false;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_PHONE_STATE)) {
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                    , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }

    private boolean checkPermission() {
        int result = (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION));
        int result2 = (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION));
        int result3 = (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE));

        if (result == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

}
