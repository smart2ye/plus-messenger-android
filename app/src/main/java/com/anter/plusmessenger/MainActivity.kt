package com.anter.plusmessenger

import android.os.Bundle
import android.util.TypedValue
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.anter.plusmessenger.ui.navigation.AppNavigation
import com.anter.plusmessenger.ui.theme.PlusMessengerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
            setContent {
                PlusMessengerTheme {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            AppNavigation()
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            showCrash(t)
        }
    }

    private fun showCrash(t: Throwable) {
        val tv = TextView(this).apply {
            text = "CRASH:\n\n" + t.stackTraceToString()
            setPadding(40, 60, 40, 60)
            setTextColor(0xFFB00020.toInt())
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTextIsSelectable(true)
        }
        val scroll = ScrollView(this).apply { addView(tv) }
        setContentView(scroll)
    }
}
