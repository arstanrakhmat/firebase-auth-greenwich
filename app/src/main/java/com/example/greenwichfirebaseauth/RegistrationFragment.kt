package com.example.greenwichfirebaseauth

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.greenwichfirebaseauth.databinding.FragmentRegistrationBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var number: String

    companion object {
        val TAG1 = "OTP CHECK"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListeners()
    }

    private fun clickListeners() {
        binding.btnRegister.setOnClickListener {
            if (checkInput(binding.etName, binding.etPhoneNumber)) {
                Toast.makeText(requireContext(), "CORRECT", Toast.LENGTH_LONG).show()

                number = "+996${binding.etPhoneNumber.text.trim()}"

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(number)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    /** May be an error bellow**/
                    .setActivity(requireActivity())                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

                Log.d(TAG1, "register button was clicked")

            } else {
                Toast.makeText(requireContext(), "INVALID", Toast.LENGTH_LONG).show()
            }
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.

            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.w(TAG, "onVerificationFailed: ${e.message}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.w(TAG, "onVerificationFailed: ${e.message}")
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later
//            storedVerificationId = verificationId
//            resendToken = token

            /**
             * Navigate user to otp activity
             */

//            val bundle = Bundle().apply {
//                putString("id", verificationId)
//                putString("token", token.toString())
//            }

            val bundle = Bundle().apply {
                putString("otp", verificationId)
                putParcelable("token", token)
                putString("phoneNumber", number)
            }
            Log.d(TAG1, "onCode sent")

//            val action =
//                RegistrationFragmentDirections.actionRegistrationFragmentToVerificationFragment(
//                    verificationId, token.toString(), number
//                )

            findNavController().navigate(
                R.id.action_registrationFragment_to_verificationFragment, bundle
            )

            Toast.makeText(requireContext(), "CODE was sent", Toast.LENGTH_LONG).show()

        }
    }



    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(requireContext(), "Authenticate successfully", Toast.LENGTH_LONG)
                        .show()

//                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d(
                        TAG, "signInWithCredential:failure ${task.exception.toString()}"
                    )
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid

                        Toast.makeText(requireContext(), "Incorrect otp", Toast.LENGTH_LONG)
                            .show()
                    }
                    // Update UI
                }
            }
    }


    private fun userNameValidate(text: String?): Boolean {
        if (text != null) {
            if (text.isNotEmpty()) {
                return true
            }
        }

        return false
    }

    private fun mobileValidate(text: String?): Boolean {

//        val patternBeeline =
//            Pattern.compile("^(996|\\\\+996|0)?(77\\\\d{7}|31258\\\\d{4}|22\\\\d{7}|20\\\\d{7})\$")
//
//        val patternMega =
//            Pattern.compile("^(996|\\\\+996|0)?(55\\\\d{7}|755\\\\d{6}|99\\\\d{7}|88[1-9]\\\\d{6})\$")
//
//        val patternO = Pattern.compile("^(996|\\\\+996|0)?([5,7]0\\\\d{7})\$")
//
//        return patternBeeline.matcher(text.toString())
//            .matches() || patternMega.matcher(text.toString())
//            .matches() || patternO.matcher(text.toString()).matches()

        return text != null && text.length == 9 && text.isNotEmpty()
    }

    private fun checkInput(userName: EditText, phoneNum: EditText): Boolean {
        return userNameValidate(userName.text.toString()) && mobileValidate(phoneNum.text.toString())
    }
}