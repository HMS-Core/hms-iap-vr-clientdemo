/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hms.iap.vrpaydemo;

/**
 * Constant
 * 支付相关参数及公私钥，需商户根据自己的应用进行配置
 *
 * @since 2020/12/2
 */
public class Constant {
    /**
     *  APPID
     */
    private static final String APPID = "";
    /**
     *  商户ID
     */
    private static final String MERCHANTID = "";

    /**
     * 私钥 非单机应用一定要在服务器端储存签名私钥
     */
    private static final String PRIVATE_KEY = "";
    /**
     * 公钥
     */
    private static final String PUBLIC_KEY = "";

    public static String getAppID() {
        return APPID;
    }

    public static String getPrivateKey() {
        return PRIVATE_KEY;
    }

    public static String getPublicKey() {
        return PUBLIC_KEY;
    }

    public static String getMerchantID() {
        return MERCHANTID;
    }
}
