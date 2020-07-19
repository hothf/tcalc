<div align="center">
<img src="./icon.png" width="150" height="150" />

<h1 align="center">
    TCalc
</h1>

<a href="https://git.jamitlabs.net/th/tcalc/-/releases">Changelog</a> •
<a href="#setup">Setup</a> •
<a href="#architecture">Architecture</a> •
<a href="#contribute">Contribute</a> •
<a href="#licensing">Licensing</a>

</div>

This is a bit under construction for now.


## Screens
--
## Setup
This app is based on android "jetpack" and architecture components and uses at least Gradle 4.10.1
This means that at least `Android Studio 3.3` should be used for successful builds.

`Kotlin` is used as the main language of this project.

## Testing
This project uses **unit tests** with injected mocks, residing under the `test...mocked` package for pure business logic tests, roboelectric unit tests under `test..roboelectric` to android mocked capabilities of the app such as importing and exporting files and instrumented tests under `androidTest` for pure ui testsing.

Please note that the tests can *not be run all at once*. 
Especially roboelectric seems to have initializing and parallelism bugs when running multiple tests at once, making it only feasible to run tests per file. Otherwise perfectly fine working tests just fail. 

## Architecture
To accelerate development and testability, a `Model-View-ViewModel (MVVM)` approach with android [architecture components](https://developer.android.com/jetpack/arch/) and [Databinding](https://developer.android.com/topic/libraries/data-binding/) is used. Base classes for viewModels and fragments give a structure to build upon. `LiveData<>` is used in conjuction with Databinding for updating activities and fragments with their corresponding `lifecycle`.

Business logic will mainly be implemented in `viewModels`. Only certain callbacks will need activities or fragments.

## Linting
The [detekt](https://github.com/arturbosch/detekt) plugin is used for kotlin linting. Usage: `./gradlew detektCheck`

## Javadoc / KDoc
Javadoc or KDoc generation can be triggered via the [dokka](https://github.com/Kotlin/dokka) plugin. Usage: `./gradlew dokka`

## Contribute
Contribution and feedback are always welcome.
Please work in your own branch (`work/your_feature`) and make a Merge-Request. After the request is reviewed and an approval has been given, the code changes can be merged into the defaulting `stable` branch.

## Licensing

Copyright (c) 2020 Thomas Hofmann.

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at https://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.
