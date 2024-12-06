pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // R.W Excel
        maven {
            url = uri("https://jitpack.io")
        }
        // jcenter()
        // MeowBottomNavigation (tạm thời không sử dụng jcenter)
        flatDir {
            dirs("libs")
        }
    }
}

rootProject.name = "taskmanager"
include(":app")
