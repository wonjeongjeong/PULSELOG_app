package com.example.pulselog_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    var DB:DBHelper?=null
    lateinit var editTextId: EditText
    lateinit var editTextPassword: EditText
    lateinit var editTextRePassword: EditText
    lateinit var editTextNick: EditText
    lateinit var editTextPhone: EditText
    lateinit var btnRegister: Button
    lateinit var btnCheckId: Button
    var CheckId:Boolean=false
    lateinit var checkBox1: CheckBox
    lateinit var checkBox2: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_main)

        DB = DBHelper(this)
        editTextId = findViewById(R.id.editTextId)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextRePassword = findViewById(R.id.editTextCheckPW)
        editTextNick = findViewById(R.id.editTextName)
        editTextPhone = findViewById(R.id.editTextPhonenum)
        btnRegister = findViewById(R.id.registerBtn)
        btnCheckId = findViewById(R.id.btnCheckId)
        checkBox1 = findViewById(R.id.checkBox1)
        checkBox2 = findViewById(R.id.checkBox2)

        // 아이디 중복확인
        btnCheckId.setOnClickListener {
            val user = editTextId.text.toString()
            val idPattern = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{4,12}$"

            if (user == "") {
                Toast.makeText(
                    this@RegisterActivity,
                    "아이디를 입력해주세요.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                if (Pattern.matches(idPattern, user)) {
                    val checkUsername = DB!!.checkUser(user)
                    if(checkUsername == false){
                        CheckId = true
                        Toast.makeText(this@RegisterActivity, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(this@RegisterActivity, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this@RegisterActivity, "아이디 형식이 옳지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 완료 버튼 클릭 시
        btnRegister.setOnClickListener {
            val user = editTextId.text.toString()
            val pass = editTextPassword.text.toString()
            val repass = editTextRePassword.text.toString()
            val nick = editTextNick.text.toString()
            val phone = editTextPhone.text.toString()
            val pwPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z[0-9]@$!%*?&]{8,}$"
            val phonePattern = "^010[0-9]{8}$"
            var isCheckBox1 = checkBox1.isChecked()
            var isCheckBox2 = checkBox2.isChecked()
            // 사용자 입력이 비었을 때
            if (user == "" || pass == "" || repass == "" || nick == "" || phone == "") Toast.makeText(
                this@RegisterActivity,
                "회원정보를 모두 입력해주세요.",
                Toast.LENGTH_SHORT
            ).show()
            else {
                // 아이디 중복 확인이 됐을 때
                if (CheckId == true) {
                    // 비밀번호 형식이 맞을 때
                    if (Pattern.matches(pwPattern, pass)) {
                        // 비밀번호 재확인 성공
                        if (pass == repass) {
                            // 번호 형식
                            if (Pattern.matches(phonePattern, phone)) {
                                // 이용약관 동의
                                if(isCheckBox1 == true) {
                                    // 개인정보 수집 동의
                                    if (isCheckBox2 == true) {
                                        val insert = DB!!.insertData(user, pass, nick, phone)
                                        // 가입 성공 시 Toast를 띄우고 메인 화면으로 전환
                                        if (insert == true) {
                                            Toast.makeText(
                                                this@RegisterActivity,
                                                "가입되었습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent =
                                                Intent(applicationContext, MainActivity::class.java)
                                            startActivity(intent)
                                        }
                                        // 가입 실패 시
                                        else {
                                            Toast.makeText(
                                                this@RegisterActivity,
                                                "가입 실패하였습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    // 개인정보 수집 미동의
                                    } else {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "개인정보 수집에 동의해주세요.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                // 이용약관 미동의
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "이용약관에 동의해주세요.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            // 전화번호 형식이 맞지 않을 때
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "전화번호 형식이 옳지 않습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        // 비밀번호 재확인 실패
                        else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "비밀번호가 일치하지 않습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    // 비밀번호 형식이 맞지 않을 때
                    else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "비밀번호 형식이 옳지 않습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                // 아이디 중복확인이 되지 않았을 때
                else {
                    Toast.makeText(this@RegisterActivity, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}