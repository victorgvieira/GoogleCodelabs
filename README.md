# Build adaptive apps with Jetpack Compose Codelab

This folder contains the source code for
the [Build adaptive apps with Jetpack Compose Codelab](https://codelabs.developers.google.com/jetpack-compose-adaptability)

## License

```
Copyright 2022 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

# 1. Introduction

In this codelab you learn how to build adaptive apps for phones, tablets, and foldables, and how
they enhance reachability with Jetpack Compose. You also learn best practices for using Material 3
components and theming.

Before we dive in, it's important to understand what we mean by adaptability.

### Adaptability

The UI for your app should be responsive to account for different screen sizes, orientations and
form factors. An adaptive layout changes based on the screen space available to it. These changes
range from simple layout adjustments to fill up space, choosing respective navigation styles, to
changing layouts completely to make use of additional room.

To learn more, check
out [Adaptive design](https://m3.material.io/foundations/layout/understanding-layout/overview).

In this codelab you explore how to use and think about adaptability when using Jetpack Compose. You
build an application, called Reply, that shows you how to implement adaptability for all kinds of
screens, and how adaptability and reachability work together to give users an optimal experience.

### What you'll learn

1. [x] How to design your app to target all screen sizes with Jetpack Compose.
2. [x] How to target your app for different foldables.
3. [x] How to use different types of navigation for better reachability and accessibility.
4. [x] How to design Material 3 color-schemes and dynamic theming to provide an optimal
   accessibility
   experience.
5. [x] How to use Material 3 components to provide the best experience for every screen size.

### What you'll need

* [Android Studio Flamingo or higher.](https://developer.android.com/studio)
* [An Android 13 resizable virtual device.](https://developer.android.com/about/versions/13/get#resizable-emulator)
* Knowledge of Kotlin.
* Basic understanding of Compose (such as the @Composable annotation).
* Basic familiarity with Compose layouts (e.g., the Row and Column).
* Basic familiarity with modifiers (e.g., Modifier.padding()).

We will use
the [Resizable emulator](https://developer.android.com/about/versions/14/get#resizable-emulator) for
this codelab, as it lets us switch between different types of
devices and screen sizes.

Resizable emulator with options of phone, unfolded, tablet and desktop.

If you're unfamiliar with Compose, consider taking the [Jetpack Compose basics codelab](https://codelabs.developers.google.com/codelabs/jetpack-compose-basics/) before
completing this codelab.

What you'll build
An interactive Reply Email client app using best practices for adaptable designs, different material
navigations and optimal screen space usage.
Multiple device support showcase that you will achieve in this codelab