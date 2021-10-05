package com.timothygoon.note_taking_app

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.io.ObjectInputStream
import java.io.ObjectOutputStream


private const val TAG: String = "LoginFragment";

class LoginFragment : Fragment() {

    interface Callbacks {
        fun onLogin()
    }

    private var callbacks: Callbacks? = null

    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate() called")
        super.onCreate(savedInstanceState)

        try{
            val fis = requireActivity().openFileInput("loginToken")
            val ois = ObjectInputStream(fis)

            val token = ois.readObject() as String

            if(token.isNotEmpty()) {
                callbacks?.onLogin()
            }

            ois.close()
            fis.close()
        } catch(err: Throwable){

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        userNameEditText = view.findViewById(R.id.usernameEditText) as EditText
        passwordEditText = view.findViewById(R.id.passwordEditText) as EditText
        loginButton = view.findViewById(R.id.loginButton) as Button


        // Update the UI after configuration changes using information from ViewModel
        userNameEditText.setText(loginViewModel.userName)
        passwordEditText.setText(loginViewModel.password)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener { view: View ->
            val mongoDBLiveData: LiveData<String> = MongoDBFetchr().checkUserCredentials(loginViewModel.userName, loginViewModel.password)
            mongoDBLiveData.observe(
                viewLifecycleOwner,
                Observer { responseString ->
                    // Store the object in a file
                    try{
                        val fos = requireActivity().openFileOutput(
                            "loginToken",
                            Context.MODE_PRIVATE
                        )
                        val oos = ObjectOutputStream(fos)

                        oos.writeObject(responseString)

                        oos.close()
                        fos.close()

                        callbacks?.onLogin()
                    } catch(err: Throwable){
                        Log.e(TAG, err.toString())
                    }

                })
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
        val userNameWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
//                crime.title = sequence.toString()
                loginViewModel.userName = userNameEditText.text.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }

        userNameEditText.addTextChangedListener(userNameWatcher)


        val passwordWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
//                crime.title = sequence.toString()
                loginViewModel.password = passwordEditText.text.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }

        passwordEditText.addTextChangedListener(passwordWatcher)

    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    companion object{
        fun newInstance() : LoginFragment{
//            val args = Bundle().apply {
//                putSerializable(ARG_GAME_ID, gameId)
//            }
            return LoginFragment()
        }
    }
}