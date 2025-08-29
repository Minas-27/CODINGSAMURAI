package com.example.mychatapp.firebase

import android.util.Log
import com.example.mychatapp.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

object FirebaseHelper {
    private const val TAG = "FirebaseHelper"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance().reference

    // ✅ sanitize emails for Firebase keys
    private fun sanitizeEmail(email: String): String {
        return email.replace(".", ",")
    }

    // ✅ Shared chatId based on 2 emails
    private fun getChatId(user1: String, user2: String): String {
        val u1 = sanitizeEmail(user1)
        val u2 = sanitizeEmail(user2)
        return if (u1 < u2) "${u1}_$u2" else "${u2}_$u1"
    }

    // ================= AUTH =================

    // ================= AUTH =================
    fun signUp(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userKey = sanitizeEmail(email)
                    val user = mapOf(
                        "email" to email,
                        "online" to true,
                        "lastSeen" to System.currentTimeMillis()
                    )
                    FirebaseDatabase.getInstance().reference
                        .child("users").child(userKey).setValue(user)

                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userKey = sanitizeEmail(email)
                    val user = mapOf(
                        "email" to email,
                        "online" to true,
                        "lastSeen" to System.currentTimeMillis()
                    )
                    FirebaseDatabase.getInstance().reference
                        .child("users").child(userKey).updateChildren(user)

                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // ================= USERS =================

    fun getAllUsers(onResult: (List<Pair<String, String>>) -> Unit) {
        FirebaseDatabase.getInstance().reference
            .child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<Pair<String, String>>()
                    for (child in snapshot.children) {
                        val email = child.child("email").getValue(String::class.java) ?: continue
                        val name = email.substringBefore("@") // simple name
                        users.add(name to email)
                    }
                    onResult(users)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // ================= MESSAGES =================

    fun sendMessage(toUser: String, text: String) {
        val currentUser = auth.currentUser?.email ?: return
        val chatId = getChatId(currentUser, toUser)
        val msg = Message(
            sender = currentUser,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        database.child("chats").child(chatId).child("messages").push().setValue(msg)
            .addOnSuccessListener { Log.d(TAG, "✅ Message sent: $msg") }
            .addOnFailureListener { Log.e(TAG, "❌ Failed to send: ${it.message}") }
    }

    // ✅ NEW: return whole conversation instead of just single message
    fun listenConversation(partner: String, onMessages: (List<Message>) -> Unit) {
        val currentUser = auth.currentUser?.email ?: return
        val chatId = getChatId(currentUser, partner)

        database.child("chats").child(chatId).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val msgs = mutableListOf<Message>()
                    for (child in snapshot.children) {
                        val msg = child.getValue(Message::class.java)
                        msg?.let { msgs.add(it) }
                    }
                    msgs.sortBy { it.timestamp }
                    onMessages(msgs)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "❌ Conversation listen cancelled: ${error.message}")
                }
            })
    }

    // ================= STATUS =================

    fun setUserOnline(isOnline: Boolean) {
        val currentUser = auth.currentUser?.email ?: return
        val userKey = sanitizeEmail(currentUser)

        val data = mapOf(
            "online" to isOnline,
            "lastSeen" to System.currentTimeMillis()
        )

        database.child("users")
            .child(userKey)
            .updateChildren(data)
            .addOnSuccessListener { Log.d(TAG, "✅ Status updated: $isOnline") }
            .addOnFailureListener { Log.e(TAG, "❌ Failed to update status", it) }
    }

    fun listenUserStatus(partner: String, onStatus: (Boolean, Long) -> Unit) {
        val userKey = sanitizeEmail(partner)

        database.child("users")
            .child(userKey)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val online = snapshot.child("online").getValue(Boolean::class.java) ?: false
                    val lastSeen = snapshot.child("lastSeen").getValue(Long::class.java) ?: 0L
                    onStatus(online, lastSeen)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "❌ Status listen cancelled: ${error.message}")
                }
            })
    }

    // ================= HELPERS =================

    fun getCurrentUser() = auth.currentUser?.email
    fun signOut() = auth.signOut()
}
