package tech.androidplay.sonali.todo.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import tech.androidplay.sonali.todo.data.repository.AuthRepository
import tech.androidplay.sonali.todo.data.repository.TaskRepository
import tech.androidplay.sonali.todo.data.viewmodel.TaskViewModel
import tech.androidplay.sonali.todo.ui.adapter.TodoAdapter
import tech.androidplay.sonali.todo.utils.CacheManager
import tech.androidplay.sonali.todo.utils.Constants.FIRESTORE_COLLECTION
import tech.androidplay.sonali.todo.utils.PermissionManager

/**
 * Created by Androidplay
 * Author: Ankush
 * On: 24/Aug/2020
 * Email: ankush@androidplay.in
 */

@Module
@InstallIn(ActivityComponent::class)
class AppModule {

    @Provides
    fun provideFirebaseInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    fun providesFireStoreReference(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(FIRESTORE_COLLECTION)
    }

    @Provides
    fun providesStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }

    @Provides
    fun providesPermissionManager(): PermissionManager {
        return PermissionManager()
    }

    @Provides
    fun providesCacheManager(): CacheManager {
        return CacheManager()
    }


    @Provides
    fun providesTaskRepository(
        firebaseAuth: FirebaseAuth,
        collectionReference: CollectionReference,
        storageReference: StorageReference
    ): TaskRepository {
        return TaskRepository(firebaseAuth, collectionReference, storageReference)
    }

    @Provides
    fun provideTaskViewModel(
        taskRepository: TaskRepository
    ): TaskViewModel {
        return TaskViewModel(taskRepository)
    }

    @Provides
    fun providesTodoAdapter(
        viewModel: TaskViewModel
    ): TodoAdapter {
        return TodoAdapter(viewModel)
    }

    @Provides
    fun providesAuthRepository(
        firebaseAuth: FirebaseAuth
    ): AuthRepository {
        return AuthRepository(firebaseAuth)
    }

}