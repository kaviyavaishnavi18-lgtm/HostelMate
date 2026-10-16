package com.example.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.models.Student
import com.example.ui.components.FourStatPillsRow
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.NeoDarkOutline
import com.example.ui.theme.NeoDarkText
import com.example.ui.theme.NeoMutedText
import com.example.ui.theme.PastelBlue
import com.example.ui.theme.PastelLilacBackground
import com.example.ui.theme.PastelMint
import com.example.ui.theme.PastelPeach
import com.example.ui.theme.PastelPink
import com.example.ui.theme.StatusNegativeRed
import com.example.ui.theme.StatusPositiveGreen
import com.example.ui.viewmodel.HostelMateViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(viewModel: HostelMateViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val currentStudent by viewModel.currentStudent.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val financialSummary by viewModel.userFinancialSummary.collectAsStateWithLifecycle()
    val groups by viewModel.allGroups.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()

    var firebaseUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isSigningIn by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            firebaseUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelLilacBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Profile & Cloud Sync",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = NeoDarkText
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "☁️", fontSize = 24.sp)
            }
            Text(
                text = "Manage your roommate profile and Firebase Cloud sync",
                style = MaterialTheme.typography.bodySmall,
                color = NeoMutedText
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Active Profile Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, NeoDarkOutline),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StudentAvatar(
                        name = currentStudent?.name ?: "Student",
                        avatarColorHex = currentStudent?.avatarColorHex ?: "#10B981",
                        size = 72.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentStudent?.name ?: "Student",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = NeoDarkText
                    )
                    Text(
                        text = "${currentStudent?.roomNumber} • ${currentStudent?.email}",
                        fontSize = 13.sp,
                        color = NeoMutedText
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PastelPeach,
                        border = BorderStroke(1.5.dp, NeoDarkOutline)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = NeoDarkText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UPI: ${currentStudent?.upiId}",
                                fontWeight = FontWeight.Black,
                                color = NeoDarkText,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = NeoDarkOutline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    FourStatPillsRow(
                        stat1Label = "Lent",
                        stat1Val = "₹%,.0f".format(financialSummary.totalOthersOweMe),
                        stat1Bg = PastelMint,

                        stat2Label = "Borrowed",
                        stat2Val = "₹%,.0f".format(financialSummary.totalIOwe),
                        stat2Bg = PastelPink,

                        stat3Label = "Groups",
                        stat3Val = "${groups.size}",
                        stat3Bg = PastelPeach,

                        stat4Label = "Net",
                        stat4Val = if (financialSummary.netBalance >= 0) "+₹%,.0f".format(financialSummary.netBalance)
                        else "-₹%,.0f".format(kotlin.math.abs(financialSummary.netBalance)),
                        stat4Bg = PastelBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Firebase Cloud Sync Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, NeoDarkOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PastelMint,
                                border = BorderStroke(1.dp, NeoDarkOutline),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = NeoDarkText, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Firebase Cloud Sync",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = NeoDarkText
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (firebaseUser != null) PastelMint else PastelPeach,
                            border = BorderStroke(1.dp, NeoDarkOutline)
                        ) {
                            Text(
                                text = if (firebaseUser != null) "Connected" else "Offline",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeoDarkText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (firebaseUser != null) {
                        Text(
                            text = "Signed in as: ${firebaseUser?.displayName ?: firebaseUser?.email ?: "Google User"}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeoDarkText
                        )
                        Text(
                            text = "UID: ${firebaseUser?.uid?.take(16)}...",
                            fontSize = 11.sp,
                            color = NeoMutedText
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (syncStatus != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PastelMint,
                                border = BorderStroke(1.dp, NeoDarkOutline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = syncStatus!!,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeoDarkText,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.syncAllToCloud() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeoDarkOutline),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, NeoDarkOutline),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("sync_to_firebase_button")
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sync Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    Firebase.auth.signOut()
                                    coroutineScope.launch {
                                        try {
                                            credentialManager.clearCredentialState(ClearCredentialStateRequest())
                                        } catch (e: Exception) {
                                            Log.e("Auth", "Failed to clear credential state", e)
                                        }
                                        firebaseUser = null
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PastelPink),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.5.dp, NeoDarkOutline)
                            ) {
                                Text("Sign Out", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = StatusNegativeRed)
                            }
                        }
                    } else {
                        Text(
                            text = "Connect your Google account to back up and sync your shared hostel expenses securely to Google Cloud Firestore.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeoMutedText
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val clientId = try {
                                    context.getString(R.string.default_web_client_id)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Google Client ID configuration missing", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                isSigningIn = true
                                val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
                                val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

                                coroutineScope.launch {
                                    try {
                                        val result = credentialManager.getCredential(context as Activity, request)
                                        val credential = result.credential
                                        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                            val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                                            val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                                            Firebase.auth.signInWithCredential(authCredential).await()
                                            firebaseUser = Firebase.auth.currentUser
                                            Toast.makeText(context, "Signed in with Google successfully!", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: GetCredentialCancellationException) {
                                        Log.w("Auth", "Google Sign-In cancelled: ${e.message}")
                                    } catch (e: Exception) {
                                        Log.e("Auth", "Google Sign-In error", e)
                                        Toast.makeText(context, "Sign in failed: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isSigningIn = false
                                    }
                                }
                            },
                            enabled = !isSigningIn,
                            colors = ButtonDefaults.buttonColors(containerColor = NeoDarkOutline),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.5.dp, NeoDarkOutline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("google_sign_in_button")
                        ) {
                            if (isSigningIn) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Sign in with Google", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Roommate Persona Switcher List
        item {
            Text(
                text = "Switch Roommate Persona:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = NeoDarkText
            )
            Text(
                text = "Tap any roommate to immediately recalculate the app from their point of view:",
                style = MaterialTheme.typography.bodySmall,
                color = NeoMutedText
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(allStudents) { student ->
            val isCurrent = student.id == currentStudent?.id
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCurrent) PastelPeach else Color.White
                ),
                border = BorderStroke(1.5.dp, NeoDarkOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { viewModel.switchCurrentStudent(student.id) }
                    .testTag("switch_profile_to_${student.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StudentAvatar(name = student.name, avatarColorHex = student.avatarColorHex, size = 38.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = student.name,
                            fontWeight = FontWeight.Bold,
                            color = NeoDarkText
                        )
                        Text(
                            text = "${student.roomNumber} • ${student.upiId}",
                            fontSize = 11.sp,
                            color = NeoMutedText
                        )
                    }
                    if (isCurrent) {
                        Surface(
                            shape = CircleShape,
                            color = NeoDarkOutline,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
