package com.ccube9.user.registration;

import android.content.Intent;
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
import com.ccube9.user.login.LoginActivity;
import com.ccube9.user.network.BaseUrl;
import com.ccube9.user.util.CustomUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
Button btn_sign_up;
EditText et_f_name,et_l_name,et_email,et_mobile,et_password,et_c_password;
    TextView tv_title;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
        requestQueue= Volley.newRequestQueue(SignUpActivity.this);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(SignUpActivity.this,AddPaymentActivity.class));
                submitForm();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,ChooseRegistrationRoleActivity.class));
                finish();
            }
        });

    }//onCreateClose

    public void submitForm()
    {
        //  SignUp();
        if (isValidateFirstName(et_f_name.getText().toString().trim())==false)
        {
            if (et_l_name.getText().toString().trim().length()==0)
            {
                et_f_name.setError(getResources().getString(R.string.enter_first_name));
            }
            else
            {
                et_f_name.setError(getResources().getString(R.string.first_name_error));
            }

            return;
        }
        if (isValidateLastName(et_l_name.getText().toString().trim())==false)
        {
            if (et_l_name.getText().toString().trim().length()==0)
            {
                et_l_name.setError(getResources().getString(R.string.enter_last_name));
            }
            else
                et_l_name.setError(getResources().getString(R.string.last_name_error));
            return;
        }
        if (!isValidMail(et_email.getText().toString().trim()))
        {
            if (et_email.getText().toString().trim().length()==0)
            {
                et_email.setError(getResources().getString(R.string.enter_email_id));
            }
            else
            {
                et_email.setError(getResources().getString(R.string.email_error));
            }

            return;
        }

        if (!isValidMobile(et_mobile.getText().toString().trim()))
        {
            if (et_mobile.getText().toString().trim().length()==0)
            {
                et_mobile.setError(getResources().getString(R.string.enter_mobile_no));
            }
            else
            {
                et_mobile.setError(getResources().getString(R.string.mobile_error));
            }

            return;
        }

        if (!validatePassword(et_password.getText().toString().trim()))
        {
            et_password.setError(getResources().getString(R.string.password_error));
            return;
        }
        if (!validateConfirmPassword(et_c_password.getText().toString().trim()))
        {
            if (et_c_password.getText().toString().trim().length()==0)
            {
                et_c_password.setError(getResources().getString(R.string.enter_confirm_pass));
            }
            else
                et_c_password.setError(getResources().getString(R.string.c_pass_error));
            return;
        }
        createUser();
    }

    private boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        if(!check) {
            et_email.setError("Not Valid Email");
        }
        return check;
    }
    private boolean isValidMobile(String phone) {
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 8 || phone.length() > 15) {
                // if(phone.length() != 10) {
                check = false;
                et_mobile.setError("Not Valid Number");
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }
    public  boolean isValidateFirstName( String firstName )
    {
        if (firstName.trim().equals(""))
            return  false;
        else
            return firstName.matches("[a-zA-Z]*");

    } // end method validateFirstName

    // validate last name
    public  boolean isValidateLastName( String lastName )
    {
        if (lastName.trim().equals(""))
            return  false;
        else
            return lastName.matches("[a-zA-Z]*");
    }

    public boolean validatePassword( String password)
    {
        if (password.length()>0)
        {
            return true;
        }
        else
            return false;
    }

    public boolean validateConfirmPassword( String Cpassword)
    {
        if (Cpassword.length()>0 && Cpassword.equals(et_password.getText().toString()))
        {
            return true;
        }
        else
            return false;
    }
    public void createUser()
    {


       CustomUtil.ShowDialog(SignUpActivity.this);

       stringRequest=new StringRequest(Request.Method.POST, BaseUrl.BASE_URL.concat("register"), new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {
               CustomUtil.DismissDialog(SignUpActivity.this);
               Log.d("dsfd",response);
               try {
                   JSONObject jsonObject=new JSONObject(response);
                   if(jsonObject.getString("status").equals("1")) {
                       Toast.makeText(SignUpActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                       finish();
                   }
                   else if(jsonObject.getString("status").equals("0")) {

                       JSONObject jsonObject1=jsonObject.getJSONObject("message");
                       if(jsonObject1.has("phone")){
                           JSONArray jsonArray=jsonObject1.getJSONArray("phone");

                           Toast.makeText(SignUpActivity.this, jsonArray.get(0).toString(), Toast.LENGTH_SHORT).show();
                       }
                       else if(jsonObject1.has("email")){
                           JSONArray jsonArray1=jsonObject1.getJSONArray("email");
                           Toast.makeText(SignUpActivity.this,  jsonArray1.get(0).toString(), Toast.LENGTH_SHORT).show();
                       }
                         else if(jsonObject1.has("password")){
                           JSONArray jsonArray1=jsonObject1.getJSONArray("password");
                           Toast.makeText(SignUpActivity.this,  jsonArray1.get(0).toString(), Toast.LENGTH_SHORT).show();
                       }
                   }
                   } catch (JSONException e) {
                   e.printStackTrace();
               }

           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError volleyError) {
               CustomUtil.DismissDialog(SignUpActivity.this);

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
                   Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
               } else
                   Toast.makeText(SignUpActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
           }
       })
       {

           @Override
           protected Map<String, String> getParams() {
               Map<String, String> params = new HashMap<String, String>();
               params.put("first_name", et_f_name.getText().toString());
               params.put("last_name", et_l_name.getText().toString());
               params.put("phone", et_mobile.getText().toString());
               params.put("email", et_email.getText().toString());
               params.put("password", et_password.getText().toString());
               params.put("password_confirmation", et_c_password.getText().toString());
               params.put("language","en");
               params.put("user_type","1");

               return params;
           }
       };

       requestQueue.add(stringRequest);


    }//createUser

      public void initView()
      {
          tv_title=findViewById(R.id.tv_title);
          iv_back=findViewById(R.id.iv_back);
          tv_title.setText(getResources().getString(R.string.create_an_account));
          et_c_password=findViewById(R.id.et_c_password);
          et_f_name=findViewById(R.id.et_f_name);
          et_l_name=findViewById(R.id.et_l_name);
          et_email=findViewById(R.id.et_email);
          et_mobile=findViewById(R.id.et_mobile);
          et_password=findViewById(R.id.et_password);
          btn_sign_up=findViewById(R.id.btn_sign_up);

      }//initViewClose
}



//stringRequest=new StringRequest(Request.Method.POST, Baseurl.BASE_URL.concat(""), new Response.Listener<String>() {
//@Override
//public void onResponse(String response) {
//
//        }
//        }, new Response.ErrorListener() {
//@Override
//public void onErrorResponse(VolleyError error) {
//        requestQueue.cancelAll(stringRequest);
//
//        }
//        })
//        {
//
//@Override
//protected Map<String, String> getParams() {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("mobile", Lmobno);
//
//        return params;
//        }
//        };
//
//        requestQueue.add(stringRequest);