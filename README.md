# Plus Messenger (ANTER)

تطبيق مراسلة أصلي لأندرويد يتصل بمنصة ANTER.

## التقنية
- Kotlin 2.0 + Jetpack Compose (Material 3)
- Hilt (DI) + Retrofit + Moshi + OkHttp
- DataStore للتوكن
- MVVM + Navigation Compose
- RTL كامل (العربية)

## البناء التلقائي
- كل push إلى `main` → بناء APK تلقائي على GitHub Actions.
- التنزيل: تبويب **Actions** → آخر تشغيل → **Artifacts**.

## المتطلبات المحلية
- Android Studio Hedgehog أو أحدث
- JDK 17
- Android SDK 34

## الميزات الحالية
- تسجيل الدخول عبر `/api/mobile/v1/auth/login`
- قائمة المحادثات عبر `/api/mobile/v1/conversations`

## الخطوات القادمة
- شاشة الدردشة
- إشعارات FCM
- مزامنة جهات الاتصال (Contact Matching)
- الريلز
