package com.example.cmprojectandroid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest



@Composable
fun SignUpPage(onNavigateBack: () -> Unit, onSignUpSuccess: () -> Unit) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Sign Up", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("User Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                auth.createUserWithEmailAndPassword(email.trim(), password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Update the user's display name
                            val user = auth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build()
                            user?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { updateTask ->
                                    isLoading = false
                                    if (updateTask.isSuccessful) {
                                        // Sign-up and profile update successful
                                        onSignUpSuccess()
                                    } else {
                                        // Profile update failed
                                        errorMessage = updateTask.exception?.message ?: "Sign-up failed"
                                    }
                                }
                        } else {
                            isLoading = false
                            // Sign-up failed
                            errorMessage = task.exception?.message ?: "Sign-up failed"
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { onNavigateBack() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Already have an account? Login")
        }
    }
}
