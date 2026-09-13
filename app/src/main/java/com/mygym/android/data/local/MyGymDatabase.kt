package com.mygym.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mygym.android.data.local.converter.DateConverters
import com.mygym.android.data.local.dao.WorkoutDao
import com.mygym.android.data.local.dao.WorkoutLogDao
import com.mygym.android.data.local.entity.ExerciseEntity
import com.mygym.android.data.local.entity.ExerciseLogEntity
import com.mygym.android.data.local.entity.WorkoutEntity
import com.mygym.android.data.local.entity.WorkoutLogEntity
import java.time.DayOfWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        WorkoutLogEntity::class,
        ExerciseLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class MyGymDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutLogDao(): WorkoutLogDao

    companion object {
        @Volatile
        private var INSTANCE: MyGymDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE exercises ADD COLUMN targetReps TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE exercises ADD COLUMN targetWeightKg REAL")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): MyGymDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyGymDatabase::class.java,
                    "mygym_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.workoutDao())
                }
            }
        }

        suspend fun populateInitialData(dao: WorkoutDao) {
            if (dao.getWorkoutCount() > 0) return

            // 1. Segunda: Peito & Tríceps
            val segId = dao.insertWorkout(
                WorkoutEntity(
                    title = "Peito & Tríceps",
                    tag = "PT",
                    dayOfWeek = DayOfWeek.MONDAY,
                    estimatedMinutes = 45,
                    colorPrimaryHex = 0xFF3B82F6,
                    colorSecondaryHex = 0xFF2563EB,
                    orderIndex = 1
                )
            )
            dao.insertExercises(
                listOf(
                    ExerciseEntity(workoutId = segId, name = "Supino Reto com Barra", sets = 4, reps = "8-10", weightKg = 60.0, restSeconds = 90, orderIndex = 0, targetReps = "10-12", targetWeightKg = 70.0),
                    ExerciseEntity(workoutId = segId, name = "Supino Inclinado com Halteres", sets = 3, reps = "10-12", weightKg = 24.0, restSeconds = 60, orderIndex = 1, targetReps = "12-15", targetWeightKg = 28.0),
                    ExerciseEntity(workoutId = segId, name = "Crossover Polia Média", sets = 3, reps = "12-15", weightKg = 15.0, restSeconds = 45, orderIndex = 2, targetReps = "15-20", targetWeightKg = 18.0),
                    ExerciseEntity(workoutId = segId, name = "Tríceps Corda no Pulley", sets = 4, reps = "10-12", weightKg = 25.0, restSeconds = 45, orderIndex = 3, targetReps = "12-15", targetWeightKg = 30.0),
                    ExerciseEntity(workoutId = segId, name = "Tríceps Francês Unilateral", sets = 3, reps = "12", weightKg = 12.0, restSeconds = 45, orderIndex = 4, targetReps = "12", targetWeightKg = 15.0)
                )
            )

            // 2. Terça: Costas & Bíceps
            val terId = dao.insertWorkout(
                WorkoutEntity(
                    title = "Costas & Bíceps",
                    tag = "CB",
                    dayOfWeek = DayOfWeek.TUESDAY,
                    estimatedMinutes = 50,
                    colorPrimaryHex = 0xFF60A5FA,
                    colorSecondaryHex = 0xFF3B82F6,
                    orderIndex = 2
                )
            )
            dao.insertExercises(
                listOf(
                    ExerciseEntity(workoutId = terId, name = "Puxada Frontal Aberta", sets = 4, reps = "10-12", weightKg = 50.0, restSeconds = 60, orderIndex = 0, targetReps = "12-15", targetWeightKg = 60.0),
                    ExerciseEntity(workoutId = terId, name = "Remada Baixa Triângulo", sets = 3, reps = "10-12", weightKg = 45.0, restSeconds = 60, orderIndex = 1, targetReps = "12", targetWeightKg = 50.0),
                    ExerciseEntity(workoutId = terId, name = "Remada Curvada com Barra", sets = 3, reps = "8-10", weightKg = 40.0, restSeconds = 90, orderIndex = 2, targetReps = "10-12", targetWeightKg = 50.0),
                    ExerciseEntity(workoutId = terId, name = "Rosca Direta Barra W", sets = 4, reps = "10-12", weightKg = 20.0, restSeconds = 45, orderIndex = 3, targetReps = "12-15", targetWeightKg = 24.0),
                    ExerciseEntity(workoutId = terId, name = "Rosca Martelo Alternada", sets = 3, reps = "12", weightKg = 14.0, restSeconds = 45, orderIndex = 4, targetReps = "12", targetWeightKg = 16.0)
                )
            )

            // 3. Quarta: Pernas & Panturrilha
            val quaId = dao.insertWorkout(
                WorkoutEntity(
                    title = "Pernas & Panturrilha",
                    tag = "PE",
                    dayOfWeek = DayOfWeek.WEDNESDAY,
                    estimatedMinutes = 55,
                    colorPrimaryHex = 0xFFF43F5E,
                    colorSecondaryHex = 0xFFE11D48,
                    orderIndex = 3
                )
            )
            dao.insertExercises(
                listOf(
                    ExerciseEntity(workoutId = quaId, name = "Agachamento Livre", sets = 4, reps = "8-10", weightKg = 70.0, restSeconds = 90, orderIndex = 0, targetReps = "10-12", targetWeightKg = 85.0),
                    ExerciseEntity(workoutId = quaId, name = "Leg Press 45º", sets = 4, reps = "10-12", weightKg = 180.0, restSeconds = 75, orderIndex = 1, targetReps = "12-15", targetWeightKg = 200.0),
                    ExerciseEntity(workoutId = quaId, name = "Cadeira Extensora", sets = 3, reps = "12-15", weightKg = 40.0, restSeconds = 45, orderIndex = 2, targetReps = "15-20", targetWeightKg = 50.0),
                    ExerciseEntity(workoutId = quaId, name = "Mesa Flexora", sets = 3, reps = "12", weightKg = 35.0, restSeconds = 45, orderIndex = 3, targetReps = "15", targetWeightKg = 40.0),
                    ExerciseEntity(workoutId = quaId, name = "Panturrilha em Pé no Smith", sets = 4, reps = "15-20", weightKg = 50.0, restSeconds = 45, orderIndex = 4, targetReps = "20-25", targetWeightKg = 60.0)
                )
            )

            // 4. Quinta: Ombros & Abdômen
            val quiId = dao.insertWorkout(
                WorkoutEntity(
                    title = "Ombros & Abdômen",
                    tag = "OA",
                    dayOfWeek = DayOfWeek.THURSDAY,
                    estimatedMinutes = 45,
                    colorPrimaryHex = 0xFF3B82F6,
                    colorSecondaryHex = 0xFF2563EB,
                    orderIndex = 4
                )
            )
            dao.insertExercises(
                listOf(
                    ExerciseEntity(workoutId = quiId, name = "Desenvolvimento com Halteres", sets = 4, reps = "10-12", weightKg = 18.0, restSeconds = 60, orderIndex = 0, targetReps = "12-15", targetWeightKg = 22.0),
                    ExerciseEntity(workoutId = quiId, name = "Elevação Lateral com Halteres", sets = 4, reps = "12-15", weightKg = 10.0, restSeconds = 45, orderIndex = 1, targetReps = "15-20", targetWeightKg = 12.0),
                    ExerciseEntity(workoutId = quiId, name = "Elevação Frontal na Polia", sets = 3, reps = "12", weightKg = 12.0, restSeconds = 45, orderIndex = 2, targetReps = "15", targetWeightKg = 15.0),
                    ExerciseEntity(workoutId = quiId, name = "Crucifixo Invertido", sets = 3, reps = "12-15", weightKg = 8.0, restSeconds = 45, orderIndex = 3, targetReps = "15-20", targetWeightKg = 10.0),
                    ExerciseEntity(workoutId = quiId, name = "Prancha Abdominal", sets = 3, reps = "60s", durationMinutes = 1, restSeconds = 45, orderIndex = 4, targetReps = "90s")
                )
            )

            // 5. Sexta: Superior Completo
            val sexId = dao.insertWorkout(
                WorkoutEntity(
                    title = "Superior Completo",
                    tag = "SU",
                    dayOfWeek = DayOfWeek.FRIDAY,
                    estimatedMinutes = 50,
                    colorPrimaryHex = 0xFF60A5FA,
                    colorSecondaryHex = 0xFF3B82F6,
                    orderIndex = 5
                )
            )
            dao.insertExercises(
                listOf(
                    ExerciseEntity(workoutId = sexId, name = "Supino Reto com Halteres", sets = 3, reps = "10", weightKg = 26.0, restSeconds = 60, orderIndex = 0, targetReps = "12", targetWeightKg = 30.0),
                    ExerciseEntity(workoutId = sexId, name = "Puxada Triângulo", sets = 3, reps = "10", weightKg = 50.0, restSeconds = 60, orderIndex = 1, targetReps = "12", targetWeightKg = 60.0),
                    ExerciseEntity(workoutId = sexId, name = "Desenvolvimento Militar", sets = 3, reps = "10", weightKg = 16.0, restSeconds = 60, orderIndex = 2, targetReps = "12", targetWeightKg = 20.0),
                    ExerciseEntity(workoutId = sexId, name = "Rosca Scott", sets = 3, reps = "12", weightKg = 16.0, restSeconds = 45, orderIndex = 3, targetReps = "15", targetWeightKg = 18.0),
                    ExerciseEntity(workoutId = sexId, name = "Tríceps Mergulho no Banco", sets = 3, reps = "12-15", restSeconds = 45, orderIndex = 4, targetReps = "15-20")
                )
            )

            // 6. Sábado: Cardio & Core
            val sabId = dao.insertWorkout(
                WorkoutEntity(
                    title = "Cardio & Core",
                    tag = "CC",
                    dayOfWeek = DayOfWeek.SATURDAY,
                    estimatedMinutes = 35,
                    colorPrimaryHex = 0xFFFB7185,
                    colorSecondaryHex = 0xFFE11D48,
                    orderIndex = 6
                )
            )
            dao.insertExercises(
                listOf(
                    ExerciseEntity(workoutId = sabId, name = "Corrida na Esteira", sets = 1, reps = "15 min", durationMinutes = 15, restSeconds = 60, orderIndex = 0, targetReps = "20 min"),
                    ExerciseEntity(workoutId = sabId, name = "Prancha Abdominal", sets = 3, reps = "45 seg", durationMinutes = 1, restSeconds = 45, orderIndex = 1, targetReps = "60 seg"),
                    ExerciseEntity(workoutId = sabId, name = "Russian Twist com Carga", sets = 3, reps = "20 reps", weightKg = 6.0, restSeconds = 45, orderIndex = 2, targetReps = "25 reps", targetWeightKg = 8.0),
                    ExerciseEntity(workoutId = sabId, name = "Bicicleta no Solo", sets = 3, reps = "20 reps", restSeconds = 45, orderIndex = 3, targetReps = "30 reps")
                )
            )
        }

    }
}

