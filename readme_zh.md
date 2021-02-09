# 华为应用内支付服务VR支付客户端示例代码

本文主要介绍华为应用内支付服务（HUAWEI In-App Purchases，IAP）VR支付的客户端开发步骤，帮助您快速了解华为VR支付提供的客户端接口及其使用方法。

## 目录
  - [简介](#简介)
  - [开发准备](#开发准备)
  - [环境要求](#环境要求)
  - [运行结果](#运行结果)
  - [授权许可](#授权许可)


## 简介

华为应用内支付服务为VR设备提供VR支付服务。目前，我们仅提供价格定义接口，商品信息的定义需要您在自己的应用中实现。
免责声明：本demo仅演示商品购买过程，并未实际使用购买的商品。

## 开发准备
1.	确认Android Studio开发环境准备就绪，使用Android Studio打开示例代码工程路径下的build.gradle文件。

2.	在AppGallery Connect中创建应用并配置应用信息。详情请参见配置AppGallery Connect。

3.	在 Android Studio（3.0及以上版本）导入demo，再进行构建。

4.	配置示例代码：
    - 在AppGallery Connect中下载应用的agconnect-services.json文件，把该文件添加至本demo的应用根目录（app）中。
    - 添加证书文件，在应用级build.gradle文件中添加配置。
    - 打开AndroidManifest文件，修改package的值为您的应用包名。
    - 将Constant类中的PUBLIC_KEY替换为您应用的公钥。
    - 将Constant类中的PRIVATE_KEY替换为您应用的私钥。
    - Replace the APPID and MERCHANTID in the Constant class with the appid and merchantid of your app.将Constant类中的APPID替换为创建应用的App ID，将Constant类中的MERCHANTID替换为创建应用的CP ID。
  
5.	在Android设备或模拟机上运行该示例代码。

## 环境要求

推荐使用Android SDK 22及以上版本、JDK 1.8及以上版本。

## 运行结果

运行demo后，会出现如下页面。

<img src="images/homepage_cn.jpg" alt="运行结果" height="600"/> 

点击“确认支付 ¥0.01”，demo会调用pay接口，跳转至付款界面。该界面跳转由华为应用内支付服务支持。

<img src="images/checkout_cn.jpg" alt="付款页面" height="600"/> 

付款成功后，页面将显示支付结果。该页面跳转由华为应用内支付服务支持。

<img src="images/purchase_result_cn.jpg" alt="支付结果" height="600"/> 
 
## 授权许可

华为应用内支付服务VR支付客户端示例代码经过Apache License 2.0授权许可。
