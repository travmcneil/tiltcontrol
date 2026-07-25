# Implementation Plan - Fix Room/KSP Build Error

The project is failing to build with the error `cannot find required type XTypeName[androidx.room.util.ByteArrayWrapper]`. This is caused by a version mismatch between the Room compiler (v2.8.4) and the Room runtime/ktx (v2.6.1).

## Proposed Changes

### [gradle/libs.versions.toml](file:///C:/Users/space/Desktop/development/TiltControl/gradle/libs.versions.toml)

- Consolidate all Room versions to `2.8.4`.
- Clean up duplicate and confusing version/library definitions.
- Ensure Kotlin and KSP versions are consistent if needed (currently they seem to match each other at `2.2.10`, but I will focus on Room first).

### [app/build.gradle.kts](file:///C:/Users/space/Desktop/development/TiltControl/app/build.gradle.kts)

- Update dependency declarations to use the cleaned-up Room library aliases.
- Use `libs.androidx.room.runtime`, `libs.androidx.room.ktx`, and `libs.androidx.room.compiler` (all pointing to `2.8.4`).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:kspDebugKotlin` to verify the KSP error is resolved.
- Run a full build `./gradlew assembleDebug` to ensure overall project health.
