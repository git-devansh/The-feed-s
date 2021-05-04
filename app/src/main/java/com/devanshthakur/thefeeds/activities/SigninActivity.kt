package com.devanshthakur.thefeeds.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.devanshthakur.thefeeds.R
import com.devanshthakur.thefeeds.dao.MyDao
import com.devanshthakur.thefeeds.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SigninActivity : AppCompatActivity() {
    private val TAG = "GoogleActivity"
    private val RC_SIGN_IN: Int = 901
    private lateinit var signInProgressBar: ProgressBar
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigin)

        // Initializing views
        initViews()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initializing firebase auth
        auth = Firebase.auth


        val signInButton: com.google.android.gms.common.SignInButton = findViewById(R.id.sign_in_button)


        /***
         * On click of sign in button
         */
        signInButton.setOnClickListener {
            signIn()
        }

    }

    private fun initViews() {
        signInProgressBar = findViewById(R.id.sign_in_progress_bar)
    }

    // Google sign in function
    private fun signIn() {
        // setting progress bar visibility to visible
        //      hiding it later again if firebaseUser is not available
        signInProgressBar.visibility = View.VISIBLE
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        GlobalScope.launch(Dispatchers.IO){
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user

            // getting back into main scope
            withContext(Dispatchers.Main){
                updatedUI(firebaseUser)
            }
        }

    }

    private fun updatedUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null){

            // adding user into firestore
            val user = User(firebaseUser.uid, firebaseUser.displayName, firebaseUser.photoUrl.toString())
            val myDao = MyDao()
            myDao.addUser(user)


            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else{
            // hiding progress bar
            signInProgressBar.visibility = View.INVISIBLE
        }
    }


    /***
     * If user is already Signed in, then UI can navigate to main screen
     */
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updatedUI(currentUser)
    }

}