# Fix Compose Test Dependency Resolution

The project is failing to resolve `androidx.compose.ui:ui-test-junit4` because the Compose Bill of Materials (BOM) is only applied to the `implementation` configuration. Android test configurations (like `androidTestImplementation`) do not automatically inherit the platform (BOM) from `implementation`.

## Proposed Changes

### [app module](file:///C:/Users/space/Desktop/development/TiltControl/app/build.gradle.kts)

#### [MODIFY] [build.gradle.kts](file:///C:/Users/space/Desktop/development/TiltControl/app/build.gradle.kts)
- Add `androidTestImplementation(platform(libs.androidx.compose.bom))` to ensure testing libraries get their versions from the BOM.
- Add `debugImplementation(platform(libs.androidx.compose.bom))` to ensure debug libraries (like tooling and test manifest) also get their versions from the BOM.

### [Gradle Version Catalog](file:///C:/Users/space/Desktop/development/TiltControl/gradle/libs.versions.toml)

#### [MODIFY] [libs.versions.toml](file:///C:/Users/space/Desktop/development/TiltControl/gradle/libs.versions.toml)
- Update `composeBom` version from `2026.02.01` to `2026.06.01` to use the latest stable version.

## Verification Plan

### Automated Tests
- Run Gradle sync to verify the dependencies resolve correctly.
- Run `gradle :app:assembleAndroidTest` to ensure the test artifact builds.
