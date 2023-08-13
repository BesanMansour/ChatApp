package Notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        // هنا يمكنك التعامل مع الـ FCM token الجديد
        // على سبيل المثال: إرساله إلى الخادم الخاص بك لتحديث الأجهزة المسجلة
        // أو تخزينه محليًا إذا لزم الأمر.
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            updateToken(token);
        }
    }

    private void updateToken(String refreshToken){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token = new Token(refreshToken);
        reference.child(firebaseUser.getUid()).setValue(token);
    }
}
