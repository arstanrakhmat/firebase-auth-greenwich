package com.example.greenwichfirebaseauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.greenwichfirebaseauth.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)

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

            } else {
                Toast.makeText(requireContext(), "INVALID", Toast.LENGTH_LONG).show()
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