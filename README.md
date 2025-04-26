<p align="center">
<img src="https://github.com/user-attachments/assets/eeb50c5b-a157-492f-8ade-a459e1219117" height = "100px"/>
</p>

<div align = "center">
<h1 align="center"> Bluetooth Scanner </h1>
<a href="https://www.codefactor.io/repository/github/dangiashish/geotagimage/overview/master"><img src="https://www.codefactor.io/repository/github/dangiashish/geotagimage/badge/master" alt="CodeFactor" /></a>
<a href="https://jitpack.io/#dangiashish/bluetooth-scanner"><img src="https://jitpack.io/v/dangiashish/bluetooth-scanner.svg" alt=""/></a>

</div>

### Gradle

Add repository in your `settings.gradle`
 
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
#### OR 
in your `settings.gradle.kts`
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven( url = "https://jitpack.io")
    }
}

```

Updated 

```gradle
pluginManagement {
    repositories {
        ...
        maven( url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven( url = "https://jitpack.io")
    }
}
```
### Add dependency :
Add dependency in your `build.gradle.kts` (module-level) file :

```groovy
dependencies{

    implementation("com.github.dangiashish:bluetooth-scanner:1.0.1-beta")
}
```
#### OR
Add dependency in your `build.gradle` (module-level) file :

```groovy
dependencies{

    implementation 'com.github.dangiashish:bluetooth-scanner:1.0.1-beta'
}
```


#### Thank You
