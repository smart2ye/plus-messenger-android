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

---

## حالة التطبيق (15 سبتمبر 2026)

### ✅ يعمل
- بناء APK تلقائيًا على GitHub Actions (Debug + Release).
- تسجيل دخول فعلي على `anter-1.onrender.com`.
- قائمة محادثات تُحمَّل من `/api/mobile/conversations`.
- توكن محفوظ في DataStore.
- RTL كامل + Material 3 + وضع داكن.

### 🔜 لم يُبنَ بعد
- شاشة الدردشة الفعلية (فتح محادثة + إرسال + استقبال).
- مزامنة جهات الاتصال (`/api/mobile/suggested-follows` جاهز على السيرفر).
- الإشعارات (FCM).
- الريلز.
- المنشورات.
- تحديث الإجراءات إلى `setup-java@v5` و`actions/checkout@v4`.

### 🔗 الربط
- الرابط: `https://anter-1.onrender.com/`
- الـPrefix الفعلي: `/api/mobile` (وليس `/api/mobile/v1`).
- التوكن: `accessToken` (وليس `token`).
