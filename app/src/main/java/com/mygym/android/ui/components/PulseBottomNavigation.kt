package com.mygym.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mygym.android.ui.theme.MyGymTheme
import com.mygym.android.ui.theme.PulseBlue
import com.mygym.android.ui.theme.PulseNavBackground
import com.mygym.android.ui.theme.PulseNavBorder
import com.mygym.android.ui.theme.PulseTextMuted

enum class PulseNavTab(
    val label: String,
    val icon: ImageVector
) {
    INICIO("Início", Icons.Filled.Home),
    TREINOS("Treinos", Icons.Filled.FitnessCenter),
    PROGRESSO("Progresso", Icons.Filled.Leaderboard),
    CONTA("Conta", Icons.Filled.Person)
}

@Composable
fun PulseBottomNavigation(
    selectedTab: PulseNavTab = PulseNavTab.INICIO,
    onTabSelected: (PulseNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .border(
                width = 1.dp,
                color = PulseNavBorder,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ),
        color = PulseNavBackground,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PulseNavTab.values().forEach { tab ->
                val isSelected = tab == selectedTab
                val itemColor = if (isSelected) PulseBlue else PulseTextMuted

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = itemColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tab.label,
                        color = itemColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080D1A)
@Composable
fun PulseBottomNavigationPreview() {
    var currentTab by remember { mutableStateOf(PulseNavTab.INICIO) }

    MyGymTheme {
        PulseBottomNavigation(
            selectedTab = currentTab,
            onTabSelected = { currentTab = it }
        )
    }
}
