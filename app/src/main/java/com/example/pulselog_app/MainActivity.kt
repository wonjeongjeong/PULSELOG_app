package com.example.pulselog_app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.biometric.BiometricManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.pulselog_app.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    lateinit var btnLogin: Button
    lateinit var editTextId: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnRegister: Button
    lateinit var btnBioRegister: Button
    var DB:DBHelper?=null

    companion object {
        const val TAG: String = "MainActivity"
    }
    lateinit var binding: ActivityMainBinding

    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d(TAG, "registerForActivityResult - result : $result")

            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "registerForActivityResult - RESULT_OK")
                authenticateToEncrypt()  //생체 인증 가능 여부확인 다시 호출
            } else {
                Log.d(TAG, "registerForActivityResult - NOT RESULT_OK")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        setContentView(R.layout.activity_main)

        DB = DBHelper(this)

        btnLogin = findViewById(R.id.registerBtn)
        editTextId = findViewById(R.id.editTextId)
        editTextPassword = findViewById(R.id.editTextPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnBioRegister = findViewById(R.id.bioRegister)

        // 로그인 버튼 클릭
        btnLogin!!.setOnClickListener {
            val user = editTextId!!.text.toString()
            val pass = editTextPassword!!.text.toString()

            // 빈칸 제출시 Toast
            if (user == "" || pass == "") {
                Toast.makeText(this@MainActivity, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val checkUserpass = DB!!.checkUserpass(user, pass)
                // id 와 password 일치시
                if (checkUserpass == true) {
                    Toast.makeText(this@MainActivity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(this@MainActivity, "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // 회원가입 버튼 클릭시
        btnRegister.setOnClickListener {
            val registerIntent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(registerIntent)
        }
        // 생체인증 버튼 클릭시
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        biometricPrompt = setBiometricPrompt()
        promptInfo = setPromptInfo()

        btnBioRegister.setOnClickListener {
            authenticateToEncrypt()  //생체 인증 가능 여부확인
        }
    }
    private fun setPromptInfo(): BiometricPrompt.PromptInfo {

        val promptBuilder: BiometricPrompt.PromptInfo.Builder = BiometricPrompt.PromptInfo.Builder()

        promptBuilder.setTitle("Biometric login for my app")
        promptBuilder.setSubtitle("Log in using your biometric credential")
        promptBuilder.setNegativeButtonText("Use account password")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { //  안면인식 ap사용 android 11부터 지원
            promptBuilder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        }

        promptInfo = promptBuilder.build()
        return promptInfo as PromptInfo
    }

    private fun setBiometricPrompt(): BiometricPrompt {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this@MainActivity, executor!!, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@MainActivity, """"지문 인식 ERROR [ errorCode: $errorCode, errString: $errString ]""".trimIndent(), Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(this@MainActivity, "지문 인식 성공", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@MainActivity, "지문 인식 실패", Toast.LENGTH_SHORT).show()
            }

        } )
        return biometricPrompt as BiometricPrompt
    }

    /*
   * 생체 인식 인증을 사용할 수 있는지 확인
   * */
    fun authenticateToEncrypt() {

        Log.d(TAG, "authenticateToEncrypt() ")

        val biometricManager = BiometricManager.from(this@MainActivity)
//        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {

            //생체 인증 가능
            BiometricManager.BIOMETRIC_SUCCESS -> Toast.makeText(this@MainActivity, "App can authenticate using biometrics", Toast.LENGTH_SHORT).show()

            //기기에서 생체 인증을 지원하지 않는 경우
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Toast.makeText(this@MainActivity, "No biometric features available on this device.", Toast.LENGTH_SHORT).show()

            //현재 생체 인증을 사용할 수 없는 경우
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Toast.makeText(this@MainActivity, "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show()

            //생체 인식 정보가 등록되어 있지 않은 경우
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this@MainActivity, "Prompts the user to create credentials that your app accepts.", Toast.LENGTH_SHORT).show()

                val dialogBuilder = AlertDialog.Builder(this@MainActivity)
                dialogBuilder
                    .setTitle("PULSELOG")
                    .setMessage("지문 등록이 필요합니다. 지문등록 설정화면으로 이동하시겠습니까?")
                    .setPositiveButton("확인") {dialog, which -> goBiometricSettings() }
                    .setNegativeButton("취소") {dialog, which -> dialog.cancel() }
                dialogBuilder.show()
            }

            //기타 실패
            else ->  Toast.makeText(this@MainActivity, "Fail Biometric facility", Toast.LENGTH_SHORT).show()

        }

        //인증 실행하기
        goAuthenticate()
    }
    /*
   * 생체 인식 인증 실행
   * */
    private fun goAuthenticate() {
        Log.d(TAG, "goAuthenticate - promptInfo : $promptInfo")
        promptInfo?.let {
            biometricPrompt?.authenticate(it);  //인증 실행
        }
    }


    /*
    * 지문 등록 화면으로 이동
    * */
    fun goBiometricSettings() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        }
        loginLauncher.launch(enrollIntent)
    }


}