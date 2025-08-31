pluginManagement {
    repositories {
        maven { setUrl("https://www.jitpack.io") }
        maven { setUrl("https://maven.aliyun.com/repository/public/") }
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
        maven { setUrl("https://www.jitpack.io") }
        maven { setUrl("https://maven.aliyun.com/repository/public/") }
        google()
        mavenCentral()
    }
}

rootProject.name = "HamburgerIPC"
include(":ipc:common")
include(":ipc:client")
include(":ipc:service")

include(":demo:ipc:common")
include(":demo:ipc:client_demo")
include(":demo:ipc:service_demo")