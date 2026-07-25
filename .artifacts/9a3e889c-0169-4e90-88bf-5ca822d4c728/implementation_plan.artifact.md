# Fix Compose Test Resolution Error

The project is failing to resolve `androidx.compose.ui:ui-test-junit4`. This is likely because the Compose BOM is only applied to the `implementation` configuration, and `androidTestImplementation` is not inheriting the version constraints correctly in this setup.

## Proposed Changes

### [MODIFY] [libs.versions.toml](file:///C:/Users/space/Desktop/development/TiltControl/gradle/libs.versions.toml)
- Update `composeBom` version to `2026.06.01` to use the latest stable version.

### [MODIFY] [build.gradle.kts](file:///C:/Users/space/Desktop/development/TiltControl/app/build.gradle.kts)
- Explicitly add the Compose BOM to `androidTestImplementation` and `debugImplementation` to ensure all configurations have access to the version constraints.

```diff
 dependencies {
     implementation(libs.androidx.room.runtime)
     implementation(libs.androidx.room.ktx)
     ksp(libs.androidx.room.compiler)
     implementation(libs.androidx.navigation.compose)
     implementation(platform(libs.androidx.compose.bom))
+    androidTestImplementation(platform(libs.androidx.compose.bom))
+    debugImplementation(platform(libs.androidx.compose.bom))
     implementation(libs.androidx.activity.compose)
     implementation(libs.androidx.compose.material3)
     implementation(libs.androidx.compose.ui)
```

## Verification Plan

### Automated Tests
- Run Gradle sync: `gradle_sync()`
- Run a build task to verify resolution: `gradle_build("app:assembleDebug")`
