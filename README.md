# HUAWEI In-App Purchases(IAP) Clientdemo for VR

The iap_demo App demonstrates Huawei VR Pay client APIs and usages.


## Table of Content
    
<!-- TOC -->


- [Table of Content](#table-of-content)
- [Introduction](#introduction)
- [Getting Started](#getting-started)
- [Supported Environments](#supported-environments)
- [Result](#result)
- [License](#license)

<!-- /TOC -->

## Introduction

Huawei VR Pay is a payment service provided by Huawei in-app Purchases in VR devices. Currently, only the api for customizing prices is provided. CP need to define offering information in their apps.

Disclaimer: The demo only demonstrates the purchase procedure, and it does not have a real use of purchased products.

## Getting Started

1. Check whether the Android studio development environment  is ready. Open the sample code project directory with file "build.gradle" in Android Studio. 

2. Finish the configuration in AppGallery Connect. 
See details: [Configuring AppGallery Connect](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides-V5/config-agc-0000001050033072-V5)

3. To build this demo, please first import the demo in the Android Studio (3.x+).

4. Configure the sample code:
      - Download the file "agconnect-services.json" of the app on AGC, and add the file to the app root directory(\app) of the demo. 
      - Add the certificate file to the project and add your configuration to  in the app-level `build.gradle` file. 
      - Open the `AndroidManifest` file and change the value of package to your app package name.  
      - Replace the PUBLIC_KEY in the Constant class with the public key of your app.
      - Replace the PRIVATE_KEY in the Constant class with the private key of your app.
      - Replace the APPID and MERCHANTID in the Constant class with the appid and merchantid of your app.

5. Run the sample on your Android device or emulator.

## Supported Environments
Android SDK Version >= 22 and JDK version >= 1.8 is recommended.

## Result

Once you start the demo, you should be able to see the following page.

<img src=https://github.com/HMS-Core/hms-iap-vr-clientdemo/blob/master/image/homepage.JPG alt="demo home page" height="600"/>

Tap **确认支付 ¥0.01**, the demo will call the `pay` API, and jump to the checkout page which is provided by IAP Service.

 <img src=https://github.com/HMS-Core/hms-iap-vr-clientdemo/blob/master/image/checkout-page.JPG alt="payment selection" height="600"/>

Once purchase succeed, IAP Service will display the purchase result.

 <img src=https://github.com/HMS-Core/hms-iap-vr-clientdemo/blob/master/image/purchase-result.JPG alt="payment result" height="600"/>

 ## License

This demo is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
