package com.example.projectstages.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.data.entity.ProjectsWithTasks
import com.example.projectstages.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Dao
abstract class ProjectDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertProject(project: ProjectEntity) : Long

//    @Query("SELECT * FROM projects ORDER BY id ASC")
//    abstract fun getProjects() : Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects ORDER BY id ASC")
    abstract fun getProjects2() : Flow<List<ProjectsWithTasks>>

//    @Query("SELECT COUNT(*) from tasks WHERE projectId = :projectID")
//    abstract suspend fun countTasksByProjectId(projectID: Long) : Int

//    @Query("SELECT COUNT(*) from tasks WHERE projectId = :projectID AND state =:taskState")
//    abstract suspend fun countStatesTasksByProjectId(projectID: Long, taskState: Int) : Int
//    //TODO(Think about name)

//    @Query("SELECT createdTimestamp FROM tasks WHERE projectId = :projectID ORDER BY id DESC LIMIT 1")
//    abstract suspend fun getLastTaskTimestampsByProjectId(projectID: Long) : Long

    @Query("SELECT * FROM tasks WHERE projectId =:projectId ORDER BY id")
    abstract fun getTasksByProjectId(projectId: Long) : Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertTask(taskEntity: TaskEntity) : Long

    @Query("DELETE FROM tasks WHERE id =:id")
    abstract suspend fun deleteTaskById(id: Long) : Int

    @Query("UPDATE tasks SET description =:description, state=:type, createdTimestamp=:timestamp WHERE id=:id")
    abstract suspend fun updateTaskById(id: Long, description: String, type: Int, timestamp: Long) : Int

    @Query("SELECT description FROM tasks WHERE id =:taskID")
    abstract suspend fun getTaskDescriptionByTaskId(taskID: Long) : String

    @Query("SELECT state FROM tasks WHERE id =:taskID")
    abstract suspend fun getTaskStateByTaskId(taskID: Long) : Int
}