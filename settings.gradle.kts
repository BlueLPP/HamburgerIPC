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
    }
}

rootProject.name = "HamburgerIPC"
include(":ipc:annotations")
include(":ipc:common")
include(":ipc:client")
include(":ipc:service")

include(":demo:ipc:common")
include(":demo:ipc:client_demo")
include(":demo:ipc:service_demo")