package ovh.gabrielhuav.firebasetutorial

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class AuthActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Inicialización
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAuth = FirebaseAuth.getInstance()

        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        firebaseAnalytics.logEvent("InitScreen", bundle)

        // Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // UI
        val emailInput = findViewById<EditText>(R.id.username_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerButton = findViewById<Button>(R.id.register_button)
        val googleLoginButton = findViewById<ImageView>(R.id.googleLogin_button)

        // Login con Email y Contraseña
        loginButton.setOnClickListener {
            if (emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(
                    emailInput.text.toString(),
                    passwordInput.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showHome(task.result?.user?.email ?: "", "BASIC")
                    } else {
                        showToast("Error: Verifique su correo y contraseña.")
                    }
                }
            } else {
                showToast("Por favor, complete los campos.")
            }
        }

        // Registro con Email y Contraseña
        registerButton.setOnClickListener {
            if (emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(
                    emailInput.text.toString(),
                    passwordInput.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showHome(task.result?.user?.email ?: "", "BASIC")
                    } else {
                        showToast("Error: Este usuario ya existe.")
                    }
                }
            } else {
                showToast("Por favor, complete los campos.")
            }
        }

        // Google Sign-In
        val googleLoginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account = task.result
                account?.idToken?.let { idToken ->
                    firebaseAuthWithGoogle(idToken)
                }
            } else {
                showToast("Error al iniciar sesión con Google.")
            }
        }

        googleLoginButton.setOnClickListener {
            // Cerrar sesión activa antes de lanzar el flujo
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleLoginLauncher.launch(signInIntent)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                showHome(user?.email ?: "", "GOOGLE")
            } else {
                showToast("Error al autenticar con Google.")
            }
        }
    }

    private fun showHome(email: String, provider: String) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
