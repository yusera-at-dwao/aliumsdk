 # AliumAndroidSDK

Alium's Android SDK.


## Install Via Gradle

### for gradle 6.5 and above add the following code to your settings.gradle
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven{url 'https://jitpack.io'}
    }
}
```

for Gradle below 6.5, add the code to your project's build.gradle
```groovy
allprojects{
	repositories{
        google()
        jcenter()
        maven{url 'https://jitpack.io'}
    }
}
```
### Add the dependency to your app's build.gradle

```groovy
dependencies {
	        implementation 'com.github.yusera-at-dwao:aliumsdk:test-1.0.2'
	}
```
### In your app's build.gradle update minSdkVersion to 21
```groovy
defaultConfig {
				....
				minSdkVersion 21
    }
```

## Usage
### Configure the SDK with Configure()

import the alium package.

```java
import com.dwao.alium.survey.Alium;
```

Call configure method in MainApplication.java.

```java


  AliumSdk.configure("your_project_url");
```

This method should be the first method called when initializing the Alium SDK. It sets the project key or URL and must be invoked as soon as possible in your application's lifecycle.

### Trigger Surveys

Use AliumSdk.loadAliumSurvey() with the screen name to display surveys.

Create an instnace **SurveyParameters** to pass the screen name and parameters.

```java
 Alium.loadAliumSurvey(this, new SurveyParameters("firstscreen", null));
```

### Passing custom parameters to survey

```java
   Alium.loadAliumSurvey(this, new SurveyParameters("firstscreen", new HashMap()));
```
