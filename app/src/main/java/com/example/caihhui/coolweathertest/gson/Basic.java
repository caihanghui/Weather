package com.example.caihhui.coolweathertest.gson;

import com.google.gson.annotations.SerializedName;
    //@SerializedName 注解的方式来让JSON字段和JAVA字段之间建立映射关系
public class Basic {
   //表示城市名
    @SerializedName("city")
    public String cityName;
    //城市对应的天气ID
    @SerializedName("id")
    public String weatherId;



    public Update update;
        //loc 表示天气的更新时间
    public class Update {

        @SerializedName("loc")
        public String updateTime;

    }

}
