package com.example.greenwichfirebaseauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.greenwichfirebaseauth.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLogInBinding.inflate(layoutInflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickListeners()
    }

    private fun clickListeners() {
        binding.btnRegister.setOnClickListener {
//            if (checkInput(binding.etName, binding.etPhoneNumber)) {
//                findNavController().navigate(R.id.registrationFragment)
//            } else {
//                Toast.makeText(requireContext(), "INVALID", Toast.LENGTH_LONG).show()
//            }
            findNavController().navigate(R.id.action_logInFragment_to_registrationFragment)
        }
    }


}