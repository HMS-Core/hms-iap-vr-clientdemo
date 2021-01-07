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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.huawei.hms.R;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.iap.entity.OrderStatusCode;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.pay.HwPayConstant;
import com.huawei.hms.support.api.entity.pay.OrderRequest;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.huawei.hms.support.api.pay.OrderResult;
import com.huawei.hms.support.api.pay.PayResult;
import com.huawei.hms.support.api.pay.PayResultInfo;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * MainActivity
 *
 * @since 2020/12/2
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG =  "VRShop MainActivity";

    private Button payBtn;

    private static final int REQUEST_PAY = 2000;

    private HuaweiApiClient mClient;

    // client连接
    private static final int CONNECT_STATUS_SUCCUSS = 0;

    private static final int CONNECT_STATUS_SUSSPAND = 1;

    private static final int CONNECT_STATUS_FAILED = 2;

    // 配置在华为开发者联盟的回调URL
    private static final String KEY_URL = "https://*****";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        payBtn = (Button) findViewById(R.id.btn_pay2);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPay();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private void toPay() {
        Context context = this.getApplicationContext();
        mClient = getApiClient(context);
        if (mClient != null && !mClient.isConnected()) {
            startConnect();
        }
    }

    private void startConnect() {
        Log.i(TAG, "startConnect");
        mClient.connect(MainActivity.this);
    }

    private HuaweiApiClient getApiClient(Context context) {
        return new HuaweiApiClient.Builder(context)
            .addApi(HuaweiPay.PAY_API)
            .addConnectionCallbacks(new HuaweiApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected() {
                    Log.i(TAG, "onConnected");
                    onConnectResult(CONNECT_STATUS_SUCCUSS);
                }

                @Override
                public void onConnectionSuspended(int i) {
                    Log.i(TAG, "onConnectionSuspended");
                    onConnectResult(CONNECT_STATUS_SUSSPAND);
                }
            })
            .addOnConnectionFailedListener(new HuaweiApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult connectionResult) {
                    Log.i(TAG, "onConnectionFailed");
                    onConnectResult(CONNECT_STATUS_FAILED);
                }
            })
            .build();
    }

    private void onConnectResult(int connectStatus) {
        if (connectStatus == CONNECT_STATUS_SUCCUSS) {
            if (mClient != null && mClient.isConnected()) {
                Log.i(TAG, "connect success");
                startPay();
            }
        } else if (connectStatus == CONNECT_STATUS_SUSSPAND) {
            Log.i(TAG, "connect suspend");
            startConnect();
        } else if (connectStatus == CONNECT_STATUS_FAILED) {
            Toast.makeText(this, "client connect failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPay() {
        PendingResult<PayResult> payResult =
            HuaweiPay.HuaweiPayApi.pay(mClient, createPayReq("黄金会员", "开通会员服务", "0.01"));
        payResult.setResultCallback(new ResultCallback<PayResult>() {
            @Override
            public void onResult(PayResult payResult) {
                if (payResult == null) {
                    Log.e(TAG, "payResult is null");
                    return;
                }
                Status status = payResult.getStatus();
                if (status == null) {
                    Log.e(TAG, "status is null");
                    return;
                }
                int rstCode = status.getStatusCode();
                if (rstCode == PayStatusCodes.PAY_STATE_SUCCESS) {
                    //启动支付流程
                    try {
                        status.startResolutionForResult(MainActivity.this, REQUEST_PAY);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "SendIntentException");
                    }
                } else {
                    Log.e(TAG, "pay state failed");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PAY) {
            if (resultCode == Activity.RESULT_OK) {
                //获取支付完成信息
                PayResultInfo payResultInfo = HuaweiPay.HuaweiPayApi.getPayResultInfoFromIntent(data);
                dealPayResult(payResultInfo);
            }
        }
    }

    private void dealPayResult(PayResultInfo payResultInfo) {
        if (payResultInfo == null) {
            return;
        }
        int retuenCode = payResultInfo.getReturnCode();
        if (retuenCode == PayStatusCodes.PAY_STATE_SUCCESS) {
            Toast.makeText(this, "pay success", Toast.LENGTH_SHORT).show();
            boolean checkRst = PaySignUtil.checkSign(payResultInfo, Constant.getPublicKey());
            if(checkRst){
                // 支付成功且验签成功，发放商品
                Log.i(TAG, "pay success");
            } else {
                // 签名失败，需要查询订单状态：对于没有服务器的单机应用，调用查询订单接口查询；其他应用到开发者服务器查询订单状态
                startQueryOrder(payResultInfo.getRequestId());
            }
        } else {
            dealPayResultErrCode(retuenCode);
        }
    }

    private void startQueryOrder(String requestID) {
        PendingResult<OrderResult> orderDetail = HuaweiPay.HuaweiPayApi.getOrderDetail(mClient, createOrderRequest(requestID));
        orderDetail.setResultCallback(new ResultCallback<OrderResult>() {
            @Override
            public void onResult(OrderResult orderResult) {
                Log.i(TAG, "startQueryOrder success");
                if (orderResult.getReturnCode() == OrderStatusCode.ORDER_STATE_SUCCESS) {
                    // 订单查询成功
                    boolean orderSignCheck = PaySignUtil.checkSign(orderResult, Constant.getPublicKey());
                    if (orderSignCheck) {
                        // 订单验签成功， 发放商品
                        Log.i(TAG, "orderResult checkSign success");
                    } else {
                        // 订单验签失败
                        Log.i(TAG, "orderResult checkSign failed");
                    }
                } else {
                    // 订单查询失败
                    Log.i(TAG, "getOrderDetail failed");
                }

            }
        });
    }

    /**
     * 创建支付请求参数
     *
     * @param productName 商品名
     * @param productDesc 商品描述
     * @param amount 商品金额
     * @return 请求参数
     */
    private PayReq createPayReq(String productName, String productDesc, String amount) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(HwPayConstant.KEY_MERCHANTID, Constant.getMerchantID());
        params.put(HwPayConstant.KEY_APPLICATIONID, Constant.getAppID());
        params.put(HwPayConstant.KEY_PRODUCTNAME, productName);
        params.put(HwPayConstant.KEY_PRODUCTDESC, productDesc);
        //
        params.put(HwPayConstant.KEY_REQUESTID, getRequestID());
        params.put(HwPayConstant.KEY_SDKCHANNEL, 1);
        params.put(HwPayConstant.KEY_URLVER, "2");
        // 请保留小数点后两位，如20.00。如果不按照格式传入金额，会导致支付失败。
        params.put(HwPayConstant.KEY_AMOUNT, amount);
        // 说明一下 常量
        params.put(HwPayConstant.KEY_URL, KEY_URL);

        params.put(HwPayConstant.KEY_COUNTRY, "CN");
        params.put(HwPayConstant.KEY_CURRENCY, "CNY");

        // 强烈建议在 商户服务端做签名处理，且私钥存储在服务端
        String noSign = PaySignUtil.getSignData(params);
        String sign = PaySignUtil.rsaSign(noSign);

        PayReq payReq = new PayReq();
        // 商品名称 必填。 此名称将会在支付时显示给用户确认 注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > ,
        payReq.productName = (String) params.get(HwPayConstant.KEY_PRODUCTNAME);

        // 商品描述 必填。注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > , |
        payReq.productDesc = (String) params.get(HwPayConstant.KEY_PRODUCTDESC);

        // 商户ID 必填。 由华为开发者联盟分配
        payReq.merchantId = Constant.getMerchantID();

        // 应用ID 必填 由华为开发者联盟分配
        payReq.applicationID = (String) params.get(HwPayConstant.KEY_APPLICATIONID);

        // 待支付金额 必填 。格式为：元.角分，最小金额为分， 例如：20.00，此金额将会在支付时显示给用户确认)，保留到小数点后两位
        payReq.amount = (String) params.get(HwPayConstant.KEY_AMOUNT);

        // 请求订单号 必填。其值由商户定义生成，用于标识一次支付请求，每次请求需唯一，不可重复。
        // 支付平台在服务器回调接口中会原样返回requestId的值。
        // 注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > , |
        payReq.requestId = (String) params.get(HwPayConstant.KEY_REQUESTID);

        // 支付结果回调URL 选填， 华为服务器收到后检查该应用有无在开发者联盟配置回调URL，如果配置了则使用应用配置的URL，否则使用此url
        // 作为该次支付的回调URL
        // 建议直接 以配置在 华为开发者联盟的回调URL为准
        payReq.url = (String) params.get(HwPayConstant.KEY_URL);
        //
        // // 渠道信息，选填。 取值如下：0 代表自有应用，无渠道 1 代表智汇云渠道 2 代表预装渠道 3 代表游戏吧
        payReq.sdkChannel = (Integer) params.get(HwPayConstant.KEY_SDKCHANNEL);

        // 回调接口版本号， 选填。 建议传值2， 额外回调信息，具体参考接口文档
        payReq.urlVer = (String) params.get(HwPayConstant.KEY_URLVER);

        // // 国家码 选填。建议无特殊需要，不传
        payReq.country = (String) params.get(HwPayConstant.KEY_COUNTRY);
        // // 币种 选填。建议无特殊需要不传此参数。目前仅支持CNY，默认CNY
        payReq.currency = (String) params.get(HwPayConstant.KEY_CURRENCY);

        /* 以上字段皆需要参与签名 * */

        // 签名字段 必填。采用华为开发者联盟分配的私钥进行签名，强烈建议在服务端进行签名 .
        // 【注意】以下参数不参与签名：
        // a）sign参数
        // b)参数说明中标识不参与签名的参数
        // c)没有值的参数，包括null和“”两种情况
        // 2、排序完成之后，再把所有参数名和参数值的键值对以“&”字符连接起来，得到的字符串即为待签名串。如：
        // amount=XXX&applicationID=XXX&country=XXX&currency=XXX&merchantId=XXX&productDesc=XXX&productName=XXX&requestId=XXX&sdkChannel=XXX&url=XXX&urlver=XXX
        // 3、将待签名字符串使用RSA私钥进行签名，采用 SHA256WithRSA签名算法 得到的字符串即为sign参数的值
        payReq.sign = sign;

        // 商户名称，必填，不参与签名。会显示在支付结果页面
        payReq.merchantName = getString(R.string.merchantName);

        // 分类，选填，不参与签名。该字段会影响风控策略
        // X4：主题  X5：应用商店 X6：游戏 X7：天际通 X8：云空间 X9：电子书  X10：华为学习  X11：音乐  X12 视频
        // X31 话费充值   X32 机票/酒店   X33 电影票  X34 团购  X35 手机预购  X36 公共缴费   X39 流量充值
        payReq.serviceCatalog = "X6";

        return payReq;
    }

    /**
     * 创建订单请求参数
     *
     * @param requestId 商户订单号
     * @return 订单请求参数
     */
    private static OrderRequest createOrderRequest(String requestId) {
        String merchantId = Constant.getMerchantID();
        String time = String.valueOf(System.currentTimeMillis());

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setRequestId(requestId);
        orderRequest.setKeyType("1");
        orderRequest.setMerchantId(merchantId);
        orderRequest.setTime(time);

        //以上信息按照一定规则进行签名,建议CP在服务器端储存签名私钥，并在服务器端进行签名操作。
        Map<String, Object> params = new HashMap<>();
        params.put(HwPayConstant.KEY_MERCHANTID, merchantId);
        params.put(HwPayConstant.KEY_REQUESTID, requestId);
        params.put("keyType", "1");
        params.put("time", time);
        String noSign = PaySignUtil.getSignData(params);
        String sign = PaySignUtil.rsaSign(noSign);
        orderRequest.setSign(sign);
        return orderRequest;
    }

    /**
     * 支付失败处理
     * @param returnCode
     */
    private void dealPayResultErrCode(int returnCode) {
        Log.e(TAG, "dealPayResultErrCode");
        String logMsg = "";
        switch (returnCode) {
            case PayStatusCodes.PAY_STATE_CANCEL:
                // 用户取消支付
                logMsg = "cancel pay";
                break;
            case PayStatusCodes.PAY_STATE_NET_ERROR:
                // 网络连接异常
                logMsg = "network error";
                break;
            case PayStatusCodes.PAY_STATE_PARAM_ERROR:
                // 参数错误，包括无参
                logMsg = "params error";
                break;
            case PayStatusCodes.PAY_STATE_TIME_OUT:
                // 支付结果查询超时（建议此时客户端去服务器查询订单是否支付成功)
                logMsg = "pay result query timeout";
                break;
            default:
                logMsg = "pay failed, errorcode: " + returnCode;
                break;
        }
        Log.e(TAG, logMsg);
    }

    private static String getRequestID() {
        SimpleDateFormat sdf = null;
        sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS", Locale.US);
        // 生成6位真随机数
        SecureRandom random = new SecureRandom();
        String randomNum = String.format(Locale.ROOT, "%06d", random.nextInt(1000000));
        return sdf.format(new Date()) + "-" + randomNum;
    }
}
