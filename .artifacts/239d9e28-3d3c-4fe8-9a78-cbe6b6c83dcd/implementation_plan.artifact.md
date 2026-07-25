# Fix Unresolved reference 'ksp'

The project is using `ksp(...)` in `app/build.gradle.kts` but the KSP (Kotlin Symbol Processing) plugin is not applied. This plan adds the KSP plugin to the project configuration.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/space/Desktop/development/TiltControl/gradle/libs.versions.toml)
- Add KSP version `2.2.10-2.0.2` to match Kotlin `2.2.10`.
- Add KSP plugin definition to the `[plugins]` block.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/space/Desktop/development/TiltControl/build.gradle.kts) (root)
- Register the KSP plugin in the top-level build file.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/space/Desktop/development/TiltControl/app/build.gradle.kts)
- Apply the KSP plugin to the app module.

## Verification Plan

### Automated Tests
- Run `gradlew :app:assembleDebug` to verify that the build succeeds and the 'ksp' reference is resolved.
- Perform a Gradle Sync to ensure the IDE no longer reports the error.
