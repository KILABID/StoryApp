package com.kilabid.storyapp.CustomView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.kilabid.storyapp.R

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s.toString()
                val currentInputType = inputType

                when (currentInputType) {
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT -> {
                        // Email input
                        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
                        if (!isValid) {
                            setError("Email tidak valid", null)
                        } else {
                            error = null
                        }
                    }

                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                        // Password input
                        if (input.length < 8) {
                            setError("Password harus lebih dari 8 karakter", null)
                        } else if (input.contains(" ")) {
                            setError("Password tidak boleh menggunakan spasi", null)
                        } else {
                            error = null
                        }
                    }

                    else -> {
                        // Text input (no specific validation)
                        if (input.contains(" ")) {
                            error = "Tidak boleh mengandung spasi"
                        } else if (input.isEmpty()) {
                            error = "Tidak boleh kosong"
                        }
                        error = null
                    }
                }


                if (error != null) {
                    setBackgroundResource(R.drawable.rectangle_edit_text_error)
                } else {
                    setBackgroundResource(R.drawable.rectangle_edit_text_success)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setPadding(15, 0, 15, 0)
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }

}