package com.example.geoquiz
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateQusetion()
        true_button.setOnClickListener {
            checkAnswer(true)
            quizViewModel.freezeButtons(true_button,false_button,prev_button)
        }
        false_button.setOnClickListener {
            checkAnswer(false)
            quizViewModel.freezeButtons(true_button,false_button,prev_button)
        }
        next_button.setOnClickListener {
            quizViewModel.moveToNext()
            updateQusetion()
            quizViewModel.activeButtons(true_button,false_button,prev_button)
        }
        prev_button.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQusetion()
        }
        cheat_button.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatingActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }


    }


    private fun updateQusetion() {
        val questionTextResId = quizViewModel.currentQuestionText
        question_text_view.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val answerGrade = quizViewModel.currentQuestionGrade
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()

        if (userAnswer==correctAnswer)
            quizViewModel.updateGrade(answerGrade)
        gradeTV.setText("Your score is "+quizViewModel.totalGrade.toString())



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }
}
