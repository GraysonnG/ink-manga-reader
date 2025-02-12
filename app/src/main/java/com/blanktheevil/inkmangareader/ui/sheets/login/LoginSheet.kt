package com.blanktheevil.inkmangareader.ui.sheets.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.blanktheevil.inkmangareader.R
import com.blanktheevil.inkmangareader.ui.DefaultPreview
import com.blanktheevil.inkmangareader.ui.InkIcon
import com.blanktheevil.inkmangareader.ui.components.TextInputField
import com.blanktheevil.inkmangareader.ui.permanentNavigationBarSize
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginSheet(
    onDismissRequest: () -> Unit = {},
) = Column {
    val viewModel = koinViewModel<LoginViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val state = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    LaunchedEffect(Unit) {
        viewModel.initViewModel(false)
    }

    LaunchedEffect(uiState.shouldDismiss) {
        if (uiState.shouldDismiss) {
            onDismissRequest()
        }
    }

    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        sheetState = state,
    ) {
        LoginSheetContent(
            username = uiState.username,
            password = uiState.password,
            onUsernameUpdated = viewModel::updateUsername,
            onPasswordUpdated = viewModel::updatePassword,
            onLogin = viewModel::login,
            onSignUpClicked = {}
        )
    }
}

@Composable
private fun LoginSheetContent(
    username: String,
    password: String,
    onUsernameUpdated: (String) -> Unit,
    onPasswordUpdated: (String) -> Unit,
    onLogin: () -> Unit,
    onSignUpClicked: () -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(
            horizontal = 8.dp,
        )
        .padding(
            bottom = permanentNavigationBarSize,
        ),
    verticalArrangement = Arrangement.Center,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var showPassword by remember { mutableStateOf(false) }

        val icon = remember (showPassword) {
            if (showPassword) {
                R.drawable.read_24
            } else {
                R.drawable.outline_visibility_off_24
            }
        }

        val visualTransformation = remember (showPassword) {
            if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            }
        }
        Text(
            "Login",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            "Enter your Mangadex credentials.",
            modifier = Modifier.padding(start = 6.dp),
            style = MaterialTheme.typography.bodySmall,
        )

        TextInputField(
            modifier = Modifier.fillMaxWidth(),
            value = username,
            onValueChange = onUsernameUpdated,
            shape = RoundedCornerShape(50),
            placeholder = "Username"
        )
        TextInputField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = onPasswordUpdated,
            shape = RoundedCornerShape(50),
            placeholder = "Password",
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    InkIcon(resId = icon)
                }
            },
            visualTransformation = visualTransformation
        )
        Row(
            modifier = Modifier.padding(start = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Don't have an account?", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.size(8.dp))
            Text(
                modifier = Modifier.clickable(
                    role = Role.Button,
                    onClick = onSignUpClicked,
                ),
                text = "Sign up here.",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onLogin) {
                Text("Login")
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() = DefaultPreview {
    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    LoginSheetContent(
        username = username,
        password = password,
        onUsernameUpdated = { username = it },
        onPasswordUpdated = { password = it },
        onLogin = {},
        onSignUpClicked = {},
    )
}