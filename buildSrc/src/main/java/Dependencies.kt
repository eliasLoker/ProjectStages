object Dependencies {

    private const val kotlinVersion = GlobalDependencies.kotlinVersion
    private const val daggerHiltVersion =  GlobalDependencies.daggerHiltVersion
    private const val navigationVersion = GlobalDependencies.navigationVersion

    private const val coroutines = "1.6.4"
    private const val roomVersion = "2.5.0"

    private const val androidXCore = "androidXcore"
    private const val appCompat = "appCompat"
    private const val material = "material"
    private const val constraintLayout = "constraintLayout"

    private const val stdLib = "stdLib"
    private const val lifecycleRuntime = "lifecycleRuntime"
    private const val lifecycleViewModel = "lifecycleViewModel"
    private const val navigation = "navigation"

    private const val jUnit = "jUnit"
    private const val jUnitExt = "jUnitExt"
    private const val espressoCore = "espressoCore"

    private const val coroutinesCore = "coroutinesCore"
    private const val coroutinesAndroid = "coroutinesAndroid"
    private const val roomRuntime = "roomRuntime"
    private const val roomKtx = "roomKtx"
    private const val roomCompiler = "roomCompiler"
    private const val daggerHiltCore = "daggerHiltCore"
    private const val daggerHiltCompiler = "daggerHiltCompiler"

    fun getAppDependencies() : Array<DependencyData> {
        val dependenciesMap = getDependencies()
        return arrayOf(
            dependenciesMap[stdLib]!!,
            dependenciesMap[androidXCore]!!,
            dependenciesMap[appCompat]!!,
            dependenciesMap[material]!!,
            dependenciesMap[constraintLayout]!!,
            dependenciesMap[jUnit]!!,
            dependenciesMap[jUnitExt]!!,
            dependenciesMap[espressoCore]!!,
            dependenciesMap[navigation]!!,
            dependenciesMap[roomRuntime]!!,
            dependenciesMap[roomKtx]!!,
            dependenciesMap[roomCompiler]!!,
            dependenciesMap[coroutinesCore]!!,
            dependenciesMap[coroutinesAndroid]!!,
            dependenciesMap[daggerHiltCore]!!,
            dependenciesMap[daggerHiltCompiler]!!,
        )
    }

    fun getBaseDependencies() : Array<DependencyData> {
        val dependenciesMap = getDependencies()
        return arrayOf(
            dependenciesMap[material]!!,
            dependenciesMap[lifecycleRuntime]!!,
            dependenciesMap[lifecycleViewModel]!!,
            dependenciesMap[coroutinesAndroid]!!,
        )
    }

    private fun getDependencies() : Map<String, DependencyData> {
        return mapOf(
            //CORE
            androidXCore to DependencyData(
                dependency      = "androidx.core:core-ktx:1.9.0",
                dependencyType  = DependencyType.Implementation
            ),
            appCompat to DependencyData(
                dependency      = "androidx.appcompat:appcompat:1.6.0",
                dependencyType  = DependencyType.Implementation
            ),
            material to DependencyData(
                dependency      = "com.google.android.material:material:1.7.0",
                dependencyType  = DependencyType.Implementation
            ),
            constraintLayout to DependencyData(
                dependency      = "androidx.constraintlayout:constraintlayout:2.1.4",
                dependencyType  = DependencyType.Implementation
            ),

            stdLib to DependencyData(
                dependency      = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion",
                dependencyType  = DependencyType.Implementation
            ),
            lifecycleRuntime to DependencyData(
                dependency      = "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1",
                dependencyType  = DependencyType.Implementation
            ),
            lifecycleViewModel to DependencyData(
                dependency      = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1",
                dependencyType  = DependencyType.Implementation
            ),
            navigation to DependencyData(
                dependency      = "androidx.navigation:navigation-fragment-ktx:$navigationVersion",
                dependencyType  = DependencyType.Implementation
            ),

            //TEST
            jUnit to DependencyData(
                dependency      = "junit:junit:4.13.2",
                dependencyType  = DependencyType.TestImplementation
            ),
            jUnitExt to DependencyData(
                dependency      = "androidx.test.ext:junit:1.1.5",
                dependencyType  = DependencyType.AndroidTestImplementation
            ),
            espressoCore to DependencyData(
                dependency      = "androidx.test.espresso:espresso-core:3.5.1",
                dependencyType  = DependencyType.AndroidTestImplementation
            ),

            //COROUTINES
            coroutinesCore to DependencyData(
                dependency      = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines",
                dependencyType  = DependencyType.Implementation
            ),
            coroutinesAndroid to DependencyData(
                dependency      = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines",
                dependencyType  = DependencyType.Implementation
            ),

            //ROOM
            roomRuntime to DependencyData(
                dependency      = "androidx.room:room-runtime:$roomVersion",
                dependencyType  = DependencyType.Implementation
            ),
            roomKtx to DependencyData(
                dependency      = "androidx.room:room-ktx:$roomVersion",
                dependencyType  = DependencyType.Implementation
            ),
            roomCompiler to DependencyData(
                dependency      = "androidx.room:room-compiler:$roomVersion",
                dependencyType  = DependencyType.Kapt
            ),

            //DAGGER HILT
            daggerHiltCore to DependencyData(
                dependency      = "com.google.dagger:hilt-android:$daggerHiltVersion",
                dependencyType  = DependencyType.Implementation
            ),
            daggerHiltCompiler to DependencyData(
                dependency      = "com.google.dagger:hilt-compiler:$daggerHiltVersion",
                dependencyType  = DependencyType.Kapt
            ),
        )
    }

    data class DependencyData(
        val dependency: String,
        val dependencyType: DependencyType
    )

    sealed class DependencyType {
        object Implementation : DependencyType()
        object TestImplementation : DependencyType()
        object AndroidTestImplementation : DependencyType()
        object Kapt : DependencyType()
    }
}