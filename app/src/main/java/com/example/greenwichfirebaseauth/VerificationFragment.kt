package com.example.greenwichfirebaseauth

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.greenwichfirebaseauth.databinding.FragmentVerificationBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import java.util.concurrent.TimeUnit

class VerificationFragment : Fragment() {

    private lateinit var binding: FragmentVerificationBinding
    private lateinit var auth: FirebaseAuth

    private val args: VerificationFragmentArgs by navArgs()

    private lateinit var otp: String
    private lateinit var resendingToken: ForceResendingToken
    private lateinit var phoneNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentVerificationBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()

        phoneNumber = args.phoneNumber

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendVerificationCode(phoneNumber)
        addTextChangeListener()
        clickListeners()
        resendOTPvVisibility()
    }

    private fun clickListeners() {
        binding.apply {
            btnRegister.setOnClickListener {

                //Collect otp from edit texts
                val typedOtp = collectOtpFromEditTexts()

                if (typedOtp.isNotEmpty()) {
                    if (typedOtp.length == 6) {
                        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                            otp, typedOtp
                        )

                        signInWithPhoneAuthCredential(credential)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please Enter correct otp (LENGTH OF NUMBERS)",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please Enter OTP", Toast.LENGTH_LONG).show()
                }
            }

            resentOtp.setOnClickListener {
                resentVerificationCode(phoneNumber, resendingToken)
                resendOTPvVisibility()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    Toast.makeText(requireContext(), "Authenticate successfully", Toast.LENGTH_LONG)
                        .show()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d(
                        ContentValues.TAG,
                        "signInWithCredential:failure ${task.exception.toString()}"
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

    private fun collectOtpFromEditTexts(): String {
//        val otp = binding.apply {
//            et1.text.toString() + et2.text.toString() + et3.text.toString() + et4.text.toString() +
//                    et5.text.toString() + et6.text.toString()
//        }

        val OTP = binding.et1.text.toString() +
                binding.et2.text.toString() +
                binding.et3.text.toString() +
                binding.et4.text.toString() +
                binding.et5.text.toString() +
                binding.et6.text.toString()

        return OTP
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            /** May be an error bellow**/
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resentVerificationCode(number: String, token: ForceResendingToken) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            /** May be an error bellow**/
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
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
            Log.w(ContentValues.TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.w(ContentValues.TAG, "onVerificationFailed: ${e.message}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.w(ContentValues.TAG, "onVerificationFailed: ${e.message}")
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

            otp = verificationId
            resendingToken = token
        }
    }

    private fun resendOTPvVisibility() {
        binding.apply {
            et1.setText("")
            et2.setText("")
            et3.setText("")
            et4.setText("")
            et5.setText("")
            et6.setText("")
            resentOtp.visibility = View.INVISIBLE
            resentOtp.isEnabled = false

            Handler(Looper.myLooper()!!).postDelayed(Runnable {
                resentOtp.visibility = View.VISIBLE
                resentOtp.isEnabled = true
            }, 6000)
        }
    }

    private fun addTextChangeListener() {
        binding.apply {
            et1.addTextChangedListener(EditTextWatcher(et1))
            et2.addTextChangedListener(EditTextWatcher(et2))
            et3.addTextChangedListener(EditTextWatcher(et3))
            et4.addTextChangedListener(EditTextWatcher(et4))
            et5.addTextChangedListener(EditTextWatcher(et5))
            et6.addTextChangedListener(EditTextWatcher(et6))
        }
    }

    inner class EditTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {

            val text = p0.toString()
            when (view.id) {
                R.id.et_1 -> if (text.length == 1) binding.et2.requestFocus()
                R.id.et_2 -> if (text.length == 1) binding.et3.requestFocus() else if (text.isEmpty()) binding.et1.requestFocus()
                R.id.et_3 -> if (text.length == 1) binding.et4.requestFocus() else if (text.isEmpty()) binding.et2.requestFocus()
                R.id.et_4 -> if (text.length == 1) binding.et5.requestFocus() else if (text.isEmpty()) binding.et3.requestFocus()
                R.id.et_5 -> if (text.length == 1) binding.et6.requestFocus() else if (text.isEmpty()) binding.et4.requestFocus()
                R.id.et_6 -> if (text.isEmpty()) binding.et5.requestFocus()
            }
        }
    }

}