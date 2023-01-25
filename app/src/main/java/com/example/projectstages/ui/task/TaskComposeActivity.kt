package com.example.projectstages.ui.task

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.projectstages.ui.task.ui.theme.ProjectStagesTheme

class TaskComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectStagesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android ${intent.getLongExtra(PROJECT_ID, 0L)}")
                }
            }
        }
    }

    companion object {

        const val PROJECT_ID = "PROJECT_ID"

        @JvmStatic
        fun getIntent(context: Context, projectID: Long) : Intent
        = Intent(context, TaskComposeActivity::class.java).apply {
                putExtra(PROJECT_ID, projectID)
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProjectStagesTheme {
        Greeting("Android")
    }
}