# Walkthrough - Fixing Unresolved reference 'ksp'

I have successfully resolved the `Unresolved reference 'ksp'` error by correctly configuring the KSP (Kotlin Symbol Processing) plugin in your project.

## Changes Made

### Dependency Versioning
In [libs.versions.toml](file:///C:/Users/space/Desktop/development/TiltControl/gradle/libs.versions.toml), I added:
- KSP version `2.2.10-2.0.2` to match your Kotlin version `2.2.10`.
- The KSP plugin definition in the `[plugins]` block.

### Plugin Registration
- **Root [build.gradle.kts](file:///C:/Users/space/Desktop/development/TiltControl/build.gradle.kts)**: Registered the KSP plugin using `alias(libs.plugins.ksp) apply false`.
- **App [build.gradle.kts](file:///C:/Users/space/Desktop/development/TiltControl/app/build.gradle.kts)**: Applied the plugin using `alias(libs.plugins.ksp)`.

### Compatibility Fix
- **[gradle.properties](file:///C:/Users/space/Desktop/development/TiltControl/gradle.properties)**: Added `android.disallowKotlinSourceSets=false`. This was necessary because AGP 9.3.1 (with built-in Kotlin) restricts how source sets are added, and KSP needs this permission to automatically include its generated code.

## Verification Results

### Gradle Sync
- Successfully performed a Gradle Sync, and the `ksp(...)` references are now resolved in the IDE.

### Build Verification
- Ran `:app:assembleDebug` and the build finished successfully. Room is now able to use KSP for its annotation processing.
