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

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.huawei.hms.support.api.pay.OrderResult;
import com.huawei.hms.support.api.pay.PayResultInfo;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PaySignUtil
 *
 * @since 2020/12/2
 */
public class PaySignUtil {
    private static final String TAG = "PaySignUtil";

    private static final String SIGN_ALGORITHMS = "SHA256WithRSA";
    /**
     * 对支付结果的签名进行校验
     * @param rst 支付结果
     * @param pubKey 公钥
     * @return 是否校验通过
     */
    public static boolean checkSign(PayResultInfo rst, String pubKey) {

        if (rst == null || pubKey == null) {
            return false;
        }

        Map<String, Object> paramsa = new HashMap<String, Object>();

        // 必选参数
        paramsa.put("returnCode", rst.getReturnCode());
        paramsa.put("userName", rst.getUserName());
        paramsa.put("requestId", rst.getRequestId());
        paramsa.put("amount", rst.getAmount());
        paramsa.put("time", rst.getTime());

        // 可选参数
        paramsa.put("orderID", rst.getOrderID());
        paramsa.put("withholdID", rst.getWithholdID());
        paramsa.put("errMsg", rst.getErrMsg());

        String noSignStr = getNoSign(paramsa, false);
        return doCheck(noSignStr, rst.getSign(), pubKey);
    }

    /**
     * 根据参数map获取待签名字符串
     * @param params 待签名参数map
     * @param includeEmptyParam 是否包含值为空的参数：
     *                          与 HMS-SDK 支付能力交互的签名或验签，需要为false（不包含空参数）
     *                          由华为支付服务器回调给开发者的服务器的支付结果验签，需要为true（包含空参数）
     * @return 待签名字符串
     */
    private static String getNoSign(Map<String, Object> params, boolean includeEmptyParam) {
        //对参数按照key做升序排序，对map的所有value进行处理，转化成string类型
        //拼接成key=value&key=value&....格式的字符串
        StringBuilder content = new StringBuilder();

        // 按照key做排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String value = null;
        Object object = null;
        boolean isFirstParm = true;
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            object = params.get(key);

            if (object == null) {
                value = "";
            }else if (object instanceof String) {
                value = (String) object;
            } else {
                value = String.valueOf(object);
            }

            if (includeEmptyParam || !TextUtils.isEmpty(value)) {
                content.append((isFirstParm ? "" : "&") + key + "=" + value);
                isFirstParm = false;
            } else {
                continue;
            }
        }

        //待签名的字符串
        return content.toString();
    }

    /**
     * 校验签名信息
     * @param noSignStr 待校验未字符串
     * @param sign 签名字符串
     * @param publicKey 公钥
     * @return 是否校验通过
     */
    public static boolean doCheck(String noSignStr, String sign, String publicKey) {

        if (sign == null || noSignStr == null || publicKey == null) {
            return false;
        }

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(publicKey, Base64.DEFAULT);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

            signature.initVerify(pubKey);
            signature.update(noSignStr.getBytes("UTF-8"));

            return signature.verify(Base64.decode(sign, Base64.DEFAULT));
        } catch (Exception e) {
            //  这里是安全算法，为避免出现异常时泄露加密信息，这里不打印具体日志
            Log.e(TAG, "doCheck error");
        }
        return false;
    }

    public static String getSignData(Map<String, Object> params) {
        StringBuffer content = new StringBuffer();
        // 按照key做排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String value = null;
        Object object = null;
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            object = params.get(key);
            if (object instanceof String) {
                value = (String) object;
            } else {
                value = String.valueOf(object);
            }

            if (value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            } else {
                continue;
            }
        }
        return content.toString();
    }

    public static String rsaSign(String content) {
        if (null == content) {
            return null;
        }
        String privateKey = Constant.getPrivateKey();
        String charset = "UTF-8";
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(priKey);
            signature.update(content.getBytes(charset));
            byte[] signed = signature.sign();
            return Base64.encodeToString(signed, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "sign NoSuchAlgorithmException");
        } catch (InvalidKeySpecException e) {
            Log.e(TAG, "sign InvalidKeySpecException");
        } catch (InvalidKeyException e) {
            Log.e(TAG, "sign InvalidKeyException");
        } catch (SignatureException e) {
            Log.e(TAG, "sign SignatureException");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "sign UnsupportedEncodingException");
        }
        return null;
    }

    /**
     * 对查询普通订单结果签名进行校验
     * @param result 查询订单结果
     * @param pubKey 公钥
     * @return 是否校验通过
     */
    public static boolean checkSign(OrderResult result, String pubKey) {

        if (result == null || pubKey == null) {
            return false;
        }

        Map<String, Object> paramsa = new HashMap<String, Object>();
        //必选参数
        paramsa.put("returnCode", result.getReturnCode());
        paramsa.put("returnDesc", result.getReturnDesc());

        //可选参数
        paramsa.put("requestId", result.getRequestId());
        paramsa.put("orderID", result.getOrderID());
        paramsa.put("status", result.getOrderStatus());
        paramsa.put("orderTime", result.getOrderTime());
        paramsa.put("tradeTime", result.getTradeTime());

        String noSignStr = getNoSign(paramsa, false);
        return doCheck(noSignStr, result.getSign(), pubKey);
    }

}
