package com.template.project.model.repositories


import android.content.Context
import android.os.AsyncTask
import com.template.project.data.local.ProjectDatabase
import com.template.project.data.local.entities.Todo
import com.template.project.data.network.ApiBuilder
import com.template.project.tools.AsyncData
import kotlinx.coroutines.*
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.RootContext

@EBean
class TodoRepository {

    @RootContext
    protected lateinit var applicationContext: Context

    private val database = lazy { ProjectDatabase.getDatabase(applicationContext) }
    private val network = ApiBuilder().todoApi()

    /** Espresso wait automatically until all AsyncTask.THREAD_POOL_EXECUTOR threads are idle. */
    private val coroutineDispatcher = AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()

    fun getTodos(): AsyncData<List<Todo>> {
        val result = AsyncData<List<Todo>>(null)


        CoroutineScope(coroutineDispatcher).launch {

            if (database.value.todo().getAll().isEmpty()) {
                database.value.todo().insert(Todo(1, 1, "first", false))
                database.value.todo().insert(Todo(2, 1, "second", false))
                database.value.todo().insert(Todo(3, 1, "it", false))
                database.value.todo().insert(Todo(4, 1, "will", false))
                database.value.todo().insert(Todo(5, 1, "happen", false))
            }

            result.value = database.value.todo().getAll()

            val response = network.todos().execute()

            val content = response.body()!!
            database.value.todo().deleteAll()
            content.forEach {
                database.value.todo().insert(
                    Todo(
                        it.id,
                        it.userId,
                        it.title,
                        it.completed
                    )
                )
            }

            result.value = database.value.todo().getAll()
        }

        return result
    }
}