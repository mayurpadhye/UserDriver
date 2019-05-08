package com.ccube9.user.network;

import com.ccube9.user.login.LoginDataModel;
import com.ccube9.user.profile.UserProfile;
import com.ccube9.user.registration.UserDetails;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/cabbooking/app/User/user_registration")
    Call<UserDetails> doCreateUserWithField(@Field("first_name") String first_name,
                                            @Field("last_name") String last_name,
                                            @Field("email") String email_id,
                                            @Field("phone") String phone_number,
                                            @Field("password") String password,
                                            @Field("password_confirmation") String con_password,
                                            @Field("language") String lan,
                                            @Field("user_type") String u_type);

    @FormUrlEncoded
    @POST("/cabbooking/app/User/user_login")
    Call<LoginDataModel> doUserLogin(
                                     @Field("email_id") String email_id,
                                     @Field("password") String password);

    @FormUrlEncoded
    @POST("/cabbooking/app/User/user_login")
    Call<UserProfile> update_user_data(
            @Field("user_id") String user_id,
            @Field("first_name") String first_name,
            @Field("last_name") String last_name,
            @Field("mobile_number") String mobile_number
            );

    @FormUrlEncoded
    @POST("/cabbooking/app/User/change_password")
    Call<UserProfile> change_password(
            @Field("user_id") String user_id,
            @Field("new_password") String new_password,
            @Field("old_password") String old_password);
}
