package ovh.gabrielhuav.firebasetutorial

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_FirebaseTutorial) // Asegúrate de aplicar el tema
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inicialización de Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Obtener datos pasados desde AuthActivity
        val email = intent.getStringExtra("email")
        val provider = intent.getStringExtra("provider")

        // Referencias UI
        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        val providerTextView = findViewById<TextView>(R.id.providerTextView)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        // Setup
        emailTextView.text = email
        providerTextView.text = provider

        // Evento de Analytics
        val bundle = Bundle()
        bundle.putString("message", "Usuario autenticado en Home")
        firebaseAnalytics.logEvent("HomeScreen", bundle)

        // Botón Logout
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        }
    }
}
