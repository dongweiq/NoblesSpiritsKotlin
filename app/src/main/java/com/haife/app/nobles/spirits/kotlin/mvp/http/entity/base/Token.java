package com.haife.app.nobles.spirits.kotlin.mvp.http.entity.base;


import com.haife.app.nobles.spirits.kotlin.BuildConfig;
import com.haife.app.nobles.spirits.kotlin.mvp.ui.utlis.MD5;

import javax.inject.Inject;

public class Token {

    @Inject
    public Token() {
        return;
    }

    String time = String.valueOf(System.currentTimeMillis());
    String uuid = "1";
    String sign = MD5.encryptMD5ToString(BuildConfig.App_CODE + time + uuid);
    String from = "android";
    String lang = "cn";
    String version = "2.1";

}
