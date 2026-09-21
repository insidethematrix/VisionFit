# VisionFit 🏋️‍♂️

VisionFit, cihazınızın kamerasını kullanarak gerçek zamanlı vücut takibi yapan ve yapay zeka destekli bir akıllı antrenör uygulamasıdır. Google ML Kit ve CameraX altyapısını kullanarak egzersiz hareketlerinizi analiz eder, tekrarlarınızı otomatik sayar ve formunuz hakkında canlı geri bildirim verir.

## Özellikler ✨
* **Gerçek Zamanlı İskelet Çizimi (Pose Detection):** Kameradan alınan görüntü üzerinde eklemlerinizi ve iskelet yapınızı anlık olarak analiz ederek ekrana çizer.
* **Otomatik Squat Sayacı:** Diz, kalça ve ayak bileği açılarını matematiksel olarak hesaplayarak çömelme/kalkma hareketlerini algılar ve tekrarlarınızı otomatik sayar.
* **Canlı Form Asistanı:** Hareket sırasında ne kadar eğilmeniz gerektiği veya pozisyonunuzun doğruluğu hakkında ("Daha da eğil...", "Harika!") anlık Türkçe metin geri bildirimleri verir.
* **Gizlilik Odaklı:** Tüm yapay zeka analizleri (ML Kit) tamamen cihaz üzerinde (on-device) gerçekleşir, görüntüler hiçbir şekilde internete gönderilmez.

## Teknolojiler 🛠️
* **Dil:** Java
* **Kamera API:** [Android CameraX](https://developer.android.com/training/camerax)
* **Görüntü İşleme (ML):** [Google ML Kit Pose Detection](https://developers.google.com/ml-kit/vision/pose-detection)
* **Arayüz (UI):** XML, ConstraintLayout
* **Asenkron İşlemler:** ExecutorService

## Kurulum ve Çalıştırma 🚀
1. Bu depoyu bilgisayarınıza klonlayın: 
   ```bash
   git clone https://github.com/insidethematrix/VisionFit.git
   ```
2. Projeyi **Android Studio** ile açın.
3. Gradle senkronizasyonunun bitmesini bekleyin.
4. Kamerayı doğru test edebilmek için uygulamayı **fiziksel bir Android cihazda** çalıştırın (Emülatörlerde kamera hareketleri simüle etmek zor olabilir).
5. Uygulama açıldığında Kamera iznini onaylayın, kameranın sizi tam boy görebileceği bir yere geçin ve antrenmana başlayın!

## Projenin Geleceği (Hedefler) 🎯
- [ ] Farklı egzersizlerin (Şınav, Lunge, Mekik vb.) eklenmesi.
- [ ] Egzersiz sırasında sesli yönlendirme (Text-to-Speech) asistanı.
- [ ] Antrenman geçmişini kaydedebileceğiniz bir profil ve istatistik sayfası.
- [ ] Material Design 3 ile modern bir UI/UX.

---
*Bu proje geliştirme aşamasındadır.*
