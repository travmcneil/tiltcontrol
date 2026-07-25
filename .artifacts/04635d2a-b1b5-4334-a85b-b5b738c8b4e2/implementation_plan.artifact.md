# Fix missing version for Compose UI Test JUnit4

The build error `Could not find androidx.compose.ui:ui-test-junit4:` occurs because the dependency is declared without a version in `libs.versions.toml`, and the Compose BOM (Bill of Materials) is only applied to the `implementation` configuration. Test and debug configurations do not automatically inherit the BOM's version constraints.

## Proposed Changes

### [app]

#### [MODIFY] [build.gradle.kts](file:///C:/Users/space/Desktop/development/TiltControl/app/build.gradle.kts)
- Add `androidTestImplementation(platform(libs.androidx.compose.bom))` to provide versions for test dependencies.
- Add `debugImplementation(platform(libs.androidx.compose.bom))` to provide versions for debug dependencies (like `ui-tooling`).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebugAndroidTest` to verify that test dependencies resolve correctly.
- Run `./gradlew :app:assembleDebug` to ensure general build still works.
