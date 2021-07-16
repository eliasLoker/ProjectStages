package com.example.projectstages.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projectstages.data.entity.ProjectEntity
import com.example.projectstages.data.entity.ProjectsWithTasks
import com.example.projectstages.data.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProjectDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertProject(project: ProjectEntity) : Long

    @Query("SELECT * FROM projects ORDER BY id ASC")
    abstract fun getProjects() : Flow<List<ProjectsWithTasks>>

    @Query("DELETE FROM projects WHERE id =:projectID")
    abstract fun deleteProjectById(projectID: Long) : Int

    @Query("UPDATE projects SET name=:name, type=:type WHERE id=:projectID ")
    abstract fun updateProjectById(projectID: Long, name: String, type: Int) : Int

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