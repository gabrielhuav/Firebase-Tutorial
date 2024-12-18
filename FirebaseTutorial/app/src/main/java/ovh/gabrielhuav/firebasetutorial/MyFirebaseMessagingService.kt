package ovh.gabrielhuav.firebasetutorial

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("FCM", "MyFirebaseMessagingService creado")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token generado: $token")

        // Importante: Configura la auto-inicialización del token
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        // Recupera el token actualizado
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val refreshedToken = task.result
                Log.d("FCM", "Token recuperado: $refreshedToken")

                // Envía el token a tu servidor
                sendTokenToServer(refreshedToken)
            } else {
                Log.e("FCM", "Error al recuperar el token", task.exception)
            }
        }
    }

    private fun sendTokenToServer(token: String) {
        // Implementa la lógica para enviar el token a tu backend
        Log.d("FCM", "Enviando token al servidor: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM", "Mensaje recibido")
        Log.d("FCM", "De: ${remoteMessage.from}")

        // Registro de datos del mensaje
        remoteMessage.data.let {
            Log.d("FCM", "Payload de datos: $it")
        }

        // Registro de notificación
        remoteMessage.notification?.let {
            Log.d("FCM", "Título: ${it.title}, Cuerpo: ${it.body}")
        }

        // Muestra la notificación
        showNotification(remoteMessage)
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        val channelId = "default_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación para Android Oreo y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones principales",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Construir notificación
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(remoteMessage.notification?.title ?: "Notificación")
            .setContentText(remoteMessage.notification?.body ?: "Mensaje recibido")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Mostrar notificación
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}