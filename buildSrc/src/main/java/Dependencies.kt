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
            dependenciesMap[androidXCore]!!,
            dependenciesMap[appCompat]!!,
            dependenciesMap[material]!!,
            dependenciesMap[stdLib]!!,
            dependenciesMap[lifecycleRuntime]!!,
            dependenciesMap[lifecycleViewModel]!!,
            dependenciesMap[jUnit]!!,
            dependenciesMap[jUnitExt]!!,
            dependenciesMap[espressoCore]!!,
            dependenciesMap[coroutinesCore]!!,
            dependenciesMap[coroutinesAndroid]!!,
            dependenciesMap[roomRuntime]!!,
        )
    }

    private fun getDependencies() : Map<String, DependencyData> {
        return mapOf(
            //CORE
            androidXCore to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "androidx.core:core-ktx:1.9.0"
            ),
            appCompat to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "androidx.appcompat:appcompat:1.6.0"
            ),
            material to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "com.google.android.material:material:1.7.0"
            ),
            constraintLayout to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency = "androidx.constraintlayout:constraintlayout:2.1.4"
            ),

            stdLib to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
            ),
            lifecycleRuntime to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "androidx.lifecycle:lifecycle-runtime-ktx:2.5.1"
            ),
            lifecycleViewModel to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1"
            ),
            navigation to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "androidx.navigation:navigation-fragment-ktx:$navigationVersion"
            ),

            //TEST
            jUnit to DependencyData(
                dependencyType  = DependencyType.TestImplementation,
                dependency      = "junit:junit:4.13.2"
            ),
            jUnitExt to DependencyData(
                dependencyType  = DependencyType.AndroidTestImplementation,
                dependency      = "androidx.test.ext:junit:1.1.5"
            ),
            espressoCore to DependencyData(
                dependencyType  = DependencyType.AndroidTestImplementation,
                dependency      = "androidx.test.espresso:espresso-core:3.5.1"
            ),

            //COROUTINES
            coroutinesCore to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines"
            ),
            coroutinesAndroid to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines"
            ),

            //ROOM
            roomRuntime to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "androidx.room:room-runtime:$roomVersion"
            ),
            roomKtx to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "androidx.room:room-ktx:$roomVersion"
            ),
            roomCompiler to DependencyData(
                dependencyType  = DependencyType.Kapt,
                dependency      = "androidx.room:room-compiler:$roomVersion"
            ),

            //DAGGER HILT
            daggerHiltCore to DependencyData(
                dependencyType  = DependencyType.Implementation,
                dependency      = "com.google.dagger:hilt-android:$daggerHiltVersion"
            ),
            daggerHiltCompiler to DependencyData(
                dependencyType  = DependencyType.Kapt,
                dependency      = "com.google.dagger:hilt-compiler:$daggerHiltVersion"
            ),
        )
    }

    data class DependencyData(
        val dependencyType: DependencyType,
        val dependency: String
    )

    sealed class DependencyType {
        object Implementation : DependencyType()
        object TestImplementation : DependencyType()
        object AndroidTestImplementation : DependencyType()
        object Kapt : DependencyType()
    }
}