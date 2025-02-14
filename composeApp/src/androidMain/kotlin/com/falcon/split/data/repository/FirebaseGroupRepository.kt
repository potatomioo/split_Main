package com.falcon.split.data.repository

import com.falcon.split.contact.Contact
import com.falcon.split.data.Repository.GroupRepository
import com.falcon.split.data.network.models_app.Group
import com.falcon.split.data.network.models_app.GroupMember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseGroupRepository : GroupRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getPhoneNumberFromId(userId: String): String? {
        val querySnapshot = db.collection("phoneNumbers")
            .whereEqualTo("userId", userId)
            .get()
            .await()

        return querySnapshot.documents.firstOrNull()?.getString("phoneNumber")
    }

    override suspend fun createGroup(name: String, members: List<Contact>): Result<Group> {
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser
                ?: return Result.failure(Exception("User not logged in"))

            val groupMembers = members.map { contact ->
                // Get user info from phoneNumbers collection if exists
                val userDoc = db.collection("phoneNumbers")
                    .document(contact.contactNumber)
                    .get()
                    .await()


                GroupMember(
                    userId = userDoc.getString("userId"),
                    phoneNumber = contact.contactNumber,
                    name = contact.contactName,
                    balance = 0.0  // Default balance for new member
                )
            }

            val currentUserPhoneNumber = getPhoneNumberFromId(currentUser.uid)
            val currentTime = System.currentTimeMillis()
            val groupRef = db.collection("groups").document()

            val group = Group(
                id = groupRef.id,
                name = name,
                createdBy = currentUser.uid,
                members = groupMembers + GroupMember(
                    userId = currentUser.uid,
                    phoneNumber = currentUserPhoneNumber.toString(),
                    name = currentUser.displayName,
                    balance = 0.0
                ),
                totalAmount = 0.0,
                createdAt = currentTime,
                updatedAt = null
            )

            groupRef.set(group).await()

            Result.success(group)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGroupsByUser(userId: String): Flow<List<Group>> = callbackFlow {
        val listener = db.collection("groups")
            .whereArrayContains("members.userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val groups = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Group::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(groups)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addMembersToGroup(groupId: String, memberPhoneNumbers: List<String>): Result<Unit> {
        return try {
            coroutineScope {
                val groupRef = db.collection("groups").document(groupId)
                val groupDoc = groupRef.get().await()
                val group = groupDoc.toObject(Group::class.java)!!

                val newMembers = memberPhoneNumbers.map { phoneNumber ->
                    val userDoc = db.collection("phoneNumbers")
                        .document(phoneNumber)
                        .get()
                        .await()

                    GroupMember(
                        userId = userDoc.getString("userId"),
                        phoneNumber = phoneNumber,
                        name = null,
                        balance = 0.0
                    )
                }

                val updatedMembers = (group.members + newMembers).distinctBy { it.phoneNumber }
                groupRef.update("members", updatedMembers).await()

                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGroupDetails(groupId: String): Flow<Group> = callbackFlow {
        val listener = db.collection("groups")
            .document(groupId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.toObject(Group::class.java)?.let { group ->
                    trySend(group.copy(id = snapshot.id))
                }
            }

        awaitClose { listener.remove() }
    }
}